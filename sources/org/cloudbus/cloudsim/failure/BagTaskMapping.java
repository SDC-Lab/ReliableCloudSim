package org.cloudbus.cloudsim.failure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;


public class BagTaskMapping {
	
	public static HashMap<Integer, ArrayList<Integer>>BagTaskMap= new HashMap<Integer, ArrayList<Integer>>();
	
	public static HashMap<Integer, Long>MaxTaskPerBag = new HashMap<Integer, Long>(); 
	
	public static ArrayList<Integer> taskList;		
		
	public static void setBagTaskMap(int bagID, int taskID){		
		if(BagTaskMap.isEmpty()){
			taskList = new ArrayList<Integer>();
			taskList.add(taskID);
			BagTaskMap.put(bagID, taskList);
		}
		else{
			if(BagTaskMap.containsKey(bagID)){
				taskList = new ArrayList<Integer>();
				taskList = BagTaskMap.get(bagID);
				taskList.add(taskID);
				BagTaskMap.put(bagID, taskList);
			}
			else{
				taskList = new ArrayList<Integer>();
				taskList.add(taskID);
				BagTaskMap.put(bagID, taskList);
			}
		}
	}
	
	public static void setMaxTaskPerBag(int bagID, long maxLength){
		MaxTaskPerBag.put(bagID, maxLength);
	}
	
	public static ArrayList<Integer>getBagTaskMap(int bagID){
		return BagTaskMap.get(bagID);
	}
	
	public static int getBagID(int cletID){
		int bagID = 0;
		for(Map.Entry<Integer, ArrayList<Integer>> e: BagTaskMap.entrySet()){
			if(e.getValue().contains(cletID)){
				bagID = e.getKey();
				break;
			}
		}	
		return(bagID);		
		
	}
	
	public static Long getMaxTask(int bagID){
		return MaxTaskPerBag.get(bagID);
	}

}
