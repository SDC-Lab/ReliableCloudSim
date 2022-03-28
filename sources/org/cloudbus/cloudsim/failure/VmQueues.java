package org.cloudbus.cloudsim.failure;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class VmQueues {
	public HashMap<Integer, Queue<Integer>>vmQueueMap = new HashMap<Integer, Queue<Integer>>();
	public Queue<Integer>vmQueue; 
	
	public void addToQueue(int vmID, int cletID){
		if(vmQueueMap.isEmpty()){
			vmQueue = new LinkedList<Integer>();
			vmQueue.offer(cletID);
			vmQueueMap.put(vmID, vmQueue);
		}
		else{
			if(vmQueueMap.containsKey(vmID)){
				vmQueue = new LinkedList<Integer>();
				for(int i=0;i<vmQueueMap.get(vmID).size();i++){
					vmQueue.offer(vmQueueMap.get(vmID).poll());
				}
				vmQueueMap.put(vmID, vmQueue);
			}
			else{
				vmQueue = new LinkedList<Integer>();
				vmQueue.offer(cletID);
				vmQueueMap.put(vmID, vmQueue);
			}
		}
	}
	
	public Integer getFromQueue(int vmID){
		int cletID;
		cletID = vmQueueMap.get(vmID).poll();
		return cletID;
	}
	
	 
}
