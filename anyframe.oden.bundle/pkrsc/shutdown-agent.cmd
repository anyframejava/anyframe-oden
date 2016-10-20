@echo off

@echo .
@echo . shutdown-agent.cmd - uninstall script for Oden Agent Service
@echo .


setlocal
@rem note that if JVM not found, service 'does not report an error' when startup fails, although event logged
if "%JAVA_HOME%" == "" set JAVA_HOME=d:\j2sdk1.4.2_10\jre
set JVMDIR=%JAVA_HOME%\jre\bin\server
set JSBINDIR=%CD%
set JSEXE=%JSBINDIR%\JavaService.exe
set SSBINDIR=%JSBINDIR%


@echo . Using following version of JavaService executable:
@echo .
%JSEXE% -version
@echo .


@echo Stopping Anyframe Oden Agent Service... Press Control-C to abort
@pause
@echo .
net stop "Anyframe Oden Agent Service"
@echo .

@echo Un-installing Anyframe Oden Agent Service... Press Control-C to abort
@pause
@echo .
%JSEXE% -uninstall "Anyframe Oden Agent Service"
@echo .

@echo End of script
@pause
