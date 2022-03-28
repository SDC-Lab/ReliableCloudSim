package org.cloudbus.cloudsim.failure;

import java.util.ArrayList;

public class HostCounter {

	public static int currentHostCount;
	public static double clock;
	public static double previousClock;
	public static ArrayList<Integer>hostCountList = new ArrayList<Integer>();
	public static ArrayList<Double>clockList = new ArrayList<Double>();
	
	public static void setHostCount(int hostCount){		
		currentHostCount = hostCount;
		hostCountList.add(hostCount);		
	}
	
	public static int getHostCount(){
		if(hostCountList.isEmpty()){
			return 0;
		}else{
			return currentHostCount;
		}
	}
	
	public static ArrayList<Integer>getHostCountList(){
		return hostCountList;
	}
	
	public static void setClockList(double clock){		
		//previousClock = clock;
		clockList.add(clock);
	}
	
	public static double getPreviousClock(){
		if(clockList.isEmpty()){
			return 0.0;
		}
		else{
			return previousClock;
		}
	}
	
	public static ArrayList<Double>getClockList(){
		return clockList;
	}
	
}
