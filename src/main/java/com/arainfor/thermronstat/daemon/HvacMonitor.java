package com.arainfor.thermronstat.daemon;

import com.arainfor.thermronstat.*;
import com.arainfor.thermronstat.logger.StatusLogger;
import com.arainfor.util.file.PropertiesLoader;
import com.arainfor.util.file.io.gpio.SysFsGpio;
import com.arainfor.util.file.io.gpio.SysFsGpioCallback;
import com.arainfor.util.file.io.thermometer.ThermometerCallback;
import com.arainfor.util.logger.AppLogger;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

/**
 * Created by ARAINFOR on 1/31/2015.
 */
public class HvacMonitor extends Thread implements SysFsGpioCallback, ThermometerCallback {

    protected static final String APPLICATION_NAME = "HvacMonitor";
    protected static final int APPLICATION_VERSION_MAJOR = 1;
    protected static final int APPLICATION_VERSION_MINOR = 1;
    protected static final int APPLICATION_VERSION_BUILD = 0;
    private static StatusLogger statusLogger;
    protected final Logger logger;
    protected final int sleep = Integer.parseInt(System.getProperty(APPLICATION_NAME + ".poll.sleep", "1000"));
    private StatusRelaysList statusRelaysList;
    private ThermometersList thermometersList;

    public HvacMonitor() {

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
                if (statusLogger != null) {
                    statusLogger.logMessage(APPLICATION_NAME + " shutdown!");
                }
            }
        }));

        logger = new AppLogger().getLogger(this.getClass().getName());
        logger.info(this.getClass().getName() + " starting...");

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

        statusLogger = new StatusLogger();

//        log.info("spawning task to read thermometers");
//        ThermometersThread thermometersThread = new ThermometersThread();
//        thermometersThread.start();

//        log.info("spawning task to log status changes");
//        StatusThread statusThread = new StatusThread();
//        statusThread.start();

        new HvacMonitor().start();

    }

    @Override
    public void run() {

        statusLogger.logMessage(APPLICATION_NAME + " Startup...");

        // Create the thermometers
        thermometersList = ThermometersList.getInstance();

        // Register as the callback class
        for (Thermometer thermometer : thermometersList.list()) {
            if (thermometer.getDs18B20().isValid()) {
                thermometer.getDs18B20().registerCallback(this);
            }
        }

        // Create the status relays
        statusRelaysList = StatusRelaysList.getInstance();
        StatusRelayCache statusRelayCache = StatusRelayCache.getInstance();

        // Register as the callback class
        for (RelayMap relayMap : statusRelaysList.list()) {
            statusRelayCache.setValue(relayMap, false);  // Set the initial value
            relayMap.getSysFsGpio().registerCallback(this);
        }

        // Now just sit and wait for the magic to happen!!
        while (true) {
//            try {
//                Map<RelayMap, Boolean> statusRelayCache = StatusRelayCache.getInstance().getCache();
//                statusLogger.logRelays(statusRelayCache);
                try {
                    sleep(sleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//            } catch (Exception e) {
//                logger.error("Unhandled exception:", e);
//            }
        }
    }

    @Override
    public synchronized void subjectChanged(SysFsGpio sysFsGpio, boolean value) {

        //logger.debug("GPIO Pin:{} changed to:{}", piGpio.getPin(), value);
        ArrayList<RelayMap> relaysList = StatusRelaysList.getInstance().list();
        StatusRelayCache statusRelayCache = StatusRelayCache.getInstance();

        for (RelayMap relay : relaysList) {
            if (relay.getSysFsGpio().getPin() == sysFsGpio.getPin())
                statusRelayCache.setValue(relay, value);
        }

        statusLogger.logRelays(statusRelayCache.getCache());

    }

    @Override
    public synchronized void subjectChanged(Thermometer thermometerChanged, Double value) {

        ArrayList<Temperature> temperatureList = TemperaturesList.getInstance().list();

        Iterator<Temperature> it = temperatureList.iterator();
        while (it.hasNext()) {
            Temperature temperature = it.next();
            if (temperature.getIndex() == thermometerChanged.getIndex()) {
                temperature.setValue(value);
                temperatureList.set(temperature.getIndex(), temperature);

            }
        }
    }
}