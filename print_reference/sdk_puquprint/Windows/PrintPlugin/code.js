var printername = "POSTEK wewin 268 200DPI"; //POSTEK wewin 268 200DPI
var fontname = "宋体";
var Darkness = 18;
var Speed = 5;
var list;
var labelTyped = "";
var p_strxml = "";
var obj;
var xmldocs;
var Tx;
var Ty;
var Clearance;
var font;
/*print math*/
function PQPrint() {

	window.oldOnError = window.onerror;
	window._command = 'PQprint.RunProgX';
	window.onerror = function(err) {
		if(err.indexOf('Automation') != -1) {
			alert('执行' + window._command + '过程中\n\r\n\r无法找到路径或被您禁止运行本地文件！');
			alert('您载未安装所需要的浏览器插件，请下安装');
			//window.open('http://www.baidu.com/');
			return true;
		} else {
			alert("错误");
			return false;
		}
	};
	a: String;
	obj = new ActiveXObject('PQprint.RunProgX');

}

function Print() {

	LoadPrint();
}
/* tool function */
function GetLen(str) {

	if(str == undefined) {
		return 0;
	} else {
		if(str != "")
			return str.replace(/[^\x00-\xff]/g, "**").length;
		else
			return 0;
	}
}
//function SetList() {
//    list = window.dialogArguments; 
//
//}
/* 加载XML */
function loadXML(xmlString) {

	var xmlDoc = null;
	if(!window.DOMParser && window.ActiveXObject) {
		var xmlDomVersions = ['MSXML.2.DOMDocument.6.0',
			'MSXML.2.DOMDocument.3.0', 'Microsoft.XMLDOM'
		];
		for(var i = 0; i < xmlDomVersions.length; i++) {
			try {
				xmlDoc = new ActiveXObject(xmlDomVersions[i]);
				xmlDoc.async = false;
				xmlDoc.loadXML(xmlString);
				break;
			} catch(e) {}
		}
	} else if(window.DOMParser && document.implementation &&
		document.implementation.createDocument) {
		try {
			domParser = new DOMParser();
			xmlDoc = domParser.parseFromString(xmlString, 'text/xml');
		} catch(e) {}
	} else {
		return null;
	}
	return xmlDoc;
}

function LoadPrint() {
	DoLabelPrint(p_strxml);
}
/**
 * 预览入口
 */
function Load(strxml) {
	p_strxml = strxml;
	//Getperinterlist();
	PQPrint();
	ViewPrint(p_strxml);
}

function Getperinterlist() {
	var list = "";
	try {
		list = printUtil.GetPrintName();
	} catch(e) {
		//alert("请先安装打印插件！");
		return;
	}
	var parray = list.split(',');
	var pelement = document.getElementById("printername");
	for(var k = 0; k < parray.length; k++) {
		var pname = parray[k];
		if(pname.length > 0)
			pelement.options.add(new Option(pname, pname));
	}
}

function chage() {
	preview.innerHTML = "";
	ViewPrint(p_strxml);

}
//计算字节长度
function byteLength(str) {
	var byteLen = 0,
		len = str.length;
	if(!str) return 0;
	for(var i = 0; i < len; i++)
		byteLen += str.charCodeAt(i) > 255 ? 2 : 1;
	return byteLen;
}

