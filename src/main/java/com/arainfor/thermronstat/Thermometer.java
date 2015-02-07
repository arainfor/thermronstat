package com.arainfor.thermronstat;

import com.arainfor.util.file.io.thermometer.DS18B20;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.NumberFormat;

/**
 * Created by arainfor on 1/10/15.
 */
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

    public String toString() {

        String temperature = Integer.toString(Integer.MIN_VALUE);

        try {
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(1);

            StringBuilder sb = new StringBuilder();
            sb.append(name);
            sb.append(": ");


            try {
                temperature = nf.format(ds18B20.getTempF());
            } catch (IOException e) {
                logger.warn("Error reading thermometer {} Exception:", ds18B20.getFilename(), e.getMessage());
            }
        } catch (Exception e) {
            logger.warn("Error reading thermometer {} Exception:", ds18B20.getFilename(), e.getMessage());
        } finally {
            return new String(name + ": " + temperature);
        }

    }

}
