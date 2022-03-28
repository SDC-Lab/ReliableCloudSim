package org.cloudbus.cloudsim.failure;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.frequency.CheckNodeCompatibility;
import org.cloudbus.cloudsim.frequency.FirstLotofVMs;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

public class VmAllocationPolicyFailureAware extends VmAllocationPolicy{
	/** The vm table. */
	private Map<String, Host> vmTable;
	
	public ArrayList<Integer>ftaNodeList = new ArrayList<Integer>();	
	
	public HashMap<Vm, Host>lastHostTable = new HashMap<Vm, Host>(); 
	
	public double previousClock = 0.0;
	
	public double currentClock = 0.0;
	
	boolean migrationCase;
	
	public VmUtilization vmUtl;
	
	public HostVMMappingReliability hvmRbl;
	//FTAFileReader ftaread = new FTAFileReader();
	//FTAFileReader ftaread;
	FTAFileReaderGrid5000 ftareadGrid5000;
	
	FTAFileReader ftareadLANL;
	
	CheckNodeCompatibility chkNodeComp;
	
	public FirstLotofVMs firstVMLot;
	
	boolean firstHostCount = true;
	
	public int hostCount;
	//Datacenter DC;
	
	public int host_order = 0;
	
	public K_MeansAlgorithm kMeans;
	
	
	/**
	 * Creates the new VmAllocationPolicySimple object.
	 * 
	 * @param list the list
	 * @return 
	 * @pre $none
	 * @post $none
	 */
	//public VmAllocationPolicyFailureAware(List<? extends Host> list, FTAFileReader ftaread) {
	public VmAllocationPolicyFailureAware(List<? extends Host> list, FTAFileReaderGrid5000 ftareadGrid5000, VmUtilization vmUtl, HostVMMappingReliability hvmRbl, CheckNodeCompatibility chkNodeComp, FirstLotofVMs firstVMLot) {	
	//public VmAllocationPolicyFailureAware(List<? extends Host> list) {				
		super(list);
		setVmTable(new HashMap<String, Host>());		
		this.ftareadGrid5000 = ftareadGrid5000;		
		this.vmUtl = vmUtl;
		this.hvmRbl = hvmRbl;
		this.chkNodeComp = chkNodeComp;
		this.firstVMLot = firstVMLot;
		}
	
	public VmAllocationPolicyFailureAware(List<? extends Host> list, FTAFileReader ftareadLANL, VmUtilization vmUtl, HostVMMappingReliability hvmRbl, CheckNodeCompatibility chkNodeComp, FirstLotofVMs firstVMLot) {	
		//public VmAllocationPolicyFailureAware(List<? extends Host> list) {				
			super(list);
			setVmTable(new HashMap<String, Host>());		
			this.ftareadLANL = ftareadLANL;		
			this.vmUtl = vmUtl;
			this.hvmRbl = hvmRbl;
			this.chkNodeComp = chkNodeComp;
			this.firstVMLot = firstVMLot;
		}
	
