package com.arainfor.thermronstat;

import com.arainfor.util.file.io.thermometer.DS18B20;

import java.util.ArrayList;

/**
 * Created by arainfor on 1/31/15.
 */
public class ThermometersList {

    protected static ArrayList<Thermometer> thermometers = new ArrayList<Thermometer>();

    public ThermometersList() {
        thermometers.add(new Thermometer(0, System.getProperty("0.name"), new DS18B20(System.getProperty("0.source"))));
        thermometers.add(new Thermometer(1, System.getProperty("1.name"), new DS18B20(System.getProperty("1.source"))));
        thermometers.add(new Thermometer(2, System.getProperty("2.name"), new DS18B20(System.getProperty("2.source"))));
        thermometers.add(new Thermometer(3, System.getProperty("3.name"), new DS18B20(System.getProperty("3.source"))));
    }

    public ArrayList<Thermometer> list() {
        return thermometers;
    }

}
