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
ARGS="-Xmx32m -XX:NewRatio=1 -XX:MinHeapFreeRatio=20 -XX:MaxHeapFreeRatio=25 -Djava.security.egd=file:/dev/urandom"

ODENF=`ls $PRGDIR | grep jar`
java $ARGS -jar ${PRGDIR}/${ODENF} conf/agent.ini &