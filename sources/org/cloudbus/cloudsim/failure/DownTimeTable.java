package org.cloudbus.cloudsim.failure;

import java.util.ArrayList;
import java.util.HashMap;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

public class DownTimeTable {

	public static HashMap<Vm, Double>downTimeTable = new HashMap<Vm, Double>();
	
	public static HashMap<Vm, Double>failTimeTable = new HashMap<Vm, Double>();
	
	public static HashMap<Integer, Double>cletFailTimeTable = new HashMap<Integer, Double>();
	
	public static HashMap<Integer, Double>cletDownTimeTable = new HashMap<Integer, Double>();
	
	public static HashMap<Integer, Double>cletDownTimeTableforMigration = new HashMap<Integer, Double>();
	
	public static HashMap<Integer, HashMap<Integer, Double>>cletPerHostDownTimeTable = new HashMap<Integer, HashMap<Integer, Double>>();
	
	public static HashMap<Integer, Double>hostDownTimeTable;
	
	public static ArrayList<Integer>cletList;
	
	public static void setCletPerHostDownTimeTable(int cletID, int hostID, double downTime){
		double downTimeTemp;
		if(cletPerHostDownTimeTable.isEmpty()){
			hostDownTimeTable = new HashMap<Integer, Double>();
			hostDownTimeTable.put(hostID, downTime);
			cletPerHostDownTimeTable.put(cletID, hostDownTimeTable);
		}
		else{
			if(cletPerHostDownTimeTable.containsKey(cletID)){
				hostDownTimeTable = new HashMap<Integer, Double>();
				hostDownTimeTable = cletPerHostDownTimeTable.get(cletID);
				if(hostDownTimeTable.containsKey(hostID)){
					downTimeTemp = hostDownTimeTable.get(hostID);
					downTimeTemp = downTimeTemp + downTime;
					hostDownTimeTable.put(hostID, downTimeTemp);
				}
				else{
					hostDownTimeTable.put(hostID, downTime);
				}
				cletPerHostDownTimeTable.put(cletID, hostDownTimeTable);
			}
			else{
				hostDownTimeTable = new HashMap<Integer, Double>();
				hostDownTimeTable.put(hostID, downTime);
				cletPerHostDownTimeTable.put(cletID, hostDownTimeTable);
			}
		}
	}
	
	public static double getCletPerHostDownTimeTable(int cletID, int hostID){
		hostDownTimeTable = new HashMap<Integer, Double>();		
		if(cletPerHostDownTimeTable.containsKey(cletID)){
			hostDownTimeTable = cletPerHostDownTimeTable.get(cletID);
			if(hostDownTimeTable.containsKey(hostID)){
				return hostDownTimeTable.get(hostID);
			}	
			else{
				return 0.0;
			}
		}
		else{
			return 0.0;
		}
		
	}
	
	public  static void setFailTimeTable(Vm vm, Double fail_time){
		
		if(failTimeTable.isEmpty()){
			failTimeTable.put(vm, fail_time);
		}
		else{
				failTimeTable.put(vm, fail_time);
			}
	}
	
	
	
	public static void setCletFailTimeTable(int cletID, double fail_time){		
			cletFailTimeTable.put(cletID, fail_time);		
	}
	
	public static double getCletFailTimeTable(int cletID){
		if(cletFailTimeTable.containsKey(cletID)){
			return cletFailTimeTable.get(cletID);
		}
		else{
			return 0.0;
		}
	}
	
	public  static void setDownTimeTable(Vm vm, Double down_time){
		
		if(downTimeTable.isEmpty()){
			downTimeTable.put(vm, down_time);
			//setCloudletDownTime(vm, down_time);
		}
		else{
			if(downTimeTable.containsKey(vm)){
				double downTime;
				downTime = down_time;
				downTime = downTimeTable.get(vm) + downTime;
				downTimeTable.put(vm, downTime);
			//	setCloudletDownTime(vm, down_time);
			}
			else{
				downTimeTable.put(vm, down_time);
			//	setCloudletDownTime(vm, down_time);
			}
		}
		
	}
	
	public static double getFailTimeTable(Vm vm){
		if(failTimeTable.containsKey(vm)){
			return failTimeTable.get(vm);
		}
		else{
			return 0.0;
		}
		
	}
	
	public static double getDownTimeTable(Vm vm){
		if(downTimeTable.containsKey(vm)){
			return downTimeTable.get(vm);
		}
		else{
			return 0.0;
		}
	}
	
	//public static void setCloudletDownTime(Vm vm, double down_Time){
	public static void setCloudletDownTime(int cletID, double down_time){	
		
		if(cletDownTimeTable.isEmpty()){
			cletDownTimeTable.put(cletID, down_time);			
		}
		else{
			if(cletDownTimeTable.containsKey(cletID)){
				double downTime;
				downTime = cletDownTimeTable.get(cletID);
				downTime =  downTime + down_time;
				cletDownTimeTable.put(cletID, downTime);				
			}
			else{
				cletDownTimeTable.put(cletID, down_time);				
			}
		}
	}
		
		public static void setCloudletDownTimeforMigration(int cletID, double down_time){	
			
			if(cletDownTimeTableforMigration.isEmpty()){
				cletDownTimeTableforMigration.put(cletID, down_time);			
			}
			else{
				if(cletDownTimeTableforMigration.containsKey(cletID)){
					double downTime;
					downTime = cletDownTimeTableforMigration.get(cletID);
					downTime =  downTime + down_time;
					cletDownTimeTableforMigration.put(cletID, downTime);				
				}
				else{
					cletDownTimeTableforMigration.put(cletID, down_time);				
				}
			}
		}		
	
	public static double getCloudletDownTime(int cletID){
		if(cletDownTimeTable.containsKey(cletID)){
			return cletDownTimeTable.get(cletID);
		}
		else{
			return 0.0;
		}		
	}
	
	public static double getCloudletDownTimeforMigration(int cletID){
		if(cletDownTimeTableforMigration.containsKey(cletID)){
			return cletDownTimeTableforMigration.get(cletID);
		}
		else{
			return 0.0;
		}
	}
	
}
