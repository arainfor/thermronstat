/**
 * 
 */
package com.arainfor.util.file.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
	
	public void write(double value) throws IOException {
		FileWriter fstream = new FileWriter(_filename, false);
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(Double.toString(value));
		out.close();
	}

	public double read() throws IOException {
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
