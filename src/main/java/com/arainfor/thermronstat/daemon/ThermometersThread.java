package com.arainfor.thermronstat.daemon;

import com.arainfor.thermronstat.Temperature;
import com.arainfor.thermronstat.TemperaturesList;
import com.arainfor.thermronstat.Thermometer;
import com.arainfor.thermronstat.ThermometersList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This class reads the thermometers in a thread and saves the values in a TemperaturesList object.
 *
 * Created by ARAINFOR on 1/31/2015.
 */
public class ThermometersThread extends Thread {

    private static String APPLICATION_NAME = "ThermometerMonitor";
    protected Logger logger = LoggerFactory.getLogger(ThermometersThread.class);
    protected int sleep = Integer.parseInt(System.getProperty(APPLICATION_NAME + ".poll.sleep", "1400"));

    public ThermometersThread() {
        super();
        logger.info(this.getClass().getName() + " starting...");
    }

    @Override
    public void run() {

        ArrayList<Temperature> temperaturesList = TemperaturesList.getInstance().list();
        ArrayList<Thermometer> thermometersList = new ThermometersList().list();

        while (true) {

            for (Temperature temperature: temperaturesList) {
                Thermometer thermometer = thermometersList.get(temperature.getIndex());
                if (thermometer.getDs18B20().getFilename().contains("unknown")) {
                    temperature.setValue((double)0);
                } else {
                    try {
                        temperature.setValue(thermometer.getDs18B20().getTempF());
                    } catch (IOException e) {
                        logger.warn("Error reading thermometer " + thermometer.getName() + " Exception:" + e.getMessage());
                    }
                }
            }

            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                logger.error(e.toString());
                continue;
            }

        }
    }
}