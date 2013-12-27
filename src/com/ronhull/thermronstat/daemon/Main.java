/**
 * 
 */
package com.ronhull.thermronstat.daemon;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.arainfor.util.file.io.Path;
import com.arainfor.util.file.io.ValueFileIO;
import com.arainfor.util.file.io.gpio.Direction;
import com.arainfor.util.file.io.gpio.PiGPIO;
import com.arainfor.util.file.io.gpio.Pin;
import com.ronhull.thermronstat.gui.Monitor;

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
	 * @param args
	 */
	public static void main(String[] args) {

		//System.err.println("The " + APPLICATION_NAME +" v1" + APPLICATION_VERSION_MAJOR + "." + APPLICATION_VERSION_MINOR + "." + APPLICATION_VERSION_BUILD);
		Options options = new Options();
		options.addOption("help", false, "This message isn't very helpful");
		options.addOption("version", false, "Print the version number");
		options.addOption("mkdirs", false, "Create missing paths");
		options.addOption("monitor", false, "Start GUI Monitor");

		CommandLineParser parser = new GnuParser();
		CommandLine cmd = null;
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
		
		Path targetPath = new Path(IO_BASE_FS + "/target");
		Path indoorPath = new Path(IO_BASE_FS + "/temperature/" + 0);
		Path outdoorPath = new Path(IO_BASE_FS + "/temperature/" + 1);
		Path relayPath = new Path(IO_BASE_FS + "/relay");
		Path statusPath = new Path(IO_BASE_FS + "/status");
		
		if (cmd != null && cmd.hasOption("mkdirs")) {
			targetPath.build();
			indoorPath.build();
			outdoorPath.build();
			relayPath.build();
			statusPath.build();
		}
		
		ValueFileIO targetVFIO = new ValueFileIO(targetPath.getAbsolutePath()+ "/0");
		ValueFileIO indoorVFIO = new ValueFileIO(indoorPath.getAbsolutePath() + "/f");
		ValueFileIO outdoorVFIO = new ValueFileIO(outdoorPath.getAbsolutePath() + "/f");
		ValueFileIO relayVFIO = new ValueFileIO(relayPath.getAbsolutePath() + "/0");
		ValueFileIO statusVFIO = new ValueFileIO(statusPath.getAbsolutePath() + "/0");
		
		System.out.println("Target Temperature File: " + targetVFIO);
		System.out.println("Indoor Temperature File: " + indoorVFIO);
		System.out.println("Outdoor Temperature File: " + outdoorVFIO);
		System.out.println("Relay Control File: " + relayVFIO);
		System.out.println("Status Control File: " + statusVFIO);  // User want's us on or off
		
		if (cmd != null && cmd.hasOption("monitor")) {
			new Monitor(statusVFIO, relayVFIO, indoorVFIO, outdoorVFIO, targetVFIO);
		}
		
		// Main entry point to launch the program
		new PollThread(Integer.parseInt(System.getProperty("poll.sleep", "1000")), heatPiGPIO, statusVFIO, relayVFIO, indoorVFIO, outdoorVFIO, targetVFIO).run();
		
	}

}
