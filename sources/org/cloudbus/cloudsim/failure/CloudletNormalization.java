package org.cloudbus.cloudsim.failure;

import java.util.HashMap;

import org.cloudbus.cloudsim.Cloudlet;

public class CloudletNormalization {
	
	public HashMap<Cloudlet, Double> cletNormalizedLengthTable = new HashMap<Cloudlet, Double>();
		
	public void setCloudletNormalizedLength(Cloudlet clet, long cloudlet_max){
		double normalizedLength;		
		normalizedLength = clet.getCloudletLength()/(double)cloudlet_max;
		cletNormalizedLengthTable.put(clet, normalizedLength);		
	}
	
	public double getNormalizedLength(Cloudlet clet){		
		double length = cletNormalizedLengthTable.get(clet);
		return(length);		
	}
	
	public boolean checkCloudletPresence(Cloudlet clet){
		if(cletNormalizedLengthTable.containsKey(clet)){
			return true;
		}
		else{
			return false;
		}
	}
	
}
