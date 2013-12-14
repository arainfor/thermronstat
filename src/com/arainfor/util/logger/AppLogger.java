/**
 * 
 */
package com.arainfor.util.logger;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	

}
