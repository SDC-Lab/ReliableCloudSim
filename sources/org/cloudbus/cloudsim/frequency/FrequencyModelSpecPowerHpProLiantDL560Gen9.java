package org.cloudbus.cloudsim.frequency;

import org.cloudbus.cloudsim.failure.PowerModelSpecPowerHpProLiantDL560Gen9;

/**
 * This model corresponds to FTA nodes with CPU count 128 and 80
 * @author Yogesh
 *
 */

// Values are taken from https://www.spec.org/power_ssj2008/results/res2016q2/power_ssj2008-20160607-00734.html

public class FrequencyModelSpecPowerHpProLiantDL560Gen9 {
	
	private double maxFrequency = 2.1;
	private double minFrequency = (maxFrequency*37.5)/100;
	private double maxPower;
	public PowerModelSpecPowerHpProLiantDL560Gen9 powObject;
	
	public void setPowerObject(PowerModelSpecPowerHpProLiantDL560Gen9 powObject) {
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
