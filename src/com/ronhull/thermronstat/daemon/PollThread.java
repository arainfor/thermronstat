/**
 * 
 */
package com.ronhull.thermronstat.daemon;

import java.io.IOException;

import org.slf4j.Logger;

import com.arainfor.util.file.io.PiGPIO;
import com.arainfor.util.file.io.ValueFileIO;
import com.arainfor.util.logger.AppLogger;
import com.ronhull.thermronstat.TemperatureControl;

/**
 * @author arainfor
 *
 */
public class PollThread {
	
	protected Logger _logger;
	protected ValueFileIO _relayVFIO;
	protected ValueFileIO _statusVFIO;
	
	public PollThread(final PiGPIO piGPIO, ValueFileIO statusVFIO, ValueFileIO relayVFIO, ValueFileIO indoorVFIO, ValueFileIO outdoorVFIO, ValueFileIO targetVFIO) {
		
		_logger = new AppLogger().getLogger(this.getClass().getName());
		_logger.info(this.getClass().getName() + " starting...");

		_relayVFIO = relayVFIO;  // We keep a copy of the relay file object for emergency shutdown!
		_statusVFIO = statusVFIO;
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
		    public void run() {
		    	_logger.info("Turning OFF HVAC...");
		    	try {
		    		piGPIO.set(false);
		    		//_relayVFIO.write(0);
					_statusVFIO.write(0);
				} catch (IOException e) {
					e.printStackTrace();
				}  // Try to turn off the HVAC if we are terminated!!
		    	_logger.info(this.getClass().getName() + " terminated...");		    }
		}));

		int lastSystemStatus = -1;

		while (true) {

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				_logger.error(e.toString());
				continue;
			}
			
			int relayPosistion = 0;
			int systemStatus = 0;
			try {
				systemStatus = (int)statusVFIO.read();
				relayPosistion = (int)relayVFIO.read();
				
				// Display a message if we toggled systemStatus.
				if (systemStatus != lastSystemStatus) {
					if (systemStatus == 0)
						_logger.info("Turning System OFF");
					else
						_logger.info("Turning System ON");
					lastSystemStatus = systemStatus;
				}

				if (systemStatus == 0) {
					if (relayPosistion != 0) { // turn off the relay if user wants it off!
						piGPIO.set(false);
						//relayVFIO.write(0);
					}
					continue;
				}
				
			} catch (IOException e2) {
				_logger.error("Error reading file: " + e2);
				e2.printStackTrace();
				continue;
			}
			
			// read all the known values
			double controlTemp, ambientTemp, targetTemp = 0;
			try {
				targetTemp = targetVFIO.read();
				controlTemp = indoorVFIO.read();
				ambientTemp = outdoorVFIO.read();
//				_logger.debug("***************");
//				_logger.debug("target_temp=" + target);
//				_logger.debug("indoor_temp=" + control);
//				_logger.debug("outdoor_temp=" + ambient);
			} catch (IOException ioe) {
				_logger.error(ioe.toString());
				ioe.printStackTrace();
				continue;
			}
			
			// the real decision is here!
			TemperatureControl controller = new TemperatureControl(targetTemp, controlTemp, ambientTemp, 0.5, relayPosistion > 0);
			int relayValue = controller.enable();
			
			try {
				if (relayPosistion != relayValue) {
					//relayVFIO.write(relayValue);
					if (relayValue > 0) 
						piGPIO.set(true);
					else
						piGPIO.set(false);
					_logger.debug("***************");
					_logger.debug("heat mode? " + controller.isHeat());
					_logger.debug("target_temp=" + targetTemp);
					_logger.debug("indoor_temp=" + controlTemp);
					_logger.debug("outdoor_temp=" + ambientTemp);
					_logger.info("Relay changed to " + relayValue);
				}
			} catch (IOException e) {
				_logger.error(e.toString());
				e.printStackTrace();
				continue;
			}
		}
		
	}
	
}
