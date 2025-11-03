# NFC 读写系统 - 快速开始指南

## 📋 项目概览

这是一个 Android NFC 读写系统应用，支持以下核心功能：
- ✅ NFC 标签读取和写入
- ✅ SQLite 数据库存储
- ✅ 表格形式显示读写记录
- ✅ 蓝牙打印机集成
- ✅ 数据上传功能（待配置）

## 🚀 快速开始

### 环境要求
- Android Studio 2022.3.1+
- Android SDK 33+
- JDK 11+
- Gradle 7.3+

### 项目配置

1. **打开项目**
   ```bash
   cd k:\NFC\NFCApp
   # 在 Android Studio 中打开项目
   ```

2. **配置 SDK 路径** (local.properties)
   ```properties
   sdk.dir=C:\\Users\\YourUsername\\AppData\\Local\\Android\\sdk
   ```

3. **同步 Gradle**
   - Android Studio 会自动提示同步
   - 或手动运行：`./gradlew sync`

4. **构建应用**
   ```bash
   ./gradlew build
   ```

5. **运行应用**
   - 连接 Android 设备或启动模拟器
   - 点击 Android Studio 的 Run 按钮
   - 或使用：`./gradlew installDebug`

## 📁 项目结构速览

```
NFCApp/
├── app/                              # 应用模块
│   ├── src/main/
│   │   ├── kotlin/com/nfc/app/
│   │   │   ├── MainActivity.kt       # 主界面 (已实现)
│   │   │   ├── database/            # 数据库模块 (已实现)
│   │   │   ├── nfc/                 # NFC功能 (已实现)
│   │   │   ├── print/               # 打印功能 (已实现)
│   │   │   └── ui/                  # UI组件 (已实现)
│   │   ├── res/                     # 资源文件
│   │   └── AndroidManifest.xml      # 应用清单
│   └── build.gradle.kts             # 应用构建配置
├── build.gradle.kts                 # 项目构建配置
├── settings.gradle.kts
├── README.md                        # 详细文档
└── DEVELOPMENT.md                   # 开发指南
```

## 🔧 核心功能实现状态

| 功能 | 状态 | 说明 |
|------|------|------|
| NFC 读取 | ✅ 完成 | 支持 NDEF 格式标签 |
| NFC 写入 | ✅ 完成 | 支持自动格式化 |
| 数据库 | ✅ 完成 | Room ORM 框架 |
| 表格显示 | ✅ 完成 | RecyclerView + Adapter |
| 蓝牙打印 | ✅ 完成 | 基础通信协议 |
| 数据上传 | ⏳ 待配置 | 需要 API 端点 |
| UI 界面 | ✅ 完成 | 所有布局和资源 |

## 📱 使用流程

### 读取 NFC 标签
1. 点击主界面"读取"按钮
2. 将手机靠近 NFC 标签
3. 自动读取并保存到数据库
4. 记录显示在表格中

### 写入 NFC 标签
1. 输入"卡号"和"车号"信息
2. 点击"写入"按钮
3. 将手机靠近 NFC 标签
4. 数据写入完成并保存

### 打印记录
1. 连接蓝牙打印机（系统设置中配对）
2. 点击"打印"按钮
3. 表格数据发送到打印机

### 上传数据
1. 配置服务器 API 端点
2. 点击"上传"按钮
3. 数据上传到服务器

## 🔐 权限申请

应用需要以下权限（已在 AndroidManifest.xml 中声明）：
- `NFC` - NFC 功能
- `BLUETOOTH` - 蓝牙操作
- `BLUETOOTH_CONNECT` - 蓝牙连接（Android 12+）
- `READ/WRITE_EXTERNAL_STORAGE` - 文件访问

首次运行时，应用会请求必要的运行时权限。

## 🐛 常见问题

### Q: 模拟器是否支持 NFC？
**A:** 不支持。需要使用支持 NFC 的真实 Android 设备进行测试。

### Q: 如何连接蓝牙打印机？
**A:** 
1. 在手机蓝牙设置中搜索打印机
2. 配对成功后，应用会自动检测
3. 或在应用中添加"选择打印机"功能

### Q: 如何配置上传功能？
**A:**
1. 在 `MainActivity.handleUpload()` 中实现 API 调用
2. 创建 `network/` 模块（使用 Retrofit）
3. 配置服务器端点 URL
4. 实现数据序列化和上传逻辑

### Q: 数据库在哪里保存？
**A:** SQLite 数据库文件位于设备的应用数据目录：
```
/data/data/com.nfc.app/databases/nfc_database
```

### Q: 如何导出数据？
**A:** 可使用 Android Studio 的 Device File Explorer：
1. 打开 Device File Explorer
2. 导航到 `/data/data/com.nfc.app/databases/`
3. 下载 `nfc_database` 文件

## 📈 下一步

### 短期 (1-2 周)
- [ ] 实现数据上传 API
- [ ] 添加蓝牙设备选择界面
- [ ] 完善错误处理和日志

### 中期 (2-4 周)
- [ ] 数据导出功能（CSV/PDF）
- [ ] 统计分析页面
- [ ] 搜索和过滤功能

### 长期 (1 个月+)
- [ ] 离线同步
- [ ] 数据加密
- [ ] 云端备份
- [ ] 多语言支持

## 🤝 技术支持

遇到问题？请检查：
1. README.md - 详细文档
2. DEVELOPMENT.md - 开发指南
3. 代码注释 - 各个模块的说明

## 📞 联系方式

如有任何问题或建议，请联系开发团队。

---

**版本** ：1.0
**最后更新** ：2025年11月2日
**状态** ：可以开始开发和测试 ✅
