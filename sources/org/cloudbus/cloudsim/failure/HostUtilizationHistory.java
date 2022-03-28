package org.cloudbus.cloudsim.failure;

import java.util.ArrayList;
import java.util.HashMap;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Host;

public class HostUtilizationHistory {
	
	public static ArrayList<Double>utilizationList = new ArrayList<Double>();  
	public static HashMap<Host, ArrayList<Double>> hostUtilizationHistoryTable= new HashMap<Host, ArrayList<Double>>();
	
	public static void setHostUtilizationHistory(Host host, double utilization){
		
		if(hostUtilizationHistoryTable.isEmpty()){
			utilizationList = new ArrayList<Double>();
			hostUtilizationHistoryTable.put(host, utilizationList);
		}
		else{
			if(hostUtilizationHistoryTable.containsKey(host)){
				utilizationList = new ArrayList<Double>();
				utilizationList = hostUtilizationHistoryTable.get(host);
				utilizationList.add(utilization);
				hostUtilizationHistoryTable.put(host, utilizationList);
			}
			else{
				utilizationList = new ArrayList<Double>();
				hostUtilizationHistoryTable.put(host, utilizationList);
			}
		}
		
		
	}
	
	public static ArrayList<Double> getHostUtilizationHistory(Host host){
		if(hostUtilizationHistoryTable.containsKey(host)){
			return(hostUtilizationHistoryTable.get(host)); 
		}
		else{
			return null;
		}
			
	}
	
	
}
