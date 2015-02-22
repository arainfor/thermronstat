#!/bin/sh

cd ${THERMOSTAT_HOME}
echo "$!" > pid

. ${THERMOSTAT_HOME}/thermostat.common

CONFIG="--config=${THERMOSTAT_HOME}/thermostat.properties"

LOG="-Dlogback.configurationFile=${THERMOSTAT_HOME}/config/logback.xml"
PACKAGE="com.arainfor.thermronstat.daemon"

#java -cp "/home/pi/thermronstat/lib/*" $DEBUG $LOG $PACKAGE.ThermometersThread $CONFIG
#java -cp "/home/pi/thermronstat/lib/*" $DEBUG $LOG $PACKAGE.StatusThread $CONFIG
#java -cp "/home/pi/thermronstat/lib/*" $DEBUG $LOG $PACKAGE.HvacMonitor $CONFIG

#2>/dev/null 1>&2 java -cp "/home/pi/thermronstat/lib/*" $LOG $PACKAGE.ControlThread $CONFIG&
#2>/dev/null 1>&2 java -cp "/home/pi/thermronstat/lib/*" $LOG $PACKAGE.StatusThread $CONFIG&

#sudo java -cp "/home/pi/thermronstat/lib/*" -Dlogback.configurationFile=/home/pi/thermronstat/config/logback.xml com.arainfor.thermronstat.daemon.StatusThread --config=/home/pi/nodeThermostat/thermostat.properties
sudo LOGPATH="${THERMOSTAT_HOME}/log" java -cp "${THERMOSTAT_HOME}/lib/*" ${DEBUG} ${LOG} ${PACKAGE}.HvacMonitor ${CONFIG}&
#sudo java -cp "/home/pi/thermronstat/lib/*" $DEBUG $LOG $PACKAGE.ThermometersThread $CONFIG&
