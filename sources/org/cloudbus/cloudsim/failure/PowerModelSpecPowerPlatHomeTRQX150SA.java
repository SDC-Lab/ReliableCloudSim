package org.cloudbus.cloudsim.failure;


/**
 * This model corresponds to FTA nodes with CPU count 4 and Memory size 16GB.   
 * @author Yogesh
 *
 */

//Values are taken from https://www.spec.org/power_ssj2008/results/res2009q2/power_ssj2008-20090325-00138.html

public class PowerModelSpecPowerPlatHomeTRQX150SA {	
	
	/** The power. */
	private double[] power = { 178, 186, 195, 204, 213, 222, 232, 241, 250, 260, 268 };

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
