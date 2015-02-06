#!/bin/sh
. /home/pi/thermronstat/thermostat.common
java ${DEBUG} -cp "/home/pi/thermronstat/lib/*" "com.arainfor.thermronstat.logReader.StatusLogReader" $1 $2
