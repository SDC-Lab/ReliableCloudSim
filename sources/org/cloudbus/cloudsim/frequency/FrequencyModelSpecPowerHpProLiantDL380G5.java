package org.cloudbus.cloudsim.frequency;

import org.cloudbus.cloudsim.failure.PowerModelSpecPowerHpProLiantDL380G5;

/**
 * This model corresponds to FTA nodes with CPU count 4 and Memory size 8GB.   
 * @author Yogesh
 *
 */

//Values are taken from https://www.spec.org/power_ssj2008/results/res2009q2/power_ssj2008-20090325-00140.html

public class FrequencyModelSpecPowerHpProLiantDL380G5 {
	
	private double maxFrequency = 3.8;
	private double minFrequency = (maxFrequency*37.5)/100;
	private double maxPower;
	public PowerModelSpecPowerHpProLiantDL380G5 powObject;
	
	public void setPowerObject(PowerModelSpecPowerHpProLiantDL380G5 powObject) {
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
