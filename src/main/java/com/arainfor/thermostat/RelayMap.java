package com.arainfor.thermostat;

import com.arainfor.util.file.io.ValueFileIO;
import com.arainfor.util.file.io.gpio.SysFsGpio;

/**
 * Created by arainfor on 1/10/15.
 */
public class RelayMap {
    private final ValueFileIO valueFileIO;
    private final SysFsGpio sysFsGpio;
    private final RelayDef relayDef;

    public RelayMap(RelayDef relayDef, SysFsGpio sysFsGpio, ValueFileIO valueFileIO) {
        this.relayDef = relayDef;
        this.sysFsGpio = sysFsGpio;
        this.valueFileIO = valueFileIO;
    }

    public ValueFileIO getValueFileIO() {
        return valueFileIO;
    }

    public SysFsGpio getSysFsGpio() {
        return sysFsGpio;
    }

    public RelayDef getRelayDef() {
        return relayDef;
    }

}
