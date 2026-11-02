# Nginx

💡 [Tap here](https://new.oprosso.net/p/4cb31ec3f47a4596bc758ea1861fb624) **to leave your feedback on the project**. It's anonymous and will help our team make your educational experience better. We recommend completing the survey immediately after the project.

## Contents

[[_TOC_]]


## Chapter I

### What is a Web server?
A web server is a computer that stores a website's files (HTML documents, CSS, JavaScript files, images, etc.) and delivers them to the end user device (web browser, etc.). It is connected to the Internet and can be accessed through a domain name such as mozilla.org.

From a software perspective, a web server includes several components that control how web users access the files on the server, such as an HTTP server. An HTTP server is the part of the software that understands URLs (web addresses) and HTTP (the protocol your browser uses to view web pages).
<br/>
![web_server](misc/images/web-server.svg)
<br/>

### Nginx

**Nginx** [engine x] is the HTTP and reverse proxy server, a mail proxy server, and a TCP/UDP general purpose proxy server, originally written by Igor Sysoev. It has been maintaining the servers of many high loaded Russian sites, such as Yandex, Mail.Ru, VKontakte, and Rambler, for a long time now. According to Netcraft statistics, Nginx maintained or proxied 21.23% of the busiest sites in February 2023.

Basic HTTP server functionality:

- Maintenance of static queries, index files, automatic file list creation, open file descriptor cache;
- Accelerated reverse proxying with caching, load balancing and fault tolerance;
- Accelerated support for FastCGI, uwsgi, SCGI and memcached servers with caching, load balancing and fault tolerance;
- Modularity, filters, including compression (gzip), byte-ranges, chunked responses, XSLT-filter, SSI-filter, image conversion;
- Several subrequests on the same page, handled by SSI filter through proxy or FastCGI/uwsgi/SCGI, are executed in parallel;
- Support for SSL and TLS SNI extension;
- HTTP/2 support with prioritization based on weights and dependencies.

### Reverse Proxy

Forward proxy works on behalf of clients, which means we need to configure the browser (client) to go through the direct proxy server. Reverse proxy works on behalf of the servers, so the client does not know that it is going to the proxy server.

Reverse proxy takes requests from clients to proxy them to the proxied web servers.

Proxying in Nginx is done by processing a request sent to the Nginx server and passing it to other servers for actual processing. The result of the request is sent back to Nginx, which then passes the information to the client. The other servers in this case can be remote machines, local servers, or even other virtual servers defined in the Nginx configuration. The servers accessed by the Nginx proxy are called upstream servers.

Sample configuration file to configure Nginx as a reverse proxy for HTTP server:

```
server {
    listen 80;
    server_name www.example.com example.com;

    location /app {
       proxy_pass http://127.0.0.1:8080;
    }
}
```
The URL of the proxy server is set with the proxy_pass directive and can use HTTP or HTTPS as the protocol, a domain name or IP address, and an optional port and URI as the address.

The above configuration tells Nginx to send all requests to /app to the proxy server at http://127.0.0.1:8080.

### Caching

**Caching** allows web applications to improve performance by using previously stored data, such as responses to network requests or calculation results. Caching allows the server to process requests for the same data more quickly when the client requests the same data again.

Caching is an efficient architectural pattern because most programs often access the same data and instructions. This technology exists at all levels of computer systems. Processors, disks, servers, and browsers all have caches.

The web server can be configured to cache responses so that it does not have to keep sending similar requests to the server application. Similarly, the main application can cache some of its own responses to resource-intensive database queries or common file requests.

Only two directives are needed to enable basic caching in Nginx: **proxy_cache_path** and **proxy_cache**. The **proxy_cache_path** directive sets the cache path and configuration, and the **proxy_cache** directive enables it.
```
proxy_cache_path /path/to/cache levels=1:2 keys_zone=my_cache:10m max_size=10g inactive=60m use_temp_path=off;

server {
    # ...
    location / {
        proxy_cache my_cache;
        proxy_pass http://my_upstream;
    }
}
```

This example sets the global configuration for caching:
- var/cache/nginx — cache path;

- levels — directory nesting level. In this example, we set the configuration to create a directory with the cache and another directory in it;

- keys_zone — the name of the zone in shared memory where the cache will be stored, and its size;

- inactive — the time after which the cache is automatically cleared;

- max_size — the maximum size of the cache. When the cache runs out of space, Nginx will remove the outdated data itself.

### Balancing
Load **balancing** is the efficient distribution of incoming network traffic across a group of backend servers. The role of the controller is to distribute the load across multiple installed backend servers.

Load balancing helps you scale your application by handling traffic spikes without increasing cloud costs. It also helps eliminate the single point of failure problem. Because the load is distributed, if one of the servers fails, the service will continue to run.

**Nginx as a Load Balancer**

Load Balancing Methods: The following load balancing mechanisms (or methods) are supported in Nginx:
- round-robin — requests to application servers are distributed cyclically;
- least-connected — the next request is assigned to the server with the lowest number of active connections;
- ip-hash — the hash function used to determine which server should be chosen for the next request (based on the client's IP address).

The simplest configuration for load balancing with Nginx might look like this (round-robin is used by default):

```http {
    upstream myapp1 {
        server srv1.example.com;
        server srv2.example.com;
        server srv3.example.com;
    }

    server {
        listen 80;

        location / {
            proxy_pass http://myapp1;
        }
    }
}
```
An example of using the least-connected method for balancing:
```
upstream myapp1 {
        least_conn;
        server srv1.example.com;
        server srv2.example.com;
        server srv3.example.com;
    }
```

### Compression
Enabling web page compression is a simple and effective way to increase the loading speed of your website. When the GZIP compressor is enabled, the flow of information transmitted from the server to the browser is recoded on the fly. As a result, the client browser receives the traffic in a compressed form (minimum size), which it unpacks upon receipt.

**GZIP compression** is successfully applied to textual information used for most web resources (including files with .html, .js, .css, .svg extensions).

A general configuration for GZIP compression might look like this:

```
server {
    gzip on;
    gzip_vary on;
    gzip_proxied any;
    gzip_comp_level 6;
    gzip_buffers 16 8k;
    gzip_http_version 1.1;
    gzip_min_length 1100;
    gzip_types text/plain text/css application/json application/x-javascript text/xml application/xml application/xml+rss text/javascript application/javascript application/vnd.ms-fontobject application/x-font-ttf font/opentype image/svg+xml image/x-icon;
    ...
}
```
- gzip on — enables support for GZIP compression;

- gzip_disable "msie6" — excludes IE6 from receiving compressed files. (does not support GZIP);

- gzip_buffers — specifies the number and size of buffers to compress the response into. By default, the size of a buffer is equal to the size of the page. This is either 4K or 8K, depending on the platform;

- gzip_proxied — compresses responses for proxy servers;

- gzip_vary on — includes the addition of the "Vary: Accept-Encoding" header to the response; for IE4-6 this will cause the data not to be cached due to the bug;

- gzip_comp_level 6 — sets the number of files to compress. The higher the number, the higher the compression level and resource usage. Compression levels: 1 — minimum, 9 — maximum;

- gzip_http_version 1.1 — its directive is used to limit gzip compression for browsers that support the HTTP/1.1 protocol. If the browser does not support it, it probably does not support gzip;

- gzip_min_length 1100 — tells Nginx not to compress files smaller than 256 bytes;

- gzip_types — shows all MIME types that will be compressed. In this case, the list includes HTML pages, CSS stylesheets, Javascript and JSON files, XML files, icons, SVG images, and web fonts.

### HTTPS, TLS, SSL 

The problem with the HTTP protocol is that data is transmitted over the network in clear, unencrypted form. This allows an intruder to eavesdrop on transmitted packets and extract any information from the parameters, headers, and body of the message. To address this vulnerability, **HTTPS** (the S at the end stands for secure) was developed — it is not a separate protocol, just HTTP over **SSL** (and later **TLS**), but it allows secure data exchange. Unlike HTTP, which uses the standard TCP/IP port 80, **HTTPS** uses port 443.

**SSL**

Secure Sockets Layer (SSL) is a cryptographic protocol that provides secure communication between a user and a server over an insecure network. It sits between the transport layer and the client program layer (FTP, HTTP, etc.). It was first introduced to the public in 1995, but has been recognized as fully deprecated since 2015. TLS 1.0 was developed in 1996 based on the SSL 3.0 specification.

**TLS**

Transport Layer Security is an evolution of the ideas behind the SSL protocol. TLSv1.2 is currently current, TLSv1.3 has been in active use since August 2018, while TLSv1.1, TLSv1.0, SSLv3.0, SSLv2.0, SSLv1.0 are deprecated. The protocol provides services: privacy (hiding transmitted data), integrity (detecting changes), authentication (verifying authorship). They are achieved through hybrid encryption, i.e. the joint use of asymmetric and symmetric encryption.

To configure the HTTPS server, you should enable the ssl parameter on listening sockets in the server block and specify the location of files containing the server certificate and secret key:
```
server {
    listen              443 ssl;
    server_name         www.example.com;
    ssl_certificate     www.example.com.crt;
    ssl_certificate_key www.example.com.key;
    ssl_protocols       TLSv1 TLSv1.1 TLSv1.2;
    ssl_ciphers         HIGH:!aNULL:!MD5;
    ...
}
```
The server certificate is public. It is sent to every client that connects to the server. The private key should be stored in a restricted file (the permissions should allow the main Nginx process to read this file). The secret key can also be stored in the same file as the certificate:

```
    ssl_certificate     www.example.com.cert;
    ssl_certificate_key www.example.com.cert;
```
Access rights to the file must also be restricted. Although both the certificate and the key are stored in the same file, only the certificate is sent to the client.

The ssl_protocols and ssl_ciphers directives can be used to restrict connections to "strong" versions and SSL/TLS ciphers only. By default, Nginx uses "ssl_protocols TLSv1 TLSv1.1 TLSv1.2" and "ssl_ciphers HIGH:!aNULL:!MD5", so it is generally not necessary to configure them.

###  HTTP 2/0, ServerPush

**HTTP/2**

In 2014, the HTTP/2 specification was approved as a standard, and as of 2015, it is supported in all major browsers. New features include

- Multiplexed Asynchronous Transfer: On the same connection, requests are divided into alternating packets that are grouped into separate streams.

- Requests are prioritized, eliminating the problem of sending all requests at once.

- Compression of HTTP headers is implemented. Each sent header contains information about the sender and recipient, and this is excessive. Thanks to compression, full information is sent only in the first header, and there is no such information in subsequent headers.

- Unlike the text-based HTTP protocol, HTTP/2 is binary. This allows for the handling of small messages that are assembled into larger ones.

- In HTTP/1.1, multiple parallel TCP connections were used to transfer different types of data quickly. In the new version, all data can be transferred using a single connection. The need to make only one connection greatly reduces the time it takes to deliver content.

- Server Push. In HTTP/1, the browser had to get the home page first and then understand what resources it needed to render it, but HTTP/2 allows you to send all the necessary resources at once the first time you address the server.

These features make it possible to increase productivity without using workarounds and tricks.

The main difference in this protocol is that it uses binary data instead of text data. Computers have more difficulty working with text than with the binary protocol. In addition, it is already difficult to imagine the modern Internet in a purely text-based format.

![http2](misc/images/http2.png)

**More about Server Push**

Websites are always accessed in a request-response pattern: the user sends a request to a remote server, which, after some delay, sends back a response with the requested content.

The initial request to the web server is usually for an HTML document. The server responds with the requested HTML resource. The resulting HTML document is then parsed by the browser, which extracts links to other resources, such as style sheets, scripts, and images. Once these are discovered, the browser sends a separate request for each resource and receives the appropriate responses.

The problem with this mechanism is that it forces the user to wait for the browser to detect and retrieve the necessary resources after the HTML document loads. This delays rendering and increases load time.

**Server Push** allows the server to preemptively "push" website resources to the client before the user explicitly requests them. In other words, we can send in advance what we know the user will need for the requested page.

![server_push](misc/images/HTTP-2-Server-Push.png)

**NGINX 1.13.9**, released on February 20, 2018, includes support for HTTP/2 pushing to the server. \
To send resources on page load, use the http2_push directive as follows:

```
server {
    listen 443 ssl http2;

    ssl_certificate ssl/certificate.pem;
    ssl_certificate_key ssl/key.pem;

    root /var/www/html;

    # when the client requests demo.html, also push 
    # /style.css, /image1.jpg and /image2.jpg
    location = /demo.html {
        http2_push /style.css;
        http2_push /image1.jpg;
        http2_push /image2.jpg;
    }
}
```

## Chapter II

Let's move from theory to practice.

In this block, you will need to:
1. Configure reverse proxying to your application port.
2. Configure Nginx for the routing part of the web application:
  * Configure routing /api -> to /api/v1, which you developed in the 2 API block.
  * On the /api/v1 path, output Swagger.
  * Configure the stats distribution on the / path. Place 2 files in the root of the stats distribution — index.html and image.png.
  * Configure /admin pgAdmin — GUI DBMS POSTGRES.
  * Configure /status to return the Nginx server status page (Nginx status).
3. Configure Nginx in the balancing section: 
  * Run 2 more backend instances on different ports with read-only database access and set up GET request balancing for /api/v1 (/api/v2) in Nginx for 3 backends in a 2:1:1 ratio, with the first being the main backend server. 
4. Configure caching (for all GET requests except /api).
5. Configure gzip compression in Nginx. Compression should not be applied to media types (jpeg, png, etc.).
6. You need to configure HTTPS on a local machine:
  * Create a domain name on the local DNS server. Every computer has a local DNS repository where you can write your own site name and where this address will be resolved. It is not difficult to find information on how to do this.
  * Create a self-signed certificate using openssl for the domain name you created and bind it in the Nginx config.
  * Configure a reverse proxy for a running application.
