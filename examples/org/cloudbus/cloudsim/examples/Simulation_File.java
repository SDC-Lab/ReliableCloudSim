package org.cloudbus.cloudsim.examples;

/**
 * This Simulation uses Reliability Aware Best Fit Decreasing algorithm to provision the hosts and before allocation of VM to the provisioned
 * nodes, it sort all the cloudlets running on the VMs in decreasing order according to their lengths. 
 * 
 * The fault tolerance mechanism used in this simulation is Restarting
 * 
 * */


import java.io.BufferedReader;
import java.io.BufferedWriter;
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
import org.cloudbus.cloudsim.distributions.LognormalDistr;
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
import org.cloudbus.cloudsim.failure.FailurePredictionSynthetic;
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
import org.cloudbus.cloudsim.failure.K_MeansAlgorithm;
import org.cloudbus.cloudsim.failure.K_MeansAlgorithmLANL;
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
import org.cloudbus.cloudsim.frequency.CheckNodeCompatibility;
import org.cloudbus.cloudsim.frequency.DeadlineModel;
import org.cloudbus.cloudsim.frequency.FirstLotofVMs;
import org.cloudbus.cloudsim.frequency.FrequencyDetails;
import org.cloudbus.cloudsim.frequency.FrequencyModelSpecPowerHpProLiantDL380G5;
import org.cloudbus.cloudsim.frequency.FrequencyModelSpecPowerHpProLiantDL560Gen9;
import org.cloudbus.cloudsim.frequency.FrequencyModelSpecPowerHpProLiantDL785G5AMD8376;
import org.cloudbus.cloudsim.frequency.FrequencyModelSpecPowerIntelSE7520AF2Xeon3600;
import org.cloudbus.cloudsim.frequency.FrequencyModelSpecPowerIntelXeonE54669;
import org.cloudbus.cloudsim.frequency.FrequencyModelSpecPowerPlatHomeTRQX150SA;
import org.cloudbus.cloudsim.power.*;

public class Simulation_File{
	
//Create the list of cloudlets
	private static List<Cloudlet> cloudletList;
	
//Create the list of virtual machines
	//private static List<Vm> vmList;
	private static List<Vm> vmList;
	
	
	//private static List<Vm> vmListForHostProvisioning;
	private static List<Vm> vmListForHostProvisioning = new ArrayList<Vm>();;

	//public static List<Host> hostListBackup = new ArrayList<Host>();
	public static List<Host> hostListBackup = new ArrayList<Host>();
	
//Create the list of backup cloudlets
	//private static List<Cloudlet> cloudletListbackup = new ArrayList<Cloudlet>();
	
	private static HashMap<Integer, Long> cloudletTableBackup = new HashMap<Integer, Long>();
	
	private static long cloudlet_max = 0;
	
	private static int total_core_count = 0;
	
	private static FTAFileReader ftareadLANL;
	
	private static FTAFileReaderGrid5000 ftareadGrid5000;
	
	//public static Object ftaread;
	
	public static PowerModelSpecPowerHpProLiantDL560Gen9 PMcore128and80 = new PowerModelSpecPowerHpProLiantDL560Gen9();
	
	public static PowerModelSpecPowerHpProLiantDL380G5 PMcore4Mem8 = new PowerModelSpecPowerHpProLiantDL380G5();
	
	public static PowerModelSpecPowerHpProLiantDL785G5AMD8376 PMcore32 = new PowerModelSpecPowerHpProLiantDL785G5AMD8376();
	
	public static PowerModelSpecPowerIntelSE7520AF2Xeon3600 PMcore2 = new PowerModelSpecPowerIntelSE7520AF2Xeon3600();
	
	public static PowerModelSpecPowerIntelXeonE54669 PMcore256 = new PowerModelSpecPowerIntelXeonE54669();
	
	public static PowerModelSpecPowerPlatHomeTRQX150SA PMcore4Mem16 = new PowerModelSpecPowerPlatHomeTRQX150SA();
	
	public static BinPacking bpack = new BinPacking();
	
	public static K_MeansAlgorithm kmeans ;
	
	public static K_MeansAlgorithmLANL kmeansLANL ;
	
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
	
	public static FailurePredictionSynthetic failPredictsynthetic = new FailurePredictionSynthetic();
	
	public static VmUtilization vmUtl = new VmUtilization();
	
	public static HostVMMappingReliability hvmRbl = new HostVMMappingReliability();
	
	public static MigrationOverheadModel mgOver = new MigrationOverheadModel();
	
	public static VmMigrationRecord vmRecord = new VmMigrationRecord();
	
	public static int totalVmMigrations = 0;
	
	public static int totalVmConsolidations = 0;
	
	public static double finalTotalDownTime = 0.0;
	
	public static double finalTotalReexecutionTime = 0.0;
	
	public static WeibullDistr wblBagSize;
	
	public static boolean frequencyScaling = true;
	
	/**
	 * Following four arraylists are to store the values for 6th chapter of thesis. 
	 */
	public static ArrayList<Integer>hostPerTimeBetweenFailure = new ArrayList<Integer>();
	public static ArrayList<Double>timeBetweenFailures = new ArrayList<Double>();
	
	public static ArrayList<Integer>hostPerTimeToReturn = new ArrayList<Integer>();
	public static ArrayList<Double>timeToReturns = new ArrayList<Double>();
	
	
	public static PowerModel pow = new PowerModel();
	

