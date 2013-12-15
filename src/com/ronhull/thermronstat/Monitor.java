/**
 * 
 */
package com.ronhull.thermronstat;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;

import com.arainfor.util.file.io.ValueFileIO;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;

/**
 * @author arainfor
 *
 */
public class Monitor {
	
	public Monitor(final ValueFileIO statusVFIO, final ValueFileIO relayTempVFIO,  ValueFileIO indoorTempVFIO, ValueFileIO outdoorTempVFIO, final ValueFileIO targetVFIO) {

		JFrame guiFrame = new JFrame();

		//make sure the program exits when the frame closes
		guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		guiFrame.setTitle("ThermRonStat GUI");
		guiFrame.setSize(300, 250);

		//This will center the JFrame in the middle of the screen
		guiFrame.setLocationRelativeTo(null);

		//Options for the JComboBox 
		String[] targetTempOptions = { "50", "51", "52", "53", "54", "55", "56", "57", "58", "59",
				"60", "61", "62", "63", "64", "65", "66", "67", "68", "69",
				"70", "71", "72", "73", "74", "75", "76", "77", "78", "79",
				"80", "81", "82", "83", "84", "85", "86", "87", "88", "89",
				};

		String[] statusOptions = { "Off", "On" };
		
		//The first JPanel contains a JLabel and JCombobox
		final JPanel mainPanel = new JPanel();

		JLabel statusL = new JLabel("System Status:");
		final JComboBox statusCB = new JComboBox(statusOptions);
		String systemStatusValue = "Off";
		try {
			if (statusVFIO.read() != 0.0) {
				systemStatusValue = "On";
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		statusCB.setSelectedItem(systemStatusValue);
		statusCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
            	System.out.println("Change System Status due to event:" + statusCB.getSelectedItem());
            	try {
            		if (((String)statusCB.getSelectedItem()).equalsIgnoreCase("on"))
            			statusVFIO.write(1.0);
            		else {
            			statusVFIO.write(0.0);
            			relayTempVFIO.write(0.0);
            		}
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
		
		JLabel targetL = new JLabel("Target Temperature:");
		final JComboBox targetCB = new JComboBox(targetTempOptions);
		try {
			targetCB.setSelectedItem(new Integer((int)targetVFIO.read()).toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		targetCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
            	System.out.println("Change Target Temp: " + targetCB.getSelectedItem());
            	try {
					targetVFIO.write(Double.parseDouble((String)targetCB.getSelectedItem()));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
		
		ValueFileLabel indoorTemp = new ValueFileLabel("Indoor Temp.", indoorTempVFIO);
		ValueFileLabel outdoorTemp = new ValueFileLabel("Outdoor Temp.", outdoorTempVFIO);
		ValueFileLabel relayValue = new ValueFileLabel("Running.", relayTempVFIO);
		
		mainPanel.add(relayValue);
		mainPanel.add(statusL);
		mainPanel.add(statusCB);
		mainPanel.add(targetL);
		mainPanel.add(targetCB);
		mainPanel.add(indoorTemp);
		mainPanel.add(outdoorTemp);
		
		guiFrame.add(mainPanel);

		//make sure the JFrame is visible
		guiFrame.setVisible(true);

		
	}
}