	public boolean allocateHostForVm(Vm vm){
		Host host1 = null;		
		int peCount;		
		int vmCount;
		Host lastHost;
		
		boolean result = false;			
		boolean nodeCompatible = false;
	//	if(migrationCase == 0){			
			if (!getVmTable().containsKey(vm.getUid())) {		
				for(Host host: getHostList()){
					lastHost = lastHostTable.get(vm);					
					/**
					 * By applying this migration check here, the preference has been given to the hosts that are already active or
					 * provisioned earlier but sitting idle after finishing the execution of the cloudlets assigned to them. 
					 */
					if(vm.isBeingInstantiated()==false){
						vm.setBeingInstantiated(true);
					}
					if(vm.isInMigration()){					
						if(host == lastHostTable.get(vm)){							
							System.out.println("Vm " +vm.getId()+ " can not migrate to the same host");
							migrationCase = true;
							continue;
						}
					}
					if(host.isFailed()==true){				
						continue;
					}
					if(RunTimeConstants.predictionFlag==true || RunTimeConstants.failureCorrelation == true){
						if((IdleTime.expectedFailingHost.isEmpty()==false) && (IdleTime.checkExpectedFailingHost(host.getId())==true)){
							if(RunTimeConstants.test == true){
								System.out.println("Host "+host.getId()+" is expected to be failed. So it will not get allocated to VMs");
							}
							continue;
						}
					}
					host1 = host;		
					peCount = host1.getNumberOfPes();			
					vmCount = host1.getVmList().size();
					if(peCount > vmCount){
						if(firstVMLot.containsVMID(vm.getId()) == false) {
							nodeCompatible = chkNodeComp.isNodeCompatible(HostMapping.getFTAhostID(host1.getId()), vm);
							if(nodeCompatible == false) {
								continue;
							}
						}
						if(previousClock == CloudSim.clock()){
							previousClock = CloudSim.clock();
						}
						if(previousClock!=CloudSim.clock()){
							hostCount = IdleTime.getActiveHostCount();							
							HostCounter.setHostCount(hostCount);
							HostCounter.setClockList(previousClock);
							previousClock = CloudSim.clock();
						}					
						if(IdleTime.getHostActiveStatus(host1.getId())==false){
							IdleTime.setHostActiveStatus(host1.getId(), true);
						}					
						result = host1.vmCreate(vm);						
						if(result==false){
							continue;
						}
						HostVMMapping.sethostVmMapTable(host1, vm);					
						lastHostTable.put(vm, host1);
						if(migrationCase == true){
							Log.formatLine(
									"%.2f: VM #" + vm.getId() + " has been migrated from host # " +lastHost.getId()+ " to the host #" + host1.getId(),
									CloudSim.clock());							
							migrationCase = false;						
						}
						else{
						Log.formatLine(
								"%.2f: VM #" + vm.getId() + " has been allocated to the host #" + host1.getId(),
								CloudSim.clock());	
						}						
						List<Pe> peList = new ArrayList<Pe>();
						peList = host1.getVmScheduler().getPesAllocatedForVM(vm);						
						for(int i=0; i<peList.size(); i++){
							int peID;
							peID = peList.get(i).getId();
							boolean idleStatus;
							idleStatus = IdleTime.getHostPeIdleTable(host1.getId(), peID);
							if(idleStatus == true){								
								IdleTime.setHostPeIdleTable(host1.getId(), peID, false);
								if(IdleTime.checkHostPeIdleClockTable(host1.getId(), peID)==true){
									IdleTime.setHostPeIdleTimeTable(host1.getId(), peID, CloudSim.clock());	
									IdleTime.removeHostPeIdleClockTable(host1.getId(), peID);
								}													
							}							
						}											
					break;
				}
			}
			if(result){
				getVmTable().put(vm.getUid(), host1);			
			}
			else{
				if(RunTimeConstants.test == true){
					System.out.println("An another node need to be provisioned");
				}				
				lastHost = lastHostTable.get(vm);
				if(RunTimeConstants.failureCorrelation == true && ftareadGrid5000.getCorrelatedMigrationCounter() > 0){
					//int correlatedCounter = ftareadGrid5000.getCorrelatedMigrationCounter();
					//correlatedCounter = correlatedCounter - 1;
					//ftareadGrid5000.setCorrelatedMigraitonCounter(correlatedCounter);					
					host1 = provisionNodeWithCorrelation(lastHost);
				}
				else{
					host1 = provisionNode(vm);
				}				
				System.out.println("Host " +host1.getId()+" has been provisioned");				
				if(previousClock == CloudSim.clock()){
					previousClock = CloudSim.clock();
				}
				if(previousClock!=CloudSim.clock()){
					hostCount = IdleTime.getActiveHostCount();					
					HostCounter.setHostCount(hostCount);
					HostCounter.setClockList(previousClock);
					previousClock = CloudSim.clock();
				}
				if(IdleTime.getHostActiveStatus(host1.getId())==false){
					IdleTime.setHostActiveStatus(host1.getId(), true);
				}			
				List<Pe>peListForHost = new ArrayList<Pe>();
				peListForHost = host1.getPeList();
				for(int j=0 ;j<peListForHost.size(); j++){
					IdleTime.setHostPeIdleTable(host1.getId(), peListForHost.get(j).getId(), true);
					IdleTime.setHostPeIdleClockTable(host1.getId(), peListForHost.get(j).getId(), CloudSim.clock());
				}
				result = host1.vmCreate(vm);					
				HostVMMapping.sethostVmMapTable(host1, vm);		
				lastHostTable.put(vm, host1);
				if(migrationCase == true){
					Log.formatLine(
							"%.2f: VM #" + vm.getId() + " has been migrated from host # " +lastHost.getId()+ " to the host #" + host1.getId(),
							CloudSim.clock());
					migrationCase = false;
				}
				else{
					Log.formatLine(
							"%.2f: VM #" + vm.getId() + " has been allocated to the host #" + host1.getId(),
							CloudSim.clock());
				}
				List<Pe> peList = new ArrayList<Pe>();
				peList = host1.getVmScheduler().getPesAllocatedForVM(vm);						
				for(int i=0; i<peList.size(); i++){
					int peID;
					peID = peList.get(i).getId();
					boolean idleStatus;
					idleStatus = IdleTime.getHostPeIdleTable(host1.getId(), peID);
					if(idleStatus == true){								
						IdleTime.setHostPeIdleTable(host1.getId(), peID, false);
						if(IdleTime.checkHostPeIdleClockTable(host1.getId(), peID)==true){
							IdleTime.setHostPeIdleTimeTable(host1.getId(), peID, CloudSim.clock());	
							IdleTime.removeHostPeIdleClockTable(host1.getId(), peID);
						}													
					}							
				}				
				getVmTable().put(vm.getUid(), host1);				
			}
		}			
		return result;
	}	
	
