package org.cloudbus.cloudsim.failure;

import java.text.DecimalFormat;
import java.util.Random;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.UtilizationModel;

public class UtilizationModelRandom implements UtilizationModel{	
	Random rand = new Random();
	public double getUtilization(double time){
		double utilization;
		do{
			utilization = rand.nextDouble();
		}while(utilization<.1);
		utilization  = Math.floor(utilization*100)/100;
		return(utilization);
	}	
	
	
}
