/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.failure.AvailabilityCalculator;
import org.cloudbus.cloudsim.failure.FTAFileReader;
import org.cloudbus.cloudsim.failure.HostMapping;
import org.cloudbus.cloudsim.failure.HostVMMapping;
import org.cloudbus.cloudsim.failure.HostVMMappingReliability;
import org.cloudbus.cloudsim.failure.HostVMMappingUtilization;
import org.cloudbus.cloudsim.failure.MaintainabilityCalculator;
import org.cloudbus.cloudsim.failure.PeListforFailures;
import org.cloudbus.cloudsim.failure.ReliabilityCalculator;
import org.cloudbus.cloudsim.failure.VmCloudletMapping;
import org.cloudbus.cloudsim.failure.VmUtilization;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

/**
 * VmAllocationPolicySimple is an VmAllocationPolicy that chooses, as the host for a VM, the host
 * with less PEs in use.
 * 
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 */
public class VmAllocationPolicySimple extends VmAllocationPolicy {

	/** The vm table. */
	private Map<String, Host> vmTable;

	/** The used pes. */
	private Map<String, Integer> usedPes;

	/** The free pes. */
	private List<Integer> freePes;

	public HashMap<Host, Integer>remainingPes = new HashMap<Host, Integer>();
	
	public ArrayList<Integer>ftaNodeList = new ArrayList<Integer>();	
	
	FTAFileReader ftaread = new FTAFileReader();
	

	/**
	 * Creates the new VmAllocationPolicySimple object.
	 * 
	 * @param list the list
	 * @pre $none
	 * @post $none
	 */
	public VmAllocationPolicySimple(List<? extends Host> list) {
		super(list);

		setFreePes(new ArrayList<Integer>());
		for (Host host : getHostList()) {
			getFreePes().add(host.getNumberOfPes());

		}

		setVmTable(new HashMap<String, Host>());
		setUsedPes(new HashMap<String, Integer>());
		RemainingPesTable();
	}

	public boolean allocateHostForVm(Vm vm){
		Host host1 = null;
		int peCount;
		int vmCount;
		boolean result = false;
		if (!getVmTable().containsKey(vm.getUid())) {
		for(Host host: getHostList()){
			host1 = host;
			peCount = host1.getNumberOfPes();
			vmCount = host1.getVmList().size();
			if(peCount > vmCount){
				result = host1.vmCreate(vm);						
				HostVMMapping.sethostVmMapTable(host1, vm);				
				System.out.println("#################################################");				
				Log.formatLine(
						"%.2f: VM #" + vm.getId() + " has been allocated to the host #" + host1.getId(),
						CloudSim.clock());
				ReliabilityCalculator(host1, vm);
				break;
			}
		}
		if(result){
			getVmTable().put(vm.getUid(), host1);			
		}
		else{
			System.out.println("An another node need to be provisioned");
			host1 = provisionNode();
			result = host1.vmCreate(vm);	
			HostVMMapping.sethostVmMapTable(host1, vm);					
			System.out.println("#################################################");					
			Log.formatLine(
					"%.2f: VM #" + vm.getId() + " has been allocated to the host #" + host1.getId(),
					CloudSim.clock());
			ReliabilityCalculator(host1, vm);
			getVmTable().put(vm.getUid(), host1);
			//getUsedPes().put(vm.getUid(), requiredPes);
			//setRemainingPesTable(host, requiredPes);
		}
	}
		return result;
	}
	/**
	 * Allocates a host for a given VM.
	 * 
	 * @param vm VM specification
	 * @return $true if the host could be allocated; $false otherwise
	 * @pre $none
	 * @post $none
	 */
	//@Override
	/*
	public boolean allocateHostForVm(Vm vm) {
		
		int requiredPes = vm.getNumberOfPes();
		boolean result = false;
		int tries = 0;
		
		List<Integer> freePesTmp = new ArrayList<Integer>();	
		
		for (Integer freePes : getFreePes()) {
			freePesTmp.add(freePes);			
		}

		if (!getVmTable().containsKey(vm.getUid())) { // if this vm was not created
			do {// we still trying until we find a host or until we try all of them
				int moreFree = Integer.MIN_VALUE;
				int idx = -1;
				
				// we want the host with less pes in use
				for (int i = 0; i < freePesTmp.size(); i++) {
					if (freePesTmp.get(i) > moreFree) {
						moreFree = freePesTmp.get(i);
						idx = i;
					}
				}			
								
				Host host = getHostList().get(idx);
				result = host.vmCreate(vm);

				if (result) { // if vm were succesfully created in the host
					getVmTable().put(vm.getUid(), host);
					getUsedPes().put(vm.getUid(), requiredPes);
					getFreePes().set(idx, getFreePes().get(idx) - requiredPes);
					result = true;
					break;
				} else {
					freePesTmp.set(idx, Integer.MIN_VALUE);
				}
				tries++;
			} while (!result && tries < getFreePes().size());

		}

		return result;
	}
	*/
	
	
	