	/**
	 *  Following variables are for Frequency Scaling Evaluations
	 */
	public static DeadlineModel dlModel = new DeadlineModel();
	public static FrequencyDetails frqDtls = new FrequencyDetails();
	public static CheckNodeCompatibility chkNodeComp = new CheckNodeCompatibility();
	public static FirstLotofVMs firstVMLot = new FirstLotofVMs();
//	public static FrequencyModelSpecPowerHpProLiantDL560Gen9 pmCore128and80 = new FrequencyModelSpecPowerHpProLiantDL560Gen9();	
//	public static FrequencyModelSpecPowerHpProLiantDL380G5 pmCore4Mem8 = new FrequencyModelSpecPowerHpProLiantDL380G5();	
//	public static FrequencyModelSpecPowerHpProLiantDL785G5AMD8376 pmCore32 = new FrequencyModelSpecPowerHpProLiantDL785G5AMD8376();	
//	public static FrequencyModelSpecPowerIntelSE7520AF2Xeon3600 pmCore2 = new FrequencyModelSpecPowerIntelSE7520AF2Xeon3600();	
//	public static FrequencyModelSpecPowerIntelXeonE54669 pmCore256 = new FrequencyModelSpecPowerIntelXeonE54669();	
//	public static FrequencyModelSpecPowerPlatHomeTRQX150SA pmCore4Mem16 = new FrequencyModelSpecPowerPlatHomeTRQX150SA();
	
	
	
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
			/*
			taskRunTime = new LognormalDistr(RunTimeConstants.taskRunTimeMean, RunTimeConstants.taskRunTimeDeviation);
			for(int i=0; i<cloudlets; i++){
				double cletSize;
				cletSize = Math.ceil(taskRunTime.sample());
				if(cletSize == 0){
					continue;
				}
				else{
					if(cletSize < 0){
						cletSize = cletSize * (-1);
					}
				}
				cletSize = cletSize * 60 * 1000000;
				length_cloudlet[i] = (long)(cletSize);
			}
			*/
			for(int i=0;i<cletLengthTemp.size();i++){
		//	length_cloudlet[i] = (cletLengthTemp.get(i)*10000);
			length_cloudlet[i] = (cletLengthTemp.get(i)*100000); //This is the one
		//	length_cloudlet[i] = (cletLengthTemp.get(i)*100000000);
		//		length_cloudlet[i] = (cletLengthTemp.get(i));
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
		BagTaskMapping.setMaxTaskPerBag(0, cloudlet_max);
		//UtilizationModel utilizationModel = new UtilizationModel10Percent();
		
		for(int i=0;i<cloudlets;i++){			
			//cloudlet[i] = new Cloudlet(i, length_cloudlet[i], pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
			cloudlet[i] = new Cloudlet(i, length_cloudlet[i], pesNumber, fileSize, outputSize, new UtilizationModelNormalized(length_cloudlet[i],cloudlet_max), new UtilizationModelNormalized(length_cloudlet[i],cloudlet_max), utilizationModel);
			// setting the owner of these Cloudlets
			cloudlet[i].setUserId(userId);
			list.add(cloudlet[i]);		
			//cloudletListbackup.add(cloudlet[i]);			
			cletNorm.setCloudletNormalizedLength(cloudlet[i], cloudlet_max);
			dlModel.calCletDeadline(cloudlet[i].getCloudletId(), cloudlet[i].getCloudletLength());
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
			firstVMLot.addVMID(vmID);			
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
		
		/**
		 * Create a list to store the cores for a machine
		 * To create heterogeneous machines, separate lists need to be created
		 * In the given data centers, there are five different kind of nodes containing 2, 4, 8, 16 and 32 physical elements/cores.		
		 * To accommodate different number of physical elements such that to create heterogeneous machines, separate Pe lists need to be created.
		 */
		
		
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
		
		// For two core list, peId = 2		
		peId=2;
		for(int i=0; i<peId; i++)
		{
			peList2.add(new Pe(i, new PeProvisionerSimple(mips)));
		}
		peList.add(peList2);
		PeListforFailures.setPeList(peList2);
		
		// For four core list, peId = 4		
		peId=4;
		for(int i=0; i<peId; i++)
		{
			peList4.add(new Pe(i, new PeProvisionerSimple(mips)));
		}
		peList.add(peList4);
		PeListforFailures.setPeList(peList4);
		
		// For four core list, peId = 8		
		peId=8;
		for(int i=0; i<peId; i++)
		{
			peList8.add(new Pe(i, new PeProvisionerSimple(mips)));
		}
		peList.add(peList8);
		PeListforFailures.setPeList(peList8);
		
		// For thirty two core list, peId = 32		
		peId=32;
		for(int i=0; i<peId; i++)
		{
			peList32.add(new Pe(i, new PeProvisionerSimple(mips)));
		}
		peList.add(peList32);
		PeListforFailures.setPeList(peList32);
		
		// For eighty core list, peId = 80		
		peId=80;
		for(int i=0; i<peId; i++)
		{
			peList80.add(new Pe(i, new PeProvisionerSimple(mips)));
		}
		peList.add(peList80);
		PeListforFailures.setPeList(peList80);
		
		// For one twenty eight core list, peId = 128		
		peId=128;
		for(int i=0; i<peId; i++)
		{
			peList128.add(new Pe(i, new PeProvisionerSimple(mips)));
		}
		peList.add(peList128);
		PeListforFailures.setPeList(peList128);
		
		// For two fifty six core list, peId = 256		
		peId=256;
		for(int i=0; i<peId; i++)
		{
			peList256.add(new Pe(i, new PeProvisionerSimple(mips)));
		}
		peList.add(peList256);	
		PeListforFailures.setPeList(peList256);
		
		//Create the host list with processing cores list (peList)
		int host_count = 1;		
		System.out.println("\n");
		
		host_order = RunTimeConstants.host_order;
			switch(host_order){
				case 1:
					System.out.println(" Energy aware resource provisioning is being done");					
					if(RunTimeConstants.traceType.equals("LANL")){
						ftareadLANL.setNodeListPower();
						ftareadLANL.prepareCurrentHazardRate();
						ftareadLANL.setSortedPowerNodeID();
						ftareadLANL.setPowerHostListSorted();
						sortedFtaNodeList = ftareadLANL.getPowerHostListSorted(); // For energy conscious provisioning
					}
					if(RunTimeConstants.traceType.equals("Grid5000")){
						ftareadGrid5000.setNodeListPower();
						ftareadGrid5000.prepareCurrentHazardRate();
						ftareadGrid5000.setSortedPowerNodeID();
						ftareadGrid5000.setPowerHostListSorted();
						sortedFtaNodeList = ftareadGrid5000.getPowerHostListSorted(); // For energy conscious provisioning
					}										
					break;
					
				case 2:
					System.out.println(" Reliability aware resource provisioning is being done");
					if(RunTimeConstants.traceType.equals("LANL")){
						ftareadLANL.setNodeListPower();
						ftareadLANL.prepareCurrentHazardRate();
						ftareadLANL.setSortedHazardRateNodeID();
						ftareadLANL.setNodeIDListSorted();
						sortedFtaNodeList = ftareadLANL.getSortedNodeIDList();	// For reliability conscious provisioning
					}
					if(RunTimeConstants.traceType.equals("Grid5000")){
						ftareadGrid5000.setNodeListPower();
						ftareadGrid5000.prepareCurrentHazardRate();
						ftareadGrid5000.setSortedHazardRateNodeID();
						ftareadGrid5000.setNodeIDListSorted();
						sortedFtaNodeList = ftareadGrid5000.getSortedNodeIDList();	// For reliability conscious provisioning
					}					
					break;
					
				case 3:
					System.out.println(" Reliability and Energy aware resource provisioning is being done");
					if(RunTimeConstants.traceType.equals("LANL")){
						ftareadLANL.setNodeListPower();
						ftareadLANL.prepareCurrentHazardRate();
						ftareadLANL.setProductPowerandHazardRate();
						ftareadLANL.setSortedPowerandHazardRateNodeID();
						ftareadLANL.setPowerandHazardRateHostListSorted();
						sortedFtaNodeList = ftareadLANL.getPowerandHazardRateHostListSorted(); // For reliability and energy conscious provisioning						
					}
					if(RunTimeConstants.traceType.equals("Grid5000")){
						ftareadGrid5000.setNodeListPower();
						ftareadGrid5000.prepareCurrentHazardRate();
						ftareadGrid5000.setProductPowerandHazardRate();
						ftareadGrid5000.setSortedPowerandHazardRateNodeID();
						ftareadGrid5000.setPowerandHazardRateHostListSorted();
						sortedFtaNodeList = ftareadGrid5000.getPowerandHazardRateHostListSorted(); // For reliability and energy conscious provisioning						
					}
					break;
					
				case 4:
					System.out.println(" Random resource provisioning is being done");
					if(RunTimeConstants.traceType.equals("LANL")){
						sortedFtaNodeList = ftareadLANL.getNodeIDList();// For random provisioning		
						ftareadLANL.setNodeListPower();
						ftareadLANL.prepareCurrentHazardRate();
					}
					if(RunTimeConstants.traceType.equals("Grid5000")){
						sortedFtaNodeList = ftareadGrid5000.getNodeIDList();// For random provisioning	
						ftareadGrid5000.setNodeListPower();
						ftareadGrid5000.prepareCurrentHazardRate();
					}								
					break;
					
				case 5: 
					System.out.println(" Availability aware resource provisioning is being done");
					if(RunTimeConstants.traceType.equals("LANL")){
						ftareadLANL.createNodeAvailabilityList();
						ftareadLANL.setSortedAvailabilityNodeID();
						ftareadLANL.setAvailabilityHostListSorted();
						sortedFtaNodeList = ftareadLANL.getAvailabilityHostListSorted();
					}
					if(RunTimeConstants.traceType.equals("Grid5000")){
						ftareadGrid5000.createNodeAvailabilityList();
						ftareadGrid5000.setSortedAvailabilityNodeID();
						ftareadGrid5000.setAvailabilityHostListSorted();
						sortedFtaNodeList = ftareadGrid5000.getAvailabilityHostListSorted();
					}				
					break;
					
				case 6:				
					System.out.println(" Maintainability aware resource provisioning is being done");
					if(RunTimeConstants.traceType.equals("LANL")){
						ftareadLANL.createNodeMaintainabilityList();
						ftareadLANL.setSortedMaintainabilityNodeID();
						ftareadLANL.setMaintainabilityHostListSorted();
						sortedFtaNodeList = ftareadLANL.getMaintainabilityHostListSorted();
					}
					if(RunTimeConstants.traceType.equals("Grid5000")){
						ftareadGrid5000.createNodeMaintainabilityList();
						ftareadGrid5000.setSortedMaintainabilityNodeID();
						ftareadGrid5000.setMaintainabilityHostListSorted();
						sortedFtaNodeList = ftareadGrid5000.getMaintainabilityHostListSorted();
					}					
					break;
					
				case 7:				
					System.out.println(" Maintainability and Energy aware resource provisioning is being done");
					if(RunTimeConstants.traceType.equals("LANL")){
						ftareadLANL.createNodeMaintainabilityList();
						ftareadLANL.setNodeListPower();
						ftareadLANL.setProductPowerandMaintainability();
						ftareadLANL.setSortedPowerandMaintainabilityNodeID();
						ftareadLANL.setPowerandMaintainabilityHostListSorted();
						sortedFtaNodeList = ftareadLANL.getPowerandMaintainabilityHostListSorted();
					}
					if(RunTimeConstants.traceType.equals("Grid5000")){
						ftareadGrid5000.createNodeMaintainabilityList();
						ftareadGrid5000.setNodeListPower();
						ftareadGrid5000.setProductPowerandMaintainability();
						ftareadGrid5000.setSortedPowerandMaintainabilityNodeID();
						ftareadGrid5000.setPowerandMaintainabilityHostListSorted();
						sortedFtaNodeList = ftareadGrid5000.getPowerandMaintainabilityHostListSorted();
					}					
					break;	
				
				case 8:				
					System.out.println(" Availability and Energy aware resource provisioning is being done");
					if(RunTimeConstants.traceType.equals("LANL")){
						ftareadLANL.createNodeAvailabilityList();
						ftareadLANL.setNodeListPower();
						ftareadLANL.setFractionPowerandAvailability();	
						ftareadLANL.setSortedPowerandAvailabilityNodeID();
						ftareadLANL.setPowerandAvailabilityHostListSorted();
						sortedFtaNodeList = ftareadLANL.getPowerandAvailabilityHostListSorted();
					}
					if(RunTimeConstants.traceType.equals("Grid5000")){
						ftareadGrid5000.createNodeAvailabilityList();
						ftareadGrid5000.setNodeListPower();
						ftareadGrid5000.setFractionPowerandAvailability();	
						ftareadGrid5000.setSortedPowerandAvailabilityNodeID();
						ftareadGrid5000.setPowerandAvailabilityHostListSorted();
						sortedFtaNodeList = ftareadGrid5000.getPowerandAvailabilityHostListSorted();
					}					
					break;				
			}		
			
		int peListIndex = 0;		
		int hostId = 0;
		int ram = 0;
		long storage = 1000000;
		int bw = 10000;		
		double utilization = 0.0;
		int peCount = 0;
		int peCount1 = 0;
		
		System.out.println("Nodes have started being provisioned");	
		
		int fta_index = -1;
		int ftaNodeID = 0;	
		boolean check_host;
		boolean check_vmlist;
		boolean check_increase = false;
		boolean checkProvisionedHost = false;				
			
		/**
		 * host_order = 4 is the case for random selection of nodes.
		 */
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
				if(checkProvisionedHost == false){
					ftaNodeID = sortedFtaNodeList.get(fta_index);									
					//System.out.println("FTA ID of provisioned node is " +ftaNodeID);				
					HostMapping.createHostMap(hostId, ftaNodeID);
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
												
					for(int k=0;k<peList.size();k++){
						if(peList.get(k).size()==peCount){
							peListIndex = k;
							break;
						}
					}			
				check_host = false;
				peCount1 = 0;
				peCount1 = peCount1 + peList.get(peListIndex).size();			
				for(int t=0;t<vmListForHostProvisioning.size();){					
					if(peCount1>=vmListForHostProvisioning.get(t).getNumberOfPes()){						
						peCount1=peCount1 - vmListForHostProvisioning.get(t).getNumberOfPes();						
						vmListForHostProvisioning.remove(t);						
						t=0;						
						check_host = true;			
					}
					if(peCount1==0){
						break;
					}					
				}			
				if(check_host==true){											
					hostList.add(
						new Host(
								hostId,							
								new RamProvisionerSimple(ram), 
								new BwProvisionerSimple(bw), 
								storage,							
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
						break;
					}				
				}
				else{								
					if(check_increase == true){					
						i = i-1;					
						check_increase = false;
					}
				}					
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
				if(RunTimeConstants.workingWithFrequencyScaling == true) {					
					boolean nodeCompatible = false;
					while(nodeCompatible == false) {
						fta_index = fta_index + 1;
						ftaNodeID = sortedFtaNodeList.get(fta_index);						
						long cletLengthTemp = 0;
						int vmListIndex = 0;					
						for(int j=0; j<vmListForHostProvisioning.size(); j++) {
							if(cletLengthTemp < VmCloudletMapping.getLongestTaskFromAvailableVMs(vmListForHostProvisioning.get(j))) {
								cletLengthTemp = VmCloudletMapping.getLongestTaskFromAvailableVMs(vmListForHostProvisioning.get(j));		
								vmListIndex = j;
							}							
						}						
						//nodeCompatible = checkNodeCompatibility(ftaNodeID, vmListForHostProvisioning.get(vmListIndex));
						nodeCompatible = chkNodeComp.isNodeCompatible(ftaNodeID, vmListForHostProvisioning.get(vmListIndex));						
					}				
				}
				else {
					fta_index = fta_index + 1;				
					ftaNodeID = sortedFtaNodeList.get(fta_index);
				}			
				
				HostMapping.createHostMap(hostId, ftaNodeID);								
				//System.out.println("FTA ID of provisioned node is " +ftaNodeID);
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
			}			
		}
			
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
		
		if(RunTimeConstants.traceType.equals("LANL")){
			datacenter = new FailureDatacenter(name, characteristics, new VmAllocationPolicyFailureAware(hostList, ftareadLANL, vmUtl, hvmRbl, chkNodeComp, firstVMLot), storageList, 0);			
		}
		if(RunTimeConstants.traceType.equals("Grid5000")){
			datacenter = new FailureDatacenter(name, characteristics, new VmAllocationPolicyFailureAware(hostList, ftareadGrid5000, vmUtl, hvmRbl, chkNodeComp, firstVMLot), storageList, 0);			
		}
		
		
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
	
	/*
	public static boolean checkNodeCompatibility(int ftaNodeID, Vm vm) {	
		int coreCount = 0;		
		double deadline = 0;
		double tentativeCompletionTime = 0;
		coreCount = ftareadGrid5000.getProcessorCount(ftaNodeID);
		frqDtls.setFrequencyLevels(coreCount);
		frqDtls.setNormalizedFrequencyLevels();
		ArrayList<Cloudlet>cletList = new ArrayList<Cloudlet>();
		cletList.addAll(VmCloudletMapping.getCloudlet(vm));
		for(int i=0; i<cletList.size(); i++) {
			deadline = dlModel.getCletDeadline(cletList.get(i).getCloudletId());
			tentativeCompletionTime = cletList.get(i).getCloudletLength()/frqDtls.getNormalizedMaxFrequency(coreCount);
			if(tentativeCompletionTime<=deadline) {
				return true;
			}			
		}			
		return false;
	}
	*/

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
		int core_count = 0;		
		for(int i=0;i<hostListBackup.size();i++){
			if(RunTimeConstants.traceType.equals("LANL")){
				core_count = ftareadLANL.getProcessorCount(ftaHostID[i]);	
			}
			if(RunTimeConstants.traceType.equals("Grid5000")){
				core_count = ftareadGrid5000.getProcessorCount(ftaHostID[i]);
			}		
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
	
	if(RunTimeConstants.traceType.equals("LANL")){
		ftareadLANL = new FTAFileReader();
	}
	if(RunTimeConstants.traceType.equals("Grid5000")){
		ftareadGrid5000 = new FTAFileReaderGrid5000();
	}		
	
	if(RunTimeConstants.traceType.equals("LANL")) {
		chkNodeComp.setFTARead(ftareadLANL);
	}
	if(RunTimeConstants.traceType.equals("Grid5000")) {
		chkNodeComp.setFTARead(ftareadGrid5000);
	}
	chkNodeComp.setFrequencyDetails(frqDtls);
	chkNodeComp.setDeadlineModel(dlModel);
	
	broker.setCheckNodeCompatibility(chkNodeComp);
	
	//System.out.println("Data Rate is " +RunTimeConstants.dataRate);
	System.out.println("Data Rate is generated using Weibull Distribution using following parameters: ");
	System.out.println("Scale Parameter: " +RunTimeConstants.scaleParameter);
	System.out.println("Shape Parameter: " +RunTimeConstants.shapeParameter);
	//ReliabilityCalculator rblcal;
	
	
	
	//Create the list of Cloudlets or tasks
	//For the time being, we are assuming that there is only one user operating on the cloud. 
	
	//System.out.println("Enter the number of cloudlets for which the resources from the datacenter need to be provisioned");
	int clets;
	int botID = 0; // First bag of Tasks ID
	//Scanner abc;
	//abc = new Scanner(System.in);
	//clets=abc.nextInt();
	System.out.println("Bag Size is generated using Weibull Distribution using following parameters: ");
	System.out.println("Scale Parameter: " +RunTimeConstants.scaleParameterBagSize);
	System.out.println("Shape Parameter: " +RunTimeConstants.shapeParameterBagSize);
	
//	wblBagSize = new WeibullDistr(RunTimeConstants.scaleParameterBagSize, RunTimeConstants.shapeParameterBagSize);
	//clets = (int)(Math.pow(2, Math.ceil(wblBagSize.sample()))*20);
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
	
	//ftaread = new FTAFileReaderGrid5000();
	
	//kmeans = new K_MeansAlgorithm();
	
	//kmeans.setFTARead(ftaread);
	
	//ftaread.setK_MeansAlgorithm(kmeans);
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
	//vmListForHostProvisioning = vmList;
	vmListForHostProvisioning.addAll(vmList);
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
		datacenter0.setCheckNodeCompatibility(chkNodeComp);
		
		if(RunTimeConstants.syntheticPrediction == true){
			datacenter0.setFailurePredictorSynthetic(failPredictsynthetic);
		}else{
			datacenter0.setFailurePredictor(failPredict);
		}		
		if(RunTimeConstants.traceType.equals("LANL")){			
			ftareadLANL.setK_MeansAlgorithm(kmeansLANL);	
			hvmRbl.setFTARead(ftareadLANL);			
			datacenter0.setFTARead(ftareadLANL);
			datacenter0.setKMeans(kmeansLANL);
			failPredict.setFTAReader(ftareadLANL);	
			if(RunTimeConstants.syntheticPrediction == true) {
				failPredictsynthetic.setFTAReader(ftareadLANL);
			}
		}
		if(RunTimeConstants.traceType.equals("Grid5000")){
			//ftareadGrid5000.setK_MeansAlgorithm(kmeans);	
			hvmRbl.setFTARead(ftareadGrid5000);
			datacenter0.setFTARead(ftareadGrid5000);
			datacenter0.setKMeans(ftareadGrid5000.getKMeansObject());
			failPredict.setFTAReader(ftareadGrid5000);	
			if(RunTimeConstants.syntheticPrediction == true) {
				failPredictsynthetic.setFTAReader(ftareadGrid5000);
			}
		}
		
		broker.setDeadlineModel(dlModel);
		broker.setFrequencyDetails(frqDtls);
		datacenter0.setDeadlineModel(dlModel);
		datacenter0.setFrequencyDetails(frqDtls);
		
		broker.setDC(datacenter0);
		hvmRbl.setDatacenter(datacenter0);		
		datacenter0.setCletNorm(cletNorm);		
		datacenter0.setReliabilityCalculator(rblcal);
		datacenter0.setVmUtilizationObject(vmUtl);
		datacenter0.setHostVMMappingReliability(hvmRbl);
		datacenter0.setVmMigrationOverheadObject(mgOver);
		datacenter0.setVmMigrationRecordObject(vmRecord);		
		mgOver.setVmUtilizationObject(vmUtl);	
		if(RunTimeConstants.syntheticPrediction == true) {
			failPredictsynthetic.setLockFlag(false);
		}
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
	if(RunTimeConstants.failureInjection == true){
		sufferedClets.addAll(broker.sufferedCloudlets);
	}
	else{		
		sufferedClets.addAll(broker.getCloudletSubmittedList());	
	}	
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
	
	List<Vm>newVmList = new ArrayList<Vm>();
	if(RunTimeConstants.failureInjection == true){
		newVmList.addAll(broker.getVmBackupList());
	}
	else{
		newVmList.addAll(broker.getVmList());
	}
	
	
	
	//List<Cloudlet>sufferedCletList = broker.getSufferedCloudletsList();
	
	List<Host> hostList = HostVMMapping.getHostKeySet();
	for(int i=0; i<datacenter0.hostPerTimeBetweenFailure.size(); i++){
		hostPerTimeBetweenFailure.add(datacenter0.hostPerTimeBetweenFailure.get(i));
		timeBetweenFailures.add(datacenter0.timeBetweenFailure.get(i));
	}
	
	for(int i=0; i<datacenter0.hostPerTimeToReturn.size(); i++){
		hostPerTimeToReturn.add(datacenter0.hostPerTimeToReturn.get(i));
		timeToReturns.add(datacenter0.timeToReturn.get(i));
	}
	
	//List<Integer> vmfailList = datacenter0.getvmFailedlist();
	//List<Integer> clfailList = datacenter0.getcloudletFailedlist();
	//List<Integer> hostfailList = datacenter0.getfailHostList();
	
	//List<Cloudlet> cloudletfailList = datacenter0.getrecoveringCloudletList();
	
	//List<Double> averageFinishingTime = datacenter0.getFailedCletAverageExectTime();
	//List<Integer> MTBFhostfailList;
			
	CloudSim.stopSimulation();	
	
	//BufferedReader read_fault = new BufferedReader(new FileReader("/rusers/postgrad/ysharma1/PhDWork/Java/Results/Fault_Tolerance_Mechanism.txt"));
	//int faultToleranceMechanism = Integer.parseInt(read_fault.readLine());	
	
	FileWriter writeTBF = new FileWriter("/rusers/postgrad/ysharma1/PhDWork/Java/Results/timeBetweenFailures.txt", false);
	FileWriter writeTBFHost = new FileWriter("/rusers/postgrad/ysharma1/PhDWork/Java/Results/timeBetweenFailureHosts.txt", false);
	
	FileWriter writeTTR= new FileWriter("/rusers/postgrad/ysharma1/PhDWork/Java/Results/timeToReturn.txt", false);
	FileWriter writeTTRHost = new FileWriter("/rusers/postgrad/ysharma1/PhDWork/Java/Results/timeToReturnHosts.txt", false);
	
	for(int i=0; i<timeBetweenFailures.size(); i++){
		writeTBF.write(timeBetweenFailures.get(i)+ System.getProperty( "line.separator" ));
		writeTBFHost.write(hostPerTimeBetweenFailure.get(i)+ System.getProperty( "line.separator" ));
	}
	writeTBF.close();
	writeTBFHost.close();
	
	for(int i=0; i<timeToReturns.size(); i++){
		writeTTR.write(timeToReturns.get(i)+ System.getProperty( "line.separator" ));
		writeTTRHost.write(hostPerTimeToReturn.get(i)+ System.getProperty( "line.separator" ));
	}
	writeTTR.close();
	writeTTRHost.close();
	
	System.out.println("Selecting the fault tolerance mechanism being used");
	switch(faultToleranceMechanism){	
	
	case 1:
		if(RunTimeConstants.vmConsolidationFlag == false){
			System.out.println("Printing and storing the results for checkpointing");
			if(RunTimeConstants.failureCorrelation == true){
				System.out.println("These results are with correlated failures");
			}
			if(RunTimeConstants.traceType.equals("LANL")){
				printCloudletListCheckpointing(newList, hostList, ftareadLANL, broker, hvmRbl, newVmList, vmUtl, pow);
			}
			if(RunTimeConstants.traceType.equals("Grid5000")){
				printCloudletListCheckpointing(newList, hostList, ftareadGrid5000, broker, hvmRbl, newVmList, vmUtl, pow);
			}
		}
		else{
			System.out.println("Printing and storing the results for checkpointing with Consolidation");
			if(RunTimeConstants.failureCorrelation == true){
				System.out.println("These results are with correlated failures");
			}
			if(RunTimeConstants.traceType.equals("LANL")){
				printCloudletListCheckpointingwithConsolidation(newList, hostList, ftareadLANL, broker, hvmRbl, newVmList, vmUtl, pow);
			}
			if(RunTimeConstants.traceType.equals("Grid5000")){
				printCloudletListCheckpointingwithConsolidation(newList, hostList, ftareadGrid5000, broker, hvmRbl, newVmList, vmUtl, pow);
			}			
		}		
		break;
		
	case 2:		
		System.out.println("Printing and storing the results for restarting");
		if(RunTimeConstants.failureCorrelation == true){
			System.out.println("These results are with correlated failures");
		}
		if(RunTimeConstants.traceType.equals("LANL")){
			printCloudletListRestarting(newList, hostList, ftareadLANL, broker, hvmRbl, newVmList, vmUtl, pow);	
		}
		if(RunTimeConstants.traceType.equals("Grid5000")){
			printCloudletListRestarting(newList, hostList, ftareadGrid5000, broker, hvmRbl, newVmList, vmUtl, pow);	
		}
			
		break;
		
	case 3:		
		if(RunTimeConstants.vmConsolidationFlag == false ){
			System.out.println("Printing and storing the results for restarting with migration");
			if(RunTimeConstants.failureCorrelation == true){
				System.out.println("These results are with correlated failures");
			}
			if(RunTimeConstants.traceType.equals("LANL")){
				printCloudletListRestartingwithMigration(newList, hostList, ftareadLANL, broker, hvmRbl, newVmList, vmUtl, pow);
			}
			if(RunTimeConstants.traceType.equals("Grid5000")){
				printCloudletListRestartingwithMigration(newList, hostList, ftareadGrid5000, broker, hvmRbl, newVmList, vmUtl, pow);
			}
		}
		else{
			if(RunTimeConstants.vmConsolidationFlag==true && RunTimeConstants.predictionFlag == true){
				System.out.println("Printing and storing the results for restarting with migration and consolidation");
				if(RunTimeConstants.failureCorrelation == true){
					System.out.println("These results are with correlated failures");
				}
				if(RunTimeConstants.traceType.equals("LANL")){
					printCloudletListRestartingwithMigration(newList, hostList, ftareadLANL, broker, hvmRbl, newVmList, vmUtl, pow);
				}
				if(RunTimeConstants.traceType.equals("Grid5000")){
					printCloudletListRestartingwithMigration(newList, hostList, ftareadGrid5000, broker, hvmRbl, newVmList, vmUtl, pow);
				}				
			}
			else{
				System.out.println("Printing and storing the results for restarting with consolidation");
				if(RunTimeConstants.failureCorrelation == true){
					System.out.println("These results are with correlated failures");
				}
				if(RunTimeConstants.traceType.equals("LANL")){
					printCloudletListRestartingwithConsolidation(newList, hostList, ftareadLANL, broker, hvmRbl, newVmList, vmUtl, pow);
				}
				if(RunTimeConstants.traceType.equals("Grid5000")){
					printCloudletListRestartingwithConsolidation(newList, hostList, ftareadGrid5000, broker, hvmRbl, newVmList, vmUtl, pow);
				}				
			}			
		}
		break;	
		
	case 4:
		System.out.println("Printing and storing the results for checkpointing with migration and Consolidation");
		if(RunTimeConstants.failureCorrelation == true){
			System.out.println("These results are with correlated failures");
		}
		if(RunTimeConstants.traceType.equals("LANL")){
			printCloudletListCheckpointingwithMigration(newList, hostList, ftareadLANL, broker, hvmRbl, newVmList, vmUtl, pow);
		}
		if(RunTimeConstants.traceType.equals("Grid5000")){
			printCloudletListCheckpointingwithMigration(newList, hostList, ftareadGrid5000, broker, hvmRbl, newVmList, vmUtl, pow);
		}
		
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
//	Double executionCost;
	Double changeinExecutionTime;
	Double totalDownTime;
	Double totalFinishedTime_withoutDownTime;
	Double totalFinishedTime_withoutChkptOverhead;	
	Double energyWastagePerClet;
	Double totalEnergyWastage = 0.0;
	int countDeadlineMissed = 0;
	int countDeadlineAchieved = 0;	
	Double totalReexecutionTime = 0.0;
	Double overallEnergyConsumption = 0.0;
	double totalDeadlineMissedMargin = 0;
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
					indent + indent + indent +  dft.format(0)+
					indent + indent + indent+ "Achieved" );
			}
			else{				
				totalDeadlineMissedMargin = totalDeadlineMissedMargin + (100-((deadline*100)/finishedTime));
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
						indent + indent + indent +  dft.format(100-((deadline*100)/finishedTime))+
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
		//applicationFinishingTime = applicationFinishingTime/3600;
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
//	double totalIdleEnergyCost = 0.0;
//	double totalIdleCO2Emission = 0.0;
	double idleEnergyConsumption = 0.0;
//	double idleEnergyCost = 0.0;
//	double idleCO2Emission = 0.0;
	double averageIdleEnergyConsumptionPerHost = 0.0;
	//double totalAverageIdleEnergyConsumption = 0.0;
//	double averageIdleEnergyCostPerHost = 0.0;
	//double totalAverageIdleEnergyCost = 0.0;
//	double averageIdleCO2EmissionPerHost = 0.0;
	//double totalAverageIdleCO2Emission = 0.0;
	
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
	//	idleEnergyCost = idleEnergyConsumption * RunTimeConstants.energyCost;
	//	idleCO2Emission = idleEnergyConsumption * RunTimeConstants.carbonDioxideEmission;
		averageIdleEnergyConsumptionPerHost = idleEnergyConsumption/peListSize;		
		totalIdleEnergyConsumption = totalIdleEnergyConsumption + idleEnergyConsumption;		
		//totalAverageIdleEnergyConsumption = totalAverageIdleEnergyConsumption + averageIdleEnergyConsumptionPerHost;		
		Log.printLine( indent+ hostIdIdle + indent + indent+ indent+ idleTime + indent + indent+ indent+ downTime + indent + indent+ indent+ idleEnergyConsumption+ indent + indent+ indent+ averageIdleEnergyConsumptionPerHost);
	}

	Log.printLine("========== OUTPUT For Host Idle Time  ==========");
	Log.printLine("Host ID" +indent+ indent + indent + "Total Idle Time" +indent+ indent + indent + "Total Down Time" +indent+ indent + indent + "Total Idle Energy Consumption" +indent+ indent + indent + "Average Idle Energy Consumption" );		
	/*
	Log.printLine();
	Log.printLine();
	Log.printLine("========== OUTPUT For Idle Energy Cost and CO2 Emission ==========");
	Log.printLine("Host ID" +indent+ indent + indent + "Total Idle Cost" +indent+ indent + indent + "Total Idle CO2" +indent+ indent + indent + "Average Idle Cost" +indent+ indent + indent + "Average Idle CO2");	
	
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
			idleTime = idleTime + IdleTime.getPeIdleTime(hostList.get(i).getId(), peList.get(j).getId());			
		}			
		//averageIdleTime = idleTime/peListSize;
		//idleTime = idleTimewithDownTime - downTime;
		averageIdleTime = idleTime;
		idlePower = pow.getPower(idleUtilization, peListSize);
		idleEnergyConsumption = ((idlePower/3600) * (averageIdleTime/1000));
		idleEnergyCost = idleEnergyConsumption * RunTimeConstants.energyCost;
		idleCO2Emission = idleEnergyConsumption * RunTimeConstants.carbonDioxideEmission;
		totalIdleEnergyCost= totalIdleEnergyCost + idleEnergyCost;
		totalIdleCO2Emission = totalIdleCO2Emission + idleCO2Emission;
		averageIdleEnergyCostPerHost = idleEnergyCost/peListSize;
		averageIdleCO2EmissionPerHost = idleCO2Emission/peListSize;		
		//totalAverageIdleEnergyCost = totalAverageIdleEnergyCost + averageIdleEnergyCostPerHost;
		//totalAverageIdleCO2Emission = totalAverageIdleCO2Emission + averageIdleCO2EmissionPerHost;
		Log.printLine( indent+ hostIdIdle + indent + indent+ indent+ totalIdleEnergyCost + indent + indent+ indent+ totalIdleCO2Emission + indent + indent+ indent+ averageIdleEnergyCostPerHost + indent + indent+ indent+ averageIdleCO2EmissionPerHost);		
		
	}
	Log.printLine("========== OUTPUT For Idle Energy Cost and CO2 Emission ==========");
	Log.printLine("Host ID" +indent+ indent + indent + "Total Idle Cost" +indent+ indent + indent + "Total Idle CO2" +indent+ indent + indent + "Average Idle Cost" +indent+ indent + indent + "Average Idle CO2");	
	*/
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
//	double tempVmEnergyCost;
	double tempVmEnergyWastage;
//	double tempVmRevenueLoss;
	double difference = 0.0;
	
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
			ftaHostID = HostMapping.getFTAhostID(HostVMMapping.getHost(vm).getId());
			idleUtilization = ftaread.getCurrentUtilization(ftaHostID);			
			vmUtilization = vmUtl.getVmAverageUtilization(vm.getId());
			coreCount = HostVMMapping.getHost(vm).getNumberOfPes();			
			power = pow.getPower(vmUtilization, coreCount);
			idlePower = pow.getPower(idleUtilization, coreCount);
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
			
			
			if(CheckpointOverhead.checkpointOverheadTable.containsKey(cloudlet.getCloudletId())){
				totalChkptOverheads = CheckpointOverhead.getCheckpointOverheadTable(cloudlet.getCloudletId());
			}
			else{
				totalChkptOverheads = 0;
			}		
			
			totalFinishedTime_withoutDownTime = turnAroundTime - totalDownTime ; //with re-execution time and checkpoint overhead				
			totalFinishedTime_withoutChkptOverhead = totalFinishedTime_withoutDownTime - totalChkptOverheads; //only with re-execution time			
			energyPerCletWithoutOverheads = (((totalFinishedTime_withoutChkptOverhead)/3600) * (power/1000)); //Division is happening to convert seconds into hours and to convert watts into kilowats			
			energyPerCletForChkptOverheads = ((((totalChkptOverheads)/3600) * 1.15 * (idlePower/1000)));			
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
			difference = changeinExecutionTime - (totalDownTime + totalReexecutionTime + totalChkptOverheads);			
			
			if(difference > 0){
				totalReexecutionTime = totalReexecutionTime + difference;
				CloudletReexecutionPart.setCletReexecutionTime(cloudlet.getCloudletId(), (long)difference);				
			}
			
			finalTotalReexecutionTime = finalTotalReexecutionTime + totalReexecutionTime;
			Log.printLine(indent + indent + indent + indent + dft.format(cletInitialLength) +					
					indent + indent + indent  + indent + indent + dft.format(turnAroundTime)+ 
					indent + indent + indent + indent + indent + dft.format(changeinExecutionTime) + 
					indent + indent + indent + indent + indent + dft.format(totalDownTime)  
					+indent + indent + indent + indent + indent + dft.format(totalReexecutionTime)
					+indent + indent + indent + indent + indent + dft.format(totalChkptOverheads)+					
					indent + indent + indent + indent + indent + indent + indent + dft.format(totalEnergyPerClet)		
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
		ftaHostID = HostMapping.getFTAhostID(HostVMMapping.getHost(vm).getId());
		idleUtilization = ftaread.getCurrentUtilization(ftaHostID);		
		vmUtilization = vmUtl.getVmAverageUtilization(vm.getId());
		coreCount = HostVMMapping.getHost(vm).getNumberOfPes();
		power = pow.getPower(vmUtilization, coreCount);
		idlePower = pow.getPower(idleUtilization, coreCount);
		//Log.print(indent + cloudlet.getCloudletId() + indent + indent);				
		if(CheckpointOverhead.checkpointOverheadTable.containsKey(cloudlet.getCloudletId())){
			totalChkptOverheads = CheckpointOverhead.getCheckpointOverheadTable(cloudlet.getCloudletId());
		}
		else{
			totalChkptOverheads = 0;
		}			
		totalReexecutionTime = ((double)CloudletReexecutionPart.getCletReexecutionTime(cloudlet.getCloudletId()))/1000;			
		energyPerCletForChkptOverheads = ((((totalChkptOverheads)/3600) * 1.15 * (idlePower/1000)));
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
	
	//Log.printLine();
	//Log.printLine();
	//Log.printLine("========== Total OUTPUT ==========");
	//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Idle Energy Consumption" +indent+ "Total Idle Time" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
	//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Deadlines Achieved Percentage" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
	//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
	//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
	//Log.printLine("Total Energy Consumption" + indent +indent + "Total Energy Wastage" +indent+indent + "Total Deadlines Missed" +indent+indent + "Total Deadlines Achieved"   +indent+indent + "Percentage Achieved Deadlines" +indent+indent +"Application Utility Value" +indent+indent + "Total Failures Occurred" +indent+indent + "Total Checkpoints");
	//Log.printLine("Total Energy Consumption" + indent + indent + indent + "Total Energy Wastage" + indent + indent + indent + "Total Idle Energy Consumption"  +indent +indent + indent + "Application Utility Value" );
	
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
		
		double averageCletCompletionTime;
		double averageCletLength;
		double averageExecutionTimeChanged;	
		
		averageCletCompletionTime = totalCletCompletionTime/size;
		averageCletLength = totalCletLength/size;
		averageExecutionTimeChanged = totalExecutionTimeChanged/size;	
			
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
		double averageEnergyConsumption = 0.0;
		double averageEnergyWastage = 0.0;
		double averageIdleEnergyConsumption = 0.0;
		double averageReexecutionTime = 0.0;
		double averageReexecutionTimeHours = 0.0;
		double averageDownTime = 0.0;
		double averageDownTimeHours = 0.0;
		double averageDeadlineMissedMargin = 0.0;
		
		totalEnergyWastage = 0.0;
		
		for(int i=0; i<newVmList.size(); i++){
			totalEnergyConsumption = totalEnergyConsumption + vmEnergyConsumptionTable.get(newVmList.get(i).getId());			
			totalEnergyWastage = totalEnergyWastage + vmEnergyWastageTable.get(newVmList.get(i).getId());
		}
			
		jobUtilityValue = jobUtilityValue/size;			
		percentageAchievedDeadlines = (((double)countDeadlineAchieved*100)/((double)countDeadlineAchieved+(double)countDeadlineMissed));		
		
		averageEnergyConsumption = totalEnergyConsumption/size;
		averageEnergyWastage = totalEnergyWastage/size;
		averageIdleEnergyConsumption = totalIdleEnergyConsumption/size;
		averageReexecutionTime = finalTotalReexecutionTime/size;
		averageReexecutionTimeHours = ((finalTotalReexecutionTime/3600)/size);
		averageDownTime = finalTotalDownTime/size;
		averageDownTimeHours = ((finalTotalDownTime/3600)/size);
		averageDeadlineMissedMargin = totalDeadlineMissedMargin/countDeadlineMissed;
		
		Log.printLine();
		Log.printLine("==========================================================================================================================================================");
		Log.printLine("**********************************************************************************************************************************************************");
		Log.printLine("==========================================================================================================================================================");
			
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Energy Consumption ==========");
		Log.printLine(dft.format(totalEnergyConsumption));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Energy Wastage ==========");
		Log.printLine(dft.format(totalEnergyWastage));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Idle Energy Consumption ==========");
		Log.printLine(dft.format(totalIdleEnergyConsumption));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Application Utility Value ==========");
		Log.printLine(dft.format(jobUtilityValue));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Deadlines Achieved ==========");
		Log.printLine(countDeadlineAchieved);
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Deadlines Missed ==========");
		Log.printLine(countDeadlineMissed);
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Percentage Achieved Deadlines ==========");
		Log.printLine(dft.format(percentageAchievedDeadlines));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Maintainability ==========");
		Log.printLine(dft.format(totalMaintainability));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Availability ==========");
		Log.printLine(dft.format(totalAvailability));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Reliability ==========");
		Log.printLine(dft.format(totalReliabilityAverage*100));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Execution Time Changed Hours ==========");
		Log.printLine(dft.format(averageExecutionTimeChanged/3600));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Execution Time Changed  ==========");
		Log.printLine(dft.format(averageExecutionTimeChanged));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Finished Time Hours ==========");
		Log.printLine(dft.format(averageCletCompletionTime/3600));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Finished Time  ==========");
		Log.printLine(dft.format(averageCletCompletionTime));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Checkpoints  ==========");
		Log.printLine(totalCheckpointsCount);
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total VM Consolidations  ==========");
		Log.printLine(totalVmConsolidations);
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Failures Occurred ==========");
		Log.printLine(totalFailureCount);
	
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Hosts ==========");
		Log.printLine(hostList.size());
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Reexecution Time Hours ==========");
		Log.printLine( dft.format(finalTotalReexecutionTime/3600));
	
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Reexecution Time ==========");
		Log.printLine( dft.format(finalTotalReexecutionTime));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total DownTime Hours ==========");
		Log.printLine( dft.format(finalTotalDownTime/3600));
	
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total DownTime ==========");
		Log.printLine( dft.format(finalTotalDownTime));
	
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Application Finishing Time ==========");
		Log.printLine( dft.format(applicationFinishingTime/3600));
		Log.printLine();		
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Energy Consumption ==========");
		Log.printLine( dft.format(averageEnergyConsumption));
		Log.printLine();
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Energy Wastage ==========");
		Log.printLine( dft.format(averageEnergyWastage));
		Log.printLine();
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Idle Energy Consumption ==========");
		Log.printLine( dft.format(averageIdleEnergyConsumption));
		Log.printLine();
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Reexecution Time ==========");
		Log.printLine( dft.format(averageReexecutionTime));
		Log.printLine();
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Reexecution Time Hours ==========");
		Log.printLine( dft.format(averageReexecutionTimeHours));
		Log.printLine();
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Down Time ==========");
		Log.printLine( dft.format(averageDownTime));
		Log.printLine();
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Down Time Hours ==========");
		Log.printLine( dft.format(averageDownTimeHours));
		Log.printLine();
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Deadline Missed Margin ==========");
		Log.printLine( dft.format(averageDeadlineMissedMargin));
		Log.printLine();

	}

private static void printCloudletListCheckpointing(List<Cloudlet> list, List<Host> hostList, FTAFileReader ftaread, FailureDatacenterBroker broker, HostVMMappingReliability hvmRbl, List<Vm>newVmList, VmUtilization vmUtl, PowerModel pow) {	
	
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
	double totalDeadlineMissedMargin = 0;
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
			"Data center ID" + indent + indent + "VM ID" + indent + indent + "Bag ID"+ indent +  indent + "Cloudlet Initial Length" + indent + "Intial Start Time" + indent + indent +"Finish Time" + indent + indent +"Turnaround Time" +indent+ indent+ "Deadline Value" +indent+ indent+ "Deadline Actual Value"+indent+ indent+ "Utility Value"+indent+ indent +"Deadline Margin"+indent+ indent +"Deadline");
	
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
					indent + indent + indent +  dft.format(0)+
					indent + indent + indent+ "Achieved" );
			}
			else{				
				countDeadlineMissed = countDeadlineMissed + 1;
				totalDeadlineMissedMargin = totalDeadlineMissedMargin + (100-((deadline*100)/finishedTime));
				Log.printLine( indent + indent + cloudlet.getResourceId() + indent + indent + indent + indent + cloudlet.getVmId() +
						indent + indent + indent + indent + bagID+
						indent + indent + indent + indent + dft.format(cloudletInitialLength) +
						indent + indent + indent + indent + indent + dft.format(initTime) +
						indent + indent + indent +  dft.format(finishedTime)+
						indent + indent + indent + dft.format(turnAroundTime)+
						indent + indent + indent +  dft.format(deadline)+
						indent + indent + indent +  dft.format(deadline-initTime)+
						indent + indent + indent +  dft.format(utilityValue)+
						indent + indent + indent +  dft.format(100-((deadline*100)/finishedTime))+
						indent + indent + indent+ "Missed" );
				}
		}
	}	
	Log.printLine("========== OUTPUT ==========");
	//Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
	//		"Data center ID" + indent + "VM ID" + indent +  "Cloudlet Initial Length" + indent + "Intial Start Time" + indent + "Finish Time" +indent+ "Utility Value" +indent+ "Deadline");
	Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
			"Data center ID" + indent + indent + "VM ID" + indent + indent + "Bag ID"+ indent +  indent + "Cloudlet Initial Length" + indent + "Intial Start Time" + indent + indent +"Finish Time" + indent + indent +"Turnaround Time" +indent+ indent+ "Deadline Value" +indent+ indent+ "Deadline Actual Value"+indent+ indent+ "Utility Value"+indent+ indent +"Deadline Margin"+indent+ indent +"Deadline");
	
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
	//	applicationFinishingTime = applicationFinishingTime/3600;
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
	double averageIdleEnergyConsumptionPerHost = 0.0;
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
		averageIdleEnergyConsumptionPerHost = idleEnergyConsumption/peListSize;
		totalIdleEnergyConsumption = totalIdleEnergyConsumption + idleEnergyConsumption; 
		totalAverageIdleEnergyConsumption = totalAverageIdleEnergyConsumption + averageIdleEnergyConsumptionPerHost;
		Log.printLine( indent+ hostIdIdle + indent + indent+ indent+ idleTime + indent + indent+ indent+ downTime + indent + indent+ indent+ idleEnergyConsumption+ indent + indent+ indent+ averageIdleEnergyConsumptionPerHost);
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
	double difference = 0.0;
	
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
			ftaHostID = HostMapping.getFTAhostID(HostVMMapping.getHost(vm).getId());
			idleUtilization = ftaread.getCurrentUtilization(ftaHostID);			
			vmUtilization = vmUtl.getVmAverageUtilization(vm.getId());
			coreCount = HostVMMapping.getHost(vm).getNumberOfPes();			
			power = pow.getPower(vmUtilization, coreCount);
			idlePower = pow.getPower(idleUtilization, coreCount);
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
			
			
			if(CheckpointOverhead.checkpointOverheadTable.containsKey(cloudlet.getCloudletId())){
				totalChkptOverheads = CheckpointOverhead.getCheckpointOverheadTable(cloudlet.getCloudletId());
			}
			else{
				totalChkptOverheads = 0;
			}		
			
			totalFinishedTime_withoutDownTime = turnAroundTime - totalDownTime ; //with re-execution time and checkpoint overhead				
			totalFinishedTime_withoutChkptOverhead = totalFinishedTime_withoutDownTime - totalChkptOverheads; //only with re-execution time			
			energyPerCletWithoutOverheads = (((totalFinishedTime_withoutChkptOverhead)/3600) * (power/1000)); //Division is happening to convert seconds into hours and to convert watts into kilowats			
			energyPerCletForChkptOverheads = ((((totalChkptOverheads)/3600) * 1.15 * (idlePower/1000)));			
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
			difference = changeinExecutionTime - (totalDownTime + totalReexecutionTime + totalChkptOverheads);			
			
			if(difference > 0){
				totalReexecutionTime = totalReexecutionTime + difference;
				CloudletReexecutionPart.setCletReexecutionTime(cloudlet.getCloudletId(), (long)difference);				
			}
			
			finalTotalReexecutionTime = finalTotalReexecutionTime + totalReexecutionTime;
			Log.printLine(indent + indent + indent + indent + dft.format(cletInitialLength) +					
					indent + indent + indent  + indent + indent + dft.format(turnAroundTime)+ 
					indent + indent + indent + indent + indent + dft.format(changeinExecutionTime) + 
					indent + indent + indent + indent + indent + dft.format(totalDownTime)  
					+indent + indent + indent + indent + indent + dft.format(totalReexecutionTime)
					+indent + indent + indent + indent + indent + dft.format(totalChkptOverheads)+					
					indent + indent + indent + indent + indent + indent + indent + dft.format(totalEnergyPerClet)		
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
		ftaHostID = HostMapping.getFTAhostID(HostVMMapping.getHost(vm).getId());
		idleUtilization = ftaread.getCurrentUtilization(ftaHostID);		
		vmUtilization = vmUtl.getVmAverageUtilization(vm.getId());
		coreCount = HostVMMapping.getHost(vm).getNumberOfPes();
		power = pow.getPower(vmUtilization, coreCount);
		idlePower = pow.getPower(idleUtilization, coreCount);
		//Log.print(indent + cloudlet.getCloudletId() + indent + indent);				
		if(CheckpointOverhead.checkpointOverheadTable.containsKey(cloudlet.getCloudletId())){
			totalChkptOverheads = CheckpointOverhead.getCheckpointOverheadTable(cloudlet.getCloudletId());
		}
		else{
			totalChkptOverheads = 0;
		}			
		totalReexecutionTime = ((double)CloudletReexecutionPart.getCletReexecutionTime(cloudlet.getCloudletId()))/1000;			
		energyPerCletForChkptOverheads = ((((totalChkptOverheads)/3600) * 1.15 * (idlePower/1000)));
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
	
	//Log.printLine();
	//Log.printLine();
	//Log.printLine("========== Total OUTPUT ==========");
	//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Idle Energy Consumption" +indent+ "Total Idle Time" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
	//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Deadlines Achieved Percentage" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
	//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Application Finished Time" +indent+ "Application Deadline" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
	//Log.printLine("Total Reliability" + indent + "Total Energy Consumption" + indent + "Total Energy Wastage" +indent+ "Total Deadlines Missed" +indent+ "Total Deadlines Achieved" +indent+ "Application Utility Value" +indent+ "Total Failures Occurred" +indent+ "Total Checkpoints");
	//Log.printLine("Total Energy Consumption" + indent +indent + "Total Energy Wastage" +indent+indent + "Total Deadlines Missed" +indent+indent + "Total Deadlines Achieved"   +indent+indent + "Percentage Achieved Deadlines" +indent+indent +"Application Utility Value" +indent+indent + "Total Failures Occurred" +indent+indent + "Total Checkpoints");
	//Log.printLine("Total Energy Consumption" + indent + indent + indent + "Total Energy Wastage" + indent + indent + indent + "Total Idle Energy Consumption"  +indent +indent + indent + "Application Utility Value" );
	
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
		
		double averageCletCompletionTime;
		double averageCletLength;
		double averageExecutionTimeChanged;	
		
		averageCletCompletionTime = totalCletCompletionTime/size;
		averageCletLength = totalCletLength/size;
		averageExecutionTimeChanged = totalExecutionTimeChanged/size;	
			
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
		double averageEnergyConsumption = 0.0;
		double averageEnergyWastage = 0.0;
		double averageIdleEnergyConsumption = 0.0;
		double averageReexecutionTime = 0.0;
		double averageReexecutionTimeHours = 0.0;
		double averageDownTime = 0.0;
		double averageDownTimeHours = 0.0;
		double averageDeadlineMissedMargin = 0.0;
		
		totalEnergyWastage = 0.0;
		
		for(int i=0; i<newVmList.size(); i++){
			totalEnergyConsumption = totalEnergyConsumption + vmEnergyConsumptionTable.get(newVmList.get(i).getId());			
			totalEnergyWastage = totalEnergyWastage + vmEnergyWastageTable.get(newVmList.get(i).getId());
		}
			
		jobUtilityValue = jobUtilityValue/size;			
		percentageAchievedDeadlines = (((double)countDeadlineAchieved*100)/((double)countDeadlineAchieved+(double)countDeadlineMissed));		
		
		averageEnergyConsumption = totalEnergyConsumption/size;
		averageEnergyWastage = totalEnergyWastage/size;
		averageIdleEnergyConsumption = totalIdleEnergyConsumption/size;
		averageReexecutionTime = finalTotalReexecutionTime/size;
		averageReexecutionTimeHours = ((finalTotalReexecutionTime/3600)/size);
		averageDownTime = finalTotalDownTime/size;
		averageDownTimeHours = ((finalTotalDownTime/3600)/size);
		averageDeadlineMissedMargin = totalDeadlineMissedMargin/countDeadlineMissed;
		
		Log.printLine();
		Log.printLine("==========================================================================================================================================================");
		Log.printLine("**********************************************************************************************************************************************************");
		Log.printLine("==========================================================================================================================================================");
			
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Energy Consumption ==========");
		Log.printLine(dft.format(totalEnergyConsumption));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Energy Wastage ==========");
		Log.printLine(dft.format(totalEnergyWastage));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Idle Energy Consumption ==========");
		Log.printLine(dft.format(totalIdleEnergyConsumption));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Application Utility Value ==========");
		Log.printLine(dft.format(jobUtilityValue));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Deadlines Achieved ==========");
		Log.printLine(countDeadlineAchieved);
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Deadlines Missed ==========");
		Log.printLine(countDeadlineMissed);
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Percentage Achieved Deadlines ==========");
		Log.printLine(dft.format(percentageAchievedDeadlines));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Maintainability ==========");
		Log.printLine(dft.format(totalMaintainability));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Availability ==========");
		Log.printLine(dft.format(totalAvailability));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Reliability ==========");
		Log.printLine(dft.format(totalReliabilityAverage*100));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Execution Time Changed Hours ==========");
		Log.printLine(dft.format(averageExecutionTimeChanged/3600));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Execution Time Changed  ==========");
		Log.printLine(dft.format(averageExecutionTimeChanged));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Finished Time Hours ==========");
		Log.printLine(dft.format(averageCletCompletionTime/3600));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Finished Time  ==========");
		Log.printLine(dft.format(averageCletCompletionTime));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Checkpoints  ==========");
		Log.printLine(totalCheckpointsCount);
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total VM Consolidations  ==========");
		Log.printLine(totalVmConsolidations);
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Failures Occurred ==========");
		Log.printLine(totalFailureCount);
	
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Hosts ==========");
		Log.printLine(hostList.size());
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Reexecution Time Hours ==========");
		Log.printLine( dft.format(finalTotalReexecutionTime/3600));
	
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Reexecution Time ==========");
		Log.printLine( dft.format(finalTotalReexecutionTime));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total DownTime Hours ==========");
		Log.printLine( dft.format(finalTotalDownTime/3600));
	
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total DownTime ==========");
		Log.printLine( dft.format(finalTotalDownTime));
	
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Application Finishing Time ==========");
		Log.printLine( dft.format(applicationFinishingTime/3600));
		Log.printLine();		
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Energy Consumption ==========");
		Log.printLine( dft.format(averageEnergyConsumption));
		Log.printLine();
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Energy Wastage ==========");
		Log.printLine( dft.format(averageEnergyWastage));
		Log.printLine();
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Idle Energy Consumption ==========");
		Log.printLine( dft.format(averageIdleEnergyConsumption));
		Log.printLine();
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Reexecution Time ==========");
		Log.printLine( dft.format(averageReexecutionTime));
		Log.printLine();
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Reexecution Time Hours ==========");
		Log.printLine( dft.format(averageReexecutionTimeHours));
		Log.printLine();
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Down Time ==========");
		Log.printLine( dft.format(averageDownTime));
		Log.printLine();
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Down Time Hours ==========");
		Log.printLine( dft.format(averageDownTimeHours));
		Log.printLine();
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Deadline Missed Margin ==========");
		Log.printLine( dft.format(averageDeadlineMissedMargin));
		Log.printLine();

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
		double totalDeadlineMissedMargin = 0;
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
	//	FileWriter HostCountClock = null;
	//	FileWriter HostCount = null;
		
		System.out.println("Printing results for Restarting Fault Tolerance Mechanism");
		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
				"Data center ID" + indent + indent + "VM ID" + indent +  indent + "Bag ID" + indent +  indent + "Cloudlet Initial Length" + indent + "Intial Start Time" + indent + indent +"Finish Time" +indent+ indent+ "Turnaround Time" +indent+ indent+ "Deadline Value" +indent+ indent+ "Deadline Actual Value"+indent+ indent+ "Utility Value"+indent+ indent +"Deadline Margin"+indent+ indent +"Deadline");
		
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
						indent + indent + indent +  dft.format(0)+
						indent + indent + indent+ "Achieved" );
				}
				else{					
					
					totalDeadlineMissedMargin = totalDeadlineMissedMargin + (100-((deadline*100)/finishedTime));
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
							indent + indent + indent +  dft.format(100-((deadline*100)/finishedTime))+
							indent + indent + indent+ "Missed" );
					}
				}
			}
		
		
		Log.printLine("========== OUTPUT ==========");
		
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
				"Data center ID" + indent + indent + "VM ID" + indent +  indent + "Bag ID" + indent +  indent + "Cloudlet Initial Length" + indent + "Intial Start Time" + indent + indent +"Finish Time" +indent+ indent+ "Turnaround Time" +indent+ indent+ "Deadline Value" +indent+ indent+ "Deadline Actual Value"+indent+ indent+ "Utility Value"+indent+ indent +"Deadline Margin"+indent+ indent +"Deadline");
		
		
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
			//applicationFinishingTime = applicationFinishingTime/3600;
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
		Log.printLine("Host ID" + indent + "FTA Host ID" + indent + indent + indent +" MTBF" + indent + indent + indent + indent + indent + "MTTR" +indent + indent + indent + indent + indent + indent +"Max Hazard Rate" +indent+ indent + indent + indent + "Current Hazard Rate" + indent + indent + indent + indent + "Host Power" + indent + indent + "Host Availability" +indent+ "Host Maintainability");
		
		int ftaHostID;	
		for(int i=0;i<HostMapping.getHostMapSize();i++){
			ftaHostID = HostMapping.getFTAhostID(i);
			
			Log.printLine(  i + indent + indent + indent + ftaHostID +
					indent + indent + indent + indent + dft.format(ftaread.getMTBF(ftaHostID)) +
					indent + indent + indent + indent + dft.format(ftaread.getMTTR(ftaHostID)) +
					indent + indent + indent + indent + ftaread.getMaxHazardRate(ftaHostID)+
					indent + indent + indent + ftaread.getCurrentHazardRate(ftaHostID)+
					indent + indent + indent + indent + indent +dft.format(ftaread.getPowerforHost(ftaHostID))+
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
		double averageIdleEnergyConsumptionPerHost;
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
			averageIdleEnergyConsumptionPerHost = idleEnergyConsumption/peListSize;
			totalIdleEnergyConsumption = totalIdleEnergyConsumption + idleEnergyConsumption; 
			totalAverageIdleEnergyConsumption = totalAverageIdleEnergyConsumption + averageIdleEnergyConsumptionPerHost;
			Log.printLine( indent+ hostIdIdle + indent + indent+ indent+ idleTime + indent + indent+ indent+ downTime + indent + indent+ indent+ idleEnergyConsumption + indent + indent+ indent+ averageIdleEnergyConsumptionPerHost);
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
		double difference = 0.0;
		
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
				
				difference = changeinExecutionTime - (totalDownTime + totalReexecutionTime);
				
				if(difference > 0){
					CloudletReexecutionPart.setCletReexecutionTime(cloudlet.getCloudletId(), (long)difference);
					totalReexecutionTime = totalReexecutionTime + difference;				
				}
				
				finalTotalReexecutionTime = finalTotalReexecutionTime + totalReexecutionTime;
				
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
		
		
	//	Log.printLine();
	//	Log.printLine();
	//	Log.printLine("========== Total OUTPUT ==========");
	//	Log.printLine(indent + "Total Energy Consumption" + indent + indent + indent + "Total Energy Wastage" + indent + indent + indent + "Total Idle Energy Consumption"  +indent +indent + indent + "Application Utility Value" );
						
			double totalReliability;	
			double totalReliabilityParallel = 0.0;
			double totalReliabilityAverage = 0.0;
			double totalAvailability = 0.0;
			double totalMaintainability = 0.0;
			double percentageAchievedDeadlines;
			
			double averageCletCompletionTime;
		//	double averageCletLength;
			double averageExecutionTimeChanged;		
			
			averageCletCompletionTime = totalCletCompletionTime/size;
		//	averageCletLength = totalCletLength/size;
			averageExecutionTimeChanged = totalExecutionTimeChanged/size;
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
			double averageEnergyConsumption = 0.0;
			double averageEnergyWastage = 0.0;
			double averageIdleEnergyConsumption = 0.0;
			double averageReexecutionTime = 0.0;
			double averageReexecutionTimeHours = 0.0;
			double averageDownTime = 0.0;
			double averageDownTimeHours = 0.0;	
			double averageDeadlineMissedMargin = 0.0;
			
			for(int i=0; i<newVmList.size();i++){
				totalEnergyConsumption = totalEnergyConsumption + vmEnergyConsumptionTable.get(newVmList.get(i).getId());
				totalEnergyWastage = totalEnergyWastage + vmEnergyWastageTable.get(newVmList.get(i).getId());
			}					
			
			jobUtilityValue = jobUtilityValue/size;		
		
			percentageAchievedDeadlines = (((double)countDeadlineAchieved*100)/((double)countDeadlineAchieved+(double)countDeadlineMissed));
			
			averageEnergyConsumption = totalEnergyConsumption/size;
			averageEnergyWastage = totalEnergyWastage/size;
			averageIdleEnergyConsumption = totalIdleEnergyConsumption/size;
			averageReexecutionTime = finalTotalReexecutionTime/size;
			averageReexecutionTimeHours = ((finalTotalReexecutionTime/3600)/size);
			averageDownTime = finalTotalDownTime/size;
			averageDownTimeHours = ((finalTotalDownTime/3600)/size);
			averageDeadlineMissedMargin = totalDeadlineMissedMargin/countDeadlineMissed;
			
		Log.printLine();
		Log.printLine("==========================================================================================================================================================");
		Log.printLine("**********************************************************************************************************************************************************");
		Log.printLine("==========================================================================================================================================================");
			
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Energy Consumption ==========");
		Log.printLine(dft.format(totalEnergyConsumption));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Energy Wastage ==========");
		Log.printLine(dft.format(totalEnergyWastage));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Idle Energy Consumption ==========");
		Log.printLine(dft.format(totalIdleEnergyConsumption));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Application Utility Value ==========");
		Log.printLine(dft.format(jobUtilityValue));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Deadlines Achieved ==========");
		Log.printLine(countDeadlineAchieved);
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Deadlines Missed ==========");
		Log.printLine(countDeadlineMissed);
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Percentage Achieved Deadlines ==========");
		Log.printLine(dft.format(percentageAchievedDeadlines));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Maintainability ==========");
		Log.printLine(dft.format(totalMaintainability));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Availability ==========");
		Log.printLine(dft.format(totalAvailability));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Reliability ==========");
		Log.printLine(dft.format(totalReliabilityAverage*100));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Execution Time Changed Hours ==========");
		Log.printLine(dft.format(averageExecutionTimeChanged/3600));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Execution Time Changed  ==========");
		Log.printLine(dft.format(averageExecutionTimeChanged));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Finished Time Hours ==========");
		Log.printLine(dft.format(averageCletCompletionTime/3600));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Finished Time  ==========");
		Log.printLine(dft.format(averageCletCompletionTime));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total VM Consolidations  ==========");
		Log.printLine(totalVmConsolidations);
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total VM Migrations  ==========");
		Log.printLine(totalVmMigrations);
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Failures Occurred ==========");
		Log.printLine(totalFailureCount);
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Hosts ==========");
		Log.printLine(hostList.size());
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Reexecution Time Hours ==========");
		Log.printLine( dft.format(finalTotalReexecutionTime/3600));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Reexecution Time ==========");
		Log.printLine( dft.format(finalTotalReexecutionTime));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total DownTime Hours ==========");
		Log.printLine( dft.format(finalTotalDownTime/3600));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total DownTime ==========");
		Log.printLine( dft.format(finalTotalDownTime));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Application Finishing Time ==========");
		Log.printLine( dft.format(applicationFinishingTime/3600));
		Log.printLine();		
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Energy Consumption ==========");
		Log.printLine( dft.format(averageEnergyConsumption));
		Log.printLine();
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Energy Wastage ==========");
		Log.printLine( dft.format(averageEnergyWastage));
		Log.printLine();
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Idle Energy Consumption ==========");
		Log.printLine( dft.format(averageIdleEnergyConsumption));
		Log.printLine();
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Reexecution Time ==========");
		Log.printLine( dft.format(averageReexecutionTime));
		Log.printLine();
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Reexecution Time Hours ==========");
		Log.printLine( dft.format(averageReexecutionTimeHours));
		Log.printLine();
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Down Time ==========");
		Log.printLine( dft.format(averageDownTime));
		Log.printLine();
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Down Time Hours ==========");
		Log.printLine( dft.format(averageDownTimeHours));
		Log.printLine();
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Deadline Missed Margin ==========");
		Log.printLine( dft.format(averageDeadlineMissedMargin));
		Log.printLine();
		
}

private static void printCloudletListRestarting(List<Cloudlet> list, List<Host> hostList, FTAFileReader ftaread, FailureDatacenterBroker broker, HostVMMappingReliability hvmRbl, List<Vm>newVmList, VmUtilization vmUtl, PowerModel pow){	
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
		double totalDeadlineMissedMargin = 0;
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
	//	FileWriter HostCountClock = null;
	//	FileWriter HostCount = null;
		
		System.out.println("Printing results for Restarting Fault Tolerance Mechanism");
		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
				"Data center ID" + indent + indent + "VM ID" + indent +  indent + "Bag ID" + indent +  indent + "Cloudlet Initial Length" + indent + "Intial Start Time" + indent + indent +"Finish Time" +indent+ indent+ "Turnaround Time" +indent+ indent+ "Deadline Value" +indent+ indent+ "Deadline Actual Value"+indent+ indent+ "Utility Value"+indent+ indent +"Deadline Margin"+indent+ indent +"Deadline");
		
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
						indent + indent + indent +  dft.format(0)+
						indent + indent + indent+ "Achieved" );
				}
				else{					
					totalDeadlineMissedMargin = totalDeadlineMissedMargin + (100-((deadline*100)/finishedTime));
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
							indent + indent + indent +  dft.format(100-((deadline*100)/finishedTime))+
							indent + indent + indent+ "Missed" );
					}
				}
			}
		
		
		Log.printLine("========== OUTPUT ==========");
		
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
				"Data center ID" + indent + indent + "VM ID" + indent +  indent + "Bag ID" + indent +  indent + "Cloudlet Initial Length" + indent + "Intial Start Time" + indent + indent +"Finish Time" +indent+ indent+ "Turnaround Time" +indent+ indent+ "Deadline Value" +indent+ indent+ "Deadline Actual Value"+indent+ indent+ "Utility Value"+indent+ indent +"Deadline Margin"+indent+ indent +"Deadline");
		
		
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
			//applicationFinishingTime = applicationFinishingTime/3600;
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
		Log.printLine("Host ID" + indent + "FTA Host ID" + indent + indent + indent +" MTBF" + indent + indent + indent + indent + indent + "MTTR" +indent + indent + indent + indent + indent + indent +"Max Hazard Rate" +indent+ indent + indent + indent + "Current Hazard Rate" + indent + indent + indent + indent + "Host Power" + indent + indent + "Host Availability" +indent+ "Host Maintainability");
		
		int ftaHostID;	
		for(int i=0;i<HostMapping.getHostMapSize();i++){
			ftaHostID = HostMapping.getFTAhostID(i);
			
			Log.printLine(  i + indent + indent + indent + ftaHostID +
					indent + indent + indent + indent + dft.format(ftaread.getMTBF(ftaHostID)) +
					indent + indent + indent + indent + dft.format(ftaread.getMTTR(ftaHostID)) +
					indent + indent + indent + indent + ftaread.getMaxHazardRate(ftaHostID)+
					indent + indent + indent + ftaread.getCurrentHazardRate(ftaHostID)+
					indent + indent + indent + indent + indent +dft.format(ftaread.getPowerforHost(ftaHostID))+
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
		double averageIdleEnergyConsumptionPerHost;
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
			averageIdleEnergyConsumptionPerHost = idleEnergyConsumption/peListSize;
			totalIdleEnergyConsumption = totalIdleEnergyConsumption + idleEnergyConsumption; 
			totalAverageIdleEnergyConsumption = totalAverageIdleEnergyConsumption + averageIdleEnergyConsumptionPerHost;
			Log.printLine( indent+ hostIdIdle + indent + indent+ indent+ idleTime + indent + indent+ indent+ downTime + indent + indent+ indent+ idleEnergyConsumption + indent + indent+ indent+ averageIdleEnergyConsumptionPerHost);
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
		double difference = 0.0;
		
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
				
				difference = changeinExecutionTime - (totalDownTime + totalReexecutionTime);
				
				if(difference > 0){
					CloudletReexecutionPart.setCletReexecutionTime(cloudlet.getCloudletId(), (long)difference);
					totalReexecutionTime = totalReexecutionTime + difference;				
				}
				
				finalTotalReexecutionTime = finalTotalReexecutionTime + totalReexecutionTime;
				
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
		
		
	//	Log.printLine();
	//	Log.printLine();
	//	Log.printLine("========== Total OUTPUT ==========");
	//	Log.printLine(indent + "Total Energy Consumption" + indent + indent + indent + "Total Energy Wastage" + indent + indent + indent + "Total Idle Energy Consumption"  +indent +indent + indent + "Application Utility Value" );
						
			double totalReliability;	
			double totalReliabilityParallel = 0.0;
			double totalReliabilityAverage = 0.0;
			double totalAvailability = 0.0;
			double totalMaintainability = 0.0;
			double percentageAchievedDeadlines;
			
			double averageCletCompletionTime;
		//	double averageCletLength;
			double averageExecutionTimeChanged;		
			
			averageCletCompletionTime = totalCletCompletionTime/size;
		//	averageCletLength = totalCletLength/size;
			averageExecutionTimeChanged = totalExecutionTimeChanged/size;
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
			double averageEnergyConsumption = 0.0;
			double averageEnergyWastage = 0.0;
			double averageIdleEnergyConsumption = 0.0;
			double averageReexecutionTime = 0.0;
			double averageReexecutionTimeHours = 0.0;
			double averageDownTime = 0.0;
			double averageDownTimeHours = 0.0;	
			double averageDeadlineMissedMargin = 0.0;
			
			for(int i=0; i<newVmList.size();i++){
				totalEnergyConsumption = totalEnergyConsumption + vmEnergyConsumptionTable.get(newVmList.get(i).getId());
				totalEnergyWastage = totalEnergyWastage + vmEnergyWastageTable.get(newVmList.get(i).getId());
			}					
			
			jobUtilityValue = jobUtilityValue/size;		
		
			percentageAchievedDeadlines = (((double)countDeadlineAchieved*100)/((double)countDeadlineAchieved+(double)countDeadlineMissed));
			
			averageEnergyConsumption = totalEnergyConsumption/size;
			averageEnergyWastage = totalEnergyWastage/size;
			averageIdleEnergyConsumption = totalIdleEnergyConsumption/size;
			averageReexecutionTime = finalTotalReexecutionTime/size;
			averageReexecutionTimeHours = ((finalTotalReexecutionTime/3600)/size);
			averageDownTime = finalTotalDownTime/size;
			averageDownTimeHours = ((finalTotalDownTime/3600)/size);
			averageDeadlineMissedMargin = totalDeadlineMissedMargin/countDeadlineMissed;
			
		Log.printLine();
		Log.printLine("==========================================================================================================================================================");
		Log.printLine("**********************************************************************************************************************************************************");
		Log.printLine("==========================================================================================================================================================");
			
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Energy Consumption ==========");
		Log.printLine(dft.format(totalEnergyConsumption));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Energy Wastage ==========");
		Log.printLine(dft.format(totalEnergyWastage));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Idle Energy Consumption ==========");
		Log.printLine(dft.format(totalIdleEnergyConsumption));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Application Utility Value ==========");
		Log.printLine(dft.format(jobUtilityValue));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Deadlines Achieved ==========");
		Log.printLine(countDeadlineAchieved);
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Deadlines Missed ==========");
		Log.printLine(countDeadlineMissed);
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Percentage Achieved Deadlines ==========");
		Log.printLine(dft.format(percentageAchievedDeadlines));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Maintainability ==========");
		Log.printLine(dft.format(totalMaintainability));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Availability ==========");
		Log.printLine(dft.format(totalAvailability));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Reliability ==========");
		Log.printLine(dft.format(totalReliabilityAverage*100));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Execution Time Changed Hours ==========");
		Log.printLine(dft.format(averageExecutionTimeChanged/3600));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Execution Time Changed  ==========");
		Log.printLine(dft.format(averageExecutionTimeChanged));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Finished Time Hours ==========");
		Log.printLine(dft.format(averageCletCompletionTime/3600));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Finished Time  ==========");
		Log.printLine(dft.format(averageCletCompletionTime));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total VM Consolidations  ==========");
		Log.printLine(totalVmConsolidations);
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total VM Migrations  ==========");
		Log.printLine(totalVmMigrations);
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Failures Occurred ==========");
		Log.printLine(totalFailureCount);
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Hosts ==========");
		Log.printLine(hostList.size());
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Reexecution Time Hours ==========");
		Log.printLine( dft.format(finalTotalReexecutionTime/3600));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Reexecution Time ==========");
		Log.printLine( dft.format(finalTotalReexecutionTime));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total DownTime Hours ==========");
		Log.printLine( dft.format(finalTotalDownTime/3600));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total DownTime ==========");
		Log.printLine( dft.format(finalTotalDownTime));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Application Finishing Time ==========");
		Log.printLine( dft.format(applicationFinishingTime/3600));
		Log.printLine();		
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Energy Consumption ==========");
		Log.printLine( dft.format(averageEnergyConsumption));
		Log.printLine();
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Energy Wastage ==========");
		Log.printLine( dft.format(averageEnergyWastage));
		Log.printLine();
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Idle Energy Consumption ==========");
		Log.printLine( dft.format(averageIdleEnergyConsumption));
		Log.printLine();
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Reexecution Time ==========");
		Log.printLine( dft.format(averageReexecutionTime));
		Log.printLine();
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Reexecution Time Hours ==========");
		Log.printLine( dft.format(averageReexecutionTimeHours));
		Log.printLine();
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Down Time ==========");
		Log.printLine( dft.format(averageDownTime));
		Log.printLine();
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Down Time Hours ==========");
		Log.printLine( dft.format(averageDownTimeHours));
		Log.printLine();		
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Deadline Missed Margin ==========");
		Log.printLine( dft.format(averageDeadlineMissedMargin));
		Log.printLine();
		
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
			//applicationFinishingTime = applicationFinishingTime/3600;
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
		double averageIdleEnergyConsumptionPerHost = 0.0;
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
			averageIdleEnergyConsumptionPerHost = idleEnergyConsumption/peListSize;
			totalIdleEnergyConsumption = totalIdleEnergyConsumption + idleEnergyConsumption; 
			totalAverageIdleEnergyConsumption = totalAverageIdleEnergyConsumption + averageIdleEnergyConsumptionPerHost;
			Log.printLine( indent+ hostIdIdle + indent + indent+ indent+ idleTime + indent + indent+ indent+ downTime + indent + indent+ indent+ idleEnergyConsumption+ indent + indent+ indent+ averageIdleEnergyConsumptionPerHost);
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
		double backupPower;
		
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
			backupPower = 0.0;
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
			double difference;
			
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
				backupPower = power;
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
			//cletEnergyConsumptionWithoutReexecution.put(sufferedClets.get(i).getCloudletId(), energyConsumptionwithoutReexecution);		
		
			cletMigrationDownTime = vmRecord.getVmMigrationDownTimePerHostList(sufferedClets.get(i).getCloudletId());		
			
			double cletMigrationDownTimeEnergyPerHost = 0.0;
			double cletMigrationDownTimeEnergy = 0.0;
			double migrationDownTime = 0.0;
			double actualCletEnergyConsumptionWithReexecution = 0.0;
			double differenceEnergy = 0.0;
			
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
			
			difference = changeinExecutionTime - (totalDownTime + totalMigrationOverheads + reexecutionCletLength);
			
			if(difference > 0.0){
				reexecutionCletLength = reexecutionCletLength + difference;
				differenceEnergy = ((backupPower/1000)*(difference/3600));
				cletReexecutionEnergy = cletReexecutionEnergy + differenceEnergy;
				actualCletEnergyConsumptionWithReexecution = actualCletEnergyConsumptionWithReexecution + differenceEnergy;
			}
			
			cletEnergyWastageWithReexecution.put(sufferedClets.get(i).getCloudletId(), cletReexecutionEnergy);
			
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

		//FileWriter HostCountClock = null;
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
		
		
	//	Log.printLine();
	//	Log.printLine();
		//Log.printLine(indent + "Total Energy Consumption" + indent + indent + indent + "Total Energy Consumption with Reexecution"+ indent + indent + indent + "Total Energy Wastage" + indent + indent + indent + "Total Idle Energy Consumption"  +indent +indent + indent + "Application Utility Value" );
	//	Log.printLine(indent + "Total Energy Consumption" + indent + indent + indent + "Total Energy Wastage" + indent + indent + indent + "Total Idle Energy Consumption"  +indent +indent + indent + "Application Utility Value" );
		
			double totalReliability;	
			double totalReliabilityParallel = 0.0;
			double totalReliabilityAverage = 0.0;
			double totalAvailability = 0.0;
			double totalMaintainability = 0.0;
			double percentageAchievedDeadlines;
			double averageCletCompletionTime;
		//	double averageCletLength;
			double averageExecutionTimeChanged;	
			
			averageCletCompletionTime = totalMakeSpan/size;
		//	averageCletLength = totalCletLength/size;
			averageExecutionTimeChanged = totalExecutionTimeChanged/size;	
			
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
		
			
			double averageEnergyConsumption = 0.0;
			double averageEnergyWastage = 0.0;
			double averageIdleEnergyConsumption = 0.0;
			double averageReexecutionTime = 0.0;
			double averageReexecutionTimeHours = 0.0;
			double averageDownTime = 0.0;
			double averageDownTimeHours = 0.0;
			
			averageEnergyConsumption = totalEnergyConsumption/size;
			averageEnergyWastage = totalEnergyWastage/size;
			averageIdleEnergyConsumption = totalIdleEnergyConsumption/size;
			averageReexecutionTime = finalTotalReexecutionTime/size;
			averageReexecutionTimeHours = ((finalTotalReexecutionTime/3600)/size);
			averageDownTime = finalTotalDownTime/size;
			averageDownTimeHours = ((finalTotalDownTime/3600)/size);
			
		Log.printLine();	
		Log.printLine("==========================================================================================================================================================");
		Log.printLine("**********************************************************************************************************************************************************");
		Log.printLine("==========================================================================================================================================================");
			
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Energy Consumption ==========");
		Log.printLine(dft.format(totalEnergyConsumption));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Energy Wastage ==========");
		Log.printLine(dft.format(totalEnergyWastage));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Idle Energy Consumption ==========");
		Log.printLine(dft.format(totalIdleEnergyConsumption));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Application Utility Value ==========");
		Log.printLine(dft.format(jobUtilityValue));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Deadlines Achieved ==========");
		Log.printLine(countDeadlineAchieved);
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Deadlines Missed ==========");
		Log.printLine(countDeadlineMissed);
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Percentage Achieved Deadlines ==========");
		Log.printLine(dft.format(percentageAchievedDeadlines));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Maintainability ==========");
		Log.printLine(dft.format(totalMaintainability));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Availability ==========");
		Log.printLine(dft.format(totalAvailability));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Reliability ==========");
		Log.printLine(dft.format(totalReliabilityAverage*100));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Execution Time Changed Hours ==========");
		Log.printLine(dft.format(averageExecutionTimeChanged/3600));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Execution Time Changed  ==========");
		Log.printLine(dft.format(averageExecutionTimeChanged));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Finished Time Hours ==========");
		Log.printLine(dft.format(averageCletCompletionTime/3600));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Finished Time  ==========");
		Log.printLine(dft.format(averageCletCompletionTime));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total VM Consolidations  ==========");
		Log.printLine(totalVmConsolidations);
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total VM Migrations  ==========");
		Log.printLine(totalVmMigrations);
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Failures Occurred ==========");
		Log.printLine(totalFailureCount);
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Migration DownTime Hours ==========");
		Log.printLine(dft.format(totalMigrationDownTime/3600));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Migration DownTime ==========");
		Log.printLine(dft.format(totalMigrationDownTime));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Migration Overheads Hours ==========");
		Log.printLine(dft.format(totalMigrationOverhead/3600));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Migration Overheads ==========");
		Log.printLine(dft.format(totalMigrationOverhead));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Hosts ==========");
		Log.printLine(hostList.size());
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Reexecution Time Hours ==========");
		Log.printLine( dft.format(finalTotalReexecutionTime/3600));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total Reexecution Time ==========");
		Log.printLine( dft.format(finalTotalReexecutionTime));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total DownTime Hours ==========");
		Log.printLine( dft.format(finalTotalDownTime/3600));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Total DownTime ==========");
		Log.printLine( dft.format(finalTotalDownTime));
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Application Finishing Time ==========");
		Log.printLine( dft.format(applicationFinishingTime/3600));
		Log.printLine();		
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Energy Consumption ==========");
		Log.printLine( dft.format(averageEnergyConsumption));
		Log.printLine();
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Energy Wastage ==========");
		Log.printLine( dft.format(averageEnergyWastage));
		Log.printLine();
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Idle Energy Consumption ==========");
		Log.printLine( dft.format(averageIdleEnergyConsumption));
		Log.printLine();
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Reexecution Time ==========");
		Log.printLine( dft.format(averageReexecutionTime));
		Log.printLine();
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Reexecution Time Hours ==========");
		Log.printLine( dft.format(averageReexecutionTimeHours));
		Log.printLine();
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Down Time ==========");
		Log.printLine( dft.format(averageDownTime));
		Log.printLine();
		
		Log.printLine();
		Log.printLine();
		Log.printLine("========== Average Down Time Hours ==========");
		Log.printLine( dft.format(averageDownTimeHours));
		Log.printLine();
	
}

private static void printCloudletListRestartingwithMigration(List<Cloudlet> list, List<Host> hostList, FTAFileReader ftaread, FailureDatacenterBroker broker, HostVMMappingReliability hvmRbl, List<Vm>newVmList, VmUtilization vmUtl, PowerModel pow){	
	
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
		//applicationFinishingTime = applicationFinishingTime/3600;
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
	double averageIdleEnergyConsumptionPerHost = 0.0;
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
		averageIdleEnergyConsumptionPerHost = idleEnergyConsumption/peListSize;
		totalIdleEnergyConsumption = totalIdleEnergyConsumption + idleEnergyConsumption; 
		totalAverageIdleEnergyConsumption = totalAverageIdleEnergyConsumption + averageIdleEnergyConsumptionPerHost;
		Log.printLine( indent+ hostIdIdle + indent + indent+ indent+ idleTime + indent + indent+ indent+ downTime + indent + indent+ indent+ idleEnergyConsumption+ indent + indent+ indent+ averageIdleEnergyConsumptionPerHost);
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
	double backupPower;
	
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
		backupPower = 0.0;
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
		double difference;
		
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
			backupPower = power;
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
		//cletEnergyConsumptionWithoutReexecution.put(sufferedClets.get(i).getCloudletId(), energyConsumptionwithoutReexecution);		
	
		cletMigrationDownTime = vmRecord.getVmMigrationDownTimePerHostList(sufferedClets.get(i).getCloudletId());		
		
		double cletMigrationDownTimeEnergyPerHost = 0.0;
		double cletMigrationDownTimeEnergy = 0.0;
		double migrationDownTime = 0.0;
		double actualCletEnergyConsumptionWithReexecution = 0.0;
		double differenceEnergy = 0.0;
		
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
		
		difference = changeinExecutionTime - (totalDownTime + totalMigrationOverheads + reexecutionCletLength);
		
		if(difference > 0.0){
			reexecutionCletLength = reexecutionCletLength + difference;
			differenceEnergy = ((backupPower/1000)*(difference/3600));
			cletReexecutionEnergy = cletReexecutionEnergy + differenceEnergy;
			actualCletEnergyConsumptionWithReexecution = actualCletEnergyConsumptionWithReexecution + differenceEnergy;
		}
		
		cletEnergyWastageWithReexecution.put(sufferedClets.get(i).getCloudletId(), cletReexecutionEnergy);
		
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

	//FileWriter HostCountClock = null;
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
	
	
//	Log.printLine();
//	Log.printLine();
	//Log.printLine(indent + "Total Energy Consumption" + indent + indent + indent + "Total Energy Consumption with Reexecution"+ indent + indent + indent + "Total Energy Wastage" + indent + indent + indent + "Total Idle Energy Consumption"  +indent +indent + indent + "Application Utility Value" );
//	Log.printLine(indent + "Total Energy Consumption" + indent + indent + indent + "Total Energy Wastage" + indent + indent + indent + "Total Idle Energy Consumption"  +indent +indent + indent + "Application Utility Value" );
	
		double totalReliability;	
		double totalReliabilityParallel = 0.0;
		double totalReliabilityAverage = 0.0;
		double totalAvailability = 0.0;
		double totalMaintainability = 0.0;
		double percentageAchievedDeadlines;
		double averageCletCompletionTime;
	//	double averageCletLength;
		double averageExecutionTimeChanged;	
		
		averageCletCompletionTime = totalMakeSpan/size;
	//	averageCletLength = totalCletLength/size;
		averageExecutionTimeChanged = totalExecutionTimeChanged/size;	
		
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
	
		
		double averageEnergyConsumption = 0.0;
		double averageEnergyWastage = 0.0;
		double averageIdleEnergyConsumption = 0.0;
		double averageReexecutionTime = 0.0;
		double averageReexecutionTimeHours = 0.0;
		double averageDownTime = 0.0;
		double averageDownTimeHours = 0.0;
		
		averageEnergyConsumption = totalEnergyConsumption/size;
		averageEnergyWastage = totalEnergyWastage/size;
		averageIdleEnergyConsumption = totalIdleEnergyConsumption/size;
		averageReexecutionTime = finalTotalReexecutionTime/size;
		averageReexecutionTimeHours = ((finalTotalReexecutionTime/3600)/size);
		averageDownTime = finalTotalDownTime/size;
		averageDownTimeHours = ((finalTotalDownTime/3600)/size);
		
	Log.printLine();	
	Log.printLine("==========================================================================================================================================================");
	Log.printLine("**********************************************************************************************************************************************************");
	Log.printLine("==========================================================================================================================================================");
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Energy Consumption ==========");
	Log.printLine(dft.format(totalEnergyConsumption));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Energy Wastage ==========");
	Log.printLine(dft.format(totalEnergyWastage));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Idle Energy Consumption ==========");
	Log.printLine(dft.format(totalIdleEnergyConsumption));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Application Utility Value ==========");
	Log.printLine(dft.format(jobUtilityValue));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Deadlines Achieved ==========");
	Log.printLine(countDeadlineAchieved);
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Deadlines Missed ==========");
	Log.printLine(countDeadlineMissed);
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Percentage Achieved Deadlines ==========");
	Log.printLine(dft.format(percentageAchievedDeadlines));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Maintainability ==========");
	Log.printLine(dft.format(totalMaintainability));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Availability ==========");
	Log.printLine(dft.format(totalAvailability));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Reliability ==========");
	Log.printLine(dft.format(totalReliabilityAverage*100));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Execution Time Changed Hours ==========");
	Log.printLine(dft.format(averageExecutionTimeChanged/3600));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Execution Time Changed  ==========");
	Log.printLine(dft.format(averageExecutionTimeChanged));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Finished Time Hours ==========");
	Log.printLine(dft.format(averageCletCompletionTime/3600));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Finished Time  ==========");
	Log.printLine(dft.format(averageCletCompletionTime));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total VM Consolidations  ==========");
	Log.printLine(totalVmConsolidations);
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total VM Migrations  ==========");
	Log.printLine(totalVmMigrations);
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Failures Occurred ==========");
	Log.printLine(totalFailureCount);
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Migration DownTime Hours ==========");
	Log.printLine(dft.format(totalMigrationDownTime/3600));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Migration DownTime ==========");
	Log.printLine(dft.format(totalMigrationDownTime));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Migration Overheads Hours ==========");
	Log.printLine(dft.format(totalMigrationOverhead/3600));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Migration Overheads ==========");
	Log.printLine(dft.format(totalMigrationOverhead));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Hosts ==========");
	Log.printLine(hostList.size());
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Reexecution Time Hours ==========");
	Log.printLine( dft.format(finalTotalReexecutionTime/3600));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Reexecution Time ==========");
	Log.printLine( dft.format(finalTotalReexecutionTime));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total DownTime Hours ==========");
	Log.printLine( dft.format(finalTotalDownTime/3600));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total DownTime ==========");
	Log.printLine( dft.format(finalTotalDownTime));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Application Finishing Time ==========");
	Log.printLine( dft.format(applicationFinishingTime/3600));
	Log.printLine();		
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Energy Consumption ==========");
	Log.printLine( dft.format(averageEnergyConsumption));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Energy Wastage ==========");
	Log.printLine( dft.format(averageEnergyWastage));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Idle Energy Consumption ==========");
	Log.printLine( dft.format(averageIdleEnergyConsumption));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Reexecution Time ==========");
	Log.printLine( dft.format(averageReexecutionTime));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Reexecution Time Hours ==========");
	Log.printLine( dft.format(averageReexecutionTimeHours));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Down Time ==========");
	Log.printLine( dft.format(averageDownTime));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Down Time Hours ==========");
	Log.printLine( dft.format(averageDownTimeHours));
	Log.printLine();

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
	double averageIdleEnergyConsumptionPerHost = 0.0;
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
		averageIdleEnergyConsumptionPerHost = idleEnergyConsumption/peListSize;
		totalIdleEnergyConsumption = totalIdleEnergyConsumption + idleEnergyConsumption; 
		totalAverageIdleEnergyConsumption =totalAverageIdleEnergyConsumption + averageIdleEnergyConsumptionPerHost;
		
		//Log.printLine( indent+ hostIdIdle + indent + indent+ indent+ idleTime + indent + indent+ indent+ idleEnergyConsumption);
		Log.printLine( indent+ hostIdIdle + indent + indent+ indent+ idleTime + indent + indent+ indent+ downTime + indent + indent+ indent+ idleEnergyConsumption+ indent + indent+ indent+ averageIdleEnergyConsumptionPerHost);

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
	double difference;
	
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
		double backupPower = 0.0;
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
			backupPower = power;
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
	//	cletEnergyConsumptionWithoutReexecution.put(sufferedClets.get(i).getCloudletId(), energyConsumptionwithoutReexecution);
	
		cletMigrationDownTime = vmRecord.getVmMigrationDownTimePerHostList(sufferedClets.get(i).getCloudletId());		
		
		double cletMigrationDownTimeEnergyPerHost = 0.0;
		double cletMigrationDownTimeEnergy = 0.0;
		double migrationDownTime = 0.0;			
		double actualCletEnergyConsumptionWithReexecution = 0.0;
		double differenceEnergy;
		
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
		
		difference = changeinExecutionTime - (totalDownTime + totalMigrationOverheads + reexecutionCletLength);
		
		if(difference > 0.0){
			reexecutionCletLength = reexecutionCletLength + difference;
			differenceEnergy = ((backupPower/1000)*(difference/3600));
			cletReexecutionEnergy = cletReexecutionEnergy + differenceEnergy;
			actualCletEnergyConsumptionWithReexecution = actualCletEnergyConsumptionWithReexecution + differenceEnergy;
		}
		
		cletEnergyWastageWithReexecution.put(sufferedClets.get(i).getCloudletId(), cletReexecutionEnergy);
		
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
	//Log.printLine("Cloudlet ID" + indent +" Task Execution Length" + indent + indent + "Total Finish Time" + indent + indent + indent + "Execution Time Changed" + indent + indent + indent +indent + "Total DownTime" + indent +indent + indent + "Total Migration Overheads" + indent +indent + indent + "Total Re-execution Time" + indent + indent + indent + "Energy Consumption without Reexecution" + indent + indent + indent + "Energy Consumption with Reexecution" );
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
	
	
//	Log.printLine();
//	Log.printLine();
//	Log.printLine("========== Total Energy OUTPUT ==========");
	//Log.printLine("Total Energy Consumption" + indent + indent + indent + "Total Energy Consumption with Reexecution" + indent + indent + indent + "Total Energy Wastage" + indent + indent + indent + "Total Idle Energy Consumption"  +indent +indent + indent + "Application Utility Value" );
//	Log.printLine("Total Energy Consumption" + indent + indent + indent + "Total Energy Wastage" + indent + indent + indent + "Total Idle Energy Consumption"  +indent +indent + indent + "Application Utility Value" );
	
		double totalReliability;	
		double totalReliabilityParallel = 0.0;
		double totalReliabilityAverage = 0.0;
		double totalAvailability = 0.0;
		double totalMaintainability = 0.0;
		double percentageAchievedDeadlines;
		
		double averageCletCompletionTime;
		//double averageCletLength;
		double averageExecutionTimeChanged;		
		
		averageCletCompletionTime = totalMakeSpan/size;
		//averageCletLength = totalCletLength/size;
		averageExecutionTimeChanged = totalExecutionTimeChanged/size;	
				
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
	
		double averageEnergyConsumption = 0.0;
		double averageEnergyWastage = 0.0;
		double averageIdleEnergyConsumption = 0.0;
		double averageReexecutionTime = 0.0;
		double averageReexecutionTimeHours = 0.0;
		double averageDownTime = 0.0;
		double averageDownTimeHours = 0.0;
		
		averageEnergyConsumption = totalEnergyConsumption/size;
		averageEnergyWastage = totalEnergyWastage/size;
		averageIdleEnergyConsumption = totalIdleEnergyConsumption/size;
		averageReexecutionTime = finalTotalReexecutionTime/size;
		averageReexecutionTimeHours = ((finalTotalReexecutionTime/3600)/size);
		averageDownTime = finalTotalDownTime/size;
		averageDownTimeHours = ((finalTotalDownTime/3600)/size);		
		
	Log.printLine();	
	Log.printLine("==========================================================================================================================================================");
	Log.printLine("**********************************************************************************************************************************************************");
	Log.printLine("==========================================================================================================================================================");
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Energy Consumption ==========");
	Log.printLine(dft.format(totalEnergyConsumption));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Energy Wastage ==========");
	Log.printLine(dft.format(totalEnergyWastage));

	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Idle Energy Consumption ==========");
	Log.printLine(dft.format(totalIdleEnergyConsumption));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Application Utility Value ==========");
	Log.printLine(dft.format(jobUtilityValue));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Deadlines Achieved ==========");
	Log.printLine(countDeadlineAchieved);
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Deadlines Missed ==========");
	Log.printLine(countDeadlineMissed);
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Percentage Achieved Deadlines ==========");
	Log.printLine(dft.format(percentageAchievedDeadlines));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Maintainability ==========");
	Log.printLine(dft.format(totalMaintainability));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Availability ==========");
	Log.printLine(dft.format(totalAvailability));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Reliability ==========");
	Log.printLine(dft.format(totalReliabilityAverage*100));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Execution Time Changed ==========");
	Log.printLine(dft.format(averageExecutionTimeChanged));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Execution Time Changed Hours ==========");
	Log.printLine(dft.format(averageExecutionTimeChanged/3600));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Finished Time ==========");
	Log.printLine(dft.format(averageCletCompletionTime));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Finished Time Hours ==========");
	Log.printLine(dft.format(averageCletCompletionTime/3600));
			
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total VM Consolidations ==========");
	Log.printLine(totalVmConsolidations);
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Checkpoints ==========");
	Log.printLine(totalCheckpointsCount);
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total VM Migrations ==========");
	Log.printLine(totalVmMigrations);
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Failures Occurred ==========");
	Log.printLine(totalFailureCount);
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Migration DownTime ==========");
	Log.printLine(dft.format(totalMigrationDownTime));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Migration DownTime Hours ==========");
	Log.printLine(dft.format(totalMigrationDownTime/3600));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Migration Overheads ==========");
	Log.printLine(dft.format(totalMigrationOverhead));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Migration Overheads Hours ==========");
	Log.printLine(dft.format(totalMigrationOverhead/3600));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Hosts ==========");
	Log.printLine(hostList.size());
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Reexecution Time Hours ==========");
	Log.printLine(dft.format(finalTotalReexecutionTime/3600));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Reexecution Time  ==========");
	Log.printLine(dft.format(finalTotalReexecutionTime));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total DownTime Hours  ==========");
	Log.printLine(dft.format(finalTotalDownTime/3600));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total DownTime  ==========");
	Log.printLine(dft.format(finalTotalDownTime));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Application Finishing Time ==========");	
	Log.printLine(dft.format(applicationFinishingTime/3600));	
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Energy Consumption ==========");
	Log.printLine( dft.format(averageEnergyConsumption));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Energy Wastage ==========");
	Log.printLine( dft.format(averageEnergyWastage));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Idle Energy Consumption ==========");
	Log.printLine( dft.format(averageIdleEnergyConsumption));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Reexecution Time ==========");
	Log.printLine( dft.format(averageReexecutionTime));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Reexecution Time Hours ==========");
	Log.printLine( dft.format(averageReexecutionTimeHours));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Down Time ==========");
	Log.printLine( dft.format(averageDownTime));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Down Time Hours ==========");
	Log.printLine( dft.format(averageDownTimeHours));
	Log.printLine();	
}

private static void printCloudletListCheckpointingwithMigration(List<Cloudlet> list, List<Host> hostList, FTAFileReader ftaread, FailureDatacenterBroker broker, HostVMMappingReliability hvmRbl, List<Vm>newVmList, VmUtilization vmUtl, PowerModel pow){
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
	double averageIdleEnergyConsumptionPerHost = 0.0;
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
		averageIdleEnergyConsumptionPerHost = idleEnergyConsumption/peListSize;
		totalIdleEnergyConsumption = totalIdleEnergyConsumption + idleEnergyConsumption; 
		totalAverageIdleEnergyConsumption =totalAverageIdleEnergyConsumption + averageIdleEnergyConsumptionPerHost;
		
		//Log.printLine( indent+ hostIdIdle + indent + indent+ indent+ idleTime + indent + indent+ indent+ idleEnergyConsumption);
		Log.printLine( indent+ hostIdIdle + indent + indent+ indent+ idleTime + indent + indent+ indent+ downTime + indent + indent+ indent+ idleEnergyConsumption+ indent + indent+ indent+ averageIdleEnergyConsumptionPerHost);

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
	double difference;
	
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
		double backupPower = 0.0;
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
			backupPower = power;
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
	//	cletEnergyConsumptionWithoutReexecution.put(sufferedClets.get(i).getCloudletId(), energyConsumptionwithoutReexecution);
	
		cletMigrationDownTime = vmRecord.getVmMigrationDownTimePerHostList(sufferedClets.get(i).getCloudletId());		
		
		double cletMigrationDownTimeEnergyPerHost = 0.0;
		double cletMigrationDownTimeEnergy = 0.0;
		double migrationDownTime = 0.0;			
		double actualCletEnergyConsumptionWithReexecution = 0.0;
		double differenceEnergy;
		
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
		
		difference = changeinExecutionTime - (totalDownTime + totalMigrationOverheads + reexecutionCletLength);
		
		if(difference > 0.0){
			reexecutionCletLength = reexecutionCletLength + difference;
			differenceEnergy = ((backupPower/1000)*(difference/3600));
			cletReexecutionEnergy = cletReexecutionEnergy + differenceEnergy;
			actualCletEnergyConsumptionWithReexecution = actualCletEnergyConsumptionWithReexecution + differenceEnergy;
		}
		
		cletEnergyWastageWithReexecution.put(sufferedClets.get(i).getCloudletId(), cletReexecutionEnergy);
		
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
	//Log.printLine("Cloudlet ID" + indent +" Task Execution Length" + indent + indent + "Total Finish Time" + indent + indent + indent + "Execution Time Changed" + indent + indent + indent +indent + "Total DownTime" + indent +indent + indent + "Total Migration Overheads" + indent +indent + indent + "Total Re-execution Time" + indent + indent + indent + "Energy Consumption without Reexecution" + indent + indent + indent + "Energy Consumption with Reexecution" );
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
	
	
//	Log.printLine();
//	Log.printLine();
//	Log.printLine("========== Total Energy OUTPUT ==========");
	//Log.printLine("Total Energy Consumption" + indent + indent + indent + "Total Energy Consumption with Reexecution" + indent + indent + indent + "Total Energy Wastage" + indent + indent + indent + "Total Idle Energy Consumption"  +indent +indent + indent + "Application Utility Value" );
//	Log.printLine("Total Energy Consumption" + indent + indent + indent + "Total Energy Wastage" + indent + indent + indent + "Total Idle Energy Consumption"  +indent +indent + indent + "Application Utility Value" );
	
		double totalReliability;	
		double totalReliabilityParallel = 0.0;
		double totalReliabilityAverage = 0.0;
		double totalAvailability = 0.0;
		double totalMaintainability = 0.0;
		double percentageAchievedDeadlines;
		
		double averageCletCompletionTime;
		//double averageCletLength;
		double averageExecutionTimeChanged;		
		
		averageCletCompletionTime = totalMakeSpan/size;
		//averageCletLength = totalCletLength/size;
		averageExecutionTimeChanged = totalExecutionTimeChanged/size;	
				
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
	
		double averageEnergyConsumption = 0.0;
		double averageEnergyWastage = 0.0;
		double averageIdleEnergyConsumption = 0.0;
		double averageReexecutionTime = 0.0;
		double averageReexecutionTimeHours = 0.0;
		double averageDownTime = 0.0;
		double averageDownTimeHours = 0.0;
		
		averageEnergyConsumption = totalEnergyConsumption/size;
		averageEnergyWastage = totalEnergyWastage/size;
		averageIdleEnergyConsumption = totalIdleEnergyConsumption/size;
		averageReexecutionTime = finalTotalReexecutionTime/size;
		averageReexecutionTimeHours = ((finalTotalReexecutionTime/3600)/size);
		averageDownTime = finalTotalDownTime/size;
		averageDownTimeHours = ((finalTotalDownTime/3600)/size);		
		
	Log.printLine();	
	Log.printLine("==========================================================================================================================================================");
	Log.printLine("**********************************************************************************************************************************************************");
	Log.printLine("==========================================================================================================================================================");
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Energy Consumption ==========");
	Log.printLine(dft.format(totalEnergyConsumption));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Energy Wastage ==========");
	Log.printLine(dft.format(totalEnergyWastage));

	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Idle Energy Consumption ==========");
	Log.printLine(dft.format(totalIdleEnergyConsumption));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Application Utility Value ==========");
	Log.printLine(dft.format(jobUtilityValue));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Deadlines Achieved ==========");
	Log.printLine(countDeadlineAchieved);
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Deadlines Missed ==========");
	Log.printLine(countDeadlineMissed);
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Percentage Achieved Deadlines ==========");
	Log.printLine(dft.format(percentageAchievedDeadlines));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Maintainability ==========");
	Log.printLine(dft.format(totalMaintainability));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Availability ==========");
	Log.printLine(dft.format(totalAvailability));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Reliability ==========");
	Log.printLine(dft.format(totalReliabilityAverage*100));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Execution Time Changed ==========");
	Log.printLine(dft.format(averageExecutionTimeChanged));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Execution Time Changed Hours ==========");
	Log.printLine(dft.format(averageExecutionTimeChanged/3600));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Finished Time ==========");
	Log.printLine(dft.format(averageCletCompletionTime));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Finished Time Hours ==========");
	Log.printLine(dft.format(averageCletCompletionTime/3600));
			
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total VM Consolidations ==========");
	Log.printLine(totalVmConsolidations);
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Checkpoints ==========");
	Log.printLine(totalCheckpointsCount);
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total VM Migrations ==========");
	Log.printLine(totalVmMigrations);
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Failures Occurred ==========");
	Log.printLine(totalFailureCount);
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Migration DownTime ==========");
	Log.printLine(dft.format(totalMigrationDownTime));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Migration DownTime Hours ==========");
	Log.printLine(dft.format(totalMigrationDownTime/3600));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Migration Overheads ==========");
	Log.printLine(dft.format(totalMigrationOverhead));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Migration Overheads Hours ==========");
	Log.printLine(dft.format(totalMigrationOverhead/3600));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Hosts ==========");
	Log.printLine(hostList.size());
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Reexecution Time Hours ==========");
	Log.printLine(dft.format(finalTotalReexecutionTime/3600));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Reexecution Time  ==========");
	Log.printLine(dft.format(finalTotalReexecutionTime));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total DownTime Hours  ==========");
	Log.printLine(dft.format(finalTotalDownTime/3600));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total DownTime  ==========");
	Log.printLine(dft.format(finalTotalDownTime));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Application Finishing Time ==========");	
	Log.printLine(dft.format(applicationFinishingTime/3600));	
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Energy Consumption ==========");
	Log.printLine( dft.format(averageEnergyConsumption));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Energy Wastage ==========");
	Log.printLine( dft.format(averageEnergyWastage));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Idle Energy Consumption ==========");
	Log.printLine( dft.format(averageIdleEnergyConsumption));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Reexecution Time ==========");
	Log.printLine( dft.format(averageReexecutionTime));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Reexecution Time Hours ==========");
	Log.printLine( dft.format(averageReexecutionTimeHours));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Down Time ==========");
	Log.printLine( dft.format(averageDownTime));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Down Time Hours ==========");
	Log.printLine( dft.format(averageDownTimeHours));
	Log.printLine();	
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
	double averageIdleEnergyConsumptionPerHost = 0.0;
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
		averageIdleEnergyConsumptionPerHost = idleEnergyConsumption/peListSize;
		totalIdleEnergyConsumption = totalIdleEnergyConsumption + idleEnergyConsumption; 
		totalAverageIdleEnergyConsumption = totalAverageIdleEnergyConsumption + averageIdleEnergyConsumptionPerHost;
		Log.printLine( indent+ hostIDIdle + indent + indent+ indent+ idleTime + indent + indent+ indent+ downTime + indent + indent+ indent+ idleEnergyConsumption+ indent + indent+ indent+ averageIdleEnergyConsumptionPerHost);
	}

	Log.printLine("========== OUTPUT For Host Idle Time  ==========");
	Log.printLine("Host ID" +indent+ indent + indent + "Total Idle Time" +indent+ indent + indent + "Total Down Time" +indent+ indent + indent + "Total Idle Energy Consumption" +indent+ indent + indent + "Average Idle Energy Consumption" );		
		
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== OUTPUT For Energy Consumption Cloudlet Level==========");
//	Log.printLine("Cloudlet ID" + indent +" Task Execution Length" + indent + indent + "Total Finish Time" + indent + indent + indent + "Execution Time Changed" + indent + indent + indent +indent + "Total DownTime" + indent +indent + indent + "Total Migration Overheads" + indent +indent + indent + "Total Re-execution Time" + indent + indent + indent + "Energy Consumption without Reexecution" + indent + indent + indent + "Energy Consumption with Reexecution" );
	Log.printLine("Cloudlet ID" + indent +" Task Execution Length" + indent + indent + "Total Finish Time" + indent + indent + indent + "Execution Time Changed" + indent + indent + indent +indent + "Total DownTime" + indent +indent + indent + "Total Migration Overheads" + indent +indent + indent + "Total Re-execution Time" + indent + indent + indent + "Energy Consumption with Reexecution" );
	
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
		double backupPower = 0.0;
		double difference = 0.0;
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
			backupPower = power;
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
		//cletEnergyConsumptionWithoutReexecution.put(sufferedClets.get(i).getCloudletId(), energyConsumptionwithoutReexecution);	
		
	
		cletMigrationDownTime = vmRecord.getVmMigrationDownTimePerHostList(sufferedClets.get(i).getCloudletId());	
		
		double cletMigrationDownTimeEnergyPerHost = 0.0;
		double cletMigrationDownTimeEnergy = 0.0;
		double migrationDownTime = 0.0;		
		double actualCletEnergyConsumptionWithReexecution = 0.0;
		double differenceEnergy = 0.0;
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
		
		difference = changeinExecutionTime - (totalDownTime + totalMigrationOverheads + reexecutionCletLength);
		if(difference > 0.0){
			reexecutionCletLength = reexecutionCletLength + difference;
			differenceEnergy = ((backupPower/1000)*(difference/3600));
			cletReexecutionEnergy = cletReexecutionEnergy + differenceEnergy;
			actualCletEnergyConsumptionWithReexecution = actualCletEnergyConsumptionWithReexecution + differenceEnergy;
		}
		
		cletEnergyWastageWithReexecution.put(sufferedClets.get(i).getCloudletId(), cletReexecutionEnergy);
		
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
				//	indent + indent + indent + indent + indent + indent + indent + indent + dft.format(actualCletEnergyConsumption)+
					indent + indent + indent + indent +indent + indent + indent +dft.format(actualCletEnergyConsumptionWithReexecution));		
	}		
	
	
	Log.printLine("========== OUTPUT For Energy Consumption Cloudlet Level ==========");
