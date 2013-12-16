/**
 * 
 */
package com.ronhull.thermronstat;

import java.io.IOException;

import org.slf4j.Logger;

import com.arainfor.util.file.io.ValueFileIO;
import com.arainfor.util.logger.AppLogger;

/**
 * @author arainfor
 *
 */
public class PollThread {
	
	protected Logger _logger;
	protected ValueFileIO _relayVFIO;
	protected ValueFileIO _statusVFIO;
	
	public PollThread(ValueFileIO statusVFIO, ValueFileIO relayVFIO, ValueFileIO indoorVFIO, ValueFileIO outdoorVFIO, ValueFileIO targetVFIO) {
		
		_logger = new AppLogger().getLogger(this.getClass().getName());
		_logger.info(this.getClass().getName() + " starting...");

		_relayVFIO = relayVFIO;  // We keep a copy of the relay file object for emergency shutdown!
		_statusVFIO = statusVFIO;
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
		    public void run() {
		    	_logger.info("Turning of HVAC...");
		    	try {
		    		_relayVFIO.write(0);
					_statusVFIO.write(0);
				} catch (IOException e) {
					e.printStackTrace();
				}  // Try to turn off the HVAC if we are terminated!!
		    	_logger.info(this.getClass().getName() + " terminated...");		    }
		}));
		
		while (true) {

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				_logger.error(e.toString());
				continue;
			}
			
			int relayPosistion = 0;
			int systemStatus = 0;
			int lastSystemStatus = -1;
			try {
				systemStatus = (int)statusVFIO.read();
				relayPosistion = (int)relayVFIO.read();
				
				// Display a message if we toggled systemStatus.
				if (systemStatus != lastSystemStatus) {
					if (systemStatus == 0)
						_logger.info("Turning System off");
					else
						_logger.info("Turning System on");
					lastSystemStatus = systemStatus;
				}

				if (systemStatus == 0) {
					if (relayPosistion != 0)  // turn off the relay if user wants it off!
						relayVFIO.write(0);
					continue;
				}
				
			} catch (IOException e2) {
				_logger.warn("Error reading file: " + e2);
				continue;
			}
			
			// read all the known values
			double control, ambient, target = 0;
			try {
				target = targetVFIO.read();
				control = indoorVFIO.read();
				ambient = outdoorVFIO.read();
				_logger.debug("***************");
				_logger.debug("target_temp=" + target);
				_logger.debug("indoor_temp=" + control);
				_logger.debug("outdoor_temp=" + ambient);
			} catch (IOException ioe) {
				_logger.error(ioe.toString());
				continue;
			}
			
			// the real decision is here!
			int relayValue = new Control(target, control, ambient, 0.5, relayPosistion > 0).getResult();
			
			try {
				if (relayPosistion != relayValue) {
					relayVFIO.write(relayValue);
					_logger.info("Relay changed to " + relayValue);
				}
			} catch (IOException e) {
				_logger.error(e.toString());
				continue;
			}
		}
		
	}
	
}
