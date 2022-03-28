package org.cloudbus.cloudsim.failure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Consts;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelStochastic;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.distributions.WeibullDistr;
import org.cloudbus.cloudsim.frequency.CheckNodeCompatibility;
import org.cloudbus.cloudsim.frequency.DeadlineModel;
import org.cloudbus.cloudsim.frequency.FrequencyDetails;
import org.cloudbus.cloudsim.lists.VmList;

public class FailureDatacenterBroker extends DatacenterBroker{
	
	/** List of Cloudlets and corresponding virtual machines */
	//public static Hashtable<Integer, Integer> clvmList = new Hashtable<Integer, Integer>();

	public static Hashtable<Integer, Integer> clusrlist = new Hashtable<Integer, Integer>();

	public static Hashtable<Integer, Cloudlet> idtoCletlist = new Hashtable<Integer, Cloudlet>();

	public static boolean flag;

	public static FailureDatacenter dc;
	
	public ReliabilityCalculator rblcal; 
	
	public int counter = 1;

	public static int botID = 1;

	public int numberofBags;

	public int cloudlet_order = 0;
	
	public int data_received = 0;
	
	public ArrayList<Integer>vmIDBackupList = new ArrayList<Integer>();
	
	public BinPacking bpack;
	
	public FailureDatacenter datacenter;
	
	public ArrayList<Cloudlet>sufferedCloudlets = new ArrayList<Cloudlet>();
	
	public CloudletNormalization cletNorm;
	
	public WorkLoadModel wlModel;
	
	public VmUtilization vmUtl;
	
	public HostVMMappingReliability hvmRbl;
	
	public VmMigrationRecord vmRecord;
	
	public WeibullDistr wbl;
	
	public WeibullDistr wblBagSize;
	
	public boolean nextCycleFlag = true;
	
	public ArrayList<Vm>VmListBackup = new ArrayList<Vm>();
	
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
	
	public FailureDatacenterBroker(String name) throws Exception {
		super(name);		
	}
	
	public void setBinPacking(BinPacking bpack){
		this.bpack = bpack;
	}
	
	public void setCloudletNormalization(CloudletNormalization cletNorm){
		this.cletNorm = cletNorm;
	}
	
	public void setWorkLoadModel(WorkLoadModel wlModel){
		this.wlModel = wlModel;
	}
	
	public void setReliabilityCalculator(ReliabilityCalculator rblcal){
		this.rblcal = rblcal;
	}
	
	public void setVmUtilizationObject(VmUtilization vmUtl){
		this.vmUtl = vmUtl;		
	}
	
	public void setHostVMMappingReliability(HostVMMappingReliability hvmRbl){
		this.hvmRbl = hvmRbl;
	}	
	
	public void setVmMigrationRecord(VmMigrationRecord vmRecord){
		this.vmRecord = vmRecord;
	}
	
	public void setCheckNodeCompatibility(CheckNodeCompatibility chkNodeComp) {
		this.chkNodeComp = chkNodeComp;
	}
	/**
	 * Stores the cloudlets corresponding to each virtual machine
	 * 
	 * @param vmId
	 *            is the virtual machine Id
	 * @param cletId
	 *            is the cloudlet Id *
	 */
	//public void maplistCloudlettoVm(int vmId, int cletId) {
	//	clvmList.put(vmId, cletId);		
	//}
	
	
	/**
	 * Stores the user corresponding to each cloudlet
	 * 
	 * @param usrId
	 *            is the user Id
	 * @param cletId
	 *            is the cloudlet Id *
	 */
	public void maplistUsertoCloudlet(int cletId, int usrId) {
		clusrlist.put(cletId, usrId);
	}

	/**
	 * Stores the cloudlet object corresponding to each cloudlet id
	 * 
	 * @param usrId
	 *            is the user Id
	 * @param cletId
	 *            is the cloudlet Id *
	 */
	public void idtoCloudletList(int cletId, Cloudlet clet) {
		idtoCletlist.put(cletId, clet);
	}
	
	/**
	 * Returns the cloudlet-virtual machine mapping list
	 */
	//public Hashtable<Integer, Integer> getmaplistCloudlettovm() {
		
	//	return (clvmList);
	//}
	
	/**
	 * Returns the user-cloudlet mapping list
	 */
	public Hashtable<Integer, Integer> getmaplistUsertocloudlet() {
		return (clusrlist);
	}

	public Hashtable<Integer, Cloudlet> getidtoCloudletLst() {
		return (idtoCletlist);
	}

	//public static int clvmlistsize() {
	//	return (clvmList.size());
	//}

