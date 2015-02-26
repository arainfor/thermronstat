#!/bin/sh

cd ${THERMOSTAT_HOME}
. ${THERMOSTAT_HOME}/thermostat.common

CONFIG="--config=${THERMOSTAT_HOME}/thermostat.properties"

LOG="-Dlogback.configurationFile=${THERMOSTAT_HOME}/logback.xml"
PACKAGE="com.arainfor.thermostat"

java -cp "${THERMOSTAT_HOME}/lib/*" ${DEBUG} ${LOG} ${PACKAGE}.QuickStat ${CONFIG}