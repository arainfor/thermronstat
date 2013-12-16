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
	
	/** 
	 * Create a path if we can and it doesn't exist.
	 * @param path
	 */
	public Path(String path) {
		super(path);
	}

	public boolean build() {
		if (exists() && isDirectory())
			return true;
		return mkdirs();
	}
}
