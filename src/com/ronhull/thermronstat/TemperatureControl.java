/**
 * HVAC Control Logic
 * 
 * This class provides the business logic for controlling a zone.
 * 
 * No external I/O should ever live here.  
 * 
 */
package com.ronhull.thermronstat;

import org.slf4j.Logger;

import com.arainfor.util.logger.AppLogger;

/**
 * @author arainfor
 *
 */
public class TemperatureControl {

	private boolean _running;
	private double _target;
	private double _control;
	private double _ambient;
	private double _anticipator;
	//private Logger _logger;

	public TemperatureControl(double target, double control, double ambient, double anticipator, boolean running) {
		//_logger = new AppLogger().getLogger(this.getClass().getName());
		_target = target;
		_control = control;
		_ambient = ambient;
		_running = running;
		_anticipator = anticipator;
	}
	
	/**
	 * Heat or cool mode.  Answers the question.
	 * @return
	 */
	public boolean isHeat() {
		if (_target > _ambient)
			return true;
		return false;
	}
	
	/**
	 * Returns if we should energized the relay depending on mode.
	 * @return
	 */
	public int enable() {
		if (isHeat()) {
			//_logger.debug("Heat mode");
			if (_running) {
				if (_control + _anticipator < _target) {
					return 1;
				}
			} else {
				if (_control < _target) {
					return 1;
				}
			}
		} else {
			//_logger.debug("Cool mode");
			if (_running) {
				if (_control - _anticipator > _target) {
					return 1;
				}
			} else {
				if (_control > _target) {
					return 1;
				}
			}
		}
		return 0;
	}
}
