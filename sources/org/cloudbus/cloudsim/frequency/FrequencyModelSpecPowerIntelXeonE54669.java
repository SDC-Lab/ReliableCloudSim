package org.cloudbus.cloudsim.frequency;

import org.cloudbus.cloudsim.failure.PowerModelSpecPowerIntelXeonE54669;

/**
 * This model corresponds to FTA nodes with CPU count 256.   
 * @author Yogesh
 *
 */

//Values are taken from https://www.spec.org/power_ssj2008/results/res2016q3/power_ssj2008-20160705-00737.html

public class FrequencyModelSpecPowerIntelXeonE54669 {

	private double maxFrequency = 2.2;
	private double minFrequency = (maxFrequency*37.5)/100;
	private double maxPower;
	public PowerModelSpecPowerIntelXeonE54669 powObject;
	
	public void setPowerObject(PowerModelSpecPowerIntelXeonE54669 powObject) {
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
