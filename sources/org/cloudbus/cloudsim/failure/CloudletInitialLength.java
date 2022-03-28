package org.cloudbus.cloudsim.failure;

import java.util.HashMap;

import org.cloudbus.cloudsim.Cloudlet;

public class CloudletInitialLength {

	//public static HashMap<Cloudlet, Long> cletInitialLength = new HashMap<Cloudlet, Long>();
	public static HashMap<Integer, Long> cletInitialLength = new HashMap<Integer, Long>();
	
	public static void setCletInitialLength(Integer clet, long length){
		cletInitialLength.put(clet, length);
	}
	
	public static long getCletIntialLength(Integer clet){
		return cletInitialLength.get(clet);
	}
	
}
