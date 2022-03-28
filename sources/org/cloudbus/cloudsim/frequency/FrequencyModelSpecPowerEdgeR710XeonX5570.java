package org.cloudbus.cloudsim.frequency;

import org.cloudbus.cloudsim.failure.PowerModelSpecPowerEdgeR710XeonX5570;

/**
 * This model corresponds to FTA nodes with CPU count 8 and Memory size 16GB.   
 * @author Yogesh
 *
 */

//Values are taken from https://www.spec.org/power_ssj2008/results/res2009q1/power_ssj2008-20090310-00129.html

public class FrequencyModelSpecPowerEdgeR710XeonX5570 {
	
	private double maxFrequency = 2.9;
	private double minFrequency = (maxFrequency*37.5)/100;
	private double maxPower;
	public PowerModelSpecPowerEdgeR710XeonX5570 powObject;
	
	public void setPowerObject(PowerModelSpecPowerEdgeR710XeonX5570 powObject) {
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
