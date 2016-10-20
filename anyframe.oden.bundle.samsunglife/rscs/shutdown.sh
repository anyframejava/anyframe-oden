#!/usr/bin/ksh
ODENF=`ls bin | grep jar`
ps -ef | grep bin/${ODENF} | awk '{ print $2; }' | xargs kill -9

echo "############################################################\n"
echo " Anyframe Open Deployment ENvironment has been finished.\n"
echo " See the manual for connect ODEN shell or GUI environment.\n"
echo " \n"
echo " Copyright (C) 2009 SAMSUNG SDS CO., Ltd.\n"
echo " All rights reserved."
echo "############################################################\n"
