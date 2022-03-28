package org.cloudbus.cloudsim.failure;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class RunTimeConstants {
	public static double stringencyFactor;
	public static double sensitivityFactor;
	public static int host_order;	
	public static int cloudlet_order;
	public static int faultToleranceMechanism;
	public static double dataRate;
	public static int numberofBags;
	public static boolean test;
	public static boolean firstCycleFlag = true;
	public static boolean vmMigrationFlag = false;
	public static int runCount;
	public static double checkpointOverhead;
	public static int instructionsOverhead;
	public static double smoothingConstant;
	public static int failurePredictionMethod;
	public static int windowSize;
	public static boolean predictionFlag;
	public static boolean vmConsolidationFlag;
	public static boolean resourceTurnOffCase;
	public static boolean syntheticPrediction;
	public static boolean generatePredictionFlagValues;	
	public static int predictionAccuracy;
	public static int ratioPrecedingtoLaterFailures;
	public static int percentagePrecedingPrediction;
	public static int numberofClusters;
	public static boolean considerLaterPrediction;
	public static boolean failureCorrelation;
	public static boolean hierarchicalClustering;
	public static boolean nonhierarchicalClustering;
	public static double scaleParameter;
	public static double shapeParameter;
	public static double scaleParameterBagSize;
	public static double shapeParameterBagSize;
	public static double taskRunTimeMean;
	public static double taskRunTimeDeviation;
	public static boolean failureCorrelationConsolidation;
	public static boolean failureCorrelationWithRstrMig;
	public static boolean failureCorrelationWithRstrChkpt;
	public static boolean testingBagSize;
	public static String traceType;
	public static boolean failureInjection;
	public static double energyCost;
	public static double carbonDioxideEmission;
	public static ArrayList<Double>randomEndTime = new ArrayList<Double>();
	public static ArrayList<Long>cloudletLength = new ArrayList<Long>();
	public static ArrayList<Integer>bagSize = new ArrayList<Integer>();
	public static HashMap<Integer, Double>currentUtilization = new HashMap<Integer, Double>();
	
	public static int numberofFrequencyLevels;
	public static boolean workingWithFrequencyScaling;

	
	public static void setRunTimeConstants(){
		BufferedReader read;
		//BufferedReader read1;
		int count=0;
		try {
			String line;
			read = new BufferedReader(new FileReader("/Users/yogeshsharma/Documents/Data/Publications/runTimeConstants/ConfigurationFile.txt"));
		//	read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/Sensitivity_Factor.txt"));
		//	read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/Sensitivity_Factor.txt"));
		//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/Sensitivity_Factor.txt"));
		//	sensitivityFactor = Double.parseDouble(read.readLine());
			
			while((line=read.readLine())!=null)
			{
				if(count == 0) {
					sensitivityFactor = Double.parseDouble(line);					
				}
				if(count == 1) {
					stringencyFactor = Double.parseDouble(line);					
				}
				if(count == 2) {				
					cloudlet_order = Integer.parseInt(line);
				}
				if(count == 3) {
					host_order = Integer.parseInt(line);					
				}
				if(count == 4) {
					/**
					 * 1.	With Checkpointing
					 * 2.	Without Checkpointing 
					 * 3.	VM Migration
					 * 4. 	VM Migration with Checkpointing
					 */
					faultToleranceMechanism = Integer.parseInt(line);					
				}
			//	if(count == 5) {
			//		dataRate = Double.parseDouble(line);
			//		dataRate = dataRate * 100;
			//	}
				if(count == 5) {					
				//	if(numberofBags == 0){
				//		firstCycleFlag = false;
				//	}
					numberofBags = Integer.parseInt(line);
				}
				if(count == 6) {
					test = Boolean.parseBoolean(line);
				}
				if(count == 7) {
					checkpointOverhead = Double.parseDouble(line);
				}
				if(count == 8) {
					instructionsOverhead = Integer.parseInt(line);
				}		
				if(count == 9) {
					smoothingConstant = Double.parseDouble(line);	
				}
				if(count == 10) {
					windowSize = Integer.parseInt(line);	
				}
				if(count == 11) {
					predictionFlag = Boolean.parseBoolean(line);
				}
				if(count == 12) {
					failurePredictionMethod = Integer.parseInt(line);
				}
				if(count == 13) {
					vmConsolidationFlag = Boolean.parseBoolean(line);
				}
				if(count == 14) {
					resourceTurnOffCase = Boolean.parseBoolean(line);
				}
				if(count == 15) {
					syntheticPrediction = Boolean.parseBoolean(line);
				}
				if(count == 16) {
					generatePredictionFlagValues = Boolean.parseBoolean(line);
				}
				if(count == 17) {
					predictionAccuracy = Integer.parseInt(line);
				}
				if(count == 18) {
					ratioPrecedingtoLaterFailures = Integer.parseInt(line);
				}
				if(count == 19) {
					considerLaterPrediction = Boolean.parseBoolean(line);
				}
				if(count == 20) {
					numberofClusters = Integer.parseInt(line);
				}
				if(count == 21) {
					failureCorrelation = Boolean.parseBoolean(line);	
				}
				if(count == 22) {
					nonhierarchicalClustering = Boolean.parseBoolean(line);
				}
				if(count == 23) {
					hierarchicalClustering = Boolean.parseBoolean(line);
				}
				if(count == 24) {
					traceType = line;
				}
				if(count == 25) {
					scaleParameter = Double.parseDouble(line);
				}
				if(count == 26) {
					shapeParameter = Double.parseDouble(line);
				}
				if(count == 27) {
					failureCorrelationConsolidation = Boolean.parseBoolean(line);
				}
				if(count == 28) {
					failureCorrelationWithRstrMig = Boolean.parseBoolean(line);
				}
				if(count == 29) {
					failureCorrelationWithRstrChkpt = Boolean.parseBoolean(line);
				}
				if(count == 30) {
					failureInjection = Boolean.parseBoolean(line);
				}
				if(count == 31) {
					energyCost = Double.parseDouble(line);
				}
				if(count == 32) {
					carbonDioxideEmission = Double.parseDouble(line);
				}			
				if(count == 33) {
					scaleParameterBagSize = Double.parseDouble(line);
				}
				if(count == 34) {
					shapeParameterBagSize = Double.parseDouble(line);
				}
				if(count == 35) {
					numberofFrequencyLevels = Integer.parseInt(line);
				}
				if(count == 36) {
					workingWithFrequencyScaling = Boolean.parseBoolean(line);
				}
				count++;
			}
			
			read = new BufferedReader(new FileReader("/Users/yogeshsharma/Documents/Data/Publications/runTimeConstants/Cloudlet_length.txt"));
			for(int i=0;i<500000;i++){				
				cloudletLength.add(Long.parseLong(read.readLine()));			
				}					
			
	//		read = new BufferedReader(new FileReader("/Users/yogeshsharma/Documents/Data/Publications/runTimeConstants/BagSize.txt"));
	//		for(int i=0;i<3000;i++){
	//			bagSize.add(Integer.parseInt(read.readLine()));
	//			}
			
			read = new BufferedReader(new FileReader("/Users/yogeshsharma/Documents/Data/Publications/runTimeConstants/BagSize1.txt"));
			for(int i=0;i<4;i++){
				bagSize.add(Integer.parseInt(read.readLine()));
				}
			
			
/*			
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/Stringency_Factor.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/Stringency_Factor.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/Stringency_Factor.txt"));
			stringencyFactor = Double.parseDouble(read.readLine());
			
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/Cloudlet_Ordering.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/Cloudlet_Ordering.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/Cloudlet_Ordering.txt"));
			cloudlet_order = Integer.parseInt(read.readLine());
			
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/Host_Ordering.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/Host_Ordering.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/Host_Ordering.txt"));
			host_order = Integer.parseInt(read.readLine());
*/			
			/**
			 * 1.	With Checkpointing
			 * 2.	Without Checkpointing 
			 * 3.	VM Migration
			 * 4. 	VM Migration with Checkpointing
			 */
/*
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/Fault_Tolerance_Mechanism.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/Fault_Tolerance_Mechanism.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/Fault_Tolerance_Mechanism.txt"));
			faultToleranceMechanism = Integer.parseInt(read.readLine());
			
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/DataRate.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/DataRate.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/DataRate.txt"));
			dataRate = Double.parseDouble(read.readLine());
			dataRate = dataRate * 100;
			
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/Number_of_Bags.txt"));
			//	read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/Number_of_Bags1.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/Number_of_Bags.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/Number_of_Bags.txt"));
			numberofBags = Integer.parseInt(read.readLine());
			if(numberofBags == 0){
				firstCycleFlag = false;
			}
			
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/Test_or_Experiment.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/Test_or_Experiment.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/Test_or_Experiment.txt"));
			test = Boolean.parseBoolean(read.readLine());			
			
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/CheckPoint_Overhead.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/CheckPoint_Overhead.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/CheckPoint_Overhead.txt"));
			checkpointOverhead = Double.parseDouble(read.readLine());
			
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/Instructions_Overhead.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/Instructions_Overhead.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/Instructions_Overhead.txt"));
			instructionsOverhead = Integer.parseInt(read.readLine());			
			
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/TestingBagSize.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/TestingBagSize.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/TestingBagSize.txt"));
			testingBagSize = Boolean.parseBoolean(read.readLine());
			
			if(testingBagSize == true){
				//read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/Cloudlet_length.txt"));
					read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/Cloudlet_length1.txt"));
				//read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/Cloudlet_length.txt"));
				//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/Cloudlet_length.txt"));
				//for(int i=0;i<500000;i++){
					for(int i=0;i<24;i++){	
					cloudletLength.add(Long.parseLong(read.readLine()));
				}				
			}
			else{
				read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/Cloudlet_length_for_Bag_Size_Test.txt"));				
				//	read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/Cloudlet_length_for_Bag_Size_Test.txt"));
					//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/Cloudlet_length_for_Bag_Size_Test.txt"));
					for(int i=0;i<1000000;i++){				
						cloudletLength.add(Long.parseLong(read.readLine()));
					}				
			}
			
		//	read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/BagSize.txt"));
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/BagSize1.txt"));
		//	read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/BagSize.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/BagSize.txt"));
		//	for(int i=0;i<3000;i++){
			for(int i=0;i<6;i++){
				bagSize.add(Integer.parseInt(read.readLine()));
			}
			
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/Random_EndTime.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/Random_EndTime.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/Random_EndTime.txt"));
			for(int i=0;i<3000;i++){
				randomEndTime.add(Double.parseDouble(read.readLine()));
			}
			
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/Smoothing_Constant.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/Smoothing_Constant.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/Smoothing_Constant.txt"));
			smoothingConstant = Double.parseDouble(read.readLine());	
			
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/Window_Size.txt"));
			//read = new BufferedReader(new FileReader("/ho me/ec2-user/Results_for_Migration_and_Consolidation/Window_Size.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/Window_Size.txt"));
			windowSize = Integer.parseInt(read.readLine());	
			
			//read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results/VM_Migration_Flag.txt"));
			//vmMigrationFlag = Boolean.parseBoolean(read.readLine());
			
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/Prediction_Flag.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/Prediction_Flag.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/Prediction_Flag.txt"));
			predictionFlag = Boolean.parseBoolean(read.readLine());
*/			
			/**
			 * 1.	Exponential Smoothing
			 * 2.	Moving Average
			 */
/*
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/FailurePredictionMethod.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/FailurePredictionMethod.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/FailurePredictionMethod.txt"));
			failurePredictionMethod = Integer.parseInt(read.readLine());
			
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/VmConsolidationFlag.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/VmConsolidationFlag.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/VmConsolidationFlag.txt"));
			vmConsolidationFlag = Boolean.parseBoolean(read.readLine());
			
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/ResourceTurnOffCase.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/ResourceTurnOffCase.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/ResourceTurnOffCase.txt"));
			resourceTurnOffCase = Boolean.parseBoolean(read.readLine());
			
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/SyntheticPrediction.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/SyntheticPrediction.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/SyntheticPrediction.txt"));
			syntheticPrediction = Boolean.parseBoolean(read.readLine());
			
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/PredictionFlagValues.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/PredictionFlagValues.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/PredictionFlagValues.txt"));
			generatePredictionFlagValues = Boolean.parseBoolean(read.readLine());
			
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/PredictionAccuracy.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/PredictionAccuracy.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/PredictionAccuracy.txt"));
			predictionAccuracy = Integer.parseInt(read.readLine());
			
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/RatioPrecedingtoLaterFailures.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/RatioPrecedingtoLaterFailures.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/RatioPrecedingtoLaterFailures.txt"));
			ratioPrecedingtoLaterFailures = Integer.parseInt(read.readLine());
			
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/ConsiderLaterPrediction.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/ConsiderLaterPrediction.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/ConsiderLaterPrediction.txt"));
			considerLaterPrediction = Boolean.parseBoolean(read.readLine());
			
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/NumberofClusters.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/NumberofClusters.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/NumberofClusters.txt"));
			numberofClusters = Integer.parseInt(read.readLine());		
			
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/FailureCorrelation.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/FailureCorrelation.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/FailureCorrelation.txt"));
			failureCorrelation = Boolean.parseBoolean(read.readLine());		
			
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/NonHierarchicalClustering.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/NonHierarchicalClustering.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/NonHierarchicalClustering.txt"));
			nonhierarchicalClustering = Boolean.parseBoolean(read.readLine());		
			
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/HierarchicalClustering.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/HierarchicalClustering.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/HierarchicalClustering.txt"));
			hierarchicalClustering = Boolean.parseBoolean(read.readLine());			
			
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/TraceType.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/TraceType.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/TraceType.txt"));
			traceType = read.readLine();			
			
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/ScaleParameter.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/ScaleParameter.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/ScaleParameter.txt"));
			scaleParameter = Double.parseDouble(read.readLine());
			
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/ShapeParameter.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/ShapeParameter.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/ShapeParameter.txt"));
			shapeParameter = Double.parseDouble(read.readLine());	
			
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/ScaleParameterBagSize.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/ScaleParameterBagSize.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/ScaleParameterBagSize.txt"));
			scaleParameterBagSize = Double.parseDouble(read.readLine());
					
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/ShapeParameterBagSize.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/ShapeParameterBagSize.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/ShapeParameterBagSize.txt"));
			shapeParameterBagSize = Double.parseDouble(read.readLine());	
			
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/TaskRunTimeMean.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/TaskRunTimeMean.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/TaskRunTimeMean.txt"));
			taskRunTimeMean = Double.parseDouble(read.readLine());	
			
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/TaskRunTimeDeviation.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/TaskRunTimeDeviation.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/TaskRunTimeDeviation.txt"));
			taskRunTimeDeviation = Double.parseDouble(read.readLine());	
			
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/FailureCorrelationConsolidation.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/FailureCorrelationConsolidation.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/FailureCorrelationConsolidation.txt"));
			failureCorrelationConsolidation = Boolean.parseBoolean(read.readLine());	
			
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/FailureCorrelationWithRstrMig.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/FailureCorrelationWithRstrMig.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/FailureCorrelationWithRstrMig.txt"));
			failureCorrelationWithRstrMig = Boolean.parseBoolean(read.readLine());
			
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/FailureCorrelationWithRstrChkpt.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/FailureCorrelationWithRstrChkpt.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/FailureCorrelationWithRstrChkpt.txt"));
			failureCorrelationWithRstrChkpt = Boolean.parseBoolean(read.readLine());	
			
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/FailureInjection.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/FailureInjection.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/FailureInjection.txt"));
			failureInjection = Boolean.parseBoolean(read.readLine());	
			
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/EnergyCost.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/EnergyCost.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/EnergyCost.txt"));
			energyCost = Double.parseDouble(read.readLine());	
			
			read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/CarbonDioxideEmission.txt"));
			//read = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/CarbonDioxideEmission.txt"));
			//	read = new BufferedReader(new FileReader("/home/ec2-user/Simulation_Configuration_Files/CarbonDioxideEmission.txt"));
			carbonDioxideEmission = Double.parseDouble(read.readLine());				
			
		//	read = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results_for_Migration_and_Consolidation/PercentagePrecedingPrediciton.txt"));
		//	percentagePrecedingPrediction = Integer.parseInt(read.readLine());
*/	
		} catch (NumberFormatException | IOException e) {
			System.out.println("Error occurred while setting the values of RunTimeConstants");
		}
		
	}
	

}
