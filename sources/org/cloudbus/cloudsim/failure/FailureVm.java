package org.cloudbus.cloudsim.failure;

import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.Vm;

public class FailureVm extends Vm{
	
	double vmUtilization;
	
	public FailureVm(int id,
			int userId,
			double mips,
			int numberOfPes,
			int ram,
			long bw,
			double vmUtilization,
			long size,			 
			String vmm,
			CloudletScheduler cloudletScheduler){
		super(id, userId, mips, numberOfPes, ram, bw, size, vmm, cloudletScheduler);
	
	setCurrentUtilization(vmUtilization);	}
	
	public void setCurrentUtilization(double vmUtilization){ 
		this.vmUtilization= vmUtilization;
	}
	public double getCurrentUtilization(){ 
		return vmUtilization;
	}	
	
	
	//public void setId(int id){
	//	super.setId(id);		
	//}
}
