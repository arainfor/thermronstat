package com.arainfor.thermronstat.daemon;

import com.arainfor.thermronstat.RelayDef;
import com.arainfor.thermronstat.RelayMap;
import com.arainfor.util.file.PropertiesLoader;
import com.arainfor.util.file.io.gpio.Direction;
import com.arainfor.util.file.io.gpio.PiGPIO;
import com.arainfor.util.file.io.gpio.Pin;
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
    protected static PiGPIO relayG;   // relay for Fan G
    protected static PiGPIO relayY1;  // relay for Stage 1
    protected static PiGPIO relayY2;  // relay for Stage 2
    private static String APPLICATION_NAME = "HvacMonitor";
    private static int APPLICATION_VERSION_MAJOR = 1;
    private static int APPLICATION_VERSION_MINOR = 0;
    private static int APPLICATION_VERSION_BUILD = 0;
    private static StatusLogger statusLogger;
    // these map the GPIO to a RelayInputs value
    protected ArrayList<RelayMap> relayMap = new ArrayList<RelayMap>();
    protected Logger logger;
    protected int sleep = Integer.parseInt(System.getProperty("poll.sleep", "1000"));

    public StatusThread() {

        super();

        logger = new AppLogger().getLogger(this.getClass().getName());
        logger.info(this.getClass().getName() + " starting...");


        // setup gpio
        try {

            relayG = new PiGPIO(new Pin(27), Direction.IN);
            relayY1 = new PiGPIO(new Pin(17), Direction.IN);
            relayY2 = new PiGPIO(new Pin(22), Direction.IN);

        } catch (IOException ioe) {
            System.err.println("Fatal error initializing GPIO: " + ioe.getLocalizedMessage());
            ioe.printStackTrace();
            System.exit(-1);
        }

        // map the relays
        relayMap.add(new RelayMap(RelayDef.G, relayG, null));
        relayMap.add(new RelayMap(RelayDef.Y1, relayY1, null));
        relayMap.add(new RelayMap(RelayDef.Y2, relayY2, null));

    }

    /**
     * @param args The Program Arguments
     */
    public static void main(String[] args) throws IOException {

        Logger log = LoggerFactory.getLogger(ControlThread.class);

        //System.err.println("The " + APPLICATION_NAME +" v1" + APPLICATION_VERSION_MAJOR + "." + APPLICATION_VERSION_MINOR + "." + APPLICATION_VERSION_BUILD);
        Options options = new Options();
        options.addOption("help", false, "This message isn't very helpful");
        options.addOption("version", false, "Print the version number");
        options.addOption("mkdirs", false, "Create missing paths");
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

            // Append the system properties with our applicaton properties
            props.putAll(System.getProperties());
            System.setProperties(props);
        } catch (FileNotFoundException fnfe) {}

        statusLogger = new StatusLogger();

        // Main entry point to launch the program
        StatusThread statusThread = new StatusThread();
        statusThread.start();

    }


    @Override
    public void run() {

        while (true) {
            statusLogger.logRelays(relayMap);
            try {
                sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}