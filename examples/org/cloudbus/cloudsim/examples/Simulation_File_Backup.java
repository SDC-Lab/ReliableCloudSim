package org.cloudbus.cloudsim.examples;

/**
 * This Simulation uses Reliability Aware Best Fit Decreasing algorithm to provision the hosts and before allocation of VM to the provisioned
 * nodes, it sort all the cloudlets running on the VMs in decreasing order according to their lengths. 
 * 
 * The fault tolerance mechanism used in this simulation is Restarting
 * 
 * */


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
//import java.util.Random;
import java.util.Scanner;
//import java.util.concurrent.ThreadLocalRandom;

import org.cloudbus.cloudsim.Broker;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.UtilizationModelStochastic;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.distributions.ExponentialDistr;
//import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.distributions.WeibullDistr;
import org.cloudbus.cloudsim.failure.BagTaskMapping;
import org.cloudbus.cloudsim.failure.BinPacking;
import org.cloudbus.cloudsim.failure.CheckpointOverhead;
import org.cloudbus.cloudsim.failure.CloudletInitialLength;
import org.cloudbus.cloudsim.failure.CloudletNormalization;
import org.cloudbus.cloudsim.failure.CloudletReexecutionPart;
import org.cloudbus.cloudsim.failure.CloudletUtilization;
import org.cloudbus.cloudsim.failure.DownTimeTable;
import org.cloudbus.cloudsim.failure.FTAFailureGenerator;
import org.cloudbus.cloudsim.failure.FTAFileReader;
import org.cloudbus.cloudsim.failure.FTAFileReaderGrid5000;
import org.cloudbus.cloudsim.failure.FailureCloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.failure.FailureCloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.failure.FailureHost;
import org.cloudbus.cloudsim.failure.FailurePrediction;
import org.cloudbus.cloudsim.failure.FailureVm;
import org.cloudbus.cloudsim.failure.FailureDatacenter;
import org.cloudbus.cloudsim.failure.FailureDatacenterBroker;
import org.cloudbus.cloudsim.failure.FailureGenerator;
import org.cloudbus.cloudsim.failure.GoogleFailureGenerator;
import org.cloudbus.cloudsim.failure.HostCounter;
import org.cloudbus.cloudsim.failure.HostMapping;
import org.cloudbus.cloudsim.failure.HostProvisioning;
import org.cloudbus.cloudsim.failure.HostUtilization;
import org.cloudbus.cloudsim.failure.HostVMMapping;
import org.cloudbus.cloudsim.failure.HostVMMappingReliability;
import org.cloudbus.cloudsim.failure.HostVMMappingUtilization;
import org.cloudbus.cloudsim.failure.IdleHostTime;
import org.cloudbus.cloudsim.failure.IdleTime;
import org.cloudbus.cloudsim.failure.MigrationOverheadModel;
import org.cloudbus.cloudsim.failure.MigrationOverheadTable;
import org.cloudbus.cloudsim.failure.PeListforFailures;
import org.cloudbus.cloudsim.failure.PowerModel;
import org.cloudbus.cloudsim.failure.PowerModelSpecPowerHpProLiantDL380G5;
import org.cloudbus.cloudsim.failure.PowerModelSpecPowerHpProLiantDL560Gen9;
import org.cloudbus.cloudsim.failure.PowerModelSpecPowerHpProLiantDL785G5AMD8376;
import org.cloudbus.cloudsim.failure.PowerModelSpecPowerIntelSE7520AF2Xeon3600;
import org.cloudbus.cloudsim.failure.PowerModelSpecPowerIntelXeonE54669;
import org.cloudbus.cloudsim.failure.PowerModelSpecPowerPlatHomeTRQX150SA;
import org.cloudbus.cloudsim.failure.ReliabilityCalculator;
import org.cloudbus.cloudsim.failure.RunTimeConstants;
import org.cloudbus.cloudsim.failure.UtilizationModel10Percent;
import org.cloudbus.cloudsim.failure.UtilizationModelNormalized;
import org.cloudbus.cloudsim.failure.UtilizationModelRandom;
import org.cloudbus.cloudsim.failure.VmAllocationPolicyFailureAware;
import org.cloudbus.cloudsim.failure.VmCloudletMapping;
import org.cloudbus.cloudsim.failure.VmMigrationRecord;
import org.cloudbus.cloudsim.failure.VmUtilization;
import org.cloudbus.cloudsim.failure.WorkLoadModel;
import org.cloudbus.cloudsim.power.*;

public class Simulation_File_Backup{
	
//Create the list of cloudlets
	private static List<Cloudlet> cloudletList;
	
//Create the list of virtual machines
	//private static List<Vm> vmList;
	private static List<Vm> vmList;
	
	
	//private static List<Vm> vmListForHostProvisioning;
	private static List<Vm> vmListForHostProvisioning;

	//public static List<Host> hostListBackup = new ArrayList<Host>();
	public static List<Host> hostListBackup = new ArrayList<Host>();
	
//Create the list of backup cloudlets
	//private static List<Cloudlet> cloudletListbackup = new ArrayList<Cloudlet>();
	
	private static HashMap<Integer, Long> cloudletTableBackup = new HashMap<Integer, Long>();
	
	private static long cloudlet_max = 0;
	
	private static int total_core_count = 0;
	
	//private static FTAFileReader ftaread;
	
	private static FTAFileReaderGrid5000 ftaread;
	
	public static PowerModelSpecPowerHpProLiantDL560Gen9 PMcore128and80 = new PowerModelSpecPowerHpProLiantDL560Gen9();
	
	public static PowerModelSpecPowerHpProLiantDL380G5 PMcore4Mem8 = new PowerModelSpecPowerHpProLiantDL380G5();
	
	public static PowerModelSpecPowerHpProLiantDL785G5AMD8376 PMcore32 = new PowerModelSpecPowerHpProLiantDL785G5AMD8376();
	
	public static PowerModelSpecPowerIntelSE7520AF2Xeon3600 PMcore2 = new PowerModelSpecPowerIntelSE7520AF2Xeon3600();
	
	public static PowerModelSpecPowerIntelXeonE54669 PMcore256 = new PowerModelSpecPowerIntelXeonE54669();
	
	public static PowerModelSpecPowerPlatHomeTRQX150SA PMcore4Mem16 = new PowerModelSpecPowerPlatHomeTRQX150SA();
	
	public static BinPacking bpack = new BinPacking();
	
	//public static HostMapping hostmap = new HostMapping();
	
	public static CloudletNormalization cletNorm = new CloudletNormalization();
	
	//public static HashMap<Vm, Cloudlet>VmCletBindingTable = new HashMap<Vm, Cloudlet>();
	public static HashMap<Integer, ArrayList<Integer>>VmCletBindingTable = new HashMap<Integer, ArrayList<Integer>>();
	
	public static ArrayList<Integer> sortedFtaNodeList = new ArrayList<Integer>();
	
	public static double  stringencyFactor;
	
	public static double jobDeadline = 0.0;
	
	public static double jobUtilityValue = 0.0;
	
	public static double totalJobFinishedTime = 0.0;
	
	public static int host_order;
	
	public static int cloudlet_order;
	
	public static int faultToleranceMechanism;
	
	public static double previousCletFinishedTime = 0.0;
	
	public static int totalFailureCount = 0;
	
	public static long simulationStartTime;
	
	public static long simulationFinishedTime;
	
	public static int totalCheckpointsCount = 0;
	
	public static int countUtilityValue = 0;
	
	public static WorkLoadModel wlModel = new WorkLoadModel();
	
	public static ReliabilityCalculator rblcal = new ReliabilityCalculator();
	
	public static FailurePrediction failPredict = new FailurePrediction();
	
	public static VmUtilization vmUtl = new VmUtilization();
	
	public static HostVMMappingReliability hvmRbl = new HostVMMappingReliability();
	
	public static MigrationOverheadModel mgOver = new MigrationOverheadModel();
	
	public static VmMigrationRecord vmRecord = new VmMigrationRecord();
	
	public static int totalVmMigrations;
	
	public static int totalVmConsolidations;
	
	public static double finalTotalDownTime = 0.0;
	
	public static double finalTotalReexecutionTime = 0.0;
	
	public static PowerModel pow = new PowerModel();
	
	//public static FTAFailureGenerator ftagen;
	
	//public static List<Vm> vmList1;
	
//Populate the list of cloudlets/tasks	
private static List<Cloudlet> createCloudlet(int userId, int cloudlets){
		// Creates a container to store Cloudlets.
		
		List<Cloudlet> list = new ArrayList<Cloudlet>();
		
		//long length_temp;
		long[] length_cloudlet = new long[cloudlets]; 
		long fileSize = 300;
		long outputSize = 300;
		int pesNumber = 1;
		long temp;
		
		//long cloudlet_max = 0;
		
		/*
		Random rand = new Random();
		
		for(int i=0;i<cloudlets;i++){
			length1[i] = (long) ((long) ((rand.nextGaussian())*6.1+2.73)*3600);
			if(length1[i]<0){
				length1[i] = (length1[i] * -1);
			}
		}
		long max_length=0;
		for(int i=0;i<cloudlets;i++){
			if(max_length<length1[i]){
				max_length=length1[i];
			}
		}
		UtilizationModel utilizationModel = new UtilizationModelNormalized(length1[i], max_length);
		*/
		
		//System.out.println("Which way you wants to get the information regarding the Cloudlet Length");
	//	System.out.println(" To generate new cloudlets		  ----------------- 1 ");
	//	System.out.println(" To read from a file			  ----------------- 2 ");
		System.out.println("Setting Cloudlet Length");
		
		Cloudlet[] cloudlet = new Cloudlet[cloudlets];
		
		//int choice1;
	//	Scanner ch;
		//ch = new Scanner(System.in);		
	//	choice1 = ch.nextInt();
		//ch.close();
		
		//switch(choice1){
		/*		
		case 1:
			FileWriter writer; 
			Random rand = new Random();
			try {
				writer = new FileWriter("/rusers/postgrad/ysharma1/PhDWork/Java/Results/Cloudlets.txt", false);
				
				for(int i=0;i<cloudlets;i++){
					//numGen = new Random(System.currentTimeMillis());
					//length=(long) (2.11 * Math.pow(-Math.log(numGen.nextDouble()), 1 / 1.76));
					//System.out.println(length);
					//WeibullDistr weib = new WeibullDistr(2.05, 12.25);
					length_temp = (long) ((long) ((rand.nextGaussian())*6.1+2.73)*3600);
					if(length_temp==0){// This condition guarantees that the cloudlet length will not be 0.
						i=i-1;
						continue;
					}
					if(length_temp<0){// This condition guarantees that the cloudlet length will not be negative 
						length_temp=(length_temp*-1);
					}
					//ExponentialDistr exp = new ExponentialDistr(2.73, 6.1);
					System.out.println("Length of Cloudlet #" +i+ " is " +length_temp);
					length_cloudlet[i] = length_temp;		
					
					//length = (((long) weib.sample())*3600);
				}				
				
				for(int i=0;i<cloudlets;i++){
					writer.write(length_cloudlet[i]+ System.getProperty( "line.separator" ));
				}
				
				for(int i=0;i<cloudlets-1;i++){
					for(int j=i+1;j<cloudlets;j++){
						if(length_cloudlet[i]>length_cloudlet[j]){
							temp = length_cloudlet[i];
							length_cloudlet[i] = length_cloudlet[j];
							length_cloudlet[j] = temp;
						}
					}
				}	
				
				
				
				writer.close();
				
			} catch (IOException e) {
				System.out.println("Error has been occurred while writting cloudlet information to the file");
			}			
			
			break;
		*/
		//case 2:
			//try {
				//BufferedReader read = new BufferedReader(new FileReader("/rusers/postgrad/ysharma1/PhDWork/Java/Results/Cloudlets.txt"));
				
				//String current;				
				//for(int i=0;i<cloudlets;i++){	
				//	length_cloudlet[i] = Integer.parseInt(read.readLine());
								
				//}
			
			ArrayList<Long>cletLengthTemp = new ArrayList<Long>();
			cletLengthTemp = wlModel.getCloudlets(cloudlets);
			
			for(int i=0;i<cletLengthTemp.size();i++){
				length_cloudlet[i] = (cletLengthTemp.get(i)*10000);
				//length_cloudlet[i] = (cletLengthTemp.get(i)*100000000);
				//length_cloudlet[i] = (cletLengthTemp.get(i));
			}
			
			//try {
				//if(cloudlet_order == 0){
				//	BufferedReader read = new BufferedReader(new FileReader("/rusers/postgrad/ysharma1/PhDWork/Java/Results/Cloudlet_Ordering.txt"));
				//	cloudlet_order = Integer.parseInt(read.readLine());
				//}
			cloudlet_order = RunTimeConstants.cloudlet_order;
				switch(cloudlet_order){
					case 1:
						System.out.println(" Cloudlets have been ordered in Decreasing order ");
						for(int i=0;i<cloudlets-1;i++){
							for(int j=i+1;j<cloudlets;j++){
								if(length_cloudlet[i]<length_cloudlet[j]){
									temp = length_cloudlet[i];
									length_cloudlet[i] = length_cloudlet[j];
									length_cloudlet[j] = temp;
								}
							}
						}						
						break;
						
					case 2:
						System.out.println(" Cloudlets have been ordered in Increasing order ");
						for(int i=0;i<cloudlets-1;i++){
							for(int j=i+1;j<cloudlets;j++){
								if(length_cloudlet[i]>length_cloudlet[j]){
									temp = length_cloudlet[i];
									length_cloudlet[i] = length_cloudlet[j];
									length_cloudlet[j] = temp;
								}
							}
						}
						break;
						
					case 3:
						System.out.println(" Cloudlets will be processed as they are arriving without preprocessing");
						break;
				}
			//} catch (NumberFormatException | IOException e) {
				//System.out.println("Error occurred while reading the cloudlet order in simulation script");
			//}			
				
				System.out.println("Cloudlet length is ");
				
				for(int i=0;i<cloudlets;i++){					
					System.out.println(length_cloudlet[i]);					
				}
				
			//} catch (NumberFormatException | IOException e) {
				//System.out.println("Error has been occurred while reading cloudlet information from the file");
			//}
		
		//}		
		
		for(int i=0;i<cloudlets;i++){
			if(cloudlet_max<length_cloudlet[i]){
				cloudlet_max=length_cloudlet[i];
			}
		}
		
			
		//UtilizationModel utilizationModel1 = new UtilizationModelNormalized(); 
		UtilizationModel utilizationModel = new UtilizationModelStochastic();
		System.out.println("Bag of Tasks # " +0+ " has been received with " +cloudlets+ " tasks");
		//UtilizationModel utilizationModel = new UtilizationModel10Percent();
		
		for(int i=0;i<cloudlets;i++){			
			//cloudlet[i] = new Cloudlet(i, length_cloudlet[i], pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
			cloudlet[i] = new Cloudlet(i, length_cloudlet[i], pesNumber, fileSize, outputSize, new UtilizationModelNormalized(length_cloudlet[i],cloudlet_max), new UtilizationModelNormalized(length_cloudlet[i],cloudlet_max), utilizationModel);
			// setting the owner of these Cloudlets
			cloudlet[i].setUserId(userId);
			list.add(cloudlet[i]);		
			//cloudletListbackup.add(cloudlet[i]);
			cletNorm.setCloudletNormalizedLength(cloudlet[i], cloudlet_max);			
		}
		//for(int k=0;k<list.size();k++)
		//System.out.println("Cloudlets are " +list.get(k).getCloudletLength());
		return list;
	}	
	
//Populate the virtual machine list	
	//private static List<Vm> createVM(int userId, int vms) {
	private static List<Vm> createVM(int userId, int vms) {	

		//Creates a container to store VMs. This list is passed to the broker later
		List<Vm> list = new ArrayList<Vm>();
		ArrayList<Integer> cletIDList;
		ArrayList<Cloudlet> cletListTemp;
		//List<FailureVm> list = new ArrayList<FailureVm>();
				
		//VM Parameters
		
		int pesNumber; //number of cpus
		double utilization;
		String vmm = "Xen"; //VMM name
		//double[] utilization = new double[vms];
		//int[] utilization_sorted = new int[vms];
		//double[] normalizedCletLength = new double[vms];
		//int temp;
		
		//create VMs
		//Vm[] vm = new Vm[vms];
		
		//for(int i=0;i<vms;i++){
			//normalizedCletLength[i] = cletNorm.getNormalizedLength(cloudletList.get(i));
			//utilization[i] = cloudletList.get(i).getCloudletLength()/(double)cloudlet_max;
			//utilization_sorted[i] = (int) (utilization[i]*100);
			//utilization_sorted[i]=utilization[i];
		//}
		
		/*
		for(int i=0;i<vms-1;i++){
			for(int j=i+1;j<vms;j++){
				if(utilization_sorted[i]>utilization_sorted[j]){
					temp = utilization_sorted[i];
					utilization_sorted[i]=utilization_sorted[j];
					utilization_sorted[j]=temp;
				}
			}
		}
		*/
		
		
		for(int i=0;i<vms;i++){			
			long size = 1000; //image size (MB)
			int ram = 128; //vm memory (MB)
			int mips = 1000;
			//int mips = 1;
			//long bw = 1000;
			long bw = 1;
			int vmID;
			long cletLength;
			utilization = 0;
			pesNumber = 1;
			cletIDList = new ArrayList<Integer>();
			cletListTemp = new ArrayList<Cloudlet>();
			total_core_count = total_core_count + 1;
			list.add(new Vm(i, userId, mips, pesNumber, ram, bw, size, vmm, new FailureCloudletSchedulerTimeShared()));
			//list.add(new FailureVm(i, userId, mips, pesNumber, ram, bw, utilization, size, vmm, new FailureCloudletSchedulerTimeShared()))
			vmID = list.get(i).getId();
			cletIDList = bpack.getVMCletMapList(vmID);			
			VmCletBindingTable.put(vmID, cletIDList);
			//mips = mips * cletIDList.size();
			//list.get(i).setMips(mips);
			for(int k=0;k<cletIDList.size();k++){
				for(int l=0;l<cloudletList.size();l++){
					if(cletIDList.get(k) == cloudletList.get(l).getCloudletId()){
						cletLength = cloudletList.get(l).getCloudletLength();
						cletListTemp.add(cloudletList.get(l));						
						CloudletInitialLength.setCletInitialLength(cletIDList.get(k), cletLength);						
						utilization = utilization + cletNorm.getNormalizedLength(cloudletList.get(l));
						break;
					}
				}				
			}			
			VmCloudletMapping.createVmCloudletMap(list.get(i), cletListTemp);		
		}

		return list;
	}	
	
	//@SuppressWarnings("null")
	//private static Datacenter createDatacenter(String name) throws Exception
	private static FailureDatacenter createDatacenter(String name) throws Exception
	{
		//Create a list to store the physical machines
		List<Host> hostList = new ArrayList<Host>();
		
		int mips = 1000;
		//int mips = 1;
		
		//Create a list to store the cores for a machine
		//To create heterogeneous machines, separate lists need to be created
				
		//List<List<Pe>> peList = new ArrayList<List<Pe>>();
		
		//In the given data centers, there are five different kind of nodes containing 2, 4, 8, 16 and 32 physical elements/cores.
		
		//To accommodate different number of physical elements such that to create heterogeneous machines, separate Pe lists need to be created.
		
		List<List<Pe>> peList = new ArrayList<List<Pe>>();
		
		//List for two cores
		List<Pe> peList2 = new ArrayList<Pe>();
		
		//List for four cores
		List<Pe> peList4 = new ArrayList<Pe>();
		
		//List for eight cores
		List<Pe> peList8 = new ArrayList<Pe>();
		
		//List for thirty two cores		
		List<Pe> peList32 = new ArrayList<Pe>();
		
		//List for eighty cores
		List<Pe> peList80 = new ArrayList<Pe>();
		
		//List for one twenty eight cores
		List<Pe> peList128 = new ArrayList<Pe>();
		
		//List for two fifty six cores
		List<Pe> peList256 = new ArrayList<Pe>();
	
		System.out.println("Number of cores per machine are between 2 to 256.");		
		
		int peId;
		
		//Populate the core lists with PEs
		
		// For two core list, peId = 0		
		peId=2;
		for(int i=0; i<peId; i++)
		{
			peList2.add(new Pe(i, new PeProvisionerSimple(mips)));
		}
		peList.add(peList2);
		PeListforFailures.setPeList(peList2);
		
		// For four core list, peId = 0		
		peId=4;
		for(int i=0; i<peId; i++)
		{
			peList4.add(new Pe(i, new PeProvisionerSimple(mips)));
		}
		peList.add(peList4);
		PeListforFailures.setPeList(peList4);
		
		// For four core list, peId = 0		
		peId=8;
		for(int i=0; i<peId; i++)
		{
			peList8.add(new Pe(i, new PeProvisionerSimple(mips)));
		}
		peList.add(peList8);
		PeListforFailures.setPeList(peList8);
		
		// For thirty two core list, peId = 0		
		peId=32;
		for(int i=0; i<peId; i++)
		{
			peList32.add(new Pe(i, new PeProvisionerSimple(mips)));
		}
		peList.add(peList32);
		PeListforFailures.setPeList(peList32);
		
		// For eighty core list, peId = 0		
		peId=80;
		for(int i=0; i<peId; i++)
		{
			peList80.add(new Pe(i, new PeProvisionerSimple(mips)));
		}
		peList.add(peList80);
		PeListforFailures.setPeList(peList80);
		
		// For one twenty eight core list, peId = 0		
		peId=128;
		for(int i=0; i<peId; i++)
		{
			peList128.add(new Pe(i, new PeProvisionerSimple(mips)));
		}
		peList.add(peList128);
		PeListforFailures.setPeList(peList128);
		
		// For two fifty six core list, peId = 0		
		peId=256;
		for(int i=0; i<peId; i++)
		{
			peList256.add(new Pe(i, new PeProvisionerSimple(mips)));
		}
		peList.add(peList256);	
		PeListforFailures.setPeList(peList256);
		
		//System.out.print("Size of the peList is" +peList.size());
		//int size=peList.size();
		
		//Create the host list with processing cores list (peList)
		int host_count = 1;
		
		//HostProvisioning hostprov = new HostProvisioning();
		
		//host_count = hostprov.getHostCount(cloudletList);
		
		System.out.println("\n");
		
		//System.out.println("Number of nodes need to provisioned is " +host_count);
		
		//int ftaNodeListSize;	
		
		//ftaNodeListSize = ftaread.getnodeIdListSize();
		//int host_order = 0;		
		
		//try {
			//if(host_order == 0){
			//	BufferedReader read = new BufferedReader(new FileReader("/rusers/postgrad/ysharma1/PhDWork/Java/Results/Host_Ordering.txt"));
			//	host_order = Integer.parseInt(read.readLine());
			//}
		host_order = RunTimeConstants.host_order;
			switch(host_order){
				case 1:
					System.out.println(" Energy aware resource provisioning is being done");
					ftaread.setNodeListPower();
					ftaread.setSortedPowerNodeID();
					ftaread.setPowerHostListSorted();
					sortedFtaNodeList = ftaread.getPowerHostListSorted(); // For energy conscious provisioning
					break;
					
				case 2:
					System.out.println(" Reliability aware resource provisioning is being done");
					ftaread.prepareCurrentHazardRate();
					ftaread.setSortedHazardRateNodeID();
					ftaread.setNodeIDListSorted();
					sortedFtaNodeList = ftaread.getSortedNodeIDList();	// For reliability conscious provisioning
					break;
					
				case 3:
					System.out.println(" Reliability and Energy aware resource provisioning is being done");
					ftaread.setNodeListPower();
					ftaread.prepareCurrentHazardRate();
					ftaread.setProductPowerandHazardRate();
					ftaread.setSortedPowerandHazardRateNodeID();
					ftaread.setPowerandHazardRateHostListSorted();
					sortedFtaNodeList = ftaread.getPowerandHazardRateHostListSorted(); // For reliability and energy conscious provisioning
					break;
					
				case 4:
					System.out.println(" Random resource provisioning is being done");
					sortedFtaNodeList = ftaread.getNodeIDList();// For random provisioning
					break;
					
				case 5: 
					System.out.println(" Availability aware resource provisioning is being done");				
					ftaread.createNodeAvailabilityList();
					ftaread.setSortedAvailabilityNodeID();
					ftaread.setAvailabilityHostListSorted();
					sortedFtaNodeList = ftaread.getAvailabilityHostListSorted();
					break;
					
				case 6:				
					System.out.println(" Maintainability aware resource provisioning is being done");	
					ftaread.createNodeMaintainabilityList();
					ftaread.setSortedMaintainabilityNodeID();
					ftaread.setMaintainabilityHostListSorted();
					sortedFtaNodeList = ftaread.getMaintainabilityHostListSorted();
					break;
					
				case 7:				
					System.out.println(" Maintainability and Energy aware resource provisioning is being done");				
					ftaread.createNodeMaintainabilityList();
					ftaread.setNodeListPower();
					ftaread.setProductPowerandMaintainability();
					ftaread.setSortedPowerandMaintainabilityNodeID();
					ftaread.setPowerandMaintainabilityHostListSorted();
					sortedFtaNodeList = ftaread.getPowerandMaintainabilityHostListSorted();
					break;	
				
				case 8:				
					System.out.println(" Availability and Energy aware resource provisioning is being done");	
					ftaread.createNodeAvailabilityList();
					ftaread.setNodeListPower();
					ftaread.setFractionPowerandAvailability();	
					ftaread.setSortedPowerandAvailabilityNodeID();
					ftaread.setPowerandAvailabilityHostListSorted();
					sortedFtaNodeList = ftaread.getPowerandAvailabilityHostListSorted();
					break;
					
					
					
			}
		//} catch (NumberFormatException | IOException e) {
		//	System.out.println("Error occurred while reading the host order in simulation script");
		//}
		
		//sortedFtaNodeList = ftaread.getPowerandHazardRateHostListSorted();
		
		// int min, max;
		int peListIndex = 0;
		
		//min =1;
		//max = 5;
		
		int hostId = 0;
		int ram;
		long storage = 1000000;
		int bw = 10000;
		
		double utilization;
		int peCount = 0;
		int peCount1 = 0;
		//int peCount_temp;
		
		//Random rand = new Random();
		
		//System.out.println("Which way you wants to get the information regarding Hosts");
		//System.out.println(" To provision new Hosts			  ----------------- 1 ");
		//System.out.println(" To read from a file			  ----------------- 2 ");
		System.out.println("Nodes are being provisioned");
		
		//int choice2;		
		//Scanner ch3;
		//ch3 = new Scanner(System.in);		
		//choice2 = ch3.nextInt();
		//ch3.close();
		int fta_index = -1;
		int ftaNodeID;
		//boolean flag;
		boolean check_host;
		boolean check_vmlist;
		boolean check_increase = false;
		boolean checkProvisionedHost = false;
		//ArrayList<Integer>ftarandList = new ArrayList<Integer>();
		//double minHazardRate = 0;
		//double hazardRateTemp;
		//double hazardRatePrev;
		//double hazardRateMin;
		//boolean check_HRmin;
		//int index;
		//switch(choice2){
		//case 1:
		//FileWriter writer;	
			//FileWriter writer1;
			//writer = new FileWriter("/rusers/postgrad/ysharma1/PhDWork/Java/Results/Reliability_and_Energy_Aware_Best_Fit_Decreasing_Checkpointing_Single_core_per_VM_with_Increasing_Cloudlets_new_Reliability_Model/HostCount.txt", false);			
			//writer1 = new FileWriter("/rusers/postgrad/ysharma1/PhDWork/Java/Results/Reliability_and_Energy_Aware_Best_Fit_Decreasing_Checkpointing_Single_core_per_VM_with_Increasing_Cloudlets_new_Reliability_Model/FTANodeID.txt", false);
			
			//hazardRateMin = ftaread.getHazardRateMax();
			//ftaread.setHazaradRateMinimum(hazardRateMin);		
			
		if(RunTimeConstants.host_order == 4){	
			for(int i=0;i<host_count;)
			{				
				if(HostMapping.getHostMapSize() == 0){
					//fta_index = (int)(Math.random()*(sortedFtaNodeList.size()+1));	
					fta_index = (int)(Math.random()*(sortedFtaNodeList.size()));	
				}
				else{						
					//fta_index = (int)(Math.random()*(sortedFtaNodeList.size()+1));	
					fta_index = (int)(Math.random()*(sortedFtaNodeList.size()));	
					for(int j=0;j<HostMapping.getHostMapSize();j++){
						if(sortedFtaNodeList.get(fta_index)==HostMapping.getFTAhostID(j)){
							checkProvisionedHost = true;
							break;
						}						
					}
					
				}
				//fta_index = fta_index + 1;				
				//if(host_order == 4){
					//Collections.shuffle(sortedFtaNodeList);
				//}
				
				if(checkProvisionedHost == false){
					ftaNodeID = sortedFtaNodeList.get(fta_index);				
					utilization = ftaread.getCurrentUtilization(ftaNodeID); 				
					//System.out.println("FTA ID of provisioned node is " +ftaNodeID);				
					HostMapping.createHostMap(hostId, ftaNodeID);				
					ram = ftaread.getMemorySize(ftaNodeID);				
					peCount = ftaread.getProcessorCount(ftaNodeID);								
					for(int k=0;k<peList.size();k++){
						if(peList.get(k).size()==peCount){
							peListIndex = k;
							break;
						}
					}			
				//System.out.println("\n Number of physical elements on the provisioned node " +i+ " are" +peCount);			
				check_host = false;
				peCount1 = 0;
				peCount1 = peCount1 + peList.get(peListIndex).size();				
				//peCount_temp = peCount1;
				for(int t=0;t<vmListForHostProvisioning.size();){					
					if(peCount1>=vmListForHostProvisioning.get(t).getNumberOfPes()){						
						peCount1=peCount1 - vmListForHostProvisioning.get(t).getNumberOfPes();						
						vmListForHostProvisioning.remove(t);						
						t=0;						
						check_host = true;
						//continue;
					}
					if(peCount1==0){
						break;
					}
					//else{
						//t++; //Activate this part when VM with PE more than 1 are involved. 
						//}
				}				
				
				//hostId[i]=i;
				//rand=min + (int)(Math.random() * ((max - min) + 1));
				//writer.write(rand+ System.getProperty( "line.separator" ));
				
				if(check_host==true){
					//writer1.write(ftaNodeID+ System.getProperty("line.separator"));							
					hostList.add(
						new Host(
								hostId,							
								new RamProvisionerSimple(ram), 
								new BwProvisionerSimple(bw), 
								storage, 
							//	utilization,
								peList.get(peListIndex),							
								//new VmSchedulerTimeShared(peList.get(peListIndex))
								new VmSchedulerSpaceShared(peList.get(peListIndex))
								));				
					HostUtilization.sethostUtilization(hostId, utilization);
					check_vmlist = vmListForHostProvisioning.isEmpty();
					if(check_vmlist==false){
						host_count = host_count + 1;
						hostId++;
						i++;
						check_increase = true;
						}
					else{
						if(RunTimeConstants.test == true){
							System.out.println("Enough nodes have been provisioned to accomodate all the VMs");
						}
						System.out.println("\n Number of provisioned nodes are " +host_count);
						//HostCounter.setHostCount(host_count);
						//HostCounter.setClockList(CloudSim.clock());
						//writer.write(host_count+ System.getProperty("line.separator"));
						break;
					}				
				}
				else{
					//System.out.println("Selected FTA node has not provisioned");				
					if(check_increase == true){					
						//host_count = host_count - 1;
						i = i-1;
						//hostId--;
						check_increase = false;
					}
				}
					//new FailureGenerator(hostId, outputfilewriter);
				//new FailureGenerator(hostId);
				
				
				
				/*
				if(total_core_count>peCount1){
					host_count = host_count + 1;
					hostId++;
				}			
				*/
			}
				else{
					System.out.println("FTA node has already been provisioned");					
					continue;
				}
			}
	}
		else{
			for(int i=0;i<host_count;)	
			{				
				fta_index = fta_index + 1;				
				//if(host_order == 4){
					//Collections.shuffle(sortedFtaNodeList);
				//}			
				
				ftaNodeID = sortedFtaNodeList.get(fta_index);				
				utilization = ftaread.getCurrentUtilization(ftaNodeID); 				
				//System.out.println("FTA ID of provisioned node is " +ftaNodeID);				
				HostMapping.createHostMap(hostId, ftaNodeID);				
				ram = ftaread.getMemorySize(ftaNodeID);				
				peCount = ftaread.getProcessorCount(ftaNodeID);								
				for(int k=0;k<peList.size();k++){
					if(peList.get(k).size()==peCount){
						peListIndex = k;
						break;
					}
				}			
				//System.out.println("\n Number of physical elements on the provisioned node " +i+ " are" +peCount);			
				check_host = false;
				peCount1 = 0;
				peCount1 = peCount1 + peList.get(peListIndex).size();				
				//peCount_temp = peCount1;
				for(int t=0;t<vmListForHostProvisioning.size();){					
					if(peCount1>=vmListForHostProvisioning.get(t).getNumberOfPes()){						
						peCount1=peCount1 - vmListForHostProvisioning.get(t).getNumberOfPes();						
						vmListForHostProvisioning.remove(t);						
						t=0;						
						check_host = true;
						//continue;
					}
					else{
						t++;
						}
				}				
				
				//hostId[i]=i;
				//rand=min + (int)(Math.random() * ((max - min) + 1));
				//writer.write(rand+ System.getProperty( "line.separator" ));
				
				if(check_host==true){
					//writer1.write(ftaNodeID+ System.getProperty("line.separator"));				
					hostList.add(
						new Host(
								hostId, 								
								new RamProvisionerSimple(ram), 
								new BwProvisionerSimple(bw), 
								storage, 
						//		utilization,
								peList.get(peListIndex),							
								new VmSchedulerSpaceShared(peList.get(peListIndex))
								));				
					HostUtilization.sethostUtilization(hostId, utilization);
					check_vmlist = vmListForHostProvisioning.isEmpty();
					if(check_vmlist==false){
						host_count = host_count + 1;
						hostId++;
						i++;
						check_increase = true;
						}
					else{
						if(RunTimeConstants.test == true){
							System.out.println("Enough nodes have been provisioned to accomodate all the VMs");
						}
						System.out.println("\n Number of provisioned nodes are " +host_count);
						//writer.write(host_count+ System.getProperty("line.separator"));
						break;
					}				
				}
				else{
					//System.out.println("Selected FTA node has not provisioned");				
					if(check_increase == true){					
						//host_count = host_count - 1;
						i = i-1;
						//hostId--;
						check_increase = false;
					}
				}
					//new FailureGenerator(hostId, outputfilewriter);
				//new FailureGenerator(hostId);
				
				
				
				/*
				if(total_core_count>peCount1){
					host_count = host_count + 1;
					hostId++;
				}			
				*/
			
				
			}
			
		}
			//writer.close();
			//writer1.close();
			//System.out.println(" \n Number of nodes need to be provisioned is " +host_count);
			
		//break;
		/*
		case 2:	
						
			BufferedReader read = new BufferedReader(new FileReader("/rusers/postgrad/ysharma1/PhDWork/Java/Results/Reliability_and_Energy_Aware_Best_Fit_Decreasing_Checkpointing_Single_core_per_VM_with_Increasing_Cloudlets_new_Reliability_Model/HostCount.txt"));
			
			BufferedReader read1 = new BufferedReader(new FileReader("/rusers/postgrad/ysharma1/PhDWork/Java/Results/Reliability_and_Energy_Aware_Best_Fit_Decreasing_Checkpointing_Single_core_per_VM_with_Increasing_Cloudlets_new_Reliability_Model/FTANodeID.txt"));
			
			host_count = Integer.parseInt(read.readLine());				
			
			for(int i=0;i<host_count;i++){	
				//while(read1.readLine()!=null){				
					
				ftaNodeID = Integer.parseInt(read1.readLine());
				
				System.out.println("FTA ID of provisioned node is " +ftaNodeID);
				
				HostMapping.createHostMap(hostId, ftaNodeID);
				
				ram = ftaread.getMemorySize(ftaNodeID);
				
				peCount = ftaread.getProcessorCount(ftaNodeID);			
								
				System.out.println("\n Number of physical elements on the provisioned node " +i+ " are " +peCount);
				
				for(int k=0;k<peList.size();k++){
					if(peList.get(k).size()==peCount){
						peListIndex = k;
						break;
					}
				}
				
				hostList.add(
						new Host(
								hostId, 
								.1,
								new RamProvisionerSimple(ram), 
								new BwProvisionerSimple(bw), 
								storage, 
								peList.get(peListIndex),							
								new VmSchedulerTimeShared(peList.get(peListIndex))
								));			
				hostId++;
				//peCount1 = peCount1 + peList.get(peListIndex).size();						
				
				//if(total_core_count>peCount1){
					//host_count = host_count + 1;
					//hostId++;
				//}			
				//total_core_count = total_core_count + peList.size();
				//if(total_core_count<vmList.size()){
					//host_count = host_count + 1;
					//hostId++;
				//}
				//i=i+1;
			}			
			
		break;
		*/
		//}
		
	/**
		for(int i=0;i<host_count;i++)
		{
			//hostId[i]=i;
			rand=min + (int)(Math.random() * ((max - min) + 1));
			System.out.println("\n Number of physical elements on the node " +i+ " are" +peList.get(rand).size());
			hostList.add(
					new Host(
							hostId, 
							new RamProvisionerSimple(ram), 
							new BwProvisionerSimple(bw), 
							storage, 
							peList.get(rand),							
							new VmSchedulerTimeShared(peList.get(rand))
							));				
				//new FailureGenerator(hostId, outputfilewriter);
			//new FailureGenerator(hostId);
			total_core_count = total_core_count + peList.size();
			if(total_core_count<vmList.size()){
				host_count = host_count + 1;
				hostId++;
			}			
			
		}
		
		System.out.println(" \n Number of nodes need to provisioned is " +host_count);
	*/	
		//new GoogleFailureGenerator();
		//bwriter.close();
		//outputfilewriter.flush();
		//outputfilewriter.close();
				//Create the datacenter characteristics
		String arch = "x86";
		String os = "Linux";
		String vmm = "Xen";
		double time_zone = 10.0;
		double cost = 3.0;
		double costPerMem = 0.05;
		double costPerStorage = 0.1;
		double costPerBw = 0.1;
		
		LinkedList<Storage> storageList = new LinkedList<Storage>();
		
		//DatacenterCharacteristics characteristics = new DatacenterCharacteristics(arch, os, vmm, time_zone, cost, costPerMem, costPerStorage, costPerBw);
		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
	            arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);
		//Create the object of datacenter
		//Datacenter datacenter = null;
		FailureDatacenter datacenter = null;
		//try
		//{
		//datacenter = new Datacenter(name, characteristics, new VmAllocationPolicyFailureAware(hostList, ftaread), storageList, 0);
		datacenter = new FailureDatacenter(name, characteristics, new VmAllocationPolicyFailureAware(hostList, ftaread, vmUtl, hvmRbl), storageList, 0);
		
