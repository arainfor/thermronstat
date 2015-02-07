#!/bin/sh

sensor_1=28-0000057b7552
sensor_2=28-0000054d6b25

# define the input sensors
export indoorSensor=/sys/bus/w1/devices/${sensor_1}/w1_slave
export outdoorSensor=/sys/bus/w1/devices/${sensor_2}/w1_slave

# define output files
export indoorOutput=/var/thermronstat/temperature/0/f
export outdoorOutput=/var/thermronstat/temperature/1/f

# read the device values
./readTempSensor.sh ${indoorSensor} ${indoorOutput} > /dev/null
./readTempSensor.sh ${outdoorSensor} ${outdoorOutput} > /dev/null

# read the files that have the data
read inside < ${indoorOutput}
read outside < ${outdoorOutput}
read target < /var/thermronstat/target/0
read systemState < /var/thermronstat/status/0
read relay < /var/thermronstat/relay/0

# export so visible in evnvironment
export inside
export outside
export target
export systemState
export relay

