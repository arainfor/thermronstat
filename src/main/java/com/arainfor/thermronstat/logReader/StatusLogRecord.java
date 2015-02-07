package com.arainfor.thermronstat.logReader;

import com.arainfor.thermronstat.RelayDef;
import com.arainfor.thermronstat.StringConstants;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ARAINFOR on 1/25/2015.
 */
public class StatusLogRecord {

    final HashMap<RelayDef, Boolean> relays = new HashMap<RelayDef, Boolean>();
    Date date;

    // 02:20:48.347 - G: false, Y1: false, Y2: false,
    public StatusLogRecord(String yyyymmdd, String thisLine) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(StringConstants.FmtDateTimeMs);
        String key = " - ";  // this separates date from relays.
        int keyIdx = thisLine.indexOf(key);

        if (keyIdx > "HH:mm:ss.SSS".length()) {
            date = formatter.parse(thisLine.substring(0, keyIdx));
        } else {
            // old format didn't have date prefix
            String time = thisLine.substring(0, keyIdx);
            date = formatter.parse(yyyymmdd + " " + time);
        }

        key = RelayDef.G.toString() + StringConstants.KeyValueDelimiter;
        int idx = thisLine.indexOf(key);
        String value = thisLine.substring(idx + key.length(), thisLine.indexOf(",", idx + key.length()));
        relays.put(RelayDef.G, value.equalsIgnoreCase(Boolean.TRUE.toString()));

        key = RelayDef.Y1.toString() + StringConstants.KeyValueDelimiter;
        idx = thisLine.indexOf(key);
        value = thisLine.substring(idx + key.length(), thisLine.indexOf(",", idx + key.length()));
        relays.put(RelayDef.Y1, value.equalsIgnoreCase(Boolean.TRUE.toString()));

        key = RelayDef.Y2.toString() + StringConstants.KeyValueDelimiter;
        idx = thisLine.indexOf(key);
        value = thisLine.substring(idx + key.length(), thisLine.indexOf(",", idx + key.length()));
        relays.put(RelayDef.Y2, value.equalsIgnoreCase(Boolean.TRUE.toString()));
    }

    /**
     * @param args The Program Arguments
     */
    public static void main(String[] args) throws IOException {

        StatusLogRecord slr = null;
        try {
            slr = new StatusLogRecord("2015-01-25", "02:20:48.347 - G: false, Y1: false, Y2: false,");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(slr != null ? slr.toString() : null);
        System.out.println("Done");
    }

    public Date getDate() {
        return date;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Date" + StringConstants.KeyValueDelimiter).append(date).append(" ");
        for (Map.Entry<RelayDef, Boolean> relay : relays.entrySet()) {
            sb.append(relay.getKey().getName()).append(StringConstants.KeyValueDelimiter).append(relay.getValue()).append(" ");
        }
        return sb.toString();
    }

}