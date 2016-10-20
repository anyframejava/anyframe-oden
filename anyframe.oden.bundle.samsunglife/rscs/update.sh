# vars
BAKDIR="_bak"
PATCH=$1

echo "PATCH name: $PATCH"

# methods
check_owner() {
if [ ! -O "." ]; then
  echo "permission denied."
  exit 1
fi
}

check_patch_file() {
if [ ! -f "${PATCH}" ]; then
  echo "Couldn't find any patch files: ${PATCH}"
  exit 1
fi
}

backup() {
if [ ! -d "$BAKDIR" ]; then
  mkdir $BAKDIR
fi

DATE=`date "+%Y%m%d%H%M%S"`
tar cf - bin bundle startup.sh | gzip -c > ${BAKDIR}/oden-${DATE}.tar.gz 
}

kill_proc() {
ODEN=`ls bin/oden*.jar`
ps -ef | grep $ODEN | awk '{ print $2; }' | xargs kill -9
}

rm_dirs(){
rm -Rf bin
rm -Rf bundle
}

extract_patch(){
#tar -xzf ${PATCH}
gunzip < $PATCH | tar xf - 
}

start_oden(){
./startup.sh
}

# main
check_owner

check_patch_file

backup

kill_proc

rm_dirs

extract_patch

start_oden

##################################
#
# written by joon1k.kim@samsung.com
#

