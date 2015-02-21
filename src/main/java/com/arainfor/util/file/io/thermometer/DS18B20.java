/**
 * 
 */
package com.arainfor.util.file.io.thermometer;

import com.arainfor.thermronstat.Temperature;
import com.arainfor.thermronstat.Thermometer;
import com.arainfor.thermronstat.ThermometersList;

import java.io.*;
import java.nio.charset.Charset;

/**
 * @author arainfor
 *
 */
public class DS18B20 extends Thread {

	// The 1wire DS18B20's are connected to GPIO4 pin.
    private final String SYS_BUS_FS = "/sys/bus/w1/devices/";

    private String filename = null;
    private String serialId = null;
    private ThermometerCallback thermometerCallback;
    private boolean valid;

    public DS18B20(String serialId) {
        this.serialId = serialId;
        this.filename = SYS_BUS_FS + serialId + "/w1_slave";
        valid = new File(this.filename).exists();
    }

    public String getFilename() {
        return filename;
    }

    public String getSerialId() {
        return serialId;
    }

    public Double getTempF() throws IOException {
		return CelToFar(readRaw());
	}
	
	public Double getTempC() throws IOException {
		return readRaw();
	}

    public void registerCallback(ThermometerCallback thermometerCallback) {
        this.thermometerCallback = thermometerCallback;
        if (this.thermometerCallback != null) {
            CallbackMonitor cm = new CallbackMonitor(this);
            cm.start();

        }
    }

	protected Double readRaw() throws IOException {
        if (isValid()) {
            InputStream fis;
			BufferedReader br;
			String line;

            fis = new FileInputStream(filename);
            br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));

			line = br.readLine();

			if (line.contains("YES")) {
				line = br.readLine();
			} else {
				line = null;
			}
			br.close();

			if (line != null) {
				int indexOfTemp = line.indexOf("=");
                String celsius = line.substring(indexOfTemp + 1);
                return Double.valueOf(celsius) / 1000;
            }
		}
		return Double.NEGATIVE_INFINITY;

	}

	protected double CelToFar (double cel) {
		return cel * 1.8000 + 32.00;
	}

    public boolean isValid() {
        return valid;
    }

    private class CallbackMonitor extends Thread {

        DS18B20 ds18B20;
        String lastTemperature;

        public CallbackMonitor(DS18B20 ds18B20) {
            super(CallbackMonitor.class.getSimpleName() + ds18B20.getSerialId());
            this.ds18B20 = ds18B20;
            try {
                lastTemperature = Temperature.getValueString(this.ds18B20.getTempF());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (true) {

                try {
                    double tempF = this.ds18B20.getTempF();
                    String currentTemperature = Temperature.getValueString(tempF);
                    if (!currentTemperature.equalsIgnoreCase(lastTemperature)) {
                        ThermometersList thermometersList = ThermometersList.getInstance();
                        for (Thermometer thermometer : thermometersList.list()) {
                            if (thermometer.getDs18B20().getFilename().equalsIgnoreCase(this.ds18B20.getFilename())) {
                                thermometerCallback.subjectChanged(thermometer, Double.parseDouble(currentTemperature));
                            }
                        }
                    }
                    lastTemperature = currentTemperature.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(4098);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
