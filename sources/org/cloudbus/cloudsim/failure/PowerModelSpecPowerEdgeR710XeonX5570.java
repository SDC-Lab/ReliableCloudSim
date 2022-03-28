package org.cloudbus.cloudsim.failure;

/**
 * This model corresponds to FTA nodes with CPU count 8 and Memory size 16GB.   
 * @author Yogesh
 *
 */

//Values are taken from https://www.spec.org/power_ssj2008/results/res2009q1/power_ssj2008-20090310-00129.html

public class PowerModelSpecPowerEdgeR710XeonX5570 {
	/** The power. */
	private final double[] power = { 75.6, 105, 119, 129, 140, 151, 164, 180, 198, 218, 237};

	/*
	 * (non-Javadoc)
	 * @see org.cloudbus.cloudsim.power.models.PowerModelSpecPower#getPowerData(int)
	 */
	
	protected double getPowerData(int index) {
		return power[index];
	}
	
	public double getMaxPowerData() {
		return power[10];
	}

}