		//datacenter = new Datacenter(name, characteristics, new VmAllocationPolicyFailureAware(hostList), storageList, 0);
		
		//}catch(Exception e)
		//{
			//e.printStackTrace();
		//}
		hostListBackup = hostList;
		
		for(int i=0;i <hostList.size(); i++){
			Host host;
			List<Pe>peListForHost = new ArrayList<Pe>();
			host = hostList.get(i);
			peListForHost = host.getPeList();
			for(int j=0; j<peListForHost.size(); j++){
				IdleTime.setHostPeIdleTable(host.getId(), peListForHost.get(j).getId(), true);
				IdleTime.setHostPeIdleClockTable(host.getId(), peListForHost.get(j).getId(), CloudSim.clock());				
			}
		//	IdleTime.setHostActiveStatus(host.getId(), true);
		}
		
		return datacenter;
	}		
	
	
	//Create the datacenter broker
	//private static DatacenterBroker createBroker()
	private static FailureDatacenterBroker createBroker()
	{
		//DatacenterBroker broker = null;
		FailureDatacenterBroker fbroker = null;
		try {
			//broker = new DatacenterBroker("Broker");
			fbroker = new FailureDatacenterBroker("Broker");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		//return broker;
		return fbroker;
	}	
	
	
	public static HashMap<Integer, Double>vmPower = new HashMap<Integer, Double>();
	public static HashMap<Integer, Double>vmPowerIdle = new HashMap<Integer, Double>();
	public static ArrayList<Cloudlet>sufferedClets = new ArrayList<Cloudlet>();
	
	//@SuppressWarnings("unused")
	public static void calculatePower(){	
		//PowerModel pow = new PowerModel();;	
		ArrayList<Vm> vmListPerHostForPower;	
		ArrayList<Double> powerPerVmList;
		ArrayList<Double> powerPerVmListIdle;
		double vm_utilization;		
		int[] ftaHostID = new int[hostListBackup.size()];
		for(int i=0;i<hostListBackup.size();i++){
			ftaHostID[i] = HostMapping.getFTAhostID(hostListBackup.get(i).getId());			
		}		
		int core_count;		
		for(int i=0;i<hostListBackup.size();i++){
			core_count = ftaread.getProcessorCount(ftaHostID[i]);			
			vmListPerHostForPower = new ArrayList<Vm>();
			vmListPerHostForPower = HostVMMapping.gethostVmMap(hostListBackup.get(i));
			powerPerVmList = new ArrayList<Double>();
			powerPerVmListIdle = new ArrayList<Double>();
			for(int j=0;j<vmListPerHostForPower.size();j++){			
				vm_utilization= vmUtl.getVmAverageUtilization(vmListPerHostForPower.get(j).getId());
				powerPerVmList.add(pow.getPower(vm_utilization, core_count));
				powerPerVmListIdle.add(pow.getPower(0, core_count));
				//vmPower.put(vmListPerHostForPower.get(j).getId(), powerPerVmList.get(j));
				//vmPowerIdle.put(vmListPerHostForPower.get(j).getId(), powerPerVmList.get(j));
				}	
			HostVMMapping.sethostVmMapPowerTable(hostListBackup.get(i), powerPerVmList);
			}		
		}	
	
@SuppressWarnings("resource")
public static void main(String[] args)	
{
	Log.printLine("This is the program simulates the cloud computing environment with failures");	
	
	try
	{
	simulationStartTime = System.currentTimeMillis();	
	// Initialize the cloudsim package	
	int num_user = 1;   // number of users
	Calendar calendar = Calendar.getInstance();
	boolean trace_flag = false;  // mean trace events
	
	RunTimeConstants.setRunTimeConstants();
	//Initialize the cloudsim library		
	CloudSim.init(num_user, calendar, trace_flag);	
	
	//Create broker	
	//DatacenterBroker broker = createBroker();
	FailureDatacenterBroker broker = createBroker();
	
	broker.setCloudletNormalization(cletNorm);
	
	broker.setWorkLoadModel(wlModel);
	
	broker.setReliabilityCalculator(rblcal);
	
	broker.setVmUtilizationObject(vmUtl);
	
	rblcal.setBroker(broker);
	
	int brokerId = broker.getId();
	
	broker.numberofBags = RunTimeConstants.numberofBags;	
	
	stringencyFactor = RunTimeConstants.stringencyFactor;
	
	System.out.println("Data Rate is " +RunTimeConstants.dataRate);
	//ReliabilityCalculator rblcal;
	
	
	
	//Create the list of Cloudlets or tasks
	//For the time being, we are assuming that there is only one user operating on the cloud. 
	
	//System.out.println("Enter the number of cloudlets for which the resources from the datacenter need to be provisioned");
	int clets;
	int botID = 0; // First bag of Tasks ID
	//Scanner abc;
	//abc = new Scanner(System.in);
	//clets=abc.nextInt();
	clets = wlModel.getBagSize(botID);
	cloudletList = createCloudlet(brokerId,clets);
	for(int i=0;i<cloudletList.size();i++){
		BagTaskMapping.setBagTaskMap(0, cloudletList.get(i).getCloudletId());
	}
	
	//cloudletListbackup = cloudletList; 
	//for(int i=0;i<cloudletList.size();i++){
		//cloudletListbackup.add(cloudletList.get(i)); //Storing a backup list to save the exact cloudlet length because during checkpoint mechanism the length of cloudlet changes.
	//}
	
	for(int i=0;i<cloudletList.size();i++){
		cloudletTableBackup.put(cloudletList.get(i).getCloudletId(), cloudletList.get(i).getCloudletLength());
	}
	
	//ftaread = new FTAFileReader();
	ftaread = new FTAFileReaderGrid5000();
	
	//ftagen.setFTAFileReaderObject(ftaread);
	
	//Create the list of virtual machines
	//There is one virtual machine for each cloudlet. 
	int vlist;
	
	bpack.setCloudletNormalization(cletNorm);
	bpack.calVMCount(cloudletList);
	vlist = bpack.getVmCount();
	broker.setBinPacking(bpack);
	//vlist=clets;
	//System.out.println("The number of virtual machines will be equal to the number of tasks need to be scheduled");
	vmList = createVM(brokerId, vlist);
	vmListForHostProvisioning = vmList;
	
	// Submit the list of tasks and virtual machines to broker
	broker.submitVmList(vmList);
	// This flag has been used by broker to restrict the change of start time of a cloudlet again and again.  
	broker.flag=true;
		
	broker.submitCloudletList(cloudletList);
	
	broker.setHostVMMappingReliability(hvmRbl);
	
	broker.setVmMigrationRecord(vmRecord);

	int cletId;
	int vmId;
	//double mipsShare;
	//double newmipsShare;
	ArrayList<Integer>cletIDList;
	for(int i=0;i<vmList.size();i++){
		cletIDList = new ArrayList<Integer>();
		//mipsShare = new ArrayList<Double>();
		vmId = vmList.get(i).getId();
		if(VmCletBindingTable.containsKey(vmId)){
			cletIDList = VmCletBindingTable.get(vmId);
			for(int k=0;k<cletIDList.size();k++){
				cletId = cletIDList.get(k);		
				broker.bindCloudletToVm(cletId, vmId);
			}
		//	cletId = VmCletBindingTable.get(vmList.get(i)).getCloudletId();
		//	vmId = vmList.get(i).getId();
			
			//mipsShare = vmList.get(i).getMips();		
			
			//newmipsShare = mipsShare * VmCletBindingTable.get(vmList.get(i)).getUtilizationOfCpu(CloudSim.clock());
			
			//mipsShare.clear();
			
			//mipsShare.add(newmipsShare);
			
			//vmList.get(i).getCloudletScheduler().setCurrentMipsShare(mipsShare);
			
			//vmList.get(i).setMips(newmipsShare);
			
		}		
	}
		
	
	//Create datacenters	
		@SuppressWarnings("unused")
		//Datacenter datacenter0 = createDatacenter("Datacenter_0");
		FailureDatacenter datacenter0 = createDatacenter("Datacenter_0");
		
		
		/*
		Host hostReliability;
		double tempReliability;
		for(int i=0;i<datacenter0.hostList().size();i++){
			hostReliability = datacenter0.hostList().get(i);
			tempReliability = 1;
			for(int j=0;j<hostReliability.getVmList().size();j++){
				tempReliability = tempReliability * HostVMMappingReliability.getVmReliability(hostReliability.getVmList().get(j));
			}
			HostVMMappingReliability.sethostVmReliabilityTable(hostReliability, tempReliability);
		}
		*/
		
		datacenter0.setBroker(broker);
		datacenter0.setFailurePredictor(failPredict);
		broker.setDC(datacenter0);
		hvmRbl.setDatacenter(datacenter0);
		hvmRbl.setFTARead(ftaread);
		datacenter0.setCletNorm(cletNorm);
		datacenter0.setFTARead(ftaread);
		datacenter0.setReliabilityCalculator(rblcal);
		datacenter0.setVmUtilizationObject(vmUtl);
		datacenter0.setHostVMMappingReliability(hvmRbl);
		datacenter0.setVmMigrationOverheadObject(mgOver);
		datacenter0.setVmMigrationRecordObject(vmRecord);	
		
		mgOver.setVmUtilizationObject(vmUtl);
		
		failPredict.setFTAReader(ftaread);		
		//failPredict.setFailureDatacenter(datacenter0);
		faultToleranceMechanism = RunTimeConstants.faultToleranceMechanism;
		
		
	//PowerDatacenter pow = (PowerDatacenter) createDatacenter("Datacenter_1");
	
		
	// Submit the list of tasks and virtual machines to broker
	//broker.submitVmList(vmList);
	
	// This flag has been used by broker to restrict the change of start time of a cloudlet again and again.  
	//broker.flag=true;
	
	//broker.submitCloudletList(cloudletList);
	
	//Start the simulation
	CloudSim.startSimulation();
	
	cloudletTableBackup.putAll(broker.cloudletTableBackup);
	
	sufferedClets.addAll(broker.sufferedCloudlets);
	ArrayList<Integer>sufferedCletsID = new ArrayList<Integer>();
	for(int i=0;i<sufferedClets.size();i++){
		sufferedCletsID.add(sufferedClets.get(i).getCloudletId());
	}
	
	totalVmMigrations = datacenter0.totalVmMigrations;
	//calculatePower(broker.sufferedCloudlets);
	calculatePower();
	
	totalFailureCount = datacenter0.totalFailureCount;
	
	totalCheckpointsCount = datacenter0.totalCheckpointEvents;
	
	totalVmConsolidations = datacenter0.consolidationCount;
	
	System.out.println("Returning from start simulation");
	
	//Print the results
	List<Cloudlet> newList = broker.getCloudletReceivedList();
	
	List<Vm> newVmList = broker.getVmBackupList();
	
	//List<Cloudlet>sufferedCletList = broker.getSufferedCloudletsList();
	
	List<Host> hostList = HostVMMapping.getHostKeySet();
	
	//List<Integer> vmfailList = datacenter0.getvmFailedlist();
	//List<Integer> clfailList = datacenter0.getcloudletFailedlist();
	//List<Integer> hostfailList = datacenter0.getfailHostList();
	
	//List<Cloudlet> cloudletfailList = datacenter0.getrecoveringCloudletList();
	
	//List<Double> averageFinishingTime = datacenter0.getFailedCletAverageExectTime();
	//List<Integer> MTBFhostfailList;
	
	
			
	CloudSim.stopSimulation();	
	
	//BufferedReader read_fault = new BufferedReader(new FileReader("/rusers/postgrad/ysharma1/PhDWork/Java/Results/Fault_Tolerance_Mechanism.txt"));
	//int faultToleranceMechanism = Integer.parseInt(read_fault.readLine());	
	
	System.out.println("Selecting the fault tolerance mechanism being used");
	switch(faultToleranceMechanism){	
	
	case 1:
		if(RunTimeConstants.vmConsolidationFlag == false){
			System.out.println("Printing and storing the results for checkpointing");		
			printCloudletListCheckpointing(newList, hostList, ftaread, broker, hvmRbl, newVmList, vmUtl, pow);
		}
		else{
			printCloudletListCheckpointingwithConsolidation(newList, hostList, ftaread, broker, hvmRbl, newVmList, vmUtl, pow);
		}		
		break;
		
	case 2:		
		System.out.println("Printing and storing the results for restarting");
		printCloudletListRestarting(newList, hostList, ftaread, broker, hvmRbl, newVmList, vmUtl, pow);		
		break;
		
	case 3:		
		if(RunTimeConstants.vmConsolidationFlag == false ){
			System.out.println("Printing and storing the results for restarting with migration");
			printCloudletListRestartingwithMigration(newList, hostList, ftaread, broker, hvmRbl, newVmList, vmUtl, pow);
		}
		else{
			if(RunTimeConstants.vmConsolidationFlag==true && RunTimeConstants.predictionFlag == true){
				System.out.println("Printing and storing the results for restarting with migration and consolidation");
				printCloudletListRestartingwithMigration(newList, hostList, ftaread, broker, hvmRbl, newVmList, vmUtl, pow);
			}
			else{
				System.out.println("Printing and storing the results for restarting with consolidation");
				printCloudletListRestartingwithConsolidation(newList, hostList, ftaread, broker, hvmRbl, newVmList, vmUtl, pow);
			}			
		}
		break;	
		
	case 4:
		System.out.println("Printing and storing the results for checkpointing with migration");
		printCloudletListCheckpointingwithMigration(newList, hostList, ftaread, broker, hvmRbl, newVmList, vmUtl, pow);
		break;
		
	}

	
	//printCloudletfailList(vmfailList, clfailList);

	Log.printLine("The execution of the program has been finished");
	simulationFinishedTime = System.currentTimeMillis() - simulationStartTime;
	simulationFinishedTime = (simulationFinishedTime / 60000);
	System.out.println("Simulation has taken " +simulationFinishedTime+ " minutes to finish");
}
catch(Exception e)
	{
		e.printStackTrace();
		Log.printLine("The simulation has been terminated due to an unexpected error");	
	}
}	

//Print the results
//private static void printCloudletListCheckpointing(List<Cloudlet> list, List<Host> hostList, FTAFileReader ftaread, List<Cloudlet>sufferedClets) {
//private static void printCloudletListCheckpointing(List<Cloudlet> list, List<Host> hostList, FTAFileReader ftaread, FailureDatacenterBroker broker) {	
private static void printCloudletListCheckpointing(List<Cloudlet> list, List<Host> hostList, FTAFileReaderGrid5000 ftaread, FailureDatacenterBroker broker, HostVMMappingReliability hvmRbl, List<Vm>newVmList, VmUtilization vmUtl, PowerModel pow) {	
	
	Double initTime;		
	Double totalEnergyPerClet;	
	Double energyPerCletWithoutOverheads;
	Double energyPerCletForChkptOverheads;
	//Double energyWastagePerCletWithOverheads;
	Double energyPerCletForReexec;
	Double changeinExecutionTime;
	Double totalDownTime;
	Double totalFinishedTime_withoutDownTime;
	Double totalFinishedTime_withoutChkptOverhead;	
	Double energyWastagePerClet;
	Double totalEnergyWastage = 0.0;
	int countDeadlineMissed = 0;
	int countDeadlineAchieved = 0;	
	Double totalReexecutionTime = 0.0;
	double applicationFinishingTime = 0.0;
	double totalDeadline = 0;	
	HashMap<Integer, Double> cletEnergyTable= new HashMap<Integer, Double>();
	HashMap<Integer, Double> cletEnergyWastageTable= new HashMap<Integer, Double>();
	HashMap<Integer, Double> cletEnergyWastageChkptTable= new HashMap<Integer, Double>();
	HashMap<Integer, Double> cletEnergyWastageReExecTable= new HashMap<Integer, Double>();
	
	Vm vm;		
	int size = sufferedClets.size(); //For only suffered cloudlets and corresponding VMs. 
	Cloudlet cloudlet;
	
	System.out.println("Printing Results for Checkpointing Fault Tolerance Mechanism");
	String indent = "    ";
	Log.printLine();
	Log.printLine("========== OUTPUT ==========");
	//Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
	//		"Data center ID" + indent + "VM ID" + indent +"Cloudlet Initial Length" + indent + "Intial Start Time" + indent + "Finish Time" +indent+ "Utility Value" +indent+ "Deadline");
	Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
			"Data center ID" + indent + indent + "VM ID" + indent + indent + "Bag ID"+ indent +  indent + "Cloudlet Initial Length" + indent + "Intial Start Time" + indent + indent +"Finish Time" + indent + indent +"Turnaround Time" +indent+ indent+ "Deadline Value" +indent+ indent+ "Deadline Actual Value"+indent+ indent+ "Utility Value"+indent+ indent +"Deadline");
	
	DecimalFormat dft = new DecimalFormat("###.####");		
		
	for (int i = 0; i < size; i++) {	
		cloudlet = sufferedClets.get(i); //This is for evaluating the only cloudlets that have been suffered or submitted after the occurrence of first failure.		
		double cloudletInitialLength = 0.0;	
		vm = VmCloudletMapping.getVm(cloudlet);				
		Log.print(indent + cloudlet.getCloudletId() + indent + indent);
		if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS){
			Log.print("SUCCESS");		
			initTime = broker.getcloudletInitialStartTime(cloudlet);			
			if(cloudletTableBackup.containsKey(cloudlet.getCloudletId())){					
				cloudletInitialLength = ((double)cloudletTableBackup.get(cloudlet.getCloudletId()))/1000;				
			}			
			double deadline;			
			double utilityValue;			
			double turnAroundTime;
			int bagID;
			//double gap;
			double finishedTime;				
			bagID = BagTaskMapping.getBagID(cloudlet.getCloudletId());
			finishedTime = cloudlet.getFinishTime();
			deadline = initTime + (stringencyFactor * (cloudletInitialLength));		
			//gap = cloudlet.getFinishTime()-deadline;		
			if(finishedTime<=deadline){
				utilityValue = 1;
			}
			else{
				utilityValue = (1-((finishedTime-deadline)/deadline));				
				if(utilityValue<0){
					utilityValue = 0;
				}
			}			
			jobUtilityValue = jobUtilityValue + utilityValue;
			totalDeadline = totalDeadline + (deadline-initTime);
			turnAroundTime = finishedTime - initTime;
			
			if(deadline >= cloudlet.getFinishTime()){			
				countDeadlineAchieved = countDeadlineAchieved + 1;
				Log.printLine( indent + indent + cloudlet.getResourceId() + indent + indent + indent + indent + cloudlet.getVmId() +
					indent + indent + indent + indent + bagID+
					indent + indent + indent + indent + dft.format(cloudletInitialLength) +
					indent + indent + indent + indent + indent + dft.format(initTime) +
					indent + indent + indent +  dft.format(finishedTime)+
					indent + indent + indent + dft.format(turnAroundTime)+
					indent + indent + indent +  dft.format(deadline)+
					indent + indent + indent +  dft.format(deadline-initTime)+
					indent + indent + indent +  dft.format(utilityValue)+
					indent + indent + indent+ "Achieved" );
			}
			else{				
				countDeadlineMissed = countDeadlineMissed + 1;
				Log.printLine( indent + indent + cloudlet.getResourceId() + indent + indent + indent + indent + cloudlet.getVmId() +
						indent + indent + indent + indent + bagID+
						indent + indent + indent + indent + dft.format(cloudletInitialLength) +
						indent + indent + indent + indent + indent + dft.format(initTime) +
						indent + indent + indent +  dft.format(finishedTime)+
						indent + indent + indent + dft.format(turnAroundTime)+
						indent + indent + indent +  dft.format(deadline)+
						indent + indent + indent +  dft.format(deadline-initTime)+
						indent + indent + indent +  dft.format(utilityValue)+
						indent + indent + indent+ "Missed" );
				}
		}
	}	
	Log.printLine("========== OUTPUT ==========");
	//Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
	//		"Data center ID" + indent + "VM ID" + indent +  "Cloudlet Initial Length" + indent + "Intial Start Time" + indent + "Finish Time" +indent+ "Utility Value" +indent+ "Deadline");
	Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
			"Data center ID" + indent + indent + "VM ID" + indent + indent + "Bag ID"+ indent +  indent + "Cloudlet Initial Length" + indent + "Intial Start Time" + indent + indent +"Finish Time" + indent + indent +"Turnaround Time" +indent+ indent+ "Deadline Value" +indent+ indent+ "Deadline Actual Value"+indent+ indent+ "Utility Value"+indent+ indent +"Deadline");
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== OUTPUT For Bag Level Information ==========");
	//Log.printLine("Job ID" + indent + indent + indent + "Finishing Time");
	Log.printLine( "Finishing Time");
	int bagCount;
	int cletBagID;	
	bagCount = RunTimeConstants.numberofBags;
	for(int i=0; i<bagCount; i++){
		double jobFinishingTime = 0;
		double turnAroundTime = 0;
		for(int j=0; j<size; j++){
			cloudlet = sufferedClets.get(j);
			initTime = broker.getcloudletInitialStartTime(cloudlet);	
			turnAroundTime = cloudlet.getFinishTime() - initTime;
			cletBagID = BagTaskMapping.getBagID(cloudlet.getCloudletId());
			if(i == cletBagID){
				if(jobFinishingTime < turnAroundTime){
					jobFinishingTime = turnAroundTime;
				}
			}
		}
		if(applicationFinishingTime<jobFinishingTime){
			applicationFinishingTime = jobFinishingTime;
		}
		//Log.printLine( indent + i + indent + indent + indent + indent + dft.format(jobFinishingTime));
		Log.printLine( indent + dft.format(jobFinishingTime));
	}
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== OUTPUT For Bag Level Information ==========");
	///Log.printLine("Job ID" + indent + indent + indent + "Finishing Time");
	Log.printLine( "Finishing Time");	
	
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== OUTPUT For Host Information ==========");
	Log.printLine("Host ID" + indent + "FTA Host ID" + indent +" MTBF" + indent + "MTTR" +indent+ "Max Hazard Rate" +indent+ "Current Hazard Rate" +indent+ "Host Power" +indent+ "Host Availability" +indent+ "Host Maintainability");
	
	int ftaHostID;	
	for(int i=0;i<HostMapping.getHostMapSize();i++){
		ftaHostID = HostMapping.getFTAhostID(i);		
		Log.printLine(  i + indent + indent + ftaHostID +
				indent + indent + indent + indent + dft.format(ftaread.getMTBF(ftaHostID)) +
				indent + indent + indent + indent + dft.format(ftaread.getMTTR(ftaHostID)) +
				indent + indent + indent + indent + ftaread.getMaxHazardRate(ftaHostID)+
				indent + indent + indent + indent + ftaread.getCurrentHazardRate(ftaHostID)+
				indent + indent + indent + indent + dft.format(ftaread.getPowerforHost(ftaHostID))+
				indent + indent + indent + indent + dft.format(ftaread.getAvailability(ftaHostID))+
				indent + indent + indent + indent + dft.format(ftaread.getMaintainability(ftaHostID))
				);
		
	}	
	
	Log.printLine("========== OUTPUT For Host Information ==========");
	Log.printLine("Host ID" + indent + "FTA Host ID" + indent +" MTBF" + indent + "MTTR" +indent+ "Max Hazard Rate" +indent+ "Current Hazard Rate" +indent+ "Host Power" +indent+ "Host Availability" +indent+ "Host Maintainability");

	Log.printLine();
	Log.printLine();
	Log.printLine("========== OUTPUT For Host Idle Time  ==========");
	Log.printLine("Host ID" +indent+ indent + indent + "Total Idle Time" +indent+ indent + indent + "Total Down Time" +indent+ indent + indent + "Total Idle Energy Consumption" );		
	
	int hostIdIdle;
	double idleTime;
	double averageIdleTime;
	int peListSize;
	double idleUtilization = 0.0;
	double idlePower = 0.0;		
	double totalIdleEnergyConsumption = 0.0;
	double idleEnergyConsumption = 0.0;
	double averageIdleEnergyConsumption = 0.0;
	double totalAverageIdleEnergyConsumption = 0.0;
	double downTime;
	//double idleTimewithDownTime;
	List<Pe>peList;
	for(int i=0; i<hostList.size(); i++){
		idleTime = 0.0;
		downTime = 0.0;
		//idleTimewithDownTime = 0.0;
		peList = new ArrayList<Pe>();
		ftaHostID = HostMapping.getFTAhostID(hostList.get(i).getId());
		idleUtilization = ftaread.getCurrentUtilization(ftaHostID);
		hostIdIdle = hostList.get(i).getId();			
		peListSize = hostList.get(i).getNumberOfPes();
		peList = hostList.get(i).getPeList();
		for(int j=0; j<peList.size(); j++){
			//idleTimewithDownTime = idleTimewithDownTime + IdleTime.getPeIdleTime(hostList.get(i).getId(), peList.get(j).getId());
			idleTime = idleTime + IdleTime.getPeIdleTime(hostList.get(i).getId(), peList.get(j).getId());
			downTime = downTime + IdleTime.getPeDownTime(hostList.get(i).getId(), peList.get(j).getId());
		}			
		//averageIdleTime = idleTime/peListSize;
		//idleTime = idleTimewithDownTime - downTime;
		averageIdleTime = idleTime;
		idlePower = pow.getPower(idleUtilization, peListSize);
		idleEnergyConsumption = ((idlePower/3600) * (averageIdleTime/1000));
		averageIdleEnergyConsumption = idleEnergyConsumption/peListSize;
		totalIdleEnergyConsumption = totalIdleEnergyConsumption + idleEnergyConsumption; 
		totalAverageIdleEnergyConsumption = totalAverageIdleEnergyConsumption + averageIdleEnergyConsumption;
		Log.printLine( indent+ hostIdIdle + indent + indent+ indent+ idleTime + indent + indent+ indent+ downTime + indent + indent+ indent+ idleEnergyConsumption+ indent + indent+ indent+ averageIdleEnergyConsumption);
	}

	Log.printLine("========== OUTPUT For Host Idle Time  ==========");
	Log.printLine("Host ID" +indent+ indent + indent + "Total Idle Time" +indent+ indent + indent + "Total Down Time" +indent+ indent + indent + "Total Idle Energy Consumption" +indent+ indent + indent + "Average Idle Energy Consumption");		
	
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== OUTPUT For Energy Consumption Cloudlet Level ==========");
//	Log.printLine("Cloudlet ID" + indent + indent + indent + "VM ID" + indent +" Task Execution Length" + indent + "Total Finish Time" + indent + "Execution Time Changed" + indent + "Total DownTime" + indent + "Finished Time without DownTime" +indent+ "Energy Consumption" +indent+ "Total Checkpoint Overhead" +indent+ "Total Reexecution time" +indent+ "Ratio Clet Length");
	//Log.printLine("Cloudlet ID" + indent +" Task Execution Length" + indent + "Total Finish Time" + indent + "Execution Time Changed" + indent + "Total DownTime" + indent + "Finished Time without DownTime" +indent+ "Energy Consumption" +indent+ "Total Reexecution time" +indent+ "Total Checkpoint Overhead" );
	Log.printLine("Cloudlet ID" + indent +" Task Execution Length" + indent + indent + "Total Finish Time" + indent + indent +indent + "Execution Time Changed" + indent+ indent+ indent+ indent + "Total DownTime" + indent + indent + indent + "Total Re-execution Time" + indent + indent + indent + "Total Checkpoint Overhead" + indent + indent + indent + "Energy Consumption" );

	double totalCletCompletionTime = 0;
	double totalCletLength = 0;
	double totalExecutionTimeChanged = 0;
	int coreCount;
	double power;
	double vmUtilization;
	double turnAroundTime = 0.0;
	double tempVmEnergy;
	double tempVmEnergyWastage;
	HashMap<Integer, Double>vmEnergyConsumptionTable = new HashMap<Integer, Double>();
	HashMap<Integer, Double>vmEnergyWastageTable = new HashMap<Integer, Double>();
	
	for (int i = 0; i < size; i++) {		
		double cletInitialLength = 0.0;		
		double totalChkptOverheads = 0.0;		
		//cloudlet = list.get(i);			
		cloudlet = sufferedClets.get(i);
		vm = VmCloudletMapping.getVm(cloudlet);		
		Log.print(indent + cloudlet.getCloudletId() + indent + indent);		
		if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS){					
			if(cloudletTableBackup.containsKey(cloudlet.getCloudletId())){					
				cletInitialLength = ((double)cloudletTableBackup.get(cloudlet.getCloudletId()))/1000;				
			}
			vmUtilization = vmUtl.getVmAverageUtilization(vm.getId());
			coreCount = HostVMMapping.getHost(vm).getNumberOfPes();
			power = pow.getPower(vmUtilization, coreCount);
			initTime = broker.getcloudletInitialStartTime(cloudlet);			
			turnAroundTime = cloudlet.getFinishTime() - initTime;
			
			changeinExecutionTime = turnAroundTime - cletInitialLength;
			if(changeinExecutionTime<0.0){
				changeinExecutionTime = 0.0;				
				//totalFinishedTime = cloudletInitialLength;
			}
			
			totalCletCompletionTime = totalCletCompletionTime + turnAroundTime;			
			totalCletLength = totalCletLength + cletInitialLength;
			totalExecutionTimeChanged = totalExecutionTimeChanged + changeinExecutionTime;	
		
			totalDownTime = DownTimeTable.getCloudletDownTime(cloudlet.getCloudletId());
			totalReexecutionTime = ((double)CloudletReexecutionPart.getCletReexecutionTime(cloudlet.getCloudletId()))/1000;			
			finalTotalDownTime = finalTotalDownTime + totalDownTime;			
			finalTotalReexecutionTime = finalTotalReexecutionTime + totalReexecutionTime;
			
			if(CheckpointOverhead.checkpointOverheadTable.containsKey(cloudlet.getCloudletId())){
				totalChkptOverheads = CheckpointOverhead.getCheckpointOverheadTable(cloudlet.getCloudletId());
			}
			else{
				totalChkptOverheads = 0;
			}		
			
			totalFinishedTime_withoutDownTime = turnAroundTime - totalDownTime ; //with re-execution time and checkpoint overhead				
			totalFinishedTime_withoutChkptOverhead = totalFinishedTime_withoutDownTime - totalChkptOverheads; //only with re-execution time			
			energyPerCletWithoutOverheads = (((totalFinishedTime_withoutChkptOverhead)/3600) * (power/1000)); //Division is happening to convert seconds into hours and to convert watts into kilowats			
			energyPerCletForChkptOverheads = ((((totalChkptOverheads)/3600) * 1.15 * (power/1000)));			
			totalEnergyPerClet = energyPerCletWithoutOverheads + energyPerCletForChkptOverheads;		
			cletEnergyTable.put(cloudlet.getCloudletId(), totalEnergyPerClet);
			
			if(vmEnergyConsumptionTable.isEmpty()){
				vmEnergyConsumptionTable.put(vm.getId(), totalEnergyPerClet);
			}
			else{
				if(vmEnergyConsumptionTable.containsKey(vm.getId())){
					tempVmEnergy = vmEnergyConsumptionTable.get(vm.getId());
					tempVmEnergy = tempVmEnergy + totalEnergyPerClet;
					vmEnergyConsumptionTable.put(vm.getId(), tempVmEnergy);
				}
				else{
					vmEnergyConsumptionTable.put(vm.getId(), totalEnergyPerClet);
				}
			}
			
			Log.printLine(indent + indent + indent + indent + dft.format(cletInitialLength) +					
					indent + indent + indent  + indent + indent + dft.format(turnAroundTime)+ 
					indent + indent + indent + indent + indent + dft.format(changeinExecutionTime) + 
					indent + indent + indent + indent + indent + dft.format(totalDownTime)  
					+indent + indent + indent + indent + indent + dft.format(totalReexecutionTime)
					+indent + indent + indent + indent + indent + dft.format(totalChkptOverheads)+					
					indent + indent + indent + indent + indent + dft.format(totalEnergyPerClet)		
					);		
			}
		
	}
	
	Log.printLine("========== OUTPUT For Energy Consumption Cloudlet Level ==========");
	//Log.printLine("Cloudlet ID" + indent + indent + indent + "VM ID" + indent +" Task Execution Length" + indent + "Total Finish Time" + indent + "Execution Time Changed" + indent + "Total DownTime" + indent + "Finished Time without DownTime" +indent+ "Energy Consumption" +indent+ "Total Checkpoint Overhead" +indent+ "Total Reexecution time" +indent+ "Ratio Clet Length");
	Log.printLine("Cloudlet ID" + indent +" Task Execution Length" + indent + indent + "Total Finish Time" + indent + indent +indent + "Execution Time Changed" + indent+ indent+ indent+ indent + "Total DownTime" + indent + indent + indent + "Total Re-execution Time" + indent + indent + indent + "Total Checkpoint Overhead" + indent + indent + indent + "Energy Consumption" );

	/*
	Log.printLine();
	Log.printLine();
	Log.printLine("========== OUTPUT For Energy Consumption VM Level ==========");
	Log.printLine("VM ID" + indent +" Total Energy Consumption" );
	
	
	for(int i=0; i<newVmList.size(); i++){			
		vm = newVmList.get(i);
		ArrayList<Cloudlet>cletList = new ArrayList<Cloudlet>();
		cletList = VmCloudletMapping.getCloudlet(vm);
		totalEnergyConsumptionPerVm = 0.0;
		for(int j=0; j<cletList.size(); j++){
			if(cletEnergyTable.containsKey(cletList.get(j).getCloudletId())){
				totalEnergyConsumptionPerVm = totalEnergyConsumptionPerVm + cletEnergyTable.get(cletList.get(j).getCloudletId());
			}				
		}
		
		if(vmEnergyTable.isEmpty()){
			energyList = new ArrayList<Double>();
			energyList.add(totalEnergyConsumptionPerVm);
			vmEnergyTable.put(HostVMMapping.getHost(vm), energyList);			
		}
		else{
			if(vmEnergyTable.containsKey(HostVMMapping.getHost(vm))){
				energyList = new ArrayList<Double>();
				energyList = vmEnergyTable.get(HostVMMapping.getHost(vm));
				energyList.add(totalEnergyConsumptionPerVm);
				vmEnergyTable.put(HostVMMapping.getHost(vm), energyList);				
			}
			else{
				energyList = new ArrayList<Double>();
				energyList.add(totalEnergyConsumptionPerVm);
				vmEnergyTable.put(HostVMMapping.getHost(vm), energyList);				
			}
		}			
		
		Log.printLine( indent + vm.getId() +
				indent + indent + indent + indent + dft.format(totalEnergyConsumptionPerVm)
				);
	}		
	Log.printLine("========== OUTPUT For Energy Consumption VM Level==========");
	Log.printLine("VM ID" + indent +" Total Energy Consumption" );
	*/
	Log.printLine();
	Log.printLine();
	Log.printLine("========== OUTPUT For Energy Wastage Cloudlet Level ==========");
	//Log.printLine("Cloudlet ID" + indent + "Cloudlet Initial Length" +indent+ "Energy Wastage" +indent+ "Energy Chkpt overhead" +indent+ "Energy Reexecution");	
	Log.printLine( indent+"Cloudlet ID" + indent + indent+ indent+ "Energy Wastage" + indent + indent + indent+ "Energy Chkpt overhead" + indent + indent + indent+ "Energy Reexecution");
	
	
	for (int i = 0; i < size; i++){				
		double totalChkptOverheads = 0.0;		
		//cloudlet = list.get(i);	
		cloudlet = sufferedClets.get(i);
		vm = VmCloudletMapping.getVm(cloudlet);		
		vmUtilization = vmUtl.getVmAverageUtilization(vm.getId());
		coreCount = HostVMMapping.getHost(vm).getNumberOfPes();
		power = pow.getPower(vmUtilization, coreCount);
		//Log.print(indent + cloudlet.getCloudletId() + indent + indent);				
		if(CheckpointOverhead.checkpointOverheadTable.containsKey(cloudlet.getCloudletId())){
			totalChkptOverheads = CheckpointOverhead.getCheckpointOverheadTable(cloudlet.getCloudletId());
		}
		else{
			totalChkptOverheads = 0;
		}			
		totalReexecutionTime = ((double)CloudletReexecutionPart.getCletReexecutionTime(cloudlet.getCloudletId()))/1000;			
		energyPerCletForChkptOverheads = ((((totalChkptOverheads)/3600) * 1.15 * (power/1000)));
		energyPerCletForReexec = (((totalReexecutionTime)/3600) * (power/1000));					
		energyWastagePerClet = energyPerCletForChkptOverheads + energyPerCletForReexec;
		
		if(energyWastagePerClet<0.0){
			energyWastagePerClet = 0.0;
		}			
		
		if(vmEnergyWastageTable.isEmpty()){
			vmEnergyWastageTable.put(vm.getId(), energyWastagePerClet);
		}
		else{
			if(vmEnergyWastageTable.containsKey(vm.getId())){
				tempVmEnergyWastage = vmEnergyWastageTable.get(vm.getId());
				tempVmEnergyWastage = tempVmEnergyWastage + energyWastagePerClet;
				vmEnergyWastageTable.put(vm.getId(), tempVmEnergyWastage);
			}
			else{
				vmEnergyWastageTable.put(vm.getId(), energyWastagePerClet);
			}
		}
		
		cletEnergyWastageTable.put(cloudlet.getCloudletId(), energyWastagePerClet);
		cletEnergyWastageChkptTable.put(cloudlet.getCloudletId(), energyPerCletForChkptOverheads);
		cletEnergyWastageReExecTable.put(cloudlet.getCloudletId(), energyPerCletForReexec);			
			
			Log.printLine( indent + cloudlet.getCloudletId() +indent+ indent+ indent+ indent+ indent+ dft.format(energyWastagePerClet)+									
				indent + indent + indent + indent + indent +  dft.format(energyPerCletForChkptOverheads)+
				indent + indent + indent + indent + indent + dft.format(energyPerCletForReexec));			
			
	}	
	
	Log.printLine("========== OUTPUT For Energy Wastage Cloudlet Level ==========");
	Log.printLine( indent+"Cloudlet ID" + indent + indent+ indent+ "Energy Wastage" + indent + indent + indent+ "Energy Chkpt overhead" + indent + indent + indent+ "Energy Reexecution");
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== OUTPUT For Host Count ==========");	
	Log.printLine(indent + "Clock" + indent + indent + indent + "Host Count" );
	
	ArrayList<Integer>hostCount = new ArrayList<Integer>();
	ArrayList<Double>clockForHostCount = new ArrayList<Double>();
	hostCount = HostCounter.getHostCountList();
	clockForHostCount = HostCounter.getClockList();
	for(int i=0;i<hostCount.size();i++){
		Log.printLine(indent + clockForHostCount.get(i)  
		  +indent + indent +indent + hostCount.get(i)
		  );
		}
	
	Log.printLine("========== OUTPUT For Host Count ==========");		
	Log.printLine(indent + "Clock" + indent + indent + indent + "Host Count" );
	
	
	/*
	Log.printLine("========== OUTPUT For VM Wastage Cloudlet Level ==========");
	Log.printLine("VM ID" + indent + indent+ "Energy Wastage" + indent + indent + "Energy Chkpt overhead" + indent + indent + "Energy Reexecution");
	
	for(int i=0; i<newVmList.size(); i++){			
		vm = newVmList.get(i);
		ArrayList<Cloudlet>cletList = new ArrayList<Cloudlet>();
		cletList = VmCloudletMapping.getCloudlet(vm);
		double energyWastagePerVm = 0.0;
		double energyWastagePerVmChkpt = 0.0;
		double energyWastagePerVmReExec = 0.0;
		for(int j=0; j<cletList.size(); j++){
			if(cletEnergyWastageTable.containsKey(cletList.get(j).getCloudletId())){
				energyWastagePerVm = energyWastagePerVm + cletEnergyWastageTable.get(cletList.get(j).getCloudletId());
			}		
			if(cletEnergyWastageChkptTable.containsKey(cletList.get(j).getCloudletId())){
				energyWastagePerVmChkpt = energyWastagePerVmChkpt + cletEnergyWastageChkptTable.get(cletList.get(j).getCloudletId());
			}
			if(cletEnergyWastageReExecTable.containsKey(cletList.get(j).getCloudletId())){
				energyWastagePerVmReExec = energyWastagePerVmReExec + cletEnergyWastageReExecTable.get(cletList.get(j).getCloudletId());
			}
		}			
		Log.printLine( indent + vm.getId() +
				indent + indent + indent + indent + dft.format(energyWastagePerVm)+
				indent + indent + indent + indent + dft.format(energyWastagePerVmChkpt)+
				indent + indent + indent + indent + dft.format(energyWastagePerVmReExec)
				);
	}
	
	Log.printLine("========== OUTPUT For VM Wastage Cloudlet Level ==========");
	Log.printLine("VM ID" + indent + indent+ "Energy Wastage" + indent + indent + "Energy Chkpt overhead" + indent + indent + "Energy Reexecution");
	*/
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total OUTPUT ==========");
	//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Idle Energy Consumption" +indent+ "Total Idle Time" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
	//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Deadlines Achieved Percentage" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
	//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
	//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
	//Log.printLine("Total Energy Consumption" + indent +indent + "Total Energy Wastage" +indent+indent + "Total Deadlines Missed" +indent+indent + "Total Deadlines Achieved"   +indent+indent + "Percentage Achieved Deadlines" +indent+indent +"Application Utility Value" +indent+indent + "Total Failures Occurred" +indent+indent + "Total Checkpoints");
	Log.printLine("Total Energy Consumption" + indent + indent + indent + "Total Energy Wastage" + indent + indent + indent + "Total Idle Energy Consumption"  +indent +indent + indent + "Application Utility Value" );
	
		/**
		 * This gives the average of the reliabilities possessed by the whole system
		 * at different instances while executing all the VMs.   
		 */
		double totalReliability;	
		double totalReliabilityParallel = 0.0;
		double totalReliabilityAverage = 0.0;
		double totalAvailability = 0.0;
		double totalMaintainability = 0.0;	
		double percentageAchievedDeadlines;
			
		totalReliability = 0.0;		
		int systemReliabilityListSize;
		ArrayList<Double>systemReliabilityList = new ArrayList<Double>();
		ArrayList<Double>systemReliabilityListParallel = new ArrayList<Double>();
		ArrayList<Double>systemReliabilityListAverage = new ArrayList<Double>();
		ArrayList<Double>systemAvailabilityList = new ArrayList<Double>();
		ArrayList<Double>systemMaintainabilityList = new ArrayList<Double>();
		systemReliabilityList.addAll(hvmRbl.getSystemReliability());
		systemReliabilityListSize = hvmRbl.getSystemReliability().size();
		systemReliabilityListParallel.addAll(hvmRbl.getSystemReliabilityParallel());
		systemReliabilityListAverage.addAll(hvmRbl.getSystemReliabilityAverage());
		systemAvailabilityList.addAll(hvmRbl.getSystemAvailability());
		systemMaintainabilityList.addAll(hvmRbl.getSystemMaintainability());
		for(int i=0;i<systemReliabilityListSize;i++){
			totalReliability = totalReliability + systemReliabilityList.get(i);
			totalReliabilityParallel = totalReliabilityParallel + systemReliabilityListParallel.get(i);
			totalReliabilityAverage = totalReliabilityAverage + systemReliabilityListAverage.get(i);
			totalAvailability = totalAvailability + systemAvailabilityList.get(i);
			totalMaintainability = totalMaintainability + systemMaintainabilityList.get(i);
		}
		totalReliability = totalReliability/systemReliabilityListSize;
		totalReliabilityParallel = totalReliabilityParallel/systemReliabilityListSize;
		totalReliabilityAverage = totalReliabilityAverage/systemReliabilityListSize;
		totalAvailability = (totalAvailability/systemReliabilityListSize)*100;
		totalMaintainability = (totalMaintainability/systemReliabilityListSize)*100;		
				
		double totalEnergyConsumption = 0.0;
		totalEnergyWastage = 0.0;
		
		for(int i=0; i<newVmList.size(); i++){
			totalEnergyConsumption = totalEnergyConsumption + vmEnergyConsumptionTable.get(newVmList.get(i).getId());			
			totalEnergyWastage = totalEnergyWastage + vmEnergyWastageTable.get(newVmList.get(i).getId());
		}
			
		jobUtilityValue = jobUtilityValue/size;			
		percentageAchievedDeadlines = (((double)countDeadlineAchieved*100)/((double)countDeadlineAchieved+(double)countDeadlineMissed));
		
		Log.printLine(indent+ dft.format(totalEnergyConsumption)+
				indent + indent + indent + indent+ indent + indent + indent +dft.format(totalEnergyWastage)+
				indent + indent + indent + indent+ indent + indent +dft.format(totalIdleEnergyConsumption)+
				indent + indent + indent + indent + indent + indent + indent + indent +dft.format(jobUtilityValue));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total OUTPUT ==========");
		Log.printLine( "Total Energy Consumption" + indent + indent + indent + "Total Energy Wastage" + indent + indent + indent + "Total Idle Energy Consumption"  +indent +indent + indent + "Application Utility Value" );
		
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Deadlines OUTPUT ==========");
		Log.printLine(indent + "Total Deadlines Achieved" + indent + indent + "Total Deadlines Missed" + indent + indent + "Percentage Achieved Deadlines");
		
		Log.printLine( indent + indent + indent + countDeadlineAchieved +
				indent + indent + indent +  indent + indent +  indent + indent +countDeadlineMissed+
				indent + indent + indent + indent + indent + dft.format(percentageAchievedDeadlines));		
		
		Log.printLine("========== Deadlines OUTPUT ==========");
		Log.printLine(indent + "Total Deadlines Achieved" + indent + indent + "Total Deadlines Missed" + indent + indent + "Percentage Achieved Deadlines");
		
		
		Log.printLine();
		Log.printLine();		
		Log.printLine("========== Reliability OUTPUT ==========");
		Log.printLine(indent + "Total Reliability Serial" + indent + indent + "Total Reliability Parallel" + indent + indent + "Total Reliability Average" + indent + indent + "Total Availability" + indent + indent + "Total Maintainability");
		
		Log.printLine( indent + dft.format(totalReliability*100) +
				indent + indent + indent + indent + indent + dft.format(totalReliabilityParallel*100)+
				indent + indent + indent + indent + indent + indent + dft.format(totalReliabilityAverage*100)+
				indent + indent + indent + indent + indent + dft.format(totalAvailability)+
				indent + indent + indent + indent + indent + dft.format(totalMaintainability));		
		
		Log.printLine("========== Reliability OUTPUT ==========");
		Log.printLine(indent + "Total Reliability Serial" + indent + indent + "Total Reliability Parallel" + indent + indent + "Total Reliability Average" + indent + indent + "Total Availability" + indent + indent + "Total Maintainability");
			
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average OUTPUTs ==========");
		Log.printLine("Average Task Length" + indent + indent+ indent + indent+ "Average Makespan" + indent + indent+ indent + indent+ "Average Execution Time Changed");
				
		double averageCletCompletionTime;
		double averageCletLength;
		double averageExecutionTimeChanged;		
	
		averageCletCompletionTime = totalCletCompletionTime/size;
		averageCletLength = totalCletLength/size;
		averageExecutionTimeChanged = totalExecutionTimeChanged/size;	
		
		Log.printLine( indent +dft.format(averageCletLength/3600) + indent + indent + indent + indent +indent +dft.format(averageCletCompletionTime/3600) + indent + indent + indent + indent +dft.format(averageExecutionTimeChanged/3600));

		Log.printLine("========== Average OUTPUTs ==========");
		Log.printLine("Average Task Length" + indent + indent+ indent + indent+ "Average Makespan" + indent + indent+ indent + indent+ "Average Execution Time Changed");
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Checkpoint OUTPUT ==========");
		//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Idle Energy Consumption" +indent+ "Total Idle Time" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
		Log.printLine("Total Failures Occurred" + indent + indent + indent + indent+ "Total Checkpoints " + indent + indent + indent + indent+ "Total VM Consolidations ");
		Log.printLine(indent + indent + totalFailureCount + indent + indent + indent + indent + indent + indent + indent + indent+ totalCheckpointsCount+ indent + indent + indent + indent + indent + indent+ indent + indent + indent+ totalVmConsolidations);
		Log.printLine("Total Failures Occurred" + indent + indent + indent + indent+ "Total Checkpoints " + indent + indent + indent + indent+ "Total VM Consolidations ");
		
		Log.printLine("========== Checkpoint OUTPUT ==========");		
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Overhead OUTPUTs ==========");
		//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Idle Energy Consumption" +indent+ "Total Idle Time" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
		Log.printLine("Total DownTime" + indent + indent + indent + indent+ indent + indent+ "Total Reexecution Time "+ indent + indent + indent + indent+ indent + indent+ "Host Count " );
		Log.printLine(indent  + dft.format(finalTotalDownTime/3600) + indent + indent + indent + indent+ indent + indent+ dft.format(finalTotalReexecutionTime/3600)+ indent + indent + indent + indent+ indent + indent+ hostList.size());
		Log.printLine();		
		Log.printLine(indent  + dft.format(finalTotalDownTime) + indent + indent + indent + indent+ indent + indent+ dft.format(finalTotalReexecutionTime)+ indent + indent + indent + indent+ indent + indent+ hostList.size());
		
		Log.printLine("Total DownTime" + indent + indent + indent + indent+ "Total Reexecution Time" + indent + indent + indent + indent+ indent + indent+ "Host Count ");
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Application Finishing Time ==========");
		Log.printLine( indent + indent +dft.format(applicationFinishingTime) );
		
		Log.printLine();
		Log.printLine("========== Application Finishing Time ==========");

	}

