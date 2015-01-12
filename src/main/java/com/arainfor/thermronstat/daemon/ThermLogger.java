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
public class ThermLogger {

    private static final Logger logger = LoggerFactory.getLogger(ThermLogger.class);
    final String FieldDelimiter = ", ";
    final String LineSeparator = System.getProperty("line.separator");
    private FileOutputStream fos = null;

    public ThermLogger(String logFileName) {
        if (logFileName != null) {
            File logFile;

            logFile = new File(logFileName);
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    logger.error("Exception:", e);
                }
            }
            try {
                fos = new FileOutputStream(logFile, true);
            } catch (FileNotFoundException e) {
                logger.error("Exception:", e);
            }

        }
    }

    protected void logHeader(StringBuffer sb) {
        sb.append("Time: " + System.currentTimeMillis() + FieldDelimiter);
    }

    protected void logSystemOnOff(boolean value) {
        StringBuffer sb = new StringBuffer();
        logHeader(sb);
        sb.append("System: " + (value ? "ON" : "OFF") + LineSeparator);

        try {
            fos.write(sb.toString().getBytes());
        } catch (IOException e) {
            logger.error("Exception:", e);
        }
    }

    protected void logRuntime(long runtime) {
        if (fos != null) {
            StringBuffer sb = new StringBuffer();
            logHeader(sb);
            sb.append("runtime: " + runtime + LineSeparator);

            try {
                fos.write(sb.toString().getBytes());
            } catch (IOException e) {
                logger.error("Exception:", e);
            }

        }
    }

    protected void logSummary(ArrayList<RelayDef> relaysEnabled, ArrayList<Thermometer> thermometers) {

        if (fos != null) {
            StringBuffer sb = new StringBuffer();
            logHeader(sb);
            sb.append("summary: ");
            for (RelayDef relayDef : RelayDef.values()) {
                sb.append(relayDef + " " + relaysEnabled.contains(relayDef) + FieldDelimiter);
            }

            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(1);
            for (Thermometer thermometer : thermometers) {
                try {
                    sb.append("Thermometer idx: " + thermometer.getIndex()
                            + " " + thermometer.getName()
                            + " " + nf.format(thermometer.getDs18B20().getTempF()) + " ");
                } catch (IOException e) {
                    logger.error("Exception:", e);
                }
            }

            sb.append(LineSeparator);
            try {
                fos.write(sb.toString().getBytes());
            } catch (IOException e) {
                logger.error("Exception:", e);
            }

        }
    }

    public void close() {
        if (fos != null) {
            try {
                fos.flush();
                fos.close();
            } catch (IOException e) {
                logger.error("Exception:", e);
            }
        }
    }
}
