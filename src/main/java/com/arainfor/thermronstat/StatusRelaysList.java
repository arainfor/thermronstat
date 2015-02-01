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

    protected static PiGPIO relayG;   // relay for Fan G
    protected static PiGPIO relayY1;  // relay for Stage 1
    protected static PiGPIO relayY2;  // relay for Stage 2
    // these map the GPIO to a RelayInputs value
    protected ArrayList<RelayMap> relaysList = new ArrayList<RelayMap>();

    private static StatusRelaysList instance;

    public synchronized static StatusRelaysList getInstance() {
        if (instance == null) {
            instance = new StatusRelaysList();
        }
        return instance;
    }

    private StatusRelaysList() {
        // setup gpio
        try {

            relayG = new PiGPIO(new Pin(27), Direction.IN);
            relayY1 = new PiGPIO(new Pin(17), Direction.IN);
            relayY2 = new PiGPIO(new Pin(22), Direction.IN);

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

    public ArrayList<RelayMap> list() {
        return relaysList;
    }
}