//	Log.printLine("Cloudlet ID" + indent +" Task Execution Length" + indent + indent + "Total Finish Time" + indent + indent + indent + "Execution Time Changed" + indent + indent + indent +indent + "Total DownTime" + indent +indent + indent + "Total Migration Overheads" + indent +indent + indent + "Total Re-execution Time with Last Host" + indent +indent + indent + "Total Re-execution Time with per Host" +indent+indent + indent + "Energy Consumption without Reexecution" +indent+indent + indent + "Energy Consumption with Reexecution" );
	//Log.printLine("Cloudlet ID" + indent +" Task Execution Length" + indent + indent + "Total Finish Time" + indent + indent + indent + "Execution Time Changed" + indent + indent + indent +indent + "Total DownTime" + indent +indent + indent + "Total Migration Overheads" + indent +indent + indent + "Total Re-execution Time" + indent + indent + indent + "Energy Consumption without Reexecution" + indent + indent + indent + "Energy Consumption with Reexecution" );
	Log.printLine("Cloudlet ID" + indent +" Task Execution Length" + indent + indent + "Total Finish Time" + indent + indent + indent + "Execution Time Changed" + indent + indent + indent +indent + "Total DownTime" + indent +indent + indent + "Total Migration Overheads" + indent +indent + indent + "Total Re-execution Time"  + indent + indent + indent + "Energy Consumption with Reexecution" );
	
	
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
	
	
//	Log.printLine();
//	Log.printLine();
	//Log.printLine("Total Energy Consumption" + indent + indent + indent + "Total Energy Consumption with Reexecution" + indent + indent + indent + "Total Energy Wastage per Host" + indent + indent + indent + "Total Energy Wastage Last Host"+ indent + indent + indent + "Total Idle Energy Consumption"  +indent +indent + indent + "Application Utility Value" );
