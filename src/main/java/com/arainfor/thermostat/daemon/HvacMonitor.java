/**
 * Copyright 2014-2015
 * Alan Rainford arainfor@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.arainfor.thermostat.daemon;

import com.arainfor.thermostat.*;
import com.arainfor.thermostat.logger.StatusLogger;
import com.arainfor.thermostat.logger.TemperatureLogger;
import com.arainfor.util.file.PropertiesLoader;
import com.arainfor.util.file.io.gpio.SysFsGpio;
import com.arainfor.util.file.io.gpio.SysFsGpioCallback;
import com.arainfor.util.file.io.thermometer.ThermometerCallback;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

/**
 * Created by ARAINFOR on 1/31/2015.
 */
public class HvacMonitor extends Thread implements SysFsGpioCallback, ThermometerCallback {

    protected static final String APPLICATION_NAME = HvacMonitor.class.getSimpleName();
    protected static final int APPLICATION_VERSION_MAJOR = 2;
    protected static final int APPLICATION_VERSION_MINOR = 0;
    protected static final int APPLICATION_VERSION_BUILD = 0;
    private static final Logger logger = LoggerFactory.getLogger(HvacMonitor.class);
    private final int sleep = Integer.parseInt(System.getProperty(APPLICATION_NAME + ".poll.sleep", "1000"));
    private StatusLogger statusLogger;
    private StatusRelaysList statusRelaysList;
    private ThermometersList thermometersList;
    private TemperatureLogger temperatureLogger;

    public HvacMonitor() throws IOException {

        super();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                StatusRelaysList srl = StatusRelaysList.getInstance();
                Iterator<RelayMap> it = srl.list().iterator();
                while (it.hasNext()) {
                    SysFsGpio gpio = it.next().getSysFsGpio();
                    if (gpio != null) {
                        try {
                            gpio.cleanup(gpio.getPin());
                        } catch (IOException e) {
                            logger.error("Exception cleaning up gpio:{}", gpio.toString(), e);
                        }
                    }
                }

                try {
                    new File("pid").delete();
                } catch (Exception e) {
                    logger.error("Error removing pid file:", e);
                }
                if (statusLogger != null) {
                    statusLogger.logMessage(APPLICATION_NAME + " shutdown!");
                }
            }
        }));

        logger.info(this.getClass().getName() + " starting...");
        statusLogger = new StatusLogger();

    }

    /**
     * @param args The Program Arguments
     */
    public static void main(String[] args) throws IOException {

        Logger log = LoggerFactory.getLogger(HvacMonitor.class);

        //System.err.println(APPLICATION_NAME + " v" + APPLICATION_VERSION_MAJOR + "." + APPLICATION_VERSION_MINOR + "." + APPLICATION_VERSION_BUILD);
        Options options = new Options();
        options.addOption("help", false, "This message isn't very helpful");
        options.addOption("version", false, "Print the version number");
        options.addOption("monitor", false, "Start GUI Monitor");
        options.addOption("config", true, "The configuration file");

        CommandLineParser parser = new GnuParser();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption("help")) {
                HelpFormatter hf = new HelpFormatter();
                hf.printHelp(APPLICATION_NAME, options);
                return;
            }
            if (cmd.hasOption("version")) {
                System.out.println("The " + APPLICATION_NAME + " v" + APPLICATION_VERSION_MAJOR + "." + APPLICATION_VERSION_MINOR + "." + APPLICATION_VERSION_BUILD);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        String propFileName = "thermostat.properties";
        if (cmd.getOptionValue("config") != null)
            propFileName = cmd.getOptionValue("config");

        log.info("loading...{}", propFileName);

        try {
            Properties props = new PropertiesLoader(propFileName).getProps();

            // Append the system properties with our application properties
            props.putAll(System.getProperties());
            System.setProperties(props);
        } catch (FileNotFoundException fnfe) {
            log.warn("Cannot load file:", fnfe);
        }

        new HvacMonitor().start();

    }

    /**
     *  This daemonizes this java class.
     */
    @Override
    public void run() {

        statusLogger.logMessage(APPLICATION_NAME + " Startup...");

        // Create the thermometers
        thermometersList = ThermometersList.getInstance();
        temperatureLogger = new TemperatureLogger();

        // Register as the callback class
        for (Thermometer thermometer : thermometersList.list()) {
            if (thermometer.getDs18B20().isValid()) {
                new com.arainfor.util.file.io.thermometer.CallbackMonitor(this, thermometer).registerCallback();
            }
        }

        // Create the status relays
        statusRelaysList = StatusRelaysList.getInstance();
        StatusRelayCache statusRelayCache = StatusRelayCache.getInstance();

        // Register as the callback class
        for (RelayMap relayMap : statusRelaysList.list()) {
            statusRelayCache.setValue(relayMap, false);  // Set the initial value
            new com.arainfor.util.file.io.gpio.CallbackMonitor(this, relayMap.getSysFsGpio()).registerCallback();
        }

        // Now just sit and wait for the magic to happen!!
        while (true) {
            try {
                sleep(sleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This handles the callback for the GPIO changes.
     *
     * @param sysFsGpio
     * @param value
     */
    @Override
    public synchronized void subjectChanged(SysFsGpio sysFsGpio, boolean value) {

        //logger.debug("GPIO Pin:{} changed to:{}", piGpio.getPin(), value);
        ArrayList<RelayMap> relaysList = StatusRelaysList.getInstance().list();
        StatusRelayCache statusRelayCache = StatusRelayCache.getInstance();

        for (RelayMap relay : relaysList) {
            if (relay.getSysFsGpio().getPin() == sysFsGpio.getPin()) {
                statusRelayCache.setValue(relay, value);
                statusLogger.logRelays(statusRelayCache.getCache());
                break;
            }
        }
   }

    /**
     * This handles the callback for Thermometer changes.
     * @param thermometerChanged
     * @param value
     */
    @Override
    public synchronized void subjectChanged(Thermometer thermometerChanged, double value) {

        ArrayList<Temperature> temperatureList = TemperaturesList.getInstance().list();
        Iterator<Temperature> it = temperatureList.iterator();

        while (it.hasNext()) {
            Temperature temperature = it.next();
            if (temperature.getIndex() == thermometerChanged.getIndex()) {
                temperature.setValue(value);
                temperatureList.set(temperature.getIndex(), temperature);
                temperatureLogger.logMessage(temperatureList.toString());
                break;
            }
        }
    }
}