function ViewPrint(strxml) {

	var xmldoc = loadXML(strxml);
	// var Textelement = xmldoc.getElementsByTagName("Data");
	var Textelements = xmldoc.getElementsByTagName("Print");
		//alert(Textelements[0].getElementsByTagName("Text")[0].firstChild.nodeValue);
		//alert(Textelements.length);

	//循环每一张标签
	var text = "";
	//text+="<center>";
	var t = 110;
	for(var i = 0; i < Textelements.length; i++) {

		labelTyped = Textelements[i].getElementsByTagName("CodeType")[0].firstChild.nodeValue;

		var Code_elements = Textelements[i].getElementsByTagName("QRCode");

		var Text_elements = Textelements[i].getElementsByTagName("Text");

		
		if(labelTyped == "50_25") {

			var Code = "空";
			var top = "top:" + "200" + "px;";
			text += "<div style=\"position:absolute; background-image:url('labelimage/50_25.jpg');width:480px;height:304px;left:240px; " + top + "\">";
			t += 200;

			var text0 = "";
			var text1 = "";
			var text2 = "";
			var text3 = "";
			var k = 0;
			if(Text_elements.length > 0) {
				text0 = Text_elements[0].childNodes[0].nodeValue;
				//alert(Text_elements[0].childNodes[0].nodeValue);
			}
			if(Text_elements.length > 1) {
				text1 = Text_elements[1].childNodes[0].nodeValue;
			}
			if(Text_elements.length > 2) {
				text2 = Text_elements[2].childNodes[0].nodeValue;
			}
			if(Text_elements.length > 3) {
				text3 = Text_elements[3].childNodes[0].nodeValue;
			}

			var fonts = 18 * 0.352 * 4;
			var x1 = 24;
			var y1 = 24;
			var Tx1 = "left:" + x1 + "px;";
			var Ty1 = "top:" + y1 + "px;";

			var x2 = 200;
			var y2 = 98;
			var Tx2 = "left:" + x2 + "px;";
			var Ty2 = "top:" + y2 + "px;";

			var x3 = 24;
			var y3 = 240;
			var Tx3 = "left:" + x3 + "px;";
			var Ty3 = "top:" + y3 + "px;";

			var x4 = 200;
			var y4 = 133;
			var Tx4 = "left:" + x4 + "px;";
			var Ty4 = "top:" + y4 + "px;";

			var font0 = "font-size:" + fonts + "px;";
			text += "<div  style=\" position:absolute; " + Tx1 + " " + Ty1 + " " + font0 + " \">" + text0 + "</div>";
			text += "<div  style=\" position:absolute; " + Tx2 + " " + Ty2 + " " + font0 + " \">" + text1 + "</div>";
			text += "<div  style=\" position:absolute; " + Tx3 + " " + Ty3 + " " + font0 + " \">" + text2 + "</div>";
			text += "<div  style=\" position:absolute; " + Tx4 + " " + Ty4 + " " + font0 + " \">" + text3 + "</div>";

			text += "</div>";

		}

	}
	//text+="</center>";
	var preview = document.getElementById("preview");
	preview.innerHTML += text;

}

function loadXMLDoc(dname) {
	try //Internet Explorer
	{
		xmldocs = new ActiveXObject("Microsoft.XMLDOM");
	} catch(e) {
		try //Firefox, Mozilla, Opera, etc.
		{
			xmldocs = document.implementation.createDocument("", "", null);
		} catch(e) {
			alert(e.message)
		}
	}
	try {
		xmldocs.async = false;
		xmldocs.load(dname);
		return(xmldocs);
	} catch(e) {
		alert(e.message)
	}
	return(null);
}

/**
 * 打印入口
 */
function LabelPrint(strxml) {
	//alert(strxml);
	var list = new Array();
	var obj = new Object();
	obj.xml = strxml;
	list.push(obj);
	var openUrl = "HTMLPage.html"; //预览页面
	var targetWindowStyle = "dialogHeight: 550px; dialogWidth: 850px; center: Yes; help: No; resizable: yes; status: yes; scroll:yes";
	showModalDialog(openUrl, list, targetWindowStyle);
}

function PrintTextviews(str, fontheight, fontwidth, writesize, x, y, Fspin, ystep, name, rowcount) {
	var backstr = "";
	var font = fontname;
	var rowlen = writesize / fontwidth;
	var strlen = GetLen(str);
	var begin = 0;
	var newy = y;
	var rcount = 0;
	for(var i = 0; i < str.length; i++) {
		var strcell = str.substr(begin, i - begin + 1);
		if(i == str.length - 1) {
			strcell = str.substr(begin, i - begin + 1);
			backstr += strcell;
			break;
		}
		if(GetLen(strcell) >= rowlen) {
			begin = i + 1;
			y += ystep;
			rcount++;
			backstr += strcell;
			if(rcount == rowcount)
				break;
			else
				var nbsp = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
			backstr += "<br/>" + nbsp;
			continue;
		}
	}
	return backstr;
}

