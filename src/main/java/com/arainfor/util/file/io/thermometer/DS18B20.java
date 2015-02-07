/**
 * 
 */
package com.arainfor.util.file.io.thermometer;

import java.io.*;
import java.nio.charset.Charset;

/**
 * @author arainfor
 *
 */
public class DS18B20 {

	// The 1wire DS18B20's are connected to GPIO4 pin.
	String SYS_BUS_FS = "/sys/bus/w1/devices/";

	String filename = null;

	public DS18B20(String serialId) {
        this.filename = SYS_BUS_FS + serialId + "/w1_slave";
    }

    public String getFilename() {
        return filename;
    }

    public Double getTempF() throws IOException {
		return CelToFar(readRaw());
	}
	
	public Double getTempC() throws IOException {
		return readRaw();
	}

	protected Double readRaw() throws IOException {
        if (!filename.equalsIgnoreCase("unknown")) {
            InputStream fis;
			BufferedReader br;
			String line;

            fis = new FileInputStream(filename);
            br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));

			line = br.readLine();

			if (line.contains("YES")) {
				line = br.readLine();
			} else {
				line = null;
			}
			br.close();

			if (line != null) {
				int indexOfTemp = line.indexOf("=");
				String celcius = line.substring(indexOfTemp + 1);
				return Double.valueOf(celcius) / 1000;
			}
		}
		return Double.NEGATIVE_INFINITY;

	}

	protected double CelToFar (double cel) {
		return cel * 1.8000 + 32.00;
	}

}
