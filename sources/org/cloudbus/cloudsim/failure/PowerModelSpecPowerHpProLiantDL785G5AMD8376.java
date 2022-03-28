package org.cloudbus.cloudsim.failure;

/**
 * This model corresponds to FTA nodes with CPU count 32.   
 * @author Yogesh
 *
 */

//Values are taken from https://www.spec.org/power_ssj2008/results/res2009q1/power_ssj2008-20090210-00113.html

public class PowerModelSpecPowerHpProLiantDL785G5AMD8376{	
	
	/** The power. */
	private double[] power = { 444, 468, 520, 576, 619, 655, 687, 715, 745, 775, 799 };

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
