#!/bin/sh
BASE_DIR=`dirname $0`
CORE_DIR="$BASE_DIR/ext/core"
CORE_LIB="$CORE_DIR/lib"
BOOTSTRAP_DIR="$BASE_DIR/bootstrap"

LIKEY_LIB="$CORE_LIB/likey-no-iaik-1.0.3.jar"
IO_UTILS_LIB="$CORE_LIB/commons-io-2.4.jar"
COMMONS_LANG_LIB="$CORE_LIB/commons-lang-2.6.jar"
LOG_4J_LIB="$CORE_LIB/log4j-1.2.17.jar"
SPRING_LIB="$CORE_LIB/spring-core-3.2.8.RELEASE.jar:$CORE_LIB/spring-context-3.2.8.RELEASE.jar:$CORE_LIB/spring-beans-3.2.8.RELEASE.jar"
CORE_CLASSES="$CORE_DIR/classes"
CORE_RESOURCES="$CORE_DIR/resources"
CORE_SERVER="$CORE_DIR/bin/coreserver.jar"
BOOTSTRAP_CLASSES="$BOOTSTRAP_DIR/classes"
BOOTSTRAP_JAR="$BOOTSTRAP_DIR/bin/ybootstrap.jar"

java -classpath ${LIKEY_LIB}:${IO_UTILS_LIB}:${COMMONS_LANG_LIB}:${SPRING_LIB}:${LOG_4J_LIB}:${CORE_SERVER}:${CORE_CLASSES}:${BOOTSTRAP_JAR}:${BOOTSTRAP_CLASSES}:${CORE_RESOURCES} \
    -Dpcd.home=${BASE_DIR} \
    de.hybris.platform.licence.sap.HybrisAdmin $1 $2 $3 $4