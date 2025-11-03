# 显示当前安装状态（修正编码版本）
# 使用 UTF-8 编码显示中文

# 修正编码
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8
chcp 65001 | Out-Null

Write-Host ""
Write-Host "================================================" -ForegroundColor Cyan
Write-Host "     当前状态和建议方案                    " -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "已完成：" -ForegroundColor Green
Write-Host "  √ Java 17.0.12 安装并验证" -ForegroundColor Cyan
Write-Host "  √ Android Studio 2025.2.1.7 安装" -ForegroundColor Cyan
Write-Host "  √ SDK工具已下载（94MB）" -ForegroundColor Cyan
Write-Host ""

Write-Host "SDK配置遇到小问题" -ForegroundColor Yellow
Write-Host "  解压路径需要调整" -ForegroundColor Gray
Write-Host ""

Write-Host "推荐方案（最快）：" -ForegroundColor Yellow
Write-Host ""
Write-Host "方案：使用 Android Studio GUI 完成初始化" -ForegroundColor Cyan
Write-Host ""
Write-Host "步骤：" -ForegroundColor Yellow
Write-Host "  1. 打开 Android Studio" -ForegroundColor Gray
Write-Host "  2. 完成初始化向导（约5分钟）" -ForegroundColor Gray
Write-Host "  3. 它会自动下载和配置SDK" -ForegroundColor Gray
Write-Host "  4. 完成后告诉我SDK路径" -ForegroundColor Gray
Write-Host ""

Write-Host "或者：" -ForegroundColor Cyan
Write-Host "  输入 '继续命令行' - 我继续调试命令行方案" -ForegroundColor Gray
Write-Host "  输入 '跳过' - 暂时跳过Android配置" -ForegroundColor Gray
Write-Host ""
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""
