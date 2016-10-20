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
ARGS="-XX:NewRatio=1 -XX:MinHeapFreeRatio=20 -XX:MaxHeapFreeRatio=25"

ODENF=`ls ${PRGDIR}/bin | grep jar`
/opt/java6/bin/IA64N/java $ARGS -jar ${PRGDIR}/bin/${ODENF} &

echo "############################################################\n"
echo " Anyframe Open Deployment ENvironment has been started.\n"
echo " See the manual for connect ODEN shell or GUI environment.\n"
echo " \n"
echo " Copyright (C) 2009 SAMSUNG SDS CO., Ltd.\n"
echo " All rights reserved."
echo "############################################################\n"