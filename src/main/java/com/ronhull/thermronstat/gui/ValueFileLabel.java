package com.ronhull.thermronstat.gui;

import com.arainfor.util.file.io.ValueFileIO;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ValueFileLabel extends JPanel implements ActionListener {

    private final Timer timer = new Timer(1000, this);
    private final JLabel descriptionLabel = new JLabel();
    private final JLabel valueLabel = new JLabel();
    protected ValueFileIO _vfioMonitor;

    public ValueFileLabel(String description, ValueFileIO vfioMonitor) {
        super(true);

        _vfioMonitor = vfioMonitor;

        descriptionLabel.setText(description);
        add(descriptionLabel);
        add(valueLabel);

        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {

        try {
            valueLabel.setText(_vfioMonitor.readDouble() + "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
