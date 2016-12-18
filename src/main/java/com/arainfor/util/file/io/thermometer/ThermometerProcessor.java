/**
 * Copyright 2014-2015
 * Alan Rainford arainfor@gmail.com
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * <p>
 * 24-mar-2015 akr Per Thermometer change threshold.
 */
package com.arainfor.util.file.io.thermometer;

import com.arainfor.thermostat.Temperature;
import com.arainfor.thermostat.Thermometer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * This class is provides access to the DS18x20 series of temperature sensors.
 * The DS18x20 is a 1-wire (Dallas) protocol sensor.
 *
 * With this class you are able to register a callback and be notified when the
 * temperature crosses the boundary value.
 *
 * @author arainfor
 *
 */
public class ThermometerProcessor extends Thread {

  private static final Logger logger = LoggerFactory.getLogger(ThermometerProcessor.class);
  private String lastTemperature;
  private ThermometerCallback thermometerCallback;
  private Thermometer thermometer;
  private Double changeThreshold;

  public ThermometerProcessor(ThermometerCallback thermometerCallback, Thermometer thermometer) {
    super(thermometer.getName());
    this.thermometerCallback = thermometerCallback;
    this.thermometer = thermometer;
    this.changeThreshold = Double.parseDouble(System.getProperty(thermometer.getName() + ".change.threshold", ".2"));
    logger.debug("Instance created for: {}", this.toString());
  }

  public static void main(String[] args) throws IOException {
    ThermometerProcessor cm = new ThermometerProcessor(null, new Thermometer(0, "Test", null));
    System.err.println(cm.crossedThreshold("80.0", "80.2"));
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("thermometer = ");
    sb.append(thermometer.getName());
    sb.append(" changeThreshold = ");
    sb.append(changeThreshold);
    return sb.toString();
  }

  @Override
  public void run() {
    while (true) {

      try {
        double tempF = thermometer.getDs18B20().getTempF();
        String currentTemperature = Temperature.getValueString(tempF);

        if (crossedThreshold(lastTemperature, currentTemperature)) {
          logger.debug("new temperature:{}", currentTemperature);
          thermometerCallback.subjectChanged(thermometer, Double.parseDouble(currentTemperature));
          lastTemperature = currentTemperature;
        }

      } catch (Exception e) {
        logger.warn("Thread exception:", e);
      }

      try {
        Thread.sleep(Integer.parseInt(System.getProperty(DS18B20.class.getSimpleName() + ".sleep", "4750")));
      } catch (InterruptedException e) {
        logger.warn("Thread interrupted:", e);
      }

    }
  }

  private boolean crossedThreshold(String lastTemperature, String currentTemperature) {
    try {
      double diff = Math.abs(Double.parseDouble(lastTemperature) - Double.parseDouble(currentTemperature));
      diff = Double.parseDouble(Temperature.getValueString(diff));
      return diff > changeThreshold;
    } catch (Exception e) {
      return true;
    }
  }

  public void registerCallback() {
    this.start();
  }

}