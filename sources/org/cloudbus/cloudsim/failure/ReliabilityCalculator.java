package org.cloudbus.cloudsim.failure;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.failure.*;

public class ReliabilityCalculator {
	
	public double reliability;
	
	public double sensitivityFactor;
	
	DecimalFormat dft = new DecimalFormat("###.####");
	
	FileWriter writeReliability; 
	
	FileWriter vmIDsForReliability;
	
	FileWriter hostIDsForReliability;
	
	//FTAFileReader fta = new FTAFileReader();
	
	ArrayList<Double>reliabilityList;
	
	public static HashMap<Vm, ArrayList<Double>> reliabilityTable = new HashMap<Vm, ArrayList<Double>>();
	
	public static HashMap<Integer, ArrayList<Double>> reliabilityTablewithVmID = new HashMap<Integer, ArrayList<Double>>();
	
	public static HashMap<Cloudlet, Double>previousUtilization = new HashMap<Cloudlet, Double>();	
	
	public Host host;	
	
	public ArrayList<Cloudlet>cletList;
	
	FailureDatacenterBroker broker;
	//try{
		
	//}catch(IOException e) {
		
		//e.printStackTrace();		
	//}
	
	//writeReliability = new FileWriter("C:\\Data\\Australia\\Publication_Files\\Reliability_reliability.txt", false);
	//writeTotalReliability = new FileWriter("C:\\Data\\Australia\\Publication_Files\\TotalReliability_reliability.txt", false);
	public void setBroker(FailureDatacenterBroker broker){
		this.broker = broker;
	}
	
	public double getReliability(Host host, Vm vm, double utilization){
		this.host = host;
		//if(host.getId()==141){
			//System.out.println("Check");
		//}
		reliability = setHost(host, vm, utilization);
		/*
		if(reliabilityTable.isEmpty()){ //this storing reliability in a table is not required. 
			reliabilityList = new ArrayList<Double>();
			reliabilityList.add(reliability);
			reliabilityTable.put(vm, reliabilityList);
			reliabilityTablewithVmID.put(vm.getId(), reliabilityList);
		}
		else{
			if(reliabilityTable.containsKey(vm)){
				reliabilityList = new ArrayList<Double>();
				reliabilityList = reliabilityTable.get(vm);
				reliabilityList.add(reliability);
				reliabilityTable.put(vm, reliabilityList);
				reliabilityTablewithVmID.put(vm.getId(), reliabilityList);
			}
			else{
				reliabilityList = new ArrayList<Double>();
				reliabilityList.add(reliability);
				reliabilityTable.put(vm, reliabilityList);
				reliabilityTablewithVmID.put(vm.getId(), reliabilityList);
			}
		}
		*/
		/*
		try {
			writeReliability = new FileWriter("C:\\Data\\Australia\\Publication_Files\\Reliability_Aware_Best_Fit_Decreasing_Restart_Single_core_per_VM_with_Decreasing_Cloudlets\\VmReliability.txt", true);
			
			vmIDsForReliability = new FileWriter("C:\\Data\\Australia\\Publication_Files\\Reliability_Aware_Best_Fit_Decreasing_Restart_Single_core_per_VM_with_Decreasing_Cloudlets\\vmIDsForReliability.txt", true);
			
			hostIDsForReliability = new FileWriter("C:\\Data\\Australia\\Publication_Files\\Reliability_Aware_Best_Fit_Decreasing_Restart_Single_core_per_VM_with_Decreasing_Cloudlets\\hostIDsForReliability.txt", true);
			
			writeReliability.write(reliability+ System.getProperty( "line.separator" ));
			
			vmIDsForReliability.write(vm.getId()+ System.getProperty( "line.separator" ));
			
			hostIDsForReliability.write(host.getId()+ System.getProperty( "line.separator" ));
			
			writeReliability.close();
			
			vmIDsForReliability.close();
			
			hostIDsForReliability.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		*/
		//System.out.println("Probability with which VM #" +vm.getId()+ " running on a host #" +host.getId()+ " will be able to execute the cloudlet #" +VmCloudletMapping.getCloudlet(vm).getCloudletId()+ " before the occurrence of a failure is " +reliability);
		return(reliability);		
	}
	
