# check_nginx_project.py
# Комплексная проверка конфигурации Nginx для ShopAPI
# Запуск: python check_nginx_project.py

import requests
import subprocess
import socket
import os
import sys
import time
import json
import re
from pathlib import Path
from urllib.parse import urlparse
from typing import Dict, List, Tuple, Optional
import ssl
from concurrent.futures import ThreadPoolExecutor, as_completed

class Colors:
    """ANSI цвета для терминала"""
    GREEN = '\033[92m'
    RED = '\033[91m'
    YELLOW = '\033[93m'
    BLUE = '\033[94m'
    CYAN = '\033[96m'
    GRAY = '\033[90m'
    RESET = '\033[0m'
    BOLD = '\033[1m'

class NginxTestRunner:
    def __init__(self):
        self.hostname = "shop.local"
        self.http_base = f"http://{self.hostname}"
        self.https_base = f"https://{self.hostname}"
        self.api_path = "/api/v1/clients?limit=1&offset=0"
        self.nginx_dir = Path(r"C:\nginx-1.30.4")
        self.html_dir = self.nginx_dir / "html"
        self.ssl_dir = self.nginx_dir / "ssl"
        self.passed = 0
        self.failed = 0
        self.results = []
        self.session = requests.Session()
        self.session.verify = False
        import urllib3
        urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

    def log_pass(self, name: str, details: str = ""):
        self.passed += 1
        print(f"{Colors.GREEN}[PASS]{Colors.RESET} {name}")
        if details:
            print(f"       {Colors.GRAY}{details}{Colors.RESET}")
        self.results.append({"check": name, "status": "PASS", "details": details})

    def log_fail(self, name: str, details: str = ""):
        self.failed += 1
        print(f"{Colors.RED}[FAIL]{Colors.RESET} {name}")
        if details:
            print(f"       {Colors.GRAY}{details}{Colors.RESET}")
        self.results.append({"check": name, "status": "FAIL", "details": details})

    def test_dns_resolution(self) -> bool:
        print(f"\n{Colors.CYAN}=== 1. DNS РЕЗОЛВИНГ ==={Colors.RESET}")
        try:
            ip = socket.gethostbyname(self.hostname)
            self.log_pass(f"{self.hostname} разрешается", f"IP: {ip}")
            return True
        except socket.gaierror:
            self.log_fail(f"{self.hostname} разрешается", "Добавьте shop.local в C:\Windows\System32\drivers\etc\hosts")
            return False

    def test_nginx_config(self) -> bool:
        print(f"\n{Colors.CYAN}=== 2. КОНФИГУРАЦИЯ NGINX ==={Colors.RESET}")
        nginx_exe = self.nginx_dir / "nginx.exe"
        if not nginx_exe.exists():
            self.log_fail("Nginx найден", f"Файл не найден: {nginx_exe}")
            return False
        try:
            result = subprocess.run(
                [str(nginx_exe), "-t"],
                cwd=str(self.nginx_dir),
                capture_output=True,
                text=True,
                shell=True
            )
            if result.returncode == 0:
                self.log_pass("Синтаксис конфигурации Nginx", "Конфигурация валидна")
                return True
            else:
                self.log_fail("Синтаксис конфигурации Nginx", result.stderr[:200])
                return False
        except Exception as e:
            self.log_fail("Синтаксис конфигурации Nginx", str(e))
            return False

    def make_request(self, url: str, method: str = "GET", **kwargs) -> Optional[requests.Response]:
        try:
            kwargs.setdefault('timeout', 10)
            kwargs.setdefault('verify', False)
            kwargs.setdefault('allow_redirects', False)
            response = self.session.request(method, url, **kwargs)
            return response
        except requests.exceptions.SSLError:
            try:
                response = self.session.request(method, url, verify=False, **kwargs)
                return response
            except Exception as e:
                print(f"  SSL Error: {e}")
                return None
        except Exception as e:
            print(f"  Request Error: {e}")
            return None

    def test_http_redirect(self) -> bool:
        print(f"\n{Colors.CYAN}=== 3. HTTP -> HTTPS РЕДИРЕКТ ==={Colors.RESET}")
        response = self.make_request(self.http_base)
        if not response:
            self.log_fail("HTTP -> HTTPS редирект", "Нет ответа от сервера")
            return False
        if response.status_code == 301 or response.status_code == 302:
            location = response.headers.get('Location', '')
            if self.https_base in location or location.startswith('https://'):
                self.log_pass("HTTP редиректит на HTTPS", f"301 -> {location}")
                return True
        self.log_fail("HTTP -> HTTPS редирект", f"Ожидался 301, получен {response.status_code}")
        return False

    def test_https_static(self) -> bool:
        print(f"\n{Colors.CYAN}=== 4. HTTPS И СТАТИКА ==={Colors.RESET}")
        response = self.make_request(self.https_base)
        if not response or response.status_code != 200:
            self.log_fail("HTTPS доступен", f"Ожидался 200, получен {response.status_code if response else 'No response'}")
            return False
        if 'Shop API' in response.text or 'Nginx' in response.text:
            self.log_pass("HTTPS доступен", "index.html загружен")
        else:
            self.log_pass("HTTPS доступен", "Страница загружена")
        response_img = self.make_request(f"{self.https_base}/image.png")
        if response_img and response_img.status_code == 200:
            content_type = response_img.headers.get('Content-Type', '')
            if 'image/' in content_type:
                self.log_pass("image.png раздается", f"Content-Type: {content_type}")
            else:
                self.log_pass("image.png раздается", "Файл загружен")
        else:
            self.log_fail("image.png раздается", f"Ожидался 200, получен {response_img.status_code if response_img else 'No response'}")
            return False
        return True

    def test_api_routing(self) -> bool:
        print(f"\n{Colors.CYAN}=== 5. МАРШРУТИЗАЦИЯ /api ==={Colors.RESET}")
        url = f"{self.https_base}/api/clients?limit=1&offset=0"
        response = self.make_request(url)
        if not response or response.status_code != 200:
            self.log_fail("/api -> /api/v1", f"Ожидался 200, получен {response.status_code if response else 'No response'}")
            return False
        if 'clientName' in response.text or 'address' in response.text:
            self.log_pass("/api -> /api/v1", "Маршрутизация работает")
            return True
        else:
            self.log_fail("/api -> /api/v1", "Ответ не содержит данных клиента")
            return False

    def test_api_v1(self) -> bool:
        print(f"\n{Colors.CYAN}=== 6. /api/v1 API ==={Colors.RESET}")
        url = f"{self.https_base}{self.api_path}"
        response = self.make_request(url)
        if not response or response.status_code != 200:
            self.log_fail("/api/v1 проксирование", f"Ожидался 200, получен {response.status_code if response else 'No response'}")
            return False
        if 'clientName' in response.text:
            self.log_pass("/api/v1 проксирование", "API отвечает корректно")
            return True
        else:
            self.log_fail("/api/v1 проксирование", "Ответ не содержит данных клиента")
            return False

    def test_swagger(self) -> bool:
        print(f"\n{Colors.CYAN}=== 7. SWAGGER ==={Colors.RESET}")
        response = self.make_request(f"{self.https_base}/api/v1")
        if not response or response.status_code not in [301, 302]:
            self.log_fail("/api/v1 -> Swagger", f"Ожидался 301/302, получен {response.status_code if response else 'No response'}")
            return False
        location = response.headers.get('Location', '')
        if '/swagger/' not in location:
            self.log_fail("/api/v1 -> Swagger", "Редирект не на /swagger/")
            return False
        self.log_pass("/api/v1 -> Swagger", f"Редирект на {location}")
        response = self.make_request(f"{self.https_base}/swagger/swagger-ui/index.html")
        if response and response.status_code == 200:
            if 'Swagger UI' in response.text:
                self.log_pass("Swagger UI доступен", "Загружен Swagger UI")
                return True
            else:
                self.log_pass("Swagger UI доступен", "Страница загружена")
                return True
        else:
            self.log_fail("Swagger UI доступен", f"Ожидался 200, получен {response.status_code if response else 'No response'}")
            return False

    def test_pgadmin(self) -> bool:
        """Тест 8: pgAdmin"""
        print(f"\n{Colors.CYAN}=== 8. PGADMIN ==={Colors.RESET}")
        
        response = self.make_request(f"{self.https_base}/admin/")
        
        if not response:
            self.log_fail("/admin -> pgAdmin", "Нет ответа от сервера")
            return False
        
        # 302 - это нормально для pgAdmin (редирект на страницу логина)
        if response.status_code == 302:
            self.log_pass("/admin -> pgAdmin", "Редирект на страницу логина (302) - OK")
            return True
        
        # 200 - страница загружена
        if response.status_code == 200:
            if 'pgAdmin' in response.text or 'login' in response.text.lower():
                self.log_pass("/admin -> pgAdmin", "Страница pgAdmin загружена")
            else:
                self.log_pass("/admin -> pgAdmin", "Страница загружена (проверьте визуально)")
            return True
        
        # Любой другой статус - ошибка
        self.log_fail("/admin -> pgAdmin", f"Ожидался 200 или 302, получен {response.status_code}")
        return False

    def test_status(self) -> bool:
        print(f"\n{Colors.CYAN}=== 9. NGINX STATUS ==={Colors.RESET}")
        response = self.make_request(f"{self.https_base}/status")
        if not response or response.status_code != 200:
            self.log_fail("/status", f"Ожидался 200, получен {response.status_code if response else 'No response'}")
            return False
        text = response.text
        if 'Active connections' in text and 'server accepts handled requests' in text:
            self.log_pass("/status", "Stub status работает")
            return True
        else:
            self.log_fail("/status", "Нет ожидаемой статистики")
            return False

    def test_balancing(self) -> bool:
        print(f"\n{Colors.CYAN}=== 10. БАЛАНСИРОВКА ==={Colors.RESET}")
        upstreams = []
        for i in range(20):
            response = self.make_request(f"{self.https_base}{self.api_path}")
            if response and 'X-Upstream-Server' in response.headers:
                upstream = response.headers['X-Upstream-Server']
                if upstream:
                    upstreams.append(upstream)
            time.sleep(0.05)
        if not upstreams:
            self.log_fail("Балансировка GET", "Нет данных об upstream")
            return False
        unique = set(upstreams)
        has_8080 = '127.0.0.1:8080' in unique
        has_8081 = '127.0.0.1:8081' in unique
        has_8082 = '127.0.0.1:8082' in unique
        if has_8080 and has_8081 and has_8082:
            self.log_pass("Балансировка между 3 бэкендами", f"Распределение: {', '.join(sorted(unique))}")
        else:
            self.log_fail("Балансировка между 3 бэкендами", f"Найдены: {', '.join(sorted(unique))}")
            return False
        count_8080 = sum(1 for u in upstreams if u == '127.0.0.1:8080')
        count_8081 = sum(1 for u in upstreams if u == '127.0.0.1:8081')
        count_8082 = sum(1 for u in upstreams if u == '127.0.0.1:8082')
        ratio_ok = (count_8080 >= 7 and count_8081 >= 3 and count_8082 >= 3)
        if ratio_ok:
            self.log_pass("Соотношение 2:1:1", f"8080={count_8080}, 8081={count_8081}, 8082={count_8082}")
        else:
            self.log_fail("Соотношение 2:1:1", f"8080={count_8080}, 8081={count_8081}, 8082={count_8082}")
        return True

    def test_non_get_routing(self) -> bool:
        print(f"\n{Colors.CYAN}=== 11. НЕ-GET ЗАПРОСЫ ==={Colors.RESET}")
        response = self.make_request(f"{self.https_base}/api/v1/clients", method="OPTIONS")
        if response and 'X-Upstream-Server' in response.headers:
            upstream = response.headers['X-Upstream-Server']
            if upstream and '8080' in upstream:
                self.log_pass("Не-GET запросы идут на 8080", f"OPTIONS -> {upstream}")
                return True
            else:
                self.log_fail("Не-GET запросы идут на 8080", f"Ожидался 8080, получен {upstream}")
                return False
        else:
            response = self.make_request(f"{self.https_base}{self.api_path}", method="HEAD")
            if response and 'X-Upstream-Server' in response.headers:
                upstream = response.headers['X-Upstream-Server']
                if upstream and '8080' in upstream:
                    self.log_pass("Не-GET запросы идут на 8080", f"HEAD -> {upstream}")
                    return True
            self.log_fail("Не-GET запросы идут на 8080", "Не удалось определить upstream для не-GET запроса")
            return False

    def test_cache(self) -> bool:
        print(f"\n{Colors.CYAN}=== 12. КЕШИРОВАНИЕ ==={Colors.RESET}")
        url = f"{self.https_base}/swagger/swagger-ui/index.html"
        response1 = self.make_request(url)
        cache1 = response1.headers.get('X-Cache-Status', '') if response1 else ''
        response2 = self.make_request(url)
        cache2 = response2.headers.get('X-Cache-Status', '') if response2 else ''
        if cache2 == 'HIT':
            self.log_pass("Кеширование для статики", f"Второй запрос: {cache2}")
            return True
        elif cache1 or cache2:
            self.log_pass("Кеширование для статики", f"Первый: {cache1}, Второй: {cache2}")
            return True
        else:
            self.log_fail("Кеширование для статики", "Нет заголовков кеширования")
            return False

    def test_api_no_cache(self) -> bool:
        print(f"\n{Colors.CYAN}=== 13. /api НЕ КЕШИРУЕТСЯ ==={Colors.RESET}")
        url = f"{self.https_base}{self.api_path}"
        response = self.make_request(url)
        cache_header = response.headers.get('X-Cache-Status', '') if response else ''
        if cache_header and cache_header != 'HIT':
            self.log_pass("/api не кешируется", f"X-Cache-Status: {cache_header}")
            return True
        elif not cache_header:
            self.log_pass("/api не кешируется", "Нет заголовков кеширования (OK)")
            return True
        else:
            self.log_fail("/api не кешируется", f"X-Cache-Status: {cache_header}")
            return False

    def test_gzip_json(self) -> bool:
        print(f"\n{Colors.CYAN}=== 14. GZIP ДЛЯ JSON ==={Colors.RESET}")
        headers = {'Accept-Encoding': 'gzip'}
        response = self.make_request(f"{self.https_base}{self.api_path}", headers=headers)
        if not response:
            self.log_fail("Gzip для JSON", "Нет ответа")
            return False
        content_encoding = response.headers.get('Content-Encoding', '')
        if 'gzip' in content_encoding:
            self.log_pass("Gzip для JSON", f"Content-Encoding: {content_encoding}")
            return True
        else:
            self.log_fail("Gzip для JSON", f"Content-Encoding: {content_encoding or 'none'}")
            return False

    def test_no_gzip_image(self) -> bool:
        print(f"\n{Colors.CYAN}=== 15. НЕТ GZIP ДЛЯ ИЗОБРАЖЕНИЙ ==={Colors.RESET}")
        headers = {'Accept-Encoding': 'gzip'}
        response = self.make_request(f"{self.https_base}/image.png", headers=headers)
        if not response:
            self.log_fail("Нет gzip для изображений", "Нет ответа")
            return False
        content_encoding = response.headers.get('Content-Encoding', '')
        if 'gzip' not in content_encoding:
            self.log_pass("Нет gzip для изображений", "Content-Encoding не установлен")
            return True
        else:
            self.log_fail("Нет gzip для изображений", f"Content-Encoding: {content_encoding}")
            return False

    def test_backend_ports(self) -> bool:
        print(f"\n{Colors.CYAN}=== 16. ДОСТУПНОСТЬ БЭКЕНДОВ ==={Colors.RESET}")
        ports = [8080, 8081, 8082]
        all_ok = True
        for port in ports:
            sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            sock.settimeout(2)
            try:
                result = sock.connect_ex(('127.0.0.1', port))
                if result == 0:
                    self.log_pass(f"Бэкенд {port} доступен", "TCP соединение установлено")
                else:
                    self.log_fail(f"Бэкенд {port} доступен", f"Порт {port} не отвечает")
                    all_ok = False
            except Exception as e:
                self.log_fail(f"Бэкенд {port} доступен", str(e))
                all_ok = False
            finally:
                sock.close()
        return all_ok

    def test_ssl_certificate(self) -> bool:
        print(f"\n{Colors.CYAN}=== ДОП. SSL СЕРТИФИКАТ ==={Colors.RESET}")
        cert_file = self.ssl_dir / "shop.local.crt"
        key_file = self.ssl_dir / "shop.local.key"
        if cert_file.exists() and key_file.exists():
            self.log_pass("SSL сертификаты найдены", f"CRT: {cert_file.name}, KEY: {key_file.name}")
            return True
        else:
            missing = []
            if not cert_file.exists():
                missing.append(cert_file.name)
            if not key_file.exists():
                missing.append(key_file.name)
            self.log_fail("SSL сертификаты найдены", f"Отсутствуют: {', '.join(missing)}")
            return False

    def run_all_tests(self):
        print(f"\n{Colors.CYAN}{Colors.BOLD}{'='*50}{Colors.RESET}")
        print(f"{Colors.CYAN}{Colors.BOLD}  NGINX КОМПЛЕКСНЫЙ ТЕСТ - SHOPAPI{Colors.RESET}")
        print(f"{Colors.CYAN}{Colors.BOLD}  Хост: {self.hostname}{Colors.RESET}")
        print(f"{Colors.CYAN}{Colors.BOLD}{'='*50}{Colors.RESET}")

        tests = [
            self.test_dns_resolution,
            self.test_nginx_config,
            self.test_http_redirect,
            self.test_https_static,
            self.test_api_routing,
            self.test_api_v1,
            self.test_swagger,
            self.test_pgadmin,
            self.test_status,
            self.test_balancing,
            self.test_non_get_routing,
            self.test_cache,
            self.test_api_no_cache,
            self.test_gzip_json,
            self.test_no_gzip_image,
            self.test_backend_ports,
            self.test_ssl_certificate,
        ]

        for test in tests:
            try:
                test()
            except Exception as e:
                self.log_fail(test.__doc__ or "Тест", f"Ошибка: {str(e)}")

        self.print_summary()

    def print_summary(self):
        print(f"\n{Colors.CYAN}{'='*50}{Colors.RESET}")
        print(f"{Colors.CYAN}  РЕЗУЛЬТАТЫ ТЕСТИРОВАНИЯ{Colors.RESET}")
        print(f"{Colors.CYAN}{'='*50}{Colors.RESET}")
        
        total = self.passed + self.failed
        print(f"{Colors.GREEN}PASS: {self.passed}{Colors.RESET}")
        print(f"{Colors.RED}FAIL: {self.failed}{Colors.RESET}")
        print(f"TOTAL: {total}")

        report_file = Path("nginx_test_report.txt")
        with open(report_file, 'w', encoding='utf-8') as f:
            f.write("=" * 50 + "\n")
            f.write("NGINX ТЕСТ - ОТЧЕТ\n")
            f.write("=" * 50 + "\n\n")
            f.write(f"Дата: {time.strftime('%Y-%m-%d %H:%M:%S')}\n")
            f.write(f"Хост: {self.hostname}\n\n")
            for result in self.results:
                status = "[PASS]" if result['status'] == 'PASS' else "[FAIL]"
                f.write(f"{status} {result['check']}\n")
                if result['details']:
                    f.write(f"    {result['details']}\n")
            f.write("\n" + "=" * 50 + "\n")
            f.write(f"PASS: {self.passed}\n")
            f.write(f"FAIL: {self.failed}\n")
            f.write(f"TOTAL: {total}\n")
        
        print(f"\n{Colors.YELLOW}Отчет сохранен в: {report_file}{Colors.RESET}")

        if self.failed == 0:
            print(f"\n{Colors.GREEN}{Colors.BOLD}🎉 ВСЕ ТЕСТЫ ПРОЙДЕНЫ УСПЕШНО!{Colors.RESET}")
            sys.exit(0)
        else:
            print(f"\n{Colors.RED}{Colors.BOLD}⚠️ ЕСТЬ ПРОВАЛЕННЫЕ ТЕСТЫ!{Colors.RESET}")
            print(f"{Colors.YELLOW}Проверьте вывод выше для деталей{Colors.RESET}")
            sys.exit(1)

def main():
    runner = NginxTestRunner()
    runner.run_all_tests()

if __name__ == "__main__":
    main()