//private static void printCloudletListRestarting(List<Cloudlet> list, List<Host> hostList, FTAFileReader ftaread, FailureDatacenterBroker broker){
private static void printCloudletListRestarting(List<Cloudlet> list, List<Host> hostList, FTAFileReaderGrid5000 ftaread, FailureDatacenterBroker broker, HostVMMappingReliability hvmRbl, List<Vm>newVmList, VmUtilization vmUtl, PowerModel pow){	
	//System.out.println("Printing is going to happen");
		Double initTime;		
	//	Double totalFinishedTime;	
	//	Double totalEnergyConsumptionPerVm;
		Double totalEnergyConsumptionPerCloudlet;
		Double changeinExecutionTime;
		Double totalDownTime;
		Double totalFinishedTime_withoutDownTime;
		Double energyWastage;
		double applicationFinishingTime = 0.0;
	//	Double totalEnergyWastagePerVm;
	//	double totalMakeSpan = 0.0;
		//Double actualEnergyConsumption;
		Double totalEnergyWastage = 0.0;
		int countDeadlineMissed = 0;
		int countDeadlineAchieved = 0;
		Double 	totalReexecutionTime;
		//Double cloudletInitialLength;
	//	ArrayList<Double>energyList;
		double totalDeadline = 0;
		HashMap<Integer, Double> cletEnergyTable= new HashMap<Integer, Double>();
		HashMap<Integer, Double> cletEnergyWastageTable= new HashMap<Integer, Double>();		
		Vm vm;			
		int size = sufferedClets.size(); //For only suffered cloudlets and corresponding VMs. 
		Cloudlet cloudlet;	
		FileWriter HostCountClock = null;
		FileWriter HostCount = null;
		
		System.out.println("Printing results for Restarting Fault Tolerance Mechanism");
		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
				"Data center ID" + indent + indent + "VM ID" + indent +  indent + "Bag ID" + indent +  indent + "Cloudlet Initial Length" + indent + "Intial Start Time" + indent + indent +"Finish Time" +indent+ indent+ "Turnaround Time" +indent+ indent+ "Deadline Value" +indent+ indent+ "Deadline Actual Value"+indent+ indent+ "Utility Value"+indent+ indent +"Deadline");
		
		DecimalFormat dft = new DecimalFormat("###.####");			
		
		
		for (int i = 0; i < size; i++) {		
			cloudlet = sufferedClets.get(i);		
			double cloudletInitialLength = 0.0;		
			vm = VmCloudletMapping.getVm(cloudlet);				
			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS){
				Log.print(indent + cloudlet.getCloudletId() + indent + indent);
				Log.print("SUCCESS");				
				initTime = broker.getcloudletInitialStartTime(cloudlet);						
				if(cloudletTableBackup.containsKey(cloudlet.getCloudletId())){					
					cloudletInitialLength = ((double)cloudletTableBackup.get(cloudlet.getCloudletId()))/1000;
				}					
				double deadline;			
				double utilityValue;	
				double turnAroundTime;
				int bagID;
				double finishedTime;
				
				bagID = BagTaskMapping.getBagID(cloudlet.getCloudletId());
				finishedTime = cloudlet.getFinishTime();
				deadline = initTime + (stringencyFactor * (cloudletInitialLength));						
				if(finishedTime<=deadline){				
					utilityValue = 1;
				}
				else{
					utilityValue = (1-((finishedTime-deadline)/deadline));				
					if(utilityValue<0){
						utilityValue = 0;
					}
				}			
				jobUtilityValue = jobUtilityValue + utilityValue;
				totalDeadline = totalDeadline + (deadline-initTime);	
				turnAroundTime = finishedTime - initTime;

				if(deadline > cloudlet.getFinishTime()){
					
					countDeadlineAchieved = countDeadlineAchieved + 1;
				Log.printLine( indent + indent + cloudlet.getResourceId() + indent + indent + indent + indent + cloudlet.getVmId() +
						indent + indent + indent + indent + bagID +
						indent + indent + indent + indent + dft.format(cloudletInitialLength) +
						indent + indent + indent + indent + indent + dft.format(initTime) +
						indent + indent + indent +  dft.format(finishedTime)+	
						indent + indent + indent +  dft.format(turnAroundTime)+
						indent + indent + indent +  dft.format(deadline)+
						indent + indent + indent +  dft.format(deadline-initTime)+
						indent + indent + indent +  dft.format(utilityValue)+
						indent + indent + indent+ "Achieved" );
				}
				else{					
					countDeadlineMissed = countDeadlineMissed + 1;
					Log.printLine( indent + indent + cloudlet.getResourceId() + indent + indent + indent + indent + cloudlet.getVmId() +
							indent + indent + indent + indent + bagID +
							indent + indent + indent + indent + dft.format(cloudletInitialLength) +
							indent + indent + indent + indent + indent + dft.format(initTime) +
							indent + indent + indent +  dft.format(finishedTime)+	
							indent + indent + indent +  dft.format(turnAroundTime)+														
							indent + indent + indent +  dft.format(deadline)+
							indent + indent + indent +  dft.format(deadline-initTime)+
							indent + indent + indent +  dft.format(utilityValue)+
							indent + indent + indent+ "Missed" );
					}
				}
			}
		
		
		Log.printLine("========== OUTPUT ==========");
		
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
				"Data center ID" + indent + indent + "VM ID" + indent +  indent + "Bag ID" + indent +  indent + "Cloudlet Initial Length" + indent + "Intial Start Time" + indent + indent +"Finish Time" +indent+ indent+ "Turnaround Time" +indent+ indent+ "Deadline Value" +indent+ indent+ "Deadline Actual Value"+indent+ indent+ "Utility Value"+indent+ indent +"Deadline");
		
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== OUTPUT For Bag Level Information ==========");
		//Log.printLine("Job ID" + indent + indent + indent + "Finishing Time");
		Log.printLine( "Finishing Time");
		int bagCount;
		int cletBagID;	
		bagCount = RunTimeConstants.numberofBags;
		for(int i=0; i<bagCount; i++){
			double jobFinishingTime = 0;
			double turnAroundTime = 0;
			for(int j=0; j<size; j++){
				cloudlet = sufferedClets.get(j);
				initTime = broker.getcloudletInitialStartTime(cloudlet);	
				turnAroundTime = cloudlet.getFinishTime() - initTime;
				cletBagID = BagTaskMapping.getBagID(cloudlet.getCloudletId());
				if(i == cletBagID){
					if(jobFinishingTime < turnAroundTime){
						jobFinishingTime = turnAroundTime;
					}
				}
			}
			if(applicationFinishingTime < jobFinishingTime){
				applicationFinishingTime = jobFinishingTime;
			}
			//Log.printLine( indent + i + indent + indent + indent + indent + dft.format(jobFinishingTime));
			Log.printLine( indent + dft.format(jobFinishingTime));
		}
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== OUTPUT For Bag Level Information ==========");
		///Log.printLine("Job ID" + indent + indent + indent + "Finishing Time");
		Log.printLine( "Finishing Time");
		
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== OUTPUT For Host Information ==========");
		Log.printLine("Host ID" + indent + "FTA Host ID" + indent +" MTBF" + indent + "MTTR" +indent+ "Max Hazard Rate" +indent+ "Current Hazard Rate" +indent+ "Host Power" +indent+ "Host Availability" +indent+ "Host Maintainability");
		
		int ftaHostID;	
		for(int i=0;i<HostMapping.getHostMapSize();i++){
			ftaHostID = HostMapping.getFTAhostID(i);
			
			Log.printLine(  i + indent + indent + ftaHostID +
					indent + indent + indent + indent + dft.format(ftaread.getMTBF(ftaHostID)) +
					indent + indent + indent + indent + dft.format(ftaread.getMTTR(ftaHostID)) +
					indent + indent + indent + indent + ftaread.getMaxHazardRate(ftaHostID)+
					indent + indent + indent + indent + ftaread.getCurrentHazardRate(ftaHostID)+
					indent + indent + indent + indent + dft.format(ftaread.getPowerforHost(ftaHostID))+
					indent + indent + indent + indent + dft.format(ftaread.getAvailability(ftaHostID))+
					indent + indent + indent + indent + dft.format(ftaread.getMaintainability(ftaHostID))
					);			
		}	
		
		Log.printLine("========== OUTPUT For Host Information ==========");
		Log.printLine("Host ID" + indent + "FTA Host ID" + indent +" MTBF" + indent + "MTTR" +indent+ "Max Hazard Rate" +indent+ "Current Hazard Rate" +indent+ "Host Power" +indent+ "Host Availability" +indent+ "Host Maintainability");

		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== OUTPUT For Host Idle Time  ==========");
		Log.printLine("Host ID" +indent+ indent + indent + "Total Idle Time" +indent+ indent + indent + "Total Down Time" +indent+ indent + indent + "Total Idle Energy Consumption" +indent+ indent + indent + "Average Idle Energy Consumption");		
		
		int hostIdIdle;
		double idleTime;
		double averageIdleTime;
		double averageIdleEnergyConsumption;
		int peListSize;
		double idleUtilization = 0.0;
		double idlePower = 0.0;		
		double totalIdleEnergyConsumption = 0.0;
		double totalAverageIdleEnergyConsumption = 0.0;
		double idleEnergyConsumption = 0.0;
		double downTime;
	//	double idleTimewithDownTime;
		List<Pe>peList;
		for(int i=0; i<hostList.size(); i++){			
			idleTime = 0.0;
			downTime = 0.0;
		//	idleTimewithDownTime = 0.0;
			peList = new ArrayList<Pe>();
			ftaHostID = HostMapping.getFTAhostID(hostList.get(i).getId());
			idleUtilization = ftaread.getCurrentUtilization(ftaHostID);
			hostIdIdle = hostList.get(i).getId();			
			peListSize = hostList.get(i).getNumberOfPes();
			peList = hostList.get(i).getPeList();
			for(int j=0; j<peList.size(); j++){
			//	idleTimewithDownTime = idleTimewithDownTime + IdleTime.getPeIdleTime(hostList.get(i).getId(), peList.get(j).getId());
				idleTime = idleTime + IdleTime.getPeIdleTime(hostList.get(i).getId(), peList.get(j).getId());
				downTime = downTime + IdleTime.getPeDownTime(hostList.get(i).getId(), peList.get(j).getId());
			}			
			//averageIdleTime = idleTime/peListSize;
			//idleTime = idleTimewithDownTime - downTime;
			averageIdleTime = idleTime;
			idlePower = pow.getPower(idleUtilization, peListSize);
			idleEnergyConsumption = ((idlePower/3600) * (averageIdleTime/1000));
			averageIdleEnergyConsumption = idleEnergyConsumption/peListSize;
			totalIdleEnergyConsumption = totalIdleEnergyConsumption + idleEnergyConsumption; 
			totalAverageIdleEnergyConsumption = totalAverageIdleEnergyConsumption + averageIdleEnergyConsumption;
			Log.printLine( indent+ hostIdIdle + indent + indent+ indent+ idleTime + indent + indent+ indent+ downTime + indent + indent+ indent+ idleEnergyConsumption + indent + indent+ indent+ averageIdleEnergyConsumption);
		}
	
		Log.printLine("========== OUTPUT For Host Idle Time  ==========");
		Log.printLine("Host ID" +indent+ indent + indent + "Total Idle Time" +indent+ indent + indent + "Total Down Time" +indent+ indent + indent + "Total Idle Energy Consumption" +indent+ indent + indent + "Average Idle Energy Consumption");		
		
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== OUTPUT For Energy Consumption Cloudlet Level==========");
		//Log.printLine("Cloudlet ID" + indent + "VM ID" + indent +" Task Execution Length" + indent + "Total Finish Time" + indent + "Execution Time Changed" + indent + "Total DownTime" + indent + "Finished Time without DownTime" +indent+ "Energy Consumption" +indent+ "Total Reexecution Time" +indent+ "Ratio Clet Length");
		//Log.printLine("Cloudlet ID" + indent +" Task Execution Length" + indent + "Total Finish Time" + indent + "Execution Time Changed" + indent + "Total DownTime" + indent + "Finished Time without DownTime" +indent+ "Energy Consumption" +indent+ "Total Re-execution Time");
		Log.printLine("Cloudlet ID" + indent +" Task Execution Length" + indent + indent + "Total Finish Time" + indent + indent + indent + "Execution Time Changed" + indent + indent + indent +indent + "Total DownTime" +indent +indent + indent + "Total Re-execution Time" +indent+indent + indent + "Energy Consumption" );
		
		double totalCletCompletionTime = 0;
		double totalCletLength = 0;
		double totalExecutionTimeChanged = 0;		
		int coreCount;
		double power;
		double vmUtilization;
		double turnAroundTime = 0.0;
		HashMap<Integer, Double>vmEnergyConsumptionTable = new HashMap<Integer, Double>();
		HashMap<Integer, Double>vmEnergyWastageTable = new HashMap<Integer, Double>();
		
		for (int i = 0; i < size; i++) {			
			double cloudletInitialLength = 0.0;			
			cloudlet = sufferedClets.get(i);
				
			vm = VmCloudletMapping.getVm(cloudlet);			
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);			
			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS){			
				if(cloudletTableBackup.containsKey(cloudlet.getCloudletId())){			
					cloudletInitialLength = ((double)cloudletTableBackup.get(cloudlet.getCloudletId()))/1000;				
				}				
				vmUtilization = vmUtl.getVmAverageUtilization(vm.getId());
				coreCount = HostVMMapping.getHost(vm).getNumberOfPes();
				power = pow.getPower(vmUtilization, coreCount);	
				initTime = broker.getcloudletInitialStartTime(cloudlet);		
				totalDownTime = DownTimeTable.getCloudletDownTime(cloudlet.getCloudletId());
				totalReexecutionTime = ((double)CloudletReexecutionPart.getCletReexecutionTime(cloudlet.getCloudletId()))/1000;
				turnAroundTime = cloudlet.getFinishTime() - initTime;		
				//totalFinishedTime = cloudletInitialLength+totalDownTime+totalReexecutionTime;
				totalCletCompletionTime = totalCletCompletionTime + turnAroundTime;					
				totalFinishedTime_withoutDownTime = turnAroundTime - totalDownTime;			
				finalTotalDownTime = finalTotalDownTime + totalDownTime;				
				finalTotalReexecutionTime = finalTotalReexecutionTime + totalReexecutionTime;				
				totalEnergyConsumptionPerCloudlet = (((totalFinishedTime_withoutDownTime)/3600) * (power/1000));				
				changeinExecutionTime = (turnAroundTime - cloudletInitialLength);				
				if(changeinExecutionTime < 0){
					changeinExecutionTime = 0.0;					
					turnAroundTime = cloudletInitialLength;
				}							
				totalCletLength = totalCletLength + cloudletInitialLength;				
				totalExecutionTimeChanged = totalExecutionTimeChanged + changeinExecutionTime;				
				cletEnergyTable.put(cloudlet.getCloudletId(), totalEnergyConsumptionPerCloudlet);	
				
				if(vmEnergyConsumptionTable.isEmpty()){
					vmEnergyConsumptionTable.put(vm.getId(), totalEnergyConsumptionPerCloudlet);
				}
				else
				{				
					double tempVmEnergy;				
					if(vmEnergyConsumptionTable.containsKey(vm.getId())){
						tempVmEnergy = vmEnergyConsumptionTable.get(vm.getId());
						tempVmEnergy = tempVmEnergy + totalEnergyConsumptionPerCloudlet;
						vmEnergyConsumptionTable.put(vm.getId(), tempVmEnergy);
					}
					else{
						vmEnergyConsumptionTable.put(vm.getId(), totalEnergyConsumptionPerCloudlet);
					}				
				}
				
				Log.printLine( indent + indent +dft.format(cloudletInitialLength) +					
						indent + indent + indent  + indent + dft.format(turnAroundTime)+ 
						indent + indent + indent + indent + indent + dft.format(changeinExecutionTime) + 
						indent + indent + indent + indent + dft.format(totalDownTime) + 
						indent + indent + indent + indent + indent + dft.format(totalReexecutionTime)						
						+indent + indent + indent + indent + dft.format(totalEnergyConsumptionPerCloudlet)														
						);			
					}			
			}
		
		Log.printLine("========== OUTPUT For Energy Consumption Cloudlet Level ==========");
		//Log.printLine("Cloudlet ID" + indent +" Task Execution Length" + indent + "Total Finish Time" + indent + "Execution Time Changed" + indent + "Total DownTime" + indent + "Finished Time without DownTime" +indent+ "Energy Consumption" +indent+ "Total Re-execution Time");
		Log.printLine("Cloudlet ID" + indent +" Task Execution Length" + indent + indent + "Total Finish Time" + indent + indent + indent + "Execution Time Changed" + indent + indent + indent +indent + "Total DownTime" +indent +indent + indent + "Total Re-execution Time" +indent+indent + indent + "Energy Consumption" );
		
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== OUTPUT For Energy Wastage Cloudlet Level ==========");
		//Log.printLine("Cloudlet ID" + indent + "VM ID" + indent + "Cloudlet Initial Length" +indent+ "Energy Wastage");
		Log.printLine("Cloudlet ID" + indent + indent + indent + "Energy Wastage");
		
		for (int i = 0; i < size; i++) {				
			cloudlet = sufferedClets.get(i);
			vm = VmCloudletMapping.getVm(cloudlet);		
			vmUtilization = vmUtl.getVmAverageUtilization(vm.getId());
			coreCount = HostVMMapping.getHost(vm).getNumberOfPes();
			power = pow.getPower(vmUtilization, coreCount);					
			totalReexecutionTime = ((double)CloudletReexecutionPart.getCletReexecutionTime(cloudlet.getCloudletId()))/1000;				
			energyWastage = (((totalReexecutionTime)/3600) * (power/1000));				
			cletEnergyWastageTable.put(cloudlet.getCloudletId(), energyWastage);	
			if(vmEnergyWastageTable.isEmpty()){
				vmEnergyWastageTable.put(vm.getId(), energyWastage);
			}
			else
			{				
				double tempVmEnergyWastage;				
				if(vmEnergyWastageTable.containsKey(vm.getId())){
					tempVmEnergyWastage = vmEnergyWastageTable.get(vm.getId());
					tempVmEnergyWastage = tempVmEnergyWastage + energyWastage;
					vmEnergyWastageTable.put(vm.getId(), tempVmEnergyWastage);
				}
				else{
					vmEnergyWastageTable.put(vm.getId(), energyWastage);
				}				
			}	
			Log.printLine(indent + cloudlet.getCloudletId()+
						indent + indent + indent + dft.format(energyWastage));			
				
		}	
		
		Log.printLine("========== OUTPUT For Energy Wastage Cloudlet Level ==========");
		//Log.printLine("Cloudlet ID" + indent + "VM ID" + indent + "Cloudlet Initial Length" +indent+ "Energy Wastage");
		Log.printLine("Cloudlet ID" + indent + indent + indent + "Energy Wastage");
		
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== OUTPUT For Host Count ==========");	
		Log.printLine(indent + "Clock" + indent + indent + indent + "Host Count" );
		//try{
		//HostCountClock = new FileWriter("/raid/disc1/ysharma1/Java/Results/Simulation_File/HostCountClock.txt", false);
		//HostCount = new FileWriter("/raid/disc1/ysharma1/Java/Results/Simulation_File/HostCount.txt", false);
		ArrayList<Integer>hostCount = new ArrayList<Integer>();
		ArrayList<Double>clockForHostCount = new ArrayList<Double>();
		hostCount = HostCounter.getHostCountList();
		clockForHostCount = HostCounter.getClockList();
		for(int i=0;i<hostCount.size();i++){
			//HostCountClock.write((clockForHostCount.get(i))+System.getProperty("line.seperator"));
			//HostCount.write((hostCount.get(i))+System.getProperty("line.seperator"));
			Log.printLine(indent + clockForHostCount.get(i)  
			  +indent + indent +indent + hostCount.get(i)
			  );
			}
		//HostCountClock.close();
		//HostCount.close();
		
		//}catch(IOException e){
			//System.out.println("Error has been occurred while writing output for host count");
		//}
		Log.printLine("========== OUTPUT For Host Count ==========");		
		Log.printLine(indent + "Clock" + indent + indent + indent + "Host Count" );
		
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total OUTPUT ==========");
		Log.printLine(indent + "Total Energy Consumption" + indent + indent + indent + "Total Energy Wastage" + indent + indent + indent + "Total Idle Energy Consumption"  +indent +indent + indent + "Application Utility Value" );
						
			double totalReliability;	
			double totalReliabilityParallel = 0.0;
			double totalReliabilityAverage = 0.0;
			double totalAvailability = 0.0;
			double totalMaintainability = 0.0;
			double percentageAchievedDeadlines;
			//ArrayList<Double>energyConsumptionperHost = new ArrayList<Double>();		
			totalReliability = 0.0;		
			int systemReliabilityListSize;
			ArrayList<Double>systemReliabilityList = new ArrayList<Double>();
			ArrayList<Double>systemReliabilityListParallel = new ArrayList<Double>();
			ArrayList<Double>systemReliabilityListAverage = new ArrayList<Double>();
			ArrayList<Double>systemAvailabilityList = new ArrayList<Double>();
			ArrayList<Double>systemMaintainabilityList = new ArrayList<Double>();
			systemReliabilityList.addAll(hvmRbl.getSystemReliability());
			systemReliabilityListSize = hvmRbl.getSystemReliability().size();
			systemReliabilityListParallel.addAll(hvmRbl.getSystemReliabilityParallel());
			systemReliabilityListAverage.addAll(hvmRbl.getSystemReliabilityAverage());
			systemAvailabilityList.addAll(hvmRbl.getSystemAvailability());
			systemMaintainabilityList.addAll(hvmRbl.getSystemMaintainability());
			for(int i=0;i<systemReliabilityListSize;i++){
				totalReliability = totalReliability + systemReliabilityList.get(i);
				totalReliabilityParallel = totalReliabilityParallel + systemReliabilityListParallel.get(i);
				totalReliabilityAverage = totalReliabilityAverage + systemReliabilityListAverage.get(i);
				totalAvailability = totalAvailability + systemAvailabilityList.get(i);
				totalMaintainability = totalMaintainability + systemMaintainabilityList.get(i);
				
			}
			totalReliability = totalReliability/systemReliabilityListSize;
			totalReliabilityParallel = totalReliabilityParallel/systemReliabilityListSize;
			totalReliabilityAverage = totalReliabilityAverage/systemReliabilityListSize;
			totalAvailability = (totalAvailability/systemReliabilityListSize)*100;
			totalMaintainability = (totalMaintainability/systemReliabilityListSize)*100;			
			
		//	energyList = new ArrayList<Double>();			
			
			double totalEnergyConsumption = 0.0;
			
			for(int i=0; i<newVmList.size();i++){
				totalEnergyConsumption = totalEnergyConsumption + vmEnergyConsumptionTable.get(newVmList.get(i).getId());
				totalEnergyWastage = totalEnergyWastage + vmEnergyWastageTable.get(newVmList.get(i).getId());
			}					
			
			jobUtilityValue = jobUtilityValue/size;		
		
			percentageAchievedDeadlines = (((double)countDeadlineAchieved*100)/((double)countDeadlineAchieved+(double)countDeadlineMissed));	
			
			Log.printLine( indent + dft.format(totalEnergyConsumption)+					
					indent + indent + indent + indent + indent +dft.format(totalEnergyWastage)+
					indent + indent + indent + indent + indent +dft.format(totalIdleEnergyConsumption)+									
					indent + indent + indent + indent + indent + indent + indent +dft.format(jobUtilityValue));			
			
				
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total OUTPUT ==========");
		//Log.printLine(indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Percentage Achieved Deadlines" +indent+ "Application Utility Value" +indent+ "Total Failures Occurred" );
		Log.printLine(indent + "Total Energy Consumption" + indent + indent + indent + "Total Energy Wastage" + indent + indent + indent + "Total Idle Energy Consumption"  +indent +indent + indent + "Application Utility Value" );
		
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Deadlines OUTPUT ==========");
		//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Idle Energy Consumption" +indent+ "Total Idle Time" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
		Log.printLine(indent + "Total Deadlines Achieved" + indent + indent + "Total Deadlines Missed" + indent + indent + "Percentage Achieved Deadlines");
		
		Log.printLine( indent + indent + indent + countDeadlineAchieved +
				indent + indent + indent +  indent + indent +countDeadlineMissed+
				indent + indent + indent + indent + dft.format(percentageAchievedDeadlines));		
		
		Log.printLine("========== Deadlines OUTPUT ==========");
		//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Idle Energy Consumption" +indent+ "Total Idle Time" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
		Log.printLine(indent + "Total Deadlines Achieved" + indent + indent + "Total Deadlines Missed" + indent + indent + "Percentage Achieved Deadlines");
		
		
		Log.printLine();
		Log.printLine();		
		Log.printLine("========== Reliability OUTPUT ==========");
		//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Idle Energy Consumption" +indent+ "Total Idle Time" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
		Log.printLine(indent + "Total Reliability Serial" + indent + indent + "Total Reliability Parallel" + indent + indent + "Total Reliability Average" + indent + indent + "Total Availability" + indent + indent + "Total Maintainability");
		
		Log.printLine( indent + dft.format(totalReliability*100) +
				indent + indent + indent + indent + indent + dft.format(totalReliabilityParallel*100)+
				indent + indent + indent + indent + indent + indent + indent + dft.format(totalReliabilityAverage*100)+
				indent + indent + indent + indent + indent + dft.format(totalAvailability)+
				indent + indent + indent + indent + indent + dft.format(totalMaintainability));		
		
		Log.printLine("========== Reliability OUTPUT ==========");
		//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Idle Energy Consumption" +indent+ "Total Idle Time" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
		Log.printLine(indent + "Total Reliability Serial" + indent + indent + "Total Reliability Parallel" + indent + indent + "Total Reliability Average" + indent + indent + "Total Availability" + indent + indent + "Total Maintainability");
		
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average OUTPUTs ==========");
		//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Idle Energy Consumption" +indent+ "Total Idle Time" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
		//Log.printLine("Benefit Function Serial" + indent + indent + indent + indent+ "Benefit Function Average" + indent + indent + indent + indent + "Average Task Length" + indent + indent+ indent + indent+ "Average Makespan" + indent + indent+ indent + indent+ "Average Execution Time Changed"+ indent + indent+ indent + indent+ "Average Deadline");
		Log.printLine("Average Task Length" + indent + indent+ indent + indent+ "Average Makespan" + indent + indent+ indent + indent+ "Average Execution Time Changed");
		
		//double benefitFunctionSerial;
		//double benefitFunctionAverage;
		double averageCletCompletionTime;
		double averageCletLength;
		double averageExecutionTimeChanged;		
	//	benefitFunctionSerial = (totalReliability*100)/totalEnergyConsumption;
		//benefitFunctionAverage = ((totalReliabilityAverage*100)/totalEnergyConsumption);
		//totalRatioCletLengthAndCletFinishTime = totalRatioCletLengthAndCletFinishTime/list.size();
		averageCletCompletionTime = totalCletCompletionTime/size;
		averageCletLength = totalCletLength/size;
		averageExecutionTimeChanged = totalExecutionTimeChanged/size;		
		Log.printLine( indent +dft.format(averageCletLength/3600) + indent + indent + indent + indent +indent +dft.format(averageCletCompletionTime/3600) + indent + indent + indent + indent + indent +dft.format(averageExecutionTimeChanged/3600));
		
		
		//Log.printLine( benefitFunctionSerial + indent + indent + indent + indent +indent +benefitFunctionAverage + indent + indent + indent + indent +indent +averageCletLength + indent + indent + indent + indent +indent +averageCletCompletionTime + indent + indent + indent + indent + indent +averageExecutionTimeChanged + indent + indent + indent + indent + indent +averageDeadline);
		
		Log.printLine("========== Average OUTPUTs ==========");
		//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Idle Energy Consumption" +indent+ "Total Idle Time" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
		//Log.printLine("Benefit Function Serial" + indent + indent + indent + indent+ "Benefit Function Average" + indent + indent + indent + indent + "Average Task Length" + indent + indent+ indent + indent+ "Average Makespan" + indent + indent+ indent + indent+ "Average Execution Time Changed"+ indent + indent+ indent + indent+ "Average Deadline");
		Log.printLine("Average Task Length" + indent + indent+ indent + indent+ "Average Makespan" + indent + indent+ indent + indent+ "Average Execution Time Changed");
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Migration OUTPUTs ==========");
		//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Idle Energy Consumption" +indent+ "Total Idle Time" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
		Log.printLine("Total Failures Occurred" + indent + indent + indent + indent+ "Total VM Migrations " + indent + indent + indent + indent+ "Total VM Consolidations ");
		Log.printLine(indent + indent + totalFailureCount + indent + indent + indent + indent + indent + indent+ totalVmMigrations+ indent + indent + indent + indent + indent + indent+ totalVmConsolidations);
		Log.printLine("Total Failures Occurred" + indent + indent + indent + indent+ "Total VM Migrations " + indent + indent + indent + indent+ "Total VM Consolidations ");
		
		Log.printLine("========== Migration OUTPUTs ==========");
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Overhead OUTPUTs ==========");
		//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Idle Energy Consumption" +indent+ "Total Idle Time" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
		Log.printLine("Total DownTime" + indent + indent + indent + indent+ indent + indent+ "Total Reexecution Time "+ indent + indent + indent + indent+ indent + indent+ "Host Count " );
		Log.printLine(indent  + dft.format(finalTotalDownTime/3600) + indent + indent + indent + indent+ dft.format(finalTotalReexecutionTime/3600)+ indent + indent + indent + indent+ hostList.size());
		Log.printLine();
		Log.printLine(indent  + dft.format(finalTotalDownTime) + indent + indent + indent + indent+ dft.format(finalTotalReexecutionTime)+ indent + indent + indent + indent+ hostList.size());
		
		Log.printLine("Total DownTime" + indent + indent + indent + indent+ "Total Reexecution Time" + indent + indent + indent + indent+ indent + indent+ "Host Count ");
	
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Application Finishing Time ==========");
		Log.printLine(indent + indent + dft.format(applicationFinishingTime));
		Log.printLine();
		Log.printLine("========== Application Finishing Time ==========");
}


