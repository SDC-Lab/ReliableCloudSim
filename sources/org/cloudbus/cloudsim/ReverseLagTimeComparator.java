package org.cloudbus.cloudsim;

import java.util.Comparator;

import org.cloudbus.cloudsim.core.CloudSim;

class ReverseLagTimeComparator implements Comparator<Cloudlet> {
	
	HeftJobDpBroker broker;
	
	public ReverseLagTimeComparator(HeftJobDpBroker broker){
		this.broker = broker;
	}

	@Override
	public int compare(Cloudlet cl1, Cloudlet cl2) {
		long currentTime = (long) CloudSim.clock();
		
		CloudletDeadlineInfo info1 = broker.getCloudletDeadlineInfo(cl1);
		CloudletDeadlineInfo info2 = broker.getCloudletDeadlineInfo(cl2); 
		
		long lag1 = currentTime-info1.getDeadline()-info1.getEstimatedRunTime();
		long lag2 = currentTime-info2.getDeadline()-info2.getEstimatedRunTime();
				
		if(lag1>lag2) return -1;
		if(lag1<lag2) return 1;
		return 0;
	}
}
