package org.cloudbus.cloudsim.failure;
//This class has been written to speed up the simulation process. 

import java.util.HashMap;

public class FTACurrentUtilization {
	
	public static HashMap<Integer, Double>ftaHostCurrentUtilization = new HashMap<Integer, Double>(); 
	
	public static void setCurrentUtilization(int ftaNode, double utilization){
		ftaHostCurrentUtilization.put(ftaNode, utilization);
	}
	
	public static double getCurrentUtilization(int ftaNode){
		return ftaHostCurrentUtilization.get(ftaNode);
	}

}