function PrintTextview(str, fontheight, fontwidth, writesize, x, y, Fspin, ystep, name, rowcount) {
	var backstr = "";
	var font = fontname;
	var rowlen = writesize / fontwidth;
	var strlen = GetLen(str);
	var begin = 0;
	var newy = y;
	var rcount = 0;
	for(var i = 0; i < str.length; i++) {
		var strcell = str.substr(begin, i - begin + 1);
		if(i == str.length - 1) {
			strcell = str.substr(begin, i - begin + 1);
			backstr += strcell;
			break;
		}
		if(GetLen(strcell) >= rowlen) {
			begin = i + 1;
			y += ystep;
			rcount++;
			backstr += strcell;
			if(rcount == rowcount)
				break;
			else
				backstr += "<br/>";
			continue;
		}
	}
	return backstr;
}

function DoLabelPrint(strxml) {
	var xmldoc = loadXML(strxml)
	var Textelements = xmldoc.getElementsByTagName("Print");

	//循环每一张标签
	for(var i = 0; i < Textelements.length; i++) {
		var LabelType_elements = Textelements[i].getElementsByTagName("CodeType");
		var Code_elements = Textelements[i].getElementsByTagName("QRCode");
		var Text_elements = Textelements[i].getElementsByTagName("Text");
		var labelTyped = "";

		if(LabelType_elements != null) labelTyped = LabelType_elements[0].childNodes[0].nodeValue;
		
		if(labelTyped == "50_25") {
			Print_50_25(Code_elements, Text_elements);
		}

	}

	
	
	
	//新增的纸张类型50_25
	function Print_50_25(Code_elements, Text_elements) {
		var shebei = document.getElementById("Dtypes").value;
		obj['OpenPrint'](shebei);
		obj['PageSet'](50, 25);
		obj['BeginDoc']();
		fontheight = 40;
		var p_y = 200;

		//for (var k = 0; k < Text_elements.length; k++) {
		var text0 = "";
		var text1 = "";
		var text2 = "";
		var text3 = "";
		if(Text_elements.length > 0) {
			var text0 = Text_elements[0].childNodes[0].nodeValue;
			p_y = Print_Text_50_25(text0, fontheight, fontheight / 2, 480, 16, 8, 2, fontheight/1.5, "k", 2);
		}
		if(Text_elements.length > 1) {
			var text1 = Text_elements[1].childNodes[0].nodeValue;
			p_y = Print_Text_50_25(text1, fontheight, fontheight / 2, 360, 120, 44, 2, fontheight/1.5, "k", 4);
		}
		if(Text_elements.length > 2) {
			var text2 = Text_elements[2].childNodes[0].nodeValue;
			p_y = Print_Text_50_25(text2, fontheight, fontheight / 2, 320, 16, 160, 2, fontheight/1.5, "k", 2);
		}
		if(Text_elements.length > 3) {
			var text3 = Text_elements[3].childNodes[0].nodeValue;
			p_y = Print_Text_50_25(text3, fontheight, fontheight / 2, 320, 16, 200, 2, fontheight/1.5, "k", 2);
		}
		

		obj['QrcodePrint'](58, 100, 16, 50, 0, text1);
		obj['BarcodePrint'](7,52,1,1,5,150,50,90,'123456');		//条码打印
		obj['LinePrint'](20,20,4,240,0);
		obj['LinePrint'](20,20,4,180,1);
		obj['NewPage']();
		obj['LinePrint'](40,20,4,240,0);
		obj['LinePrint'](40,20,4,180,1);

		obj['EndDoc']();


	}
	
	//新增标签文本打印50_25
	function Print_Text_50_25(str, fontheight, fontwidth, writesize, x, y, Fspin, xstep, name, rowcount) {
		var rowlen = writesize / fontwidth;
		var strlen = GetLen(str);
		var begin = 0;

		var rcount = 0;
		for(var i = 0; i < str.length; i++) {
			var strcell = str.substr(begin, i - begin + 1);
			if(GetLen(strcell) >= rowlen) {
				
				obj['TextPrint'](x, y, 20, 0, 1, '黑体', strcell);
//文本打印  参数依次为：X、Y坐标、字号大小、旋转角度（0、90、180、270）、字体类型（0、1、2、3代表常规、粗体、斜体、粗斜体）、字体名称、文本内容
				y += xstep;
				begin = i + 1;
				rcount++;
				if(rcount == rowcount) break;
				continue;
			}
			if(i == str.length - 1) {
				//y += xstep;
				strcell = str.substr(begin, i - begin + 1);
				obj['TextPrint'](x, y, 20, 0, 1, '黑体', strcell);

				break;
			}
		}

		return y;
	}
	

	function XML() {

	}
}