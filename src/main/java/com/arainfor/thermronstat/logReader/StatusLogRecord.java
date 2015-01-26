package com.arainfor.thermronstat.logReader;

import com.arainfor.thermronstat.RelayDef;

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

    Date date;
    HashMap<RelayDef, Boolean> relays = new HashMap<RelayDef, Boolean>();

    // 02:20:48.347 - G: false, Y1: false, Y2: false,
    public StatusLogRecord(String yyyymmdd, String thisLine) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String time = thisLine.substring(0, thisLine.indexOf("-"));
        date = formatter.parse(yyyymmdd + " " + time);

        String key = RelayDef.G.toString() + ": ";
        int gidx = thisLine.indexOf(key);
        String value = thisLine.substring(gidx + key.length(), thisLine.indexOf(",", gidx + key.length()));
        relays.put(RelayDef.G, value.equalsIgnoreCase(Boolean.TRUE.toString()));

        key = RelayDef.Y1.toString() + ": ";
        gidx = thisLine.indexOf(key);
        value = thisLine.substring(gidx + key.length(), thisLine.indexOf(",", gidx + key.length()));
        relays.put(RelayDef.Y1, value.equalsIgnoreCase(Boolean.TRUE.toString()));

        key = RelayDef.Y2.toString() + ": ";
        gidx = thisLine.indexOf(key);
        value = thisLine.substring(gidx + key.length(), thisLine.indexOf(",", gidx + key.length()));
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
        System.out.println(slr.toString());
        System.out.println("Done");
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Date: " + date + " ");
        for (Map.Entry<RelayDef, Boolean> relay : relays.entrySet()) {
            sb.append(relay.getKey().getName() + ": " + relay.getValue() + " ");
        }
        return sb.toString();
    }

}