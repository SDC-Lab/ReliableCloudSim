package org.cloudbus.cloudsim.failure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.core.CloudSim;

public class IdleTime {

	public static HashMap<Integer, HashMap<Integer, Boolean>>hostPeIdleTable = new HashMap<Integer, HashMap<Integer, Boolean>>();
	public static HashMap<Integer,HashMap<Integer, Double>>hostPeIdleClockTable = new HashMap<Integer, HashMap<Integer, Double>>();
	public static HashMap<Integer, HashMap<Integer, Double>>hostPeIdleTime = new HashMap<Integer, HashMap<Integer, Double>>();
	public static HashMap<Integer, HashMap<Integer, Double>>hostPeDownTime = new HashMap<Integer, HashMap<Integer, Double>>();
	public static HashMap<Integer, ArrayList<Boolean>>hostPeIdleStatus = new HashMap<Integer, ArrayList<Boolean>>(); 
	
	public static HashMap<Integer, Double>hostTotalIdleTime = new HashMap<Integer, Double>();
	public static HashMap<Integer, Boolean>hostStatus = new HashMap<Integer, Boolean>();
	
	
	public static HashMap<Integer, Boolean>expectedFailingHost = new HashMap<Integer, Boolean>();
	
	public static HashMap<Integer, Boolean>offHost = new HashMap<Integer, Boolean>();
	
	public static HashMap<Integer, Boolean>peMap;
	public static HashMap<Integer, Double>peClockMap;	
	public static HashMap<Integer, Double>peIdleTimeMap;
	public static HashMap<Integer, Double>peDownTime;
	public static ArrayList<Boolean>peStatusList;
	
	
	public static int index;
	public static boolean alreadyIdle = false;
	
	public static void setHostPeIdleTable(int hostID, int peID, boolean status){	
		boolean currentStatus;
		if(hostPeIdleTable.isEmpty()){
			peMap = new HashMap<Integer, Boolean>();
			peMap.put(peID, status);
			hostPeIdleTable.put(hostID, peMap);			
		}
		else{
			if(hostPeIdleTable.containsKey(hostID)){
				peMap = new HashMap<Integer, Boolean>();
				peMap = hostPeIdleTable.get(hostID);
				if(peMap.containsKey(peID)){
					currentStatus = peMap.get(peID);
					if(currentStatus == true && status == true){
						alreadyIdle = true;
					}
					else{
						peMap.put(peID, status);
						hostPeIdleTable.put(hostID, peMap);
					}
				}
				else{
					peMap.put(peID, status);
					hostPeIdleTable.put(hostID, peMap);
				}
			}
			else{
				peMap = new HashMap<Integer, Boolean>();
				peMap.put(peID, status);
				hostPeIdleTable.put(hostID, peMap);				
			}
		}
	}
	
	public static boolean getHostPeIdleTable(int hostID, int peID){
		if(hostPeIdleTable.containsKey(hostID)){
			peMap = new HashMap<Integer, Boolean>();
			peMap = hostPeIdleTable.get(hostID);
			if(peMap.containsKey(peID)){
				return peMap.get(peID);
			}
			else{
				return true;
			}
		}
		else{
			return true;
		}
	}
	
	public static void removeHostPeIdleTable(int hostID, int peID){
		peMap = new HashMap<Integer, Boolean>();
		peMap = hostPeIdleTable.get(hostID);
		peMap.remove(peID);
		hostPeIdleTable.put(hostID, peMap);		
	}
	
	public static void setHostPeIdleClockTable(int hostID, int peID, double clock){
		if(hostPeIdleClockTable.isEmpty()){			
			peClockMap = new HashMap<Integer, Double>();
			peClockMap.put(peID, clock);
			hostPeIdleClockTable.put(hostID, peClockMap);			
		}
		else{
			if(hostPeIdleClockTable.containsKey(hostID)){
				peClockMap = new HashMap<Integer, Double>();
				peClockMap = hostPeIdleClockTable.get(hostID);
				if(alreadyIdle == true){
					alreadyIdle = false;
				}
				else{
					peClockMap.put(peID, clock);			
					hostPeIdleClockTable.put(hostID, peClockMap);
				}
			}
			else{				
				peClockMap = new HashMap<Integer, Double>();
				peClockMap.put(peID, clock);
				hostPeIdleClockTable.put(hostID, peClockMap);				
			}
		}
	}
	
