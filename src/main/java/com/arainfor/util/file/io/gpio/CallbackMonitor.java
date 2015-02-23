package com.arainfor.util.file.io.gpio;

import com.arainfor.thermronstat.RelayMap;
import com.arainfor.thermronstat.StringConstants;

import java.io.IOException;

/**
 * Created by arainfor on 2/22/15.
 */
public class CallbackMonitor extends Thread {
    private SysFsGpioCallback sysFsGpioCallback;
    private RelayMap relayMap;
    private boolean lastValue = false;
    private boolean currentValue = false;
    private boolean firstRun = true;

    public CallbackMonitor(SysFsGpioCallback sysFsGpioCallback, RelayMap relayMap) {
        super(relayMap.getRelayDef().getName() + StringConstants.KeyValueDelimiter
                + relayMap.getSysFsGpio().getPin() + relayMap.getSysFsGpio().getDirection());
        this.sysFsGpioCallback = sysFsGpioCallback;
        this.relayMap = relayMap;
    }

    @Override
    public void run() {
        while (true) {

            try {
                currentValue = relayMap.getSysFsGpio().getValue();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (firstRun || currentValue != lastValue) {
                //logger.debug("GPIO pin:{} changed to:{}", getPin(), currentValue);
                lastValue = currentValue;
                firstRun = false;
                sysFsGpioCallback.subjectChanged(relayMap.getSysFsGpio(), currentValue);
            }

            try {
                Thread.sleep(Integer.parseInt(System.getProperty(SysFsGpio.class.getSimpleName() + ".sleep", "1100")));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void registerCallback() {
        this.start();
    }

}
