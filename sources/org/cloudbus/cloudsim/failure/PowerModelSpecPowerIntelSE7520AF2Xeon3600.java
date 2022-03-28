package org.cloudbus.cloudsim.failure;

import org.cloudbus.cloudsim.power.models.PowerModelSpecPower;

/**
 * This model corresponds to FTA nodes with CPU count 2  
 * @author Yogesh
 *
 */


// Values have been taken from https://www.spec.org/power_ssj2008/results/res2007q4/power_ssj2008-20071129-00015.html

public class PowerModelSpecPowerIntelSE7520AF2Xeon3600 {

		/** The power. */
		private double[] power = { 159, 170, 181, 194, 207, 225, 241, 263, 288, 308, 336 };

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



