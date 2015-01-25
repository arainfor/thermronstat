package com.arainfor.thermronstat.daemon;

import com.arainfor.thermronstat.RelayMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by arainfor on 1/24/15.
 */
public class StatusLogger extends FileLogger {

    private static final Logger logger = LoggerFactory.getLogger(StatusLogger.class);
    private FileOutputStream fos = null;
    private HashMap<Integer, Boolean> relayMapLast = new HashMap<Integer, Boolean>();

    public StatusLogger(String logFileName) throws IOException {
        super(logFileName);
        try {
            fos = new FileOutputStream(new File(logFileName), true);
        } catch (FileNotFoundException e) {
            logger.error("Exception:", e);
        }

    }


    public void logRelays(ArrayList<RelayMap> relayMap) {

        HashMap<Integer, Boolean> relayMapNow = new HashMap<Integer, Boolean>();
        boolean dirty = false;

        // Read the relays...
        for (int i = 0; i < relayMap.size(); i++) {
            try {
                relayMapNow.put(i, relayMap.get(i).getPiGPIO().getValue());
                if (relayMapNow.get(i) != relayMapLast.get(i))
                    dirty = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        // Do entry if dirty
        if (dirty) {
            StringBuffer sb = new StringBuffer();

            logHeader(sb);
            for (int i = 0; i < relayMap.size(); i++) {
                String entry = relayMap.get(i).getRelayDef() + ": " + relayMapNow.get(i);
                sb.append(entry);
                sb.append(FieldDelimiter);
            }
            sb.append(System.getProperty("line.separator"));

            try {
                fos.write(sb.toString().getBytes());
            } catch (IOException e) {
                logger.error("Exception:", e);
            }
        }

        relayMapLast = relayMapNow;

    }
}
