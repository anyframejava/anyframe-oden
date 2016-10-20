@echo off

@echo .
@echo . startup.cmd - executable using oden Server
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


@echo Installing Anyframe Oden Server Service... Press Control-C to abort
@pause
@echo .
%JSEXE% -install "Anyframe Oden Server Service" %JVMDIR%\jvm.dll -Djava.class.path=%SSBINDIR%\oden-2.1.0.jar -Xmx256m -XX:NewRatio=1 -XX:MinHeapFreeRatio=20 -XX:MaxHeapFreeRatio=25 -Dfile.encoding=utf-8 -start anyframe.oden.bundle.Oden -out %JSBINDIR%\stdout.log -err %JSBINDIR%\stderr.log -current %JSBINDIR% -manual -description "Provides Oden Server Service"
@echo .


@echo Starting Anyframe Oden Server Service... Press Control-C to abort
@pause
@echo .
net start "Anyframe Oden Server Service"
@echo .

@echo End of script
@pause
