WAR=anyframe-oden-admin.war
OPTION="-Xmx256m -XX:NewRatio=1 -XX:MinHeapFreeRatio=20 -XX:MaxHeapFreeRatio=25"
ARGS=--httpPort=9880
nohup java $OPTION -jar $WAR $ARGS > ./logs/oden_admin.out &