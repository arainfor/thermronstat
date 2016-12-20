/**
 * Copyright 2014-2015
 * Alan Rainford arainfor@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.arainfor.util.file.io.gpio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * This class provides a POJ interface to the SysFs User Space Kernel driver to GPIO
 * @author arainfor
 */
public class SysFsGpio {

    protected static final String IO_BASE_FS = System.getProperty("GPIO.IO_BASE_FS", "/sys/class/gpio/");
    private static final Logger logger = LoggerFactory.getLogger(SysFsGpio.class);
    private static boolean foreignHardware = false;
    private final Pin pin;
    private FileWriter commandFile;
    private Direction direction;
    private String valueFileName;
    private String directionFileName;

    public SysFsGpio(Pin pin, Direction direction, boolean reConfigure) throws IOException {

        this.pin = pin;
        valueFileName = IO_BASE_FS + "gpio" + pin + "/value";
        directionFileName = IO_BASE_FS + "gpio" + pin.getName() + "/direction";

        foreignHardware = !new File(IO_BASE_FS).exists();

        if (foreignHardware)
            return;

        // Reset the port if its not setup how we expect it!
        if (reConfigure || !new File(directionFileName).exists()) {
            configure(direction);
        }

        if (direction.ordinal() == Direction.OUT.ordinal()) {
            // Open file handle to issue commands to GPIO port
            commandFile = new FileWriter(valueFileName);
        }
    }

    public SysFsGpio(Pin pin, Direction direction) throws IOException {
        this(pin, direction, false);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        SysFsGpio pigpio = new SysFsGpio(new Pin(17), Direction.OUT);
        pigpio.setValue(true);

    }

    private void configure(Direction direction) throws IOException {
        File configuredFile = new File(IO_BASE_FS + "gpio" + pin);
        if (configuredFile.exists()) {
            unExport();
        }

        // Set the port for use
        export();

        setDirection(direction);
    }

    private void unExport() throws IOException {

        if (foreignHardware)
            return;

        FileWriter unexportFile = new FileWriter(IO_BASE_FS + "unexport");

    	unexportFile.write(getPin().getName());
    	unexportFile.flush();
    	unexportFile.close();
    }

    private void export() throws IOException {

        if (foreignHardware)
            return;

        String exportFileName = IO_BASE_FS + "export";
        FileWriter exportFile = new FileWriter(exportFileName);

        try {
            exportFile.write(getPin().getName());
            exportFile.flush();
            exportFile.close();
        } catch (IOException e) {
            logger.error("Cannot export {} at {}", pin, exportFileName, e);
            throw e;
        }
    }

    public boolean getValue() throws IOException {
        if (foreignHardware)
            return false;

        String line = "0";
        try {
            BufferedReader br = new BufferedReader(new FileReader(valueFileName));
            try {
                line = br.readLine();
            } finally {
                br.close();
            }
        } catch (Exception e) {
            throw new IOException("getValue reader error:" + e);
        }

        return Integer.parseInt(line) != 0;
    }

    public void setValue(Boolean value) throws IOException {
        if (foreignHardware)
            return;

        if (value) {
            commandFile.write(Value.SET.value());
    	} else {
    		commandFile.write(Value.UNSET.value());
    	}
    	commandFile.flush();
    }

    public Pin getPin() {
        return pin;
    }

    public Direction getDirection() {
        if (foreignHardware)
            return Direction.OUT;

        try {
            BufferedReader br = new BufferedReader(new FileReader(directionFileName));
            try {
                String line = br.readLine();

                if (direction == null || !direction.toString().equalsIgnoreCase(line)) {
                    if (line.equalsIgnoreCase(Direction.IN.toString())) {
                        direction = Direction.IN;
                    } else {
                        direction = Direction.OUT;
                    }
                }

            } finally {
                br.close();
            }
        } catch (Exception e) {
            return direction;
        }

        return direction;
    }

    private void setDirection(Direction direction) throws IOException {

        this.direction = direction;

        if (foreignHardware)
            return;

        // Open file handle to port input/output control
        FileWriter directionFile = new FileWriter(directionFileName);

        // Set port for output
        directionFile.write(direction.value());
        directionFile.flush();
        directionFile.close();
    }

    public String toString() {
        return "GPIO Pin: " + getPin() + " Direction: " + getDirection();
    }

    public void cleanup(Pin pin) throws IOException {
        unExport();
    }

}