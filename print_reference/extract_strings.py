import re
import sys

def extract_strings(dex_file, min_length=10):
    """从DEX文件中提取可读字符串"""
    with open(dex_file, 'rb') as f:
        data = f.read()
    
    # 搜索包含蓝牙相关关键词的字符串
    keywords = [
        b'ae01', b'ae3b', b'ae30', b'ae3a',
        b'BluetoothGatt', b'Bluetooth', b'GATT',
        b'Characteristic', b'Service',
        b'0000ae', b'UUID',
        b'print', b'Print', b'PRINT',
        b'write', b'Write', b'WRITE',
        b'notify', b'Notify', b'NOTIFY',
        b'esc', b'ESC', b'command'
    ]
    
    # 使用正则表达式提取可打印字符串
    pattern = rb'[\x20-\x7e]{' + str(min_length).encode() + rb',}'
    strings = re.findall(pattern, data)
    
    # 过滤包含关键词的字符串
    relevant_strings = []
    for s in strings:
        s_lower = s.lower()
        for keyword in keywords:
            if keyword.lower() in s_lower:
                relevant_strings.append(s.decode('ascii', errors='ignore'))
                break
    
    return relevant_strings

if __name__ == '__main__':
    dex_file = r'k:\Print\puqu_decompiled\classes.dex'
    strings = extract_strings(dex_file, min_length=8)
    
    print("=== 找到的相关字符串 ===\n")
    for s in sorted(set(strings)):
        print(s)
    
    print(f"\n总共找到 {len(set(strings))} 个相关字符串")