	public void RemainingPesTable(){
		Host host;		
		for(int i=0;i<getHostList().size();i++){
			host = getHostList().get(i);
			remainingPes.put(host, host.getNumberOfPes());
		}		
	
	}
	
	public void setRemainingPesTable(Host host, int requiredPes){
		int initialPes = 0;
		if(remainingPes.containsKey(host)){
			initialPes = remainingPes.get(host);
		}
		initialPes = initialPes - requiredPes;
		remainingPes.put(host, initialPes);
	}
	
	
	boolean flag = false;
	ReliabilityCalculator rblcal = new ReliabilityCalculator();
	//ArrayList<Double>reliability = new ArrayList<Double>();
	//FileWriter writer; 
	
	AvailabilityCalculator avlcal = new AvailabilityCalculator();
	
	MaintainabilityCalculator mtncal = new MaintainabilityCalculator();
	/*
	public boolean allocateHostForVm(Vm vm){
		boolean result = false;
		Host host = null;		
		int requiredPes = vm.getNumberOfPes();
		 
			if (!getVmTable().containsKey(vm.getUid())) {
				
				for(int i=0;i<remainingPes.size();i++){
					host = getHostList().get(i);
					if(requiredPes<=remainingPes.get(host)){					
						result = host.vmCreate(vm);						
						HostVMMapping.sethostVmMapTable(host, vm);
						//reliability.add(rblcal.getReliability(host, vm));	
						//rblcal.getReliability(host, vm);
						//mtncal.getMaintainability(host, vm);
						//avlcal.getAvailability(host);
						System.out.println("#################################################");
						//if(vm.isBeingInstantiated()){
						Log.formatLine(
								"%.2f: VM #" + vm.getId() + " has been allocated to the host #" + host.getId(),
								CloudSim.clock());
							ReliabilityCalculator(host, vm);
						//}
						break;
					}				
				}			
				
				if(result){
					getVmTable().put(vm.getUid(), host);
					getUsedPes().put(vm.getUid(), requiredPes);
					setRemainingPesTable(host, requiredPes);
					//getFreePes().set(idx, getFreePes().get(idx) - requiredPes);
				}
				else{
					System.out.println("An another node need to be provisioned");
					host = provisionNode();
					result = host.vmCreate(vm);	
					HostVMMapping.sethostVmMapTable(host, vm);					
					System.out.println("#################################################");					
					Log.formatLine(
							"%.2f: VM #" + vm.getId() + " has been allocated to the host #" + host.getId(),
							CloudSim.clock());
					ReliabilityCalculator(host, vm);
					getVmTable().put(vm.getUid(), host);
					getUsedPes().put(vm.getUid(), requiredPes);
					setRemainingPesTable(host, requiredPes);
					
				}
			}		
			
		return result;
		
	}
	
	*/
	public static ArrayList<Integer> sortedFtaNodeList = new ArrayList<Integer>();
	
