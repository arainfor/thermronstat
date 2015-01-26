#!/bin/bash
pid=`ps aux | grep ControlThread | awk '{print $2}'`
echo "killing pid:" ${pid}
kill -9 $pid
pid=`ps aux | grep StatusThread | awk '{print $2}'`
echo "killing pid:" ${pid}
kill -9 $pid
