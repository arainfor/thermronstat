#! /bin/bash

#
# ----------------------------------------------------------------------------
# "THE BEER-WARE LICENSE" (Revision 42):
# <phk@FreeBSD.ORG> wrote this file. As long as you retain this notice you
# can do whatever you want with this stuff. If we meet some day, and you think
# this stuff is worth it, you can buy me a beer in return Poul-Henning Kamp
# ----------------------------------------------------------------------------
#

#sudo modprobe w1-gpio
#sudo modprobe w1-therm
echo "Sensor $1"
temperature=$(cat $1 | grep  -E -o ".{0,0}t=.{0,5}" | cut -c 3-)
echo "Temperature RAW: $temperature"

cel=$(echo "scale=3;$temperature/1000" | bc)
echo "Temperature Celsius: $cel"
far=$(echo "$cel * 1.8000 + 32.00" | bc)
echo "Temperature Farenheit: $far"

# write the file
echo "Output: $2"
echo $far > $2

