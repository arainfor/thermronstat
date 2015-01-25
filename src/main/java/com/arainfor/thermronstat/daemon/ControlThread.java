/**
 * 
 */
package com.arainfor.thermronstat.daemon;

import com.arainfor.thermronstat.H1TemperatureControl;
import com.arainfor.thermronstat.RelayDef;
import com.arainfor.thermronstat.RelayMap;
import com.arainfor.thermronstat.Thermometer;
import com.arainfor.util.file.PropertiesLoader;
import com.arainfor.util.file.io.Path;
import com.arainfor.util.file.io.ValueFileIO;
import com.arainfor.util.file.io.gpio.PiGPIO;
import com.arainfor.util.file.io.thermometer.DS18B20;
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

	// relays
	protected static PiGPIO relayG;   // relay for Fan G
	protected static PiGPIO relayY1;  // relay for Stage 1
	protected static PiGPIO relayY2;  // relay for Stage 2
	protected static PiGPIO relayW;   // relay for Emergency Heat
	protected static PiGPIO relayO;   // relay for Reversing valve
	// value files used for user control and feedback
	protected static ValueFileIO statusControlValue; // This file enables/disables the entire system
	protected static ValueFileIO userY1value;        // user feedback file for Y1 relay
	protected static ValueFileIO userY2value;        // user feedback file for Y2 relay
	protected static ValueFileIO userGvalue;         // user feedback file for G relay
	protected static ValueFileIO userWvalue;         // user feedback file for W relay
	protected static ValueFileIO userOvalue;         // user feedback file for O relay
	protected static ValueFileIO userTargetTempValue;  // This file is the user target temperature
	protected static ArrayList<Thermometer> thermometers = new ArrayList<Thermometer>();
	static String oldSingleMsg;
	private static ControlLogger thermlogger;
	private static String APPLICATION_NAME = "ThermRonStat";
	private static int APPLICATION_VERSION_MAJOR = 2;
	private static int APPLICATION_VERSION_MINOR = 0;
	private static int APPLICATION_VERSION_BUILD = 0;
	private static String logFileName = null;
	// these map the GPIO to a RelayOutputs value
	protected ArrayList<RelayMap> relayMap = new ArrayList<RelayMap>();
	protected Logger logger;
	protected int sleep = Integer.parseInt(System.getProperty("poll.sleep", "1000"));
	private long currentRuntimeStart;

	public ControlThread() {

		super();

		logger = new AppLogger().getLogger(this.getClass().getName());
		logger.info(this.getClass().getName() + " starting...");


/*
		// setup gpio
		try {

			relayG = new PiGPIO(new Pin(21), Direction.OUT);
			relayY1 = new PiGPIO(new Pin(17), Direction.OUT);
			relayY2 = new PiGPIO(new Pin(22), Direction.OUT);
			relayW = new PiGPIO(new Pin(23), Direction.OUT);
			relayO = new PiGPIO(new Pin(24), Direction.OUT);

		} catch (IOException ioe) {
			System.err.println("Fatal error initializing GPIO: " + ioe.getLocalizedMessage());
			ioe.printStackTrace();
			System.exit(-1);
		}

		// map the relays
		relayMap.add(new RelayMap(RelayDef.G, relayG, userGvalue));
		relayMap.add(new RelayMap(RelayDef.Y1, relayY1, userY1value));
		relayMap.add(new RelayMap(RelayDef.Y2, relayY2, userY2value));
		relayMap.add(new RelayMap(RelayDef.W, relayW, userWvalue));
		relayMap.add(new RelayMap(RelayDef.O, relayO, userOvalue));
*/

		// Add hook to turn off everything...
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				logger.info("Turning OFF HVAC...");
				try {
					relayY1.setValue(false);
					statusControlValue.write(0);
					thermlogger.logSystemOnOff(false);
				} catch (IOException e) {
					e.printStackTrace();
				}  // Try to turn off the HVAC if we are terminated!!

				if (thermlogger != null)
					thermlogger.close();

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

		// Append the system properties with our applicaton properties
		props.putAll(System.getProperties());
		System.setProperties(props);

		logFileName = System.getProperty("dataLogFileName", "/var/log/" + ControlThread.APPLICATION_NAME + ".log");

		if (logFileName != null) {
			log.info("logging to: {}", logFileName);
			thermlogger = new ControlLogger(logFileName);
		}

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

		// The 1wire DS18B20's are connected to GPIO4 pin.
		String SYS_BUS_FS = System.getProperty(APPLICATION_NAME.toLowerCase() + ".SYS_BUS_FS", "/sys/bus/w1/devices/");

		String indoorFilename = SYS_BUS_FS + System.getProperty("0.source") + "/w1_slave";
		String outdoorFilename = SYS_BUS_FS + System.getProperty("1.source") + "/w1_slave";
		String plenumFilename = SYS_BUS_FS + System.getProperty("2.source") + "/w1_slave";
		String returnFilename = SYS_BUS_FS + System.getProperty("3.source") + "/w1_slave";

		thermometers.add(new Thermometer(0, System.getProperty("0.name"), new DS18B20(indoorFilename)));
		thermometers.add(new Thermometer(1, System.getProperty("1.name"), new DS18B20(outdoorFilename)));
		thermometers.add(new Thermometer(2, System.getProperty("2.name"), new DS18B20(plenumFilename)));
		thermometers.add(new Thermometer(3, System.getProperty("3.name"), new DS18B20(returnFilename)));

		System.out.println("Target Temperature File: " + userTargetTempValue);
		System.out.println("Indoor Temperature Name: " + System.getProperty("0.name") + " File: " + indoorFilename);
		System.out.println("Outdoor Temperature Name: " + System.getProperty("1.name") + " File: " + outdoorFilename);
		System.out.println("Plenum Temperature Name: " + System.getProperty("2.name") + " File: " + plenumFilename);
		System.out.println("Return Temperature Name: " + System.getProperty("3.name") + " File: " + returnFilename);
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
					thermlogger.logSystemOnOff(systemStatus);
					if (!systemStatus)
						logger.info("Turning System OFF");
					else
						logger.info("Turning System ON");
					lastSystemStatus = systemStatus;
				}

				if (!systemStatus) {
					if (stage1RelayPosition) { // turn off the relay if user wants it off!
						// TODO: call new method to turn off all outputs
						relayY1.setValue(false);
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
			H1TemperatureControl controller = new H1TemperatureControl(
					targetTemp,
					indoorTemp,
					0.5,
					currentRuntimeStart);

			ArrayList<RelayDef> relaysEnabled = controller.execute();
			boolean stage1Enable = relaysEnabled.contains(RelayDef.Y1);
			if (stage1Enable) {
				if (currentRuntimeStart == 0)
					currentRuntimeStart = System.currentTimeMillis();
			} else {
				if (currentRuntimeStart > 0) {
					thermlogger.logRuntime(System.currentTimeMillis() - currentRuntimeStart);
					currentRuntimeStart = 0;
				}
			}

			logSingle("Run?" + stage1Enable + " target:" + targetTemp + " indoorTemp:" + indoorTemp + " " + " stage1RelayPosition:" + stage1RelayPosition);

			thermlogger.logSummary(relaysEnabled, thermometers);

			try {
				// loop thru all the relays and set values accordingly.
				for (RelayMap rm : relayMap) {
					RelayDef rd = rm.getRelayDef();
					if (relaysEnabled.contains(rd)) {
						rm.getPiGPIO().setValue(true);
					} else {
						rm.getPiGPIO().setValue(false);
					}
				}

				if (stage1RelayPosition != stage1Enable) {
					logger.debug("***************");
					logger.debug("heat mode? " + controller.isHeat());
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
				continue;
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