//	Log.printLine("Total Energy Consumption" + indent + indent + indent + "Total Energy Wastage " +  indent + indent + indent + "Total Idle Energy Consumption"  +indent +indent + indent + "Application Utility Value" );
	
	
		double totalReliability;	
		double totalReliabilityParallel = 0.0;
		double totalReliabilityAverage = 0.0;
		double totalAvailability = 0.0;
		double totalMaintainability = 0.0;
		double percentageAchievedDeadlines;
		double averageCletCompletionTime;
		double averageCletLength;
		double averageExecutionTimeChanged;
		//ArrayList<Double>energyConsumptionperHost = new ArrayList<Double>();	
		averageCletCompletionTime = totalMakeSpan/size;
		averageCletLength = totalCletLength/size;
		averageExecutionTimeChanged = totalExecutionTimeChanged/size;			
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
		
		double averageEnergyConsumption = 0.0;
		double averageEnergyWastage = 0.0;
		double averageIdleEnergyConsumption = 0.0;
		double averageReexecutionTime = 0.0;
		double averageReexecutionTimeHours = 0.0;
		double averageDownTime = 0.0;
		double averageDownTimeHours = 0.0;
		
		averageEnergyConsumption = totalEnergyConsumption/size;
		averageEnergyWastage = totalEnergyWastage/size;
		averageIdleEnergyConsumption = totalIdleEnergyConsumption/size;
		averageReexecutionTime = finalTotalReexecutionTime/size;
		averageReexecutionTimeHours = ((finalTotalReexecutionTime/3600)/size);
		averageDownTime = finalTotalDownTime/size;
		averageDownTimeHours = ((finalTotalDownTime/3600)/size);		
		
	Log.printLine();
	Log.printLine("==========================================================================================================================================================");
	Log.printLine("**********************************************************************************************************************************************************");
	Log.printLine("==========================================================================================================================================================");
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Energy Consumption ==========");
	Log.printLine(dft.format(totalEnergyConsumption));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Energy Wastage ==========");
	Log.printLine(dft.format(totalEnergyWastage));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Idle Energy Consumption ==========");
	Log.printLine(dft.format(totalIdleEnergyConsumption));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Application Utility Value ==========");
	Log.printLine(dft.format(jobUtilityValue));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Deadlines Achieved ==========");
	Log.printLine(countDeadlineAchieved);
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Deadlines Missed ==========");
	Log.printLine(countDeadlineMissed);
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Percentage Achieved Deadlines ==========");
	Log.printLine(dft.format(percentageAchievedDeadlines));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Maintainability ==========");
	Log.printLine(dft.format(totalMaintainability));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Availability ==========");
	Log.printLine(dft.format(totalAvailability));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Reliability ==========");
	Log.printLine(dft.format(totalReliabilityAverage*100));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Execution Time Changed Hours ==========");
	Log.printLine(dft.format(averageExecutionTimeChanged/3600));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Execution Time Changed  ==========");
	Log.printLine(dft.format(averageExecutionTimeChanged));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Makespan Hours ==========");
	Log.printLine(dft.format(averageCletCompletionTime/3600));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Makespan  ==========");
	Log.printLine(dft.format(averageCletCompletionTime));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total VM Consolidations  ==========");
	Log.printLine(totalVmConsolidations);

	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Failures Occurred ==========");
	Log.printLine(totalFailureCount);
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Migration DownTime Hours ==========");
	Log.printLine(dft.format(totalMigrationDownTime/3600));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Migration DownTime ==========");
	Log.printLine(dft.format(totalMigrationDownTime));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Migration Overheads Hours ==========");
	Log.printLine(dft.format(totalMigrationOverhead/3600));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Migration Overheads ==========");
	Log.printLine(dft.format(totalMigrationOverhead));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Hosts ==========");
	Log.printLine(hostList.size());
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Reexecution Time Hours ==========");
	Log.printLine( dft.format(finalTotalReexecutionTime/3600));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Reexecution Time ==========");
	Log.printLine( dft.format(finalTotalReexecutionTime));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total DownTime Hours ==========");
	Log.printLine( dft.format(finalTotalDownTime/3600));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total DownTime ==========");
	Log.printLine( dft.format(finalTotalDownTime));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Application Finishing Time ==========");
	Log.printLine( dft.format(applicationFinishingTime/3600));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Energy Consumption ==========");
	Log.printLine( dft.format(averageEnergyConsumption));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Energy Wastage ==========");
	Log.printLine( dft.format(averageEnergyWastage));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Idle Energy Consumption ==========");
	Log.printLine( dft.format(averageIdleEnergyConsumption));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Reexecution Time ==========");
	Log.printLine( dft.format(averageReexecutionTime));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Reexecution Time Hours ==========");
	Log.printLine( dft.format(averageReexecutionTimeHours));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Down Time ==========");
	Log.printLine( dft.format(averageDownTime));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Down Time Hours ==========");
	Log.printLine( dft.format(averageDownTimeHours));
	Log.printLine();	
}

