package org.cloudbus.cloudsim.frequency;

import java.util.ArrayList;

public class FirstLotofVMs {
	public ArrayList<Integer>vmIDsList = new ArrayList<Integer>();;
	
	public void addVMID(int vmID) {
		vmIDsList.add(vmID);
	}
	
	public boolean containsVMID(int vmID) {
		if(vmIDsList.contains(vmID)) {
			return true;
		}
		return false;
	}

}
