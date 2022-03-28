
package org.cloudbus.cloudsim.frequency;

import org.cloudbus.cloudsim.failure.PowerModelSpecPowerPlatHomeTRQX150SA;

/**
 * This model corresponds to FTA nodes with CPU count 4 and Memory size 16GB.   
 * @author Yogesh Sharma
 *
 */

//Values are taken from https://www.spec.org/power_ssj2008/results/res2009q2/power_ssj2008-20090325-00138.html


public class FrequencyModelSpecPowerPlatHomeTRQX150SA {
	
	private double maxFrequency = 3.0;
	private double minFrequency = (maxFrequency*37.5)/100;
	private double maxPower;
	public PowerModelSpecPowerPlatHomeTRQX150SA powObject;
	
	public void setPowerObject(PowerModelSpecPowerPlatHomeTRQX150SA powObject) {
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
