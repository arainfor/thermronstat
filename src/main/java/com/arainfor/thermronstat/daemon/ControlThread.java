/**
 * 
 */
package com.arainfor.thermronstat.daemon;

import com.arainfor.thermronstat.*;
import com.arainfor.thermronstat.logger.ControlLogger;
import com.arainfor.util.file.PropertiesLoader;
import com.arainfor.util.file.io.Path;
import com.arainfor.util.file.io.ValueFileIO;
import com.arainfor.util.logger.AppLogger;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

/**
 * @author arainfor
 *
 */
public class ControlThread extends Thread {

    private static final String APPLICATION_NAME = "ThermRonStat";
    private static final int APPLICATION_VERSION_MAJOR = 2;
    private static final int APPLICATION_VERSION_MINOR = 0;
    private static final int APPLICATION_VERSION_BUILD = 0;
    // value files used for user control and feedback
    private static ValueFileIO statusControlValue; // This file enables/disables the entire system
    private static ValueFileIO userY1value;        // user feedback file for Y1 relay
    private static ValueFileIO userY2value;        // user feedback file for Y2 relay
    private static ValueFileIO userGvalue;         // user feedback file for G relay
    private static ValueFileIO userWvalue;         // user feedback file for W relay
    private static ValueFileIO userOvalue;         // user feedback file for O relay
    private static ValueFileIO userTargetTempValue;  // This file is the user target temperature
    private static ArrayList<Thermometer> thermometers = new ArrayList<Thermometer>();
    private static String oldSingleMsg;
    private static ControlLogger controlLogger;
    private static Logger logger;
    // these map the GPIO to a RelayOutputs value
    private final ArrayList<RelayMap> relayMap = new ArrayList<RelayMap>();
    private final int sleep = Integer.parseInt(System.getProperty(APPLICATION_NAME + ".poll.sleep", "1000"));
    private long currentRuntimeStart;

