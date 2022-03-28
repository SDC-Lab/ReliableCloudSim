package org.cloudbus.cloudsim.failure;

import java.util.List;

import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.ResCloudlet;

public class FailureCloudletSchedulerSpaceShared extends CloudletSchedulerSpaceShared{
	
	public void setPreviousTime(double time){
		super.setPreviousTime(time);
	}

	public  <T extends ResCloudlet> List<T> getCloudletExecList(){
		return super.getCloudletExecList();
	}

}
