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
 */
package com.arainfor.util.file.io.gpio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * This class monitors GPIO changes.
 *
 * Created by arainfor on 2/22/15.
 */
public class GpioProcessor extends Thread {

  private static final Logger logger = LoggerFactory.getLogger(GpioProcessor.class);
  private SysFsGpio sysFsGpio;
  private SysFsGpioCallback sysFsGpioCallback;
  private boolean lastValue = false;
  private boolean currentValue = false;
  private boolean firstRun = true;

  public GpioProcessor(SysFsGpioCallback sysFsGpioCallback, SysFsGpio sysFsGpio) {
    super(sysFsGpio.toString());
    this.sysFsGpioCallback = sysFsGpioCallback;
    this.sysFsGpio = sysFsGpio;
  }

  @Override
  public void run() {
    while (true) {

      try {
        currentValue = sysFsGpio.getValue();
      } catch (IOException e) {
        logger.error("Exception reading gpio:{}", sysFsGpio.toString(), e);
      }

      if (firstRun || currentValue != lastValue) {
        logger.debug("GPIO:{} changed from: {} to:{}", sysFsGpio, lastValue, currentValue);
        lastValue = currentValue;
        firstRun = false;
        sysFsGpioCallback.subjectChanged(sysFsGpio, currentValue);
      }

      try {
        Thread.sleep(Integer.parseInt(System.getProperty(SysFsGpio.class.getSimpleName() + ".sleep", "1100")));
      } catch (InterruptedException e) {
        logger.warn("Thread interrupted:", e);
      }
    }
  }

  public void registerCallback() {
    this.start();
  }

}
