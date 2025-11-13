# 故障排查指南

## 🔍 当前状态检查

### 立即执行诊断脚本
```powershell
cd K:\NFC\NFCApp
.\diagnose-issue.bat
```

---

## 常见失败原因及解决方案

### ❌ 情况1: 设备未连接

**现象**: `adb devices` 显示为空

**解决方案**:
```powershell
# 1. 检查USB连接
# 2. 重新插拔USB线
# 3. 在手机上重新授权USB调试
# 4. 重启ADB服务
.\adb.exe kill-server
.\adb.exe start-server
.\adb.exe devices
```

---

### ❌ 情况2: 找不到打印机

**现象**: 提示 "未找到已配对的 AQ-V 打印机"

**可能原因**:
1. 打印机未配对
2. 打印机名称不以 AQ-V 开头
3. 蓝牙权限未授予

**解决方案**:

**步骤1: 检查打印机配对**
```powershell
# 查看已配对设备
.\adb.exe shell dumpsys bluetooth_manager | findstr -i "bond"
```

**步骤2: 手动配对打印机**
1. 打开手机 "设置"
2. 进入 "蓝牙"
3. 确保蓝牙已开启
4. 点击 "搜索设备" 或 "扫描"
5. 找到 AQ-V258007640
6. 点击配对

**步骤3: 验证配对成功**
```powershell
# 应该能看到 AQ-V 设备
.\adb.exe shell settings get global bluetooth_name
.\adb.exe shell dumpsys bluetooth_manager
```

**步骤4: 检查应用权限**
```powershell
# 检查蓝牙权限
.\adb.exe shell dumpsys package com.nfc.app | findstr permission
```

如果缺少权限,手动授权:
1. 设置 → 应用 → NFC缴费应用
2. 权限 → 附近的设备 → 允许
3. 权限 → 蓝牙 → 允许

---

### ❌ 情况3: 连接超时

**现象**: 日志显示 "连接超时 (20秒)"

**可能原因**:
1. 打印机未开机
2. 打印机距离太远
3. 打印机被其他设备占用
4. 蓝牙干扰

**解决方案**:

**步骤1: 检查打印机状态**
- [ ] 打印机电源指示灯是否亮起
- [ ] 打印机是否处于待机状态
- [ ] 打印机纸张是否充足

**步骤2: 重启打印机**
1. 关闭打印机电源
2. 等待10秒
3. 重新开机
4. 等待打印机就绪 (指示灯常亮)

**步骤3: 检查距离和干扰**
- 将打印机移到距离手机1米内
- 远离其他蓝牙设备
- 远离WiFi路由器和微波炉

**步骤4: 取消配对后重新配对**
```powershell
# 在手机蓝牙设置中:
# 1. 找到 AQ-V258007640
# 2. 点击设置图标
# 3. 选择 "取消配对" 或 "忘记设备"
# 4. 重新搜索并配对
```

---

### ❌ 情况4: 打印失败

**现象**: 连接成功但打印失败

**可能原因**:
1. 打印机缺纸
2. 打印机卡纸
3. 打印机电量不足
4. PUQU SDK 错误

**解决方案**:

**步骤1: 检查打印机硬件**
- [ ] 打开打印机仓盖检查纸张
- [ ] 检查是否有卡纸
- [ ] 确认电量充足 (充电或更换电池)

**步骤2: 查看详细日志**
```powershell
# 实时查看打印日志
.\adb.exe logcat -c  # 清空日志
# 然后点击应用中的 "确认" 按钮
.\adb.exe logcat -v time | findstr "PuQuPrinterManager"
```

关键日志:
```
PuQuPrinterManager: ========== 打印到指定打印机 ==========
PuQuPrinterManager: 打印机地址: CD:AC:B0:07:00:72
PuQuPrinterManager: 连接打印机: CD:AC:B0:07:00:72
PuQuPrinterManager: ✓ 打印机已连接
PuQuPrinterManager: 开始打印...
PuQuPrinterManager: ❌ 打印失败  <-- 看这里的错误信息
```

