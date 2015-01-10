/**
 * HVAC Control Logic
 * 
 * This class provides the business logic for controlling a zone.
 * 
 * No external I/O should ever live here.  
 * 
 */
package com.arainfor.thermronstat;

import java.util.ArrayList;

/**
 * @author arainfor
 *
 */
public class H1TemperatureControl {

	protected final boolean HEAT_ONLY = true;
	private boolean running;
	private double target;
	private double control;
	private double ambient;
	private double anticipator;

	public H1TemperatureControl(double target, double control, double ambient, double anticipator, boolean running) {
		this.target = target;
		this.control = control;
		this.ambient = ambient;
		this.running = running;
		this.anticipator = anticipator;
	}
	
	/**
	 * Heat or cool mode.  Answers the question.
	 * @return
	 */
	public boolean isHeat() {
		if (HEAT_ONLY)
			return HEAT_ONLY;

		if (target > ambient)
			return true;
		return false;
	}
	
	/**
	 * Returns if we should energized the relay depending on mode.
	 * @return
	 */
	public ArrayList<RelayOutputs> execute() {

		if (isHeat()) {

			//_logger.debug("Heat mode");
			if (running) {
				// TODO: This logic should consider the current runtime and adjust anticipator
				if (control < target - anticipator) {
					return new RelayControls().heatStage1();
				}
			} else {
				if (control < target) {
					return new RelayControls().heatStage1();
				}
			}
		} else {

			//_logger.debug("Cool mode");
			if (running) {
				if (control > target + anticipator) {
					return new RelayControls().coolStage1();
				}
			} else {
				if (control > target) {
					return new RelayControls().coolStage1();
				}
			}
		}

		return new ArrayList<RelayOutputs>();  // No relays are energized
	}
}