private static void printCloudletListRestartingwithConsolidation(List<Cloudlet> list, List<Host> hostList, FTAFileReader ftaread, FailureDatacenterBroker broker, HostVMMappingReliability hvmRbl, List<Vm>newVmList, VmUtilization vmUtl, PowerModel pow){	
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
	double averageIdleEnergyConsumptionPerHost = 0.0;
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
		averageIdleEnergyConsumptionPerHost = idleEnergyConsumption/peListSize;
		totalIdleEnergyConsumption = totalIdleEnergyConsumption + idleEnergyConsumption; 
		totalAverageIdleEnergyConsumption = totalAverageIdleEnergyConsumption + averageIdleEnergyConsumptionPerHost;
		Log.printLine( indent+ hostIDIdle + indent + indent+ indent+ idleTime + indent + indent+ indent+ downTime + indent + indent+ indent+ idleEnergyConsumption+ indent + indent+ indent+ averageIdleEnergyConsumptionPerHost);
	}

	Log.printLine("========== OUTPUT For Host Idle Time  ==========");
	Log.printLine("Host ID" +indent+ indent + indent + "Total Idle Time" +indent+ indent + indent + "Total Down Time" +indent+ indent + indent + "Total Idle Energy Consumption" +indent+ indent + indent + "Average Idle Energy Consumption" );		
		
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== OUTPUT For Energy Consumption Cloudlet Level==========");
//	Log.printLine("Cloudlet ID" + indent +" Task Execution Length" + indent + indent + "Total Finish Time" + indent + indent + indent + "Execution Time Changed" + indent + indent + indent +indent + "Total DownTime" + indent +indent + indent + "Total Migration Overheads" + indent +indent + indent + "Total Re-execution Time" + indent + indent + indent + "Energy Consumption without Reexecution" + indent + indent + indent + "Energy Consumption with Reexecution" );
	Log.printLine("Cloudlet ID" + indent +" Task Execution Length" + indent + indent + "Total Finish Time" + indent + indent + indent + "Execution Time Changed" + indent + indent + indent +indent + "Total DownTime" + indent +indent + indent + "Total Migration Overheads" + indent +indent + indent + "Total Re-execution Time" + indent + indent + indent + "Energy Consumption with Reexecution" );
	
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
		double backupPower = 0.0;
		double difference = 0.0;
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
			backupPower = power;
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
		//cletEnergyConsumptionWithoutReexecution.put(sufferedClets.get(i).getCloudletId(), energyConsumptionwithoutReexecution);	
		
	
		cletMigrationDownTime = vmRecord.getVmMigrationDownTimePerHostList(sufferedClets.get(i).getCloudletId());	
		
		double cletMigrationDownTimeEnergyPerHost = 0.0;
		double cletMigrationDownTimeEnergy = 0.0;
		double migrationDownTime = 0.0;		
		double actualCletEnergyConsumptionWithReexecution = 0.0;
		double differenceEnergy = 0.0;
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
		
		difference = changeinExecutionTime - (totalDownTime + totalMigrationOverheads + reexecutionCletLength);
		if(difference > 0.0){
			reexecutionCletLength = reexecutionCletLength + difference;
			differenceEnergy = ((backupPower/1000)*(difference/3600));
			cletReexecutionEnergy = cletReexecutionEnergy + differenceEnergy;
			actualCletEnergyConsumptionWithReexecution = actualCletEnergyConsumptionWithReexecution + differenceEnergy;
		}
		
		cletEnergyWastageWithReexecution.put(sufferedClets.get(i).getCloudletId(), cletReexecutionEnergy);
		
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
				//	indent + indent + indent + indent + indent + indent + indent + indent + dft.format(actualCletEnergyConsumption)+
					indent + indent + indent + indent +indent + indent + indent +dft.format(actualCletEnergyConsumptionWithReexecution));		
	}		
	
	
	Log.printLine("========== OUTPUT For Energy Consumption Cloudlet Level ==========");
