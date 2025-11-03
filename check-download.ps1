$file = "$env:TEMP\cmdtools.zip"
if (Test-Path $file) {
    $size = (Get-Item $file).Length / 1MB
    Write-Host "Download complete!" -ForegroundColor Green
    Write-Host "File size: $([math]::Round($size,2)) MB" -ForegroundColor Cyan
} else {
    Write-Host "File not found yet" -ForegroundColor Yellow
}
