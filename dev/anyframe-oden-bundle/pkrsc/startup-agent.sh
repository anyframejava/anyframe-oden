#!/bin/sh
PRG="$0"

while [ -h "$PRG" ] ; do
  ls=$(ls -ld "$PRG")
  link=$(expr "$ls" : '.*-> \(.*\)$')
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=$(dirname "$PRG")/"$link"
  fi
done
 
JAVA_BIN=java
INI='../conf/agent.ini'
APPNAME='ODEN_AGENT'
APP_VERSION='2.6.2'
INFO_FILE=${APPNAME}.txt
USER=$(whoami)

PRGDIR=$(dirname "$PRG")
ODENF=$(ls $PRGDIR | grep jar)
ARGS="-Xmx32m -XX:NewRatio=1 -XX:MinHeapFreeRatio=20 -XX:MaxHeapFreeRatio=25 -Djava.security.egd=file:/dev/urandom"

if [ -e "$INI" ]; then
  PORT=$(awk -F "=" '/http.port/ {print $2}' "$INI")
  if [ -z "$PORT" ]; then
    echo "[CANCELED] http.port property not set ${INI}"
    exit 1
  else
    PORT_LISTEN=$(netstat -na TCP | grep ${PORT} | awk '{print $6}' | grep "LISTEN")
    if [ "${PORT_LISTEN}" = "LISTEN" ];  then
      echo "Already ${APPNAME} ${PORT} Port Listening."
      exit 1
    else
      if [ -e "${INFO_FILE}" ]; then
        mv ${INFO_FILE} ${INFO_FILE}_backup.txt
      fi
      echo Start ${APPNAME} v${APP_VERSION} USING PORT ${PORT} > ${INFO_FILE}
      echo ======================================= >> ${INFO_FILE} && echo >> ${INFO_FILE}
      echo [START USER] >> ${INFO_FILE} && echo ${USER} >> ${INFO_FILE} && echo >> ${INFO_FILE}
      echo [START DATE] >> ${INFO_FILE} && date >> ${INFO_FILE} && echo >> ${INFO_FILE}
      echo [JAVA VERSION] >> ${INFO_FILE} && ${JAVA_BIN} -version 2>> ${INFO_FILE} && echo >> ${INFO_FILE}
      echo [CMD] >> ${INFO_FILE} && echo ${JAVA_BIN} $ARGS -jar ${PRGDIR}/${ODENF} conf/agent.ini >> ${INFO_FILE} 
    fi 
  fi 
else
  echo "[CANCELED] ${INI} File Not Exist"
  exit 1
fi

${JAVA_BIN} $ARGS -jar ${PRGDIR}/${ODENF} conf/agent.ini &
