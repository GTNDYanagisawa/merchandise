#!/bin/sh
# -----------------------------------------------------------------------------
# Run Script for the CATALINA Server
#

DIR=`dirname $0`
TOMCAT_DIR="${DIR}/tomcat"

MODE=$1

if [ "$MODE" = "" ] ; then
	MODE="run"
fi

case $MODE in
	"minimal" | "-m" )
		export WRAPPER_CONF="../conf/wrapper-minimal.conf"
		COMMAND="./catalina.sh run"
		;;
	"debug" | "-d" )
		export WRAPPER_CONF="../conf/wrapper-debug.conf"
		COMMAND="./catalina.sh run"
		;;
	"jprofiler" | "-j" )
		export WRAPPER_CONF="../conf/wrapper-jprofiler.conf"
		COMMAND="./catalina.sh run"
		;;
    "version" | "-v" )
		COMMAND="java -cp ../lib/catalina.jar org.apache.catalina.util.ServerInfo"
		;;
	* )
		COMMAND="./catalina.sh ${MODE}"
		;;
esac

cd ${TOMCAT_DIR}/bin
exec $COMMAND
