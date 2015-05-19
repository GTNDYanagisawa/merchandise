@echo off
SET BASE_DIR=%~dp0
SET CORE_DIR=%BASE_DIR%/ext/core
SET CORE_LIB=%CORE_DIR%/lib
SET BOOTSTRAP_DIR=%BASE_DIR%/bootstrap

SET LIKEY_LIB=%CORE_LIB%/likey-no-iaik-1.0.3.jar
SET IO_UTILS_LIB=%CORE_LIB%/commons-io-2.4.jar
SET COMMONS_LANG_LIB=%CORE_LIB%/commons-lang-2.6.jar
SET LOG_4J_LIB=%CORE_LIB%/log4j-1.2.17.jar
SET SPRING_LIB=%CORE_LIB%/spring-core-3.2.8.RELEASE.jar;%CORE_LIB%/spring-context-3.2.8.RELEASE.jar;%CORE_LIB%/spring-beans-3.2.8.RELEASE.jar
SET CORE_CLASSES=%CORE_DIR%/classes
SET CORE_RESOURCES=%CORE_DIR%/resources
SET CORE_SERVER=%CORE_DIR%/bin/coreserver.jar
SET BOOTSTRAP_CLASSES=%BOOTSTRAP_DIR%/classes
SET BOOTSTRAP_JAR=%BOOTSTRAP_DIR%/bin/ybootstrap.jar

java.exe -classpath "%classpath%";"%LIKEY_LIB%";"%IO_UTILS_LIB%";"%COMMONS_LANG_LIB%";"%LOG_4J_LIB%";"%SPRING_LIB%";"%CORE_SERVER%";"%CORE_CLASSES%";"%BOOTSTRAP_JAR%";"%BOOTSTRAP_CLASSES%";"%CORE_RESOURCES%"^
 -Dpcd.home=%BASE_DIR% de.hybris.platform.licence.sap.HybrisAdmin %1 %2 %3 %4
