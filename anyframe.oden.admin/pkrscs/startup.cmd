set WAR=anyframe.oden.admin.war
set OPTION=-Xmx256m -XX:NewRatio=1 -XX:MinHeapFreeRatio=20 -XX:MaxHeapFreeRatio=25
set ARGS=--httpPort=9880

java %OPTION% -jar %WAR% %ARGS%
