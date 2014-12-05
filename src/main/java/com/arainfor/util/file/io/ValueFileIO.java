/**
 * Utility class that can read/write values on a single line of a file.
 */
package com.arainfor.util.file.io;

import java.io.*;
import java.nio.charset.Charset;

/**
 * @author arainfor
 *
 */
public class ValueFileIO {
	
	String _filename;
	public ValueFileIO(String filename) {
		_filename = filename;
	}
	
	public void write(boolean value) throws IOException {
		write(value ? 1.0 : 0.0);
	}
	
	public void write(double value) throws IOException {
		FileWriter fstream = new FileWriter(_filename, false);
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(Double.toString(value));
		out.close();
	}

	public boolean read() throws IOException {
		return readDouble() != 0.0;
	}
	
	public double readDouble() throws IOException {
		InputStream fis;
		BufferedReader br;
		String  line;
		
		fis = new FileInputStream(_filename);
		br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
		while ((line = br.readLine()) != null) {
			br.close();
			return Double.parseDouble(line);
		}
		br.close();
		
		return -1;
	}
	
	public String toString() {
		return _filename;
	}

}
