#!/bin/sh
val=$(echo $1|cut -c 1)
if [ $val -eq 0 ] 
then
  echo "Off"
else
  echo "Auto"
fi
