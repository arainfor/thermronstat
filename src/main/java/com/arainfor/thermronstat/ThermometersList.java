package com.arainfor.thermronstat;

import com.arainfor.util.file.io.thermometer.DS18B20;

import java.util.ArrayList;

/**
 * Created by arainfor on 1/31/15.
 */
public class ThermometersList {

    protected static ArrayList<Thermometer> thermometers = new ArrayList<Thermometer>();

    // The 1wire DS18B20's are connected to GPIO4 pin.
    String SYS_BUS_FS = "/sys/bus/w1/devices/";

    public ThermometersList() {
        String indoorFilename = SYS_BUS_FS + System.getProperty("0.source") + "/w1_slave";
        String outdoorFilename = SYS_BUS_FS + System.getProperty("1.source") + "/w1_slave";
        String plenumFilename = SYS_BUS_FS + System.getProperty("2.source") + "/w1_slave";
        String returnFilename = SYS_BUS_FS + System.getProperty("3.source") + "/w1_slave";

        thermometers.add(new Thermometer(0, System.getProperty("0.name"), new DS18B20(indoorFilename)));
        thermometers.add(new Thermometer(1, System.getProperty("1.name"), new DS18B20(outdoorFilename)));
        thermometers.add(new Thermometer(2, System.getProperty("2.name"), new DS18B20(plenumFilename)));
        thermometers.add(new Thermometer(3, System.getProperty("3.name"), new DS18B20(returnFilename)));


    }

    public ArrayList<Thermometer> list() {

        return thermometers;
    }

}
