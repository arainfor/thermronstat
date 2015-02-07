package com.arainfor.thermronstat.daemon;

import com.arainfor.thermronstat.Temperature;
import com.arainfor.thermronstat.TemperaturesList;
import com.arainfor.thermronstat.Thermometer;
import com.arainfor.thermronstat.ThermometersList;
import com.arainfor.thermronstat.logger.TemperatureLogger;
import com.arainfor.util.file.PropertiesLoader;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

/**
 * This class reads the thermometers in a thread and saves the values in a TemperaturesList object.
 *
 * Created by ARAINFOR on 1/31/2015.
 */
public class ThermometersThread extends Thread {

    protected static final int APPLICATION_VERSION_MAJOR = 1;
    protected static final int APPLICATION_VERSION_MINOR = 0;
    protected static final int APPLICATION_VERSION_BUILD = 0;
    private static final String APPLICATION_NAME = "ThermometerMonitor";
    private final Logger logger = LoggerFactory.getLogger(ThermometersThread.class);
    private final int sleep = Integer.parseInt(System.getProperty(APPLICATION_NAME + ".poll.sleep", "1400"));

    public ThermometersThread() {
        super();
        logger.info(this.getClass().getName() + " starting...");
    }

    /**
     * @param args The Program Arguments
     */
    public static void main(String[] args) throws IOException {

        Logger log = LoggerFactory.getLogger(ThermometersThread.class);

        //System.err.println("The " + APPLICATION_NAME +" v1" + APPLICATION_VERSION_MAJOR + "." + APPLICATION_VERSION_MINOR + "." + APPLICATION_VERSION_BUILD);
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


        log.info("spawning task to read thermometers");
        ThermometersThread thermometersThread = new ThermometersThread();
        thermometersThread.start();

    }

    @Override
    public void run() {

        ArrayList<Temperature> temperaturesList = TemperaturesList.getInstance().list();
        ArrayList<Thermometer> thermometersList = new ThermometersList().list();

        ArrayList<Temperature> temperaturesListCache = new ArrayList<Temperature>();

        TemperatureLogger temperatureLogger = new TemperatureLogger();

        for (int i = 0; i < temperaturesList.size(); i++) {
            temperaturesListCache.add(i, new Temperature(i, System.getProperty(i + ".name")));
        }

        while (true) {

            try {

                for (Temperature temperature : temperaturesList) {
                    Thermometer thermometer = thermometersList.get(temperature.getIndex());

                    if (!thermometer.getDs18B20().getFilename().contains("unknown")) {
                        try {
                            Double tempF = thermometer.getDs18B20().getTempF();
                            temperature.setValue(tempF);
                        } catch (IOException e) {
                            logger.warn("Error reading thermometer {} Exception:", thermometer.getName(), e.getMessage());
                        }
                    }
                }

                boolean bDirty = false;
                for (int i = 0; i < temperaturesList.size(); i++) {
                    if (!temperaturesList.get(i).toString().equals(temperaturesListCache.get(i).toString())) {
                        bDirty = true;
                        temperaturesListCache.get(i).setValue(temperaturesList.get(i).getValue());
                    }
                }

                if (bDirty) {
                    temperatureLogger.logMessage(temperaturesList.toString());
                }

                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    logger.error("Interupted Exception:", e);
                }
            } catch (Exception e) {
                logger.error("Unhandled Exception:", e);
            }
        }
    }

}