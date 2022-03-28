package org.cloudbus.cloudsim.failure;

/**
 * This model corresponds to FTA nodes with CPU count 128 and 80
 * @author Yogesh
 *
 */

// Values are taken from https://www.spec.org/power_ssj2008/results/res2016q2/power_ssj2008-20160607-00734.html

public class PowerModelSpecPowerHpProLiantDL560Gen9{
	
	/** The power. */
	private double[] power = { 86.3, 178, 218, 257, 289, 315, 340, 382, 433, 498, 557 };

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
