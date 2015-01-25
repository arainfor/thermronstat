/**
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