	public Host provisionNode(){
		int ftaNodeID = 0;		
		double utilization;
		int ram;
		int peCount;
		int peListIndex = 0;
		long storage = 1000000;
		int bw = 10000;
		int hostId = 0;
		ftaNodeList = ftaread.getPowerHostListSorted(); //This statement need to be changed with every simulation. 
		for(int i=0;i<ftaNodeList.size();i++){
			if(ftaNodeList.get(i)!=HostMapping.getFTAhostID(i)){
				HostMapping.createHostMap(i,ftaNodeList.get(i));
				ftaNodeID = ftaNodeList.get(i);
				break;
			}
		}
		utilization = ftaread.getCurrentUtilization(ftaNodeID);
		ram = ftaread.getMemorySize(ftaNodeID);		
		peCount = ftaread.getProcessorCount(ftaNodeID);	
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
						//utilization,
						new RamProvisionerSimple(ram), 
						new BwProvisionerSimple(bw), 
						storage, 
						PeListforFailures.getPeList(peListIndex),							
						new VmSchedulerTimeShared(PeListforFailures.getPeList(peListIndex))
						));		
		//RemainingPesTable();
		return getHostList().get(hostId);
	}

	/**
	 * Function to calculate the reliability of every allocated VM.
	 */
	
	ReliabilityCalculator rlbcal = new ReliabilityCalculator();
	public void ReliabilityCalculator(Host host, Vm vm){
		double ftaUtilization;
		double utilization = 0;
		double reliability;
		//utilization = VmUtilization.getVmUtilization(vm.getId());;
		ftaUtilization = ftaread.getCurrentUtilization(HostMapping.getFTAhostID(host.getId()));
		utilization = utilization + ftaUtilization;		
		if(utilization > 1.0){
			utilization = 1.0;
		}
		//VmUtilization.setVmUtilization(vm.getId(), utilization);		
		reliability = rblcal.getReliability(host, vm, utilization);
		reliability = Math.ceil(reliability * 1000);
		reliability = reliability / 1000;
		System.out.println("Reliability of VM #" +vm.getId()+ " at utilization " +utilization+ " is " +reliability);
		//HostVMMappingReliability.sethostVmReliabilityTable(host, vm);
		HostVMMappingUtilization.sethostVmUtilizationTable(host, utilization);
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
		//int pes = getUsedPes().remove(vm.getUid());
		if (host != null) {
			host.vmDestroy(vm);
			//getFreePes().set(idx, getFreePes().get(idx) + pes);
			//setRemainingPesTable(host, -1);
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

	/**
	 * Gets the used pes.
	 * 
	 * @return the used pes
	 */
	protected Map<String, Integer> getUsedPes() {
		return usedPes;
	}

	/**
	 * Sets the used pes.
	 * 
	 * @param usedPes the used pes
	 */
	protected void setUsedPes(Map<String, Integer> usedPes) {
		this.usedPes = usedPes;
	}

	/**
	 * Gets the free pes.
	 * 
	 * @return the free pes
	 */
	protected List<Integer> getFreePes() {
		return freePes;
	}

	/**
	 * Sets the free pes.
	 * 
	 * @param freePes the new free pes
	 */
	protected void setFreePes(List<Integer> freePes) {
		this.freePes = freePes;
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

	/*
	 * (non-Javadoc)
	 * @see org.cloudbus.cloudsim.VmAllocationPolicy#allocateHostForVm(org.cloudbus.cloudsim.Vm,
	 * org.cloudbus.cloudsim.Host)
	 */
	@Override
	public boolean allocateHostForVm(Vm vm, Host host) {
		if (host.vmCreate(vm)) { // if vm has been succesfully created in the host
			getVmTable().put(vm.getUid(), host);

			int requiredPes = vm.getNumberOfPes();
			int idx = getHostList().indexOf(host);
			getUsedPes().put(vm.getUid(), requiredPes);
			getFreePes().set(idx, getFreePes().get(idx) - requiredPes);

			Log.formatLine(
					"%.2f: VM #" + vm.getId() + " has been allocated to the host #" + host.getId(),
					CloudSim.clock());
			return true;
		}

		return false;
	}
}