	/*
	 * (non-Javadoc)
	 * @see org.cloudbus.cloudsim.VmAllocationPolicy#allocateHostForVm(org.cloudbus.cloudsim.Vm,
	 * org.cloudbus.cloudsim.Host)
	 */
	@Override
	/**
	 * This is only for VM consolidation where the VM gets migrated only to the provisioned nodes. 
	 */
	public boolean allocateHostForVm(Vm vm, Host host) {
			Host lastHost;
			List<Pe> peList = new ArrayList<Pe>();
			lastHost = lastHostTable.get(vm);			
			if(!vm.isBeingInstantiated()){
				vm.setBeingInstantiated(true);
			}		
			if(vm.isInMigration()){                    
				if(host == lastHost){
					System.out.println("Vm " +vm.getId()+ " can not consolidate to the same host");							
				}
			
			else{
				//IdleTime.setHostActiveStatus(host.getId(), true);
				
				if (host.vmCreate(vm)) { // if vm has been succesfully created in the host
					
					getVmTable().put(vm.getUid(), host);
					lastHostTable.put(vm, host);
					HostVMMapping.sethostVmMapTable(host, vm);
					Log.formatLine(
							"%.2f: VM #" + vm.getId() + " has been consolidated to the host #" + host.getId(),
							CloudSim.clock());
					
					peList = new ArrayList<Pe>();
					peList = host.getVmScheduler().getPesAllocatedForVM(vm);
					for(int i=0; i<peList.size(); i++){
						int peID;
						peID = peList.get(i).getId();
						boolean idleStatus;
						idleStatus = IdleTime.getHostPeIdleTable(host.getId(), peID);
						if(idleStatus == false){
							System.out.println("Check");
						}
						if(idleStatus == true){
							IdleTime.setHostPeIdleTable(host.getId(), peID, false);
							if(IdleTime.checkHostPeIdleClockTable(host.getId(), peID)==true){
								IdleTime.setHostPeIdleTimeTable(host.getId(), peID, CloudSim.clock());
								System.out.println("An idle core at host " +host.getId()+" has been reallocated during consolidation");
								IdleTime.removeHostPeIdleClockTable(host.getId(), peID);
								}
							}							
						}					
					//if(previousClock == CloudSim.clock()){
					//	previousClock = CloudSim.clock();
					//}
					//if(previousClock!=CloudSim.clock()){
					//	hostCount = IdleTime.getActiveHostCount();					
					//	HostCounter.setHostCount(hostCount);
					//	HostCounter.setClockList(previousClock);
					//	previousClock = CloudSim.clock();
					//}
					return true;				
				}
			}
			return false;
			}
			else{
				System.out.println("VM is not in migration");
				return false;
			}
	}
	
