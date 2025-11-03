# Java download script
$javaUrl = "https://download.oracle.com/java/17/latest/jdk-17_windows-x64_bin.exe"
$tempPath = $env:TEMP + '\jdk-17.exe'

Write-Host "Starting download from: $javaUrl"
Write-Host "Saving to: $tempPath"
Write-Host "This may take 2-5 minutes..."
Write-Host ""

try {
    $ProgressPreference = 'Continue'
    Invoke-WebRequest -Uri $javaUrl -OutFile $tempPath -UseBasicParsing -TimeoutSec 600 -Verbose
    
    if (Test-Path $tempPath) {
        $size = (Get-Item $tempPath).Length / 1MB
        Write-Host ""
        Write-Host "Download successful! File size: $([math]::Round($size,2)) MB"
    } else {
        Write-Host "Download file not found!"
    }
}
catch {
    Write-Host "Download failed: $_"
    Write-Host ""
    Write-Host "Please download manually from:"
    Write-Host "https://www.oracle.com/java/technologies/downloads/"
    Write-Host ""
    Write-Host "Select: Java SE 17 LTS -> Windows x64 Installer"
}
