package org.cloudbus.cloudsim.failure;

import java.io.FileWriter;
import java.io.IOException;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;

public class AvailabilityCalculator {

	public double availability;
	
	FileWriter writeAvailability; 
	
	FTAFileReader fta = new FTAFileReader();
	
public double getAvailability(Host host){
		
		availability = setHost(host);
		
		
		try {
			writeAvailability = new FileWriter("C:\\Data\\Australia\\Publication_Files\\Reliability_Aware_Best_Fit_Decreasing_Restart_Single_core_per_VM_with_Decreasing_Cloudlets\\hostAvailability.txt", true);		
			
			writeAvailability.write(availability+ System.getProperty( "line.separator" ));		
			
			writeAvailability.close();
			
		} catch (IOException e) {
			System.out.println("Error has occurred while calculating the availability");
		}
		
		System.out.println("Availability of host #" +host.getId()+ " is " +availability);
		return(availability);		
	}

public double setHost(Host host){
	int FTAId;
	double MTBF;
	double MTTR;
	FTAId = HostMapping.getFTAhostID(host.getId());
	MTBF = getMTBF(FTAId);
	MTTR = getMTTR(FTAId);	
	return(calAvailability(MTBF, MTTR));
}	

public double getMTBF(int FTAId){
	double MTBF;
	MTBF = fta.getMTBF(FTAId);
	return(MTBF);
}

public double getMTTR(int FTAId){
	double MTTR;
	MTTR = fta.getMTTR(FTAId);
	return(MTTR);
}

public double calAvailability(double MTBF, double MTTR){
	double availability;
	availability = MTBF/(MTBF + MTTR);
	return(availability);
}

}
