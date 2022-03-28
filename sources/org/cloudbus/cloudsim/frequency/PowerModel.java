package org.cloudbus.cloudsim.frequency;

import org.cloudbus.cloudsim.frequency.FrequencyModelSpecPowerIntelSE7520AF2Xeon3600;


public class PowerModel {
	
	public FrequencyDetails freq;
	
	public void setFrequencyDetails(FrequencyDetails freq) {
		this.freq = freq;
	}
	
	public double getPower(int coreCount, char scaleUpDown) {
		double power = 0;
		double indpendentPower = 0;
		if(coreCount == 2){
			FrequencyModelSpecPowerIntelSE7520AF2Xeon3600 freqObj = new FrequencyModelSpecPowerIntelSE7520AF2Xeon3600();
			indpendentPower = freqObj.getIndependentPower();
			
			
		}
		
		return power;
		
	}
	
	public double getPower(int coreCount) {
		double power = 0;
		double indpendentPower = 0;
		if(coreCount == 2){
			FrequencyModelSpecPowerIntelSE7520AF2Xeon3600 freqObj = new FrequencyModelSpecPowerIntelSE7520AF2Xeon3600();
			indpendentPower = freqObj.getIndependentPower();
			
			
		}
		return power;
		
	}

}
