package org.cloudbus.cloudsim.failure;

//import org.cloudbus.cloudsim.*;
import java.util.HashMap;

public class CloudletReexecutionPart {
	
	public static HashMap<Integer, Long> cletReexecutionTime = new HashMap<Integer, Long>();
	public static HashMap<Integer, Long>hostReexecutionTimeTable;
	public static HashMap<Integer, HashMap<Integer, Long>> cletPerHostReexecutionTimeTable = new HashMap<Integer, HashMap<Integer, Long>>();
	
	
	public static void setCletPerHostReexecutionTimeTable(int cletID, int hostID, long reexecutionTime){
		long reexecutionTimeTemp;
		if(cletPerHostReexecutionTimeTable.isEmpty()){
			hostReexecutionTimeTable = new HashMap<Integer, Long>();
			hostReexecutionTimeTable.put(hostID, reexecutionTime);
			cletPerHostReexecutionTimeTable.put(cletID, hostReexecutionTimeTable);
		}
		else{
			if(cletPerHostReexecutionTimeTable.containsKey(cletID)){
				hostReexecutionTimeTable = new HashMap<Integer, Long>();
				hostReexecutionTimeTable = cletPerHostReexecutionTimeTable.get(cletID);
				if(hostReexecutionTimeTable.containsKey(hostID)){
					reexecutionTimeTemp = hostReexecutionTimeTable.get(hostID);
					reexecutionTimeTemp = reexecutionTimeTemp + reexecutionTime;
					hostReexecutionTimeTable.put(hostID, reexecutionTimeTemp);
				}
				else{
					hostReexecutionTimeTable.put(hostID, reexecutionTime);
				}
				cletPerHostReexecutionTimeTable.put(cletID, hostReexecutionTimeTable);
			}
			else{
				hostReexecutionTimeTable = new HashMap<Integer, Long>();
				hostReexecutionTimeTable.put(hostID, reexecutionTime);
				cletPerHostReexecutionTimeTable.put(cletID, hostReexecutionTimeTable);
			}
		}
	}
	
	public static double getCletPerHostReexecutionTimeTable(int cletID, int hostID){
		hostReexecutionTimeTable = new HashMap<Integer, Long>();		
		if(cletPerHostReexecutionTimeTable.containsKey(cletID)){
			hostReexecutionTimeTable = cletPerHostReexecutionTimeTable.get(cletID);
			if(hostReexecutionTimeTable.containsKey(hostID)){
				return hostReexecutionTimeTable.get(hostID);
			}	
			else{
				return 0.0;
			}
		}
		else{
			return 0.0;
		}
		
	}
	
	public static void setCletReexecutionTime(int cletID, Long time){
		long timeTemp;
		if(cletReexecutionTime.isEmpty()){
			cletReexecutionTime.put(cletID, time);
		}
		else{
			if(cletReexecutionTime.containsKey(cletID)){
				timeTemp = cletReexecutionTime.get(cletID);
				timeTemp = timeTemp + time;
				cletReexecutionTime.put(cletID, timeTemp);
			}
			else{
				cletReexecutionTime.put(cletID, time);
			}
		}
	}
	
	public static long getCletReexecutionTime(int cletID){
		if(cletReexecutionTime.get(cletID)== null){
			return 0;
		}
		else{
			return cletReexecutionTime.get(cletID);
		}
	}
	

}
