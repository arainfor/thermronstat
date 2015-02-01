package com.arainfor.thermronstat.daemon;

import com.arainfor.thermronstat.RelayMap;
import com.arainfor.thermronstat.Thermometer;
import com.arainfor.thermronstat.ThermometersList;
import com.arainfor.util.file.PropertiesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;


/**
 * Created by arainfor on 1/24/15.
 */
public class StatusLogger extends FileLogger {

    private static final Logger logger = LoggerFactory.getLogger(StatusLogger.class);
    protected static ArrayList<Thermometer> thermometers = new ArrayList<Thermometer>();
    // The 1wire DS18B20's are connected to GPIO4 pin.
    String SYS_BUS_FS = System.getProperty(StatusThread.APPLICATION_NAME.toLowerCase() + ".SYS_BUS_FS", "/sys/bus/w1/devices/");
    String propFileName = "thermostat.properties";
    private HashMap<Integer, Boolean> relayMapLast = new HashMap<Integer, Boolean>();
    private List<String> eventList = Collections.synchronizedList(new ArrayList());


    public StatusLogger() throws IOException {
        super();

        logger.info("loading...{}", propFileName);
        Properties props = new PropertiesLoader(propFileName).getProps();

        // Append the system properties with our application properties
        props.putAll(System.getProperties());
        System.setProperties(props);
        thermometers = new ThermometersList().list();
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
            eventList.add(sb.toString() + ", " + thermometers.get(0).toString() + ", " + thermometers.get(2).toString() + ", " + thermometers.get(3).toString());

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
