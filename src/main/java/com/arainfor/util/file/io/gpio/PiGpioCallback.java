package com.arainfor.util.file.io.gpio;

/**
 * Created by arainfor on 2/18/15.
 */
public interface PiGpioCallback {
    void subjectChanged(PiGpio piGpio, boolean value);
}
