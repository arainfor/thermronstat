package com.arainfor.thermostat.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by arainfor on 2/5/15.
 */
public class TemperatureLogger extends FileLogger {

    private static final Logger logger = LoggerFactory.getLogger(TemperatureLogger.class);

    public TemperatureLogger() {
        super();
    }

    public void logMessage(String message) {
        StringBuffer sb = new StringBuffer();
        super.logMessage(sb, message);
        logger.info(sb.toString());
    }

}
