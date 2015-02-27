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
