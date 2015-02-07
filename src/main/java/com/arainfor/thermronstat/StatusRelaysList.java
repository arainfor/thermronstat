package com.arainfor.thermronstat;

import com.arainfor.util.file.io.gpio.Direction;
import com.arainfor.util.file.io.gpio.PiGPIO;
import com.arainfor.util.file.io.gpio.Pin;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ARAINFOR on 1/31/2015.
 */
public class StatusRelaysList {

    private static PiGPIO relayG;   // relay for Fan G
    private static PiGPIO relayY1;  // relay for Stage 1
    private static PiGPIO relayY2;  // relay for Stage 2
    private static StatusRelaysList instance;
    // these map the GPIO to a RelayInputs value
    private final ArrayList<RelayMap> relaysList = new ArrayList<RelayMap>();

    private StatusRelaysList() {
        // setup gpio
        try {

            relayG = new PiGPIO(new Pin(Integer.parseInt(System.getProperty("g.pin", "27"))), Direction.IN);
            relayY1 = new PiGPIO(new Pin(Integer.parseInt(System.getProperty("y1.pin", "17"))), Direction.IN);
            relayY2 = new PiGPIO(new Pin(Integer.parseInt(System.getProperty("y2.pin", "22"))), Direction.IN);

        } catch (IOException ioe) {
            System.err.println("Fatal error initializing GPIO: " + ioe.getLocalizedMessage());
            ioe.printStackTrace();
            System.exit(-1);
        }

        // map the relays
        relaysList.add(new RelayMap(RelayDef.G, relayG, null));
        relaysList.add(new RelayMap(RelayDef.Y1, relayY1, null));
        relaysList.add(new RelayMap(RelayDef.Y2, relayY2, null));
    }

    public synchronized static StatusRelaysList getInstance() {
        if (instance == null) {
            instance = new StatusRelaysList();
        }
        return instance;
    }

    public ArrayList<RelayMap> list() {
        return relaysList;
    }
}
