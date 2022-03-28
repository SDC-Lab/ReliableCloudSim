/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.distributions.WeibullDistr;
import org.cloudbus.cloudsim.failure.BagTaskMapping;
import org.cloudbus.cloudsim.failure.CloudletInitialLength;
import org.cloudbus.cloudsim.failure.CloudletNormalization;
import org.cloudbus.cloudsim.failure.CloudletStatus;
import org.cloudbus.cloudsim.failure.CloudletUtilization;
import org.cloudbus.cloudsim.failure.HostMapping;
import org.cloudbus.cloudsim.failure.HostVMMapping;
import org.cloudbus.cloudsim.failure.HostVMMappingReliability;
import org.cloudbus.cloudsim.failure.HostVMMappingUtilization;
import org.cloudbus.cloudsim.failure.IdleHostTime;
import org.cloudbus.cloudsim.failure.ReliabilityCalculator;
import org.cloudbus.cloudsim.failure.RunTimeConstants;
import org.cloudbus.cloudsim.failure.UtilizationModelNormalized;
import org.cloudbus.cloudsim.failure.VmCloudletMapping;
import org.cloudbus.cloudsim.failure.WorkLoadModel;
import org.cloudbus.cloudsim.lists.CloudletList;
import org.cloudbus.cloudsim.lists.VmList;

/**
 * DatacentreBroker represents a broker acting on behalf of a user. It hides VM
 * management, as vm creation, sumbission of cloudlets to this VMs and
 * destruction of VMs.
 * 
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 */
public class DatacenterBroker extends SimEntity {

	/** The vm list. */
	protected List<? extends Vm> vmList;

	/** The vms created list. */
	protected static List<? extends Vm> vmsCreatedList;

	/** The cloudlet list. */
	protected static List<? extends Cloudlet> cloudletList;

	/** The cloudlet submitted list. */
	protected List<? extends Cloudlet> cloudletSubmittedList;

	/** The cloudlet received list. */
	protected static List<? extends Cloudlet> cloudletReceivedList;

	/** The cloudlets submitted. */
	protected int cloudletsSubmitted;

	/** The vms requested. */
	protected int vmsRequested;

	/** The vms acks. */
	protected int vmsAcks;

	/** The vms destroyed. */
	protected int vmsDestroyed;

	/** The datacenter ids list. */
	protected List<Integer> datacenterIdsList;

	/** The datacenter requested ids list. */
	protected List<Integer> datacenterRequestedIdsList;

	/** The vms to datacenters map. */
	protected Map<Integer, Integer> vmsToDatacentersMap;

	/** The datacenter characteristics list. */
	protected Map<Integer, DatacenterCharacteristics> datacenterCharacteristicsList;

	/** List of Cloudlets and corresponding virtual machines */
	public static Hashtable<Integer, Integer> clvmList = new Hashtable<Integer, Integer>();

	public static Hashtable<Integer, Integer> clusrlist = new Hashtable<Integer, Integer>();

	public static Hashtable<Integer, Cloudlet> idtoCletlist = new Hashtable<Integer, Cloudlet>();

	public static boolean flag;

	public static Datacenter dc;

	public boolean firstCycleFlag = true;
	
	ReliabilityCalculator rblcal = new ReliabilityCalculator();

	// public int dataRateIteration = 10;

	// public double dataRateValue = 15.0;

	public int counter = 1;

	public static int botID = 1;

	public int numberofBags;

	public int cloudlet_order = 0;
	
	public ArrayList<Cloudlet>sufferedCloudlets = new ArrayList<Cloudlet>();

	/**
	 * Created a new DatacenterBroker object.
	 * 
	 * @param name
	 *            name to be associated with this entity (as required by
	 *            Sim_entity class from simjava package)
	 * @throws Exception
	 *             the exception
	 * @pre name != null
	 * @post $none
	 */
	public DatacenterBroker(String name) throws Exception {
		super(name);

		setVmList(new ArrayList<Vm>());
		setVmsCreatedList(new ArrayList<Vm>());
		setCloudletList(new ArrayList<Cloudlet>());
		setCloudletSubmittedList(new ArrayList<Cloudlet>());
		setCloudletReceivedList(new ArrayList<Cloudlet>());

		cloudletsSubmitted = 0;
		setVmsRequested(0);
		setVmsAcks(0);
		setVmsDestroyed(0);

		setDatacenterIdsList(new LinkedList<Integer>());
		setDatacenterRequestedIdsList(new ArrayList<Integer>());
		setVmsToDatacentersMap(new HashMap<Integer, Integer>());
		setDatacenterCharacteristicsList(new HashMap<Integer, DatacenterCharacteristics>());
	}

