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

    private String pathName;

	/** 
	 * Create a path if we can and it doesn't exist.
     * @param path  The path name
     */
	public Path(String path) {
		super(path);
		pathName = path;
	}

	public boolean build() {
        return exists() && isDirectory() || mkdirs();
    }

	@Override
	public String toString() {
		if (!pathName.endsWith(File.separator))
			pathName = pathName.concat(File.separator);
		return pathName;
	}
}
