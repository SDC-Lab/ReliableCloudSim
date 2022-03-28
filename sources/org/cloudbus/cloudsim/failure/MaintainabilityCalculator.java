package org.cloudbus.cloudsim.failure;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;

public class MaintainabilityCalculator {
	
	public ArrayList<Cloudlet>cletList;
	
public double maintainability;
	
	DecimalFormat dft = new DecimalFormat("###.####");
	
	FileWriter writeMaintainability; 
	
	FileWriter vmIDsForMaintainability;
	
	FileWriter hostIDsForMaintainability;
	
	//FTAFileReader fta = new FTAFileReader();
	
	FTAFileReaderGrid5000 fta;
	
	//try{
		
	//}catch(IOException e) {
		
		//e.printStackTrace();		
	//}
	
	//writeReliability = new FileWriter("C:\\Data\\Australia\\Publication_Files\\Reliability_reliability.txt", false);
	//writeTotalReliability = new FileWriter("C:\\Data\\Australia\\Publication_Files\\TotalReliability_reliability.txt", false);
	
	public double getMaintainability(Host host, Vm vm){
		
		maintainability = setHost(host, vm);
		
		try {
			writeMaintainability = new FileWriter("C:\\Data\\Australia\\Publication_Files\\Reliability_Aware_Best_Fit_Decreasing_Restart_Single_core_per_VM_with_Decreasing_Cloudlets\\VmMaintainability.txt", true);
			
			vmIDsForMaintainability = new FileWriter("C:\\Data\\Australia\\Publication_Files\\Reliability_Aware_Best_Fit_Decreasing_Restart_Single_core_per_VM_with_Decreasing_Cloudlets\\vmIDsForMaintainability.txt", true);
			
			hostIDsForMaintainability = new FileWriter("C:\\Data\\Australia\\Publication_Files\\Reliability_Aware_Best_Fit_Decreasing_Restart_Single_core_per_VM_with_Decreasing_Cloudlets\\hostIDsForMaintainability.txt", true);
			
			writeMaintainability.write(maintainability+ System.getProperty( "line.separator" ));
			
			vmIDsForMaintainability.write(vm.getId()+ System.getProperty( "line.separator" ));
			
			hostIDsForMaintainability.write(host.getId()+ System.getProperty( "line.separator" ));
			
			writeMaintainability.close();
			
			vmIDsForMaintainability.close();
			
			hostIDsForMaintainability.close();
			
		} catch (IOException e) {
			System.out.println("Error has been occurred while writting about Maintainability");
		}
		
	//	System.out.println("Probability with which VM #" +vm.getId()+ " executing the cloudlet #" +VmCloudletMapping.getCloudlet(vm).getCloudletId()+ " running on a host #" +host.getId()+ " will be able to recover before missing the deadline is " +maintainability);
		System.out.println("Probability with which VM #" +vm.getId()+ " running on a host #" +host.getId()+ " will be able to recover before missing the deadline is " +maintainability);

		return(maintainability);		
	}
	
	public double setHost(Host host, Vm vm){
		int FTAId;
		FTAId = HostMapping.getFTAhostID(host.getId());
		getMTTR(FTAId, vm);		
		return(maintainability);
	}	
	
	public void getMTTR(int FTAId, Vm vm){
		double MTTR;
		MTTR = fta.getMTTR(FTAId);
		//getMTTR(FTAId, vm, MTBF); // MTBF is being passed because calAvailability function need to be called from getMTTR function.
		RecoveryRate(MTTR, vm);
	}
	
	public void RecoveryRate(double MTTR, Vm vm){	
		double Rrate;
		Rrate = 1/MTTR;
		calMaintainability(MTTR, vm, Rrate);
	}
	
	public double calMaintainability(double MTTR, Vm vm, double Rrate){
		//double reliability;
		double product;
		long cletLength;
		int cletListSize;
		Cloudlet clet;
		cletList = new ArrayList<Cloudlet>();
		cletListSize = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().size();
		for(int i=0;i<cletListSize;i++){
			clet = cletList.get(i);
			cletLength = clet.getCloudletLength();
		}
		clet = VmCloudletMapping.getCloudlet(vm);
		cletLength = clet.getCloudletLength();
		product = (Rrate * cletLength);
		maintainability = Math.exp(-product);		
		return(maintainability);
	} 
	
	public void setFTAReader(FTAFileReaderGrid5000 ftaread){
		this.fta = ftaread;
	}
		
	

}
