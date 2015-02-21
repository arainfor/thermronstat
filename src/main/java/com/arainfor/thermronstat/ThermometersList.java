package com.arainfor.thermronstat;

import com.arainfor.util.file.io.thermometer.DS18B20;

import java.util.ArrayList;

/**
 * Created by arainfor on 1/31/15.
 * 18-feb-15 akr - Changed to Singleton Object
 */
public class ThermometersList {

    private static final ArrayList<Thermometer> thermometers = new ArrayList<Thermometer>();
    private static ThermometersList instance;

    private ThermometersList() {
        thermometers.add(new Thermometer(0, System.getProperty("i.name"), new DS18B20(System.getProperty("i.source")))); // indoor
        thermometers.add(new Thermometer(1, System.getProperty("o.name"), new DS18B20(System.getProperty("o.source")))); // outdoor
        thermometers.add(new Thermometer(2, System.getProperty("p.name"), new DS18B20(System.getProperty("p.source")))); // plenum
        thermometers.add(new Thermometer(3, System.getProperty("r.name"), new DS18B20(System.getProperty("r.source")))); // return
    }

    public static synchronized ThermometersList getInstance() {
        if (instance == null) {
            instance = new ThermometersList();
        }
        return instance;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Singleton object " + this.getClass().getSimpleName() + " cannot be cloned");
    }

    public ArrayList<Thermometer> list() {
        return thermometers;
    }

}
