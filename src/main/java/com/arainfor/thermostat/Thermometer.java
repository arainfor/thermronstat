package com.arainfor.thermostat;

import com.arainfor.util.file.io.thermometer.DS18B20;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Thermometer {

    private static final Logger logger = LoggerFactory.getLogger(Thermometer.class);
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

    /**
     * Prefered method to call as there is no IO
     *
     * @param value The temperature.
     * @return Human readable description
     */
    public String toString(double value) {
        String temperature = Integer.toString(Integer.MIN_VALUE);

        try {
            temperature = Temperature.getValueString(value);
        } catch (Exception e) {
            logger.warn("Error reading thermometer {} Exception:", ds18B20.getFilename(), e);
        }
        return name + StringConstants.KeyValueDelimiter + temperature;
    }

    @Override
    public String toString() {
        try {
            return toString(getDs18B20().getTempF());
        } catch (IOException e) {
            logger.warn("Error reading thermometer {} IOException:", ds18B20.getFilename(), e);
        }
        return name + StringConstants.KeyValueDelimiter + Double.POSITIVE_INFINITY;
    }

}
