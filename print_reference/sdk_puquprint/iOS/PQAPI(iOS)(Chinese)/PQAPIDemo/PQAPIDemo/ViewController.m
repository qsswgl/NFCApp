//
//  ViewController.m
//  PQAPIDemo
//
//  Created by Mac on 2021/12/13.
//

#import "ViewController.h"
#import "PQAPI/PQAPI.h"
#import <CoreBluetooth/CoreBluetooth.h>

@interface ViewController ()<UITableViewDelegate,UITableViewDataSource>
{
    UIImageView * printImage;
}
@property(nonatomic,strong)UITableView * bleTableView;  // 蓝牙列表
@property(nonatomic,strong)UILabel * BTConnectLabel;  // 蓝牙列表
@end

@implementation ViewController



-(UITableView *)bleTableView {
    if (_bleTableView == nil) {
        _bleTableView = [[UITableView alloc] init];
        _bleTableView.backgroundColor = [UIColor colorWithRed:0.95 green:0.95 blue:0.95 alpha:1];
        _bleTableView.layer.cornerRadius = 10;
        _bleTableView.delegate = self;
        _bleTableView.dataSource = self;

    }
    return  _bleTableView;
}

- (void)viewDidLoad {
    [super viewDidLoad];

    CGFloat screenWidth = UIScreen.mainScreen.bounds.size.width;
    CGFloat screenHeight = UIScreen.mainScreen.bounds.size.height;
    CGFloat statusHeight =  [UIApplication sharedApplication].statusBarFrame.size.height;
    
    // 蓝牙连接状态
    self.BTConnectLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, statusHeight, screenWidth, 40)];
    self.BTConnectLabel.text = @"未连接";
    self.BTConnectLabel.textAlignment = NSTextAlignmentCenter;
    [self.view addSubview:self.BTConnectLabel];
    
    // 要打印的图片
    printImage = [[UIImageView alloc] initWithFrame: CGRectMake(30, statusHeight + 50, screenWidth-60, (screenWidth-60)*3/4)];
    printImage.image = [UIImage imageNamed:@"demojpg"];
    printImage.layer.borderWidth = 1;
    printImage.layer.borderColor = [UIColor colorWithRed:0.9 green:0.9 blue:0.9 alpha:1].CGColor;
    [self.view addSubview:printImage];

    // 打印按钮
    UIButton * printButton = [[UIButton alloc] initWithFrame:CGRectMake(30, screenHeight - 80, screenWidth-60, 50)];
    [printButton addTarget:self action:@selector(printButtonTouch) forControlEvents:UIControlEventTouchUpInside];
    printButton.layer.cornerRadius = 8;
    [printButton setTitle:@"打印" forState:UIControlStateNormal];
    printButton.titleLabel.font = [UIFont systemFontOfSize:20];
    printButton.backgroundColor = UIColor.blueColor;
    [self.view addSubview:printButton];
    // 蓝牙列表
    self.bleTableView.frame = CGRectMake(30, CGRectGetMaxY(printImage.frame) + 10, screenWidth-60, CGRectGetMinY(printButton.frame)-(CGRectGetMaxY(printImage.frame) + 30));
    [self.view addSubview:self.bleTableView];
    
    UIView * BTHeadView  = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.bleTableView.frame.size.width, 45)];
    self.bleTableView.tableHeaderView = BTHeadView;
    UILabel * titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, BTHeadView.frame.size.width, BTHeadView.frame.size.height)];
    titleLabel.textAlignment = NSTextAlignmentCenter;
    titleLabel.text = @"蓝牙列表";
    [BTHeadView addSubview:titleLabel];
    
    UIButton * refreshBtn = [[UIButton alloc] initWithFrame:CGRectMake(BTHeadView.frame.size.width-60, 0, 60, BTHeadView.frame.size.height)];
    [refreshBtn addTarget:self action:@selector(refreshBtnTouch) forControlEvents:UIControlEventTouchUpInside];
    [refreshBtn setTitle:@"刷新" forState:UIControlStateNormal];
    [refreshBtn setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
    [BTHeadView addSubview:refreshBtn];
    
    [PQAPI enableProgress:YES];
    [PQAPI discoveredPeripheralBlock:^{
        [self.bleTableView reloadData];
    }];
    [PQAPI didReadPrinterStateHandler:^(int code, NSString *message) {
        NSLog(@"%d ----  %@",code,message);
        if (code == 0x30) {
            self.BTConnectLabel.text = @"未连接";
        }
    }];
}

-(void)refreshBtnTouch {
    [PQAPI refreshDiscoveredPeripheral];
}

-(void)printButtonTouch {
    [PQAPI printImage:printImage.image width:40 height:30 completion:^(BOOL isSuccess) {
        if (isSuccess) {
//            [self printButtonTouch];
        }
    }];
}



#pragma mark ---UITableViewDelegate
-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return [PQAPI getDiscoveredPeripherals].count;
}
-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell * cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"cellID"];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    cell.backgroundColor = [UIColor colorWithRed:0.95 green:0.95 blue:0.95 alpha:1];
    cell.textLabel.text = [PQAPI getDiscoveredPeripherals][indexPath.row].name;
    return cell;
}
    
-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    CBPeripheral * peripheral = [PQAPI getDiscoveredPeripherals][indexPath.row];
    
    [PQAPI openPrinterWithPeripheral:peripheral completion:^(BOOL isSuccess) {
        if (isSuccess) {
            self.BTConnectLabel.text = [PQAPI getConnectingPrinterName];
            NSLog(@"连接成功 %@",[PQAPI getConnectingPrinterName]);
        }
        else {
            NSLog(@"连接失败");
        }
    }];
    
//    [PQAPI openPrinterWithName:peripheral.name completion:^(BOOL isSuccess) {
//        if (isSuccess) {
//
//            NSLog(@"连接成功 %@",[PQAPI getConnectingPrinterName]);
//        }
//        else {
//            NSLog(@"连接失败");
//        }
//    }];
}

@end
