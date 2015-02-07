package com.arainfor.thermronstat.daemon;

import com.arainfor.thermronstat.RelayMap;
import com.arainfor.thermronstat.StatusRelayCache;
import com.arainfor.thermronstat.logger.StatusLogger;
import com.arainfor.util.file.PropertiesLoader;
import com.arainfor.util.logger.AppLogger;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Created by ARAINFOR on 1/31/2015.
 */
public class HvacMonitor extends Thread {

    protected static final String APPLICATION_NAME = "HvacMonitor";
    protected static final int APPLICATION_VERSION_MAJOR = 1;
    protected static final int APPLICATION_VERSION_MINOR = 0;
    protected static final int APPLICATION_VERSION_BUILD = 0;
    private static StatusLogger statusLogger;
    protected final Logger logger;
    protected final int sleep = Integer.parseInt(System.getProperty(APPLICATION_NAME + ".poll.sleep", "1000"));

    public HvacMonitor() {

        super();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
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

        statusLogger = new StatusLogger();

        log.info("spawning task to read thermometers");
        ThermometersThread thermometersThread = new ThermometersThread();
        thermometersThread.start();

        log.info("spawning task to log status changes");
        StatusThread statusThread = new StatusThread();
        statusThread.start();

        new HvacMonitor().start();

    }

    @Override
    public void run() {

        statusLogger.logMessage(StatusThread.APPLICATION_NAME + " Startup...");

        while (true) {
            try {
                Map<RelayMap, Boolean> statusRelayCache = StatusRelayCache.getInstance().getCache();
                statusLogger.logRelays(statusRelayCache);
                try {
                    sleep(sleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                logger.error("Unhandled exception:", e);
            }
        }
    }

}