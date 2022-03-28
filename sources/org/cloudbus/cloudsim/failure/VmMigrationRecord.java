package org.cloudbus.cloudsim.failure;

import java.util.ArrayList;
import java.util.HashMap;

public class VmMigrationRecord {

	public HashMap<Integer, ArrayList<Integer>> cletMigrationHostRecord= new HashMap<Integer, ArrayList<Integer>>();
	public HashMap<Integer, ArrayList<Double>> cletExecutionDurationPerHostRecord = new HashMap<Integer, ArrayList<Double>>();
	
	// These tables are only to get the migration down time for a vm
	public HashMap<Integer, ArrayList<Double>> cletMigrationDownTime = new HashMap<Integer, ArrayList<Double>>();
	public HashMap<Integer, ArrayList<Integer>> cletMigrationHostRecordforDownTime = new HashMap<Integer, ArrayList<Integer>>();
	
	public HashMap<Integer, ArrayList<Double>> cletMigrationOverhead = new HashMap<Integer, ArrayList<Double>>();
	
	public HashMap<Integer, Integer> vmCurrentHost = new HashMap<Integer, Integer>();
	
	public ArrayList<Integer>hostList;
	public ArrayList<Double>executionDurationList;
	public boolean hostContainFlag;	
	public int index;
	
	public ArrayList<Integer>downTimeHostList;
	public ArrayList<Double>downTimeList;
	public boolean hostContainFlagforDownTime;
	public int indexforDownTime;
	
	public ArrayList<Double>migrationOverheadList;
	/**
	 * In this function, information about the host from which the migration
	 * is happening is getting stored.
	 * @param cletID: Migrating cloudlet ID
	 * @param hostID: Current host on which the cloudlet is running before migration
	 * 
	 */
	
	public void setCletMigrationHostRecord(int cletID, int hostID){		
		hostContainFlag = false;
		if(cletMigrationHostRecord.isEmpty()){
			hostList = new ArrayList<Integer>();
			hostList.add(hostID);
			cletMigrationHostRecord.put(cletID, hostList);
		}
		else{
			if(cletMigrationHostRecord.containsKey(cletID)){
				hostList = new ArrayList<Integer>();
				hostList = cletMigrationHostRecord.get(cletID);				
				for(int i=0; i<hostList.size(); i++){
					if(hostList.get(i).equals(hostID)){
						index = i;
						hostContainFlag = true;
						break;
					}					
				}	
				if(hostContainFlag == false){
					hostList.add(hostID);
					cletMigrationHostRecord.put(cletID, hostList);					
				}
			}
			else{
				hostList = new ArrayList<Integer>();
				hostList.add(hostID);
				cletMigrationHostRecord.put(cletID, hostList);
			}
		}
	}	
	
	
	public void setVmExecutionDurationPerHostRecord(int cletID, double soFarExecution){		
		if(cletExecutionDurationPerHostRecord.isEmpty()){
			executionDurationList = new ArrayList<Double>();
			executionDurationList.add(soFarExecution);
			System.out.println("Current soFarExecution of Cloudlet "+cletID+ " is " +soFarExecution);
			cletExecutionDurationPerHostRecord.put(cletID, executionDurationList);
		}
		else{
			if(cletExecutionDurationPerHostRecord.containsKey(cletID)){
				executionDurationList = new ArrayList<Double>();
				executionDurationList = cletExecutionDurationPerHostRecord.get(cletID);
				if(hostContainFlag == true){
					double soFarExecutionPrevious = 0.0;
					soFarExecutionPrevious = executionDurationList.get(index);
					System.out.println("Previous soFarExecution of Cloudlet "+cletID+ " is " +soFarExecutionPrevious);
					System.out.println("Current soFarExecution of Cloudlet "+cletID+ " is " +soFarExecution);
					soFarExecution = soFarExecutionPrevious + soFarExecution;
					System.out.println("New soFarExecution of Cloudlet "+cletID+ " is " +soFarExecution);
					executionDurationList.remove(index);
					executionDurationList.add(index, soFarExecution);
					cletExecutionDurationPerHostRecord.put(cletID, executionDurationList);
				//	hostContainFlag = false;
				}
				else{
					executionDurationList.add(soFarExecution);
					System.out.println("Current soFarExecution of Cloudlet "+cletID+ " is " +soFarExecution);
					cletExecutionDurationPerHostRecord.put(cletID, executionDurationList);
				}
			}
			else{
				executionDurationList = new ArrayList<Double>();
				executionDurationList.add(soFarExecution);
				System.out.println("Current soFarExecution of Cloudlet "+cletID+ " is " +soFarExecution);
				cletExecutionDurationPerHostRecord.put(cletID, executionDurationList);
			}
		}
	}
	
