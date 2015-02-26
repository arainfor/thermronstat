#!/bin/sh
. ${THERMOSTAT_HOME}/thermostat.common
LOG="-Dlogback.configurationFile=${THERMOSTAT_HOME}/logback.xml"
java ${DEBUG} ${LOG} -cp "${THERMOSTAT_HOME}/lib/*" "com.arainfor.thermostat.logReader.StatusLogReader" $1 $2 $3 $4 $5
