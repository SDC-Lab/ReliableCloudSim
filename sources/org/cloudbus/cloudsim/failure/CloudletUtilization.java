package org.cloudbus.cloudsim.failure;

import java.util.ArrayList;
import java.util.HashMap;

import org.cloudbus.cloudsim.Cloudlet;

public class CloudletUtilization {

	//public static ArrayList<Double>utlList;
	//public static HashMap<Cloudlet, Double> cletUtilizationTable = new HashMap<Cloudlet, Double>();
	public static HashMap<Cloudlet, Integer> cletEventCountTable = new HashMap<Cloudlet, Integer>();
	public static ArrayList<Double> utilizationList;
	public static HashMap<Cloudlet, ArrayList<Double>> cletUtilizationTable = new HashMap<Cloudlet, ArrayList<Double>>();
	
	PowerModel pow;
	
	/*
	public static void setCletUtilization(Cloudlet clet, double utilization){
		double utilization_prev;
		int event_count;		
		utilization_prev = getCletUtilization(clet);
		utilization = utilization_prev + utilization;		
		cletUtilizationTable.put(clet, utilization);
		System.out.println("Total utilization of cloudlet #" +clet.getCloudletId()+ " is " +cletUtilizationTable.get(clet));
		event_count = getCletEventCount(clet);		
		event_count = event_count + 1;		
		setCletEventCountTable(clet, event_count);
		System.out.println("Total event count of cloudlet #" +clet.getCloudletId()+ " is " +cletEventCountTable.get(clet));
	}
	*/
	public static void setCletUtilization(Cloudlet clet, double utilization){	
		
		if(cletUtilizationTable.isEmpty()){
			utilizationList = new ArrayList<Double>();
			cletUtilizationTable.put(clet, utilizationList);
		}
		else{
			if(cletUtilizationTable.containsKey(clet)){
				utilizationList = new ArrayList<Double>();
				utilizationList = cletUtilizationTable.get(clet);
				utilizationList.add(utilization);
				cletUtilizationTable.put(clet, utilizationList);
			}
			else{
				utilizationList = new ArrayList<Double>();
				cletUtilizationTable.put(clet, utilizationList);
			}
		}
		
		//for(int i=0;i<cletUtilizationTable.get(clet).size();i++){
			//System.out.println("Utilization List for cloudlet #" +clet.getCloudletId()+ " is " +cletUtilizationTable.get(clet).get(i));
		//}
		
	}
	
	/*
	public static void setCletEventCountTable(Cloudlet cloudlet, int count) {		
		cletEventCountTable.put(cloudlet, count);		
	}	
	
	
	public static double getCletUtilization(Cloudlet clet){
		if(cletUtilizationTable.isEmpty()){
			return(0);
		}
		else{
			if(cletUtilizationTable.containsKey(clet)){
				return(cletUtilizationTable.get(clet));
			}
			else{
				return(0);
			}
		}
	}
	*/
	public static int getCletEventCount(Cloudlet cloudlet){		
		if(cletUtilizationTable.containsKey(cloudlet)){
			return(cletUtilizationTable.get(cloudlet).size());
		}
		else{
			return 0;
		}
		/*
		if(cletEventCountTable.isEmpty()){
			return(0);
		}
		else{
			if(cletEventCountTable.containsKey(cloudlet)){
				return(cletEventCountTable.get(cloudlet));
			}
			else{
				return(0);
			}
		}
		*/
	}
	
	public static double getCloudletAverageUtilization(Cloudlet clet){
		double total_utilization = 0.0;
		int total_events_count;
		double averageUtilization;
		total_events_count = getCletEventCount(clet);
		for(int i=0;i<total_events_count;i++){
			total_utilization = total_utilization + cletUtilizationTable.get(clet).get(i);
		}
		//total_utilization = getCletUtilization(clet);
		//total_events_count = getCletEventCount(clet);
		averageUtilization = total_utilization/total_events_count;
		return(averageUtilization);		
		
	}
	
	
}
