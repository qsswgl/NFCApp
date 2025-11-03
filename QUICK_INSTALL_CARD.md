# ⚡ Java 17 安装 - 快速参考卡（可打印）

## 🎯 3 个安装选项（选一个）

### ✅ 选项 1：最快（Windows 11）
```
winget install Oracle.JDK.17
```
**耗时：5-10 分钟**
**优点：最简单，自动设置环境变量**

---

### ✅ 选项 2：最稳定（推荐）
1. 访问：https://corretto.aws/downloads/latest/
2. 下载：Windows x64 JDK (ZIP)
3. 保存到：C:\Users\[YourName]\Downloads\
4. 运行：`.\install-java-manual.bat`

**耗时：10-15 分钟**
**优点：最可靠，支持所有 Windows 版本**

---

### ✅ 选项 3：包管理器
```
choco install openjdk17 -y
```
**耗时：10-15 分钟**
**优点：自动化程度高**

---

## 📋 步骤流程

```
1. 选择上面的一个选项安装
   ↓
2. 重启电脑（⚠️ 必须！）
   ↓
3. 验证：java -version
   ↓
4. 运行检查脚本
   ↓
5. 开始开发！
```

---

## ✓ 验证安装

```powershell
java -version
javac -version
echo %JAVA_HOME%
```

**成功标志：有版本输出且无错误**

---

## 🔧 如果出问题

| 问题 | 解决方案 |
|------|--------|
| 命令找不到 | 重启电脑后再试 |
| 权限不足 | 以管理员身份运行 |
| 找不到 Java | 检查 JAVA_HOME 环境变量 |
| winget 不存在 | Windows 10？用选项 2 或 3 |

---

## 📁 所有脚本文件

位置：`k:\NFC\NFCApp\`

| 文件 | 用途 |
|------|------|
| install-java-manual.bat | 自动安装 Java |
| Check-System-Ready.bat | 检查系统 |
| Setup-LocalProperties.bat | 配置 SDK |
| check_env.bat | 验证环境 |

---

## 🎉 完成后

```powershell
cd k:\NFC\NFCApp
code .
```

在 VS Code 中按 `Ctrl+Shift+P` → Tasks: Run Task → Build, Install and Run

---

**打印这张卡片，贴在显示器旁边！** 📌

---

**现在就开始吧！** 🚀
