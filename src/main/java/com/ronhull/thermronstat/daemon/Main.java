/**
 * 
 */
package com.ronhull.thermronstat.daemon;

import com.arainfor.util.file.io.Path;
import com.arainfor.util.file.io.ValueFileIO;
import com.arainfor.util.file.io.gpio.Direction;
import com.arainfor.util.file.io.gpio.PiGPIO;
import com.arainfor.util.file.io.gpio.Pin;
import com.arainfor.util.file.io.thermometer.DS18B20;
import org.apache.commons.cli.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author arainfor
 *
 */
public class Main {

	protected static final String IO_BASE_FS = "/var/thermronstat";
	private static String APPLICATION_NAME = "ThermRonStat";
	private static int APPLICATION_VERSION_MAJOR = 1;
	private static int APPLICATION_VERSION_MINOR = 0;
	private static int APPLICATION_VERSION_BUILD = 5;

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
				hf.printHelp(APPLICATION_NAME , options);
				return;
			}
			if (cmd.hasOption("version")) {
				System.out.println("The " + APPLICATION_NAME +" v" + APPLICATION_VERSION_MAJOR + "." + APPLICATION_VERSION_MINOR + "." + APPLICATION_VERSION_BUILD);
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		
		// setup gpio
		PiGPIO heatPiGPIO = null;
		try {
			heatPiGPIO = new PiGPIO(new Pin(17), Direction.OUT);
		} catch (IOException ioe) {
			System.err.println("Fatal error initializing GPIO: " + ioe.getLocalizedMessage());
			ioe.printStackTrace();
			System.exit(-1);
		}

		String propFileName = "thermostat.properties";
		if (cmd.getOptionValue("config") != null)
			propFileName = cmd.getOptionValue("config");

		InputStream inputStream = new FileInputStream(propFileName);
		Properties prop = new Properties();

		if (inputStream != null) {
			prop.load(inputStream);
		} else {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		}

		Path targetPath = new Path(IO_BASE_FS + "/target");
		Path relayPath = new Path(IO_BASE_FS + "/relay");
		Path statusPath = new Path(IO_BASE_FS + "/status");

		if (cmd.hasOption("mkdirs")) {
			targetPath.build();
			relayPath.build();
			statusPath.build();
		}
		
		ValueFileIO targetVFIO = new ValueFileIO(targetPath.getAbsolutePath()+ "/0");
		ValueFileIO relayVFIO = new ValueFileIO(relayPath.getAbsolutePath() + "/0");
		ValueFileIO statusVFIO = new ValueFileIO(statusPath.getAbsolutePath() + "/0");

		String indoorFilename = "/sys/bus/w1/devices/" + prop.getProperty("0.source") + "/w1_slave";
		String outdoorFilename = "/sys/bus/w1/devices/" + prop.getProperty("1.source") + "/w1_slave";
		String plenumFilename = "/sys/bus/w1/devices/" + prop.getProperty("2.source") + "/w1_slave";
		String returnFilename = "/sys/bus/w1/devices/" + prop.getProperty("3.source") + "/w1_slave";

		DS18B20 indoorSensor = new DS18B20(indoorFilename);
		DS18B20 outdoorSensor = new DS18B20(outdoorFilename);
		DS18B20 plenumSensor = new DS18B20(indoorFilename);
		DS18B20 returnSensor = new DS18B20(indoorFilename);

		System.out.println("Target Temperature File: " + targetVFIO);
		System.out.println("Indoor Temperature File: " + indoorFilename);
		System.out.println("Outdoor Temperature File: " + outdoorFilename);
		System.out.println("Plenum Temperature File: " + plenumFilename);
		System.out.println("Return Temperature File: " + returnFilename);
		System.out.println("Relay Control File: " + relayVFIO);
		System.out.println("System Available Control File: " + statusVFIO);  // User want's us on or off
		
//		if (cmd != null && cmd.hasOption("monitor")) {
//			new Monitor(statusVFIO, relayVFIO, indoorVFIO, outdoorVFIO, targetVFIO);
//		}
		
		// Main entry point to launch the program
		new PollThread(Integer.parseInt(System.getProperty("poll.sleep", "1000")), heatPiGPIO, statusVFIO, relayVFIO, indoorSensor, outdoorSensor, targetVFIO).run();
		
	}

}
