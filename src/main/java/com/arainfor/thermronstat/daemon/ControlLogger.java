package com.arainfor.thermronstat.daemon;

import com.arainfor.thermronstat.RelayDef;
import com.arainfor.thermronstat.Thermometer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by arainfor on 1/10/15.
 */
public class ControlLogger extends FileLogger {

    private static final Logger logger = LoggerFactory.getLogger(ControlLogger.class);

    public ControlLogger() throws IOException {
        super();
    }


    protected void logSystemOnOff(boolean value) {
        StringBuffer sb = new StringBuffer();
        logHeader(sb);
        String entry = "System: " + (value ? "ON" : "OFF") + LineSeparator;
        sb.append(entry);

        logger.info(sb.toString());
    }

    protected void logRuntime(long runtime) {
        StringBuffer sb = new StringBuffer();
        logHeader(sb);
        String entry = "runtime: " + runtime + LineSeparator;
        sb.append(entry);

        logger.info(sb.toString());
    }

    protected void logSummary(ArrayList<RelayDef> relaysEnabled, ArrayList<Thermometer> thermometers) {

        StringBuffer sb = new StringBuffer();
        logHeader(sb);
        sb.append("summary: ");
        for (RelayDef relayDef : RelayDef.values()) {
            String entry = relayDef + " " + relaysEnabled.contains(relayDef) + FieldDelimiter;
            sb.append(entry);
        }

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(1);
        for (Thermometer thermometer : thermometers) {
            try {
                String entry = "Thermometer idx: " + thermometer.getIndex()
                        + " " + thermometer.getName()
                        + " " + nf.format(thermometer.getDs18B20().getTempF()) + " ";
                sb.append(entry);
            } catch (IOException e) {
                logger.error("Exception:", e);
            }
        }

        sb.append(LineSeparator);
        logger.info(sb.toString());

    }

}