	//boolean flag = false;
	ReliabilityCalculator rblcal = new ReliabilityCalculator();
	
	public static ArrayList<Integer> sortedFtaNodeList = new ArrayList<Integer>();
	boolean nodeCompatible;
	
	public Host provisionNode(Vm vm){
		int ftaNodeID = 0;		
		double utilization = 0.0;
		int ram = 0;
		int peCount = 0;
		int peListIndex = 0;
		long storage = 1000000;
		int bw = 10000;
		int hostId = 0;
		int host_count = HostMapping.getHostMapSize();
		boolean checkProvisionedHost = false;	
		if(RunTimeConstants.traceType.equals("LANL")){
			host_order = RunTimeConstants.host_order;
			switch(host_order){				
				case 1:
					System.out.println(" Energy aware resource provisioning is being done");
					ftaNodeList = ftareadLANL.getPowerHostListSorted(); // For energy conscious provisioning
					break;
				case 2:
					System.out.println(" Reliability aware resource provisioning is being done");
					ftaNodeList = ftareadLANL.getSortedNodeIDList();	// For reliability conscious provisioning
					break;
				case 3:
					System.out.println(" Reliability and Energy aware resource provisioning is being done");
					ftaNodeList = ftareadLANL.getPowerandHazardRateHostListSorted(); // For reliability and energy conscious provisioning
					break;
				case 4:
					System.out.println(" Random resource provisioning is being done");
					ftaNodeList = ftareadLANL.getNodeIDList();// For random provisioning
					break;
				case 5: 
					System.out.println(" Availability aware resource provisioning is being done");	
					ftaNodeList = ftareadLANL.getAvailabilityHostListSorted();
					break;					
				case 6:				
					System.out.println(" Maintainability aware resource provisioning is being done");	
					ftaNodeList = ftareadLANL.getMaintainabilityHostListSorted();
					break;					
				case 7:				
					System.out.println(" Maintainability and Energy aware resource provisioning is being done");	
					ftaNodeList = ftareadLANL.getPowerandMaintainabilityHostListSorted();
					break;				
				case 8:				
					System.out.println(" Availability and Energy aware resource provisioning is being done");	
					ftaNodeList = ftareadLANL.getPowerandAvailabilityHostListSorted();
					break;	
				}
		}
		if(RunTimeConstants.traceType.equals("Grid5000")){
			host_order = RunTimeConstants.host_order;
			switch(host_order){	
				case 1:
					System.out.println(" Energy aware resource provisioning is being done");
					ftaNodeList = ftareadGrid5000.getPowerHostListSorted(); // For energy conscious provisioning
					break;
				case 2:
					System.out.println(" Reliability aware resource provisioning is being done");
					ftaNodeList = ftareadGrid5000.getSortedNodeIDList();	// For reliability conscious provisioning
					break;
				case 3:
					System.out.println(" Reliability and Energy aware resource provisioning is being done");
					ftaNodeList = ftareadGrid5000.getPowerandHazardRateHostListSorted(); // For reliability and energy conscious provisioning
					break;
				case 4:
					System.out.println(" Random resource provisioning is being done");
					ftaNodeList = ftareadGrid5000.getNodeIDList();// For random provisioning
					break;
				case 5: 
					System.out.println(" Availability aware resource provisioning is being done");	
					ftaNodeList = ftareadGrid5000.getAvailabilityHostListSorted();
					break;					
				case 6:				
					System.out.println(" Maintainability aware resource provisioning is being done");	
					ftaNodeList = ftareadGrid5000.getMaintainabilityHostListSorted();
					break;					
				case 7:				
					System.out.println(" Maintainability and Energy aware resource provisioning is being done");	
					ftaNodeList = ftareadGrid5000.getPowerandMaintainabilityHostListSorted();
					break;				
				case 8:				
					System.out.println(" Availability and Energy aware resource provisioning is being done");	
					ftaNodeList = ftareadGrid5000.getPowerandAvailabilityHostListSorted();
					break;	
				}				
			}		
			if(RunTimeConstants.host_order == 4){				
				int fta_index = 0;				
				boolean ftaProvisionedHost = false;								
				while(ftaProvisionedHost == false){				
					fta_index = (int)(Math.random()*(ftaNodeList.size()));
					for(int j=0;j<HostMapping.getHostMapSize();j++){
						ftaProvisionedHost = true;
						if(ftaNodeList.get(fta_index)==HostMapping.getFTAhostID(j)){
							System.out.println("System has already been provisioned, keep generating");
							ftaProvisionedHost = false;							
							break;
						}						
					}
				}
				if(ftaProvisionedHost == true){					
					HostMapping.createHostMap(host_count, ftaNodeList.get(fta_index));
					ftaNodeID = ftaNodeList.get(fta_index);					
				}			
			}		
			else{
				for(int i=0;i<ftaNodeList.size();i++){
					nodeCompatible = false;
					for(int j=0;j<HostMapping.getHostMapSize();j++){
						checkProvisionedHost = false;
						if(ftaNodeList.get(i)==HostMapping.getFTAhostID(j)){							
							checkProvisionedHost = true;
							break;						
						}
					}
					if(checkProvisionedHost == false){
						if(RunTimeConstants.workingWithFrequencyScaling == true) {
							nodeCompatible = chkNodeComp.isNodeCompatible(ftaNodeList.get(i), vm);
							if(nodeCompatible == true) {
								HostMapping.createHostMap(host_count,ftaNodeList.get(i));
								ftaNodeID = ftaNodeList.get(i);
								break;								
							}
						}
						else {
							HostMapping.createHostMap(host_count,ftaNodeList.get(i));
							ftaNodeID = ftaNodeList.get(i);
							break;
						}						
					}
				}
			}
			if(RunTimeConstants.traceType.equals("LANL")){
				utilization = ftareadLANL.getCurrentUtilization(ftaNodeID);
				ram = ftareadLANL.getMemorySize(ftaNodeID);		
				peCount = ftareadLANL.getProcessorCount(ftaNodeID);	
			}	
			if(RunTimeConstants.traceType.equals("Grid5000")){
				utilization = ftareadGrid5000.getCurrentUtilization(ftaNodeID);
				ram = ftareadGrid5000.getMemorySize(ftaNodeID);		
				peCount = ftareadGrid5000.getProcessorCount(ftaNodeID);	
			}
		
			for(int k=0;k<PeListforFailures.getPeListSize();k++){
				if(PeListforFailures.getPeList(k).size() == peCount){			
					peListIndex = k;
					break;
				}
			}
	
			for(int k=0;k<getHostList().size();k++){
				hostId = getHostList().get(k).getId();
			}
			hostId = hostId + 1;
			getHostList().add(
				new Host(
						hostId,						
						new RamProvisionerSimple(ram), 
						new BwProvisionerSimple(bw), 
						storage, 
						PeListforFailures.getPeList(peListIndex),							
						new VmSchedulerSpaceShared(PeListforFailures.getPeList(peListIndex))
						));
		
		HostUtilization.sethostUtilization(hostId, utilization);
		hvmRbl.sethostClock(getHostList().get(hostId), CloudSim.clock());		
		return getHostList().get(hostId);
	}
	