private static void printCloudletListRestartingwithMigration(List<Cloudlet> list, List<Host> hostList, FTAFileReaderGrid5000 ftaread, FailureDatacenterBroker broker, HostVMMappingReliability hvmRbl, List<Vm>newVmList, VmUtilization vmUtl, PowerModel pow){	
	
		Double initTime;	
		double totalMakeSpan = 0.0;
		Double changeinExecutionTime;
		Double totalDownTime;		
		int countDeadlineMissed = 0;
		int countDeadlineAchieved = 0;
		double totalReexecutionTime;	
		double applicationFinishingTime = 0.0;
		double totalDeadline = 0;		
		double totalMigrationDownTime = 0;
		double totalMigrationOverhead = 0;
		Vm vm;	
		
		int size = sufferedClets.size(); //For only suffered cloudlets and corresponding VMs. 
		Cloudlet cloudlet;		//Host host;
		
		System.out.println("Printing results for Restarting with Migration Fault Tolerance Mechanism");
		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
				"Data center ID" + indent + indent + "VM ID" + indent +  indent + "Bag ID" + indent +  indent +"Cloudlet Initial Length" + indent + "Intial Start Time" + indent + indent +"Finish Time" +indent+ indent+ "Turnaround Time" +indent+ indent+ "Deadline Value" +indent+ indent+ "Deadline Actual Value"+indent+ indent+ "Utility Value"+indent+ indent +"Deadline");
		
		DecimalFormat dft = new DecimalFormat("###.####");			
		
		for (int i = 0; i < size; i++) {		
			cloudlet = sufferedClets.get(i);			
			double cloudletInitialLength = 0.0;			
			vm = VmCloudletMapping.getVm(cloudlet);
			int bagID;
			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS){
				Log.print(indent + cloudlet.getCloudletId() + indent + indent);
				Log.print("SUCCESS");			
				initTime = broker.getcloudletInitialStartTime(cloudlet);		
				if(cloudletTableBackup.containsKey(cloudlet.getCloudletId())){						
					cloudletInitialLength = ((double)cloudletTableBackup.get(cloudlet.getCloudletId()))/1000;
				}		
					
				double deadline;			
				double utilityValue;
				double finishedTime;
				double downTime;
				double downTimeforMigration;
				double reExecutionTime;
				//double migrationOverheads;
				double turnAroundTime;				
				bagID = BagTaskMapping.getBagID(cloudlet.getCloudletId());				
 				downTime = DownTimeTable.getCloudletDownTime(cloudlet.getCloudletId());				
				downTimeforMigration = DownTimeTable.getCloudletDownTimeforMigration(cloudlet.getCloudletId());				
				downTime = downTime + downTimeforMigration;				
				reExecutionTime = totalReexecutionTime = ((double)CloudletReexecutionPart.getCletReexecutionTime(cloudlet.getCloudletId()))/1000;				
			//	migrationOverheads = MigrationOverheadTable.getCletMigrationOverhead(cloudlet);				
				finishedTime = cloudlet.getFinishTime();				
				turnAroundTime = finishedTime - initTime;				
				totalMakeSpan = totalMakeSpan + turnAroundTime;			
				finalTotalReexecutionTime = finalTotalReexecutionTime + reExecutionTime;				
				deadline = initTime + (stringencyFactor * (cloudletInitialLength));			
				if(finishedTime<=deadline){		
					utilityValue = 1;
				}
				else{					
					utilityValue = (1-((finishedTime-deadline)/deadline));
					if(utilityValue<0){
						utilityValue = 0;
					}
				}	
				jobUtilityValue = jobUtilityValue + utilityValue;				
				totalDeadline = totalDeadline + (deadline-initTime);		
				
				if(deadline > finishedTime){	
					countDeadlineAchieved = countDeadlineAchieved + 1;
				Log.printLine( indent + indent + cloudlet.getResourceId() + indent + indent + indent + indent + cloudlet.getVmId() +
						indent + indent + indent + indent + bagID +
						indent + indent + indent + indent + dft.format(cloudletInitialLength) +
						indent + indent + indent + indent + indent + dft.format(initTime) +						
						indent + indent + indent +  dft.format(finishedTime)+
						indent + indent + indent +  dft.format(turnAroundTime)+
						indent + indent + indent +  dft.format(deadline)+
						indent + indent + indent +  dft.format(deadline-initTime)+
						indent + indent + indent +  dft.format(utilityValue)+
						indent + indent + indent+ "Achieved" );
				}
				else{					
					countDeadlineMissed = countDeadlineMissed + 1;
					Log.printLine( indent + indent + cloudlet.getResourceId() + indent + indent + indent + indent + cloudlet.getVmId() +
							indent + indent + indent + indent + bagID +
							indent + indent + indent + indent + dft.format(cloudletInitialLength) +
							indent + indent + indent + indent + indent + dft.format(initTime) +							
							indent + indent + indent +  dft.format(finishedTime)+
							indent + indent + indent +  dft.format(turnAroundTime)+
							indent + indent + indent +  dft.format(deadline)+
							indent + indent + indent +  dft.format(deadline-initTime)+
							indent + indent + indent +  dft.format(utilityValue)+
							indent + indent + indent+ "Missed" );
					}
				}
			}
		
		Log.printLine("========== OUTPUT ==========");
		
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
				"Data center ID" + indent + indent + "VM ID" + indent + indent + "Bag ID" + indent +  indent + "Cloudlet Initial Length" + indent + "Intial Start Time" + indent + indent +"Finish Time" +indent+ indent+ "Turnaround Time" +indent+ indent+ "Deadline Value" +indent+ indent+ "Deadline Actual Value"+indent+ indent+ "Utility Value"+indent+ indent +"Deadline");
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== OUTPUT For Bag Level Information ==========");
		//Log.printLine("Job ID" + indent + indent + indent + "Finishing Time");
		Log.printLine( "Finishing Time");
		int bagCount;
		int cletBagID;	
		bagCount = RunTimeConstants.numberofBags;
		for(int i=0; i<bagCount; i++){
			double jobFinishingTime = 0;
			double turnAroundTime = 0;
			for(int j=0; j<size; j++){
				cloudlet = sufferedClets.get(j);
				initTime = broker.getcloudletInitialStartTime(cloudlet);	
				turnAroundTime = cloudlet.getFinishTime() - initTime;
				cletBagID = BagTaskMapping.getBagID(cloudlet.getCloudletId());
				if(i == cletBagID){
					if(jobFinishingTime < turnAroundTime){
						jobFinishingTime = turnAroundTime;
					}
				}
			}
			if(applicationFinishingTime < jobFinishingTime){
				applicationFinishingTime = jobFinishingTime;
			}
			//Log.printLine( indent + i + indent + indent + indent + indent + dft.format(jobFinishingTime));
			Log.printLine( indent + dft.format(jobFinishingTime));
		}
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== OUTPUT For Bag Level Information ==========");
		///Log.printLine("Job ID" + indent + indent + indent + "Finishing Time");
		Log.printLine( "Finishing Time");
		
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== OUTPUT For Host Information ==========");
		Log.printLine("Host ID" + indent + "FTA Host ID" + indent +" MTBF" + indent + "MTTR" +indent+ "Max Hazard Rate" +indent+ "Current Hazard Rate" +indent+ "Host Power" +indent+ "Host Availability" +indent+ "Host Maintainability");
		
		int ftaHostID;	
		for(int i=0;i<HostMapping.getHostMapSize();i++){
			ftaHostID = HostMapping.getFTAhostID(i);
			
			Log.printLine(  i + indent + indent + ftaHostID +
					indent + indent + indent + indent + dft.format(ftaread.getMTBF(ftaHostID)) +
					indent + indent + indent + indent + dft.format(ftaread.getMTTR(ftaHostID)) +
					indent + indent + indent + indent + ftaread.getMaxHazardRate(ftaHostID)+
					indent + indent + indent + indent + ftaread.getCurrentHazardRate(ftaHostID)+
					indent + indent + indent + indent + dft.format(ftaread.getPowerforHost(ftaHostID))+
					indent + indent + indent + indent + dft.format(ftaread.getAvailability(ftaHostID))+
					indent + indent + indent + indent + dft.format(ftaread.getMaintainability(ftaHostID))
					);			
		}	
		
		Log.printLine("========== OUTPUT For Host Information ==========");
		Log.printLine("Host ID" + indent + "FTA Host ID" + indent +" MTBF" + indent + "MTTR" +indent+ "Max Hazard Rate" +indent+ "Current Hazard Rate" +indent+ "Host Power" +indent+ "Host Availability" +indent+ "Host Maintainability");
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== OUTPUT For Host Idle Time  ==========");
		Log.printLine("Host ID" +indent+ indent + indent + "Total Idle Time" +indent+ indent + indent + "Total Down Time" +indent+ indent + indent + "Total Idle Energy Consumption" +indent+ indent + indent + "Average Idle Energy Consumption");		
		
		int hostIdIdle;
		//double idleTimewithDownTime;
		double idleTime;
		double averageIdleTime;
		int peListSize;
		double idleUtilization = 0.0;
		double idlePower = 0.0;		
		double totalIdleEnergyConsumption = 0.0;
		double idleEnergyConsumption = 0.0;
		double averageIdleEnergyConsumption = 0.0;
		double totalAverageIdleEnergyConsumption = 0.0;
		double downTime;
		List<Pe>peList;
		for(int i=0; i<hostList.size(); i++){
			idleTime = 0.0;
			//idleTimewithDownTime = 0.0;
			downTime = 0.0;
			peList = new ArrayList<Pe>();
			ftaHostID = HostMapping.getFTAhostID(hostList.get(i).getId());
			idleUtilization = ftaread.getCurrentUtilization(ftaHostID);
			hostIdIdle = hostList.get(i).getId();			
			peListSize = hostList.get(i).getNumberOfPes();
			peList = hostList.get(i).getPeList();
			for(int j=0; j<peList.size(); j++){
				//idleTimewithDownTime = idleTimewithDownTime + IdleTime.getPeIdleTime(hostList.get(i).getId(), peList.get(j).getId());
				idleTime = idleTime + IdleTime.getPeIdleTime(hostList.get(i).getId(), peList.get(j).getId());
				downTime = downTime + IdleTime.getPeDownTime(hostList.get(i).getId(), peList.get(j).getId());
			}			
			//averageIdleTime = idleTime/peListSize;
			//idleTime = idleTimewithDownTime - downTime;
			averageIdleTime = idleTime;
			idlePower = pow.getPower(idleUtilization, peListSize);
			idleEnergyConsumption = ((idlePower/3600) * (averageIdleTime/1000));
			averageIdleEnergyConsumption = idleEnergyConsumption/peListSize;
			totalIdleEnergyConsumption = totalIdleEnergyConsumption + idleEnergyConsumption; 
			totalAverageIdleEnergyConsumption = totalAverageIdleEnergyConsumption + averageIdleEnergyConsumption;
			Log.printLine( indent+ hostIdIdle + indent + indent+ indent+ idleTime + indent + indent+ indent+ downTime + indent + indent+ indent+ idleEnergyConsumption+ indent + indent+ indent+ averageIdleEnergyConsumption);
		}
	
		Log.printLine("========== OUTPUT For Host Idle Time  ==========");
		Log.printLine("Host ID" +indent+ indent + indent + "Total Idle Time" +indent+ indent + indent + "Total Down Time" +indent+ indent + indent + "Total Idle Energy Consumption" +indent+ indent + indent + "Average Idle Energy Consumption" );		
			
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== OUTPUT For Energy Consumption Cloudlet Level==========");
		//Log.printLine("Cloudlet ID" + indent +" Task Execution Length" + indent + indent + "Total Finish Time" + indent + indent + indent + "Execution Time Changed" + indent + indent + indent +indent + "Total DownTime" + indent +indent + indent + "Total Migration Overheads" + indent +indent + indent + "Total Re-execution Time" +indent+indent + indent + "Energy Consumption" );
		Log.printLine("Cloudlet ID" + indent +" Task Execution Length" + indent + indent + "Total Finish Time" + indent + indent + indent + "Execution Time Changed" + indent + indent + indent +indent + "Total DownTime" + indent +indent + indent + "Total Migration Overheads" + indent +indent + indent + "Total Re-execution Time" +indent+indent + indent + "Energy Consumption without Reexecution " +indent+indent + indent + "Energy Consumption with Reexecution ");
		
		double totalCletLength = 0.0;
		double totalExecutionTimeChanged = 0.0;		
		double cloudletInitialLength;
		double turnAroundTime;
		double reexecutionCletLength;	
		
		ArrayList<Integer>hostIDListCletMigrated;
		ArrayList<Host>hostListCletMigrated;
		ArrayList<Double>hostListCletExecutedSoFar;		
		ArrayList<Host>hostListCletMigrationDownTime;
		ArrayList<Integer>hostIDListCletMigrationDownTime;
		ArrayList<Double>cletMigrationDownTime;
		ArrayList<Double>cletMigrationOverheads;
		
		HashMap<Integer, Double>cletEnergyConsumption = new HashMap<Integer, Double>();
		HashMap<Integer, Double>cletEnergyConsumptionWithoutReexecution = new HashMap<Integer, Double>();
		HashMap<Integer, Double>cletEnergyConsumptionWithMigrationDownTime = new HashMap<Integer, Double>();
		HashMap<Integer, Double>cletEnergyWastageWithMigrationDownTime = new HashMap<Integer, Double>();
		HashMap<Integer, Double>cletEnergyWastageWithReexecution = new HashMap<Integer, Double>();
		HashMap<Integer, Double>vmEnergyConsumptionTable = new HashMap<Integer, Double>();
		HashMap<Integer, Double>vmEnergyConsumptionTablewithReexecution = new HashMap<Integer, Double>();
		
		
		//int lastHostIDforClet;
		//Host lastHostforClet = null;
		double vmUtilization;
	
		for(int i=0; i<size; i++){			
			hostIDListCletMigrated = new ArrayList<Integer>();
			hostListCletMigrated = new ArrayList<Host>();
			cloudletInitialLength = 0.0;
			initTime = 0.0;
			turnAroundTime = 0.0;
			reexecutionCletLength = 0.0;
			changeinExecutionTime = 0.0;
			totalDownTime = 0.0;			
			vm = VmCloudletMapping.getVm(sufferedClets.get(i));
			initTime = broker.getcloudletInitialStartTime(sufferedClets.get(i));	
			Log.print(indent + sufferedClets.get(i).getCloudletId() + indent + indent);	
			if(cloudletTableBackup.containsKey(sufferedClets.get(i).getCloudletId())){		
				cloudletInitialLength = ((double)cloudletTableBackup.get(sufferedClets.get(i).getCloudletId()))/1000;			
				totalCletLength = totalCletLength + cloudletInitialLength;
				}
			turnAroundTime = sufferedClets.get(i).getFinishTime() - initTime;
			changeinExecutionTime = turnAroundTime - cloudletInitialLength;
			
			totalExecutionTimeChanged = totalExecutionTimeChanged + changeinExecutionTime;
			
			vmUtilization = vmUtl.getVmAverageUtilization(vm.getId());
			hostIDListCletMigrated = vmRecord.getVmMigrationHostList(sufferedClets.get(i).getCloudletId());
			hostListCletExecutedSoFar = vmRecord.getVmExecutionDurationPerHostList(sufferedClets.get(i).getCloudletId());
			
			for(int k=0; k<hostIDListCletMigrated.size(); k++){
				for(int l=0; l<hostList.size(); l++){
					if(hostList.get(l).getId()==hostIDListCletMigrated.get(k)){
						hostListCletMigrated.add(hostList.get(l));
						break;
					}
				}
			}
			double power;
			double cletEnergy = 0.0;
			double cletReexecutionEnergy = 0.0;
			//double energyWastageforReexecution;
			double cletDownTimePerHost;	
			double cletReexecutionPerHost;
			double cletExecutionPerHost;
			double energyConsumptionwithoutReexecution;
			double actualCletEnergyConsumption;
			int coreCount;
			downTime = 0.0;
			
			for(int j=0; j<hostIDListCletMigrated.size(); j++){				
				coreCount = hostListCletMigrated.get(j).getNumberOfPes();
				power = pow.getPower(vmUtilization, coreCount);
			//	power = HostVMMapping.gethostVMMapPower(hostListCletMigrated.get(j), vm);
				cletDownTimePerHost = DownTimeTable.getCletPerHostDownTimeTable(sufferedClets.get(i).getCloudletId(), hostListCletMigrated.get(j).getId());
				cletReexecutionPerHost = CloudletReexecutionPart.getCletPerHostReexecutionTimeTable(sufferedClets.get(i).getCloudletId(), hostListCletMigrated.get(j).getId());
				cletExecutionPerHost = (hostListCletExecutedSoFar.get(j)/1000) + (cletReexecutionPerHost/1000) - cletDownTimePerHost;
				downTime = downTime + cletDownTimePerHost;
				cletEnergy = cletEnergy + ((power/1000)*(cletExecutionPerHost/3600));
				cletReexecutionEnergy = cletReexecutionEnergy + ((power/1000)*(cletReexecutionPerHost/3600000));
				reexecutionCletLength = reexecutionCletLength + (cletReexecutionPerHost/1000);
			}	
			
			hostIDListCletMigrationDownTime = new ArrayList<Integer>();
			hostListCletMigrationDownTime = new ArrayList<Host>();
			cletMigrationDownTime = new ArrayList<Double>();
			cletMigrationOverheads = new ArrayList<Double>();			
			
			hostIDListCletMigrationDownTime = vmRecord.getCletMigrationHostRecordforDownTime(sufferedClets.get(i).getCloudletId());
			cletMigrationOverheads = vmRecord.getVmMigrationOverheadPerHostList(sufferedClets.get(i).getCloudletId());
			
			double cletMigrationOverheadEnergyPerHost = 0.0; 
			double cletMigrationOverheadEnergy = 0.0;
			double totalMigrationOverheads = 0.0;
			
			if(hostIDListCletMigrationDownTime != null){
				for(int k=0; k<hostIDListCletMigrationDownTime.size(); k++){
					for(int l=0; l<hostList.size(); l++){
						if(hostList.get(l).getId()==hostIDListCletMigrationDownTime.get(k)){
							hostListCletMigrationDownTime.add(hostList.get(l));
							break;
						}
					}
				}			
			
				for(int j=0; j<hostIDListCletMigrationDownTime.size(); j++){	
					coreCount = hostListCletMigrationDownTime.get(j).getNumberOfPes();
					power = pow.getPower(vmUtilization, coreCount);
					cletMigrationOverheadEnergyPerHost = (((cletMigrationOverheads.get(j))/3600)*(power/1000));
					totalMigrationOverheads = totalMigrationOverheads + cletMigrationOverheads.get(j);
					cletMigrationOverheadEnergy = cletMigrationOverheadEnergy + cletMigrationOverheadEnergyPerHost;			
				}
			
				cletEnergy = cletEnergy - cletMigrationOverheadEnergy;
			}
			cletEnergyConsumption.put(sufferedClets.get(i).getCloudletId(), cletEnergy);
			cletEnergyWastageWithReexecution.put(sufferedClets.get(i).getCloudletId(), cletReexecutionEnergy);
			
			/**
			lastHostIDforClet = vmRecord.getLastHostforClet(sufferedClets.get(i).getCloudletId());
			
			for(int j=0; j<hostIDListCletMigrated.size(); j++){
				if(hostListCletMigrated.get(j).getId()==lastHostIDforClet){
					lastHostforClet = hostListCletMigrated.get(j);
					break;
				}
			}
			
			coreCount = lastHostforClet.getNumberOfPes();
			//power = HostVMMapping.gethostVMMapPower(lastHostforClet, vm);
			power = pow.getPower(vmUtilization, coreCount);
			reexecutionCletLength = CloudletReexecutionPart.getCletReexecutionTime(sufferedClets.get(i).getCloudletId())/1000;
			energyWastageforReexecution = ((power/1000) * (reexecutionCletLength/3600));
			cletEnergyWastageWithReexecution.put(sufferedClets.get(i).getCloudletId(), energyWastageforReexecution);
			
			energyConsumptionwithoutReexecution = cletEnergy - energyWastageforReexecution;
			*/
			energyConsumptionwithoutReexecution = cletEnergy - cletReexecutionEnergy;
			cletEnergyConsumptionWithoutReexecution.put(sufferedClets.get(i).getCloudletId(), energyConsumptionwithoutReexecution);		
		
			cletMigrationDownTime = vmRecord.getVmMigrationDownTimePerHostList(sufferedClets.get(i).getCloudletId());		
			
			double cletMigrationDownTimeEnergyPerHost = 0.0;
			double cletMigrationDownTimeEnergy = 0.0;
			double migrationDownTime = 0.0;
			double actualCletEnergyConsumptionWithReexecution = 0.0;
			
			if(cletMigrationDownTime!=null){
				for(int j=0; j<hostIDListCletMigrationDownTime.size(); j++){
					ftaHostID = HostMapping.getFTAhostID(hostIDListCletMigrationDownTime.get(j));
					idleUtilization = ftaread.getCurrentUtilization(ftaHostID);
					coreCount = hostListCletMigrationDownTime.get(j).getNumberOfPes();
					power = pow.getPower(idleUtilization, coreCount);
					cletMigrationDownTimeEnergyPerHost = ((cletMigrationDownTime.get(j)/3600)*(power/1000));				
					cletMigrationDownTimeEnergy = cletMigrationDownTimeEnergy + cletMigrationDownTimeEnergyPerHost;		
					migrationDownTime = migrationDownTime + cletMigrationDownTime.get(j);
				}
				cletEnergyWastageWithMigrationDownTime.put(sufferedClets.get(i).getCloudletId(), cletMigrationDownTimeEnergy);
				actualCletEnergyConsumption = energyConsumptionwithoutReexecution + cletMigrationDownTimeEnergy;
				actualCletEnergyConsumptionWithReexecution = cletEnergy + cletMigrationDownTimeEnergy;
				cletEnergyConsumptionWithMigrationDownTime.put(sufferedClets.get(i).getCloudletId(), actualCletEnergyConsumption);
				totalDownTime = downTime + migrationDownTime;
				totalMigrationDownTime = totalMigrationDownTime + migrationDownTime;
			}
			else{
				actualCletEnergyConsumption = energyConsumptionwithoutReexecution;
				actualCletEnergyConsumptionWithReexecution = cletEnergy;
				totalDownTime = downTime;
			}		
			
			finalTotalDownTime = finalTotalDownTime + totalDownTime;
			
			if(vmEnergyConsumptionTable.isEmpty()){
				vmEnergyConsumptionTable.put(vm.getId(), actualCletEnergyConsumption);
				vmEnergyConsumptionTablewithReexecution.put(vm.getId(), actualCletEnergyConsumptionWithReexecution);				
			}
			else
			{				
				double tempVmEnergy;
				double tempVmEnergywithReexecution;
				if(vmEnergyConsumptionTable.containsKey(vm.getId())){
					tempVmEnergy = vmEnergyConsumptionTable.get(vm.getId());
					tempVmEnergywithReexecution = vmEnergyConsumptionTablewithReexecution.get(vm.getId());
					tempVmEnergy = tempVmEnergy + actualCletEnergyConsumption;
					tempVmEnergywithReexecution = tempVmEnergywithReexecution + actualCletEnergyConsumptionWithReexecution;
					vmEnergyConsumptionTable.put(vm.getId(), tempVmEnergy);
					vmEnergyConsumptionTablewithReexecution.put(vm.getId(), tempVmEnergywithReexecution);
				}
				else{
					vmEnergyConsumptionTable.put(vm.getId(), actualCletEnergyConsumption);
					vmEnergyConsumptionTablewithReexecution.put(vm.getId(), actualCletEnergyConsumptionWithReexecution);
				}				
			}
			totalMigrationOverhead = totalMigrationOverhead + totalMigrationOverheads;
			
			Log.printLine( indent + indent + dft.format(cloudletInitialLength)+
						indent + indent  + indent + indent + dft.format(turnAroundTime)+
						indent + indent + indent + indent + dft.format(changeinExecutionTime)+
						indent + indent + indent + indent + indent + dft.format(totalDownTime)+
						indent + indent + indent + indent + dft.format(totalMigrationOverheads)+
						indent + indent + indent  + indent + indent + dft.format(reexecutionCletLength)+						
						indent + indent + indent + indent +dft.format(actualCletEnergyConsumption)+
						indent + indent + indent + indent +indent + indent + indent +dft.format(actualCletEnergyConsumptionWithReexecution));		
		}		
		
		
		Log.printLine("========== OUTPUT For Energy Consumption Cloudlet Level ==========");
		Log.printLine("Cloudlet ID" + indent +" Task Execution Length" + indent + indent + "Total Finish Time" + indent + indent + indent + "Execution Time Changed" + indent + indent + indent +indent + "Total DownTime" + indent +indent + indent + "Total Migration Overheads" + indent +indent + indent + "Total Re-execution Time" +indent+indent + indent + "Energy Consumption without Reexecution " +indent+indent + indent + "Energy Consumption with Reexecution ");
	
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== OUTPUT For Energy Wastage Cloudlet Level ==========");
		//Log.printLine("Cloudlet ID" + indent + "VM ID" + indent + "Cloudlet Initial Length" +indent+ "Energy Wastage");
		Log.printLine("Cloudlet ID" + indent + indent + "Reexecution Energy" + indent + indent + "Migration DownTime Energy" + indent + indent + "Total Energy Wastage");
		
		double reexecutionEnergy = 0.0;
		double migrationDownTimeEnergy = 0.0;
		double totalCletEnergyWastage = 0.0;
		HashMap<Integer, Double>vmEnergyWastageTable = new HashMap<Integer, Double>();
		
		for (int i = 0; i < size; i++) {				
			cloudlet = sufferedClets.get(i);
			vm = VmCloudletMapping.getVm(sufferedClets.get(i));
			if(cletEnergyWastageWithReexecution.containsKey(cloudlet.getCloudletId())){
				reexecutionEnergy = cletEnergyWastageWithReexecution.get(cloudlet.getCloudletId());
			}
			else{
				reexecutionEnergy = 0.0;
			}
			if(cletEnergyWastageWithMigrationDownTime.containsKey(cloudlet.getCloudletId())){
				migrationDownTimeEnergy = cletEnergyWastageWithMigrationDownTime.get(cloudlet.getCloudletId());
			}
			else{
				migrationDownTimeEnergy = 0.0;
			}
			
			totalCletEnergyWastage = reexecutionEnergy + migrationDownTimeEnergy;	
			
			if(vmEnergyWastageTable.isEmpty()){
				vmEnergyWastageTable.put(vm.getId(), totalCletEnergyWastage);
			}
			else
			{				
				double tempVmEnergyWastage;				
				if(vmEnergyWastageTable.containsKey(vm.getId())){
					tempVmEnergyWastage = vmEnergyWastageTable.get(vm.getId());
					tempVmEnergyWastage = tempVmEnergyWastage + totalCletEnergyWastage;
					vmEnergyWastageTable.put(vm.getId(), tempVmEnergyWastage);
				}
				else{
					vmEnergyWastageTable.put(vm.getId(), totalCletEnergyWastage);
				}				
			}			
			
			Log.printLine(indent + cloudlet.getCloudletId()  
						  +indent + indent +indent + indent + dft.format(reexecutionEnergy)
						  +indent + indent +indent + indent + dft.format(migrationDownTimeEnergy)
						  +indent + indent +indent + indent + dft.format(totalCletEnergyWastage));			
			
		}
	
		Log.printLine("========== OUTPUT For Energy Wastage Cloudlet Level ==========");
		//Log.printLine("Cloudlet ID" + indent + "VM ID" + indent + "Cloudlet Initial Length" +indent+ "Energy Wastage");
		Log.printLine("Cloudlet ID"  + indent + indent + "Reexecution Energy" + indent + indent + "Migration DownTime Energy" + indent + indent + "Total Energy Wastage");

		FileWriter HostCountClock = null;
		FileWriter HostCount = null;
		Log.printLine();
		Log.printLine();
		Log.printLine("========== OUTPUT For Host Count ==========");	
		Log.printLine(indent + "Clock" + indent + indent + indent + "Host Count" );
	//	try{
	//	HostCountClock = new FileWriter("/raid/disc1/ysharma1/Java/Results/Simulation_File/HostCountClock.txt", false);
	//	HostCount = new FileWriter("/raid/disc1/ysharma1/Java/Results/Simulation_File/HostCount.txt", false);
		ArrayList<Integer>hostCount = new ArrayList<Integer>();
		ArrayList<Double>clockForHostCount = new ArrayList<Double>();
		hostCount = HostCounter.getHostCountList();
		clockForHostCount = HostCounter.getClockList();
		for(int i=0;i<hostCount.size();i++){
		//	HostCountClock.write((clockForHostCount.get(i))+System.getProperty("line.seperator"));
		//	HostCount.write((hostCount.get(i))+System.getProperty("line.seperator"));
			Log.printLine(indent + clockForHostCount.get(i)  
			  +indent + indent +indent + hostCount.get(i)
			  );
			}
	//	HostCountClock.close();
	//	HostCount.close();
		
	//	}catch(IOException e){
	//		System.out.println("Error has been occurred while writing output for host count");
	//	}
		Log.printLine("========== OUTPUT For Host Count ==========");		
		Log.printLine(indent + "Clock" + indent + indent + indent + "Host Count" );
		
		
		Log.printLine();
		Log.printLine();
		//Log.printLine(indent + "Total Energy Consumption" + indent + indent + indent + "Total Energy Consumption with Reexecution"+ indent + indent + indent + "Total Energy Wastage" + indent + indent + indent + "Total Idle Energy Consumption"  +indent +indent + indent + "Application Utility Value" );
		Log.printLine(indent + "Total Energy Consumption" + indent + indent + indent + "Total Energy Wastage" + indent + indent + indent + "Total Idle Energy Consumption"  +indent +indent + indent + "Application Utility Value" );
		
			double totalReliability;	
			double totalReliabilityParallel = 0.0;
			double totalReliabilityAverage = 0.0;
			double totalAvailability = 0.0;
			double totalMaintainability = 0.0;
			double percentageAchievedDeadlines;
			//ArrayList<Double>energyConsumptionperHost = new ArrayList<Double>();		
			totalReliability = 0.0;		
			int systemReliabilityListSize;
			ArrayList<Double>systemReliabilityList = new ArrayList<Double>();
			ArrayList<Double>systemReliabilityListParallel = new ArrayList<Double>();
			ArrayList<Double>systemReliabilityListAverage = new ArrayList<Double>();
			ArrayList<Double>systemAvailabilityList = new ArrayList<Double>();
			ArrayList<Double>systemMaintainabilityList = new ArrayList<Double>();
			systemReliabilityList.addAll(hvmRbl.getSystemReliability());
			systemReliabilityListSize = hvmRbl.getSystemReliability().size();
			systemReliabilityListParallel.addAll(hvmRbl.getSystemReliabilityParallel());
			systemReliabilityListAverage.addAll(hvmRbl.getSystemReliabilityAverage());
			systemAvailabilityList.addAll(hvmRbl.getSystemAvailability());
			systemMaintainabilityList.addAll(hvmRbl.getSystemMaintainability());
			for(int i=0;i<systemReliabilityListSize;i++){
				totalReliability = totalReliability + systemReliabilityList.get(i);
				totalReliabilityParallel = totalReliabilityParallel + systemReliabilityListParallel.get(i);
				totalReliabilityAverage = totalReliabilityAverage + systemReliabilityListAverage.get(i);
				totalAvailability = totalAvailability + systemAvailabilityList.get(i);
				totalMaintainability = totalMaintainability + systemMaintainabilityList.get(i);
			//	AverageSystemReliability.write((dft.format(systemReliabilityListAverage.get(i)))+ System.getProperty( "line.separator"));
			}
			totalReliability = totalReliability/systemReliabilityListSize;
			totalReliabilityParallel = totalReliabilityParallel/systemReliabilityListSize;
			totalReliabilityAverage = totalReliabilityAverage/systemReliabilityListSize;
			totalAvailability = (totalAvailability/systemReliabilityListSize)*100;
			totalMaintainability = (totalMaintainability/systemReliabilityListSize)*100;			
			
			double totalEnergyConsumption = 0.0;		
			double totalEnergyConsumptionwithReexecution = 0.0;
			double totalEnergyWastage = 0.0;			
			for(int i=0; i<newVmList.size();i++){
				//totalEnergyConsumption = totalEnergyConsumption + vmEnergyConsumptionTable.get(newVmList.get(i).getId());
				totalEnergyConsumption = totalEnergyConsumptionwithReexecution = totalEnergyConsumptionwithReexecution + vmEnergyConsumptionTablewithReexecution.get(newVmList.get(i).getId());
				totalEnergyWastage = totalEnergyWastage + vmEnergyWastageTable.get(newVmList.get(i).getId());
				
			}
			
			jobUtilityValue = jobUtilityValue/size;			
		
			percentageAchievedDeadlines = (((double)countDeadlineAchieved*100)/((double)countDeadlineAchieved+(double)countDeadlineMissed));
			
			Log.printLine( indent + indent + dft.format(totalEnergyConsumption)+	
				//	indent + indent + indent + indent + indent + indent + indent +dft.format(totalEnergyConsumptionwithReexecution)+
					indent + indent + indent + indent + indent + indent + indent + indent +dft.format(totalEnergyWastage)+
					indent + indent + indent + indent + indent + indent + indent +dft.format(totalIdleEnergyConsumption)+									
					indent + indent + indent + indent + indent + indent + indent +dft.format(jobUtilityValue));	
				
		Log.printLine("========== Total OUTPUT ==========");
		//Log.printLine(indent + "Total Energy Consumption" + indent + indent + indent + "Total Energy Consumption with Reexecution"+ indent + indent + indent + "Total Energy Wastage" + indent + indent + indent + "Total Idle Energy Consumption"  +indent +indent + indent + "Application Utility Value" );
		Log.printLine(indent + "Total Energy Consumption" + indent + indent + indent + "Total Energy Wastage" + indent + indent + indent + "Total Idle Energy Consumption"  +indent +indent + indent + "Application Utility Value" );
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Deadlines OUTPUT ==========");
		//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Idle Energy Consumption" +indent+ "Total Idle Time" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
		Log.printLine(indent + "Total Deadlines Achieved" + indent + indent + "Total Deadlines Missed" + indent + indent + "Percentage Achieved Deadlines");
		
		Log.printLine( indent + indent + indent + countDeadlineAchieved +
				indent + indent + indent +  indent + indent + indent + indent +countDeadlineMissed+
				indent + indent + indent + indent + indent + indent + indent + dft.format(percentageAchievedDeadlines));		
		
		Log.printLine("========== Deadlines OUTPUT ==========");
		//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Idle Energy Consumption" +indent+ "Total Idle Time" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
		Log.printLine(indent + "Total Deadlines Achieved" + indent + indent + "Total Deadlines Missed" + indent + indent + "Percentage Achieved Deadlines");
		
		
		
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Reliability OUTPUT ==========");
		//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Idle Energy Consumption" +indent+ "Total Idle Time" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
		Log.printLine(indent + "Total Reliability Serial" + indent + indent + "Total Reliability Parallel" + indent + indent + "Total Reliability Average" + indent + indent + "Total Availability" + indent + indent + "Total Maintainability");
		
		Log.printLine( indent + dft.format((totalReliability*100)) +
				indent + indent + indent + indent + indent + dft.format(totalReliabilityParallel*100)+
				indent + indent + indent + indent + indent + indent + indent + dft.format((totalReliabilityAverage*100))+
				indent + indent + indent + indent + indent + dft.format(totalAvailability)+
				indent + indent + indent + indent + indent + dft.format(totalMaintainability));		
		
		Log.printLine("========== Reliability OUTPUT ==========");
		//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Idle Energy Consumption" +indent+ "Total Idle Time" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
		Log.printLine(indent + "Total Reliability Serial" + indent + indent + "Total Reliability Parallel" + indent + indent + "Total Reliability Average" + indent + indent + "Total Availability" + indent + indent + "Total Maintainability");
		
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average OUTPUTs ==========");
		Log.printLine("Average Task Length" + indent + indent+ indent + indent+ "Average Makespan" + indent + indent+ indent + indent+ "Average Execution Time Changed");
				
		double averageCletCompletionTime;
		double averageCletLength;
		double averageExecutionTimeChanged;		
		averageCletCompletionTime = totalMakeSpan/size;
		averageCletLength = totalCletLength/size;
		averageExecutionTimeChanged = totalExecutionTimeChanged/size;		
		Log.printLine( indent +dft.format(averageCletLength/3600) + indent + indent + indent + indent +indent +dft.format(averageCletCompletionTime/3600) + indent + indent + indent + indent + indent +dft.format(averageExecutionTimeChanged/3600));
		Log.printLine();
		Log.printLine( indent +dft.format(averageCletLength) + indent + indent + indent + indent +indent +dft.format(averageCletCompletionTime) + indent + indent + indent + indent + indent +dft.format(averageExecutionTimeChanged));
		
		Log.printLine("========== Average OUTPUTs ==========");
		Log.printLine("Average Task Length" + indent + indent+ indent + indent+ "Average Makespan" + indent + indent+ indent + indent+ "Average Execution Time Changed"+ indent + indent+ indent + indent+ "Average Deadline");
		
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Migration OUTPUTs ==========");
		//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Idle Energy Consumption" +indent+ "Total Idle Time" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
		if(RunTimeConstants.vmConsolidationFlag == false){
			Log.printLine("Total Failures Occurred" + indent + indent + indent + indent+ "Total VM Migrations " );
			Log.printLine(indent + indent + totalFailureCount + indent + indent + indent + indent + indent + indent+ totalVmMigrations);
			Log.printLine("Total Failures Occurred" + indent + indent + indent + indent+ "Total VM Migrations " );
		}else{
			Log.printLine("Total Failures Occurred" + indent + indent + indent + indent+ "Total VM Migrations " + indent + indent + indent + indent+ "Total VM Consolidations ");
			Log.printLine(indent + indent + totalFailureCount + indent + indent + indent + indent + indent + indent+ indent + indent + indent+ totalVmMigrations + indent + indent + indent + indent+ indent+ indent + indent + indent+ totalVmConsolidations);
			Log.printLine("Total Failures Occurred" + indent + indent + indent + indent+ "Total VM Migrations " + indent + indent + indent + indent+ "Total VM Consolidations ");
		}	
		
		Log.printLine("========== Migration OUTPUTs ==========");
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Overhead OUTPUTs ==========");
		//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Idle Energy Consumption" +indent+ "Total Idle Time" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
		Log.printLine("Total DownTime" + indent + indent + indent + indent+ "Total Reexecution Time " + indent + indent + indent + indent+ "Total Hosts "  + indent + indent + indent + indent+ "Total Migration DownTime "  + indent + indent + indent + indent+ "Total Migration Overhead ");		
		Log.printLine(indent  + dft.format((finalTotalDownTime/3600)) + indent + indent + indent + indent+ dft.format((finalTotalReexecutionTime/3600))+ indent + indent + indent + indent+ hostList.size()+ indent + indent + indent + indent+ dft.format(totalMigrationDownTime/3600) + indent + indent + indent + indent+ dft.format(totalMigrationOverhead/3600));
		Log.printLine();
		Log.printLine(indent  + dft.format((finalTotalDownTime)) + indent + indent + indent + indent+ dft.format((finalTotalReexecutionTime))+ indent + indent + indent + indent+ hostList.size()+ indent + indent + indent + indent+ dft.format(totalMigrationDownTime) + indent + indent + indent + indent+ dft.format(totalMigrationOverhead));		
		Log.printLine("Total DownTime" + indent + indent + indent + indent+ "Total Reexecution Time" + indent + indent + indent + indent+ "Total Hosts "  + indent + indent + indent + indent+ "Total Migration DownTime "  + indent + indent + indent + indent+ "Total Migration Overhead ");
		
		Log.printLine("========== Overhead OUTPUTs ==========");	
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Application Finishing Time ==========");
		
		Log.printLine(indent + indent + dft.format(applicationFinishingTime));
		
		Log.printLine();
		Log.printLine("========== Application Finishing Time ==========");
		
	
}


