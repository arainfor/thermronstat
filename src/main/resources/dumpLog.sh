#!/bin/sh
. ./thermostat.common
java ${DEBUG} -cp "/home/pi/thermronstat/lib/*" "com.arainfor.thermronstat.logReader.LogReader" $1