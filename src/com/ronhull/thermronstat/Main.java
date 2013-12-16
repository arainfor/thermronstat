/**
 * 
 */
package com.ronhull.thermronstat;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.arainfor.util.file.io.Path;
import com.arainfor.util.file.io.ValueFileIO;

/**
 * @author arainfor
 *
 */
public class Main {

	protected static final String IO_BASE_FS = "/var/thermronstat";

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		System.err.println("The ThermRonStat v1.0");
		Options options = new Options();
		options.addOption("help", false, "This message isn't very helpful");
		options.addOption("mkdirs", false, "Create missing paths");
		options.addOption("monitor", false, "Start GUI Monitor");

		CommandLineParser parser = new GnuParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
			if (cmd.hasOption("help")) {
				HelpFormatter hf = new HelpFormatter();
				hf.printHelp("thermronstat", options);
				return;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return;
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
		new PollThread(statusVFIO, relayVFIO, indoorVFIO, outdoorVFIO, targetVFIO);
		
	}


}