private static void printCloudletListCheckpointingwithMigration(List<Cloudlet> list, List<Host> hostList, FTAFileReaderGrid5000 ftaread, FailureDatacenterBroker broker, HostVMMappingReliability hvmRbl, List<Vm>newVmList, VmUtilization vmUtl, PowerModel pow){
	Double initTime;		
	
	double totalMakeSpan = 0.0;
	Double changeinExecutionTime;
	Double totalDownTime;		
	int countDeadlineMissed = 0;
	int countDeadlineAchieved = 0;
	double totalReexecutionTime;	
	double totalDeadline = 0;		
	double totalMigrationDownTime = 0;
	double totalMigrationOverhead = 0;
	double applicationFinishingTime = 0.0;
	Vm vm;	
	
	int size = sufferedClets.size(); //For only suffered cloudlets and corresponding VMs. 
	Cloudlet cloudlet;		//Host host;
	
	System.out.println("Printing results for Restarting with Migration Fault Tolerance Mechanism");
	String indent = "    ";
	Log.printLine();
	Log.printLine("========== OUTPUT ==========");
	
	Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
			"Data center ID" + indent + indent + "VM ID" + indent +  indent + "Bag ID" + indent +  indent +"Cloudlet Initial Length" + indent + "Intial Start Time" + indent + indent +"Finish Time" +indent+ indent+ "Turnaround Time" +indent+ indent+ "Deadline Value" +indent+ indent+ "Deadline Actual Value"+indent+ indent+ "Utility Value"+indent+ indent +"Deadline");
	
	DecimalFormat dft = new DecimalFormat("###.####");			
	
	for (int i = 0; i < size; i++) {		
		cloudlet = sufferedClets.get(i);			
		double cloudletInitialLength = 0.0;			
		vm = VmCloudletMapping.getVm(cloudlet);
		int bagID;
		if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS){
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);
			Log.print("SUCCESS");			
			initTime = broker.getcloudletInitialStartTime(cloudlet);		
			if(cloudletTableBackup.containsKey(cloudlet.getCloudletId())){						
				cloudletInitialLength = ((double)cloudletTableBackup.get(cloudlet.getCloudletId()))/1000;
			}		
				
			double deadline;			
			double utilityValue;
			double finishedTime;
			//double downTime;
			//double downTimeforMigration;
			double reExecutionTime;
		//	double migrationOverheads;
			double turnAroundTime;				
			bagID = BagTaskMapping.getBagID(cloudlet.getCloudletId());				
			//downTime = DownTimeTable.getCloudletDownTime(cloudlet.getCloudletId());				
			//downTimeforMigration = DownTimeTable.getCloudletDownTimeforMigration(cloudlet.getCloudletId());				
			//downTime = downTime + downTimeforMigration;				
			reExecutionTime = totalReexecutionTime = ((double)CloudletReexecutionPart.getCletReexecutionTime(cloudlet.getCloudletId()))/1000;				
		//	migrationOverheads = MigrationOverheadTable.getCletMigrationOverhead(cloudlet.getCloudletId());				
			finishedTime = cloudlet.getFinishTime();				
			turnAroundTime = finishedTime - initTime;				
			totalMakeSpan = totalMakeSpan + turnAroundTime;			
			finalTotalReexecutionTime = finalTotalReexecutionTime + reExecutionTime;				
			deadline = initTime + (stringencyFactor * (cloudletInitialLength));			
			if(finishedTime<=deadline){		
				utilityValue = 1;
			}
			else{					
				utilityValue = (1-((finishedTime-deadline)/deadline));
				if(utilityValue<0){
					utilityValue = 0;
				}
			}	
			jobUtilityValue = jobUtilityValue + utilityValue;				
			totalDeadline = totalDeadline + (deadline-initTime);		
			
			if(deadline > finishedTime){	
				countDeadlineAchieved = countDeadlineAchieved + 1;
			Log.printLine( indent + indent + cloudlet.getResourceId() + indent + indent + indent + indent + cloudlet.getVmId() +
					indent + indent + indent + indent + bagID +
					indent + indent + indent + indent + dft.format(cloudletInitialLength) +
					indent + indent + indent + indent + indent + dft.format(initTime) +						
					indent + indent + indent +  dft.format(finishedTime)+
					indent + indent + indent +  dft.format(turnAroundTime)+
					indent + indent + indent +  dft.format(deadline)+
					indent + indent + indent +  dft.format(deadline-initTime)+
					indent + indent + indent +  dft.format(utilityValue)+
					indent + indent + indent+ "Achieved" );
			}
			else{					
				countDeadlineMissed = countDeadlineMissed + 1;
				Log.printLine( indent + indent + cloudlet.getResourceId() + indent + indent + indent + indent + cloudlet.getVmId() +
						indent + indent + indent + indent + bagID +
						indent + indent + indent + indent + dft.format(cloudletInitialLength) +
						indent + indent + indent + indent + indent + dft.format(initTime) +							
						indent + indent + indent +  dft.format(finishedTime)+
						indent + indent + indent +  dft.format(turnAroundTime)+
						indent + indent + indent +  dft.format(deadline)+
						indent + indent + indent +  dft.format(deadline-initTime)+
						indent + indent + indent +  dft.format(utilityValue)+
						indent + indent + indent+ "Missed" );
				}
			}
		}
	
	Log.printLine("========== OUTPUT ==========");
	
	Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
			"Data center ID" + indent + indent + "VM ID" + indent + indent + "Bag ID" + indent +  indent + "Cloudlet Initial Length" + indent + "Intial Start Time" + indent + indent +"Finish Time" +indent+ indent+ "Turnaround Time" +indent+ indent+ "Deadline Value" +indent+ indent+ "Deadline Actual Value"+indent+ indent+ "Utility Value"+indent+ indent +"Deadline");
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== OUTPUT For Bag Level Information ==========");
	//Log.printLine("Job ID" + indent + indent + indent + "Finishing Time");
	Log.printLine( "Finishing Time");
	int bagCount;
	int cletBagID;	
	bagCount = RunTimeConstants.numberofBags;
	for(int i=0; i<bagCount; i++){
		double jobFinishingTime = 0;
		double turnAroundTime = 0;
		for(int j=0; j<size; j++){
			cloudlet = sufferedClets.get(j);
			initTime = broker.getcloudletInitialStartTime(cloudlet);	
			turnAroundTime = cloudlet.getFinishTime() - initTime;
			cletBagID = BagTaskMapping.getBagID(cloudlet.getCloudletId());
			if(i == cletBagID){
				if(jobFinishingTime < turnAroundTime){
					jobFinishingTime = turnAroundTime;
				}
			}
		}
		if(applicationFinishingTime < jobFinishingTime){
			applicationFinishingTime = jobFinishingTime;
		}
		//Log.printLine( indent + i + indent + indent + indent + indent + dft.format(jobFinishingTime));
		Log.printLine( indent + dft.format(jobFinishingTime));
	}
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== OUTPUT For Bag Level Information ==========");
	///Log.printLine("Job ID" + indent + indent + indent + "Finishing Time");
	Log.printLine( "Finishing Time");	
	
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== OUTPUT For Host Information ==========");
	Log.printLine("Host ID" + indent + "FTA Host ID" + indent +" MTBF" + indent + "MTTR" +indent+ "Max Hazard Rate" +indent+ "Current Hazard Rate" +indent+ "Host Power" +indent+ "Host Availability" +indent+ "Host Maintainability");
	
	int ftaHostID;	
	for(int i=0;i<HostMapping.getHostMapSize();i++){
		ftaHostID = HostMapping.getFTAhostID(i);
		
		Log.printLine(  i + indent + indent + ftaHostID +
				indent + indent + indent + indent + dft.format(ftaread.getMTBF(ftaHostID)) +
				indent + indent + indent + indent + dft.format(ftaread.getMTTR(ftaHostID)) +
				indent + indent + indent + indent + ftaread.getMaxHazardRate(ftaHostID)+
				indent + indent + indent + indent + ftaread.getCurrentHazardRate(ftaHostID)+
				indent + indent + indent + indent + dft.format(ftaread.getPowerforHost(ftaHostID))+
				indent + indent + indent + indent + dft.format(ftaread.getAvailability(ftaHostID))+
				indent + indent + indent + indent + dft.format(ftaread.getMaintainability(ftaHostID))
				);			
	}	
	
	Log.printLine("========== OUTPUT For Host Information ==========");
	Log.printLine("Host ID" + indent + "FTA Host ID" + indent +" MTBF" + indent + "MTTR" +indent+ "Max Hazard Rate" +indent+ "Current Hazard Rate" +indent+ "Host Power" +indent+ "Host Availability" +indent+ "Host Maintainability");
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== OUTPUT For Host Idle Time  ==========");
	//Log.printLine("Host ID" +indent+ indent + indent + "Total Idle Time" +indent+ indent + indent + "Total Idle Energy Consumption" );		
	Log.printLine("Host ID" +indent+ indent + indent + "Total Idle Time" +indent+ indent + indent + "Total Down Time" +indent+ indent + indent + "Total Idle Energy Consumption" +indent+ indent + indent + "Average Idle Energy Consumption" );		

	
	int hostIdIdle;
	//double idleTimewithDownTime;
	double idleTime;
	double averageIdleTime;
	int peListSize;
	double idleUtilization = 0.0;
	double idlePower = 0.0;		
	double totalIdleEnergyConsumption = 0.0;
	double totalAverageIdleEnergyConsumption = 0.0;
	double averageIdleEnergyConsumption = 0.0;
	double idleEnergyConsumption = 0.0;
	double downTime;
	List<Pe>peList;
	for(int i=0; i<hostList.size(); i++){
		idleTime = 0.0;
	//	idleTimewithDownTime = 0.0;
		downTime = 0.0;
		peList = new ArrayList<Pe>();
		ftaHostID = HostMapping.getFTAhostID(hostList.get(i).getId());
		idleUtilization = ftaread.getCurrentUtilization(ftaHostID);
		hostIdIdle = hostList.get(i).getId();			
		peListSize = hostList.get(i).getNumberOfPes();
		peList = hostList.get(i).getPeList();
		for(int j=0; j<peList.size(); j++){
			idleTime = idleTime + IdleTime.getPeIdleTime(hostIdIdle, peList.get(j).getId());
			downTime = downTime + IdleTime.getPeDownTime(hostIdIdle, peList.get(j).getId());
		}			
		//averageIdleTime = idleTime/peListSize;
		//idleTime = idleTimewithDownTime - downTime;
		//idleTime = idleTimewithDownTime;
		averageIdleTime = idleTime;
		idlePower = pow.getPower(idleUtilization, peListSize);
		idleEnergyConsumption = ((idlePower/3600) * (averageIdleTime/1000));
		averageIdleEnergyConsumption = idleEnergyConsumption/peListSize;
		totalIdleEnergyConsumption = totalIdleEnergyConsumption + idleEnergyConsumption; 
		totalAverageIdleEnergyConsumption =totalAverageIdleEnergyConsumption + averageIdleEnergyConsumption;
		
		//Log.printLine( indent+ hostIdIdle + indent + indent+ indent+ idleTime + indent + indent+ indent+ idleEnergyConsumption);
		Log.printLine( indent+ hostIdIdle + indent + indent+ indent+ idleTime + indent + indent+ indent+ downTime + indent + indent+ indent+ idleEnergyConsumption+ indent + indent+ indent+ averageIdleEnergyConsumption);

	}

	Log.printLine("========== OUTPUT For Host Idle Time  ==========");
	//Log.printLine("Host ID" +indent+ indent + indent + "Total Idle Time" +indent+ indent + indent + "Total Idle Energy Consumption" );		
	Log.printLine("Host ID" +indent+ indent + indent + "Total Idle Time" +indent+ indent + indent + "Total Down Time" +indent+ indent + indent + "Total Idle Energy Consumption" +indent+ indent + indent + "Average Idle Energy Consumption" );		

		
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== OUTPUT For Energy Consumption Cloudlet Level==========");
	//Log.printLine("Cloudlet ID" + indent +" Task Execution Length" + indent + indent + "Total Finish Time" + indent + indent + indent + "Execution Time Changed" + indent + indent + indent +indent + "Total DownTime" + indent +indent + indent + "Total Migration Overheads" + indent +indent + indent + "Total Re-execution Time" +indent+indent + indent + "Energy Consumption" );
	Log.printLine("Cloudlet ID" + indent +" Task Execution Length" + indent + indent + "Total Finish Time" + indent + indent + indent + "Execution Time Changed" + indent + indent + indent +indent + "Total DownTime" + indent +indent + indent + "Total Migration Overheads" + indent +indent + indent + "Total Re-execution Time" + indent + indent + indent + "Energy Consumption without Reexecution" + indent + indent + indent + "Energy Consumption with Reexecution" );

	double totalCletLength = 0.0;
	double totalExecutionTimeChanged = 0.0;		
	double cloudletInitialLength;
	double turnAroundTime;
	double reexecutionCletLength;		
	
	ArrayList<Integer>hostIDListCletMigrated;
	ArrayList<Host>hostListCletMigrated;
	ArrayList<Double>hostListCletExecutedSoFar;		
	ArrayList<Host>hostListCletMigrationDownTime;
	ArrayList<Integer>hostIDListCletMigrationDownTime;
	ArrayList<Double>cletMigrationDownTime;
	ArrayList<Double>cletMigrationOverheads;
	
	HashMap<Integer, Double>cletEnergyConsumption = new HashMap<Integer, Double>();
	HashMap<Integer, Double>cletEnergyConsumptionWithoutReexecution = new HashMap<Integer, Double>();
	HashMap<Integer, Double>cletEnergyConsumptionWithMigrationDownTime = new HashMap<Integer, Double>();
	HashMap<Integer, Double>cletEnergyWastageWithMigrationDownTime = new HashMap<Integer, Double>();
	HashMap<Integer, Double>cletEnergyWastageWithReexecution = new HashMap<Integer, Double>();
	HashMap<Integer, Double>vmEnergyConsumptionTable = new HashMap<Integer, Double>();
	
	HashMap<Integer, Double>vmEnergyConsumptionTablewithReexecution = new HashMap<Integer, Double>();
	
	//int lastHostIDforClet;
	//Host lastHostforClet = null;
	double vmUtilization;

	for(int i=0; i<size; i++){			
		hostIDListCletMigrated = new ArrayList<Integer>();
		hostListCletMigrated = new ArrayList<Host>();
		cloudletInitialLength = 0.0;
		initTime = 0.0;
		turnAroundTime = 0.0;
		reexecutionCletLength = 0.0;
		changeinExecutionTime = 0.0;
		totalDownTime = 0.0;			
		vm = VmCloudletMapping.getVm(sufferedClets.get(i));
		initTime = broker.getcloudletInitialStartTime(sufferedClets.get(i));	
		Log.print(indent + sufferedClets.get(i).getCloudletId() + indent + indent);	
		if(cloudletTableBackup.containsKey(sufferedClets.get(i).getCloudletId())){		
			cloudletInitialLength = ((double)cloudletTableBackup.get(sufferedClets.get(i).getCloudletId()))/1000;			
			totalCletLength = totalCletLength + cloudletInitialLength;
			}
		turnAroundTime = sufferedClets.get(i).getFinishTime() - initTime;
		changeinExecutionTime = turnAroundTime - cloudletInitialLength;
		
		totalExecutionTimeChanged = totalExecutionTimeChanged + changeinExecutionTime;
		
		vmUtilization = vmUtl.getVmAverageUtilization(vm.getId());
		hostIDListCletMigrated = vmRecord.getVmMigrationHostList(sufferedClets.get(i).getCloudletId());
		hostListCletExecutedSoFar = vmRecord.getVmExecutionDurationPerHostList(sufferedClets.get(i).getCloudletId());
		
		for(int k=0; k<hostIDListCletMigrated.size(); k++){
			for(int l=0; l<hostList.size(); l++){
				if(hostList.get(l).getId()==hostIDListCletMigrated.get(k)){
					hostListCletMigrated.add(hostList.get(l));
					break;
				}
			}
		}
		double power;
		double cletEnergy = 0.0;
		double cletReexecutionEnergy = 0.0;
	//	double energyWastageforReexecution;
		double cletDownTimePerHost;	
		double cletReexecutionPerHost;
		double cletExecutionPerHost;
		double energyConsumptionwithoutReexecution;
		double actualCletEnergyConsumption;
		int coreCount;
		downTime = 0.0;	
		
		for(int j=0; j<hostIDListCletMigrated.size(); j++){				
			coreCount = hostListCletMigrated.get(j).getNumberOfPes();
			power = pow.getPower(vmUtilization, coreCount);
		//	power = HostVMMapping.gethostVMMapPower(hostListCletMigrated.get(j), vm);
			cletDownTimePerHost = DownTimeTable.getCletPerHostDownTimeTable(sufferedClets.get(i).getCloudletId(), hostListCletMigrated.get(j).getId());
			cletReexecutionPerHost = CloudletReexecutionPart.getCletPerHostReexecutionTimeTable(sufferedClets.get(i).getCloudletId(), hostListCletMigrated.get(j).getId());
			cletExecutionPerHost = (hostListCletExecutedSoFar.get(j)/1000) + (cletReexecutionPerHost/1000) - cletDownTimePerHost;
			downTime = downTime + cletDownTimePerHost;
			cletEnergy = cletEnergy + ((power/1000)*(cletExecutionPerHost/3600));
			cletReexecutionEnergy = cletReexecutionEnergy + ((power/1000)*(cletReexecutionPerHost/3600000));
			reexecutionCletLength = reexecutionCletLength + (cletReexecutionPerHost/1000);
		}	
		
		hostIDListCletMigrationDownTime = new ArrayList<Integer>();
		hostListCletMigrationDownTime = new ArrayList<Host>();
		cletMigrationDownTime = new ArrayList<Double>();
		cletMigrationOverheads = new ArrayList<Double>();		
		
		hostIDListCletMigrationDownTime = vmRecord.getCletMigrationHostRecordforDownTime(sufferedClets.get(i).getCloudletId());
		cletMigrationOverheads = vmRecord.getVmMigrationOverheadPerHostList(sufferedClets.get(i).getCloudletId());		

		double cletMigrationOverheadEnergyPerHost = 0.0; 
		double cletMigrationOverheadEnergy = 0.0;
		double totalMigrationOverheads = 0.0;
		
		if(hostIDListCletMigrationDownTime != null){			
		
			for(int k=0; k<hostIDListCletMigrationDownTime.size(); k++){
				for(int l=0; l<hostList.size(); l++){
					if(hostList.get(l).getId()==hostIDListCletMigrationDownTime.get(k)){
						hostListCletMigrationDownTime.add(hostList.get(l));
						break;
					}
				}
			}
		
			for(int j=0; j<hostIDListCletMigrationDownTime.size(); j++){	
				coreCount = hostListCletMigrationDownTime.get(j).getNumberOfPes();
				power = pow.getPower(vmUtilization, coreCount);
				cletMigrationOverheadEnergyPerHost = (((cletMigrationOverheads.get(j))/3600)*(power/1000));
				totalMigrationOverheads = totalMigrationOverheads + cletMigrationOverheads.get(j);
				cletMigrationOverheadEnergy = cletMigrationOverheadEnergy + cletMigrationOverheadEnergyPerHost;			
			}
			
			cletEnergy = cletEnergy - cletMigrationOverheadEnergy;
		}
		
		cletEnergyConsumption.put(sufferedClets.get(i).getCloudletId(), cletEnergy);
		cletEnergyWastageWithReexecution.put(sufferedClets.get(i).getCloudletId(), cletReexecutionEnergy);
		
		/*
		lastHostIDforClet = vmRecord.getLastHostforClet(sufferedClets.get(i).getCloudletId());		
		for(int j=0; j<hostIDListCletMigrated.size(); j++){
			if(hostListCletMigrated.get(j).getId()==lastHostIDforClet){
				lastHostforClet = hostListCletMigrated.get(j);
				break;
			}
		}
		
		coreCount = lastHostforClet.getNumberOfPes();
		//power = HostVMMapping.gethostVMMapPower(lastHostforClet, vm);
		power = pow.getPower(vmUtilization, coreCount);
		reexecutionCletLength = CloudletReexecutionPart.getCletReexecutionTime(sufferedClets.get(i).getCloudletId())/1000;
		energyWastageforReexecution = ((power/1000) * (reexecutionCletLength/3600));
		cletEnergyWastageWithReexecution.put(sufferedClets.get(i).getCloudletId(), energyWastageforReexecution);	
		*/
		
		
		energyConsumptionwithoutReexecution = cletEnergy - cletReexecutionEnergy;
		cletEnergyConsumptionWithoutReexecution.put(sufferedClets.get(i).getCloudletId(), energyConsumptionwithoutReexecution);
	
		cletMigrationDownTime = vmRecord.getVmMigrationDownTimePerHostList(sufferedClets.get(i).getCloudletId());		
		
		double cletMigrationDownTimeEnergyPerHost = 0.0;
		double cletMigrationDownTimeEnergy = 0.0;
		double migrationDownTime = 0.0;			
		double actualCletEnergyConsumptionWithReexecution = 0.0;
		
		if(cletMigrationDownTime != null){
			for(int j=0; j<hostIDListCletMigrationDownTime.size(); j++){
				ftaHostID = HostMapping.getFTAhostID(hostIDListCletMigrationDownTime.get(j));
				idleUtilization = ftaread.getCurrentUtilization(ftaHostID);
				coreCount = hostListCletMigrationDownTime.get(j).getNumberOfPes();
				power = pow.getPower(idleUtilization, coreCount);
				cletMigrationDownTimeEnergyPerHost = ((cletMigrationDownTime.get(j)/3600)*(power/1000));				
				cletMigrationDownTimeEnergy = cletMigrationDownTimeEnergy + cletMigrationDownTimeEnergyPerHost;		
				migrationDownTime = migrationDownTime + cletMigrationDownTime.get(j);
			}
			cletEnergyWastageWithMigrationDownTime.put(sufferedClets.get(i).getCloudletId(), cletMigrationDownTimeEnergy);
			actualCletEnergyConsumption = energyConsumptionwithoutReexecution + cletMigrationDownTimeEnergy;
			actualCletEnergyConsumptionWithReexecution = cletEnergy + cletMigrationDownTimeEnergy;
			cletEnergyConsumptionWithMigrationDownTime.put(sufferedClets.get(i).getCloudletId(), actualCletEnergyConsumption);
			totalDownTime = downTime + migrationDownTime;
			totalMigrationDownTime = totalMigrationDownTime + migrationDownTime;
		}
		else{
			actualCletEnergyConsumption = energyConsumptionwithoutReexecution;
			actualCletEnergyConsumptionWithReexecution = cletEnergy;
			totalDownTime = downTime;
		}
		
		finalTotalDownTime = finalTotalDownTime + totalDownTime;
		
		if(vmEnergyConsumptionTable.isEmpty()){
			vmEnergyConsumptionTable.put(vm.getId(), actualCletEnergyConsumption);
			vmEnergyConsumptionTablewithReexecution.put(vm.getId(), actualCletEnergyConsumptionWithReexecution);
		}
		else
		{				
			double tempVmEnergy;				
			double tempVmEnergywithReexecution;
			if(vmEnergyConsumptionTable.containsKey(vm.getId())){				
				tempVmEnergy = vmEnergyConsumptionTable.get(vm.getId());
				tempVmEnergywithReexecution = vmEnergyConsumptionTablewithReexecution.get(vm.getId());
				tempVmEnergy = tempVmEnergy + actualCletEnergyConsumption;
				tempVmEnergywithReexecution = tempVmEnergywithReexecution + actualCletEnergyConsumptionWithReexecution;
				vmEnergyConsumptionTable.put(vm.getId(), tempVmEnergy);
				vmEnergyConsumptionTablewithReexecution.put(vm.getId(), tempVmEnergywithReexecution);
			}
			else{
				vmEnergyConsumptionTable.put(vm.getId(), actualCletEnergyConsumption);
				vmEnergyConsumptionTablewithReexecution.put(vm.getId(), actualCletEnergyConsumptionWithReexecution);
			}				
		}
		totalMigrationOverhead = totalMigrationOverhead + totalMigrationOverheads;
		
		Log.printLine( indent + indent + dft.format(cloudletInitialLength)+
					indent + indent  + indent + indent + dft.format(turnAroundTime)+
					indent + indent + indent + indent + dft.format(changeinExecutionTime)+
					indent + indent + indent + indent + indent + dft.format(totalDownTime)+
					indent + indent + indent + indent + dft.format(totalMigrationOverheads)+
					indent + indent + indent  + indent + indent + dft.format(reexecutionCletLength)+
					indent + indent + indent  + indent + indent + dft.format(actualCletEnergyConsumption)+
					indent + indent + indent  + indent + indent + dft.format(actualCletEnergyConsumptionWithReexecution));		
	}		
	
	
	Log.printLine("========== OUTPUT For Energy Consumption Cloudlet Level ==========");
	//Log.printLine("Cloudlet ID" + indent +" Task Execution Length" + indent + indent + "Total Finish Time" + indent + indent + indent + "Execution Time Changed" + indent + indent + indent +indent + "Total DownTime" + indent +indent + indent + "Total Migration Overheads" + indent +indent + indent + "Total Re-execution Time" +indent+indent + indent + "Energy Consumption" );
	Log.printLine("Cloudlet ID" + indent +" Task Execution Length" + indent + indent + "Total Finish Time" + indent + indent + indent + "Execution Time Changed" + indent + indent + indent +indent + "Total DownTime" + indent +indent + indent + "Total Migration Overheads" + indent +indent + indent + "Total Re-execution Time" + indent + indent + indent + "Energy Consumption without Reexecution" + indent + indent + indent + "Energy Consumption with Reexecution" );

	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== OUTPUT For Energy Wastage Cloudlet Level ==========");
	//Log.printLine("Cloudlet ID" + indent + "VM ID" + indent + "Cloudlet Initial Length" +indent+ "Energy Wastage");
	Log.printLine("Cloudlet ID" + indent + indent + "Reexecution Energy" + indent + indent + "Migration DownTime Energy" + indent + indent + "Total Energy Wastage");
	
	HashMap<Integer, Double>vmEnergyWastageTable = new HashMap<Integer, Double>();
	
	double reexecutionEnergy = 0.0;
	double migrationDownTimeEnergy = 0.0;
	double totalCletEnergyWastage = 0.0;
	
	for (int i = 0; i < size; i++) {				
		cloudlet = sufferedClets.get(i);
		vm = VmCloudletMapping.getVm(sufferedClets.get(i));
		if(cletEnergyWastageWithReexecution.containsKey(cloudlet.getCloudletId())){
			reexecutionEnergy = cletEnergyWastageWithReexecution.get(cloudlet.getCloudletId());
		}
		else{
			reexecutionEnergy = 0.0;
		}
		
		if(cletEnergyWastageWithMigrationDownTime.containsKey(cloudlet.getCloudletId())){
			migrationDownTimeEnergy = cletEnergyWastageWithMigrationDownTime.get(cloudlet.getCloudletId());
		}
		else{
			migrationDownTimeEnergy = 0.0;
		}	
		
		totalCletEnergyWastage = reexecutionEnergy + migrationDownTimeEnergy;	
		
		if(vmEnergyWastageTable.isEmpty()){
			vmEnergyWastageTable.put(vm.getId(), totalCletEnergyWastage);
		}
		else
		{				
			double tempVmEnergyWastage;				
			if(vmEnergyWastageTable.containsKey(vm.getId())){
				tempVmEnergyWastage = vmEnergyWastageTable.get(vm.getId());
				tempVmEnergyWastage = tempVmEnergyWastage + totalCletEnergyWastage;
				vmEnergyWastageTable.put(vm.getId(), tempVmEnergyWastage);
			}
			else{
				vmEnergyWastageTable.put(vm.getId(), totalCletEnergyWastage);
			}				
		}			
		
		Log.printLine(indent + cloudlet.getCloudletId()  
					  +indent + indent +indent + indent + dft.format(reexecutionEnergy)
					  +indent + indent +indent + indent + dft.format(migrationDownTimeEnergy)
					  +indent + indent +indent + indent + dft.format(totalCletEnergyWastage));			
		
	}

	Log.printLine("========== OUTPUT For Energy Wastage Cloudlet Level ==========");
	//Log.printLine("Cloudlet ID" + indent + "VM ID" + indent + "Cloudlet Initial Length" +indent+ "Energy Wastage");
	Log.printLine("Cloudlet ID"  + indent + indent + "Reexecution Energy" + indent + indent + "Migration DownTime Energy" + indent + indent + "Total Energy Wastage");
	
	//FileWriter HostCountClock = null;
	//FileWriter HostCount = null;
	Log.printLine();
	Log.printLine();
	Log.printLine("========== OUTPUT For Host Count ==========");	
	Log.printLine(indent + "Clock" + indent + indent + indent + "Host Count" );
