package com.arainfor.thermronstat;

import java.util.ArrayList;

/**
 * Created by arainfor on 1/31/15.
 */
public class TemperaturesList {

    private static final ArrayList<Temperature> temperatures = new ArrayList<Temperature>();

    private static TemperaturesList instance;

    private TemperaturesList() {
        temperatures.add(new Temperature(0, System.getProperty("i.name"))); // indoor
        temperatures.add(new Temperature(1, System.getProperty("o.name"))); // outdoor
        temperatures.add(new Temperature(2, System.getProperty("p.name"))); // plenum
        temperatures.add(new Temperature(3, System.getProperty("r.name"))); // return
    }

    public static synchronized TemperaturesList getInstance() {
        if (instance == null) {
            instance = new TemperaturesList();
        }
        return instance;
    }

    public ArrayList<Temperature> list() {
        return temperatures;
    }

}
