WAR=anyframe.oden.admin.war
for pno in `ps -C | grep -u "$WAR" | awk '{print $1}'` 
do
	echo "Shutdown Anyframe Oden Web Admin"
	kill -9 ${pno}	
	exit 1
done