# AQ打印机通信协议分析

## 厂家信息
- 公司: 上海璞趣标识设备有限公司
- APP: 璞趣标贴打印 (com.fzpq.print)
- 版本: 1942
- 官网: www.puqulabel.com
- 邮箱: service@puqulabel.com
- 电话: +86-4008-708-022

## 打印机信息
- 型号: AQ
- 设备名: AQ-V258000114(BLE)
- 硬件版本: V1.3
- 固件版本: V3.0.2

## 已知GATT信息
从日志分析得到的AQ打印机BLE特征:

### 服务和特征
1. **Service: 00001800-0000-1000-8000-00805f9b34fb** (Generic Access)
   - Characteristic: 00002a00 (Properties: READ WRITE)

2. **Service: 0000ae30-0000-1000-8000-00805f9b34fb** (自定义服务)
   - Characteristic: 0000ae01 (Properties: WRITE WRITE_NO_RESP) ← 我们尝试过
   - Characteristic: 0000ae02 (Properties: NOTIFY)

3. **Service: 0000ae3a-0000-1000-8000-00805f9b34fb** (自定义服务)
   - Characteristic: 0000ae3b (Properties: WRITE WRITE_NO_RESP) ← 我们尝试过
   - Characteristic: 0000ae3c (Properties: NOTIFY)

## 尝试过的方法

### 方法1: 使用ae01特征 + WRITE_NO_RESPONSE
- 结果: 所有数据发送成功,但打印机无响应

### 方法2: 使用ae3b特征 + WRITE_NO_RESPONSE  
- 结果: 所有数据发送成功,但打印机无响应

### 方法3: 使用ae01特征 + WRITE模式(需要响应)
- 结果: 所有数据发送成功,但打印机无响应

### 方法4: 简化命令 (只发送"TEST")
- 结果: 数据发送成功,但打印机无响应

### 方法5: 添加唤醒命令 (0x10, 0x14, 0x01, 0x00, 0x05)
- 结果: 唤醒命令和打印数据都发送成功,但打印机无响应

### 方法6: 启用NOTIFY + descriptor配置
- 当前正在测试...

## 对比分析
- **GP打印机** (工作正常):
  - 特征UUID: 49535343-8841-43f4-a8d4-ecbe34729bb3
  - 使用WRITE_NO_RESPONSE
  - 标准ESC/POS命令有效

- **AQ打印机** (不工作):
  - 特征UUID: ae01或ae3b (都尝试过)
  - 尝试了WRITE和WRITE_NO_RESPONSE
  - ESC/POS命令似乎无效

## 推测
1. AQ打印机可能不使用标准ESC/POS协议
2. 可能需要特殊的初始化握手序列
3. 可能需要通过NOTIFY通道接收"准备好"信号后才能打印
4. 数据格式可能需要特殊的包装/协议头

## 下一步
需要反编译 `puqu_printer.apk` 来学习正确的通信协议。

重点查找:
- 蓝牙连接相关代码
- GATT特征选择逻辑
- 数据发送格式
- 是否有特殊的协议封装
