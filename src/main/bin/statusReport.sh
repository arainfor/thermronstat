#!/bin/sh
. ${THERMOSTAT_HOME}/thermostat.common
java ${DEBUG} -cp "${THERMOSTAT_HOME}/lib/*" "com.arainfor.thermostat.logReader.StatusLogReader" $1 $2 $3 $4 $5
