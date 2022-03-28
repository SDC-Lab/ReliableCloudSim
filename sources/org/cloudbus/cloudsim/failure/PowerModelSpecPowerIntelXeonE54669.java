package org.cloudbus.cloudsim.failure;

/**
 * This model corresponds to FTA nodes with CPU count 256.   
 * @author Yogesh
 *
 */

//Values are taken from https://www.spec.org/power_ssj2008/results/res2016q3/power_ssj2008-20160705-00737.html

public class PowerModelSpecPowerIntelXeonE54669{
	
	private double[] power = { 86.4, 181, 215, 253, 287, 315, 340, 377, 421, 483, 562 };

	/*
	 * (non-Javadoc)
	 * @see org.cloudbus.cloudsim.power.models.PowerModelSpecPower#getPowerData(int)
	 */
	
	public double getPowerData(int index) {
		return power[index];
	}
	
	public double getMaxPowerData() {
		return power[10];
	}

}
