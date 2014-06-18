/**
 * 
 */
package com.ronhull.thermronstat.daemon;

import java.io.IOException;

import org.slf4j.Logger;

import com.arainfor.util.file.io.ValueFileIO;
import com.arainfor.util.file.io.gpio.PiGPIO;
import com.arainfor.util.file.io.thermometer.DS18B20;
import com.arainfor.util.logger.AppLogger;
import com.ronhull.thermronstat.TemperatureControl;

/**
 * @author arainfor
 *
 */
public class PollThread extends Thread {
	
	protected Logger _logger;
	protected PiGPIO _heatPiGPIO;
	protected ValueFileIO _statusVFIO, _relayVFIO, _targetVFIO; 
	protected DS18B20 _indoorSensor, _outdoorSensor;
	protected int _sleep;
	
	public PollThread(int sleep, final PiGPIO heatPiGPIO, ValueFileIO statusVFIO, ValueFileIO relayVFIO, DS18B20 indoorSensor, DS18B20 outdoorSensor, ValueFileIO targetVFIO) {
		
		super();
		
		_sleep = sleep;
		_heatPiGPIO = heatPiGPIO;
		_statusVFIO = statusVFIO;
		_relayVFIO = relayVFIO;  
		_indoorSensor = indoorSensor;
		_outdoorSensor = outdoorSensor;
		_targetVFIO = targetVFIO;
		
		_logger = new AppLogger().getLogger(this.getClass().getName());
		_logger.info(this.getClass().getName() + " starting...");
		
		// Add hook to turn off everything...
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
		    public void run() {
		    	_logger.info("Turning OFF HVAC...");
		    	try {
		    		heatPiGPIO.setValue(false);
		    		//_relayVFIO.write(0);
					_statusVFIO.write(0);
				} catch (IOException e) {
					e.printStackTrace();
				}  // Try to turn off the HVAC if we are terminated!!
		    	_logger.info(this.getClass().getName() + " terminated...");		    }
		}));

	}
	
	@Override
	public void run() {
		int lastSystemStatus = -1;

		while (true) {

			try {
				Thread.sleep(_sleep);
			} catch (InterruptedException e) {
				_logger.error(e.toString());
				continue;
			}
			
			int relayPosistion = 0;
			int systemStatus = 0;
			try {
				systemStatus = (int)_statusVFIO.readDouble();
				relayPosistion = (int)_relayVFIO.readDouble();
				
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
						_heatPiGPIO.setValue(false);
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
				targetTemp = _targetVFIO.readDouble();
				controlTemp = _indoorSensor.getTempF();
				ambientTemp = _outdoorSensor.getTempF();
			} catch (IOException ioe) {
				_logger.error("Temperature Read error!: " + ioe.toString());
				ioe.printStackTrace();
				continue;
			}
			
			// the real decision is here!
			TemperatureControl controller = new TemperatureControl(targetTemp, controlTemp, ambientTemp, 0.5, relayPosistion > 0);
			int relayValue = controller.enable();
			
			try {
				if (relayPosistion != relayValue) {
					if (relayValue > 0) 
						_heatPiGPIO.setValue(true);
					else
						_heatPiGPIO.setValue(false);
					_logger.debug("***************");
					_logger.debug("heat mode? " + controller.isHeat());
					_logger.debug("target_temp=" + targetTemp);
					_logger.debug("indoor_temp=" + controlTemp);
					_logger.debug("outdoor_temp=" + ambientTemp);
					_logger.info("Relay changed to " + relayValue);
				}
			} catch (IOException e) {
				_logger.error("Relay Control Error: " + e.toString());
				e.printStackTrace();
				continue;
			}
		}
	}
}
