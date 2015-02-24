package com.arainfor.util.file.io.gpio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by arainfor on 2/22/15.
 */
public class CallbackMonitor extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(CallbackMonitor.class);
    private SysFsGpio sysFsGpio;
    private SysFsGpioCallback sysFsGpioCallback;
    private boolean lastValue = false;
    private boolean currentValue = false;
    private boolean firstRun = true;

    public CallbackMonitor(SysFsGpioCallback sysFsGpioCallback, SysFsGpio sysFsGpio) {
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