	public static boolean checkHostPeIdleClockTable(int hostID, int peID){
		if(hostPeIdleClockTable.containsKey(hostID)){
			peClockMap = new HashMap<Integer, Double>();
			peClockMap = hostPeIdleClockTable.get(hostID);
			if(peClockMap.containsKey(peID)){
				return true;
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}
	}
	
	public static void removeHostPeIdleClockTable(int hostID, int peID){
		peClockMap = new HashMap<Integer, Double>();
		peClockMap = hostPeIdleClockTable.get(hostID);
		peClockMap.remove(peID);
		hostPeIdleClockTable.put(hostID, peClockMap);		
	}
	
	
	public static void setHostPeIdleTimeTable(int hostID, int peID, double clock){		
		peClockMap = new HashMap<Integer, Double>();
		double idleStartTime;
		double difference;
		peClockMap = hostPeIdleClockTable.get(hostID);		
		idleStartTime = peClockMap.get(peID);
		difference = clock - idleStartTime;
		if(hostPeIdleTime.isEmpty()){
			peIdleTimeMap = new HashMap<Integer, Double>();
			peIdleTimeMap.put(peID, difference);
			hostPeIdleTime.put(hostID, peIdleTimeMap);
		}
		else{
			if(hostPeIdleTime.containsKey(hostID)){
				peIdleTimeMap = new HashMap<Integer, Double>();
				peIdleTimeMap = hostPeIdleTime.get(hostID);
				if(peIdleTimeMap.containsKey(peID)){
					double lastIdleTime;
					lastIdleTime = peIdleTimeMap.get(peID);
					lastIdleTime = lastIdleTime + difference;
					peIdleTimeMap.put(peID, lastIdleTime);
					hostPeIdleTime.put(hostID, peIdleTimeMap);
				}
				else{					
					peIdleTimeMap.put(peID, difference);
					hostPeIdleTime.put(hostID, peIdleTimeMap);
				}
			}
			else{
				peIdleTimeMap = new HashMap<Integer, Double>();
				peIdleTimeMap.put(peID, difference);
				hostPeIdleTime.put(hostID, peIdleTimeMap);
			}
		}		
	}
	
	public static double getPeIdleTime(int hostID, Integer peID){
		if(hostPeIdleTime.containsKey(hostID)){
			peIdleTimeMap = new HashMap<Integer, Double>();
			peIdleTimeMap = hostPeIdleTime.get(hostID);
			if(peIdleTimeMap.containsKey(peID)){
				return peIdleTimeMap.get(peID);
			}
			else{
				return 0.0;
			}
		}
		else{
			return 0.0;
		}
	}
	
	public static double getPeDownTime(int hostID, Integer peID){
		if(hostPeDownTime.containsKey(hostID)){
			peDownTime = new HashMap<Integer, Double>();
			peDownTime = hostPeDownTime.get(hostID);
			if(peDownTime.containsKey(peID)){
				return peDownTime.get(peID);
			}
			else{
				return 0.0;
			}
		}
		else{
			return 0.0;
		}
	}
	
	
	public static void setHostPeDownTime(int hostID, int peID, double downTime){
		if(hostPeDownTime.isEmpty()){
			peDownTime = new HashMap<Integer, Double>();
			peDownTime.put(peID, downTime);
			hostPeDownTime.put(hostID, peDownTime);
		}
		else{
			if(hostPeDownTime.containsKey(hostID)){
				peDownTime = new HashMap<Integer, Double>();
				peDownTime = hostPeDownTime.get(hostID);
				if(peDownTime.containsKey(peID)){
					double downTimePrevious;
					downTimePrevious = peDownTime.get(peID);
					downTimePrevious = downTimePrevious + downTime;
					peDownTime.put(peID, downTimePrevious);
					hostPeDownTime.put(hostID, peDownTime);
				}
				else{
					peDownTime.put(peID, downTime);
					hostPeDownTime.put(hostID, peDownTime);
				}
			}
			else{
				peDownTime = new HashMap<Integer, Double>();
				peDownTime.put(peID, downTime);
				hostPeDownTime.put(hostID, peDownTime);
			}
		}
	}
	
	public static void setHostActiveStatus(int hostID, boolean status, List<Integer>peIDList){
		boolean peStatus;
		for(int i=0; i<peIDList.size(); i++){
			peStatus = getHostPeIdleTable(hostID, peIDList.get(i));
			if(peStatus == false){
				setHostPeIdleTable(hostID, peIDList.get(i), true);
				//setHostPeIdleTimeTable(hostID, peIDList.get(i), CloudSim.clock());
			}
		}		
		hostStatus.put(hostID, status);
	}
	
	public static void setHostActiveStatus(int hostID, boolean status){		
		hostStatus.put(hostID, status);
	}
	
	public static boolean getHostActiveStatus(int hostID){
		if(!hostStatus.containsKey(hostID)){
			return false;
		}else{
			return hostStatus.get(hostID);
		}	
	}
	
	public static int getActiveHostCount(){		
		int activeCount = 0;		
		for(int i=0;i<hostStatus.size();i++){
			if(hostStatus.get(i).equals(true)){
				activeCount = activeCount + 1;
			}
		}
		return activeCount;
	}
	
	public static void setExpectedFailingHost(int hostId, boolean status){
		expectedFailingHost.put(hostId, status);
	}
	
	public static boolean checkExpectedFailingHost(int hostId){
		if(expectedFailingHost.containsKey(hostId)){
			return expectedFailingHost.get(hostId);
		}
		else{
			return false;
		}		
	}
	
	public static boolean checkHostActiveStatus(int hostId){
		if(hostStatus.containsKey(hostId)){
			return hostStatus.get(hostId);
		}
		else{
			return false;
		}		
	}	
	
}
