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
package com.arainfor.util.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
/**
 * @author arainfor
 *
 */
public class AppLogger {
	
	public void setup(File configFile) {
		if (configFile != null) {
			System.setProperty("logback.configurationFile",  configFile.toString());
		}
	}

	public Logger getLogger(String className) {
		return LoggerFactory.getLogger(className);
	}
	public Logger getLogger(Class aclass) {
		return LoggerFactory.getLogger(aclass);
	}

}
