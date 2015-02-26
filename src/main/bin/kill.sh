#!/bin/bash
pid=$(cat ${THERMOSTAT_HOME}/pid)
if [ -n "$pid" ]; then
    echo " "
    echo "killing pid:" ${pid}
    kill -9 ${pid}
    if [ $? -eq 0 ]; then
        rm ${THERMOSTAT_HOME}/pid
    fi
fi

#pid=`ps xauww | grep java | grep ControlThread | cut -d" " -f3`
#if [ -n "$pid" ]; then
#    echo "killing pid:" ${pid}
#    kill -9 ${pid}
#fi
#
#pid=`ps xauww | grep java | grep StatusThread | cut -d" " -f3`
#if [ -n "$pid" ]; then
#    echo "killing pid:" ${pid}
#    kill -9 ${pid}
#fi
#
#pid=`ps xauww | grep java | grep HvacMonitor | cut -d" " -f3`
#if [ -n "$pid" ]; then
#    echo "killing pid:" ${pid}
#    kill -9 ${pid}
#fi
