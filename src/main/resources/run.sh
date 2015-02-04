#!/bin/sh

cd /home/pi/thermronstat

. /home/pi/thermronstat/thermostat.common

CONFIG="--config=/home/pi/nodeThermostat/thermostat.properties"

LOG="-Dlogback.configurationFile=/home/pi/thermronstat/config/logback.xml"
PACKAGE="com.arainfor.thermronstat.daemon"
#2>/dev/null 1>&2 java -cp "/home/pi/thermronstat/lib/*" $LOG $PACKAGE.ControlThread $CONFIG&
#2>/dev/null 1>&2 java -cp "/home/pi/thermronstat/lib/*" $LOG $PACKAGE.StatusThread $CONFIG&

#sudo java -cp "/home/pi/thermronstat/lib/*" -Dlogback.configurationFile=/home/pi/thermronstat/config/logback.xml com.arainfor.thermronstat.daemon.StatusThread --config=/home/pi/nodeThermostat/thermostat.properties
sudo java -cp "/home/pi/thermronstat/lib/*" $DEBUG $LOG $PACKAGE.HvacMonitor $CONFIG&