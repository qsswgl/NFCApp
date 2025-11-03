$tempPath = $env:TEMP + '\jdk-17.exe'
if (Test-Path $tempPath) {
    $size = (Get-Item $tempPath).Length / 1MB
    Write-Host "Java file found! Size: $([math]::Round($size,2)) MB"
} else {
    Write-Host "Java file not found"
}