	/**
	 * This method is used to send to the broker the list with virtual machines
	 * that must be created.
	 * 
	 * @param list
	 *            the list
	 * @pre list !=null
	 * @post $none
	 */
	public void submitVmList(List<? extends Vm> list) {
		getVmList().addAll(list);
	}

	/**
	 * This method is used to send to the broker the list of cloudlets.
	 * 
	 * @param list
	 *            the list
	 * @pre list !=null
	 * @post $none
	 */
	public void submitCloudletList(List<? extends Cloudlet> list) {
		getCloudletList().addAll(list);
	}

	/**
	 * Specifies that a given cloudlet must run in a specific virtual machine.
	 * 
	 * @param cloudletId
	 *            ID of the cloudlet being bount to a vm
	 * @param vmId
	 *            the vm id
	 * @pre cloudletId > 0
	 * @pre id > 0
	 * @post $none
	 */
	public void bindCloudletToVm(int cloudletId, int vmId) {
		CloudletList.getById(getCloudletList(), cloudletId).setVmId(vmId);
		// maplistCloudlettoVm(cloudletId, vmId);
		// maplistCloudlettoVm(cloudletId, vmId);
		// maplistCloudlettoVm(clvmList);
	}

	/**
	 * Stores the cloudlets corresponding to each virtual machine
	 * 
	 * @param vmId
	 *            is the virtual machine Id
	 * @param cletId
	 *            is the cloudlet Id *
	 */
	public void maplistCloudlettoVm(int vmId, int cletId) {
		clvmList.put(vmId, cletId);

		// System.out.println("Message is getting called"
		// + " " + clvmList.get(vmId));
	}

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

	public void idtoCloudletList(int cletId, Cloudlet clet) {
		idtoCletlist.put(cletId, clet);
	}

	/**
	 * Returns the cloudlet-virtual machine mapping list
	 */
	//public static Hashtable<Integer, Integer> getmaplistCloudlettovm() {

		// ***************************************************************************/
	//	return (clvmList);
	//}

	/**
	 * Returns the user-cloudlet mapping list
	 */
	//public static Hashtable<Integer, Integer> getmaplistUsertocloutlet() {
	//	return (clusrlist);
	//}

	//public static Hashtable<Integer, Cloudlet> getidtoCloudletLst() {
	//	return (idtoCletlist);
	//}

	public static int clvmlistsize() {
		return (clvmList.size());
	}

	/**
	 * Processes events available for this Broker.
	 * 
	 * @param ev
	 *            a SimEvent object
	 * @pre ev != null
	 * @post $none
	 */
	@Override
	public void processEvent(SimEvent ev) {
		switch (ev.getTag()) {
		// Resource characteristics request
		case CloudSimTags.RESOURCE_CHARACTERISTICS_REQUEST:
			processResourceCharacteristicsRequest(ev);
			break;
		// Resource characteristics answer
		case CloudSimTags.RESOURCE_CHARACTERISTICS:
			processResourceCharacteristics(ev);
			break;
		// VM Creation answer
		case CloudSimTags.VM_CREATE_ACK:
			processVmCreate(ev);
			break;
		// A finished cloudlet returned
		case CloudSimTags.CLOUDLET_RETURN:
			processCloudletReturn(ev);
			break;
		//case CloudSimTags.CLOUDLET_CANCEL:
			//processCloudletCancel((Cloudlet) ev.getData());
		//	break;
		// if the simulation finishes
		case CloudSimTags.END_OF_SIMULATION:
			shutdownEntity();
			break;	
		// case CloudSimTags.SAVE_VM_UTILIZATION:
		// setVMUtilization((Cloudlet)ev.getData());
		// other unknown tags are processed by this method
		default:
			processOtherEvent(ev);
			break;
		}
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

		setVmsDestroyed(clet.getVmId());

		/*
		 * for(int i=0;i<vmList.size();i++){
		 * if(vmList.get(i).getId()==clet.getVmId()){ vmList.remove(i); } }
		 */

	}