	public Host provisionNodeWithCorrelation(Host lastHost){
		int ftaNodeID = 0;		
		double utilization = 0.0;
		int ram = 0;
		int peCount = 0;
		int peListIndex = 0;
		long storage = 1000000;
		int bw = 10000;
		int hostId = 0;
		int host_count = HostMapping.getHostMapSize();
		boolean checkProvisionedHost = false;	
		if(RunTimeConstants.traceType.equals("LANL")){
			host_order = RunTimeConstants.host_order;
			switch(host_order){				
				case 1:
					System.out.println(" Energy aware resource provisioning is being done");
					ftaNodeList = ftareadLANL.getPowerHostListSorted(); // For energy conscious provisioning
					break;
				case 2:
					System.out.println(" Reliability aware resource provisioning is being done");
					ftaNodeList = ftareadLANL.getSortedNodeIDList();	// For reliability conscious provisioning
					break;
				case 3:
					System.out.println(" Reliability and Energy aware resource provisioning is being done");
					ftaNodeList = ftareadLANL.getPowerandHazardRateHostListSorted(); // For reliability and energy conscious provisioning
					break;
				case 4:
					System.out.println(" Random resource provisioning is being done");
					ftaNodeList = ftareadLANL.getNodeIDList();// For random provisioning
					break;
				case 5: 
					System.out.println(" Availability aware resource provisioning is being done");	
					ftaNodeList = ftareadLANL.getAvailabilityHostListSorted();
					break;					
				case 6:				
					System.out.println(" Maintainability aware resource provisioning is being done");	
					ftaNodeList = ftareadLANL.getMaintainabilityHostListSorted();
					break;					
				case 7:				
					System.out.println(" Maintainability and Energy aware resource provisioning is being done");	
					ftaNodeList = ftareadLANL.getPowerandMaintainabilityHostListSorted();
					break;				
				case 8:				
					System.out.println(" Availability and Energy aware resource provisioning is being done");	
					ftaNodeList = ftareadLANL.getPowerandAvailabilityHostListSorted();
					break;	
				}
		}
		if(RunTimeConstants.traceType.equals("Grid5000")){
			host_order = RunTimeConstants.host_order;
			kMeans = ftareadGrid5000.getKMeansObject();
			switch(host_order){	
				case 1:
					System.out.println(" Energy aware resource provisioning is being done");
					ftaNodeList = ftareadGrid5000.getPowerHostListSorted(); // For energy conscious provisioning
					break;
				case 2:
					System.out.println(" Reliability aware resource provisioning is being done");
					ftaNodeList = ftareadGrid5000.getSortedNodeIDList();	// For reliability conscious provisioning
					break;
				case 3:
					System.out.println(" Reliability and Energy aware resource provisioning is being done");
					ftaNodeList = ftareadGrid5000.getPowerandHazardRateHostListSorted(); // For reliability and energy conscious provisioning
					break;
				case 4:
					System.out.println(" Random resource provisioning is being done");
					ftaNodeList = ftareadGrid5000.getNodeIDList();// For random provisioning
					break;
				case 5: 
					System.out.println(" Availability aware resource provisioning is being done");	
					ftaNodeList = ftareadGrid5000.getAvailabilityHostListSorted();
					break;					
				case 6:				
					System.out.println(" Maintainability aware resource provisioning is being done");	
					ftaNodeList = ftareadGrid5000.getMaintainabilityHostListSorted();
					break;					
				case 7:				
					System.out.println(" Maintainability and Energy aware resource provisioning is being done");	
					ftaNodeList = ftareadGrid5000.getPowerandMaintainabilityHostListSorted();
					break;				
				case 8:				
					System.out.println(" Availability and Energy aware resource provisioning is being done");	
					ftaNodeList = ftareadGrid5000.getPowerandAvailabilityHostListSorted();
					break;	
				}				
			}		
			ArrayList<Integer>correlatedNodes = new ArrayList<Integer>();
			String nodeLocation;
			int ftaHostID;
			ftaHostID = HostMapping.getFTAhostID(lastHost.getId());
			nodeLocation = ftareadGrid5000.getClusterNameForNode(ftaHostID);
			correlatedNodes.addAll(kMeans.getCorrelatedNodes(nodeLocation, ftaHostID));
			for(int i=0; i<correlatedNodes.size(); i++){
				if(correlatedNodes.get(i).equals(ftaHostID)){
					correlatedNodes.remove(i);
					break;
				}
			}
			if(RunTimeConstants.host_order == 4){				
				int fta_index = 0;				
				boolean ftaProvisionedHost = false;								
				while(ftaProvisionedHost == false){				
					fta_index = (int)(Math.random()*(ftaNodeList.size()));
					for(int j=0;j<HostMapping.getHostMapSize();j++){
						ftaProvisionedHost = true;
						if(ftaNodeList.get(fta_index)==HostMapping.getFTAhostID(j)){
							System.out.println("System has already been provisioned, keep generating");
							ftaProvisionedHost = false;							
							break;
						}						
					}
					if(ftaProvisionedHost == true){
						for(int j=0; j<correlatedNodes.size(); j++){
							if(ftaNodeList.get(fta_index).equals(correlatedNodes.get(j))){
								ftaProvisionedHost = false;
								break;
							}
						}
					}
				}
				if(ftaProvisionedHost == true){					
					HostMapping.createHostMap(host_count, ftaNodeList.get(fta_index));
					ftaNodeID = ftaNodeList.get(fta_index);					
				}			
			}		
			else{			
				for(int i=0;i<ftaNodeList.size();i++){
					for(int j=0;j<HostMapping.getHostMapSize();j++){
						checkProvisionedHost = false;
						if(ftaNodeList.get(i)==HostMapping.getFTAhostID(j)){							
							checkProvisionedHost = true;
							break;						
						}
					}	
					if(checkProvisionedHost == false){
						for(int j=0; j<correlatedNodes.size(); j++){
							if(ftaNodeList.get(i).equals(correlatedNodes.get(j))){
								checkProvisionedHost = true;
								break;
							}
						}
					}					
					if(checkProvisionedHost == false){
						HostMapping.createHostMap(host_count,ftaNodeList.get(i));
						ftaNodeID = ftaNodeList.get(i);
						break;
					}
				}
			}
			if(RunTimeConstants.traceType.equals("LANL")){
				utilization = ftareadLANL.getCurrentUtilization(ftaNodeID);
				ram = ftareadLANL.getMemorySize(ftaNodeID);		
				peCount = ftareadLANL.getProcessorCount(ftaNodeID);	
			}	
			if(RunTimeConstants.traceType.equals("Grid5000")){
				utilization = ftareadGrid5000.getCurrentUtilization(ftaNodeID);
				ram = ftareadGrid5000.getMemorySize(ftaNodeID);		
				peCount = ftareadGrid5000.getProcessorCount(ftaNodeID);	
			}
		
			for(int k=0;k<PeListforFailures.getPeListSize();k++){
				if(PeListforFailures.getPeList(k).size() == peCount){			
					peListIndex = k;
					break;
				}
			}
	
			for(int k=0;k<getHostList().size();k++){
				hostId = getHostList().get(k).getId();
			}
			hostId = hostId + 1;
			getHostList().add(
				new Host(
						hostId,						
						new RamProvisionerSimple(ram), 
						new BwProvisionerSimple(bw), 
						storage, 
						PeListforFailures.getPeList(peListIndex),							
						new VmSchedulerSpaceShared(PeListforFailures.getPeList(peListIndex))
						));
		
		HostUtilization.sethostUtilization(hostId, utilization);
		hvmRbl.sethostClock(getHostList().get(hostId), CloudSim.clock());		
		return getHostList().get(hostId);
	}
	
