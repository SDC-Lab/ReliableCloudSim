package org.cloudbus.cloudsim.failure;

import java.util.ArrayList;

import org.cloudbus.cloudsim.Vm;

public class MigrationOverheadModel {
	
	public ArrayList<Integer>cletIDList;
	
	public VmUtilization vmUtl;
	
	//Initialization and Reservation pre-overheads
	public double preMigrationOverheads = 15.64;
	
	public double postMigrationOverheads = .9070;
	
	//Xen default page size in bits. Actual size is 4KB
	public int pageSize = 32768;
	
	//Link speed is in bits equivalent to 1Gbps 
	public int linkSpeed = 1000000000;
	
	public void setVmUtilizationObject(VmUtilization vmUtl){
		this.vmUtl = vmUtl;
	}
		
	public double TotalMigrationTime(Vm vm){
		double vmUtilization;
		double vmSize;		
		double totalMigrationTime;
		vmUtilization = vmUtl.getCurrentVmUtilization(vm.getId());
		vmSize = vm.getRam()*vmUtilization;
		totalMigrationTime = ((preMigrationOverheads + ((((4 * (vmSize*1024*1024*8))-pageSize))/linkSpeed)));
		return totalMigrationTime;
	}	
	
	public double TotalDownTime(Vm vm){
		double vmUtilization;
		double vmSize;		
		double downTimeOverheads;
		vmUtilization = vmUtl.getCurrentVmUtilization(vm.getId());
		vmSize = vm.getRam()*vmUtilization;
		downTimeOverheads = (postMigrationOverheads + ((vmSize*1024*1024*8)/linkSpeed));
		return downTimeOverheads;
	}
	
	public double getMigrationOverhead(Vm vm){
		double totalMigrationOverhead;		
		totalMigrationOverhead = TotalMigrationTime(vm) + TotalDownTime(vm);
		return totalMigrationOverhead;
	}
}
