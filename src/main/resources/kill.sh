#!/bin/bash
pid=`ps xauww | grep java | grep ControlThread | cut -d" " -f6`
if [ -n "$pid" ]; then
    echo "killing pid:" ${pid}
    kill -9 ${pid}
fi

pid=`ps xauww | grep java | grep StatusThread | cut -d" " -f6`
if [ -n "$pid" ]; then
    echo "killing pid:" ${pid}
    kill -9 ${pid}
fi

pid=`ps xauww | grep java | grep HvacMonitor | cut -d" " -f6`
if [ -n "$pid" ]; then
    echo "killing pid:" ${pid}
    kill -9 ${pid}
fi