**步骤3: 使用 "打印" 按钮测试**
应用中有2个按钮:
- "确认" 按钮: 使用新的打印机选择功能
- "打印" 按钮: 使用旧的自动打印功能

尝试使用 "打印" 按钮看是否能成功,以排除是新功能的问题还是打印机本身的问题。

---

### ❌ 情况5: 应用崩溃

**现象**: 点击 "确认" 后应用退出

**解决方案**:

**步骤1: 查看崩溃日志**
```powershell
.\adb.exe logcat -d -v time | findstr "AndroidRuntime"
```

查找类似这样的信息:
```
AndroidRuntime: FATAL EXCEPTION
AndroidRuntime: java.lang.NullPointerException
AndroidRuntime: at com.nfc.app.MainActivity.selectPrinterAndPrint
```

**步骤2: 重新安装应用**
```powershell
.\adb.exe uninstall com.nfc.app
.\adb.exe install app\build\outputs\apk\debug\app-debug.apk
```

**步骤3: 清除应用数据**
```powershell
.\adb.exe shell pm clear com.nfc.app
```

---

### ❌ 情况6: 选择对话框不显示

**现象**: 有多个打印机但没有弹出选择框

**解决方案**:

**步骤1: 确认配对了多个 AQ-V 打印机**
```powershell
.\adb.exe logcat -c
# 点击 "确认" 按钮
.\adb.exe logcat -d | findstr "找到.*个已配对的打印机"
```

应该看到类似:
```
PuQuPrinterManager: 找到 2 个已配对的打印机:
PuQuPrinterManager:   [1] AQ-V258007640 - CD:AC:B0:07:00:72
PuQuPrinterManager:   [2] AQ-V258000114 - CD:AC:B0:07:01:83
```

**步骤2: 检查打印机名称**
只有以 "AQ-V" 开头的打印机才会被识别。

如果打印机名称不符,需要修改代码中的过滤条件:

```kotlin
// 在 PuQuPrinterManager.kt 的 getPairedPrinters() 方法中
device.name.startsWith("AQ-V", ignoreCase = true)
// 改为你的打印机名称前缀
```

---

## 📊 收集诊断信息

如果以上方案都无法解决,请执行以下命令并提供输出:

```powershell
# 1. 设备信息
.\adb.exe shell getprop ro.build.version.release  # Android版本
.\adb.exe shell getprop ro.product.model          # 手机型号

# 2. 应用版本
.\adb.exe shell dumpsys package com.nfc.app | findstr "versionName"

# 3. 蓝牙设备列表
.\adb.exe shell dumpsys bluetooth_manager | findstr -i "bond"

# 4. 完整日志
.\adb.exe logcat -d -v time > full_log.txt
# 将 full_log.txt 文件提供给我
```

---

## 🔧 临时解决方案

如果新功能暂时无法使用,可以使用旧的打印功能:

1. 点击 "打印" 按钮 (不是 "确认" 按钮)
2. 这会使用 `autoPrintReceipt()` 方法
3. 但只会打印1份,不是2份

如需打印2份:
1. 点击 "打印" 按钮打印第1份
2. 等待2秒
3. 再次点击 "打印" 按钮打印第2份

---

## 📞 需要提供的信息

请告诉我:

1. **具体的失败现象**:
   - [ ] 找不到打印机
   - [ ] 连接超时
   - [ ] 打印失败
   - [ ] 应用崩溃
   - [ ] 对话框不显示
   - [ ] 其他: _______________

2. **设备状态**:
   - 手机型号: _______________
   - Android版本: _______________
   - 打印机型号: _______________
   - 打印机配对状态: [ ] 已配对 / [ ] 未配对

3. **日志输出**:
   ```
   (粘贴 diagnose-issue.bat 的输出)
   ```

提供这些信息后,我能更准确地帮你解决问题! 🔧