	public void setCletMigrationHostRecordforDownTime(int cletID, int hostID){
		hostContainFlagforDownTime = false;
		if(cletMigrationHostRecordforDownTime.isEmpty()){
			downTimeHostList = new ArrayList<Integer>();
			downTimeHostList.add(hostID);
			cletMigrationHostRecordforDownTime.put(cletID, downTimeHostList);
		}
		else{
			if(cletMigrationHostRecordforDownTime.containsKey(cletID)){
				downTimeHostList = new ArrayList<Integer>();
				downTimeHostList = cletMigrationHostRecordforDownTime.get(cletID);				
				for(int i=0; i<downTimeHostList.size(); i++){
					if(downTimeHostList.get(i).equals(hostID)){
						indexforDownTime = i;
						hostContainFlagforDownTime = true;
						break;
					}					
				}	
				if(hostContainFlagforDownTime == false){
					downTimeHostList.add(hostID);
					cletMigrationHostRecordforDownTime.put(cletID, downTimeHostList);					
				}
			}
			else{
				downTimeHostList = new ArrayList<Integer>();
				downTimeHostList.add(hostID);
				cletMigrationHostRecordforDownTime.put(cletID, downTimeHostList);
			}
		}
	}	
	
	
	public void setCletMigrationDownTime(int cletID, double downTime){		
		if(cletMigrationDownTime.isEmpty()){
			downTimeList = new ArrayList<Double>();
			downTimeList.add(downTime);
			System.out.println("Current migration downtime for clet "+cletID+ " is " +downTime);
			cletMigrationDownTime.put(cletID, downTimeList);
		}
		else{
			if(cletMigrationDownTime.containsKey(cletID)){
				downTimeList = new ArrayList<Double>();
				downTimeList = cletMigrationDownTime.get(cletID);
				if(hostContainFlagforDownTime == true){
					double downTimeSoFar = 0.0;
					downTimeSoFar = downTimeList.get(indexforDownTime);
					System.out.println("So far migration downTime for clet "+cletID+ " is " +downTimeSoFar);					
					downTimeSoFar = downTimeSoFar + downTime;
					System.out.println("New so far migration downTime for clet "+cletID+ " is " +downTimeSoFar);
					downTimeList.remove(indexforDownTime);
					downTimeList.add(indexforDownTime, downTimeSoFar);
					cletMigrationDownTime.put(cletID, downTimeList);
				//	hostContainFlag = false;
				}
				else{
					downTimeList.add(downTime);
					System.out.println("So far migration downTime for clet "+cletID+ " is " +downTime);
					cletMigrationDownTime.put(cletID, downTimeList);
				}
			}
			else{
				downTimeList = new ArrayList<Double>();
				downTimeList.add(downTime);
				System.out.println("So far migration downTime for clet "+cletID+ " is " +downTime);
				cletMigrationDownTime.put(cletID, downTimeList);
			}
		}
	}
	
	public void setCletMigrationOverhead(int cletID, double overheadTime){		
		if(cletMigrationOverhead.isEmpty()){
			migrationOverheadList = new ArrayList<Double>();
			migrationOverheadList.add(overheadTime);
			System.out.println("Current migration overhead time for clet "+cletID+ " is " +overheadTime);
			cletMigrationOverhead.put(cletID, migrationOverheadList);
		}
		else{
			if(cletMigrationOverhead.containsKey(cletID)){
				migrationOverheadList = new ArrayList<Double>();
				migrationOverheadList = cletMigrationOverhead.get(cletID);
				if(hostContainFlagforDownTime == true){
					double overheadTimeSoFar = 0.0;
					overheadTimeSoFar = migrationOverheadList.get(indexforDownTime);
					System.out.println("So far migration overhead time for clet "+cletID+ " is " +overheadTimeSoFar);					
					overheadTimeSoFar = overheadTimeSoFar + overheadTime;
					System.out.println("New so far migration overhead time for clet "+cletID+ " is " +overheadTimeSoFar);
					migrationOverheadList.remove(indexforDownTime);
					migrationOverheadList.add(indexforDownTime, overheadTimeSoFar);
					cletMigrationOverhead.put(cletID, migrationOverheadList);
				//	hostContainFlag = false;
				}
				else{
					migrationOverheadList.add(overheadTime);
					System.out.println("So far migration overhead time for clet "+cletID+ " is " +overheadTime);
					cletMigrationOverhead.put(cletID, migrationOverheadList);
				}
			}
			else{
				migrationOverheadList = new ArrayList<Double>();
				migrationOverheadList.add(overheadTime);
				System.out.println("So far migration overhead time for clet "+cletID+ " is " +overheadTime);
				cletMigrationOverhead.put(cletID, migrationOverheadList);
			}
		}
	}
	
	
	public int getLastHostforClet(int cletID){
		int hostID;
		int size;		
		size = cletMigrationHostRecord.get(cletID).size();
		hostID = cletMigrationHostRecord.get(cletID).get(size-1);
		return hostID;
		
	}
	
	public void setVmCurrentHost(int vmId, int hostId){
		vmCurrentHost.put(vmId, hostId);
	}
	
	public int getVmCurrentHost(int vmId){
		return vmCurrentHost.get(vmId);
	}
	
	public ArrayList<Integer>getVmMigrationHostList(int cletID){
		return cletMigrationHostRecord.get(cletID);
	}
	
	public ArrayList<Integer>getCletMigrationHostRecordforDownTime(int cletID){
		return cletMigrationHostRecordforDownTime.get(cletID);
	}
	
	public ArrayList<Double>getVmExecutionDurationPerHostList(int cletID){
		return cletExecutionDurationPerHostRecord.get(cletID);
	}
	
	public ArrayList<Double>getVmMigrationDownTimePerHostList(int cletID){
		return cletMigrationDownTime.get(cletID);
	}
	
	public ArrayList<Double>getVmMigrationOverheadPerHostList(int cletID){
		return cletMigrationOverhead.get(cletID);
	}
	
}
