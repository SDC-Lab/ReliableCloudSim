package org.cloudbus.cloudsim.failure;

/**
 * This model corresponds to FTA nodes with CPU count 4 and Memory size 8GB.   
 * @author Yogesh
 *
 */

//Values are taken from https://www.spec.org/power_ssj2008/results/res2009q2/power_ssj2008-20090325-00140.html

public class PowerModelSpecPowerHpProLiantDL380G5 {
	
	/** The power. */
	private final double[] power = { 226, 236, 245, 255, 271, 289, 312, 330, 355, 381, 394 };

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
