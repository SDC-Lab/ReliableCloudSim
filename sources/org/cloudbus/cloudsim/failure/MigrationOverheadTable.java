package org.cloudbus.cloudsim.failure;

import java.util.HashMap;

import org.cloudbus.cloudsim.Cloudlet;

public class MigrationOverheadTable {
	
public static HashMap<Integer, Double> cletMigrationOverhead = new HashMap<Integer, Double>();

public static HashMap<Integer, Double>cletMigrationMiscellaneousOverhead = new HashMap<Integer, Double>();
	
	public static void setCletMigrationOverhead(int cletID, double time){
		double timeTemp;
		if(cletMigrationOverhead.isEmpty()){
			cletMigrationOverhead.put(cletID, time);
		}
		else{
			if(cletMigrationOverhead.containsKey(cletID)){
				timeTemp = cletMigrationOverhead.get(cletID);
				timeTemp = timeTemp + time;
				cletMigrationOverhead.put(cletID, timeTemp);
			}
			else{
				cletMigrationOverhead.put(cletID, time);
			}
		}
	}
	
	public static void setCletMigrationMiscellaneousOverhead(int cletID, double time){
		double timeTemp;
		if(cletMigrationMiscellaneousOverhead.isEmpty()){
			cletMigrationMiscellaneousOverhead.put(cletID, time);
		}
		else{
			if(cletMigrationMiscellaneousOverhead.containsKey(cletID)){
				timeTemp = cletMigrationMiscellaneousOverhead.get(cletID);
				timeTemp = timeTemp + time;
				cletMigrationMiscellaneousOverhead.put(cletID, timeTemp);
			}
			else{
				cletMigrationMiscellaneousOverhead.put(cletID, time);
			}
		}
	}
	
	
	
	public static double getCletMigrationOverhead(int cletID){
		if(cletMigrationOverhead.containsKey(cletID)){
			return cletMigrationOverhead.get(cletID);
		}
		else{
			return 0;
		}
	}
	
	public static double getCletMigrationMiscellaneousOverhead(int cletID){
		if(cletMigrationMiscellaneousOverhead.containsKey(cletID)){
			return cletMigrationMiscellaneousOverhead.get(cletID);
		}
		else{
			return 0;
		}
	}
	
	
}