//	Log.printLine("Cloudlet ID" + indent +" Task Execution Length" + indent + indent + "Total Finish Time" + indent + indent + indent + "Execution Time Changed" + indent + indent + indent +indent + "Total DownTime" + indent +indent + indent + "Total Migration Overheads" + indent +indent + indent + "Total Re-execution Time with Last Host" + indent +indent + indent + "Total Re-execution Time with per Host" +indent+indent + indent + "Energy Consumption without Reexecution" +indent+indent + indent + "Energy Consumption with Reexecution" );
	//Log.printLine("Cloudlet ID" + indent +" Task Execution Length" + indent + indent + "Total Finish Time" + indent + indent + indent + "Execution Time Changed" + indent + indent + indent +indent + "Total DownTime" + indent +indent + indent + "Total Migration Overheads" + indent +indent + indent + "Total Re-execution Time" + indent + indent + indent + "Energy Consumption without Reexecution" + indent + indent + indent + "Energy Consumption with Reexecution" );
	Log.printLine("Cloudlet ID" + indent +" Task Execution Length" + indent + indent + "Total Finish Time" + indent + indent + indent + "Execution Time Changed" + indent + indent + indent +indent + "Total DownTime" + indent +indent + indent + "Total Migration Overheads" + indent +indent + indent + "Total Re-execution Time"  + indent + indent + indent + "Energy Consumption with Reexecution" );
	
	
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
	
	
//	Log.printLine();
//	Log.printLine();
	//Log.printLine("Total Energy Consumption" + indent + indent + indent + "Total Energy Consumption with Reexecution" + indent + indent + indent + "Total Energy Wastage per Host" + indent + indent + indent + "Total Energy Wastage Last Host"+ indent + indent + indent + "Total Idle Energy Consumption"  +indent +indent + indent + "Application Utility Value" );
