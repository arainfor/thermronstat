/**
 * 
 */
package com.ronhull.thermronstat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.slf4j.Logger;

import com.arainfor.util.logger.AppLogger;

/**
 * @author arainfor
 *
 */
public class Thermronstat {
	
	protected static final String IO_BASE_FS = "/var/thermronstat";
	protected Logger _logger;
	
	public Thermronstat(int zone, int indoorIdx, int outdoorIdx) {
		
		_logger = new AppLogger().getLogger(this.getClass().getName());
		_logger.info("Application starting...");
		
		while (true) {
			
			// read all the known values
			int indoor_temp, outdoor_temp, target_temp = 0;
			try {
				target_temp = readTemperatureFile(IO_BASE_FS + "/" + zone + "/target");
				indoor_temp = readTemperatureFile(IO_BASE_FS + "/temperature/" + indoorIdx + "/f");
				outdoor_temp = readTemperatureFile(IO_BASE_FS + "/temperature/" + outdoorIdx + "/f");
			} catch (IOException ioe) {
				_logger.error(ioe.toString());
				break;
			}
			
			// determine if relay is on or off.
			String relayValue = "0";
			if (indoor_temp < target_temp) {
				relayValue = "1";
			}
			
			try {
				writeTemperatureFile(IO_BASE_FS + "/" + zone + "/relay/" + zone, relayValue);
			} catch (IOException e) {
				_logger.error(e.toString());
				break;
			}
		}

		_logger.info("Application stopping...");
	}
	
	protected void writeTemperatureFile(String filename, String text) throws IOException {
		FileWriter fstream = new FileWriter(filename, true);
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(text);
		out.close();
	}

	protected int readTemperatureFile(String filename) throws IOException {
		InputStream fis;
		BufferedReader br;
		String  line;
		
		fis = new FileInputStream(filename);
		br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
		while ((line = br.readLine()) != null) {
			return Integer.parseInt(line);
		}
		br.close();
		
		
		return -1;
	}
}
