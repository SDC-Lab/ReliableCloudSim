package org.cloudbus.cloudsim.failure;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Pe;

public class PeListforFailures {
	
	public static List<List<Pe>> peList = new ArrayList<List<Pe>>();
	
	public static void setPeList(List<Pe> peList2){
		peList.add(peList2);
	} 
	
	public static List<Pe> getPeList(int index){
		return peList.get(index);
	}
	
	public static int getPeListSize(){
		return peList.size();
	}

}
