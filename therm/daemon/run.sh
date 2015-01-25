#!/bin/sh
sudo java -cp "lib/*" com.arainfor.thermronstat.daemon.ControlThread --config=thermostat.properties&
