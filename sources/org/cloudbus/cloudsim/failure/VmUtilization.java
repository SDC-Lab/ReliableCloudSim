package org.cloudbus.cloudsim.failure;

import java.util.ArrayList;
import java.util.HashMap;

import org.cloudbus.cloudsim.Vm;

public class VmUtilization {
	
	public  HashMap<Integer, ArrayList<Double>> vmUtilization = new HashMap<Integer, ArrayList<Double>>();
	public  HashMap<Integer, Double> vmCurrentUtilization = new HashMap<Integer, Double>();
	
	public ArrayList<Double>utilization;
	
	
	public void setVmUtilization(Integer vmID, Double utl){
		if(vmUtilization.isEmpty()){
			utilization = new ArrayList<Double>();
			utilization.add(utl);
			vmUtilization.put(vmID, utilization);
		}
		else{
			if(vmUtilization.containsKey(vmID)){
				utilization = new ArrayList<Double>();
				utilization = vmUtilization.get(vmID);
				utilization.add(utl);
				vmUtilization.put(vmID, utilization);
			}
			else{
				utilization = new ArrayList<Double>();
				utilization.add(utl);
				vmUtilization.put(vmID, utilization);
			}
		}	
	}
	
	public void setCurrentVmUtilization(int vmID, double utl){
		vmCurrentUtilization.put(vmID, utl);
	}
	
	public double getCurrentVmUtilization(Integer vmID){
		if(vmCurrentUtilization.containsKey(vmID)){
			return  vmCurrentUtilization.get(vmID);	
		}else{
			return 0;
		}		
	}
	
	
	public double getVmAverageUtilization(Integer vmID){
		utilization = new ArrayList<Double>();
		utilization = vmUtilization.get(vmID);
		double totalUtilization = 0;
		double averageUtilization = 0;
		for(int i=0;i<utilization.size();i++){
			totalUtilization = totalUtilization + utilization.get(i);			
		}
		averageUtilization = totalUtilization/utilization.size();
		return averageUtilization;
	}
}
