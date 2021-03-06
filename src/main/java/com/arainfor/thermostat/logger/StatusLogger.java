package com.arainfor.thermostat.logger;

import com.arainfor.thermostat.RelayMap;
import com.arainfor.thermostat.StringConstants;
import com.arainfor.thermostat.Temperature;
import com.arainfor.thermostat.TemperaturesList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;


/**
 * Created by arainfor on 1/24/15.
 */
public class StatusLogger extends FileLogger {

    private static final Logger logger = LoggerFactory.getLogger(StatusLogger.class);
    private final String propFileName = "thermostat.properties";
    private final List<String> eventList = Collections.synchronizedList(new ArrayList<String>());
    private HashMap<Integer, Boolean> relayMapLast = new HashMap<Integer, Boolean>();

    public StatusLogger() throws IOException {
        super();
    }

    public void logRelays(Map<RelayMap, Boolean> statusRelayValue) {

        HashMap<Integer, Boolean> relayMapNow = new HashMap<Integer, Boolean>();
        boolean dirty = false;
        boolean shutdown = true;

        ArrayList<Temperature> temperaturesList = TemperaturesList.getInstance().list();

        // Read the relays...
        Iterator<Map.Entry<RelayMap, Boolean>> relayMapInterator = statusRelayValue.entrySet().iterator();
        while (relayMapInterator.hasNext()) {
            Map.Entry<RelayMap, Boolean> relayMapEntry = relayMapInterator.next();
            RelayMap relay = relayMapEntry.getKey();
            int idx = relay.getRelayDef().ordinal();
            relayMapNow.put(idx, statusRelayValue.get(relay));
            if (relayMapNow.get(idx) != relayMapLast.get(idx)) {
                // if any relay position changed then we want a log entry.
                dirty = true;
            }
            if (relayMapNow.get(idx)) {
                // if any relay is closed then we are not shutdown
                shutdown = false;
            }
        }

        // Do entry if dirty
        if (dirty) {
            StringBuffer sb = new StringBuffer();
            logHeader(sb);

            Iterator<Map.Entry<RelayMap, Boolean>> entryIterator = statusRelayValue.entrySet().iterator();
            while (entryIterator.hasNext()) {
                Map.Entry<RelayMap, Boolean> mapEntry = entryIterator.next();
                RelayMap relay = mapEntry.getKey();
                int idx = relay.getRelayDef().ordinal();
                String entry = relay.getRelayDef() + StringConstants.KeyValueDelimiter + relayMapNow.get(idx);
                sb.append(entry);
                sb.append(StringConstants.FieldDelimiter);
            }

            // add the string to our event list
            eventList.add(sb.toString() + temperaturesList.get(0).toString()
                    + StringConstants.FieldDelimiter + temperaturesList.get(2).toString()
                    + StringConstants.FieldDelimiter + temperaturesList.get(3).toString()
                    + StringConstants.FieldDelimiter + temperaturesList.get(1).toString());

            if (shutdown) {
                // the system just shutdown so record all the completed cycle's
                Iterator<String> it = eventList.iterator();
                while (it.hasNext()) {
                    logger.info(it.next());
                }
                eventList.clear();
            }
        }

        relayMapLast = relayMapNow;

    }

    public void logMessage(String message) {
        StringBuffer sb = new StringBuffer();
        super.logMessage(sb, message);
        logger.info(sb.toString());
    }
}
