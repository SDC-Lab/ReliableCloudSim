package org.cloudbus.cloudsim.failure;


import java.util.List;
import java.util.logging.Level;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmScheduler;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.provisioners.BwProvisioner;
import org.cloudbus.cloudsim.provisioners.RamProvisioner;

public class FailureHost extends Host{
	
	private double utilization;
	
	public FailureHost(int id,
			RamProvisioner ramProvisioner,
			BwProvisioner bwProvisioner,
			long storage,	
			double utilization,
			List<? extends Pe> peList,
			VmScheduler vmScheduler){
		super(id, ramProvisioner, bwProvisioner, storage, peList, vmScheduler);
		
		setFailed(false);
		setUtilization(utilization);
	}


	public void setUtilization(double utilization){		
		this.utilization = utilization;
		
	}
	
	public double getUtilization(){ 
		return utilization;
	}
	
	public void calUtilization(){ 
		FailureVm vm;
		double utilization;
		double totalUtilization = 0.0;
		for(int i=0; i<getVmList().size(); i++){
			vm = (FailureVm) getVmList().get(i);			
			totalUtilization = totalUtilization + vm.getCurrentUtilization();
			}
		
		utilization = totalUtilization/getNumberOfPes();
		setUtilization(utilization);		
	}
	



}