//	try{
//	HostCountClock = new FileWriter("/raid/disc1/ysharma1/Java/Results/Simulation_File/HostCountClock.txt", false);
//	HostCount = new FileWriter("/raid/disc1/ysharma1/Java/Results/Simulation_File/HostCount.txt", false);
	ArrayList<Integer>hostCount = new ArrayList<Integer>();
	ArrayList<Double>clockForHostCount = new ArrayList<Double>();
	hostCount = HostCounter.getHostCountList();
	clockForHostCount = HostCounter.getClockList();
	for(int i=0;i<hostCount.size();i++){
	//	HostCountClock.write((clockForHostCount.get(i))+System.getProperty("line.seperator"));
	//	HostCount.write((hostCount.get(i))+System.getProperty("line.seperator"));
		Log.printLine(indent + clockForHostCount.get(i)  
		  +indent + indent +indent + hostCount.get(i)
		  );
		}
//	HostCountClock.close();
//	HostCount.close();
	
//	}catch(IOException e){
//		System.out.println("Error has been occurred while writing output for host count");
//	}
	Log.printLine("========== OUTPUT For Host Count ==========");		
	Log.printLine(indent + "Clock" + indent + indent + indent + "Host Count" );
	
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Energy OUTPUT ==========");
	//Log.printLine("Total Energy Consumption" + indent + indent + indent + "Total Energy Consumption with Reexecution" + indent + indent + indent + "Total Energy Wastage" + indent + indent + indent + "Total Idle Energy Consumption"  +indent +indent + indent + "Application Utility Value" );
	Log.printLine("Total Energy Consumption" + indent + indent + indent + "Total Energy Wastage" + indent + indent + indent + "Total Idle Energy Consumption"  +indent +indent + indent + "Application Utility Value" );
	
		double totalReliability;	
		double totalReliabilityParallel = 0.0;
		double totalReliabilityAverage = 0.0;
		double totalAvailability = 0.0;
		double totalMaintainability = 0.0;
		double percentageAchievedDeadlines;
		//ArrayList<Double>energyConsumptionperHost = new ArrayList<Double>();		
		totalReliability = 0.0;		
		int systemReliabilityListSize;
		ArrayList<Double>systemReliabilityList = new ArrayList<Double>();
		ArrayList<Double>systemReliabilityListParallel = new ArrayList<Double>();
		ArrayList<Double>systemReliabilityListAverage = new ArrayList<Double>();
		ArrayList<Double>systemAvailabilityList = new ArrayList<Double>();
		ArrayList<Double>systemMaintainabilityList = new ArrayList<Double>();
		systemReliabilityList.addAll(hvmRbl.getSystemReliability());
		systemReliabilityListSize = hvmRbl.getSystemReliability().size();
		systemReliabilityListParallel.addAll(hvmRbl.getSystemReliabilityParallel());
		systemReliabilityListAverage.addAll(hvmRbl.getSystemReliabilityAverage());
		systemAvailabilityList.addAll(hvmRbl.getSystemAvailability());
		systemMaintainabilityList.addAll(hvmRbl.getSystemMaintainability());
		for(int i=0;i<systemReliabilityListSize;i++){
			totalReliability = totalReliability + systemReliabilityList.get(i);
			totalReliabilityParallel = totalReliabilityParallel + systemReliabilityListParallel.get(i);
			totalReliabilityAverage = totalReliabilityAverage + systemReliabilityListAverage.get(i);
			totalAvailability = totalAvailability + systemAvailabilityList.get(i);
			totalMaintainability = totalMaintainability + systemMaintainabilityList.get(i);
		//	AverageSystemReliability.write((dft.format(systemReliabilityListAverage.get(i)))+ System.getProperty( "line.separator"));
		}
		totalReliability = totalReliability/systemReliabilityListSize;
		totalReliabilityParallel = totalReliabilityParallel/systemReliabilityListSize;
		totalReliabilityAverage = totalReliabilityAverage/systemReliabilityListSize;
		totalAvailability = (totalAvailability/systemReliabilityListSize)*100;
		totalMaintainability = (totalMaintainability/systemReliabilityListSize)*100;			
		
		double totalEnergyConsumption = 0.0;		
		double totalEnergyConsumptionwithReexecution = 0.0;
		double totalEnergyWastage = 0.0;		
		
		for(int i=0; i<newVmList.size();i++){
			//totalEnergyConsumption = totalEnergyConsumption + vmEnergyConsumptionTable.get(newVmList.get(i).getId());
			totalEnergyConsumption = totalEnergyConsumptionwithReexecution = totalEnergyConsumptionwithReexecution + vmEnergyConsumptionTablewithReexecution.get(newVmList.get(i).getId());
			totalEnergyWastage = totalEnergyWastage + vmEnergyWastageTable.get(newVmList.get(i).getId());
		}
		
		jobUtilityValue = jobUtilityValue/size;			
	
		percentageAchievedDeadlines = (((double)countDeadlineAchieved*100)/((double)countDeadlineAchieved+(double)countDeadlineMissed));
		
		Log.printLine( indent + dft.format(totalEnergyConsumption)+			
				//indent + indent + indent + indent + indent +dft.format(totalEnergyConsumptionwithReexecution)+
				indent + indent + indent + indent + indent +dft.format(totalEnergyWastage)+
				indent + indent + indent + indent + indent +dft.format(totalIdleEnergyConsumption)+									
				indent + indent + indent + indent + indent + indent + indent +dft.format(jobUtilityValue));	
			
	Log.printLine("========== Total Energy OUTPUT ==========");
	//Log.printLine("Total Energy Consumption" + indent + indent + indent + "Total Energy Consumption with Reexecution"+ indent + indent + indent + "Total Energy Wastage" + indent + indent + indent + "Total Idle Energy Consumption"  +indent +indent + indent + "Application Utility Value" );
	Log.printLine("Total Energy Consumption" + indent + indent + indent + "Total Energy Wastage" + indent + indent + indent + "Total Idle Energy Consumption"  +indent +indent + indent + "Application Utility Value" );
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Deadlines OUTPUT ==========");
	//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Idle Energy Consumption" +indent+ "Total Idle Time" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
	Log.printLine(indent + "Total Deadlines Achieved" + indent + indent + "Total Deadlines Missed" + indent + indent + "Percentage Achieved Deadlines");
	
	Log.printLine( indent + indent + indent + countDeadlineAchieved +
			indent + indent + indent +  indent + indent +  indent + indent +countDeadlineMissed+
			indent + indent + indent + indent + indent + dft.format(percentageAchievedDeadlines));		
	
	Log.printLine("========== Deadlines OUTPUT ==========");
	//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Idle Energy Consumption" +indent+ "Total Idle Time" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
	Log.printLine(indent + "Total Deadlines Achieved" + indent + indent + "Total Deadlines Missed" + indent + indent + "Percentage Achieved Deadlines");
	
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Reliability OUTPUT ==========");
	//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Idle Energy Consumption" +indent+ "Total Idle Time" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
	Log.printLine(indent + "Total Reliability Serial" + indent + indent + "Total Reliability Parallel" + indent + indent + "Total Reliability Average" + indent + indent + "Total Availability" + indent + indent + "Total Maintainability");
	
	Log.printLine( indent + dft.format(totalReliability*100) +
			indent + indent + indent + indent + indent + indent + dft.format(totalReliabilityParallel*100)+
			indent + indent + indent + indent + indent + indent + indent + dft.format((totalReliabilityAverage*100))+
			indent + indent + indent + indent + indent + indent + dft.format(totalAvailability)+
			indent + indent + indent + indent + indent + dft.format(totalMaintainability));		
	
	Log.printLine("========== Reliability OUTPUT ==========");
	//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Idle Energy Consumption" +indent+ "Total Idle Time" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
	Log.printLine(indent + "Total Reliability Serial" + indent + indent + "Total Reliability Parallel" + indent + indent + "Total Reliability Average" + indent + indent + "Total Availability" + indent + indent + "Total Maintainability");
	
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average OUTPUTs ==========");
	Log.printLine("Average Task Length" + indent + indent+ indent + indent+ "Average Makespan" + indent + indent+ indent + indent+ "Average Execution Time Changed");
			
	double averageCletCompletionTime;
	double averageCletLength;
	double averageExecutionTimeChanged;		
	averageCletCompletionTime = totalMakeSpan/size;
	averageCletLength = totalCletLength/size;
	averageExecutionTimeChanged = totalExecutionTimeChanged/size;		
	Log.printLine( indent +dft.format(averageCletLength/3600) + indent + indent + indent + indent + indent +indent +dft.format(averageCletCompletionTime/3600) + indent + indent + indent + indent + indent + indent +dft.format(averageExecutionTimeChanged/3600));
	Log.printLine();
	Log.printLine( indent +dft.format(averageCletLength) + indent + indent + indent + indent +indent +dft.format(averageCletCompletionTime) + indent + indent + indent + indent + indent + indent +dft.format(averageExecutionTimeChanged));
	
	Log.printLine("========== Average OUTPUTs ==========");
	Log.printLine("Average Task Length" + indent + indent+ indent + indent+ "Average Makespan" + indent + indent+ indent + indent+ "Average Execution Time Changed"+ indent + indent+ indent + indent+ "Average Deadline");
	
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Migration OUTPUTs ==========");
	//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Idle Energy Consumption" +indent+ "Total Idle Time" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
	Log.printLine("Total Failures Occurred" + indent + indent + indent + indent+ "Total VM Migrations " + indent + indent + indent + indent+ "Total Checkpoints "+ indent + indent + indent + indent+ "Total Consolidations ");
	Log.printLine(indent + indent + totalFailureCount + indent + indent + indent + indent  + indent + indent + indent + indent+ totalVmMigrations+ indent + indent + indent + indent + indent + indent + indent+ totalCheckpointsCount+ indent + indent + indent + indent + indent + indent+ totalVmConsolidations);
	Log.printLine("Total Failures Occurred" + indent + indent + indent + indent+ "Total VM Migrations " + indent + indent + indent + indent+ "Total Checkpoints"+ indent + indent + indent + indent+ "Total Consolidations ");
	
	Log.printLine("========== Migration OUTPUTs ==========");
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Overhead OUTPUTs ==========");
	//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Idle Energy Consumption" +indent+ "Total Idle Time" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
	Log.printLine("Total DownTime" + indent + indent + indent + indent+ "Total Reexecution Time " + indent + indent + indent + indent+ "Total Hosts "  + indent + indent + indent + indent+ "Total Migration DownTime "  + indent + indent + indent + indent+ "Total Migration Overhead ");
	
	Log.printLine(indent  + dft.format(finalTotalDownTime/3600) + indent + indent + indent + indent + indent+ dft.format(finalTotalReexecutionTime/3600)+ indent + indent + indent + indent + indent + indent+ hostList.size()+ indent + indent + indent + indent + indent + indent+ dft.format(totalMigrationDownTime/3600) + indent + indent + indent + indent+ indent+ dft.format(totalMigrationOverhead/3600));
	Log.printLine();
	Log.printLine(indent  + dft.format(finalTotalDownTime) + indent + indent + indent + indent+ dft.format(finalTotalReexecutionTime)+ indent + indent + indent + indent+ hostList.size()+ indent + indent + indent + indent+ dft.format(totalMigrationDownTime) + indent + indent + indent + indent+ dft.format(totalMigrationOverhead));
	
	Log.printLine("Total DownTime" + indent + indent + indent + indent+ "Total Reexecution Time" + indent + indent + indent + indent+ "Total Hosts "  + indent + indent + indent + indent+ "Total Migration DownTime "  + indent + indent + indent + indent+ "Total Migration Overhead ");
	
	Log.printLine("========== Overhead OUTPUTs ==========");		
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Application Finishing Time ==========");	
	Log.printLine(indent + indent + dft.format(applicationFinishingTime));	
	Log.printLine();
	Log.printLine("========== Application Finishing Time ==========");
}

private static void printCloudletListRestartingwithConsolidation(List<Cloudlet> list, List<Host> hostList, FTAFileReaderGrid5000 ftaread, FailureDatacenterBroker broker, HostVMMappingReliability hvmRbl, List<Vm>newVmList, VmUtilization vmUtl, PowerModel pow){	
	

	Double initTime;
	double totalMakeSpan = 0.0;
	Double changeinExecutionTime;
	Double totalDownTime;		
	int countDeadlineMissed = 0;
	int countDeadlineAchieved = 0;
	double applicationFinishingTime = 0.0;
	double totalReexecutionTime;	
	double totalDeadline = 0;		
	double totalMigrationDownTime = 0;
	double totalMigrationOverhead = 0;
	Vm vm;	
	
	int size = sufferedClets.size(); //For only suffered cloudlets and corresponding VMs. 
	Cloudlet cloudlet;		//Host host;
	
	System.out.println("Printing results for Restarting with Consolidation Fault Tolerance Mechanism");
	String indent = "    ";
	Log.printLine();
	Log.printLine("========== OUTPUT ==========");
	
	Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
			"Data center ID" + indent + indent + "VM ID" + indent +  indent + "Bag ID" + indent +  indent +"Cloudlet Initial Length" + indent + "Intial Start Time" + indent + indent +"Finish Time" +indent+ indent+ "Turnaround Time" +indent+ indent+ "Deadline Value" +indent+ indent+ "Deadline Actual Value"+indent+ indent+ "Utility Value"+indent+ indent +"Deadline");
	
	DecimalFormat dft = new DecimalFormat("###.####");			
	
	for (int i = 0; i < size; i++) {		
		cloudlet = sufferedClets.get(i);			
		double cloudletInitialLength = 0.0;			
		vm = VmCloudletMapping.getVm(cloudlet);
		int bagID;
		if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS){
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);
			Log.print("SUCCESS");			
			initTime = broker.getcloudletInitialStartTime(cloudlet);		
			if(cloudletTableBackup.containsKey(cloudlet.getCloudletId())){						
				cloudletInitialLength = ((double)cloudletTableBackup.get(cloudlet.getCloudletId()))/1000;
			}		
				
			double deadline;			
			double utilityValue;
			double finishedTime;
			double downTime;
			double downTimeforMigration;
			double reExecutionTime;
			//double migrationOverheads;
			double turnAroundTime;				
			bagID = BagTaskMapping.getBagID(cloudlet.getCloudletId());				
			downTime = DownTimeTable.getCloudletDownTime(cloudlet.getCloudletId());				
			downTimeforMigration = DownTimeTable.getCloudletDownTimeforMigration(cloudlet.getCloudletId());				
			downTime = downTime + downTimeforMigration;				
			reExecutionTime = totalReexecutionTime = ((double)CloudletReexecutionPart.getCletReexecutionTime(cloudlet.getCloudletId()))/1000;				
		//	migrationOverheads = MigrationOverheadTable.getCletMigrationOverhead(cloudlet);				
			finishedTime = cloudlet.getFinishTime();				
			turnAroundTime = finishedTime - initTime;				
			totalMakeSpan = totalMakeSpan + turnAroundTime;			
			finalTotalReexecutionTime = finalTotalReexecutionTime + reExecutionTime;				
			deadline = initTime + (stringencyFactor * (cloudletInitialLength));			
			if(finishedTime<=deadline){		
				utilityValue = 1;
			}
			else{					
				utilityValue = (1-((finishedTime-deadline)/deadline));
				if(utilityValue<0){
					utilityValue = 0;
				}
			}	
			jobUtilityValue = jobUtilityValue + utilityValue;				
			totalDeadline = totalDeadline + (deadline-initTime);		
			
			if(deadline > finishedTime){	
				countDeadlineAchieved = countDeadlineAchieved + 1;
			Log.printLine( indent + indent + cloudlet.getResourceId() + indent + indent + indent + indent + cloudlet.getVmId() +
					indent + indent + indent + indent + bagID +
					indent + indent + indent + indent + dft.format(cloudletInitialLength) +
					indent + indent + indent + indent + indent + dft.format(initTime) +						
					indent + indent + indent +  dft.format(finishedTime)+
					indent + indent + indent +  dft.format(turnAroundTime)+
					indent + indent + indent +  dft.format(deadline)+
					indent + indent + indent +  dft.format(deadline-initTime)+
					indent + indent + indent +  dft.format(utilityValue)+
					indent + indent + indent+ "Achieved" );
			}
			else{					
				countDeadlineMissed = countDeadlineMissed + 1;
				Log.printLine( indent + indent + cloudlet.getResourceId() + indent + indent + indent + indent + cloudlet.getVmId() +
						indent + indent + indent + indent + bagID +
						indent + indent + indent + indent + dft.format(cloudletInitialLength) +
						indent + indent + indent + indent + indent + dft.format(initTime) +							
						indent + indent + indent +  dft.format(finishedTime)+
						indent + indent + indent +  dft.format(turnAroundTime)+
						indent + indent + indent +  dft.format(deadline)+
						indent + indent + indent +  dft.format(deadline-initTime)+
						indent + indent + indent +  dft.format(utilityValue)+
						indent + indent + indent+ "Missed" );
				}
			}
		}
	
	Log.printLine("========== OUTPUT ==========");
	
	Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
			"Data center ID" + indent + indent + "VM ID" + indent + indent + "Bag ID" + indent +  indent + "Cloudlet Initial Length" + indent + "Intial Start Time" + indent + indent +"Finish Time" +indent+ indent+ "Turnaround Time" +indent+ indent+ "Deadline Value" +indent+ indent+ "Deadline Actual Value"+indent+ indent+ "Utility Value"+indent+ indent +"Deadline");
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== OUTPUT For Bag Level Information ==========");
	//Log.printLine("Job ID" + indent + indent + indent + "Finishing Time");
	Log.printLine( "Finishing Time");
	int bagCount;
	int cletBagID;	
	bagCount = RunTimeConstants.numberofBags;
	for(int i=0; i<bagCount; i++){
		double jobFinishingTime = 0;
		double turnAroundTime = 0;
		for(int j=0; j<size; j++){
			cloudlet = sufferedClets.get(j);
			initTime = broker.getcloudletInitialStartTime(cloudlet);	
			turnAroundTime = cloudlet.getFinishTime() - initTime;
			cletBagID = BagTaskMapping.getBagID(cloudlet.getCloudletId());
			if(i == cletBagID){
				if(jobFinishingTime < turnAroundTime){
					jobFinishingTime = turnAroundTime;
				}
			}
		}
		if(applicationFinishingTime < jobFinishingTime){
			applicationFinishingTime = jobFinishingTime;
		}
		//Log.printLine( indent + i + indent + indent + indent + indent + dft.format(jobFinishingTime));
		Log.printLine( indent + dft.format(jobFinishingTime));
	}
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== OUTPUT For Bag Level Information ==========");
	///Log.printLine("Job ID" + indent + indent + indent + "Finishing Time");
	Log.printLine( "Finishing Time");
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== OUTPUT For Host Information ==========");
	Log.printLine("Host ID" + indent + "FTA Host ID" + indent +" MTBF" + indent + "MTTR" +indent+ "Max Hazard Rate" +indent+ "Current Hazard Rate" +indent+ "Host Power" +indent+ "Host Availability" +indent+ "Host Maintainability");
	
	int ftaHostID;	
	for(int i=0;i<HostMapping.getHostMapSize();i++){
		ftaHostID = HostMapping.getFTAhostID(i);
		
		Log.printLine(  i + indent + indent + ftaHostID +
				indent + indent + indent + indent + dft.format(ftaread.getMTBF(ftaHostID)) +
				indent + indent + indent + indent + dft.format(ftaread.getMTTR(ftaHostID)) +
				indent + indent + indent + indent + ftaread.getMaxHazardRate(ftaHostID)+
				indent + indent + indent + indent + ftaread.getCurrentHazardRate(ftaHostID)+
				indent + indent + indent + indent + dft.format(ftaread.getPowerforHost(ftaHostID))+
				indent + indent + indent + indent + dft.format(ftaread.getAvailability(ftaHostID))+
				indent + indent + indent + indent + dft.format(ftaread.getMaintainability(ftaHostID))
				);			
	}	
	
	Log.printLine("========== OUTPUT For Host Information ==========");
	Log.printLine("Host ID" + indent + "FTA Host ID" + indent +" MTBF" + indent + "MTTR" +indent+ "Max Hazard Rate" +indent+ "Current Hazard Rate" +indent+ "Host Power" +indent+ "Host Availability" +indent+ "Host Maintainability");
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== OUTPUT For Host Idle Time  ==========");
	Log.printLine("Host ID" +indent+ indent + indent + "Total Idle Time" +indent+ indent + indent + "Total Down Time" +indent+ indent + indent + "Total Idle Energy Consumption" +indent+ indent + indent + "Average Idle Energy Consumption");		
	
	int hostIDIdle;
	//double idleTimewithDownTime;
	double idleTime;
	double averageIdleTime;
	int peListSize;
	double idleUtilization = 0.0;
	double idlePower = 0.0;		
	double totalIdleEnergyConsumption = 0.0;
	double idleEnergyConsumption = 0.0;
	double averageIdleEnergyConsumption = 0.0;
	double totalAverageIdleEnergyConsumption = 0.0;
	double downTime;
	List<Pe>peList;
	for(int i=0; i<hostList.size(); i++){
		idleTime = 0.0;
		//idleTimewithDownTime = 0.0;
		downTime = 0.0;
		peList = new ArrayList<Pe>();
		ftaHostID = HostMapping.getFTAhostID(hostList.get(i).getId());
		idleUtilization = ftaread.getCurrentUtilization(ftaHostID);
		hostIDIdle = hostList.get(i).getId();			
		peListSize = hostList.get(i).getNumberOfPes();
		peList = hostList.get(i).getPeList();
		for(int j=0; j<peList.size(); j++){
			//idleTimewithDownTime = idleTimewithDownTime + IdleTime.getPeIdleTime(hostList.get(i).getId(), peList.get(j).getId());
			idleTime = idleTime + IdleTime.getPeIdleTime(hostIDIdle, peList.get(j).getId());
			downTime = downTime + IdleTime.getPeDownTime(hostIDIdle, peList.get(j).getId());
		}			
		//averageIdleTime = idleTime/peListSize;
		//idleTime = idleTimewithDownTime - downTime;
		averageIdleTime = idleTime;
		idlePower = pow.getPower(idleUtilization, peListSize);
		idleEnergyConsumption = ((idlePower/3600) * (averageIdleTime/1000));
		averageIdleEnergyConsumption = idleEnergyConsumption/peListSize;
		totalIdleEnergyConsumption = totalIdleEnergyConsumption + idleEnergyConsumption; 
		totalAverageIdleEnergyConsumption = totalAverageIdleEnergyConsumption + averageIdleEnergyConsumption;
		Log.printLine( indent+ hostIDIdle + indent + indent+ indent+ idleTime + indent + indent+ indent+ downTime + indent + indent+ indent+ idleEnergyConsumption+ indent + indent+ indent+ averageIdleEnergyConsumption);
	}

	Log.printLine("========== OUTPUT For Host Idle Time  ==========");
	Log.printLine("Host ID" +indent+ indent + indent + "Total Idle Time" +indent+ indent + indent + "Total Down Time" +indent+ indent + indent + "Total Idle Energy Consumption" +indent+ indent + indent + "Average Idle Energy Consumption" );		
		
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== OUTPUT For Energy Consumption Cloudlet Level==========");
	Log.printLine("Cloudlet ID" + indent +" Task Execution Length" + indent + indent + "Total Finish Time" + indent + indent + indent + "Execution Time Changed" + indent + indent + indent +indent + "Total DownTime" + indent +indent + indent + "Total Migration Overheads" + indent +indent + indent + "Total Re-execution Time" + indent + indent + indent + "Energy Consumption without Reexecution" + indent + indent + indent + "Energy Consumption with Reexecution" );
	
	double totalCletLength = 0.0;
	double totalExecutionTimeChanged = 0.0;		
	double cloudletInitialLength;
	double turnAroundTime;
	double reexecutionCletLength;		
	
	ArrayList<Integer>hostIDListCletMigrated;
	ArrayList<Host>hostListCletMigrated;
	ArrayList<Double>hostListCletExecutedSoFar;		
	ArrayList<Host>hostListCletMigrationDownTime;
	ArrayList<Integer>hostIDListCletMigrationDownTime;
	ArrayList<Double>cletMigrationDownTime;
	ArrayList<Double>cletMigrationOverheads;
	
	HashMap<Integer, Double>cletEnergyConsumption = new HashMap<Integer, Double>();
	HashMap<Integer, Double>cletEnergyConsumptionWithoutReexecution = new HashMap<Integer, Double>();
	HashMap<Integer, Double>cletEnergyConsumptionWithMigrationDownTime = new HashMap<Integer, Double>();
	HashMap<Integer, Double>cletEnergyWastageWithMigrationDownTime = new HashMap<Integer, Double>();
	HashMap<Integer, Double>cletEnergyWastageWithReexecution = new HashMap<Integer, Double>();
	//HashMap<Integer, Double>cletEnergyWastageWithReexecutionLastHost = new HashMap<Integer, Double>();
	HashMap<Integer, Double>vmEnergyConsumptionTable = new HashMap<Integer, Double>();
	HashMap<Integer, Double>vmEnergyConsumptionTablewithReexecution = new HashMap<Integer, Double>();
	
	
	//int lastHostIDforClet;
	//Host lastHostforClet = null;
	double vmUtilization;

	for(int i=0; i<size; i++){			
		hostIDListCletMigrated = new ArrayList<Integer>();
		hostListCletMigrated = new ArrayList<Host>();
		cloudletInitialLength = 0.0;
		initTime = 0.0;
		turnAroundTime = 0.0;
		reexecutionCletLength = 0.0;
		changeinExecutionTime = 0.0;
		totalDownTime = 0.0;			
		if(sufferedClets.get(i).getCloudletId() == 3){
			System.out.println("Check");
		}
		vm = VmCloudletMapping.getVm(sufferedClets.get(i));
		initTime = broker.getcloudletInitialStartTime(sufferedClets.get(i));	
		Log.print(indent + sufferedClets.get(i).getCloudletId() + indent + indent);	
		if(cloudletTableBackup.containsKey(sufferedClets.get(i).getCloudletId())){		
			cloudletInitialLength = ((double)cloudletTableBackup.get(sufferedClets.get(i).getCloudletId()))/1000;			
			totalCletLength = totalCletLength + cloudletInitialLength;
			}
		turnAroundTime = sufferedClets.get(i).getFinishTime() - initTime;
		changeinExecutionTime = turnAroundTime - cloudletInitialLength;
		
		totalExecutionTimeChanged = totalExecutionTimeChanged + changeinExecutionTime;
		
		vmUtilization = vmUtl.getVmAverageUtilization(vm.getId());
		hostIDListCletMigrated = vmRecord.getVmMigrationHostList(sufferedClets.get(i).getCloudletId());
		hostListCletExecutedSoFar = vmRecord.getVmExecutionDurationPerHostList(sufferedClets.get(i).getCloudletId());
		
		for(int k=0; k<hostIDListCletMigrated.size(); k++){
			for(int l=0; l<hostList.size(); l++){
				if(hostList.get(l).getId()==hostIDListCletMigrated.get(k)){
					hostListCletMigrated.add(hostList.get(l));
					break;
				}
			}
		}
		double power;
		double cletEnergy = 0.0;
		double cletReexecutionEnergy = 0.0;
		//double energyWastageforReexecution;
		double cletDownTimePerHost;	
		double cletReexecutionPerHost;
		double cletExecutionPerHost;
		double energyConsumptionwithoutReexecution;
		double actualCletEnergyConsumption;
		//double cletExecutionPerHostwithReexecution;
		//double cletEnergyWithReexecution = 0.0;
		int coreCount;
		downTime = 0.0;
		
		for(int j=0; j<hostIDListCletMigrated.size(); j++){				
			coreCount = hostListCletMigrated.get(j).getNumberOfPes();
			power = pow.getPower(vmUtilization, coreCount);
		//	power = HostVMMapping.gethostVMMapPower(hostListCletMigrated.get(j), vm);
			cletDownTimePerHost = DownTimeTable.getCletPerHostDownTimeTable(sufferedClets.get(i).getCloudletId(), hostListCletMigrated.get(j).getId());
			cletReexecutionPerHost = CloudletReexecutionPart.getCletPerHostReexecutionTimeTable(sufferedClets.get(i).getCloudletId(), hostListCletMigrated.get(j).getId());
			//cletExecutionPerHost = (hostListCletExecutedSoFar.get(j)/1000)-cletDownTimePerHost-(cletReexecutionPerHost/1000);
			//cletExecutionPerHostwithReexecution = (hostListCletExecutedSoFar.get(j)/1000)-cletDownTimePerHost;
			cletExecutionPerHost = (hostListCletExecutedSoFar.get(j)/1000) + (cletReexecutionPerHost/1000) - cletDownTimePerHost;
			downTime = downTime + cletDownTimePerHost;
			cletEnergy = cletEnergy + ((power/1000)*(cletExecutionPerHost/3600));
			//cletEnergyWithReexecution = cletEnergyWithReexecution + ((power/1000)*(cletExecutionPerHostwithReexecution/3600));
			cletReexecutionEnergy = cletReexecutionEnergy + ((power/1000)*(cletReexecutionPerHost/3600000));
			reexecutionCletLength = reexecutionCletLength + (cletReexecutionPerHost/1000);
		}	
		
		hostIDListCletMigrationDownTime = new ArrayList<Integer>();
		hostListCletMigrationDownTime = new ArrayList<Host>();
		cletMigrationDownTime = new ArrayList<Double>();
		cletMigrationOverheads = new ArrayList<Double>();
		
		hostIDListCletMigrationDownTime = vmRecord.getCletMigrationHostRecordforDownTime(sufferedClets.get(i).getCloudletId());
		cletMigrationOverheads = vmRecord.getVmMigrationOverheadPerHostList(sufferedClets.get(i).getCloudletId());
		
		double cletMigrationOverheadEnergyPerHost = 0.0; 
		double cletMigrationOverheadEnergy = 0.0;
		double totalMigrationOverheads = 0.0;
		
		if(hostIDListCletMigrationDownTime != null){		
			for(int k=0; k<hostIDListCletMigrationDownTime.size(); k++){
				for(int l=0; l<hostList.size(); l++){
					if(hostList.get(l).getId()==hostIDListCletMigrationDownTime.get(k)){
						hostListCletMigrationDownTime.add(hostList.get(l));
						break;
					}
				}
			}		
		
			for(int j=0; j<hostIDListCletMigrationDownTime.size(); j++){	
				coreCount = hostListCletMigrationDownTime.get(j).getNumberOfPes();
				power = pow.getPower(vmUtilization, coreCount);
				cletMigrationOverheadEnergyPerHost = (((cletMigrationOverheads.get(j))/3600)*(power/1000));
				totalMigrationOverheads = totalMigrationOverheads + cletMigrationOverheads.get(j);
				cletMigrationOverheadEnergy = cletMigrationOverheadEnergy + cletMigrationOverheadEnergyPerHost;			
			}
		
			cletEnergy = cletEnergy - cletMigrationOverheadEnergy;
		//	cletEnergyWithReexecution = cletEnergyWithReexecution - cletMigrationOverheadEnergy;
		}
		
		cletEnergyConsumption.put(sufferedClets.get(i).getCloudletId(), cletEnergy);
		cletEnergyWastageWithReexecution.put(sufferedClets.get(i).getCloudletId(), cletReexecutionEnergy);
		/*
		lastHostIDforClet = vmRecord.getLastHostforClet(sufferedClets.get(i).getCloudletId());		
		for(int j=0; j<hostIDListCletMigrated.size(); j++){
			if(hostListCletMigrated.get(j).getId()==lastHostIDforClet){
				lastHostforClet = hostListCletMigrated.get(j);
				break;
			}
		}
		
		coreCount = lastHostforClet.getNumberOfPes();
		//power = HostVMMapping.gethostVMMapPower(lastHostforClet, vm);
		power = pow.getPower(vmUtilization, coreCount);
		reexecutionCletLength = CloudletReexecutionPart.getCletReexecutionTime(sufferedClets.get(i).getCloudletId())/1000;
		energyWastageforReexecution = ((power/1000) * (reexecutionCletLength/3600));
		cletEnergyWastageWithReexecutionLastHost.put(sufferedClets.get(i).getCloudletId(), energyWastageforReexecution);
		*/
		//energyConsumptionwithoutReexecution = cletEnergyWithReexecution - energyWastageforReexecution;
		energyConsumptionwithoutReexecution = cletEnergy - cletReexecutionEnergy;
		cletEnergyConsumptionWithoutReexecution.put(sufferedClets.get(i).getCloudletId(), energyConsumptionwithoutReexecution);	
		
	
		cletMigrationDownTime = vmRecord.getVmMigrationDownTimePerHostList(sufferedClets.get(i).getCloudletId());	
		
		double cletMigrationDownTimeEnergyPerHost = 0.0;
		double cletMigrationDownTimeEnergy = 0.0;
		double migrationDownTime = 0.0;		
		double actualCletEnergyConsumptionWithReexecution = 0.0;
		if(cletMigrationDownTime!=null){			
			for(int j=0; j<hostIDListCletMigrationDownTime.size(); j++)
			{
				ftaHostID = HostMapping.getFTAhostID(hostIDListCletMigrationDownTime.get(j));
				idleUtilization = ftaread.getCurrentUtilization(ftaHostID);
				coreCount = hostListCletMigrationDownTime.get(j).getNumberOfPes();
				power = pow.getPower(idleUtilization, coreCount);
				cletMigrationDownTimeEnergyPerHost = ((cletMigrationDownTime.get(j)/3600)*(power/1000));				
				cletMigrationDownTimeEnergy = cletMigrationDownTimeEnergy + cletMigrationDownTimeEnergyPerHost;		
				migrationDownTime = migrationDownTime + cletMigrationDownTime.get(j);
				}		
			cletEnergyWastageWithMigrationDownTime.put(sufferedClets.get(i).getCloudletId(), cletMigrationDownTimeEnergy);		
			actualCletEnergyConsumption = energyConsumptionwithoutReexecution + cletMigrationDownTimeEnergy;
			actualCletEnergyConsumptionWithReexecution =  cletEnergy + cletMigrationDownTimeEnergy; 				
			cletEnergyConsumptionWithMigrationDownTime.put(sufferedClets.get(i).getCloudletId(), actualCletEnergyConsumption);
			totalDownTime = downTime + migrationDownTime;
			totalMigrationDownTime = totalMigrationDownTime + migrationDownTime;
		}
		else{
			actualCletEnergyConsumption = energyConsumptionwithoutReexecution;
			actualCletEnergyConsumptionWithReexecution = cletEnergy;
			totalDownTime = downTime;			
		}
		
		finalTotalDownTime = finalTotalDownTime + totalDownTime;
		
		if(vmEnergyConsumptionTable.isEmpty()){
			vmEnergyConsumptionTable.put(vm.getId(), actualCletEnergyConsumption); //This energy consumption is without re-execution energy
			vmEnergyConsumptionTablewithReexecution.put(vm.getId(), actualCletEnergyConsumptionWithReexecution);
		}
		else
		{				
			double tempVmEnergy;				
			double tempVmEnergywithReexecution;
			if(vmEnergyConsumptionTable.containsKey(vm.getId())){
				tempVmEnergy = vmEnergyConsumptionTable.get(vm.getId());
				tempVmEnergywithReexecution = vmEnergyConsumptionTablewithReexecution.get(vm.getId());				
				tempVmEnergy = tempVmEnergy + actualCletEnergyConsumption;
				tempVmEnergywithReexecution = tempVmEnergywithReexecution + actualCletEnergyConsumptionWithReexecution;
				vmEnergyConsumptionTable.put(vm.getId(), tempVmEnergy);
				vmEnergyConsumptionTablewithReexecution.put(vm.getId(), tempVmEnergywithReexecution);
			}
			else{
				vmEnergyConsumptionTable.put(vm.getId(), actualCletEnergyConsumption);
				vmEnergyConsumptionTablewithReexecution.put(vm.getId(), actualCletEnergyConsumptionWithReexecution);
			}				
		}
		
		totalMigrationOverhead = totalMigrationOverhead + totalMigrationOverheads;
		
		Log.printLine( indent + indent + dft.format(cloudletInitialLength)+
					indent + indent  + indent + indent + dft.format(turnAroundTime)+
					indent + indent + indent + indent + dft.format(changeinExecutionTime)+
					indent + indent + indent + indent + indent + dft.format(totalDownTime)+
					indent + indent + indent + indent + indent + indent +dft.format(totalMigrationOverheads)+
					indent + indent + indent  + indent + indent + indent + indent  + indent + dft.format(reexecutionCletLength)+						
					indent + indent + indent + indent + indent + indent + indent + indent + dft.format(actualCletEnergyConsumption)+
					indent + indent + indent + indent +indent + indent + indent +dft.format(actualCletEnergyConsumptionWithReexecution));		
	}		
	
	
	Log.printLine("========== OUTPUT For Energy Consumption Cloudlet Level ==========");
