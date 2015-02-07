/**
 * Utility class that can read/write values on a single line of a file.
 */
package com.arainfor.util.file.io;

import com.arainfor.util.logger.AppLogger;
import org.slf4j.Logger;

import java.io.*;
import java.nio.charset.Charset;

/**
 * @author arainfor
 *
 */
public class ValueFileIO {
    private final Logger logger = new AppLogger().getLogger(this.getClass().getName());
    private final String filename;

    public ValueFileIO(String filename) {
		this.filename = filename;
	}
	
	public void write(boolean value) throws IOException {
		write(value ? 1.0 : 0.0);
	}
	
	public void write(double value) throws IOException {
		FileWriter fileWriter = new FileWriter(filename, false);
		BufferedWriter out = new BufferedWriter(fileWriter);
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

		fis = new FileInputStream(filename);
		br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
        line = br.readLine();
        br.close();
        return Double.parseDouble(line);

	}
	
	public String toString() {
		return filename;
	}

}
