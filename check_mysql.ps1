# Check MySQL Installation
Write-Host "Checking MySQL Installation..." -ForegroundColor Cyan

# Check if MySQL service exists
$mysqlService = Get-Service | Where-Object {$_.Name -like "*mysql*"}
if ($mysqlService) {
    Write-Host "`nMySQL Service Found: $($mysqlService.Name)" -ForegroundColor Green
    Write-Host "Status: $($mysqlService.Status)" -ForegroundColor $(if ($mysqlService.Status -eq 'Running') {'Green'} else {'Yellow'})
    
    if ($mysqlService.Status -ne 'Running') {
        Write-Host "`nMySQL service is not running!" -ForegroundColor Yellow
        Write-Host "To start it, run (as Administrator):" -ForegroundColor Yellow
        Write-Host "Start-Service $($mysqlService.Name)" -ForegroundColor White
    }
} else {
    Write-Host "`nNo MySQL service found!" -ForegroundColor Red
    Write-Host "MySQL Server may not be properly configured." -ForegroundColor Yellow
    Write-Host "Please click 'Reconfigure' in MySQL Installer." -ForegroundColor Yellow
}

# Check if MySQL is in PATH
$mysqlPath = Get-Command mysql -ErrorAction SilentlyContinue
if ($mysqlPath) {
    Write-Host "`nMySQL command found at: $($mysqlPath.Source)" -ForegroundColor Green
} else {
    Write-Host "`nMySQL command not in PATH" -ForegroundColor Yellow
    Write-Host "This is normal - you can use MySQL Workbench instead" -ForegroundColor Gray
}

Write-Host "`nDone!" -ForegroundColor Cyan

