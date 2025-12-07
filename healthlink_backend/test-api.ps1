# HealthLink API Testing Script
# Run this in PowerShell to test your API endpoints

$baseUrl = "http://localhost:8080"

Write-Host "=== HealthLink API Testing ===" -ForegroundColor Cyan
Write-Host ""

# 1. Health Check
Write-Host "1. Testing Health Endpoint..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/health" -Method Get
    Write-Host "✓ Health Check: " -ForegroundColor Green -NoNewline
    Write-Host ($response | ConvertTo-Json)
} catch {
    Write-Host "✗ Health Check Failed: $_" -ForegroundColor Red
}
Write-Host ""

# 2. Swagger UI Info
Write-Host "2. API Documentation:" -ForegroundColor Yellow
Write-Host "   Swagger UI: $baseUrl/swagger-ui.html" -ForegroundColor Cyan
Write-Host "   API Docs: $baseUrl/api-docs" -ForegroundColor Cyan
Write-Host ""

# 3. Example: Test Authentication Endpoint (Register)
Write-Host "3. Example: Register a new patient..." -ForegroundColor Yellow
Write-Host "   POST $baseUrl/api/v1/auth/register" -ForegroundColor Gray
Write-Host ""
Write-Host "   Example JSON body:" -ForegroundColor Gray
$exampleBody = @{
    email = "test@example.com"
    password = "Test123!@#"
    firstName = "Test"
    lastName = "User"
    phoneNumber = "+1234567890"
    userType = "PATIENT"
} | ConvertTo-Json
Write-Host $exampleBody -ForegroundColor DarkGray
Write-Host ""

# 4. Example: Login
Write-Host "4. Example: Login..." -ForegroundColor Yellow
Write-Host "   POST $baseUrl/api/v1/auth/login" -ForegroundColor Gray
Write-Host ""
Write-Host "   Example JSON body:" -ForegroundColor Gray
$loginBody = @{
    email = "test@example.com"
    password = "Test123!@#"
} | ConvertTo-Json
Write-Host $loginBody -ForegroundColor DarkGray
Write-Host ""

Write-Host "=== Tips ===" -ForegroundColor Cyan
Write-Host "• Use Swagger UI for interactive testing: $baseUrl/swagger-ui.html" -ForegroundColor White
Write-Host "• Most endpoints require JWT token in Authorization header" -ForegroundColor White
Write-Host "• Format: Authorization: Bearer <your_token>" -ForegroundColor White
Write-Host "• Use Postman or curl for advanced testing" -ForegroundColor White

