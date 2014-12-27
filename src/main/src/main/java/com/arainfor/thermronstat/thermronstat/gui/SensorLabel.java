package com.arainfor.thermronstat.thermronstat.gui;

import com.arainfor.util.file.io.thermometer.DS18B20;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class SensorLabel extends JPanel implements ActionListener {

    private final Timer timer = new Timer(1000, this);
    private final JLabel descriptionLabel = new JLabel();
    private final JLabel valueLabel = new JLabel();
    protected DS18B20 _sensor;

    public SensorLabel(String description, DS18B20 sensor) {
        super(true);

        _sensor = sensor;

        descriptionLabel.setText(description);
        add(descriptionLabel);
        add(valueLabel);

        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {

        try {
            valueLabel.setText(_sensor.getTempF() + "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
