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
    private HashMap<Integer, Boolean> relayMapLast = new HashMap<Integer, Boolean>();

    public StatusLogger() throws IOException {
        super();
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
                logger.warn("Error reading relay:{}", relayMap.get(i).getRelayDef(), e);
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

            logger.info(sb.toString());
        }

        relayMapLast = relayMapNow;

    }
}