//	Log.printLine("Cloudlet ID" + indent +" Task Execution Length" + indent + indent + "Total Finish Time" + indent + indent + indent + "Execution Time Changed" + indent + indent + indent +indent + "Total DownTime" + indent +indent + indent + "Total Migration Overheads" + indent +indent + indent + "Total Re-execution Time with Last Host" + indent +indent + indent + "Total Re-execution Time with per Host" +indent+indent + indent + "Energy Consumption without Reexecution" +indent+indent + indent + "Energy Consumption with Reexecution" );
	Log.printLine("Cloudlet ID" + indent +" Task Execution Length" + indent + indent + "Total Finish Time" + indent + indent + indent + "Execution Time Changed" + indent + indent + indent +indent + "Total DownTime" + indent +indent + indent + "Total Migration Overheads" + indent +indent + indent + "Total Re-execution Time" + indent + indent + indent + "Energy Consumption without Reexecution" + indent + indent + indent + "Energy Consumption with Reexecution" );
	
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== OUTPUT For Energy Wastage Cloudlet Level ==========");
	//Log.printLine("Cloudlet ID" + indent + "VM ID" + indent + "Cloudlet Initial Length" +indent+ "Energy Wastage");
	Log.printLine("Cloudlet ID"  + indent + indent + "Reexecution Energy per Host" + indent + indent + "Migration DownTime Energy" + indent + indent + "Total Energy Wastage per Host");

	HashMap<Integer, Double>vmEnergyWastageTable = new HashMap<Integer, Double>();
	//HashMap<Integer, Double>vmEnergyWastageTableLastHost = new HashMap<Integer, Double>();
	
	//double reexecutionEnergyLastHost = 0.0;
	double reexecutionEnergy = 0.0;
	double migrationDownTimeEnergy = 0.0;
	double totalCletEnergyWastage = 0.0;
	//double totalCletEnergyWastageLastHost = 0.0;
	for (int i = 0; i < size; i++) {				
		cloudlet = sufferedClets.get(i);		
		vm = VmCloudletMapping.getVm(sufferedClets.get(i));
		if(cletEnergyWastageWithReexecution.containsKey(cloudlet.getCloudletId())){
			reexecutionEnergy = cletEnergyWastageWithReexecution.get(cloudlet.getCloudletId());
		}
		else{
			reexecutionEnergy = 0.0;
		}		
		//reexecutionEnergyLastHost = cletEnergyWastageWithReexecutionLastHost.get(cloudlet.getCloudletId());
		if(cletEnergyWastageWithMigrationDownTime.containsKey(cloudlet.getCloudletId())){
			migrationDownTimeEnergy = cletEnergyWastageWithMigrationDownTime.get(cloudlet.getCloudletId());
		}
		else{
			migrationDownTimeEnergy = 0.0;
		}
		
		totalCletEnergyWastage = reexecutionEnergy + migrationDownTimeEnergy;
		//totalCletEnergyWastageLastHost = reexecutionEnergyLastHost + migrationDownTimeEnergy;
		
		if(vmEnergyWastageTable.isEmpty()){
			vmEnergyWastageTable.put(vm.getId(), totalCletEnergyWastage);
		}
		else
		{				
			double tempVmEnergyWastage;				
			if(vmEnergyWastageTable.containsKey(vm.getId())){
				tempVmEnergyWastage = vmEnergyWastageTable.get(vm.getId());
				tempVmEnergyWastage = tempVmEnergyWastage + totalCletEnergyWastage;
				vmEnergyWastageTable.put(vm.getId(), tempVmEnergyWastage);
			}
			else{
				vmEnergyWastageTable.put(vm.getId(), totalCletEnergyWastage);
			}				
		}			
		/*
		if(vmEnergyWastageTableLastHost.isEmpty()){
			vmEnergyWastageTableLastHost.put(vm.getId(), totalCletEnergyWastageLastHost);
		}
		else
		{				
			double tempVmEnergyWastage;				
			if(vmEnergyWastageTableLastHost.containsKey(vm.getId())){
				tempVmEnergyWastage = vmEnergyWastageTableLastHost.get(vm.getId());
				tempVmEnergyWastage = tempVmEnergyWastage + totalCletEnergyWastageLastHost;
				vmEnergyWastageTableLastHost.put(vm.getId(), tempVmEnergyWastage);
			}
			else{
				vmEnergyWastageTableLastHost.put(vm.getId(), totalCletEnergyWastageLastHost);
			}				
		}	
		*/
		Log.printLine(indent + cloudlet.getCloudletId()  
					  +indent + indent +indent + indent + indent + dft.format(reexecutionEnergy)
					 // +indent + indent +indent + indent + dft.format(reexecutionEnergyLastHost)
					  +indent + indent +indent + indent + indent +indent + indent + dft.format(migrationDownTimeEnergy)
					  +indent + indent +indent + indent + indent +indent + indent + dft.format(totalCletEnergyWastage));
					 // +indent + indent +indent + indent + dft.format(totalCletEnergyWastageLastHost));			
		
	}

	Log.printLine("========== OUTPUT For Energy Wastage Cloudlet Level ==========");
	//Log.printLine("Cloudlet ID" + indent + "VM ID" + indent + "Cloudlet Initial Length" +indent+ "Energy Wastage");
	Log.printLine("Cloudlet ID"  + indent + indent + "Reexecution Energy per Host" + indent + indent + "Migration DownTime Energy" + indent + indent + "Total Energy Wastage per Host");

	
//	FileWriter HostCountClock = null;
//	FileWriter HostCount = null;
	Log.printLine();
	Log.printLine();
	Log.printLine("========== OUTPUT For Host Count ==========");	
	Log.printLine(indent + "Clock" + indent + indent + indent + "Host Count" );
//	try{
//	HostCountClock = new FileWriter("/raid/disc1/ysharma1/Java/Results/Simulation_File/HostCountClock.txt", false);
//	HostCount = new FileWriter("/raid/disc1/ysharma1/Java/Results/Simulation_File/HostCount.txt", false);
	ArrayList<Integer>hostCount = new ArrayList<Integer>();
	ArrayList<Double>clockForHostCount = new ArrayList<Double>();
	hostCount = HostCounter.getHostCountList();
	clockForHostCount = HostCounter.getClockList();
	for(int i=0;i<hostCount.size();i++){
//		HostCountClock.write((clockForHostCount.get(i))+System.getProperty("line.seperator"));
//		HostCount.write((hostCount.get(i))+System.getProperty("line.seperator"));
		Log.printLine(indent + clockForHostCount.get(i)  
		  +indent + indent +indent + hostCount.get(i)
		  );
		}
//	HostCountClock.close();
//	HostCount.close();
	
//	}catch(IOException e){
	//	System.out.println("Error has been occurred while writing output for host count");
//	}
	Log.printLine("========== OUTPUT For Host Count ==========");		
	Log.printLine(indent + "Clock" + indent + indent + indent + "Host Count" );
	
	
	Log.printLine();
	Log.printLine();
	//Log.printLine("Total Energy Consumption" + indent + indent + indent + "Total Energy Consumption with Reexecution" + indent + indent + indent + "Total Energy Wastage per Host" + indent + indent + indent + "Total Energy Wastage Last Host"+ indent + indent + indent + "Total Idle Energy Consumption"  +indent +indent + indent + "Application Utility Value" );
	Log.printLine("Total Energy Consumption" + indent + indent + indent + "Total Energy Wastage " +  indent + indent + indent + "Total Idle Energy Consumption"  +indent +indent + indent + "Application Utility Value" );
	
	
		double totalReliability;	
		double totalReliabilityParallel = 0.0;
		double totalReliabilityAverage = 0.0;
		double totalAvailability = 0.0;
		double totalMaintainability = 0.0;
		double percentageAchievedDeadlines;
		//ArrayList<Double>energyConsumptionperHost = new ArrayList<Double>();		
		totalReliability = 0.0;		
		int systemReliabilityListSize;
		ArrayList<Double>systemReliabilityList = new ArrayList<Double>();
		ArrayList<Double>systemReliabilityListParallel = new ArrayList<Double>();
		ArrayList<Double>systemReliabilityListAverage = new ArrayList<Double>();
		ArrayList<Double>systemAvailabilityList = new ArrayList<Double>();
		ArrayList<Double>systemMaintainabilityList = new ArrayList<Double>();
		systemReliabilityList.addAll(hvmRbl.getSystemReliability());
		systemReliabilityListSize = hvmRbl.getSystemReliability().size();
		systemReliabilityListParallel.addAll(hvmRbl.getSystemReliabilityParallel());
		systemReliabilityListAverage.addAll(hvmRbl.getSystemReliabilityAverage());
		systemAvailabilityList.addAll(hvmRbl.getSystemAvailability());
		systemMaintainabilityList.addAll(hvmRbl.getSystemMaintainability());
		for(int i=0;i<systemReliabilityListSize;i++){
			totalReliability = totalReliability + systemReliabilityList.get(i);
			totalReliabilityParallel = totalReliabilityParallel + systemReliabilityListParallel.get(i);
			totalReliabilityAverage = totalReliabilityAverage + systemReliabilityListAverage.get(i);
			totalAvailability = totalAvailability + systemAvailabilityList.get(i);
			totalMaintainability = totalMaintainability + systemMaintainabilityList.get(i);
		//	AverageSystemReliability.write((dft.format(systemReliabilityListAverage.get(i)))+ System.getProperty( "line.separator"));
		}
		totalReliability = totalReliability/systemReliabilityListSize;
		totalReliabilityParallel = totalReliabilityParallel/systemReliabilityListSize;
		totalReliabilityAverage = totalReliabilityAverage/systemReliabilityListSize;
		totalAvailability = (totalAvailability/systemReliabilityListSize)*100;
		totalMaintainability = (totalMaintainability/systemReliabilityListSize)*100;			
		
		double totalEnergyConsumption = 0.0;
		double totalEnergyConsumptionwithReexecution = 0.0;
		double totalEnergyWastage = 0.0;
	//	double totalEnergyWastagewithLastHost = 0.0;
		for(int i=0; i<newVmList.size();i++){
			//totalEnergyConsumption = totalEnergyConsumption + vmEnergyConsumptionTable.get(newVmList.get(i).getId());
			totalEnergyConsumption = totalEnergyConsumptionwithReexecution = totalEnergyConsumptionwithReexecution + vmEnergyConsumptionTablewithReexecution.get(newVmList.get(i).getId());
			totalEnergyWastage = totalEnergyWastage + vmEnergyWastageTable.get(newVmList.get(i).getId());
		//	totalEnergyWastagewithLastHost = totalEnergyWastagewithLastHost + vmEnergyWastageTableLastHost.get(newVmList.get(i).getId());
		}
		
		jobUtilityValue = jobUtilityValue/size;			
	
		percentageAchievedDeadlines = (((double)countDeadlineAchieved*100)/((double)countDeadlineAchieved+(double)countDeadlineMissed));
		
		Log.printLine( indent + dft.format(totalEnergyConsumption)+		
			//	indent + indent + indent + indent + indent +dft.format(totalEnergyConsumptionwithReexecution)+
				indent + indent + indent + indent + indent + indent +dft.format(totalEnergyWastage)+
				//indent + indent + indent + indent + indent +dft.format(totalEnergyWastagewithLastHost)+
				indent + indent + indent + indent + indent + indent + indent +dft.format(totalIdleEnergyConsumption)+									
				indent + indent + indent + indent + indent + indent + indent + indent +dft.format(jobUtilityValue));	
			
	Log.printLine("========== Total OUTPUT ==========");
	//Log.printLine(indent + "Total Energy Consumption" + indent + indent + indent + "Total Energy Consumption with Reexecution" + indent + indent + indent + "Total Energy Wastage per Host" + indent + indent + indent + "Total Energy Wastage Last Host"+ indent + indent + indent + "Total Idle Energy Consumption"  +indent +indent + indent + "Application Utility Value" );
	Log.printLine(indent + "Total Energy Consumption" + indent + indent + indent + "Total Energy Wastage " + indent + indent + indent + "Total Idle Energy Consumption"  +indent +indent + indent + "Application Utility Value" );
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Deadlines OUTPUT ==========");
	//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Idle Energy Consumption" +indent+ "Total Idle Time" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
	Log.printLine(indent + "Total Deadlines Achieved" + indent + indent + "Total Deadlines Missed" + indent + indent + "Percentage Achieved Deadlines");
	
	Log.printLine( indent + indent + indent + countDeadlineAchieved +
			indent + indent + indent +  indent + indent + indent +  indent + indent +countDeadlineMissed+
			indent + indent + indent + indent + indent +  indent + indent + dft.format(percentageAchievedDeadlines));		
	
	Log.printLine("========== Deadlines OUTPUT ==========");
	//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Idle Energy Consumption" +indent+ "Total Idle Time" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
	Log.printLine(indent + "Total Deadlines Achieved" + indent + indent + "Total Deadlines Missed" + indent + indent + "Percentage Achieved Deadlines");
	
	
	
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Reliability OUTPUT ==========");
	//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Idle Energy Consumption" +indent+ "Total Idle Time" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
	Log.printLine(indent + "Total Reliability Serial" + indent + indent + "Total Reliability Parallel" + indent + indent + "Total Reliability Average" + indent + indent + "Total Availability" + indent + indent + "Total Maintainability");
	
	Log.printLine( indent + dft.format(totalReliability*100) +
			indent + indent + indent + indent + indent + dft.format(totalReliabilityParallel*100)+
			indent + indent + indent + indent + indent + indent + indent + dft.format(totalReliabilityAverage*100)+
			indent + indent + indent + indent + indent + indent + dft.format(totalAvailability)+
			indent + indent + indent + indent + indent + indent + dft.format(totalMaintainability));		
	
	Log.printLine("========== Reliability OUTPUT ==========");
	//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Idle Energy Consumption" +indent+ "Total Idle Time" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
	Log.printLine(indent + "Total Reliability Serial" + indent + indent + "Total Reliability Parallel" + indent + indent + "Total Reliability Average" + indent + indent + "Total Availability" + indent + indent + "Total Maintainability");
	
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average OUTPUTs ==========");
	Log.printLine("Average Task Length" + indent + indent+ indent + indent+ "Average Makespan" + indent + indent+ indent + indent+ "Average Execution Time Changed");
			
	double averageCletCompletionTime;
	double averageCletLength;
	double averageExecutionTimeChanged;		
	averageCletCompletionTime = totalMakeSpan/size;
	averageCletLength = totalCletLength/size;
	averageExecutionTimeChanged = totalExecutionTimeChanged/size;		
	Log.printLine( indent +dft.format(averageCletLength/3600) + indent + indent + indent + indent +indent +dft.format(averageCletCompletionTime/3600) + indent + indent + indent + indent + indent + indent +dft.format(averageExecutionTimeChanged/3600));
	Log.printLine();
	Log.printLine( indent +dft.format(averageCletLength) + indent + indent + indent + indent +indent +dft.format(averageCletCompletionTime) + indent + indent + indent + indent + indent +dft.format(averageExecutionTimeChanged));
	
	Log.printLine("========== Average OUTPUTs ==========");
	Log.printLine("Average Task Length" + indent + indent+ indent + indent+ "Average Makespan" + indent + indent+ indent + indent+ "Average Execution Time Changed");
	
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Consolidation OUTPUTs ==========");
	//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Idle Energy Consumption" +indent+ "Total Idle Time" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
	Log.printLine("Total Failures Occurred" + indent + indent + indent + indent+ "Total VM Consolidations " );
	Log.printLine(indent + indent + totalFailureCount + indent + indent + indent + indent + indent + indent + indent + indent + indent+ totalVmConsolidations);
	Log.printLine("Total Failures Occurred" + indent + indent + indent + indent+ "Total VM Consolidations " );
	
	Log.printLine("========== Consolidation OUTPUTs ==========");
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Overhead OUTPUTs ==========");
	//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Idle Energy Consumption" +indent+ "Total Idle Time" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
	Log.printLine("Total DownTime" + indent + indent + indent + indent+ "Total Reexecution Time " + indent + indent + indent + indent+ "Total Hosts "  + indent + indent + indent + indent+ "Total Migration DownTime "  + indent + indent + indent + indent+ "Total Migration Overhead ");
	Log.printLine(dft.format(finalTotalDownTime/3600) + indent + indent + indent + indent+ indent + indent+ dft.format(finalTotalReexecutionTime/3600)+ indent + indent + indent + indent+ indent + indent + indent + hostList.size()+ indent + indent + indent + indent+ indent + indent+ indent + dft.format(totalMigrationDownTime/3600) + indent + indent + indent + indent + indent + indent + indent+ dft.format(totalMigrationOverhead/3600));
	Log.printLine();
	Log.printLine(dft.format(finalTotalDownTime) + indent + indent + indent + indent+ indent + indent+ dft.format(finalTotalReexecutionTime)+ indent + indent + indent + indent+ indent + indent + indent + hostList.size()+ indent + indent + indent + indent+ indent + indent+ indent + dft.format(totalMigrationDownTime) + indent + indent + indent + indent + indent + indent + indent+ dft.format(totalMigrationOverhead));

	//Log.printLine();
	//Log.printLine((finalTotalDownTime) + indent + indent + indent + indent+ (finalTotalReexecutionTime)+ indent + indent + indent + indent+ indent + indent+ hostList.size()+ indent + indent + indent + indent+ indent + indent+ totalMigrationDownTime + indent + indent + indent + indent+ totalMigrationOverhead);
	
	Log.printLine("Total DownTime" + indent + indent + indent + indent+ "Total Reexecution Time" + indent + indent + indent + indent+ "Total Hosts "  + indent + indent + indent + indent+ "Total Migration DownTime "  + indent + indent + indent + indent+ "Total Migration Overhead ");	
	Log.printLine("========== Overhead OUTPUTs ==========");	
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Application Finishing Time ==========");
	Log.printLine( indent + indent + dft.format(applicationFinishingTime));
	Log.printLine();
	Log.printLine("========== Application Finishing Time ==========");

	
}

