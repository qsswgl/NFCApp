$sdkRoot = "$env:LOCALAPPDATA\Android\Sdk"
$url = "https://dl.google.com/android/repository/commandlinetools-win-11076708_latest.zip"
$zip = "$env:TEMP\cmdtools.zip"

Write-Host "Downloading Android SDK tools..." -ForegroundColor Cyan
Write-Host "URL: $url" -ForegroundColor Gray
Write-Host "Save to: $zip" -ForegroundColor Gray
Write-Host ""

[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

try {
    Invoke-WebRequest -Uri $url -OutFile $zip -UseBasicParsing
    Write-Host "Download complete!" -ForegroundColor Green
    Write-Host "File size: $((Get-Item $zip).Length / 1MB) MB" -ForegroundColor Gray
}
catch {
    Write-Host "Download failed: $_" -ForegroundColor Red
}
