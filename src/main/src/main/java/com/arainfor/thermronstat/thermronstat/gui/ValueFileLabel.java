package com.arainfor.thermronstat.thermronstat.gui;

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
    protected boolean showBool;

    public ValueFileLabel(String description, ValueFileIO vfioMonitor, boolean bool) {
        super(true);

        _vfioMonitor = vfioMonitor;
        showBool = bool;
        descriptionLabel.setText(description);
        add(descriptionLabel);
        add(valueLabel);

        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {

        try {
            if (showBool)
                valueLabel.setText(new Boolean(_vfioMonitor.read()).toString());
            else
                valueLabel.setText(_vfioMonitor.readDouble() + "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
