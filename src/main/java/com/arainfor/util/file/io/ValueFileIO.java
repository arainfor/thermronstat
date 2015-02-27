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
package com.arainfor.util.file.io;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Utility class that can read/write values on a single line of a file.
 *
 * @author arainfor
 *
 */
public class ValueFileIO {
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
