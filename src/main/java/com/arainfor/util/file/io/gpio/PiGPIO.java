/**
 * 
 */
package com.arainfor.util.file.io.gpio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;


/**
 *
 * @author atael
 */
public class PiGPIO {

    public static final String GPIO_ON = "1";
    public static final String GPIO_OFF = "0";
    protected static final String IO_BASE_FS = System.getProperty("PiGPIO.IO_BASE_FS", "/sys/class/gpio/");
    private static final Logger logger = LoggerFactory.getLogger(PiGPIO.class);
    static boolean foreignHardware = false;
    protected FileWriter commandFile;
    private Pin pin;
    private Direction direction;

    public PiGPIO (Pin pin, Direction direction) throws IOException {

    	/*** Init GPIO port for output ***/

        this.pin = pin;

        foreignHardware = !new File(IO_BASE_FS).exists();

        if (foreignHardware)
            return;

    	// Reset the port
        File exportFileCheck = new File(IO_BASE_FS + "gpio" + pin);
        if (exportFileCheck.exists()) {
    		unExport(pin);
    	}            

    	// Set the port for use
    	export(pin);

    	setDirection(pin, direction);

    	// Open file handle to issue commands to GPIO port
        commandFile = new FileWriter(IO_BASE_FS + "gpio" + pin + "/value");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {
            PiGPIO pigpio = new PiGPIO(new Pin(17), Direction.OUT);
            pigpio.setValue(true);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void unExport(Pin pin) throws IOException {

        if (foreignHardware)
            return;

        FileWriter unexportFile = new FileWriter(IO_BASE_FS + "unexport");

    	unexportFile.write(pin.getName());
    	unexportFile.flush();
    	unexportFile.close();
    }

    public void export(Pin pin) throws IOException {

        if (foreignHardware)
            return;

        String exportFileName = IO_BASE_FS + "export";
        FileWriter exportFile = new FileWriter(exportFileName);

        try {
            exportFile.write(pin.getName());
            exportFile.flush();
            exportFile.close();
        } catch (IOException e) {
            logger.error("Cannot export {} at {}", pin, exportFileName, e);
            throw e;
        }
    }

    public void setDirection(Pin pin, Direction direction) throws IOException {

        this.direction = direction;

        if (foreignHardware)
            return;

        // Open file handle to port input/output control
        FileWriter directionFile = new FileWriter(IO_BASE_FS + "gpio" + pin.getName() + "/direction");

        // Set port for output
        directionFile.write(direction.get());
        directionFile.flush();
        directionFile.close();
    }

    public boolean getValue() throws IOException {
        if (foreignHardware)
            return false;

        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(IO_BASE_FS + "gpio" + pin + "/value"));
            try {
                String line = br.readLine();

                if (line != null) {
                    sb.append(line);
                }

            } finally {
                br.close();
            }
        } catch (Exception e) {
            throw new IOException("getValue reader error:" + e);
        }

        return Integer.parseInt(sb.toString()) != 0;
    }

    public void setValue(Boolean value) throws IOException {

        if (foreignHardware)
            return;

        if (value) {
            commandFile.write(GPIO_ON);
    	} else {
    		commandFile.write(GPIO_OFF);
    	}
    	commandFile.flush();
    }

    public Pin getPin() {
        return pin;
    }

    public Direction getDirection() {
        return direction;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("GPIO Pin: " + getPin() + " Direction: " + getDirection());
        return sb.toString();
    }

}