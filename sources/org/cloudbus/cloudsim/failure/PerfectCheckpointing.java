package org.cloudbus.cloudsim.failure;

import java.util.HashMap;

import org.cloudbus.cloudsim.Cloudlet;

public class PerfectCheckpointing {
	
public HashMap<Integer, Double>checkpointTable = new HashMap<Integer, Double>();
public HashMap<Integer, Double>recoveryTimeTable = new HashMap<Integer, Double>();
	
public void setCheckpoints(int cletID, Double checkpointTime){
	checkpointTable.put(cletID, checkpointTime);	
}

public Double getCheckpoints(int cletID){			
		return (checkpointTable.get(cletID));	
}	

public void setRecoveryTime(Integer cletID, Double recoveryTime){
	recoveryTimeTable.put(cletID, recoveryTime);
}

public Double getRecoveryTime(Integer cletID){
	return(recoveryTimeTable.get(cletID));
}
	
}
