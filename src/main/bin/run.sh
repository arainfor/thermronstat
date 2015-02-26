#!/bin/sh

cd ${THERMOSTAT_HOME}

. ${THERMOSTAT_HOME}/thermostat.common

CONFIG="--config=${THERMOSTAT_HOME}/thermostat.properties"

LOG="-Dlogback.configurationFile=${THERMOSTAT_HOME}/logback.xml"
PACKAGE="com.arainfor.thermostat.daemon"

#java -cp "/home/pi/thermostat/lib/*" $DEBUG $LOG $PACKAGE.ThermometersThread $CONFIG
#java -cp "/home/pi/thermostat/lib/*" $DEBUG $LOG $PACKAGE.StatusThread $CONFIG
#java -cp "/home/pi/thermostat/lib/*" $DEBUG $LOG $PACKAGE.HvacMonitor $CONFIG

#2>/dev/null 1>&2 java -cp "/home/pi/thermostat/lib/*" $LOG $PACKAGE.ControlThread $CONFIG&
#2>/dev/null 1>&2 java -cp "/home/pi/thermostat/lib/*" $LOG $PACKAGE.StatusThread $CONFIG&

#java -cp "/home/pi/thermostat/lib/*" -Dlogback.configurationFile=/home/pi/thermostat/config/logback.xml com.arainfor.thermostataemon.StatusThread --config=/home/pi/nodeThermostat/thermostat.properties
#java -cp "/home/pi/thermostat/lib/*" $DEBUG $LOG $PACKAGE.ThermometersThread $CONFIG&

2>/dev/null 1>&2 LOGPATH="${THERMOSTAT_HOME}/log" java -cp "${THERMOSTAT_HOME}/lib/*" ${DEBUG} ${LOG} ${PACKAGE}.HvacMonitor ${CONFIG}&
myPid=$!
echo myPid is ${myPid}
echo ${myPid} > ${THERMOSTAT_HOME}/pid
