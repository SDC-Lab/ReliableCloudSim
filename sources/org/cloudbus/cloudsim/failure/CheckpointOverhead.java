package org.cloudbus.cloudsim.failure;
import java.util.ArrayList;
import java.util.HashMap;
import org.cloudbus.cloudsim.*;

public class CheckpointOverhead {
	//public static HashMap<Vm,Double> checkpointOverheadTable= new HashMap<Vm,Double>();
	public static HashMap<Integer,Double> checkpointOverheadTable= new HashMap<Integer,Double>();
	public static HashMap<Integer, HashMap<Integer,Double>>checkpointOverheadPerHostTable = new HashMap<Integer, HashMap<Integer,Double>>();
	public static HashMap<Integer, Double>hostChkptOverheadTable; 
	
	public static void setCheckpointOverheadTable(Integer cletID, Double time){
		double totalTime;	
		if(checkpointOverheadTable.isEmpty()){
			//utilizationList = new ArrayList<Double>();
			checkpointOverheadTable.put(cletID, time);
		}
		else{
			if(checkpointOverheadTable.containsKey(cletID)){
				totalTime = checkpointOverheadTable.get(cletID);
				totalTime = totalTime + time;
				checkpointOverheadTable.put(cletID, totalTime);
			}		
			else{
				checkpointOverheadTable.put(cletID, time);
			}
		}
	}
	
	public static double getCheckpointOverheadTable(Integer cletID){
		return checkpointOverheadTable.get(cletID);
	}	
	
	public static void setCheckpointOverheadTablePerHost(int cletID, int hostID, double overheads){
		hostChkptOverheadTable = new HashMap<Integer, Double>();
		double oldOverheads = 0;
		if(checkpointOverheadPerHostTable.isEmpty()){
			hostChkptOverheadTable.put(hostID, overheads);
			checkpointOverheadPerHostTable.put(cletID, hostChkptOverheadTable);
		}
		else{
			if(checkpointOverheadPerHostTable.containsKey(cletID)){
				hostChkptOverheadTable = checkpointOverheadPerHostTable.get(cletID);
				if(hostChkptOverheadTable.containsKey(hostID)){
					oldOverheads = hostChkptOverheadTable.get(hostID);
					oldOverheads = oldOverheads + overheads;
					hostChkptOverheadTable.put(hostID, oldOverheads);
				}
				else{
					hostChkptOverheadTable.put(hostID, overheads);
				}
				checkpointOverheadPerHostTable.put(cletID, hostChkptOverheadTable);
			}
			else{
				hostChkptOverheadTable.put(hostID, overheads);
				checkpointOverheadPerHostTable.put(cletID, hostChkptOverheadTable);
			}
		}	
	}
	
	public static double getCheckpointOverheadPerHostTable(int cletID, int hostID){
		hostChkptOverheadTable = new HashMap<Integer, Double>();
		if(checkpointOverheadPerHostTable.containsKey(cletID)){
			hostChkptOverheadTable = checkpointOverheadPerHostTable.get(cletID);
			if(hostChkptOverheadTable.containsKey(hostID)){
				return hostChkptOverheadTable.get(hostID);				
			}
			else{
				return 0.0;
			}
		}
		else{
			return 0.0;
		}
	} 

}
