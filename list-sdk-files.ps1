Get-ChildItem "$env:LOCALAPPDATA\Android\Sdk\cmdline-tools" -Recurse -Depth 3 | Select-Object FullName, Name
