$json = '{"clientName":"Test","clientSurname":"Balance","birthday":"2000-01-01","gender":"M","address":{"street":"Test St","city":"Moscow","country":"Russia"}}'
$response = curl.exe -k -i -s -X POST "https://shop.local/api/v1/clients" -H "Content-Type: application/json" -d $json
$port = ($response | Select-String "X-Upstream-Server:\s*127\.0\.0\.1:(\d+)").Matches.Groups[1].Value
Write-Host "POST -> Port $port" -ForegroundColor Green