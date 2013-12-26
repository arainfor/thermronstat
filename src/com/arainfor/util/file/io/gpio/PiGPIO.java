/**
 * 
 */
package com.arainfor.util.file.io.gpio;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author atael
 */
public class PiGPIO {

    public static final String GPIO_ON = "1";
    public static final String GPIO_OFF = "0";

    protected FileWriter commandFile;
    
    public PiGPIO (Pin pin, Direction direction) throws IOException {

    	/*** Init GPIO port for output ***/

    	// Reset the port
    	File exportFileCheck = new File("/sys/class/gpio/gpio" + pin);
    	if (exportFileCheck.exists()) {
    		unExport(pin);
    	}            

    	// Set the port for use
    	export(pin);

    	setDirection(pin, direction);

    	// Open file handle to issue commands to GPIO port
    	commandFile = new FileWriter("/sys/class/gpio/gpio" + pin + "/value");
    }

    public void unExport(Pin pin) throws IOException {
        FileWriter unexportFile = new FileWriter("/sys/class/gpio/unexport");

    	unexportFile.write(pin.getName());
    	unexportFile.flush();
    	unexportFile.close();
    }

    public void export(Pin pin) throws IOException {
        FileWriter exportFile = new FileWriter("/sys/class/gpio/export");

        exportFile.write(pin.getName());   
        exportFile.flush();
        exportFile.close();
    }

    public void setDirection(Pin pin, Direction direction) throws IOException {
        // Open file handle to port input/output control
        FileWriter directionFile = new FileWriter("/sys/class/gpio/gpio" + pin.getName() + "/direction");
        
        // Set port for output
        directionFile.write(direction.get());
        directionFile.flush();
        directionFile.close();
    }
    
    public void setValue(Boolean value) throws IOException {
    	if (value) {
    		commandFile.write(GPIO_ON);
    	} else {
    		commandFile.write(GPIO_OFF);
    	}
    	commandFile.flush();
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

}