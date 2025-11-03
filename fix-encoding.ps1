# 修正 PowerShell 中文显示问题
# 设置控制台编码为 UTF-8

Write-Host "正在修正终端编码设置..." -ForegroundColor Cyan

# 设置当前会话编码
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
[Console]::InputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8

# 设置 PowerShell 会话编码
chcp 65001 | Out-Null

Write-Host "✓ 编码已修正为 UTF-8" -ForegroundColor Green
Write-Host ""
Write-Host "测试中文显示：" -ForegroundColor Yellow
Write-Host "  中文测试：你好，世界！" -ForegroundColor White
Write-Host "  符号测试：√ × ✓ ✗ ● ○" -ForegroundColor White
Write-Host "  框线测试：╔═══╗ ║ ║ ╚═══╝" -ForegroundColor White
Write-Host ""

Write-Host "如果上面的中文显示正常，说明修正成功！" -ForegroundColor Green
Write-Host ""
Write-Host "提示：" -ForegroundColor Yellow
Write-Host "  • 此设置仅对当前终端有效" -ForegroundColor Gray
Write-Host "  • 每次打开新终端都需要运行此脚本" -ForegroundColor Gray
Write-Host "  • 或者添加到 PowerShell 配置文件中" -ForegroundColor Gray
Write-Host ""
