package org.cloudbus.cloudsim.failure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

public class HostVMMappingReliability {
	
	public HashMap<Host, ArrayList<Double>> hostVmReliabilityTable = new HashMap<Host, ArrayList<Double>>();
	public HashMap<Vm, ArrayList<Double>> vmReliabilityTable = new HashMap<Vm, ArrayList<Double>>();
	public ArrayList<Double>vmReliability;
	public HashMap<Host, Double>hostClock = new HashMap<Host, Double>();
	public ArrayList<Double> systemReliability = new ArrayList<Double>();
	public ArrayList<Double> systemReliabilityParallel = new ArrayList<Double>();
	public HashMap<Host, Double>hostCurrentReliability = new HashMap<Host, Double>();
	public ArrayList<Double> systemReliabilityAverage = new ArrayList<Double>();
	public ArrayList<Double> systemAvailability = new ArrayList<Double>();
	public ArrayList<Double> systemMaintainability = new ArrayList<Double>();
	public FailureDatacenter DC;
	//public static FTAFileReader ftaread;
	public FTAFileReaderGrid5000 ftareadGrid5000;
	public FTAFileReader ftareadLANL;
	public boolean newReliability=false;	
	public ArrayList<Double> reliabilityList;	
	public double previousClockReliability = 0.0;
	public double previousClockAvailability = 0.0;
	public double previousClockMaintainability = 0.0;	
	public double tempReliability = 1;
	//public Object test;
	
	public void setDatacenter(FailureDatacenter DC){
		this.DC = DC;	
	}
	
	//public static void setFTARead(FTAFileReader fread){
	public void setFTARead(FTAFileReaderGrid5000 ftareadGrid5000){	
		this.ftareadGrid5000 = ftareadGrid5000;
	}
	
	public void setFTARead(FTAFileReader ftareadLANL){
		this.ftareadLANL = ftareadLANL;
	}
	
//	public void setFTARead(Object test){
//		this.test = test;
//	}
	
	public void setVmReliability(Vm vm, double reliability){
		if(vmReliabilityTable.isEmpty()){
			vmReliability = new ArrayList<Double>();
			vmReliability.add(reliability);
			vmReliabilityTable.put(vm, vmReliability);
		}
		else{
			if(vmReliabilityTable.containsKey(vm)){
				vmReliability = new ArrayList<Double>();
				vmReliability = vmReliabilityTable.get(vm);
				vmReliability.add(reliability);
				vmReliabilityTable.put(vm, vmReliability);
			}
			else{
				vmReliability = new ArrayList<Double>();
				vmReliability.add(reliability);
				vmReliabilityTable.put(vm, vmReliability);
			}
		}	
	}	
	
	public void sethostVmReliabilityTable(Host host, Vm vm){	
		double reliabilityHost=1;	
		if(hostCurrentReliability.get(host)==null){
			reliabilityHost = getVmReliability(vm);
			hostCurrentReliability.put(host, reliabilityHost);
		}
		else{
			if(RunTimeConstants.test == true){
				System.out.println("Host#" +host.getId()+ "'s Old Current Reliability was " +hostCurrentReliability.get(host));
			}
			for(int i=0;i<host.getVmList().size();i++){		
				if(vmReliabilityTable.containsKey(host.getVmList().get(i))){			
					reliabilityHost = reliabilityHost*getVmReliability(host.getVmList().get(i));
				}
			}	
			hostCurrentReliability.put(host, reliabilityHost);
			if(RunTimeConstants.test == true){
				System.out.println("Host#" +host.getId()+ "'s New Current Reliability is " +hostCurrentReliability.get(host));
			}
		}
	}	

	public void setSystemReliability(){
		int count = 0;	
		Host host;
		double systemReliabilityTemp = 1;	
		double systemReliabilityParallelTemp = 1;
		double systemReliabilityAverageTemp = 0;			
			for(int i=0;i<DC.getHostList().size();i++){
				host = DC.getHostList().get(i);
				if(hostCurrentReliability.containsKey(host)){					
					systemReliabilityTemp = systemReliabilityTemp * hostCurrentReliability.get(host);		
					systemReliabilityParallelTemp = systemReliabilityParallelTemp * (1-hostCurrentReliability.get(host));
					systemReliabilityAverageTemp = systemReliabilityAverageTemp+hostCurrentReliability.get(host);						
				}
			}			
			systemReliabilityParallelTemp = 1-systemReliabilityParallelTemp;			
			systemReliabilityAverageTemp = systemReliabilityAverageTemp/hostCurrentReliability.size();
			if(RunTimeConstants.test == true){
				System.out.println("System reliability is " +systemReliabilityAverageTemp);
			}
			if(previousClockReliability==CloudSim.clock()){	
				if(systemReliability.isEmpty()){
					systemReliability.add(systemReliabilityTemp);
					systemReliabilityParallel.add(systemReliabilityParallelTemp);		
					systemReliabilityAverage.add(systemReliabilityAverageTemp);
				
				}
				else{			
					for(int k=0;k<systemReliability.size();k++){
						count = k;
					}
					systemReliability.remove(count);
					systemReliability.add(systemReliabilityTemp);
					systemReliabilityParallel.remove(count);
					systemReliabilityParallel.add(systemReliabilityParallelTemp);
					systemReliabilityAverage.remove(count);
					systemReliabilityAverage.add(systemReliabilityAverageTemp);
				}
				previousClockReliability = CloudSim.clock();
		}
		else{			
			systemReliability.add(systemReliabilityTemp);
			systemReliabilityParallel.add(systemReliabilityParallelTemp);
			systemReliabilityAverage.add(systemReliabilityAverageTemp);
			previousClockReliability = CloudSim.clock();
		}
			
	}
	
