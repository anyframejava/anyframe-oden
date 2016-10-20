set WAR=anyframe-oden-admin.war
set PORTS=oden.db.port=9001 oden.port=9860
set ARGS=--httpPort=9880
java -jar %WAR% %PORTS% %ARGS%
