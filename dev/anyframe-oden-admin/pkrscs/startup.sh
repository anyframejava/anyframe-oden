WAR=anyframe-oden-admin.war
OPTION="-Xmx256m -XX:NewRatio=1 -XX:MinHeapFreeRatio=20 -XX:MaxHeapFreeRatio=25"
PORTS="oden.db.port=9001 oden.port=9860"
ARGS=--httpPort=9880
nohup java $OPTION -jar $WAR $PORTS $ARGS > ./logs/oden_admin.out &