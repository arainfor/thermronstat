/**
 * HVAC Control Logic
 *
 * This class provides the business logic for controlling a zone.
 *
 * No external I/O should ever live here.
 *
 */
package com.arainfor.thermostat;

import java.util.ArrayList;

/**
 * This class is for a Single Stage Heat Only furnace.
 *
 * @author arainfor
 *
 */
public class SingleStageHvacHandler {

	private static final long MIN_RUNTIME = Long.parseLong(System.getProperty("MIN_RUNTIME", String.valueOf(1000 * 60 * 5)));
	private static final long MAX_ANTICIPATOR_RUNTIME = Long.parseLong(System.getProperty("MAX_ANTICIPATOR_RUNTIME", String.valueOf(1000 * 60 * 15)));
	protected final boolean HEAT_ONLY = true;
    private final long runtime;
    private final double targetTemperature;
    private final double currentTemperature;
    private final double anticipator;

    public SingleStageHvacHandler(double targetTemperature, double currentTemperature, double anticipator, long runtime) {
        this.targetTemperature = targetTemperature;
		this.currentTemperature = currentTemperature;
		this.runtime = runtime;
		this.anticipator = anticipator;
	}

	/**
	 * Heat or cool mode.  Answers the question.
     * @return Answers the question
     */
	public boolean isHeat() {
        return HEAT_ONLY && HEAT_ONLY;

    }

	/**
	 * Returns the relays we should energize depending on mode.
	 *
     * @return Which releay to energize
     */
	public ArrayList<RelayDef> execute() {

		double anticpatorValue = anticipator;

		// Shall we use the anticpator value or have we run too long to need it?
		if (runtime > MAX_ANTICIPATOR_RUNTIME) {
			anticpatorValue = 0;
		}

		// We need a minimum runtime for the anticipator to work at all too!!
		if (runtime > 0 && runtime < MIN_RUNTIME) {
			anticpatorValue = 0;
		}

		if (isHeat()) {

			//_logger.debug("Heat mode");
			if (runtime != 0) {

				// TODO: This logic should consider the current runtime and adjust anticipator
				if (currentTemperature < targetTemperature - anticpatorValue) {
					return new RelayControls().heatStage1();
				}
			} else {
				if (currentTemperature < targetTemperature) {
					return new RelayControls().heatStage1();
				}
			}
		} else {

			//_logger.debug("Cool mode");
			if (runtime != 0) {
				if (currentTemperature > targetTemperature + anticpatorValue) {
					return new RelayControls().coolStage1();
				}
			} else {
				if (currentTemperature > targetTemperature) {
					return new RelayControls().coolStage1();
				}
			}
		}

		return new ArrayList<RelayDef>();  // No relays are energized
	}
}