    private ControlThread() {

		super();

		logger = new AppLogger().getLogger(this.getClass().getName());
		logger.info(this.getClass().getName() + " starting...");

		// Add hook to turn off everything...
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				logger.info("Turning OFF HVAC...");
				try {
                    // loop thru all the relays and set values accordingly.
                    for (RelayMap rm : relayMap) {
                        rm.getSysFsGpio().setValue(false);
                    }

					statusControlValue.write(0);
					controlLogger.logSystemOnOff(false);
				} catch (IOException e) {
					e.printStackTrace();
				}  // Try to turn off the HVAC if we are terminated!!

				logger.info(this.getClass().getName() + " terminated...");
			}
		}));

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
		Properties props = new PropertiesLoader(propFileName).getProps();

		// Append the system properties with our application properties
		props.putAll(System.getProperties());
		System.setProperties(props);

		controlLogger = new ControlLogger();

		String IO_BASE_FS = System.getProperty(APPLICATION_NAME.toLowerCase() + ".IO_BASE_FS", "/var/" + APPLICATION_NAME.toLowerCase());

		Path targetPath = new Path(IO_BASE_FS + "/target");
		Path relayPath = new Path(IO_BASE_FS + "/relay");
		Path statusPath = new Path(IO_BASE_FS + "/status");

		if (cmd.hasOption("mkdirs")) {
			targetPath.build();
			relayPath.build();
			statusPath.build();
		}

		userTargetTempValue = new ValueFileIO(targetPath.getAbsolutePath() + "/0");
		statusControlValue = new ValueFileIO(statusPath.getAbsolutePath() + "/0");
		userGvalue = new ValueFileIO(relayPath.getAbsolutePath() + "/0");
		userY1value = new ValueFileIO(relayPath.getAbsolutePath() + "/1");
		userY2value = new ValueFileIO(relayPath.getAbsolutePath() + "/2");
		userWvalue = new ValueFileIO(relayPath.getAbsolutePath() + "/3");
		userOvalue = new ValueFileIO(relayPath.getAbsolutePath() + "/4");

        thermometers = ThermometersList.getInstance().list();

		System.out.println("Target Temperature File: " + userTargetTempValue);
        System.out.println("Indoor Temperature Name: " + thermometers.get(0).getName() + " File: " + thermometers.get(0).getDs18B20().getFilename());
        System.out.println("Outdoor Temperature Name: " + thermometers.get(1).getName() + " File: " + thermometers.get(1).getDs18B20().getFilename());
        System.out.println("Plenum Temperature Name: " + thermometers.get(2).getName() + " File: " + thermometers.get(2).getDs18B20().getFilename());
        System.out.println("Return Temperature Name: " + thermometers.get(3).getName() + " File: " + thermometers.get(3).getDs18B20().getFilename());
        System.out.println("Relay Control File: " + userY1value);  // Is the system currently running?
		System.out.println("System Available Control File: " + statusControlValue);  // User desired state of relay, on or off

		// Main entry point to launch the program
		ControlThread thermostat = new ControlThread();
		thermostat.start();

	}


	@Override
	public void run() {

		boolean lastSystemStatus = false;

		while (true) {

            try {
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    logger.error(e.toString());
                    continue;
                }

                boolean stage1RelayPosition;
                boolean systemStatus;

                try {
                    systemStatus = statusControlValue.read();
                    stage1RelayPosition = userY1value.read();

                    // Display a message if we toggled systemStatus.
                    if (systemStatus != lastSystemStatus) {
                        controlLogger.logSystemOnOff(systemStatus);
                        if (!systemStatus)
                            logger.info("Turning System OFF");
                        else
                            logger.info("Turning System ON");
                        lastSystemStatus = systemStatus;
                    }

                    if (!systemStatus) {
                        if (stage1RelayPosition) { // turn off the relay if user wants it off!
                            // loop thru all the relays and set values accordingly.
                            for (RelayMap rm : relayMap) {
                                rm.getSysFsGpio().setValue(false);
                            }
                            logSingle("Stage1 OFF");
                        }
                        continue;
                    }

                } catch (IOException e2) {
                    logger.error("Error reading file: " + e2);
                    e2.printStackTrace();
                    continue;
                }

                double targetTemp;
                double indoorTemp;
                double outdoorTemp;

                try {
                    targetTemp = userTargetTempValue.readDouble();
                    indoorTemp = thermometers.get(0).getDs18B20().getTempF();
                    outdoorTemp = thermometers.get(1).getDs18B20().getTempF();
                } catch (IOException ioe) {
                    logger.error("Target Temperature Read error!: " + ioe.toString());
                    ioe.printStackTrace();
                    continue;
                }

                // the real decision is here!
                SingleStageHvacHandler handler = new SingleStageHvacHandler(
                        targetTemp,
                        indoorTemp,
                        0.5,
                        currentRuntimeStart);

                ArrayList<RelayDef> relaysEnabled = handler.execute();
                boolean stage1Enable = relaysEnabled.contains(RelayDef.Y1);
                if (stage1Enable) {
                    if (currentRuntimeStart == 0)
                        currentRuntimeStart = System.currentTimeMillis();
                } else {
                    if (currentRuntimeStart > 0) {
                        controlLogger.logRuntime(System.currentTimeMillis() - currentRuntimeStart);
                        currentRuntimeStart = 0;
                    }
                }

                logSingle("Run?" + stage1Enable + " target:" + targetTemp + " indoorTemp:" + indoorTemp + " " + " stage1RelayPosition:" + stage1RelayPosition);

                controlLogger.logSummary(relaysEnabled, thermometers);

                try {
                    // loop thru all the relays and set values accordingly.
                    for (RelayMap rm : relayMap) {
                        RelayDef rd = rm.getRelayDef();
                        if (relaysEnabled.contains(rd)) {
                            rm.getSysFsGpio().setValue(true);
                        } else {
                            rm.getSysFsGpio().setValue(false);
                        }
                    }

                    if (stage1RelayPosition != stage1Enable) {
                        logger.debug("***************");
                        logger.debug("heat mode? " + handler.isHeat());
                        logger.debug("target_temp=" + targetTemp);
                        logger.debug("indoor_temp=" + indoorTemp);
                        logger.debug("outdoor_temp=" + outdoorTemp);
                        logger.info("Stage1 Relay changed from:" + stage1RelayPosition + " to:" + stage1Enable);

                        // Change to the new setting...
                        userY1value.write(stage1Enable);
                    }
                } catch (IOException e) {
                    logger.error("Relay Control Error: " + e.toString());
                    e.printStackTrace();
                }
            } catch (Exception e) {
                logger.error("Unhandled exception:", e);
            }
        }
	}

	/**
	 * Log a message but don't repeat the same message over and over.
	 *
	 * @param msg The message to log
	 * @return true if the message is new.
	 */
	private boolean logSingle(String msg) {
		if (msg.equals(oldSingleMsg))
			return false;
		logger.debug(msg);
		oldSingleMsg = msg;
		return true;
	}

}
