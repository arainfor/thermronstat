/**
 * 
 */
package com.arainfor.util.file.io.thermometer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * @author arainfor
 *
 */
public class DS18B20 {
	
	String _filename = null;

	public DS18B20(String filename) {
		_filename = filename;
	}
	
	public Double getTempF() throws IOException {
		return CelToFar(readRaw());
		
	}
	
	public Double getTempC() throws IOException {
		return readRaw();
	}
	
	protected Double readRaw() throws IOException {
		InputStream fis;
		BufferedReader br;
		String  line;
		
		fis = new FileInputStream(_filename);
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
			return Double.valueOf(celcius) /  1000;
		}
		return (double) Double.NEGATIVE_INFINITY;

	}

	protected double CelToFar (double cel) {
		return cel * 1.8000 + 32.00;
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		DS18B20 sensor = new DS18B20("/home/arainfor/tmp/w1_slave");
		
		System.err.println(sensor.CelToFar(sensor.readRaw()));

	}

}