	public double setHost(Host host, Vm vm, double utilization){
		int FTAId;
		FTAId = HostMapping.getFTAhostID(host.getId());
		//if(FTAId == 3496){
		//	System.out.println("Check it");
		//}
		getMTBF(FTAId, vm, utilization);		
		return(reliability);
	}	
	
	public void getMTBF(int FTAId, Vm vm, double utilization){
		double MTBF;
		//MTBF = fta.getMTBF(FTAId);
		MTBF = FTAMeanTimeBetweenFailure.getMTBF(FTAId);
		//getMTTR(FTAId, vm, MTBF); // MTBF is being passed because calAvailability function need to be called from getMTTR function.
		HazardRateMax(MTBF, vm, utilization);
	}
	
	public void HazardRateMax(double MTBF, Vm vm, double utilization){	
		double hrate;
		hrate = 1/MTBF;
		HazardRateCurrent(hrate, utilization, vm);
		
	}
	
	public void HazardRateCurrent(double hrate, double utilization, Vm vm){
		double currentHazardRate;
		//BufferedReader read;
		//try {
			//read = new BufferedReader(new FileReader("/rusers/postgrad/ysharma1/PhDWork/Java/Results/Sensitivity_Factor.txt"));
			//sensitivityFactor = Double.parseDouble(read.readLine());
		//} catch (NumberFormatException | IOException e) {
		//	System.out.println("Error has occurred while reading sensitivity factor value");
		//}
		sensitivityFactor = RunTimeConstants.sensitivityFactor;
				
		currentHazardRate = hrate * Math.pow(utilization, sensitivityFactor);
		calReliability(vm, currentHazardRate);
	}
	
	public double calReliability(Vm vm, double currentHazardRate){
		//double reliability;
		double product;
		double cletLength;
		int cletListLength;
		int cletID;
		Cloudlet clet;
		cletList = new ArrayList<Cloudlet>();
		cletListLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().size();
		//cletListLength = ((FailureCloudletSchedulerSpaceShared)vm.getCloudletScheduler()).getCloudletExecList().size();
		for(int i=0; i<cletListLength; i++){
			cletID = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(i).getCloudletId();
			//cletID = ((FailureCloudletSchedulerSpaceShared)vm.getCloudletScheduler()).getCloudletExecList().get(i).getCloudletId();
			//if(broker.getidtoCloudletLst().contains(cletID)){	
		
				//clet = broker.getidtoCloudletLst().get(cletID);
				clet = broker.getCloudletFromID(cletID);
				cletList.add(clet);
			//}
		}
		long cletLengthMax = 0;
		//long numerator = 10000000000L;
		//long numerator = 10000000;
		long numerator = 10000000;
		for(int i=0; i<cletListLength; i++){
			if(cletList.get(i).getCloudletLength()>cletLengthMax){
				cletLengthMax = cletList.get(i).getCloudletLength();
			}
		}
		
		cletLength = cletLengthMax/numerator;	
		//cletLength = cletLengthMax/1000;
	//	cletLength = cletLengthMax;
		//System.out.println("Clet length is " +cletLength);
		product = (currentHazardRate * cletLength);
		reliability = Math.exp(-product);	
		
		return(reliability);
	} 
	
	/*
	public void newCloudletLength(Cloudlet clet, double utilization){
		double theta = 1.0;
		double mew = 0.0;
		long newCletLength;
		newCletLength = (long) ((theta * (1/utilization)+mew)*CloudletInitialLength.getCletIntialLength(clet));
		System.out.println("Initial length of cloudlet #" +clet.getCloudletId()+ " is " +CloudletInitialLength.getCletIntialLength(clet));
		System.out.println("New length of cloudlet #" +clet.getCloudletId()+ " with utilization " +utilization+ " is " +newCletLength);
		previousUtilization.put(clet, utilization);
		clet.setCloudletLength(newCletLength);
		
	}
	*/
		
		
}
	
	
	

