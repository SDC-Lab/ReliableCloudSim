package org.cloudbus.cloudsim.failure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;

/**
 * This class maintains a table with mapping information between host IDs
 * retrieved from failure traces and host IDs generated by CloudSim.
 *  
 * @author Yogesh Sharma
 * @author Bahman Javadi
 * @since CloudSimSDN 1.0
 */

public class HostMapping {
	
	public static HashMap<Integer, Integer> hostMap= new HashMap<Integer, Integer>();
	
	public static void createHostMap(int hostID, int FTAhostID){
		hostMap.put(hostID, FTAhostID);
	}
	
	public static int getFTAhostID(int hostID){
		int FTAhostID = 0;
		if(hostMap.containsKey(hostID)){
			FTAhostID = hostMap.get(hostID);
		}
		return(FTAhostID);
	}
	
	public static int getHostID(int FTAhostID){
		int hostID = 0;
		for(Map.Entry<Integer, Integer> e: hostMap.entrySet()){
			
			if(FTAhostID == e.getValue()){
				hostID = e.getKey();
				break;
			}
		}
		return(hostID);
	}
	
	public static int getHostMapSize(){
		return hostMap.size();
	}

}
