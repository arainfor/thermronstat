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
public class PiGpio {

    public static final String GPIO_ON = "1";
    public static final String GPIO_OFF = "0";
    protected static final String IO_BASE_FS = System.getProperty("PiGPIO.IO_BASE_FS", "/sys/class/gpio/");
    private static final Logger logger = LoggerFactory.getLogger(PiGpio.class);
    private static boolean foreignHardware = false;
    private final Pin pin;
    private PiGpioCallback piGpioCallback;
    private FileWriter commandFile;
    private Direction direction;
    private String valueFileName;

    public PiGpio(Pin pin, Direction direction) throws IOException {

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

        valueFileName = IO_BASE_FS + "gpio" + pin + "/value";

        if (direction.ordinal() == Direction.OUT.ordinal()) {
            // Open file handle to issue commands to GPIO port
            commandFile = new FileWriter(valueFileName);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {
            PiGpio pigpio = new PiGpio(new Pin(17), Direction.OUT);
            pigpio.setValue(true);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void registerCallback(PiGpioCallback piGpioCallback) {

        this.piGpioCallback = piGpioCallback;

        if (this.piGpioCallback != null) {
            CallbackMonitor cm = new CallbackMonitor(this);
            cm.start();
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
            BufferedReader br = new BufferedReader(new FileReader(valueFileName));
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
        return "GPIO Pin: " + getPin() + " Direction: " + getDirection();
    }

    public void cleanup(Pin pin) throws IOException {
        unExport(pin);
    }

    private class CallbackMonitor extends Thread {
        long lastModified;
        PiGpio piGpio;
        private boolean lastValue = true;
        private boolean currentValue = false;

        public CallbackMonitor(PiGpio piGpio) {
            super(CallbackMonitor.class.getSimpleName() + piGpio.getPin() + piGpio.getDirection());
            this.piGpio = piGpio;
            lastModified = new File(valueFileName).lastModified();
        }

        @Override
        public void run() {
            while (true) {
                long modified = new File(valueFileName).lastModified();

                try {
                    currentValue = getValue();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (currentValue != lastValue) {
                    //logger.debug("GPIO pin:{} changed to:{}", getPin(), currentValue);
                    lastModified = modified;
                    lastValue = currentValue;
                    piGpioCallback.subjectChanged(this.piGpio, currentValue);
                }

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}