	protected void processCloudletRecovery(Cloudlet clet) {

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
	 * Process the return of a request for the characteristics of a
	 * PowerDatacenter.
	 * 
	 * @param ev
	 *            a SimEvent object
	 * @pre ev != $null
	 * @post $none
	 */
	protected void processResourceCharacteristics(SimEvent ev) {
		DatacenterCharacteristics characteristics = (DatacenterCharacteristics) ev.getData();
		getDatacenterCharacteristicsList().put(characteristics.getId(), characteristics);

		if (getDatacenterCharacteristicsList().size() == getDatacenterIdsList().size()) {
			setDatacenterRequestedIdsList(new ArrayList<Integer>());
			createVmsInDatacenter(getDatacenterIdsList().get(0));
		}
	}

	/**
	 * Process a request for the characteristics of a PowerDatacenter.
	 * 
	 * @param ev
	 *            a SimEvent object
	 * @pre ev != $null
	 * @post $none
	 */
	protected void processResourceCharacteristicsRequest(SimEvent ev) {
		setDatacenterIdsList(CloudSim.getCloudResourceList());
		setDatacenterCharacteristicsList(new HashMap<Integer, DatacenterCharacteristics>());

		Log.printLine(CloudSim.clock() + ": " + getName() + ": Cloud Resource List received with "
				+ getDatacenterIdsList().size() + " resource(s)");

		for (Integer datacenterId : getDatacenterIdsList()) {
			sendNow(datacenterId, CloudSimTags.RESOURCE_CHARACTERISTICS, getId());
		}
	}

	/**
	 * Process the ack received due to a request for VM creation.
	 * 
	 * @param ev
	 *            a SimEvent object
	 * @pre ev != null
	 * @post $none
	 */
	protected void processVmCreate(SimEvent ev) {
		int[] data = (int[]) ev.getData();
		int datacenterId = data[0];
		int vmId = data[1];
		int result = data[2];

		if (result == CloudSimTags.TRUE) {
			getVmsToDatacentersMap().put(vmId, datacenterId);
			getVmsCreatedList().add(VmList.getById(getVmList(), vmId));
			Log.printLine(CloudSim.clock() + ": " + getName() + ": VM #" + vmId + " has been created in Datacenter #"
					+ datacenterId + ", Host #" + VmList.getById(getVmsCreatedList(), vmId).getHost().getId());
		} else {
			Log.printLine(CloudSim.clock() + ": " + getName() + ": Creation of VM #" + vmId + " failed in Datacenter #"
					+ datacenterId);
		}

		incrementVmsAcks();

		// System.out.println("Utilization of VM " +vmId+ " is "
		// +VmList.getById(getVmsCreatedList(),
		// vmId).getTotalUtilizationOfCpu(CloudSim.clock()));
		// all the requested VMs have been created

		if (getVmsCreatedList().size() == getVmList().size() - getVmsDestroyed()) {
			submitCloudlets();
		} else {
			// all the acks received, but some VMs were not created
			if (getVmsRequested() == getVmsAcks()) {
				// find id of the next datacenter that has not been tried
				for (int nextDatacenterId : getDatacenterIdsList()) {
					if (!getDatacenterRequestedIdsList().contains(nextDatacenterId)) {
						createVmsInDatacenter(nextDatacenterId);
						return;
					}
				}
				
				// all datacenters already queried
				if (getVmsCreatedList().size() > 0) { // if some vm were created
					submitCloudlets();
				} else { // no vms created. abort
					Log.printLine(CloudSim.clock() + ": " + getName()
							+ ": none of the required VMs could be created. Aborting");
					finishExecution();
				}
			}
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
/*
	protected void processCloudletReturn(SimEvent ev) {
		Cloudlet cloudlet = (Cloudlet) ev.getData();
		// System.out.println("Utilization of the virtual machine executing
		// cloudlet #" +cloudlet.getCloudletId()+ " has been saved in the
		// table.");
		// System.out.println("Clock value of the simulation while saving the
		// utilization of the cloudlet # " +cloudlet.getCloudletId()+ " is "
		// +CloudSim.clock());
		//CloudletUtilization.setCletUtilization(cloudlet, cloudlet.getUtilizationOfCpu(CloudSim.clock()));
		getCloudletReceivedList().add(cloudlet);		
		Log.printLine(CloudSim.clock() + ": " + getName() + ": Cloudlet " + cloudlet.getCloudletId() + " received");
		cloudletsSubmitted--;
		//if(cloudlet.getCloudletId() == 61){
			//System.out.println("Check for cloudlet 61");
		//}
		Vm vm; // Written by Yogesh Sharma
		Host host;
		vm = VmCloudletMapping.getVm(cloudlet); // Written by Yogesh Sharma
		host = vm.getHost(); // Written by Yogesh Sharma
		if(RunTimeConstants.test == true){
			System.out.println("VM #" + vm.getId() + " corresponding to Cloudlet #" + cloudlet.getCloudletId()
				+ " has also been destroyed");
		}
		WorkLoadModel.setFinishedCloudlets();
		host.vmDestroy(vm); // Written by Yogesh Sharma
		setVmsDestroyed(cloudlet.getVmId());

		for (int i = 0; i < vmsCreatedList.size(); i++) {
			if (vmsCreatedList.get(i) == vm) {
				vmsCreatedList.remove(i);
				break;
			}
		}
		if (host.getVmList().isEmpty()) {
			if(RunTimeConstants.test == true){
				System.out.println(CloudSim.clock() + " State of the host #" + host.getId() + " has been changed to Idle");
			}
			IdleHostTime.setHostDeallocationTime(host, CloudSim.clock());
			IdleHostTime.setHostDeallocationFlag(host, true);
		}
		sendNow(getVmsToDatacentersMap().get(vm.getId()), CloudSimTags.VM_DESTROY, vm);

		if (getCloudletList().size() == 0 && cloudletsSubmitted == 0) { // all
																		// cloudlets
																		// executed
			Log.printLine(CloudSim.clock() + ": " + getName() + ": All Cloudlets executed. Finishing...");
			clearDatacenters();
			if (counter >= numberofBags) {				
				finishExecution();
			}
		} else { // some cloudlets haven't finished yet
			if (getCloudletList().size() > 0 && cloudletsSubmitted == 0) {
				// all the cloudlets sent finished. It means that some bount
				// cloudlet is waiting its VM be created
				clearDatacenters();
				createVmsInDatacenter(0);

			}

		}
	}
*/
	
	protected void processCloudletReturn(SimEvent ev) {
		Cloudlet cloudlet = (Cloudlet) ev.getData();
		getCloudletReceivedList().add(cloudlet);
		Log.printLine(CloudSim.clock() + ": " + getName() + ": Cloudlet " + cloudlet.getCloudletId()
				+ " received");
		cloudletsSubmitted--;
		if (getCloudletList().size() == 0 && cloudletsSubmitted == 0) { // all cloudlets executed
			Log.printLine(CloudSim.clock() + ": " + getName() + ": All Cloudlets executed. Finishing...");
			clearDatacenters();
			finishExecution();
		} else { // some cloudlets haven't finished yet
			if (getCloudletList().size() > 0 && cloudletsSubmitted == 0) {
				// all the cloudlets sent finished. It means that some bount
				// cloudlet is waiting its VM be created
				clearDatacenters();
				createVmsInDatacenter(0);
			}

		}
	}
	
	/**
	 * Overrides this method when making a new and different type of Broker.
	 * This method is called by {@link #body()} for incoming unknown tags.
	 * 
	 * @param ev
	 *            a SimEvent object
	 * @pre ev != null
	 * @post $none
	 */
	protected void processOtherEvent(SimEvent ev) {
		if (ev == null) {
			Log.printLine(getName() + ".processOtherEvent(): " + "Error - an event is null.");
			return;
		}

		Log.printLine(getName() + ".processOtherEvent(): " + "Error - event unknown by this DatacenterBroker."
				+ ev.getTag() + " " + ev.getSource());
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
		/*
		if (firstCycleFlag == true) {
			bagSize = WorkLoadModel.getBagSize(botID);
			double dRate = WorkLoadModel.getDataRate();
			CloudletLength(bagSize);			
			if(RunTimeConstants.test == true){
				System.out.println("Bag of Tasks # " + botID + " has been received with " + bagSize + " tasks");
			}
			// for(int i=0;i<bagSize;i++){
			schedule(getId(), dRate, CloudSimTags.NextCycle);
			// }
			firstCycleFlag = false;
		}
		*/
	}
	
/*
	public ArrayList<Long> cletLengthList;
	public static long cletMax;
	public static int bagSize;

	private void CloudletLength(int bagSizeTemp) { // Written by Yogesh Sharma
		cletLengthList = new ArrayList<Long>();
		long cletLength[] = new long[bagSizeTemp];
		cletLengthList = WorkLoadModel.getCloudlets(bagSizeTemp);
		for (int i = 0; i < cletLengthList.size(); i++) {
			//cletLength[i] = cletLengthList.get(i);
			cletLength[i] = cletLengthList.get(i);
		}
		
		if (bagSizeTemp > 1) { // This part need to changed according to the
								// order of the cloudlets(increasing or
								// decreasing)required in the simulation.
			long temp;
			// try {
			// if(cloudlet_order == 0){
			// BufferedReader read = new BufferedReader(new
			// FileReader("/rusers/postgrad/ysharma1/PhDWork/Java/Results/Cloudlet_Ordering.txt"));
			// cloudlet_order = Integer.parseInt(read.readLine());
			// }
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
			// } catch (NumberFormatException | IOException e) {
			// System.out.println("Error occurred while reading the cloudlet
			// order in Broker");
			// }
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
*/
	
/*	
	private void CreateVMs(Cloudlet clet, int datacenterId) { // This function
																// is written by
																// Yogesh Sharma
																// for the
																// implementation
																// of rate of
																// arrival of
																// cloudlets.
		int cletId = clet.getCloudletId();
		int vmid = clet.getCloudletId();
		int mips = 1000;
		//int mips = 1;		
		long size = 1000; // image size (MB)
		int ram = 128; // vm memory (MB)
		long bw = 1;
		int pesNumber = 1;
		String vmm = "Xen"; // VMM name

		// create VM
		Vm vm = new Vm(vmid, getId(), mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
		// add the VM to the vmList
		getVmList().add(vm);
		bindCloudletToVm(cletId, vmid);
		VmCloudletMapping.createVmCloutletMap(vm, clet);
		CloudletInitialLength.setCletInitialLength(clet, clet.getCloudletLength());
		VmCloudletMapping.createVmCloutletMap(vm, clet);
		dc.processVmCreate(vm);
		getVmsToDatacentersMap().put(vmid, datacenterId);
		getVmsCreatedList().add(VmList.getById(getVmList(), vmid));
		flag = true; // This flag is for storing the initial cloudlet length and
						// to create cloudlet backup for the later use.
		submitCloudlets();

		// }

	}
*/
	//public static CloudletNormalization cletNorm = new CloudletNormalization();

	/*
	private void CreateCloudlets(int datacenterId, int bagSize) { // This
																	// function
																	// is
																	// written
																	// by Yogesh
																	// Sharma
																	// for the
																	// implementation
																	// of rate
																	// of
																	// arrival
																	// of
																	// cloudlets.
		for (int k = 0; k < bagSize; k++) {
			int newCletID = 0;
			int userID = cloudletSubmittedList.get(0).getUserId();
			// userID = cloudletList.get(1).getUserId();
			for (int i = 0; i < cloudletSubmittedList.size(); i++) {
				newCletID = i;
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

			// Cloudlet cloudlet = new Cloudlet(newCletID, length_temp,
			// pesNumber, fileSize, outputSize, new
			// UtilizationModelNormalized(length_temp,length_temp), new
			// UtilizationModelNormalized(length_temp,length_temp),
			// utilizationModel);

			Cloudlet cloudlet = new Cloudlet(newCletID, length_temp, pesNumber, fileSize, outputSize,
					new UtilizationModelNormalized(length_temp, cletMax),
					new UtilizationModelNormalized(length_temp, cletMax), utilizationModel);

			cloudlet.setUserId(userID);

			BagTaskMapping.setBagTaskMap(botID, newCletID);

			dc.cletNormDC.setCloudletNormalizedLength(cloudlet, cletMax);			

			getCloudletList().add(cloudlet);			
			
			if(dc.flag_record == true){ 				
				sufferedCloudlets.add(cloudlet);
			}

			CreateVMs(cloudlet, datacenterId);
		}
		cletLengthList.clear();

	}
*/	
	/*
	public void setSufferedCloudletsList(){
		for(int i=0;i<cloudletSubmittedList.size();i++){
			Cloudlet clet;
			clet = cloudletSubmittedList.get(i);
			if(clet.isFinished()==false){
				sufferedCloudlets.add(clet);
			}
		}
	}
	*/
	//public static HashMap<Cloudlet, Double> inittimeCloudlet = new HashMap<Cloudlet, Double>();
	//public HashMap<Integer, Long> cloudletTableBackup = new HashMap<Integer, Long>();

	/**
	 * Submit cloudlets to the created VMs.
	 * 
	 * @pre $none
	 * @post $none
	 */
	/*
	protected void submitCloudlets() {
		int vmIndex = 0;
		Host host;
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

			
			maplistCloudlettoVm(cloudlet.getVmId(), cloudlet.getCloudletId());
			idtoCloudletList(cloudlet.getCloudletId(), cloudlet);
			maplistUsertoCloudlet(cloudlet.getCloudletId(), cloudlet.getUserId());

			if (flag == true) {
				inittimeCloudlet.put(cloudlet, CloudSim.clock());
				cloudletTableBackup.put(cloudlet.getCloudletId(), cloudlet.getCloudletLength());
				CloudletStatus.setremainingCloudlet(cloudlet,
						(long) Math.floor(cloudlet.getCloudletLength() / Consts.MILLION)); // Written
																							// by
																							// Yogesh
																							// Sharma
				CloudletStatus.setsofarCloudlet(cloudlet, (long) 0); // Written
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
	*/	
		protected void submitCloudlets() {
			int vmIndex = 0;
			for (Cloudlet cloudlet : getCloudletList()) {
				Vm vm;
				// if user didn't bind this cloudlet and it has not been executed yet
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

				Log.printLine(CloudSim.clock() + ": " + getName() + ": Sending cloudlet "
						+ cloudlet.getCloudletId() + " to VM #" + vm.getId());
				cloudlet.setVmId(vm.getId());
				sendNow(getVmsToDatacentersMap().get(vm.getId()), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
				cloudletsSubmitted++;
				vmIndex = (vmIndex + 1) % getVmsCreatedList().size();
				getCloudletSubmittedList().add(cloudlet);
			}

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
		utilization = VmCloudletMapping.getCloudlet(vm).getUtilizationOfCpu(CloudSim.clock());
		ftaUtilization = dc.fread.getCurrentUtilization(HostMapping.getFTAhostID(host.getId()));
		utilization = utilization + ftaUtilization;		
		if(utilization > 1.0){
			utilization = 1.0;
		}
		vm.setCurrentUtilization(utilization);		
		reliability = rblcal.getReliability(host, vm, utilization);
		reliability = Math.ceil(reliability * 1000);
		reliability = reliability / 1000;				
		System.out.println("Reliability of VM #" +vm.getId()+ " at utilization " +utilization+ " is " +reliability);
		
		HostVMMappingReliability.setVmReliability(vm, reliability);		
		HostVMMappingReliability.sethostVmReliabilityTable(host, vm);
		
		//HostVMMappingReliability.setSystemReliability(host);
		HostVMMappingReliability.setSystemReliability();
		HostVMMappingReliability.sethostClock(host, CloudSim.clock());		
		HostVMMappingUtilization.setVmUtilization(vm, utilization);
	}
	*/
	
/*
	public static Double getcloudletInitialStartTime(Cloudlet clet) {
		Double time = 0.0;
		if (inittimeCloudlet.containsKey(clet)) {
			time = inittimeCloudlet.get(clet);
		}
		return time;
	}
*/
	/**
	 * Destroy the virtual machines running in datacenters.
	 * 
	 * @pre $none
	 * @post $none
	 */
	protected void clearDatacenters() {
		for (Vm vm : getVmsCreatedList()) {
			Log.printLine(CloudSim.clock() + ": " + getName() + ": Destroying VM #" + vm.getId());
			sendNow(getVmsToDatacentersMap().get(vm.getId()), CloudSimTags.VM_DESTROY, vm);
		}

		getVmsCreatedList().clear();

	}

	/**
	 * Send an internal event communicating the end of the simulation.
	 * 
	 * @pre $none
	 * @post $none
	 */
	
	protected void finishExecution() {
		sendNow(getId(), CloudSimTags.END_OF_SIMULATION);
	}
	/*
	protected void finishExecution() {
		ArrayList<Host> hostList = new ArrayList<Host>(); // Written by Yogesh
															// Sharma
		for (int i = 0; i < dc.hostList().size(); i++) {
			hostList.add(dc.hostList().get(i));
		}
		for (int i = 0; i < hostList.size(); i++) {
			if (hostList.get(i).isFailed() == false) {
				IdleHostTime.setHostAllocationTime(hostList.get(i), CloudSim.clock());
				IdleHostTime.setHostDeallocationFlag(hostList.get(i), false);
				IdleHostTime.setHostIdleTime(hostList.get(i));
			}
		}

		sendNow(getId(), CloudSimTags.END_OF_SIMULATION);
	}
*/
	/*
	 * (non-Javadoc)
	 * 
	 * @see cloudsim.core.SimEntity#shutdownEntity()
	 */
	@Override
	public void shutdownEntity() {
		Log.printLine(getName() + " is shutting down...");
	//	dc.furtherCheckpoints = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cloudsim.core.SimEntity#startEntity()
	 */
	@Override
	public void startEntity() {
		Log.printLine(getName() + " is starting...");
		schedule(getId(), 0, CloudSimTags.RESOURCE_CHARACTERISTICS_REQUEST);

		// for(int i=0;i<getVmList().size();i++){

		// Vm vm = getVmList().get(i);

		// Cloudlet clet = VmCloudletMapping.getCloudlet(vm);

		// send(2, .101, CloudSimTags.SAVE_VM_UTILIZATION, clet);
		// }
	}

	/**
	 * Gets the vm list.
	 * 
	 * @param <T>
	 *            the generic type
	 * @return the vm list
	 */
	@SuppressWarnings("unchecked")
	public <T extends Vm> List<T> getVmList() {
		return (List<T>) vmList;
	}

	/**
	 * Sets the vm list.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param vmList
	 *            the new vm list
	 */
	protected <T extends Vm> void setVmList(List<T> vmList) {
		this.vmList = vmList;
	}

	/**
	 * Gets the cloudlet list.
	 * 
	 * @param <T>
	 *            the generic type
	 * @return the cloudlet list
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Cloudlet> List<T> getCloudletList() {
		return (List<T>) cloudletList;
	}

	// public <T extends Cloudlet> void setNormCloudletList(List<T>Cloudlet){

	// }
	/**
	 * Sets the cloudlet list.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param cloudletList
	 *            the new cloudlet list
	 */
	protected <T extends Cloudlet> void setCloudletList(List<T> cloudletList) {
		this.cloudletList = cloudletList;
	}

	/**
	 * Gets the cloudlet submitted list.
	 * 
	 * @param <T>
	 *            the generic type
	 * @return the cloudlet submitted list
	 */
	@SuppressWarnings("unchecked")
	
	public <T extends Cloudlet> List<T> getCloudletSubmittedList() {	
		return (List<T>) cloudletSubmittedList;
	}

	/**
	 * Sets the cloudlet submitted list.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param cloudletSubmittedList
	 *            the new cloudlet submitted list
	 */
	protected <T extends Cloudlet> void setCloudletSubmittedList(List<T> cloudletSubmittedList) {
		this.cloudletSubmittedList = cloudletSubmittedList;
	}

	/**
	 * Gets the cloudlet received list.
	 * 
	 * @param <T>
	 *            the generic type
	 * @return the cloudlet received list
	 */
	@SuppressWarnings("unchecked")
	//public static <T extends Cloudlet> List<T> getCloudletReceivedList() {
	public <T extends Cloudlet> List<T> getCloudletReceivedList() {	
		return (List<T>) cloudletReceivedList;
	}
	
	//public ArrayList<Cloudlet>getSufferedCloudletsList(){
	//	return sufferedCloudlets;
	//}

	/**
	 * Sets the cloudlet received list.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param cloudletReceivedList
	 *            the new cloudlet received list
	 */
	protected <T extends Cloudlet> void setCloudletReceivedList(List<T> cloudletReceivedList) {
		this.cloudletReceivedList = cloudletReceivedList;
	}

	/**
	 * Gets the vm list.
	 * 
	 * @param <T>
	 *            the generic type
	 * @return the vm list
	 */
	@SuppressWarnings("unchecked")
	//public static <T extends Vm> List<T> getVmsCreatedList() {
	public <T extends Vm> List<T> getVmsCreatedList() {	
		return (List<T>) vmsCreatedList;
	}

	/**
	 * Sets the vm list.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param vmsCreatedList
	 *            the vms created list
	 */
	protected <T extends Vm> void setVmsCreatedList(List<T> vmsCreatedList) {
		this.vmsCreatedList = vmsCreatedList;
	}

	/**
	 * Gets the vms requested.
	 * 
	 * @return the vms requested
	 */
	protected int getVmsRequested() {
		return vmsRequested;
	}

	/**
	 * Sets the vms requested.
	 * 
	 * @param vmsRequested
	 *            the new vms requested
	 */
	protected void setVmsRequested(int vmsRequested) {
		this.vmsRequested = vmsRequested;
	}

	/**
	 * Gets the vms acks.
	 * 
	 * @return the vms acks
	 */
	protected int getVmsAcks() {
		return vmsAcks;
	}

	/**
	 * Sets the vms acks.
	 * 
	 * @param vmsAcks
	 *            the new vms acks
	 */
	protected void setVmsAcks(int vmsAcks) {
		this.vmsAcks = vmsAcks;
	}

	/**
	 * Increment vms acks.
	 */
	protected void incrementVmsAcks() {
		vmsAcks++;
	}

	/**
	 * Gets the vms destroyed.
	 * 
	 * @return the vms destroyed
	 */
	protected int getVmsDestroyed() {
		return vmsDestroyed;
	}

	/**
	 * Sets the vms destroyed.
	 * 
	 * @param vmsDestroyed
	 *            the new vms destroyed
	 */
	protected void setVmsDestroyed(int vmsDestroyed) {
		this.vmsDestroyed = vmsDestroyed;
	}

	/**
	 * Gets the datacenter ids list.
	 * 
	 * @return the datacenter ids list
	 */
	protected List<Integer> getDatacenterIdsList() {
		return datacenterIdsList;
	}

	/**
	 * Sets the datacenter ids list.
	 * 
	 * @param datacenterIdsList
	 *            the new datacenter ids list
	 */
	protected void setDatacenterIdsList(List<Integer> datacenterIdsList) {
		this.datacenterIdsList = datacenterIdsList;
	}

	/**
	 * Gets the vms to datacenters map.
	 * 
	 * @return the vms to datacenters map
	 */
	protected Map<Integer, Integer> getVmsToDatacentersMap() {
		return vmsToDatacentersMap;
	}

	/**
	 * Sets the vms to datacenters map.
	 * 
	 * @param vmsToDatacentersMap
	 *            the vms to datacenters map
	 */
	protected void setVmsToDatacentersMap(Map<Integer, Integer> vmsToDatacentersMap) {
		this.vmsToDatacentersMap = vmsToDatacentersMap;
	}

	/**
	 * Gets the datacenter characteristics list.
	 * 
	 * @return the datacenter characteristics list
	 */
	protected Map<Integer, DatacenterCharacteristics> getDatacenterCharacteristicsList() {
		return datacenterCharacteristicsList;
	}

	/**
	 * Sets the datacenter characteristics list.
	 * 
	 * @param datacenterCharacteristicsList
	 *            the datacenter characteristics list
	 */
	protected void setDatacenterCharacteristicsList(
			Map<Integer, DatacenterCharacteristics> datacenterCharacteristicsList) {
		this.datacenterCharacteristicsList = datacenterCharacteristicsList;
	}

	/**
	 * Gets the datacenter requested ids list.
	 * 
	 * @return the datacenter requested ids list
	 */
	protected List<Integer> getDatacenterRequestedIdsList() {
		return datacenterRequestedIdsList;
	}

	/**
	 * Sets the datacenter requested ids list.
	 * 
	 * @param datacenterRequestedIdsList
	 *            the new datacenter requested ids list
	 */
	protected void setDatacenterRequestedIdsList(List<Integer> datacenterRequestedIdsList) {
		this.datacenterRequestedIdsList = datacenterRequestedIdsList;
	}

}
