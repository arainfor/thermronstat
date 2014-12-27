/**
 * 
 */
package com.arainfor.thermronstat.daemon;

import com.arainfor.thermronstat.H1TemperatureControl;
import com.arainfor.util.file.PropertiesLoader;
import com.arainfor.util.file.io.Path;
import com.arainfor.util.file.io.ValueFileIO;
import com.arainfor.util.file.io.gpio.Direction;
import com.arainfor.util.file.io.gpio.PiGPIO;
import com.arainfor.util.file.io.gpio.Pin;
import com.arainfor.util.file.io.thermometer.DS18B20;
import com.arainfor.util.logger.AppLogger;
import org.apache.commons.cli.*;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Properties;

/**
 * @author arainfor
 *
 */
public class PollThread extends Thread {

	// relays
	protected static PiGPIO stage1Relay;  // relay for Stage 1
	// value files
	protected static ValueFileIO statusControl;
	protected static ValueFileIO stage1Control;   // user control file for stage1Relay
	protected static ValueFileIO targetControl;
	// Thermometers
	protected static DS18B20 indoorSensor;
	protected static DS18B20 outdoorSensor;
	protected static DS18B20 plenumSensor;
	protected static DS18B20 returnSensor;
	static String oldSingleMsg;
	private static String APPLICATION_NAME = "ThermRonStat";
	private static int APPLICATION_VERSION_MAJOR = 1;
	private static int APPLICATION_VERSION_MINOR = 0;
	private static int APPLICATION_VERSION_BUILD = 5;
	protected Logger logger;
	protected int sleep = Integer.parseInt(System.getProperty("poll.sleep", "1000"));

	public PollThread() {

		super();

		logger = new AppLogger().getLogger(this.getClass().getName());
		logger.info(this.getClass().getName() + " starting...");

		// Add hook to turn off everything...
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
		    public void run() {
				logger.info("Turning OFF HVAC...");
				try {
					stage1Relay.setValue(false);
					statusControl.write(0);
				} catch (IOException e) {
					e.printStackTrace();
				}  // Try to turn off the HVAC if we are terminated!!
				logger.info(this.getClass().getName() + " terminated...");
			}
		}));

		// setup gpio
		try {
			stage1Relay = new PiGPIO(new Pin(17), Direction.OUT);
		} catch (IOException ioe) {
			System.err.println("Fatal error initializing GPIO: " + ioe.getLocalizedMessage());
			ioe.printStackTrace();
			System.exit(-1);
		}

	}

	/**
	 * @param args The Program Arguments
	 */
	public static void main(String[] args) throws IOException {

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

		Properties props = new PropertiesLoader(propFileName).getProps();

		// Append the system properties with our applicaton properties
		props.putAll(System.getProperties());
		System.setProperties(props);

		String IO_BASE_FS = System.getProperty("thermronstat.IO_BASE_FS", "/var/thermronstat");

		Path targetPath = new Path(IO_BASE_FS + "/target");
		Path relayPath = new Path(IO_BASE_FS + "/relay");
		Path statusPath = new Path(IO_BASE_FS + "/status");

		if (cmd.hasOption("mkdirs")) {
			targetPath.build();
			relayPath.build();
			statusPath.build();
		}

		targetControl = new ValueFileIO(targetPath.getAbsolutePath() + "/0");
		stage1Control = new ValueFileIO(relayPath.getAbsolutePath() + "/0");
		statusControl = new ValueFileIO(statusPath.getAbsolutePath() + "/0");

		String SYS_BUS_FS = System.getProperty("thermronstat.SYS_BUS_FS", "/sys/bus/w1/devices/");

		String indoorFilename = SYS_BUS_FS + System.getProperty("0.source") + "/w1_slave";
		String outdoorFilename = SYS_BUS_FS + System.getProperty("1.source") + "/w1_slave";
		String plenumFilename = SYS_BUS_FS + System.getProperty("2.source") + "/w1_slave";
		String returnFilename = SYS_BUS_FS + System.getProperty("3.source") + "/w1_slave";

		indoorSensor = new DS18B20(indoorFilename);
		outdoorSensor = new DS18B20(outdoorFilename);
		plenumSensor = new DS18B20(plenumFilename);
		returnSensor = new DS18B20(returnFilename);

		System.out.println("Target Temperature File: " + targetControl);
		System.out.println("Indoor Temperature Name: " + System.getProperty("0.name") + " File: " + indoorFilename);
		System.out.println("Outdoor Temperature Name: " + System.getProperty("1.name") + " File: " + outdoorFilename);
		System.out.println("Plenum Temperature Name: " + System.getProperty("2.name") + " File: " + plenumFilename);
		System.out.println("Return Temperature Name: " + System.getProperty("3.name") + " File: " + returnFilename);
		System.out.println("Relay Control File: " + stage1Control);  // Is the system currently running?
		System.out.println("System Available Control File: " + statusControl);  // User desired state of relay, on or off

		// Main entry point to launch the program
		PollThread thermostat = new PollThread();
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

			boolean relayPosition = false;
			boolean systemStatus = false;

			try {
				systemStatus = statusControl.read();
				relayPosition = stage1Control.read();

				// Display a message if we toggled systemStatus.
				if (systemStatus != lastSystemStatus) {
					if (!systemStatus)
						logger.info("Turning System OFF");
					else
						logger.info("Turning System ON");
					lastSystemStatus = systemStatus;
				}

				if (!systemStatus) {
					if (relayPosition) { // turn off the relay if user wants it off!
						// TODO: call new method to turn off all outputs
						stage1Relay.setValue(false);
					}
					continue;
				}

			} catch (IOException e2) {
				logger.error("Error reading file: " + e2);
				e2.printStackTrace();
				continue;
			}

			// read all the known values
			double controlTemp, ambientTemp, targetTemp = 0;

			try {
				targetTemp = targetControl.readDouble();
				controlTemp = indoorSensor.getTempF();
				ambientTemp = outdoorSensor.getTempF();
			} catch (IOException ioe) {
				logger.error("Temperature Read error!: " + ioe.toString());
				ioe.printStackTrace();
				continue;
			}

			// the real decision is here!
			H1TemperatureControl controller = new H1TemperatureControl(targetTemp, controlTemp, ambientTemp, 0.5, relayPosition);
			boolean stage1Enable = controller.enable();

			logSingle("Run?" + stage1Enable + " target:" + targetTemp + " controlTemp:" + controlTemp + " " + " relayPosition:" + relayPosition);

			try {
				if (relayPosition != stage1Enable) {
					if (stage1Enable)
						stage1Relay.setValue(true);
					else
						stage1Relay.setValue(false);

					logger.debug("***************");
					logger.debug("heat mode? " + controller.isHeat());
					logger.debug("target_temp=" + targetTemp);
					logger.debug("indoor_temp=" + controlTemp);
					logger.debug("outdoor_temp=" + ambientTemp);
					logger.info("Relay changed from:" + relayPosition + " to:" + stage1Enable);

					// Change to the new setting...
					stage1Control.write(stage1Enable);
				}
			} catch (IOException e) {
				logger.error("Relay Control Error: " + e.toString());
				e.printStackTrace();
				continue;
			}
		}
	}

	private void logSingle(String msg) {
		if (msg.equals(oldSingleMsg))
			return;
		logger.debug(msg);
		oldSingleMsg = msg;
	}

}