	@Override
	public void processEvent(SimEvent ev) {
		switch (ev.getTag()) {
		
		case CloudSimTags.CLOUDLET_CANCEL:
			processCloudletCancel((Cloudlet) ev.getData());
			break;
			
		case CloudSimTags.CLOUDLET_RECOVER:
			processCloudletRecovery((Cloudlet) ev.getData());
			break;
			
		case CloudSimTags.NextCycle:			
	//		else{				
			counter = counter + 1;
			CreateCloudlets(dc.getId(), bagSize);
			if (counter < numberofBags) {
				 double dRate = wbl.sample()*1000;
				//double dRate = wlModel.getDataRate();
				botID = botID + 1;
				//bagSize = (int)(Math.pow(2, Math.ceil(wblBagSize.sample()))*20);
				bagSize = wlModel.getBagSize(botID);
			
				if(RunTimeConstants.test == true){
					System.out.println("Bag of Tasks # " + botID + " has been received with " + bagSize + " tasks at " +CloudSim.clock());
				}
				CloudletLength(bagSize);
				// for(int i=0;i<bagSize;i++){				
				schedule(getId(), dRate, CloudSimTags.NextCycle);
				// }
			} else {
				wlModel.setTotalCloudlets();
				// System.out.println("All the workload generation iterations
				// are finished");
				// dc.doCheckpoint = false;
			}
	//	}
			// CreateVMs();
			break;	
			
		default:
			//processOtherEvent(ev);
			super.processEvent(ev);
			break;		
		
		}
	}
	
	public void setDC(FailureDatacenter DC) { 
		dc = DC;
	}
	
	protected void processCloudletCancel(Cloudlet clet) {

		if (clet == null) {
			return;
		}
		// System.out.println("Utilization of Cloudlet # "
		// +clet.getCloudletId()+ " is"
		// +clet.getUtilizationOfCpu(CloudSim.clock()));
		if (RunTimeConstants.test == true) {
			System.out.println("Cloudlet " + clet + " with ID " + clet.getCloudletId() + " has failed ");
		}
		try {
			clet.setCloudletStatus(Cloudlet.FAILED);
			// getCloudletSubmittedList().remove(clet.getCloudletId());
		} catch (Exception e) {
			System.out.println("Exception has been occurred in processCloudletCancel function in DatacenterBroker");
		}

		//setVmsDestroyed(clet.getVmId());

		/*
		 * for(int i=0;i<vmList.size();i++){
		 * if(vmList.get(i).getId()==clet.getVmId()){ vmList.remove(i); } }
		 */

	}

	public void processCloudletRecovery(Cloudlet clet) {

		if (clet == null) {
			return;
		}
		if(RunTimeConstants.test == true){
			System.out.println("Cloudlet " + clet + " with ID " + clet.getCloudletId() + " is getting recovered ");
		}
		try {
			clet.setCloudletStatus(Cloudlet.RESUMED);
		} catch (Exception e) {
			System.out.println("Exception has been occurred in processCloudletRecovery function in DatacenterBroker");
		}
	}
	
