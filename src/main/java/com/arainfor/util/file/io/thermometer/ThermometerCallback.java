package com.arainfor.util.file.io.thermometer;

import com.arainfor.thermostat.Thermometer;

/**
 * Created by arainfor on 2/18/15.
 */
public interface ThermometerCallback {
    void subjectChanged(Thermometer thermometer, double value);
}
