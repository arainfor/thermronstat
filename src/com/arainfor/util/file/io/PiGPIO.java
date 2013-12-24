/**
 * 
 */
package com.arainfor.util.file.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author atael
 */
public class PiGPIO {

    static final String GPIO_OUT = "out";
    static final String GPIO_ON = "1";
    static final String GPIO_OFF = "0";

    protected FileWriter commandFile;
    
    public PiGPIO (String pin) {
        try {
            
            /*** Init GPIO port for output ***/

            // Reset the port
            File exportFileCheck = new File("/sys/class/gpio/gpio"+pin);
            if (exportFileCheck.exists()) {
                unExport(pin);
            }            
            
            // Set the port for use
            export(pin);

            setDirection(pin, GPIO_OUT);
            
            // Open file handle to issue commands to GPIO port
            commandFile = new FileWriter("/sys/class/gpio/gpio" + pin + "/value");
            

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void unExport(String pin) throws IOException {
        FileWriter unexportFile = new FileWriter("/sys/class/gpio/unexport");

    	unexportFile.write(pin);
    	unexportFile.flush();
    	unexportFile.close();
    }

    public void export(String pin) throws IOException {
        FileWriter exportFile = new FileWriter("/sys/class/gpio/export");

        exportFile.write(pin);   
        exportFile.flush();
        exportFile.close();
    }

    public void setDirection(String pin, String direction) throws IOException {
        // Open file handle to port input/output control
        FileWriter directionFile = new FileWriter("/sys/class/gpio/gpio"+pin+"/direction");
        
        // Set port for output
        directionFile.write(GPIO_OUT);
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
    	
    	String gpioChannel = "17";
    	PiGPIO pigpio = new PiGPIO(gpioChannel);
    	try {
			pigpio.setValue(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    }

}