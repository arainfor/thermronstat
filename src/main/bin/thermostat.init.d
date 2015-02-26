#! /bin/sh
### BEGIN INIT INFO
# Provides:          thermostat
# Required-Start:    $all
# Required-Stop:     
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: Manage my thermostat
### END INIT INFO

PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/opt/bin
export THERMOSTAT_HOME=/var/local/thermostat

. /lib/init/vars.sh
. /lib/lsb/init-functions
# If you need to source some other scripts, do it here

case "$1" in
  start)
      log_begin_msg "Starting thermostat... "
      # do something
      rm ${THERMOSTAT_HOME}/pid
      ${THERMOSTAT_HOME}/run.sh
      log_end_msg $?
      exit 0
      ;;
  stop)
      log_begin_msg "Stopping thermostat... "
      # do something to kill the service or cleanup or nothing
      ${THERMOSTAT_HOME}/kill.sh
      log_end_msg $?
      exit 0
      ;;
  restart)
      log_begin_msg "Restarting thermostat... "
      ${THERMOSTAT_HOME}/kill.sh
      ${THERMOSTAT_HOME}/run.sh
      log_end_msg $?
      exit 0
      ;;
  *)
      echo "Usage: /etc/init.d/thermostat {start|stop}"
      exit 1
      ;;
esac
 