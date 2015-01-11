package com.arainfor.thermronstat;

import com.arainfor.util.file.io.ValueFileIO;
import com.arainfor.util.file.io.gpio.PiGPIO;

/**
 * Created by arainfor on 1/10/15.
 */
public class RelayMap {
    private final ValueFileIO valueFileIO;
    private final PiGPIO piGPIO;
    private final RelayDef relayDef;

    public RelayMap(RelayDef relayDef, PiGPIO piGPIO, ValueFileIO valueFileIO) {
        this.relayDef = relayDef;
        this.piGPIO = piGPIO;
        this.valueFileIO = valueFileIO;
    }

    public ValueFileIO getValueFileIO() {
        return valueFileIO;
    }

    public PiGPIO getPiGPIO() {
        return piGPIO;
    }

    public RelayDef getRelayDef() {
        return relayDef;
    }

}
