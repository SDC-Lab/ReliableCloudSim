package org.cloudbus.cloudsim.failure;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;

public class HostProvisioning {

	public int getHostCount(List<Cloudlet>cloudlets){
		List<Double>normalizedClets = new ArrayList<Double>();
		int hostCount;
		double h = 0.0;		
		normalizedClets = getNormalizedCloudlets(cloudlets);
		for(int i=0;i<normalizedClets.size();i++){
			h = h+normalizedClets.get(i);
		}	
		hostCount = (int) Math.ceil(h);
		return(hostCount);
	}
	
	public List<Double> getNormalizedCloudlets(List<Cloudlet>cloudlets){
		List<Double>normalizedCloudlets = new ArrayList<Double>();
		long maxCloudlet = getMaxCloudlet(cloudlets);
		for(int i=0;i<cloudlets.size();i++){
			normalizedCloudlets.add(((double)cloudlets.get(i).getCloudletLength())/maxCloudlet);
		}
		return(normalizedCloudlets);
	}
	
	public long getMaxCloudlet(List<Cloudlet>cloudlets){
		long maxCloudlet = 0;
		for(int i=0;i<cloudlets.size();i++){
			if(maxCloudlet<cloudlets.get(i).getCloudletLength()){
				maxCloudlet=cloudlets.get(i).getCloudletLength();
			}
		}
		return(maxCloudlet);
	}
}