	/**
	 * Process a cloudlet return event.
	 * 
	 * @param ev
	 *            a SimEvent object
	 * @pre ev != $null
	 * @post $none
	 */
	public Vm lastRemovedVm = null;
	ArrayList<Double>tempExecutionLength;
	protected void processCloudletReturn(SimEvent ev) {
		Cloudlet cloudlet = (Cloudlet) ev.getData();
		Vm vm; // Written by Yogesh Sharma
		Host host;	
		double sofar;		
		double totalLength = 0.0;
		tempExecutionLength = new ArrayList<Double>();
		vm = VmCloudletMapping.getVm(cloudlet); // Written by Yogesh Sharma
		host = vm.getHost(); // Written by Yogesh Sharma	
		//sofar = dc.getCletLastRemaining(cloudlet.getCloudletId())+CloudletReexecutionPart.getCletReexecutionTime(cloudlet);
		//if(cloudlet.getCloudletId() == 0){
		//	System.out.println("Check");
		//}		
		
		if(vmRecord.getVmMigrationHostList(cloudlet.getCloudletId())==null){
		//	sofar = (cloudlet.getFinishTime() - getcloudletInitialStartTime(cloudlet));
		//	sofar = CloudSim.clock() - getcloudletInitialStartTime(cloudlet);
			sofar = cloudlet.getCloudletFinishedSoFar();
		//	sofar = sofar * 1000;
		}
		else{
			tempExecutionLength = vmRecord.getVmExecutionDurationPerHostList(cloudlet.getCloudletId());
			for(int i=0; i<tempExecutionLength.size(); i++){
				totalLength = totalLength + tempExecutionLength.get(i);
			}
		//	sofar = (cloudlet.getFinishTime() - getcloudletInitialStartTime(cloudlet));
		//	sofar = sofar * 1000;
			sofar = cloudlet.getCloudletFinishedSoFar();
			sofar = sofar + totalLength;
			//sofar = dc.getCletLastRemaining(cloudlet.getCloudletId());
		}
		vmRecord.setCletMigrationHostRecord(cloudlet.getCloudletId(), host.getId());
		vmRecord.setVmExecutionDurationPerHostRecord(cloudlet.getCloudletId(), sofar);		
		getCloudletReceivedList().add(cloudlet);		
		if(cloudlet.getCloudletId() == 22919){
			//System.out.println("Check");
			data_received = 22919 ;
		}
		Log.printLine(CloudSim.clock() + ": " + getName() + ": Cloudlet " + cloudlet.getCloudletId() + " received");
		Log.printLine("Length of Cloudlet # " + cloudlet.getCloudletId() + " was " +cloudlet.getCloudletTotalLength());		
		if(cloudlet.getCloudletId()==4140){
			System.out.println("Check");
		}
		cloudletsSubmitted--;		
		wlModel.setFinishedCloudlets();
		
		//if(VmCloudletMapping.vmcletmapping.containsKey(vm)){
		if(RunTimeConstants.test == true){
			if(lastRemovedVm != vm){	
				double normLength;
				double utilization;
				normLength = cletNorm.getNormalizedLength(cloudlet);
				utilization = vmUtl.getCurrentVmUtilization(vm.getId());
				System.out.println("Current Utilization of VM #" +vm.getId()+ " while finishing a cloudlets #" +cloudlet.getCloudletId()+ " is " +utilization);
				utilization = utilization - normLength;		
				System.out.println("Utilization of VM #" +vm.getId()+" after finishing a cloudlet #" +cloudlet.getCloudletId()+ " is " +utilization);
				if(utilization > 0){
					vmUtl.setVmUtilization(vm.getId(), utilization);	
					vmUtl.setCurrentVmUtilization(vm.getId(), utilization);			
				}
				if(vm.getCloudletScheduler().runningCloudlets()>0 && utilization>0){
					double reliability;
					reliability = rblcal.getReliability(host, vm, utilization);
					reliability = Math.ceil(reliability * 1000);
					reliability = reliability / 1000;	
					System.out.println("New Reliability of VM #" +vm.getId()+ " after finishing a cloudlet #" +cloudlet.getCloudletId()+" is " +reliability);
					if(RunTimeConstants.failureInjection == true){
						if(dc.flag_record == true){
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
				if(vm.getCloudletScheduler().runningCloudlets()<=0){
					if(RunTimeConstants.test == true){
						System.out.println("VM #" + vm.getId() + " corresponding to Cloudlet #" + cloudlet.getCloudletId()
						+ " has also been destroyed");		}
					//		VmCloudletMapping.removeVm(vm);
					vmIDBackupList.add(vm.getId());
					List<Pe> peList = new ArrayList<Pe>();
					//List<Vm>vmList = new ArrayList<Vm>();
					//vmList = host.getVmList();			
					peList = host.getVmScheduler().getPesAllocatedForVM(vm);
			
					for(int i=0; i<peList.size(); i++){
						int peID;
						peID = peList.get(i).getId();
						boolean idleStatus;
						idleStatus = IdleTime.getHostPeIdleTable(host.getId(), peID);
						if(idleStatus == false){
							IdleTime.setHostPeIdleTable(host.getId(), peID, true);					
							IdleTime.setHostPeIdleClockTable(host.getId(), peID, CloudSim.clock());
						}							
					}		
			/*
			if(vmList.isEmpty()){
				peList = new ArrayList<Pe>();
				peList = host.getPeList();
				boolean idleStatus;
				boolean clockStatus;
				for(int j=0; j<peList.size(); j++){		
					idleStatus = IdleTime.getHostPeIdleTable(host.getId(), peList.get(j).getId());
					clockStatus = IdleTime.checkHostPeIdleClockTable(host.getId(), peList.get(j).getId());
					if(idleStatus==true && clockStatus == true){					
						IdleTime.setHostPeIdleTimeTable(host.getId(), peList.get(j).getId(), CloudSim.clock());
						IdleTime.removeHostPeIdleClockTable(host.getId(), peList.get(j).getId());
					}
				}
				IdleTime.setHostActiveStatus(host.getId(), false);
			}		
			*/
					if(dc.flag_record == true){
						VmListBackup.add(vm);
					}
		//	host.vmDestroy(vm); // Written by Yogesh Sharma			
								
					for (int i = 0; i < vmsCreatedList.size(); i++) {
						if (vmsCreatedList.get(i) == vm) {
							vmsCreatedList.remove(i);
							break;
						}
					}
					lastRemovedVm = vm;	
					sendNow(getVmsToDatacentersMap().get(vm.getId()), CloudSimTags.VM_DESTROY, vm);
					setVmsDestroyed(cloudlet.getVmId());
				}
		
		//if (host.getVmList().isEmpty()) {
			//if(RunTimeConstants.test == true){
				//System.out.println(CloudSim.clock() + " Host #" + host.getId() + " has been turned off");
			//}
			//IdleHostTime.setHostDeallocationTime(host, CloudSim.clock());
			//IdleHostTime.setHostDeallocationFlag(host, true);
		//}
		
			}
		}
		else{			
			if(lastRemovedVm != vm){	
				double normLength;
				double utilization;
				normLength = cletNorm.getNormalizedLength(cloudlet);
				utilization = vmUtl.getCurrentVmUtilization(vm.getId());
				utilization = utilization - normLength;		
				if(utilization > 0){
					vmUtl.setVmUtilization(vm.getId(), utilization);	
					vmUtl.setCurrentVmUtilization(vm.getId(), utilization);			
				}
				if(vm.getCloudletScheduler().runningCloudlets()>0 && utilization>0){
					double reliability;
					reliability = rblcal.getReliability(host, vm, utilization);
					reliability = Math.ceil(reliability * 1000);
					reliability = reliability / 1000;	
					if(RunTimeConstants.failureInjection == true){
						if(dc.flag_record == true){
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
				if(vm.getCloudletScheduler().runningCloudlets()<=0){
					if(RunTimeConstants.test == true){
						System.out.println("VM #" + vm.getId() + " corresponding to Cloudlet #" + cloudlet.getCloudletId()
						+ " has also been destroyed");	
						}					
					vmIDBackupList.add(vm.getId());
					List<Pe> peList = new ArrayList<Pe>();								
					peList = host.getVmScheduler().getPesAllocatedForVM(vm);
			
					for(int i=0; i<peList.size(); i++){
						int peID;
						peID = peList.get(i).getId();
						boolean idleStatus;
						idleStatus = IdleTime.getHostPeIdleTable(host.getId(), peID);
						if(idleStatus == false){
							IdleTime.setHostPeIdleTable(host.getId(), peID, true);					
							IdleTime.setHostPeIdleClockTable(host.getId(), peID, CloudSim.clock());
						}							
					}			
					if(dc.flag_record == true){
						VmListBackup.add(vm);
					}								
					for (int i = 0; i < vmsCreatedList.size(); i++) {
						if (vmsCreatedList.get(i) == vm) {
							vmsCreatedList.remove(i);
							break;
						}
					}
					lastRemovedVm = vm;	
					sendNow(getVmsToDatacentersMap().get(vm.getId()), CloudSimTags.VM_DESTROY, vm);
					setVmsDestroyed(cloudlet.getVmId());
				}	
			}		
		}
		if (getCloudletList().size() == 0 && cloudletsSubmitted == 0) { // all
																		// cloudlets
																		// executed
			Log.printLine(CloudSim.clock() + ": " + getName() + ": All Cloudlets executed. Finishing...");
			clearDatacenters();
			if (counter >= numberofBags) {				
				finishExecution();
			}
		} else { // some cloudlets haven't finished yet
			//if (getCloudletList().size() > 0 && cloudletsSubmitted == 0) {
			if (getCloudletList().size() > 0) {	
				// all the cloudlets sent finished. It means that some bount
				// cloudlet is waiting its VM be created
			//	clearDatacenters();
				createVmsInDatacenter(0);

			}

		}
	}	
	
	/**
	 * Create the virtual machines in a datacenter.
	 * 
	 * @param datacenterId
	 *            Id of the chosen PowerDatacenter
	 * @pre $none
	 * @post $none
	 */
	protected void createVmsInDatacenter(int datacenterId) {
		// send as much vms as possible for this datacenter before trying the
		// next one
		int requestedVms = 0;
		String datacenterName = CloudSim.getEntityName(datacenterId);
		for (Vm vm : getVmList()) {			
			if (!getVmsToDatacentersMap().containsKey(vm.getId())) {
				Log.printLine(CloudSim.clock() + ": " + getName() + ": Trying to Create VM #" + vm.getId() + " in "
						+ datacenterName);
				sendNow(datacenterId, CloudSimTags.VM_CREATE_ACK, vm);
				requestedVms++;
			}
		}

		getDatacenterRequestedIdsList().add(datacenterId);

		setVmsRequested(requestedVms);
		setVmsAcks(0);
		// double dRate = 0;
		if (RunTimeConstants.firstCycleFlag == true) {
			wblBagSize = new WeibullDistr(RunTimeConstants.scaleParameterBagSize, RunTimeConstants.shapeParameterBagSize);
		//	bagSize = (int)(Math.pow(2, Math.ceil(wblBagSize.sample()))*20);
			bagSize = wlModel.getBagSize(botID);
			wbl = new WeibullDistr(RunTimeConstants.scaleParameter, RunTimeConstants.shapeParameter);
			//double dRate = wlModel.getDataRate();
			double dRate = wbl.sample()*100;
			CloudletLength(bagSize);			
			if(RunTimeConstants.test == true){
				System.out.println("Bag of Tasks # " + botID + " has been received with " + bagSize + " tasks");
			}
			// for(int i=0;i<bagSize;i++){
			if(nextCycleFlag == true){
				dRate = dRate*10000;
				System.out.println(CloudSim.clock());
				schedule(getId(), dRate, CloudSimTags.NextCycle);
				nextCycleFlag = false;
			}
			//schedule(getId(), dRate, CloudSimTags.NextCycle);
			// }
			RunTimeConstants.firstCycleFlag = false;
		}
	}
	
	public ArrayList<Long> cletLengthList;
	public static long cletMax;
	public static int bagSize;

	private void CloudletLength(int bagSizeTemp) { // Written by Yogesh Sharma
		cletLengthList = new ArrayList<Long>();
		long cletLength[] = new long[bagSizeTemp];
		cletLengthList = wlModel.getCloudlets(bagSizeTemp);
		for (int i = 0; i < cletLengthList.size(); i++) {
		//	cletLength[i] = cletLengthList.get(i);
		//	cletLength[i] = (cletLengthList.get(i)*10000);
			cletLength[i] = (cletLengthList.get(i)*100000); //This is the one
		//	cletLength[i] = (cletLengthList.get(i)*100000000);
		}		
		if (bagSizeTemp > 1) { // This part need to be changed according to the
								// order of the cloudlets(increasing or
								// decreasing)required in the simulation.
			long temp;			
			cloudlet_order = RunTimeConstants.cloudlet_order;
			switch (cloudlet_order) {
			case 1:			
				if(RunTimeConstants.test == true){
					System.out.println(" Cloudlets have been ordered in Decreasing order ");
				}
				for (int i = 0; i < bagSizeTemp - 1; i++) {
					for (int j = i + 1; j < bagSizeTemp; j++) {
						if (cletLength[i] < cletLength[j]) {
							temp = cletLength[i];
							cletLength[i] = cletLength[j];
							cletLength[j] = temp;
						}
					}
				}
				break;

			case 2:
				if(RunTimeConstants.test == true){
					System.out.println(" Cloudlets have been ordered in Increasing order ");
				}
				for (int i = 0; i < bagSizeTemp - 1; i++) {
					for (int j = i + 1; j < bagSizeTemp; j++) {
						if (cletLength[i] > cletLength[j]) {
							temp = cletLength[i];
							cletLength[i] = cletLength[j];
							cletLength[j] = temp;
						}
					}
				}
				break;

			case 3:
				if(RunTimeConstants.test == true){
					System.out.println(" Cloudlets will be processed as they are arriving without preprocessing ");
				}
				break;
			}			
		}
		cletMax = 0;
		for (int i = 0; i < bagSizeTemp; i++) {
			if (cletMax < cletLength[i]) {
				cletMax = cletLength[i];
			}
		}
		cletLengthList.clear();
		for (int i = 0; i < bagSizeTemp; i++) {
			cletLengthList.add(cletLength[i]);
		}
	}
	
	
	private void CreateVMs(ArrayList<Cloudlet>cletList, int datacenterId) { // This function is written by Yogesh Sharma for the implementation of rate of arrival of cloudlets.
		
		List<Vm> vmList = new ArrayList<Vm>();
		HashMap<Integer, Double> vmUtilizationTemp = new HashMap<Integer, Double>();
		ArrayList<Cloudlet> cletListTemp;
		ArrayList<Cloudlet>cletListAllocated = new ArrayList<Cloudlet>();		
		vmList = dc.getVmList();
		for(int i=0; i<vmList.size();i++){
			vmUtilizationTemp.put(vmList.get(i).getId(), vmUtl.getCurrentVmUtilization(vmList.get(i).getId()));
		}
		double normUtilization;
		double utilization;		
		for(int j=0;j<cletList.size();j++){		
			//normUtilization = 0;
			utilization = 0;
			normUtilization = cletNorm.getNormalizedLength(cletList.get(j));
			//if(cletList.get(j).getCloudletId() == 18) {
			//	System.out.println("Check");
			//}
			for(int i=0;i<vmList.size();i++){	
				if(normUtilization == 1.0){
					break;
				}
				//utilization = 0;
				//utilization = vmUtl.getCurrentVmUtilization(vmList.get(i).getId());
				utilization = vmUtilizationTemp.get(vmList.get(i).getId());
				utilization = normUtilization + utilization;
				if(utilization <= 1.0){
					cletListTemp = new ArrayList<Cloudlet>();					
					bindCloudletToVm(cletList.get(j).getCloudletId(), vmList.get(i).getId());					
					System.out.println("Cloudlet " +cletList.get(j).getCloudletId()+ " has been allocated to an existing VM " +vmList.get(i).getId());					
					cletListTemp = VmCloudletMapping.getCloudlet(vmList.get(i));
					cletListTemp.add(cletList.get(j));
					VmCloudletMapping.createVmCloudletMap(vmList.get(i), cletListTemp);
					CloudletInitialLength.setCletInitialLength(cletList.get(j).getCloudletId(), cletList.get(j).getCloudletLength());
					cletListAllocated.add(cletList.get(j));				
					vmUtilizationTemp.put(vmList.get(i).getId(), utilization);
					flag = true; // This flag is for storing the initial cloudlet length and to create cloudlet backup for the later use.
					break;
					//submitCloudlets();
				}
			}			
		}			
		for(int i=0;i<cletList.size();i++){
			for(int j=0;j<cletListAllocated.size();j++){
				if(cletList.get(i).getCloudletId() == cletListAllocated.get(j).getCloudletId()){
					cletList.remove(i);
					cletListAllocated.remove(j);
					i=0;
					j=0;					
				}				
			}
		}
		
		if(cletList.size()>=1){
			for(int i=0; i<cletList.size(); i++) {
				if(cletList.get(i).getCloudletId() == 15) {
					System.out.println("Check");
				}
			}
			int vmid = 0; 
			for(int i=0;i<vmList.size();i++){
				if(vmid <= vmList.get(i).getId()){
					vmid = vmList.get(i).getId();
				}			
			}				
			for(int i=0; i<vmIDBackupList.size(); i++){ //vmIDBackupList stores the IDs of all the destroyed VMs
				if(vmid <= vmIDBackupList.get(i)){
					vmid = vmIDBackupList.get(i);
				}			
			}
			bpack.vmID = vmid + 1;
			if(counter == 2){
				bpack.count = 2;
				//System.out.println("Check");
			}
			bpack.calVMCount(cletList);
			int vmCount;			
			vmCount = bpack.getVmCount();			
			ArrayList<Integer>cletIDList;
			for(int i=0;i<vmCount;i++){
				vmid = vmid + 1;
				int mips = 1000;
				long size = 1000; // image size (MB)
				int ram = 128; // vm memory (MB)
				long bw = 1;
				int pesNumber = 1;
				utilization = 0;
				String vmm = "Xen"; // VMM name
				Vm vm = new Vm(vmid, getId(), mips, pesNumber, ram, bw, size, vmm, new FailureCloudletSchedulerTimeShared());			
				getVmList().add(vm);				
				cletIDList = new ArrayList<Integer>();
				cletIDList = bpack.getVMCletMapList(vmid);			
				
				long cletLength;
				cletListTemp = new ArrayList<Cloudlet>();
				try{
				for(int j=0;j<cletIDList.size();j++){
					bindCloudletToVm(cletIDList.get(j), vmid);
				}
				}
				catch(NullPointerException e){
					System.out.println("Check");
				}
				for(int j=0;j<cletIDList.size();j++){
					for(int k=0;k<cletList.size();k++){
						if(cletIDList.get(j) == cletList.get(k).getCloudletId()){
							cletLength = cletList.get(k).getCloudletLength();
							cletListTemp.add(cletList.get(k));
							CloudletInitialLength.setCletInitialLength(cletIDList.get(j), cletLength);							
							utilization = utilization + cletNorm.getNormalizedLength(cletList.get(k));
							break;
						}
					}
				}
				VmCloudletMapping.createVmCloudletMap(vm, cletListTemp);
				dc.processVmCreate(vm);
				getVmsToDatacentersMap().put(vmid, datacenterId);
				getVmsCreatedList().add(VmList.getById(getVmList(), vmid));
				flag = true; // This flag is for storing the initial cloudlet length and to create cloudlet backup for the later use.						
			}		
			submitCloudlets();	
		}
	}
	
	protected void CreateCloudlets(int datacenterId, int bagSize) { // This function is written by Yogesh Sharma for the implementation of rate of arrival of cloudlets.
		ArrayList<Cloudlet>cletList = new ArrayList<Cloudlet>();
		int newCletID = 0;
		boolean flag_cletID = false;
		for (int k = 0; k < bagSize; k++) {			
			int userID = cloudletSubmittedList.get(0).getUserId();
			// userID = cloudletList.get(1).getUserId();
			if(flag_cletID == false){				
				for (int i = 0; i < cloudletSubmittedList.size(); i++) {
					newCletID = i;
					flag_cletID = true;
				}
			}			
			newCletID = newCletID + 1;
			// Random rand = new Random();
			long fileSize = 300;
			long outputSize = 300;
			int pesNumber = 1;
			UtilizationModel utilizationModel = new UtilizationModelStochastic();			
			long length_temp = cletLengthList.get(k);
			// cletLengthList.remove(k);
			System.out.println("Length of Cloudlet #" + newCletID + " is " + length_temp);
			if(newCletID == 15) {
				System.out.println("Check");
			}
			Cloudlet cloudlet = new Cloudlet(newCletID, length_temp, pesNumber, fileSize, outputSize,
					new UtilizationModelNormalized(length_temp, cletMax),
					new UtilizationModelNormalized(length_temp, cletMax), utilizationModel);
			cloudlet.setUserId(userID);
			BagTaskMapping.setBagTaskMap(botID, newCletID);
			BagTaskMapping.setMaxTaskPerBag(botID, cletMax);
			cletNorm.setCloudletNormalizedLength(cloudlet, cletMax);		
			dlModel.calCletDeadline(cloudlet.getCloudletId(), cloudlet.getCloudletLength());
			getCloudletList().add(cloudlet);

			/**
			 * This part is for recording only the cloudlets that are submitted 
			 * after the occurrence of first failure and all other cloudlets that 
			 * were executing during the instance of first failure. 			
			 * 
			 * */
			if(dc.flag_record == true){ 			
				sufferedCloudlets.add(cloudlet);
			}
		cletList.add(cloudlet);			
		}
		
		CreateVMs(cletList, datacenterId);
		cletLengthList.clear();
	}
	
	//This function is for Replication Mechanism
	protected Cloudlet CreateCloudlets(Cloudlet currentClet){
		int newCletID = 0;
		long cletMaxLocal = 0;		
		int botIDLocal;		
		for (int i = 0; i < cloudletSubmittedList.size(); i++) {
			newCletID = i;			
		}
		newCletID = newCletID + 1;
		cletMaxLocal = BagTaskMapping.getMaxTask(currentClet.getCloudletId());
		botIDLocal = BagTaskMapping.getBagID(currentClet.getCloudletId());
		long fileSize = currentClet.getCloudletFileSize();		
		long outputSize = currentClet.getCloudletOutputSize();
		int pesNumber = currentClet.getNumberOfPes();
		long cletLengthLocal = currentClet.getCloudletLength();
		int cletUserID = currentClet.getUserId();
		UtilizationModel utilizationModel = new UtilizationModelStochastic();		
		Cloudlet replicatedClet = new Cloudlet(newCletID, cletLengthLocal, pesNumber, fileSize, outputSize,
				new UtilizationModelNormalized(cletLengthLocal, cletMaxLocal),
				new UtilizationModelNormalized(cletLengthLocal, cletMaxLocal), utilizationModel);
		replicatedClet.setUserId(cletUserID);
		BagTaskMapping.setBagTaskMap(botIDLocal, newCletID);
		cletNorm.setCloudletNormalizedLength(replicatedClet, cletMaxLocal);				
		getCloudletList().add(replicatedClet);
		getCloudletSubmittedList().add(replicatedClet);
		if(dc.flag_record == true){ 			
			sufferedCloudlets.add(replicatedClet);
		}
		return replicatedClet;		
	}

	public void setSufferedCloudletsList(){
		for(int i=0;i<cloudletSubmittedList.size();i++){
			Cloudlet clet;
			clet = cloudletSubmittedList.get(i);			
			if(clet.isFinished()==false){
				sufferedCloudlets.add(clet);
			}
		}
	}
	
	public static HashMap<Cloudlet, Double> inittimeCloudlet = new HashMap<Cloudlet, Double>();
	public HashMap<Integer, Long> cloudletTableBackup = new HashMap<Integer, Long>();
	
	
	protected void submitCloudlets() {
		int vmIndex = 0;
		//Host host;
		for (Cloudlet cloudlet : getCloudletList()) {
			Vm vm;
			// if user didn't bind this cloudlet and it has not been executed
			// yet
			if (cloudlet.getVmId() == -1) {
				vm = getVmsCreatedList().get(vmIndex);
			} else { // submit to the specific vm
				vm = VmList.getById(getVmsCreatedList(), cloudlet.getVmId());
				if (vm == null) { // vm was not created
					Log.printLine(CloudSim.clock() + ": " + getName() + ": Postponing execution of cloudlet "
							+ cloudlet.getCloudletId() + ": bount VM not available");
					continue;
				}
			}
			dc.cletLastRemainingMap.put(cloudlet.getCloudletId(), cloudlet.getCloudletLength());			
			Log.printLine(CloudSim.clock() + ": " + getName() + ": Sending cloudlet " + cloudlet.getCloudletId()
					+ " to VM #" + vm.getId());
			cloudlet.setVmId(vm.getId());
			//host = HostVMMapping.getHost(vm);			
			//ReliabilityCalculator(host, vm);
			//dc.ReliabilityCalculator(host, vm);
			sendNow(getVmsToDatacentersMap().get(vm.getId()), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
			cloudletsSubmitted++;
			vmIndex = (vmIndex + 1) % getVmsCreatedList().size();
			getCloudletSubmittedList().add(cloudlet);

			//maplistCloudlettoVm(cloudlet.getVmId(), cloudlet.getCloudletId());
			idtoCloudletList(cloudlet.getCloudletId(), cloudlet);
			maplistUsertoCloudlet(cloudlet.getCloudletId(), cloudlet.getUserId());

			if (flag == true) {
				inittimeCloudlet.put(cloudlet, CloudSim.clock());
				cloudletTableBackup.put(cloudlet.getCloudletId(), cloudlet.getCloudletLength());
				CloudletStatus.setremainingCloudlet(cloudlet.getCloudletId(),
						(long) Math.floor(cloudlet.getCloudletLength() / Consts.MILLION)); // Written
																							// by
																							// Yogesh
																							// Sharma
				CloudletStatus.setsofarCloudlet(cloudlet.getCloudletId(), (long) 0); // Written
																		// by
																		// Yogesh
																		// Sharma
			}

		}
		flag = false;
		// remove submitted cloudlets from waiting list
		for (Cloudlet cloudlet : getCloudletSubmittedList()) {
			getCloudletList().remove(cloudlet);
		}
	}
	/*
	public void ReliabilityCalculator(Host host, Vm vm){
		double ftaUtilization;
		double utilization;
		double reliability;		
		//utilization = VmCloudletMapping.getCloudlet(vm).getUtilizationOfCpu(CloudSim.clock());
		utilization = vmUtl.getVmUtilization(vm.getId());
		ftaUtilization = dc.fread.getCurrentUtilization(HostMapping.getFTAhostID(host.getId()));
		utilization = utilization + ftaUtilization;		
		if(utilization > 1.0){
			utilization = 1.0;
		}
		//vm.setCurrentUtilization(utilization);
		vmUtl.setVmUtilization(vm.getId(), utilization);
		System.out.println("Utilization of VM #" +vm.getId()+" is " +utilization);
		reliability = rblcal.getReliability(host, vm, utilization);
		reliability = Math.ceil(reliability * 1000);
		reliability = reliability / 1000;				
		System.out.println("Reliability of VM #" +vm.getId()+ " at utilization " +utilization+ " is " +reliability);
		
		hvmRbl.setVmReliability(vm, reliability);		
		hvmRbl.sethostVmReliabilityTable(host, vm);
		
		//HostVMMappingReliability.setSystemReliability(host);
		hvmRbl.setSystemReliability();
		hvmRbl.sethostClock(host, CloudSim.clock());		
		//HostVMMappingUtilization.setVmUtilization(vm, utilization);
	}
	*/
	public Double getcloudletInitialStartTime(Cloudlet clet) {
		Double time = 0.0;
		if (inittimeCloudlet.containsKey(clet)) {
			time = inittimeCloudlet.get(clet);
		}
		return time;
	}
	
	/**
	 * Send an internal event communicating the end of the simulation.
	 * 
	 * @pre $none
	 * @post $none
	 */
	protected void finishExecution() {
		ArrayList<Host> hostList = new ArrayList<Host>(); // Written by Yogesh
															// Sharma
		for (int i = 0; i < dc.hostList().size(); i++) {
			hostList.add(dc.hostList().get(i));
		}		
		
		for(int i=0; i<hostList.size(); i++){
			int hostCount;
			//if(hostList.get(i).getId()==58){
			//	System.out.println("Check");
			//}
			if(IdleTime.checkHostActiveStatus(hostList.get(i).getId())==false){
				continue;
			}			
			List<Pe>peList = new ArrayList<Pe>();
			peList = hostList.get(i).getPeList();	
			
			boolean idleStatus;
			boolean clockStatus;
			for(int j=0; j<peList.size(); j++){		
				idleStatus = IdleTime.getHostPeIdleTable(hostList.get(i).getId(), peList.get(j).getId());
				clockStatus = IdleTime.checkHostPeIdleClockTable(hostList.get(i).getId(), peList.get(j).getId());
				if(idleStatus==true && clockStatus == true){					
					IdleTime.setHostPeIdleTimeTable(hostList.get(i).getId(), peList.get(j).getId(), CloudSim.clock());
					IdleTime.removeHostPeIdleClockTable(hostList.get(i).getId(), peList.get(j).getId());
				}
			}
			IdleTime.setHostActiveStatus(hostList.get(i).getId(), false);
			hostCount = IdleTime.getActiveHostCount();			
			HostCounter.setHostCount(hostCount);
			HostCounter.setClockList(CloudSim.clock());
		}		
			sendNow(getId(), CloudSimTags.END_OF_SIMULATION);
				
	}
	
	public void shutdownEntity() {
		Log.printLine(getName() + " is shutting down...");		
		dc.furtherCheckpoints = false;
	}
	
	public ArrayList<Cloudlet>getSufferedCloudletsList(){
		return sufferedCloudlets;
	}
	
	public ArrayList<Vm>getVmBackupList(){
		return VmListBackup;
	}
	
	public Cloudlet getCloudletFromID(int cletID){
		return idtoCletlist.get(cletID);
	}
	
}
