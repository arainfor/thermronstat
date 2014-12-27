/**
 * 
 */
package com.arainfor.util.file.io;

import java.io.File;

/**
 * @author arainfor
 *
 */
public class Path extends File {

	String pathName;

	/** 
	 * Create a path if we can and it doesn't exist.
	 * @param path
	 */
	public Path(String path) {
		super(path);
		pathName = path;
	}

	public boolean build() {
		if (exists() && isDirectory())
			return true;
		return mkdirs();
	}

	@Override
	public String toString() {
		if (!pathName.endsWith(File.separator))
			pathName = pathName.concat(File.separator);
		return pathName;
	}
}
