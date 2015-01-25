package com.arainfor.thermronstat.daemon;

import java.io.File;
import java.io.IOException;

/**
 * Created by arainfor on 1/24/15.
 */
public class FileLogger {

    final String FieldDelimiter = ", ";
    final String LineSeparator = System.getProperty("line.separator");

    public FileLogger(String logFileName) throws IOException {
        if (logFileName != null) {
            File logFile;

            logFile = new File(logFileName);
            if (!logFile.exists()) {
                logFile.createNewFile();
            }

        }
    }

    protected void logHeader(StringBuffer sb) {
        sb.append("Time: " + System.currentTimeMillis() + FieldDelimiter);
    }


}
