package org.cloudbus.cloudsim.failure;

import java.util.HashMap;

import org.cloudbus.cloudsim.Host;

public class IdleHostTime {

	public static HashMap<Host, Double>hostDeallocationTime = new HashMap<Host, Double>();
	public static HashMap<Host, Boolean>hostDeallocationFlag = new HashMap<Host, Boolean>();
	public static HashMap<Host, Double>hostAllocationTime = new HashMap<Host, Double>();
	public static HashMap<Host, Double>hostIdleTime = new HashMap<Host, Double>();
	public static HashMap<Host, Boolean>failInjectionFlag = new HashMap<Host, Boolean>();
	//public static boolean flag;
	//public static double time;
	
	public static void setHostDeallocationTime(Host host, double time){
		hostDeallocationTime.put(host, time);		
	}
	
	public static void setHostDeallocationFlag(Host host, boolean flag){
		hostDeallocationFlag.put(host, flag);
	}
	
	public static double getHostDeallocationTime(Host host){
		double time = 0;
		if(hostDeallocationTime.containsKey(host)){
			time = hostDeallocationTime.get(host);
		}
		return time;
	}
	
	public static boolean getHostDeallocationFlag(Host host){		
		boolean flag = false;
		if(hostDeallocationFlag.containsKey(host)){			
			flag = hostDeallocationFlag.get(host);
		}
		return flag;
	}
	
	public static void setHostAllocationTime(Host host, double time){
		hostAllocationTime.put(host,  time);
	}
	
	public static double getHostAllocationTime(Host host){
		double time = 0;
		if(hostAllocationTime.containsKey(host)){
			time = hostAllocationTime.get(host);
		}
		return time;
	}
	
	public static void setHostTotalIdleTime(Host host, double time){
		double previousIdleTime;
		if(hostIdleTime.containsKey(host)){
			previousIdleTime = hostIdleTime.get(host);
			//System.out.println("@@@@@@@@@@@@@@@ So far total idle time for host #" +host.getId()+" is " +previousIdleTime);
			previousIdleTime = previousIdleTime + time;
			hostIdleTime.put(host, previousIdleTime);
			//System.out.println("$$$$$$$$$$$$$$$ New total idle time for host #" +host.getId()+" is " +previousIdleTime);
		}
		else{
			hostIdleTime.put(host, time);
			//System.out.println("@@@@@@@@@@@@@@@ So far total idle time for host #" +host.getId()+" is " +0);
			//System.out.println("$$$$$$$$$$$$$$$ New total idle time for host #" +host.getId()+" is " +time);
		}
	}
	
	public static void setHostIdleTime(Host host){
		double allocationTime = getHostAllocationTime(host);
		double deallocationTime = getHostDeallocationTime(host);
		double idleTime = allocationTime - deallocationTime;
		//System.out.println("%%%%%%%%%%%%%%%%%%% Current Idle time for host #" +host.getId()+ " is " +idleTime);
		setHostTotalIdleTime(host, idleTime);		
	}
	
	public static double getHostTotalIdleTime(Host host){
		double totalIdleTime = 0;
		if(hostIdleTime.containsKey(host)){
			totalIdleTime = hostIdleTime.get(host);
		}
		return totalIdleTime;
	}
	
	/**
	 * This flag is being operated from the allocation policy and is being used by the processVmCreate(Vm) function in the data center
	 * By using this function, re-injection of failures has been simulated in the case of allocation of VM to a provisioned node which was sitting Idle. 
	 */
	public static void setFailInjectionFlag(Host host, boolean flagFail){
		failInjectionFlag.put(host, flagFail);
	}
	
	public static boolean getFailInjectionFlag(Host host){
		boolean failFlag = false;
		if(failInjectionFlag.containsKey(host)){
			failFlag = failInjectionFlag.get(host);
			//return failFlag;
		}
		return failFlag;
	}
	
	
}
