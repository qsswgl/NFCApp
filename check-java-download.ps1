$tempPath = $env:TEMP + '\jdk-17.exe'
if (Test-Path $tempPath) {
    $size = (Get-Item $tempPath).Length / 1MB
    Write-Host "Java 文件找到！大小: $([math]::Round($size,2)) MB"
} else {
    Write-Host "Java 文件未找到"
}
