package com.arainfor.thermronstat.logReader;

import com.arainfor.thermronstat.RelayDef;
import com.arainfor.thermronstat.StringConstants;
import com.arainfor.thermronstat.Temperature;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ARAINFOR on 1/25/2015.
 */
public class StatusLogRecord {

    final HashMap<RelayDef, Boolean> relays = new HashMap<RelayDef, Boolean>();
    ArrayList<Temperature> temperatures = new ArrayList<Temperature>();

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

        // set idx to end of relays
        idx += value.length() + key.length() + StringConstants.FieldDelimiter.length();

        if (thisLine.length() > idx) {
            key = thisLine.substring(idx, thisLine.indexOf(StringConstants.KeyValueDelimiter, idx));
            value = thisLine.substring(idx + key.length() + StringConstants.KeyValueDelimiter.length(), thisLine.indexOf(",", idx + key.length()));
            Temperature temperature = new Temperature(0, key);
            try {
                temperature.setValue(Double.parseDouble(value));

            } catch (NumberFormatException nfe) {
            }
            temperatures.add(temperature);

            idx += value.length() + key.length() + StringConstants.FieldDelimiter.length();
            key = thisLine.substring(idx, thisLine.indexOf(StringConstants.KeyValueDelimiter, idx));
            value = thisLine.substring(idx + key.length() + StringConstants.KeyValueDelimiter.length(), thisLine.indexOf(StringConstants.FieldDelimiter, idx + key.length()));

            temperature = new Temperature(2, key);
            try {
                temperature.setValue(Double.parseDouble(value));

            } catch (NumberFormatException nfe) {
            }
            temperatures.add(temperature);

            idx += value.length() + key.length() + StringConstants.FieldDelimiter.length();
            key = thisLine.substring(idx, thisLine.indexOf(StringConstants.KeyValueDelimiter, idx));

            value = thisLine.substring(idx + key.length() + StringConstants.KeyValueDelimiter.length());  // last one no field

            temperature = new Temperature(3, key);
            try {
                temperature.setValue(Double.parseDouble(value));

            } catch (NumberFormatException nfe) {
            }
            temperatures.add(temperature);
        }
    }

    /**
     * @param args The Program Arguments
     */
    public static void main(String[] args) throws IOException {

        StatusLogRecord slr = null;
        try {
            slr = new StatusLogRecord("2015-01-25", "2015-02-14 10:30:13.670 - G: true, Y1: true, Y2: false, indoor: 68.4, plenum: 79.2, return: 63.6");
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

        for (Temperature temp : temperatures) {
            sb.append(temp).append(" ");
        }
        return sb.toString();
    }

}