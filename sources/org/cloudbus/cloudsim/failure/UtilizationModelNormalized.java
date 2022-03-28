package org.cloudbus.cloudsim.failure;


import org.cloudbus.cloudsim.UtilizationModel;



public class UtilizationModelNormalized implements UtilizationModel {
	
	long Max_Cloudlet;
	long Cloudlet_Length;
	
	public UtilizationModelNormalized(long cloudlet_length, long cloudlet_max){	
			setMaxCloudlet(cloudlet_max);
			setCloudletLength(cloudlet_length);		
	}
	
	public void setMaxCloudlet(long cloudlet_max){
		this.Max_Cloudlet = cloudlet_max;
	}
	
	public void setCloudletLength(long cloudlet_length){
		this.Cloudlet_Length = cloudlet_length;
	}
	
	public double getUtilization(double time){	
		double utilization;		
		utilization = Cloudlet_Length/(double)Max_Cloudlet;
		utilization = Math.ceil(utilization * 100);
		utilization = utilization / 100;		
		return(utilization);		
	}	
	
}
