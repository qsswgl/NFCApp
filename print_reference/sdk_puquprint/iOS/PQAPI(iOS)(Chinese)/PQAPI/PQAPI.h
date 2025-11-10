//
//  PQAPI.h
//  PQAPI
//
//  Created by Mac on 2021/12/13.
//

#import <UIKit/UIKit.h>
#import <CoreBluetooth/CoreBluetooth.h>

typedef void (^PQDidOpenedPrinterBlock)(BOOL isSuccess);

@interface PQAPI : NSObject


/**
 * @brief   开始搜索蓝牙设备。
 */
+ (void)openScan;

/**
 * @brief   获取搜索到的蓝牙列表。
 */
+ (NSArray<CBPeripheral *> *)getDiscoveredPeripherals;
/**
 * @brief   连接指定名称的打印机。
 *
 * @param   printerName     打印机蓝牙名称。
 * @param   completion      连接打印机后的操作。
 *
 * @note    isSuccess       连接打印机是否成功。
 */
+ (void)openPrinterWithName:(NSString *)printerName completion:(PQDidOpenedPrinterBlock)completion;

/**
 * @brief   连接指定外设。
 *
 * @param   peripheral      打印机蓝牙外设。
 * @param   completion      连接打印机后的操作。
 *
 * @note    isSuccess       连接打印机是否成功。
 */
+ (void)openPrinterWithPeripheral:(CBPeripheral *)peripheral completion:(PQDidOpenedPrinterBlock)completion;

/**
 * @brief   获取当前连接的打印机名称。
 */
+ (NSString *)getConnectingPrinterName;

/**
 * @brief   关闭当前连接的打印机。
 */
+ (void)closePrinter;
/**
 * @brief   连接指定外设。
 
 * @param   discovered      扫描蓝牙设备时，发现蓝牙外设的回调。
 */
+ (void)discoveredPeripheralBlock:(void(^)(void))discovered;

/**
 * @brief  刷新搜索到的蓝牙外设
 */
+ (void)refreshDiscoveredPeripheral;

/**
 * @brief   直接打印图片。
 *
 * @param   image          打印的图片
 * @param   width          打印宽度，单位毫米
 * @param   height         打印高度，单位毫米
 * @param   completion     打印结果回调
 *
 * @note    isSuccess       连接打印机是否成功。
 */
+ (void)printImage:(UIImage *)image width:(NSInteger)width height:(NSInteger)height completion:(void(^)(BOOL isSuccess))completion;

/**
 * @brief   启用进度提示框。
 */
+ (void)enableProgress:(BOOL)enable;


/**
 * @brief   开启提示回调。
 *
 
 0x30    打印机设备断开连接
 0x31    连接打印机设备失败
 * 
 0x50    打印机缺纸
 0x51    打印机盖子打开了
 0x52    打印数据错误
 0x53    打印机电量过低

 *
 */
+ (void)didReadPrinterStateHandler:(void(^)(int code, NSString *message))didReadPrinterStateHandler;

@end
