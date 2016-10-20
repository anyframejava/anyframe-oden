@echo off
fop.bat -c %DOCBOOK_HOME%\conf\fop-config.xml -fo %1 -pdf %2
