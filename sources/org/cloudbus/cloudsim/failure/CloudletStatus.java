package org.cloudbus.cloudsim.failure;

import java.util.HashMap;

import org.cloudbus.cloudsim.Cloudlet;

public class CloudletStatus {
	
	public static HashMap<Integer, Long>remainingCloudlet = new HashMap<Integer, Long>();
	public static HashMap<Integer, Long>sofarCloudlet = new HashMap<Integer, Long>();
	public static HashMap<Integer, Integer> cletEventCountTable = new HashMap<Integer, Integer>();
	public  static int counter = 0;
	
	public static void setremainingCloudlet(int clet, Long remainingLength){		
		remainingCloudlet.put(clet, remainingLength);	
	}
	
	public static void setsofarCloudlet(int clet, Long sofar){		
		sofarCloudlet.put(clet, sofar);
	}
	
	public static void setCletEventCountTable(int clet, int count) {		
		int previous_count = 0;
		if(cletEventCountTable.containsKey(clet)){
			previous_count = cletEventCountTable.get(clet);
		}
		previous_count = previous_count + count;
		cletEventCountTable.put(clet, previous_count);
		
	}
	
	public static Long getremainingCloudlet(int clet){
		return(remainingCloudlet.get(clet));
	}
	
	public static Long getsofarCloudlet(int clet){
		return(sofarCloudlet.get(clet));
	}
	
	public static int getCletEventCount(int clet){		
		return(cletEventCountTable.get(clet));
	}
}
