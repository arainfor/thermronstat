/**
 * HVAC Control Logic
 * 
 * This class provides the business logic for controlling a zone.
 * 
 * No external I/O should ever live here.  
 * 
 */
package com.ronhull.thermronstat;

/**
 * @author arainfor
 *
 */
public class TemperatureControl {

	protected final boolean HEAT_ONLY = true;
	private boolean _running;
	private double _target;
	private double _control;
	private double _ambient;
	private double _anticipator;

	public TemperatureControl(double target, double control, double ambient, double anticipator, boolean running) {
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
		if (HEAT_ONLY)
			return HEAT_ONLY;
		
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
				if (_control < _target) {
					return 1;
				}
			} else {
				if (_control < _target - _anticipator) {
					return 1;
				}
			}
		} else {
			//_logger.debug("Cool mode");
			if (_running) {
				if (_control > _target) {
					return 1;
				}
			} else {
				if (_control > _target + _anticipator) {
					return 1;
				}
			}
		}
		return 0;
	}
}
