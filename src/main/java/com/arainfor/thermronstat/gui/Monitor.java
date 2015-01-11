/**
 *
 */
package com.arainfor.thermronstat.gui;

import com.arainfor.util.file.io.Path;
import com.arainfor.util.file.io.ValueFileIO;
import com.arainfor.util.file.io.gpio.PiGPIO;
import com.arainfor.util.file.io.thermometer.DS18B20;
import org.apache.commons.cli.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author arainfor
 */
public class Monitor {

    // relays
    protected static PiGPIO stage1Relay;  // relay for Stage 1
    // value files
    protected static ValueFileIO statusControl;
    protected static ValueFileIO stage1Control;   // user control file for stage1Relay
    protected static ValueFileIO targetControl;
    // Thermometers
    protected static DS18B20 indoorSensor;
    protected static DS18B20 outdoorSensor;
    protected static DS18B20 plenumSensor;
    protected static DS18B20 returnSensor;

    private static String APPLICATION_NAME = "ThermRonStat";
    private static int APPLICATION_VERSION_MAJOR = 1;
    private static int APPLICATION_VERSION_MINOR = 0;
    private static int APPLICATION_VERSION_BUILD = 5;

    public Monitor() {

        JFrame guiFrame = new JFrame();

        //make sure the program exits when the frame closes
        guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        guiFrame.setTitle("ThermRonStat GUI");
        guiFrame.setSize(300, 250);

        //This will center the JFrame in the middle of the screen
        guiFrame.setLocationRelativeTo(null);

        //Options for the JComboBox
        String[] targetTempOptions = {
                "40", "41", "42", "43", "44", "45", "46", "47", "48", "49",
                "50", "51", "52", "53", "54", "55", "56", "57", "58", "59",
                "60", "61", "62", "63", "64", "65", "66", "67", "68", "69",
                "70", "71", "72", "73", "74", "75", "76", "77", "78", "79",
                "80", "81", "82", "83", "84", "85", "86", "87", "88", "89",
        };

        String[] statusOptions = {"Off", "On"};

        //The first JPanel contains a JLabel and JCombobox
        final JPanel mainPanel = new JPanel();

        JLabel statusL = new JLabel("System Status:");
        final JComboBox statusCB = new JComboBox(statusOptions);
        String systemStatusValue = "Off";
        try {
            if (statusControl.read()) {
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
                    if (((String) statusCB.getSelectedItem()).equalsIgnoreCase("on"))
                        statusControl.write(1.0);
                    else {
                        statusControl.write(0.0);
                        stage1Control.write(0.0);
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
            targetCB.setSelectedItem(new Integer((int) targetControl.readDouble()).toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        targetCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.out.println("Change Target Temp: " + targetCB.getSelectedItem());
                try {
                    targetControl.write(Double.parseDouble((String) targetCB.getSelectedItem()));
                } catch (NumberFormatException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        SensorLabel indoorTemp = new SensorLabel("Indoor Temp.", indoorSensor);
        SensorLabel outdoorTemp = new SensorLabel("Outdoor Temp.", outdoorSensor);
        SensorLabel plenumTemp = new SensorLabel("Plenum Temp.", plenumSensor);
        SensorLabel returnTemp = new SensorLabel("Return Temp.", returnSensor);
        ValueFileLabel relayValue = new ValueFileLabel("Running:", stage1Control, true);

        mainPanel.add(relayValue);
        mainPanel.add(statusL);
        mainPanel.add(statusCB);
        mainPanel.add(targetL);
        mainPanel.add(targetCB);
        mainPanel.add(indoorTemp);
        mainPanel.add(outdoorTemp);
        mainPanel.add(plenumTemp);
        mainPanel.add(returnTemp);

        guiFrame.add(mainPanel);

        //make sure the JFrame is visible
        guiFrame.setVisible(true);


    }

    /*
     * @param args The Program Arguments
     */
    public static void main(String[] args) throws IOException {
        //System.err.println("The " + APPLICATION_NAME +" v1" + APPLICATION_VERSION_MAJOR + "." + APPLICATION_VERSION_MINOR + "." + APPLICATION_VERSION_BUILD);
        Options options = new Options();
        options.addOption("help", false, "This message isn't very helpful");
        options.addOption("version", false, "Print the version number");
        options.addOption("mkdirs", false, "Create missing paths");
        options.addOption("monitor", false, "Start GUI Monitor");
        options.addOption("config", true, "The configuration file");

        CommandLineParser parser = new GnuParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption("help")) {
                HelpFormatter hf = new HelpFormatter();
                hf.printHelp(APPLICATION_NAME, options);
                return;
            }
            if (cmd.hasOption("version")) {
                System.out.println("The " + APPLICATION_NAME + " v" + APPLICATION_VERSION_MAJOR + "." + APPLICATION_VERSION_MINOR + "." + APPLICATION_VERSION_BUILD);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        String propFileName = "thermostat.properties";
        if (cmd.getOptionValue("config") != null)
            propFileName = cmd.getOptionValue("config");

        InputStream inputStream = new FileInputStream(propFileName);
        Properties props = new Properties();

        if (inputStream != null) {
            props.load(inputStream);
        } else {
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }

        // Append the system properties with our applicaton properties
        props.putAll(System.getProperties());
        System.setProperties(props);

        String IO_BASE_FS = System.getProperty("thermronstat.IO_BASE_FS", "/var/thermronstat");

        Path targetPath = new Path(IO_BASE_FS + "/target");
        Path relayPath = new Path(IO_BASE_FS + "/relay");
        Path statusPath = new Path(IO_BASE_FS + "/status");

        if (cmd.hasOption("mkdirs")) {
            targetPath.build();
            relayPath.build();
            statusPath.build();
        }

        targetControl = new ValueFileIO(targetPath.getAbsolutePath() + "/0");
        stage1Control = new ValueFileIO(relayPath.getAbsolutePath() + "/0");
        statusControl = new ValueFileIO(statusPath.getAbsolutePath() + "/0");

        String SYS_BUS_FS = System.getProperty("thermronstat.SYS_BUS_FS", "/sys/bus/w1/devices/");

        String indoorFilename = SYS_BUS_FS + System.getProperty("0.source") + "/w1_slave";
        String outdoorFilename = SYS_BUS_FS + System.getProperty("1.source") + "/w1_slave";
        String plenumFilename = SYS_BUS_FS + System.getProperty("2.source") + "/w1_slave";
        String returnFilename = SYS_BUS_FS + System.getProperty("3.source") + "/w1_slave";

        indoorSensor = new DS18B20(indoorFilename);
        outdoorSensor = new DS18B20(outdoorFilename);
        plenumSensor = new DS18B20(plenumFilename);
        returnSensor = new DS18B20(returnFilename);

        System.out.println("Target Temperature File: " + targetControl);
        System.out.println("Indoor Temperature Name: " + System.getProperty("0.name") + " File: " + indoorFilename);
        System.out.println("Outdoor Temperature Name: " + System.getProperty("1.name") + " File: " + outdoorFilename);
        System.out.println("Plenum Temperature Name: " + System.getProperty("2.name") + " File: " + plenumFilename);
        System.out.println("Return Temperature Name: " + System.getProperty("3.name") + " File: " + returnFilename);
        System.out.println("Relay Control File: " + stage1Control);  // Is the system currently running?
        System.out.println("System Available Control File: " + statusControl);  // User desired state of relay, on or off


        new Monitor();

        //final ValueFileIO statusVFIO, final ValueFileIO relayTempVFIO, ValueFileIO indoorTempVFIO, ValueFileIO outdoorTempVFIO, final ValueFileIO targetVFIO
    }
}