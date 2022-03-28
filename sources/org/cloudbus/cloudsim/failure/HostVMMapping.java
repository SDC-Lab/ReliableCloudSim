package org.cloudbus.cloudsim.failure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Host;

import org.cloudbus.cloudsim.Vm;

public class HostVMMapping {
	
	public static HashMap<Host, ArrayList<Vm>> hostVmMapTable = new HashMap<Host, ArrayList<Vm>>();
	
	public static HashMap<Host, ArrayList<Double>>hostVmMapPowerTable = new HashMap<Host, ArrayList<Double>>();
	
	public static HashMap<Host, ArrayList<Double>>hostVmMapIdlePowerTable = new HashMap<Host, ArrayList<Double>>();
	
	public static ArrayList<Vm> vmList;
	
	public static ArrayList<Double>vmPowerList;
	
	public static ArrayList<Host> hostList = new ArrayList<Host>();
	
	public static Vm value;
	
	public static Host host;
	
	public static void sethostVmMapTable(Host host, Vm vm){
		
		if(hostVmMapTable.isEmpty()){
			vmList = new ArrayList<Vm>();
			vmList.add(vm);
			hostVmMapTable.put(host, vmList);			
		}
		else{			
			if(hostVmMapTable.containsKey(host)){
				boolean vmContains = false;
				vmList = new ArrayList<Vm>();
				vmList = hostVmMapTable.get(host);
				for(int i=0; i<vmList.size(); i++){
					if(vmList.contains(vm)){
						vmContains = true;
					}
				}
				if(vmContains == false){
					vmList.add(vm);
				}				
				hostVmMapTable.put(host, vmList);				
			}
			else{
				vmList = new ArrayList<Vm>();
				vmList.add(vm);
				hostVmMapTable.put(host, vmList);				
			}
		}
		
	}
	
	public static void sethostVmMapPowerTable(Host host, ArrayList<Double>powerList){
		hostVmMapPowerTable.put(host, powerList);
	}
	
	public static void sethostVmMapIdlePowerTable(Host host, ArrayList<Double>powerList){
		hostVmMapIdlePowerTable.put(host, powerList);
	}
	
	public static double gethostVMMapPower(Host host, Vm vm){
		vmList = new ArrayList<Vm>();
		vmPowerList = new ArrayList<Double>();		
		vmList = hostVmMapTable.get(host);
		vmPowerList = hostVmMapPowerTable.get(host);
		double power = 0.0;
		boolean flagVmFound = false;
		for(int i=0; i<vmList.size(); i++){
			if(vmList.get(i) == vm){
				power = vmPowerList.get(i);
				flagVmFound = true;
				break;
			}			
		}
		if(flagVmFound == true){
			return power;
		}
		else{
			return 0.0;
		}
		
	}
	
	public static ArrayList<Vm> gethostVmMap(Host host){
		vmList = new ArrayList<Vm>();
		if(hostVmMapTable.containsKey(host)){
			vmList = hostVmMapTable.get(host);
		}
		return(vmList);
	}	
	
	public static ArrayList<Host> getHostKeySet(){
		for(Host host: hostVmMapTable.keySet()){
			hostList.add(host);
		}
		return hostList;
	}
	
	public static Host getHost(Vm vm){	
		for(Map.Entry<Host, ArrayList<Vm>> e: hostVmMapTable.entrySet()){
			if(e.getValue().contains(vm)){
				host = e.getKey();
				break;
			}
		}	
		return(host);
	}

}
