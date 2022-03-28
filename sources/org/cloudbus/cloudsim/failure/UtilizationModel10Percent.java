package org.cloudbus.cloudsim.failure;

import java.util.Random;

import org.cloudbus.cloudsim.UtilizationModel;

public class UtilizationModel10Percent implements UtilizationModel{	
	public double getUtilization(double time){		
		return .1;
	}	
}
