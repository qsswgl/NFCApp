# Alternative Java download sources
$downloadOptions = @(
    @{
        Name = "Method 1: Amazon Corretto (Recommended)";
        Url = "https://corretto.aws/downloads/latest/amazon-corretto-17-x64-windows-jdk.zip"
    },
    @{
        Name = "Method 2: Eclipse Adoptium";
        Url = "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.11%2B9/OpenJDK17U-jdk_x64_windows_hotspot_17.0.11_9.zip"
    }
)

Write-Host "Java download options:"
Write-Host ""

foreach ($option in $downloadOptions) {
    Write-Host $option.Name
    Write-Host "URL: $($option.Url)"
    Write-Host ""
}

Write-Host "Let me try downloading from Amazon Corretto..."
Write-Host ""

$javaUrl = "https://corretto.aws/downloads/latest/amazon-corretto-17-x64-windows-jdk.zip"
$tempPath = $env:TEMP + '\jdk-17.zip'

Write-Host "Downloading... (this may take 3-5 minutes)"

try {
    Invoke-WebRequest -Uri $javaUrl -OutFile $tempPath -UseBasicParsing -TimeoutSec 600
    
    if (Test-Path $tempPath) {
        $size = (Get-Item $tempPath).Length / 1MB
        Write-Host ""
        Write-Host "Download successful! File size: $([math]::Round($size,2)) MB"
        Write-Host "Saved to: $tempPath"
    }
}
catch {
    Write-Host "Failed with Amazon. Trying Eclipse Adoptium..."
    
    $javaUrl = "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.11%2B9/OpenJDK17U-jdk_x64_windows_hotspot_17.0.11_9.zip"
    $tempPath = $env:TEMP + '\jdk-17-adoptium.zip'
    
    try {
        Invoke-WebRequest -Uri $javaUrl -OutFile $tempPath -UseBasicParsing -TimeoutSec 600
        
        if (Test-Path $tempPath) {
            $size = (Get-Item $tempPath).Length / 1MB
            Write-Host ""
            Write-Host "Download successful from Adoptium! File size: $([math]::Round($size,2)) MB"
            Write-Host "Saved to: $tempPath"
        }
    }
    catch {
        Write-Host ""
        Write-Host "Download failed from all sources."
        Write-Host ""
        Write-Host "Please download manually from one of these sources:"
        Write-Host "1. Amazon Corretto: https://corretto.aws/downloads/latest/"
        Write-Host "2. Eclipse Adoptium: https://adoptium.net/installation/"
        Write-Host "3. Oracle: https://www.oracle.com/java/technologies/downloads/"
    }
}
