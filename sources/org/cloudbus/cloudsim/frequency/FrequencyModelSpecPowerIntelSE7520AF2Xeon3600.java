package org.cloudbus.cloudsim.frequency;

import org.cloudbus.cloudsim.failure.PowerModelSpecPowerIntelSE7520AF2Xeon3600;

/**
 * This model corresponds to FTA nodes with CPU count 2  
 * @author Yogesh Sharma
 *
 */

// Values have been taken from https://www.spec.org/power_ssj2008/results/res2007q4/power_ssj2008-20071129-00015.html

public class FrequencyModelSpecPowerIntelSE7520AF2Xeon3600 {
	
	private double maxFrequency = 3.6;
	private double minFrequency = (maxFrequency*37.5)/100;
	private double maxPower;
	public PowerModelSpecPowerIntelSE7520AF2Xeon3600 powObject;
	
	public void setPowerObject(PowerModelSpecPowerIntelSE7520AF2Xeon3600 powObject) {
		this.powObject = powObject;		
	}
	
	public double getMaxFrequency() {
		return maxFrequency;		
	}
	
	public double getMinFrequency() {
		return minFrequency;
	}
	
	public double getIndependentPower() {
		maxPower = powObject.getMaxPowerData();
		return (maxPower*45)/100;		
	}

}
