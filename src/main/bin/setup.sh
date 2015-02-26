#!/bin/bash

if [ -e /usr/bin/banner ]; then
    banner "Setup v1.0"
else
    echo "Setup v1.0"
fi

# Check for root permissions
if [ $(id -u) != 0 ]; then
   echo "This script ${0} requires root permissions"
   exit 1
fi

# Check that we are in correct directory
if [ ! -e "./thermostat.properties" ]; then
    echo "Can't locate thermostat.properties!"
    echo "Setup should be run from the thermostat home directory"
    exit 1
fi

# Make sure user knows the consiquences.
echo "Enter the number for your selection:"
select opt in "Yes" "No" "Summary" "Debug"; do
    case $opt in
        "Yes" ) break;;
        "No" ) exit;;
        "Summary" ) SUMMARY_ONLY=Yes; break;;
        "Debug" ) DEBUG=echo; break;;
    esac
done

# helper functions...
function setHome {
    if grep THERMOSTAT_HOME /etc/environment
    then
        echo "Fatal error: ${0} can't replace the existing setting!"
        exit 1
    else
        echo "Setting /etc/environment"
        if [ -z ${DEBUG} ]; then
            echo "THERMOSTAT_HOME=${PWD}" >> /etc/environment
        else
            echo "echo "THERMOSTAT_HOME=${PWD}" >> /etc/environment"
        fi
    fi
}

# Do the dirty work!
function thermometerSetup {

    if grep w1 /boot/config.txt
    then
        echo "config.txt patch exists!"
    else
        echo "writing /boot/config.txt update..."
        if [ -z ${DEBUG} ]; then
            echo dtoverlay=w1-gpio >> /boot/config.txt
        else
            echo "echo dtoverlay=w1-gpio >> /boot/config.txt"
        fi
        sync
        ${DEBUG} shutdown -r +1 "Reboot for new config.txt change"
        exit
    fi

    if [ ! -e /sys/bus/w1 ]; then
        if grep w1 /etc/modules
        then
            echo "Kernel modules configured already!"
        else
            echo w1-gpio >> /etc/modules
            echo w1-therm >> /etc/modules
            echo "Reboot required!!!"
            sync
            ${DEBUG} shutdown -r +1 "Reboot for new kernel modules"
            exit
        fi
    else
        echo "1-wire thermostat support already installed"
    fi

}

function applicationSetup {
    if [ -z ${THERMOSTAT_HOME} ]; then
      echo "THERMOSTAT_HOME environment variable unset."
      echo "This shouldn't happen unless its a new install"
      echo "Shall I set it anyway?"
      select yn in "Yes" "No"; do
        case $yn in
          Yes ) DOSETHOME=true; break;;
           No ) break;;
        esac
      done

      if [ ! -z ${DOSETHOME} ]; then
        setHome
      fi
    else
      echo "THERMOSTAT_HOME already set to ${THERMOSTAT_HOME}"
      echo "If thats wrong you should change /etc/environment yourself!"
    fi

    if [ -e ${THERMOSTAT_HOME}/thermostat.init.d ]; then
      echo "setting up runlevel init"
      ${DEBUG} mv ${THERMOSTAT_HOME}/thermostat.init.d /etc/init.d/thermostat
      ${DEBUG} update-rc.d thermostat defaults
    fi

    if [ ! -e ${THERMOSTAT_HOME}/log ]; then
        ${DEBUG} mkdir ${THERMOSTAT_HOME}/log
        ${DEBUG} chmod 664 ${THERMOSTAT_HOME}/log
    fi
}

function summaryList {

    echo "Connected thermometers"
    ls -dc1 /sys/bus/w1/devices/28*

    echo "Active gpio pins"
    ls -dc1 /sys/class/gpio/gpio*

    echo "Please verify the settings in thermostat.properties are correct for your setup"

}

# Finally what we want to do!!!
if [ ! -z ${DEBUG} ]; then
    echo "Debug mode"
fi

if [ -z ${SUMMARY_ONLY} ]; then
    thermometerSetup
    applicationSetup
fi

summaryList
echo "Done."


