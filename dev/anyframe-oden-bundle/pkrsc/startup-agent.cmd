@echo off

:: [application startup script]
::	APPNAME : application name
:: 	APP_VERSION : application version 
:: 	INI : application config file ( http.port set)
::	JAVA_BIN : java full path
:: 	JVM_OPTION : N/A
::	APP_CMD : application execution command
::	INFO_FILE : excution information file name 

set APPNAME=ODEN_AGENT
set APP_VERSION=2.6.2
set INI=../conf/agent.ini
set JAVA_BIN=C:\Program Files\Java\jdk1.7.0_80\bin\java
set JVM_OPTION=-Xmx32m -XX:NewRatio=1 -XX:MinHeapFreeRatio=20 -XX:MaxHeapFreeRatio=25
set APP_CMD="%JAVA_BIN%" %JVM_OPTION% -jar oden-%APP_VERSION%.jar conf/agent.ini
set INFO_FILE=%APPNAME%.txt


:: check exist ini file 
IF EXIST %INI% goto :READ_INI
echo [CANCELED] %INI% File Not Exist && pause && goto END

:: read http listen port from ini file 
:READ_INI
For /F "tokens=1,* delims== " %%A IN (%INI%) DO ( IF "%%A"=="http.port"  set PORT=%%B )	
IF NOT "%PORT%" =="" goto :CHECK PORT
echo [CANCELED] http.port property not set %INI% && pause && goto END

:: check port in use.
:CHECK PORT
set PORT_LISTEN=NO
netstat -na -p TCP | findstr "%PORT%" 2> NUL | %WINDIR%\system32\find "LISTENING" && set PORT_LISTEN=YES
if /I "%PORT_LISTEN%"=="NO" goto :SAVE_START_INFO
echo [CANCELED] Already %APPNAME% %PORT%PORT LISTENING. && pause && goto END

:: save process info
:SAVE_START_INFO
IF EXIST %INFO_FILE% MOVE /Y %INFO_FILE% %INFO_FILE%_backup.txt > NUL
:: print username
for /f %%i in ('whoami') do set user=%%i
:: save process info
echo Start %APPNAME% v%APP_VERSION% USING PORT %PORT% > %INFO_FILE%
echo ======================================== >> %INFO_FILE% & echo. >> %INFO_FILE% 
echo [START USER] >> %INFO_FILE% & echo %user% >> %INFO_FILE% & echo. >> %INFO_FILE%
echo [START DATE] >> %INFO_FILE% & echo %date% %time% >> %INFO_FILE% & echo. >> %INFO_FILE%
echo [JAVA VERSION] >> %INFO_FILE% & "%JAVA_BIN%" -version 2>> %INFO_FILE% & echo. >> %INFO_FILE%
echo [CMD] >> %INFO_FILE% & echo %APP_CMD% >> %INFO_FILE%
type %INFO_FILE%

:: execution script
:EXEC
%APP_CMD%

:END