private static void printCloudletListCheckpointingwithConsolidation(List<Cloudlet> list, List<Host> hostList, FTAFileReaderGrid5000 ftaread, FailureDatacenterBroker broker, HostVMMappingReliability hvmRbl, List<Vm>newVmList, VmUtilization vmUtl, PowerModel pow){
	
	Double initTime;	
	double totalMakeSpan = 0.0;
	Double changeinExecutionTime;
	Double totalDownTime;		
	int countDeadlineMissed = 0;
	int countDeadlineAchieved = 0;
	double totalReexecutionTime;	
	double totalDeadline = 0;		
	double totalMigrationDownTime = 0;
	double totalMigrationOverhead = 0;
	double applicationFinishingTime = 0.0;
	Vm vm;	
	
	int size = sufferedClets.size(); //For only suffered cloudlets and corresponding VMs. 
	Cloudlet cloudlet;		//Host host;
	
	System.out.println("Printing results for Restarting with Migration Fault Tolerance Mechanism");
	String indent = "    ";
	Log.printLine();
	Log.printLine("========== OUTPUT ==========");
	
	Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
			"Data center ID" + indent + indent + "VM ID" + indent +  indent + "Bag ID" + indent +  indent +"Cloudlet Initial Length" + indent + "Intial Start Time" + indent + indent +"Finish Time" +indent+ indent+ "Turnaround Time" +indent+ indent+ "Deadline Value" +indent+ indent+ "Deadline Actual Value"+indent+ indent+ "Utility Value"+indent+ indent +"Deadline");
	
	DecimalFormat dft = new DecimalFormat("###.####");			
	
	for (int i = 0; i < size; i++) {		
		cloudlet = sufferedClets.get(i);			
		double cloudletInitialLength = 0.0;			
		vm = VmCloudletMapping.getVm(cloudlet);
		int bagID;
		if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS){
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);
			Log.print("SUCCESS");			
			initTime = broker.getcloudletInitialStartTime(cloudlet);		
			if(cloudletTableBackup.containsKey(cloudlet.getCloudletId())){						
				cloudletInitialLength = ((double)cloudletTableBackup.get(cloudlet.getCloudletId()))/1000;
			}		
				
			double deadline;			
			double utilityValue;
			double finishedTime;
			double downTime;
			double downTimeforMigration;
			double reExecutionTime;
			//double migrationOverheads;
			double turnAroundTime;				
			bagID = BagTaskMapping.getBagID(cloudlet.getCloudletId());				
			downTime = DownTimeTable.getCloudletDownTime(cloudlet.getCloudletId());				
			downTimeforMigration = DownTimeTable.getCloudletDownTimeforMigration(cloudlet.getCloudletId());				
			downTime = downTime + downTimeforMigration;				
			reExecutionTime = totalReexecutionTime = ((double)CloudletReexecutionPart.getCletReexecutionTime(cloudlet.getCloudletId()))/1000;				
		//	migrationOverheads = MigrationOverheadTable.getCletMigrationOverhead(cloudlet);				
			finishedTime = cloudlet.getFinishTime();				
			turnAroundTime = finishedTime - initTime;				
			totalMakeSpan = totalMakeSpan + turnAroundTime;			
			finalTotalReexecutionTime = finalTotalReexecutionTime + reExecutionTime;				
			deadline = initTime + (stringencyFactor * (cloudletInitialLength));			
			if(finishedTime<=deadline){		
				utilityValue = 1;
			}
			else{					
				utilityValue = (1-((finishedTime-deadline)/deadline));
				if(utilityValue<0){
					utilityValue = 0;
				}
			}	
			jobUtilityValue = jobUtilityValue + utilityValue;				
			totalDeadline = totalDeadline + (deadline-initTime);		
			
			if(deadline > finishedTime){	
				countDeadlineAchieved = countDeadlineAchieved + 1;
			Log.printLine( indent + indent + cloudlet.getResourceId() + indent + indent + indent + indent + cloudlet.getVmId() +
					indent + indent + indent + indent + bagID +
					indent + indent + indent + indent + dft.format(cloudletInitialLength) +
					indent + indent + indent + indent + indent + dft.format(initTime) +						
					indent + indent + indent +  dft.format(finishedTime)+
					indent + indent + indent +  dft.format(turnAroundTime)+
					indent + indent + indent +  dft.format(deadline)+
					indent + indent + indent +  dft.format(deadline-initTime)+
					indent + indent + indent +  dft.format(utilityValue)+
					indent + indent + indent+ "Achieved" );
			}
			else{					
				countDeadlineMissed = countDeadlineMissed + 1;
				Log.printLine( indent + indent + cloudlet.getResourceId() + indent + indent + indent + indent + cloudlet.getVmId() +
						indent + indent + indent + indent + bagID +
						indent + indent + indent + indent + dft.format(cloudletInitialLength) +
						indent + indent + indent + indent + indent + dft.format(initTime) +							
						indent + indent + indent +  dft.format(finishedTime)+
						indent + indent + indent +  dft.format(turnAroundTime)+
						indent + indent + indent +  dft.format(deadline)+
						indent + indent + indent +  dft.format(deadline-initTime)+
						indent + indent + indent +  dft.format(utilityValue)+
						indent + indent + indent+ "Missed" );
				}
			}
		}
	
	Log.printLine("========== OUTPUT ==========");
	
	Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
			"Data center ID" + indent + indent + "VM ID" + indent + indent + "Bag ID" + indent +  indent + "Cloudlet Initial Length" + indent + "Intial Start Time" + indent + indent +"Finish Time" +indent+ indent+ "Turnaround Time" +indent+ indent+ "Deadline Value" +indent+ indent+ "Deadline Actual Value"+indent+ indent+ "Utility Value"+indent+ indent +"Deadline");
	
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== OUTPUT For Bag Level Information ==========");
	//Log.printLine("Job ID" + indent + indent + indent + "Finishing Time");
	Log.printLine( "Finishing Time");
	int bagCount;
	int cletBagID;	
	bagCount = RunTimeConstants.numberofBags;
	for(int i=0; i<bagCount; i++){
		double jobFinishingTime = 0;
		double turnAroundTime = 0;
		for(int j=0; j<size; j++){
			cloudlet = sufferedClets.get(j);
			initTime = broker.getcloudletInitialStartTime(cloudlet);	
			turnAroundTime = cloudlet.getFinishTime() - initTime;
			cletBagID = BagTaskMapping.getBagID(cloudlet.getCloudletId());
			if(i == cletBagID){
				if(jobFinishingTime < turnAroundTime){
					jobFinishingTime = turnAroundTime;
				}
			}
		}
		if(applicationFinishingTime < jobFinishingTime){
			applicationFinishingTime = jobFinishingTime;
		}
		//Log.printLine( indent + i + indent + indent + indent + indent + dft.format(jobFinishingTime));
		Log.printLine( indent + dft.format(jobFinishingTime));
	}
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== OUTPUT For Bag Level Information ==========");
	///Log.printLine("Job ID" + indent + indent + indent + "Finishing Time");
	Log.printLine( "Finishing Time");
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== OUTPUT For Host Information ==========");
	Log.printLine("Host ID" + indent + "FTA Host ID" + indent +" MTBF" + indent + "MTTR" +indent+ "Max Hazard Rate" +indent+ "Current Hazard Rate" +indent+ "Host Power" +indent+ "Host Availability" +indent+ "Host Maintainability");
	
	int ftaHostID;	
	for(int i=0;i<HostMapping.getHostMapSize();i++){
		ftaHostID = HostMapping.getFTAhostID(i);
		
		Log.printLine(  i + indent + indent + ftaHostID +
				indent + indent + indent + indent + dft.format(ftaread.getMTBF(ftaHostID)) +
				indent + indent + indent + indent + dft.format(ftaread.getMTTR(ftaHostID)) +
				indent + indent + indent + indent + ftaread.getMaxHazardRate(ftaHostID)+
				indent + indent + indent + indent + ftaread.getCurrentHazardRate(ftaHostID)+
				indent + indent + indent + indent + dft.format(ftaread.getPowerforHost(ftaHostID))+
				indent + indent + indent + indent + dft.format(ftaread.getAvailability(ftaHostID))+
				indent + indent + indent + indent + dft.format(ftaread.getMaintainability(ftaHostID))
				);			
	}	
	
	Log.printLine("========== OUTPUT For Host Information ==========");
	Log.printLine("Host ID" + indent + "FTA Host ID" + indent +" MTBF" + indent + "MTTR" +indent+ "Max Hazard Rate" +indent+ "Current Hazard Rate" +indent+ "Host Power" +indent+ "Host Availability" +indent+ "Host Maintainability");
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== OUTPUT For Host Idle Time  ==========");
	Log.printLine("Host ID" +indent+ indent + indent + "Total Idle Time" +indent+ indent + indent + "Total Down Time" +indent+ indent + indent + "Total Idle Energy Consumption" +indent+ indent + indent + "Average Idle Energy Consumption");		
	
	int hostIdIdle;
	//double idleTimewithDownTime;
	double idleTime;
	double averageIdleTime;
	int peListSize;
	double idleUtilization = 0.0;
	double idlePower = 0.0;		
	double totalIdleEnergyConsumption = 0.0;
	double idleEnergyConsumption = 0.0;
	double averageIdleEnergyConsumption = 0.0;
	double totalAverageIdleEnergyConsumption = 0.0;
	double downTime;
	List<Pe>peList;
	for(int i=0; i<hostList.size(); i++){
		idleTime = 0.0;	
		downTime = 0.0;
		peList = new ArrayList<Pe>();
		ftaHostID = HostMapping.getFTAhostID(hostList.get(i).getId());
		idleUtilization = ftaread.getCurrentUtilization(ftaHostID);
		hostIdIdle = hostList.get(i).getId();			
		peListSize = hostList.get(i).getNumberOfPes();
		peList = hostList.get(i).getPeList();
		for(int j=0; j<peList.size(); j++){
			//idleTimewithDownTime = idleTimewithDownTime + IdleTime.getPeIdleTime(hostList.get(i).getId(), peList.get(j).getId());
			idleTime = idleTime + IdleTime.getPeIdleTime(hostList.get(i).getId(), peList.get(j).getId());
			downTime = downTime + IdleTime.getPeDownTime(hostList.get(i).getId(), peList.get(j).getId());
		}			
		//averageIdleTime = idleTime/peListSize;
		//idleTime = idleTimewithDownTime - downTime;
		averageIdleTime = idleTime;
		idlePower = pow.getPower(idleUtilization, peListSize);
		idleEnergyConsumption = ((idlePower/3600) * (averageIdleTime/1000));
		averageIdleEnergyConsumption = idleEnergyConsumption/peListSize;
		totalIdleEnergyConsumption = totalIdleEnergyConsumption + idleEnergyConsumption; 
		totalAverageIdleEnergyConsumption = totalAverageIdleEnergyConsumption + averageIdleEnergyConsumption;
		Log.printLine( indent+ hostIdIdle + indent + indent+ indent+ idleTime + indent + indent+ indent+ downTime + indent + indent+ indent+ idleEnergyConsumption+ indent + indent+ indent+ averageIdleEnergyConsumption);
	}

	Log.printLine("========== OUTPUT For Host Idle Time  ==========");
	Log.printLine("Host ID" +indent+ indent + indent + "Total Idle Time" +indent+ indent + indent + "Total Down Time" +indent+ indent + indent + "Total Idle Energy Consumption" +indent+ indent + indent + "Average Idle Energy Consumption" );		
		
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== OUTPUT For Energy Consumption Cloudlet Level==========");
	//Log.printLine("Cloudlet ID" + indent +" Task Execution Length" + indent + indent + "Total Finish Time" + indent + indent + indent + "Execution Time Changed" + indent + indent + indent +indent + "Total DownTime" + indent +indent + indent + "Total Migration Overheads" + indent +indent + indent + "Total Re-execution Time" +indent+indent + indent + "Energy Consumption" );
	Log.printLine("Cloudlet ID" + indent +" Task Execution Length" + indent + indent + "Total Finish Time" + indent + indent + indent + "Execution Time Changed" + indent + indent + indent +indent + "Total DownTime" + indent +indent + indent + "Total Checkpoint Overheads" + indent +indent + indent + "Total Migration Overheads" + indent +indent + indent + "Total Re-execution Time" +indent+indent + indent + "Energy Consumption without Reexecution " +indent+indent + indent + "Energy Consumption with Reexecution ");
	
	double totalCletLength = 0.0;
	double totalExecutionTimeChanged = 0.0;		
	double finalChkptOverhead = 0.0;
	double cloudletInitialLength;
	double turnAroundTime;
	double reexecutionCletLength;	
	
	ArrayList<Integer>hostIDListCletMigrated;
	ArrayList<Host>hostListCletMigrated;
	ArrayList<Double>hostListCletExecutedSoFar;		
	ArrayList<Host>hostListCletMigrationDownTime;
	ArrayList<Integer>hostIDListCletMigrationDownTime;
	ArrayList<Double>cletMigrationDownTime;
	ArrayList<Double>cletMigrationOverheads;
	
	HashMap<Integer, Double>cletEnergyConsumption = new HashMap<Integer, Double>();
	HashMap<Integer, Double>cletEnergyConsumptionWithoutReexecution = new HashMap<Integer, Double>();
	HashMap<Integer, Double>cletEnergyConsumptionWithoutChkpt = new HashMap<Integer, Double>();
	HashMap<Integer, Double>cletEnergyConsumptionWithMigrationDownTime = new HashMap<Integer, Double>();
	HashMap<Integer, Double>cletEnergyWastageWithMigrationDownTime = new HashMap<Integer, Double>();
	HashMap<Integer, Double>cletEnergyWastageWithReexecution = new HashMap<Integer, Double>();
	HashMap<Integer, Double>cletEnergyWastageWithCheckpointing = new HashMap<Integer, Double>();
	HashMap<Integer, Double>vmEnergyConsumptionTable = new HashMap<Integer, Double>();
	HashMap<Integer, Double>vmEnergyConsumptionTablewithReexecution = new HashMap<Integer, Double>();
	//HashMap<Integer, Double>vmEnergyConsumptionTablewithCheckpointing = new HashMap<Integer, Double>();
	
	
	//int lastHostIDforClet;
	//Host lastHostforClet = null;
	double vmUtilization;

	for(int i=0; i<size; i++){			
		hostIDListCletMigrated = new ArrayList<Integer>();
		hostListCletMigrated = new ArrayList<Host>();
		cloudletInitialLength = 0.0;
		initTime = 0.0;
		turnAroundTime = 0.0;
		reexecutionCletLength = 0.0;
		changeinExecutionTime = 0.0;
		totalDownTime = 0.0;		
		
		vm = VmCloudletMapping.getVm(sufferedClets.get(i));
		initTime = broker.getcloudletInitialStartTime(sufferedClets.get(i));	
		Log.print(indent + sufferedClets.get(i).getCloudletId() + indent + indent);	
		if(cloudletTableBackup.containsKey(sufferedClets.get(i).getCloudletId())){		
			cloudletInitialLength = ((double)cloudletTableBackup.get(sufferedClets.get(i).getCloudletId()))/1000;			
			totalCletLength = totalCletLength + cloudletInitialLength;
			}
		turnAroundTime = sufferedClets.get(i).getFinishTime() - initTime;
		changeinExecutionTime = turnAroundTime - cloudletInitialLength;
		
		totalExecutionTimeChanged = totalExecutionTimeChanged + changeinExecutionTime;
		
		vmUtilization = vmUtl.getVmAverageUtilization(vm.getId());
		hostIDListCletMigrated = vmRecord.getVmMigrationHostList(sufferedClets.get(i).getCloudletId());
		hostListCletExecutedSoFar = vmRecord.getVmExecutionDurationPerHostList(sufferedClets.get(i).getCloudletId());
		
		for(int k=0; k<hostIDListCletMigrated.size(); k++){
			for(int l=0; l<hostList.size(); l++){
				if(hostList.get(l).getId()==hostIDListCletMigrated.get(k)){
					hostListCletMigrated.add(hostList.get(l));
					break;
				}
			}
		}
		double power;
		double cletEnergy = 0.0;
		double cletReexecutionEnergy = 0.0;
		//double energyWastageforReexecution;
		double cletDownTimePerHost;	
		double cletReexecutionPerHost;
		double cletExecutionPerHost;
		double energyConsumptionwithoutReexecution;
		double energyConsumptionwithoutChkpt;
		double actualCletEnergyConsumption;
		double chkptOverheads = 0.0;
		double totalChkptOverhead = 0.0;
		
		int coreCount;
		double cletChkptEnergyConsumption = 0.0;
		double cletEnergyWithoutChkpt = 0.0;
		//double idlePower;
		downTime = 0.0;
		
		for(int j=0; j<hostIDListCletMigrated.size(); j++){				
			coreCount = hostListCletMigrated.get(j).getNumberOfPes();
			ftaHostID = HostMapping.getFTAhostID(hostIDListCletMigrated.get(j));
			idleUtilization = ftaread.getCurrentUtilization(ftaHostID);;
			power = pow.getPower(vmUtilization, coreCount);			
			idlePower = pow.getPower(idleUtilization, coreCount);
		//	power = HostVMMapping.gethostVMMapPower(hostListCletMigrated.get(j), vm);
			cletDownTimePerHost = DownTimeTable.getCletPerHostDownTimeTable(sufferedClets.get(i).getCloudletId(), hostListCletMigrated.get(j).getId());
			cletReexecutionPerHost = CloudletReexecutionPart.getCletPerHostReexecutionTimeTable(sufferedClets.get(i).getCloudletId(), hostListCletMigrated.get(j).getId());
			chkptOverheads = CheckpointOverhead.getCheckpointOverheadPerHostTable(sufferedClets.get(i).getCloudletId(), hostListCletMigrated.get(j).getId());
			cletExecutionPerHost = (hostListCletExecutedSoFar.get(j)/1000) + (cletReexecutionPerHost/1000) - cletDownTimePerHost - (chkptOverheads);
			downTime = downTime + cletDownTimePerHost;
			cletChkptEnergyConsumption = cletChkptEnergyConsumption + ((1.15*(idlePower/1000))*(chkptOverheads/3600));
			cletEnergyWithoutChkpt = cletEnergyWithoutChkpt + ((power/1000)*(cletExecutionPerHost/3600));
			cletEnergy = cletEnergy + (cletChkptEnergyConsumption + cletEnergyWithoutChkpt);
			cletReexecutionEnergy = cletReexecutionEnergy + ((power/1000)*(cletReexecutionPerHost/3600000));
			reexecutionCletLength = reexecutionCletLength + (cletReexecutionPerHost/1000);
			totalChkptOverhead = totalChkptOverhead + (chkptOverheads);
			finalChkptOverhead = finalChkptOverhead + chkptOverheads;
		}	
		
		hostIDListCletMigrationDownTime = new ArrayList<Integer>();
		hostListCletMigrationDownTime = new ArrayList<Host>();
		cletMigrationDownTime = new ArrayList<Double>();
		cletMigrationOverheads = new ArrayList<Double>();			
		
		hostIDListCletMigrationDownTime = vmRecord.getCletMigrationHostRecordforDownTime(sufferedClets.get(i).getCloudletId());
		cletMigrationOverheads = vmRecord.getVmMigrationOverheadPerHostList(sufferedClets.get(i).getCloudletId());
		
		double cletMigrationOverheadEnergyPerHost = 0.0; 
		double cletMigrationOverheadEnergy = 0.0;
		double totalMigrationOverheads = 0.0;
		
		if(hostIDListCletMigrationDownTime != null){
			for(int k=0; k<hostIDListCletMigrationDownTime.size(); k++){
				for(int l=0; l<hostList.size(); l++){
					if(hostList.get(l).getId()==hostIDListCletMigrationDownTime.get(k)){
						hostListCletMigrationDownTime.add(hostList.get(l));
						break;
					}
				}
			}			
		
			for(int j=0; j<hostIDListCletMigrationDownTime.size(); j++){	
				coreCount = hostListCletMigrationDownTime.get(j).getNumberOfPes();
				power = pow.getPower(vmUtilization, coreCount);
				cletMigrationOverheadEnergyPerHost = (((cletMigrationOverheads.get(j))/3600)*(power/1000));
				totalMigrationOverheads = totalMigrationOverheads + cletMigrationOverheads.get(j);
				cletMigrationOverheadEnergy = cletMigrationOverheadEnergy + cletMigrationOverheadEnergyPerHost;			
			}
		
			cletEnergy = cletEnergy - cletMigrationOverheadEnergy;
		}
		cletEnergyConsumption.put(sufferedClets.get(i).getCloudletId(), cletEnergy);
		cletEnergyWastageWithReexecution.put(sufferedClets.get(i).getCloudletId(), cletReexecutionEnergy);
		cletEnergyWastageWithCheckpointing.put(sufferedClets.get(i).getCloudletId(), cletChkptEnergyConsumption);
		
		/**
		lastHostIDforClet = vmRecord.getLastHostforClet(sufferedClets.get(i).getCloudletId());
		
		for(int j=0; j<hostIDListCletMigrated.size(); j++){
			if(hostListCletMigrated.get(j).getId()==lastHostIDforClet){
				lastHostforClet = hostListCletMigrated.get(j);
				break;
			}
		}
		
		coreCount = lastHostforClet.getNumberOfPes();
		//power = HostVMMapping.gethostVMMapPower(lastHostforClet, vm);
		power = pow.getPower(vmUtilization, coreCount);
		reexecutionCletLength = CloudletReexecutionPart.getCletReexecutionTime(sufferedClets.get(i).getCloudletId())/1000;
		energyWastageforReexecution = ((power/1000) * (reexecutionCletLength/3600));
		cletEnergyWastageWithReexecution.put(sufferedClets.get(i).getCloudletId(), energyWastageforReexecution);
		
		energyConsumptionwithoutReexecution = cletEnergy - energyWastageforReexecution;
		*/
		energyConsumptionwithoutReexecution = cletEnergy - cletReexecutionEnergy;
		energyConsumptionwithoutChkpt = energyConsumptionwithoutReexecution - cletChkptEnergyConsumption;
		cletEnergyConsumptionWithoutReexecution.put(sufferedClets.get(i).getCloudletId(), energyConsumptionwithoutReexecution);		
		cletEnergyConsumptionWithoutChkpt.put(sufferedClets.get(i).getCloudletId(), energyConsumptionwithoutChkpt);
	
		cletMigrationDownTime = vmRecord.getVmMigrationDownTimePerHostList(sufferedClets.get(i).getCloudletId());		
		
		double cletMigrationDownTimeEnergyPerHost = 0.0;
		double cletMigrationDownTimeEnergy = 0.0;
		double migrationDownTime = 0.0;
		double actualCletEnergyConsumptionWithReexecution = 0.0;
		
		if(cletMigrationDownTime!=null){
			for(int j=0; j<hostIDListCletMigrationDownTime.size(); j++){
				ftaHostID = HostMapping.getFTAhostID(hostIDListCletMigrationDownTime.get(j));
				idleUtilization = ftaread.getCurrentUtilization(ftaHostID);
				coreCount = hostListCletMigrationDownTime.get(j).getNumberOfPes();
				power = pow.getPower(idleUtilization, coreCount);
				cletMigrationDownTimeEnergyPerHost = ((cletMigrationDownTime.get(j)/3600)*(power/1000));				
				cletMigrationDownTimeEnergy = cletMigrationDownTimeEnergy + cletMigrationDownTimeEnergyPerHost;		
				migrationDownTime = migrationDownTime + cletMigrationDownTime.get(j);
			}
			cletEnergyWastageWithMigrationDownTime.put(sufferedClets.get(i).getCloudletId(), cletMigrationDownTimeEnergy);
			actualCletEnergyConsumption = energyConsumptionwithoutReexecution + cletMigrationDownTimeEnergy;
			actualCletEnergyConsumptionWithReexecution = cletEnergy + cletMigrationDownTimeEnergy;
			cletEnergyConsumptionWithMigrationDownTime.put(sufferedClets.get(i).getCloudletId(), actualCletEnergyConsumption);
			totalDownTime = downTime + migrationDownTime;
			totalMigrationDownTime = totalMigrationDownTime + migrationDownTime;
		}
		else{
			actualCletEnergyConsumption = energyConsumptionwithoutReexecution;
			actualCletEnergyConsumptionWithReexecution = cletEnergy;
			totalDownTime = downTime;
		}		
		
		finalTotalDownTime = finalTotalDownTime + totalDownTime;
		
		if(vmEnergyConsumptionTable.isEmpty()){
			vmEnergyConsumptionTable.put(vm.getId(), actualCletEnergyConsumption);
			vmEnergyConsumptionTablewithReexecution.put(vm.getId(), actualCletEnergyConsumptionWithReexecution);		
		}
		else
		{				
			double tempVmEnergy;
			double tempVmEnergywithReexecution;
			if(vmEnergyConsumptionTable.containsKey(vm.getId())){
				tempVmEnergy = vmEnergyConsumptionTable.get(vm.getId());
				tempVmEnergywithReexecution = vmEnergyConsumptionTablewithReexecution.get(vm.getId());
				tempVmEnergy = tempVmEnergy + actualCletEnergyConsumption;
				tempVmEnergywithReexecution = tempVmEnergywithReexecution + actualCletEnergyConsumptionWithReexecution;
				vmEnergyConsumptionTable.put(vm.getId(), tempVmEnergy);
				vmEnergyConsumptionTablewithReexecution.put(vm.getId(), tempVmEnergywithReexecution);
			}
			else{
				vmEnergyConsumptionTable.put(vm.getId(), actualCletEnergyConsumption);
				vmEnergyConsumptionTablewithReexecution.put(vm.getId(), actualCletEnergyConsumptionWithReexecution);
			}				
		}
		totalMigrationOverhead = totalMigrationOverhead + totalMigrationOverheads;
		
		Log.printLine( indent + indent + dft.format(cloudletInitialLength)+
					indent + indent  + indent + indent + dft.format(turnAroundTime)+
					indent + indent + indent + indent + dft.format(changeinExecutionTime)+
					indent + indent + indent + indent + indent + dft.format(totalDownTime)+					
					indent + indent + indent  + indent + indent + dft.format(totalChkptOverhead)+	
					indent + indent + indent  + indent + indent + dft.format(totalMigrationOverheads)+	
					indent + indent + indent  + indent + indent + dft.format(reexecutionCletLength)+				
					indent + indent + indent + indent +dft.format(actualCletEnergyConsumption)+
					indent + indent + indent + indent +indent + indent + indent +dft.format(actualCletEnergyConsumptionWithReexecution));		
	}		
	
	
	Log.printLine("========== OUTPUT For Energy Consumption Cloudlet Level ==========");
	Log.printLine("Cloudlet ID" + indent +" Task Execution Length" + indent + indent + "Total Finish Time" + indent + indent + indent + "Execution Time Changed" + indent + indent + indent +indent + "Total DownTime" + indent +indent + indent + "Total Checkpoint Overheads"+ indent +indent + indent + "Total Migration Overheads" + indent +indent + indent + "Total Re-execution Time" +indent+indent + indent + "Energy Consumption without Reexecution " +indent+indent + indent + "Energy Consumption with Reexecution ");

	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== OUTPUT For Energy Wastage Cloudlet Level ==========");
	//Log.printLine("Cloudlet ID" + indent + "VM ID" + indent + "Cloudlet Initial Length" +indent+ "Energy Wastage");
	Log.printLine("Cloudlet ID" + indent + indent + "Reexecution Energy" + indent + indent + "Migration DownTime Energy" + indent + indent + "Checkpoint Energy"+ indent + indent + "Total Energy Wastage");
	
	double reexecutionEnergy = 0.0;
	double migrationDownTimeEnergy = 0.0;
	double checkpointEnergy = 0.0;
	double totalCletEnergyWastage = 0.0;
	
	HashMap<Integer, Double>vmEnergyWastageTable = new HashMap<Integer, Double>();
	
	for (int i = 0; i < size; i++) {				
		cloudlet = sufferedClets.get(i);
		vm = VmCloudletMapping.getVm(sufferedClets.get(i));
		if(cletEnergyWastageWithReexecution.containsKey(cloudlet.getCloudletId())){
			reexecutionEnergy = cletEnergyWastageWithReexecution.get(cloudlet.getCloudletId());
		}
		else{
			reexecutionEnergy = 0.0;
		}
		if(cletEnergyWastageWithMigrationDownTime.containsKey(cloudlet.getCloudletId())){
			migrationDownTimeEnergy = cletEnergyWastageWithMigrationDownTime.get(cloudlet.getCloudletId());
		}
		else{
			migrationDownTimeEnergy = 0.0;
		}
		if(cletEnergyWastageWithCheckpointing.containsKey(cloudlet.getCloudletId())){
			checkpointEnergy = cletEnergyWastageWithCheckpointing.get(cloudlet.getCloudletId());	
		}
		else{
			checkpointEnergy = 0.0;
		}
		
		totalCletEnergyWastage = reexecutionEnergy + migrationDownTimeEnergy + checkpointEnergy;	
		
		if(vmEnergyWastageTable.isEmpty()){
			vmEnergyWastageTable.put(vm.getId(), totalCletEnergyWastage);
		}
		else
		{				
			double tempVmEnergyWastage;				
			if(vmEnergyWastageTable.containsKey(vm.getId())){
				tempVmEnergyWastage = vmEnergyWastageTable.get(vm.getId());
				tempVmEnergyWastage = tempVmEnergyWastage + totalCletEnergyWastage;
				vmEnergyWastageTable.put(vm.getId(), tempVmEnergyWastage);
			}
			else{
				vmEnergyWastageTable.put(vm.getId(), totalCletEnergyWastage);
			}				
		}			
		
		Log.printLine(indent + cloudlet.getCloudletId()  
					  +indent + indent +indent + indent + dft.format(reexecutionEnergy)
					  +indent + indent +indent + indent + dft.format(migrationDownTimeEnergy)
					  +indent + indent +indent + indent + dft.format(checkpointEnergy)
					  +indent + indent +indent + indent + dft.format(totalCletEnergyWastage));			
		
	}

	Log.printLine("========== OUTPUT For Energy Wastage Cloudlet Level ==========");
	//Log.printLine("Cloudlet ID" + indent + "VM ID" + indent + "Cloudlet Initial Length" +indent+ "Energy Wastage");
	Log.printLine("Cloudlet ID"  + indent + indent + "Reexecution Energy" + indent + indent + "Migration DownTime Energy" + indent + indent + "Checkpoint Energy" + indent + indent + "Total Energy Wastage");

//	FileWriter HostCountClock = null;
//	FileWriter HostCount = null;
	Log.printLine();
	Log.printLine();
	Log.printLine("========== OUTPUT For Host Count ==========");	
	Log.printLine(indent + "Clock" + indent + indent + indent + "Host Count" );
//	try{
//	HostCountClock = new FileWriter("/raid/disc1/ysharma1/Java/Results/Simulation_File/HostCountClock.txt", false);
//	HostCount = new FileWriter("/raid/disc1/ysharma1/Java/Results/Simulation_File/HostCount.txt", false);
	ArrayList<Integer>hostCount = new ArrayList<Integer>();
	ArrayList<Double>clockForHostCount = new ArrayList<Double>();
	hostCount = HostCounter.getHostCountList();
	clockForHostCount = HostCounter.getClockList();
	for(int i=0;i<hostCount.size();i++){
//		HostCountClock.write((clockForHostCount.get(i))+System.getProperty("line.seperator"));
//		HostCount.write((hostCount.get(i))+System.getProperty("line.seperator"));
		Log.printLine(indent + clockForHostCount.get(i)  
		  +indent + indent +indent + hostCount.get(i)
		  );
		}
//	HostCountClock.close();
//	HostCount.close();
	
//	}catch(IOException e){
	//	System.out.println("Error has been occurred while writing output for host count");
//	}
	Log.printLine("========== OUTPUT For Host Count ==========");		
	Log.printLine(indent + "Clock" + indent + indent + indent + "Host Count" );
	
	
	Log.printLine();
	Log.printLine();
	Log.printLine(indent + "Total Energy Consumption" + indent + indent + indent + "Total Energy Wastage" + indent + indent + indent + "Total Idle Energy Consumption"  +indent +indent + indent + "Application Utility Value" );
	
		double totalReliability;	
		double totalReliabilityParallel = 0.0;
		double totalReliabilityAverage = 0.0;
		double totalAvailability = 0.0;
		double totalMaintainability = 0.0;
		double percentageAchievedDeadlines;
		//ArrayList<Double>energyConsumptionperHost = new ArrayList<Double>();		
		totalReliability = 0.0;		
		int systemReliabilityListSize;
		ArrayList<Double>systemReliabilityList = new ArrayList<Double>();
		ArrayList<Double>systemReliabilityListParallel = new ArrayList<Double>();
		ArrayList<Double>systemReliabilityListAverage = new ArrayList<Double>();
		ArrayList<Double>systemAvailabilityList = new ArrayList<Double>();
		ArrayList<Double>systemMaintainabilityList = new ArrayList<Double>();
		systemReliabilityList.addAll(hvmRbl.getSystemReliability());
		systemReliabilityListSize = hvmRbl.getSystemReliability().size();
		systemReliabilityListParallel.addAll(hvmRbl.getSystemReliabilityParallel());
		systemReliabilityListAverage.addAll(hvmRbl.getSystemReliabilityAverage());
		systemAvailabilityList.addAll(hvmRbl.getSystemAvailability());
		systemMaintainabilityList.addAll(hvmRbl.getSystemMaintainability());
		for(int i=0;i<systemReliabilityListSize;i++){
			totalReliability = totalReliability + systemReliabilityList.get(i);
			totalReliabilityParallel = totalReliabilityParallel + systemReliabilityListParallel.get(i);
			totalReliabilityAverage = totalReliabilityAverage + systemReliabilityListAverage.get(i);
			totalAvailability = totalAvailability + systemAvailabilityList.get(i);
			totalMaintainability = totalMaintainability + systemMaintainabilityList.get(i);
		//	AverageSystemReliability.write((dft.format(systemReliabilityListAverage.get(i)))+ System.getProperty( "line.separator"));
		}
		totalReliability = totalReliability/systemReliabilityListSize;
		totalReliabilityParallel = totalReliabilityParallel/systemReliabilityListSize;
		totalReliabilityAverage = totalReliabilityAverage/systemReliabilityListSize;
		totalAvailability = (totalAvailability/systemReliabilityListSize)*100;
		totalMaintainability = (totalMaintainability/systemReliabilityListSize)*100;			
		
		double totalEnergyConsumption = 0.0;		
		double totalEnergyConsumptionwithReexecution = 0.0;
		double totalEnergyWastage = 0.0;			
		for(int i=0; i<newVmList.size();i++){
			//totalEnergyConsumption = totalEnergyConsumption + vmEnergyConsumptionTable.get(newVmList.get(i).getId());
			totalEnergyConsumption = totalEnergyConsumptionwithReexecution = totalEnergyConsumptionwithReexecution + vmEnergyConsumptionTablewithReexecution.get(newVmList.get(i).getId());
			totalEnergyWastage = totalEnergyWastage + vmEnergyWastageTable.get(newVmList.get(i).getId());
			
		}
		
		jobUtilityValue = jobUtilityValue/size;			
	
		percentageAchievedDeadlines = (((double)countDeadlineAchieved*100)/((double)countDeadlineAchieved+(double)countDeadlineMissed));
		
		Log.printLine( indent + indent + dft.format(totalEnergyConsumption)+	
			//	indent + indent + indent + indent + indent + indent + indent +dft.format(totalEnergyConsumptionwithReexecution)+
				indent + indent + indent + indent + indent + indent + indent + indent +dft.format(totalEnergyWastage)+
				indent + indent + indent + indent + indent + indent + indent +dft.format(totalIdleEnergyConsumption)+									
				indent + indent + indent + indent + indent + indent + indent +dft.format(jobUtilityValue));	
			
	Log.printLine("========== Total OUTPUT ==========");
	//Log.printLine(indent + "Total Energy Consumption" + indent + indent + indent + "Total Energy Consumption with Reexecution"+ indent + indent + indent + "Total Energy Wastage" + indent + indent + indent + "Total Idle Energy Consumption"  +indent +indent + indent + "Application Utility Value" );
	Log.printLine(indent + "Total Energy Consumption" + indent + indent + indent + "Total Energy Wastage" + indent + indent + indent + "Total Idle Energy Consumption"  +indent +indent + indent + "Application Utility Value" );
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Deadlines OUTPUT ==========");
	//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Idle Energy Consumption" +indent+ "Total Idle Time" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
	Log.printLine(indent + "Total Deadlines Achieved" + indent + indent + "Total Deadlines Missed" + indent + indent + "Percentage Achieved Deadlines");
	
	Log.printLine( indent + indent + indent + countDeadlineAchieved +
			indent + indent + indent +  indent + indent + indent + indent +countDeadlineMissed+
			indent + indent + indent + indent + indent + indent + indent + dft.format(percentageAchievedDeadlines));		
	
	Log.printLine("========== Deadlines OUTPUT ==========");
	//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Idle Energy Consumption" +indent+ "Total Idle Time" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
	Log.printLine(indent + "Total Deadlines Achieved" + indent + indent + "Total Deadlines Missed" + indent + indent + "Percentage Achieved Deadlines");
	
	
	
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Reliability OUTPUT ==========");
	//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Idle Energy Consumption" +indent+ "Total Idle Time" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
	Log.printLine(indent + "Total Reliability Serial" + indent + indent + "Total Reliability Parallel" + indent + indent + "Total Reliability Average" + indent + indent + "Total Availability" + indent + indent + "Total Maintainability");
	
	Log.printLine( indent+ indent + dft.format(totalReliability*100) +
			indent + indent + indent + indent + indent + indent + indent + dft.format(totalReliabilityParallel*100)+
			indent + indent + indent + indent + indent + indent + indent + dft.format(totalReliabilityAverage*100)+
			indent + indent + indent + indent + indent + indent + indent + dft.format(totalAvailability)+
			indent + indent + indent + indent + indent + dft.format(totalMaintainability));		
	
	Log.printLine("========== Reliability OUTPUT ==========");
	//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Idle Energy Consumption" +indent+ "Total Idle Time" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
	Log.printLine(indent + "Total Reliability Serial" + indent + indent + "Total Reliability Parallel" + indent + indent + "Total Reliability Average" + indent + indent + "Total Availability" + indent + indent + "Total Maintainability");
	
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average OUTPUTs ==========");
	Log.printLine("Average Task Length" + indent + indent+ indent + indent+ "Average Makespan" + indent + indent+ indent + indent+ "Average Execution Time Changed");
			
	double averageCletCompletionTime;
	double averageCletLength;
	double averageExecutionTimeChanged;		
	averageCletCompletionTime = totalMakeSpan/size;
	averageCletLength = totalCletLength/size;
	averageExecutionTimeChanged = totalExecutionTimeChanged/size;		
	Log.printLine( indent +dft.format(averageCletLength/3600) + indent + indent + indent + indent +indent + indent +indent +dft.format(averageCletCompletionTime/3600) + indent + indent + indent + indent + indent + indent +dft.format(averageExecutionTimeChanged/3600));
	Log.printLine();
	Log.printLine( indent +dft.format(averageCletLength) + indent + indent + indent + indent +indent +dft.format(averageCletCompletionTime) + indent + indent + indent + indent + indent +dft.format(averageExecutionTimeChanged));
	
	Log.printLine("========== Average OUTPUTs ==========");
	Log.printLine("Average Task Length" + indent + indent+ indent + indent+ "Average Makespan" + indent + indent+ indent + indent+ "Average Execution Time Changed");
	
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Migration OUTPUTs ==========");
	//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Idle Energy Consumption" +indent+ "Total Idle Time" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
	//if(RunTimeConstants.vmConsolidationFlag == false){
		Log.printLine("Total Failures Occurred" + indent + indent + indent + indent+ "Total Checkpoints " + indent + indent + indent + indent+ "Total VM Consolidations " );
		Log.printLine(indent + indent + totalFailureCount + indent + indent + indent + indent + indent + indent + indent + indent+ totalCheckpointsCount + indent + indent + indent + indent + indent + indent + indent + indent+ totalVmConsolidations );
		Log.printLine("Total Failures Occurred" + indent + indent + indent + indent+ "Total Checkpoints " + indent + indent + indent + indent+ "Total VM Consolidations " );
	//}else{
	//	Log.printLine("Total Failures Occurred" + indent + indent + indent + indent+ "Total VM Migrations " + indent + indent + indent + indent+ "Total VM Consolidations ");
	//	Log.printLine(indent + indent + totalFailureCount + indent + indent + indent + indent + indent + indent+ indent + indent + indent+ totalVmMigrations + indent + indent + indent + indent+ indent+ indent + indent + indent+ totalVmConsolidations);
	//	Log.printLine("Total Failures Occurred" + indent + indent + indent + indent+ "Total VM Migrations " + indent + indent + indent + indent+ "Total VM Consolidations ");
	//}	
	
	Log.printLine("========== Migration OUTPUTs ==========");
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Overhead OUTPUTs ==========");
	//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Idle Energy Consumption" +indent+ "Total Idle Time" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
	Log.printLine("Total DownTime" + indent + indent + indent + indent+ "Total Reexecution Time " + indent + indent + indent + indent+ "Total Hosts "  + indent + indent + indent + indent+ "Total Migration DownTime "  + indent + indent + indent + indent+ "Total Migration Overhead "+ indent + indent + indent + indent+ "Total Checkpoint Overheads ");
	Log.printLine(indent + dft.format(finalTotalDownTime/3600) + indent + indent + indent + indent + indent + dft.format(finalTotalReexecutionTime/3600)+ indent + indent + indent + indent + indent + indent + indent+ hostList.size()+ indent + indent + indent + indent + indent + indent + dft.format(totalMigrationDownTime/3600) + indent + indent + indent + indent + indent + indent+ indent + indent + indent + dft.format(totalMigrationOverhead/3600) + indent + indent + indent + indent+ indent+ indent + indent + indent+ indent+ dft.format(finalChkptOverhead/3600));
	Log.printLine();
	Log.printLine(indent  + dft.format(finalTotalDownTime) + indent + indent + indent + indent+ dft.format(finalTotalReexecutionTime)+ indent + indent + indent + indent+ indent+ indent+ hostList.size()+ indent + indent + indent + indent+ indent + indent+ dft.format(totalMigrationDownTime) + indent + indent + indent + indent+ indent + indent+ indent + indent+ dft.format(totalMigrationOverhead)+ indent + indent + indent + indent+ indent + indent+ indent + indent+ dft.format(finalChkptOverhead));
	
	Log.printLine("Total DownTime" + indent + indent + indent + indent+ "Total Reexecution Time" + indent + indent + indent + indent+ "Total Hosts "  + indent + indent + indent + indent+ "Total Migration DownTime "  + indent + indent + indent + indent+ "Total Migration Overhead " + indent + indent + indent + indent+ "Total Checkpoint Overheads ");
	
	Log.printLine("========== Overhead OUTPUTs ==========");
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Checkpoint Overheads ==========");
	Log.printLine(dft.format(finalChkptOverhead));
	Log.printLine();
	Log.printLine("========== Total Checkpoint Overheads ==========");
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Checkpoint Overheads Hours ==========");
	Log.printLine(dft.format(finalChkptOverhead/3600));
	Log.printLine();
	Log.printLine("========== Total Checkpoint Overheads Hours ==========");
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Migration DownTime ==========");
	Log.printLine(dft.format(totalMigrationDownTime));
	Log.printLine();
	Log.printLine("========== Total Migration DownTime ==========");
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Migration DownTime Hours ==========");
	Log.printLine(dft.format(totalMigrationDownTime/3600));
	Log.printLine();
	Log.printLine("========== Total Migration DownTime Hours ==========");
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Migration Overheads ==========");
	Log.printLine(dft.format(totalMigrationOverhead));
	Log.printLine();
	Log.printLine("========== Total Migration Overheads ==========");
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Migration Overheads Hours ==========");
	Log.printLine(dft.format(totalMigrationOverhead/3600));
	Log.printLine();
	Log.printLine("========== Total Migration Overheads Hours ==========");
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Hosts ==========");
	Log.printLine(hostList.size());
	Log.printLine();
	Log.printLine("========== Total Hosts ==========");
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Reexecution Time Hours ==========");
	Log.printLine(dft.format(finalTotalReexecutionTime/3600));
	Log.printLine();
	Log.printLine("========== Total Reexecution Time Hours ==========");
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Reexecution Time  ==========");
	Log.printLine(dft.format(finalTotalReexecutionTime));
	Log.printLine();
	Log.printLine("========== Total Reexecution Time ==========");	
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total DownTime Hours  ==========");
	Log.printLine(dft.format(finalTotalDownTime/3600));
	Log.printLine();
	Log.printLine("========== Total DownTime Hours==========");	
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total DownTime  ==========");
	Log.printLine(dft.format(finalTotalDownTime));
	Log.printLine();
	Log.printLine("========== Total DownTime ==========");	
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Application Finishing Time ==========");
	Log.printLine(dft.format(applicationFinishingTime));
	Log.printLine();
	Log.printLine("========== Application Finishing Time ==========");
	
	
}



}
	
	

//}