	/**
	 * Function to calculate the reliability of every allocated VM.
	 */
	
	//ReliabilityCalculator rlbcal = new ReliabilityCalculator();
	//CloudletNormalization cletNorm = new CloudletNormalization();
	/*
	public void ReliabilityCalculator(Host host, Vm vm){
		double ftaUtilization;
		double utilization;
		double reliability;
		//double tempReliability = 1;
		//if(cletNorm)
		utilization = VmCloudletMapping.getCloudlet(vm).getUtilizationOfCpu(CloudSim.clock());
		ftaUtilization = ftaread.getCurrentUtilization(HostMapping.getFTAhostID(host.getId()));
		utilization = utilization + ftaUtilization;		
		if(utilization > 1.0){
			utilization = 1.0;
		}
		vm.setCurrentUtilization(utilization);
		//reliability = rblcal.getReliability(host, vm, vm.getCurrentUtilization());
		reliability = rblcal.getReliability(host, vm, utilization);
		reliability = Math.ceil(reliability * 1000);
		reliability = reliability / 1000;		
		//System.out.println("Old Reliability of VM #" +vm.getId()+ " is " +HostVMMappingReliability.getVmReliability(vm));
		System.out.println("Reliability of VM #" +vm.getId()+ " at utilization " +utilization+ " is " +reliability);
		
		HostVMMappingReliability.setVmReliability(vm, reliability);		
		
		//for(int i=0;i<host.getVmList().size();i++){
		//	tempReliability = tempReliability * HostVMMappingReliability.getVmReliability(host.getVmList().get(i));
		//}
		//if(HostVMMappingReliability.clock!=CloudSim.clock()){
			
		//	HostVMMappingReliability.sethostVmReliabilityTable(host, tempReliability);
			//HostVMMappingReliability.clock = CloudSim.clock();
			//HostVMMappingReliability.sethostVmReliabilityTable(host, tempReliability);
		//}
		//HostVMMappingReliability.sethostVmReliabilityTable(host, reliability);
		HostVMMappingReliability.sethostVmReliabilityTable(host);
		HostVMMappingReliability.sethostClock(host, CloudSim.clock());
		//HostVMMappingReliability.sethostVmReliabilityTable(host, host.getVmList());
		//HostVMMappingUtilization.sethostVmUtilizationTable(host, utilization);
		HostVMMappingUtilization.setVmUtilization(vm, utilization);
	}
	*/
	
