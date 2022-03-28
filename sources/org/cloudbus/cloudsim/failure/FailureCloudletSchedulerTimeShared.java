package org.cloudbus.cloudsim.failure;

import java.util.List;

import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.ResCloudlet;

public class FailureCloudletSchedulerTimeShared extends CloudletSchedulerTimeShared{
	
	public void setPreviousTime(double time){
		super.setPreviousTime(time);
	}

	public  <T extends ResCloudlet> List<T> getCloudletExecList(){
		return super.getCloudletExecList();
	}
	
	public double getCapacity(List<Double> mipsShare) {
		double capacity = 0.0;
		int cpus = 0;
		for (Double mips : mipsShare) {
			capacity += mips;
			if (mips > 0.0) {
				cpus++;
			}
		}
		currentCPUs = cpus;

		int pesInUse = 0;
		for (ResCloudlet rcl : getCloudletExecList()) {
			pesInUse += rcl.getNumberOfPes();
		}

		//if (pesInUse > currentCPUs) {
		//	capacity /= pesInUse;
		//} else {
			capacity /= currentCPUs;
		//}
		return capacity;
	}
	
}