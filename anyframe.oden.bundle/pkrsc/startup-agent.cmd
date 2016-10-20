@echo off

@echo .
@echo . startup-agent.cmd - executable using oden Agent
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


@echo Installing Anyframe Oden Agent Service... Press Control-C to abort
@pause
@echo .
%JSEXE% -install "Anyframe Oden Agent Service" %JVMDIR%\jvm.dll -Djava.class.path=%SSBINDIR%\oden-2.1.0.jar -Xmx32m -XX:NewRatio=1 -XX:MinHeapFreeRatio=20 -XX:MaxHeapFreeRatio=25 -start anyframe.oden.bundle.Oden -params conf/agent.ini memory -out %JSBINDIR%\agentout.log -err %JSBINDIR%\agenterr.log -current %JSBINDIR% -manual -description "Provides Oden Agent Service"
@echo .


@echo Starting Anyframe Oden Agent Service... Press Control-C to abort
@pause
@echo .
net start "Anyframe Oden Agent Service"
@echo .

@echo End of script
@pause
