/**
 * Copyright 2014-2015
 * Alan Rainford arainfor@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.arainfor.util.file.io.thermometer;

import com.arainfor.thermostat.Temperature;
import com.arainfor.thermostat.Thermometer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by arainfor on 2/22/15.
 */
public class CallbackMonitor extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(CallbackMonitor.class);
    private String lastTemperature;
    private ThermometerCallback thermometerCallback;
    private Thermometer thermometer;

    public CallbackMonitor(ThermometerCallback thermometerCallback, Thermometer thermometer) {
        super(thermometer.getName());
        this.thermometerCallback = thermometerCallback;
        this.thermometer = thermometer;
    }

    public static void main(String[] args) throws IOException {
        CallbackMonitor cm = new CallbackMonitor(null, new Thermometer(0, "Test", null));
        System.err.println(cm.crossedThreshold("80.0", "80.2"));
    }

    @Override
    public void run() {
        while (true) {

            try {
                double tempF = thermometer.getDs18B20().getTempF();
                String currentTemperature = Temperature.getValueString(tempF);

                if (crossedThreshold(lastTemperature, currentTemperature)) {
                    thermometerCallback.subjectChanged(thermometer, Double.parseDouble(currentTemperature));
                    lastTemperature = currentTemperature;
                }

            } catch (IOException e) {
                logger.warn("Thread interrupted:", e);
            }

            try {
                Thread.sleep(Integer.parseInt(System.getProperty(DS18B20.class.getSimpleName() + ".sleep", "4750")));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private boolean crossedThreshold(String lastTemperature, String currentTemperature) {
        try {
            double diff = Math.abs(Double.parseDouble(lastTemperature) - Double.parseDouble(currentTemperature));
            diff = Double.parseDouble(Temperature.getValueString(diff));
            return diff > Double.parseDouble(System.getProperty(CallbackMonitor.class.getSimpleName() + ".change.threshold", ".2"));
        } catch (Exception e) {
            return true;
        }
    }

    public void registerCallback() {
        this.start();
    }

}