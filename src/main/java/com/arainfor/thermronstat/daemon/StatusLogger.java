package com.arainfor.thermronstat.daemon;

import com.arainfor.thermronstat.RelayMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


/**
 * Created by arainfor on 1/24/15.
 */
public class StatusLogger extends FileLogger {

    private static final Logger logger = LoggerFactory.getLogger(StatusLogger.class);
    private HashMap<Integer, Boolean> relayMapLast = new HashMap<Integer, Boolean>();

    private List<String> eventList = Collections.synchronizedList(new ArrayList());

    public StatusLogger() throws IOException {
        super();
    }

    public void logRelays(ArrayList<RelayMap> relayMap) {

        HashMap<Integer, Boolean> relayMapNow = new HashMap<Integer, Boolean>();
        boolean dirty = false;
        boolean shutdown = true;

        // Read the relays...
        for (int i = 0; i < relayMap.size(); i++) {
            try {
                relayMapNow.put(i, relayMap.get(i).getPiGPIO().getValue());
                if (relayMapNow.get(i) != relayMapLast.get(i)) {
                    // if any relay position changed then we want a log entry.
                    dirty = true;
                    if (relayMapNow.get(i)) {
                        // if any relay is closed then we are not shutdown
                        shutdown = false;
                    }
                }
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

            // add the string to our event list
            eventList.add(sb.toString());

            if (shutdown) {
                // the system just shutdown so record all the completed cycle's
                for (String event : eventList) {
                    logger.info(event);
                }
                eventList.clear();
            }
        }

        relayMapLast = relayMapNow;

    }

    public void logMessage(String message) {
        StringBuffer sb = new StringBuffer();
        logHeader(sb);
        sb.append(message);
        logger.info(sb.toString());
    }
}
