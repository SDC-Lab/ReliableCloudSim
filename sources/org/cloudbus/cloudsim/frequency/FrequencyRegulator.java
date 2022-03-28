package org.cloudbus.cloudsim.frequency;

import org.cloudbus.cloudsim.Vm;

public class FrequencyRegulator {

	public DeadlineModel dlModel;
	public FrequencyDetails frqDetails;
	
	public void setDeadlineModel(DeadlineModel dlModel) {
		this.dlModel = dlModel;
	}
	
	public void setFrequencyDetails(FrequencyDetails frqDetails) {
		this.frqDetails = frqDetails;
	}
	
	public double regulatedFrequency(int cletID, Vm vm) {
		double regulatedMips = 0;
		
		return regulatedMips;
	}
	
	

}
