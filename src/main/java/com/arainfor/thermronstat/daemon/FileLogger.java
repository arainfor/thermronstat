package com.arainfor.thermronstat.daemon;

import java.io.File;
import java.io.IOException;

/**
 * Created by arainfor on 1/24/15.
 */
public class FileLogger {

    final String FieldDelimiter = ", ";
    final String LineSeparator = System.getProperty("line.separator");

    protected void logHeader(StringBuffer sb) {
        //sb.append("Time: " + System.currentTimeMillis() + FieldDelimiter);
    }


}
