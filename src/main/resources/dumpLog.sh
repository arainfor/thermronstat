#!/bin/sh
. /home/pi/thermronstat/thermostat.common
java ${DEBUG} -cp "/home/pi/thermronstat/lib/*" "com.arainfor.thermronstat.logReader.LogReader" $1 $2
