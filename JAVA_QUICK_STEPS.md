# ⚡ Java 安装快速步骤（5 分钟版）

## 🚀 三个核心步骤

### 1️⃣ 下载（2 分钟）
```
访问：https://www.oracle.com/java/technologies/downloads/
选择：Java SE 17 LTS 或 Java SE 11
下载：Windows x64 Installer
```

### 2️⃣ 安装（2 分钟）
```
双击下载的 .exe 文件
运行：一路点 Next → Install → Close
安装位置：保持默认 C:\Program Files\Java\jdk-17
```

### 3️⃣ 验证（1 分钟）
```powershell
打开 PowerShell
输入：java -version
查看：输出 Java 版本信息（如 java version "17.0.x"）
```

---

## 🔧 设置环境变量（3 分钟）

```
按 Windows+X → 系统 → 高级系统设置 → 环境变量

新建系统变量：
  变量名：  JAVA_HOME
  变量值：  C:\Program Files\Java\jdk-17

编辑 PATH 变量，新建一行：
  %JAVA_HOME%\bin

点击确定保存
```

---

## ✅ 最终验证

```powershell
关闭 PowerShell
重新打开 PowerShell
输入：java -version
查看：成功输出版本信息 ✓
```

---

**总耗时**：8-10 分钟

**下一步**：安装 Android Studio
