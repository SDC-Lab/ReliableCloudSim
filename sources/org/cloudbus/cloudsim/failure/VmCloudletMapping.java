package org.cloudbus.cloudsim.failure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

public class VmCloudletMapping {

public static HashMap<Vm, ArrayList<Cloudlet>> vmcletmapping= new HashMap<Vm, ArrayList<Cloudlet>>();

public static HashMap<Integer, ArrayList<Integer>> vmcletmappingIDs = new HashMap<Integer, ArrayList<Integer>>();

public static ArrayList<Cloudlet> value;

public static ArrayList<Integer>cletIDs;

public static Vm vm;
	
	public static void createVmCloudletMap(Vm vm, ArrayList<Cloudlet>cletList){
		vmcletmapping.put(vm, cletList);
		cletIDs = new ArrayList<Integer>();
		for(int i=0;i<cletList.size();i++){
			cletIDs.add(cletList.get(i).getCloudletId());
		}
		createVmCloudletIDsMap(vm.getId(), cletIDs);
	}
	
	public static void createVmCloudletIDsMap(Integer vmID, ArrayList<Integer>cletIDs){		
		vmcletmappingIDs.put(vmID, cletIDs);
	}
	
	public static HashMap<Integer, ArrayList<Integer>> getVmCloudletIDsMap(){
		return vmcletmappingIDs;
	}
//	public static HashMap<Vm, ArrayList<Cloudlet>> 
	
	public static ArrayList<Cloudlet> getCloudlet(Vm vm){
		ArrayList<Cloudlet> clet = new ArrayList<Cloudlet>();
		if(vmcletmapping.containsKey(vm)){
			clet = vmcletmapping.get(vm);
		}
		return(clet);
	}
	
	public static Vm getVm(Cloudlet clet){
		boolean flag = false;
		for(Map.Entry<Vm, ArrayList<Cloudlet>> e: vmcletmapping.entrySet()){
			value=e.getValue();
			for(int i=0;i<value.size();i++){
				if(value.get(i) == clet){
					vm = e.getKey();
					flag = true;
					break;
				}
			}
			if(flag == true){
				flag = false;
				break;
			}
			//if(value == clet){
			//	vm = e.getKey();
			//	break;
			//}
		}		
		return(vm);
	}
	
	public static Long getLongestTaskFromAvailableVMs(Vm vmTemp){	
		ArrayList<Cloudlet>cletListTemp;	
			cletListTemp = new ArrayList<Cloudlet>();
			cletListTemp.addAll(getCloudlet(vmTemp));
			long tempCletLength = 0;			
			for(int j=0; j<cletListTemp.size(); j++) {
				if(tempCletLength < cletListTemp.get(j).getCloudletLength()) {
					tempCletLength = cletListTemp.get(j).getCloudletLength();						
				}
			}	
		return tempCletLength;
	}
	
	public static void removeVm(Vm vm){
		vmcletmapping.remove(vm);
	}
}
