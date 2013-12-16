package com.ronhull.thermronstat;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.arainfor.util.file.io.ValueFileIO;

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
			valueLabel.setText(_vfioMonitor.read() + "");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}
