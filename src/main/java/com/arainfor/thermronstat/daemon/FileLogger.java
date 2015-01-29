package com.arainfor.thermronstat.daemon;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by arainfor on 1/24/15.
 */
public class FileLogger {

    final String FieldDelimiter = ", ";
    final String LineSeparator = System.getProperty("line.separator");

    protected void logHeader(StringBuffer sb) {
        //sb.append("Time: " + System.currentTimeMillis() + FieldDelimiter);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS - ");
        sb.append(formatter.format(new Date()));
    }


}
