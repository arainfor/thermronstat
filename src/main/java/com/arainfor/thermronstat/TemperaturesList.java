package com.arainfor.thermronstat;

import java.util.ArrayList;

/**
 * Created by arainfor on 1/31/15.
 */
public class TemperaturesList {

    protected static ArrayList<Temperature> temperatures = new ArrayList<Temperature>();

    static TemperaturesList instance;

    private TemperaturesList() {
        temperatures.add(new Temperature(0, System.getProperty("0.name")));
        temperatures.add(new Temperature(1, System.getProperty("1.name")));
        temperatures.add(new Temperature(2, System.getProperty("2.name")));
        temperatures.add(new Temperature(3, System.getProperty("3.name")));
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
