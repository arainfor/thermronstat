package com.arainfor.thermronstat;

import com.arainfor.util.file.io.ValueFileIO;
import com.arainfor.util.file.io.gpio.PiGpio;

/**
 * Created by arainfor on 1/10/15.
 */
public class RelayMap {
    private final ValueFileIO valueFileIO;
    private final PiGpio piGpio;
    private final RelayDef relayDef;

    public RelayMap(RelayDef relayDef, PiGpio piGpio, ValueFileIO valueFileIO) {
        this.relayDef = relayDef;
        this.piGpio = piGpio;
        this.valueFileIO = valueFileIO;
    }

    public ValueFileIO getValueFileIO() {
        return valueFileIO;
    }

    public PiGpio getPiGpio() {
        return piGpio;
    }

    public RelayDef getRelayDef() {
        return relayDef;
    }

}
