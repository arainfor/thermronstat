package com.arainfor.thermronstat;

import com.arainfor.util.file.io.thermometer.DS18B20;

/**
 * Created by arainfor on 1/10/15.
 */
public class Thermometer {

    private final int index;
    private final String name;
    private final DS18B20 ds18B20;

    public Thermometer(int index, String name, DS18B20 ds18B20) {
        this.index = index;
        this.name = name;
        this.ds18B20 = ds18B20;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public DS18B20 getDs18B20() {
        return ds18B20;
    }

}
