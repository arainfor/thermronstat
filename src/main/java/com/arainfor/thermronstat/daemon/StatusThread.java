package com.arainfor.thermronstat.daemon;

import com.arainfor.thermronstat.RelayMap;
import com.arainfor.thermronstat.StatusRelayCache;
import com.arainfor.thermronstat.StatusRelaysList;
import com.arainfor.util.file.PropertiesLoader;
import com.arainfor.util.logger.AppLogger;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by arainfor on 1/24/15.
 */
public class StatusThread extends Thread {

    // relays
    protected static final String APPLICATION_NAME = "StatusMonitor";
    protected static final int APPLICATION_VERSION_MAJOR = 1;
    protected static final int APPLICATION_VERSION_MINOR = 0;
    protected static final int APPLICATION_VERSION_BUILD = 0;
    private final Logger logger;
    private final int sleep = Integer.parseInt(System.getProperty(APPLICATION_NAME + ".poll.sleep", "300"));

    public StatusThread() {

        super();

        logger = new AppLogger().getLogger(this.getClass().getName());
        logger.info(this.getClass().getName() + " starting...");

    }

    /**
     * @param args The Program Arguments
     */
    public static void main(String[] args) throws IOException {

        Logger log = LoggerFactory.getLogger(StatusThread.class);

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
            System.err.println("Parse error:" + e);
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

        ThermometersThread thermometersThread = new ThermometersThread();
        thermometersThread.start();

        // Main entry point to launch the program
        StatusThread statusThread = new StatusThread();
        statusThread.start();

    }

    @Override
    public void run() {

        ArrayList<RelayMap> relaysList = StatusRelaysList.getInstance().list();
        StatusRelayCache statusRelayCache = StatusRelayCache.getInstance();

        while (true) {
            for (RelayMap relay : relaysList) {
                try {
                    statusRelayCache.setValue(relay, relay.getPiGPIO().getValue());
                } catch (IOException e) {
                    logger.error("Error reading relay:", e);
                    e.printStackTrace();
                }
            }

            try {
                sleep(sleep);
            } catch (InterruptedException e) {
                logger.error("Thread interrupted!:", e);
            }
        }
    }
}