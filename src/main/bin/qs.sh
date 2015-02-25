#!/bin/sh

cd ${THERMOSTAT_HOME}
echo "$!" > pid

. ${THERMOSTAT_HOME}/thermostat.common

CONFIG="--config=${THERMOSTAT_HOME}/thermostat.properties"

LOG="-Dlogback.configurationFile=${THERMOSTAT_HOME}/config/logback.xml"
PACKAGE="com.arainfor.thermostat"

java -cp "/home/pi/thermostat/lib/*" $DEBUG $LOG $PACKAGE.QuickStat $CONFIG&

