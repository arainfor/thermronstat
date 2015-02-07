package com.arainfor.thermronstat.logReader;

import com.arainfor.thermronstat.StringConstants;
import com.arainfor.thermronstat.Temperature;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ARAINFOR on 1/25/2015.
 */
public class TemperatureLogRecord {

    private final ArrayList<Temperature> temperatures = new ArrayList<Temperature>();
    private Date date;

    // 2015-02-05 12:31:33.506 - [indoor: 66.6, outdoor: ∞, plenum: 66, return: 63.6]
    public TemperatureLogRecord(String yyyymmdd, String thisLine) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(StringConstants.FmtDateTimeMs);
        String key = " - ";  // this separates date from relays.
        int keyIdx = thisLine.indexOf(key);

        if (keyIdx > StringConstants.FmtTimeMs.length()) {
            date = formatter.parse(thisLine.substring(0, keyIdx));
        } else {
            // old format didn't have date prefix
            String time = thisLine.substring(0, keyIdx);
            date = formatter.parse(yyyymmdd + " " + time);
        }


        int idx;
        key = StringConstants.ValueGroupStart;
        idx = thisLine.indexOf(key);
        idx += key.length();

        // indoor temp
        key = StringConstants.KeyValueDelimiter; // name terminate with
        String name = thisLine.substring(idx, thisLine.indexOf(key));
        idx += key.length() + name.length();
        key = StringConstants.FieldDelimiter; // temperature ends with
        String value = thisLine.substring(idx, thisLine.indexOf(key));
        idx += key.length() + value.length();
        Temperature temperature = new Temperature(0, name);
        temperature.setValue(parseTemperature(value));

        temperatures.add(temperature);

        // outdoor temp
        key = StringConstants.KeyValueDelimiter; // name terminate with
        name = thisLine.substring(idx, thisLine.indexOf(key, idx));
        idx += key.length() + name.length();
        key = StringConstants.FieldDelimiter; // temperature ends with
        value = thisLine.substring(idx, thisLine.indexOf(key, idx));
        idx += key.length() + value.length();
        temperature = new Temperature(0, name);
        temperature.setValue(parseTemperature(value));

        temperatures.add(temperature);

        // plenum
        key = StringConstants.KeyValueDelimiter; // name terminate with
        name = thisLine.substring(idx, thisLine.indexOf(key, idx));
        idx += key.length() + name.length();
        key = StringConstants.FieldDelimiter; // temperature ends with
        value = thisLine.substring(idx, thisLine.indexOf(key, idx));
        idx += key.length() + value.length();
        temperature = new Temperature(0, name);
        temperature.setValue(parseTemperature(value));

        temperatures.add(temperature);

        // returntemp
        key = StringConstants.KeyValueDelimiter; // name terminate with
        name = thisLine.substring(idx, thisLine.indexOf(key, idx));
        idx += key.length() + name.length();
        key = StringConstants.ValueGroupEnd; // temperature ends with
        value = thisLine.substring(idx, thisLine.indexOf(key, idx));
        idx += key.length() + value.length();
        temperature = new Temperature(0, name);
        temperature.setValue(parseTemperature(value));

        temperatures.add(temperature);

    }

    /**
     * @param args The Program Arguments
     */
    public static void main(String[] args) throws IOException {

        TemperatureLogRecord tlr = null;
        try {
            tlr = new TemperatureLogRecord("2015-02-05", "2015-02-05 12:31:33.506 - [indoor: 66.6, outdoor: ∞, plenum: 66, return: 63.6]");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(tlr != null ? tlr.toString() : null);
        System.out.println("Done");
    }

    protected Double parseTemperature(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return Double.POSITIVE_INFINITY;
        }

    }

    public Date getDate() {
        return date;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Date" + StringConstants.KeyValueDelimiter + " ").append(date).append(" ");

        for (Temperature temperature : temperatures) {
            sb.append(temperature.toString());
            sb.append(" ");
        }
        return sb.toString();
    }

}