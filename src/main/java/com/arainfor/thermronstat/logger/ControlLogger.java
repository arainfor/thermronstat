package com.arainfor.thermronstat.logger;

import com.arainfor.thermronstat.RelayDef;
import com.arainfor.thermronstat.StringConstants;
import com.arainfor.thermronstat.Thermometer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by arainfor on 1/10/15.
 */
public class ControlLogger extends FileLogger {

    private static final Logger logger = LoggerFactory.getLogger(ControlLogger.class);

    public ControlLogger() {
        super();
    }


    public void logSystemOnOff(boolean value) {
        StringBuffer sb = new StringBuffer();
        logHeader(sb);
        String entry = "System" + StringConstants.KeyValueDelimiter + (value ? "ON" : "OFF") + StringConstants.LineSeparator;
        sb.append(entry);

        logger.info(sb.toString());
    }

    public void logRuntime(long runtime) {
        StringBuffer sb = new StringBuffer();
        logHeader(sb);
        String entry = "Runtime" + StringConstants.KeyValueDelimiter + runtime + StringConstants.LineSeparator;
        sb.append(entry);

        logger.info(sb.toString());
    }

    public void logSummary(ArrayList<RelayDef> relaysEnabled, ArrayList<Thermometer> thermometers) {

        StringBuffer sb = new StringBuffer();
        logHeader(sb);
        sb.append("summary");
        sb.append(StringConstants.KeyValueDelimiter);
        for (RelayDef relayDef : RelayDef.values()) {
            String entry = relayDef + " " + relaysEnabled.contains(relayDef) + StringConstants.FieldDelimiter;
            sb.append(entry);
        }

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(1);
        for (Thermometer thermometer : thermometers) {
            try {
                String entry = "Thermometer idx" + StringConstants.KeyValueDelimiter + thermometer.getIndex()
                        + " " + thermometer.getName()
                        + " " + nf.format(thermometer.getDs18B20().getTempF()) + " ";
                sb.append(entry);
            } catch (IOException e) {
                logger.error("Exception:", e);
            }
        }

        sb.append(StringConstants.LineSeparator);
        logger.info(sb.toString());

    }

}
