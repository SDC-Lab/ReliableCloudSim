package org.cloudbus.cloudsim.failure;

import java.util.HashMap;

public class HostUtilization {
	
		
		public static HashMap<Integer, Double> hostUtilization = new HashMap<Integer, Double>();
		
		public static void sethostUtilization(Integer hostID, Double utl){
			hostUtilization.put(hostID, utl);
		}
		
		public static double gethostUtilization(Integer vmID){
			return hostUtilization.get(vmID);
			
		}

}
