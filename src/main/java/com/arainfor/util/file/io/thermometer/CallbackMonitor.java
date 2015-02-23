package com.arainfor.util.file.io.thermometer;

import com.arainfor.thermronstat.StringConstants;
import com.arainfor.thermronstat.Temperature;
import com.arainfor.thermronstat.Thermometer;

import java.io.IOException;

/**
 * Created by arainfor on 2/22/15.
 */
public class CallbackMonitor extends Thread {

    private String lastTemperature;
    private ThermometerCallback thermometerCallback;
    private Thermometer thermometer;

    public CallbackMonitor(ThermometerCallback thermometerCallback, Thermometer thermometer) {
        super(thermometer.getName() + StringConstants.KeyValueDelimiter + thermometer.getDs18B20().getSerialId());
        this.thermometerCallback = thermometerCallback;
        this.thermometer = thermometer;
    }

    @Override
    public void run() {
        while (true) {
            double tempF = 0;
            try {
                tempF = thermometer.getDs18B20().getTempF();
                String currentTemperature = Temperature.getValueString(tempF);
                if (!currentTemperature.equalsIgnoreCase(lastTemperature)) {
                    thermometerCallback.subjectChanged(thermometer, Double.parseDouble(currentTemperature));
                }
                lastTemperature = currentTemperature;
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(Integer.parseInt(System.getProperty(DS18B20.class.getSimpleName() + ".sleep", "4750")));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void registerCallback() {
        this.start();
    }

}