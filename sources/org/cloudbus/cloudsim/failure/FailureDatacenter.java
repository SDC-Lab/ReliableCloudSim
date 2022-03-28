package org.cloudbus.cloudsim.failure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.failure.CheckpointOverhead;
import org.cloudbus.cloudsim.failure.CloudletNormalization;
import org.cloudbus.cloudsim.failure.CloudletReexecutionPart;
import org.cloudbus.cloudsim.failure.CloudletStatus;
import org.cloudbus.cloudsim.failure.DownTimeTable;
import org.cloudbus.cloudsim.failure.ExecutionModel;
import org.cloudbus.cloudsim.failure.FTACurrentUtilization;
import org.cloudbus.cloudsim.failure.FTAFileReader;
import org.cloudbus.cloudsim.failure.HostVMMappingReliability;
import org.cloudbus.cloudsim.failure.HostVMMappingUtilization;
import org.cloudbus.cloudsim.failure.IdleHostTime;
import org.cloudbus.cloudsim.failure.PerfectCheckpointing;
import org.cloudbus.cloudsim.failure.ReliabilityCalculator;
import org.cloudbus.cloudsim.failure.RunTimeConstants;
import org.cloudbus.cloudsim.frequency.CheckNodeCompatibility;
import org.cloudbus.cloudsim.frequency.DeadlineModel;
import org.cloudbus.cloudsim.frequency.FrequencyDetails;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.ResCloudlet;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.failure.FailureCloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;


public class FailureDatacenter extends Datacenter {
	
	public int flag = 0;
	
	public int count = 0;	
	// This class variable gives the host set to failed
	public Host fail = null;
	
	public Host failingHost = null;
	//public FailureHost fail = null;
	
	//public List <Vm> failedVMs;
	
	public Vm vmId;
	
	//public FailureVm vmId;
	
	//public Cloudlet clet;
	
	public String usrId;
	//public List<Integer>vmIdent = new ArrayList<Integer>();
	
	

	public List<Vm>distVMs = new ArrayList<Vm>();
	
	//public List<FailureVm>distVMs = new ArrayList<FailureVm>();
	
	public boolean flag_reliability = false;
	
	public double previousClock = 0;
	
	//public DatacenterBroker dcBroker;
	
	/** List of Cloudlets and corresponding virtual machines */
	
	//public Hashtable<Integer, ArrayList<Integer>> clvmList = new Hashtable<Integer, ArrayList<Integer>>();
	
	public Hashtable<Integer, Integer> usrclList = new Hashtable<Integer, Integer>();
	 
	public List<Integer> cletList = new ArrayList<Integer>();
	
	public List<Integer> failhostList = new ArrayList<Integer>();
	
	public List<Integer> failNodes = new ArrayList<Integer>();
	
	public Hashtable<Integer, Integer> failhostTable = new Hashtable<Integer, Integer>();
	
	public VmUtilization vmUtl;
	
	public int totalVmMigrations = 0;
	
	
	//GoogleFailureGenerator failgen = new GoogleFailureGenerator();
	
	FTAFileReader freadLANL;
	
	FTAFileReaderGrid5000 freadGrid5000;
	
	ExecutionModel exec = new ExecutionModel();
	
	int value;
	
	int ftaNodeId;
	
	double failed_time;
	
	double recovery_time;
	
	double down_time;
	
	public HashMap<Vm, Double>downTimeTable = new HashMap<Vm, Double>();
	
	//public HashMap<FailureVm, Double>downTimeTable = new HashMap<FailureVm, Double>();
	
	public int counterCloudlets = 0;
	
	public boolean furtherCheckpoints = true;
	
	public int totalFailureCount = 0;
	
	public int faultToleranceMechanism;
	
	public ReliabilityCalculator rblcal;
	
	public CloudletNormalization cletNorm;
	
	public static int counter_endTime=0;
	
	public int totalCheckpointEvents = 0;
	
	public boolean flag_record = false;
	
	public boolean lock_sufferedCletList = false;
	
	public FailureDatacenterBroker broker;
	
	public HostVMMappingReliability hvmRbl;
	
	public FailurePrediction failPredict;
	
	public FailurePredictionSynthetic failPredictSynthetic;
	
	public MigrationOverheadModel mgOver;
	
	public VmMigrationRecord vmRecord;
	
	public K_MeansAlgorithm kMeans;
	
	public K_MeansAlgorithmLANL kMeansLANL;
	
	public int consolidationCount = 0;
	
	public boolean ignoreFailure = true;
	
	public HashMap<Integer, Boolean>firstFailureFlag = new HashMap<Integer, Boolean>();
	
	//public HashMap<Integer, Boolean>firstRecoveryFlag = new HashMap<Integer, Boolean>();
	
	//public HashMap<Integer, Boolean>firstPredictionFlag = new HashMap<Integer, Boolean>();
	
	//public HashMap<Integer, Boolean>firstCheckpointFlag = new HashMap<Integer, Boolean>();
	
	public ArrayList<Double>failurePrediction;
	
	public HashMap<Integer, ArrayList<Double>>currentPrediction = new HashMap<Integer, ArrayList<Double>>();
	
	public ArrayList<Double>currentPredictionList;
	
	public HashMap<Integer, Long>cletLengthBackup = new HashMap<Integer, Long>();
	
	public HashMap<Integer, Long>previousChkptSoFarTable = new HashMap<Integer, Long>();
	
	public HashMap<Integer, Boolean>nodeWithFirstFailEventMap = new HashMap<Integer, Boolean>();
	
	
	/**
	 * 
	 * Variables for Frequency Scaling Evaluation
	 * 
	 */
	public DeadlineModel dlModel;
	public FrequencyDetails frqDtls;
	public CheckNodeCompatibility chkNodeComp;
	
	public void setDeadlineModel(DeadlineModel dlModel){
		this.dlModel = dlModel; 
	}
	
	public void setFrequencyDetails(FrequencyDetails frqDtls){
		this.frqDtls = frqDtls;
	}
	
	public void setCheckNodeCompatibility(CheckNodeCompatibility chkNodeComp) {
		this.chkNodeComp = chkNodeComp;
	}
	
	
	
	public FailureDatacenter(String name, DatacenterCharacteristics characteristics,
			VmAllocationPolicy vmAllocationPolicy, List<Storage> storageList, double schedulingInterval)
			throws Exception {
		super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Processes events or services that are available for this PowerDatacenter.
	 * 
	 * @param ev a Sim_event object
	 * @pre ev != null
	 * @post $none
	 */
	
	@Override	
	public void processEvent(SimEvent ev) {	

		switch (ev.getTag()) {	
		
			case CloudSimTags.FAILURE_OCCURRENCE_EVENT :			
				processFailureEvent((int)ev.getData());			
				break;
				
			case CloudSimTags.FAILURE_RECOVERY_EVENT:
				processRecoveryEvent((int)ev.getData());
				break;
				
			case CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_PERFECT_CHECKPOINTING :			
				processFailureEventWithPerfectCheckpointing((int)ev.getData());			
				break;	
				
			case CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_PERFECT_CHECKPOINTING:
				processRestartFromPerfectCheckpointEvent((int)ev.getData());
				break;
				
			case CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING :			
				processFailureEventWithCheckpointing((int)ev.getData());			
				break;	
				
			case CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING:
				processRecoveryEventWithCheckpointing((int)ev.getData());
				break;
				
			case CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING:
				processFailureWithMigrationCheckpointing((int)ev.getData());
				break;
				
			case CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING:
				processRecoveryWithMigrationCheckpointing((int)ev.getData());
				break;				

			case CloudSimTags.CREATE_CHECKPOINT:
				createCheckpoints((int)ev.getData());
				break;	
				
			case CloudSimTags.VM_MIGRATION:	
				processVmMigrate((int)ev.getData());
				break;
				
			case CloudSimTags.VM_MIGRATION_CHECKPOINTING:
				processVmMigrateWithCheckpointing((int)ev.getData());
				break;
				
			case CloudSimTags.CREATE_CHECKPOINT_WITH_MIGRATION:
				createCheckpointsWithMigration((int)ev.getData());
				break;
				
			case CloudSimTags.VM_CONSOLIDATION:
				processVMConsolidation((Host)ev.getData());	
				break;
			
			case CloudSimTags.VM_CONSOLIDATION_CHECKPOINTING:
				processVMConsolidationForCheckpointing((Host)ev.getData());
				break;
				
			case CloudSimTags.CREATE_VM_REPLICATION:
				createReplication((int)ev.getData());
				break;
			// other unknown tags are processed by this method
			default:
				//processOtherEvent(ev);
				super.processEvent(ev);
				break;
		}
	}
	//public void maplistCloudlettoVm(int vmId, int cletId)
		//{
			//Iterator itr = clvmList.iterator();
			//clvmList.put(vmId, cletId);
			
			//System.out.println("Message is getting called"
				//	+ "  " + clvmList.get(vmId));
		//}
		
		//public Hashtable<Integer, Integer>getmaplistCloudlettovm(){
			//return clvmList;
		//}
		
		
		
		/**
		 *  These two functions should be called only once otherwise 
		 *  failed VMs and Cloudlets will not be removed from the lists.
		 * 
		 */
		
		//public Hashtable<Integer, Integer> getCloudlettoVminfo(){
			//return(DatacenterBroker.getmaplistCloudlettovm());
		//}
		
		//public Hashtable<Integer, Integer> getUsertoCloudletinfo(){
			//return(DatacenterBroker.getmaplistUsertocloutlet());
		//}
	
		public List<? extends Host> Nodes;
		public List<? extends Host>hostList(){
			return(getHostList());
		}
		
	//	public List<? extends FailureHost> Nodes;
	//	public List<? extends FailureHost>hostList(){
	//		return(getHostList());
	//	}
		
		/**
		 * Process the failure events
		 * @param ev
		 * @param ack
		 */
		
		//Host fail1;
		List<Cloudlet> finishedcletList = new ArrayList<Cloudlet>();
		ArrayList<Vm> finishedvmList = new ArrayList<Vm>();
		//ArrayList<FailureVm> finishedvmList = new ArrayList<FailureVm>();
		int failID;
		HashMap<Host, ArrayList<Vm>> hostvmTable = new HashMap<Host, ArrayList<Vm>>();
		HashMap<Host, ArrayList<Integer>> hostvmIdTable = new HashMap<Host, ArrayList<Integer>>();
		
		//public ArrayList<FailureVm>failVMs = new ArrayList<FailureVm>();
		public ArrayList<Vm>failVMs = new ArrayList<Vm>();
		
		//public ArrayList<FailureVm>failVMstemp = new ArrayList<FailureVm>();
		public ArrayList<Vm>failVMstemp = new ArrayList<Vm>();
		
		public ArrayList<Integer> failVMsID = new ArrayList<Integer>();
		
		public ArrayList<Integer>failVMsIDtemp = new ArrayList<Integer>();
		
		public ArrayList<Integer>failedHosts = new ArrayList<Integer>();
		
		public HashMap<Integer, ArrayList<Integer>>failedVMCletMap = new HashMap<Integer, ArrayList<Integer>>();
		
		public ArrayList<Integer>cletsTempID;
		public ArrayList<Cloudlet>cletsTemp;
		
		public HashMap<Integer, Long>cletSoFarBeforeFailing = new HashMap<Integer, Long>();
		public HashMap<Integer, Integer>cletHostIdBeforeFailing = new HashMap<Integer, Integer>();
		public HashMap<Integer, HashMap<Integer, Integer>>hostVMPeMap = new HashMap<Integer, HashMap<Integer, Integer>>();
		public HashMap<Integer, HashMap<Integer, Double>>hostPeDownTimeMap = new HashMap<Integer, HashMap<Integer, Double>>();
		public HashMap<Integer, Integer>vmPeMap;
		public HashMap<Integer, Double>peDownTimeMap;
		public HashMap<Integer, Long>remainingCletLengthTable = new HashMap<Integer, Long>();
		int hostCounter = 0;
		ArrayList<Integer>failingHosts = new ArrayList<Integer>();
		ArrayList<Double>failureTime = new ArrayList<Double>();
		HashMap<Integer, Double>lastRecoveryTime = new HashMap<Integer, Double>();		
		public ArrayList<Double>timeBetweenFailure = new ArrayList<Double>();
		public ArrayList<Integer>hostPerTimeBetweenFailure = new ArrayList<Integer>();
		
		HashMap<Integer, Double>lastFailureTime = new HashMap<Integer, Double>();		
		public ArrayList<Double>timeToReturn = new ArrayList<Double>();
		public ArrayList<Integer>hostPerTimeToReturn = new ArrayList<Integer>();
		
	//	public HashMap<Integer, Double>cletSoFarMap = new HashMap<>();
		
		protected void processFailureEvent(int node){	
			updateCloudletProcessing();
			failingHosts.add(node);
			failureTime.add(CloudSim.clock());	
			//if(!lastRecoveryTime.containsKey(node)){
			//	lastRecoveryTime.put(node, CloudSim.clock());									
			//}
			//else{
			lastFailureTime.put(node, CloudSim.clock());
			
			if(lastRecoveryTime.containsKey(node)){	
				double recoveryTime;
				double timeBetweenFailureLocal;
				recoveryTime = lastRecoveryTime.get(node);
				timeBetweenFailureLocal = CloudSim.clock()-recoveryTime;
				timeBetweenFailure.add(timeBetweenFailureLocal);
				hostPerTimeBetweenFailure.add(node);
			}
			if(RunTimeConstants.test == true){	
			//	if(firstFailureFlag.get(node)== true){
			//		System.out.println("This is the first failure and will be ignored inorder to synchronize with the checkpointing functions");
			//		firstFailureFlag.put(node, false);					
			//	}
			//	else{
			//	if(RunTimeConstants.failureCorrelation == true && correlatedFailEventWithChkpt){}
				int counterLocal = 0;
				if(RunTimeConstants.failureCorrelation == true && RunTimeConstants.faultToleranceMechanism == 2 && RunTimeConstants.failureCorrelationWithRstrMig == true){
					ArrayList<Integer>correlatedNodes = new ArrayList<Integer>();					
					String nodeLocation;
					nodeLocation = freadGrid5000.getClusterNameForNode(node);
					correlatedNodes.addAll(kMeans.getCorrelatedNodes(nodeLocation, node));
					for(int i=0; i<correlatedNodes.size(); i++){
						if(correlatedNodes.get(i).equals(node)){
							correlatedNodes.remove(i);
							break;
						}
					}
					for(int i=0; i<correlatedNodes.size(); i++){
						if(failhostTable.containsKey(correlatedNodes.get(i))){
							Host hostLocal;
							int nodeID;
							nodeID = failhostTable.get(correlatedNodes.get(i));
							hostLocal = hostList().get(nodeID);						
							IdleTime.setExpectedFailingHost(hostLocal.getId(), true);
							for(int j=0; j<hostLocal.getNumberOfPes(); j++){
								int peID;
								peID = hostLocal.getPeList().get(j).getId();						
								IdleTime.setHostPeIdleTable(hostLocal.getId(), peID, true);
								IdleTime.setHostPeIdleClockTable(hostLocal.getId(), peID, CloudSim.clock());
							}
						}
					}
					
					for(int i=0; i<correlatedNodes.size(); i++){
						if(failhostTable.containsKey(correlatedNodes.get(i))){
							instantCorrelatedMigration.put(correlatedNodes.get(i), true);
							//counterLocal = freadGrid5000.getCorrelatedMigrationCounter();
							counterLocal = counterLocal + 1;
							//freadGrid5000.setCorrelatedMigraitonCounter(counterLocal);
							//processVmMigrate(correlatedNodes.get(i));
							sendNow(3, CloudSimTags.VM_MIGRATION, correlatedNodes.get(i));
						}		
					//	freadGrid5000.setCorrelatedMigraiton(false);
					}
					freadGrid5000.setCorrelatedMigraitonCounter(counterLocal);
				}
				
				if(RunTimeConstants.failureCorrelation == true && RunTimeConstants.faultToleranceMechanism == 2 && RunTimeConstants.failureCorrelationWithRstrChkpt == true){
					ArrayList<Integer>correlatedNodes = new ArrayList<Integer>();					
					String nodeLocation;
					nodeLocation = freadGrid5000.getClusterNameForNode(node);
					correlatedNodes.addAll(kMeans.getCorrelatedNodes(nodeLocation, node));
					for(int i=0; i<correlatedNodes.size(); i++){
						if(correlatedNodes.get(i).equals(node)){
							correlatedNodes.remove(i);
							break;
						}
					}
					for(int i=0; i<correlatedNodes.size(); i++){
						if(failhostTable.containsKey(correlatedNodes.get(i))){						
							instantCorrelatedCheckpoint.put(correlatedNodes.get(i), true);							
							sendNow(3, CloudSimTags.CREATE_CHECKPOINT, correlatedNodes.get(i));												
						}					
					}				
				}					
				if(correlatedFailEventWithChkpt.containsKey(node)){
					//correlatedFailEventWithChkpt.remove(node);
					
					processFailureEventWithCheckpointing(node);
				}
				else{				
					boolean flag_fail = false;
					int rand=0;
					Vm vm;
					int hostCount;	
					//FailureVm vm;
					//String usr;
					flag_record = true;   //This flag is being used in DatacenterBroker to record only the activities that happened after the occurrence of a failure. 
			
					if(lock_sufferedCletList == false){
						broker.setSufferedCloudletsList(); //This call is to copy all the cloudlets that are under processing while occurrence of first failure. 
						lock_sufferedCletList = true;
					}
				
					for(int j=0;j<hostList().size();j++){
						if(failhostTable.containsKey(node)){
							rand=failhostTable.get(node);
							break;
						}				
					}			
					fail = hostList().get(rand); // Node that is set to be failed			
					if(hostList().get(rand).isFailed()){					
						System.out.println( "Host " +node+ " has already been failed");	
						flag_fail=true;
					}			
					if(flag_fail==false){
						// If getCloudletReceivedList() is not empty then it means that the cloudlets have been processed without any failure. 
						// By checking the contents, it has been ensured that a failure will not occur after the completion of execution.
						// finishedcletList=Dbr.getCloudletReceivedList();				
						vmPeMap = new HashMap<Integer, Integer>();
						peDownTimeMap = new HashMap<Integer, Double>();
						System.out.println("Size of the cloudlet received list is " +broker.getCloudletReceivedList().size());
						//System.out.println("Size of cloudlet list is " +Dbr.getCloudletSubmittedList().size());
						if(broker.getCloudletReceivedList().size()<broker.getCloudletSubmittedList().size()){	
							Log.printLine("Failure has occurred at " +CloudSim.clock()+ " for host " +node);					
							System.out.println("Size of host list is " +hostList().size());
					
							// A seperate list for storing the node information is required because when a failure will occur, then the 
							// host from the list ""Nodes" will be removed rather than removing the original hostList(). When the node 
							// will be recovered then the node in hostList() will simply set be Recovered status. This will decrease
							// the work of re-provisioning and reallocation of the nodes. 					
					
							System.out.println("Id of the Failing Host in the provisioned nodes list is  #" +fail.getId());	// ID of the failing host							
							//System.out.println("FTA ID of the failing Host is #" +failNodes.get(fail.getId()));					
							/**
							 * This part is to turn off the idle physical machine inorder to save idle energy consumption
							 */														
							if(RunTimeConstants.predictionFlag == true && (RunTimeConstants.faultToleranceMechanism == 3) && RunTimeConstants.resourceTurnOffCase == true){
								if((IdleTime.expectedFailingHost.isEmpty()==false) && IdleTime.checkExpectedFailingHost(fail.getId())==true){
									IdleTime.setExpectedFailingHost(fail.getId(), false);
									List<Pe>peList = new ArrayList<Pe>();								
									boolean idleStatus;
									boolean clockStatus;								
									peList = fail.getPeList();
									for(int i=0; i<peList.size(); i++){		
										idleStatus = IdleTime.getHostPeIdleTable(fail.getId(), peList.get(i).getId());
										clockStatus = IdleTime.checkHostPeIdleClockTable(fail.getId(), peList.get(i).getId());
										if(idleStatus==true && clockStatus == true){					
											IdleTime.setHostPeIdleTimeTable(fail.getId(), peList.get(i).getId(), CloudSim.clock());
											IdleTime.removeHostPeIdleClockTable(fail.getId(), peList.get(i).getId());
										}
									}
									IdleTime.setHostActiveStatus(fail.getId(), false);															
									hostCount = IdleTime.getActiveHostCount();									
									HostCounter.setHostCount(hostCount);
									HostCounter.setClockList(CloudSim.clock());	
								}
							}		
							
							//hostList().get(rand).getVmList().size gives the number of virtual machines running on the failing host.					
							if(fail.getVmList().size()==0){								
								System.out.println("No virtual machine and cloudlets are running on the failing node");								
								System.out.println("Setting host to the state Fail");								
								hostvmTable.put(fail, null);								
								hostvmIdTable.put(fail, null);								
								fail.setFailed(true);								
								failedHosts.add(node); // Maintaining the list of failed hosts. 								
							}
							else{								
								totalFailureCount = totalFailureCount + 1;									
								failVMs = new ArrayList<Vm>();									
								//failVMs = new ArrayList<FailureVm>();									
								failVMsID = new ArrayList<Integer>();								
								System.out.println("Number of VMs running on failed host are " +fail.getVmList().size());									
								System.out.println("Virtual Machines running on the failed host are " +fail.getVmList());									
								//System.out.println("Utilization of a virtual machine is " +hostList().get(rand).getVmList().get(0).getTotalUtilizationOfCpu(CloudSim.clock()));													
								//int failVMsize = distVMs.size();
								List<Pe>peList = new ArrayList<Pe>();
								for(int i=0;i<fail.getVmList().size();i++){										
									vm = fail.getVmList().get(i); //Getting a VM at an index i.							
									peList = fail.getVmScheduler().getPesAllocatedForVM(vm);										
									for(int x=0; x<peList.size(); x++){									
										System.out.println("PE Id for VM " +vm.getId()+ " is "+peList.get(x).getId());
										vmPeMap.put(vm.getId(), peList.get(x).getId());
										peDownTimeMap.put(peList.get(x).getId(), CloudSim.clock());
									}
									//	usr=hostList().get(rand).getVmList().get(i).getUid();		// Getting User for VM at an index i.											
									System.out.println("Virtual Machine ID running on the failing host is " +vm.getId()); // Id of the VM at an index vm.																					 
									failVMs.add(fail.getVmList().get(i)); //List of the failing virtual machines							
									failVMsID.add(fail.getVmList().get(i).getId()); //List of the IDs of failing virtual machines										
									DownTimeTable.setFailTimeTable(vm, CloudSim.clock()); // Saving the failing time of a virtual machine									
								}									
								hostvmTable.put(fail, failVMs); // Table of failed hosts and their corresponding failed virtual machines									
								hostvmIdTable.put(fail, failVMsID); // Table of failed hosts and their corresponding failed virtual machine IDs				
								usrclList.putAll(broker.getmaplistUsertocloudlet()); // This table is storing Cloudlet IDs and corresponding User IDs					
								hostVMPeMap.put(fail.getId(), vmPeMap);										
								hostPeDownTimeMap.put(fail.getId(), peDownTimeMap);										
								for(int i=0;i<failVMsID.size();i++){			
									int size; 
									cletsTempID = new ArrayList<Integer>();										
									size = ((FailureCloudletSchedulerTimeShared)failVMs.get(i).getCloudletScheduler()).getCloudletExecList().size();
									for(int j=0;j<size;j++){
										cletsTempID.add(((FailureCloudletSchedulerTimeShared)failVMs.get(i).getCloudletScheduler()).getCloudletExecList().get(j).getCloudletId());										
									}											
									failedVMCletMap.put(failVMsID.get(i), cletsTempID);
									System.out.println("IDs of Cloudlets running on the failing virtual machine are " );											
									for(int j=0;j<cletsTempID.size();j++){												
										System.out.println(+cletsTempID.get(j));	
									//	if(cletsTempID.get(j)==1631){
									//		System.out.println("Check");
									//	}
									}									
									if(RunTimeConstants.faultToleranceMechanism == 3){						
										for(int j=0;j<cletsTempID.size();j++){												
											long tempSoFar;
											long tempRemaining;
											long tempLength;
											if(cletLastRemainingMap.containsKey(cletsTempID.get(j))&& cletMigrated.containsKey(cletsTempID.get(j))){														
												if(cletSoFarBeforeFailing.containsKey(cletsTempID.get(j))){
													tempRemaining = ((FailureCloudletSchedulerTimeShared)failVMs.get(i).getCloudletScheduler()).getCloudletExecList().get(j).getRemainingCloudletLength();
													System.out.println("Temporary remaining length of cloudlet " + cletsTempID.get(j)+ " is " +tempRemaining);
													tempLength = ((FailureCloudletSchedulerTimeShared)failVMs.get(i).getCloudletScheduler()).getCloudletExecList().get(j).getCloudletLength();
													System.out.println("Temporary length of cloudlet " + cletsTempID.get(j)+ " is " +tempLength);
													tempSoFar = tempLength - tempRemaining;
													cletSoFarTempMap.put(cletsTempID.get(j), tempSoFar);
													System.out.println("So far execution length of cloudlet " + cletsTempID.get(j)+ " after the resubmission because of failure is " +tempSoFar);															
													tempSoFar = tempSoFar + cletSoFarBeforeFailing.get(cletsTempID.get(j));
													System.out.println("So far execution length of cloudlet " + cletsTempID.get(j)+ " after the resubmission and since the last migration is " +tempSoFar);
													cletSoFarBeforeFailing.put(cletsTempID.get(j), tempSoFar);
													cletLastRemainingMap.put(cletsTempID.get(j), tempRemaining);
												}
												else{
													tempRemaining = ((FailureCloudletSchedulerTimeShared)failVMs.get(i).getCloudletScheduler()).getCloudletExecList().get(j).getRemainingCloudletLength();
													System.out.println("Temporary remaining length of cloudlet " + cletsTempID.get(j)+ " is " +tempRemaining);
													tempLength = ((FailureCloudletSchedulerTimeShared)failVMs.get(i).getCloudletScheduler()).getCloudletExecList().get(j).getCloudletLength();
													System.out.println("Temporary length of cloudlet " + cletsTempID.get(j)+ " is " +tempLength);
													tempSoFar = cletLastRemainingMap.get(cletsTempID.get(j))-tempRemaining;												
													cletSoFarTempMap.put(cletsTempID.get(j), tempSoFar);
													System.out.println("So far execution length of cloudlet " + cletsTempID.get(j)+ " since last migration is and before the first failure is " +tempSoFar);
													cletSoFarBeforeFailing.put(cletsTempID.get(j), tempSoFar);
													cletLastRemainingMap.put(cletsTempID.get(j), tempRemaining);
												}
											}												
										}
									}											
									for(int j=0;j<cletsTempID.size();j++){												
										DownTimeTable.setCletFailTimeTable(cletsTempID.get(j), CloudSim.clock());
										processCloudletCancel(cletsTempID.get(j), usrclList.get(cletsTempID.get(j)), failVMsID.get(i));
										//migrationForCloudlets.put(cletsTempID.get(j), 0L);
										usrclList.remove(cletsTempID.get(j));
									}										 
									for(int k=0;k<getVmList().size();k++){
										if(getVmList().get(k)==failVMs.get(i)){									 				
											getVmList().remove(k);
											break;
										}
									}										 
								}						
								System.out.println("Destroying all the virtual machines running on the failing host");	
								for(int i=0; i<failVMs.size(); i++){									
									hostList().get(rand).vmDestroy(failVMs.get(i));											
								}																	
								System.out.println("Setting host to the state Fail");								
								failedHosts.add(node);										
								hostList().get(rand).setFailed(true);								
								}		
							
								IdleTime.setHostActiveStatus(fail.getId(), false);															
								hostCount = IdleTime.getActiveHostCount();									
								HostCounter.setHostCount(hostCount);
								HostCounter.setClockList(CloudSim.clock());								
							}						
							else{
								System.out.println("Processing of all the cloudlets has been finished without a failure by "+CloudSim.clock());
							}
						}
					}
				}
			else{ //This else is for non-testing simulation
				int counterLocal = 0;
				if(RunTimeConstants.failureCorrelation == true && RunTimeConstants.faultToleranceMechanism == 2 && RunTimeConstants.failureCorrelationWithRstrMig == true){
					ArrayList<Integer>correlatedNodes = new ArrayList<Integer>();					
					String nodeLocation;
					nodeLocation = freadGrid5000.getClusterNameForNode(node);
					correlatedNodes.addAll(kMeans.getCorrelatedNodes(nodeLocation, node));
					for(int i=0; i<correlatedNodes.size(); i++){
						if(correlatedNodes.get(i).equals(node)){
							correlatedNodes.remove(i);
							break;
						}
					}
					for(int i=0; i<correlatedNodes.size(); i++){
						if(failhostTable.containsKey(correlatedNodes.get(i))){
							Host hostLocal;
							int nodeID;
							nodeID = failhostTable.get(correlatedNodes.get(i));
							hostLocal = hostList().get(nodeID);						
							IdleTime.setExpectedFailingHost(hostLocal.getId(), true);
							for(int j=0; j<hostLocal.getNumberOfPes(); j++){
								int peID;
								peID = hostLocal.getPeList().get(j).getId();						
								IdleTime.setHostPeIdleTable(hostLocal.getId(), peID, true);
								IdleTime.setHostPeIdleClockTable(hostLocal.getId(), peID, CloudSim.clock());
							}
						}
					}
					
					for(int i=0; i<correlatedNodes.size(); i++){
						if(failhostTable.containsKey(correlatedNodes.get(i))){
							instantCorrelatedMigration.put(correlatedNodes.get(i), true);							
							counterLocal = counterLocal + 1;							
							sendNow(3, CloudSimTags.VM_MIGRATION, correlatedNodes.get(i));
						}				
					}
					freadGrid5000.setCorrelatedMigraitonCounter(counterLocal);
				}				
				if(RunTimeConstants.failureCorrelation == true && RunTimeConstants.faultToleranceMechanism == 2 && RunTimeConstants.failureCorrelationWithRstrChkpt == true){
					ArrayList<Integer>correlatedNodes = new ArrayList<Integer>();					
					String nodeLocation;
					nodeLocation = freadGrid5000.getClusterNameForNode(node);
					correlatedNodes.addAll(kMeans.getCorrelatedNodes(nodeLocation, node));
					for(int i=0; i<correlatedNodes.size(); i++){
						if(correlatedNodes.get(i).equals(node)){
							correlatedNodes.remove(i);
							break;
						}
					}
					for(int i=0; i<correlatedNodes.size(); i++){
						if(failhostTable.containsKey(correlatedNodes.get(i))){						
							instantCorrelatedCheckpoint.put(correlatedNodes.get(i), true);							
							sendNow(3, CloudSimTags.CREATE_CHECKPOINT, correlatedNodes.get(i));												
						}					
					}				
				}
				if(correlatedFailEventWithChkpt.containsKey(node)){					
					processFailureEventWithCheckpointing(node);
				}
				else{
					boolean flag_fail = false;
					int rand=0;
					Vm vm;
					int hostCount;					
					flag_record = true;   //This flag is being used in DatacenterBroker to record only the activities that happened after the occurrence of a failure. 
		
					if(lock_sufferedCletList == false){
						broker.setSufferedCloudletsList(); //This call is to copy all the cloudlets that are under processing while occurrence of first failure. 
						lock_sufferedCletList = true;
					}			
					for(int j=0;j<hostList().size();j++){
						if(failhostTable.containsKey(node)){
							rand=failhostTable.get(node);
							break;
						}				
					}			
					fail = hostList().get(rand); // Node that is set to be failed			
					if(hostList().get(rand).isFailed()){				
						flag_fail=true;
					}			
					if(flag_fail==false){								
						vmPeMap = new HashMap<Integer, Integer>();
						peDownTimeMap = new HashMap<Integer, Double>();				
						if(broker.getCloudletReceivedList().size()<broker.getCloudletSubmittedList().size()){						
							if(RunTimeConstants.predictionFlag == true && (RunTimeConstants.faultToleranceMechanism == 3) && RunTimeConstants.resourceTurnOffCase == true){
								if((IdleTime.expectedFailingHost.isEmpty()==false) && IdleTime.checkExpectedFailingHost(fail.getId())==true){
									IdleTime.setExpectedFailingHost(fail.getId(), false);
									List<Pe>peList = new ArrayList<Pe>();								
									boolean idleStatus;
									boolean clockStatus;								
									peList = fail.getPeList();
									for(int i=0; i<peList.size(); i++){		
										idleStatus = IdleTime.getHostPeIdleTable(fail.getId(), peList.get(i).getId());
										clockStatus = IdleTime.checkHostPeIdleClockTable(fail.getId(), peList.get(i).getId());
										if(idleStatus==true && clockStatus == true){					
											IdleTime.setHostPeIdleTimeTable(fail.getId(), peList.get(i).getId(), CloudSim.clock());
											IdleTime.removeHostPeIdleClockTable(fail.getId(), peList.get(i).getId());
										}
									}
									IdleTime.setHostActiveStatus(fail.getId(), false);															
									hostCount = IdleTime.getActiveHostCount();									
									HostCounter.setHostCount(hostCount);
									HostCounter.setClockList(CloudSim.clock());	
								}
							}				
									
							if(fail.getVmList().size()==0){													
								hostvmTable.put(fail, null);								
								hostvmIdTable.put(fail, null);								
								fail.setFailed(true);								
								failedHosts.add(node); // Maintaining the list of failed hosts. 								
							}
							else{								
								totalFailureCount = totalFailureCount + 1;									
								failVMs = new ArrayList<Vm>();															
								failVMsID = new ArrayList<Integer>();						
								List<Pe>peList = new ArrayList<Pe>();
								for(int i=0;i<fail.getVmList().size();i++){										
									vm = fail.getVmList().get(i); //Getting a VM at an index i.							
									peList = fail.getVmScheduler().getPesAllocatedForVM(vm);										
									for(int x=0; x<peList.size(); x++){								
										vmPeMap.put(vm.getId(), peList.get(x).getId());
										peDownTimeMap.put(peList.get(x).getId(), CloudSim.clock());
									}																													 
									failVMs.add(fail.getVmList().get(i)); //List of the failing virtual machines							
									failVMsID.add(fail.getVmList().get(i).getId()); //List of the IDs of failing virtual machines										
									DownTimeTable.setFailTimeTable(vm, CloudSim.clock()); // Saving the failing time of a virtual machine									
								}									
								hostvmTable.put(fail, failVMs); // Table of failed hosts and their corresponding failed virtual machines									
								hostvmIdTable.put(fail, failVMsID); // Table of failed hosts and their corresponding failed virtual machine IDs				
								usrclList.putAll(broker.getmaplistUsertocloudlet()); // This table is storing Cloudlet IDs and corresponding User IDs					
								hostVMPeMap.put(fail.getId(), vmPeMap);										
								hostPeDownTimeMap.put(fail.getId(), peDownTimeMap);										
								for(int i=0;i<failVMsID.size();i++){			
									int size; 
									cletsTempID = new ArrayList<Integer>();										
									size = ((FailureCloudletSchedulerTimeShared)failVMs.get(i).getCloudletScheduler()).getCloudletExecList().size();
									for(int j=0;j<size;j++){
										cletsTempID.add(((FailureCloudletSchedulerTimeShared)failVMs.get(i).getCloudletScheduler()).getCloudletExecList().get(j).getCloudletId());										
									}											
									failedVMCletMap.put(failVMsID.get(i), cletsTempID);																	
									if(RunTimeConstants.faultToleranceMechanism == 3){						
										for(int j=0;j<cletsTempID.size();j++){												
											long tempSoFar;
											long tempRemaining;
											long tempLength;
											if(cletLastRemainingMap.containsKey(cletsTempID.get(j))&& cletMigrated.containsKey(cletsTempID.get(j))){														
												if(cletSoFarBeforeFailing.containsKey(cletsTempID.get(j))){
													tempRemaining = ((FailureCloudletSchedulerTimeShared)failVMs.get(i).getCloudletScheduler()).getCloudletExecList().get(j).getRemainingCloudletLength();												
													tempLength = ((FailureCloudletSchedulerTimeShared)failVMs.get(i).getCloudletScheduler()).getCloudletExecList().get(j).getCloudletLength();												
													tempSoFar = tempLength - tempRemaining;
													cletSoFarTempMap.put(cletsTempID.get(j), tempSoFar);																										
													tempSoFar = tempSoFar + cletSoFarBeforeFailing.get(cletsTempID.get(j));												
													cletSoFarBeforeFailing.put(cletsTempID.get(j), tempSoFar);
													cletLastRemainingMap.put(cletsTempID.get(j), tempRemaining);
												}
												else{
													tempRemaining = ((FailureCloudletSchedulerTimeShared)failVMs.get(i).getCloudletScheduler()).getCloudletExecList().get(j).getRemainingCloudletLength();												
													tempLength = ((FailureCloudletSchedulerTimeShared)failVMs.get(i).getCloudletScheduler()).getCloudletExecList().get(j).getCloudletLength();												
													tempSoFar = cletLastRemainingMap.get(cletsTempID.get(j))-tempRemaining;												
													cletSoFarTempMap.put(cletsTempID.get(j), tempSoFar);												
													cletSoFarBeforeFailing.put(cletsTempID.get(j), tempSoFar);
													cletLastRemainingMap.put(cletsTempID.get(j), tempRemaining);
												}
											}												
										}
									}											
									for(int j=0;j<cletsTempID.size();j++){												
										DownTimeTable.setCletFailTimeTable(cletsTempID.get(j), CloudSim.clock());
										processCloudletCancel(cletsTempID.get(j), usrclList.get(cletsTempID.get(j)), failVMsID.get(i));									
										usrclList.remove(cletsTempID.get(j));
									}										 
									for(int k=0;k<getVmList().size();k++){
										if(getVmList().get(k)==failVMs.get(i)){									 				
											getVmList().remove(k);
											break;
										}
									}										 
								}						
								for(int i=0; i<failVMs.size(); i++){									
									hostList().get(rand).vmDestroy(failVMs.get(i));											
								}											
								failedHosts.add(node);										
								hostList().get(rand).setFailed(true);								
								}					
								IdleTime.setHostActiveStatus(fail.getId(), false);															
								hostCount = IdleTime.getActiveHostCount();									
								HostCounter.setHostCount(hostCount);
								HostCounter.setClockList(CloudSim.clock());								
							}					
						}
					}
				}					
			}
		
		
		protected void processFailureEventWithReplication(int node){
			updateCloudletProcessing();
			if(RunTimeConstants.test == true){
				boolean flag_fail = false;
				int rand=0;
				Vm vm;
				int hostCount;					
				flag_record = true; 
				if(lock_sufferedCletList == false){
					broker.setSufferedCloudletsList(); //This call is to copy all the cloudlets that are under processing while occurrence of first failure. 
					lock_sufferedCletList = true;
				}			
				for(int j=0;j<hostList().size();j++){
					if(failhostTable.containsKey(node)){
						rand=failhostTable.get(node);
						break;
					}				
				}			
				failingHost = hostList().get(rand); // Node that is set to be failed								
				if(hostList().get(rand).isFailed()){				
					flag_fail=true;
				}	
				if(flag_fail == false){
					vmPeMap = new HashMap<Integer, Integer>();
					peDownTimeMap = new HashMap<Integer, Double>();
					System.out.println("Size of the cloudlet received list is " +broker.getCloudletReceivedList().size());					
					if(broker.getCloudletReceivedList().size()<broker.getCloudletSubmittedList().size()){	
						Log.printLine("Failure has occurred at " +CloudSim.clock()+ " for host " +node);					
						System.out.println("Size of host list is " +hostList().size());
						if(fail.getVmList().size()==0){								
							System.out.println("No virtual machine and cloudlets are running on the failing node");								
							System.out.println("Setting host to the state Fail");								
							//hostvmTable.put(failingHost, null);								
							//hostvmIdTable.put(failingHost, null);								
							failingHost.setFailed(true);								
							failedHosts.add(node); // Maintaining the list of failed hosts. 								
						}
						else{
							totalFailureCount = totalFailureCount + 1;									
							failVMs = new ArrayList<Vm>();																
							failVMsID = new ArrayList<Integer>();								
							System.out.println("Number of VMs running on failed host are " +fail.getVmList().size());									
							System.out.println("Virtual Machines running on the failed host are " +fail.getVmList());						
							List<Pe>peList = new ArrayList<Pe>();
							for(int i=0;i<fail.getVmList().size();i++){										
								vm = fail.getVmList().get(i); //Getting a VM at an index i.							
								peList = fail.getVmScheduler().getPesAllocatedForVM(vm);										
								for(int x=0; x<peList.size(); x++){									
									System.out.println("PE Id for VM " +vm.getId()+ " is "+peList.get(x).getId());
									vmPeMap.put(vm.getId(), peList.get(x).getId());
									peDownTimeMap.put(peList.get(x).getId(), CloudSim.clock());
								}												
								System.out.println("Virtual Machine ID running on the failing host is " +vm.getId()); // Id of the VM at an index vm.																					 
								failVMs.add(fail.getVmList().get(i)); //List of the failing virtual machines							
								failVMsID.add(fail.getVmList().get(i).getId()); //List of the IDs of failing virtual machines										
								DownTimeTable.setFailTimeTable(vm, CloudSim.clock()); // Saving the failing time of a virtual machine									
							}									
							hostvmTable.put(fail, failVMs); // Table of failed hosts and their corresponding failed virtual machines									
							hostvmIdTable.put(fail, failVMsID); // Table of failed hosts and their corresponding failed virtual machine IDs				
							usrclList.putAll(broker.getmaplistUsertocloudlet()); // This table is storing Cloudlet IDs and corresponding User IDs					
							hostVMPeMap.put(fail.getId(), vmPeMap);										
							hostPeDownTimeMap.put(fail.getId(), peDownTimeMap);										
							for(int i=0;i<failVMsID.size();i++){			
								int size; 
								cletsTempID = new ArrayList<Integer>();										
								size = ((FailureCloudletSchedulerTimeShared)failVMs.get(i).getCloudletScheduler()).getCloudletExecList().size();
								for(int j=0;j<size;j++){
									cletsTempID.add(((FailureCloudletSchedulerTimeShared)failVMs.get(i).getCloudletScheduler()).getCloudletExecList().get(j).getCloudletId());										
								}											
								failedVMCletMap.put(failVMsID.get(i), cletsTempID);
								System.out.println("IDs of Cloudlets running on the failing virtual machine are " );											
								for(int j=0;j<cletsTempID.size();j++){												
									System.out.println(+cletsTempID.get(j));					
								}
								for(int j=0;j<cletsTempID.size();j++){												
									DownTimeTable.setCletFailTimeTable(cletsTempID.get(j), CloudSim.clock());
									processCloudletCancel(cletsTempID.get(j), usrclList.get(cletsTempID.get(j)), failVMsID.get(i));
									//migrationForCloudlets.put(cletsTempID.get(j), 0L);
									usrclList.remove(cletsTempID.get(j));
								}										 
								for(int k=0;k<getVmList().size();k++){
									if(getVmList().get(k)==failVMs.get(i)){									 				
										getVmList().remove(k);
										break;
									}
								}								
							}
							System.out.println("Destroying all the virtual machines running on the failing host");	
							for(int i=0; i<failVMs.size(); i++){									
								hostList().get(rand).vmDestroy(failVMs.get(i));											
							}																	
								System.out.println("Setting host to the state Fail");								
								failedHosts.add(node);										
								hostList().get(rand).setFailed(true);								
						}								
						IdleTime.setHostActiveStatus(fail.getId(), false);															
						hostCount = IdleTime.getActiveHostCount();									
						HostCounter.setHostCount(hostCount);
						HostCounter.setClockList(CloudSim.clock());	
					}			
				}
			}			
		}
		
		
		PerfectCheckpointing pchkpt = new PerfectCheckpointing();	
		CloudletStatus cletstatus = new CloudletStatus();
		ArrayList<Double> reliabilityList = new ArrayList<Double>();
		
		protected void processFailureEventWithPerfectCheckpointing(int node){
			
			updateCloudletProcessing(); // To process a cloudlet manually this function need to be called otherwise, in this case process cloudlet length will get calculated wrong. 
				
				boolean flag = false;
				int rand=0;
				boolean flag2;
				Vm vm;
				String usr;		
					for(int j=0;j<hostList().size();j++){
						if(failhostTable.containsKey(node)){
							rand=failhostTable.get(node);
							break;
						}
					
					}
				
					fail = hostList().get(rand); // Node that is set to be failed		
						
					if(hostList().get(rand).isFailed()){					
						System.out.println( "Host " +node+ " has already been failed");	
						flag=true;
						}			
				
				
				if(flag==false){
			    //If getCloudletReceivedList() is not empty then it means that the cloudlets have been processed without any failure. 
			    // By checking the contents, it has been ensured that a failure will not occur after the completion of execution.
				//finishedcletList=Dbr.getCloudletReceivedList();
				System.out.println("Size of the cloudlet received list is " +broker.getCloudletReceivedList().size());
				//System.out.println("Size of cloudlet list is " +Dbr.getCloudletSubmittedList().size());
				if(broker.getCloudletReceivedList().size() < broker.getCloudletSubmittedList().size()){							
					
					Log.printLine("Failure has occurred at " +CloudSim.clock()+ " for host " +node);
						
					System.out.println("Size of host list is " +hostList().size());				
				
					System.out.println("Id of the Failing Host in the provisioned nodes list is  #" +hostList().get(rand).getId());	// ID of the failing host							
					
					if(hostList().get(rand).getVmList().size()==0){
									
						System.out.println("No virtual machine and cloudlets are running on the failing node");
								
						System.out.println("Setting host to the state Fail");
									
						hostvmTable.put(hostList().get(rand), null);
									
						hostvmIdTable.put(hostList().get(rand), null);
									
						hostList().get(rand).setFailed(true);
								
						failedHosts.add(node); // Maintaining the list of failed hosts. 
									
						}
					else{	
						totalFailureCount = totalFailureCount + 1;
						
						failVMs = new ArrayList<Vm>();
						
						failVMsID = new ArrayList<Integer>();
										
						System.out.println("Number of VMs running on failed host are " +hostList().get(rand).getVmList().size());	
										
						System.out.println("Virtual Machines running on the failed host are " +hostList().get(rand).getVmList());
																							
						for(int i=0;i<hostList().get(rand).getVmList().size();i++){	// Running upto the number of virtual machines running on the failing host
											
							vm = hostList().get(rand).getVmList().get(i); //Getting a VM at an index i.
													
						//	usr = hostList().get(rand).getVmList().get(i).getUid();		// Getting User for VM at an index i.			
													
							System.out.println("Virtual Machine ID running on the failing host is " +vm.getId()); // Id of the VM at an index vm.
							
							//System.out.println("CPU utilization of a VM " +vm.getId()+ " is " +vm.getTotalUtilizationOfCpu(CloudSim.clock()));
																						 
							failVMs.add(vm); //List of the failing virtual machines 									
											
							failVMsID.add(vm.getId()); //List of the IDs of failing virtual machines
							
							DownTimeTable.setFailTimeTable(vm, CloudSim.clock()); // Saving the failing time of a virtual machine					
											
							}		
											
						hostvmTable.put(hostList().get(rand), failVMs); // Table of failed hosts and their corresponding failed virtual machines
										
						hostvmIdTable.put(hostList().get(rand), failVMsID); // Table of failed hosts and their corresponding failed virtual machine IDs		
																								
					//	clvmList.putAll(broker.getmaplistCloudlettovm()); // This table is storing VM IDs and corresponding Cloudlet IDs 
												
						usrclList.putAll(broker.getmaplistUsertocloudlet()); // This table is storing Cloudlet IDs and corresponding User IDs 
												
						for(int i=0;i<failVMsID.size();i++){
							flag2 = false;
							int size;
						//	if(clvmList.containsKey(failVMsID.get(i))){
							
							cletsTempID = new ArrayList<Integer>();
							
							size = ((FailureCloudletSchedulerTimeShared)failVMs.get(i).getCloudletScheduler()).getCloudletExecList().size();
							
							for(int j=0;j<size;j++){
								cletsTempID.add(((FailureCloudletSchedulerTimeShared)failVMs.get(i).getCloudletScheduler()).getCloudletExecList().get(j).getCloudletId());												
							}
							
							failedVMCletMap.put(failVMsID.get(i), cletsTempID);
							//	System.out.println("ID of a Cloudlet running on the failing virtual machine is " +clvmList.get(failVMsID.get(i)));
							System.out.println("IDs of a Cloudlet running on the failing virtual machine are " );
							
							
							for(int j=0;j<size;j++){
								 System.out.println(cletsTempID.get(j));												 
							 }
							
							//	if(usrclList.containsKey(clvmList.get(failVMsID.get(i)))){
									
							//		System.out.println("User corresponding to the failing cloudlet is " +usrclList.get(clvmList.get(failVMsID.get(i))));
							//		}						
							for(int j=0; j<size; j++){
								
								//Cloudlet failingCloudlet;
														
								//failingCloudlet = broker.getidtoCloudletLst().get(clvmList.get(failVMsID.get(i)));			
										
										long sofarCletLength;
										
										long remainingCletLength;
										
										remainingCletLength = CloudletStatus.getremainingCloudlet(cletsTempID.get(j));
										
										//sofarCletLength = failingCloudlet.getCloudletLength() - remainingCletLength;
										
										sofarCletLength = CloudletStatus.getsofarCloudlet(cletsTempID.get(j));
										
										System.out.println("Cloudlet #" +cletsTempID.get(j)+ " executed so far is " +sofarCletLength);
									
										System.out.println("Cloudlet #" +cletsTempID.get(j)+ " remaining length is " +remainingCletLength);										
										
										//System.out.println("Cloudlet executed so far is " +failingCloudlet.getCloudletFinishedSoFar());
													
										//updateCloudletProcessing();
																
										//remainingCletLength = (long) (failingCloudlet.getCloudletLength() - CloudSim.clock()*1000);
								
										//cletstatus.setremainingCloudlet(failingCloudlet, remainingCletLength);		
										
										//remainingCletLength = failingCloudlet.getCloudletLength() - (failingCloudlet.getCloudletFinishedSoFar() * Consts.MILLION);
								
										pchkpt.setCheckpoints(cletsTempID.get(j), CloudSim.clock());						
								
										failed_time = CloudSim.clock();
										
										DownTimeTable.setFailTimeTable(failVMs.get(i), failed_time);
										
										processCloudletCancel(cletsTempID.get(j), usrclList.get(cletsTempID.get(j)), failVMsID.get(i));
										
										
										//processCloudletPause(clvmList.get(failVMsID.get(i)), usrclList.get(clvmList.get(failVMsID.get(i))), failVMsID.get(i), false);
																		
										usrclList.remove(cletsTempID.get(j)); // Removing the cloudlets (keys) from the hashtable																			
																											
							  //This bracket is for cloudlet cancel 	
						}		
						//	}								 
																						 
							for(int k=0;k<getVmList().size();k++){
								
								if(getVmList().get(k)==failVMs.get(i)){
									
									getVmList().remove(k);
											 				
									}
								}
											 
						}											
													
						System.out.println("Destroying all the virtual machines running on the failing host");
													
						hostList().get(rand).vmDestroyAll();																				
																																	
						System.out.println("Setting host to the state Fail");
											
						failedHosts.add(node);
											
						hostList().get(rand).setFailed(true);								
						}																										
										
					}
							
					else{
						
						System.out.println("Processing of all the cloudlets has been finished without a failure");
						
						}
				
					}
							
			}
			
		
		boolean check;
		List<Cloudlet> recoveringCloudletList = new ArrayList<Cloudlet>();
		boolean lock = false;
		List<Double> failedCletAverageExecTime = new ArrayList<Double>();
		UtilizationModel utilizationModelFull = new UtilizationModelFull();
		double utilization;
		List<Double>mipsShare;	
		
		protected void processRecoveryEvent(int node){
			if(RunTimeConstants.test == true)
			{		
			//	if(firstRecoveryFlag.get(node)==true){
			//		System.out.println("This event is for first recovery which needs to be ignored");
			//		firstRecoveryFlag.put(node, false);
			//	}
			//	else{
				lastRecoveryTime.put(node, CloudSim.clock());	
				if(lastFailureTime.containsKey(node)){	
					double failureTime;
					double timeToReturnLocal;
					failureTime = lastFailureTime.get(node);
					timeToReturnLocal = CloudSim.clock()-failureTime;
					timeToReturn.add(timeToReturnLocal);
					hostPerTimeToReturn.add(node);
				}
				
				if(correlatedRecoveryEventWithChkpt.containsKey(node)){
				//	if(node == 540){
				//		System.out.println("Check");
				//	}
					processRecoveryEventWithCheckpointing(node);
				}
				else{
					updateCloudletProcessing();					
					Host recoveringHost;		
					int hostCount;
					ArrayList<Cloudlet> recoveringCloudlet;		
					recoveringHost = hostList().get(failhostTable.get(node));			
					if(recoveringHost.isFailed()){							
						System.out.println("This recovery was scheduled at " +CloudSim.clock());			
						System.out.println("Host " +recoveringHost+ " with ID " +recoveringHost.getId()+ "  has been recovered from a failure");
						recoveringHost.setFailed(false);					
						if(recoveringHost.getDatacenter()==null){
							recoveringHost.setDatacenter(this);
						}	
						
						// Generally, if a node is failed then it will not consume any energy. This module is only for the case
						// when the node is sitting idle and has been marked as an expected failing host due to the prediction results. 
						// After the occurrence of failure this node will be available and sitting idle and waiting 
						// to get allocated to VMs.
						
						
						if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 3 && RunTimeConstants.resourceTurnOffCase == false){
							if((IdleTime.expectedFailingHost.isEmpty()==false) && IdleTime.checkExpectedFailingHost(recoveringHost.getId())==true){
								IdleTime.setExpectedFailingHost(recoveringHost.getId(), false);					
								for(int i=0; i<recoveringHost.getNumberOfPes(); i++){
									int peID;
									peID = recoveringHost.getPeList().get(i).getId();
									boolean idleStatus;
									idleStatus = IdleTime.getHostPeIdleTable(recoveringHost.getId(), peID);
									if(idleStatus == true){
										IdleTime.setHostPeIdleTable(recoveringHost.getId(), peID, false);
										IdleTime.setHostPeIdleTimeTable(recoveringHost.getId(), peID, CloudSim.clock());						
									}							
								}					
							}				
						}
						for(int i=0;i<failedHosts.size();i++){
							if(failedHosts.get(i)==node){
								failedHosts.remove(i);
							}
						}		
						if(hostvmTable.containsKey(recoveringHost)){			
							failVMs = new ArrayList<Vm>();
							failVMsID = new ArrayList<Integer>();				
							failVMs=hostvmTable.get(recoveringHost);
							failVMsID = hostvmIdTable.get(recoveringHost);				
							if(failVMs == null){
								System.out.println("No virtual machines were running on the recovered host");								
							}
							else{
								
								IdleTime.setHostActiveStatus(recoveringHost.getId(), true);															
								hostCount = IdleTime.getActiveHostCount();									
								HostCounter.setHostCount(hostCount);
								HostCounter.setClockList(CloudSim.clock());		
								
								int size=failVMs.size();
								System.out.println("Number of VMs were running on host " +recoveringHost+ " of ID "+ recoveringHost.getId()+ " are " +size);
								int sizeCletList;
								vmPeMap = new HashMap<Integer, Integer>();
								vmPeMap = hostVMPeMap.get(recoveringHost.getId());				
								peDownTimeMap = new HashMap<Integer, Double>();
								peDownTimeMap = hostPeDownTimeMap.get(recoveringHost.getId());
								Set<Integer>peKeySet = new HashSet<Integer>();
								peKeySet = peDownTimeMap.keySet();
								ArrayList<Integer>peKeySetList = new ArrayList<Integer>(peKeySet);				
								for(int i=0; i<peDownTimeMap.size(); i++){
									double downTime;				
									downTime = CloudSim.clock() - peDownTimeMap.get(peKeySetList.get(i));
									IdleTime.setHostPeDownTime(recoveringHost.getId(), peKeySetList.get(i), downTime);
								}
								hostPeDownTimeMap.remove(recoveringHost.getId());				
								for(int i=0;i<size;i++){
									//check=true;	// Flag to check whether the cloudlet is present in the received list or not. 			
									//if(DatacenterBroker.getmaplistCloudlettovm().contains(failVMsID.get(i))){
									//System.out.println("Cloudlet corresponding to the recreated VM is " +DatacenterBroker.getidtoCloudletLst().get(failVMs.get(i).getId()));
									//sizeCletList = ((FailureCloudletSchedulerTimeShared)failVMs.get(i).getCloudletScheduler()).getCloudletExecList().size();
									sizeCletList = failedVMCletMap.get(failVMsID.get(i)).size();
									cletsTempID = new ArrayList<Integer>();			
									cletsTempID = failedVMCletMap.get(failVMsID.get(i));									
									failedVMCletMap.remove(failVMsID.get(i));
									failVMs.get(i).setBeingInstantiated(true); //This matters. If this variable will not set to true then the reallocation of resources will not happen and estimatedFinishingTime of a corresponding cloudlet will set to infinity.						
									recoveringCloudlet = new ArrayList<Cloudlet>();
									double cletFailTime = 0.0;
									double cletDownTime = 0.0;
									long cletLengthTemp;
									long soFarClet;		
									//long oldBackupLength;
									for(int j=0;j<sizeCletList;j++){	
										if(cletsTempID.get(j) == 1631){
											System.out.println("Check");
										}
										cletFailTime = DownTimeTable.getCletFailTimeTable(cletsTempID.get(j));
										cletDownTime = CloudSim.clock() - cletFailTime;
										recoveringCloudlet.add(broker.getidtoCloudletLst().get(cletsTempID.get(j)));										
										soFarClet = CloudletStatus.getsofarCloudlet(recoveringCloudlet.get(j).getCloudletId());
										//cletLengthTemp = recoveringCloudlet.get(j).getCloudletLength();								
										//if(!cletLengthBackup.containsKey(cletsTempID.get(j))){
										//	cletLengthBackup.put(cletsTempID.get(j), recoveringCloudlet.get(j).getCloudletLength());
										//}
										cletLengthTemp = recoveringCloudlet.get(j).getCloudletLength() + ((long)(cletDownTime*1000));										
										//oldBackupLength = cletLengthBackup.get(recoveringCloudlet.get(j).getCloudletLength());
										//oldBackupLength = oldBackupLength + ((long)(cletDownTime*1000));
										//cletLengthBackup.put(recoveringCloudlet.get(j).getCloudletId(), oldBackupLength);
										recoveringCloudlet.get(j).setCloudletLength(cletLengthTemp);
										System.out.println("New Length of cloudlet " +cletsTempID.get(j)+" after the recovery from a failure is " +cletLengthTemp);
										System.out.println("Failure downtime overhead added to the length of cloudlet "+cletsTempID.get(j)+" is "+cletDownTime);
										System.out.println("Failure re-execution overhead added to the length of cloudlet "+cletsTempID.get(j)+" is "+soFarClet);
										DownTimeTable.setCloudletDownTime(cletsTempID.get(j), cletDownTime);
										CloudletReexecutionPart.setCletReexecutionTime(cletsTempID.get(j), soFarClet);										
										DownTimeTable.setCletPerHostDownTimeTable(cletsTempID.get(j), recoveringHost.getId(), cletDownTime);
										CloudletReexecutionPart.setCletPerHostReexecutionTimeTable(cletsTempID.get(j), recoveringHost.getId(), soFarClet);
										
										cletLastRemainingMap.put(cletsTempID.get(j), cletLengthTemp);
										// This part of the function is saving the recovering cloudlets for the future use. Cloudlets from this list will not be removed.
										if(recoveringCloudletList.isEmpty()){
											recoveringCloudletList.add(recoveringCloudlet.get(j));
								
								// This part of the function searches the failhostTable by using object value to get the corresponding key which is fta node ID. 
								// Both recovering cloudlet and the host on which the cloudlet is executing are required to calculate the average finishing time of the cloudlet in the presence of failures.
								/*
								for(Map.Entry<Integer, Integer> e: failhostTable.entrySet()){
									value=e.getValue();
									if(value == recoveringHost.getId()){
										ftaNodeId = e.getKey();
										break;
									}
								}								
								failedCletAverageExecTime.add(exec.averageExecutionTime(ftaNodeId, recoveringCloudlet.get(j).getCloudletLength(), fread.getMTBF(ftaNodeId), fread.getMTTR(ftaNodeId)));
								*/
										}
										else{
											for(int x=0;x<recoveringCloudletList.size();x++){
												if(recoveringCloudletList.get(x)==recoveringCloudlet.get(j)){
													lock=true;
													break;										
												}
												if(lock==false){
													recoveringCloudletList.add(recoveringCloudlet.get(j));
										/*
										for(Map.Entry<Integer, Integer> e: failhostTable.entrySet()){
											value=e.getValue();
											if(value == recoveringHost.getId()){
												ftaNodeId = e.getKey();
												break;
											}
										}
										
										failedCletAverageExecTime.add(exec.averageExecutionTime(ftaNodeId, recoveringCloudlet.get(j).getCloudletLength(), fread.getMTBF(ftaNodeId), fread.getMTTR(ftaNodeId)));
										*/
												}									
											}
										}
									}
							//double newmipsShare;							
							//mipsShare = new ArrayList<Double>();													
							//mipsShare.add(1000.0); //MIPS need to set to 1000 towards the 100% utilization							
							//failVMs.get(i).getCloudletScheduler().setCurrentMipsShare(mipsShare);								
									recoveringHost.vmCreate(failVMs.get(i)); //Creating a virtual machine on the host with the same name that the VM had before the failure event.							
									List<Pe> peList = new ArrayList<Pe>();
									peList = recoveringHost.getVmScheduler().getPesAllocatedForVM(failVMs.get(i));
									for(int x=0; x<peList.size(); x++){
										int peID = peList.get(x).getId();
										int previousPeID = vmPeMap.get(failVMs.get(i).getId());
										//vmPeMap.remove(failVMs.get(i).getId());
										if(peID != previousPeID){
											IdleTime.setHostPeIdleTable(recoveringHost.getId(), previousPeID, true);									
											IdleTime.setHostPeIdleClockTable(recoveringHost.getId(), previousPeID, CloudSim.clock());									
											IdleTime.setHostPeIdleTable(recoveringHost.getId(), peID, false);											
											if(IdleTime.checkHostPeIdleClockTable(recoveringHost.getId(), peID)){
												IdleTime.setHostPeIdleTimeTable(recoveringHost.getId(), peID, CloudSim.clock());
												IdleTime.removeHostPeIdleClockTable(recoveringHost.getId(),peID);									
											}
										}
									}						
									down_time = CloudSim.clock() - DownTimeTable.getFailTimeTable(failVMs.get(i));						
									//System.out.println("Host where the VM has been recreated is " +failVMs.get(i).getHost().getId());							
									//broker.processVmCreate(ev);					
									failVMs.get(i).setId(failVMsID.get(i)); // Setting the ID of the new virtual machine equal to the ID the VM had before the failure event.							
									//DatacenterBroker.getidtoCloudletLst().get(failVMsID.get(i)).setVmId(failVMs.get(i).getId());
									System.out.println("VM #" +failVMs.get(i).getId()+" has been recreated on Host "+recoveringHost.getId());
									for(int j=0; j<recoveringCloudlet.size(); j++){								
										double cletUtilization;
										cletUtilization = cletNorm.getNormalizedLength(recoveringCloudlet.get(j));
										recoveringCloudlet.get(j).setVmId(failVMs.get(i).getId());								
										failVMs.get(i).getCloudletScheduler().cloudletSubmit(recoveringCloudlet.get(j));
										System.out.println("Cloudlet #" +recoveringCloudlet.get(j).getCloudletId()+ " has been recreated on VM " +failVMs.get(i).getId());								
										System.out.println("Initial submission time of recovering cloudlet is " +broker.getcloudletInitialStartTime(recoveringCloudlet.get(j)));
										ReliabilityCalculator(recoveringHost, failVMs.get(i), cletUtilization);
									}						
									getVmList().add(failVMs.get(i));						
									failVMs.get(i).updateVmProcessing(CloudSim.clock(), getVmAllocationPolicy().getHost(failVMs.get(i)).getVmScheduler().getAllocatedMipsForVm(failVMs.get(i)));							
									//updateCloudletProcessing();							
									DownTimeTable.setDownTimeTable(failVMs.get(i), down_time);							
								}		
								hostVMPeMap.remove(recoveringHost.getId());
							}
						}			
					}			
					else{			
						System.out.println(" Host " +recoveringHost.getId()+  " is already up ");
					}
				}
			}
			else{ //This else is for non-testing simulation 	
				if(correlatedRecoveryEventWithChkpt.containsKey(node)){
					processRecoveryEventWithCheckpointing(node);
				}
				else{
					updateCloudletProcessing();			
					Host recoveringHost;		
					int hostCount;
					ArrayList<Cloudlet> recoveringCloudlet;		
					recoveringHost = hostList().get(failhostTable.get(node));			
					if(recoveringHost.isFailed()){				
						recoveringHost.setFailed(false);					
						if(recoveringHost.getDatacenter()==null){
							recoveringHost.setDatacenter(this);
						}			
						if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 3 && RunTimeConstants.resourceTurnOffCase == false){
							if((IdleTime.expectedFailingHost.isEmpty()==false) && IdleTime.checkExpectedFailingHost(recoveringHost.getId())==true){
								IdleTime.setExpectedFailingHost(recoveringHost.getId(), false);					
								for(int i=0; i<recoveringHost.getNumberOfPes(); i++){
									int peID;
									peID = recoveringHost.getPeList().get(i).getId();
									boolean idleStatus;
									idleStatus = IdleTime.getHostPeIdleTable(recoveringHost.getId(), peID);
									if(idleStatus == true){
										IdleTime.setHostPeIdleTable(recoveringHost.getId(), peID, false);
										IdleTime.setHostPeIdleTimeTable(recoveringHost.getId(), peID, CloudSim.clock());						
									}							
								}					
							}				
						}
						for(int i=0;i<failedHosts.size();i++){
							if(failedHosts.get(i)==node){
								failedHosts.remove(i);
							}
						}		
						if(hostvmTable.containsKey(recoveringHost)){			
							failVMs = new ArrayList<Vm>();
							failVMsID = new ArrayList<Integer>();				
							failVMs=hostvmTable.get(recoveringHost);
							failVMsID = hostvmIdTable.get(recoveringHost);						
							if(failVMs != null){							
								IdleTime.setHostActiveStatus(recoveringHost.getId(), true);															
								hostCount = IdleTime.getActiveHostCount();									
								HostCounter.setHostCount(hostCount);
								HostCounter.setClockList(CloudSim.clock());						
								int size=failVMs.size();							
								int sizeCletList;
								vmPeMap = new HashMap<Integer, Integer>();
								vmPeMap = hostVMPeMap.get(recoveringHost.getId());				
								peDownTimeMap = new HashMap<Integer, Double>();
								peDownTimeMap = hostPeDownTimeMap.get(recoveringHost.getId());
								Set<Integer>peKeySet = new HashSet<Integer>();
								peKeySet = peDownTimeMap.keySet();
								ArrayList<Integer>peKeySetList = new ArrayList<Integer>(peKeySet);				
								for(int i=0; i<peDownTimeMap.size(); i++){
									double downTime;				
									downTime = CloudSim.clock() - peDownTimeMap.get(peKeySetList.get(i));
									IdleTime.setHostPeDownTime(recoveringHost.getId(), peKeySetList.get(i), downTime);
								}
								hostPeDownTimeMap.remove(recoveringHost.getId());				
								for(int i=0;i<size;i++){								
									sizeCletList = failedVMCletMap.get(failVMsID.get(i)).size();
									cletsTempID = new ArrayList<Integer>();			
									cletsTempID = failedVMCletMap.get(failVMsID.get(i));									
									failedVMCletMap.remove(failVMsID.get(i));
									failVMs.get(i).setBeingInstantiated(true); //This matters. If this variable will not set to true then the reallocation of resources will not happen and estimatedFinishingTime of a corresponding cloudlet will set to infinity.						
									recoveringCloudlet = new ArrayList<Cloudlet>();
									double cletFailTime = 0.0;
									double cletDownTime = 0.0;
									long cletLengthTemp;
									long soFarClet;				
									for(int j=0;j<sizeCletList;j++){								
										cletFailTime = DownTimeTable.getCletFailTimeTable(cletsTempID.get(j));
										cletDownTime = CloudSim.clock() - cletFailTime;
										recoveringCloudlet.add(broker.getidtoCloudletLst().get(cletsTempID.get(j)));										
										soFarClet = CloudletStatus.getsofarCloudlet(recoveringCloudlet.get(j).getCloudletId());									
										cletLengthTemp = recoveringCloudlet.get(j).getCloudletLength() + ((long)(cletDownTime*1000));									
										recoveringCloudlet.get(j).setCloudletLength(cletLengthTemp);									
										DownTimeTable.setCloudletDownTime(cletsTempID.get(j), cletDownTime);
										CloudletReexecutionPart.setCletReexecutionTime(cletsTempID.get(j), soFarClet);										
										DownTimeTable.setCletPerHostDownTimeTable(cletsTempID.get(j), recoveringHost.getId(), cletDownTime);
										CloudletReexecutionPart.setCletPerHostReexecutionTimeTable(cletsTempID.get(j), recoveringHost.getId(), soFarClet);									
										cletLastRemainingMap.put(cletsTempID.get(j), cletLengthTemp);									
										if(recoveringCloudletList.isEmpty()){
											recoveringCloudletList.add(recoveringCloudlet.get(j));							
										}
										else{
											for(int x=0;x<recoveringCloudletList.size();x++){
												if(recoveringCloudletList.get(x)==recoveringCloudlet.get(j)){
													lock=true;
													break;										
												}
												if(lock==false){
													recoveringCloudletList.add(recoveringCloudlet.get(j));									
												}									
											}
										}
									}											
									recoveringHost.vmCreate(failVMs.get(i)); //Creating a virtual machine on the host with the same name that the VM had before the failure event.							
									List<Pe> peList = new ArrayList<Pe>();
									peList = recoveringHost.getVmScheduler().getPesAllocatedForVM(failVMs.get(i));
									for(int x=0; x<peList.size(); x++){
										int peID = peList.get(x).getId();
										int previousPeID = vmPeMap.get(failVMs.get(i).getId());									
										if(peID != previousPeID){
											IdleTime.setHostPeIdleTable(recoveringHost.getId(), previousPeID, true);									
											IdleTime.setHostPeIdleClockTable(recoveringHost.getId(), previousPeID, CloudSim.clock());									
											IdleTime.setHostPeIdleTable(recoveringHost.getId(), peID, false);											
											if(IdleTime.checkHostPeIdleClockTable(recoveringHost.getId(), peID)){
												IdleTime.setHostPeIdleTimeTable(recoveringHost.getId(), peID, CloudSim.clock());
												IdleTime.removeHostPeIdleClockTable(recoveringHost.getId(),peID);									
											}
										}
									}						
									down_time = CloudSim.clock() - DownTimeTable.getFailTimeTable(failVMs.get(i));									
									failVMs.get(i).setId(failVMsID.get(i)); // Setting the ID of the new virtual machine equal to the ID the VM had before the failure event.							
									for(int j=0; j<recoveringCloudlet.size(); j++){								
										double cletUtilization;
										cletUtilization = cletNorm.getNormalizedLength(recoveringCloudlet.get(j));
										recoveringCloudlet.get(j).setVmId(failVMs.get(i).getId());								
										failVMs.get(i).getCloudletScheduler().cloudletSubmit(recoveringCloudlet.get(j));									
										ReliabilityCalculator(recoveringHost, failVMs.get(i), cletUtilization);
									}						
									getVmList().add(failVMs.get(i));						
									failVMs.get(i).updateVmProcessing(CloudSim.clock(), getVmAllocationPolicy().getHost(failVMs.get(i)).getVmScheduler().getAllocatedMipsForVm(failVMs.get(i)));											
									DownTimeTable.setDownTimeTable(failVMs.get(i), down_time);							
								}		
								hostVMPeMap.remove(recoveringHost.getId());
							}
						}			
					}	
				}
			}
		}

		protected void processRestartFromPerfectCheckpointEvent(int node){
			
			updateCloudletProcessing();
			
			Host recoveringHost;
			
			ArrayList<Cloudlet> recoveringCloudlet;
			
			recoveringHost = hostList().get(failhostTable.get(node));
			
			if(recoveringHost.isFailed()){		
		//if(hostList().get(failhostTable.get(node)).isFailed()){
			
				System.out.println("This recovery was scheduled at " +CloudSim.clock());
			
			//System.out.println("Host " +hostList().get(failhostTable.get(node))+ " with ID " +hostList().get(failhostTable.get(node)).getId()+ "  has been recovered from a failure");
				recoveringHost.setFailed(false);
				
				System.out.println("Host " +recoveringHost+ " with ID " +recoveringHost.getId()+ "  has been recovered from a failure");
			
			//hostList().get(failhostTable.get(node)).setFailed(false);			
			
				for(int i=0;i<failedHosts.size();i++){
					if(failedHosts.get(i)==node){
						failedHosts.remove(i);
					}
				}				
			
				
			//if(hostvmTable.containsKey(hostList().get(failhostTable.get(node)))){
				if(hostvmTable.containsKey(recoveringHost)){	
				
					failVMs = new ArrayList<Vm>();
					failVMsID = new ArrayList<Integer>();
				
				//failVMs=hostvmTable.get(hostList().get(failhostTable.get(node)));
				//failVMsID = hostvmIdTable.get(hostList().get(failhostTable.get(node)));
				
					failVMs = hostvmTable.get(recoveringHost);
					failVMsID = hostvmIdTable.get(recoveringHost);
				
					if(failVMs == null){
						System.out.println("Though no VMs were running on host # " +recoveringHost.getId()+ " but it will be recovered because of the dynamic cloudlets");
					}
					else{
						int size=failVMs.size();
				//System.out.println("Number of VMs running on host " +hostList().get(failhostTable.get(node))+ " of ID "+ hostList().get(failhostTable.get(node)).getId()+ " are " +size);
				
						System.out.println("Number of VMs running on host " +recoveringHost+ " of ID "+ recoveringHost.getId()+ " are " +size);
						
						int sizeCletList;
						
						for(int i=0;i<size;i++){
							
							check=true;
							
							sizeCletList = failedVMCletMap.get(failVMsID.get(i)).size();
							cletsTempID = new ArrayList<Integer>();
							cletsTempID = failedVMCletMap.get(failVMsID.get(i));
							failedVMCletMap.remove(failVMsID.get(i));
							recoveringCloudlet = new ArrayList<Cloudlet>();
					//if(DatacenterBroker.getmaplistCloudlettovm().contains(failVMsID.get(i))){
						//System.out.println("Cloudlet corresponding to the recreated VM is " +DatacenterBroker.getidtoCloudletLst().get(failVMs.get(i).getId()));
							
							for(int j=0;j<sizeCletList;j++){
								recoveringCloudlet.add(broker.getidtoCloudletLst().get(cletsTempID.get(j)));
							}
							// hostList().get(failhostTable.get(node)).vmCreate(failVMs.get(i)); //Creating a virtual machine on the host with the same name that the VM had before the failure event.
							
								
							
							
							// This part of the function is saving the recovering cloudlets for the future use. Cloudlets from this list will not be removed.
							for(int j=0;j<sizeCletList;j++){
								if(recoveringCloudletList.isEmpty()){
									recoveringCloudletList.add(recoveringCloudlet.get(j));
								
								// This part of the function searches the failhostTable by using object value to get the corresponding key which is fta node ID. 
								// Both recovering cloudlet and the host on which the cloudlet is executing are required to calculate the average finishing time of the cloudlet in the presence of failures.
								
								for(Map.Entry<Integer, Integer> e: failhostTable.entrySet()){
									value=e.getValue();
									if(value == recoveringHost.getId()){
										ftaNodeId = e.getKey();
										break;
									}
								}
								if(RunTimeConstants.traceType.equals("LANL")){
									failedCletAverageExecTime.add(exec.averageExecutionTime(ftaNodeId, recoveringCloudlet.get(j).getCloudletLength(), freadLANL.getMTBF(ftaNodeId), freadLANL.getMTTR(ftaNodeId)));
								}
								if(RunTimeConstants.traceType.equals("Grid5000")){
									failedCletAverageExecTime.add(exec.averageExecutionTime(ftaNodeId, recoveringCloudlet.get(j).getCloudletLength(), freadGrid5000.getMTBF(ftaNodeId), freadGrid5000.getMTTR(ftaNodeId)));
								}
							}
							else{
								for(int x=0;x<recoveringCloudletList.size();x++){
									if(recoveringCloudletList.get(x)==recoveringCloudlet.get(j)){
										lock=true;
										break;
										
									}
									if(lock==false){
										recoveringCloudletList.add(recoveringCloudlet.get(j));
										for(Map.Entry<Integer, Integer> e: failhostTable.entrySet()){
											value=e.getValue();
											if(value == recoveringHost.getId()){
												ftaNodeId = e.getKey();
												break;
											}
										}
										if(RunTimeConstants.traceType.equals("LANL")){
											failedCletAverageExecTime.add(exec.averageExecutionTime(ftaNodeId, recoveringCloudlet.get(j).getCloudletLength(), freadLANL.getMTBF(ftaNodeId), freadLANL.getMTTR(ftaNodeId)));
										}
										if(RunTimeConstants.traceType.equals("Grid5000")){
											failedCletAverageExecTime.add(exec.averageExecutionTime(ftaNodeId, recoveringCloudlet.get(j).getCloudletLength(), freadGrid5000.getMTBF(ftaNodeId), freadGrid5000.getMTTR(ftaNodeId)));
										}
									}									
								}
							}
							}
							failVMs.get(i).setBeingInstantiated(true); //This matters. If this variable will not set to true then the reallocation of resources will not happen and estimatedFinishingTime of a corresponding cloudlet will set to infinity. 
								
							recoveringHost.vmCreate(failVMs.get(i)); //Creating a virtual machine on the host with the same name that the VM had before the failure event.
							
							//System.out.println("Host where the VM has been recreated is " +failVMs.get(i).getHost().getId());					
										
							failVMs.get(i).setId(failVMsID.get(i)); // Setting the ID of the new virtual machine equal to the ID the VM had before the failure event. 
							
							getVmList().add(failVMs.get(i));
							//DatacenterBroker.getidtoCloudletLst().get(failVMsID.get(i)).setVmId(failVMs.get(i).getId());
							
							//Long remainingLength;
							
							//remainingLength = recoveringCloudlet.getCloudletLength() - recoveringCloudlet.getCloudletFinishedSoFar(); 
							
							double recoveryTime;
							
							recoveryTime = CloudSim.clock() - DownTimeTable.getFailTimeTable(failVMs.get(i));
							
							
							
							for(int j=0;j<sizeCletList;j++){
								
								recoveringCloudlet.get(j).setVmId(failVMs.get(i).getId());
							
								System.out.println("Remaining length of cloudlet " +recoveringCloudlet.get(j).getCloudletId()+ " is " +CloudletStatus.getremainingCloudlet(recoveringCloudlet.get(j).getCloudletId()));			
														
								Long newlength;
								
								double cletUtilization;
								
								cletUtilization = cletNorm.getNormalizedLength(recoveringCloudlet.remove(j));
							
							//double previousTime;
							//previousTime = failVMs.get(i).getCloudletScheduler().getPreviousTime();
							
							
							
							//down_time = recoveryTime;
							
							//if(recoveryTime>0 && recoveryTime<CloudSim.getMinTimeBetweenEvents()){
								//recoveryTime = CloudSim.getMinTimeBetweenEvents() * 1000;
								//down_time = .1;							
							//}
							//else{
								//recoveryTime = recoveryTime * 1000;
							//}
							
							//DownTimeTable.setDownTimeTable(failVMs.get(i), down_time);
							
							//(long) (getCapacity(mipsShare) * timeSpam * rcl.getNumberOfPes() * Consts.MILLION)
							//newlength =  (cletstatus.getremainingCloudlet(recoveringCloudlet) + (long)(recoveryTime));
							
							newlength =  (CloudletStatus.getremainingCloudlet(recoveringCloudlet.get(j).getCloudletId()) + (long)(recoveryTime));
							
							//The new length is equal to the remaining length of the cloudlet before the occurrence of a failure. 
							System.out.println("New length of recovering cloudlet " +recoveringCloudlet.get(j).getCloudletId()+ " is " +newlength);
							
							recoveringCloudlet.get(j).setCloudletLength(newlength);
							
							//length = (long) (cletstatus.getremainingCloudlet(recoveringCloudlet) + ((long)CloudSim.clock()*1000 - (long)(pchkpt.getCheckpoints(recoveringCloudlet)*1000)));
							
							//recoveringCloudlet.setCloudletLength(length);
							//failVMs.get(i).getCloudletScheduler().cloudletSubmit(DatacenterBroker.getidtoCloudletLst().get(failVMs.get(i).getId()));											
							
							pchkpt.setRecoveryTime(recoveringCloudlet.get(j).getCloudletId(), CloudSim.clock());
													
							//This step is very important. After setting the previous time to the current time, the execution of the cloudlet has been paused during the
							//period of failure recovery. 
							
							
													
							failVMs.get(i).getCloudletScheduler().cloudletSubmit(recoveringCloudlet.get(j));
							
							
							
							//processCloudletResume(recoveringCloudlet.getCloudletId(), recoveringCloudlet.getUserId(), failVMs.get(i).getId(), false);
							
							//System.out.println("Submission Time for the recreated cloudlet is " +DatacenterBroker.getidtoCloudletLst().get(failVMs.get(i).getId()).getSubmissionTime());
							//System.out.println("Utilization of the recreated cloudlet is " +DatacenterBroker.getidtoCloudletLst().get(failVMs.get(i).getId()).getUtilizationOfBw(CloudSim.clock()));						
							
							
							
							//updateCloudletProcessing();
							
							//System.out.println("Virtual Machine " +failVMs.get(i)+ " with ID " +failVMs.get(i).getId()+ " and corresponding Cloudlet " +DatacenterBroker.getidtoCloudletLst().get(failVMsID.get(i))+ " with ID " +DatacenterBroker.getidtoCloudletLst().get(failVMsID.get(i)).getCloudletId()+ " has been recreated on Host # "+failVMs.get(i).getHost().getId());
							
							System.out.println("Virtual Machine " +failVMs.get(i)+ " with ID " +failVMs.get(i).getId()+ " and corresponding Cloudlet " +recoveringCloudlet.get(j)+ " of length " +ResCloudlet.getremaininglength()+ " with ID " +recoveringCloudlet.get(j).getCloudletId()+ " has been recreated on Host # "+recoveringHost.getId());
							
							System.out.println("Initial submission time of recovering cloudlet is " +broker.getcloudletInitialStartTime(recoveringCloudlet.get(j)));
							
							//updateCloudletProcessing();
							ReliabilityCalculator(recoveringHost, failVMs.get(i), cletUtilization);
							
						}
							failVMs.get(i).getCloudletScheduler().setPreviousTime(CloudSim.clock());
							
							failVMs.get(i).updateVmProcessing(CloudSim.clock(), getVmAllocationPolicy().getHost(failVMs.get(i)).getVmScheduler().getAllocatedMipsForVm(failVMs.get(i)));
							
							DownTimeTable.setDownTimeTable(failVMs.get(i), recoveryTime);
							
							updateCloudletProcessing();
							
							
						}
						/*
						if(check==false){
							System.out.println("No virtual machine has been instantiated on the host #" +recoveringHost.getId());
							System.out.println("So the host #" +recoveringHost.getId()+ " will remain down");
							recoveringHost.setFailed(true);
						}
						*/
				}
			} 
			
		}
			else{			
				System.out.println(" Host " +recoveringHost.getId()+  " is already up ");
			
			}
		}		
		
		public List<Double> getFailedCletAverageExectTime(){
			return(failedCletAverageExecTime);
		}
		
		
		public HashMap<Integer, Double>checkpointInterval = new HashMap<Integer, Double>();
		
		public void createCheckpointInterval(Integer hostID, double MTBF){		
			double overHead;			
			double interval;
		//	double averageHostUtilization;
		//	double totalHostUtilization = 0;;
		//	List<Vm>vmList = new ArrayList<Vm>();
		//	vmList = host.getVmList();
		//	for(int i=0; i<vmList.size(); i++){
		//		totalHostUtilization = totalHostUtilization + vmUtl.getCurrentVmUtilization(vmList.get(i).getId());
		//	}
		//	averageHostUtilization = totalHostUtilization/vmList.size();
		//	overHead = RunTimeConstants.checkpointOverhead * averageHostUtilization;
			overHead = RunTimeConstants.checkpointOverhead;
			interval = Math.sqrt(2 * overHead * MTBF);		
			checkpointInterval.put(hostID, interval);
			if(RunTimeConstants.test == true){		
				System.out.println("Checkpoint interval for Host # " +hostID+ " with MTBF " +MTBF+ " is " +interval);
			}
		}
		
		
		public HashMap<Integer, Long>checkpointsForCloudlets = new HashMap<Integer, Long>();
		public HashMap<Integer, Integer>cletCurrentHostForConsolidation = new HashMap<Integer, Integer>();	
		public ArrayList<Integer>cletListIDs;
		public HashMap<Integer, Long>soFarForConsolidation = new HashMap<Integer, Long>();
		public HashMap<Integer, Boolean>instantCorrelatedCheckpoint = new HashMap<Integer, Boolean>();
		public HashMap<Integer, Integer>correlatedFailEventWithChkpt = new HashMap<Integer, Integer>();
		public HashMap<Integer, Integer>correlatedRecoveryEventWithChkpt = new HashMap<Integer, Integer>();
		//public HashMap<Integer, Boolean>cletMapforConsolidationTesting = new HashMap<Integer, Boolean>();
		int checkCount = 0;
		public void createCheckpoints(int node){
		//	if(node == 867){
		//		checkCount++;
		//		if(checkCount == 2){
		//			System.out.println("Check");
		//		}
		//	}
			updateCloudletProcessing();	
			if(RunTimeConstants.test == true){				
				//if(RunTimeConstants.predictionFlag == true && firstCheckpointFlag.get(node)==true){
				//	System.out.println("This is the checkpoint corresponding to first prediction. So it will be ignored");
				//	firstCheckpointFlag.put(node, false);
				//}
				//else{
					int rand = 0; 
					Host host;							
					for(int j=0;j<hostList().size();j++){
						if(failhostTable.containsKey(node)){
							rand=failhostTable.get(node);
							break;
						}	
					}			
					host = hostList().get(rand);		
					
					//int counterLocal = 0;
					if(RunTimeConstants.failureCorrelation == true && RunTimeConstants.faultToleranceMechanism == 1 && !instantCorrelatedCheckpoint.containsKey(node) && consolidationCheckpointing == false && (!host.isFailed())){
						ArrayList<Integer>correlatedNodes = new ArrayList<Integer>();					
						String nodeLocation;
						nodeLocation = freadGrid5000.getClusterNameForNode(node);
						correlatedNodes.addAll(kMeans.getCorrelatedNodes(nodeLocation, node));
						for(int i=0; i<correlatedNodes.size(); i++){
							if(correlatedNodes.get(i).equals(node)){
								correlatedNodes.remove(i);
								break;
							}
						}
						/*
						for(int i=0; i<correlatedNodes.size(); i++){
							if(failhostTable.containsKey(correlatedNodes.get(i))){
								Host hostLocal;
								int nodeIDLocal;
								nodeIDLocal = failhostTable.get(correlatedNodes.get(i));
								hostLocal = hostList().get(nodeIDLocal);						
								IdleTime.setExpectedFailingHost(hostLocal.getId(), true);
								for(int j=0; j<hostLocal.getNumberOfPes(); j++){
									int peID;
									peID = hostLocal.getPeList().get(j).getId();						
									IdleTime.setHostPeIdleTable(hostLocal.getId(), peID, true);
									IdleTime.setHostPeIdleClockTable(hostLocal.getId(), peID, CloudSim.clock());
								}
							}
						}
						*/
						for(int i=0; i<correlatedNodes.size(); i++){
							if(failhostTable.containsKey(correlatedNodes.get(i))){						
								instantCorrelatedCheckpoint.put(correlatedNodes.get(i), true);						
								//counterLocal = counterLocal + 1;
								sendNow(3, CloudSimTags.CREATE_CHECKPOINT, correlatedNodes.get(i));
								//processVmMigrate(correlatedNodes.get(i));							
							}		
							//freadGrid5000.setCorrelatedMigraiton(false);
						}
						//freadGrid5000.setCorrelatedMigraiton(true);
						//freadGrid5000.setCorrelatedMigraitonCounter(counterLocal);
					}			
					else{				
						if(instantCorrelatedCheckpoint.containsKey(node)){
							instantCorrelatedCheckpoint.remove(node);
							//correlationCounterFlag = true;					
						}
					}
					
					if(RunTimeConstants.failureCorrelation == true && RunTimeConstants.faultToleranceMechanism == 2 && RunTimeConstants.failureCorrelationWithRstrChkpt == true && (!host.isFailed())){
						int counterLocal = 0;						
						if(correlatedFailEventWithChkpt.containsKey(node)){
							counterLocal = counterLocal + correlatedFailEventWithChkpt.get(node);
							counterLocal = counterLocal + 1;
							correlatedFailEventWithChkpt.put(node, counterLocal);
							correlatedRecoveryEventWithChkpt.put(node, counterLocal);
						}
						else{
							counterLocal = counterLocal + 1;
							correlatedFailEventWithChkpt.put(node, counterLocal);
							correlatedRecoveryEventWithChkpt.put(node, counterLocal);
						}
					}
					
					if(host.isFailed()){
						System.out.println("Present state of the host #" +host.getId()+ " is failed or sitting Idle. So, no checkpoints will be taken");
					}			
					else{						
						System.out.println("Checkpoint for host #" +host.getId()+" is being created");				
					//	System.out.println("Clock value while saving checkpoints is " +CloudSim.clock());
						if(host.getVmList().isEmpty()){
							System.out.println("No virtual machine is running on host # " +host.getId());											
						}
						else{
							Vm vm;
							Cloudlet clet;							
							long sofarExecutedLength;
							long remainingCloudletLength;				
							long newCloudletLength;
							int size;				
							long oldCheckpointValue;
							long oldCletLength;
							long newSoFarExecutedLength;
							long sofarSinceLastChkpt;
						//	long sofarForConsolidation;
							double vmUtilization;
							for(int j=0;j<host.getVmList().size();j++){								
								boolean takeChkpt_flag = false;
								vm = host.getVmList().get(j);
								vmUtilization = vmUtl.getCurrentVmUtilization(vm.getId());								
								size =  ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().size();
								cletListIDs = new ArrayList<Integer>();
								for(int k=0;k<size;k++){															
									cletListIDs.add(((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getCloudletId());
									//cletMapforConsolidationTesting.put(cletListIDs.get(k), true);
									/*
									if(cletListIDs.get(k)==3){										
										checkCount = checkCount + 1;
										System.out.println("Checkpoint count is " +checkCount);
										if(checkCount == 120868){
											System.out.println("Check");
										}
									}
									*/
								}
								for(int k=0;k<size;k++){
									clet = broker.getidtoCloudletLst().get(cletListIDs.get(k));		
									remainingCloudletLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();								
									if(clet.getCloudletLength()==remainingCloudletLength){										
										sofarExecutedLength = 0;
									}
									else{									
										sofarExecutedLength = clet.getCloudletLength() - remainingCloudletLength;									
									}	
									//if((RunTimeConstants.faultToleranceMechanism == 4)||(RunTimeConstants.predictionFlag==false && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true)){
									if((RunTimeConstants.faultToleranceMechanism == 4)){												
										consolidationCheckpointing = true;
									}
									if(consolidationCheckpointing == true){
										takeChkpt_flag = true;
									//	if(cletListIDs.get(k)==0){										
									//		System.out.println("Check");
									//	}
									}
									
									else{									
										if(!checkpointsForCloudlets.containsKey(clet.getCloudletId())){
											takeChkpt_flag = true;
										}
										else{
											if(failureHappenedForClet.containsKey(clet.getCloudletId())){
												if(sofarExecutedLength < (RunTimeConstants.instructionsOverhead*vmUtilization)){
													takeChkpt_flag = false;
												}
												else{
													takeChkpt_flag = true;
													failureHappenedForClet.remove(clet.getCloudletId());
												}
											}
											else{
												sofarSinceLastChkpt = cletLengthBackup.get(clet.getCloudletId()) - checkpointsForCloudlets.get(clet.getCloudletId());
												sofarSinceLastChkpt = sofarSinceLastChkpt - remainingCloudletLength;
												//if(sofarExecutedLength-checkpointsForCloudlets.get(clet.getCloudletId())<(RunTimeConstants.instructionsOverhead*vmUtilization)){
												if(sofarSinceLastChkpt<(RunTimeConstants.instructionsOverhead*vmUtilization)){													
													takeChkpt_flag = false;
												}
												else{
													takeChkpt_flag = true;
												}
											}
										}
									}
									/*
									else{									
										if(sofarExecutedLength-checkpointsForCloudlets.get(clet.getCloudletId())<(RunTimeConstants.instructionsOverhead*vmUtilization)){
											takeChkpt_flag = false;		
											System.out.println("So far executed value for cloudlet " + clet.getCloudletId()+ " is " +sofarExecutedLength);										
											System.out.println("Chkpt value for cloudlet " +clet.getCloudletId()+ " is " +checkpointsForCloudlets.get(clet.getCloudletId()));
											System.out.println(CloudSim.clock()+ " Checkpoint for " +clet.getCloudletId()+ " has not been created because of low risk and more overhead");
										}
										else{
											takeChkpt_flag = true;									
										}
									}
									*/									
									if(takeChkpt_flag == true && consolidationCheckpointing == false){
										CheckpointOverhead.setCheckpointOverheadTable(clet.getCloudletId(), (RunTimeConstants.checkpointOverhead * vmUtilization));	
										CheckpointOverhead.setCheckpointOverheadTablePerHost(clet.getCloudletId(), host.getId(), (RunTimeConstants.checkpointOverhead * vmUtilization));
										System.out.println(CloudSim.clock()+ " Old length of cloudlet #" +clet.getCloudletId()+ " is " +clet.getCloudletLength());								
										if(!checkpointsForCloudlets.containsKey(clet.getCloudletId())){
											checkpointsForCloudlets.put(clet.getCloudletId(), (sofarExecutedLength));									
											//cletSoFarMap.put(clet.getCloudletId(), (sofarExecutedLength));										
											previousChkptSoFarTable.put(clet.getCloudletId(), sofarExecutedLength);										
										}
										else{
											if(!failFlagTable.containsKey(clet.getCloudletId())){											
												checkpointsForCloudlets.put(clet.getCloudletId(), (sofarExecutedLength));																						
											}
											else{
												if(!previousChkptSoFarTable.containsKey(clet.getCloudletId())){
													if(failureOccurredBeforeChkpt.containsKey(clet.getCloudletId())){
														//newSoFarExecutedLength = sofarExecutedLength - checkpointsForCloudlets.get(clet.getCloudletId());			
											//			cletSoFarMap.put(clet.getCloudletId(), (newSoFarExecutedLength));
														checkpointsForCloudlets.put(clet.getCloudletId(), (sofarExecutedLength));	
														previousChkptSoFarTable.put(clet.getCloudletId(), sofarExecutedLength);
													}
													else{
														oldCheckpointValue = checkpointsForCloudlets.get(clet.getCloudletId());
														oldCheckpointValue = oldCheckpointValue + sofarExecutedLength;
														checkpointsForCloudlets.put(clet.getCloudletId(), oldCheckpointValue);
														previousChkptSoFarTable.put(clet.getCloudletId(), sofarExecutedLength);											
											//			cletSoFarMap.put(clet.getCloudletId(), (sofarExecutedLength));
													}													
												}
												else{											
													newSoFarExecutedLength = sofarExecutedLength - previousChkptSoFarTable.get(clet.getCloudletId());
													previousChkptSoFarTable.put(clet.getCloudletId(), sofarExecutedLength);
													oldCheckpointValue = checkpointsForCloudlets.get(clet.getCloudletId());
													oldCheckpointValue = oldCheckpointValue + newSoFarExecutedLength;
													checkpointsForCloudlets.put(clet.getCloudletId(), oldCheckpointValue);										
											//		cletSoFarMap.put(clet.getCloudletId(), newSoFarExecutedLength);
												}
											}										
										}
										if(!cletLengthBackup.containsKey(clet.getCloudletId())){
											oldCletLength = clet.getCloudletLength();
											oldCletLength = oldCletLength + (long)(RunTimeConstants.instructionsOverhead * vmUtilization);
											cletLengthBackup.put(clet.getCloudletId(), oldCletLength);
										}
										else{
											oldCletLength = cletLengthBackup.get(clet.getCloudletId());
											oldCletLength = oldCletLength + (long)(RunTimeConstants.instructionsOverhead * vmUtilization);
											cletLengthBackup.put(clet.getCloudletId(), oldCletLength);
										}									
										if(previousConsolidationRemainingTable.containsKey(clet.getCloudletId())){
											long previousRemaining;
											previousRemaining = previousConsolidationRemainingTable.get(clet.getCloudletId());
											previousRemaining = previousRemaining + (long)(RunTimeConstants.instructionsOverhead * vmUtilization);
											previousConsolidationRemainingTable.put(clet.getCloudletId(), previousRemaining);
										}
										newCloudletLength = clet.getCloudletLength() + (long)(RunTimeConstants.instructionsOverhead * vmUtilization);
										clet.setCloudletLength(newCloudletLength);
										remainingCloudletLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();
										cletLastRemainingMap.put(clet.getCloudletId(), remainingCloudletLength);										
										System.out.println(CloudSim.clock()+ " New length of cloudlet #" +clet.getCloudletId()+ " after checkpoint overhead is " +clet.getCloudletLength());
									//	totalCheckpointEvents = totalCheckpointEvents + 1;
									}
									if(takeChkpt_flag == true && consolidationCheckpointing == true){
									//	consolidationCheckpointing = false;
									//	System.out.println(CloudSim.clock()+ " Old length of cloudlet #" +clet.getCloudletId()+ " is " +clet.getCloudletLength());								
										if(!checkpointsForCloudlets.containsKey(clet.getCloudletId())){
											checkpointsForCloudlets.put(clet.getCloudletId(), (sofarExecutedLength));									
											//cletSoFarMap.put(clet.getCloudletId(), (sofarExecutedLength));										
											previousChkptSoFarTable.put(clet.getCloudletId(), sofarExecutedLength);										
										}
										else{
											if(!failFlagTable.containsKey(clet.getCloudletId())){											
												checkpointsForCloudlets.put(clet.getCloudletId(), (sofarExecutedLength));																						
											}
											else{
												if(!previousChkptSoFarTable.containsKey(clet.getCloudletId())){
													if(failureOccurredBeforeChkpt.containsKey(clet.getCloudletId())){
														//newSoFarExecutedLength = sofarExecutedLength - checkpointsForCloudlets.get(clet.getCloudletId());			
											//			cletSoFarMap.put(clet.getCloudletId(), (newSoFarExecutedLength));
														checkpointsForCloudlets.put(clet.getCloudletId(), (sofarExecutedLength));	
														previousChkptSoFarTable.put(clet.getCloudletId(), sofarExecutedLength);
													}
													else{
														oldCheckpointValue = checkpointsForCloudlets.get(clet.getCloudletId());
														oldCheckpointValue = oldCheckpointValue + sofarExecutedLength;
														checkpointsForCloudlets.put(clet.getCloudletId(), oldCheckpointValue);
														previousChkptSoFarTable.put(clet.getCloudletId(), sofarExecutedLength);											
											//			cletSoFarMap.put(clet.getCloudletId(), (sofarExecutedLength));
													}													
												}
												else{											
													newSoFarExecutedLength = sofarExecutedLength - previousChkptSoFarTable.get(clet.getCloudletId());
													previousChkptSoFarTable.put(clet.getCloudletId(), sofarExecutedLength);
													oldCheckpointValue = checkpointsForCloudlets.get(clet.getCloudletId());
													oldCheckpointValue = oldCheckpointValue + newSoFarExecutedLength;
													checkpointsForCloudlets.put(clet.getCloudletId(), oldCheckpointValue);										
											//		cletSoFarMap.put(clet.getCloudletId(), newSoFarExecutedLength);
												}
											}										
										}									
										//totalCheckpointEvents = totalCheckpointEvents + 1;
									}									
								}								
								if(takeChkpt_flag == true){
									totalCheckpointEvents = totalCheckpointEvents + 1;
									//System.out.println("Checkpoint count is " + totalCheckpointEvents);
								}
							}							
						}	
						consolidationCheckpointing = false;
					}
					if(furtherCheckpoints == true){
						if(RunTimeConstants.predictionFlag == false && RunTimeConstants.failureCorrelationWithRstrChkpt == false){
							//	System.out.println("Next checkpoint saving event for host #" +host.getId()+ " has been scheduled at " +(checkpointInterval.get(host)+CloudSim.clock()));		
							send(3, checkpointInterval.get(host.getId()), CloudSimTags.CREATE_CHECKPOINT, node); //Checkpoint will be scheduled in any case whether it is idle or active.
						}					
					}	
					else{
						System.out.println("No further checkpoint saving events will be scheduled");				
					}
				//}
			}			
			else{			
				int rand = 0; 
				Host host;							
				for(int j=0;j<hostList().size();j++){
					if(failhostTable.containsKey(node)){
						rand=failhostTable.get(node);
						break;
					}	
				}			
				host = hostList().get(rand);			
				if(RunTimeConstants.failureCorrelation == true && RunTimeConstants.faultToleranceMechanism == 1 && !instantCorrelatedCheckpoint.containsKey(node) && consolidationCheckpointing == false && (!host.isFailed())){
					ArrayList<Integer>correlatedNodes = new ArrayList<Integer>();					
					String nodeLocation;
					nodeLocation = freadGrid5000.getClusterNameForNode(node);
					correlatedNodes.addAll(kMeans.getCorrelatedNodes(nodeLocation, node));
					for(int i=0; i<correlatedNodes.size(); i++){
						if(correlatedNodes.get(i).equals(node)){
							correlatedNodes.remove(i);
							break;
						}
					}				
					for(int i=0; i<correlatedNodes.size(); i++){
						if(failhostTable.containsKey(correlatedNodes.get(i))){						
							instantCorrelatedCheckpoint.put(correlatedNodes.get(i), true);						
							sendNow(3, CloudSimTags.CREATE_CHECKPOINT, correlatedNodes.get(i));												
						}					
					}				
				}			
				else{				
					if(instantCorrelatedCheckpoint.containsKey(node)){
						instantCorrelatedCheckpoint.remove(node);										
					}
				}			
				if(RunTimeConstants.failureCorrelation == true && RunTimeConstants.faultToleranceMechanism == 2 && RunTimeConstants.failureCorrelationWithRstrChkpt == true && (!host.isFailed())){
					int counterLocal = 0;				
					if(correlatedFailEventWithChkpt.containsKey(node)){
						counterLocal = counterLocal + correlatedFailEventWithChkpt.get(node);
						counterLocal = counterLocal + 1;
						correlatedFailEventWithChkpt.put(node, counterLocal);
						correlatedRecoveryEventWithChkpt.put(node, counterLocal);
					}
					else{
						counterLocal = counterLocal + 1;
						correlatedFailEventWithChkpt.put(node, counterLocal);
						correlatedRecoveryEventWithChkpt.put(node, counterLocal);
					}
				}			
				if(!host.isFailed()){				
					if(!host.getVmList().isEmpty()){				
						Vm vm;
						Cloudlet clet;							
						long sofarExecutedLength;
						long remainingCloudletLength;				
						long newCloudletLength;
						int size;				
						long oldCheckpointValue;
						long oldCletLength;
						long newSoFarExecutedLength;
						long sofarSinceLastChkpt;				
						double vmUtilization;
						for(int j=0;j<host.getVmList().size();j++){								
							boolean takeChkpt_flag = false;
							vm = host.getVmList().get(j);
							vmUtilization = vmUtl.getCurrentVmUtilization(vm.getId());								
							size =  ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().size();
							cletListIDs = new ArrayList<Integer>();
							for(int k=0;k<size;k++){															
								cletListIDs.add(((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getCloudletId());							
							}
							for(int k=0;k<size;k++){
								clet = broker.getidtoCloudletLst().get(cletListIDs.get(k));		
								remainingCloudletLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();								
								if(clet.getCloudletLength()==remainingCloudletLength){										
									sofarExecutedLength = 0;
								}
								else{									
									sofarExecutedLength = clet.getCloudletLength() - remainingCloudletLength;									
								}						
								if((RunTimeConstants.faultToleranceMechanism == 4)){												
									consolidationCheckpointing = true;
								}
								if(consolidationCheckpointing == true){
									takeChkpt_flag = true;							
								}							
								else{									
									if(!checkpointsForCloudlets.containsKey(clet.getCloudletId())){
										takeChkpt_flag = true;
									}
									else{
										if(failureHappenedForClet.containsKey(clet.getCloudletId())){
											if(sofarExecutedLength < (RunTimeConstants.instructionsOverhead*vmUtilization)){
												takeChkpt_flag = false;
											}
											else{
												takeChkpt_flag = true;
												failureHappenedForClet.remove(clet.getCloudletId());
											}
										}
										else{
											sofarSinceLastChkpt = cletLengthBackup.get(clet.getCloudletId()) - checkpointsForCloudlets.get(clet.getCloudletId());
											sofarSinceLastChkpt = sofarSinceLastChkpt - remainingCloudletLength;										
											if(sofarSinceLastChkpt<(RunTimeConstants.instructionsOverhead*vmUtilization)){													
												takeChkpt_flag = false;
											}
											else{
												takeChkpt_flag = true;
											}
										}
									}
								}													
								if(takeChkpt_flag == true && consolidationCheckpointing == false){
									CheckpointOverhead.setCheckpointOverheadTable(clet.getCloudletId(), (RunTimeConstants.checkpointOverhead * vmUtilization));	
									CheckpointOverhead.setCheckpointOverheadTablePerHost(clet.getCloudletId(), host.getId(), (RunTimeConstants.checkpointOverhead * vmUtilization));																
									if(!checkpointsForCloudlets.containsKey(clet.getCloudletId())){
										checkpointsForCloudlets.put(clet.getCloudletId(), (sofarExecutedLength));																			
										previousChkptSoFarTable.put(clet.getCloudletId(), sofarExecutedLength);										
									}
									else{
										if(!failFlagTable.containsKey(clet.getCloudletId())){											
											checkpointsForCloudlets.put(clet.getCloudletId(), (sofarExecutedLength));																						
										}
										else{
											if(!previousChkptSoFarTable.containsKey(clet.getCloudletId())){
												if(failureOccurredBeforeChkpt.containsKey(clet.getCloudletId())){												
													checkpointsForCloudlets.put(clet.getCloudletId(), (sofarExecutedLength));	
													previousChkptSoFarTable.put(clet.getCloudletId(), sofarExecutedLength);
												}
												else{
													oldCheckpointValue = checkpointsForCloudlets.get(clet.getCloudletId());
													oldCheckpointValue = oldCheckpointValue + sofarExecutedLength;
													checkpointsForCloudlets.put(clet.getCloudletId(), oldCheckpointValue);
													previousChkptSoFarTable.put(clet.getCloudletId(), sofarExecutedLength);								
												}													
											}
											else{											
												newSoFarExecutedLength = sofarExecutedLength - previousChkptSoFarTable.get(clet.getCloudletId());
												previousChkptSoFarTable.put(clet.getCloudletId(), sofarExecutedLength);
												oldCheckpointValue = checkpointsForCloudlets.get(clet.getCloudletId());
												oldCheckpointValue = oldCheckpointValue + newSoFarExecutedLength;
												checkpointsForCloudlets.put(clet.getCloudletId(), oldCheckpointValue);								
											}
										}										
									}
									if(!cletLengthBackup.containsKey(clet.getCloudletId())){
										oldCletLength = clet.getCloudletLength();
										oldCletLength = oldCletLength + (long)(RunTimeConstants.instructionsOverhead * vmUtilization);
										cletLengthBackup.put(clet.getCloudletId(), oldCletLength);
									}
									else{
										oldCletLength = cletLengthBackup.get(clet.getCloudletId());
										oldCletLength = oldCletLength + (long)(RunTimeConstants.instructionsOverhead * vmUtilization);
										cletLengthBackup.put(clet.getCloudletId(), oldCletLength);
									}									
									if(previousConsolidationRemainingTable.containsKey(clet.getCloudletId())){
										long previousRemaining;
										previousRemaining = previousConsolidationRemainingTable.get(clet.getCloudletId());
										previousRemaining = previousRemaining + (long)(RunTimeConstants.instructionsOverhead * vmUtilization);
										previousConsolidationRemainingTable.put(clet.getCloudletId(), previousRemaining);
									}
									newCloudletLength = clet.getCloudletLength() + (long)(RunTimeConstants.instructionsOverhead * vmUtilization);
									clet.setCloudletLength(newCloudletLength);
									remainingCloudletLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();
									cletLastRemainingMap.put(clet.getCloudletId(), remainingCloudletLength);				
								}
								if(takeChkpt_flag == true && consolidationCheckpointing == true){														
									if(!checkpointsForCloudlets.containsKey(clet.getCloudletId())){
										checkpointsForCloudlets.put(clet.getCloudletId(), (sofarExecutedLength));																
										previousChkptSoFarTable.put(clet.getCloudletId(), sofarExecutedLength);										
									}
									else{
										if(!failFlagTable.containsKey(clet.getCloudletId())){											
											checkpointsForCloudlets.put(clet.getCloudletId(), (sofarExecutedLength));																						
										}
										else{
											if(!previousChkptSoFarTable.containsKey(clet.getCloudletId())){
												if(failureOccurredBeforeChkpt.containsKey(clet.getCloudletId())){									
													checkpointsForCloudlets.put(clet.getCloudletId(), (sofarExecutedLength));	
													previousChkptSoFarTable.put(clet.getCloudletId(), sofarExecutedLength);
												}
												else{
													oldCheckpointValue = checkpointsForCloudlets.get(clet.getCloudletId());
													oldCheckpointValue = oldCheckpointValue + sofarExecutedLength;
													checkpointsForCloudlets.put(clet.getCloudletId(), oldCheckpointValue);
													previousChkptSoFarTable.put(clet.getCloudletId(), sofarExecutedLength);									
												}													
											}
											else{											
												newSoFarExecutedLength = sofarExecutedLength - previousChkptSoFarTable.get(clet.getCloudletId());
												previousChkptSoFarTable.put(clet.getCloudletId(), sofarExecutedLength);
												oldCheckpointValue = checkpointsForCloudlets.get(clet.getCloudletId());
												oldCheckpointValue = oldCheckpointValue + newSoFarExecutedLength;
												checkpointsForCloudlets.put(clet.getCloudletId(), oldCheckpointValue);									
											}
										}										
									}							
								}									
							}								
							if(takeChkpt_flag == true){
								totalCheckpointEvents = totalCheckpointEvents + 1;							
							}
						}							
					}	
					consolidationCheckpointing = false;
				}
				if(furtherCheckpoints == true){
					if(RunTimeConstants.predictionFlag == false && RunTimeConstants.failureCorrelationWithRstrChkpt == false){							
						send(3, checkpointInterval.get(host.getId()), CloudSimTags.CREATE_CHECKPOINT, node); //Checkpoint will be scheduled in any case whether it is idle or active.
					}					
				}			
			}			
		}
		public HashMap<Integer, Integer>vmReplicationTable = new HashMap<Integer, Integer>();
		public HashMap<Integer, Integer>cletReplicationTable = new HashMap<Integer, Integer>();
		public HashMap<Integer, Boolean>replicaActivationTable = new HashMap<Integer, Boolean>();
		//public HashMap<>
		public void createReplication(int node){			
			int rand = 0; 
			Host host;							
			for(int j=0;j<hostList().size();j++){
				if(failhostTable.containsKey(node)){
					rand=failhostTable.get(node);
					break;
				}	
			}			
			host = hostList().get(rand);
			if(host.isFailed()){
				System.out.println("Present state of the host #" +host.getId()+ " is failed or sitting Idle. So, replication will not be created");
			}
			else{
				int hostListSize = getHostList().size();
				System.out.println("Replication creation process for host #" +host.getId()+" is started");				
					if(host.getVmList().isEmpty()){
						System.out.println("No virtual machine is running on host # " +host.getId()+ ". So replication is not required");						
					}else{
						List<Vm>tempVmList = new ArrayList<Vm>();
						tempVmList.addAll(host.getVmList());
						int VmID = 0;
						for(int i=0; i<getVmList().size();i++){
							if(VmID <= getVmList().get(i).getId()){
								VmID = getVmList().get(i).getId();
							}
						}
						for(int i=0; i<broker.VmListBackup.size(); i++){
							if(VmID <= broker.vmIDBackupList.get(i)){
								VmID = broker.vmIDBackupList.get(i);
							}
						}
						ArrayList<Cloudlet>replicatedClets;
						ArrayList<Integer>cletIDList;						
						for(int i=0; i<tempVmList.size(); i++){
							Vm replicatedVm;
							Vm currentVm;
							currentVm = tempVmList.get(i);
							VmID++;	
							int mips = 1000;
							long size = currentVm.getSize(); // image size (MB)
							int ram = currentVm.getRam(); // vm memory (MB)
							long bw = currentVm.getBw();
							int pesNumber = currentVm.getNumberOfPes();
							utilization = 0;
							String vmm = "Xen"; // VMM name
							replicatedVm = new Vm(VmID, getId(), mips, pesNumber, ram, bw, size, vmm, new FailureCloudletSchedulerTimeShared());
							getVmList().add(replicatedVm);							
							currentVm = tempVmList.get(i);
							replicatedClets = new ArrayList<Cloudlet>();
							int cletListSize;
							cletListSize = ((FailureCloudletSchedulerTimeShared)currentVm.getCloudletScheduler()).getCloudletExecList().size();
							cletIDList = new ArrayList<Integer>();
							for(int j=0; j<cletListSize; j++){
								int cletID = ((FailureCloudletSchedulerTimeShared)currentVm.getCloudletScheduler()).getCloudletExecList().get(j).getCloudletId();
								cletIDList.add(cletID);								
							}
							for(int j=0; j<cletListSize; j++){
								Cloudlet currentClet, replicatedClet;								
								currentClet = broker.getidtoCloudletLst().get(cletIDList.get(j));
								replicatedClet = broker.CreateCloudlets(currentClet);
								replicatedClets.add(replicatedClet);
								cletReplicationTable.put(currentClet.getCloudletId(), replicatedClet.getCloudletId());
								broker.bindCloudletToVm(replicatedClet.getCloudletId(), replicatedVm.getId());
							}							
							boolean result = getVmAllocationPolicy().allocateHostForVm(replicatedVm);
							if(!result){
								System.out.println("Vm allocation is failed");
								System.exit(0);
							}
							System.out.println("Replication of Vm " +replicatedVm.getId()+ " is created");
							if(hostListSize > getHostList().size()){
								Host newHost;
								newHost = replicatedVm.getHost();
								newHost.setDatacenter(this);
								processNewHost(newHost);
							}
						}
						updateCloudletProcessing();
					}
				}		
			}
		
		public void processRecoveryEventWithReplication(int node){
			
			
			
			
			
		}
		
		public HashMap<Integer, Boolean>failFlagTable = new HashMap<Integer, Boolean>();
		int chkptCounter = 0 ;
		public void createCheckpointsWithMigration(int node){			
			updateCloudletProcessing();
			//if(RunTimeConstants.predictionFlag == true && firstCheckpointFlag.get(node)==true){			
			//	System.out.println("This is the checkpoint corresponding to first prediction. So it will be ignored");
			//	firstCheckpointFlag.put(node, false);
			//}
			//else{		
			if(RunTimeConstants.test == true){
				int rand = 0; 
				Host host;						
				for(int j=0;j<hostList().size();j++){
					if(failhostTable.containsKey(node)){
						rand=failhostTable.get(node);
						break;
					}	
				}			
				host = hostList().get(rand);		
				
				if(RunTimeConstants.failureCorrelation == true && RunTimeConstants.faultToleranceMechanism == 4 && !instantCorrelatedCheckpoint.containsKey(node)){
					ArrayList<Integer>correlatedNodes = new ArrayList<Integer>();					
					String nodeLocation;
					nodeLocation = freadGrid5000.getClusterNameForNode(node);
					correlatedNodes.addAll(kMeans.getCorrelatedNodes(nodeLocation, node));
					for(int i=0; i<correlatedNodes.size(); i++){
						if(correlatedNodes.get(i).equals(node)){
							correlatedNodes.remove(i);
							break;
						}
					}					
					for(int i=0; i<correlatedNodes.size(); i++){
						if(failhostTable.containsKey(correlatedNodes.get(i))){						
							instantCorrelatedCheckpoint.put(correlatedNodes.get(i), true);							
							sendNow(3, CloudSimTags.CREATE_CHECKPOINT_WITH_MIGRATION, correlatedNodes.get(i));													
						}						
					}					
				}			
				else{				
					if(instantCorrelatedCheckpoint.containsKey(node)){
						instantCorrelatedCheckpoint.remove(node);										
					}
				}			
				
				if(host.isFailed()){
					System.out.println("Present state of the host #" +host.getId()+ " is failed or sitting Idle. So, no checkpoints will be taken");
				}			
				else{						
					System.out.println("Checkpoint for host #" +host.getId()+" is being created");				
					System.out.println("Clock value while saving checkpoints is " +CloudSim.clock());
					if(host.getVmList().isEmpty()){
					//	updateCloudletProcessing();
						System.out.println("No virtual machine is running on host # " +host.getId());												
					}
					else{					
						Vm vm;
						Cloudlet clet;							
						long sofarExecutedLength;
						long remainingCloudletLength;					
						int size;			
						long oldCheckpointValue;
						long newSoFarExecutedLength;
						//updateCloudletProcessing();	
						for(int j=0;j<host.getVmList().size();j++){							
							vm = host.getVmList().get(j);								
							size =  ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().size();
							cletListIDs = new ArrayList<Integer>();
							for(int k=0;k<size;k++){															
								cletListIDs.add(((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getCloudletId());
								if(cletListIDs.get(k) == 328){
									System.out.println("Check");
								}
							}
							for(int k=0;k<size;k++){								
								clet = broker.getidtoCloudletLst().get(cletListIDs.get(k));		
								remainingCloudletLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();								
								if(clet.getCloudletLength()==remainingCloudletLength){										
									sofarExecutedLength = 0;
								}
								else{									
									sofarExecutedLength = clet.getCloudletLength() - remainingCloudletLength;									
								}																
								System.out.println(CloudSim.clock()+ " Old length of cloudlet #" +clet.getCloudletId()+ " is " +clet.getCloudletLength());								
								System.out.println(CloudSim.clock()+ " Cloudlet #" +clet.getCloudletId()+ " so far executed while taking checkpoint was " +sofarExecutedLength);								
								if(!checkpointsForCloudlets.containsKey(clet.getCloudletId())){
									checkpointsForCloudlets.put(clet.getCloudletId(), (sofarExecutedLength));
									cletSoFarMap.put(clet.getCloudletId(), (sofarExecutedLength));
								}
								else{
									if(!failFlagTable.containsKey(clet.getCloudletId())){
										newSoFarExecutedLength = sofarExecutedLength - checkpointsForCloudlets.get(clet.getCloudletId());																				
										cletSoFarMap.put(clet.getCloudletId(), (newSoFarExecutedLength));
										checkpointsForCloudlets.put(clet.getCloudletId(), (sofarExecutedLength));
									}else{
										if(!previousChkptSoFarTable.containsKey(clet.getCloudletId())){
											if(failureOccurredBeforeChkpt.containsKey(clet.getCloudletId())){
												newSoFarExecutedLength = sofarExecutedLength - checkpointsForCloudlets.get(clet.getCloudletId());																				
												cletSoFarMap.put(clet.getCloudletId(), (newSoFarExecutedLength));
												checkpointsForCloudlets.put(clet.getCloudletId(), (sofarExecutedLength));	
												previousChkptSoFarTable.put(clet.getCloudletId(), sofarExecutedLength);
											}
											else{
												oldCheckpointValue = checkpointsForCloudlets.get(clet.getCloudletId());
												oldCheckpointValue = oldCheckpointValue + sofarExecutedLength;
												checkpointsForCloudlets.put(clet.getCloudletId(), oldCheckpointValue);
												previousChkptSoFarTable.put(clet.getCloudletId(), sofarExecutedLength);
												cletSoFarMap.put(clet.getCloudletId(), (sofarExecutedLength));
											}
										}
										else{											
											newSoFarExecutedLength = sofarExecutedLength - previousChkptSoFarTable.get(clet.getCloudletId());
											previousChkptSoFarTable.put(clet.getCloudletId(), sofarExecutedLength);
											oldCheckpointValue = checkpointsForCloudlets.get(clet.getCloudletId());
											oldCheckpointValue = oldCheckpointValue + newSoFarExecutedLength;
											checkpointsForCloudlets.put(clet.getCloudletId(), oldCheckpointValue);
											cletSoFarMap.put(clet.getCloudletId(), newSoFarExecutedLength);
										}
									}							
								}
							}			
							totalCheckpointEvents = totalCheckpointEvents + 1;
						}						
					}				
				}				
			}
			else{	//This is non-testing case			
				int rand = 0; 
				Host host;						
				for(int j=0;j<hostList().size();j++){
					if(failhostTable.containsKey(node)){
						rand=failhostTable.get(node);
						break;
					}	
				}			
				host = hostList().get(rand);				
				if(RunTimeConstants.failureCorrelation == true && RunTimeConstants.faultToleranceMechanism == 4 && !instantCorrelatedCheckpoint.containsKey(node)){
					ArrayList<Integer>correlatedNodes = new ArrayList<Integer>();					
					String nodeLocation;
					nodeLocation = freadGrid5000.getClusterNameForNode(node);
					correlatedNodes.addAll(kMeans.getCorrelatedNodes(nodeLocation, node));
					for(int i=0; i<correlatedNodes.size(); i++){
						if(correlatedNodes.get(i).equals(node)){
							correlatedNodes.remove(i);
							break;
						}
					}					
					for(int i=0; i<correlatedNodes.size(); i++){
						if(failhostTable.containsKey(correlatedNodes.get(i))){						
							instantCorrelatedCheckpoint.put(correlatedNodes.get(i), true);							
							sendNow(3, CloudSimTags.CREATE_CHECKPOINT_WITH_MIGRATION, correlatedNodes.get(i));													
						}						
					}					
				}			
				else{				
					if(instantCorrelatedCheckpoint.containsKey(node)){
						instantCorrelatedCheckpoint.remove(node);										
					}
				}		
				if(!host.isFailed()){				
					if(!host.getVmList().isEmpty()){					
						Vm vm;
						Cloudlet clet;							
						long sofarExecutedLength;
						long remainingCloudletLength;					
						int size;			
						long oldCheckpointValue;
						long newSoFarExecutedLength;							
						for(int j=0;j<host.getVmList().size();j++){							
							vm = host.getVmList().get(j);								
							size =  ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().size();
							cletListIDs = new ArrayList<Integer>();
							for(int k=0;k<size;k++){															
								cletListIDs.add(((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getCloudletId());								
							}
							for(int k=0;k<size;k++){								
								clet = broker.getidtoCloudletLst().get(cletListIDs.get(k));		
								remainingCloudletLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();								
								if(clet.getCloudletLength()==remainingCloudletLength){										
									sofarExecutedLength = 0;
								}
								else{									
									sofarExecutedLength = clet.getCloudletLength() - remainingCloudletLength;									
								}																
								if(!checkpointsForCloudlets.containsKey(clet.getCloudletId())){
									checkpointsForCloudlets.put(clet.getCloudletId(), (sofarExecutedLength));
									cletSoFarMap.put(clet.getCloudletId(), (sofarExecutedLength));
								}
								else{
									if(!failFlagTable.containsKey(clet.getCloudletId())){
										newSoFarExecutedLength = sofarExecutedLength - checkpointsForCloudlets.get(clet.getCloudletId());																				
										cletSoFarMap.put(clet.getCloudletId(), (newSoFarExecutedLength));
										checkpointsForCloudlets.put(clet.getCloudletId(), (sofarExecutedLength));
									}else{
										if(!previousChkptSoFarTable.containsKey(clet.getCloudletId())){
											if(failureOccurredBeforeChkpt.containsKey(clet.getCloudletId())){
												newSoFarExecutedLength = sofarExecutedLength - checkpointsForCloudlets.get(clet.getCloudletId());																				
												cletSoFarMap.put(clet.getCloudletId(), (newSoFarExecutedLength));
												checkpointsForCloudlets.put(clet.getCloudletId(), (sofarExecutedLength));	
												previousChkptSoFarTable.put(clet.getCloudletId(), sofarExecutedLength);
											}
											else{
												oldCheckpointValue = checkpointsForCloudlets.get(clet.getCloudletId());
												oldCheckpointValue = oldCheckpointValue + sofarExecutedLength;
												checkpointsForCloudlets.put(clet.getCloudletId(), oldCheckpointValue);
												previousChkptSoFarTable.put(clet.getCloudletId(), sofarExecutedLength);
												cletSoFarMap.put(clet.getCloudletId(), (sofarExecutedLength));
											}
										}
										else{											
											newSoFarExecutedLength = sofarExecutedLength - previousChkptSoFarTable.get(clet.getCloudletId());
											previousChkptSoFarTable.put(clet.getCloudletId(), sofarExecutedLength);
											oldCheckpointValue = checkpointsForCloudlets.get(clet.getCloudletId());
											oldCheckpointValue = oldCheckpointValue + newSoFarExecutedLength;
											checkpointsForCloudlets.put(clet.getCloudletId(), oldCheckpointValue);
											cletSoFarMap.put(clet.getCloudletId(), newSoFarExecutedLength);
										}
									}							
								}
							}			
							totalCheckpointEvents = totalCheckpointEvents + 1;
						}						
					}				
				}			
			}
		}			
		
		
		/**
		 * 
		 * Failure Event with Checkpoints
		 * 
		 * 
		 */
		
		public HashMap<Integer, Double> failTimeTable = new HashMap<Integer, Double>(); 
		public HashMap<Integer, Boolean>failureHappenedForClet = new HashMap<Integer, Boolean>();
		public HashMap<Integer, Long>cletSoFarTableTemp = new HashMap<Integer, Long>();
		int failCheckCounter = 0;
		int counterLocal = 0;
		protected void processFailureEventWithCheckpointing(int node){
			updateCloudletProcessing();
			if(RunTimeConstants.test == true)
			{
				/*
				if(firstFailureFlag.get(node)== true){
					System.out.println("This is the first failure and will be ignored inorder to create optimized number of checkpoints");
					firstFailureFlag.put(node, false);
					if(RunTimeConstants.predictionFlag == false){
						int hostID;
						hostID = HostMapping.getHostID(node);
						send(3, checkpointInterval.get(hostID), CloudSimTags.CREATE_CHECKPOINT, node);	
					}
				}
				*/
			//	else{
				//if(node == 875){
				//	counterLocal = counterLocal + 1;
				//}
				if(node == 540){
					System.out.println("Check");
				}
					boolean flag_fail = false;
					int rand=0;			
					Vm vm;					
					int hostCount;
					flag_record = true;   //This flag is being used in DatacenterBroker to record only the activities that happened after the occurrence of a failure. 
				
					if(lock_sufferedCletList == false){
						broker.setSufferedCloudletsList();
						lock_sufferedCletList = true;
					}
				
					for(int j=0;j<hostList().size();j++){
						if(failhostTable.containsKey(node)){
							rand=failhostTable.get(node);
							break;
						}					
					}				
					fail = hostList().get(rand); // Node that is set to be failed		
						
					if(fail.isFailed()){					
						System.out.println( "Host " +node+ " has already been failed");	
						flag_fail=true;
					}		
				
					if(flag_fail==false){
						//If getCloudletReceivedList() is not empty then it means that the cloudlets have been processed without any failure. 
						// By checking the contents, it has been ensured that a failure will not occur after the completion of execution.
						//finishedcletList=Dbr.getCloudletReceivedList();
						vmPeMap = new HashMap<Integer, Integer>();
						peDownTimeMap = new HashMap<Integer, Double>();
						System.out.println("Size of the cloudlet received list is " +broker.getCloudletReceivedList().size());
					
						if(broker.getCloudletReceivedList().size()<broker.getCloudletSubmittedList().size()){				
							Log.printLine("Failure has occurred at " +CloudSim.clock()+ " for host " +node);						
							System.out.println("Size of host list is " +hostList().size());			
							System.out.println("Id of the Failing Host in the provisioned nodes list is  #" +fail.getId());	// ID of the failing host							
					
							if(fail.getVmList().size()==0){									
								System.out.println("No virtual machine and cloudlets are running on the failing node");								
								System.out.println("Setting host to the state Fail");									
								hostvmTable.put(fail, null);									
								hostvmIdTable.put(fail, null);									
								fail.setFailed(true);								
								failedHosts.add(node); // Maintaining the list of failed hosts.								
							}
							else{
								totalFailureCount = totalFailureCount + 1; //This counts the number of failures that actually affects the execution of virtual machines on the given node.						
								failVMs = new ArrayList<Vm>();						
								failVMsID = new ArrayList<Integer>();										
								System.out.println("Number of VMs running on failed host are " +fail.getVmList().size());		
						
								List<Pe>peList = new ArrayList<Pe>();																							
								for(int i=0;i<fail.getVmList().size();i++){	// Running upto the number of virtual machines running on the failing host											
									vm = fail.getVmList().get(i); //Getting a VM at an index i.							
									peList = fail.getVmScheduler().getPesAllocatedForVM(vm);							
									for(int x=0; x<peList.size(); x++){
										System.out.println("PE Id for VM " +vm.getId()+ " is " +peList.get(x).getId());
										vmPeMap.put(vm.getId(), peList.get(x).getId());
										peDownTimeMap.put(peList.get(x).getId(), CloudSim.clock());
									}												
									System.out.println("Virtual Machine ID running on the failing host is " +vm.getId()); // Id of the VM at an index vm.														
									failVMs.add(vm);							
									failVMsID.add(vm.getId()); //List of the IDs of failing virtual machines							
									DownTimeTable.setFailTimeTable(vm, CloudSim.clock());										
								}											
								hostvmTable.put(fail, failVMs); // Table of failed hosts and their corresponding failed virtual machines									
								hostvmIdTable.put(fail, failVMsID); // Table of failed hosts and their corresponding failed virtual machine IDs																						
								//	clvmList.putAll(broker.getmaplistCloudlettovm()); // This table is storing VM IDs and corresponding Cloudlet IDs											
								usrclList.putAll(broker.getmaplistUsertocloudlet()); // This table is storing Cloudlet IDs and corresponding User IDs					
								hostVMPeMap.put(fail.getId(), vmPeMap);						
								hostPeDownTimeMap.put(fail.getId(), peDownTimeMap);												
								for(int i=0;i<failVMsID.size();i++){						
									int size;
									cletsTempID = new ArrayList<Integer>();							
									size = ((FailureCloudletSchedulerTimeShared)failVMs.get(i).getCloudletScheduler()).getCloudletExecList().size();							
									for(int j=0; j<size; j++){
										cletsTempID.add(((FailureCloudletSchedulerTimeShared)failVMs.get(i).getCloudletScheduler()).getCloudletExecList().get(j).getCloudletId());
										if(cletsTempID.get(j)==12 && counterLocal >= 936){
											//failCheckCounter = failCheckCounter + 1;
											//if(failCheckCounter > 5){
												System.out.println("Check");
											//}										
										}
									}							
									failedVMCletMap.put(failVMsID.get(i), cletsTempID);							
									System.out.println("IDs of Cloudlets running on a failing virtual machine are ");							
									for(int j=0; j<size; j++){
										System.out.println(+cletsTempID.get(j));							
									}								
									
									for(int j=0;j<size; j++){																
										long sofarCletLength;				
										long remainingCletLength;						
										long cletLength;										
										remainingCletLength = ((FailureCloudletSchedulerTimeShared)failVMs.get(i).getCloudletScheduler()).getCloudletExecList().get(j).getRemainingCloudletLength();								
										remainingCletLengthTable.put(cletsTempID.get(j), remainingCletLength);								
										cletLength = broker.getCloudletFromID(cletsTempID.get(j)).getCloudletLength();														
										sofarCletLength = cletLength - remainingCletLength;									
										failed_time = CloudSim.clock();								
										System.out.println("Cloudlet #" +cletsTempID.get(j)+ " executed so far during failure is " +sofarCletLength);								
										System.out.println("Cloudlet #" +cletsTempID.get(j)+ " remaining length during failure is " +remainingCletLength);								
										DownTimeTable.setCletFailTimeTable(cletsTempID.get(j), failed_time);								
										failTimeTable.put(cletsTempID.get(j), failed_time);										
									}								
									
									for(int j=0;j<size; j++){
										if(previousChkptSoFarTable.containsKey(cletsTempID.get(j))){
											previousChkptSoFarTable.remove(cletsTempID.get(j));
										}
										if(checkpointsForCloudlets.containsKey(cletsTempID.get(j))){
											failureHappenedForClet.put(cletsTempID.get(j), true);
										}
										processCloudletCancel(cletsTempID.get(j), usrclList.get(cletsTempID.get(j)), failVMsID.get(i));								
										usrclList.remove(cletsTempID.get(j)); // Removing the cloudlets (keys) from the hashtable		
									}						
									for(int k=0;k<getVmList().size();k++){								
										if(getVmList().get(k)==failVMs.get(i)){									
											getVmList().remove(k);									
											break;											 				
										}
									}																											
								}  	
								System.out.println("Destroying all the virtual machines running on the failing host");						
								for(int i=0; i<failVMs.size(); i++){
									fail.vmDestroy(failVMs.get(i));
								}																															
								System.out.println("Setting host to the state Fail");										
								failedHosts.add(node);										
								fail.setFailed(true);				
								
								IdleTime.setHostActiveStatus(fail.getId(), false);															
								hostCount = IdleTime.getActiveHostCount();									
								HostCounter.setHostCount(hostCount);
								HostCounter.setClockList(CloudSim.clock());									
							}					
						}	
					else{					
						System.out.println(CloudSim.clock()+ " Processing of all the cloudlets has been finished without a failure");					
					}
				}
			//}
		}		
				else{					
					boolean flag_fail = false;
					int rand=0;			
					Vm vm;					
					int hostCount;
					flag_record = true;   //This flag is being used in DatacenterBroker to record only the activities that happened after the occurrence of a failure.				
					if(lock_sufferedCletList == false){
						broker.setSufferedCloudletsList();
						lock_sufferedCletList = true;
					}				
					for(int j=0;j<hostList().size();j++){
						if(failhostTable.containsKey(node)){
							rand=failhostTable.get(node);
							break;
						}					
					}				
					fail = hostList().get(rand); // Node that is set to be failed					
					if(fail.isFailed()){					
						flag_fail=true;
					}				
					if(flag_fail==false){						
						vmPeMap = new HashMap<Integer, Integer>();
						peDownTimeMap = new HashMap<Integer, Double>();				
						if(broker.getCloudletReceivedList().size()<broker.getCloudletSubmittedList().size()){
							if(fail.getVmList().size()==0){																	
								hostvmTable.put(fail, null);									
								hostvmIdTable.put(fail, null);									
								fail.setFailed(true);								
								failedHosts.add(node); // Maintaining the list of failed hosts.								
							}
							else{
								totalFailureCount = totalFailureCount + 1; //This counts the number of failures that actually affects the execution of virtual machines on the given node.						
								failVMs = new ArrayList<Vm>();						
								failVMsID = new ArrayList<Integer>();
								List<Pe>peList = new ArrayList<Pe>();																							
								for(int i=0;i<fail.getVmList().size();i++){	// Running upto the number of virtual machines running on the failing host											
									vm = fail.getVmList().get(i); //Getting a VM at an index i.	
								//	if(vm.getId()==4){
								//		System.out.println("Check");
								//	}
									peList = fail.getVmScheduler().getPesAllocatedForVM(vm);							
									for(int x=0; x<peList.size(); x++){										
										vmPeMap.put(vm.getId(), peList.get(x).getId());
										peDownTimeMap.put(peList.get(x).getId(), CloudSim.clock());
									}																						
									failVMs.add(vm);							
									failVMsID.add(vm.getId()); //List of the IDs of failing virtual machines							
									DownTimeTable.setFailTimeTable(vm, CloudSim.clock());										
								}											
								hostvmTable.put(fail, failVMs); // Table of failed hosts and their corresponding failed virtual machines									
								hostvmIdTable.put(fail, failVMsID); // Table of failed hosts and their corresponding failed virtual machine IDs																				
								usrclList.putAll(broker.getmaplistUsertocloudlet()); // This table is storing Cloudlet IDs and corresponding User IDs					
								hostVMPeMap.put(fail.getId(), vmPeMap);						
								hostPeDownTimeMap.put(fail.getId(), peDownTimeMap);												
								for(int i=0;i<failVMsID.size();i++){						
									int size;
									cletsTempID = new ArrayList<Integer>();							
									size = ((FailureCloudletSchedulerTimeShared)failVMs.get(i).getCloudletScheduler()).getCloudletExecList().size();							
									for(int j=0; j<size; j++){
										cletsTempID.add(((FailureCloudletSchedulerTimeShared)failVMs.get(i).getCloudletScheduler()).getCloudletExecList().get(j).getCloudletId());										
									}							
									failedVMCletMap.put(failVMsID.get(i), cletsTempID);				
									for(int j=0;j<size; j++){													
										long remainingCletLength;						
										long cletLength;										
										remainingCletLength = ((FailureCloudletSchedulerTimeShared)failVMs.get(i).getCloudletScheduler()).getCloudletExecList().get(j).getRemainingCloudletLength();								
										remainingCletLengthTable.put(cletsTempID.get(j), remainingCletLength);								
										cletLength = broker.getCloudletFromID(cletsTempID.get(j)).getCloudletLength();																		
										failed_time = CloudSim.clock();															
										DownTimeTable.setCletFailTimeTable(cletsTempID.get(j), failed_time);								
										failTimeTable.put(cletsTempID.get(j), failed_time);										
									}								
									
									for(int j=0;j<size; j++){
										if(previousChkptSoFarTable.containsKey(cletsTempID.get(j))){
											previousChkptSoFarTable.remove(cletsTempID.get(j));
										}
										if(checkpointsForCloudlets.containsKey(cletsTempID.get(j))){
											failureHappenedForClet.put(cletsTempID.get(j), true);
										}
										processCloudletCancel(cletsTempID.get(j), usrclList.get(cletsTempID.get(j)), failVMsID.get(i));								
										usrclList.remove(cletsTempID.get(j)); // Removing the cloudlets (keys) from the hashtable		
									}						
									for(int k=0;k<getVmList().size();k++){								
										if(getVmList().get(k)==failVMs.get(i)){									
											getVmList().remove(k);									
											break;											 				
										}
									}																											
								}						
								for(int i=0; i<failVMs.size(); i++){
									fail.vmDestroy(failVMs.get(i));
								}																	
								failedHosts.add(node);										
								fail.setFailed(true);	
								IdleTime.setHostActiveStatus(fail.getId(), false);															
								hostCount = IdleTime.getActiveHostCount();									
								HostCounter.setHostCount(hostCount);
								HostCounter.setClockList(CloudSim.clock());									
							}					
						}
					}				
				}
			}
		
		/**
		 * 
		 * Recovery Event with Checkpoints
		 * 
		 * 
		 */
		

		protected void processRecoveryEventWithCheckpointing(int node){
			if(RunTimeConstants.test == true)
			{
			//	if(firstRecoveryFlag.get(node)==true){
			//		System.out.println("This event is for first recovery which needs to be ignored");
			//		firstRecoveryFlag.put(node, false);
			//	}
			//	else{
				if(node == 540){
					System.out.println("Check");
				}
					updateCloudletProcessing();
					Host recoveringHost;			
					ArrayList<Cloudlet> recoveringCloudlet;		
					int hostCount;
					//long chkptTime_total = 0;					
					recoveringHost = hostList().get(failhostTable.get(node));			
					if(recoveringHost.isFailed()){							
						System.out.println("This recovery was scheduled at " +CloudSim.clock());		
						System.out.println("Host " +recoveringHost+ " with ID " +recoveringHost.getId()+ "  has been recovered from a failure");
						recoveringHost.setFailed(false);				
						if(recoveringHost.getDatacenter()==null){
							recoveringHost.setDatacenter(this);
						}		
						for(int i=0;i<failedHosts.size();i++){
							if(failedHosts.get(i)==node){
								failedHosts.remove(i);
							}
						}					
						if(hostvmTable.containsKey(recoveringHost)){			
							failVMs = new ArrayList<Vm>();
							failVMsID = new ArrayList<Integer>();				
							failVMs=hostvmTable.get(recoveringHost); // Retrieving failed virtual machines
							failVMsID = hostvmIdTable.get(recoveringHost); // Retrieving failed virtual machines IDs					
							if(failVMs == null){
								System.out.println("Though no VMs were running on host #" +recoveringHost.getId()+ " but it will be recovered because of the dynamic cloudlets");						
							}
							else{
								IdleTime.setHostActiveStatus(recoveringHost.getId(), true);															
								hostCount = IdleTime.getActiveHostCount();									
								HostCounter.setHostCount(hostCount);
								HostCounter.setClockList(CloudSim.clock());									
								
								int size=failVMs.size();
								System.out.println("Number of VMs running on host " +recoveringHost+ " of ID "+ recoveringHost.getId()+ " are " +size);
								int sizeCletList;
								vmPeMap = new HashMap<Integer, Integer>();
								vmPeMap = hostVMPeMap.get(recoveringHost.getId());						
								peDownTimeMap = new HashMap<Integer, Double>();
								peDownTimeMap = hostPeDownTimeMap.get(recoveringHost.getId());							
								Set<Integer>peKeySet = new HashSet<Integer>();
								peKeySet = peDownTimeMap.keySet();
								ArrayList<Integer>peKeySetList = new ArrayList<Integer>(peKeySet);				
								for(int i=0; i<peDownTimeMap.size(); i++){
									double downTime;				
									downTime = CloudSim.clock() - peDownTimeMap.get(peKeySetList.get(i));
									IdleTime.setHostPeDownTime(recoveringHost.getId(), peKeySetList.get(i), downTime);
								}
								hostPeDownTimeMap.remove(recoveringHost.getId());						
								for(int i=0;i<size;i++){
									sizeCletList = failedVMCletMap.get(failVMsID.get(i)).size();
									cletsTempID = new ArrayList<Integer>();
									cletsTempID = failedVMCletMap.get(failVMsID.get(i));									
									failedVMCletMap.remove(failVMsID.get(i));	
									failVMs.get(i).setBeingInstantiated(true); //This matters. If this variable will not set to true then the reallocation of resources will not happen and estimatedFinishingTime of a corresponding cloudlet will set to infinity.							
									recoveringCloudlet = new ArrayList<Cloudlet>();
									double cletFailTime = 0.0;
									double cletDownTime = 0.0;
									//long newlength;								
									double down_time;
									long cletLengthTemp;
									long soFarClet;
									long oldBackupLength;				
								//	long chkptRemaining;	
									long currentRemaining;
									// This part of the function is saving the recovering cloudlets for the future use. Cloudlets from this list will not be removed.							
									for(int j=0; j<sizeCletList; j++){							
									//	if(cletsTempID.get(j)==12 && counterLocal >= 936){
									//		System.out.println("Check");
									//	}
										failFlagTable.put(cletsTempID.get(j), true);
										cletFailTime = DownTimeTable.getCletFailTimeTable(cletsTempID.get(j));
										cletDownTime = CloudSim.clock() - cletFailTime;			
										recoveringCloudlet.add(broker.getidtoCloudletLst().get(cletsTempID.get(j)));																			
										if(!cletLengthBackup.containsKey(cletsTempID.get(j))){
											cletLengthBackup.put(cletsTempID.get(j), recoveringCloudlet.get(j).getCloudletLength());
										}
										if(!checkpointsForCloudlets.containsKey(cletsTempID.get(j))){
											soFarClet = CloudletStatus.getsofarCloudlet(cletsTempID.get(j));
											//soFarClet = cletSoFarTableTemp.get(cletsTempID.get(j));
											cletLengthTemp = recoveringCloudlet.get(j).getCloudletLength() + ((long)cletDownTime*1000);
											oldBackupLength = cletLengthBackup.get(cletsTempID.get(j));
											oldBackupLength = oldBackupLength + ((long)cletDownTime*1000);
											cletLengthBackup.put(cletsTempID.get(j), oldBackupLength);
											failureOccurredBeforeChkpt.put(cletsTempID.get(j), true);
										}
										else{											
											cletLengthTemp = cletLengthBackup.get(cletsTempID.get(j)) - checkpointsForCloudlets.get(cletsTempID.get(j));
											currentRemaining = remainingCletLengthTable.get(cletsTempID.get(j));
											soFarClet = cletLengthTemp - currentRemaining;
											//soFarClet = cletSoFarTableTemp.get(cletsTempID.get(j));										
											//cletLengthTemp = cletLengthBackup.get(cletsTempID.get(j)) - checkpointsForCloudlets.get(cletsTempID.get(j));
											//cletLengthTemp = chkptRemaining;
											cletLengthTemp = cletLengthTemp + ((long)cletDownTime*1000);
											oldBackupLength = cletLengthBackup.get(cletsTempID.get(j));
											oldBackupLength = oldBackupLength + (long)cletDownTime*1000;
											cletLengthBackup.put(cletsTempID.get(j), oldBackupLength);
											if(failureOccurredBeforeChkpt.containsKey(cletsTempID.get(j))){
												failureOccurredBeforeChkpt.remove(cletsTempID.get(j));
											}
											if(backtobackConsolidationsFlag.containsKey(cletsTempID.get(j))){
												backtobackConsolidationsFlag.remove(cletsTempID.get(j));
											}
										}
										recoveringCloudlet.get(j).setCloudletLength(cletLengthTemp);
										cletLastRemainingMap.put(cletsTempID.get(j), cletLengthTemp);
										System.out.println("New Length of cloudlet " +cletsTempID.get(j)+" after the recovery from a failure is " +cletLengthTemp);
										System.out.println("Failure downtime overhead added to the length of cloudlet "+cletsTempID.get(j)+" is "+cletDownTime);
										System.out.println("Failure re-execution overhead added to the length of cloudlet "+cletsTempID.get(j)+" is "+soFarClet);																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																	
										DownTimeTable.setCloudletDownTime(cletsTempID.get(j), cletDownTime);
										CloudletReexecutionPart.setCletReexecutionTime(cletsTempID.get(j), soFarClet);	
										DownTimeTable.setCletPerHostDownTimeTable(cletsTempID.get(j), recoveringHost.getId(), cletDownTime);
										CloudletReexecutionPart.setCletPerHostReexecutionTimeTable(cletsTempID.get(j), recoveringHost.getId(), soFarClet);
										
									}
									recoveringHost.vmCreate(failVMs.get(i)); //Creating a virtual machine on the host with the same name that the VM had before the failure event.
						
									List<Pe> peList = new ArrayList<Pe>();
									peList = recoveringHost.getVmScheduler().getPesAllocatedForVM(failVMs.get(i));
									for(int x=0; x<peList.size(); x++){
										int peID = peList.get(x).getId();
										int previousPeID = vmPeMap.get(failVMs.get(i).getId());										
										if(peID != previousPeID){
											IdleTime.setHostPeIdleTable(recoveringHost.getId(), previousPeID, true);									
											IdleTime.setHostPeIdleClockTable(recoveringHost.getId(), previousPeID, CloudSim.clock());									
											IdleTime.setHostPeIdleTable(recoveringHost.getId(), peID, false);											
											if(IdleTime.checkHostPeIdleClockTable(recoveringHost.getId(), peID)){
												IdleTime.setHostPeIdleTimeTable(recoveringHost.getId(), peID, CloudSim.clock());
												IdleTime.removeHostPeIdleClockTable(recoveringHost.getId(),peID);									
											}
										}
									}																									
									failVMs.get(i).setId(failVMsID.get(i)); // Setting the ID of the new virtual machine equal to the ID the VM had before the failure event.
									down_time = CloudSim.clock() - DownTimeTable.getFailTimeTable(failVMs.get(i));
									System.out.println("VM #" +failVMs.get(i).getId()+" has been recreated on Host "+recoveringHost.getId());
									for(int j=0; j<recoveringCloudlet.size(); j++){								
										double cletUtilization;
										cletUtilization = cletNorm.getNormalizedLength(recoveringCloudlet.get(j));
										recoveringCloudlet.get(j).setVmId(failVMs.get(i).getId());								
										failVMs.get(i).getCloudletScheduler().cloudletSubmit(recoveringCloudlet.get(j));
										if(recoveringCloudlet.get(j).getCloudletId() == 2036 && failVMs.get(i).getId()==619 ){
											System.out.println("Check");
										}
										System.out.println("Cloudlet #" +recoveringCloudlet.get(j).getCloudletId()+ " has been recreated on VM " +failVMs.get(i).getId());								
										System.out.println("Initial submission time of recovering cloudlet is " +broker.getcloudletInitialStartTime(recoveringCloudlet.get(j)));										
										ReliabilityCalculator(recoveringHost, failVMs.get(i), cletUtilization);
									}
									getVmList().add(failVMs.get(i));							
									failVMs.get(i).updateVmProcessing(CloudSim.clock(), getVmAllocationPolicy().getHost(failVMs.get(i)).getVmScheduler().getAllocatedMipsForVm(failVMs.get(i)));
									DownTimeTable.setDownTimeTable(failVMs.get(i), down_time);					
								}						
								hostVMPeMap.remove(recoveringHost.getId());
							}
						} 
			
					}
					else{			
						System.out.println(CloudSim.clock()+ " Host " +recoveringHost.getId()+  " is already up ");
			
					}			
			}
			else{		
				updateCloudletProcessing();
				Host recoveringHost;			
				ArrayList<Cloudlet> recoveringCloudlet;		
				int hostCount;									
				recoveringHost = hostList().get(failhostTable.get(node));			
				if(recoveringHost.isFailed()){					
					recoveringHost.setFailed(false);				
					if(recoveringHost.getDatacenter()==null){
						recoveringHost.setDatacenter(this);
					}		
					for(int i=0;i<failedHosts.size();i++){
						if(failedHosts.get(i)==node){
							failedHosts.remove(i);
						}
					}					
					if(hostvmTable.containsKey(recoveringHost)){			
						failVMs = new ArrayList<Vm>();
						failVMsID = new ArrayList<Integer>();				
						failVMs=hostvmTable.get(recoveringHost); // Retrieving failed virtual machines
						failVMsID = hostvmIdTable.get(recoveringHost); // Retrieving failed virtual machines IDs					
						if(failVMs != null){
							IdleTime.setHostActiveStatus(recoveringHost.getId(), true);															
							hostCount = IdleTime.getActiveHostCount();									
							HostCounter.setHostCount(hostCount);
							HostCounter.setClockList(CloudSim.clock());								
							int size=failVMs.size();							
							int sizeCletList;
							vmPeMap = new HashMap<Integer, Integer>();
							vmPeMap = hostVMPeMap.get(recoveringHost.getId());						
							peDownTimeMap = new HashMap<Integer, Double>();
							peDownTimeMap = hostPeDownTimeMap.get(recoveringHost.getId());							
							Set<Integer>peKeySet = new HashSet<Integer>();
							peKeySet = peDownTimeMap.keySet();
							ArrayList<Integer>peKeySetList = new ArrayList<Integer>(peKeySet);				
							for(int i=0; i<peDownTimeMap.size(); i++){
								double downTime;				
								downTime = CloudSim.clock() - peDownTimeMap.get(peKeySetList.get(i));
								IdleTime.setHostPeDownTime(recoveringHost.getId(), peKeySetList.get(i), downTime);
							}
							hostPeDownTimeMap.remove(recoveringHost.getId());						
							for(int i=0;i<size;i++){
								sizeCletList = failedVMCletMap.get(failVMsID.get(i)).size();
								cletsTempID = new ArrayList<Integer>();
								cletsTempID = failedVMCletMap.get(failVMsID.get(i));									
								failedVMCletMap.remove(failVMsID.get(i));	
								failVMs.get(i).setBeingInstantiated(true); //This matters. If this variable will not set to true then the reallocation of resources will not happen and estimatedFinishingTime of a corresponding cloudlet will set to infinity.							
							//	if(failVMs.get(i).getId()==4){
							//		System.out.println("Check");
							//	}
								recoveringCloudlet = new ArrayList<Cloudlet>();
								double cletFailTime = 0.0;
								double cletDownTime = 0.0;															
								double down_time;
								long cletLengthTemp;
								long soFarClet;
								long oldBackupLength;			
								long currentRemaining;
								// This part of the function is saving the recovering cloudlets for the future use. Cloudlets from this list will not be removed.							
								for(int j=0; j<sizeCletList; j++){	
									if(cletsTempID.get(j)==22871){
										System.out.println("Check");
									}
									failFlagTable.put(cletsTempID.get(j), true);
									cletFailTime = DownTimeTable.getCletFailTimeTable(cletsTempID.get(j));
									cletDownTime = CloudSim.clock() - cletFailTime;			
									recoveringCloudlet.add(broker.getidtoCloudletLst().get(cletsTempID.get(j)));																			
									if(!cletLengthBackup.containsKey(cletsTempID.get(j))){
										cletLengthBackup.put(cletsTempID.get(j), recoveringCloudlet.get(j).getCloudletLength());
									}
									if(!checkpointsForCloudlets.containsKey(cletsTempID.get(j))){
										soFarClet = CloudletStatus.getsofarCloudlet(cletsTempID.get(j));										
										cletLengthTemp = recoveringCloudlet.get(j).getCloudletLength() + ((long)cletDownTime*1000);
										oldBackupLength = cletLengthBackup.get(cletsTempID.get(j));
										oldBackupLength = oldBackupLength + ((long)cletDownTime*1000);
										cletLengthBackup.put(cletsTempID.get(j), oldBackupLength);
										failureOccurredBeforeChkpt.put(cletsTempID.get(j), true);
									}
									else{											
										cletLengthTemp = cletLengthBackup.get(cletsTempID.get(j)) - checkpointsForCloudlets.get(cletsTempID.get(j));
										currentRemaining = remainingCletLengthTable.get(cletsTempID.get(j));
										soFarClet = cletLengthTemp - currentRemaining;										
										cletLengthTemp = cletLengthTemp + ((long)cletDownTime*1000);
										oldBackupLength = cletLengthBackup.get(cletsTempID.get(j));
										oldBackupLength = oldBackupLength + (long)cletDownTime*1000;
										cletLengthBackup.put(cletsTempID.get(j), oldBackupLength);
										if(failureOccurredBeforeChkpt.containsKey(cletsTempID.get(j))){
											failureOccurredBeforeChkpt.remove(cletsTempID.get(j));
										}
										if(backtobackConsolidationsFlag.containsKey(cletsTempID.get(j))){
											backtobackConsolidationsFlag.remove(cletsTempID.get(j));
										}
									}
									recoveringCloudlet.get(j).setCloudletLength(cletLengthTemp);
									cletLastRemainingMap.put(cletsTempID.get(j), cletLengthTemp);
									DownTimeTable.setCloudletDownTime(cletsTempID.get(j), cletDownTime);
									CloudletReexecutionPart.setCletReexecutionTime(cletsTempID.get(j), soFarClet);	
									DownTimeTable.setCletPerHostDownTimeTable(cletsTempID.get(j), recoveringHost.getId(), cletDownTime);
									CloudletReexecutionPart.setCletPerHostReexecutionTimeTable(cletsTempID.get(j), recoveringHost.getId(), soFarClet);									
								}
								recoveringHost.vmCreate(failVMs.get(i)); //Creating a virtual machine on the host with the same name that the VM had before the failure event.
					
								List<Pe> peList = new ArrayList<Pe>();
								peList = recoveringHost.getVmScheduler().getPesAllocatedForVM(failVMs.get(i));
								for(int x=0; x<peList.size(); x++){
									int peID = peList.get(x).getId();
									int previousPeID = vmPeMap.get(failVMs.get(i).getId());										
									if(peID != previousPeID){
										IdleTime.setHostPeIdleTable(recoveringHost.getId(), previousPeID, true);									
										IdleTime.setHostPeIdleClockTable(recoveringHost.getId(), previousPeID, CloudSim.clock());									
										IdleTime.setHostPeIdleTable(recoveringHost.getId(), peID, false);											
										if(IdleTime.checkHostPeIdleClockTable(recoveringHost.getId(), peID)){
											IdleTime.setHostPeIdleTimeTable(recoveringHost.getId(), peID, CloudSim.clock());
											IdleTime.removeHostPeIdleClockTable(recoveringHost.getId(),peID);									
										}
									}
								}																									
								failVMs.get(i).setId(failVMsID.get(i)); // Setting the ID of the new virtual machine equal to the ID the VM had before the failure event.
								down_time = CloudSim.clock() - DownTimeTable.getFailTimeTable(failVMs.get(i));								
								for(int j=0; j<recoveringCloudlet.size(); j++){								
									double cletUtilization;
									cletUtilization = cletNorm.getNormalizedLength(recoveringCloudlet.get(j));
									recoveringCloudlet.get(j).setVmId(failVMs.get(i).getId());								
									failVMs.get(i).getCloudletScheduler().cloudletSubmit(recoveringCloudlet.get(j));
									ReliabilityCalculator(recoveringHost, failVMs.get(i), cletUtilization);
								}
								getVmList().add(failVMs.get(i));							
								failVMs.get(i).updateVmProcessing(CloudSim.clock(), getVmAllocationPolicy().getHost(failVMs.get(i)).getVmScheduler().getAllocatedMipsForVm(failVMs.get(i)));
								DownTimeTable.setDownTimeTable(failVMs.get(i), down_time);					
							}						
							hostVMPeMap.remove(recoveringHost.getId());
						}
					} 
		
				}						
			}	
		}	
		
		
		public HashMap<Integer, Long>cletSoFarTempMap = new HashMap<Integer, Long>();
		//public HashMap<Integer, Long>cletSoFarDuringFailure = new HashMap<Integer, Long>();
		public HashMap<Integer, Boolean>failureOccurredBeforeChkpt = new HashMap<Integer, Boolean>();
		public HashMap<Integer, Boolean>cletMapforConsolidationTesting = new HashMap<Integer, Boolean>();
		public void processFailureWithMigrationCheckpointing(int node){
			updateCloudletProcessing();
			if(RunTimeConstants.test == true){	
			//	if(firstFailureFlag.get(node)== true){
			//		System.out.println("This is the first failure and will be ignored inorder to synchronize with the checkpointing functions");
			//		firstFailureFlag.put(node, false);					
			//	}
			//	else{					
					boolean flag_fail = false;
					int rand=0;
					Vm vm;
					int hostCount;
					//FailureVm vm;
					//String usr;
					flag_record = true;   //This flag is being used in DatacenterBroker to record only the activities that happened after the occurrence of a failure. 
			
					if(lock_sufferedCletList == false){
						broker.setSufferedCloudletsList(); //This call is to copy all the cloudlets that are under processing while occurrence of first failure. 
						lock_sufferedCletList = true;
					}
				
					for(int j=0;j<hostList().size();j++){
						if(failhostTable.containsKey(node)){
							rand=failhostTable.get(node);
							break;
						}				
					}
			
					fail = hostList().get(rand); // Node that is set to be failed
			
					if(hostList().get(rand).isFailed()){					
						System.out.println( "Host " +node+ " has already been failed");	
						flag_fail=true;
					}	
		
					if(flag_fail==false){
						// If getCloudletReceivedList() is not empty then it means that the cloudlets have been processed without any failure. 
						// By checking the contents, it has been ensured that a failure will not occur after the completion of execution.
						// finishedcletList=Dbr.getCloudletReceivedList();				
						vmPeMap = new HashMap<Integer, Integer>();
						peDownTimeMap = new HashMap<Integer, Double>();
						System.out.println("Size of the cloudlet received list is " +broker.getCloudletReceivedList().size());
						//System.out.println("Size of cloudlet list is " +Dbr.getCloudletSubmittedList().size());
						if(broker.getCloudletReceivedList().size()<broker.getCloudletSubmittedList().size()){
							Log.printLine("Failure has occurred at " +CloudSim.clock()+ " for host " +node);					
							System.out.println("Size of host list is " +hostList().size());		
							
							// A seperate list for storing the node information is required because when a failure will occur, then the 
							// host from the list ""Nodes" will be removed rather than removing the original hostList(). When the node 
							// will be recovered then the node in hostList() will simply set be Recovered status. This will decrease
							// the work of re-provisioning and reallocation of the nodes.	
					
							System.out.println("Id of the Failing Host in the provisioned nodes list is  #" +fail.getId());	// ID of the failing host							
							//System.out.println("FTA ID of the failing Host is #" +failNodes.get(fail.getId()));			
							
							/**
							 * This part is to turn off the idle physical machine inorder to save idle energy consumption
							 */						
							
							if((RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 3 && RunTimeConstants.resourceTurnOffCase == true) || (RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4 && RunTimeConstants.resourceTurnOffCase == true)){										
								if((IdleTime.expectedFailingHost.isEmpty()==false) && IdleTime.checkExpectedFailingHost(fail.getId())==true){
									System.out.println("Resource " +fail.getId()+ " getting turned off");
									IdleTime.setExpectedFailingHost(fail.getId(), false);
									List<Pe>peList = new ArrayList<Pe>();								
									boolean idleStatus;
									boolean clockStatus;								
									peList = fail.getPeList();
									for(int i=0; i<peList.size(); i++){		
										idleStatus = IdleTime.getHostPeIdleTable(fail.getId(), peList.get(i).getId());
										clockStatus = IdleTime.checkHostPeIdleClockTable(fail.getId(), peList.get(i).getId());
										if(idleStatus==true && clockStatus == true){					
											IdleTime.setHostPeIdleTimeTable(fail.getId(), peList.get(i).getId(), CloudSim.clock());
											IdleTime.removeHostPeIdleClockTable(fail.getId(), peList.get(i).getId());
										}
									}									
									IdleTime.setHostActiveStatus(fail.getId(), false);															
									hostCount = IdleTime.getActiveHostCount();									
									HostCounter.setHostCount(hostCount);
									HostCounter.setClockList(CloudSim.clock());	
								}
							}
							
							//hostList().get(rand).getVmList().size gives the number of virtual machines running on the failing host.					
							if(fail.getVmList().size()==0){								
								System.out.println("No virtual machine and cloudlets are running on the failing node");								
								System.out.println("Setting host to the state Fail");								
								hostvmTable.put(fail, null);								
								hostvmIdTable.put(fail, null);								
								fail.setFailed(true);								
								failedHosts.add(node); // Maintaining the list of failed hosts.						
							}
							else{								
								totalFailureCount = totalFailureCount + 1;									
								failVMs = new ArrayList<Vm>();								
								failVMsID = new ArrayList<Integer>();								
								System.out.println("Number of VMs running on failed host are " +hostList().get(rand).getVmList().size());					
								System.out.println("Virtual Machines running on the failed host are " +hostList().get(rand).getVmList());								
								List<Pe>peList = new ArrayList<Pe>();
								for(int i=0;i<fail.getVmList().size();i++){									
									vm = fail.getVmList().get(i); //Getting a VM at an index i.										
									peList = fail.getVmScheduler().getPesAllocatedForVM(vm);									
									for(int x=0; x<peList.size(); x++){									
										System.out.println("PE Id for VM " +vm.getId()+ " is "+peList.get(x).getId());
										vmPeMap.put(vm.getId(), peList.get(x).getId());
										peDownTimeMap.put(peList.get(x).getId(), CloudSim.clock());
									}								
									System.out.println("Virtual Machine ID running on the failing host is " +vm.getId()); // Id of the VM at an index vm.																					 
									failVMs.add(fail.getVmList().get(i)); //List of the failing virtual machines			
									failVMsID.add(fail.getVmList().get(i).getId()); //List of the IDs of failing virtual machines										
									DownTimeTable.setFailTimeTable(vm, CloudSim.clock()); // Saving the failing time of a virtual machine										
								}								
								hostvmTable.put(fail, failVMs); // Table of failed hosts and their corresponding failed virtual machines									
								hostvmIdTable.put(fail, failVMsID); // Table of failed hosts and their corresponding failed virtual machine IDs					
								usrclList.putAll(broker.getmaplistUsertocloudlet()); // This table is storing Cloudlet IDs and corresponding User IDs					
								hostVMPeMap.put(fail.getId(), vmPeMap);										
								hostPeDownTimeMap.put(fail.getId(), peDownTimeMap);										
								for(int i=0;i<failVMsID.size();i++){			
									int size; 
									cletsTempID = new ArrayList<Integer>();										
									size = ((FailureCloudletSchedulerTimeShared)failVMs.get(i).getCloudletScheduler()).getCloudletExecList().size();
									for(int j=0;j<size;j++){
										cletsTempID.add(((FailureCloudletSchedulerTimeShared)failVMs.get(i).getCloudletScheduler()).getCloudletExecList().get(j).getCloudletId());
										if(cletsTempID.get(j)==3 && checkCount > 120868){
											System.out.println("Check");
										}										
										//cletMapforConsolidationTesting.put(cletsTempID.get(j), true);
									}											
									failedVMCletMap.put(failVMsID.get(i), cletsTempID);
									System.out.println("IDs of Cloudlets running on the failing virtual machine are " );											
									for(int j=0;j<cletsTempID.size();j++){												
										System.out.println(+cletsTempID.get(j));										
									}								
									if(RunTimeConstants.faultToleranceMechanism == 4 || RunTimeConstants.faultToleranceMechanism == 1){							
									for(int j=0;j<cletsTempID.size();j++){												
										long tempSoFar;
										long tempRemaining;
										long tempLength;
									//	if(cletLastRemainingMap.containsKey(cletsTempID.get(j))&& cletMigrated.containsKey(cletsTempID.get(j))){			
										if(cletLastRemainingMap.containsKey(cletsTempID.get(j))){		
											if(cletSoFarBeforeFailing.containsKey(cletsTempID.get(j))){
												tempRemaining = ((FailureCloudletSchedulerTimeShared)failVMs.get(i).getCloudletScheduler()).getCloudletExecList().get(j).getRemainingCloudletLength();
												remainingCletLengthTable.put(cletsTempID.get(j), tempRemaining);
												System.out.println("Temporary remaining length of cloudlet " + cletsTempID.get(j)+ " is " +tempRemaining);
												tempLength = ((FailureCloudletSchedulerTimeShared)failVMs.get(i).getCloudletScheduler()).getCloudletExecList().get(j).getCloudletLength();
												System.out.println("Temporary length of cloudlet " + cletsTempID.get(j)+ " is " +tempLength);
												tempSoFar = tempLength - tempRemaining;
											//	cletSoFarDuringFailure.put(cletsTempID.get(j), tempSoFar);
											//	cletSoFarTempMap.put(cletsTempID.get(j), tempSoFar);
												System.out.println("So far execution length of cloudlet " + cletsTempID.get(j)+ " after the resubmission because of failure is " +tempSoFar);															
												tempSoFar = tempSoFar + cletSoFarBeforeFailing.get(cletsTempID.get(j));
												System.out.println("So far execution length of cloudlet " + cletsTempID.get(j)+ " after the resubmission and since the last migration is " +tempSoFar);
												cletSoFarBeforeFailing.put(cletsTempID.get(j), tempSoFar);
											//	cletLastRemainingMap.put(cletsTempID.get(j), tempRemaining);
												
											}
											else{
												tempRemaining = ((FailureCloudletSchedulerTimeShared)failVMs.get(i).getCloudletScheduler()).getCloudletExecList().get(j).getRemainingCloudletLength();
												remainingCletLengthTable.put(cletsTempID.get(j), tempRemaining);
												System.out.println("Temporary remaining length of cloudlet " + cletsTempID.get(j)+ " is " +tempRemaining);
												tempLength = ((FailureCloudletSchedulerTimeShared)failVMs.get(i).getCloudletScheduler()).getCloudletExecList().get(j).getCloudletLength();
												System.out.println("Temporary length of cloudlet " + cletsTempID.get(j)+ " is " +tempLength);
												tempSoFar = cletLastRemainingMap.get(cletsTempID.get(j))-tempRemaining;	
											//	cletSoFarDuringFailure.put(cletsTempID.get(j), tempSoFar);
												//tempSoFar = tempLength - tempRemaining;
												//cletSoFarTempMap.put(cletsTempID.get(j), tempSoFar);
												System.out.println("So far execution length of cloudlet " + cletsTempID.get(j)+ " since last migration is and before the first failure is " +tempSoFar);
												cletSoFarBeforeFailing.put(cletsTempID.get(j), tempSoFar);
											//	cletLastRemainingMap.put(cletsTempID.get(j), tempRemaining);
											}
										}												
									}
								}											
								for(int j=0;j<cletsTempID.size();j++){				
									if(previousChkptSoFarTable.containsKey(cletsTempID.get(j))){
										previousChkptSoFarTable.remove(cletsTempID.get(j));
									}
									if(checkpointsForCloudlets.containsKey(cletsTempID.get(j))){
										failureHappenedForClet.put(cletsTempID.get(j), true);
									}
									DownTimeTable.setCletFailTimeTable(cletsTempID.get(j), CloudSim.clock());
									processCloudletCancel(cletsTempID.get(j), usrclList.get(cletsTempID.get(j)), failVMsID.get(i));											
									usrclList.remove(cletsTempID.get(j));
								}										 
								for(int k=0;k<getVmList().size();k++){
									if(getVmList().get(k)==failVMs.get(i)){									 				
										getVmList().remove(k);
										break;
									}
								}										 
							}						
							System.out.println("Destroying all the virtual machines running on the failing host");	
							for(int i=0; i<failVMs.size(); i++){										
								hostList().get(rand).vmDestroy(failVMs.get(i));											
							}																	
							System.out.println("Setting host to the state Fail");								
							failedHosts.add(node);										
							hostList().get(rand).setFailed(true);	
							
							IdleTime.setHostActiveStatus(fail.getId(), false);															
							hostCount = IdleTime.getActiveHostCount();									
							HostCounter.setHostCount(hostCount);
							HostCounter.setClockList(CloudSim.clock());	
							}		
						}						
						else{
							System.out.println("Processing of all the cloudlets has been finished without a failure by "+CloudSim.clock());
						}
					}
				//}
			}
			else{ //This else is for non-testing simulation				
				boolean flag_fail = false;
				int rand=0;
				Vm vm;
				int hostCount;				
				flag_record = true;   //This flag is being used in DatacenterBroker to record only the activities that happened after the occurrence of a failure.		
				if(lock_sufferedCletList == false){
					broker.setSufferedCloudletsList(); //This call is to copy all the cloudlets that are under processing while occurrence of first failure. 
					lock_sufferedCletList = true;
				}			
				for(int j=0;j<hostList().size();j++){
					if(failhostTable.containsKey(node)){
						rand=failhostTable.get(node);
						break;
					}				
				}		
				fail = hostList().get(rand); // Node that is set to be failed		
				if(hostList().get(rand).isFailed()){					
					flag_fail=true;
				}		
				if(flag_fail==false){								
					vmPeMap = new HashMap<Integer, Integer>();
					peDownTimeMap = new HashMap<Integer, Double>();					
					if(broker.getCloudletReceivedList().size()<broker.getCloudletSubmittedList().size()){						
						if((RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 3 && RunTimeConstants.resourceTurnOffCase == true) || (RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4 && RunTimeConstants.resourceTurnOffCase == true)){										
							if((IdleTime.expectedFailingHost.isEmpty()==false) && IdleTime.checkExpectedFailingHost(fail.getId())==true){								
								IdleTime.setExpectedFailingHost(fail.getId(), false);
								List<Pe>peList = new ArrayList<Pe>();								
								boolean idleStatus;
								boolean clockStatus;								
								peList = fail.getPeList();
								for(int i=0; i<peList.size(); i++){		
									idleStatus = IdleTime.getHostPeIdleTable(fail.getId(), peList.get(i).getId());
									clockStatus = IdleTime.checkHostPeIdleClockTable(fail.getId(), peList.get(i).getId());
									if(idleStatus==true && clockStatus == true){					
										IdleTime.setHostPeIdleTimeTable(fail.getId(), peList.get(i).getId(), CloudSim.clock());
										IdleTime.removeHostPeIdleClockTable(fail.getId(), peList.get(i).getId());
									}
								}									
								IdleTime.setHostActiveStatus(fail.getId(), false);															
								hostCount = IdleTime.getActiveHostCount();									
								HostCounter.setHostCount(hostCount);
								HostCounter.setClockList(CloudSim.clock());	
							}
						}										
						if(fail.getVmList().size()==0){														
							hostvmTable.put(fail, null);								
							hostvmIdTable.put(fail, null);								
							fail.setFailed(true);								
							failedHosts.add(node); // Maintaining the list of failed hosts.						
						}
						else{								
							totalFailureCount = totalFailureCount + 1;									
							failVMs = new ArrayList<Vm>();								
							failVMsID = new ArrayList<Integer>();														
							List<Pe>peList = new ArrayList<Pe>();
							for(int i=0;i<fail.getVmList().size();i++){									
								vm = fail.getVmList().get(i); //Getting a VM at an index i.										
								peList = fail.getVmScheduler().getPesAllocatedForVM(vm);									
								for(int x=0; x<peList.size(); x++){									
									vmPeMap.put(vm.getId(), peList.get(x).getId());
									peDownTimeMap.put(peList.get(x).getId(), CloudSim.clock());
								}																												 
								failVMs.add(fail.getVmList().get(i)); //List of the failing virtual machines			
								failVMsID.add(fail.getVmList().get(i).getId()); //List of the IDs of failing virtual machines										
								DownTimeTable.setFailTimeTable(vm, CloudSim.clock()); // Saving the failing time of a virtual machine										
							}								
							hostvmTable.put(fail, failVMs); // Table of failed hosts and their corresponding failed virtual machines									
							hostvmIdTable.put(fail, failVMsID); // Table of failed hosts and their corresponding failed virtual machine IDs					
							usrclList.putAll(broker.getmaplistUsertocloudlet()); // This table is storing Cloudlet IDs and corresponding User IDs					
							hostVMPeMap.put(fail.getId(), vmPeMap);										
							hostPeDownTimeMap.put(fail.getId(), peDownTimeMap);										
							for(int i=0;i<failVMsID.size();i++){			
								int size; 
								cletsTempID = new ArrayList<Integer>();										
								size = ((FailureCloudletSchedulerTimeShared)failVMs.get(i).getCloudletScheduler()).getCloudletExecList().size();
								for(int j=0;j<size;j++){
									cletsTempID.add(((FailureCloudletSchedulerTimeShared)failVMs.get(i).getCloudletScheduler()).getCloudletExecList().get(j).getCloudletId());									
								}											
								failedVMCletMap.put(failVMsID.get(i), cletsTempID);																
								if(RunTimeConstants.faultToleranceMechanism == 4 || RunTimeConstants.faultToleranceMechanism == 1){							
								for(int j=0;j<cletsTempID.size();j++){												
									long tempSoFar;
									long tempRemaining;
									long tempLength;					
									if(cletLastRemainingMap.containsKey(cletsTempID.get(j))){		
										if(cletSoFarBeforeFailing.containsKey(cletsTempID.get(j))){
											tempRemaining = ((FailureCloudletSchedulerTimeShared)failVMs.get(i).getCloudletScheduler()).getCloudletExecList().get(j).getRemainingCloudletLength();
											remainingCletLengthTable.put(cletsTempID.get(j), tempRemaining);											
											tempLength = ((FailureCloudletSchedulerTimeShared)failVMs.get(i).getCloudletScheduler()).getCloudletExecList().get(j).getCloudletLength();											
											tempSoFar = tempLength - tempRemaining;										
											tempSoFar = tempSoFar + cletSoFarBeforeFailing.get(cletsTempID.get(j));
											cletSoFarBeforeFailing.put(cletsTempID.get(j), tempSoFar);									
										}
										else{
											tempRemaining = ((FailureCloudletSchedulerTimeShared)failVMs.get(i).getCloudletScheduler()).getCloudletExecList().get(j).getRemainingCloudletLength();
											remainingCletLengthTable.put(cletsTempID.get(j), tempRemaining);
											tempLength = ((FailureCloudletSchedulerTimeShared)failVMs.get(i).getCloudletScheduler()).getCloudletExecList().get(j).getCloudletLength();
											tempSoFar = cletLastRemainingMap.get(cletsTempID.get(j))-tempRemaining;	
											cletSoFarBeforeFailing.put(cletsTempID.get(j), tempSoFar);										
										}
									}												
								}
							}											
							for(int j=0;j<cletsTempID.size();j++){				
								if(previousChkptSoFarTable.containsKey(cletsTempID.get(j))){
									previousChkptSoFarTable.remove(cletsTempID.get(j));
								}
								if(checkpointsForCloudlets.containsKey(cletsTempID.get(j))){
									failureHappenedForClet.put(cletsTempID.get(j), true);
								}
								DownTimeTable.setCletFailTimeTable(cletsTempID.get(j), CloudSim.clock());
								processCloudletCancel(cletsTempID.get(j), usrclList.get(cletsTempID.get(j)), failVMsID.get(i));											
								usrclList.remove(cletsTempID.get(j));
							}										 
							for(int k=0;k<getVmList().size();k++){
								if(getVmList().get(k)==failVMs.get(i)){									 				
									getVmList().remove(k);
									break;
								}
							}										 
						}			
						for(int i=0; i<failVMs.size(); i++){										
							hostList().get(rand).vmDestroy(failVMs.get(i));											
						}													
						failedHosts.add(node);										
						hostList().get(rand).setFailed(true);						
						IdleTime.setHostActiveStatus(fail.getId(), false);															
						hostCount = IdleTime.getActiveHostCount();									
						HostCounter.setHostCount(hostCount);
						HostCounter.setClockList(CloudSim.clock());	
						}		
					}			
				}			
			}					
		}
		
		
		public void processRecoveryWithMigrationCheckpointing(int node){
			if(RunTimeConstants.test == true)
			{		
			//	if(firstRecoveryFlag.get(node)==true){
			//		System.out.println("This event is for first recovery which needs to be ignored");
			//		firstRecoveryFlag.put(node, false);
			//	}
		//		else{					
					updateCloudletProcessing();		
					Host recoveringHost;
					int hostCount;
					ArrayList<Cloudlet> recoveringCloudlet; 			
					recoveringHost = hostList().get(failhostTable.get(node));			
					if(recoveringHost.isFailed()){
						System.out.println("This recovery was scheduled at " +CloudSim.clock());					
						System.out.println("Host " +recoveringHost+ " with ID " +recoveringHost.getId()+ "  has been recovered from a failure");
						recoveringHost.setFailed(false);
						if(recoveringHost.getDatacenter()==null){
							recoveringHost.setDatacenter(this);
						}				
			
						// Generally, if a node is failed then it will not consume any energy. This module is only for the case
						// when the node is sitting idle and has been marked as an expected failing host due to the prediction results. 
						// After the occurrence of failure this node will be available and sitting idle and waiting 
						// to get allocated to VMs.					
						
						if(RunTimeConstants.predictionFlag == true && (RunTimeConstants.faultToleranceMechanism == 3 || RunTimeConstants.faultToleranceMechanism == 4) && RunTimeConstants.resourceTurnOffCase == false){
							if((IdleTime.expectedFailingHost.isEmpty()==false) && IdleTime.checkExpectedFailingHost(recoveringHost.getId())==true){
								IdleTime.setExpectedFailingHost(recoveringHost.getId(), false);					
								for(int i=0; i<recoveringHost.getNumberOfPes(); i++){
									int peID;
									peID = recoveringHost.getPeList().get(i).getId();
									boolean idleStatus;
									idleStatus = IdleTime.getHostPeIdleTable(recoveringHost.getId(), peID);
									if(idleStatus == true){
										IdleTime.setHostPeIdleTable(recoveringHost.getId(), peID, false);
										IdleTime.setHostPeIdleTimeTable(recoveringHost.getId(), peID, CloudSim.clock());						
									}							
								}					
							}				
						}				
						for(int i=0;i<failedHosts.size();i++){
							if(failedHosts.get(i)==node){
								failedHosts.remove(i);
							}
						}		
						if(hostvmTable.containsKey(recoveringHost)){			
							failVMs = new ArrayList<Vm>();
							failVMsID = new ArrayList<Integer>();						
							failVMs=hostvmTable.get(recoveringHost);
							failVMsID = hostvmIdTable.get(recoveringHost);				
							if(failVMs == null){
								System.out.println("No virtual machines were running on the recovered host");								
							}
							else{
								
								IdleTime.setHostActiveStatus(recoveringHost.getId(), true);															
								hostCount = IdleTime.getActiveHostCount();									
								HostCounter.setHostCount(hostCount);
								HostCounter.setClockList(CloudSim.clock());	
								
								int size=failVMs.size();
								System.out.println("Number of VMs were running on host " +recoveringHost+ " of ID "+ recoveringHost.getId()+ " are " +size);
								int sizeCletList;
								vmPeMap = new HashMap<Integer, Integer>();
								vmPeMap = hostVMPeMap.get(recoveringHost.getId());				
								peDownTimeMap = new HashMap<Integer, Double>();
								peDownTimeMap = hostPeDownTimeMap.get(recoveringHost.getId());
								Set<Integer>peKeySet = new HashSet<Integer>();
								peKeySet = peDownTimeMap.keySet();
								ArrayList<Integer>peKeySetList = new ArrayList<Integer>(peKeySet);				
								for(int i=0; i<peDownTimeMap.size(); i++){
									double downTime;				
									downTime = CloudSim.clock() - peDownTimeMap.get(peKeySetList.get(i));
									IdleTime.setHostPeDownTime(recoveringHost.getId(), peKeySetList.get(i), downTime);
								}
								hostPeDownTimeMap.remove(recoveringHost.getId());				
								for(int i=0;i<size;i++){
									sizeCletList = failedVMCletMap.get(failVMsID.get(i)).size();
									cletsTempID = new ArrayList<Integer>();			
									cletsTempID = failedVMCletMap.get(failVMsID.get(i));								
									failedVMCletMap.remove(failVMsID.get(i));
									failVMs.get(i).setBeingInstantiated(true); //This matters. If this variable will not set to true then the reallocation of resources will not happen and estimatedFinishingTime of a corresponding cloudlet will set to infinity.			
									recoveringCloudlet = new ArrayList<Cloudlet>();
									double cletFailTime = 0.0;
									double cletDownTime = 0.0;
									long cletLengthTemp;
									long currentRemaining;
									//long cletLengthTemp1;
									long soFarClet;								
									//long remainingTemp;
									long oldBackupLength;
								//	double clock;
									for(int j=0;j<sizeCletList;j++){		
										if(cletsTempID.get(j)==3 && checkCount > 120868){
											System.out.println("Check");
										}										
										failFlagTable.put(cletsTempID.get(j), true);
										cletFailTime = DownTimeTable.getCletFailTimeTable(cletsTempID.get(j));
										cletDownTime = CloudSim.clock() - cletFailTime;										
										recoveringCloudlet.add(broker.getidtoCloudletLst().get(cletsTempID.get(j)));										
										//cletLengthTemp = recoveringCloudlet.get(j).getCloudletLength();
										if(!cletLengthBackup.containsKey(cletsTempID.get(j))){
											cletLengthBackup.put(cletsTempID.get(j), recoveringCloudlet.get(j).getCloudletLength());
										}
										//soFarClet = CloudletStatus.getsofarCloudlet(recoveringCloudlet.get(j).getCloudletId());	
										if(!checkpointsForCloudlets.containsKey(cletsTempID.get(j))){			
										//	if(cletsTempID.get(j)==328){
										//		System.out.println("Check");
										//	}
											soFarClet = CloudletStatus.getsofarCloudlet(cletsTempID.get(j));																		
											//cletLengthTemp = recoveringCloudlet.get(j).getCloudletLength() + soFarClet + ((long)(cletDownTime*1000));
											cletLengthTemp = recoveringCloudlet.get(j).getCloudletLength() + ((long)(cletDownTime*1000));
											oldBackupLength = cletLengthBackup.get(recoveringCloudlet.get(j).getCloudletId());
											oldBackupLength = oldBackupLength + ((long)(cletDownTime*1000));
											cletLengthBackup.put(cletsTempID.get(j), oldBackupLength);
											failureOccurredBeforeChkpt.put(cletsTempID.get(j), true);
										}
										else{
											cletLengthTemp = cletLengthBackup.get(cletsTempID.get(j)) - checkpointsForCloudlets.get(cletsTempID.get(j));
											currentRemaining = remainingCletLengthTable.get(cletsTempID.get(j));
											soFarClet = cletLengthTemp - currentRemaining;											
											//soFarClet = cletSoFarBeforeFailing.get(cletsTempID.get(j));
											//soFarClet = cletSoFarDuringFailure.get(cletsTempID.get(j));											
											//cletSoFarDuringFailure.remove(cletsTempID.get(j));
											//soFarClet = cletSoFarTempMap.get(cletsTempID.get(j));
										//	clock = CloudSim.clock();
											//cletLengthTemp1 = cletLastRemainingMap.get(cletsTempID.get(j))+cletSoFarBeforeFailing.get(cletsTempID.get(j))+((long)(cletDownTime*1000)) ;											
											//cletLengthTemp = cletLengthTemp + soFarClet + (long)(cletDownTime*1000);
											cletLengthTemp = cletLengthTemp + (long)(cletDownTime*1000);
											//cletLengthTemp = recoveringCloudlet.get(j).getCloudletLength() + soFarClet + (long)(cletDownTime*1000);
										//	cletLengthTemp = recoveringCloudlet.get(j).getCloudletLength() +cletSoFarBeforeFailing.get(cletsTempID.get(j))+((long)(cletDownTime*1000)) ;
											
											oldBackupLength = cletLengthBackup.get(recoveringCloudlet.get(j).getCloudletId());
											//oldBackupLength = oldBackupLength + soFarClet + (long)(cletDownTime*1000);
											oldBackupLength = oldBackupLength + (long)(cletDownTime*1000);
											cletLengthBackup.put(recoveringCloudlet.get(j).getCloudletId(), oldBackupLength);
											if(failureOccurredBeforeChkpt.containsKey(cletsTempID.get(j))){
												failureOccurredBeforeChkpt.remove(cletsTempID.get(j));
											}
										}
										recoveringCloudlet.get(j).setCloudletLength(cletLengthTemp);										 
										System.out.println("New Length of cloudlet " +cletsTempID.get(j)+" after the recovery from a failure is " +cletLengthTemp);
										System.out.println("Failure downtime overhead added to the length of cloudlet "+cletsTempID.get(j)+" is "+cletDownTime);
										System.out.println("Failure re-execution overhead added to the length of cloudlet "+cletsTempID.get(j)+" is "+soFarClet);																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																	
										DownTimeTable.setCloudletDownTime(cletsTempID.get(j), cletDownTime);
										CloudletReexecutionPart.setCletReexecutionTime(cletsTempID.get(j), soFarClet);
										DownTimeTable.setCletPerHostDownTimeTable(cletsTempID.get(j), recoveringHost.getId(), cletDownTime);
										CloudletReexecutionPart.setCletPerHostReexecutionTimeTable(cletsTempID.get(j), recoveringHost.getId(), soFarClet);
										cletLastRemainingMap.put(cletsTempID.get(j), cletLengthTemp);
										// This part of the function is saving the recovering cloudlets for the future use. Cloudlets from this list will not be removed.
										if(recoveringCloudletList.isEmpty()){
											recoveringCloudletList.add(recoveringCloudlet.get(j));								
											// This part of the function searches the failhostTable by using object value to get the corresponding key which is fta node ID. 
											// Both recovering cloudlet and the host on which the cloudlet is executing are required to calculate the average finishing time of the cloudlet in the presence of failures.
										}
										else{
											for(int x=0;x<recoveringCloudletList.size();x++){
												if(recoveringCloudletList.get(x)==recoveringCloudlet.get(j)){
													lock=true;
													break;										
												}
												if(lock==false){
													recoveringCloudletList.add(recoveringCloudlet.get(j));										
												}									
											}
										}
									}
									recoveringHost.vmCreate(failVMs.get(i)); //Creating a virtual machine on the host with the same name that the VM had before the failure event.
							
									List<Pe> peList = new ArrayList<Pe>();
									peList = recoveringHost.getVmScheduler().getPesAllocatedForVM(failVMs.get(i));
									for(int x=0; x<peList.size(); x++){
										int peID = peList.get(x).getId();
										int previousPeID = vmPeMap.get(failVMs.get(i).getId());										
										if(peID != previousPeID){
											IdleTime.setHostPeIdleTable(recoveringHost.getId(), previousPeID, true);									
											IdleTime.setHostPeIdleClockTable(recoveringHost.getId(), previousPeID, CloudSim.clock());									
											IdleTime.setHostPeIdleTable(recoveringHost.getId(), peID, false);											
											if(IdleTime.checkHostPeIdleClockTable(recoveringHost.getId(), peID)){
												IdleTime.setHostPeIdleTimeTable(recoveringHost.getId(), peID, CloudSim.clock());
												IdleTime.removeHostPeIdleClockTable(recoveringHost.getId(),peID);									
											}
										}
									}				
									down_time = CloudSim.clock() - DownTimeTable.getFailTimeTable(failVMs.get(i));
									failVMs.get(i).setId(failVMsID.get(i)); // Setting the ID of the new virtual machine equal to the ID the VM had before the failure event.							
									System.out.println("VM #" +failVMs.get(i).getId()+" has been recreated on Host "+recoveringHost.getId());
									for(int j=0; j<recoveringCloudlet.size(); j++){								
										double cletUtilization;
										cletUtilization = cletNorm.getNormalizedLength(recoveringCloudlet.get(j));
										recoveringCloudlet.get(j).setVmId(failVMs.get(i).getId());								
										failVMs.get(i).getCloudletScheduler().cloudletSubmit(recoveringCloudlet.get(j));
										System.out.println("Cloudlet #" +recoveringCloudlet.get(j).getCloudletId()+ " has been recreated on VM " +failVMs.get(i).getId());								
										System.out.println("Initial submission time of recovering cloudlet is " +broker.getcloudletInitialStartTime(recoveringCloudlet.get(j)));
										//remainingTemp = ((FailureCloudletSchedulerTimeShared)failVMs.get(i).getCloudletScheduler()).getCloudletExecList().get(j).getRemainingCloudletLength();
										ReliabilityCalculator(recoveringHost, failVMs.get(i), cletUtilization);
									}
									getVmList().add(failVMs.get(i));							
									failVMs.get(i).updateVmProcessing(CloudSim.clock(), getVmAllocationPolicy().getHost(failVMs.get(i)).getVmScheduler().getAllocatedMipsForVm(failVMs.get(i)));							
								//	updateCloudletProcessing();							
									DownTimeTable.setDownTimeTable(failVMs.get(i), down_time);							
								}		
								hostVMPeMap.remove(recoveringHost.getId());
							}
						}			
					}			
					else{			
						System.out.println(" Host " +recoveringHost.getId()+  " is already up ");
					}
			//	}
			}
			else{ //This else is for non-testing simulation 			
				updateCloudletProcessing();		
				Host recoveringHost;
				int hostCount;
				ArrayList<Cloudlet> recoveringCloudlet; 			
				recoveringHost = hostList().get(failhostTable.get(node));			
				if(recoveringHost.isFailed()){				
					recoveringHost.setFailed(false);
					if(recoveringHost.getDatacenter()==null){
						recoveringHost.setDatacenter(this);
					}				
					if(RunTimeConstants.predictionFlag == true && (RunTimeConstants.faultToleranceMechanism == 3 || RunTimeConstants.faultToleranceMechanism == 4) && RunTimeConstants.resourceTurnOffCase == false){
						if((IdleTime.expectedFailingHost.isEmpty()==false) && IdleTime.checkExpectedFailingHost(recoveringHost.getId())==true){
							IdleTime.setExpectedFailingHost(recoveringHost.getId(), false);					
							for(int i=0; i<recoveringHost.getNumberOfPes(); i++){
								int peID;
								peID = recoveringHost.getPeList().get(i).getId();
								boolean idleStatus;
								idleStatus = IdleTime.getHostPeIdleTable(recoveringHost.getId(), peID);
								if(idleStatus == true){
									IdleTime.setHostPeIdleTable(recoveringHost.getId(), peID, false);
									IdleTime.setHostPeIdleTimeTable(recoveringHost.getId(), peID, CloudSim.clock());						
								}							
							}					
						}				
					}				
					for(int i=0;i<failedHosts.size();i++){
						if(failedHosts.get(i)==node){
							failedHosts.remove(i);
						}
					}		
					if(hostvmTable.containsKey(recoveringHost)){			
						failVMs = new ArrayList<Vm>();
						failVMsID = new ArrayList<Integer>();						
						failVMs=hostvmTable.get(recoveringHost);
						failVMsID = hostvmIdTable.get(recoveringHost);					
						if(failVMs != null){							
							IdleTime.setHostActiveStatus(recoveringHost.getId(), true);															
							hostCount = IdleTime.getActiveHostCount();									
							HostCounter.setHostCount(hostCount);
							HostCounter.setClockList(CloudSim.clock());								
							int size=failVMs.size();							
							int sizeCletList;
							vmPeMap = new HashMap<Integer, Integer>();
							vmPeMap = hostVMPeMap.get(recoveringHost.getId());				
							peDownTimeMap = new HashMap<Integer, Double>();
							peDownTimeMap = hostPeDownTimeMap.get(recoveringHost.getId());
							Set<Integer>peKeySet = new HashSet<Integer>();
							peKeySet = peDownTimeMap.keySet();
							ArrayList<Integer>peKeySetList = new ArrayList<Integer>(peKeySet);				
							for(int i=0; i<peDownTimeMap.size(); i++){
								double downTime;				
								downTime = CloudSim.clock() - peDownTimeMap.get(peKeySetList.get(i));
								IdleTime.setHostPeDownTime(recoveringHost.getId(), peKeySetList.get(i), downTime);
							}
							hostPeDownTimeMap.remove(recoveringHost.getId());				
							for(int i=0;i<size;i++){
								sizeCletList = failedVMCletMap.get(failVMsID.get(i)).size();
								cletsTempID = new ArrayList<Integer>();			
								cletsTempID = failedVMCletMap.get(failVMsID.get(i));								
								failedVMCletMap.remove(failVMsID.get(i));
								failVMs.get(i).setBeingInstantiated(true); //This matters. If this variable will not set to true then the reallocation of resources will not happen and estimatedFinishingTime of a corresponding cloudlet will set to infinity.			
								recoveringCloudlet = new ArrayList<Cloudlet>();
								double cletFailTime = 0.0;
								double cletDownTime = 0.0;
								long cletLengthTemp;
								long currentRemaining;					
								long soFarClet;							
								long oldBackupLength;							
								for(int j=0;j<sizeCletList;j++){									
									failFlagTable.put(cletsTempID.get(j), true);
									cletFailTime = DownTimeTable.getCletFailTimeTable(cletsTempID.get(j));
									cletDownTime = CloudSim.clock() - cletFailTime;										
									recoveringCloudlet.add(broker.getidtoCloudletLst().get(cletsTempID.get(j)));									
									if(!cletLengthBackup.containsKey(cletsTempID.get(j))){
										cletLengthBackup.put(cletsTempID.get(j), recoveringCloudlet.get(j).getCloudletLength());
									}										
									if(!checkpointsForCloudlets.containsKey(cletsTempID.get(j))){								
										soFarClet = CloudletStatus.getsofarCloudlet(cletsTempID.get(j));										
										cletLengthTemp = recoveringCloudlet.get(j).getCloudletLength() + ((long)(cletDownTime*1000));
										oldBackupLength = cletLengthBackup.get(recoveringCloudlet.get(j).getCloudletId());
										oldBackupLength = oldBackupLength + ((long)(cletDownTime*1000));
										cletLengthBackup.put(cletsTempID.get(j), oldBackupLength);
										failureOccurredBeforeChkpt.put(cletsTempID.get(j), true);
									}
									else{
										cletLengthTemp = cletLengthBackup.get(cletsTempID.get(j)) - checkpointsForCloudlets.get(cletsTempID.get(j));
										currentRemaining = remainingCletLengthTable.get(cletsTempID.get(j));
										soFarClet = cletLengthTemp - currentRemaining;									
										cletLengthTemp = cletLengthTemp + (long)(cletDownTime*1000);
										oldBackupLength = cletLengthBackup.get(recoveringCloudlet.get(j).getCloudletId());										
										oldBackupLength = oldBackupLength + (long)(cletDownTime*1000);
										cletLengthBackup.put(recoveringCloudlet.get(j).getCloudletId(), oldBackupLength);
										if(failureOccurredBeforeChkpt.containsKey(cletsTempID.get(j))){
											failureOccurredBeforeChkpt.remove(cletsTempID.get(j));
										}
									}
									recoveringCloudlet.get(j).setCloudletLength(cletLengthTemp);										 
									DownTimeTable.setCloudletDownTime(cletsTempID.get(j), cletDownTime);
									CloudletReexecutionPart.setCletReexecutionTime(cletsTempID.get(j), soFarClet);
									DownTimeTable.setCletPerHostDownTimeTable(cletsTempID.get(j), recoveringHost.getId(), cletDownTime);
									CloudletReexecutionPart.setCletPerHostReexecutionTimeTable(cletsTempID.get(j), recoveringHost.getId(), soFarClet);
									cletLastRemainingMap.put(cletsTempID.get(j), cletLengthTemp);
									if(recoveringCloudletList.isEmpty()){
										recoveringCloudletList.add(recoveringCloudlet.get(j));								
									}
									else{
										for(int x=0;x<recoveringCloudletList.size();x++){
											if(recoveringCloudletList.get(x)==recoveringCloudlet.get(j)){
												lock=true;
												break;										
											}
											if(lock==false){
												recoveringCloudletList.add(recoveringCloudlet.get(j));										
											}									
										}
									}
								}
								recoveringHost.vmCreate(failVMs.get(i)); //Creating a virtual machine on the host with the same name that the VM had before the failure event.						
								List<Pe> peList = new ArrayList<Pe>();
								peList = recoveringHost.getVmScheduler().getPesAllocatedForVM(failVMs.get(i));
								for(int x=0; x<peList.size(); x++){
									int peID = peList.get(x).getId();
									int previousPeID = vmPeMap.get(failVMs.get(i).getId());										
									if(peID != previousPeID){
										IdleTime.setHostPeIdleTable(recoveringHost.getId(), previousPeID, true);									
										IdleTime.setHostPeIdleClockTable(recoveringHost.getId(), previousPeID, CloudSim.clock());									
										IdleTime.setHostPeIdleTable(recoveringHost.getId(), peID, false);											
										if(IdleTime.checkHostPeIdleClockTable(recoveringHost.getId(), peID)){
											IdleTime.setHostPeIdleTimeTable(recoveringHost.getId(), peID, CloudSim.clock());
											IdleTime.removeHostPeIdleClockTable(recoveringHost.getId(),peID);									
										}
									}
								}				
								down_time = CloudSim.clock() - DownTimeTable.getFailTimeTable(failVMs.get(i));
								failVMs.get(i).setId(failVMsID.get(i)); // Setting the ID of the new virtual machine equal to the ID the VM had before the failure event.							
								for(int j=0; j<recoveringCloudlet.size(); j++){								
									double cletUtilization;
									cletUtilization = cletNorm.getNormalizedLength(recoveringCloudlet.get(j));
									recoveringCloudlet.get(j).setVmId(failVMs.get(i).getId());								
									failVMs.get(i).getCloudletScheduler().cloudletSubmit(recoveringCloudlet.get(j));
									ReliabilityCalculator(recoveringHost, failVMs.get(i), cletUtilization);
								}
								getVmList().add(failVMs.get(i));							
								failVMs.get(i).updateVmProcessing(CloudSim.clock(), getVmAllocationPolicy().getHost(failVMs.get(i)).getVmScheduler().getAllocatedMipsForVm(failVMs.get(i)));													
								DownTimeTable.setDownTimeTable(failVMs.get(i), down_time);							
							}		
							hostVMPeMap.remove(recoveringHost.getId());
						}
					}			
				}			
			}
		}
		
		
		public void ReliabilityCalculator(Host host, Vm vm, double cletUtilization){			
				updateCloudletProcessing();			
				double ftaUtilization = 0.0;
				double utilization;
				double reliability;
				if(RunTimeConstants.test == true)
				{				
					if(vmUtl.getCurrentVmUtilization(vm.getId())==0){
						if(RunTimeConstants.traceType.equals("LANL")){
							ftaUtilization = freadLANL.getCurrentUtilization(HostMapping.getFTAhostID(host.getId()));
						}
						if(RunTimeConstants.traceType.equals("Grid5000")){
							ftaUtilization = freadGrid5000.getCurrentUtilization(HostMapping.getFTAhostID(host.getId()));
						}
						utilization = ftaUtilization + cletUtilization;					
					}
					else{
						utilization = vmUtl.getCurrentVmUtilization(vm.getId()) + cletUtilization;
					}					
					if(utilization > 1.0){
						utilization = 1.0;
					}				
					vmUtl.setVmUtilization(vm.getId(), utilization);
					vmUtl.setCurrentVmUtilization(vm.getId(), utilization);
					System.out.println("Utilization of VM #" +vm.getId()+" is " +utilization);				
					reliability = rblcal.getReliability(host, vm, utilization);
					reliability = Math.ceil(reliability * 1000);
					reliability = reliability / 1000;								
					System.out.println("New Reliability of VM #" +vm.getId()+ " is " +reliability);
					System.out.println("Clock value is " +CloudSim.clock());				
					if(RunTimeConstants.failureInjection == true){
						if(flag_record==true){ //Recording only after the occurrence of first failure. Recording of all the values before the occurrence of a failure effects the results.  		
							hvmRbl.setVmReliability(vm, reliability);
							hvmRbl.sethostVmReliabilityTable(host, vm);
							hvmRbl.setSystemReliability();
							hvmRbl.setSystemAvailability();
							hvmRbl.setSystemMaintainability();
						}				
					}
					else{
						hvmRbl.setVmReliability(vm, reliability);
						hvmRbl.sethostVmReliabilityTable(host, vm);
						hvmRbl.setSystemReliability();
						hvmRbl.setSystemAvailability();
						hvmRbl.setSystemMaintainability();
					}
				}
				else{
					if(vmUtl.getCurrentVmUtilization(vm.getId())==0){
						if(RunTimeConstants.traceType.equals("LANL")){
							ftaUtilization = freadLANL.getCurrentUtilization(HostMapping.getFTAhostID(host.getId()));
						}
						if(RunTimeConstants.traceType.equals("Grid5000")){
							ftaUtilization = freadGrid5000.getCurrentUtilization(HostMapping.getFTAhostID(host.getId()));
						}
						utilization = ftaUtilization + cletUtilization;					
					}
					else{
						utilization = vmUtl.getCurrentVmUtilization(vm.getId()) + cletUtilization;
					}					
					if(utilization > 1.0){
						utilization = 1.0;
					}				
					vmUtl.setVmUtilization(vm.getId(), utilization);
					vmUtl.setCurrentVmUtilization(vm.getId(), utilization);								
					reliability = rblcal.getReliability(host, vm, utilization);
					reliability = Math.ceil(reliability * 1000);
					reliability = reliability / 1000;	
					if(RunTimeConstants.failureInjection == true){
						if(flag_record==true){ //Recording only after the occurrence of first failure. Recording of all the values before the occurrence of a failure effects the results.  		
							hvmRbl.setVmReliability(vm, reliability);
							hvmRbl.sethostVmReliabilityTable(host, vm);
							hvmRbl.setSystemReliability();
							hvmRbl.setSystemAvailability();
							hvmRbl.setSystemMaintainability();
						}	
					}
					else{
						hvmRbl.setVmReliability(vm, reliability);
						hvmRbl.sethostVmReliabilityTable(host, vm);
						hvmRbl.setSystemReliability();
						hvmRbl.setSystemAvailability();
						hvmRbl.setSystemMaintainability();
					}
				}				
			}
		
		
		public void setCletNorm(CloudletNormalization cletNorm){
			this.cletNorm = cletNorm;
		}
		
		public void setReliabilityCalculator(ReliabilityCalculator rblcal){
			this.rblcal = rblcal;
		}
		
		public void setHostVMMappingReliability(HostVMMappingReliability hvmRbl){
			this.hvmRbl = hvmRbl;
		}
		
		/**
		 * Process the event for an User/Broker who wants to create a VM in this PowerDatacenter. This
		 * PowerDatacenter will then send the status back to the User/Broker.
		 * 
		 * @param ev a Sim_event object
		 * @param ack the ack
		 * @pre ev != null
		 * @post $none
		 */
		
		protected void processVmCreate(SimEvent ev, boolean ack) {
			Vm vm = (Vm) ev.getData();

			boolean result = getVmAllocationPolicy().allocateHostForVm(vm);

			if (ack) {
				int[] data = new int[3];
				data[0] = getId();
				data[1] = vm.getId();

				if (result) {
					data[2] = CloudSimTags.TRUE;
				} else {
					data[2] = CloudSimTags.FALSE;
				}
				send(vm.getUserId(), CloudSim.getMinTimeBetweenEvents(), CloudSimTags.VM_CREATE_ACK, data);
			}

			if (result) {
				getVmList().add(vm);

				//if (vm.isBeingInstantiated()) {
				//	vm.setBeingInstantiated(false);
				//}

				vm.updateVmProcessing(CloudSim.clock(), getVmAllocationPolicy().getHost(vm).getVmScheduler()
						.getAllocatedMipsForVm(vm));	
				
				vmRecord.setVmCurrentHost(vm.getId(), vm.getHost().getId());
			}
		}
		
		
		
		public void processVmCreate(Vm vm){ 	//This function is written by Yogesh Sharma for the implementation of rate of arrival of cloudlets.		
			if(RunTimeConstants.test == true)
			{
				int hostListSize = getHostList().size();		
				Host host;
				boolean result = getVmAllocationPolicy().allocateHostForVm(vm);	
				host = vm.getHost();
				if(host.getDatacenter()==null){
					host.setDatacenter(this);
				}			
				vmRecord.setVmCurrentHost(vm.getId(), vm.getHost().getId());
				if(hostListSize < getHostList().size()){
					boolean flagNewHost = false;
					int index = 0;
					for(int i=0;i<HostMapping.getHostMapSize();i++){
						index = i;
						flagNewHost = true;
						for(int j=0;j<failhostList.size();j++){
							if(failhostList.get(j)== HostMapping.getFTAhostID(i)){
								flagNewHost = false;
								break;
							}						
						}
					}
					if(flagNewHost==true){
						difference = 0;		
						difference_last = 0;
						failurePrediction = new ArrayList<Double>();
						currentPredictionList = new ArrayList<Double>();
						int ftaNodeID = HostMapping.getFTAhostID(index);
						failhostList.add(ftaNodeID);
						int count_fail = 0;
					//	firstFailureFlag.put(ftaNodeID, true);	
					//	firstRecoveryFlag.put(ftaNodeID, true);
					//	firstPredictionFlag.put(ftaNodeID, true);
					//	firstCheckpointFlag.put(ftaNodeID, true);
						int datacenterNodeID = HostMapping.getHostID(ftaNodeID);
						failhostTable.put(ftaNodeID, datacenterNodeID);				
			//			System.out.println(ftaNodeID+ " is the FTA Node ID for which failure has been scheduled");				
						if(RunTimeConstants.traceType.equals("LANL")){
							freadLANL.preparestartTime(ftaNodeID);				
							freadLANL.preparestopTime(ftaNodeID);	
							freadLANL.prepareAvailstartTime(ftaNodeID);					
							freadLANL.prepareAvailstopTime(ftaNodeID);
							freadLANL.prepareDifference(ftaNodeID);
							MTBF.add(freadLANL.getMTBF(ftaNodeID));				
							MTTR.add(freadLANL.getMTTR(ftaNodeID));										
							count_fail=freadLANL.getFailCount(ftaNodeID);
						}
						if(RunTimeConstants.traceType.equals("Grid5000")){
							freadGrid5000.preparestartTime(ftaNodeID);				
							freadGrid5000.preparestopTime(ftaNodeID);	
							freadGrid5000.prepareAvailstartTime(ftaNodeID);					
							freadGrid5000.prepareAvailstopTime(ftaNodeID);
							freadGrid5000.prepareDifference(ftaNodeID);
							MTBF.add(freadGrid5000.getMTBF(ftaNodeID));				
							MTTR.add(freadGrid5000.getMTTR(ftaNodeID));										
							count_fail=freadGrid5000.getFailCount(ftaNodeID);
						}
												
						if(RunTimeConstants.predictionFlag == true && RunTimeConstants.syntheticPrediction == true){
							failPredictSynthetic.predictFailures(ftaNodeID);					
							failurePrediction = failPredictSynthetic.getPredictedTBF(ftaNodeID);	
						}
						if(RunTimeConstants.predictionFlag == true && RunTimeConstants.syntheticPrediction == false){
							failPredict.predictFailures(ftaNodeID);
							failurePrediction = failPredict.getPredictedTBF(ftaNodeID);	
						}
										
						if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism==1){
							//createCheckpointInterval(hostList().get(index), (fread.getMTBF(ftaNodeID))/10);	
							if(RunTimeConstants.traceType.equals("LANL")){
								createCheckpointInterval(hostList().get(index).getId(), (freadLANL.getMTBF(ftaNodeID)));								
							}
							if(RunTimeConstants.traceType.equals("Grid5000")){
								createCheckpointInterval(hostList().get(index).getId(), (freadGrid5000.getMTBF(ftaNodeID)));
							}								
							send(3, checkpointInterval.get(hostList().get(index).getId()), CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);
						}
					
						System.out.println("Number of failure events for node " +ftaNodeID+ " are " +count_fail);
						if(count_fail == 0){
							System.out.println(ftaNodeID+ " is not going to fail");
						}
						else{
							if(RunTimeConstants.considerLaterPrediction == true){
								if(RunTimeConstants.traceType.equals("LANL")){							
									for(int j=0;j<count_fail;j++){						
										starttime = freadLANL.getstartTime();						
									//	starttime = starttime + difference_last;						
									//	difference = fread.getDifference();					
										endtime = freadLANL.getendTime();						
										if(freadLANL.getFirstEvent(ftaNodeID)==0 && !(nodeWithFirstFailEventMap.containsKey(ftaNodeID))){
											nodeWithFirstFailEventMap.put(ftaNodeID, true);
										}
										else{
											if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == false){								
												predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);	
												currentPredictionList.add(predictionTime);
												failurePrediction.remove(0);										
									//		System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));							
												send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);		
												if(RunTimeConstants.failureInjection == true){	
									//				System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);								
									//				System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock()+endtime));
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);					
												}
											}								
											if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){									
												predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);						
												currentPredictionList.add(predictionTime);							
												failurePrediction.remove(0);							
									//			System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));							
												send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);				
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);							
									//				System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));							
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
									//				System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + endtime));
												}
											}								
											if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){	
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);							
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
												}
											}									
											if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 3){								
												predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);								
												currentPredictionList.add(predictionTime);								
												failurePrediction.remove(0);					
									//			System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));																
												send(3, predictionTime, CloudSimTags.VM_MIGRATION, ftaNodeID);	
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);								
									//				System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));							
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);								
									//				System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + endtime));
												}
											}									
											if(RunTimeConstants.predictionFlag == false && RunTimeConstants.vmConsolidationFlag == true && RunTimeConstants.faultToleranceMechanism == 3){	
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);									
									//				System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));										
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);								
									//				System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + endtime));
												}
											}								
											if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4){								
												predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);							
												currentPredictionList.add(predictionTime);							
												failurePrediction.remove(0);								
									//			System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));								
												send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);							
												send(3, predictionTime, CloudSimTags.VM_MIGRATION_CHECKPOINTING, ftaNodeID);	
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
									//				System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));								
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
									//				System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + endtime));
												}
											}							
											if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1){
												if(RunTimeConstants.failureInjection == true){
									//				System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);								
									//				System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock()+endtime));
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
												}
											}			
											if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 2){		
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);								
									//				System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));								
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);										
									//				System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + endtime));
												}
											}								
										}				
							//  send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_PERFECT_CHECKPOINTING, node);									
							//  send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_PERFECT_CHECKPOINTING, node);						
							//	endtime=starttime + fread.getendTime(fail.getId());						
							//	System.out.println("Host " +fail.getId()+  " will be recovered at" );
							//	send(fail.getId(), endtime, CloudSimTags.FAILURE_RECOVERY_EVENT);					
							//	send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT);
									}	
								}
								if(RunTimeConstants.traceType.equals("Grid5000")){
									for(int j=0;j<count_fail;j++){						
										starttime = freadGrid5000.getstartTime();						
									//	starttime = starttime + difference_last;						
									//	difference = fread.getDifference();					
										endtime = freadGrid5000.getendTime();						
										if(freadGrid5000.getFirstEvent(ftaNodeID)==0 && !(nodeWithFirstFailEventMap.containsKey(ftaNodeID))){
											nodeWithFirstFailEventMap.put(ftaNodeID, true);
										}
										else{
											if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == false){								
												predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);	
												currentPredictionList.add(predictionTime);
												failurePrediction.remove(0);										
									//		System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));							
												send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);				
												if(RunTimeConstants.failureInjection == true){
									//				System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);								
									//				System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock()+endtime));
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
												}
											}								
											if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){									
												predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);						
												currentPredictionList.add(predictionTime);							
												failurePrediction.remove(0);							
									//			System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));							
												send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);			
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);							
									//				System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));							
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
									//				System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + endtime));		
												}
											}								
											if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){	
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);							
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
												}
											}									
											if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 3){								
												predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);								
												currentPredictionList.add(predictionTime);								
												failurePrediction.remove(0);					
									//			System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));																
												send(3, predictionTime, CloudSimTags.VM_MIGRATION, ftaNodeID);		
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);								
									//				System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));							
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);								
									//				System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + endtime));
												}
											}									
											if(RunTimeConstants.predictionFlag == false && RunTimeConstants.vmConsolidationFlag == true && RunTimeConstants.faultToleranceMechanism == 3){	
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);									
									//				System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));										
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);								
									//				System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + endtime));
												}
											}								
											if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4){								
												predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);							
												currentPredictionList.add(predictionTime);							
												failurePrediction.remove(0);								
									//			System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));								
												send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);							
												send(3, predictionTime, CloudSimTags.VM_MIGRATION_CHECKPOINTING, ftaNodeID);	
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
									//				System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));								
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
									//				System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + endtime));
												}
											}							
											if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1){
												if(RunTimeConstants.failureInjection == true){
									//				System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);								
									//				System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock()+endtime));
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
												}
											}			
											if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 2){	
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);								
									//				System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));								
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);										
									//				System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + endtime));
												}
											}								
										}						
									}	
								}
							}
							else{
								if(RunTimeConstants.traceType.equals("LANL")){
									for(int j=0;j<count_fail;j++){						
										starttime = freadLANL.getstartTime();						
										//starttime = starttime + difference_last;						
										//difference = fread.getDifference();					
										endtime = freadLANL.getendTime();						
										if(freadLANL.getFirstEvent(ftaNodeID)==0 && !(nodeWithFirstFailEventMap.containsKey(ftaNodeID))){
											nodeWithFirstFailEventMap.put(ftaNodeID, true);
										}
										else{
											if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == false){								
												predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);	
											//	currentPredictionList.add(predictionTime);
												failurePrediction.remove(0);				
												if(predictionTime > starttime){
													if(RunTimeConstants.failureInjection == true){
//														System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));
														send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);								
											//			System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock()+endtime));
														send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);		
													}
												}
												else{
													currentPredictionList.add(predictionTime);
//													System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));							
													send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);
													if(RunTimeConstants.failureInjection == true){
											//			System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));
														send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);								
											//			System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock()+endtime));
														send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
													}
												}																
											}								
											if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){									
												predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);						
											//	currentPredictionList.add(predictionTime);							
												failurePrediction.remove(0);
												if(predictionTime > starttime){
													if(RunTimeConstants.failureInjection == true){
														send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);							
													//	System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));							
														send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
													//	System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + endtime));
													}
												}
												else{
													currentPredictionList.add(predictionTime);
//													System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));							
													send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);	
													if(RunTimeConstants.failureInjection == true){
														send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);							
											//			System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));							
														send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
											//			System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + endtime));
													}
												}																
											}
										
											if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){	
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);							
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
												}
											}
										
											if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 3){								
												predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);								
											//	currentPredictionList.add(predictionTime);								
												failurePrediction.remove(0);		
												if(predictionTime > starttime){
													if(RunTimeConstants.failureInjection == true){
														send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);								
													//	System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));							
														send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);								
													//	System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + endtime));
													}
												}
												else{
													currentPredictionList.add(predictionTime);
//													System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));																
													send(3, predictionTime, CloudSimTags.VM_MIGRATION, ftaNodeID);	
													if(RunTimeConstants.failureInjection == true){
														send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);								
											//			System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));							
														send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);								
											//			System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + endtime));
													}
												}									
											}
										
											if(RunTimeConstants.predictionFlag == false && RunTimeConstants.vmConsolidationFlag == true && RunTimeConstants.faultToleranceMechanism == 3){
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);									
										//			System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));										
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);								
										//			System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + endtime));
												}
											}
									
											if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4){								
												predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);							
											//	currentPredictionList.add(predictionTime);							
												failurePrediction.remove(0);	
												if(predictionTime > starttime){
													if(RunTimeConstants.failureInjection == true){
														send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
													//	System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));								
														send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
													//	System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + endtime));	
													}
												}
												else{
													currentPredictionList.add(predictionTime);
//													System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));								
													send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);							
													send(3, predictionTime, CloudSimTags.VM_MIGRATION_CHECKPOINTING, ftaNodeID);			
													if(RunTimeConstants.failureInjection == true){
														send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
											//			System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));								
														send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
											//			System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + endtime));
													}
												}																
											}							
									
											if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1){
												if(RunTimeConstants.failureInjection == true){
										//			System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);								
										//			System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock()+endtime));
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
												}
											}						
									
											if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 2){	
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);								
													//System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));								
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);										
													//System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + endtime));
												}
											}
									
										}				
								//  send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_PERFECT_CHECKPOINTING, node);									
								//  send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_PERFECT_CHECKPOINTING, node);						
								//	endtime=starttime + fread.getendTime(fail.getId());						
								//	System.out.println("Host " +fail.getId()+  " will be recovered at" );
								//	send(fail.getId(), endtime, CloudSimTags.FAILURE_RECOVERY_EVENT);					
								//	send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT);
									}
								}
								if(RunTimeConstants.traceType.equals("Grid5000")){							
									for(int j=0;j<count_fail;j++){						
										starttime = freadGrid5000.getstartTime();						
									//starttime = starttime + difference_last;						
									//difference = fread.getDifference();					
										endtime = freadGrid5000.getendTime();						
										if(freadGrid5000.getFirstEvent(ftaNodeID)==0 && !(nodeWithFirstFailEventMap.containsKey(ftaNodeID))){
											nodeWithFirstFailEventMap.put(ftaNodeID, true);
										}
										else{
											if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == false){								
												predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);	
										//		currentPredictionList.add(predictionTime);
												failurePrediction.remove(0);				
												if(predictionTime > starttime){
													if(RunTimeConstants.failureInjection == true){
//														System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));
														send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);								
										//				System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock()+endtime));
														send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
													}
												}
												else{
													currentPredictionList.add(predictionTime);
//													System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));							
													send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);				
													if(RunTimeConstants.failureInjection == true){
										//				System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));
														send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);								
										//				System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock()+endtime));
														send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
													}
												}																
											}								
											if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){									
												predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);						
										//		currentPredictionList.add(predictionTime);							
												failurePrediction.remove(0);
												if(predictionTime > starttime){
													if(RunTimeConstants.failureInjection == true){
														send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);							
												//		System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));							
														send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
												//		System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + endtime));
													}
												}
												else{
													currentPredictionList.add(predictionTime);
//													System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));							
													send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);			
													if(RunTimeConstants.failureInjection == true){
														send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);							
										//				System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));							
														send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
										//				System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + endtime));
													}
												}																
											}									
											if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){	
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);							
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
												}
											}									
											if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 3){								
												predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);								
										//		currentPredictionList.add(predictionTime);								
												failurePrediction.remove(0);		
												if(predictionTime > starttime){
													if(RunTimeConstants.failureInjection == true){
														send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);								
												//		System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));							
														send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);								
												//		System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + endtime));
													}
												}
												else{
													currentPredictionList.add(predictionTime);
//													System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));																
													send(3, predictionTime, CloudSimTags.VM_MIGRATION, ftaNodeID);		
													if(RunTimeConstants.failureInjection == true){
														send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);								
										//				System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));							
														send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);								
										//				System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + endtime));
													}
												}									
											}							
											if(RunTimeConstants.predictionFlag == false && RunTimeConstants.vmConsolidationFlag == true && RunTimeConstants.faultToleranceMechanism == 3){	
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);									
									//				System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));										
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);								
									//				System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + endtime));
												}
											}							
											if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4){								
												predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);							
										//		currentPredictionList.add(predictionTime);							
												failurePrediction.remove(0);	
												if(predictionTime > starttime){
													if(RunTimeConstants.failureInjection == true){
														send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
												//		System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));								
														send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
												//		System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + endtime));
													}
												}
												else{
													currentPredictionList.add(predictionTime);
//													System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));								
													send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);							
													send(3, predictionTime, CloudSimTags.VM_MIGRATION_CHECKPOINTING, ftaNodeID);	
													if(RunTimeConstants.failureInjection == true){
														send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
										//				System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));								
														send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
										//				System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + endtime));
													}
												}																
											}									
											if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1){
												if(RunTimeConstants.failureInjection == true){
									//				System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);								
									//				System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock()+endtime));
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
												}
											}		
								
											if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 2){	
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);								
													//System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));								
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);										
													//System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + endtime));
												}
											}							
										}				
							//  send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_PERFECT_CHECKPOINTING, node);									
							//  send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_PERFECT_CHECKPOINTING, node);						
							//	endtime=starttime + fread.getendTime(fail.getId());						
							//	System.out.println("Host " +fail.getId()+  " will be recovered at" );
							//	send(fail.getId(), endtime, CloudSimTags.FAILURE_RECOVERY_EVENT);					
							//	send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT);
									}
								}
							}							
							//counter_endTime = counter_endTime + 1;
							if(!failurePrediction.isEmpty()){
								failurePrediction = null;							
							}							
							currentPrediction.put(host.getId(), currentPredictionList);					
						}
					}
				}
				else{
					if(IdleHostTime.getFailInjectionFlag(host)==true){
						if(RunTimeConstants.traceType.equals("LANL")){
							int ftaNodeID = HostMapping.getFTAhostID(host.getId());
							System.out.println("FTA node ID " +ftaNodeID+ " has been reallocated to VM " +vm.getId());
							System.out.println("So failure events will be rescheduled for the given node");
						//	firstFailureFlag.put(ftaNodeID, true);	
						//	firstRecoveryFlag.put(ftaNodeID, true);
						//	firstPredictionFlag.put(ftaNodeID, true);
						//	firstCheckpointFlag.put(ftaNodeID, true);
							freadLANL.preparestartTime(ftaNodeID);				
							freadLANL.preparestopTime(ftaNodeID);
							freadLANL.prepareAvailstartTime(ftaNodeID);					
							freadLANL.prepareAvailstopTime(ftaNodeID);
							freadLANL.prepareDifference(ftaNodeID);
							int count_fail=freadLANL.getFailCount(ftaNodeID);
							if(RunTimeConstants.predictionFlag == true && RunTimeConstants.syntheticPrediction == true){
								failPredictSynthetic.predictFailures(ftaNodeID);					
								failurePrediction = failPredictSynthetic.getPredictedTBF(ftaNodeID);
							}
							if(RunTimeConstants.predictionFlag == true && RunTimeConstants.syntheticPrediction == false){
								failPredict.predictFailures(ftaNodeID);					
								failurePrediction = failPredict.getPredictedTBF(ftaNodeID);	
							}						
							if(count_fail == 0){
								System.out.println(ftaNodeID+ " is not going to fail");
							}
							else{
								difference =  0;
								difference_last = 0;
								for(int j=0;j<count_fail;j++){						
									starttime = freadLANL.getstartTime();						
									//starttime = starttime + difference_last;						
									//difference = fread.getDifference();					
									endtime = freadLANL.getendTime();							
								
								//endtime = endtime + difference + difference_last;
								
								//while(difference>1){
									//difference = difference/10;
								//}
									//starttime = (starttime) + difference_last;
									//endtime = (endtime) + difference_last + difference;
									
								//	difference_last = difference;
								/*
								if(j==0){
									starttime = fread.getstartTime()/1000;
									endtime = ((fread.getendTime()/1000)+RunTimeConstants.randomEndTime.get(counter_endTime));
									
								}
								else{
									starttime = ((fread.getstartTime()/1000)+RunTimeConstants.randomEndTime.get(counter_endTime));
									endtime = fread.getendTime()/1000;
									difference = starttime - endtime;
									if(difference<=0){
										endtime = endtime + RunTimeConstants.randomEndTime.get(counter_endTime);
									}
									else{
										difference = difference + RunTimeConstants.randomEndTime.get(counter_endTime);		
										endtime = endtime + difference;
									}
									
									//endtime = ((fread.getendTime()/1000)+RunTimeConstants.randomEndTime.get(counter_endTime));
								//	counter_endTime = counter_endTime + 1;
								}
								*/
									if(RunTimeConstants.faultToleranceMechanism == 1){
										System.out.println("Failure " +j+ " for node " +host.getId()+ " is going to occur at " +(CloudSim.clock() + starttime));					
										send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);								
										System.out.println("Recovery for node " +host.getId()+ " is going to occur at " +(CloudSim.clock()+endtime));			
										send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
									
										if(RunTimeConstants.vmMigrationFlag == true){
											predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);
											failurePrediction.remove(0);								
											System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));
											send(3, predictionTime, CloudSimTags.VM_MIGRATION, ftaNodeID);
										}
									}
									else{
										if(RunTimeConstants.faultToleranceMechanism == 2){
											System.out.println("Failure " +j+ " for node " +host.getId()+ " is going to occur at " +(CloudSim.clock() + starttime));					
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);								
											System.out.println("Failure " +j+ " for node " +host.getId()+ " is going to occur at " +(CloudSim.clock() + starttime));					
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);									
											if(RunTimeConstants.vmMigrationFlag == true){
												predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);
												failurePrediction.remove(0);									
												System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));
												send(3, predictionTime, CloudSimTags.VM_MIGRATION, ftaNodeID);
											}
										}
									}					
								//				
							//  send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_PERFECT_CHECKPOINTING, node);					
												
									//					
							//  send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_PERFECT_CHECKPOINTING, node);						
							  
								}
							//counter_endTime = counter_endTime + 1;
							}
							IdleHostTime.setFailInjectionFlag(host, false);					
						}					
						
						if(RunTimeConstants.traceType.equals("Grid5000")){
							int ftaNodeID = HostMapping.getFTAhostID(host.getId());
							System.out.println("FTA node ID " +ftaNodeID+ " has been reallocated to VM " +vm.getId());
							System.out.println("So failure events will be rescheduled for the given node");
						//	firstFailureFlag.put(ftaNodeID, true);	
						//	firstRecoveryFlag.put(ftaNodeID, true);
						//	firstPredictionFlag.put(ftaNodeID, true);
						//	firstCheckpointFlag.put(ftaNodeID, true);
							freadGrid5000.preparestartTime(ftaNodeID);				
							freadGrid5000.preparestopTime(ftaNodeID);
							freadGrid5000.prepareAvailstartTime(ftaNodeID);					
							freadGrid5000.prepareAvailstopTime(ftaNodeID);
							freadGrid5000.prepareDifference(ftaNodeID);
							int count_fail=freadGrid5000.getFailCount(ftaNodeID);
							if(RunTimeConstants.predictionFlag == true && RunTimeConstants.syntheticPrediction == true){
								failPredictSynthetic.predictFailures(ftaNodeID);					
								failurePrediction = failPredictSynthetic.getPredictedTBF(ftaNodeID);
							}
							if(RunTimeConstants.predictionFlag == true && RunTimeConstants.syntheticPrediction == false){
								failPredict.predictFailures(ftaNodeID);					
								failurePrediction = failPredict.getPredictedTBF(ftaNodeID);	
							}						
							if(count_fail == 0){
								System.out.println(ftaNodeID+ " is not going to fail");
							}
							else{
								difference =  0;
								difference_last = 0;
								for(int j=0;j<count_fail;j++){						
									starttime = freadGrid5000.getstartTime();						
									//starttime = starttime + difference_last;						
									//difference = fread.getDifference();					
									endtime = freadGrid5000.getendTime();							
								
								//endtime = endtime + difference + difference_last;
								
								//while(difference>1){
									//difference = difference/10;
								//}
									//starttime = (starttime) + difference_last;
									//endtime = (endtime) + difference_last + difference;
									
								//	difference_last = difference;
								/*
								if(j==0){
									starttime = fread.getstartTime()/1000;
									endtime = ((fread.getendTime()/1000)+RunTimeConstants.randomEndTime.get(counter_endTime));
									
								}
								else{
									starttime = ((fread.getstartTime()/1000)+RunTimeConstants.randomEndTime.get(counter_endTime));
									endtime = fread.getendTime()/1000;
									difference = starttime - endtime;
									if(difference<=0){
										endtime = endtime + RunTimeConstants.randomEndTime.get(counter_endTime);
									}
									else{
										difference = difference + RunTimeConstants.randomEndTime.get(counter_endTime);		
										endtime = endtime + difference;
									}
									
									//endtime = ((fread.getendTime()/1000)+RunTimeConstants.randomEndTime.get(counter_endTime));
								//	counter_endTime = counter_endTime + 1;
								}
								*/
									if(RunTimeConstants.faultToleranceMechanism == 1){
										System.out.println("Failure " +j+ " for node " +host.getId()+ " is going to occur at " +(CloudSim.clock() + starttime));					
										send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);								
										System.out.println("Recovery for node " +host.getId()+ " is going to occur at " +(CloudSim.clock()+endtime));			
										send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
									
										if(RunTimeConstants.vmMigrationFlag == true){
											predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);
											failurePrediction.remove(0);								
											System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));
											send(3, predictionTime, CloudSimTags.VM_MIGRATION, ftaNodeID);
										}
									}
									else{
										if(RunTimeConstants.faultToleranceMechanism == 2){
											System.out.println("Failure " +j+ " for node " +host.getId()+ " is going to occur at " +(CloudSim.clock() + starttime));					
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);								
											System.out.println("Failure " +j+ " for node " +host.getId()+ " is going to occur at " +(CloudSim.clock() + starttime));					
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);									
											if(RunTimeConstants.vmMigrationFlag == true){
												predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);
												failurePrediction.remove(0);									
												System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));
												send(3, predictionTime, CloudSimTags.VM_MIGRATION, ftaNodeID);
											}
										}
									}					
								//				
							//  send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_PERFECT_CHECKPOINTING, node);					
												
									//					
							//  send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_PERFECT_CHECKPOINTING, node);						
							  
								}
							//counter_endTime = counter_endTime + 1;
							}
							IdleHostTime.setFailInjectionFlag(host, false);						
						}
					}					
				}		
			if (result) {
				getVmList().add(vm);

				if (vm.isBeingInstantiated()) {
					vm.setBeingInstantiated(false);
				}

				vm.updateVmProcessing(CloudSim.clock(), getVmAllocationPolicy().getHost(vm).getVmScheduler()
						.getAllocatedMipsForVm(vm));
			}
		}
		else{				
			int hostListSize = getHostList().size();		
			Host host;
			boolean result = getVmAllocationPolicy().allocateHostForVm(vm);	
			host = vm.getHost();				
			if(host.getDatacenter()==null){
				host.setDatacenter(this);
			}			
			vmRecord.setVmCurrentHost(vm.getId(), vm.getHost().getId());
			if(hostListSize < getHostList().size()){
				boolean flagNewHost = false;
				int index = 0;
				for(int i=0;i<HostMapping.getHostMapSize();i++){
					index = i;
					flagNewHost = true;
					for(int j=0;j<failhostList.size();j++){
						if(failhostList.get(j)== HostMapping.getFTAhostID(i)){
							flagNewHost = false;
							break;
						}						
					}
				}
				if(flagNewHost==true){
					difference = 0;		
					difference_last = 0;
					int count_fail = 0;
					failurePrediction = new ArrayList<Double>();
					currentPredictionList = new ArrayList<Double>();
					int ftaNodeID = HostMapping.getFTAhostID(index);
					failhostList.add(ftaNodeID);					
					int datacenterNodeID = HostMapping.getHostID(ftaNodeID);
					failhostTable.put(ftaNodeID, datacenterNodeID);	
					if(RunTimeConstants.traceType.equals("LANL")){
						freadLANL.preparestartTime(ftaNodeID);				
						freadLANL.preparestopTime(ftaNodeID);	
						freadLANL.prepareAvailstartTime(ftaNodeID);					
						freadLANL.prepareAvailstopTime(ftaNodeID);
						freadLANL.prepareDifference(ftaNodeID);
						MTBF.add(freadLANL.getMTBF(ftaNodeID));				
						MTTR.add(freadLANL.getMTTR(ftaNodeID));										
						count_fail=freadLANL.getFailCount(ftaNodeID);							
						if(RunTimeConstants.predictionFlag == true && RunTimeConstants.syntheticPrediction == true){
							failPredictSynthetic.predictFailures(ftaNodeID);					
							failurePrediction = failPredictSynthetic.getPredictedTBF(ftaNodeID);
						}
						if(RunTimeConstants.predictionFlag == true && RunTimeConstants.syntheticPrediction == false){
							failPredict.predictFailures(ftaNodeID);
							failurePrediction = failPredict.getPredictedTBF(ftaNodeID);	
						}										
						if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism==1){					
							createCheckpointInterval(hostList().get(index).getId(), (freadLANL.getMTBF(ftaNodeID)));		
							send(3, checkpointInterval.get(hostList().get(index).getId()), CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);
						}						
					}			
					if(RunTimeConstants.traceType.equals("Grid5000")){
						freadGrid5000.preparestartTime(ftaNodeID);				
						freadGrid5000.preparestopTime(ftaNodeID);	
						freadGrid5000.prepareAvailstartTime(ftaNodeID);					
						freadGrid5000.prepareAvailstopTime(ftaNodeID);
						freadGrid5000.prepareDifference(ftaNodeID);
						MTBF.add(freadGrid5000.getMTBF(ftaNodeID));				
						MTTR.add(freadGrid5000.getMTTR(ftaNodeID));										
						count_fail=freadGrid5000.getFailCount(ftaNodeID);							
						if(RunTimeConstants.predictionFlag == true && RunTimeConstants.syntheticPrediction == true){
							failPredictSynthetic.predictFailures(ftaNodeID);					
							failurePrediction = failPredictSynthetic.getPredictedTBF(ftaNodeID);
						}
						if(RunTimeConstants.predictionFlag == true && RunTimeConstants.syntheticPrediction == false){
							failPredict.predictFailures(ftaNodeID);
							failurePrediction = failPredict.getPredictedTBF(ftaNodeID);	
						}										
						if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism==1){					
							createCheckpointInterval(hostList().get(index).getId(), (freadGrid5000.getMTBF(ftaNodeID)));		
							send(3, checkpointInterval.get(hostList().get(index).getId()), CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);
						}						
					}
					if(count_fail != 0){	
						if(RunTimeConstants.considerLaterPrediction == true){		
							if(RunTimeConstants.traceType.equals("LANL")){
								for(int j=0;j<count_fail;j++){						
									starttime = freadLANL.getstartTime();											
									endtime = freadLANL.getendTime();	
									if(freadLANL.getFirstEvent(ftaNodeID)==0 && !(nodeWithFirstFailEventMap.containsKey(ftaNodeID))){
										nodeWithFirstFailEventMap.put(ftaNodeID, true);
									}
									else{
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == false){								
											predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);								
											currentPredictionList.add(predictionTime);
											failurePrediction.remove(0);															
											send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);			
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);								
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
											}
										}							
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){									
											predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);						
											currentPredictionList.add(predictionTime);							
											failurePrediction.remove(0);														
											send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);		
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);							
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
											}
										}								
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);							
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
											}
										}
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 3){								
											predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);								
											currentPredictionList.add(predictionTime);								
											failurePrediction.remove(0);					
											send(3, predictionTime, CloudSimTags.VM_MIGRATION, ftaNodeID);	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);								
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);
											}
										}								
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.vmConsolidationFlag == true && RunTimeConstants.faultToleranceMechanism == 3){
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);									
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);
											}
										}								
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4){								
											predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);							
											currentPredictionList.add(predictionTime);							
											failurePrediction.remove(0);								
											send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);							
											send(3, predictionTime, CloudSimTags.VM_MIGRATION_CHECKPOINTING, ftaNodeID);		
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
											}
										}				
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1){
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);								
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
											}
										}							
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 2){	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);								
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);
											}
										}		
									}								
								}	
							}							
							if(RunTimeConstants.traceType.equals("Grid5000")){
								for(int j=0;j<count_fail;j++){						
									starttime = freadGrid5000.getstartTime();											
									endtime = freadGrid5000.getendTime();	
									if(freadGrid5000.getFirstEvent(ftaNodeID)==0 && !(nodeWithFirstFailEventMap.containsKey(ftaNodeID))){
										nodeWithFirstFailEventMap.put(ftaNodeID, true);
									}
									else{
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == false){								
											predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);								
											currentPredictionList.add(predictionTime);
											failurePrediction.remove(0);															
											send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);		
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);								
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
											}
										}							
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){									
											predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);						
											currentPredictionList.add(predictionTime);							
											failurePrediction.remove(0);														
											send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);		
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);							
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
											}
										}								
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);							
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
											}
										}
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 3){								
											predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);								
											currentPredictionList.add(predictionTime);								
											failurePrediction.remove(0);					
											send(3, predictionTime, CloudSimTags.VM_MIGRATION, ftaNodeID);
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);								
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);
											}
										}								
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.vmConsolidationFlag == true && RunTimeConstants.faultToleranceMechanism == 3){	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);									
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);	
											}
										}								
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4){								
											predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);							
											currentPredictionList.add(predictionTime);							
											failurePrediction.remove(0);								
											send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);							
											send(3, predictionTime, CloudSimTags.VM_MIGRATION_CHECKPOINTING, ftaNodeID);
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
											}
										}				
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1){
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);								
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
											}
										}							
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 2){	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);								
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);
											}
										}		
									}
								}							
							}
						}
						else{
							if(RunTimeConstants.traceType.equals("LANL")){
								for(int j=0;j<count_fail;j++){								
									starttime = freadLANL.getstartTime();											
									endtime = freadLANL.getendTime();	
									if(freadLANL.getFirstEvent(ftaNodeID)==0 && !(nodeWithFirstFailEventMap.containsKey(ftaNodeID))){
										nodeWithFirstFailEventMap.put(ftaNodeID, true);
									}
									else{
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == false){								
											predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);								
										//	currentPredictionList.add(predictionTime);
											failurePrediction.remove(0);
											if(predictionTime > starttime){
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);								
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
												}
											}
											else{
												currentPredictionList.add(predictionTime);
												send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);	
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);								
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
												}
											}																
										}							
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){									
											predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);						
										//	currentPredictionList.add(predictionTime);							
											failurePrediction.remove(0);		
											if(predictionTime > starttime){
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);							
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
												}
											}
											else{
												currentPredictionList.add(predictionTime);
												send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);		
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);							
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
												}
											}																		
										}								
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);							
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
											}
										}
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 3){								
											predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);								
										//	currentPredictionList.add(predictionTime);								
											failurePrediction.remove(0);	
											if(predictionTime > starttime){
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);								
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);
												}
											}
											else{
												currentPredictionList.add(predictionTime);
												send(3, predictionTime, CloudSimTags.VM_MIGRATION, ftaNodeID);		
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);								
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);
												}
											}																		
										}								
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.vmConsolidationFlag == true && RunTimeConstants.faultToleranceMechanism == 3){
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);									
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);
											}
										}								
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4){								
											predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);							
										//	currentPredictionList.add(predictionTime);							
											failurePrediction.remove(0);
											if(predictionTime > starttime){
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
												}
											}
											else{
												currentPredictionList.add(predictionTime);
												send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);							
												send(3, predictionTime, CloudSimTags.VM_MIGRATION_CHECKPOINTING, ftaNodeID);		
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
												}
											}										
										}				
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == false){
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);								
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
											}
										}							
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 2){		
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);								
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);
											}
										}		
									}
								}	
							}							
							if(RunTimeConstants.traceType.equals("Grid5000")){
								for(int j=0;j<count_fail;j++){								
									starttime = freadGrid5000.getstartTime();											
									endtime = freadGrid5000.getendTime();	
									if(freadGrid5000.getFirstEvent(ftaNodeID)==0 && !(nodeWithFirstFailEventMap.containsKey(ftaNodeID))){
										nodeWithFirstFailEventMap.put(ftaNodeID, true);
									}
									else{
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == false){								
											predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);								
										//	currentPredictionList.add(predictionTime);
											failurePrediction.remove(0);
											if(predictionTime > starttime){
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);								
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
												}
											}
											else{
												currentPredictionList.add(predictionTime);
												send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);		
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);								
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
												}
											}																
										}							
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){									
											predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);						
										//	currentPredictionList.add(predictionTime);							
											failurePrediction.remove(0);		
											if(predictionTime > starttime){
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);							
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
												}
											}
											else{
												currentPredictionList.add(predictionTime);
												send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);	
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);							
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
												}
											}																		
										}								
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);							
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
											}
										}
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 3){								
											predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);								
										//	currentPredictionList.add(predictionTime);								
											failurePrediction.remove(0);	
											if(predictionTime > starttime){
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);								
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);
												}
											}
											else{
												currentPredictionList.add(predictionTime);
												send(3, predictionTime, CloudSimTags.VM_MIGRATION, ftaNodeID);		
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);								
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);
												}
											}																		
										}								
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.vmConsolidationFlag == true && RunTimeConstants.faultToleranceMechanism == 3){	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);									
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);
											}
										}								
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4){								
											predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);							
										//	currentPredictionList.add(predictionTime);							
											failurePrediction.remove(0);
											if(predictionTime > starttime){
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
												}
											}
											else{
												currentPredictionList.add(predictionTime);
												send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);							
												send(3, predictionTime, CloudSimTags.VM_MIGRATION_CHECKPOINTING, ftaNodeID);
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
												}
											}										
										}				
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == false){
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);								
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
											}
										}							
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 2){	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);								
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);
											}
										}		
									}
								}							
							}
						}						
						if(!failurePrediction.isEmpty()){
							failurePrediction = null;							
						}							
						currentPrediction.put(host.getId(), currentPredictionList);													
					}
				}
			}
				else{
					if(IdleHostTime.getFailInjectionFlag(host)==true){
						if(RunTimeConstants.traceType.equals("LANL")){
							int ftaNodeID = HostMapping.getFTAhostID(host.getId());						
							freadLANL.preparestartTime(ftaNodeID);				
							freadLANL.preparestopTime(ftaNodeID);
							freadLANL.prepareAvailstartTime(ftaNodeID);					
							freadLANL.prepareAvailstopTime(ftaNodeID);
							freadLANL.prepareDifference(ftaNodeID);
							int count_fail=freadLANL.getFailCount(ftaNodeID);
							if(RunTimeConstants.predictionFlag == true && RunTimeConstants.syntheticPrediction == true){
								failPredictSynthetic.predictFailures(ftaNodeID);					
								failurePrediction = failPredictSynthetic.getPredictedTBF(ftaNodeID);
							}
							if(RunTimeConstants.predictionFlag == true && RunTimeConstants.syntheticPrediction == false){
								failPredict.predictFailures(ftaNodeID);					
								failurePrediction = failPredict.getPredictedTBF(ftaNodeID);
							}						
							if(count_fail != 0){
								difference =  0;
								difference_last = 0;
								for(int j=0;j<count_fail;j++){						
									starttime = freadLANL.getstartTime();								
									endtime = freadLANL.getendTime();							
									if(RunTimeConstants.faultToleranceMechanism == 1){
										send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);								
										send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);								
										if(RunTimeConstants.vmMigrationFlag == true){
											predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);
											failurePrediction.remove(0);								
											send(3, predictionTime, CloudSimTags.VM_MIGRATION, ftaNodeID);
										}
									}
									else{
										if(RunTimeConstants.faultToleranceMechanism == 2){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);								
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);									
											if(RunTimeConstants.vmMigrationFlag == true){
												predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);
												failurePrediction.remove(0);									
												send(3, predictionTime, CloudSimTags.VM_MIGRATION, ftaNodeID);
											}
										}
									}		  
								}					
							}
						}						
						if(RunTimeConstants.traceType.equals("Grid5000")){
							int ftaNodeID = HostMapping.getFTAhostID(host.getId());						
							freadGrid5000.preparestartTime(ftaNodeID);				
							freadGrid5000.preparestopTime(ftaNodeID);
							freadGrid5000.prepareAvailstartTime(ftaNodeID);					
							freadGrid5000.prepareAvailstopTime(ftaNodeID);
							freadGrid5000.prepareDifference(ftaNodeID);
							int count_fail=freadGrid5000.getFailCount(ftaNodeID);
							if(RunTimeConstants.predictionFlag == true && RunTimeConstants.syntheticPrediction == true){
								failPredictSynthetic.predictFailures(ftaNodeID);					
								failurePrediction = failPredictSynthetic.getPredictedTBF(ftaNodeID);
							}
							if(RunTimeConstants.predictionFlag == true && RunTimeConstants.syntheticPrediction == false){
								failPredict.predictFailures(ftaNodeID);					
								failurePrediction = failPredict.getPredictedTBF(ftaNodeID);
							}						
							if(count_fail != 0){
								difference =  0;
								difference_last = 0;
								for(int j=0;j<count_fail;j++){						
									starttime = freadGrid5000.getstartTime();								
									endtime = freadGrid5000.getendTime();							
									if(RunTimeConstants.faultToleranceMechanism == 1){
										send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);								
										send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);								
										if(RunTimeConstants.vmMigrationFlag == true){
											predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);
											failurePrediction.remove(0);								
											send(3, predictionTime, CloudSimTags.VM_MIGRATION, ftaNodeID);
										}
									}
									else{
										if(RunTimeConstants.faultToleranceMechanism == 2){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);								
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);									
											if(RunTimeConstants.vmMigrationFlag == true){
												predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);
												failurePrediction.remove(0);									
												send(3, predictionTime, CloudSimTags.VM_MIGRATION, ftaNodeID);
											}
										}
									}		  
								}					
							}
						}
						IdleHostTime.setFailInjectionFlag(host, false);						
					}	
				}		
			if (result) {
				getVmList().add(vm);

				if (vm.isBeingInstantiated()) {
					vm.setBeingInstantiated(false);
				}

				vm.updateVmProcessing(CloudSim.clock(), getVmAllocationPolicy().getHost(vm).getVmScheduler()
						.getAllocatedMipsForVm(vm));
			}				
		}
	}
			
	
		
		/**
		 * Processes a Cloudlet submission.
		 * 
		 * @param ev a SimEvent object
		 * @param ack an acknowledgement
		 * @pre ev != null
		 * @post $none
		 */
		@Override
		protected void processCloudletSubmit(SimEvent ev, boolean ack) {
			updateCloudletProcessing();			

			try {
				// gets the Cloudlet object
				Cloudlet cl = (Cloudlet) ev.getData();

				// checks whether this Cloudlet has finished or not
				if (cl.isFinished()) {
					String name = CloudSim.getEntityName(cl.getUserId());
					Log.printLine(getName() + ": Warning - Cloudlet #" + cl.getCloudletId() + " owned by " + name
							+ " is already completed/finished.");
					Log.printLine("Therefore, it is not being executed again");
					Log.printLine();

					// NOTE: If a Cloudlet has finished, then it won't be processed.
					// So, if ack is required, this method sends back a result.
					// If ack is not required, this method don't send back a result.
					// Hence, this might cause CloudSim to be hanged since waiting
					// for this Cloudlet back.
					
					
					//maplistCloudlettoVm(cl.getVmId(), cl.getCloudletId());
					if (ack) {
						int[] data = new int[3];
						data[0] = getId();
						data[1] = cl.getCloudletId();
						data[2] = CloudSimTags.FALSE;

						// unique tag = operation tag
						int tag = CloudSimTags.CLOUDLET_SUBMIT_ACK;
						sendNow(cl.getUserId(), tag, data);
					}
					
					sendNow(cl.getUserId(), CloudSimTags.CLOUDLET_RETURN, cl);

					return;
				}
						
				// process this Cloudlet to this CloudResource
				cl.setResourceParameter(getId(), getCharacteristics().getCostPerSecond(), getCharacteristics()
						.getCostPerBw());

				int userId = cl.getUserId();
				int vmId = cl.getVmId();
				double cletUtilization;
				int cletId = cl.getCloudletId();
				if(RunTimeConstants.test == true){
					System.out.println("Cloudlet ID is # "+cletId+ " and length is " +cl.getCloudletLength());				
				}
				// time to transfer the files
				double fileTransferTime = predictFileTransferTime(cl.getRequiredFiles());

				Host host = getVmAllocationPolicy().getHost(vmId, userId);
				Vm vm = host.getVm(vmId, userId);
				
				CloudletScheduler scheduler = vm.getCloudletScheduler();
				double estimatedFinishTime = scheduler.cloudletSubmit(cl, fileTransferTime);

				// if this cloudlet is in the exec queue
				if (estimatedFinishTime > 0.0 && !Double.isInfinite(estimatedFinishTime)) {
					estimatedFinishTime += fileTransferTime;
					send(getId(), estimatedFinishTime, CloudSimTags.VM_DATACENTER_EVENT);
				}
				cletUtilization = cletNorm.getNormalizedLength(cl);
				//maplistCloudlettoVm(vmId, cletId);
				ReliabilityCalculator(host, vm, cletUtilization);

				if (ack) {
					int[] data = new int[3];
					data[0] = getId();
					data[1] = cl.getCloudletId();
					data[2] = CloudSimTags.TRUE;

					// unique tag = operation tag
					int tag = CloudSimTags.CLOUDLET_SUBMIT_ACK;
					sendNow(cl.getUserId(), tag, data);
				}
			} catch (ClassCastException c) {
				Log.printLine(getName() + ".processCloudletSubmit(): " + "ClassCastException error.");
				c.printStackTrace();
			} catch (Exception e) {
				Log.printLine(getName() + ".processCloudletSubmit(): " + "Exception error.");
				e.printStackTrace();
			}

			checkCloudletCompletion();
		}
		
		public boolean flagForNode = false;
		protected void processVmDestroy(SimEvent ev, boolean ack) {
			Vm vm = (Vm) ev.getData();			
			Host node;						
			node = vm.getHost();	
			int hostCount;
			boolean performConsolidation = true;
			List<Pe> peList = new ArrayList<Pe>();
			
			if(RunTimeConstants.test == true){
				System.out.println("VM " +vm.getId()+ " running on host " +node.getId()+ " is getting destroyed");							
				System.out.println("Core at host " +node.getId()+" is idle now" );
				if(node.getId() == 927){
					flagForNode = true;
				}
			}
			getVmAllocationPolicy().deallocateHostForVm(vm);	
			
			if (ack) {
				int[] data = new int[3];
				data[0] = getId();
				data[1] = vm.getId();
				data[2] = CloudSimTags.TRUE;

				sendNow(vm.getUserId(), CloudSimTags.VM_DESTROY_ACK, data);
			}			
			getVmList().remove(vm);		
			if(node.getVmList().isEmpty()){	
				performConsolidation = false;
				IdleTime.setHostActiveStatus(node.getId(), false);
				hostCount = IdleTime.getActiveHostCount();				
				HostCounter.setHostCount(hostCount);
				HostCounter.setClockList(CloudSim.clock());			
				peList = node.getPeList();
				boolean idleStatus;
				boolean clockStatus;
				for(int i=0; i<peList.size(); i++){
					idleStatus = IdleTime.getHostPeIdleTable(node.getId(), peList.get(i).getId());
					clockStatus = IdleTime.checkHostPeIdleClockTable(node.getId(), peList.get(i).getId());
					if(idleStatus == true && clockStatus == true){
						IdleTime.setHostPeIdleTimeTable(node.getId(), peList.get(i).getId(), CloudSim.clock());
						IdleTime.removeHostPeIdleClockTable(node.getId(), peList.get(i).getId());
					}
				}				
			}
			if(RunTimeConstants.vmConsolidationFlag == true && performConsolidation == true){
				if(RunTimeConstants.faultToleranceMechanism == 3 || (RunTimeConstants.faultToleranceMechanism == 2 && RunTimeConstants.failureCorrelationWithRstrChkpt == false)){
					sendNow(3, CloudSimTags.VM_CONSOLIDATION, node);
				}
				//if(RunTimeConstants.faultToleranceMechanism == 1 || RunTimeConstants.faultToleranceMechanism == 4){
				if(RunTimeConstants.faultToleranceMechanism == 4 || RunTimeConstants.faultToleranceMechanism == 1 || RunTimeConstants.failureCorrelationWithRstrChkpt == true){	
					sendNow(3, CloudSimTags.VM_CONSOLIDATION_CHECKPOINTING, node);
				}
			}
			/*
			if(RunTimeConstants.vmConsolidationFlag == true && performConsolidation == true && (RunTimeConstants.faultToleranceMechanism !=4 || RunTimeConstants.faultToleranceMechanism !=1)){
				//processVMConsolidation(node);
				sendNow(3, CloudSimTags.VM_CONSOLIDATION, node);
			}	
			if(RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true && performConsolidation == true){
				sendNow(3, CloudSimTags.VM_CONSOLIDATION_CHECKPOINTING, node);
			}
			*/
		}
		
		// In VM consolidation the effort is to migrate VMs to a host on which recently a VM gets destroyed. 
		// Object of that node has been passed as an argument in consolidation function
		int testCount = 0;
		public int cletCounter = 0;
		public HashMap<Integer, Double>lastVmConsolidationClock = new HashMap<Integer, Double>();
		boolean consolidationHappening = true;
		public void processVMConsolidation(Host node){		
			if(RunTimeConstants.test == true){
				if(node.isFailed()){
					System.out.println(" Node # " +node.getId()+ " is in failed state. So consolidation will not take place");
				}
				else{
					currentPredictionList = new ArrayList<Double>();			
					currentPredictionList = currentPrediction.get(node.getId());						
					boolean hazardRate_Flag = false;
					boolean consolidationHappened_Flag = false;
					ArrayList<Integer>correlatedProvisionedNodes = new ArrayList<Integer>();
					if(currentPredictionList == null || currentPredictionList.isEmpty()){
						System.out.println("Failure prediction information for Node #" +node.getId()+ " is not available.");
						System.out.println("So consolidation is happening using Maximum Hazard Rate criteria");					
						hazardRate_Flag = true;
						}
					else{
						System.out.println("VM consolidation is happening on the basis of failure prediction");
					}
				//	while(consolidationHappening == true){
					if(hazardRate_Flag == true){					
						int ftaNodeID;
						double hazardRateNode = 0.0;						
						ftaNodeID = HostMapping.getFTAhostID(node.getId());
						if(RunTimeConstants.traceType.equals("LANL")){
							hazardRateNode = freadLANL.getMaxHazardRate(ftaNodeID);
						}
						if(RunTimeConstants.traceType.equals("Grid5000")){
							hazardRateNode = freadGrid5000.getMaxHazardRate(ftaNodeID);
						}
						int vmCount;
						boolean consolidationFlag = false;					
						vmCount = node.getVmList().size();
						//In this if condition, VMs are moving out from the host on which a VM got destroyed recently to some other host 					
						if(consolidationFlag == false)
						{						
							vmMigrationList = new ArrayList<Vm>();
							for(int i=0;i<vmCount; i++){
								vmMigrationList.add(node.getVmList().get(i));
							}
							if(RunTimeConstants.failureCorrelationConsolidation == true){												
								String nodeLocation;										
								ArrayList<Integer>correlatedNodes = new ArrayList<Integer>();	
								int provisionedFtaNodeID;
								nodeLocation = freadGrid5000.getClusterNameForNode(ftaNodeID);
								correlatedNodes.addAll(kMeans.getCorrelatedNodes(nodeLocation, ftaNodeID));
								for(int i=0; i<correlatedNodes.size(); i++){
									if(correlatedNodes.get(i).equals(ftaNodeID)){
										correlatedNodes.remove(i);
										break;
									}
								}								
								for(int i=0; i<correlatedNodes.size(); i++){
									for(int j=0; j<getHostList().size(); j++){
										provisionedFtaNodeID = HostMapping.getFTAhostID(getHostList().get(j).getId());
										if(provisionedFtaNodeID == correlatedNodes.get(i)){
											correlatedProvisionedNodes.add(provisionedFtaNodeID);
										}
									}								
								}
							}
							for(int i=0; i<getHostList().size(); i++){
								Host host;
								int freePeCount;
								int vmCountHost;
								int ftaHostID;
								double hazardRateHost = 0.0;
								boolean flagLocal = false;
								host = getHostList().get(i);
								if(host == node){
									continue;
								}
								if(RunTimeConstants.failureCorrelationConsolidation == true){
									int hostFtaID;
									hostFtaID = HostMapping.getFTAhostID(host.getId());
									for(int j=0; j<correlatedProvisionedNodes.size(); j++){										
										if(hostFtaID == correlatedProvisionedNodes.get(j)){
											flagLocal = true;
											break;
										}
									}
									if(flagLocal == true){
										continue;
									}									
								}
								if(IdleTime.expectedFailingHost.containsKey(host.getId())||IdleTime.checkHostActiveStatus(host.getId())==false){
									continue;
								}
								vmCountHost = host.getVmList().size();
								freePeCount = host.getNumberOfPes()-vmCountHost;
								if(freePeCount >= vmCount){																
									ftaHostID = HostMapping.getFTAhostID(host.getId());
									if(RunTimeConstants.traceType.equals("LANL")){
										hazardRateHost = freadLANL.getMaxHazardRate(ftaHostID);
									}
									if(RunTimeConstants.traceType.equals("Grid5000")){
										hazardRateHost = freadGrid5000.getMaxHazardRate(ftaHostID);
									}								
									if(hazardRateHost<hazardRateNode){
										Vm vm;
										consolidationFlag = true;
										ArrayList<Integer>cletIDList;
										for(int j=0; j<vmCount; j++){	
											int cletIDListSize;										
											vm = vmMigrationList.get(j);						
											if(lastVmConsolidationClock.containsKey(vm.getId())){
												if(lastVmConsolidationClock.get(vm.getId()).equals(CloudSim.clock())){
													System.out.println("No progress has been made by VM " +vm.getId()+ " since last consolidation");
													System.out.println("So VM " +vm.getId()+ " is not getting consolidated");
													continue;
												}
											}
											cletIDList = new ArrayList<Integer>();
											cletIDListSize = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().size();
											for(int k=0; k<cletIDListSize; k++){
												cletIDList.add(((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getCloudletId());
											}	
											for(int k=0; k<cletIDListSize; k++){											
												long soFarClet;
												long length = 0;								
												long remainingLength;		
												long lastRemainingLength;										
												remainingLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();								
												length = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getCloudletLength();											
											/**
											if(cletLastRemainingMap.isEmpty()){
												lastRemainingLength = length;
												soFarClet = length - remainingLength;
												cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
												cletSoFarMap.put(cletIDList.get(k), soFarClet);
											}else{
												if(cletLastRemainingMap.containsKey(cletIDList.get(k))){
													lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
													soFarClet = lastRemainingLength - remainingLength;
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);
												}
												else{
													lastRemainingLength = length;
													soFarClet = length - remainingLength;
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);
												}												
											}							
											*/
												if(cletSoFarBeforeFailing.containsKey(cletIDList.get(k))){
													soFarClet = length - remainingLength;												
													lastRemainingLength = length;
													//	cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);
													soFarClet = soFarClet + cletSoFarBeforeFailing.get(cletIDList.get(k)) ;
													System.out.println(CloudSim.clock()+ " Sofar execution of Cloudlet " +cletIDList.get(k)+ " on node "+node.getId()+" after failure overheads is " +soFarClet);										
													cletSoFarBeforeFailing.remove(cletIDList.get(k));
												}								
												else{
													lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
													soFarClet = lastRemainingLength - remainingLength;										
													//	cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);												
												}								
											
												vmRecord.setCletMigrationHostRecord(cletIDList.get(k), node.getId());								
											
												System.out.println("Cloudlet " +cletIDList.get(k)+" is consolidating from node " +node.getId());
												System.out.println("Length of Cloudlet "+cletIDList.get(k)+ " is " +length);
												System.out.println("Current Remaining Length of Cloudlet "+cletIDList.get(k)+ " is " +remainingLength);
												System.out.println("Last Remaining Length of Cloudlet "+cletIDList.get(k)+ " is " +lastRemainingLength);
												System.out.println("Sofar execution of Cloudlet " +cletIDList.get(k)+ " on node "+node.getId()+" is " +soFarClet);
											
												vmRecord.setVmExecutionDurationPerHostRecord(cletIDList.get(k), soFarClet);
												cletMigrated.put(cletIDList.get(k), true);
											}
											lastVmConsolidationClock.put(vm.getId(), CloudSim.clock());										
										
											List<Pe> peList = new ArrayList<Pe>();
											peList = node.getVmScheduler().getPesAllocatedForVM(vm);
											for(int k=0; k<peList.size(); k++){
												int peID;
												peID = peList.get(k).getId();
												IdleTime.setHostPeIdleTable(node.getId(), peID, true);
												IdleTime.setHostPeIdleClockTable(node.getId(), peID, CloudSim.clock());
											}								
										
											getVmAllocationPolicy().deallocateHostForVm(vm);
											//node.removeMigratingInVm(vm);								
											vm.setInMigration(true);										
											boolean result = getVmAllocationPolicy().allocateHostForVm(vm, host);
											if (!result) {
												Log.printLine("[Datacenter.processVmMigrate] VM allocation to the destination host failed");
												//System.exit(0);
												break;
											}
											Log.formatLine("%.2f: Consolidation of VM #%d to Host #%d is completed", CloudSim.clock(), vm.getId(), vm.getHost().getId());
											vm.setInMigration(false);										
											consolidationCount = consolidationCount + 1;
											vmRecord.setVmCurrentHost(vm.getId(), vm.getHost().getId());
										
											//Applying VM migration overheads on all the running cloudlets running on each VM on the given node.	
											double migrationOverhead;	
											double migrationDownTime;
										//mgOver.TotalMigrationTime(vm);
										//mgOver.TotalDownTime(vm);						
											migrationOverhead = mgOver.getMigrationOverhead(vm);
											migrationDownTime = mgOver.TotalDownTime(vm);
										//Host newHost;
										//newHost = vm.getHost();
											for(int k=0; k<cletIDListSize; k++){
												long remainingCletLength;
												double cletUtilization;	
												long soFarExecutedLength;
												long cletNewLength;
												long oldBackupLength;
												Cloudlet clet;																				
												soFarExecutedLength = cletSoFarMap.get(cletIDList.get(k));
												clet = broker.getCloudletFromID(cletIDList.get(k));
												cletNewLength = clet.getCloudletLength() + ((long)(migrationOverhead*1000)) +((long)(migrationDownTime*1000));											
												if(!cletLengthBackup.containsKey(cletIDList.get(k))){
													oldBackupLength = clet.getCloudletLength(); 
													oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
													cletLengthBackup.put(cletIDList.get(k), oldBackupLength);
												}
												else{
													oldBackupLength = cletLengthBackup.get(cletIDList.get(k)); 
													oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
													cletLengthBackup.put(cletIDList.get(k), oldBackupLength);
												}
												clet.setCloudletLength(cletNewLength);
												remainingCletLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();
												cletLastRemainingMap.put(cletIDList.get(k), remainingCletLength);
												MigrationOverheadTable.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
												DownTimeTable.setCloudletDownTimeforMigration(clet.getCloudletId(), migrationDownTime);
												migrationForCloudlets.put(cletIDList.get(k), soFarExecutedLength);							
												cletUtilization = cletNorm.getNormalizedLength(clet);
												ReliabilityCalculator(host, vm, cletUtilization);
												vmRecord.setCletMigrationHostRecordforDownTime(clet.getCloudletId(), host.getId());
												vmRecord.setCletMigrationDownTime(clet.getCloudletId(), migrationDownTime);	
												vmRecord.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
											}									
										}		
										List<Pe>peListNode = new ArrayList<Pe>();
										List<Integer>peIDListNode = new ArrayList<Integer>();
										peListNode = node.getPeList();
										boolean idleStatus;
										boolean clockStatus;
										int hostCount;
										for(int k=0; k<peListNode.size(); k++){
											peIDListNode.add(peListNode.get(k).getId());
											idleStatus = IdleTime.getHostPeIdleTable(node.getId(), peListNode.get(k).getId());
											clockStatus = IdleTime.checkHostPeIdleClockTable(node.getId(), peListNode.get(k).getId());
											if(idleStatus==true && clockStatus == true){					
												IdleTime.setHostPeIdleTimeTable(node.getId(), peListNode.get(k).getId(), CloudSim.clock());
												IdleTime.removeHostPeIdleClockTable(node.getId(), peListNode.get(k).getId());
											}
										}
										IdleTime.setHostActiveStatus(node.getId(), false, peIDListNode);
										hostCount = IdleTime.getActiveHostCount();
										HostCounter.setHostCount(hostCount);
										HostCounter.setClockList(CloudSim.clock());
										consolidationHappened_Flag = true;
									//	consolidationHappening = false;
										break;
									}
								}					
							}
						}					
						// In this if condition, VMs from other host are moving in to the host where a VM got destroyed recently
						if(consolidationFlag == false){				
							if(RunTimeConstants.failureCorrelationConsolidation == true){												
								String nodeLocation;										
								ArrayList<Integer>correlatedNodes = new ArrayList<Integer>();	
								int provisionedFtaNodeID;
								nodeLocation = freadGrid5000.getClusterNameForNode(ftaNodeID);
								correlatedNodes.addAll(kMeans.getCorrelatedNodes(nodeLocation, ftaNodeID));
								for(int i=0; i<correlatedNodes.size(); i++){
									if(correlatedNodes.get(i).equals(ftaNodeID)){
										correlatedNodes.remove(i);
										break;
									}
								}	
								for(int i=0; i<correlatedNodes.size(); i++){
									for(int j=0; j<getHostList().size(); j++){
										provisionedFtaNodeID = HostMapping.getFTAhostID(getHostList().get(j).getId());
										if(provisionedFtaNodeID == correlatedNodes.get(i)){
											correlatedProvisionedNodes.add(provisionedFtaNodeID);
										}
									}								
								}
							}
							for(int i=0; i<getHostList().size(); i++){
								Host host;
								int freePeCount;	
								int vmCountHost;
								int ftaHostID;
								boolean flagLocal = false;
								double hazardRateHost = 0.0;
								host = getHostList().get(i);							
								if(host == node){
									continue;
								}					
								if(RunTimeConstants.failureCorrelationConsolidation == true){
									int hostFtaID;
									hostFtaID = HostMapping.getFTAhostID(host.getId());
									for(int j=0; j<correlatedProvisionedNodes.size(); j++){										
										if(hostFtaID == correlatedProvisionedNodes.get(j)){
											flagLocal = true;
											break;
										}
									}
									if(flagLocal == true){
										continue;
									}									
								}
								vmCountHost = host.getVmList().size();						
								if(vmCountHost == 0){
									continue;
								}							
								freePeCount = node.getNumberOfPes()-vmCount;							
								if(vmCountHost <= freePeCount){
									ftaHostID = HostMapping.getFTAhostID(host.getId());
									if(RunTimeConstants.traceType.equals("LANL")){
										hazardRateHost = freadLANL.getMaxHazardRate(ftaHostID);
									}
									if(RunTimeConstants.traceType.equals("Grid5000")){
										hazardRateHost = freadGrid5000.getMaxHazardRate(ftaHostID);
									}								
									if(hazardRateHost>hazardRateNode){
										Vm vm;
										consolidationFlag = true;
										vmMigrationList = new ArrayList<Vm>();
										for(int j=0;j<vmCountHost; j++){
											vmMigrationList.add(host.getVmList().get(j));
										}
										ArrayList<Integer>cletIDList;
										for(int j=0; j<vmCountHost; j++){										
											int cletIDListSize;										
											vm = vmMigrationList.get(j);
											if(lastVmConsolidationClock.containsKey(vm.getId())){
												if(lastVmConsolidationClock.get(vm.getId()).equals(CloudSim.clock())){
													System.out.println("No progress has been made by VM " +vm.getId()+ " since last consolidation");
													System.out.println("So VM " +vm.getId()+ " is not getting consolidated");
													continue;
												}
											}
											cletIDList = new ArrayList<Integer>();
											cletIDListSize = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().size();
											for(int k=0; k<cletIDListSize; k++){
												cletIDList.add(((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getCloudletId());
											}	
											for(int k=0; k<cletIDListSize; k++){											
												long soFarClet;
												long length = 0;								
												long remainingLength;			
												long lastRemainingLength;											
												remainingLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();								
												length = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getCloudletLength();											
											/*
											if(cletLastRemainingMap.isEmpty()){
												lastRemainingLength = length;
												soFarClet = length - remainingLength;
												cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
												cletSoFarMap.put(cletIDList.get(k), soFarClet);
											}else{
												if(cletLastRemainingMap.containsKey(cletIDList.get(k))){																										
													lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
													soFarClet = lastRemainingLength - remainingLength;
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);
												}
												else{													
													lastRemainingLength = length;
													soFarClet = length - remainingLength;
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);
												}												
											}	
											*/
												if(cletSoFarBeforeFailing.containsKey(cletIDList.get(k))){
													soFarClet = length - remainingLength;												
													lastRemainingLength = length;
													//	cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);
													soFarClet = soFarClet + cletSoFarBeforeFailing.get(cletIDList.get(k)) ;
													System.out.println(CloudSim.clock()+ " Sofar execution of Cloudlet " +cletIDList.get(k)+ " on node "+node.getId()+" after failure overheads is " +soFarClet);										
													cletSoFarBeforeFailing.remove(cletIDList.get(k));
												}								
												else{
													lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
													soFarClet = lastRemainingLength - remainingLength;										
													//	cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);												
												}	
											
												vmRecord.setCletMigrationHostRecord(cletIDList.get(k), host.getId());
											
												System.out.println("Cloudlet " +cletIDList.get(k)+" is migrating from node " +host.getId());
												System.out.println("Length of Cloudlet "+cletIDList.get(k)+ " is " +length);
												System.out.println("Current Remaining Length of Cloudlet "+cletIDList.get(k)+ " is " +remainingLength);
												System.out.println("Last Remaining Length of Cloudlet "+cletIDList.get(k)+ " is " +lastRemainingLength);
												System.out.println("Sofar execution of Cloudlet " +cletIDList.get(k)+ " on node "+host.getId()+" is " +soFarClet);
											
												vmRecord.setVmExecutionDurationPerHostRecord(cletIDList.get(k), soFarClet);
												cletMigrated.put(cletIDList.get(k), true);
											}
											lastVmConsolidationClock.put(vm.getId(), CloudSim.clock());
										
											List<Pe>peList = new ArrayList<Pe>();
											peList = host.getVmScheduler().getPesAllocatedForVM(vm);
											for(int k=0; k<peList.size(); k++){
												int peID;
												peID = peList.get(k).getId();
												IdleTime.setHostPeIdleTable(host.getId(), peID, true);
												IdleTime.setHostPeIdleClockTable(host.getId(), peID, CloudSim.clock());
											}
										
											getVmAllocationPolicy().deallocateHostForVm(vm);
											//host.removeMigratingInVm(vm);
											vm.setInMigration(true);									
											boolean result = getVmAllocationPolicy().allocateHostForVm(vm, node);
											if (!result) {
												Log.printLine("[Datacenter.processVmMigrate] VM allocation to the destination host failed");
												System.exit(0);
											}
											Log.formatLine("%.2f: Consolidation of VM #%d to Host #%d is completed", CloudSim.clock(), vm.getId(), vm.getHost().getId());										
											vm.setInMigration(false);
											consolidationCount = consolidationCount + 1;
											vmRecord.setVmCurrentHost(vm.getId(), vm.getHost().getId());
									/**	
										List<Pe>peListHost = new ArrayList<Pe>();
										List<Integer>peIDListHost = new ArrayList<Integer>();
										peListHost = host.getPeList();
										for(int k=0; k<peListHost.size(); k++){
											peIDListHost.add(peListHost.get(k).getId());
										}
										IdleTime.setHostActiveStatus(host.getId(), false, peIDListHost);
									*/	
										//Applying VM migration overheads on all the running cloudlets running on each VM on the given node.	
											double migrationOverhead;			
											double migrationDownTime;															
											migrationOverhead = mgOver.getMigrationOverhead(vm);
											migrationDownTime = mgOver.TotalDownTime(vm);
											//Host newHost;
											//newHost = vm.getHost();
											for(int k=0; k<cletIDListSize; k++){
												long remainingCletLength;
												double cletUtilization;		
												Cloudlet clet;
												long cletNewLength;
												long soFarExecutedLength;			
												long oldBackupLength;
												soFarExecutedLength = cletSoFarMap.get(cletIDList.get(k));
												clet = broker.getCloudletFromID(cletIDList.get(k));
												cletNewLength = clet.getCloudletLength() + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));							
											
												if(!cletLengthBackup.containsKey(cletIDList.get(k))){
													oldBackupLength = clet.getCloudletLength(); 
													oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
													cletLengthBackup.put(cletIDList.get(k), oldBackupLength);
													}
												else{
													oldBackupLength = cletLengthBackup.get(cletIDList.get(k)); 
													oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
													cletLengthBackup.put(cletIDList.get(k), oldBackupLength);
												}
											
												clet.setCloudletLength(cletNewLength);
												remainingCletLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();									
												cletLastRemainingMap.put(cletIDList.get(k), remainingCletLength);							
												MigrationOverheadTable.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
												DownTimeTable.setCloudletDownTimeforMigration(clet.getCloudletId(), migrationDownTime);
												migrationForCloudlets.put(cletIDList.get(k), soFarExecutedLength);							
												cletUtilization = cletNorm.getNormalizedLength(clet);
												ReliabilityCalculator(node, vm, cletUtilization);
												vmRecord.setCletMigrationHostRecordforDownTime(clet.getCloudletId(), node.getId());
												vmRecord.setCletMigrationDownTime(clet.getCloudletId(), migrationDownTime);	
												vmRecord.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
											}										
										}
										List<Pe>peListNode = new ArrayList<Pe>();
										List<Integer>peIDListNode = new ArrayList<Integer>();
										peListNode = host.getPeList();
										boolean idleStatus;
										boolean clockStatus;
										int hostCount;
										for(int k=0; k<peListNode.size(); k++){
											peIDListNode.add(peListNode.get(k).getId());
											idleStatus = IdleTime.getHostPeIdleTable(host.getId(), peListNode.get(k).getId());
											clockStatus = IdleTime.checkHostPeIdleClockTable(host.getId(), peListNode.get(k).getId());
											if(idleStatus==true && clockStatus == true){					
												IdleTime.setHostPeIdleTimeTable(host.getId(), peListNode.get(k).getId(), CloudSim.clock());
												IdleTime.removeHostPeIdleClockTable(host.getId(), peListNode.get(k).getId());
											}
										}
										IdleTime.setHostActiveStatus(host.getId(), false, peIDListNode);
										hostCount = IdleTime.getActiveHostCount();
										HostCounter.setHostCount(hostCount);
										HostCounter.setClockList(CloudSim.clock());								
										consolidationHappened_Flag = true;
										//consolidationHappening = false;
										break;
									}
								}					
							}
						}			
					}
					else{					
						double nextFailurePredictionNode = currentPredictionList.get(0);
						int vmCount;
						boolean consolidationFlag = false;
						vmCount = node.getVmList().size();
						int ftaNodeID;
						ftaNodeID = HostMapping.getFTAhostID(node.getId());
						//In this if condition, VMs are moving out from the host on which a VM got destroyed recently to some other host 
						if(consolidationFlag == false)
						{
							vmMigrationList = new ArrayList<Vm>();
							for(int i=0;i<vmCount; i++){
								vmMigrationList.add(node.getVmList().get(i));
							}
							
							if(RunTimeConstants.failureCorrelationConsolidation == true){												
								String nodeLocation;										
								ArrayList<Integer>correlatedNodes = new ArrayList<Integer>();	
								int provisionedFtaNodeID;
								nodeLocation = freadGrid5000.getClusterNameForNode(ftaNodeID);
								correlatedNodes.addAll(kMeans.getCorrelatedNodes(nodeLocation, ftaNodeID));
								for(int i=0; i<correlatedNodes.size(); i++){
									if(correlatedNodes.get(i).equals(ftaNodeID)){
										correlatedNodes.remove(i);
										break;
									}
								}	
								for(int i=0; i<correlatedNodes.size(); i++){
									for(int j=0; j<getHostList().size(); j++){
										provisionedFtaNodeID = HostMapping.getFTAhostID(getHostList().get(j).getId());
										if(provisionedFtaNodeID == correlatedNodes.get(i)){
											correlatedProvisionedNodes.add(provisionedFtaNodeID);
										}
									}								
								}
							}
							for(int i=0; i<getHostList().size(); i++){
								Host host;
								int freePeCount;
								int vmCountHost;
								boolean flagLocal = false;
								host = getHostList().get(i);
								if(host == node){
									continue;
								}
								if(RunTimeConstants.failureCorrelationConsolidation == true){
									int hostFtaID;
									hostFtaID = HostMapping.getFTAhostID(host.getId());
									for(int j=0; j<correlatedProvisionedNodes.size(); j++){										
										if(hostFtaID == correlatedProvisionedNodes.get(j)){
											flagLocal = true;
											break;
										}
									}
									if(flagLocal == true){
										continue;
									}									
								}
								if(IdleTime.checkExpectedFailingHost(host.getId())==true || IdleTime.checkHostActiveStatus(host.getId())==false){
									continue;
								}
								if(flagForNode == true){
									System.out.println("Check");
								}
								currentPredictionList = new ArrayList<Double>();
								currentPredictionList = currentPrediction.get(host.getId());								
								if(currentPredictionList == null || currentPredictionList.isEmpty()){
									continue;
								}
								vmCountHost = host.getVmList().size();							
								freePeCount = host.getNumberOfPes()-vmCountHost;
								if(freePeCount >= vmCount){								
									//currentPredictionList = new ArrayList<Double>();
									//currentPredictionList = currentPrediction.get(host.getId());
									//if(currentPredictionList.get(0)==null){
									//	System.out.println("Check");
									//}
									double nextFailurePredictionHost = currentPredictionList.get(0);								
									if(nextFailurePredictionHost>nextFailurePredictionNode){
										Vm vm;
										consolidationFlag = true;
										ArrayList<Integer>cletIDList;
										for(int j=0; j<vmCount; j++){
											int cletIDListSize;	
											vm = vmMigrationList.get(j);
											if(lastVmConsolidationClock.containsKey(vm.getId())){
												if(lastVmConsolidationClock.get(vm.getId()).equals(CloudSim.clock())){
													System.out.println("No progress has been made by VM " +vm.getId()+ " since last consolidation");
													System.out.println("So VM " +vm.getId()+ " is not getting consolidated");
													continue;
												}
											}
											cletIDList = new ArrayList<Integer>();
											cletIDListSize = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().size();
											for(int k=0; k<cletIDListSize; k++){
												cletIDList.add(((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getCloudletId());
											}									
											for(int k=0; k<cletIDListSize; k++){	
												if(cletIDList.get(k)==1631){
													System.out.println("Check");
												}
												long soFarClet;
												long length = 0;								
												long remainingLength;	
												long lastRemainingLength;											
												remainingLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();
												length = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getCloudletLength();											
											/**
											if(cletLastRemainingMap.isEmpty()){
												lastRemainingLength = length;
												soFarClet = length - remainingLength;
												cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
												cletSoFarMap.put(cletIDList.get(k), soFarClet);
											}else{
												if(cletLastRemainingMap.containsKey(cletIDList.get(k))){																									
													lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
													soFarClet = lastRemainingLength - remainingLength;
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);
												}
												else{													
													lastRemainingLength = length;
													soFarClet = length - remainingLength;
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);
												}												
											}				
											*/
												if(cletSoFarBeforeFailing.containsKey(cletIDList.get(k))){
													soFarClet = length - remainingLength;												
													lastRemainingLength = length;
													//	cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);
													soFarClet = soFarClet + cletSoFarBeforeFailing.get(cletIDList.get(k)) ;
													System.out.println(CloudSim.clock()+ " Sofar execution of Cloudlet " +cletIDList.get(k)+ " on node "+node.getId()+" after failure overheads is " +soFarClet);										
													cletSoFarBeforeFailing.remove(cletIDList.get(k));
												}								
												else{
													lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
													soFarClet = lastRemainingLength - remainingLength;										
													//	cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);												
												}	
											
												vmRecord.setCletMigrationHostRecord(cletIDList.get(k), node.getId());										
											
												System.out.println("Cloudlet " +cletIDList.get(k)+" is migrating from node " +node.getId());
												System.out.println("Length of Cloudlet "+cletIDList.get(k)+ " is " +length);
												System.out.println("Current Remaining Length of Cloudlet "+cletIDList.get(k)+ " is " +remainingLength);
												System.out.println("Last Remaining Length of Cloudlet "+cletIDList.get(k)+ " is " +lastRemainingLength);
												System.out.println("Sofar execution of Cloudlet " +cletIDList.get(k)+ " on node "+node.getId()+" is " +soFarClet);
											
												vmRecord.setVmExecutionDurationPerHostRecord(cletIDList.get(k), soFarClet);
												cletMigrated.put(cletIDList.get(k), true);
											}										
											lastVmConsolidationClock.put(vm.getId(), CloudSim.clock());									
										
											List<Pe> peList = new ArrayList<Pe>();
											peList = node.getVmScheduler().getPesAllocatedForVM(vm);
											for(int k=0; k<peList.size(); k++){
												int peID;
												peID = peList.get(k).getId();
												IdleTime.setHostPeIdleTable(node.getId(), peID, true);
												IdleTime.setHostPeIdleClockTable(node.getId(), peID, CloudSim.clock());
											}	
										
											getVmAllocationPolicy().deallocateHostForVm(vm);
											//node.removeMigratingInVm(vm);
											vm.setInMigration(true);
											boolean result = getVmAllocationPolicy().allocateHostForVm(vm, host);
											if (!result) {
												Log.printLine("[Datacenter.processVmMigrate] VM allocation to the destination host failed");
												//System.exit(0);
												break;
											}
											Log.formatLine("%.2f: Consolidation of VM #%d to Host #%d is completed", CloudSim.clock(), vm.getId(), vm.getHost().getId());
											vm.setInMigration(false);			
											consolidationCount = consolidationCount + 1;
											vmRecord.setVmCurrentHost(vm.getId(), vm.getHost().getId());	
										/**
										List<Pe>peListNode = new ArrayList<Pe>();
										List<Integer>peIDListNode = new ArrayList<Integer>();
										peListNode = node.getPeList();
										for(int k=0; k<peListNode.size(); k++){
											peIDListNode.add(peListNode.get(k).getId());
										}
										IdleTime.setHostActiveStatus(node.getId(), false, peIDListNode);										
										*/
										//Applying VM migration overheads on all the running cloudlets running on each VM on the given node.	
											double migrationOverhead;	
											double migrationDownTime;
											//mgOver.TotalMigrationTime(vm);
											//mgOver.TotalDownTime(vm);						
											migrationOverhead = mgOver.getMigrationOverhead(vm);		
											migrationDownTime = mgOver.TotalDownTime(vm);
											//	Host newHost;
											//	newHost = vm.getHost();
											for(int k=0; k<cletIDListSize; k++){											
												long remainingCletLength;											
												double cletUtilization;
												long soFarExecutedLength;											
												long cletNewLength;
												long oldBackupLength;
												Cloudlet clet;
												soFarExecutedLength = cletSoFarMap.get(cletIDList.get(k));
												clet = broker.getCloudletFromID(cletIDList.get(k));
												cletNewLength = clet.getCloudletLength() + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));	
											
												if(!cletLengthBackup.containsKey(cletIDList.get(k))){
													oldBackupLength = clet.getCloudletLength(); 
													oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
													cletLengthBackup.put(cletIDList.get(k), oldBackupLength);
												}
												else{
													oldBackupLength = cletLengthBackup.get(cletIDList.get(k)); 
													oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
													cletLengthBackup.put(cletIDList.get(k), oldBackupLength);
												}
											
												clet.setCloudletLength(cletNewLength);
												remainingCletLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();								
												cletLastRemainingMap.put(cletIDList.get(k), remainingCletLength);								
												MigrationOverheadTable.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
												DownTimeTable.setCloudletDownTimeforMigration(clet.getCloudletId(), migrationDownTime);										
												migrationForCloudlets.put(cletIDList.get(k), soFarExecutedLength);							
												cletUtilization = cletNorm.getNormalizedLength(clet);
												ReliabilityCalculator(host, vm, cletUtilization);
												vmRecord.setCletMigrationHostRecordforDownTime(clet.getCloudletId(), host.getId());
												vmRecord.setCletMigrationDownTime(clet.getCloudletId(), migrationDownTime);	
												vmRecord.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
											}										
										}	
										List<Pe>peListNode = new ArrayList<Pe>();
										List<Integer>peIDListNode = new ArrayList<Integer>();
										peListNode = node.getPeList();
										boolean idleStatus;
										boolean clockStatus;
										int hostCount;
										for(int k=0; k<peListNode.size(); k++){
											peIDListNode.add(peListNode.get(k).getId());
											idleStatus = IdleTime.getHostPeIdleTable(node.getId(), peListNode.get(k).getId());
											clockStatus = IdleTime.checkHostPeIdleClockTable(node.getId(), peListNode.get(k).getId());
											if(idleStatus==true && clockStatus == true){					
												IdleTime.setHostPeIdleTimeTable(node.getId(), peListNode.get(k).getId(), CloudSim.clock());
												IdleTime.removeHostPeIdleClockTable(node.getId(), peListNode.get(k).getId());
											}
										}
										IdleTime.setHostActiveStatus(node.getId(), false, peIDListNode);
										hostCount = IdleTime.getActiveHostCount();
										HostCounter.setHostCount(hostCount);
										HostCounter.setClockList(CloudSim.clock());								
										consolidationHappened_Flag = true;
										//consolidationHappening = false;
										break;
									}
								}					
							}
						}
						// In this if condition, VMs from other host are moving in to the host where a VM got destroyed recently
						if(consolidationFlag == false){
							if(RunTimeConstants.failureCorrelationConsolidation == true){												
								String nodeLocation;										
								ArrayList<Integer>correlatedNodes = new ArrayList<Integer>();	
								int provisionedFtaNodeID;
								nodeLocation = freadGrid5000.getClusterNameForNode(ftaNodeID);
								correlatedNodes.addAll(kMeans.getCorrelatedNodes(nodeLocation, ftaNodeID));
								for(int i=0; i<correlatedNodes.size(); i++){
									if(correlatedNodes.get(i).equals(ftaNodeID)){
										correlatedNodes.remove(i);
										break;
									}
								}	
								for(int i=0; i<correlatedNodes.size(); i++){
									for(int j=0; j<getHostList().size(); j++){
										provisionedFtaNodeID = HostMapping.getFTAhostID(getHostList().get(j).getId());
										if(provisionedFtaNodeID == correlatedNodes.get(i)){
											correlatedProvisionedNodes.add(provisionedFtaNodeID);
										}
									}								
								}
							}							
							for(int i=0; i<getHostList().size(); i++){
								Host host;
								int freePeCount;	
								int vmCountHost;
								boolean flagLocal = false;
								host = getHostList().get(i);
								if(host == node){
									continue;
								}							
								if(RunTimeConstants.failureCorrelationConsolidation == true){
									int hostFtaID;
									hostFtaID = HostMapping.getFTAhostID(host.getId());
									for(int j=0; j<correlatedProvisionedNodes.size(); j++){										
										if(hostFtaID == correlatedProvisionedNodes.get(j)){
											flagLocal = true;
											break;
										}
									}
									if(flagLocal == true){
										continue;
									}									
								}
								vmCountHost = host.getVmList().size();
								if(vmCountHost == 0){
									continue;
								}			
								currentPredictionList = new ArrayList<Double>();
								currentPredictionList = currentPrediction.get(host.getId());
								if(currentPredictionList == null || currentPredictionList.isEmpty()){
									continue;
								}
								freePeCount = node.getNumberOfPes()-vmCount;							
								if(vmCountHost <= freePeCount){								
								//	if(currentPredictionList.get(0)==null){
								//		System.out.println("Check");
								//	}
									double nextFailurePredictionHost = currentPredictionList.get(0);
									if(nextFailurePredictionHost<nextFailurePredictionNode){
										Vm vm;
										consolidationFlag = true;
										vmMigrationList = new ArrayList<Vm>();
										ArrayList<Integer>cletIDList;
										for(int j=0;j<vmCountHost; j++){
											vmMigrationList.add(host.getVmList().get(j));
										}
										for(int j=0; j<vmCountHost; j++){										
											int cletIDListSize;										
											vm = vmMigrationList.get(j);
											if(lastVmConsolidationClock.containsKey(vm.getId())){
												if(lastVmConsolidationClock.get(vm.getId()).equals(CloudSim.clock())){
													System.out.println("No progress has been made by VM " +vm.getId()+ " since last consolidation");
													System.out.println("So VM " +vm.getId()+ " is not getting consolidated");
													continue;
												}
											}
											cletIDList = new ArrayList<Integer>();
											cletIDListSize = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().size();
											for(int k=0; k<cletIDListSize; k++){
												cletIDList.add(((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getCloudletId());
											}
											for(int k=0; k<cletIDListSize; k++){											
												long soFarClet;
												long length = 0;								
												long remainingLength;		
												long lastRemainingLength;										
												remainingLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();								
												length = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getCloudletLength();
											/*
											if(cletLastRemainingMap.isEmpty()){											
												lastRemainingLength = length;
												soFarClet = length - remainingLength;
												cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
												cletSoFarMap.put(cletIDList.get(k), soFarClet);
											}else{
												if(cletLastRemainingMap.containsKey(cletIDList.get(k))){																									
													lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
													soFarClet = lastRemainingLength - remainingLength;
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);
												}
												else{													
													lastRemainingLength = length;
													soFarClet = length - remainingLength;
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);
												}												
											}		
											*/
											
												if(cletSoFarBeforeFailing.containsKey(cletIDList.get(k))){
													soFarClet = length - remainingLength;												
													lastRemainingLength = length;
													//	cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);
													soFarClet = soFarClet + cletSoFarBeforeFailing.get(cletIDList.get(k));
													System.out.println(CloudSim.clock()+ " Sofar execution of Cloudlet " +cletIDList.get(k)+ " on node "+node.getId()+" after failure overheads is " +soFarClet);										
													cletSoFarBeforeFailing.remove(cletIDList.get(k));
												}								
												else{
													lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
													soFarClet = lastRemainingLength - remainingLength;										
													//	cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);												
												}							
											
												vmRecord.setCletMigrationHostRecord(cletIDList.get(k), host.getId());									
											
												System.out.println("Cloudlet " +cletIDList.get(k)+" is migrating from node " +host.getId());
												System.out.println("Length of Cloudlet "+cletIDList.get(k)+ " is " +length);
												System.out.println("Current Remaining Length of Cloudlet "+cletIDList.get(k)+ " is " +remainingLength);
												System.out.println("Last Remaining Length of Cloudlet "+cletIDList.get(k)+ " is " +lastRemainingLength);
												System.out.println("Sofar execution of Cloudlet " +cletIDList.get(k)+ " on node "+host.getId()+" is " +soFarClet);
											
												vmRecord.setVmExecutionDurationPerHostRecord(cletIDList.get(k), soFarClet);
												cletMigrated.put(cletIDList.get(k), true);
											}										
											lastVmConsolidationClock.put(vm.getId(), CloudSim.clock());										
										
											List<Pe> peList = new ArrayList<Pe>();
											peList = host.getVmScheduler().getPesAllocatedForVM(vm);
											for(int k=0; k<peList.size(); k++){
												int peID;
												peID = peList.get(k).getId();
												IdleTime.setHostPeIdleTable(host.getId(), peID, true);
												IdleTime.setHostPeIdleClockTable(host.getId(), peID, CloudSim.clock());
											}	
										
											getVmAllocationPolicy().deallocateHostForVm(vm);
											//host.removeMigratingInVm(vm);
											vm.setInMigration(true);
											boolean result = getVmAllocationPolicy().allocateHostForVm(vm, node);
											if (!result) {
												Log.printLine("[Datacenter.processVmMigrate] VM allocation to the destination host failed");
												System.exit(0);
											}
											Log.formatLine("%.2f: Consolidation of VM #%d to Host #%d is completed", CloudSim.clock(), vm.getId(), vm.getHost().getId());
											vm.setInMigration(false);
											consolidationCount = consolidationCount + 1;	
											vmRecord.setVmCurrentHost(vm.getId(), vm.getHost().getId());
										
										/*
										List<Pe>peListHost = new ArrayList<Pe>();
										List<Integer>peIDListHost = new ArrayList<Integer>();
										peListHost = host.getPeList();
										for(int k=0; k<peListHost.size(); k++){
											peIDListHost.add(peListHost.get(k).getId());
										}
										IdleTime.setHostActiveStatus(host.getId(), false, peIDListHost);										
										*/
										
										//Applying VM migration overheads on all the running cloudlets running on each VM on the given node.	
											double migrationOverhead;									
											double migrationDownTime;															
											migrationOverhead = mgOver.getMigrationOverhead(vm);
											migrationDownTime = mgOver.TotalDownTime(vm);
										//Host newHost;
										//newHost = vm.getHost();
											for(int k=0; k<cletIDListSize; k++){										
												long remainingCletLength;
												double cletUtilization;											
												long soFarExecutedLength;											
												long cletNewLength;
												long oldBackupLength;
												Cloudlet clet;
												soFarExecutedLength = cletSoFarMap.get(cletIDList.get(k));
												clet = broker.getCloudletFromID(cletIDList.get(k));
												cletNewLength = clet.getCloudletLength() + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));											
												if(!cletLengthBackup.containsKey(cletIDList.get(k))){
													oldBackupLength = clet.getCloudletLength(); 
													oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
													cletLengthBackup.put(cletIDList.get(k), oldBackupLength);
												}
												else{
													oldBackupLength = cletLengthBackup.get(cletIDList.get(k)); 
													oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
													cletLengthBackup.put(cletIDList.get(k), oldBackupLength);
												}		
												clet.setCloudletLength(cletNewLength);	
												remainingCletLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();								
												cletLastRemainingMap.put(cletIDList.get(k), remainingCletLength);							
												MigrationOverheadTable.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
												DownTimeTable.setCloudletDownTimeforMigration(clet.getCloudletId(), migrationDownTime);										
												migrationForCloudlets.put(cletIDList.get(k), soFarExecutedLength);							
												cletUtilization = cletNorm.getNormalizedLength(clet);
												ReliabilityCalculator(node, vm, cletUtilization);
												vmRecord.setCletMigrationHostRecordforDownTime(clet.getCloudletId(), node.getId());
												vmRecord.setCletMigrationDownTime(clet.getCloudletId(), migrationDownTime);	
												vmRecord.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
											}									
										}	
										List<Pe>peListNode = new ArrayList<Pe>();
										List<Integer>peIDListNode = new ArrayList<Integer>();
										peListNode = host.getPeList();
										boolean idleStatus;
										boolean clockStatus;
										int hostCount;
										for(int k=0; k<peListNode.size(); k++){
											peIDListNode.add(peListNode.get(k).getId());
											idleStatus = IdleTime.getHostPeIdleTable(host.getId(), peListNode.get(k).getId());
											clockStatus = IdleTime.checkHostPeIdleClockTable(host.getId(), peListNode.get(k).getId());
											if(idleStatus==true && clockStatus == true){					
												IdleTime.setHostPeIdleTimeTable(host.getId(), peListNode.get(k).getId(), CloudSim.clock());
												IdleTime.removeHostPeIdleClockTable(host.getId(), peListNode.get(k).getId());
											}
										}
										IdleTime.setHostActiveStatus(host.getId(), false, peIDListNode);
										hostCount = IdleTime.getActiveHostCount();
										HostCounter.setHostCount(hostCount);
										HostCounter.setClockList(CloudSim.clock());
										consolidationHappened_Flag = true;
										//consolidationHappening = false;
										break;
									}
								}					
							}
						}			
					}
					/*
					if(consolidationHappened_Flag == false && hazardRate_Flag == false){						
							hazardRate_Flag = true;						
					}
					else{
						if(consolidationHappened_Flag == false && hazardRate_Flag == true){
							consolidationHappening = false;
							System.out.println("Consolidation didn't take place");
						}
					}					
					if(consolidationHappened_Flag == true){
						consolidationHappening = false;
					}
					*/
					if(consolidationHappened_Flag == false){
						System.out.println("Consolidation didn't take place");
					}
					
			//	}
					
				}			
			}
		else{		//This is for non-testing case	
			if(!node.isFailed()){
				currentPredictionList = new ArrayList<Double>();			
				currentPredictionList = currentPrediction.get(node.getId());				
				boolean hazardRate_Flag = false;
				boolean consolidationHappened_Flag = false;
				ArrayList<Integer>correlatedProvisionedNodes = new ArrayList<Integer>();
				if(currentPredictionList == null || currentPredictionList.isEmpty()){								
					hazardRate_Flag = true;
					}				
				if(hazardRate_Flag == true){					
					int ftaNodeID;
					double hazardRateNode = 0.0;
					ftaNodeID = HostMapping.getFTAhostID(node.getId());
					if(RunTimeConstants.traceType.equals("LANL")){
						hazardRateNode = freadLANL.getMaxHazardRate(ftaNodeID);
					}
					if(RunTimeConstants.traceType.equals("Grid5000")){
						hazardRateNode = freadGrid5000.getMaxHazardRate(ftaNodeID);
					}
					int vmCount;
					boolean consolidationFlag = false;					
					vmCount = node.getVmList().size();
					//In this if condition, VMs are moving out from the host on which a VM got destroyed recently to some other host 					
					if(consolidationFlag == false)
					{						
						vmMigrationList = new ArrayList<Vm>();
						for(int i=0;i<vmCount; i++){
							vmMigrationList.add(node.getVmList().get(i));
						}
						if(RunTimeConstants.failureCorrelationConsolidation == true){												
							String nodeLocation;										
							ArrayList<Integer>correlatedNodes = new ArrayList<Integer>();	
							int provisionedFtaNodeID;
							nodeLocation = freadGrid5000.getClusterNameForNode(ftaNodeID);
							correlatedNodes.addAll(kMeans.getCorrelatedNodes(nodeLocation, ftaNodeID));
							for(int i=0; i<correlatedNodes.size(); i++){
								if(correlatedNodes.get(i).equals(ftaNodeID)){
									correlatedNodes.remove(i);
									break;
								}
							}								
							for(int i=0; i<correlatedNodes.size(); i++){
								for(int j=0; j<getHostList().size(); j++){
									provisionedFtaNodeID = HostMapping.getFTAhostID(getHostList().get(j).getId());
									if(provisionedFtaNodeID == correlatedNodes.get(i)){
										correlatedProvisionedNodes.add(provisionedFtaNodeID);
									}
								}								
							}
						}
						for(int i=0; i<getHostList().size(); i++){
							Host host;
							int freePeCount;
							int vmCountHost;
							int ftaHostID;
							boolean flagLocal = false;
							host = getHostList().get(i);
							if(host == node){
								continue;
							}
							if(RunTimeConstants.failureCorrelationConsolidation == true){
								int hostFtaID;
								hostFtaID = HostMapping.getFTAhostID(host.getId());
								for(int j=0; j<correlatedProvisionedNodes.size(); j++){										
									if(hostFtaID == correlatedProvisionedNodes.get(j)){
										flagLocal = true;
										break;
									}
								}
								if(flagLocal == true){
									continue;
								}									
							}
							if(IdleTime.expectedFailingHost.containsKey(host.getId())||IdleTime.checkHostActiveStatus(host.getId())==false){
								continue;
							}
							vmCountHost = host.getVmList().size();
							freePeCount = host.getNumberOfPes()-vmCountHost;
							if(freePeCount >= vmCount){																
								ftaHostID = HostMapping.getFTAhostID(host.getId());
								double hazardRateHost = 0.0;
								if(RunTimeConstants.traceType.equals("LANL")){
									hazardRateHost = freadLANL.getMaxHazardRate(ftaHostID);
								}
								if(RunTimeConstants.traceType.equals("Grid5000")){
									hazardRateHost = freadGrid5000.getMaxHazardRate(ftaHostID);
								}								
								if(hazardRateHost<hazardRateNode){
									Vm vm;
									consolidationFlag = true;
									ArrayList<Integer>cletIDList;
									for(int j=0; j<vmCount; j++){	
										int cletIDListSize;										
										vm = vmMigrationList.get(j);						
										if(lastVmConsolidationClock.containsKey(vm.getId())){
											if(lastVmConsolidationClock.get(vm.getId()).equals(CloudSim.clock())){												
												continue;
											}
										}
										cletIDList = new ArrayList<Integer>();
										cletIDListSize = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().size();
										for(int k=0; k<cletIDListSize; k++){
											cletIDList.add(((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getCloudletId());
										}	
										for(int k=0; k<cletIDListSize; k++){											
											long soFarClet;
											long length = 0;								
											long remainingLength;		
											long lastRemainingLength;										
											remainingLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();								
											length = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getCloudletLength();										
											if(cletSoFarBeforeFailing.containsKey(cletIDList.get(k))){
												soFarClet = length - remainingLength;												
												lastRemainingLength = length;												
												cletSoFarMap.put(cletIDList.get(k), soFarClet);
												soFarClet = soFarClet + cletSoFarBeforeFailing.get(cletIDList.get(k)) ;
												cletSoFarBeforeFailing.remove(cletIDList.get(k));
											}								
											else{
												lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
												soFarClet = lastRemainingLength - remainingLength;												
												cletSoFarMap.put(cletIDList.get(k), soFarClet);												
											}							
											vmRecord.setCletMigrationHostRecord(cletIDList.get(k), node.getId());								
											vmRecord.setVmExecutionDurationPerHostRecord(cletIDList.get(k), soFarClet);
											cletMigrated.put(cletIDList.get(k), true);
										}
										lastVmConsolidationClock.put(vm.getId(), CloudSim.clock());								
										List<Pe> peList = new ArrayList<Pe>();
										peList = node.getVmScheduler().getPesAllocatedForVM(vm);
										for(int k=0; k<peList.size(); k++){
											int peID;
											peID = peList.get(k).getId();
											IdleTime.setHostPeIdleTable(node.getId(), peID, true);
											IdleTime.setHostPeIdleClockTable(node.getId(), peID, CloudSim.clock());
										}								
									
										getVmAllocationPolicy().deallocateHostForVm(vm);																	
										vm.setInMigration(true);										
										boolean result = getVmAllocationPolicy().allocateHostForVm(vm, host);
										if (!result) {
											Log.printLine("[Datacenter.processVmMigrate] VM allocation to the destination host failed");											
											break;
										}
										Log.formatLine("%.2f: Consolidation of VM #%d to Host #%d is completed", CloudSim.clock(), vm.getId(), vm.getHost().getId());
										vm.setInMigration(false);										
										consolidationCount = consolidationCount + 1;
										vmRecord.setVmCurrentHost(vm.getId(), vm.getHost().getId());
									
										//Applying VM migration overheads on all the running cloudlets running on each VM on the given node.	
										double migrationOverhead;	
										double migrationDownTime;															
										migrationOverhead = mgOver.getMigrationOverhead(vm);
										migrationDownTime = mgOver.TotalDownTime(vm);									
										for(int k=0; k<cletIDListSize; k++){
											long remainingCletLength;
											double cletUtilization;	
											long soFarExecutedLength;
											long cletNewLength;
											long oldBackupLength;
											Cloudlet clet;																				
											soFarExecutedLength = cletSoFarMap.get(cletIDList.get(k));
											clet = broker.getCloudletFromID(cletIDList.get(k));
											cletNewLength = clet.getCloudletLength() + ((long)(migrationOverhead*1000)) +((long)(migrationDownTime*1000));											
											if(!cletLengthBackup.containsKey(cletIDList.get(k))){
												oldBackupLength = clet.getCloudletLength(); 
												oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
												cletLengthBackup.put(cletIDList.get(k), oldBackupLength);
											}
											else{
												oldBackupLength = cletLengthBackup.get(cletIDList.get(k)); 
												oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
												cletLengthBackup.put(cletIDList.get(k), oldBackupLength);
											}
											clet.setCloudletLength(cletNewLength);
											remainingCletLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();
											cletLastRemainingMap.put(cletIDList.get(k), remainingCletLength);
											MigrationOverheadTable.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
											DownTimeTable.setCloudletDownTimeforMigration(clet.getCloudletId(), migrationDownTime);
											migrationForCloudlets.put(cletIDList.get(k), soFarExecutedLength);							
											cletUtilization = cletNorm.getNormalizedLength(clet);
											ReliabilityCalculator(host, vm, cletUtilization);
											vmRecord.setCletMigrationHostRecordforDownTime(clet.getCloudletId(), host.getId());
											vmRecord.setCletMigrationDownTime(clet.getCloudletId(), migrationDownTime);	
											vmRecord.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
										}									
									}		
									List<Pe>peListNode = new ArrayList<Pe>();
									List<Integer>peIDListNode = new ArrayList<Integer>();
									peListNode = node.getPeList();
									boolean idleStatus;
									boolean clockStatus;
									int hostCount;
									for(int k=0; k<peListNode.size(); k++){
										peIDListNode.add(peListNode.get(k).getId());
										idleStatus = IdleTime.getHostPeIdleTable(node.getId(), peListNode.get(k).getId());
										clockStatus = IdleTime.checkHostPeIdleClockTable(node.getId(), peListNode.get(k).getId());
										if(idleStatus==true && clockStatus == true){					
											IdleTime.setHostPeIdleTimeTable(node.getId(), peListNode.get(k).getId(), CloudSim.clock());
											IdleTime.removeHostPeIdleClockTable(node.getId(), peListNode.get(k).getId());
										}
									}
									IdleTime.setHostActiveStatus(node.getId(), false, peIDListNode);
									hostCount = IdleTime.getActiveHostCount();
									HostCounter.setHostCount(hostCount);
									HostCounter.setClockList(CloudSim.clock());
									consolidationHappened_Flag = true;
									break;
								}
							}					
						}
					}					
					// In this if condition, VMs from other host are moving in to the host where a VM got destroyed recently
					if(consolidationFlag == false){
						if(RunTimeConstants.failureCorrelationConsolidation == true){												
							String nodeLocation;										
							ArrayList<Integer>correlatedNodes = new ArrayList<Integer>();	
							int provisionedFtaNodeID;
							nodeLocation = freadGrid5000.getClusterNameForNode(ftaNodeID);
							correlatedNodes.addAll(kMeans.getCorrelatedNodes(nodeLocation, ftaNodeID));
							for(int i=0; i<correlatedNodes.size(); i++){
								if(correlatedNodes.get(i).equals(ftaNodeID)){
									correlatedNodes.remove(i);
									break;
								}
							}	
							for(int i=0; i<correlatedNodes.size(); i++){
								for(int j=0; j<getHostList().size(); j++){
									provisionedFtaNodeID = HostMapping.getFTAhostID(getHostList().get(j).getId());
									if(provisionedFtaNodeID == correlatedNodes.get(i)){
										correlatedProvisionedNodes.add(provisionedFtaNodeID);
									}
								}								
							}
						}
						for(int i=0; i<getHostList().size(); i++){
							Host host;
							int freePeCount;	
							int vmCountHost;
							int ftaHostID;
							boolean flagLocal = false;
							host = getHostList().get(i);							
							if(host == node){
								continue;
							}						
							if(RunTimeConstants.failureCorrelationConsolidation == true){
								int hostFtaID;
								hostFtaID = HostMapping.getFTAhostID(host.getId());
								for(int j=0; j<correlatedProvisionedNodes.size(); j++){										
									if(hostFtaID == correlatedProvisionedNodes.get(j)){
										flagLocal = true;
										break;
									}
								}
								if(flagLocal == true){
									continue;
								}									
							}
							vmCountHost = host.getVmList().size();						
							if(vmCountHost == 0){
								continue;
							}							
							freePeCount = node.getNumberOfPes()-vmCount;							
							if(vmCountHost <= freePeCount){								
								ftaHostID = HostMapping.getFTAhostID(host.getId());
								double hazardRateHost = 0.0;
								if(RunTimeConstants.traceType.equals("LANL")){
									hazardRateHost = freadLANL.getMaxHazardRate(ftaHostID);
								}
								if(RunTimeConstants.traceType.equals("Grid5000")){
									hazardRateHost = freadGrid5000.getMaxHazardRate(ftaHostID);
								}																
								if(hazardRateHost>hazardRateNode){
									Vm vm;
									consolidationFlag = true;
									vmMigrationList = new ArrayList<Vm>();
									for(int j=0;j<vmCountHost; j++){
										vmMigrationList.add(host.getVmList().get(j));
									}
									ArrayList<Integer>cletIDList;
									for(int j=0; j<vmCountHost; j++){										
										int cletIDListSize;										
										vm = vmMigrationList.get(j);
										if(lastVmConsolidationClock.containsKey(vm.getId())){
											if(lastVmConsolidationClock.get(vm.getId()).equals(CloudSim.clock())){												
												continue;
											}
										}
										cletIDList = new ArrayList<Integer>();
										cletIDListSize = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().size();
										for(int k=0; k<cletIDListSize; k++){
											cletIDList.add(((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getCloudletId());
										}	
										for(int k=0; k<cletIDListSize; k++){											
											long soFarClet;
											long length = 0;								
											long remainingLength;			
											long lastRemainingLength;											
											remainingLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();								
											length = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getCloudletLength();										
											if(cletSoFarBeforeFailing.containsKey(cletIDList.get(k))){
												soFarClet = length - remainingLength;												
												lastRemainingLength = length;												
												cletSoFarMap.put(cletIDList.get(k), soFarClet);
												soFarClet = soFarClet + cletSoFarBeforeFailing.get(cletIDList.get(k)) ;
												cletSoFarBeforeFailing.remove(cletIDList.get(k));
											}								
											else{
												lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
												soFarClet = lastRemainingLength - remainingLength;											
												cletSoFarMap.put(cletIDList.get(k), soFarClet);												
											}	
										
											vmRecord.setCletMigrationHostRecord(cletIDList.get(k), host.getId());									
											vmRecord.setVmExecutionDurationPerHostRecord(cletIDList.get(k), soFarClet);
											cletMigrated.put(cletIDList.get(k), true);
										}
										lastVmConsolidationClock.put(vm.getId(), CloudSim.clock());									
										List<Pe>peList = new ArrayList<Pe>();
										peList = host.getVmScheduler().getPesAllocatedForVM(vm);
										for(int k=0; k<peList.size(); k++){
											int peID;
											peID = peList.get(k).getId();
											IdleTime.setHostPeIdleTable(host.getId(), peID, true);
											IdleTime.setHostPeIdleClockTable(host.getId(), peID, CloudSim.clock());
										}									
										getVmAllocationPolicy().deallocateHostForVm(vm);
										//host.removeMigratingInVm(vm);
										vm.setInMigration(true);									
										boolean result = getVmAllocationPolicy().allocateHostForVm(vm, node);
										if (!result) {
											Log.printLine("[Datacenter.processVmMigrate] VM allocation to the destination host failed");
											System.exit(0);
										}
										Log.formatLine("%.2f: Consolidation of VM #%d to Host #%d is completed", CloudSim.clock(), vm.getId(), vm.getHost().getId());										
										vm.setInMigration(false);
										consolidationCount = consolidationCount + 1;
										vmRecord.setVmCurrentHost(vm.getId(), vm.getHost().getId());							
									//Applying VM migration overheads on all the running cloudlets running on each VM on the given node.	
										double migrationOverhead;			
										double migrationDownTime;															
										migrationOverhead = mgOver.getMigrationOverhead(vm);
										migrationDownTime = mgOver.TotalDownTime(vm);										
										for(int k=0; k<cletIDListSize; k++){
											long remainingCletLength;
											double cletUtilization;		
											Cloudlet clet;
											long cletNewLength;
											long soFarExecutedLength;			
											long oldBackupLength;
											soFarExecutedLength = cletSoFarMap.get(cletIDList.get(k));
											clet = broker.getCloudletFromID(cletIDList.get(k));
											cletNewLength = clet.getCloudletLength() + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));							
										
											if(!cletLengthBackup.containsKey(cletIDList.get(k))){
												oldBackupLength = clet.getCloudletLength(); 
												oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
												cletLengthBackup.put(cletIDList.get(k), oldBackupLength);
												}
											else{
												oldBackupLength = cletLengthBackup.get(cletIDList.get(k)); 
												oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
												cletLengthBackup.put(cletIDList.get(k), oldBackupLength);
											}
										
											clet.setCloudletLength(cletNewLength);
											remainingCletLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();									
											cletLastRemainingMap.put(cletIDList.get(k), remainingCletLength);							
											MigrationOverheadTable.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
											DownTimeTable.setCloudletDownTimeforMigration(clet.getCloudletId(), migrationDownTime);
											migrationForCloudlets.put(cletIDList.get(k), soFarExecutedLength);							
											cletUtilization = cletNorm.getNormalizedLength(clet);
											ReliabilityCalculator(node, vm, cletUtilization);
											vmRecord.setCletMigrationHostRecordforDownTime(clet.getCloudletId(), node.getId());
											vmRecord.setCletMigrationDownTime(clet.getCloudletId(), migrationDownTime);	
											vmRecord.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
										}										
									}
									List<Pe>peListNode = new ArrayList<Pe>();
									List<Integer>peIDListNode = new ArrayList<Integer>();
									peListNode = host.getPeList();
									boolean idleStatus;
									boolean clockStatus;
									int hostCount;
									for(int k=0; k<peListNode.size(); k++){
										peIDListNode.add(peListNode.get(k).getId());
										idleStatus = IdleTime.getHostPeIdleTable(host.getId(), peListNode.get(k).getId());
										clockStatus = IdleTime.checkHostPeIdleClockTable(host.getId(), peListNode.get(k).getId());
										if(idleStatus==true && clockStatus == true){					
											IdleTime.setHostPeIdleTimeTable(host.getId(), peListNode.get(k).getId(), CloudSim.clock());
											IdleTime.removeHostPeIdleClockTable(host.getId(), peListNode.get(k).getId());
										}
									}
									IdleTime.setHostActiveStatus(host.getId(), false, peIDListNode);
									hostCount = IdleTime.getActiveHostCount();
									HostCounter.setHostCount(hostCount);
									HostCounter.setClockList(CloudSim.clock());								
									consolidationHappened_Flag = true;
									break;
								}
							}					
						}
					}			
				}
				else{					
					double nextFailurePredictionNode = currentPredictionList.get(0);
					int vmCount;
					boolean consolidationFlag = false;
					vmCount = node.getVmList().size();
					int ftaNodeID;
					ftaNodeID = HostMapping.getFTAhostID(node.getId());
					//In this if condition, VMs are moving out from the host on which a VM got destroyed recently to some other host 
					if(consolidationFlag == false)
					{
						vmMigrationList = new ArrayList<Vm>();
						for(int i=0;i<vmCount; i++){
							vmMigrationList.add(node.getVmList().get(i));
						}
						if(RunTimeConstants.failureCorrelationConsolidation == true){												
							String nodeLocation;										
							ArrayList<Integer>correlatedNodes = new ArrayList<Integer>();	
							int provisionedFtaNodeID;
							nodeLocation = freadGrid5000.getClusterNameForNode(ftaNodeID);
							correlatedNodes.addAll(kMeans.getCorrelatedNodes(nodeLocation, ftaNodeID));
							for(int i=0; i<correlatedNodes.size(); i++){
								if(correlatedNodes.get(i).equals(ftaNodeID)){
									correlatedNodes.remove(i);
									break;
								}
							}	
							for(int i=0; i<correlatedNodes.size(); i++){
								for(int j=0; j<getHostList().size(); j++){
									provisionedFtaNodeID = HostMapping.getFTAhostID(getHostList().get(j).getId());
									if(provisionedFtaNodeID == correlatedNodes.get(i)){
										correlatedProvisionedNodes.add(provisionedFtaNodeID);
									}
								}								
							}
						}
						for(int i=0; i<getHostList().size(); i++){
							Host host;
							int freePeCount;
							int vmCountHost;
							boolean flagLocal = false;
							host = getHostList().get(i);
							if(host == node){
								continue;
							}
							if(RunTimeConstants.failureCorrelationConsolidation == true){
								int hostFtaID;
								hostFtaID = HostMapping.getFTAhostID(host.getId());
								for(int j=0; j<correlatedProvisionedNodes.size(); j++){										
									if(hostFtaID == correlatedProvisionedNodes.get(j)){
										flagLocal = true;
										break;
									}
								}
								if(flagLocal == true){
									continue;
								}									
							}
							if(IdleTime.checkExpectedFailingHost(host.getId())==true || IdleTime.checkHostActiveStatus(host.getId())==false){
								continue;
							}
							currentPredictionList = new ArrayList<Double>();
							currentPredictionList = currentPrediction.get(host.getId());
							if(currentPredictionList == null ||currentPredictionList.isEmpty()){
								continue;
							}
							vmCountHost = host.getVmList().size();							
							freePeCount = host.getNumberOfPes()-vmCountHost;
							if(freePeCount >= vmCount){							
								double nextFailurePredictionHost = currentPredictionList.get(0);								
								if(nextFailurePredictionHost>nextFailurePredictionNode){
									Vm vm;
									consolidationFlag = true;
									ArrayList<Integer>cletIDList;
									for(int j=0; j<vmCount; j++){
										int cletIDListSize;	
										vm = vmMigrationList.get(j);
										if(lastVmConsolidationClock.containsKey(vm.getId())){
											if(lastVmConsolidationClock.get(vm.getId()).equals(CloudSim.clock())){											
												continue;
											}
										}
										cletIDList = new ArrayList<Integer>();
										cletIDListSize = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().size();
										for(int k=0; k<cletIDListSize; k++){
											cletIDList.add(((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getCloudletId());
										}									
										for(int k=0; k<cletIDListSize; k++){										
											long soFarClet;
											long length = 0;								
											long remainingLength;	
											long lastRemainingLength;											
											remainingLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();
											length = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getCloudletLength();									
											if(cletSoFarBeforeFailing.containsKey(cletIDList.get(k))){
												soFarClet = length - remainingLength;												
												lastRemainingLength = length;
												cletSoFarMap.put(cletIDList.get(k), soFarClet);
												soFarClet = soFarClet + cletSoFarBeforeFailing.get(cletIDList.get(k)) ;																						
												cletSoFarBeforeFailing.remove(cletIDList.get(k));
											}								
											else{
												lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
												soFarClet = lastRemainingLength - remainingLength;											
												cletSoFarMap.put(cletIDList.get(k), soFarClet);												
											}											
											vmRecord.setCletMigrationHostRecord(cletIDList.get(k), node.getId());										
											vmRecord.setVmExecutionDurationPerHostRecord(cletIDList.get(k), soFarClet);
											cletMigrated.put(cletIDList.get(k), true);
										}										
										lastVmConsolidationClock.put(vm.getId(), CloudSim.clock());									
									
										List<Pe> peList = new ArrayList<Pe>();
										peList = node.getVmScheduler().getPesAllocatedForVM(vm);
										for(int k=0; k<peList.size(); k++){
											int peID;
											peID = peList.get(k).getId();
											IdleTime.setHostPeIdleTable(node.getId(), peID, true);
											IdleTime.setHostPeIdleClockTable(node.getId(), peID, CloudSim.clock());
										}	
									
										getVmAllocationPolicy().deallocateHostForVm(vm);										
										vm.setInMigration(true);
										boolean result = getVmAllocationPolicy().allocateHostForVm(vm, host);
										if (!result) {
											Log.printLine("[Datacenter.processVmMigrate] VM allocation to the destination host failed");
											//System.exit(0);
											break;
										}
										Log.formatLine("%.2f: Consolidation of VM #%d to Host #%d is completed", CloudSim.clock(), vm.getId(), vm.getHost().getId());
										vm.setInMigration(false);			
										consolidationCount = consolidationCount + 1;
										vmRecord.setVmCurrentHost(vm.getId(), vm.getHost().getId());									
									//Applying VM migration overheads on all the running cloudlets running on each VM on the given node.	
										double migrationOverhead;	
										double migrationDownTime;															
										migrationOverhead = mgOver.getMigrationOverhead(vm);		
										migrationDownTime = mgOver.TotalDownTime(vm);										
										for(int k=0; k<cletIDListSize; k++){											
											long remainingCletLength;											
											double cletUtilization;
											long soFarExecutedLength;											
											long cletNewLength;
											long oldBackupLength;
											Cloudlet clet;
											soFarExecutedLength = cletSoFarMap.get(cletIDList.get(k));
											clet = broker.getCloudletFromID(cletIDList.get(k));
											cletNewLength = clet.getCloudletLength() + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));										
											if(!cletLengthBackup.containsKey(cletIDList.get(k))){
												oldBackupLength = clet.getCloudletLength(); 
												oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
												cletLengthBackup.put(cletIDList.get(k), oldBackupLength);
											}
											else{
												oldBackupLength = cletLengthBackup.get(cletIDList.get(k)); 
												oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
												cletLengthBackup.put(cletIDList.get(k), oldBackupLength);
											}										
											clet.setCloudletLength(cletNewLength);
											remainingCletLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();								
											cletLastRemainingMap.put(cletIDList.get(k), remainingCletLength);								
											MigrationOverheadTable.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
											DownTimeTable.setCloudletDownTimeforMigration(clet.getCloudletId(), migrationDownTime);										
											migrationForCloudlets.put(cletIDList.get(k), soFarExecutedLength);							
											cletUtilization = cletNorm.getNormalizedLength(clet);
											ReliabilityCalculator(host, vm, cletUtilization);
											vmRecord.setCletMigrationHostRecordforDownTime(clet.getCloudletId(), host.getId());
											vmRecord.setCletMigrationDownTime(clet.getCloudletId(), migrationDownTime);	
											vmRecord.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
										}										
									}	
									List<Pe>peListNode = new ArrayList<Pe>();
									List<Integer>peIDListNode = new ArrayList<Integer>();
									peListNode = node.getPeList();
									boolean idleStatus;
									boolean clockStatus;
									int hostCount;
									for(int k=0; k<peListNode.size(); k++){
										peIDListNode.add(peListNode.get(k).getId());
										idleStatus = IdleTime.getHostPeIdleTable(node.getId(), peListNode.get(k).getId());
										clockStatus = IdleTime.checkHostPeIdleClockTable(node.getId(), peListNode.get(k).getId());
										if(idleStatus==true && clockStatus == true){					
											IdleTime.setHostPeIdleTimeTable(node.getId(), peListNode.get(k).getId(), CloudSim.clock());
											IdleTime.removeHostPeIdleClockTable(node.getId(), peListNode.get(k).getId());
										}
									}
									IdleTime.setHostActiveStatus(node.getId(), false, peIDListNode);
									hostCount = IdleTime.getActiveHostCount();
									HostCounter.setHostCount(hostCount);
									HostCounter.setClockList(CloudSim.clock());								
									consolidationHappened_Flag = true;
									break;
								}
							}					
						}
					}
					// In this if condition, VMs from other host are moving in to the host where a VM got destroyed recently
					if(consolidationFlag == false){
						if(RunTimeConstants.failureCorrelationConsolidation == true){												
							String nodeLocation;										
							ArrayList<Integer>correlatedNodes = new ArrayList<Integer>();	
							int provisionedFtaNodeID;
							nodeLocation = freadGrid5000.getClusterNameForNode(ftaNodeID);
							correlatedNodes.addAll(kMeans.getCorrelatedNodes(nodeLocation, ftaNodeID));
							for(int i=0; i<correlatedNodes.size(); i++){
								if(correlatedNodes.get(i).equals(ftaNodeID)){
									correlatedNodes.remove(i);
									break;
								}
							}	
							for(int i=0; i<correlatedNodes.size(); i++){
								for(int j=0; j<getHostList().size(); j++){
									provisionedFtaNodeID = HostMapping.getFTAhostID(getHostList().get(j).getId());
									if(provisionedFtaNodeID == correlatedNodes.get(i)){
										correlatedProvisionedNodes.add(provisionedFtaNodeID);
									}
								}								
							}
						}		
						for(int i=0; i<getHostList().size(); i++){
							Host host;
							int freePeCount;	
							int vmCountHost;
							boolean flagLocal = false;
							host = getHostList().get(i);
							if(host == node){
								continue;
							}		
							if(RunTimeConstants.failureCorrelationConsolidation == true){
								int hostFtaID;
								hostFtaID = HostMapping.getFTAhostID(host.getId());
								for(int j=0; j<correlatedProvisionedNodes.size(); j++){										
									if(hostFtaID == correlatedProvisionedNodes.get(j)){
										flagLocal = true;
										break;
									}
								}
								if(flagLocal == true){
									continue;
								}									
							}
							vmCountHost = host.getVmList().size();
							if(vmCountHost == 0){
								continue;
							}						
							currentPredictionList = new ArrayList<Double>();
							currentPredictionList = currentPrediction.get(host.getId());
							if(currentPredictionList == null || currentPredictionList.isEmpty()){
								continue;
							}
							freePeCount = node.getNumberOfPes()-vmCount;							
							if(vmCountHost <= freePeCount){							
								double nextFailurePredictionHost = currentPredictionList.get(0);
								if(nextFailurePredictionHost<nextFailurePredictionNode){
									Vm vm;
									consolidationFlag = true;
									vmMigrationList = new ArrayList<Vm>();
									ArrayList<Integer>cletIDList;
									for(int j=0;j<vmCountHost; j++){
										vmMigrationList.add(host.getVmList().get(j));
									}
									for(int j=0; j<vmCountHost; j++){										
										int cletIDListSize;										
										vm = vmMigrationList.get(j);
										if(lastVmConsolidationClock.containsKey(vm.getId())){
											if(lastVmConsolidationClock.get(vm.getId()).equals(CloudSim.clock())){												
												continue;
											}
										}
										cletIDList = new ArrayList<Integer>();
										cletIDListSize = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().size();
										for(int k=0; k<cletIDListSize; k++){
											cletIDList.add(((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getCloudletId());
										}
										for(int k=0; k<cletIDListSize; k++){											
											long soFarClet;
											long length = 0;								
											long remainingLength;		
											long lastRemainingLength;										
											remainingLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();								
											length = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getCloudletLength();										
											if(cletSoFarBeforeFailing.containsKey(cletIDList.get(k))){
												soFarClet = length - remainingLength;												
												lastRemainingLength = length;												
												cletSoFarMap.put(cletIDList.get(k), soFarClet);
												soFarClet = soFarClet + cletSoFarBeforeFailing.get(cletIDList.get(k));
												cletSoFarBeforeFailing.remove(cletIDList.get(k));
											}								
											else{
												lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
												soFarClet = lastRemainingLength - remainingLength;											
												cletSoFarMap.put(cletIDList.get(k), soFarClet);												
											}										
											vmRecord.setCletMigrationHostRecord(cletIDList.get(k), host.getId());						
											vmRecord.setVmExecutionDurationPerHostRecord(cletIDList.get(k), soFarClet);
											cletMigrated.put(cletIDList.get(k), true);
										}										
										lastVmConsolidationClock.put(vm.getId(), CloudSim.clock());										
									
										List<Pe> peList = new ArrayList<Pe>();
										peList = host.getVmScheduler().getPesAllocatedForVM(vm);
										for(int k=0; k<peList.size(); k++){
											int peID;
											peID = peList.get(k).getId();
											IdleTime.setHostPeIdleTable(host.getId(), peID, true);
											IdleTime.setHostPeIdleClockTable(host.getId(), peID, CloudSim.clock());
										}										
										getVmAllocationPolicy().deallocateHostForVm(vm);										
										vm.setInMigration(true);
										boolean result = getVmAllocationPolicy().allocateHostForVm(vm, node);
										if (!result) {
											Log.printLine("[Datacenter.processVmMigrate] VM allocation to the destination host failed");
											System.exit(0);
										}
										Log.formatLine("%.2f: Consolidation of VM #%d to Host #%d is completed", CloudSim.clock(), vm.getId(), vm.getHost().getId());
										vm.setInMigration(false);
										consolidationCount = consolidationCount + 1;	
										vmRecord.setVmCurrentHost(vm.getId(), vm.getHost().getId());									
									//Applying VM migration overheads on all the running cloudlets running on each VM on the given node.	
										double migrationOverhead;									
										double migrationDownTime;															
										migrationOverhead = mgOver.getMigrationOverhead(vm);
										migrationDownTime = mgOver.TotalDownTime(vm);
									//Host newHost;
									//newHost = vm.getHost();
										for(int k=0; k<cletIDListSize; k++){										
											long remainingCletLength;
											double cletUtilization;											
											long soFarExecutedLength;											
											long cletNewLength;
											long oldBackupLength;
											Cloudlet clet;
											soFarExecutedLength = cletSoFarMap.get(cletIDList.get(k));
											clet = broker.getCloudletFromID(cletIDList.get(k));
											cletNewLength = clet.getCloudletLength() + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));											
											if(!cletLengthBackup.containsKey(cletIDList.get(k))){
												oldBackupLength = clet.getCloudletLength(); 
												oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
												cletLengthBackup.put(cletIDList.get(k), oldBackupLength);
											}
											else{
												oldBackupLength = cletLengthBackup.get(cletIDList.get(k)); 
												oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
												cletLengthBackup.put(cletIDList.get(k), oldBackupLength);
											}		
											clet.setCloudletLength(cletNewLength);	
											remainingCletLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();								
											cletLastRemainingMap.put(cletIDList.get(k), remainingCletLength);							
											MigrationOverheadTable.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
											DownTimeTable.setCloudletDownTimeforMigration(clet.getCloudletId(), migrationDownTime);										
											migrationForCloudlets.put(cletIDList.get(k), soFarExecutedLength);							
											cletUtilization = cletNorm.getNormalizedLength(clet);
											ReliabilityCalculator(node, vm, cletUtilization);
											vmRecord.setCletMigrationHostRecordforDownTime(clet.getCloudletId(), node.getId());
											vmRecord.setCletMigrationDownTime(clet.getCloudletId(), migrationDownTime);	
											vmRecord.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
										}									
									}	
									List<Pe>peListNode = new ArrayList<Pe>();
									List<Integer>peIDListNode = new ArrayList<Integer>();
									peListNode = host.getPeList();
									boolean idleStatus;
									boolean clockStatus;
									int hostCount;
									for(int k=0; k<peListNode.size(); k++){
										peIDListNode.add(peListNode.get(k).getId());
										idleStatus = IdleTime.getHostPeIdleTable(host.getId(), peListNode.get(k).getId());
										clockStatus = IdleTime.checkHostPeIdleClockTable(host.getId(), peListNode.get(k).getId());
										if(idleStatus==true && clockStatus == true){					
											IdleTime.setHostPeIdleTimeTable(host.getId(), peListNode.get(k).getId(), CloudSim.clock());
											IdleTime.removeHostPeIdleClockTable(host.getId(), peListNode.get(k).getId());
										}
									}
									IdleTime.setHostActiveStatus(host.getId(), false, peIDListNode);
									hostCount = IdleTime.getActiveHostCount();
									HostCounter.setHostCount(hostCount);
									HostCounter.setClockList(CloudSim.clock());
									consolidationHappened_Flag = true;
									break;
								}
							}					
						}
					}			
				}				
			}			
		  }			
		}
		
		boolean afterConsolidation = false;
		boolean consolidationCheckpointing = false;
		HashMap<Integer, Long> previousConsolidationRemainingTable = new HashMap<Integer, Long>(); 
		HashMap<Integer, Boolean>backtobackConsolidationsFlag = new HashMap<Integer, Boolean>();
		public void processVMConsolidationForCheckpointing(Host node){			
			
			if(RunTimeConstants.test == true){
				if(node.isFailed()){
					System.out.println(" Node # " +node.getId()+ " is in failed state. So consolidation will not take place");
				}
				else{
				//if(node.getId() == 0){
				//	System.out.println("Check");
				//}
				updateCloudletProcessing();	
				currentPredictionList = new ArrayList<Double>();			
				currentPredictionList = currentPrediction.get(node.getId());				
				boolean hazardRate_Flag = false;
				int ftahostID;
				boolean consolidationHappened_Flag = false;
				ArrayList<Integer>correlatedProvisionedNodes = new ArrayList<Integer>();
				if(currentPredictionList == null || currentPredictionList.isEmpty()){
					System.out.println("Failure prediction information for Node #" +node.getId()+ " is not available.");
					System.out.println("So consolidation is happening using Maximum Hazard Rate criteria");					
					hazardRate_Flag = true;
					}
				else{
					System.out.println("VM consolidation is happening on the basis of failure prediction");
				}
				if(hazardRate_Flag == true){					
					int ftaNodeID;
					double hazardRateNode = 0.0;
					ftaNodeID = HostMapping.getFTAhostID(node.getId());
					if(RunTimeConstants.traceType.equals("LANL")){
						hazardRateNode = freadLANL.getMaxHazardRate(ftaNodeID);
					}
					if(RunTimeConstants.traceType.equals("Grid5000")){
						hazardRateNode = freadGrid5000.getMaxHazardRate(ftaNodeID);
					}
					int vmCount;
					boolean consolidationFlag = false;					
					vmCount = node.getVmList().size();
					//In this if condition, VMs are moving out from the host on which a VM got destroyed recently to some other host 					
					if(consolidationFlag == false)
					{						
						vmMigrationList = new ArrayList<Vm>();
						for(int i=0;i<vmCount; i++){
							vmMigrationList.add(node.getVmList().get(i));
						}						
						if(RunTimeConstants.failureCorrelationConsolidation == true){												
							String nodeLocation;										
							ArrayList<Integer>correlatedNodes = new ArrayList<Integer>();	
							int provisionedFtaNodeID;
							nodeLocation = freadGrid5000.getClusterNameForNode(ftaNodeID);
							correlatedNodes.addAll(kMeans.getCorrelatedNodes(nodeLocation, ftaNodeID));
							for(int i=0; i<correlatedNodes.size(); i++){
								if(correlatedNodes.get(i).equals(ftaNodeID)){
									correlatedNodes.remove(i);
									break;
								}
							}	
							for(int i=0; i<correlatedNodes.size(); i++){
								for(int j=0; j<getHostList().size(); j++){
									provisionedFtaNodeID = HostMapping.getFTAhostID(getHostList().get(j).getId());
									if(provisionedFtaNodeID == correlatedNodes.get(i)){
										correlatedProvisionedNodes.add(provisionedFtaNodeID);
									}
								}								
							}
						}						
						for(int i=0; i<getHostList().size(); i++){
							Host host;
							int freePeCount;
							int vmCountHost;
							int ftaHostID;
							boolean flagLocal = false;
							host = getHostList().get(i);
							if(host == node){
								continue;
							}
							if(RunTimeConstants.failureCorrelationConsolidation == true){
								int hostFtaID;
								hostFtaID = HostMapping.getFTAhostID(host.getId());
								for(int j=0; j<correlatedProvisionedNodes.size(); j++){										
									if(hostFtaID == correlatedProvisionedNodes.get(j)){
										flagLocal = true;
										break;
									}
								}
								if(flagLocal == true){
									continue;
								}								
							}
							if(IdleTime.checkExpectedFailingHost(host.getId())==true || IdleTime.checkHostActiveStatus(host.getId())==false){
								continue;
							}
							vmCountHost = host.getVmList().size();
							freePeCount = host.getNumberOfPes()-vmCountHost;
							if(freePeCount >= vmCount){																
								ftaHostID = HostMapping.getFTAhostID(host.getId());
								double hazardRateHost = 0.0; 
								if(RunTimeConstants.traceType.equals("LANL")){
									hazardRateHost = freadLANL.getMaxHazardRate(ftaHostID);
								}
								if(RunTimeConstants.traceType.equals("Grid5000")){
									hazardRateHost = freadGrid5000.getMaxHazardRate(ftaHostID);
								}								
								if(hazardRateHost<hazardRateNode){
									Vm vm;
									consolidationFlag = true;
									ArrayList<Integer>cletIDList;	
									//if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4){
									if(RunTimeConstants.faultToleranceMechanism == 4 || RunTimeConstants.faultToleranceMechanism == 1 || (RunTimeConstants.faultToleranceMechanism == 2 && RunTimeConstants.failureCorrelationWithRstrChkpt == true)){	
										//ftaNodeID = HostMapping.getFTAhostID(node.getId());
										consolidationCheckpointing = true;
										System.out.println("Checkpoint is being taken before consolidation");
										//createCheckpointsWithMigration(ftahostID);
										createCheckpoints(ftaNodeID);
									//	consolidationCheckpointing = false;
									}
									//if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 ){
									//if(RunTimeConstants.faultToleranceMechanism == 1 ){	
									//	ftahostID = HostMapping.getFTAhostID(node.getId());
									//	consolidationCheckpointing = true;
									//	System.out.println("Checkpoint is being taken before consolidation");
									//	createCheckpoints(ftahostID);
									//}
									for(int j=0; j<vmCount; j++){	
										int cletIDListSize;										
										vm = vmMigrationList.get(j);						
										if(lastVmConsolidationClock.containsKey(vm.getId())){
											if(lastVmConsolidationClock.get(vm.getId()).equals(CloudSim.clock())){
												System.out.println("No progress has been made by VM " +vm.getId()+ " since last consolidation");
												System.out.println("So VM " +vm.getId()+ " is not getting consolidated");
												continue;
											}
										}
										cletIDList = new ArrayList<Integer>();
										cletIDListSize = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().size();
										for(int k=0; k<cletIDListSize; k++){
											cletIDList.add(((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getCloudletId());
											//if(cletMapforConsolidationTesting.containsKey(cletIDList.get(k))){
											if(cletIDList.get(k)==3){
												System.out.println("Check");
											//	System.out.println(+cletIDList.get(k));
											}
										}	
										for(int k=0; k<cletIDListSize; k++){								
											long soFarClet = 0;
											long length = 0;								
											long remainingLength;		
											long lastRemainingLength = 0;	
											//long chkptRemaining = 0;
											//long currentRemaining = 0;
											remainingLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();								
											length = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getCloudletLength();									
											if(!cletLengthBackup.containsKey(cletIDList.get(k))){
												cletLengthBackup.put(cletIDList.get(k), length);
											}		
											/*
											if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4){												
										//		ftahostID = HostMapping.getFTAhostID(node.getId());
										//		System.out.println("Checkpoint is being taken before consolidation");
										//		createCheckpointsWithMigration(ftahostID);											
												if(cletSoFarBeforeFailing.containsKey(cletIDList.get(k))){
													//soFarClet = length - remainingLength;
													soFarClet = cletSoFarMap.get(cletIDList.get(k));
													lastRemainingLength = length;
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													//cletSoFarMap.put(cletIDList.get(j), soFarClet);
													soFarClet = soFarClet + cletSoFarBeforeFailing.get(cletIDList.get(k));
													System.out.println(CloudSim.clock()+ " Sofar execution of Cloudlet " +cletIDList.get(k)+ " on node "+node.getId()+" after failure overheads is " +soFarClet);										
													cletSoFarBeforeFailing.remove(cletIDList.get(k));
												}								
												else{
													lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
													//soFarClet = lastRemainingLength - remainingLength;										
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													//cletSoFarMap.put(cletIDList.get(j), soFarClet);
													soFarClet = cletSoFarMap.get(cletIDList.get(k));
												}											
											}
											*/
											//if((RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1)||(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4)){
											//	ftahostID = HostMapping.getFTAhostID(node.getId());
											//	consolidationCheckpointing = true;
											//	System.out.println("Checkpoint is being taken before consolidation");
											//	createCheckpoints(ftahostID);
												if(cletSoFarBeforeFailing.containsKey(cletIDList.get(k))){
													soFarClet = length - remainingLength;												
													lastRemainingLength = length;
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);
													soFarClet = soFarClet + cletSoFarBeforeFailing.get(cletIDList.get(k)) ;
													System.out.println(CloudSim.clock()+ " Sofar execution of Cloudlet " +cletIDList.get(k)+ " on node "+node.getId()+" after failure overheads is " +soFarClet);										
													cletSoFarBeforeFailing.remove(cletIDList.get(k));
												}								
												else{
													lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
													soFarClet = lastRemainingLength - remainingLength;										
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);												
												}						
												
											//}
											/*
											else{
												if(!cletMigrated.containsKey(cletIDList.get(k))){
													soFarClet = cletLengthBackup.get(cletIDList.get(k)) - remainingLength;
													lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);
													previousConsolidationRemainingTable.put(cletIDList.get(k), remainingLength);
												}
												if(cletMigrated.containsKey(cletIDList.get(k)) && failFlagTable.containsKey(cletIDList.get(k))==false){													
													soFarClet = previousConsolidationRemainingTable.get(cletIDList.get(k)) - remainingLength;
													lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);
													previousConsolidationRemainingTable.put(cletIDList.get(k), remainingLength);
												}
												if(cletMigrated.containsKey(cletIDList.get(k)) && failureOccurredBeforeChkpt.containsKey(cletIDList.get(k))==true){
													soFarClet = cletLengthBackup.get(cletIDList.get(k)) - remainingLength;
													lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);
													previousConsolidationRemainingTable.put(cletIDList.get(k), remainingLength);
													backtobackConsolidationsFlag.put(cletIDList.get(k), true);
												}	
												if(cletMigrated.containsKey(cletIDList.get(k)) && failureOccurredBeforeChkpt.containsKey(cletIDList.get(k))==true && backtobackConsolidationsFlag.containsKey(cletIDList.get(k))==true){
													soFarClet = previousConsolidationRemainingTable.get(cletIDList.get(k)) - remainingLength;
													lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);
													previousConsolidationRemainingTable.put(cletIDList.get(k), remainingLength);
												}
												else{
													if(failFlagTable.containsKey(cletIDList.get(k))==true){
														chkptRemaining = cletLengthBackup.get(cletIDList.get(k))- checkpointsForCloudlets.get(cletIDList.get(k));
														soFarClet = chkptRemaining - remainingLength;
														lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
														cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
														cletSoFarMap.put(cletIDList.get(k), soFarClet);
													}
													
												}								
												
											}
											*/
											vmRecord.setCletMigrationHostRecord(cletIDList.get(k), node.getId());								
										//	if(cletIDList.get(k)==0){
										//		System.out.println("Check");
										//	}
											System.out.println("Cloudlet " +cletIDList.get(k)+" is consolidating from node " +node.getId());
											System.out.println("Length of Cloudlet "+cletIDList.get(k)+ " is " +length);
											System.out.println("Current Remaining Length of Cloudlet "+cletIDList.get(k)+ " is " +remainingLength);
											System.out.println("Last Remaining Length of Cloudlet "+cletIDList.get(k)+ " is " +lastRemainingLength);
											System.out.println("Sofar execution of Cloudlet " +cletIDList.get(k)+ " on node "+node.getId()+" is " +soFarClet);
											
											vmRecord.setVmExecutionDurationPerHostRecord(cletIDList.get(k), soFarClet);
											cletMigrated.put(cletIDList.get(k), true);
										}
										lastVmConsolidationClock.put(vm.getId(), CloudSim.clock());										
										
										getVmAllocationPolicy().deallocateHostForVm(vm);
										//node.removeMigratingInVm(vm);								
										vm.setInMigration(true);
										
										boolean result = getVmAllocationPolicy().allocateHostForVm(vm, host);
										if (!result) {
											Log.printLine("[Datacenter.processVmMigrate] VM allocation to the destination host failed");
											//System.exit(0);
											break;
										}
										Log.formatLine("%.2f: Consolidation of VM #%d to Host #%d is completed", CloudSim.clock(), vm.getId(), vm.getHost().getId());
										vm.setInMigration(false);										
										consolidationCount = consolidationCount + 1;
										System.out.println("Consolidation count is " +consolidationCount);
										vmRecord.setVmCurrentHost(vm.getId(), vm.getHost().getId());
										List<Pe>peListNode = new ArrayList<Pe>();
										List<Integer>peIDListNode = new ArrayList<Integer>();
										peListNode = node.getPeList();
										for(int k=0; k<peListNode.size(); k++){
											peIDListNode.add(peListNode.get(k).getId());
										}
										IdleTime.setHostActiveStatus(node.getId(), false, peIDListNode);
										//Applying VM migration overheads on all the running cloudlets running on each VM on the given node.	
										double migrationOverhead;	
										double migrationDownTime;
										//mgOver.TotalMigrationTime(vm);
										//mgOver.TotalDownTime(vm);						
										migrationOverhead = mgOver.getMigrationOverhead(vm);
										migrationDownTime = mgOver.TotalDownTime(vm);
										Host newHost;
										newHost = vm.getHost();
										for(int k=0; k<cletIDListSize; k++){
											long remainingCletLength;
											double cletUtilization;	
											long soFarExecutedLength;
											long cletNewLength;
											long oldBackupLength;
											Cloudlet clet;																				
											soFarExecutedLength = cletSoFarMap.get(cletIDList.get(k));
											clet = broker.getCloudletFromID(cletIDList.get(k));
											cletNewLength = clet.getCloudletLength() + ((long)(migrationOverhead*1000)) +((long)(migrationDownTime*1000));											
											if(!cletLengthBackup.containsKey(cletIDList.get(k))){
												oldBackupLength = clet.getCloudletLength(); 
												oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
												cletLengthBackup.put(cletIDList.get(k), oldBackupLength);
											}
											else{
												oldBackupLength = cletLengthBackup.get(cletIDList.get(k)); 
												oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
												cletLengthBackup.put(cletIDList.get(k), oldBackupLength);
											}
											clet.setCloudletLength(cletNewLength);
											remainingCletLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();
											cletLastRemainingMap.put(cletIDList.get(k), remainingCletLength);
											MigrationOverheadTable.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
											DownTimeTable.setCloudletDownTimeforMigration(clet.getCloudletId(), migrationDownTime);
											migrationForCloudlets.put(cletIDList.get(k), soFarExecutedLength);							
											cletUtilization = cletNorm.getNormalizedLength(clet);
											ReliabilityCalculator(newHost, vm, cletUtilization);
											vmRecord.setCletMigrationHostRecordforDownTime(clet.getCloudletId(), newHost.getId());
											vmRecord.setCletMigrationDownTime(clet.getCloudletId(), migrationDownTime);	
											vmRecord.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
										}	
										consolidationHappened_Flag = true;
									}									
									break;
								}
							}					
						}
					}					
					// In this if condition, VMs from other host are moving in to the host where a VM got destroyed recently
					if(consolidationFlag == false){				
						if(RunTimeConstants.failureCorrelationConsolidation == true){												
							String nodeLocation;										
							ArrayList<Integer>correlatedNodes = new ArrayList<Integer>();	
							int provisionedFtaNodeID;
							nodeLocation = freadGrid5000.getClusterNameForNode(ftaNodeID);
							correlatedNodes.addAll(kMeans.getCorrelatedNodes(nodeLocation, ftaNodeID));
							for(int i=0; i<correlatedNodes.size(); i++){
								if(correlatedNodes.get(i).equals(ftaNodeID)){
									correlatedNodes.remove(i);
									break;
								}
							}	
							for(int i=0; i<correlatedNodes.size(); i++){
								for(int j=0; j<getHostList().size(); j++){
									provisionedFtaNodeID = HostMapping.getFTAhostID(getHostList().get(j).getId());
									if(provisionedFtaNodeID == correlatedNodes.get(i)){
										correlatedProvisionedNodes.add(provisionedFtaNodeID);
									}
								}								
							}
						}
						for(int i=0; i<getHostList().size(); i++){
							Host host;
							int freePeCount;	
							int vmCountHost;
							int ftaHostID;
							boolean flagLocal = false;
							host = getHostList().get(i);							
							if(host == node){
								continue;
							}				
							if(RunTimeConstants.failureCorrelationConsolidation == true){
								int hostFtaID;
								hostFtaID = HostMapping.getFTAhostID(host.getId());
								for(int j=0; j<correlatedProvisionedNodes.size(); j++){										
									if(hostFtaID == correlatedProvisionedNodes.get(j)){
										flagLocal = true;
										break;
									}
								}
								if(flagLocal == true){
									continue;
								}								
							}
							vmCountHost = host.getVmList().size();						
							if(vmCountHost == 0){
								continue;
							}							
							freePeCount = node.getNumberOfPes()-vmCount;							
							if(vmCountHost <= freePeCount){								
								ftaHostID = HostMapping.getFTAhostID(host.getId());
								double hazardRateHost = 0.0; 
								if(RunTimeConstants.traceType.equals("LANL")){
									hazardRateHost = freadLANL.getMaxHazardRate(ftaHostID);
								}
								if(RunTimeConstants.traceType.equals("Grid5000")){
									hazardRateHost = freadGrid5000.getMaxHazardRate(ftaHostID);
								}
								if(hazardRateHost>hazardRateNode){
									/*
									if(RunTimeConstants.predictionFlag == true &&  RunTimeConstants.faultToleranceMechanism == 4){	
										ftahostID = HostMapping.getFTAhostID(host.getId());
										consolidationCheckpointing = true;
										System.out.println("Checkpoint is being taken before consolidation");
										//createCheckpointsWithMigration(ftahostID);
										createCheckpoints(ftahostID);
									}
									if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1){
										ftahostID = HostMapping.getFTAhostID(host.getId());
										consolidationCheckpointing = true;
										System.out.println("Checkpoint is being taken before consolidation");
										createCheckpoints(ftahostID);
									}
									*/
									if(RunTimeConstants.faultToleranceMechanism == 4 || RunTimeConstants.faultToleranceMechanism == 1 || (RunTimeConstants.faultToleranceMechanism == 2 && RunTimeConstants.failureCorrelationWithRstrChkpt == true)){	
										//ftahostID = HostMapping.getFTAhostID(host.getId());
										consolidationCheckpointing = true;
										System.out.println("Checkpoint is being taken before consolidation");										
										createCheckpoints(ftaHostID);
										//consolidationCheckpointing = false;
									}									
									Vm vm;
									consolidationFlag = true;
									vmMigrationList = new ArrayList<Vm>();
									for(int j=0;j<vmCountHost; j++){
										vmMigrationList.add(host.getVmList().get(j));
									}
									ArrayList<Integer>cletIDList;
									for(int j=0; j<vmCountHost; j++){										
										int cletIDListSize;										
										vm = vmMigrationList.get(j);
										if(lastVmConsolidationClock.containsKey(vm.getId())){
											if(lastVmConsolidationClock.get(vm.getId()).equals(CloudSim.clock())){
												System.out.println("No progress has been made by VM " +vm.getId()+ " since last consolidation");
												System.out.println("So VM " +vm.getId()+ " is not getting consolidated");
												continue;
											}
										}
										cletIDList = new ArrayList<Integer>();
										cletIDListSize = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().size();
										for(int k=0; k<cletIDListSize; k++){
											cletIDList.add(((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getCloudletId());
											//if(cletMapforConsolidationTesting.containsKey(cletIDList.get(k))){
											if(cletIDList.get(k)==3){
												System.out.println("Check");
												//System.out.println(+cletIDList.get(k));
											}
										}	
										for(int k=0; k<cletIDListSize; k++){								
											long soFarClet = 0;
											long length = 0;								
											long remainingLength;			
											long lastRemainingLength = 0;
											//long chkptRemaining = 0;
											remainingLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();								
											length = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getCloudletLength();											
											/*
											if(RunTimeConstants.predictionFlag == true &&  RunTimeConstants.faultToleranceMechanism == 4){												
											//	ftahostID = HostMapping.getFTAhostID(host.getId());
											//	System.out.println("Checkpoint is being taken before consolidation");
											//	createCheckpointsWithMigration(ftahostID);											
												if(cletSoFarBeforeFailing.containsKey(cletIDList.get(k))){
													//soFarClet = length - remainingLength;
													soFarClet = cletSoFarMap.get(cletIDList.get(k));
													lastRemainingLength = length;
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													//cletSoFarMap.put(cletIDList.get(j), soFarClet);
													soFarClet = soFarClet + cletSoFarBeforeFailing.get(cletIDList.get(k)) ;
													System.out.println(CloudSim.clock()+ " Sofar execution of Cloudlet " +cletIDList.get(k)+ " on node "+host.getId()+" after failure overheads is " +soFarClet);										
													cletSoFarBeforeFailing.remove(cletIDList.get(k));
												}								
												else{
													lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
													//soFarClet = lastRemainingLength - remainingLength;										
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													//cletSoFarMap.put(cletIDList.get(j), soFarClet);
													soFarClet = cletSoFarMap.get(cletIDList.get(k));
												}							
											}
											*/
										//	if((RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1)||(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4)){
											//	ftahostID = HostMapping.getFTAhostID(host.getId());
											//	consolidationCheckpointing = true;
											//	System.out.println("Checkpoint is being taken before consolidation");
											//	createCheckpoints(ftahostID);
												if(cletSoFarBeforeFailing.containsKey(cletIDList.get(k))){
													soFarClet = length - remainingLength;												
													lastRemainingLength = length;
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);
													soFarClet = soFarClet + cletSoFarBeforeFailing.get(cletIDList.get(k)) ;
													System.out.println(CloudSim.clock()+ " Sofar execution of Cloudlet " +cletIDList.get(k)+ " on node "+host.getId()+" after failure overheads is " +soFarClet);										
													cletSoFarBeforeFailing.remove(cletIDList.get(k));
												}								
												else{
													lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
													soFarClet = lastRemainingLength - remainingLength;										
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);												
												}											
											//}
											/*
											else{
												if(!cletMigrated.containsKey(cletIDList.get(k))){
													soFarClet = cletLengthBackup.get(cletIDList.get(k)) - remainingLength;
													lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);
													previousConsolidationRemainingTable.put(cletIDList.get(k), remainingLength);
												}
												if(cletMigrated.containsKey(cletIDList.get(k)) && failFlagTable.containsKey(cletIDList.get(k))==false){													
													soFarClet = previousConsolidationRemainingTable.get(cletIDList.get(k)) - remainingLength;
													lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);
													previousConsolidationRemainingTable.put(cletIDList.get(k), remainingLength);
												}
												if(cletMigrated.containsKey(cletIDList.get(k)) && failureOccurredBeforeChkpt.containsKey(cletIDList.get(k))==true){
													soFarClet = cletLengthBackup.get(cletIDList.get(k)) - remainingLength;
													lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);
													previousConsolidationRemainingTable.put(cletIDList.get(k), remainingLength);
													backtobackConsolidationsFlag.put(cletIDList.get(k), true);
												}	
												if(cletMigrated.containsKey(cletIDList.get(k)) && failureOccurredBeforeChkpt.containsKey(cletIDList.get(k))==true && backtobackConsolidationsFlag.containsKey(cletIDList.get(k))==true){
													soFarClet = previousConsolidationRemainingTable.get(cletIDList.get(k)) - remainingLength;
													lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);
													previousConsolidationRemainingTable.put(cletIDList.get(k), remainingLength);
												}
												else{
													if(failFlagTable.containsKey(cletIDList.get(k))==true){
														chkptRemaining = cletLengthBackup.get(cletIDList.get(k))- checkpointsForCloudlets.get(cletIDList.get(k));
														soFarClet = chkptRemaining - remainingLength;
														lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
														cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
														cletSoFarMap.put(cletIDList.get(k), soFarClet);
													}
												}									
											}										
											*/
											vmRecord.setCletMigrationHostRecord(cletIDList.get(k), host.getId());
										//	if(cletIDList.get(k)==0){
									//			System.out.println("Check");
									//		}
											System.out.println("Cloudlet " +cletIDList.get(k)+" is consolidating from node " +host.getId());
											System.out.println("Length of Cloudlet "+cletIDList.get(k)+ " is " +length);
											System.out.println("Current Remaining Length of Cloudlet "+cletIDList.get(k)+ " is " +remainingLength);
											System.out.println("Last Remaining Length of Cloudlet "+cletIDList.get(k)+ " is " +lastRemainingLength);
											System.out.println("Sofar execution of Cloudlet " +cletIDList.get(k)+ " on node "+host.getId()+" is " +soFarClet);
											
											vmRecord.setVmExecutionDurationPerHostRecord(cletIDList.get(k), soFarClet);
											cletMigrated.put(cletIDList.get(k), true);
										}
										lastVmConsolidationClock.put(vm.getId(), CloudSim.clock());										
										getVmAllocationPolicy().deallocateHostForVm(vm);
										//host.removeMigratingInVm(vm);
										vm.setInMigration(true);									
										boolean result = getVmAllocationPolicy().allocateHostForVm(vm, node);
										if (!result) {
											Log.printLine("[Datacenter.processVmMigrate] VM allocation to the destination host failed");
											System.exit(0);
										}
										Log.formatLine("%.2f: Consolidation of VM #%d to Host #%d is completed", CloudSim.clock(), vm.getId(), vm.getHost().getId());										
										vm.setInMigration(false);
										consolidationCount = consolidationCount + 1;
										System.out.println("Consolidation count is " +consolidationCount);
										vmRecord.setVmCurrentHost(vm.getId(), vm.getHost().getId());
										
										List<Pe>peListHost = new ArrayList<Pe>();
										List<Integer>peIDListHost = new ArrayList<Integer>();
										peListHost = host.getPeList();
										for(int k=0; k<peListHost.size(); k++){
											peIDListHost.add(peListHost.get(k).getId());
										}
										IdleTime.setHostActiveStatus(host.getId(), false, peIDListHost);
										
										//Applying VM migration overheads on all the running cloudlets running on each VM on the given node.	
										double migrationOverhead;			
										double migrationDownTime;															
										migrationOverhead = mgOver.getMigrationOverhead(vm);
										migrationDownTime = mgOver.TotalDownTime(vm);
										Host newHost;
										newHost = vm.getHost();
										for(int k=0; k<cletIDListSize; k++){
											long remainingCletLength;
											double cletUtilization;		
											Cloudlet clet;
											long cletNewLength;
											long soFarExecutedLength;			
											long oldBackupLength;
											soFarExecutedLength = cletSoFarMap.get(cletIDList.get(k));
											clet = broker.getCloudletFromID(cletIDList.get(k));
											cletNewLength = clet.getCloudletLength() + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));											
											if(!cletLengthBackup.containsKey(cletIDList.get(k))){
												oldBackupLength = clet.getCloudletLength(); 
												oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
												cletLengthBackup.put(cletIDList.get(k), oldBackupLength);
											}
											else{
												oldBackupLength = cletLengthBackup.get(cletIDList.get(k)); 
												oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
												cletLengthBackup.put(cletIDList.get(k), oldBackupLength);
											}
											clet.setCloudletLength(cletNewLength);
											remainingCletLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();									
											cletLastRemainingMap.put(cletIDList.get(k), remainingCletLength);							
											MigrationOverheadTable.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
											DownTimeTable.setCloudletDownTimeforMigration(clet.getCloudletId(), migrationDownTime);
											migrationForCloudlets.put(cletIDList.get(k), soFarExecutedLength);							
											cletUtilization = cletNorm.getNormalizedLength(clet);
											ReliabilityCalculator(newHost, vm, cletUtilization);
											vmRecord.setCletMigrationHostRecordforDownTime(clet.getCloudletId(), newHost.getId());
											vmRecord.setCletMigrationDownTime(clet.getCloudletId(), migrationDownTime);	
											vmRecord.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
										}	
										consolidationHappened_Flag = true;
									}								
									break;
								}
							}					
						}
					}			
				}
				else{					
					double nextFailurePredictionNode = currentPredictionList.get(0);
					int vmCount;
					int ftaNodeID;
					boolean consolidationFlag = false;
					vmCount = node.getVmList().size();
					ftaNodeID = HostMapping.getFTAhostID(node.getId());
					//In this if condition, VMs are moving out from the host on which a VM got destroyed recently to some other host 
					if(consolidationFlag == false)
					{
						vmMigrationList = new ArrayList<Vm>();
						for(int i=0;i<vmCount; i++){
							vmMigrationList.add(node.getVmList().get(i));
						}
						if(RunTimeConstants.failureCorrelationConsolidation == true){												
							String nodeLocation;										
							ArrayList<Integer>correlatedNodes = new ArrayList<Integer>();	
							int provisionedFtaNodeID;
							nodeLocation = freadGrid5000.getClusterNameForNode(ftaNodeID);
							correlatedNodes.addAll(kMeans.getCorrelatedNodes(nodeLocation, ftaNodeID));
							for(int i=0; i<correlatedNodes.size(); i++){
								if(correlatedNodes.get(i).equals(ftaNodeID)){
									correlatedNodes.remove(i);
									break;
								}
							}	
							for(int i=0; i<correlatedNodes.size(); i++){
								for(int j=0; j<getHostList().size(); j++){
									provisionedFtaNodeID = HostMapping.getFTAhostID(getHostList().get(j).getId());
									if(provisionedFtaNodeID == correlatedNodes.get(i)){
										correlatedProvisionedNodes.add(provisionedFtaNodeID);
									}
								}								
							}
						}
						for(int i=0; i<getHostList().size(); i++){
							Host host;
							int freePeCount;
							int vmCountHost;
							boolean flagLocal = false;
							host = getHostList().get(i);
							if(host == node){
								continue;
							}
							if(RunTimeConstants.failureCorrelationConsolidation == true){
								int hostFtaID;
								hostFtaID = HostMapping.getFTAhostID(host.getId());
								for(int j=0; j<correlatedProvisionedNodes.size(); j++){										
									if(hostFtaID == correlatedProvisionedNodes.get(j)){
										flagLocal = true;
										break;
									}
								}
								if(flagLocal == true){
									continue;	
								}															
							}
							if(IdleTime.expectedFailingHost.containsKey(host.getId())||IdleTime.checkHostActiveStatus(host.getId())==false){
								continue;
							}
							currentPredictionList = new ArrayList<Double>();
							currentPredictionList = currentPrediction.get(host.getId());
							if(currentPredictionList == null || currentPredictionList.isEmpty()){
								continue;
							}
							vmCountHost = host.getVmList().size();							
							freePeCount = host.getNumberOfPes()-vmCountHost;
							if(freePeCount >= vmCount){								
								//currentPredictionList = new ArrayList<Double>();
								//currentPredictionList = currentPrediction.get(host.getId());
								//if(currentPredictionList.isEmpty()){
							//		System.out.println("Prediction value for destination host is not available. So hazard rate has been considered.");
							//		double hazardRateNode;
									//continue;
								//}
								double nextFailurePredictionHost = currentPredictionList.get(0);								
								if(nextFailurePredictionHost>nextFailurePredictionNode){
									/*
									if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4){
										ftahostID = HostMapping.getFTAhostID(node.getId());
										consolidationCheckpointing = true;
										System.out.println("Checkpoint is being taken before consolidation");
										//createCheckpointsWithMigration(ftahostID);
										createCheckpoints(ftahostID);
									}
									if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1){
										ftahostID = HostMapping.getFTAhostID(node.getId());
										consolidationCheckpointing = true;
										System.out.println("Checkpoint is being taken before consolidation");
										createCheckpoints(ftahostID);
									}
									*/
									if(RunTimeConstants.faultToleranceMechanism == 4 || RunTimeConstants.faultToleranceMechanism == 1 || (RunTimeConstants.faultToleranceMechanism == 2 && RunTimeConstants.failureCorrelationWithRstrChkpt == true)){	
										//ftahostID = HostMapping.getFTAhostID(node.getId());
										consolidationCheckpointing = true;
										System.out.println("Checkpoint is being taken before consolidation");										
										createCheckpoints(ftaNodeID);
										//consolidationCheckpointing = false;
									}									
									Vm vm;
									consolidationFlag = true;
									ArrayList<Integer>cletIDList;
									for(int j=0; j<vmCount; j++){
										int cletIDListSize;	
										vm = vmMigrationList.get(j);
										if(lastVmConsolidationClock.containsKey(vm.getId())){
											if(lastVmConsolidationClock.get(vm.getId()).equals(CloudSim.clock())){
												System.out.println("No progress has been made by VM " +vm.getId()+ " since last consolidation");
												System.out.println("So VM " +vm.getId()+ " is not getting consolidated");
												continue;
											}
										}
										cletIDList = new ArrayList<Integer>();
										cletIDListSize = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().size();
										for(int k=0; k<cletIDListSize; k++){
											cletIDList.add(((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getCloudletId());
											//if(cletMapforConsolidationTesting.containsKey(cletIDList.get(k))){
											if(cletIDList.get(k)==3){
												System.out.println("Check");
												//System.out.println(+cletIDList.get(k));
											}
										}									
										for(int k=0; k<cletIDListSize; k++){								
											long soFarClet = 0;
											long length = 0;								
											long remainingLength;	
											long lastRemainingLength = 0;				
											//long chkptRemaining;
											remainingLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();
											length = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getCloudletLength();														
											/*
											if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4){											
											//	ftahostID = HostMapping.getFTAhostID(node.getId());
											//	System.out.println("Checkpoint is being taken before consolidation");
											//	createCheckpointsWithMigration(ftahostID);											
												if(cletSoFarBeforeFailing.containsKey(cletIDList.get(k))){
													//soFarClet = length - remainingLength;
													soFarClet = cletSoFarMap.get(cletIDList.get(k));
													lastRemainingLength = length;
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													//cletSoFarMap.put(cletIDList.get(j), soFarClet);
													soFarClet = soFarClet + cletSoFarBeforeFailing.get(cletIDList.get(k)) ;
													System.out.println(CloudSim.clock()+ " Sofar execution of Cloudlet " +cletIDList.get(k)+ " on node "+node.getId()+" after failure overheads is " +soFarClet);										
													cletSoFarBeforeFailing.remove(cletIDList.get(k));
												}								
												else{
													lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
													soFarClet = lastRemainingLength - remainingLength;										
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(j), soFarClet);
													//soFarClet = cletSoFarMap.get(cletIDList.get(k));
												}									
												
											}
											*/
											//if((RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1)||(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4)){
											//	ftahostID = HostMapping.getFTAhostID(node.getId());
											//	consolidationCheckpointing = true;
											//	System.out.println("Checkpoint is being taken before consolidation");
											//	createCheckpoints(ftahostID);
												if(cletSoFarBeforeFailing.containsKey(cletIDList.get(k))){
													soFarClet = length - remainingLength;												
													lastRemainingLength = length;
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);
													soFarClet = soFarClet + cletSoFarBeforeFailing.get(cletIDList.get(k)) ;
													System.out.println(CloudSim.clock()+ " Sofar execution of Cloudlet " +cletIDList.get(k)+ " on node "+node.getId()+" after failure overheads is " +soFarClet);										
													cletSoFarBeforeFailing.remove(cletIDList.get(k));
												}								
												else{
													lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
													soFarClet = lastRemainingLength - remainingLength;										
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);												
												}								
											//}
											/*
											else{
												
												if(!cletMigrated.containsKey(cletIDList.get(k))){
													soFarClet = cletLengthBackup.get(cletIDList.get(k)) - remainingLength;
													lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);
													previousConsolidationRemainingTable.put(cletIDList.get(k), remainingLength);
												}
												if(cletMigrated.containsKey(cletIDList.get(k)) && failFlagTable.containsKey(cletIDList.get(k))==false){													
													soFarClet = previousConsolidationRemainingTable.get(cletIDList.get(k)) - remainingLength;
													lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);
													previousConsolidationRemainingTable.put(cletIDList.get(k), remainingLength);
												}
												if(cletMigrated.containsKey(cletIDList.get(k)) && failureOccurredBeforeChkpt.containsKey(cletIDList.get(k))==true){
													soFarClet = cletLengthBackup.get(cletIDList.get(k)) - remainingLength;
													lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);
													previousConsolidationRemainingTable.put(cletIDList.get(k), remainingLength);
													backtobackConsolidationsFlag.put(cletIDList.get(k), true);
												}	
												if(cletMigrated.containsKey(cletIDList.get(k)) && failureOccurredBeforeChkpt.containsKey(cletIDList.get(k))==true && backtobackConsolidationsFlag.containsKey(cletIDList.get(k))==true){
													soFarClet = previousConsolidationRemainingTable.get(cletIDList.get(k)) - remainingLength;
													lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);
													previousConsolidationRemainingTable.put(cletIDList.get(k), remainingLength);
												}
												else{
													if(failFlagTable.containsKey(cletIDList.get(k))==true){
														chkptRemaining = cletLengthBackup.get(cletIDList.get(k))- checkpointsForCloudlets.get(cletIDList.get(k));
														soFarClet = chkptRemaining - remainingLength;
														lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
														cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
														cletSoFarMap.put(cletIDList.get(k), soFarClet);
													}
												}											
											}										
											*/
											vmRecord.setCletMigrationHostRecord(cletIDList.get(k), node.getId());										
										//	if(cletIDList.get(k)==1631){
										//		System.out.println("Check");
										//	}
											System.out.println("Cloudlet " +cletIDList.get(k)+" is consolidating from node " +node.getId());
											System.out.println("Length of Cloudlet "+cletIDList.get(k)+ " is " +length);
											System.out.println("Current Remaining Length of Cloudlet "+cletIDList.get(k)+ " is " +remainingLength);
											System.out.println("Last Remaining Length of Cloudlet "+cletIDList.get(k)+ " is " +lastRemainingLength);
											System.out.println("Sofar execution of Cloudlet " +cletIDList.get(k)+ " on node "+node.getId()+" is " +soFarClet);
											
											vmRecord.setVmExecutionDurationPerHostRecord(cletIDList.get(k), soFarClet);
											cletMigrated.put(cletIDList.get(k), true);
										}										
										lastVmConsolidationClock.put(vm.getId(), CloudSim.clock());								
										
										getVmAllocationPolicy().deallocateHostForVm(vm);
										//node.removeMigratingInVm(vm);
										vm.setInMigration(true);
										boolean result = getVmAllocationPolicy().allocateHostForVm(vm, host);
										if (!result) {
											Log.printLine("[Datacenter.processVmMigrate] VM allocation to the destination host failed");
											//System.exit(0);
											break;
										}
										Log.formatLine("%.2f: Consolidation of VM #%d to Host #%d is completed", CloudSim.clock(), vm.getId(), vm.getHost().getId());
										vm.setInMigration(false);			
										consolidationCount = consolidationCount + 1;
										System.out.println("Consolidation count is " +consolidationCount);
										vmRecord.setVmCurrentHost(vm.getId(), vm.getHost().getId());										
										List<Pe>peListNode = new ArrayList<Pe>();
										List<Integer>peIDListNode = new ArrayList<Integer>();
										peListNode = node.getPeList();
										for(int k=0; k<peListNode.size(); k++){
											peIDListNode.add(peListNode.get(k).getId());
										}
										IdleTime.setHostActiveStatus(node.getId(), false, peIDListNode);										
										
										//Applying VM migration overheads on all the running cloudlets running on each VM on the given node.	
										double migrationOverhead;	
										double migrationDownTime;
										//mgOver.TotalMigrationTime(vm);
										//mgOver.TotalDownTime(vm);						
										migrationOverhead = mgOver.getMigrationOverhead(vm);		
										migrationDownTime = mgOver.TotalDownTime(vm);
										Host newHost;
										newHost = vm.getHost();
										for(int k=0; k<cletIDListSize; k++){											
											long remainingCletLength;											
											double cletUtilization;
											long soFarExecutedLength;											
											long cletNewLength;
											long oldBackupLength;
											Cloudlet clet;
											soFarExecutedLength = cletSoFarMap.get(cletIDList.get(k));
											clet = broker.getCloudletFromID(cletIDList.get(k));
											cletNewLength = clet.getCloudletLength() + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));											
											if(!cletLengthBackup.containsKey(cletIDList.get(k))){
												oldBackupLength = clet.getCloudletLength(); 
												oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
												cletLengthBackup.put(cletIDList.get(k), oldBackupLength);
											}
											else{
												oldBackupLength = cletLengthBackup.get(cletIDList.get(k)); 
												oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
												cletLengthBackup.put(cletIDList.get(k), oldBackupLength);
											}
											clet.setCloudletLength(cletNewLength);
											remainingCletLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();								
											cletLastRemainingMap.put(cletIDList.get(k), remainingCletLength);								
											MigrationOverheadTable.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
											DownTimeTable.setCloudletDownTimeforMigration(clet.getCloudletId(), migrationDownTime);										
											migrationForCloudlets.put(cletIDList.get(k), soFarExecutedLength);							
											cletUtilization = cletNorm.getNormalizedLength(clet);
											ReliabilityCalculator(newHost, vm, cletUtilization);
											vmRecord.setCletMigrationHostRecordforDownTime(clet.getCloudletId(), newHost.getId());
											vmRecord.setCletMigrationDownTime(clet.getCloudletId(), migrationDownTime);	
											vmRecord.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
										}
										consolidationHappened_Flag = true;
									}									
									break;
								}
							}					
						}
					}
					// In this if condition, VMs from other host are moving in to the host where a VM got destroyed recently
					if(consolidationFlag == false){
						if(RunTimeConstants.failureCorrelationConsolidation == true){												
							String nodeLocation;										
							ArrayList<Integer>correlatedNodes = new ArrayList<Integer>();	
							int provisionedFtaNodeID;
							nodeLocation = freadGrid5000.getClusterNameForNode(ftaNodeID);
							correlatedNodes.addAll(kMeans.getCorrelatedNodes(nodeLocation, ftaNodeID));
							for(int i=0; i<correlatedNodes.size(); i++){
								if(correlatedNodes.get(i).equals(ftaNodeID)){
									correlatedNodes.remove(i);
									break;
								}
							}	
							for(int i=0; i<correlatedNodes.size(); i++){
								for(int j=0; j<getHostList().size(); j++){
									provisionedFtaNodeID = HostMapping.getFTAhostID(getHostList().get(j).getId());
									if(provisionedFtaNodeID == correlatedNodes.get(i)){
										correlatedProvisionedNodes.add(provisionedFtaNodeID);
									}
								}								
							}
						}
						for(int i=0; i<getHostList().size(); i++){
							Host host;
							int freePeCount;	
							int vmCountHost;
							boolean flagLocal = false;
							host = getHostList().get(i);							
							if(host == node){
								continue;
							}					
							if(RunTimeConstants.failureCorrelationConsolidation == true){
								int hostFtaID;
								hostFtaID = HostMapping.getFTAhostID(host.getId());
								for(int j=0; j<correlatedProvisionedNodes.size(); j++){										
									if(hostFtaID == correlatedProvisionedNodes.get(j)){
										flagLocal = true;
										break;
									}
								}
								if(flagLocal == true){
									continue;
								}								
							}
							vmCountHost = host.getVmList().size();
							if(vmCountHost == 0){
								continue;
							}							
							freePeCount = node.getNumberOfPes()-vmCount;							
							if(vmCountHost <= freePeCount){								
								currentPredictionList = new ArrayList<Double>();
								currentPredictionList = currentPrediction.get(host.getId());
								if(currentPredictionList == null || currentPredictionList.isEmpty()){
									continue;
								}
								double nextFailurePredictionHost = currentPredictionList.get(0);
								if(nextFailurePredictionHost<nextFailurePredictionNode){									
									/*
									if(RunTimeConstants.predictionFlag == true &&  RunTimeConstants.faultToleranceMechanism == 4){
										ftahostID = HostMapping.getFTAhostID(host.getId());
										consolidationCheckpointing = true;
										System.out.println("Checkpoint is being taken before consolidation");
										//createCheckpointsWithMigration(ftahostID);	
										createCheckpoints(ftahostID);
									}
									if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 ){
										ftahostID = HostMapping.getFTAhostID(host.getId());
										consolidationCheckpointing = true;
										System.out.println("Checkpoint is being taken before consolidation");
										createCheckpoints(ftahostID);
									}
									*/
									if(RunTimeConstants.faultToleranceMechanism == 4 || RunTimeConstants.faultToleranceMechanism == 1 || (RunTimeConstants.faultToleranceMechanism == 2 && RunTimeConstants.failureCorrelationWithRstrChkpt == true)){	
										ftahostID = HostMapping.getFTAhostID(host.getId());
										consolidationCheckpointing = true;
										System.out.println("Checkpoint is being taken before consolidation");										
										createCheckpoints(ftahostID);
									//	consolidationCheckpointing = false;
									}
									
									Vm vm;
									consolidationFlag = true;
									vmMigrationList = new ArrayList<Vm>();
									ArrayList<Integer>cletIDList;
									for(int j=0;j<vmCountHost; j++){
										vmMigrationList.add(host.getVmList().get(j));
									}
									for(int j=0; j<vmCountHost; j++){										
										int cletIDListSize;										
										vm = vmMigrationList.get(j);
										if(lastVmConsolidationClock.containsKey(vm.getId())){
											if(lastVmConsolidationClock.get(vm.getId()).equals(CloudSim.clock())){
												System.out.println("No progress has been made by VM " +vm.getId()+ " since last consolidation");
												System.out.println("So VM " +vm.getId()+ " is not getting consolidated");
												continue;
											}
										}
										cletIDList = new ArrayList<Integer>();
										cletIDListSize = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().size();
										for(int k=0; k<cletIDListSize; k++){
											cletIDList.add(((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getCloudletId());
											//if(cletMapforConsolidationTesting.containsKey(cletIDList.get(k))){
											if(cletIDList.get(k)==3){
												System.out.println("Check");
												//System.out.println(+cletIDList.get(k));
											}
										}
										for(int k=0; k<cletIDListSize; k++){								
											long soFarClet = 0;
											long length = 0;								
											long remainingLength;		
											long lastRemainingLength = 0;	
										//	long chkptRemaining = 0;
											remainingLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();								
											length = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getCloudletLength();															
											/*
											if(RunTimeConstants.predictionFlag == true &&  RunTimeConstants.faultToleranceMechanism == 4){												
											//	ftahostID = HostMapping.getFTAhostID(host.getId());
											//	System.out.println("Checkpoint is being taken before consolidation");
											//	createCheckpointsWithMigration(ftahostID);												
												if(cletSoFarBeforeFailing.containsKey(cletIDList.get(k))){
													//soFarClet = length - remainingLength;
													soFarClet = cletSoFarMap.get(cletIDList.get(k));
													lastRemainingLength = length;
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													//cletSoFarMap.put(cletIDList.get(j), soFarClet);
													soFarClet = soFarClet + cletSoFarBeforeFailing.get(cletIDList.get(k)) ;
													System.out.println(CloudSim.clock()+ " Sofar execution of Cloudlet " +cletIDList.get(k)+ " on node "+host.getId()+" after failure overheads is " +soFarClet);										
													cletSoFarBeforeFailing.remove(cletIDList.get(k));
												}								
												else{
													lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
													//soFarClet = lastRemainingLength - remainingLength;										
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													//cletSoFarMap.put(cletIDList.get(j), soFarClet);
													soFarClet = cletSoFarMap.get(cletIDList.get(k));
												}											
											}	
											*/
											//if((RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1)||(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4)){
											//	ftahostID = HostMapping.getFTAhostID(host.getId());
											//	consolidationCheckpointing = true;
											//	System.out.println("Checkpoint is being taken before consolidation");
											//	createCheckpoints(ftahostID);
												if(cletSoFarBeforeFailing.containsKey(cletIDList.get(k))){
													soFarClet = length - remainingLength;												
													lastRemainingLength = length;
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);
													soFarClet = soFarClet + cletSoFarBeforeFailing.get(cletIDList.get(k)) ;
													System.out.println(CloudSim.clock()+ " Sofar execution of Cloudlet " +cletIDList.get(k)+ " on node "+host.getId()+" after failure overheads is " +soFarClet);										
													cletSoFarBeforeFailing.remove(cletIDList.get(k));
												}								
												else{
													lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
													soFarClet = lastRemainingLength - remainingLength;										
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);												
												}											
											//}
											/*
											else{												
												if(!cletMigrated.containsKey(cletIDList.get(k))){
													soFarClet = cletLengthBackup.get(cletIDList.get(k)) - remainingLength;
													lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);
													previousConsolidationRemainingTable.put(cletIDList.get(k), remainingLength);
												}
												if(cletMigrated.containsKey(cletIDList.get(k)) && failFlagTable.containsKey(cletIDList.get(k))==false){													
													soFarClet = previousConsolidationRemainingTable.get(cletIDList.get(k)) - remainingLength;
													lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);
													previousConsolidationRemainingTable.put(cletIDList.get(k), remainingLength);
												}
												if(cletMigrated.containsKey(cletIDList.get(k)) && failureOccurredBeforeChkpt.containsKey(cletIDList.get(k))==true){
													soFarClet = cletLengthBackup.get(cletIDList.get(k)) - remainingLength;
													lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);
													previousConsolidationRemainingTable.put(cletIDList.get(k), remainingLength);
													backtobackConsolidationsFlag.put(cletIDList.get(k), true);
												}	
												if(cletMigrated.containsKey(cletIDList.get(k)) && failureOccurredBeforeChkpt.containsKey(cletIDList.get(k))==true && backtobackConsolidationsFlag.containsKey(cletIDList.get(k))==true){
													soFarClet = previousConsolidationRemainingTable.get(cletIDList.get(k)) - remainingLength;
													lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
													cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
													cletSoFarMap.put(cletIDList.get(k), soFarClet);
													previousConsolidationRemainingTable.put(cletIDList.get(k), remainingLength);
												}
												else{
													if(failFlagTable.containsKey(cletIDList.get(k))==true){
														chkptRemaining = cletLengthBackup.get(cletIDList.get(k))- checkpointsForCloudlets.get(cletIDList.get(k));
														soFarClet = chkptRemaining - remainingLength;
														lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
														cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
														cletSoFarMap.put(cletIDList.get(k), soFarClet);
													}
												}								
												
											}																					
											*/
											vmRecord.setCletMigrationHostRecord(cletIDList.get(k), host.getId());									
											
											System.out.println("Cloudlet " +cletIDList.get(k)+" is consolidating from node " +host.getId());
											System.out.println("Length of Cloudlet "+cletIDList.get(k)+ " is " +length);
											System.out.println("Current Remaining Length of Cloudlet "+cletIDList.get(k)+ " is " +remainingLength);
											System.out.println("Last Remaining Length of Cloudlet "+cletIDList.get(k)+ " is " +lastRemainingLength);
											System.out.println("Sofar execution of Cloudlet " +cletIDList.get(k)+ " on node "+host.getId()+" is " +soFarClet);
											
											vmRecord.setVmExecutionDurationPerHostRecord(cletIDList.get(k), soFarClet);
											cletMigrated.put(cletIDList.get(k), true);
										}										
										lastVmConsolidationClock.put(vm.getId(), CloudSim.clock());										
										
										getVmAllocationPolicy().deallocateHostForVm(vm);
										//host.removeMigratingInVm(vm);
										vm.setInMigration(true);
										boolean result = getVmAllocationPolicy().allocateHostForVm(vm, node);
										if (!result) {
											Log.printLine("[Datacenter.processVmMigrate] VM allocation to the destination host failed");
											System.exit(0);
										}
										Log.formatLine("%.2f: Consolidation of VM #%d to Host #%d is completed", CloudSim.clock(), vm.getId(), vm.getHost().getId());
										vm.setInMigration(false);
										consolidationCount = consolidationCount + 1;	
										System.out.println("Consolidation count is " +consolidationCount);
										vmRecord.setVmCurrentHost(vm.getId(), vm.getHost().getId());
										
										List<Pe>peListHost = new ArrayList<Pe>();
										List<Integer>peIDListHost = new ArrayList<Integer>();
										peListHost = host.getPeList();
										for(int k=0; k<peListHost.size(); k++){
											peIDListHost.add(peListHost.get(k).getId());
										}
										IdleTime.setHostActiveStatus(host.getId(), false, peIDListHost);										
										
										//Applying VM migration overheads on all the running cloudlets running on each VM on the given node.	
										double migrationOverhead;									
										double migrationDownTime;															
										migrationOverhead = mgOver.getMigrationOverhead(vm);
										migrationDownTime = mgOver.TotalDownTime(vm);
										Host newHost;
										newHost = vm.getHost();
										for(int k=0; k<cletIDListSize; k++){										
											long remainingCletLength;
											double cletUtilization;											
											long soFarExecutedLength;											
											long cletNewLength;
											long oldBackupLength;
											Cloudlet clet;
											soFarExecutedLength = cletSoFarMap.get(cletIDList.get(k));
											clet = broker.getCloudletFromID(cletIDList.get(k));
											cletNewLength = clet.getCloudletLength() + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));															
											if(!cletLengthBackup.containsKey(cletIDList.get(k))){
												oldBackupLength = clet.getCloudletLength(); 
												oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
												cletLengthBackup.put(cletIDList.get(k), oldBackupLength);
											}
											else{
												oldBackupLength = cletLengthBackup.get(cletIDList.get(k)); 
												oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
												cletLengthBackup.put(cletIDList.get(k), oldBackupLength);
											}		
											clet.setCloudletLength(cletNewLength);
											remainingCletLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();								
											cletLastRemainingMap.put(cletIDList.get(k), remainingCletLength);							
											MigrationOverheadTable.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
											DownTimeTable.setCloudletDownTimeforMigration(clet.getCloudletId(), migrationDownTime);										
											migrationForCloudlets.put(cletIDList.get(k), soFarExecutedLength);							
											cletUtilization = cletNorm.getNormalizedLength(clet);
											ReliabilityCalculator(newHost, vm, cletUtilization);
											vmRecord.setCletMigrationHostRecordforDownTime(clet.getCloudletId(), newHost.getId());
											vmRecord.setCletMigrationDownTime(clet.getCloudletId(), migrationDownTime);	
											vmRecord.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
										}			
										consolidationHappened_Flag = true;
									}								
									break;
								}
							}					
						}
					}			
				}
				if(consolidationHappened_Flag == false){
					System.out.println("Consolidation didn't take place");
				}
			}			
		}
		else{
			//if(node.isFailed()){
			//	System.out.println(" Node # " +node.getId()+ " is in failed state. So consolidation will not take place");
			//}
			if(!node.isFailed()){			
				updateCloudletProcessing();	
				currentPredictionList = new ArrayList<Double>();			
				currentPredictionList = currentPrediction.get(node.getId());				
				boolean hazardRate_Flag = false;
				int ftahostID;
				ArrayList<Integer>correlatedProvisionedNodes = new ArrayList<Integer>();
				//boolean consolidationHappened_Flag = false;
				if(currentPredictionList == null || currentPredictionList.isEmpty()){							
					hazardRate_Flag = true;
					}			
				if(hazardRate_Flag == true){					
					int ftaNodeID;
					double hazardRateNode = 0.0;
					ftaNodeID = HostMapping.getFTAhostID(node.getId());
					if(RunTimeConstants.traceType.equals("LANL")){
						hazardRateNode = freadLANL.getMaxHazardRate(ftaNodeID);
					}
					if(RunTimeConstants.traceType.equals("Grid5000")){
						hazardRateNode = freadGrid5000.getMaxHazardRate(ftaNodeID);
					}
					int vmCount;
					boolean consolidationFlag = false;					
					vmCount = node.getVmList().size();
					//In this if condition, VMs are moving out from the host on which a VM got destroyed recently to some other host 					
				if(consolidationFlag == false)
				{						
					vmMigrationList = new ArrayList<Vm>();
					for(int i=0;i<vmCount; i++){
						vmMigrationList.add(node.getVmList().get(i));
					}
					if(RunTimeConstants.failureCorrelationConsolidation == true){												
						String nodeLocation;										
						ArrayList<Integer>correlatedNodes = new ArrayList<Integer>();	
						int provisionedFtaNodeID;
						nodeLocation = freadGrid5000.getClusterNameForNode(ftaNodeID);
						correlatedNodes.addAll(kMeans.getCorrelatedNodes(nodeLocation, ftaNodeID));
						for(int i=0; i<correlatedNodes.size(); i++){
							if(correlatedNodes.get(i).equals(ftaNodeID)){
								correlatedNodes.remove(i);
								break;
							}
						}	
						for(int i=0; i<correlatedNodes.size(); i++){
							for(int j=0; j<getHostList().size(); j++){
								provisionedFtaNodeID = HostMapping.getFTAhostID(getHostList().get(j).getId());
								if(provisionedFtaNodeID == correlatedNodes.get(i)){
									correlatedProvisionedNodes.add(provisionedFtaNodeID);
								}
							}								
						}
					}	
					for(int i=0; i<getHostList().size(); i++){
						Host host;
						int freePeCount;
						int vmCountHost;
						int ftaHostID;
						boolean flagLocal = false;
						host = getHostList().get(i);
						if(host == node){
							continue;
						}
						if(RunTimeConstants.failureCorrelationConsolidation == true){
							int hostFtaID;
							hostFtaID = HostMapping.getFTAhostID(host.getId());
							for(int j=0; j<correlatedProvisionedNodes.size(); j++){										
								if(hostFtaID == correlatedProvisionedNodes.get(j)){
									flagLocal = true;
									break;
								}
							}
							if(flagLocal == true){
								continue;
							}								
						}
						if(IdleTime.checkExpectedFailingHost(host.getId())==true || IdleTime.checkHostActiveStatus(host.getId())==false){
							continue;
						}
						vmCountHost = host.getVmList().size();
						freePeCount = host.getNumberOfPes()-vmCountHost;
						if(freePeCount >= vmCount){																
							ftaHostID = HostMapping.getFTAhostID(host.getId());
							double hazardRateHost = 0.0;	
							if(RunTimeConstants.traceType.equals("LANL")){
								hazardRateHost = freadLANL.getMaxHazardRate(ftaHostID);
							}
							if(RunTimeConstants.traceType.equals("Grid5000")){
								hazardRateHost = freadGrid5000.getMaxHazardRate(ftaHostID);
							}
							if(hazardRateHost<hazardRateNode){
								Vm vm;
								consolidationFlag = true;
								ArrayList<Integer>cletIDList;	
								/*
								if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4){
									ftahostID = HostMapping.getFTAhostID(node.getId());
									consolidationCheckpointing = true;									
									createCheckpoints(ftahostID);
								}
								if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 ){
									ftahostID = HostMapping.getFTAhostID(node.getId());
									consolidationCheckpointing = true;									
									createCheckpoints(ftahostID);
								}
								*/
								if(RunTimeConstants.faultToleranceMechanism == 4 || RunTimeConstants.faultToleranceMechanism == 1 || (RunTimeConstants.faultToleranceMechanism == 2 && RunTimeConstants.failureCorrelationWithRstrChkpt == true)){	
									ftahostID = HostMapping.getFTAhostID(node.getId());
									consolidationCheckpointing = true;																			
									createCheckpoints(ftahostID);
								//	consolidationCheckpointing = false;
								}
								for(int j=0; j<vmCount; j++){	
									int cletIDListSize;										
									vm = vmMigrationList.get(j);						
									if(lastVmConsolidationClock.containsKey(vm.getId())){
										if(lastVmConsolidationClock.get(vm.getId()).equals(CloudSim.clock())){											
											continue;
										}
									}
									cletIDList = new ArrayList<Integer>();
									cletIDListSize = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().size();
									for(int k=0; k<cletIDListSize; k++){
										cletIDList.add(((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getCloudletId());										
									}	
									for(int k=0; k<cletIDListSize; k++){								
										long soFarClet = 0;
										long length = 0;								
										long remainingLength;		
										long lastRemainingLength = 0;											
										remainingLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();								
										length = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getCloudletLength();									
										if(!cletLengthBackup.containsKey(cletIDList.get(k))){
											cletLengthBackup.put(cletIDList.get(k), length);
										}									
									//	if((RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1)||(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4)){										
											if(cletSoFarBeforeFailing.containsKey(cletIDList.get(k))){
												soFarClet = length - remainingLength;												
												lastRemainingLength = length;
												cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
												cletSoFarMap.put(cletIDList.get(k), soFarClet);
												soFarClet = soFarClet + cletSoFarBeforeFailing.get(cletIDList.get(k)) ;
												cletSoFarBeforeFailing.remove(cletIDList.get(k));
											}								
											else{
												lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
												soFarClet = lastRemainingLength - remainingLength;										
												cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
												cletSoFarMap.put(cletIDList.get(k), soFarClet);												
											}										
									//	}										
										vmRecord.setCletMigrationHostRecord(cletIDList.get(k), node.getId());								
										vmRecord.setVmExecutionDurationPerHostRecord(cletIDList.get(k), soFarClet);
										cletMigrated.put(cletIDList.get(k), true);
									}
									lastVmConsolidationClock.put(vm.getId(), CloudSim.clock());										
									
									getVmAllocationPolicy().deallocateHostForVm(vm);														
									vm.setInMigration(true);
									
									boolean result = getVmAllocationPolicy().allocateHostForVm(vm, host);
									if (!result) {
										Log.printLine("[Datacenter.processVmMigrate] VM allocation to the destination host failed");										
										break;
									}
									Log.formatLine("%.2f: Consolidation of VM #%d to Host #%d is completed", CloudSim.clock(), vm.getId(), vm.getHost().getId());
									vm.setInMigration(false);										
									consolidationCount = consolidationCount + 1;
									System.out.println("Consolidation count is " +consolidationCount);
									vmRecord.setVmCurrentHost(vm.getId(), vm.getHost().getId());
									List<Pe>peListNode = new ArrayList<Pe>();
									List<Integer>peIDListNode = new ArrayList<Integer>();
									peListNode = node.getPeList();
									for(int k=0; k<peListNode.size(); k++){
										peIDListNode.add(peListNode.get(k).getId());
									}
									IdleTime.setHostActiveStatus(node.getId(), false, peIDListNode);										
									double migrationOverhead;	
									double migrationDownTime;													
									migrationOverhead = mgOver.getMigrationOverhead(vm);
									migrationDownTime = mgOver.TotalDownTime(vm);
									Host newHost;
									newHost = vm.getHost();
									for(int k=0; k<cletIDListSize; k++){
										long remainingCletLength;
										double cletUtilization;	
										long soFarExecutedLength;
										long cletNewLength;
										long oldBackupLength;
										Cloudlet clet;																				
										soFarExecutedLength = cletSoFarMap.get(cletIDList.get(k));
										clet = broker.getCloudletFromID(cletIDList.get(k));
										cletNewLength = clet.getCloudletLength() + ((long)(migrationOverhead*1000)) +((long)(migrationDownTime*1000));											
										if(!cletLengthBackup.containsKey(cletIDList.get(k))){
											oldBackupLength = clet.getCloudletLength(); 
											oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
											cletLengthBackup.put(cletIDList.get(k), oldBackupLength);
										}
										else{
											oldBackupLength = cletLengthBackup.get(cletIDList.get(k)); 
											oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
											cletLengthBackup.put(cletIDList.get(k), oldBackupLength);
										}
										clet.setCloudletLength(cletNewLength);
										remainingCletLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();
										cletLastRemainingMap.put(cletIDList.get(k), remainingCletLength);
										MigrationOverheadTable.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
										DownTimeTable.setCloudletDownTimeforMigration(clet.getCloudletId(), migrationDownTime);
										migrationForCloudlets.put(cletIDList.get(k), soFarExecutedLength);							
										cletUtilization = cletNorm.getNormalizedLength(clet);
										ReliabilityCalculator(newHost, vm, cletUtilization);
										vmRecord.setCletMigrationHostRecordforDownTime(clet.getCloudletId(), newHost.getId());
										vmRecord.setCletMigrationDownTime(clet.getCloudletId(), migrationDownTime);	
										vmRecord.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
									}	
								//	consolidationHappened_Flag = true;
								}									
								break;
							}
						}					
					}
				}					
				// In this if condition, VMs from other host are moving in to the host where a VM got destroyed recently
				if(consolidationFlag == false){
					if(RunTimeConstants.failureCorrelationConsolidation == true){												
						String nodeLocation;										
						ArrayList<Integer>correlatedNodes = new ArrayList<Integer>();	
						int provisionedFtaNodeID;
						nodeLocation = freadGrid5000.getClusterNameForNode(ftaNodeID);
						correlatedNodes.addAll(kMeans.getCorrelatedNodes(nodeLocation, ftaNodeID));
						for(int i=0; i<correlatedNodes.size(); i++){
							if(correlatedNodes.get(i).equals(ftaNodeID)){
								correlatedNodes.remove(i);
								break;
							}
						}	
						for(int i=0; i<correlatedNodes.size(); i++){
							for(int j=0; j<getHostList().size(); j++){
								provisionedFtaNodeID = HostMapping.getFTAhostID(getHostList().get(j).getId());
								if(provisionedFtaNodeID == correlatedNodes.get(i)){
									correlatedProvisionedNodes.add(provisionedFtaNodeID);
								}
							}								
						}
					}
					for(int i=0; i<getHostList().size(); i++){
						Host host;
						int freePeCount;	
						int vmCountHost;
						int ftaHostID;
						boolean flagLocal = false;
						host = getHostList().get(i);							
						if(host == node){
							continue;
						}				
						if(RunTimeConstants.failureCorrelationConsolidation == true){
							//int hostFtaID;
							ftaHostID = HostMapping.getFTAhostID(host.getId());
							for(int j=0; j<correlatedProvisionedNodes.size(); j++){										
								if(ftaHostID == correlatedProvisionedNodes.get(j)){
									flagLocal = true;
									break;
								}
							}
							if(flagLocal == true){
								continue;
							}								
						}
						vmCountHost = host.getVmList().size();						
						if(vmCountHost == 0){
							continue;
						}							
						freePeCount = node.getNumberOfPes()-vmCount;							
						if(vmCountHost <= freePeCount){								
							ftaHostID = HostMapping.getFTAhostID(host.getId());
							double hazardRateHost = 0.0;		
							if(RunTimeConstants.traceType.equals("LANL")){
								hazardRateHost = freadLANL.getMaxHazardRate(ftaHostID);
							}
							if(RunTimeConstants.traceType.equals("Grid5000")){
								hazardRateHost = freadGrid5000.getMaxHazardRate(ftaHostID);
							}
							if(hazardRateHost>hazardRateNode){
								/*
								if(RunTimeConstants.predictionFlag == true &&  RunTimeConstants.faultToleranceMechanism == 4){	
									ftahostID = HostMapping.getFTAhostID(host.getId());
									consolidationCheckpointing = true;									
									createCheckpoints(ftahostID);
								}
								if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1){
									ftahostID = HostMapping.getFTAhostID(host.getId());
									consolidationCheckpointing = true;									
									createCheckpoints(ftahostID);
								}
								*/
								if(RunTimeConstants.faultToleranceMechanism == 4 || RunTimeConstants.faultToleranceMechanism == 1 || (RunTimeConstants.faultToleranceMechanism == 2 && RunTimeConstants.failureCorrelationWithRstrChkpt == true)){	
								//	ftahostID = HostMapping.getFTAhostID(host.getId());
									consolidationCheckpointing = true;																			
									createCheckpoints(ftaHostID);
								//	consolidationCheckpointing = false;
								}
								Vm vm;
								consolidationFlag = true;
								vmMigrationList = new ArrayList<Vm>();
								for(int j=0;j<vmCountHost; j++){
									vmMigrationList.add(host.getVmList().get(j));
								}
								ArrayList<Integer>cletIDList;
								for(int j=0; j<vmCountHost; j++){										
									int cletIDListSize;										
									vm = vmMigrationList.get(j);
									if(lastVmConsolidationClock.containsKey(vm.getId())){
										if(lastVmConsolidationClock.get(vm.getId()).equals(CloudSim.clock())){											
											continue;
										}
									}
									cletIDList = new ArrayList<Integer>();
									cletIDListSize = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().size();
									for(int k=0; k<cletIDListSize; k++){
										cletIDList.add(((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getCloudletId());										
									}	
									for(int k=0; k<cletIDListSize; k++){								
										long soFarClet = 0;
										long length = 0;								
										long remainingLength;			
										long lastRemainingLength = 0;										
										remainingLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();								
										length = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getCloudletLength();									
									//	if((RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1)||(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4)){										
											if(cletSoFarBeforeFailing.containsKey(cletIDList.get(k))){
												soFarClet = length - remainingLength;												
												lastRemainingLength = length;
												cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
												cletSoFarMap.put(cletIDList.get(k), soFarClet);
												soFarClet = soFarClet + cletSoFarBeforeFailing.get(cletIDList.get(k)) ;
												cletSoFarBeforeFailing.remove(cletIDList.get(k));
											}								
											else{
												lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
												soFarClet = lastRemainingLength - remainingLength;										
												cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
												cletSoFarMap.put(cletIDList.get(k), soFarClet);												
											}											
										//}										
										vmRecord.setCletMigrationHostRecord(cletIDList.get(k), host.getId());								
										vmRecord.setVmExecutionDurationPerHostRecord(cletIDList.get(k), soFarClet);
										cletMigrated.put(cletIDList.get(k), true);
									}
									lastVmConsolidationClock.put(vm.getId(), CloudSim.clock());										
									getVmAllocationPolicy().deallocateHostForVm(vm);									
									vm.setInMigration(true);									
									boolean result = getVmAllocationPolicy().allocateHostForVm(vm, node);
									if (!result) {
										Log.printLine("[Datacenter.processVmMigrate] VM allocation to the destination host failed");
										System.exit(0);
									}
									Log.formatLine("%.2f: Consolidation of VM #%d to Host #%d is completed", CloudSim.clock(), vm.getId(), vm.getHost().getId());										
									vm.setInMigration(false);
									consolidationCount = consolidationCount + 1;									
									vmRecord.setVmCurrentHost(vm.getId(), vm.getHost().getId());									
									List<Pe>peListHost = new ArrayList<Pe>();
									List<Integer>peIDListHost = new ArrayList<Integer>();
									peListHost = host.getPeList();
									for(int k=0; k<peListHost.size(); k++){
										peIDListHost.add(peListHost.get(k).getId());
									}
									IdleTime.setHostActiveStatus(host.getId(), false, peIDListHost);
									
									//Applying VM migration overheads on all the running cloudlets running on each VM on the given node.	
									double migrationOverhead;			
									double migrationDownTime;															
									migrationOverhead = mgOver.getMigrationOverhead(vm);
									migrationDownTime = mgOver.TotalDownTime(vm);
									Host newHost;
									newHost = vm.getHost();
									for(int k=0; k<cletIDListSize; k++){
										long remainingCletLength;
										double cletUtilization;		
										Cloudlet clet;
										long cletNewLength;
										long soFarExecutedLength;			
										long oldBackupLength;
										soFarExecutedLength = cletSoFarMap.get(cletIDList.get(k));
										clet = broker.getCloudletFromID(cletIDList.get(k));
										cletNewLength = clet.getCloudletLength() + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));											
										if(!cletLengthBackup.containsKey(cletIDList.get(k))){
											oldBackupLength = clet.getCloudletLength(); 
											oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
											cletLengthBackup.put(cletIDList.get(k), oldBackupLength);
										}
										else{
											oldBackupLength = cletLengthBackup.get(cletIDList.get(k)); 
											oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
											cletLengthBackup.put(cletIDList.get(k), oldBackupLength);
										}
										clet.setCloudletLength(cletNewLength);
										remainingCletLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();									
										cletLastRemainingMap.put(cletIDList.get(k), remainingCletLength);							
										MigrationOverheadTable.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
										DownTimeTable.setCloudletDownTimeforMigration(clet.getCloudletId(), migrationDownTime);
										migrationForCloudlets.put(cletIDList.get(k), soFarExecutedLength);							
										cletUtilization = cletNorm.getNormalizedLength(clet);
										ReliabilityCalculator(newHost, vm, cletUtilization);
										vmRecord.setCletMigrationHostRecordforDownTime(clet.getCloudletId(), newHost.getId());
										vmRecord.setCletMigrationDownTime(clet.getCloudletId(), migrationDownTime);	
										vmRecord.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
									}	
									//consolidationHappened_Flag = true;
								}								
								break;
							}
						}					
					}
				}			
			}
			else{					
				double nextFailurePredictionNode = currentPredictionList.get(0);
				int vmCount;
				int ftaNodeID;
				boolean consolidationFlag = false;
				vmCount = node.getVmList().size();
				ftaNodeID = HostMapping.getFTAhostID(node.getId());
				//In this if condition, VMs are moving out from the host on which a VM got destroyed recently to some other host 
				if(consolidationFlag == false)
				{
					vmMigrationList = new ArrayList<Vm>();
					for(int i=0;i<vmCount; i++){
						vmMigrationList.add(node.getVmList().get(i));
					}
					if(RunTimeConstants.failureCorrelationConsolidation == true){												
						String nodeLocation;										
						ArrayList<Integer>correlatedNodes = new ArrayList<Integer>();	
						int provisionedFtaNodeID;
						nodeLocation = freadGrid5000.getClusterNameForNode(ftaNodeID);
						correlatedNodes.addAll(kMeans.getCorrelatedNodes(nodeLocation, ftaNodeID));
						for(int i=0; i<correlatedNodes.size(); i++){
							if(correlatedNodes.get(i).equals(ftaNodeID)){
								correlatedNodes.remove(i);
								break;
							}
						}	
						for(int i=0; i<correlatedNodes.size(); i++){
							for(int j=0; j<getHostList().size(); j++){
								provisionedFtaNodeID = HostMapping.getFTAhostID(getHostList().get(j).getId());
								if(provisionedFtaNodeID == correlatedNodes.get(i)){
									correlatedProvisionedNodes.add(provisionedFtaNodeID);
								}
							}								
						}
					}
					for(int i=0; i<getHostList().size(); i++){
						Host host;
						int freePeCount;
						int vmCountHost;
						boolean flagLocal = false;
						host = getHostList().get(i);
						if(host == node){
							continue;
						}
						if(RunTimeConstants.failureCorrelationConsolidation == true){
							int hostFtaID;
							hostFtaID = HostMapping.getFTAhostID(host.getId());
							for(int j=0; j<correlatedProvisionedNodes.size(); j++){										
								if(hostFtaID == correlatedProvisionedNodes.get(j)){
									flagLocal = true;
									break;
								}
							}
							if(flagLocal == true){
								continue;	
							}															
						}
						if(IdleTime.expectedFailingHost.containsKey(host.getId())||IdleTime.checkHostActiveStatus(host.getId())==false){
							continue;
						}
						vmCountHost = host.getVmList().size();							
						freePeCount = host.getNumberOfPes()-vmCountHost;
						if(freePeCount >= vmCount){								
							currentPredictionList = new ArrayList<Double>();
							currentPredictionList = currentPrediction.get(host.getId());
							if(currentPredictionList == null || currentPredictionList.isEmpty()){						
								continue;
							}
							double nextFailurePredictionHost = currentPredictionList.get(0);								
							if(nextFailurePredictionHost>nextFailurePredictionNode){
								/*
								if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4){
									ftahostID = HostMapping.getFTAhostID(node.getId());
									consolidationCheckpointing = true;									
									createCheckpoints(ftahostID);
								}
								if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1){
									ftahostID = HostMapping.getFTAhostID(node.getId());
									consolidationCheckpointing = true;								
									createCheckpoints(ftahostID);
								}
								*/
								if(RunTimeConstants.faultToleranceMechanism == 4 || RunTimeConstants.faultToleranceMechanism == 1 || (RunTimeConstants.faultToleranceMechanism == 2 && RunTimeConstants.failureCorrelationWithRstrChkpt == true)){	
									ftahostID = HostMapping.getFTAhostID(node.getId());
									consolidationCheckpointing = true;																			
									createCheckpoints(ftahostID);
								//	consolidationCheckpointing = false;
								}								
								Vm vm;
								consolidationFlag = true;
								ArrayList<Integer>cletIDList;
								for(int j=0; j<vmCount; j++){
									int cletIDListSize;	
									vm = vmMigrationList.get(j);
									if(lastVmConsolidationClock.containsKey(vm.getId())){
										if(lastVmConsolidationClock.get(vm.getId()).equals(CloudSim.clock())){											
											continue;
										}
									}
									cletIDList = new ArrayList<Integer>();
									cletIDListSize = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().size();
									for(int k=0; k<cletIDListSize; k++){
										cletIDList.add(((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getCloudletId());										
									}									
									for(int k=0; k<cletIDListSize; k++){								
										long soFarClet = 0;
										long length = 0;								
										long remainingLength;	
										long lastRemainingLength = 0;									
										remainingLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();
										length = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getCloudletLength();										
									//	if((RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1)||(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4)){										
											if(cletSoFarBeforeFailing.containsKey(cletIDList.get(k))){
												soFarClet = length - remainingLength;												
												lastRemainingLength = length;
												cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
												cletSoFarMap.put(cletIDList.get(k), soFarClet);
												soFarClet = soFarClet + cletSoFarBeforeFailing.get(cletIDList.get(k)) ;
												cletSoFarBeforeFailing.remove(cletIDList.get(k));
											}								
											else{
												lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
												soFarClet = lastRemainingLength - remainingLength;										
												cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
												cletSoFarMap.put(cletIDList.get(k), soFarClet);												
											}								
									//	}										
										vmRecord.setCletMigrationHostRecord(cletIDList.get(k), node.getId());										
										vmRecord.setVmExecutionDurationPerHostRecord(cletIDList.get(k), soFarClet);
										cletMigrated.put(cletIDList.get(k), true);
									}										
									lastVmConsolidationClock.put(vm.getId(), CloudSim.clock());									
									getVmAllocationPolicy().deallocateHostForVm(vm);								
									vm.setInMigration(true);
									boolean result = getVmAllocationPolicy().allocateHostForVm(vm, host);
									if (!result) {
										Log.printLine("[Datacenter.processVmMigrate] VM allocation to the destination host failed");
										//System.exit(0);
										break;
									}
									Log.formatLine("%.2f: Consolidation of VM #%d to Host #%d is completed", CloudSim.clock(), vm.getId(), vm.getHost().getId());
									vm.setInMigration(false);			
									consolidationCount = consolidationCount + 1;									
									vmRecord.setVmCurrentHost(vm.getId(), vm.getHost().getId());										
									List<Pe>peListNode = new ArrayList<Pe>();
									List<Integer>peIDListNode = new ArrayList<Integer>();
									peListNode = node.getPeList();
									for(int k=0; k<peListNode.size(); k++){
										peIDListNode.add(peListNode.get(k).getId());
									}
									IdleTime.setHostActiveStatus(node.getId(), false, peIDListNode);										
									
									//Applying VM migration overheads on all the running cloudlets running on each VM on the given node.	
									double migrationOverhead;	
									double migrationDownTime;															
									migrationOverhead = mgOver.getMigrationOverhead(vm);		
									migrationDownTime = mgOver.TotalDownTime(vm);
									Host newHost;
									newHost = vm.getHost();
									for(int k=0; k<cletIDListSize; k++){											
										long remainingCletLength;											
										double cletUtilization;
										long soFarExecutedLength;											
										long cletNewLength;
										long oldBackupLength;
										Cloudlet clet;
										soFarExecutedLength = cletSoFarMap.get(cletIDList.get(k));
										clet = broker.getCloudletFromID(cletIDList.get(k));
										cletNewLength = clet.getCloudletLength() + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));											
										if(!cletLengthBackup.containsKey(cletIDList.get(k))){
											oldBackupLength = clet.getCloudletLength(); 
											oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
											cletLengthBackup.put(cletIDList.get(k), oldBackupLength);
										}
										else{
											oldBackupLength = cletLengthBackup.get(cletIDList.get(k)); 
											oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
											cletLengthBackup.put(cletIDList.get(k), oldBackupLength);
										}
										clet.setCloudletLength(cletNewLength);
										remainingCletLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();								
										cletLastRemainingMap.put(cletIDList.get(k), remainingCletLength);								
										MigrationOverheadTable.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
										DownTimeTable.setCloudletDownTimeforMigration(clet.getCloudletId(), migrationDownTime);										
										migrationForCloudlets.put(cletIDList.get(k), soFarExecutedLength);							
										cletUtilization = cletNorm.getNormalizedLength(clet);
										ReliabilityCalculator(newHost, vm, cletUtilization);
										vmRecord.setCletMigrationHostRecordforDownTime(clet.getCloudletId(), newHost.getId());
										vmRecord.setCletMigrationDownTime(clet.getCloudletId(), migrationDownTime);	
										vmRecord.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
									}
								//	consolidationHappened_Flag = true;
								}									
								break;
							}
						}					
					}
				}
				// In this if condition, VMs from other host are moving in to the host where a VM got destroyed recently
				if(consolidationFlag == false){
					if(RunTimeConstants.failureCorrelationConsolidation == true){												
						String nodeLocation;										
						ArrayList<Integer>correlatedNodes = new ArrayList<Integer>();	
						int provisionedFtaNodeID;
						nodeLocation = freadGrid5000.getClusterNameForNode(ftaNodeID);
						correlatedNodes.addAll(kMeans.getCorrelatedNodes(nodeLocation, ftaNodeID));
						for(int i=0; i<correlatedNodes.size(); i++){
							if(correlatedNodes.get(i).equals(ftaNodeID)){
								correlatedNodes.remove(i);
								break;
							}
						}	
						for(int i=0; i<correlatedNodes.size(); i++){
							for(int j=0; j<getHostList().size(); j++){
								provisionedFtaNodeID = HostMapping.getFTAhostID(getHostList().get(j).getId());
								if(provisionedFtaNodeID == correlatedNodes.get(i)){
									correlatedProvisionedNodes.add(provisionedFtaNodeID);
								}
							}								
						}
					}
					for(int i=0; i<getHostList().size(); i++){
						Host host;
						int freePeCount;	
						int vmCountHost;
						boolean flagLocal = false;
						host = getHostList().get(i);						
						if(host == node){
							continue;
						}		
						if(RunTimeConstants.failureCorrelationConsolidation == true){
							int hostFtaID;
							hostFtaID = HostMapping.getFTAhostID(host.getId());
							for(int j=0; j<correlatedProvisionedNodes.size(); j++){										
								if(hostFtaID == correlatedProvisionedNodes.get(j)){
									flagLocal = true;
									break;
								}
							}
							if(flagLocal == true){
								continue;
							}								
						}
						vmCountHost = host.getVmList().size();
						if(vmCountHost == 0){
							continue;
						}							
						freePeCount = node.getNumberOfPes()-vmCount;							
						if(vmCountHost <= freePeCount){								
							currentPredictionList = new ArrayList<Double>();
							currentPredictionList = currentPrediction.get(host.getId());
							if(currentPredictionList == null || currentPredictionList.isEmpty()){
								continue;
							}
							double nextFailurePredictionHost = currentPredictionList.get(0);
							if(nextFailurePredictionHost<nextFailurePredictionNode){									
								/*
								if(RunTimeConstants.predictionFlag == true &&  RunTimeConstants.faultToleranceMechanism == 4){
									ftahostID = HostMapping.getFTAhostID(host.getId());
									consolidationCheckpointing = true;										
									createCheckpoints(ftahostID);
								}
								if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 ){
									ftahostID = HostMapping.getFTAhostID(host.getId());
									consolidationCheckpointing = true;									
									createCheckpoints(ftahostID);
								}
								*/
								if(RunTimeConstants.faultToleranceMechanism == 4 || RunTimeConstants.faultToleranceMechanism == 1 || (RunTimeConstants.faultToleranceMechanism == 2 && RunTimeConstants.failureCorrelationWithRstrChkpt == true)){	
									ftahostID = HostMapping.getFTAhostID(host.getId());
									consolidationCheckpointing = true;																			
									createCheckpoints(ftahostID);
								//	consolidationCheckpointing = false;
								}
								
								Vm vm;
								consolidationFlag = true;
								vmMigrationList = new ArrayList<Vm>();
								ArrayList<Integer>cletIDList;
								for(int j=0;j<vmCountHost; j++){
									vmMigrationList.add(host.getVmList().get(j));
								}
								for(int j=0; j<vmCountHost; j++){										
									int cletIDListSize;										
									vm = vmMigrationList.get(j);
									if(lastVmConsolidationClock.containsKey(vm.getId())){
										if(lastVmConsolidationClock.get(vm.getId()).equals(CloudSim.clock())){											
											continue;
										}
									}
									cletIDList = new ArrayList<Integer>();
									cletIDListSize = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().size();
									for(int k=0; k<cletIDListSize; k++){
										cletIDList.add(((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getCloudletId());										
									}
									for(int k=0; k<cletIDListSize; k++){								
										long soFarClet = 0;
										long length = 0;								
										long remainingLength;		
										long lastRemainingLength = 0;								
										remainingLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();								
										length = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getCloudletLength();										
									//	if((RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1)||(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4)){										
											if(cletSoFarBeforeFailing.containsKey(cletIDList.get(k))){
												soFarClet = length - remainingLength;												
												lastRemainingLength = length;
												cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
												cletSoFarMap.put(cletIDList.get(k), soFarClet);
												soFarClet = soFarClet + cletSoFarBeforeFailing.get(cletIDList.get(k)) ;
												cletSoFarBeforeFailing.remove(cletIDList.get(k));
											}								
											else{
												lastRemainingLength = getCletLastRemaining(cletIDList.get(k));
												soFarClet = lastRemainingLength - remainingLength;										
												cletLastRemainingMap.put(cletIDList.get(k), remainingLength);
												cletSoFarMap.put(cletIDList.get(k), soFarClet);												
											}											
									//	}										
										vmRecord.setCletMigrationHostRecord(cletIDList.get(k), host.getId());									
										vmRecord.setVmExecutionDurationPerHostRecord(cletIDList.get(k), soFarClet);
										cletMigrated.put(cletIDList.get(k), true);
									}										
									lastVmConsolidationClock.put(vm.getId(), CloudSim.clock());								
									getVmAllocationPolicy().deallocateHostForVm(vm);									
									vm.setInMigration(true);
									boolean result = getVmAllocationPolicy().allocateHostForVm(vm, node);
									if (!result) {
										Log.printLine("[Datacenter.processVmMigrate] VM allocation to the destination host failed");
										System.exit(0);
									}
									Log.formatLine("%.2f: Consolidation of VM #%d to Host #%d is completed", CloudSim.clock(), vm.getId(), vm.getHost().getId());
									vm.setInMigration(false);
									consolidationCount = consolidationCount + 1;								
									vmRecord.setVmCurrentHost(vm.getId(), vm.getHost().getId());									
									List<Pe>peListHost = new ArrayList<Pe>();
									List<Integer>peIDListHost = new ArrayList<Integer>();
									peListHost = host.getPeList();
									for(int k=0; k<peListHost.size(); k++){
										peIDListHost.add(peListHost.get(k).getId());
									}
									IdleTime.setHostActiveStatus(host.getId(), false, peIDListHost);										
									
									//Applying VM migration overheads on all the running cloudlets running on each VM on the given node.	
									double migrationOverhead;									
									double migrationDownTime;															
									migrationOverhead = mgOver.getMigrationOverhead(vm);
									migrationDownTime = mgOver.TotalDownTime(vm);
									Host newHost;
									newHost = vm.getHost();
									for(int k=0; k<cletIDListSize; k++){										
										long remainingCletLength;
										double cletUtilization;											
										long soFarExecutedLength;											
										long cletNewLength;
										long oldBackupLength;
										Cloudlet clet;
										soFarExecutedLength = cletSoFarMap.get(cletIDList.get(k));
										clet = broker.getCloudletFromID(cletIDList.get(k));
										cletNewLength = clet.getCloudletLength() + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));															
										if(!cletLengthBackup.containsKey(cletIDList.get(k))){
											oldBackupLength = clet.getCloudletLength(); 
											oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
											cletLengthBackup.put(cletIDList.get(k), oldBackupLength);
										}
										else{
											oldBackupLength = cletLengthBackup.get(cletIDList.get(k)); 
											oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
											cletLengthBackup.put(cletIDList.get(k), oldBackupLength);
										}		
										clet.setCloudletLength(cletNewLength);
										remainingCletLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(k).getRemainingCloudletLength();								
										cletLastRemainingMap.put(cletIDList.get(k), remainingCletLength);							
										MigrationOverheadTable.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
										DownTimeTable.setCloudletDownTimeforMigration(clet.getCloudletId(), migrationDownTime);										
										migrationForCloudlets.put(cletIDList.get(k), soFarExecutedLength);							
										cletUtilization = cletNorm.getNormalizedLength(clet);
										ReliabilityCalculator(newHost, vm, cletUtilization);
										vmRecord.setCletMigrationHostRecordforDownTime(clet.getCloudletId(), newHost.getId());
										vmRecord.setCletMigrationDownTime(clet.getCloudletId(), migrationDownTime);	
										vmRecord.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
									}			
								//	consolidationHappened_Flag = true;
								}								
								break;
							}
						}					
					}
				}			
			}			
		}			
	}			
}
		
		
		public void processNewHost(Host host){		
			if(RunTimeConstants.test == true){
				boolean flagNewHost = false;
				int index = 0;
				for(int i=0;i<HostMapping.getHostMapSize();i++){
					index = i;
					flagNewHost = true;
					for(int j=0;j<failhostList.size();j++){
						if(failhostList.get(j)== HostMapping.getFTAhostID(i)){
							flagNewHost = false;
							break;
						}					
					}
				}
				if(flagNewHost==true){					
					failurePrediction = new ArrayList<Double>();
					currentPredictionList = new ArrayList<Double>();
					int ftaNodeID = HostMapping.getFTAhostID(index);
					failhostList.add(ftaNodeID);
					int datacenterNodeID = HostMapping.getHostID(ftaNodeID);
					failhostTable.put(ftaNodeID, datacenterNodeID);				
	//				System.out.println(ftaNodeID+ " is the FTA Node ID for which failure has been scheduled");
				//	firstFailureFlag.put(ftaNodeID, true);	
				//	firstRecoveryFlag.put(ftaNodeID, true);
				//	firstPredictionFlag.put(ftaNodeID, true);
				//	firstCheckpointFlag.put(ftaNodeID, true);
					int count_fail = 0;
					if(RunTimeConstants.traceType.equals("LANL")){
						freadLANL.preparestartTime(ftaNodeID);				
						freadLANL.preparestopTime(ftaNodeID);	
						freadLANL.prepareAvailstartTime(ftaNodeID);					
						freadLANL.prepareAvailstopTime(ftaNodeID);
						freadLANL.prepareDifference(ftaNodeID);
						MTBF.add(freadLANL.getMTBF(ftaNodeID));				
						MTTR.add(freadLANL.getMTTR(ftaNodeID));										
						count_fail=freadLANL.getFailCount(ftaNodeID);
					}
					if(RunTimeConstants.traceType.equals("Grid5000")){
						freadGrid5000.preparestartTime(ftaNodeID);				
						freadGrid5000.preparestopTime(ftaNodeID);	
						freadGrid5000.prepareAvailstartTime(ftaNodeID);					
						freadGrid5000.prepareAvailstopTime(ftaNodeID);
						freadGrid5000.prepareDifference(ftaNodeID);
						MTBF.add(freadGrid5000.getMTBF(ftaNodeID));				
						MTTR.add(freadGrid5000.getMTTR(ftaNodeID));										
						count_fail=freadGrid5000.getFailCount(ftaNodeID);
					}
					
					if(RunTimeConstants.predictionFlag == true && RunTimeConstants.syntheticPrediction == true){
						failPredictSynthetic.predictFailures(ftaNodeID);					
						failurePrediction = failPredictSynthetic.getPredictedTBF(ftaNodeID);
					}
					if(RunTimeConstants.predictionFlag == true && RunTimeConstants.syntheticPrediction == false){
						failPredict.predictFailures(ftaNodeID);
						failurePrediction = failPredict.getPredictedTBF(ftaNodeID);	
					}								
					if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism==1){	
						if(RunTimeConstants.traceType.equals("LANL")){
							createCheckpointInterval(hostList().get(index).getId(), (freadLANL.getMTBF(ftaNodeID)));	
						}
						if(RunTimeConstants.traceType.equals("Grid5000")){
							createCheckpointInterval(hostList().get(index).getId(), (freadGrid5000.getMTBF(ftaNodeID)));	
						}
						send(3, checkpointInterval.get(hostList().get(index).getId()), CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);
					}
					
					System.out.println("Number of failure events for node " +ftaNodeID+ " are " +count_fail);
					if(count_fail == 0){
						System.out.println(ftaNodeID+ " is not going to fail");
					}
					else{
						if(RunTimeConstants.considerLaterPrediction == true){
							if(RunTimeConstants.traceType.equals("LANL")){
								for(int j=0;j<count_fail;j++){						
									starttime = freadLANL.getstartTime();										
									endtime = freadLANL.getendTime();
									if(freadLANL.getFirstEvent(ftaNodeID)==0 && !(nodeWithFirstFailEventMap.containsKey(ftaNodeID))){
										nodeWithFirstFailEventMap.put(ftaNodeID, true);
									}
									else{
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == false){								
											predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);			
											currentPredictionList.add(predictionTime);
											failurePrediction.remove(0);						
											//	System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));							
											send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);		
											if(RunTimeConstants.failureInjection == true){
												//	System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);								
												//	System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock()+endtime));
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
											}
										}
									
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){								
											predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);			
											currentPredictionList.add(predictionTime);
											failurePrediction.remove(0);						
											//	System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));							
											send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);				
											if(RunTimeConstants.failureInjection == true){
												//	System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);								
												//	System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock()+endtime));
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
											}
										}
									
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);							
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
											}
										}
									
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 3){								
											predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);								
											currentPredictionList.add(predictionTime);								
											failurePrediction.remove(0);						
											//	System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));																
											send(3, predictionTime, CloudSimTags.VM_MIGRATION, ftaNodeID);
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);								
												//	System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));								
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);								
												//	System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + endtime));
											}
										}						
									
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.vmConsolidationFlag == true && RunTimeConstants.faultToleranceMechanism == 3){
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);									
												//		System.out.println("Failure " +j+ " for node " +j+ " is going to occur at " +(CloudSim.clock() + starttime));										
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);								
												//		System.out.println("Recovery for node " +j+ " is going to occur at " +(CloudSim.clock() + endtime));
											}
										}
									
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4){								
											predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);							
											currentPredictionList.add(predictionTime);							
											failurePrediction.remove(0);								
											//	System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));							
											send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);							
											send(3, predictionTime, CloudSimTags.VM_MIGRATION_CHECKPOINTING, ftaNodeID);		
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
												//	System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));								
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
												//	System.out.println("Recovery for node " +j+ " is going to occur at " +(CloudSim.clock() + endtime));
											}
										}						
									
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1){
											if(RunTimeConstants.failureInjection == true){
												//	System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);								
												//	System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock()+endtime));
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
											}
										}						
									
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 2){
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);								
												//	System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));								
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);							
												//	System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + endtime));
											}
										}	
									}
								}
							}							
							if(RunTimeConstants.traceType.equals("Grid5000")){
								for(int j=0;j<count_fail;j++){						
									starttime = freadGrid5000.getstartTime();										
									endtime = freadGrid5000.getendTime();
									if(freadGrid5000.getFirstEvent(ftaNodeID)==0 && !(nodeWithFirstFailEventMap.containsKey(ftaNodeID))){
										nodeWithFirstFailEventMap.put(ftaNodeID, true);
									}
									else{
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == false){								
											predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);			
											currentPredictionList.add(predictionTime);
											failurePrediction.remove(0);						
											//	System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));							
											send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);						
											if(RunTimeConstants.failureInjection == true){
												//	System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);								
												//	System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock()+endtime));
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
											}
										}
									
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){								
											predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);			
											currentPredictionList.add(predictionTime);
											failurePrediction.remove(0);						
											//	System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));							
											send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);								
											if(RunTimeConstants.failureInjection == true){
												//	System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);								
												//	System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock()+endtime));
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
											}
										}
									
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);							
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
											}
										}
									
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 3){								
											predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);								
											currentPredictionList.add(predictionTime);								
											failurePrediction.remove(0);						
											//	System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));																
											send(3, predictionTime, CloudSimTags.VM_MIGRATION, ftaNodeID);	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);								
												//	System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));								
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);								
												//	System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + endtime));
											}
										}						
									
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.vmConsolidationFlag == true && RunTimeConstants.faultToleranceMechanism == 3){	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);									
												//		System.out.println("Failure " +j+ " for node " +j+ " is going to occur at " +(CloudSim.clock() + starttime));										
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);								
												//		System.out.println("Recovery for node " +j+ " is going to occur at " +(CloudSim.clock() + endtime));
											}
										}
									
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4){								
											predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);							
											currentPredictionList.add(predictionTime);							
											failurePrediction.remove(0);								
											//	System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));							
											send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);							
											send(3, predictionTime, CloudSimTags.VM_MIGRATION_CHECKPOINTING, ftaNodeID);	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
												//	System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));								
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
												//	System.out.println("Recovery for node " +j+ " is going to occur at " +(CloudSim.clock() + endtime));			
											}
										}						
									
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1){
											if(RunTimeConstants.failureInjection == true){
												//	System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);								
												//	System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock()+endtime));
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
											}
										}						
									
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 2){	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);								
												//	System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));								
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);							
												//	System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + endtime));
											}
										}	
									}
								}
							}
						}
						else{
							if(RunTimeConstants.traceType.equals("LANL")){
								for(int j=0;j<count_fail;j++){						
									starttime = freadLANL.getstartTime();										
									endtime = freadLANL.getendTime();
									if(freadLANL.getFirstEvent(ftaNodeID)==0 && !(nodeWithFirstFailEventMap.containsKey(ftaNodeID))){
										nodeWithFirstFailEventMap.put(ftaNodeID, true);
									}
									else{
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == false){								
											predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);			
									//		currentPredictionList.add(predictionTime);
											failurePrediction.remove(0);	
											if(predictionTime > starttime){
												if(RunTimeConstants.failureInjection == true){
												//System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);								
												//	System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock()+endtime));
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
												}
											}
											else{
												currentPredictionList.add(predictionTime);
//												System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));							
												send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);		
												if(RunTimeConstants.failureInjection == true){
													//	System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);								
													//	System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock()+endtime));
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
												}
											}																	
										}
									
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){								
											predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);			
										//	currentPredictionList.add(predictionTime);
											failurePrediction.remove(0);	
											if(predictionTime > starttime){
												if(RunTimeConstants.failureInjection == true){
//													System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);								
													//	System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock()+endtime));
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
												}
											}
											else{
												currentPredictionList.add(predictionTime);
//												System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));							
												send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);								
												if(RunTimeConstants.failureInjection == true){
													//	System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);								
													//	System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock()+endtime));
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
												}
											}																	
										}
									
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);							
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
											}
										}
									
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 3){								
											predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);								
										//	currentPredictionList.add(predictionTime);								
											failurePrediction.remove(0);		
											if(predictionTime > starttime){
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);								
													//	System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));								
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);								
													//	System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + endtime));
												}
											}
											else{
												currentPredictionList.add(predictionTime);
//												System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));																
												send(3, predictionTime, CloudSimTags.VM_MIGRATION, ftaNodeID);				
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);								
													//	System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));								
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);								
													//	System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + endtime));
												}
											}										
										}						
									
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.vmConsolidationFlag == true && RunTimeConstants.faultToleranceMechanism == 3){
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);									
												//		System.out.println("Failure " +j+ " for node " +j+ " is going to occur at " +(CloudSim.clock() + starttime));										
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);								
												//		System.out.println("Recovery for node " +j+ " is going to occur at " +(CloudSim.clock() + endtime));
											}
										}
									
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4){								
											predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);							
										//	currentPredictionList.add(predictionTime);							
											failurePrediction.remove(0);	
											if(predictionTime > starttime){
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
													//	System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));								
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
													//	System.out.println("Recovery for node " +j+ " is going to occur at " +(CloudSim.clock() + endtime));
												}
											}
											else{
												currentPredictionList.add(predictionTime);
//												System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));							
												send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);							
												send(3, predictionTime, CloudSimTags.VM_MIGRATION_CHECKPOINTING, ftaNodeID);	
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
													//	System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));								
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
													//	System.out.println("Recovery for node " +j+ " is going to occur at " +(CloudSim.clock() + endtime));
												}
											}																		
										}						
									
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1){
											if(RunTimeConstants.failureInjection == true){
												//	System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);								
												//	System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock()+endtime));
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
											}
										}						
									
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 2){		
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);								
											//	System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));								
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);							
											//	System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + endtime));
											}
										}	
									}
								}
							}							
							if(RunTimeConstants.traceType.equals("Grid5000")){
								for(int j=0;j<count_fail;j++){						
									starttime = freadGrid5000.getstartTime();										
									endtime = freadGrid5000.getendTime();
									if(freadGrid5000.getFirstEvent(ftaNodeID)==0 && !(nodeWithFirstFailEventMap.containsKey(ftaNodeID))){
										nodeWithFirstFailEventMap.put(ftaNodeID, true);
									}
									else{
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == false){								
											predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);			
									//		currentPredictionList.add(predictionTime);
											failurePrediction.remove(0);	
											if(predictionTime > starttime){
												if(RunTimeConstants.failureInjection == true){
//													System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);								
													//	System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock()+endtime));
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
												}
											}
											else{
												currentPredictionList.add(predictionTime);
//												System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));							
												send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);		
												if(RunTimeConstants.failureInjection == true){
													//	System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);								
													//	System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock()+endtime));
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
												}
											}																	
										}
									
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){								
											predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);			
										//	currentPredictionList.add(predictionTime);
											failurePrediction.remove(0);	
											if(predictionTime > starttime){
												if(RunTimeConstants.failureInjection == true){
//													System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);								
													//	System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock()+endtime));
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
												}
											}
											else{
												currentPredictionList.add(predictionTime);
//												System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));							
												send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);				
												if(RunTimeConstants.failureInjection == true){
													//	System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);								
													//	System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock()+endtime));
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
												}
											}																	
										}
									
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);							
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
											}
										}
									
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 3){								
											predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);								
										//	currentPredictionList.add(predictionTime);								
											failurePrediction.remove(0);		
											if(predictionTime > starttime){
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);								
													//	System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));								
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);								
													//	System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + endtime));
												}
											}
											else{
												currentPredictionList.add(predictionTime);
//												System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));																
												send(3, predictionTime, CloudSimTags.VM_MIGRATION, ftaNodeID);	
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);								
													//	System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));								
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);								
													//	System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + endtime));
												}
											}										
										}						
									
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.vmConsolidationFlag == true && RunTimeConstants.faultToleranceMechanism == 3){	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);									
												//		System.out.println("Failure " +j+ " for node " +j+ " is going to occur at " +(CloudSim.clock() + starttime));										
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);								
												//		System.out.println("Recovery for node " +j+ " is going to occur at " +(CloudSim.clock() + endtime));
											}
										}
									
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4){								
											predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);							
										//	currentPredictionList.add(predictionTime);							
											failurePrediction.remove(0);	
											if(predictionTime > starttime){
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
													//	System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));								
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
													//	System.out.println("Recovery for node " +j+ " is going to occur at " +(CloudSim.clock() + endtime));
												}
											}
											else{
												currentPredictionList.add(predictionTime);
//												System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));							
												send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);							
												send(3, predictionTime, CloudSimTags.VM_MIGRATION_CHECKPOINTING, ftaNodeID);	
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
													//	System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));								
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
													//	System.out.println("Recovery for node " +j+ " is going to occur at " +(CloudSim.clock() + endtime));
												}
											}																		
										}						
									
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1){
											if(RunTimeConstants.failureInjection == true){
												//	System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);								
												//	System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock()+endtime));
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
											}
										}						
									
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 2){	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);								
												//System.out.println("Failure " +j+ " for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + starttime));								
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);							
												//System.out.println("Recovery for node " +datacenterNodeID+ " is going to occur at " +(CloudSim.clock() + endtime));
											}
										}	
									}
								}
							}
						}						
						//counter_endTime = counter_endTime + 1;
						if(!failurePrediction.isEmpty()){
							failurePrediction = null;							
						}						
						currentPrediction.put(host.getId(), currentPredictionList);									
					}
				}	
			}
			else{			
				boolean flagNewHost = false;
				int index = 0;
				for(int i=0;i<HostMapping.getHostMapSize();i++){
					index = i;
					flagNewHost = true;
					for(int j=0;j<failhostList.size();j++){
						if(failhostList.get(j)== HostMapping.getFTAhostID(i)){
							flagNewHost = false;
							break;
						}					
					}
				}
				if(flagNewHost==true){					
					failurePrediction = new ArrayList<Double>();
					currentPredictionList = new ArrayList<Double>();
					int ftaNodeID = HostMapping.getFTAhostID(index);
					failhostList.add(ftaNodeID);
					int datacenterNodeID = HostMapping.getHostID(ftaNodeID);
					failhostTable.put(ftaNodeID, datacenterNodeID);	
					int count_fail=0;
					if(RunTimeConstants.traceType.equals("LANL")){
						count_fail=freadLANL.getFailCount(ftaNodeID);
						freadLANL.preparestartTime(ftaNodeID);				
						freadLANL.preparestopTime(ftaNodeID);	
						freadLANL.prepareAvailstartTime(ftaNodeID);					
						freadLANL.prepareAvailstopTime(ftaNodeID);
						freadLANL.prepareDifference(ftaNodeID);
						MTBF.add(freadLANL.getMTBF(ftaNodeID));				
						MTTR.add(freadLANL.getMTTR(ftaNodeID));	
					}
					if(RunTimeConstants.traceType.equals("Grid5000")){
						count_fail=freadGrid5000.getFailCount(ftaNodeID);
						freadGrid5000.preparestartTime(ftaNodeID);				
						freadGrid5000.preparestopTime(ftaNodeID);	
						freadGrid5000.prepareAvailstartTime(ftaNodeID);					
						freadGrid5000.prepareAvailstopTime(ftaNodeID);
						freadGrid5000.prepareDifference(ftaNodeID);
						MTBF.add(freadGrid5000.getMTBF(ftaNodeID));				
						MTTR.add(freadGrid5000.getMTTR(ftaNodeID));	
					}									
							
					if(RunTimeConstants.predictionFlag == true && RunTimeConstants.syntheticPrediction == true){
						failPredictSynthetic.predictFailures(ftaNodeID);					
						failurePrediction = failPredictSynthetic.getPredictedTBF(ftaNodeID);
					}
					if(RunTimeConstants.predictionFlag == true && RunTimeConstants.syntheticPrediction == false){
						failPredict.predictFailures(ftaNodeID);
						failurePrediction = failPredict.getPredictedTBF(ftaNodeID);	
					}						
					if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism==1){	
						if(RunTimeConstants.traceType.equals("LANL")){
							createCheckpointInterval(hostList().get(index).getId(), (freadLANL.getMTBF(ftaNodeID)));
						}
						if(RunTimeConstants.traceType.equals("Grid5000")){
							createCheckpointInterval(hostList().get(index).getId(), (freadGrid5000.getMTBF(ftaNodeID)));
						}	
						send(3, checkpointInterval.get(hostList().get(index).getId()), CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);	
					}			
					if(count_fail != 0){
						if(RunTimeConstants.considerLaterPrediction == true){
							if(RunTimeConstants.traceType.equals("LANL")){
								for(int j=0;j<count_fail;j++){						
									starttime = freadLANL.getstartTime();										
									endtime = freadLANL.getendTime();		
									if(freadLANL.getFirstEvent(ftaNodeID)==0 && !(nodeWithFirstFailEventMap.containsKey(ftaNodeID))){
										nodeWithFirstFailEventMap.put(ftaNodeID, true);
									}
									else{
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == false){								
											predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);			
											currentPredictionList.add(predictionTime);
											failurePrediction.remove(0);													
											send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);			
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);						
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
											}
										}						
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){								
											predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);			
											currentPredictionList.add(predictionTime);
											failurePrediction.remove(0);														
											send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);		
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);							
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
											}
										}						
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);							
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
											}
										}						
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 3){								
											predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);								
											currentPredictionList.add(predictionTime);								
											failurePrediction.remove(0);																					
											send(3, predictionTime, CloudSimTags.VM_MIGRATION, ftaNodeID);	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);												
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);
											}
										}					
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.vmConsolidationFlag == true && RunTimeConstants.faultToleranceMechanism == 3){
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);																
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);
											}
										}							
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4){								
											predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);							
											currentPredictionList.add(predictionTime);							
											failurePrediction.remove(0);														
											send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);							
											send(3, predictionTime, CloudSimTags.VM_MIGRATION_CHECKPOINTING, ftaNodeID);		
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);						
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
											}
										}						
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1){	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);				
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
											}
										}						
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 2){
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);														
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);
											}
										}	
									}
								}
							}
							
							if(RunTimeConstants.traceType.equals("Grid5000")){
								for(int j=0;j<count_fail;j++){						
									starttime = freadGrid5000.getstartTime();										
									endtime = freadGrid5000.getendTime();		
									if(freadGrid5000.getFirstEvent(ftaNodeID)==0 && !(nodeWithFirstFailEventMap.containsKey(ftaNodeID))){
										nodeWithFirstFailEventMap.put(ftaNodeID, true);
									}
									else{
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == false){								
											predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);			
											currentPredictionList.add(predictionTime);
											failurePrediction.remove(0);													
											send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);						
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
											}
										}						
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){								
											predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);			
											currentPredictionList.add(predictionTime);
											failurePrediction.remove(0);														
											send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);			
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);							
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
											}
										}						
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);							
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
											}
										}						
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 3){								
											predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);								
											currentPredictionList.add(predictionTime);								
											failurePrediction.remove(0);																					
											send(3, predictionTime, CloudSimTags.VM_MIGRATION, ftaNodeID);	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);												
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);
											}
										}					
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.vmConsolidationFlag == true && RunTimeConstants.faultToleranceMechanism == 3){		
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);																
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);
											}
										}							
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4){								
											predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);							
											currentPredictionList.add(predictionTime);							
											failurePrediction.remove(0);														
											send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);							
											send(3, predictionTime, CloudSimTags.VM_MIGRATION_CHECKPOINTING, ftaNodeID);	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);						
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
											}
										}						
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1){	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);				
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
											}
										}						
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 2){	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);														
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);
											}
										}	
									}
								}
							}
						}
						else{
							if(RunTimeConstants.traceType.equals("LANL")){
								for(int j=0;j<count_fail;j++){						
									starttime = freadLANL.getstartTime();										
									endtime = freadLANL.getendTime();		
									if(freadLANL.getFirstEvent(ftaNodeID)==0 && !(nodeWithFirstFailEventMap.containsKey(ftaNodeID))){
										nodeWithFirstFailEventMap.put(ftaNodeID, true);
									}
								else{
									if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == false){								
										predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);			
									//	currentPredictionList.add(predictionTime);
										failurePrediction.remove(0);				
										if(predictionTime > starttime){
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);						
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
											}
										}
										else{
											currentPredictionList.add(predictionTime);
											send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);		
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);						
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
											}
										}															
									}						
									if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){								
										predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);			
									//	currentPredictionList.add(predictionTime);
										failurePrediction.remove(0);	
										if(predictionTime > starttime){
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);							
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
											}
										}
										else{
											currentPredictionList.add(predictionTime);
											send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);		
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);							
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
											}
										}															
									}						
									if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){	
										if(RunTimeConstants.failureInjection == true){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);							
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
										}
									}						
									if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 3){								
										predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);								
									//	currentPredictionList.add(predictionTime);								
										failurePrediction.remove(0);		
										if(predictionTime > starttime){
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);												
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);
											}
										}
										else{
											currentPredictionList.add(predictionTime);
											send(3, predictionTime, CloudSimTags.VM_MIGRATION, ftaNodeID);
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);												
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);
											}
										}											
									}						
									if(RunTimeConstants.predictionFlag == false && RunTimeConstants.vmConsolidationFlag == true && RunTimeConstants.faultToleranceMechanism == 3){
										if(RunTimeConstants.failureInjection == true){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);																
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);
										}
									}							
									if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4){								
										predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);							
									//	currentPredictionList.add(predictionTime);							
										failurePrediction.remove(0);		
										if(predictionTime > starttime){
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);						
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
											}
										}
										else{
											currentPredictionList.add(predictionTime);
											send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);							
											send(3, predictionTime, CloudSimTags.VM_MIGRATION_CHECKPOINTING, ftaNodeID);	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);						
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
											}
										}																						
									}						
									if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1){	
										if(RunTimeConstants.failureInjection == true){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);				
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
										}
									}						
									if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 2){	
										if(RunTimeConstants.failureInjection == true){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);														
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);
										}
									}	
								}
							}
						}							
						if(RunTimeConstants.traceType.equals("Grid5000")){
							for(int j=0;j<count_fail;j++){						
								starttime = freadGrid5000.getstartTime();										
								endtime = freadGrid5000.getendTime();		
								if(freadGrid5000.getFirstEvent(ftaNodeID)==0 && !(nodeWithFirstFailEventMap.containsKey(ftaNodeID))){
									nodeWithFirstFailEventMap.put(ftaNodeID, true);
								}
							else{
								if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == false){								
									predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);			
								//	currentPredictionList.add(predictionTime);
									failurePrediction.remove(0);				
									if(predictionTime > starttime){
										if(RunTimeConstants.failureInjection == true){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);						
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
										}
									}
									else{
										currentPredictionList.add(predictionTime);
										send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);		
										if(RunTimeConstants.failureInjection == true){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);						
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
										}
									}															
								}						
								if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){								
									predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);			
								//	currentPredictionList.add(predictionTime);
									failurePrediction.remove(0);	
									if(predictionTime > starttime){
										if(RunTimeConstants.failureInjection == true){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);							
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
										}
									}
									else{
										currentPredictionList.add(predictionTime);
										send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);			
										if(RunTimeConstants.failureInjection == true){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);							
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
										}
									}															
								}						
								if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){		
									if(RunTimeConstants.failureInjection == true){
										send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);							
										send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
									}
								}						
								if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 3){								
									predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);								
								//	currentPredictionList.add(predictionTime);								
									failurePrediction.remove(0);		
									if(predictionTime > starttime){
										if(RunTimeConstants.failureInjection == true){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);												
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);
										}
									}
									else{
										currentPredictionList.add(predictionTime);
										send(3, predictionTime, CloudSimTags.VM_MIGRATION, ftaNodeID);		
										if(RunTimeConstants.failureInjection == true){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);												
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);
										}
									}											
								}						
								if(RunTimeConstants.predictionFlag == false && RunTimeConstants.vmConsolidationFlag == true && RunTimeConstants.faultToleranceMechanism == 3){			
									if(RunTimeConstants.failureInjection == true){
										send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);																
										send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);
									}
								}							
								if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4){								
									predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);							
								//	currentPredictionList.add(predictionTime);							
									failurePrediction.remove(0);		
									if(predictionTime > starttime){
										if(RunTimeConstants.failureInjection == true){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);						
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
										}
									}
									else{
										currentPredictionList.add(predictionTime);
										send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, ftaNodeID);							
										send(3, predictionTime, CloudSimTags.VM_MIGRATION_CHECKPOINTING, ftaNodeID);			
										if(RunTimeConstants.failureInjection == true){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);						
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, ftaNodeID);
										}
									}																						
								}						
								if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1){		
									if(RunTimeConstants.failureInjection == true){
										send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, ftaNodeID);				
										send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, ftaNodeID);
									}
								}						
								if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 2){
									if(RunTimeConstants.failureInjection == true){
										send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, ftaNodeID);														
										send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, ftaNodeID);
									}
								}	
							}
						}
					}
				}						
					if(!failurePrediction.isEmpty()){
						failurePrediction = null;							
					}					
					currentPrediction.put(host.getId(), currentPredictionList);											
				}
			}		
		}			
	}
		
	public List<Vm>vmMigrationList;		
	HashMap<Integer, Long> migrationForCloudlets = new HashMap<Integer, Long>();
		
	//HashMap<Integer, Boolean>expectedFailingNode = new HashMap<Integer, Boolean>();
	//int counter = 0;
	int countforcheck = 0;
	HashMap<Integer, Long>cletLastRemainingMap = new HashMap<Integer, Long>();
	HashMap<Integer, Long>cletSoFarMap = new HashMap<Integer, Long>();
	HashMap<Integer, Double>vmMigrationDownTime = new HashMap<Integer, Double>();
	HashMap<Integer, Boolean>cletMigrated = new HashMap<Integer, Boolean>();
	HashMap<Integer, Boolean>instantCorrelatedMigration = new HashMap<Integer, Boolean>();
	int vmcount = 0;
	boolean correlationCounterFlag = false;	
	//ArrayList<Long>cletLastSofarList;
	public void processVmMigrate(int ftaNodeID){	
	//	if(firstPredictionFlag.get(ftaNodeID)==true){
	//		System.out.println("This event is for first migration which needs to be ignored");
	//		firstPredictionFlag.put(ftaNodeID, false);
	//	}
	//	else{
		//if(ftaNodeID == 885){
		//	System.out.println("Check");
	//	}
		if(RunTimeConstants.test == true){
			int nodeID = 0;
			Host node;		
			vmMigrationList = new ArrayList<Vm>();
			int vmListSize;			
			for(int j=0;j<hostList().size();j++){
				if(failhostTable.containsKey(ftaNodeID)){
					nodeID=failhostTable.get(ftaNodeID);
					break;
				}			
			}		
			node = hostList().get(nodeID); // Node that is set to be failed	
			int counterLocal = 0;
			if(RunTimeConstants.failureCorrelation == true && RunTimeConstants.faultToleranceMechanism == 3 && !(instantCorrelatedMigration.containsKey(ftaNodeID))){
				ArrayList<Integer>correlatedNodes = new ArrayList<Integer>();					
				String nodeLocation;
				nodeLocation = freadGrid5000.getClusterNameForNode(ftaNodeID);
				correlatedNodes.addAll(kMeans.getCorrelatedNodes(nodeLocation, ftaNodeID));
				for(int i=0; i<correlatedNodes.size(); i++){
					if(correlatedNodes.get(i).equals(ftaNodeID)){
						correlatedNodes.remove(i);
						break;
					}
				}
				for(int i=0; i<correlatedNodes.size(); i++){
					if(failhostTable.containsKey(correlatedNodes.get(i))){
						Host hostLocal;
						int nodeIDLocal;
						nodeIDLocal = failhostTable.get(correlatedNodes.get(i));
						hostLocal = hostList().get(nodeIDLocal);						
						IdleTime.setExpectedFailingHost(hostLocal.getId(), true);
						for(int j=0; j<hostLocal.getNumberOfPes(); j++){
							int peID;
							peID = hostLocal.getPeList().get(j).getId();						
							IdleTime.setHostPeIdleTable(hostLocal.getId(), peID, true);
							IdleTime.setHostPeIdleClockTable(hostLocal.getId(), peID, CloudSim.clock());
						}
					}
				}				
				for(int i=0; i<correlatedNodes.size(); i++){
					if(failhostTable.containsKey(correlatedNodes.get(i))){						
						instantCorrelatedMigration.put(correlatedNodes.get(i), true);						
						counterLocal = counterLocal + 1;
						sendNow(3, CloudSimTags.VM_MIGRATION, correlatedNodes.get(i));
						//processVmMigrate(correlatedNodes.get(i));							
					}		
					//freadGrid5000.setCorrelatedMigraiton(false);
				}
				//freadGrid5000.setCorrelatedMigraiton(true);
				freadGrid5000.setCorrelatedMigraitonCounter(counterLocal);
				
				/**
				 * This part is here to avoid such a situation that if a migration is happening for correlated nodes, then their corresponding
				 * prediction values will remain intact. 
				 */
				if(RunTimeConstants.vmConsolidationFlag == true && RunTimeConstants.predictionFlag == true){
					currentPredictionList = new ArrayList<Double>();
					currentPredictionList = currentPrediction.get(node.getId());
					//if(currentPredictionList == null || currentPredictionList.isEmpty()){
					//	System.out.println("Check");
					//}
					if(currentPredictionList.size()!=0){					
						currentPredictionList.remove(0);
						currentPrediction.put(node.getId(), currentPredictionList);
					}
				}
			}			
			else{				
				if(instantCorrelatedMigration.containsKey(ftaNodeID)){
					instantCorrelatedMigration.remove(ftaNodeID);
					correlationCounterFlag = true;					
				}
			}
			
			//***********************************************************************
			//This part for VM consolidation
			
			if(RunTimeConstants.vmConsolidationFlag == true && RunTimeConstants.predictionFlag == true && RunTimeConstants.failureCorrelation == false){
				currentPredictionList = new ArrayList<Double>();
				currentPredictionList = currentPrediction.get(node.getId());
				currentPredictionList.remove(0);
				currentPrediction.put(node.getId(), currentPredictionList);
			}
			//************************************************************************	
			
			if(node.isFailed()){
				System.out.println(" Node # " +node.getId()+ " with FTA ID " +ftaNodeID+ " has already been failed. So no VM is running on it");
			}
			else
			{			
				vmListSize = node.getVmList().size();				
				for(int i=0; i<vmListSize; i++){
					vmMigrationList.add(node.getVmList().get(i));
				}						
				if(vmListSize == 0){
					System.out.println("No VM is running on Node # " +node.getId());
				//	if(node.getId() == 862){
				//		System.out.println("Check");
				//	}
				}
				else{			
					IdleTime.setExpectedFailingHost(node.getId(), true);
					for(int i=0; i<node.getNumberOfPes(); i++){
						int peID;
						peID = node.getPeList().get(i).getId();						
						IdleTime.setHostPeIdleTable(node.getId(), peID, true);
						IdleTime.setHostPeIdleClockTable(node.getId(), peID, CloudSim.clock());
					}				
					System.out.println("Host " +node.getId()+" is expecting to be failed");
					for(int i=0;i<vmListSize;i++){
						Vm vm;						
						int cletIDListSize;							
						double migrationOverhead;
						double migrationDownTime;
						//	boolean flag_migration = false;
						ArrayList<Integer>cletIDList = new ArrayList<Integer>();
						Cloudlet clet;						
						vm = vmMigrationList.get(i);						
						cletIDListSize = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().size();		
						if(cletIDListSize == 0){
							System.out.println("No cloudlet is running on VM # " +vm.getId()+ ". So vm will not be migrated");
							continue;
						}					
						System.out.println(CloudSim.clock()+ " VM which is going to migrate is " +vm.getId());					
						System.out.println(CloudSim.clock()+ " Currently VM # " +vm.getId()+ " is running on host # " +node.getId());	
						System.out.println(CloudSim.clock()+ " Migrating Cloudlets with VM "+vm.getId()+" are");
						for(int j=0; j<cletIDListSize; j++){							
							cletIDList.add(((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(j).getCloudletId());		
							System.out.println(cletIDList.get(j));
						//	if(cletIDList.get(j)==1631){
						//		System.out.println("Check");
						//	}
						}				
						if(migrationForCloudlets.isEmpty()){						
							System.out.println("This is the first time for VM migration. Let it happen :-)");							
							for(int j=0; j<cletIDListSize; j++){
								long soFarClet;
								long length = 0;								
								long remainingLength;		
								long lastRemainingLength;
								remainingLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(j).getRemainingCloudletLength();	
								length = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(j).getCloudletLength();
								//if(!cletLengthBackup.containsKey(cletIDList.get(j))){
								//	cletLengthBackup.put(cletIDList.get(j), length);
								//}
								if(cletSoFarBeforeFailing.containsKey(cletIDList.get(j))){
									soFarClet = length - remainingLength;
									//soFarClet = cletSoFarMap.get(cletIDList.get(j));
									lastRemainingLength = length;
									cletLastRemainingMap.put(cletIDList.get(j), remainingLength);
									cletSoFarMap.put(cletIDList.get(j), soFarClet);
									soFarClet = soFarClet + cletSoFarBeforeFailing.get(cletIDList.get(j)) ;
									System.out.println(CloudSim.clock()+ " Sofar execution of Cloudlet " +cletIDList.get(j)+ " on node "+node.getId()+" after failure overheads is " +soFarClet);										
									cletSoFarBeforeFailing.remove(cletIDList.get(j));
								}								
								else{
									lastRemainingLength = getCletLastRemaining(cletIDList.get(j));
									soFarClet = lastRemainingLength - remainingLength;										
									cletLastRemainingMap.put(cletIDList.get(j), remainingLength);
									cletSoFarMap.put(cletIDList.get(j), soFarClet);
									//soFarClet = cletSoFarMap.get(cletIDList.get(j));
								}							
								// Here I am saving the current host of the VM before migration in to class VmMigrationRecord.
								// Besides the host information, for how long the running cloudlets corresponding to migrating VM 
								// kept running on the current host has also been saved.  
								// By saving this information about each migrating VM, it will be easy to calculate the energy consumption accurately.			
								
								vmRecord.setCletMigrationHostRecord(cletIDList.get(j), node.getId());						
								System.out.println(CloudSim.clock()+ " Cloudlet " +cletIDList.get(j)+" is migrating from node " +node.getId());
								System.out.println(CloudSim.clock()+ " Length of Cloudlet "+cletIDList.get(j)+ " is " +length);
								System.out.println(CloudSim.clock()+ " Current Remaining Length of Cloudlet "+cletIDList.get(j)+ " without migration overheads is " +remainingLength);
								System.out.println(CloudSim.clock()+ " Last Remaining Length of Cloudlet "+cletIDList.get(j)+ " is " +lastRemainingLength);
								System.out.println(CloudSim.clock()+ " Sofar execution of Cloudlet " +cletIDList.get(j)+ " on node "+node.getId()+" is " +soFarClet);								
								vmRecord.setVmExecutionDurationPerHostRecord(cletIDList.get(j), soFarClet);								
								cletMigrated.put(cletIDList.get(j), true);
							}							
							int hostListSize = getHostList().size();
							getVmAllocationPolicy().deallocateHostForVm(vm);
							//node.removeMigratingInVm(vm);
							vm.setInMigration(true);							
							boolean result = getVmAllocationPolicy().allocateHostForVm(vm);
							if (!result) {
								Log.printLine("[Datacenter.processVmMigrate] VM allocation to the destination host failed");
								System.exit(0);
							}							
							Log.formatLine("%.2f: Migration of VM #%d to Host #%d is completed", CloudSim.clock(), vm.getId(), vm.getHost().getId());							
							vm.setInMigration(false);
						//	if(correlationCounterFlag == true){
						//		counterLocal = freadGrid5000.getCorrelatedMigrationCounter();
						//		counterLocal = counterLocal - 1;
						//		freadGrid5000.setCorrelatedMigraitonCounter(counterLocal);
						//		correlationCounterFlag = false;
						//	}
							totalVmMigrations = totalVmMigrations + 1;		
								
							vmRecord.setVmCurrentHost(vm.getId(), vm.getHost().getId());
							if(hostListSize < getHostList().size()){
								Host newHost;
								newHost = vm.getHost();
								newHost.setDatacenter(this);
								processNewHost(newHost);								
							}							
							//Applying VM migration overheads on all the running cloudlets running on each VM on the given node.										
							migrationOverhead = mgOver.getMigrationOverhead(vm);	
							migrationDownTime = mgOver.TotalDownTime(vm);						
							Host newHost;
							newHost = vm.getHost();						
							for(int j=0; j<cletIDListSize; j++){
								long remainingCletLength;
								double cletUtilization;
								long soFarExecutedLength;		
								long cletNewLength;
								long oldBackupLength;
								//remainingCletLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(j).getRemainingCloudletLength();								
								//soFarExecutedLength = cletLength - remainingCletLength;
								soFarExecutedLength = cletSoFarMap.get(cletIDList.get(j));
								clet = broker.getCloudletFromID(cletIDList.get(j));			
									
								System.out.println(CloudSim.clock()+ " Migration Overhead for Cloudlet "+cletIDList.get(j)+" is " +((long)(migrationOverhead*1000)));
								System.out.println(CloudSim.clock()+ " Migration Downtime Overhead for Cloudlet "+cletIDList.get(j)+" is " +((long)(migrationDownTime*1000)));									
								cletNewLength = clet.getCloudletLength() + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));					
									
								if(!cletLengthBackup.containsKey(cletIDList.get(j))){
									oldBackupLength = clet.getCloudletLength(); 
									oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
									cletLengthBackup.put(cletIDList.get(j), oldBackupLength);
								}
								else{
									oldBackupLength = cletLengthBackup.get(cletIDList.get(j)); 
									oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
									cletLengthBackup.put(cletIDList.get(j), oldBackupLength);
								}
								clet.setCloudletLength(cletNewLength);	
								remainingCletLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(j).getRemainingCloudletLength();
								System.out.println(CloudSim.clock()+ " New current Remaining Length of Cloudlet "+cletIDList.get(j)+ " after migration overheads is " +remainingCletLength);
								cletLastRemainingMap.put(cletIDList.get(j), remainingCletLength);
								MigrationOverheadTable.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
								DownTimeTable.setCloudletDownTimeforMigration(clet.getCloudletId(), migrationDownTime);								
								migrationForCloudlets.put(cletIDList.get(j), soFarExecutedLength);							
								cletUtilization = cletNorm.getNormalizedLength(clet);
								ReliabilityCalculator(newHost, vm, cletUtilization);
								vmRecord.setCletMigrationHostRecordforDownTime(clet.getCloudletId(), newHost.getId());
								vmRecord.setCletMigrationDownTime(clet.getCloudletId(), migrationDownTime);	
								vmRecord.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
							}							
						}
						else{							
							//	flag_migration = true;					
							//	if(flag_migration == true){							
							for(int j=0; j<cletIDListSize; j++){								
								long soFarClet;
								long length = 0;								
								long remainingLength;		
								long lastRemainingLength;
								long soFarCletNew;
								remainingLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(j).getRemainingCloudletLength();	
								length = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(j).getCloudletLength();
								//	if(cletLastRemainingMap.isEmpty()){										
									//	soFarClet = length - remainingLength;
									//	soFarCletNew = soFarClet;
									//	lastRemainingLength = length;
									//	cletLastRemainingMap.put(cletIDList.get(j), remainingLength);
									//	cletSoFarMap.put(cletIDList.get(j), soFarClet);
									//}else{
									//	if(cletLastRemainingMap.containsKey(cletIDList.get(j))){		
									//if(!cletLengthBackup.containsKey(cletIDList.get(j))){
									//	cletLengthBackup.put(cletIDList.get(j), length);
									//}
								if(cletSoFarBeforeFailing.containsKey(cletIDList.get(j))){
									soFarClet = length - remainingLength;
									soFarCletNew = soFarClet;
									lastRemainingLength = length;
									cletLastRemainingMap.put(cletIDList.get(j), remainingLength);
									cletSoFarMap.put(cletIDList.get(j), soFarClet);
									soFarClet = soFarClet + cletSoFarBeforeFailing.get(cletIDList.get(j));
									System.out.println(CloudSim.clock()+ " Sofar execution of Cloudlet " +cletIDList.get(j)+ " on node "+node.getId()+" after failure overheads is " +soFarClet);
									cletSoFarBeforeFailing.remove(cletIDList.get(j));
								}
								else{
									lastRemainingLength = getCletLastRemaining(cletIDList.get(j));
									soFarClet = lastRemainingLength - remainingLength;	
									soFarCletNew = soFarClet;
									cletLastRemainingMap.put(cletIDList.get(j), remainingLength);
									cletSoFarMap.put(cletIDList.get(j), soFarClet);
								}
									//	}
									//	else{											
									//		soFarClet = length - remainingLength;
									//		soFarCletNew = soFarClet;
									//		lastRemainingLength = length;
									//		cletLastRemainingMap.put(cletIDList.get(j), remainingLength);
									//		cletSoFarMap.put(cletIDList.get(j), soFarClet);
									//	}										
									//}								
									
								vmRecord.setCletMigrationHostRecord(cletIDList.get(j), node.getId());
								//vmRecord.setVmExecutionDurationPerHostRecord(cletIDList.get(j), cletLastSofarMap.get(cletIDList.get(j)));
								System.out.println(CloudSim.clock()+ " Cloudlet " +cletIDList.get(j)+" is migrating from node " +node.getId());
								System.out.println(CloudSim.clock()+ " Length of Cloudlet "+cletIDList.get(j)+ " is " +length);
								System.out.println(CloudSim.clock()+ " Current Remaining Length of Cloudlet "+cletIDList.get(j)+ " without migration overheads is " +remainingLength);
								System.out.println(CloudSim.clock()+ " Last Remaining Length of Cloudlet "+cletIDList.get(j)+ " is " +lastRemainingLength);
								System.out.println(CloudSim.clock()+ " Sofar execution of Cloudlet " +cletIDList.get(j)+ " on node "+node.getId()+" is " +soFarCletNew);
								vmRecord.setVmExecutionDurationPerHostRecord(cletIDList.get(j), soFarClet);		
								cletMigrated.put(cletIDList.get(j), true);
							}
								
							getVmAllocationPolicy().deallocateHostForVm(vm);
							//	node.removeMigratingInVm(vm);
							vm.setInMigration(true);		
							int hostListSize = hostList().size();
							boolean result = getVmAllocationPolicy().allocateHostForVm(vm);
							if (!result) {
								Log.printLine("[Datacenter.processVmMigrate] VM allocation to the destination host failed");
								System.exit(0);
							}							
							Log.formatLine("%.2f: Migration of VM #%d to Host #%d is completed", CloudSim.clock(), vm.getId(), vm.getHost().getId());
							vm.setInMigration(false);	
							//if(correlationCounterFlag == true){
							//	counterLocal = freadGrid5000.getCorrelatedMigrationCounter();
							//	counterLocal = counterLocal - 1;
							//	freadGrid5000.setCorrelatedMigraitonCounter(counterLocal);
							//	correlationCounterFlag = false;
							//}	
							totalVmMigrations = totalVmMigrations + 1;											
							vmRecord.setVmCurrentHost(vm.getId(), vm.getHost().getId());
							if(hostListSize < getHostList().size()){
								Host newHost;
								newHost = vm.getHost();
								processNewHost(newHost);									
							}								
							//Applying VM migration overheads on all the running cloudlets running on each VM on the given node.								
												
								migrationOverhead = mgOver.getMigrationOverhead(vm);	
								migrationDownTime = mgOver.TotalDownTime(vm);
								Host newHost;
								newHost = vm.getHost();				
								
								for(int j=0; j<cletIDListSize; j++){								
									long soFarExecutedLength;
									long remainingCletLength;									
									double cletUtilization;	
									long cletNewLength;
									long oldBackupLength;
								//	soFarExecutedLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(j).getsofarlength();									
									soFarExecutedLength = cletSoFarMap.get(cletIDList.get(j));
									clet = broker.getCloudletFromID(cletIDList.get(j));			
									
									System.out.println(CloudSim.clock()+ " Migration Overhead for Cloudlet "+cletIDList.get(j)+" is " +((long)(migrationOverhead*1000)));
									System.out.println(CloudSim.clock()+ " Migration Downtime Overhead for Cloudlet "+cletIDList.get(j)+" is " +((long)(migrationDownTime*1000)));
								//	remainingCletLength = remainingCletLength + ((long)(migrationOverhead*1000));									
								//	((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(j).setremaininglength(remainingCletLength);
									cletNewLength = clet.getCloudletLength() + ((long)(migrationOverhead*1000))+((long)(migrationDownTime*1000));				
									
									if(!cletLengthBackup.containsKey(cletIDList.get(j))){
										oldBackupLength = clet.getCloudletLength(); 
										oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
										cletLengthBackup.put(cletIDList.get(j), oldBackupLength);
									}
									else{
										oldBackupLength = cletLengthBackup.get(cletIDList.get(j)); 
										oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
										cletLengthBackup.put(cletIDList.get(j), oldBackupLength);
									}
									
									clet.setCloudletLength(cletNewLength);
									remainingCletLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(j).getRemainingCloudletLength();			
									System.out.println(CloudSim.clock()+ " New current Remaining Length of Cloudlet "+cletIDList.get(j)+ " after migration overheads is " +remainingCletLength);
									cletLastRemainingMap.put(cletIDList.get(j), remainingCletLength);
									MigrationOverheadTable.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
									DownTimeTable.setCloudletDownTimeforMigration(clet.getCloudletId(), migrationDownTime);
									//soFarExecutedLength = CloudletStatus.getsofarCloudlet(cletIDList.get(j));
									migrationForCloudlets.put(cletIDList.get(j), soFarExecutedLength);
									cletUtilization = cletNorm.getNormalizedLength(clet);
									ReliabilityCalculator(newHost, vm, cletUtilization);
									vmRecord.setCletMigrationHostRecordforDownTime(clet.getCloudletId(), newHost.getId());
									vmRecord.setCletMigrationDownTime(clet.getCloudletId(), migrationDownTime);			
									vmRecord.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
								}
							//}
						}
					}					
				}
				if(correlationCounterFlag == true){
					counterLocal = freadGrid5000.getCorrelatedMigrationCounter();
					counterLocal = counterLocal - 1;
					freadGrid5000.setCorrelatedMigraitonCounter(counterLocal);
					correlationCounterFlag = false;
				}				
			}
		}
		else{			//This is for non-test case
			int nodeID = 0;
			Host node;	
			vmMigrationList = new ArrayList<Vm>();
			int vmListSize;			
			for(int j=0;j<hostList().size();j++){
				if(failhostTable.containsKey(ftaNodeID)){
					nodeID=failhostTable.get(ftaNodeID);
					break;
				}			
			}		
			node = hostList().get(nodeID); // Node that is set to be failed	
			int counterLocal = 0;
			if(RunTimeConstants.failureCorrelation == true && RunTimeConstants.faultToleranceMechanism == 3 && !(instantCorrelatedMigration.containsKey(ftaNodeID))){
				ArrayList<Integer>correlatedNodes = new ArrayList<Integer>();					
				String nodeLocation;
				nodeLocation = freadGrid5000.getClusterNameForNode(ftaNodeID);
				correlatedNodes.addAll(kMeans.getCorrelatedNodes(nodeLocation, ftaNodeID));
				for(int i=0; i<correlatedNodes.size(); i++){
					if(correlatedNodes.get(i).equals(ftaNodeID)){
						correlatedNodes.remove(i);
						break;
					}
				}
				for(int i=0; i<correlatedNodes.size(); i++){
					if(failhostTable.containsKey(correlatedNodes.get(i))){
						Host hostLocal;
						int nodeIDLocal;
						nodeIDLocal = failhostTable.get(correlatedNodes.get(i));
						hostLocal = hostList().get(nodeIDLocal);						
						IdleTime.setExpectedFailingHost(hostLocal.getId(), true);
						for(int j=0; j<hostLocal.getNumberOfPes(); j++){
							int peID;
							peID = hostLocal.getPeList().get(j).getId();						
							IdleTime.setHostPeIdleTable(hostLocal.getId(), peID, true);
							IdleTime.setHostPeIdleClockTable(hostLocal.getId(), peID, CloudSim.clock());
						}
					}
				}				
				for(int i=0; i<correlatedNodes.size(); i++){
					if(failhostTable.containsKey(correlatedNodes.get(i))){						
						instantCorrelatedMigration.put(correlatedNodes.get(i), true);						
						counterLocal = counterLocal + 1;
						sendNow(3, CloudSimTags.VM_MIGRATION, correlatedNodes.get(i));											
					}				
				}				
				freadGrid5000.setCorrelatedMigraitonCounter(counterLocal);
				if(RunTimeConstants.vmConsolidationFlag == true && RunTimeConstants.predictionFlag == true){
					currentPredictionList = new ArrayList<Double>();
					currentPredictionList = currentPrediction.get(node.getId());
					if(currentPredictionList.size()!=0){
						currentPredictionList.remove(0);
						currentPrediction.put(node.getId(), currentPredictionList);
					}
				}	
			}			
			else{				
				if(instantCorrelatedMigration.containsKey(ftaNodeID)){
					instantCorrelatedMigration.remove(ftaNodeID);
					correlationCounterFlag = true;					
				}
			}			
			if(RunTimeConstants.vmConsolidationFlag == true && RunTimeConstants.predictionFlag == true && RunTimeConstants.failureCorrelation == false){
				currentPredictionList = new ArrayList<Double>();
				currentPredictionList = currentPrediction.get(node.getId());
				currentPredictionList.remove(0);
				currentPrediction.put(node.getId(), currentPredictionList);
			}			
			if(!node.isFailed())
			{			
				vmListSize = node.getVmList().size();				
				for(int i=0; i<vmListSize; i++){
					vmMigrationList.add(node.getVmList().get(i));
				}			
				if(vmListSize != 0){			
					IdleTime.setExpectedFailingHost(node.getId(), true);
					for(int i=0; i<node.getNumberOfPes(); i++){
						int peID;
						peID = node.getPeList().get(i).getId();						
						IdleTime.setHostPeIdleTable(node.getId(), peID, true);
						IdleTime.setHostPeIdleClockTable(node.getId(), peID, CloudSim.clock());
					}				
					for(int i=0;i<vmListSize;i++){
						Vm vm;						
						int cletIDListSize;							
						double migrationOverhead;
						double migrationDownTime;						
						ArrayList<Integer>cletIDList = new ArrayList<Integer>();
						Cloudlet clet;						
						vm = vmMigrationList.get(i);						
						cletIDListSize = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().size();		
						if(cletIDListSize == 0){							
							continue;
						}			
						for(int j=0; j<cletIDListSize; j++){							
							cletIDList.add(((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(j).getCloudletId());		
						}					
						if(migrationForCloudlets.isEmpty()){												
							for(int j=0; j<cletIDListSize; j++){
								long soFarClet;
								long length = 0;								
								long remainingLength;		
								long lastRemainingLength;
								remainingLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(j).getRemainingCloudletLength();	
								length = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(j).getCloudletLength();								
								if(cletSoFarBeforeFailing.containsKey(cletIDList.get(j))){
									soFarClet = length - remainingLength;									
									lastRemainingLength = length;
									cletLastRemainingMap.put(cletIDList.get(j), remainingLength);
									cletSoFarMap.put(cletIDList.get(j), soFarClet);
									soFarClet = soFarClet + cletSoFarBeforeFailing.get(cletIDList.get(j)) ;
									cletSoFarBeforeFailing.remove(cletIDList.get(j));
								}								
								else{
									lastRemainingLength = getCletLastRemaining(cletIDList.get(j));
									soFarClet = lastRemainingLength - remainingLength;										
									cletLastRemainingMap.put(cletIDList.get(j), remainingLength);
									cletSoFarMap.put(cletIDList.get(j), soFarClet);									
								}							
								vmRecord.setCletMigrationHostRecord(cletIDList.get(j), node.getId());						
								vmRecord.setVmExecutionDurationPerHostRecord(cletIDList.get(j), soFarClet);								
								cletMigrated.put(cletIDList.get(j), true);
							}							
							int hostListSize = getHostList().size();
							getVmAllocationPolicy().deallocateHostForVm(vm);							
							vm.setInMigration(true);							
							boolean result = getVmAllocationPolicy().allocateHostForVm(vm);
							if (!result) {
								Log.printLine("[Datacenter.processVmMigrate] VM allocation to the destination host failed");
								System.exit(0);
							}
							Log.formatLine("%.2f: Migration of VM #%d to Host #%d is completed", CloudSim.clock(), vm.getId(), vm.getHost().getId());							
							vm.setInMigration(false);							
							totalVmMigrations = totalVmMigrations + 1;						
							vmRecord.setVmCurrentHost(vm.getId(), vm.getHost().getId());
							if(hostListSize < getHostList().size()){
								Host newHost;
								newHost = vm.getHost();
								newHost.setDatacenter(this);
								processNewHost(newHost);								
							}														
							migrationOverhead = mgOver.getMigrationOverhead(vm);	
							migrationDownTime = mgOver.TotalDownTime(vm);						
							Host newHost;
							newHost = vm.getHost();						
							for(int j=0; j<cletIDListSize; j++){
								long remainingCletLength;
								double cletUtilization;
								long soFarExecutedLength;		
								long cletNewLength;
								long oldBackupLength;								
								soFarExecutedLength = cletSoFarMap.get(cletIDList.get(j));
								clet = broker.getCloudletFromID(cletIDList.get(j));						
								cletNewLength = clet.getCloudletLength() + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));								
								if(!cletLengthBackup.containsKey(cletIDList.get(j))){
									oldBackupLength = clet.getCloudletLength(); 
									oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
									cletLengthBackup.put(cletIDList.get(j), oldBackupLength);
								}
								else{
									oldBackupLength = cletLengthBackup.get(cletIDList.get(j)); 
									oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
									cletLengthBackup.put(cletIDList.get(j), oldBackupLength);
								}
								clet.setCloudletLength(cletNewLength);	
								remainingCletLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(j).getRemainingCloudletLength();
								cletLastRemainingMap.put(cletIDList.get(j), remainingCletLength);
								MigrationOverheadTable.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
								DownTimeTable.setCloudletDownTimeforMigration(clet.getCloudletId(), migrationDownTime);								
								migrationForCloudlets.put(cletIDList.get(j), soFarExecutedLength);							
								cletUtilization = cletNorm.getNormalizedLength(clet);
								ReliabilityCalculator(newHost, vm, cletUtilization);
								vmRecord.setCletMigrationHostRecordforDownTime(clet.getCloudletId(), newHost.getId());
								vmRecord.setCletMigrationDownTime(clet.getCloudletId(), migrationDownTime);	
								vmRecord.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
							}							
						}
						else{												
							for(int j=0; j<cletIDListSize; j++){								
								long soFarClet;
								long length = 0;								
								long remainingLength;		
								long lastRemainingLength;
								long soFarCletNew;
								remainingLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(j).getRemainingCloudletLength();	
								length = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(j).getCloudletLength();							
								if(cletSoFarBeforeFailing.containsKey(cletIDList.get(j))){
									soFarClet = length - remainingLength;
									soFarCletNew = soFarClet;
									lastRemainingLength = length;
									cletLastRemainingMap.put(cletIDList.get(j), remainingLength);
									cletSoFarMap.put(cletIDList.get(j), soFarClet);
									soFarClet = soFarClet + cletSoFarBeforeFailing.get(cletIDList.get(j));
									cletSoFarBeforeFailing.remove(cletIDList.get(j));
								}
								else{
									lastRemainingLength = getCletLastRemaining(cletIDList.get(j));
									soFarClet = lastRemainingLength - remainingLength;	
									soFarCletNew = soFarClet;
									cletLastRemainingMap.put(cletIDList.get(j), remainingLength);
									cletSoFarMap.put(cletIDList.get(j), soFarClet);
								}								
								vmRecord.setCletMigrationHostRecord(cletIDList.get(j), node.getId());
								vmRecord.setVmExecutionDurationPerHostRecord(cletIDList.get(j), soFarClet);		
								cletMigrated.put(cletIDList.get(j), true);
							}							
							getVmAllocationPolicy().deallocateHostForVm(vm);						
							vm.setInMigration(true);		
							int hostListSize = hostList().size();
							boolean result = getVmAllocationPolicy().allocateHostForVm(vm);
							if (!result) {
								Log.printLine("[Datacenter.processVmMigrate] VM allocation to the destination host failed");
								System.exit(0);
							}							
							Log.formatLine("%.2f: Migration of VM #%d to Host #%d is completed", CloudSim.clock(), vm.getId(), vm.getHost().getId());
							vm.setInMigration(false);
							totalVmMigrations = totalVmMigrations + 1;											
							vmRecord.setVmCurrentHost(vm.getId(), vm.getHost().getId());
							if(hostListSize < getHostList().size()){
								Host newHost;
								newHost = vm.getHost();
								processNewHost(newHost);									
							}								
							migrationOverhead = mgOver.getMigrationOverhead(vm);	
							migrationDownTime = mgOver.TotalDownTime(vm);
							Host newHost;
							newHost = vm.getHost();	
							for(int j=0; j<cletIDListSize; j++){								
								long soFarExecutedLength;
								long remainingCletLength;									
								double cletUtilization;	
								long cletNewLength;
								long oldBackupLength;
								soFarExecutedLength = cletSoFarMap.get(cletIDList.get(j));
								clet = broker.getCloudletFromID(cletIDList.get(j));			
								cletNewLength = clet.getCloudletLength() + ((long)(migrationOverhead*1000))+((long)(migrationDownTime*1000));								
								if(!cletLengthBackup.containsKey(cletIDList.get(j))){
									oldBackupLength = clet.getCloudletLength(); 
									oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
									cletLengthBackup.put(cletIDList.get(j), oldBackupLength);
								}
								else{
									oldBackupLength = cletLengthBackup.get(cletIDList.get(j)); 
									oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
									cletLengthBackup.put(cletIDList.get(j), oldBackupLength);
								}								
								clet.setCloudletLength(cletNewLength);
								remainingCletLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(j).getRemainingCloudletLength();			
								cletLastRemainingMap.put(cletIDList.get(j), remainingCletLength);
								MigrationOverheadTable.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
								DownTimeTable.setCloudletDownTimeforMigration(clet.getCloudletId(), migrationDownTime);
								migrationForCloudlets.put(cletIDList.get(j), soFarExecutedLength);
								cletUtilization = cletNorm.getNormalizedLength(clet);
								ReliabilityCalculator(newHost, vm, cletUtilization);
								vmRecord.setCletMigrationHostRecordforDownTime(clet.getCloudletId(), newHost.getId());
								vmRecord.setCletMigrationDownTime(clet.getCloudletId(), migrationDownTime);			
								vmRecord.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
							}						
						}
					}
				}
			}		
		}			
		updateCloudletProcessing();		
	}
		
		public long getCletLastRemaining(int cletID){
			if(cletLastRemainingMap.containsKey(cletID)){
				return cletLastRemainingMap.get(cletID);
			}
			else{
				return 0L;
			}			
		}
		
		public void processVmMigrateWithCheckpointing(int ftaNodeID){		
			if(RunTimeConstants.test == true){
		//	if(firstPredictionFlag.get(ftaNodeID)==true){
		//		System.out.println("This event is for first migration which needs to be ignored");
		//		firstPredictionFlag.put(ftaNodeID, false);
		//	}
		//	else{				
				int nodeID = 0;
				Host node;	
				vmMigrationList = new ArrayList<Vm>();
				int vmListSize;
			
				for(int j=0;j<hostList().size();j++){
					if(failhostTable.containsKey(ftaNodeID)){
						nodeID=failhostTable.get(ftaNodeID);
						break;
					}			
				}		
				node = hostList().get(nodeID); // Node that is set to be failed	
			
				int counterLocal = 0;
				if(RunTimeConstants.failureCorrelation == true && RunTimeConstants.faultToleranceMechanism == 4 && !(instantCorrelatedMigration.containsKey(ftaNodeID))){
					ArrayList<Integer>correlatedNodes = new ArrayList<Integer>();					
					String nodeLocation;
					nodeLocation = freadGrid5000.getClusterNameForNode(ftaNodeID);
					correlatedNodes.addAll(kMeans.getCorrelatedNodes(nodeLocation, ftaNodeID));
					for(int i=0; i<correlatedNodes.size(); i++){
						if(correlatedNodes.get(i).equals(ftaNodeID)){
							correlatedNodes.remove(i);
							break;
						}
					}
					for(int i=0; i<correlatedNodes.size(); i++){
						if(failhostTable.containsKey(correlatedNodes.get(i))){
							Host hostLocal;
							int nodeIDLocal;
							nodeIDLocal = failhostTable.get(correlatedNodes.get(i));
							hostLocal = hostList().get(nodeIDLocal);						
							IdleTime.setExpectedFailingHost(hostLocal.getId(), true);
							for(int j=0; j<hostLocal.getNumberOfPes(); j++){
								int peID;
								peID = hostLocal.getPeList().get(j).getId();						
								IdleTime.setHostPeIdleTable(hostLocal.getId(), peID, true);
								IdleTime.setHostPeIdleClockTable(hostLocal.getId(), peID, CloudSim.clock());
							}
						}
					}				
					for(int i=0; i<correlatedNodes.size(); i++){
						if(failhostTable.containsKey(correlatedNodes.get(i))){						
							instantCorrelatedMigration.put(correlatedNodes.get(i), true);						
							counterLocal = counterLocal + 1;
							sendNow(3, CloudSimTags.CREATE_CHECKPOINT, correlatedNodes.get(i));
							sendNow(3, CloudSimTags.VM_MIGRATION_CHECKPOINTING, correlatedNodes.get(i));
							//processVmMigrate(correlatedNodes.get(i));							
						}		
						//freadGrid5000.setCorrelatedMigraiton(false);
					}
					//freadGrid5000.setCorrelatedMigraiton(true);
					freadGrid5000.setCorrelatedMigraitonCounter(counterLocal);
					if(RunTimeConstants.vmConsolidationFlag == true){
						currentPredictionList = new ArrayList<Double>();
						currentPredictionList = currentPrediction.get(node.getId());
						if(currentPredictionList.size()!=0){
							currentPredictionList.remove(0);
							currentPrediction.put(node.getId(), currentPredictionList);
						}
					}
				}			
				else{				
					if(instantCorrelatedMigration.containsKey(ftaNodeID)){
						instantCorrelatedMigration.remove(ftaNodeID);
						correlationCounterFlag = true;					
					}
				}
				
				
				
				//***********************************************************************
				//This part for VM consolidation
			
				if(RunTimeConstants.vmConsolidationFlag == true && RunTimeConstants.failureCorrelation == false){
					currentPredictionList = new ArrayList<Double>();
					currentPredictionList = currentPrediction.get(node.getId());
					currentPredictionList.remove(0);
					currentPrediction.put(node.getId(), currentPredictionList);
				}
				//************************************************************************	
			
				if(node.isFailed()){
					System.out.println(" Node # " +node.getId()+ " with FTA ID " +ftaNodeID+ " has already been failed. So no VM is running on it");
				}
				else
				{			
					vmListSize = node.getVmList().size();
				
					for(int i=0; i<vmListSize; i++){
						vmMigrationList.add(node.getVmList().get(i));
					}						
					if(vmListSize == 0){
						System.out.println("No VM is running on Node # " +node.getId());
					}
					else{			
						IdleTime.setExpectedFailingHost(node.getId(), true);
						for(int i=0; i<node.getNumberOfPes(); i++){
							int peID;
							peID = node.getPeList().get(i).getId();						
							IdleTime.setHostPeIdleTable(node.getId(), peID, true);
							IdleTime.setHostPeIdleClockTable(node.getId(), peID, CloudSim.clock());
						}				
						System.out.println("Host " +node.getId()+" is expecting to be failed");
						for(int i=0;i<vmListSize;i++){
							Vm vm;						
							int cletIDListSize;							
							double migrationOverhead;
							double migrationDownTime;
							//	boolean flag_migration = false;
							ArrayList<Integer>cletIDList = new ArrayList<Integer>();
							Cloudlet clet;						
							vm = vmMigrationList.get(i);						
							cletIDListSize = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().size();		
							if(cletIDListSize == 0){
								System.out.println("No cloudlet is running on VM # " +vm.getId()+ ". So vm will not be migrated");
								continue;
							}					
							System.out.println(CloudSim.clock()+ " VM which is going to migrate is " +vm.getId());					
							System.out.println(CloudSim.clock()+ " Currently VM # " +vm.getId()+ " is running on host # " +node.getId());	
							System.out.println(CloudSim.clock()+ " Migrating Cloudlets with VM "+vm.getId()+" are");
							for(int j=0; j<cletIDListSize; j++){							
								cletIDList.add(((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(j).getCloudletId());		
								System.out.println(cletIDList.get(j));
								if(cletIDList.get(j)==997){
									System.out.println("Check");
								}								
							}					
							if(migrationForCloudlets.isEmpty()){						
								System.out.println("This is the first time for VM migration. Let it happen :-)");							
								for(int j=0; j<cletIDListSize; j++){
									long soFarClet;
									long length = 0;								
									long remainingLength;		
									long lastRemainingLength;
									remainingLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(j).getRemainingCloudletLength();	
									length = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(j).getCloudletLength();
									if(!cletLengthBackup.containsKey(cletIDList.get(j))){
										cletLengthBackup.put(cletIDList.get(j), length);
									}
									/*
									if(cletSoFarBeforeFailing.containsKey(cletIDList.get(j))){
										//soFarClet = length - remainingLength;
										soFarClet = cletSoFarMap.get(cletIDList.get(j));
										lastRemainingLength = length;
									//	cletLastRemainingMap.put(cletIDList.get(j), remainingLength);
										//cletSoFarMap.put(cletIDList.get(j), soFarClet);
										soFarClet = soFarClet + cletSoFarBeforeFailing.get(cletIDList.get(j)) ;
										System.out.println(CloudSim.clock()+ " Sofar execution of Cloudlet " +cletIDList.get(j)+ " on node "+node.getId()+" after failure overheads is " +soFarClet);										
										cletSoFarBeforeFailing.remove(cletIDList.get(j));
									}								
									else{
										lastRemainingLength = getCletLastRemaining(cletIDList.get(j));
										//soFarClet = lastRemainingLength - remainingLength;										
									//	cletLastRemainingMap.put(cletIDList.get(j), remainingLength);
										//cletSoFarMap.put(cletIDList.get(j), soFarClet);
										soFarClet = cletSoFarMap.get(cletIDList.get(j));
									}						
									*/
									if(cletSoFarBeforeFailing.containsKey(cletIDList.get(j))){
										soFarClet = length - remainingLength;												
										lastRemainingLength = length;
										cletLastRemainingMap.put(cletIDList.get(j), remainingLength);
										cletSoFarMap.put(cletIDList.get(j), soFarClet);
										soFarClet = soFarClet + cletSoFarBeforeFailing.get(cletIDList.get(j)) ;
										System.out.println(CloudSim.clock()+ " Sofar execution of Cloudlet " +cletIDList.get(j)+ " on node "+node.getId()+" after failure overheads is " +soFarClet);										
										cletSoFarBeforeFailing.remove(cletIDList.get(j));
									}								
									else{
										lastRemainingLength = getCletLastRemaining(cletIDList.get(j));
										soFarClet = lastRemainingLength - remainingLength;										
										cletLastRemainingMap.put(cletIDList.get(j), remainingLength);
										cletSoFarMap.put(cletIDList.get(j), soFarClet);												
									}	
									
								// Here I am saving the current host of the VM before migration in to class VmMigrationRecord.
								// Besides the host information, for how long the running cloudlets corresponding to migrating VM 
								// kept running on the current host has also been saved.  
								// By saving this information about each migrating VM, it will be easy to calculate the energy consumption accurately.		
								
									vmRecord.setCletMigrationHostRecord(cletIDList.get(j), node.getId());					
								
									System.out.println(CloudSim.clock()+ " Cloudlet " +cletIDList.get(j)+" is migrating from node " +node.getId());
									System.out.println(CloudSim.clock()+ " Length of Cloudlet "+cletIDList.get(j)+ " is " +length);
									System.out.println(CloudSim.clock()+ " Current Remaining Length of Cloudlet "+cletIDList.get(j)+ " without migration overheads is " +remainingLength);
									System.out.println(CloudSim.clock()+ " Last Remaining Length of Cloudlet "+cletIDList.get(j)+ " is " +lastRemainingLength);
									System.out.println(CloudSim.clock()+ " Sofar execution of Cloudlet " +cletIDList.get(j)+ " on node "+node.getId()+" is " +soFarClet);
								
									vmRecord.setVmExecutionDurationPerHostRecord(cletIDList.get(j), soFarClet);							
									cletMigrated.put(cletIDList.get(j), true);
								}
							
								int hostListSize = getHostList().size();
								getVmAllocationPolicy().deallocateHostForVm(vm);
								//node.removeMigratingInVm(vm);
								vm.setInMigration(true);		
							
								boolean result = getVmAllocationPolicy().allocateHostForVm(vm);
								if (!result) {
									Log.printLine("[Datacenter.processVmMigrate] VM allocation to the destination host failed");
									System.exit(0);
								}
								Log.formatLine("%.2f: Migration of VM #%d to Host #%d is completed", CloudSim.clock(), vm.getId(), vm.getHost().getId());							
								vm.setInMigration(false);							
								totalVmMigrations = totalVmMigrations + 1;	
								System.out.println("Migration count is " +totalVmMigrations);
								vmRecord.setVmCurrentHost(vm.getId(), vm.getHost().getId());
								if(hostListSize < getHostList().size()){
									Host newHost;
									newHost = vm.getHost();
									newHost.setDatacenter(this);
									processNewHost(newHost);
								
								}							
								//Applying VM migration overheads on all the running cloudlets running on each VM on the given node.								
											
								migrationOverhead = mgOver.getMigrationOverhead(vm);	
								migrationDownTime = mgOver.TotalDownTime(vm);					
							
								Host newHost;
								newHost = vm.getHost();					
							
								for(int j=0; j<cletIDListSize; j++){
									long remainingCletLength;
									double cletUtilization;
									long soFarExecutedLength;		
									long cletNewLength;
									long oldBackupLength;
									//remainingCletLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(j).getRemainingCloudletLength();								
									//soFarExecutedLength = cletLength - remainingCletLength;
									soFarExecutedLength = cletSoFarMap.get(cletIDList.get(j));
									clet = broker.getCloudletFromID(cletIDList.get(j));	
									System.out.println(CloudSim.clock()+ " Migration Overhead for Cloudlet "+cletIDList.get(j)+" is " +((long)(migrationOverhead*1000)));
									System.out.println(CloudSim.clock()+ " Migration Downtime Overhead for Cloudlet "+cletIDList.get(j)+" is " +((long)(migrationDownTime*1000)));														
									cletNewLength = clet.getCloudletLength() + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
									clet.setCloudletLength(cletNewLength);	
									if(cletLengthBackup.containsKey(cletIDList.get(j))){
										oldBackupLength = cletLengthBackup.get(cletIDList.get(j)); 
										oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
										cletLengthBackup.put(cletIDList.get(j), oldBackupLength);
									}
									remainingCletLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(j).getRemainingCloudletLength();
									System.out.println(CloudSim.clock()+ " New current Remaining Length of Cloudlet "+cletIDList.get(j)+ " after migration overheads is " +remainingCletLength);
									cletLastRemainingMap.put(cletIDList.get(j), remainingCletLength);
									MigrationOverheadTable.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
									DownTimeTable.setCloudletDownTimeforMigration(clet.getCloudletId(), migrationDownTime);									
									migrationForCloudlets.put(cletIDList.get(j), soFarExecutedLength);							
									cletUtilization = cletNorm.getNormalizedLength(clet);
									ReliabilityCalculator(newHost, vm, cletUtilization);
									vmRecord.setCletMigrationHostRecordforDownTime(clet.getCloudletId(), newHost.getId());
									vmRecord.setCletMigrationDownTime(clet.getCloudletId(), migrationDownTime);	
									vmRecord.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
								}							
							}
							else{							
								//	flag_migration = true;					
								//	if(flag_migration == true){							
								for(int j=0; j<cletIDListSize; j++){								
									long soFarClet;
									long length = 0;								
									long remainingLength;		
									long lastRemainingLength;
									//long soFarCletNew;
									remainingLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(j).getRemainingCloudletLength();	
									length = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(j).getCloudletLength();
									if(!cletLengthBackup.containsKey(cletIDList.get(j))){
										cletLengthBackup.put(cletIDList.get(j), length);
									}		
									/*
											if(cletSoFarBeforeFailing.containsKey(cletIDList.get(j))){
												//soFarClet = length - remainingLength;
												soFarClet = cletSoFarMap.get(cletIDList.get(j));
												//soFarCletNew = soFarClet;
												lastRemainingLength = length;
												//cletLastRemainingMap.put(cletIDList.get(j), remainingLength);
											//	cletSoFarMap.put(cletIDList.get(j), soFarClet);
												soFarClet = soFarClet + cletSoFarBeforeFailing.get(cletIDList.get(j));
												System.out.println(CloudSim.clock()+ " Sofar execution of Cloudlet " +cletIDList.get(j)+ " on node "+node.getId()+" after failure overheads is " +soFarClet);
												cletSoFarBeforeFailing.remove(cletIDList.get(j));
											}
											else{												
												lastRemainingLength = getCletLastRemaining(cletIDList.get(j));
												//soFarClet = length - remainingLength;	
												//soFarCletNew = soFarClet;
												//cletLastRemainingMap.put(cletIDList.get(j), remainingLength);
												soFarClet = cletSoFarMap.get(cletIDList.get(j)); //This so far tells the sofar execution of a clet on the current node.
											}																
									*/
									
									if(cletSoFarBeforeFailing.containsKey(cletIDList.get(j))){
										soFarClet = length - remainingLength;												
										lastRemainingLength = length;
										cletLastRemainingMap.put(cletIDList.get(j), remainingLength);
										cletSoFarMap.put(cletIDList.get(j), soFarClet);
										soFarClet = soFarClet + cletSoFarBeforeFailing.get(cletIDList.get(j)) ;
										System.out.println(CloudSim.clock()+ " Sofar execution of Cloudlet " +cletIDList.get(j)+ " on node "+node.getId()+" after failure overheads is " +soFarClet);										
										cletSoFarBeforeFailing.remove(cletIDList.get(j));
									}								
									else{
										lastRemainingLength = getCletLastRemaining(cletIDList.get(j));
										soFarClet = lastRemainingLength - remainingLength;										
										cletLastRemainingMap.put(cletIDList.get(j), remainingLength);
										cletSoFarMap.put(cletIDList.get(j), soFarClet);												
									}	
									
									vmRecord.setCletMigrationHostRecord(cletIDList.get(j), node.getId());
									//vmRecord.setVmExecutionDurationPerHostRecord(cletIDList.get(j), cletLastSofarMap.get(cletIDList.get(j)));
									System.out.println(CloudSim.clock()+ " Cloudlet " +cletIDList.get(j)+" is migrating from node " +node.getId());
									System.out.println(CloudSim.clock()+ " Length of Cloudlet "+cletIDList.get(j)+ " is " +length);
									System.out.println(CloudSim.clock()+ " Current Remaining Length of Cloudlet "+cletIDList.get(j)+ " without migration overheads is " +remainingLength);
									System.out.println(CloudSim.clock()+ " Last Remaining Length of Cloudlet "+cletIDList.get(j)+ " is " +lastRemainingLength);
									//System.out.println(CloudSim.clock()+ " Sofar execution of Cloudlet " +cletIDList.get(j)+ " on node "+node.getId()+" is " +soFarCletNew);
									System.out.println(CloudSim.clock()+ " Sofar execution of Cloudlet " +cletIDList.get(j)+ " on node "+node.getId()+" is " +soFarClet);
									vmRecord.setVmExecutionDurationPerHostRecord(cletIDList.get(j), soFarClet);		
									cletMigrated.put(cletIDList.get(j), true);
								}
								
								getVmAllocationPolicy().deallocateHostForVm(vm);
							//	node.removeMigratingInVm(vm);
								vm.setInMigration(true);		
								int hostListSize = hostList().size();
								boolean result = getVmAllocationPolicy().allocateHostForVm(vm);
								if (!result) {
									Log.printLine("[Datacenter.processVmMigrate] VM allocation to the destination host failed");
									System.exit(0);
								}							
								Log.formatLine("%.2f: Migration of VM #%d to Host #%d is completed", CloudSim.clock(), vm.getId(), vm.getHost().getId());
								vm.setInMigration(false);	
								
								totalVmMigrations = totalVmMigrations + 1;		
								System.out.println("Migration count is " +totalVmMigrations);
								vmRecord.setVmCurrentHost(vm.getId(), vm.getHost().getId());
								if(hostListSize < getHostList().size()){
									Host newHost;
									newHost = vm.getHost();
									newHost.setDatacenter(this);
									processNewHost(newHost);									
								}								
								//Applying VM migration overheads on all the running cloudlets running on each VM on the given node.								
												
								migrationOverhead = mgOver.getMigrationOverhead(vm);	
								migrationDownTime = mgOver.TotalDownTime(vm);
								Host newHost;
								newHost = vm.getHost();				
								
								for(int j=0; j<cletIDListSize; j++){								
									long soFarExecutedLength;
									long remainingCletLength;									
									double cletUtilization;	
									long cletNewLength;
									long oldBackupLength;
								//	soFarExecutedLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(j).getsofarlength();									
									soFarExecutedLength = cletSoFarMap.get(cletIDList.get(j));
									clet = broker.getCloudletFromID(cletIDList.get(j));				
									System.out.println(CloudSim.clock()+ " Migration Overhead for Cloudlet "+cletIDList.get(j)+" is " +((long)(migrationOverhead*1000)));
									System.out.println(CloudSim.clock()+ " Migration Downtime Overhead for Cloudlet "+cletIDList.get(j)+" is " +((long)(migrationDownTime*1000)));
								//	remainingCletLength = remainingCletLength + ((long)(migrationOverhead*1000));									
								//	((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(j).setremaininglength(remainingCletLength);
									cletNewLength = clet.getCloudletLength() + ((long)(migrationOverhead*1000))+((long)(migrationDownTime*1000));
									if(cletLengthBackup.containsKey(cletIDList.get(j))){
										oldBackupLength = cletLengthBackup.get(cletIDList.get(j)); 
										oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
										cletLengthBackup.put(cletIDList.get(j), oldBackupLength);
									}									
									clet.setCloudletLength(cletNewLength);		
									remainingCletLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(j).getRemainingCloudletLength();			
									System.out.println(CloudSim.clock()+ " New current Remaining Length of Cloudlet "+cletIDList.get(j)+ " after migration overheads is " +remainingCletLength);
									cletLastRemainingMap.put(cletIDList.get(j), remainingCletLength);
									MigrationOverheadTable.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
									DownTimeTable.setCloudletDownTimeforMigration(clet.getCloudletId(), migrationDownTime);
									//soFarExecutedLength = CloudletStatus.getsofarCloudlet(cletIDList.get(j));
									migrationForCloudlets.put(cletIDList.get(j), soFarExecutedLength);
									cletUtilization = cletNorm.getNormalizedLength(clet);
									ReliabilityCalculator(newHost, vm, cletUtilization);
									vmRecord.setCletMigrationHostRecordforDownTime(clet.getCloudletId(), newHost.getId());
									vmRecord.setCletMigrationDownTime(clet.getCloudletId(), migrationDownTime);			
									vmRecord.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
								}
							//}
							}
						}
					}
					if(correlationCounterFlag == true){
						counterLocal = freadGrid5000.getCorrelatedMigrationCounter();
						counterLocal = counterLocal - 1;
						freadGrid5000.setCorrelatedMigraitonCounter(counterLocal);
						correlationCounterFlag = false;
					}	
				}
			}
		else{			//This is for non-test case
			int nodeID = 0;
			Host node;	
			vmMigrationList = new ArrayList<Vm>();
			int vmListSize;		
			for(int j=0;j<hostList().size();j++){
				if(failhostTable.containsKey(ftaNodeID)){
					nodeID=failhostTable.get(ftaNodeID);
					break;
				}			
			}		
			node = hostList().get(nodeID); 	
			int counterLocal = 0;
			if(RunTimeConstants.failureCorrelation == true && RunTimeConstants.faultToleranceMechanism == 4 && !(instantCorrelatedMigration.containsKey(ftaNodeID))){
				ArrayList<Integer>correlatedNodes = new ArrayList<Integer>();					
				String nodeLocation;
				nodeLocation = freadGrid5000.getClusterNameForNode(ftaNodeID);
				correlatedNodes.addAll(kMeans.getCorrelatedNodes(nodeLocation, ftaNodeID));
				for(int i=0; i<correlatedNodes.size(); i++){
					if(correlatedNodes.get(i).equals(ftaNodeID)){
						correlatedNodes.remove(i);
						break;
					}
				}
				for(int i=0; i<correlatedNodes.size(); i++){
					if(failhostTable.containsKey(correlatedNodes.get(i))){
						Host hostLocal;
						int nodeIDLocal;
						nodeIDLocal = failhostTable.get(correlatedNodes.get(i));
						hostLocal = hostList().get(nodeIDLocal);						
						IdleTime.setExpectedFailingHost(hostLocal.getId(), true);
						for(int j=0; j<hostLocal.getNumberOfPes(); j++){
							int peID;
							peID = hostLocal.getPeList().get(j).getId();						
							IdleTime.setHostPeIdleTable(hostLocal.getId(), peID, true);
							IdleTime.setHostPeIdleClockTable(hostLocal.getId(), peID, CloudSim.clock());
						}
					}
				}				
				for(int i=0; i<correlatedNodes.size(); i++){
					if(failhostTable.containsKey(correlatedNodes.get(i))){						
						instantCorrelatedMigration.put(correlatedNodes.get(i), true);						
						counterLocal = counterLocal + 1;
						sendNow(3, CloudSimTags.CREATE_CHECKPOINT, correlatedNodes.get(i));
						sendNow(3, CloudSimTags.VM_MIGRATION_CHECKPOINTING, correlatedNodes.get(i));											
					}					
				}				
				freadGrid5000.setCorrelatedMigraitonCounter(counterLocal);
				if(RunTimeConstants.vmConsolidationFlag == true){
					currentPredictionList = new ArrayList<Double>();
					currentPredictionList = currentPrediction.get(node.getId());
					if(currentPredictionList.size()!=0){
						currentPredictionList.remove(0);
						currentPrediction.put(node.getId(), currentPredictionList);
					}
				}
			}			
			else{				
				if(instantCorrelatedMigration.containsKey(ftaNodeID)){
					instantCorrelatedMigration.remove(ftaNodeID);
					correlationCounterFlag = true;					
				}
			}
			if(RunTimeConstants.vmConsolidationFlag == true && RunTimeConstants.failureCorrelation == false){
				currentPredictionList = new ArrayList<Double>();
				currentPredictionList = currentPrediction.get(node.getId());
				currentPredictionList.remove(0);
				currentPrediction.put(node.getId(), currentPredictionList);
			}			
			if(!node.isFailed())
			{			
				vmListSize = node.getVmList().size();			
				for(int i=0; i<vmListSize; i++){
					vmMigrationList.add(node.getVmList().get(i));
				}			
				if(vmListSize != 0){			
					IdleTime.setExpectedFailingHost(node.getId(), true);
					for(int i=0; i<node.getNumberOfPes(); i++){
						int peID;
						peID = node.getPeList().get(i).getId();						
						IdleTime.setHostPeIdleTable(node.getId(), peID, true);
						IdleTime.setHostPeIdleClockTable(node.getId(), peID, CloudSim.clock());
					}				
					for(int i=0;i<vmListSize;i++){
						Vm vm;						
						int cletIDListSize;							
						double migrationOverhead;
						double migrationDownTime;						
						ArrayList<Integer>cletIDList = new ArrayList<Integer>();
						Cloudlet clet;						
						vm = vmMigrationList.get(i);						
						cletIDListSize = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().size();		
						if(cletIDListSize == 0){							
							continue;
						}				
						for(int j=0; j<cletIDListSize; j++){							
							cletIDList.add(((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(j).getCloudletId());															
						}					
						if(migrationForCloudlets.isEmpty()){												
							for(int j=0; j<cletIDListSize; j++){
								long soFarClet;
								long length = 0;								
								long remainingLength;		
								long lastRemainingLength;
								remainingLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(j).getRemainingCloudletLength();	
								length = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(j).getCloudletLength();
								if(!cletLengthBackup.containsKey(cletIDList.get(j))){
									cletLengthBackup.put(cletIDList.get(j), length);
								}								
								if(cletSoFarBeforeFailing.containsKey(cletIDList.get(j))){
									soFarClet = length - remainingLength;												
									lastRemainingLength = length;
									cletLastRemainingMap.put(cletIDList.get(j), remainingLength);
									cletSoFarMap.put(cletIDList.get(j), soFarClet);
									soFarClet = soFarClet + cletSoFarBeforeFailing.get(cletIDList.get(j)) ;
									cletSoFarBeforeFailing.remove(cletIDList.get(j));
								}								
								else{
									lastRemainingLength = getCletLastRemaining(cletIDList.get(j));
									soFarClet = lastRemainingLength - remainingLength;										
									cletLastRemainingMap.put(cletIDList.get(j), remainingLength);
									cletSoFarMap.put(cletIDList.get(j), soFarClet);												
								}								
								vmRecord.setCletMigrationHostRecord(cletIDList.get(j), node.getId());					
								vmRecord.setVmExecutionDurationPerHostRecord(cletIDList.get(j), soFarClet);							
								cletMigrated.put(cletIDList.get(j), true);
							}
						
							int hostListSize = getHostList().size();
							getVmAllocationPolicy().deallocateHostForVm(vm);							
							vm.setInMigration(true);					
							boolean result = getVmAllocationPolicy().allocateHostForVm(vm);
							if (!result) {
								Log.printLine("[Datacenter.processVmMigrate] VM allocation to the destination host failed");
								System.exit(0);
							}
							Log.formatLine("%.2f: Migration of VM #%d to Host #%d is completed", CloudSim.clock(), vm.getId(), vm.getHost().getId());							
							vm.setInMigration(false);							
							totalVmMigrations = totalVmMigrations + 1;						
							vmRecord.setVmCurrentHost(vm.getId(), vm.getHost().getId());
							if(hostListSize < getHostList().size()){
								Host newHost;
								newHost = vm.getHost();
								newHost.setDatacenter(this);
								processNewHost(newHost);
							
							}							
							migrationOverhead = mgOver.getMigrationOverhead(vm);	
							migrationDownTime = mgOver.TotalDownTime(vm);					
							Host newHost;
							newHost = vm.getHost();				
							for(int j=0; j<cletIDListSize; j++){
								long remainingCletLength;
								double cletUtilization;
								long soFarExecutedLength;		
								long cletNewLength;
								long oldBackupLength;
								soFarExecutedLength = cletSoFarMap.get(cletIDList.get(j));
								clet = broker.getCloudletFromID(cletIDList.get(j));	
								cletNewLength = clet.getCloudletLength() + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
								clet.setCloudletLength(cletNewLength);	
								if(cletLengthBackup.containsKey(cletIDList.get(j))){
									oldBackupLength = cletLengthBackup.get(cletIDList.get(j)); 
									oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
									cletLengthBackup.put(cletIDList.get(j), oldBackupLength);
								}
								remainingCletLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(j).getRemainingCloudletLength();
								cletLastRemainingMap.put(cletIDList.get(j), remainingCletLength);
								MigrationOverheadTable.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
								DownTimeTable.setCloudletDownTimeforMigration(clet.getCloudletId(), migrationDownTime);									
								migrationForCloudlets.put(cletIDList.get(j), soFarExecutedLength);							
								cletUtilization = cletNorm.getNormalizedLength(clet);
								ReliabilityCalculator(newHost, vm, cletUtilization);
								vmRecord.setCletMigrationHostRecordforDownTime(clet.getCloudletId(), newHost.getId());
								vmRecord.setCletMigrationDownTime(clet.getCloudletId(), migrationDownTime);	
								vmRecord.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
							}							
						}
						else{												
							for(int j=0; j<cletIDListSize; j++){								
								long soFarClet;
								long length = 0;								
								long remainingLength;		
								long lastRemainingLength;								
								remainingLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(j).getRemainingCloudletLength();	
								length = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(j).getCloudletLength();
								if(!cletLengthBackup.containsKey(cletIDList.get(j))){
									cletLengthBackup.put(cletIDList.get(j), length);
								}							
								if(cletSoFarBeforeFailing.containsKey(cletIDList.get(j))){
									soFarClet = length - remainingLength;												
									lastRemainingLength = length;
									cletLastRemainingMap.put(cletIDList.get(j), remainingLength);
									cletSoFarMap.put(cletIDList.get(j), soFarClet);
									soFarClet = soFarClet + cletSoFarBeforeFailing.get(cletIDList.get(j)) ;
									cletSoFarBeforeFailing.remove(cletIDList.get(j));
								}								
								else{
									lastRemainingLength = getCletLastRemaining(cletIDList.get(j));
									soFarClet = lastRemainingLength - remainingLength;										
									cletLastRemainingMap.put(cletIDList.get(j), remainingLength);
									cletSoFarMap.put(cletIDList.get(j), soFarClet);												
								}	
								
								vmRecord.setCletMigrationHostRecord(cletIDList.get(j), node.getId());
								vmRecord.setVmExecutionDurationPerHostRecord(cletIDList.get(j), soFarClet);		
								cletMigrated.put(cletIDList.get(j), true);
							}
							
							getVmAllocationPolicy().deallocateHostForVm(vm);					
							vm.setInMigration(true);		
							int hostListSize = hostList().size();
							boolean result = getVmAllocationPolicy().allocateHostForVm(vm);
							if (!result) {
								Log.printLine("[Datacenter.processVmMigrate] VM allocation to the destination host failed");
								System.exit(0);
							}							
							Log.formatLine("%.2f: Migration of VM #%d to Host #%d is completed", CloudSim.clock(), vm.getId(), vm.getHost().getId());
							vm.setInMigration(false);	
							
							totalVmMigrations = totalVmMigrations + 1;					
							vmRecord.setVmCurrentHost(vm.getId(), vm.getHost().getId());
							if(hostListSize < getHostList().size()){
								Host newHost;
								newHost = vm.getHost();
								newHost.setDatacenter(this);
								processNewHost(newHost);									
							}								
							migrationOverhead = mgOver.getMigrationOverhead(vm);	
							migrationDownTime = mgOver.TotalDownTime(vm);
							Host newHost;
							newHost = vm.getHost();						
							for(int j=0; j<cletIDListSize; j++){								
								long soFarExecutedLength;
								long remainingCletLength;									
								double cletUtilization;	
								long cletNewLength;
								long oldBackupLength;
								soFarExecutedLength = cletSoFarMap.get(cletIDList.get(j));
								clet = broker.getCloudletFromID(cletIDList.get(j));				
								cletNewLength = clet.getCloudletLength() + ((long)(migrationOverhead*1000))+((long)(migrationDownTime*1000));
								if(cletLengthBackup.containsKey(cletIDList.get(j))){
									oldBackupLength = cletLengthBackup.get(cletIDList.get(j)); 
									oldBackupLength = oldBackupLength + ((long)(migrationOverhead*1000)) + ((long)(migrationDownTime*1000));
									cletLengthBackup.put(cletIDList.get(j), oldBackupLength);
								}									
								clet.setCloudletLength(cletNewLength);		
								remainingCletLength = ((FailureCloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList().get(j).getRemainingCloudletLength();			
								cletLastRemainingMap.put(cletIDList.get(j), remainingCletLength);
								MigrationOverheadTable.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
								DownTimeTable.setCloudletDownTimeforMigration(clet.getCloudletId(), migrationDownTime);
								migrationForCloudlets.put(cletIDList.get(j), soFarExecutedLength);
								cletUtilization = cletNorm.getNormalizedLength(clet);
								ReliabilityCalculator(newHost, vm, cletUtilization);
								vmRecord.setCletMigrationHostRecordforDownTime(clet.getCloudletId(), newHost.getId());
								vmRecord.setCletMigrationDownTime(clet.getCloudletId(), migrationDownTime);			
								vmRecord.setCletMigrationOverhead(clet.getCloudletId(), migrationOverhead);
							}						
						}
					}
				}
			}		
		}				
		updateCloudletProcessing();						
	}
		
				
		public void setBroker(FailureDatacenterBroker broker){
			this.broker = broker;
		}		
		
		public void setFailurePredictor(FailurePrediction failPredict){
			this.failPredict = failPredict;
		}
		
		public void setFailurePredictorSynthetic(FailurePredictionSynthetic failPredictSynthetic){
			this.failPredictSynthetic = failPredictSynthetic;
		}
		
	//	public void setFailurePredictor(FailurePredictionSynthetic failPredict){
	//		this.failPredict = failPredict;
	//	}
		
		public void setVmUtilizationObject(VmUtilization vmUtl){
			this.vmUtl = vmUtl;
		}
		
		public void setVmMigrationOverheadObject(MigrationOverheadModel mgOver){
			this.mgOver = mgOver;
		}
		
		public void setVmMigrationRecordObject(VmMigrationRecord vmRecord){
			this.vmRecord = vmRecord;
		}
		
		double starttime;
		double endtime;
		double difference;
		double difference_last;
		double predictionTime;
		
		List<Double>MTBF = new ArrayList<Double>();		
		List<Double>MTTR = new ArrayList<Double>();
					
		/*
		 * (non-Javadoc)
		 * @see cloudsim.core.SimEntity#startEntity()
		 * 
		 */
		@Override
		
		public void startEntity() {
			super.startEntity();		
			int[] provisionedFTANodes = new int[hostList().size()];
			int count_fail = 0;
			//int count_avail;
			int node;
			//double firstStartTime;		
			ArrayList<Vm>vmListTemp = new ArrayList<Vm>();
			for(int i=0;i<hostList().size();i++){		
				hvmRbl.sethostClock(hostList().get(i), CloudSim.clock());				
				provisionedFTANodes[i] = HostMapping.getFTAhostID(hostList().get(i).getId());			
				}		
			if(RunTimeConstants.test == true)
			{
			for(int i=0; i<hostList().size(); i++){
				difference = 0;		
				difference_last = 0;
				failurePrediction = new ArrayList<Double>();	
				currentPredictionList = new ArrayList<Double>();
				node = provisionedFTANodes[i];				
				failhostList.add(node);					
				/**
				 * At this step, nodeID retrived from FTA is the key and value corresponding to the key is the nodeID for data center.
				 * The FTA nodeID will be passed to the event and then the given hashtable will be searched for the corresponding 
				 * datacenter hostID to fail the right one. 
				 */
				failhostTable.put(node, i);				
	//			System.out.println(node+ " is the FTA Node ID for which failure has been scheduled");
			//	firstFailureFlag.put(node, true);	
			//	firstRecoveryFlag.put(node, true);
			//	firstPredictionFlag.put(node, true);
			//	firstCheckpointFlag.put(node, true);
				//Preparing a temporary list to store the failure start time.
				if(RunTimeConstants.traceType.equals("LANL")){
					freadLANL.preparestartTime(node);					
					freadLANL.preparestopTime(node);					
					freadLANL.prepareAvailstartTime(node);					
					freadLANL.prepareAvailstopTime(node);					
					freadLANL.prepareDifference(node);					
					MTBF.add(freadLANL.getMTBF(node));					
					MTTR.add(freadLANL.getMTTR(node));				
					count_fail=freadLANL.getFailCount(node);
				}
				if(RunTimeConstants.traceType.equals("Grid5000")){
					freadGrid5000.preparestartTime(node);					
					freadGrid5000.preparestopTime(node);					
					freadGrid5000.prepareAvailstartTime(node);					
					freadGrid5000.prepareAvailstopTime(node);					
					freadGrid5000.prepareDifference(node);					
					MTBF.add(freadGrid5000.getMTBF(node));					
					MTTR.add(freadGrid5000.getMTTR(node));				
					count_fail=freadGrid5000.getFailCount(node);
				}	
				//if(node == 875){
				//	System.out.println("Check");
				//}
				if(RunTimeConstants.predictionFlag == true && RunTimeConstants.syntheticPrediction == true){
					failPredictSynthetic.predictFailures(node);					
					failurePrediction = failPredictSynthetic.getPredictedTBF(node);						
				}
				if(RunTimeConstants.predictionFlag == true && RunTimeConstants.syntheticPrediction == false){
					failPredict.predictFailures(node);					
					failurePrediction = failPredict.getPredictedTBF(node);
				}								
				if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism==1){
					if(RunTimeConstants.traceType.equals("LANL")){
						createCheckpointInterval(hostList().get(i).getId(), (freadLANL.getMTBF(node)));	
					}
					if(RunTimeConstants.traceType.equals("Grid5000")){
						createCheckpointInterval(hostList().get(i).getId(), (freadGrid5000.getMTBF(node)));	
					}								
					send(3, checkpointInterval.get(hostList().get(i).getId()), CloudSimTags.CREATE_CHECKPOINT, node);						
					}									
				
		//		System.out.println("Number of failure events for node " +node+ " are " +count_fail);					
				if(count_fail == 0){
					System.out.println(node+ " is not going to fail");
				}
				else{		
					if(RunTimeConstants.considerLaterPrediction == true){
						if(RunTimeConstants.traceType.equals("LANL")){
							for(int j=0;j<count_fail;j++){							
								starttime = freadLANL.getstartTime();							
								endtime = freadLANL.getendTime();
								if(freadLANL.getFirstEvent(node)==0 && !(nodeWithFirstFailEventMap.containsKey(node))){
									nodeWithFirstFailEventMap.put(node, true);
								}
								else{					
									if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == false){
										
										predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);		
										currentPredictionList.add(predictionTime);
										failurePrediction.remove(0);			
										//	System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));									
										send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, node);									
										/**
										 * These methods need to executed when execution will be done using Checkpointing as the fault tolerance mechanism
										 */			
										if(RunTimeConstants.failureInjection == true){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, node);
											//	System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));									
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, node);
											//	System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));
											}
										}
									
									if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){
									/*
									predictionTime = fread.getstartTimeAvail() + failurePrediction.get(0);		
									currentPredictionList.add(predictionTime);
									failurePrediction.remove(0);			
									System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));									
									send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, node);							
									send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, node);
									System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));									
									send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, node);
									System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));	
									*/
									
										predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);						
										currentPredictionList.add(predictionTime);							
										failurePrediction.remove(0);							
										//	System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));							
										send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, node);	
										if(RunTimeConstants.failureInjection == true){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, node);							
											//	System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));							
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, node);
											//	System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));							
										}
									}
								
									if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){	
										if(RunTimeConstants.failureInjection == true){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, node);							
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, node);	
										}
									}
								
									if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 3){							
										predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);							
										currentPredictionList.add(predictionTime);							
										failurePrediction.remove(0);							
										//	System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));																
										send(3, predictionTime, CloudSimTags.VM_MIGRATION, node);		
										if(RunTimeConstants.failureInjection == true){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, node);								
											//	System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));								
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, node);								
											//	System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));
											}
										}
								
									if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 3 && RunTimeConstants.vmConsolidationFlag == true){		
										if(RunTimeConstants.failureInjection == true){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, node);									
											//	System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));								
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, node);								
											//	System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));								
										}
									}
								
									if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4 ){							
										predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);						
										currentPredictionList.add(predictionTime);							
										failurePrediction.remove(0);										
										//	System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));							
										send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, node);							
										send(3, predictionTime, CloudSimTags.VM_MIGRATION_CHECKPOINTING, node);				
										if(RunTimeConstants.failureInjection == true){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, node);							
											//	System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));							
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, node);
											//	System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));
											}										
										}
									
									if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1){
										if(RunTimeConstants.failureInjection == true){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, node);
											//	System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));									
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, node);
											//	System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));
											}
										}
									
									if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 2){
										/**
										 * These methods need to executed when execution will be done using Restart as the fault tolerance mechanism
										 */
										if(RunTimeConstants.failureInjection == true){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, node);									
											System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));									
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, node);								
											System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));
										}
									}	
								}
							}
						}						
						if(RunTimeConstants.traceType.equals("Grid5000")){
							for(int j=0;j<count_fail;j++){							
								starttime = freadGrid5000.getstartTime();							
								endtime = freadGrid5000.getendTime();
								if(freadGrid5000.getFirstEvent(node)==0 && !(nodeWithFirstFailEventMap.containsKey(node))){
									nodeWithFirstFailEventMap.put(node, true);
								}
								else{					
									if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == false){								
										predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);		
										currentPredictionList.add(predictionTime);
										failurePrediction.remove(0);			
										//	System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));									
										send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, node);									
										/**
										 * These methods need to executed when execution will be done using Checkpointing as the fault tolerance mechanism
										 */							
										if(RunTimeConstants.failureInjection == true){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, node);
											//	System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));									
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, node);
											//	System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));
										}
									}
									
									if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 5 && RunTimeConstants.vmConsolidationFlag == false){								
										predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);		
										currentPredictionList.add(predictionTime);
										failurePrediction.remove(0);			
										//	System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));									
										send(3, predictionTime, CloudSimTags.CREATE_VM_REPLICATION, node);	
									}
									
									if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){
									/*
									predictionTime = fread.getstartTimeAvail() + failurePrediction.get(0);		
									currentPredictionList.add(predictionTime);
									failurePrediction.remove(0);			
									System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));									
									send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, node);							
									send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, node);
									System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));									
									send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, node);
									System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));	
									*/
									
										predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);						
										currentPredictionList.add(predictionTime);							
										failurePrediction.remove(0);							
										//	System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));							
										send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, node);	
										if(RunTimeConstants.failureInjection == true){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, node);							
											//	System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));							
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, node);
											//	System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));
										}										
									}
								
									if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){
										if(RunTimeConstants.failureInjection == true){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, node);							
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, node);
										}
									}
								
									if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 3){							
										predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);							
										currentPredictionList.add(predictionTime);							
										failurePrediction.remove(0);							
										//	System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));																
										send(3, predictionTime, CloudSimTags.VM_MIGRATION, node);	
										if(RunTimeConstants.failureInjection == true){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, node);								
											//	System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));								
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, node);								
											//	System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));
										}
									}
								
									if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 3 && RunTimeConstants.vmConsolidationFlag == true){		
										if(RunTimeConstants.failureInjection == true){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, node);									
											//	System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));								
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, node);								
											//	System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));
										}
									}
								
									if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4 ){							
										predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);						
										currentPredictionList.add(predictionTime);							
										failurePrediction.remove(0);							
										//	System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));							
										send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, node);							
										send(3, predictionTime, CloudSimTags.VM_MIGRATION_CHECKPOINTING, node);		
										if(RunTimeConstants.failureInjection == true){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, node);							
											//	System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));							
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, node);
											//	System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));
										}
									}
									
									if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1){		
										if(RunTimeConstants.failureInjection == true){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, node);
											//	System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));									
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, node);
											//	System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));
										}
									}
									
									if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 2){
										/**
										 * These methods need to executed when execution will be done using Restart as the fault tolerance mechanism
										 */
										if(RunTimeConstants.failureInjection == true){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, node);									
											//	System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));									
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, node);								
											//	System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));		
										}
									}	
								}
							}							
						}
					}
					else{
						if(RunTimeConstants.traceType.equals("LANL")){
							for(int j=0;j<count_fail;j++){							
								starttime = freadLANL.getstartTime();							
								endtime = freadLANL.getendTime();
								if(freadLANL.getFirstEvent(node)==0 && !(nodeWithFirstFailEventMap.containsKey(node))){
									nodeWithFirstFailEventMap.put(node, true);
								}
								else{					
									if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == false){								
										predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);		
										//currentPredictionList.add(predictionTime);
										failurePrediction.remove(0);		
										if(predictionTime > starttime){
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, node);
												//	System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));									
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, node);
												//	System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));
											}
										}
										else{
											currentPredictionList.add(predictionTime);
//											System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));									
											send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, node);									
											/**
											 * These methods need to executed when execution will be done using Checkpointing as the fault tolerance mechanism
											 */						
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, node);
												//	System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));									
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, node);
												//	System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));
											}
										}																			
									}								
									if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){
									/*
									predictionTime = fread.getstartTimeAvail() + failurePrediction.get(0);		
									currentPredictionList.add(predictionTime);
									failurePrediction.remove(0);			
									System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));									
									send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, node);							
									send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, node);
									System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));									
									send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, node);
									System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));	
									*/
										predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);						
									//	currentPredictionList.add(predictionTime);							
										failurePrediction.remove(0);
										if(predictionTime > starttime){
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, node);							
												//	System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));							
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, node);
												//	System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));
											}
										}
										else{
											currentPredictionList.add(predictionTime);
//											System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));							
											send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, node);		
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, node);							
												//	System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));							
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, node);
												//	System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));
											}
										}															
									}
								
									if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){
										if(RunTimeConstants.failureInjection == true){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, node);							
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, node);
										}
									}
								
									if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 3){							
										predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);							
									//	currentPredictionList.add(predictionTime);							
										failurePrediction.remove(0);		
										if(predictionTime > starttime){
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, node);								
												//	System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));								
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, node);								
												//	System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));
											}
										}
										else{
											currentPredictionList.add(predictionTime);
//											System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));																
											send(3, predictionTime, CloudSimTags.VM_MIGRATION, node);		
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, node);								
												//	System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));								
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, node);								
												//	System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));
											}
										}																	
									}							
									if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 3 && RunTimeConstants.vmConsolidationFlag == true){
										if(RunTimeConstants.failureInjection == true){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, node);									
											//	System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));								
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, node);								
											//	System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));
										}
									}
								
									if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4 ){							
										predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);						
									//	currentPredictionList.add(predictionTime);							
										failurePrediction.remove(0);			
										if(predictionTime > starttime){
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, node);							
												//	System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));							
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, node);
												//	System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));
											}
										}
										else{
											currentPredictionList.add(predictionTime);
//											System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));							
											send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, node);							
											send(3, predictionTime, CloudSimTags.VM_MIGRATION_CHECKPOINTING, node);			
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, node);							
												//	System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));							
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, node);
												//	System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));
											}
										}																
									}
									
									if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1){	
										if(RunTimeConstants.failureInjection == true){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, node);
											System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));									
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, node);
											System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));
										}
									}
									
									if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 2){
										/**
										 * These methods need to executed when execution will be done using Restart as the fault tolerance mechanism
										 */
										if(RunTimeConstants.failureInjection == true){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, node);									
											System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));									
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, node);								
											System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));
										}
									}	
								}
							}
						}							
						if(RunTimeConstants.traceType.equals("Grid5000")){
							for(int j=0;j<count_fail;j++){							
								starttime = freadGrid5000.getstartTime();							
								endtime = freadGrid5000.getendTime();
								if(freadGrid5000.getFirstEvent(node)==0 && !(nodeWithFirstFailEventMap.containsKey(node))){
									nodeWithFirstFailEventMap.put(node, true);
								}
								else{					
									if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == false){								
										predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);		
										//currentPredictionList.add(predictionTime);
										failurePrediction.remove(0);		
										if(predictionTime > starttime){
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, node);
												//	System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));									
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, node);
												//	System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));
											}
										}
										else{
											currentPredictionList.add(predictionTime);
//											System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));									
											send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, node);									
											/**
											 * These methods need to executed when execution will be done using Checkpointing as the fault tolerance mechanism
											 */							
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, node);
												//	System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));									
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, node);
												//	System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));
											}
										}																			
									}								
									if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){
									/*
									predictionTime = fread.getstartTimeAvail() + failurePrediction.get(0);		
									currentPredictionList.add(predictionTime);
									failurePrediction.remove(0);			
									System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));									
									send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, node);							
									send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, node);
									System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));									
									send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, node);
									System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));	
									*/
										predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);						
									//	currentPredictionList.add(predictionTime);							
										failurePrediction.remove(0);
										if(predictionTime > starttime){
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, node);							
												//	System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));							
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, node);
												//	System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));
											}
										}
										else{
											currentPredictionList.add(predictionTime);
//											System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));							
											send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, node);			
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, node);							
												//	System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));							
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, node);
												//	System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));
											}
										}															
									}
								
									if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){	
										if(RunTimeConstants.failureInjection == true){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, node);							
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, node);
										}
									}
								
									if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 3){							
										predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);							
									//	currentPredictionList.add(predictionTime);							
										failurePrediction.remove(0);		
										if(predictionTime > starttime){
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, node);								
												//	System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));								
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, node);								
												//	System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));
											}
										}
										else{
											currentPredictionList.add(predictionTime);
//											System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));																
											send(3, predictionTime, CloudSimTags.VM_MIGRATION, node);	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, node);								
												//	System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));								
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, node);								
												//	System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));
											}
										}																	
									}							
									if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 3 && RunTimeConstants.vmConsolidationFlag == true){		
										if(RunTimeConstants.failureInjection == true){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, node);									
											//	System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));								
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, node);								
											//	System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));
										}
									}
								
									if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4 ){							
										predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);						
									//	currentPredictionList.add(predictionTime);							
										failurePrediction.remove(0);			
										if(predictionTime > starttime){
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, node);							
												//	System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));							
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, node);
												//	System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));
											}
										}
										else{
											currentPredictionList.add(predictionTime);
//											System.out.println("Predicted time for the occurrence of next failure is " +(CloudSim.clock() + predictionTime));							
											send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, node);							
											send(3, predictionTime, CloudSimTags.VM_MIGRATION_CHECKPOINTING, node);			
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, node);							
												//	System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));							
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, node);
												//	System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));
											}
										}																
									}
									
									if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1){		
										if(RunTimeConstants.failureInjection == true){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, node);
											//	System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));									
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, node);
											//	System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));
										}
									}
									
									if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 2){
										/**
										 * These methods need to executed when execution will be done using Restart as the fault tolerance mechanism
										 */
										if(RunTimeConstants.failureInjection == true){
											send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, node);									
											System.out.println("Failure " +j+ " for node " +i+ " is going to occur at " +(CloudSim.clock() + starttime));									
											send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, node);								
											System.out.println("Recovery for node " +i+ " is going to occur at " +(CloudSim.clock() + endtime));
										}
									}	
								}
							}
						}
					}					
						if(!failurePrediction.isEmpty()){
							failurePrediction = null;							
						}
					}
					if(count_fail!=0){
						currentPrediction.put(hostList().get(i).getId(), currentPredictionList);
					}						
				}					
			}
			else{
				for(int i=0;i<hostList().size();i++){
					difference = 0;		
					difference_last = 0;
					failurePrediction = new ArrayList<Double>();	
					currentPredictionList = new ArrayList<Double>();
					node = provisionedFTANodes[i];						
					failhostList.add(node);					
					failhostTable.put(node, i);		
					if(RunTimeConstants.traceType.equals("LANL")){
						freadLANL.preparestartTime(node);					
						freadLANL.preparestopTime(node);					
						freadLANL.prepareAvailstartTime(node);					
						freadLANL.prepareAvailstopTime(node);					
						freadLANL.prepareDifference(node);					
						MTBF.add(freadLANL.getMTBF(node));					
						MTTR.add(freadLANL.getMTTR(node));				
						count_fail=freadLANL.getFailCount(node);
					}
					if(RunTimeConstants.traceType.equals("Grid5000")){
						freadGrid5000.preparestartTime(node);					
						freadGrid5000.preparestopTime(node);					
						freadGrid5000.prepareAvailstartTime(node);					
						freadGrid5000.prepareAvailstopTime(node);					
						freadGrid5000.prepareDifference(node);					
						MTBF.add(freadGrid5000.getMTBF(node));					
						MTTR.add(freadGrid5000.getMTTR(node));				
						count_fail=freadGrid5000.getFailCount(node);
					}					
					if(RunTimeConstants.predictionFlag == true && RunTimeConstants.syntheticPrediction == true){							
						failPredictSynthetic.predictFailures(node);					
						failurePrediction = failPredictSynthetic.getPredictedTBF(node);	
					}
					if(RunTimeConstants.predictionFlag == true && RunTimeConstants.syntheticPrediction == false){
						failPredict.predictFailures(node);					
						failurePrediction = failPredict.getPredictedTBF(node);
					}										
					if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism==1){
						if(RunTimeConstants.traceType.equals("LANL")){
							createCheckpointInterval(hostList().get(i).getId(), (freadLANL.getMTBF(node)));
						}
						if(RunTimeConstants.traceType.equals("Grid5000")){
							createCheckpointInterval(hostList().get(i).getId(), (freadGrid5000.getMTBF(node)));
						}		
						send(3, checkpointInterval.get(hostList().get(i).getId()), CloudSimTags.CREATE_CHECKPOINT, node);	
					}						
					if(count_fail != 0){				
						if(RunTimeConstants.considerLaterPrediction == true){
							if(RunTimeConstants.traceType.equals("LANL")){
								for(int j=0;j<count_fail;j++){															
									starttime = freadLANL.getstartTime();							
									endtime = freadLANL.getendTime();		
									if(freadLANL.getFirstEvent(node)==0 && !(nodeWithFirstFailEventMap.containsKey(node))){
										nodeWithFirstFailEventMap.put(node, true);
									}
									else{
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == false){								
											predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);		
											currentPredictionList.add(predictionTime);
											failurePrediction.remove(0);								
											send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, node);
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, node);
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, node);
											}
										}								
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){
											predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);						
											currentPredictionList.add(predictionTime);							
											failurePrediction.remove(0);							
											send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, node);		
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, node);							
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, node);
											}
										}								
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, node);							
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, node);
											}
										}									
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == false){
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, node);
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, node);
											}
										}								
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 3){							
											predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);							
											currentPredictionList.add(predictionTime);							
											failurePrediction.remove(0);							
											send(3, predictionTime, CloudSimTags.VM_MIGRATION, node);
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, node);								
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, node);
											}
										}									
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 3 && RunTimeConstants.vmConsolidationFlag == true){	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, node);									
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, node);
											}
										}									
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4 ){							
											predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);						
											currentPredictionList.add(predictionTime);							
											failurePrediction.remove(0);							
											send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, node);							
											send(3, predictionTime, CloudSimTags.VM_MIGRATION_CHECKPOINTING, node);			
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, node);							
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, node);
											}
										}									
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 2){				
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, node);									
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, node);
											}
										}							
									}
								}
							}							
							if(RunTimeConstants.traceType.equals("Grid5000")){
								for(int j=0;j<count_fail;j++){															
									starttime = freadGrid5000.getstartTime();							
									endtime = freadGrid5000.getendTime();		
									if(freadGrid5000.getFirstEvent(node)==0 && !(nodeWithFirstFailEventMap.containsKey(node))){
										nodeWithFirstFailEventMap.put(node, true);
									}
									else{
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == false){								
											predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);		
											currentPredictionList.add(predictionTime);
											failurePrediction.remove(0);								
											send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, node);
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, node);
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, node);									
											}
										}								
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){
											predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);						
											currentPredictionList.add(predictionTime);							
											failurePrediction.remove(0);							
											send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, node);	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, node);							
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, node);
											}
										}								
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, node);							
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, node);
											}
										}									
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == false){
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, node);
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, node);
											}
										}								
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 3){							
											predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);							
											currentPredictionList.add(predictionTime);							
											failurePrediction.remove(0);							
											send(3, predictionTime, CloudSimTags.VM_MIGRATION, node);	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, node);								
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, node);
											}
										}									
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 3 && RunTimeConstants.vmConsolidationFlag == true){
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, node);									
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, node);
											}
										}									
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4 ){							
											predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);						
											currentPredictionList.add(predictionTime);							
											failurePrediction.remove(0);							
											send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, node);							
											send(3, predictionTime, CloudSimTags.VM_MIGRATION_CHECKPOINTING, node);			
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, node);							
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, node);
											}
										}									
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 2){	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, node);									
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, node);
											}
										}							
									}
								}
							}
						}
						else{
							if(RunTimeConstants.traceType.equals("LANL")){
								for(int j=0;j<count_fail;j++){															
									starttime = freadLANL.getstartTime();							
									endtime = freadLANL.getendTime();		
									if(freadLANL.getFirstEvent(node)==0 && !(nodeWithFirstFailEventMap.containsKey(node))){
										nodeWithFirstFailEventMap.put(node, true);
									}
									else{
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == false){								
											predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);		
											//currentPredictionList.add(predictionTime);
											failurePrediction.remove(0);				
											if(predictionTime > starttime){
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, node);
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, node);
												}
											}
											else{
												currentPredictionList.add(predictionTime);
												send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, node);
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, node);
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, node);
												}
											}																			
										}								
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){
											predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);						
											//currentPredictionList.add(predictionTime);							
											failurePrediction.remove(0);
											if(predictionTime > starttime){
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, node);							
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, node);
												}
											}
											else{
												currentPredictionList.add(predictionTime);
												send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, node);		
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, node);							
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, node);
												}
											}																				
										}								
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, node);							
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, node);
											}
										}									
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == false){	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, node);
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, node);
											}
										}								
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 3){							
											predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);							
											//currentPredictionList.add(predictionTime);							
											failurePrediction.remove(0);
											if(predictionTime > starttime){
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, node);								
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, node);
												}
											}
											else{
												currentPredictionList.add(predictionTime);
												send(3, predictionTime, CloudSimTags.VM_MIGRATION, node);
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, node);								
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, node);
												}
											}																	
										}									
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 3 && RunTimeConstants.vmConsolidationFlag == true){
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, node);									
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, node);
											}
										}									
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4 ){							
											predictionTime = freadLANL.getstartTimeAvail() + failurePrediction.get(0);						
											//currentPredictionList.add(predictionTime);							
											failurePrediction.remove(0);	
											if(predictionTime > starttime){
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, node);							
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, node);
												}
											}
											else{
												currentPredictionList.add(predictionTime);
												send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, node);							
												send(3, predictionTime, CloudSimTags.VM_MIGRATION_CHECKPOINTING, node);	
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, node);							
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, node);
												}
											}
										}									
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 2){		
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, node);									
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, node);
											}
										}							
									}
								}
							}
							
							if(RunTimeConstants.traceType.equals("Grid5000")){
								for(int j=0;j<count_fail;j++){															
									starttime = freadGrid5000.getstartTime();							
									endtime = freadGrid5000.getendTime();		
									if(freadGrid5000.getFirstEvent(node)==0 && !(nodeWithFirstFailEventMap.containsKey(node))){
										nodeWithFirstFailEventMap.put(node, true);
									}
									else{
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == false){								
											predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);		
											//currentPredictionList.add(predictionTime);
											failurePrediction.remove(0);				
											if(predictionTime > starttime){
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, node);
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, node);
												}
											}
											else{
												currentPredictionList.add(predictionTime);
												send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, node);
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, node);
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, node);
												}
											}																			
										}								
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){
											predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);						
											//currentPredictionList.add(predictionTime);							
											failurePrediction.remove(0);
											if(predictionTime > starttime){
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, node);							
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, node);
												}
											}
											else{
												currentPredictionList.add(predictionTime);
												send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, node);			
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, node);							
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, node);
												}
											}																				
										}								
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == true){
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, node);							
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, node);
											}
										}									
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 1 && RunTimeConstants.vmConsolidationFlag == false){
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_CHECKPOINTING, node);
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_CHECKPOINTING, node);
											}
										}								
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 3){							
											predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);							
											//currentPredictionList.add(predictionTime);							
											failurePrediction.remove(0);
											if(predictionTime > starttime){
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, node);								
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, node);
												}
											}
											else{
												currentPredictionList.add(predictionTime);
												send(3, predictionTime, CloudSimTags.VM_MIGRATION, node);
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, node);								
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, node);
												}
											}																	
										}									
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 3 && RunTimeConstants.vmConsolidationFlag == true){	
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, node);									
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, node);
											}
										}									
										if(RunTimeConstants.predictionFlag == true && RunTimeConstants.faultToleranceMechanism == 4 ){							
											predictionTime = freadGrid5000.getstartTimeAvail() + failurePrediction.get(0);						
											//currentPredictionList.add(predictionTime);							
											failurePrediction.remove(0);	
											if(predictionTime > starttime){
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, node);							
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, node);
												}
											}
											else{
												currentPredictionList.add(predictionTime);
												send(3, predictionTime, CloudSimTags.CREATE_CHECKPOINT, node);							
												send(3, predictionTime, CloudSimTags.VM_MIGRATION_CHECKPOINTING, node);		
												if(RunTimeConstants.failureInjection == true){
													send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT_WITH_MIGRATION_CHECKPOINTING, node);							
													send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT_WITH_MIGRATION_CHECKPOINTING, node);
												}
											}
										}									
										if(RunTimeConstants.predictionFlag == false && RunTimeConstants.faultToleranceMechanism == 2){		
											if(RunTimeConstants.failureInjection == true){
												send(3, starttime, CloudSimTags.FAILURE_OCCURRENCE_EVENT, node);									
												send(3, endtime, CloudSimTags.FAILURE_RECOVERY_EVENT, node);
											}
										}							
									}
								}
							}
						}							
						if(!failurePrediction.isEmpty()){
							failurePrediction = null;							
						}
					}
					if(count_fail!=0){
						currentPrediction.put(hostList().get(i).getId(), currentPredictionList);
					}
				}			
			}		
		}
				
		/**
		 * Gets the host list consisting of FTA node IDs of failed hosts .
		 * 
		 * @return the host list
		 */	
		public List<Integer> getfailHostList(){
			return(failhostList);
		}
		
		public List<Double> getMTBFfailHostList(){
			return(MTBF);
		}
		
		public List<Double> getMTTRfailHostList(){
			return(MTTR);
		}
		
		public List<Cloudlet> getrecoveringCloudletList(){
			return(recoveringCloudletList);
		}
		
		//public void setFTARead(FTAFileReader ftaread){
		public void setFTARead(FTAFileReaderGrid5000 ftareadGrid5000){	
			this.freadGrid5000 = ftareadGrid5000;
		}
		
		public void setFTARead(FTAFileReader freadLANL){
			this.freadLANL = freadLANL;
		}
		
		public void setKMeans(K_MeansAlgorithm kMeans){
			this.kMeans = kMeans;
		}
		
		public void setKMeans(K_MeansAlgorithmLANL kMeansLANL){
			this.kMeansLANL = kMeansLANL;
		}
		
}
