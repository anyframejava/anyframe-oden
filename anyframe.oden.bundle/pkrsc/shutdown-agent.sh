PRG="$0"

while [ -h "$PRG" ] ; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done
 
PRGDIR=`dirname "$PRG"`
ODENF=`ls $PRGDIR | grep jar`

for pno in `ps -C | grep -u agent.ini$ | awk '{print $1}'` 
do
	echo "Shutdown Anyframe Oden Agent"
	kill -9 ${pno}	
	exit 1
done
