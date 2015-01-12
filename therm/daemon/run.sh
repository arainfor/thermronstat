#!/bin/sh
sudo java -cp "lib/*" com.arainfor.thermronstat.daemon.PollThread --config=thermostat.properties&
