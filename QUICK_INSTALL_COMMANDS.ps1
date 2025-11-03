# ========================================
# 物理主机快速安装命令
# ========================================

# 请在物理主机的 PowerShell 中按顺序执行以下命令：

# 1. 设置ADB路径
$adb = "K:\tool\adb\adb.exe"

# 2. 检查设备连接
& $adb devices

# 3. 卸载旧版本（可选，如果之前装过）
& $adb uninstall com.nfc.app

# 4. 安装APK（直接使用虚拟机共享的K盘路径）
& $adb install -r "K:\NFC\NFCApp\app\build\outputs\apk\debug\app-debug.apk"

# 5. 启动应用
& $adb shell am start -n com.nfc.app/.MainActivity

# ========================================
# 完成！应用应该已经在手机上启动了
# ========================================
