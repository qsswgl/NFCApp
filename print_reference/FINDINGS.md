# AQ打印机关键发现

## 从BluetoothDevice配置文件发现

AQ系列打印机的关键属性:
- **androidType**: 6
- **settings**: 0 或 1
- **seriesName**: "AQ系列" 或 "AQ-V系列"

对比其他设备:
- NS系列: androidType = 1 或 6
- M系列: androidType = 10
- Q系列: androidType = 1, 2, 4, 5, 6, 7

## androidType值的含义
从配置文件分析,androidType可能指定了不同的蓝牙通信协议:
- androidType = 1: 可能是经典蓝牙SPP
- androidType = 2: 可能是另一种SPP变体
- androidType = 4: 未知
- androidType = 5: 未知  
- androidType = 6: **可能是BLE (我们的AQ打印机)**
- androidType = 7: 未知
- androidType = 8: 未知
- androidType = 9: 未知
- androidType = 10: 未知

## 下一步
需要反编译DEX文件,查找:
1. 处理androidType=6的代码逻辑
2. BLE通信的具体实现
3. 数据格式转换

建议使用JADX工具反编译DEX文件来查看源代码。