	public void ReliabilityCalculator(){
		Host host;
		Vm vm;
		for(int i=0;i<getHostList().size();i++){
			host = getHostList().get(i);
			for(int j=0;j<host.getVmList().size();j++){
				double reliability;
				vm = host.getVmList().get(j);
				reliability = rblcal.getReliability(host, vm, vmUtl.getCurrentVmUtilization(vm.getId()));
				reliability = Math.ceil(reliability * 1000);
				reliability = reliability / 1000;
				System.out.println("Updated Reliability of VM #" +vm.getId()+ " is " +reliability);
				hvmRbl.setVmReliability(vm, reliability);
			}
		}
	}
	
	/**
	 * Releases the host used by a VM.
	 * 
	 * @param vm the vm
	 * @pre $none
	 * @post none
	 */
	@Override
	public void deallocateHostForVm(Vm vm) {
		Host host = getVmTable().remove(vm.getUid());
		int idx = getHostList().indexOf(host);		
		if (host != null) {
			host.vmDestroy(vm);			
		}
	}
	
	/**
	 * Gets the host that is executing the given VM belonging to the given user.
	 * 
	 * @param vm the vm
	 * @return the Host with the given vmID and userID; $null if not found
	 * @pre $none
	 * @post $none
	 */
	@Override
	public Host getHost(Vm vm) {
		return getVmTable().get(vm.getUid());
	}
	
	/**
	 * Gets the host that is executing the given VM belonging to the given user.
	 * 
	 * @param vmId the vm id
	 * @param userId the user id
	 * @return the Host with the given vmID and userID; $null if not found
	 * @pre $none
	 * @post $none
	 */
	@Override
	public Host getHost(int vmId, int userId) {
		return getVmTable().get(Vm.getUid(userId, vmId));
	}

	/**
	 * Gets the vm table.
	 * 
	 * @return the vm table
	 */
	public Map<String, Host> getVmTable() {
		return vmTable;
	}

	/**
	 * Sets the vm table.
	 * 
	 * @param vmTable the vm table
	 */
	protected void setVmTable(Map<String, Host> vmTable) {
		this.vmTable = vmTable;
	}

	/*
	 * (non-Javadoc)
	 * @see cloudsim.VmAllocationPolicy#optimizeAllocation(double, cloudsim.VmList, double)
	 */
	@Override
	public List<Map<String, Object>> optimizeAllocation(List<? extends Vm> vmList) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
