package org.cloudbus.cloudsim.failure;

import java.util.ArrayList;
import java.util.HashMap;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;

public class HostVMMappingUtilization {
	
public static HashMap<Host, ArrayList<Double>> hostVmUtilizationTable = new HashMap<Host, ArrayList<Double>>();
public static HashMap<Vm, ArrayList<Double>> vmUtilizationTable = new HashMap<Vm, ArrayList<Double>>();
	
	public static ArrayList<Double> utilizationList;
	
	
	public static void setVmUtilization(Vm vm, double utilization){
		double totalUtilization = 0;
		if(vmUtilizationTable.isEmpty()){
			utilizationList = new ArrayList<Double>();
			utilizationList.add(utilization);
			vmUtilizationTable.put(vm, utilizationList);
			//return utilization;
		}
		else{
			if(vmUtilizationTable.containsKey(vm)){
				utilizationList = new ArrayList<Double>();
				utilizationList = vmUtilizationTable.get(vm);
				utilizationList.add(utilization);
				vmUtilizationTable.put(vm, utilizationList);
				//for(int i=0;i<utilizationList.size();i++){
					//totalUtilization = totalUtilization + utilizationList.get(i);
				//}
				//utilization = totalUtilization/utilizationList.size();
				//return utilization;
			}
			else{
				utilizationList = new ArrayList<Double>();
				utilizationList.add(utilization);
				vmUtilizationTable.put(vm, utilizationList);
				//return utilization;
			}
		}		
	}
	
	public static double getAverageVmUtilization(Vm vm){
		utilizationList = new ArrayList<Double>();
		double averageUtilization = 0;
		if(vmUtilizationTable.containsKey(vm)){
			utilizationList = vmUtilizationTable.get(vm);
		}
		for(int i=0;i<utilizationList.size();i++){
			averageUtilization = averageUtilization + utilizationList.get(i);
		}
		averageUtilization = averageUtilization/utilizationList.size();
		return averageUtilization;
	}
	
	
/*	
	public static double getVmTotalUtilization(Vm vm){
		utilizationList = new ArrayList<Double>();
		double totalVmUtilization = 0;;
		utilizationList = vmUtilizationTable.get(vm);
		for(int i=0;i<utilizationList.size();i++){
			totalVmUtilization = totalVmUtilization + utilizationList.get(i);
		}
		totalVmUtilization = totalVmUtilization / utilizationList.size();
		return totalVmUtilization;
	}
*/	
	public static void sethostVmUtilizationTable(Host host, double utilization){
		
		if(hostVmUtilizationTable.isEmpty()){
			utilizationList = new ArrayList<Double>();
			utilizationList.add(utilization);
			hostVmUtilizationTable.put(host, utilizationList);			
		}
		else{
			if(hostVmUtilizationTable.containsKey(host)){
				utilizationList = new ArrayList<Double>();
				utilizationList = hostVmUtilizationTable.get(host);
				utilizationList.add(utilization);
				hostVmUtilizationTable.put(host, utilizationList);				
			}
			else{
				utilizationList = new ArrayList<Double>();
				utilizationList.add(utilization);
				hostVmUtilizationTable.put(host, utilizationList);				
			}
		}
		
	}
	
	
	
	public static ArrayList<Double> gethostVmUtilization(Host host){
		utilizationList = new ArrayList<Double>();
		if(hostVmUtilizationTable.containsKey(host)){
			utilizationList = hostVmUtilizationTable.get(host);
		}
		return(utilizationList);
	}	

}
