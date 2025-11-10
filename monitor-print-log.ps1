# 监控打印相关日志
adb -s 192.168.1.152:45581 logcat -v time | Select-String -Pattern "PuQuPrinterManager|NFCApp.*打印|FATAL|AndroidRuntime"