//	Log.printLine("Total Energy Consumption" + indent + indent + indent + "Total Energy Wastage " +  indent + indent + indent + "Total Idle Energy Consumption"  +indent +indent + indent + "Application Utility Value" );
	
	
		double totalReliability;	
		double totalReliabilityParallel = 0.0;
		double totalReliabilityAverage = 0.0;
		double totalAvailability = 0.0;
		double totalMaintainability = 0.0;
		double percentageAchievedDeadlines;
		double averageCletCompletionTime;
		double averageCletLength;
		double averageExecutionTimeChanged;
		//ArrayList<Double>energyConsumptionperHost = new ArrayList<Double>();	
		averageCletCompletionTime = totalMakeSpan/size;
		averageCletLength = totalCletLength/size;
		averageExecutionTimeChanged = totalExecutionTimeChanged/size;			
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
		
		double averageEnergyConsumption = 0.0;
		double averageEnergyWastage = 0.0;
		double averageIdleEnergyConsumption = 0.0;
		double averageReexecutionTime = 0.0;
		double averageReexecutionTimeHours = 0.0;
		double averageDownTime = 0.0;
		double averageDownTimeHours = 0.0;
		
		averageEnergyConsumption = totalEnergyConsumption/size;
		averageEnergyWastage = totalEnergyWastage/size;
		averageIdleEnergyConsumption = totalIdleEnergyConsumption/size;
		averageReexecutionTime = finalTotalReexecutionTime/size;
		averageReexecutionTimeHours = ((finalTotalReexecutionTime/3600)/size);
		averageDownTime = finalTotalDownTime/size;
		averageDownTimeHours = ((finalTotalDownTime/3600)/size);		
		
	Log.printLine();
	Log.printLine("==========================================================================================================================================================");
	Log.printLine("**********************************************************************************************************************************************************");
	Log.printLine("==========================================================================================================================================================");
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Energy Consumption ==========");
	Log.printLine(dft.format(totalEnergyConsumption));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Energy Wastage ==========");
	Log.printLine(dft.format(totalEnergyWastage));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Idle Energy Consumption ==========");
	Log.printLine(dft.format(totalIdleEnergyConsumption));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Application Utility Value ==========");
	Log.printLine(dft.format(jobUtilityValue));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Deadlines Achieved ==========");
	Log.printLine(countDeadlineAchieved);
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Deadlines Missed ==========");
	Log.printLine(countDeadlineMissed);
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Percentage Achieved Deadlines ==========");
	Log.printLine(dft.format(percentageAchievedDeadlines));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Maintainability ==========");
	Log.printLine(dft.format(totalMaintainability));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Availability ==========");
	Log.printLine(dft.format(totalAvailability));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Reliability ==========");
	Log.printLine(dft.format(totalReliabilityAverage*100));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Execution Time Changed Hours ==========");
	Log.printLine(dft.format(averageExecutionTimeChanged/3600));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Execution Time Changed  ==========");
	Log.printLine(dft.format(averageExecutionTimeChanged));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Makespan Hours ==========");
	Log.printLine(dft.format(averageCletCompletionTime/3600));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Makespan  ==========");
	Log.printLine(dft.format(averageCletCompletionTime));
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total VM Consolidations  ==========");
	Log.printLine(totalVmConsolidations);

	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Failures Occurred ==========");
	Log.printLine(totalFailureCount);
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Migration DownTime Hours ==========");
	Log.printLine(dft.format(totalMigrationDownTime/3600));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Migration DownTime ==========");
	Log.printLine(dft.format(totalMigrationDownTime));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Migration Overheads Hours ==========");
	Log.printLine(dft.format(totalMigrationOverhead/3600));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Migration Overheads ==========");
	Log.printLine(dft.format(totalMigrationOverhead));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Hosts ==========");
	Log.printLine(hostList.size());
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Reexecution Time Hours ==========");
	Log.printLine( dft.format(finalTotalReexecutionTime/3600));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Reexecution Time ==========");
	Log.printLine( dft.format(finalTotalReexecutionTime));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total DownTime Hours ==========");
	Log.printLine( dft.format(finalTotalDownTime/3600));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total DownTime ==========");
	Log.printLine( dft.format(finalTotalDownTime));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Application Finishing Time ==========");
	Log.printLine( dft.format(applicationFinishingTime/3600));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Energy Consumption ==========");
	Log.printLine( dft.format(averageEnergyConsumption));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Energy Wastage ==========");
	Log.printLine( dft.format(averageEnergyWastage));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Idle Energy Consumption ==========");
	Log.printLine( dft.format(averageIdleEnergyConsumption));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Reexecution Time ==========");
	Log.printLine( dft.format(averageReexecutionTime));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Reexecution Time Hours ==========");
	Log.printLine( dft.format(averageReexecutionTimeHours));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Down Time ==========");
	Log.printLine( dft.format(averageDownTime));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Down Time Hours ==========");
	Log.printLine( dft.format(averageDownTimeHours));
	Log.printLine();	
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
		//applicationFinishingTime = applicationFinishingTime/3600;
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
	double averageIdleEnergyConsumptionPerHost = 0.0;
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
		averageIdleEnergyConsumptionPerHost = idleEnergyConsumption/peListSize;
		totalIdleEnergyConsumption = totalIdleEnergyConsumption + idleEnergyConsumption; 
		totalAverageIdleEnergyConsumption = totalAverageIdleEnergyConsumption + averageIdleEnergyConsumptionPerHost;
		Log.printLine( indent+ hostIdIdle + indent + indent+ indent+ idleTime + indent + indent+ indent+ downTime + indent + indent+ indent+ idleEnergyConsumption+ indent + indent+ indent+ averageIdleEnergyConsumptionPerHost);
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
		double power = 0.0;
		double powerBackup = 0.0;
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
		double totalChange = 0.0;
		double difference = 0.0;
		double differenceEnergy = 0.0;
		
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
		powerBackup = power;
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
		//energyConsumptionwithoutChkpt = energyConsumptionwithoutReexecution - cletChkptEnergyConsumption;
		cletEnergyConsumptionWithoutReexecution.put(sufferedClets.get(i).getCloudletId(), energyConsumptionwithoutReexecution);		
		//cletEnergyConsumptionWithoutChkpt.put(sufferedClets.get(i).getCloudletId(), energyConsumptionwithoutChkpt);
	
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
		//	cletEnergyConsumptionWithMigrationDownTime.put(sufferedClets.get(i).getCloudletId(), actualCletEnergyConsumption);
			totalDownTime = downTime + migrationDownTime;
			totalMigrationDownTime = totalMigrationDownTime + migrationDownTime;
		}
		else{
			actualCletEnergyConsumption = energyConsumptionwithoutReexecution;
			actualCletEnergyConsumptionWithReexecution = cletEnergy;
			totalDownTime = downTime;
		}		
		
		finalTotalDownTime = finalTotalDownTime + totalDownTime;
		
		totalMigrationOverhead = totalMigrationOverhead + totalMigrationOverheads;		
		totalChange = totalDownTime + totalChkptOverhead + totalMigrationOverheads + reexecutionCletLength;
		difference = changeinExecutionTime - totalChange;
		
		if(difference > 0){
			reexecutionCletLength = reexecutionCletLength + difference;
			differenceEnergy = ((power/1000)*(difference/3600));
		//	cletEnergy = cletEnergy + differenceEnergy;
			actualCletEnergyConsumptionWithReexecution = actualCletEnergyConsumptionWithReexecution + differenceEnergy;
			cletReexecutionEnergy = cletReexecutionEnergy + differenceEnergy;
		}
		
		cletEnergyWastageWithReexecution.put(sufferedClets.get(i).getCloudletId(), cletReexecutionEnergy);
		//cletEnergyConsumption.put(sufferedClets.get(i).getCloudletId(), cletEnergy);
		
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
	
		double totalReliability;	
		double totalReliabilityParallel = 0.0;
		double totalReliabilityAverage = 0.0;
		double totalAvailability = 0.0;
		double totalMaintainability = 0.0;
		double percentageAchievedDeadlines;
		double averageCletCompletionTime;		
		double averageExecutionTimeChanged;	
		double totalEnergyConsumption = 0.0;		
		double totalEnergyConsumptionwithReexecution = 0.0;
		double totalEnergyWastage = 0.0;
		
		averageCletCompletionTime = totalMakeSpan/size;		
		averageExecutionTimeChanged = totalExecutionTimeChanged/size;		
				
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
		
					
		for(int i=0; i<newVmList.size();i++){
			//totalEnergyConsumption = totalEnergyConsumption + vmEnergyConsumptionTable.get(newVmList.get(i).getId());
			totalEnergyConsumption = totalEnergyConsumptionwithReexecution = totalEnergyConsumptionwithReexecution + vmEnergyConsumptionTablewithReexecution.get(newVmList.get(i).getId());
			totalEnergyWastage = totalEnergyWastage + vmEnergyWastageTable.get(newVmList.get(i).getId());
			
		}
		
		jobUtilityValue = jobUtilityValue/size;			
	
		percentageAchievedDeadlines = (((double)countDeadlineAchieved*100)/((double)countDeadlineAchieved+(double)countDeadlineMissed));
		
		double averageEnergyConsumption = 0.0;
		double averageEnergyWastage = 0.0;
		double averageIdleEnergyConsumption = 0.0;
		double averageReexecutionTime = 0.0;
		double averageReexecutionTimeHours = 0.0;
		double averageDownTime = 0.0;
		double averageDownTimeHours = 0.0;
		
		averageEnergyConsumption = totalEnergyConsumption/size;
		averageEnergyWastage = totalEnergyWastage/size;
		averageIdleEnergyConsumption = totalIdleEnergyConsumption/size;
		averageReexecutionTime = finalTotalReexecutionTime/size;
		averageReexecutionTimeHours = ((finalTotalReexecutionTime/3600)/size);
		averageDownTime = finalTotalDownTime/size;
		averageDownTimeHours = ((finalTotalDownTime/3600)/size);		
		
	Log.printLine("==========================================================================================================================================================");
	Log.printLine("**********************************************************************************************************************************************************");
	Log.printLine("==========================================================================================================================================================");
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Energy Consumption ==========");
	Log.printLine(dft.format(totalEnergyConsumption));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Energy Wastage ==========");
	Log.printLine(dft.format(totalEnergyWastage));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Idle Energy Consumption ==========");
	Log.printLine(dft.format(totalIdleEnergyConsumption));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Application Utility Value ==========");
	Log.printLine(dft.format(jobUtilityValue));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Deadlines Achieved ==========");
	Log.printLine(countDeadlineAchieved);
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Deadlines Missed ==========");
	Log.printLine(countDeadlineMissed);
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Percentage Achieved Deadlines ==========");
	Log.printLine(dft.format(percentageAchievedDeadlines));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Maintainability ==========");
	Log.printLine(dft.format(totalMaintainability));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Availability ==========");
	Log.printLine(dft.format(totalAvailability));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Reliability ==========");
	Log.printLine(dft.format(totalReliabilityAverage*100));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Execution Time Changed ==========");
	Log.printLine(dft.format(averageExecutionTimeChanged));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Execution Time Changed Hours ==========");
	Log.printLine(dft.format(averageExecutionTimeChanged/3600));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Finished Time ==========");
	Log.printLine(dft.format(averageCletCompletionTime));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Finished Time Hours ==========");
	Log.printLine(dft.format(averageCletCompletionTime/3600));
			
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total VM Consolidations ==========");
	Log.printLine(totalVmConsolidations);
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Checkpoints ==========");
	Log.printLine(totalCheckpointsCount);
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Failures Occurred ==========");
	Log.printLine(totalFailureCount);
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Checkpoint Overheads ==========");
	Log.printLine(dft.format(finalChkptOverhead));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Checkpoint Overheads Hours ==========");
	Log.printLine(dft.format(finalChkptOverhead/3600));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Migration DownTime ==========");
	Log.printLine(dft.format(totalMigrationDownTime));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Migration DownTime Hours ==========");
	Log.printLine(dft.format(totalMigrationDownTime/3600));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Migration Overheads ==========");
	Log.printLine(dft.format(totalMigrationOverhead));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Migration Overheads Hours ==========");
	Log.printLine(dft.format(totalMigrationOverhead/3600));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Hosts ==========");
	Log.printLine(hostList.size());
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Reexecution Time Hours ==========");
	Log.printLine(dft.format(finalTotalReexecutionTime/3600));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Reexecution Time  ==========");
	Log.printLine(dft.format(finalTotalReexecutionTime));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total DownTime Hours  ==========");
	Log.printLine(dft.format(finalTotalDownTime/3600));	
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total DownTime  ==========");
	Log.printLine(dft.format(finalTotalDownTime));	
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Application Finishing Time ==========");
	Log.printLine(dft.format(applicationFinishingTime/3600));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Energy Consumption ==========");
	Log.printLine( dft.format(averageEnergyConsumption));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Energy Wastage ==========");
	Log.printLine( dft.format(averageEnergyWastage));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Idle Energy Consumption ==========");
	Log.printLine( dft.format(averageIdleEnergyConsumption));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Reexecution Time ==========");
	Log.printLine( dft.format(averageReexecutionTime));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Reexecution Time Hours ==========");
	Log.printLine( dft.format(averageReexecutionTimeHours));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Down Time ==========");
	Log.printLine( dft.format(averageDownTime));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Down Time Hours ==========");
	Log.printLine( dft.format(averageDownTimeHours));
	Log.printLine();
	
}

