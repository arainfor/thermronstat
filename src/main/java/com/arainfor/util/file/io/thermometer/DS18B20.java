/**
 * Copyright 2014-2015
 * Alan Rainford arainfor@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.arainfor.util.file.io.thermometer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;

/**
 * This class is provides access to the DS18x20 series of temperature sensors.
 * The DS18x20 is a 1-wire (Dallas) protocol sensor.
 *
 * With this class you are able to either manually access the device or you can
 * register a callback and be notified of any changes.
 *
 * @author arainfor
 *
 */
public class DS18B20 extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(DS18B20.class);
    // The 1wire DS18B20's are connected to GPIO4 pin.
    private final String SYS_BUS_FS = "/sys/bus/w1/devices/";
    private String filename = null;
    private String serialId = null;
    private boolean valid;

    public DS18B20(String serialId) {
        this.serialId = serialId;
        this.filename = SYS_BUS_FS + serialId + "/w1_slave";
        valid = new File(this.filename).exists();
    }

    public String getFilename() {
        return filename;
    }

    public String getSerialId() {
        return serialId;
    }

    public double getTempF() throws IOException {
        return CelToFar(read());
	}

    public double getTempC() throws IOException {
        return read();
	}

    protected double read() throws IOException {

        if (isValid()) {
            logger.debug("read(): {}", filename);
            InputStream fis;
			BufferedReader br;
			String line;

            fis = new FileInputStream(filename);
            br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));

			line = br.readLine();

            // CRC OK?
			if (line.contains("YES")) {
				line = br.readLine();
			} else {
                logger.info("read(): {} invalid CRC", filename);
                line = null;
			}
			br.close();
			if (line != null) {
				int indexOfTemp = line.indexOf("=");
                String celsius = line.substring(indexOfTemp + 1);
                logger.debug("read(): celsius {}", celsius);
                // When first initialized we can get max or min value.
                if (celsius.startsWith("85") || celsius.startsWith("-62")) {
                    return Double.POSITIVE_INFINITY;
                }
                return Double.valueOf(celsius) / 1000;
            }
		}
        return Double.POSITIVE_INFINITY;

	}

	protected double CelToFar (double cel) {
		return cel * 1.8000 + 32.00;
	}

    public boolean isValid() {
        return valid;
    }

}