	public void setSystemAvailability(){		
		int count = 0;		
		Host host;
		double systemAvailabilityTemp = 0;		
		int ftaHostID;	
		for(int i=0;i<DC.getHostList().size();i++){
			host = DC.getHostList().get(i);
			if(hostCurrentReliability.containsKey(host)){				
				//if(host.isFailed()!=true && host.getVmList().isEmpty()!=true){	
				ftaHostID = HostMapping.getFTAhostID(host.getId());
				//systemAvailabilityTemp = systemAvailabilityTemp + ftaread.getAvailability(ftaHostID);	
				systemAvailabilityTemp = systemAvailabilityTemp + FTAMeanTimeBetweenFailure.getAvailability(ftaHostID);
				//systemAvailabilityTemp = systemAvailabilityTemp + ftaread.getAvailability(ftaHostID);
				//}
			}
			
		}		
			systemAvailabilityTemp = systemAvailabilityTemp/hostCurrentReliability.size();
			if(RunTimeConstants.test == true){
				System.out.println("System availability is " +systemAvailabilityTemp);
			}
			
			if(previousClockAvailability==CloudSim.clock()){	
				if(systemAvailability.isEmpty()){
					systemAvailability.add(systemAvailabilityTemp);				
				}
				else{			
					for(int k=0;k<systemAvailability.size();k++){
						count = k;
					}
					systemAvailability.remove(count);
					systemAvailability.add(systemAvailabilityTemp);				
				}
				previousClockAvailability = CloudSim.clock();
			}
	//	}
			else{			
				systemAvailability.add(systemAvailabilityTemp);		
				previousClockAvailability = CloudSim.clock();
			}
		}
	
	public void setSystemMaintainability(){		
		int count = 0;		
		Host host;
		double systemMaintainabilityTemp = 0;		
		int ftaHostID;	
		for(int i=0;i<DC.getHostList().size();i++){
			host = DC.getHostList().get(i);
			if(hostCurrentReliability.containsKey(host)){			
				ftaHostID = HostMapping.getFTAhostID(host.getId());					
				systemMaintainabilityTemp = systemMaintainabilityTemp + FTAMeanTimeBetweenFailure.getMaintainability(ftaHostID);	
				//systemMaintainabilityTemp = systemMaintainabilityTemp + ftaread.getMaintainability(ftaHostID);
			}			
		}	
		systemMaintainabilityTemp = systemMaintainabilityTemp/hostCurrentReliability.size();
		if(RunTimeConstants.test == true){
			System.out.println("System Maintainability is " +systemMaintainabilityTemp);
		}
			
		if(previousClockMaintainability==CloudSim.clock()){	
			if(systemMaintainability.isEmpty()){
				systemMaintainability.add(systemMaintainabilityTemp);				
			}
			else{			
				for(int k=0;k<systemMaintainability.size();k++){
					count = k;
				}
				systemMaintainability.remove(count);
				systemMaintainability.add(systemMaintainabilityTemp);				
			}
			previousClockMaintainability = CloudSim.clock();
		}	
		else{				
			systemMaintainability.add(systemMaintainabilityTemp);		
			previousClockMaintainability= CloudSim.clock();
			}
		}
	
	public Double getVmReliability(Vm vm){
		vmReliability = new ArrayList<Double>();
		vmReliability = vmReliabilityTable.get(vm);
		double totalReliability = 0.0;
		double averageReliability;
		for(int i=0;i<vmReliability.size();i++){
			totalReliability = totalReliability + vmReliability.get(i);
		}
		averageReliability = totalReliability/vmReliability.size();
		return averageReliability;
	}

	public void sethostClock(Host host, Double clock){
		if(hostClock.containsKey(host)==false){
			hostClock.put(host, clock);
		}
		else{
			if(DC.flag_record==true){
				hostClock.put(host, clock);
			}
		}
	}	
	
	public ArrayList<Double> gethostVmReliability(Host host){
		reliabilityList = new ArrayList<Double>();
		if(hostVmReliabilityTable.containsKey(host)){
			reliabilityList = hostVmReliabilityTable.get(host);
		}
		return(reliabilityList);
	}	
	
	public ArrayList<Double>getSystemReliability(){
		return systemReliability;
	}
	
	public ArrayList<Double>getSystemReliabilityParallel(){
		return systemReliabilityParallel;
	}
	
	public ArrayList<Double>getSystemReliabilityAverage(){
		return systemReliabilityAverage;
	}
	
	public ArrayList<Double>getSystemAvailability(){
		return systemAvailability;
	}
	
	public ArrayList<Double>getSystemMaintainability(){
		return systemMaintainability;
	}

	public Double gethostClock(Host host){
		return hostClock.get(host);	
	}
	
}