private static void printCloudletListCheckpointingwithConsolidation(List<Cloudlet> list, List<Host> hostList, FTAFileReader ftaread, FailureDatacenterBroker broker, HostVMMappingReliability hvmRbl, List<Vm>newVmList, VmUtilization vmUtl, PowerModel pow){
	
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
	double averageIdleEnergyConsumptionPerHost = 0.0;
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
		averageIdleEnergyConsumptionPerHost = idleEnergyConsumption/peListSize;
		totalIdleEnergyConsumption = totalIdleEnergyConsumption + idleEnergyConsumption; 
		totalAverageIdleEnergyConsumption = totalAverageIdleEnergyConsumption + averageIdleEnergyConsumptionPerHost;
		Log.printLine( indent+ hostIdIdle + indent + indent+ indent+ idleTime + indent + indent+ indent+ downTime + indent + indent+ indent+ idleEnergyConsumption+ indent + indent+ indent+ averageIdleEnergyConsumptionPerHost);
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
		double power = 0.0;
		double powerBackup = 0.0;
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
		double totalChange = 0.0;
		double difference = 0.0;
		double differenceEnergy = 0.0;
		
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
		powerBackup = power;
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
		//energyConsumptionwithoutChkpt = energyConsumptionwithoutReexecution - cletChkptEnergyConsumption;
		cletEnergyConsumptionWithoutReexecution.put(sufferedClets.get(i).getCloudletId(), energyConsumptionwithoutReexecution);		
		//cletEnergyConsumptionWithoutChkpt.put(sufferedClets.get(i).getCloudletId(), energyConsumptionwithoutChkpt);
	
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
		//	cletEnergyConsumptionWithMigrationDownTime.put(sufferedClets.get(i).getCloudletId(), actualCletEnergyConsumption);
			totalDownTime = downTime + migrationDownTime;
			totalMigrationDownTime = totalMigrationDownTime + migrationDownTime;
		}
		else{
			actualCletEnergyConsumption = energyConsumptionwithoutReexecution;
			actualCletEnergyConsumptionWithReexecution = cletEnergy;
			totalDownTime = downTime;
		}		
		
		finalTotalDownTime = finalTotalDownTime + totalDownTime;
		
		totalMigrationOverhead = totalMigrationOverhead + totalMigrationOverheads;		
		totalChange = totalDownTime + totalChkptOverhead + totalMigrationOverheads + reexecutionCletLength;
		difference = changeinExecutionTime - totalChange;
		
		if(difference > 0){
			reexecutionCletLength = reexecutionCletLength + difference;
			differenceEnergy = ((power/1000)*(difference/3600));
		//	cletEnergy = cletEnergy + differenceEnergy;
			actualCletEnergyConsumptionWithReexecution = actualCletEnergyConsumptionWithReexecution + differenceEnergy;
			cletReexecutionEnergy = cletReexecutionEnergy + differenceEnergy;
		}
		
		cletEnergyWastageWithReexecution.put(sufferedClets.get(i).getCloudletId(), cletReexecutionEnergy);
		//cletEnergyConsumption.put(sufferedClets.get(i).getCloudletId(), cletEnergy);
		
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
	
		double totalReliability;	
		double totalReliabilityParallel = 0.0;
		double totalReliabilityAverage = 0.0;
		double totalAvailability = 0.0;
		double totalMaintainability = 0.0;
		double percentageAchievedDeadlines;
		double averageCletCompletionTime;		
		double averageExecutionTimeChanged;	
		double totalEnergyConsumption = 0.0;		
		double totalEnergyConsumptionwithReexecution = 0.0;
		double totalEnergyWastage = 0.0;
		
		averageCletCompletionTime = totalMakeSpan/size;		
		averageExecutionTimeChanged = totalExecutionTimeChanged/size;		
				
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
		
					
		for(int i=0; i<newVmList.size();i++){
			//totalEnergyConsumption = totalEnergyConsumption + vmEnergyConsumptionTable.get(newVmList.get(i).getId());
			totalEnergyConsumption = totalEnergyConsumptionwithReexecution = totalEnergyConsumptionwithReexecution + vmEnergyConsumptionTablewithReexecution.get(newVmList.get(i).getId());
			totalEnergyWastage = totalEnergyWastage + vmEnergyWastageTable.get(newVmList.get(i).getId());
			
		}
		
		jobUtilityValue = jobUtilityValue/size;			
	
		percentageAchievedDeadlines = (((double)countDeadlineAchieved*100)/((double)countDeadlineAchieved+(double)countDeadlineMissed));
		
		double averageEnergyConsumption = 0.0;
		double averageEnergyWastage = 0.0;
		double averageIdleEnergyConsumption = 0.0;
		double averageReexecutionTime = 0.0;
		double averageReexecutionTimeHours = 0.0;
		double averageDownTime = 0.0;
		double averageDownTimeHours = 0.0;
		
		averageEnergyConsumption = totalEnergyConsumption/size;
		averageEnergyWastage = totalEnergyWastage/size;
		averageIdleEnergyConsumption = totalIdleEnergyConsumption/size;
		averageReexecutionTime = finalTotalReexecutionTime/size;
		averageReexecutionTimeHours = ((finalTotalReexecutionTime/3600)/size);
		averageDownTime = finalTotalDownTime/size;
		averageDownTimeHours = ((finalTotalDownTime/3600)/size);		
		
	Log.printLine("==========================================================================================================================================================");
	Log.printLine("**********************************************************************************************************************************************************");
	Log.printLine("==========================================================================================================================================================");
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Energy Consumption ==========");
	Log.printLine(dft.format(totalEnergyConsumption));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Energy Wastage ==========");
	Log.printLine(dft.format(totalEnergyWastage));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Idle Energy Consumption ==========");
	Log.printLine(dft.format(totalIdleEnergyConsumption));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Application Utility Value ==========");
	Log.printLine(dft.format(jobUtilityValue));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Deadlines Achieved ==========");
	Log.printLine(countDeadlineAchieved);
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Deadlines Missed ==========");
	Log.printLine(countDeadlineMissed);
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Percentage Achieved Deadlines ==========");
	Log.printLine(dft.format(percentageAchievedDeadlines));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Maintainability ==========");
	Log.printLine(dft.format(totalMaintainability));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Availability ==========");
	Log.printLine(dft.format(totalAvailability));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Reliability ==========");
	Log.printLine(dft.format(totalReliabilityAverage*100));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Execution Time Changed ==========");
	Log.printLine(dft.format(averageExecutionTimeChanged));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Execution Time Changed Hours ==========");
	Log.printLine(dft.format(averageExecutionTimeChanged/3600));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Finished Time ==========");
	Log.printLine(dft.format(averageCletCompletionTime));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Finished Time Hours ==========");
	Log.printLine(dft.format(averageCletCompletionTime/3600));
			
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total VM Consolidations ==========");
	Log.printLine(totalVmConsolidations);
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Checkpoints ==========");
	Log.printLine(totalCheckpointsCount);
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Failures Occurred ==========");
	Log.printLine(totalFailureCount);
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Checkpoint Overheads ==========");
	Log.printLine(dft.format(finalChkptOverhead));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Checkpoint Overheads Hours ==========");
	Log.printLine(dft.format(finalChkptOverhead/3600));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Migration DownTime ==========");
	Log.printLine(dft.format(totalMigrationDownTime));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Migration DownTime Hours ==========");
	Log.printLine(dft.format(totalMigrationDownTime/3600));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Migration Overheads ==========");
	Log.printLine(dft.format(totalMigrationOverhead));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Migration Overheads Hours ==========");
	Log.printLine(dft.format(totalMigrationOverhead/3600));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Hosts ==========");
	Log.printLine(hostList.size());
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Reexecution Time Hours ==========");
	Log.printLine(dft.format(finalTotalReexecutionTime/3600));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total Reexecution Time  ==========");
	Log.printLine(dft.format(finalTotalReexecutionTime));
		
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total DownTime Hours  ==========");
	Log.printLine(dft.format(finalTotalDownTime/3600));	
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Total DownTime  ==========");
	Log.printLine(dft.format(finalTotalDownTime));	
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Application Finishing Time ==========");
	Log.printLine(dft.format(applicationFinishingTime/3600));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Energy Consumption ==========");
	Log.printLine( dft.format(averageEnergyConsumption));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Energy Wastage ==========");
	Log.printLine( dft.format(averageEnergyWastage));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Idle Energy Consumption ==========");
	Log.printLine( dft.format(averageIdleEnergyConsumption));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Reexecution Time ==========");
	Log.printLine( dft.format(averageReexecutionTime));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Reexecution Time Hours ==========");
	Log.printLine( dft.format(averageReexecutionTimeHours));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Down Time ==========");
	Log.printLine( dft.format(averageDownTime));
	Log.printLine();
	
	Log.printLine();
	Log.printLine();
	Log.printLine("========== Average Down Time Hours ==========");
	Log.printLine( dft.format(averageDownTimeHours));
	Log.printLine();
	
}


}
	
	

//}
