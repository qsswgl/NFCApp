unit Unt_demo;

interface

uses
  Windows, Messages, SysUtils, Variants, Classes, Graphics, Controls, Forms,
  Dialogs, StdCtrls, Buttons, ExtCtrls, printers, acPNG, jpeg, Grids_ts,
  TSGrid;

type
  TForm1 = class(TForm)
    PrintDialog1: TPrintDialog;
    BitBtn1: TBitBtn;
    procedure BitBtn1Click(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  Form1: TForm1;

implementation

uses StdConvs, DateUtils;

{$R *.dfm}

function OpenPrint(name : OleVariant) : boolean;stdcall;External 'PQprint.dll';
function PageSet(length,height: integer):boolean;stdcall;External 'PQprint.dll';
/// <summary>
/// 数据准备  与 EndDoc成对出现
/// </summary>
function BeginDoc :boolean;stdcall;External 'PQprint.dll';
/// <summary>
/// 数据准备结束并发送数据给打印池 与BeginDoc 成对出现
/// </summary>
function EndDoc : boolean;stdcall;External 'PQprint.dll';
function NewPage : boolean;export;stdcall;External 'PQprint.dll';     //分页打印
function WordsPrint(wpot,wx,wy,wsize,wangel,style : integer;wname,wtext : Pchar):boolean;stdcall;External 'PQprint.dll';
//btext 条码内容，btype 条码类型，bheight 条码高度，bshow 条码文本显示，bmodul 条码粗细，btextpos 条码文本位置
function BarcodePrint(ltype, height, show, modul, textpos, left, top, angel: Integer; text: OleVariant): OleVariant; safecall;External 'PQprint.dll';

function QrcodePrint(qtype,qheight,qleft,qtop,qangel : integer;qtext : OleVariant):boolean;stdcall;External 'PQprint.dll';   //二维码打印
procedure TextPrint(x, y, size, angel, style: Integer; name,text: OleVariant);stdcall;External 'PQprint.dll';
function LinePrint(x,y,width,length,ltype : integer):boolean;export;stdcall;External 'PQprint.dll';   //线条打印    长度单位mm*8或者12   (200dot or 300dot)



procedure TForm1.BitBtn1Click(Sender: TObject);
var
  i,x,y : integer;
begin
  OpenPrint('PQ00');
  PageSet(50,25);
  BeginDoc;
  TextPrint(10,20,16,0,0,'宋体','福州璞趣电子科技有限公司');
  TextPrint(100,40,16,90,0,'宋体','福州璞趣电子科技有限公司');
  BarcodePrint(7,52,1,2,5,100,50,90,'123456');
  x := 10;
  for i := 0 to 1 do
  begin
    LinePrint(x,10,4,180,1);
    LinePrint(x,10,4,240,0);
    QrcodePrint(58,128,180,40,270,'123');
    if i <> 1 then
    begin
      NewPage;
      x := x + 20;
    end;

  end;
  EndDoc;
end;


end.
