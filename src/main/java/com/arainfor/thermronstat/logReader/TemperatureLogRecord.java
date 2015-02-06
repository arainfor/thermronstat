package com.arainfor.thermronstat.logReader;

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

    Date date;
    ArrayList<Temperature> temperatures = new ArrayList<Temperature>();

    // 2015-02-05 12:31:33.506 - [indoor: 66.6, outdoor: ∞, plenum: 66, return: 63.6]
    public TemperatureLogRecord(String yyyymmdd, String thisLine) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String key = " - ";  // this separates date from relays.
        int keyIdx = thisLine.indexOf(key);

        if (keyIdx > "HH:mm:ss.SSS".length()) {
            date = formatter.parse(thisLine.substring(0, keyIdx));
        } else {
            // old format didn't have date prefix
            String time = thisLine.substring(0, keyIdx);
            date = formatter.parse(yyyymmdd + " " + time);
        }


        int idx = 0;
        key = " [";
        idx = thisLine.indexOf(key);
        idx += key.length();

        // indoor temp
        key = ": "; // name terminate with
        String name = thisLine.substring(idx, thisLine.indexOf(key));
        idx += key.length() + name.length();
        key = ", "; // temperature ends with
        String value = thisLine.substring(idx, thisLine.indexOf(key));
        idx += key.length() + value.length();
        Temperature temperature = new Temperature(0, name);
        temperature.setValue(parseTemperature(value));

        temperatures.add(temperature);

        // outdoor temp
        key = ": "; // name terminate with
        name = thisLine.substring(idx, thisLine.indexOf(key, idx));
        idx += key.length() + name.length();
        key = ", "; // temperature ends with
        value = thisLine.substring(idx, thisLine.indexOf(key, idx));
        idx += key.length() + value.length();
        temperature = new Temperature(0, name);
        temperature.setValue(parseTemperature(value));

        temperatures.add(temperature);

        // plenum
        key = ": "; // name terminate with
        name = thisLine.substring(idx, thisLine.indexOf(key, idx));
        idx += key.length() + name.length();
        key = ", "; // temperature ends with
        value = thisLine.substring(idx, thisLine.indexOf(key, idx));
        idx += key.length() + value.length();
        temperature = new Temperature(0, name);
        temperature.setValue(parseTemperature(value));

        temperatures.add(temperature);

        // returntemp
        key = ": "; // name terminate with
        name = thisLine.substring(idx, thisLine.indexOf(key, idx));
        idx += key.length() + name.length();
        key = "]"; // temperature ends with
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
        System.out.println(tlr.toString());
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
        sb.append("Date: " + date + " ");

        for (Temperature temperature : temperatures) {
            sb.append(temperature.toString());
            sb.append(" ");
        }
//        for (Map.Entry<RelayDef, Boolean> relay : relays.entrySet()) {
//            sb.append(relay.getKey().getName() + ": " + relay.getValue() + " ");
//        }
        return sb.toString();
    }

}