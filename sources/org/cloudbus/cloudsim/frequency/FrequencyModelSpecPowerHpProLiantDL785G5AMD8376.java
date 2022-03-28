package org.cloudbus.cloudsim.frequency;

import org.cloudbus.cloudsim.failure.PowerModelSpecPowerHpProLiantDL785G5AMD8376;

/**
 * This model corresponds to FTA nodes with CPU count 32.   
 * @author Yogesh
 *
 */

//Values are taken from https://www.spec.org/power_ssj2008/results/res2009q1/power_ssj2008-20090210-00113.html

public class FrequencyModelSpecPowerHpProLiantDL785G5AMD8376 {
	
	private double maxFrequency = 2.3;
	private double minFrequency = (maxFrequency*37.5)/100;
	private double maxPower;
	public PowerModelSpecPowerHpProLiantDL785G5AMD8376 powObject;
	
	public void setPowerObject(PowerModelSpecPowerHpProLiantDL785G5AMD8376 powObject) {
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
