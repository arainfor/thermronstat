package com.arainfor.thermostat;

import com.arainfor.thermostat.logger.StatusLogger;
import com.arainfor.thermostat.logger.TemperatureLogger;
import com.arainfor.util.file.PropertiesLoader;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by arainfor on 2/25/15.
 */
public class QuickStat {

    protected static final String APPLICATION_NAME = QuickStat.class.getSimpleName();
    protected static final int APPLICATION_VERSION_MAJOR = 2;
    protected static final int APPLICATION_VERSION_MINOR = 0;
    protected static final int APPLICATION_VERSION_BUILD = 0;
    private static StatusLogger statusLogger;
    protected final Logger logger = LoggerFactory.getLogger(QuickStat.class);

    /**
     * @param args The Program Arguments
     */
    public static void main(String[] args) throws IOException {

        Logger log = LoggerFactory.getLogger(QuickStat.class);

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

        // Create the thermometers
        ThermometersList thermometersList = ThermometersList.getInstance();
        TemperatureLogger temperatureLogger = new TemperatureLogger();

        StringBuilder sb = new StringBuilder();
        // Register as the callback class
        for (Thermometer thermometer : thermometersList.list()) {
            if (thermometer.getDs18B20().isValid()) {
                sb.append(thermometer);
            }
        }

        // Create the status relays
        StatusRelaysList statusRelaysList = StatusRelaysList.getInstance();
        StatusRelayCache statusRelayCache = StatusRelayCache.getInstance();

        // Register as the callback class
        for (RelayMap relayMap : statusRelaysList.list()) {
            statusRelayCache.setValue(relayMap, false);  // Set the initial value
            sb.append(relayMap);
        }

        System.out.println(sb.toString());

    }

}
