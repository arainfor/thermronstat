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
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * @author arainfor
 *
 */
public class Monitor {
	
	public Monitor() {


		JFrame guiFrame = new JFrame();

		//make sure the program exits when the frame closes
		guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		guiFrame.setTitle("Example GUI");
		guiFrame.setSize(300, 250);

		//This will center the JFrame in the middle of the screen
		guiFrame.setLocationRelativeTo(null);

		//Options for the JComboBox 
		String[] targetTempOptions = { "50", "51", "52", "53", "54", "55", "56", "57", "58", "59",
				"60", "61", "62", "63", "64", "65", "66", "67", "68", "69",
				"70", "71", "72", "73", "74", "75", "76", "77", "78", "79",
				"80", "81", "82", "83", "84", "85", "86", "87", "88", "89",
				};

		//The first JPanel contains a JLabel and JCombobox
		final JPanel mainPanel = new JPanel();
		JLabel targetL = new JLabel("Target Temperature:");
		JComboBox targetCB = new JComboBox(targetTempOptions);

		JLabel currentL = new JLabel("Current Temperature:");
		JLabel currentT = new JLabel("?");
		
		
		mainPanel.add(targetL);
		mainPanel.add(targetCB);
		mainPanel.add(currentL);
		mainPanel.add(currentT);

		//The JFrame uses the BorderLayout layout manager.
		//Put the two JPanels and JButton in different areas.
		guiFrame.add(mainPanel, BorderLayout.NORTH);

		//make sure the JFrame is visible
		guiFrame.setVisible(true);

	}
}
