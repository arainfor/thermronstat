package com.arainfor.thermronstat.logger;

import com.arainfor.thermronstat.StringConstants;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by arainfor on 1/24/15.
 */
class FileLogger {

    void logHeader(StringBuffer sb) {
        //sb.append("Time: " + System.currentTimeMillis() + FieldDelimiter);
        SimpleDateFormat formatter = new SimpleDateFormat(StringConstants.FmtDateTimeMs + StringConstants.DateMessageDelimiter);
        sb.append(formatter.format(new Date()));
    }

    void logMessage(StringBuffer sb, String message) {
        logHeader(sb);
        sb.append(message);
    }

}
