package org.cloudbus.cloudsim.failure;

import java.util.HashMap;

import org.cloudbus.cloudsim.Cloudlet;

public class CloudletEventCounter {
	
	public static HashMap<Cloudlet, Integer> cletEventCountTable = new HashMap<Cloudlet, Integer>();	
	
	public static int getCletEventCount(Cloudlet cloudlet){		
		return(cletEventCountTable.get(cloudlet));
	}

	public static void setCletEventCountTable(Cloudlet cloudlet, int count) {
		// TODO Auto-generated method stub
		int previous_count = 0;
		if(cletEventCountTable.containsKey(cloudlet)){
			previous_count = cletEventCountTable.get(cloudlet);
		}
		previous_count = previous_count + count;
		cletEventCountTable.put(cloudlet, previous_count);
		
	}

}
