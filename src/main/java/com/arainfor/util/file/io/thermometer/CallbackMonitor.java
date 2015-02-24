package com.arainfor.util.file.io.thermometer;

import com.arainfor.thermronstat.Temperature;
import com.arainfor.thermronstat.Thermometer;
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