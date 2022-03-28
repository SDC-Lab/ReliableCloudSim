package org.cloudbus.cloudsim.failure;

import java.util.HashMap;

public class FTAMeanTimeBetweenFailure {
	public static HashMap<Integer, Double>meanTimeBetweenFailure = new HashMap<Integer, Double>();
	public static HashMap<Integer, Double>meanTimeToReturn = new HashMap<Integer, Double>();
	public static HashMap<Integer, Double>Availability = new HashMap<Integer, Double>();
	public static HashMap<Integer, Double>Maintainability = new HashMap<Integer, Double>();
	
	public static void setMTBF(int FTANodeID, double MTBF){
		meanTimeBetweenFailure.put(FTANodeID, MTBF);
	}
	
	public static double getMTBF(int FTANodeID){
		return meanTimeBetweenFailure.get(FTANodeID);		
	}
	
	public static void setMTTR(int FTANodeID, double MTTR){
		meanTimeToReturn.put(FTANodeID, MTTR);
	}
	
	public static double getMTTR(int FTANodeID){
		return meanTimeToReturn.get(FTANodeID);
	}
	
	public static void setAvailability(int FTANodeID, double Avail){
		Availability.put(FTANodeID, Avail);
	}
	
	public static double getAvailability(int FTANodeID){
		return Availability.get(FTANodeID);
	}
	
	public static void setMaintainability(int FTANodeID, double Maintain){
		Maintainability.put(FTANodeID, Maintain);
	}
	
	public static double getMaintainability(int FTANodeID){
		return Maintainability.get(FTANodeID);
	}

}
