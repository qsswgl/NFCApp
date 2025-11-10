# BLE蓝牙打印机 - 安装说明

## 方法1: 使用Android Studio（推荐）

1. 打开Android Studio
2. 选择 `File` -> `Open`
3. 选择项目目录: `k:\Print`
4. 等待Gradle同步完成
5. 确保设备已连接（可以在底部看到设备名称）
6. 点击绿色的运行按钮 ▶️ 或按 `Shift+F10`
7. 应用会自动安装到设备并启动

## 方法2: 使用命令行构建

如果你的系统中有Android SDK和Gradle：

### Windows PowerShell:
```powershell
cd k:\Print
.\gradlew.bat assembleDebug
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

### 或使用系统Gradle:
```powershell
cd k:\Print
gradle assembleDebug
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

## 方法3: 手动构建APK

如果上述方法都不可用，请：

1. 在Android Studio中打开项目
2. 选择 `Build` -> `Build Bundle(s) / APK(s)` -> `Build APK(s)`
3. 等待构建完成
4. APK位置: `app\build\outputs\apk\debug\app-debug.apk`
5. 使用adb安装:
   ```
   adb install -r app\build\outputs\apk\debug\app-debug.apk
   ```

## 测试步骤

安装成功后：

1. ✅ 在设备上找到"BLE打印机"应用
2. ✅ 启动应用
3. ✅ 授予蓝牙和位置权限
4. ✅ 确保蓝牙已开启
5. ✅ 点击"开始扫描"
6. ✅ 等待发现打印机设备
7. ✅ 点击设备列表中的打印机
8. ✅ 确认连接
9. ✅ 等待状态变为"就绪(可以打印)"
10. ✅ 点击"测试打印"按钮
11. ✅ 查看打印机是否打印出测试小票

## 故障排除

### 找不到设备
- 确保打印机已开机
- 确保打印机处于配对模式
- 检查蓝牙权限是否已授予
- 尝试重启蓝牙

### 连接失败
- 打印机可能已与其他设备连接
- 尝试重启打印机
- 在手机蓝牙设置中取消配对后重试

### 打印无响应
- 检查打印机纸张
- 确认状态显示为"就绪"
- 查看Logcat日志获取详细错误信息

## 查看日志

如需调试，可以使用adb查看日志：

```powershell
adb logcat | Select-String "BluetoothScanner|BluetoothConnection|MainActivity"
```

或查看完整日志：
```powershell
adb logcat *:E
```

## 当前设备状态

已连接设备: `192.168.1.152:45581`

准备好后可以直接使用Android Studio或上述命令进行构建和安装。
