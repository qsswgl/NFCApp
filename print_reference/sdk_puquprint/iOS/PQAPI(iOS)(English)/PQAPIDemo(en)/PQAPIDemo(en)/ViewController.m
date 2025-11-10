//
//  ViewController.m
//  PQAPIDemo(en)
//
//  Created by Mac on 2022/2/26.
//

#import "ViewController.h"
#import "PQAPI/PQAPI.h"
#import <CoreBluetooth/CoreBluetooth.h>

@interface ViewController ()<UITableViewDelegate,UITableViewDataSource>
{
    UIImageView * printImage;
}
@property(nonatomic,strong)UITableView * bleTableView;  // Bluetooth list
@property(nonatomic,strong)UILabel * BTConnectLabel;
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
    
    // Bluetooth connection status
    self.BTConnectLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, statusHeight, screenWidth, 40)];
    self.BTConnectLabel.text = @"Not connected";
    self.BTConnectLabel.textAlignment = NSTextAlignmentCenter;
    [self.view addSubview:self.BTConnectLabel];
    
    // Picture to print
    printImage = [[UIImageView alloc] initWithFrame: CGRectMake(30, statusHeight + 50, screenWidth-60, (screenWidth-60)*3/5)];
    printImage.image = [UIImage imageNamed:@"demojpg"];
    printImage.layer.borderWidth = 1;
    printImage.layer.borderColor = [UIColor colorWithRed:0.9 green:0.9 blue:0.9 alpha:1].CGColor;
    [self.view addSubview:printImage];

    // Print button
    UIButton * printButton = [[UIButton alloc] initWithFrame:CGRectMake(30, screenHeight - 80, screenWidth-60, 50)];
    [printButton addTarget:self action:@selector(printButtonTouch) forControlEvents:UIControlEventTouchUpInside];
    printButton.layer.cornerRadius = 8;
    [printButton setTitle:@"Print" forState:UIControlStateNormal];
    printButton.titleLabel.font = [UIFont systemFontOfSize:20];
    printButton.backgroundColor = UIColor.blueColor;
    [self.view addSubview:printButton];
    // Bluetooth list
    self.bleTableView.frame = CGRectMake(30, CGRectGetMaxY(printImage.frame) + 10, screenWidth-60, CGRectGetMinY(printButton.frame)-(CGRectGetMaxY(printImage.frame) + 30));
    [self.view addSubview:self.bleTableView];
    
    UIView * BTHeadView  = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.bleTableView.frame.size.width, 45)];
    self.bleTableView.tableHeaderView = BTHeadView;
    UILabel * titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, BTHeadView.frame.size.width, BTHeadView.frame.size.height)];
    titleLabel.textAlignment = NSTextAlignmentCenter;
    titleLabel.text = @"Bluetooth list";
    [BTHeadView addSubview:titleLabel];
    
    UIButton * refreshBtn = [[UIButton alloc] initWithFrame:CGRectMake(BTHeadView.frame.size.width-85, 0, 85, BTHeadView.frame.size.height)];
//    refreshBtn.backgroundColor = UIColor.blueColor;
    [refreshBtn addTarget:self action:@selector(refreshBtnTouch) forControlEvents:UIControlEventTouchUpInside];
    [refreshBtn setTitle:@"Refresh" forState:UIControlStateNormal];
    [refreshBtn setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
    [BTHeadView addSubview:refreshBtn];
    
    [PQAPI enableProgress:YES];
    [PQAPI discoveredPeripheralBlock:^{
        [self.bleTableView reloadData];
    }];
    [PQAPI didReadPrinterStateHandler:^(int code, NSString *message) {
        NSLog(@"%d ----  %@",code,message);
        if (code == 0x30) {
            self.BTConnectLabel.text = @"Not connected";
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
            NSLog(@"Connection successful %@",[PQAPI getConnectingPrinterName]);
        }
        else {
            NSLog(@"Connection failed");
        }
    }];
    
}

@end

