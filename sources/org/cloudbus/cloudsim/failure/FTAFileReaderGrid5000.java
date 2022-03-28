package org.cloudbus.cloudsim.failure;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class FTAFileReaderGrid5000 {
	private ArrayList<Integer>eventID = new ArrayList<Integer>();	
	private ArrayList<Integer>componentID = new ArrayList<Integer>();
	protected ArrayList<Integer>nodeID = new ArrayList<Integer>();	
	private ArrayList<Integer>platformID = new ArrayList<Integer>();
	private ArrayList<String>nodeName = new ArrayList<String>();
	private ArrayList<Integer>eventType = new ArrayList<Integer>();
	private ArrayList<Long>eventStartTime = new ArrayList<Long>();
	private ArrayList<Long>eventStopTime = new ArrayList<Long>();
	private ArrayList<String>eventEndReason = new ArrayList<String>();
	private ArrayList<String>nodeLocation = new ArrayList<String>();
	public ArrayList<Integer>num_procs = new ArrayList<Integer>();
	public ArrayList<Integer>num_procs_const = new ArrayList<Integer>();
	public LinkedHashSet<Integer>nodeIDSet = new LinkedHashSet<Integer>();
	public ArrayList<Integer>nodeIDList = new ArrayList<Integer>();
	public ArrayList<Integer>NodeIDListSorted = new ArrayList<Integer>();
	public HashMap<Integer, Integer>mappedhostIDTable = new HashMap<Integer, Integer>();
	public ArrayList<Double>eventStartTimeHours = new ArrayList<Double>();
	public ArrayList<Double>eventStopTimeHours = new ArrayList<Double>();
	public ArrayList<Long>eventStartTimewithOffset = new ArrayList<Long>();
	public ArrayList<Long>eventStopTimewithOffset = new ArrayList<Long>();
	public ArrayList<Double>differenceEvents = new ArrayList<Double>();
	public ArrayList<Double>differenceTemp = new ArrayList<Double>();
	public ArrayList<Double>MTBF = new ArrayList<Double>();
	public ArrayList<Double>MTTR = new ArrayList<Double>();
	public ArrayList<Double>HazardRate = new ArrayList<Double>();
	public ArrayList<Double>currentUtilization = new ArrayList<Double>();
	public ArrayList<Double>currentHazardRate = new ArrayList<Double>();
	public ArrayList<Double>powerList = new ArrayList<Double>();	
	public HashMap<Integer, ArrayList<Double>>hostTBFMap = new HashMap<Integer, ArrayList<Double>>();	
	public HashMap<Integer, ArrayList<Double>>hostTTRMap = new HashMap<Integer, ArrayList<Double>>();	
	public ArrayList<Integer>nodeIDListSortedPower = new ArrayList<Integer>();
	public ArrayList<Double>productPowerandHazardRateList = new ArrayList<Double>();
	public ArrayList<Integer>nodeIDListSortedPowerandHazardRate = new ArrayList<Integer>();
	public ArrayList<Double>Maintainability = new ArrayList<Double>();
	public ArrayList<Integer>nodeIDMaintainabilityListSorted = new ArrayList<Integer>();
	public ArrayList<Double>productPowerandMaintainabilityList = new ArrayList<Double>();
	public ArrayList<Integer>nodeIDSortedPowerandMaintainability = new ArrayList<Integer>();
	public ArrayList<Double>Availability = new ArrayList<Double>();
	public ArrayList<Integer>nodeIDAvailabilityListSorted = new ArrayList<Integer>();
	public ArrayList<Double>fractionPowerandAvailabilityList = new ArrayList<Double>();
	public ArrayList<Integer>nodeIDSortedPowerandAvailability = new ArrayList<Integer>();
	public ArrayList<Double>startTimeTemp = new ArrayList<Double>();
	public ArrayList<Double>stopTimeTemp = new ArrayList<Double>();
	public ArrayList<Double>startTimeAvailTemp = new ArrayList<Double>();
	public ArrayList<Double>stopTimeAvailTemp = new ArrayList<Double>();
	public ArrayList<Integer>predictionFailureFlag = new ArrayList<Integer>();
	public HashMap<Integer, ArrayList<Integer>>hostPredictionFlagMap = new HashMap<Integer, ArrayList<Integer>>();
	public ArrayList<String>locationListNonAmbiguous = new ArrayList<String>();
	public HashMap<String, Integer>clusterConstant = new HashMap<String, Integer>();
	public HashMap<String, ArrayList<Integer>>clusterNodeMap = new HashMap<String, ArrayList<Integer>>();
	public HashMap<String, ArrayList<Long>>clusterTimeMap = new HashMap<String, ArrayList<Long>>();
	public HashMap<String, ArrayList<Long>>clusterTimeMapNonAmbiguous = new HashMap<String, ArrayList<Long>>();
	public HashMap<String, ArrayList<ArrayList<Integer>>>adjacencyMatrixMap = new HashMap<String, ArrayList<ArrayList<Integer>>>();
	public int correlatedMigrationCounter = 0;

	Integer[] nodeIDList_sorted;
	int offsetValue = 1000000;
	
	public K_MeansAlgorithm kmeans = new K_MeansAlgorithm();
	
	public FTAFileReaderGrid5000(){		
		try {
			BufferedReader buffer;		
			BufferedReader buffer2;
			BufferedReader buffer3;			
		//	buffer = new BufferedReader(new FileReader("/rusers/postgrad/ysharma1/PhDWork/Java/event_trace_Grid5000.tab"));
		//	buffer2 = new BufferedReader(new FileReader("/rusers/postgrad/ysharma1/PhDWork/Java/node_Grid5000.tab"));
		//	buffer3 = new BufferedReader(new FileReader("/rusers/postgrad/ysharma1/PhDWork/Java/core_count.txt"));
			
		//	buffer = new BufferedReader(new FileReader("/home/ec2-user/Traces/event_trace_Grid5000.tab"));
		//	buffer2 = new BufferedReader(new FileReader("/home/ec2-user/Traces/node_Grid5000.tab"));
		//	buffer3 = new BufferedReader(new FileReader("/home/ec2-user/Traces/core_count.txt"));			
		
			buffer = new BufferedReader(new FileReader("/Users/yogeshsharma/Documents/Data/FTATraces/g5k06_tab/g5k06_tab/event_trace.tab"));
			buffer2 = new BufferedReader(new FileReader("/Users/yogeshsharma/Documents/Data/FTATraces/g5k06_tab/g5k06_tab/node.tab"));
			buffer3 = new BufferedReader(new FileReader("/Users/yogeshsharma/Documents/Data/FTATraces/core_count.txt"));
			
			
			buffer.readLine(); //Skips the first line of the file.	
			buffer2.readLine();
			buffer3.readLine();
			String currentstr;
			while((currentstr=buffer.readLine()) != null){					
				String[] column = currentstr.trim().split("\\s+");			
				if(column.length>0){						
					eventID.add(Integer.parseInt(column[0]));
					componentID.add(Integer.parseInt(column[1]));
					nodeID.add(Integer.parseInt(column[2]));
					platformID.add(Integer.parseInt(column[3]));
					nodeName.add(column[4]);
					eventType.add(Integer.parseInt(column[5]));
					eventStartTime.add(Long.parseLong(column[6]));
					eventStopTime.add(Long.parseLong(column[7]));
					eventEndReason.add(column[8]);					
				}								
			}
			buffer.close(); //Closing the buffer to optimize the memory usage. 			
			
			String currentstr2;
			while((currentstr2=buffer2.readLine()) != null){				
				String[] column1 = currentstr2.trim().split("\\s+");				
				if(column1.length>0){					
					num_procs.add(Integer.parseInt(column1[9]));			
					nodeLocation.add(column1[4]);
				}				
			}
			buffer2.close();			
			
			String currentstr3;
			while((currentstr3=buffer3.readLine()) != null){				
				String[] column3 = currentstr3.trim().split("\\s+");				
				if(column3.length>0){					
					num_procs_const.add(Integer.parseInt(column3[0]));					
				}				
			}
			buffer3.close();
			
		}
		 catch (IOException e) {
			e.printStackTrace();
			System.out.println("The simulation has been terminated due to an IO error");		
		 }	
		removenodeIdAmbiguity();
}

public void setK_MeansAlgorithm(K_MeansAlgorithm kmeans){
	this.kmeans = kmeans;
}	
	
public void removenodeIdAmbiguity(){	
	for(int i=0;i<nodeID.size();i++){
		nodeIDSet.add(nodeID.get(i));
	}		
	nodeIDList.addAll(nodeIDSet);
	removeLocationListAmbiguity();
}

/**
 * This function prepares the non-ambiguous list of clusters present in the traces. 
 */
public void removeLocationListAmbiguity(){
	String currentLocationValue = null;
	for(int i=0; i<nodeLocation.size();i++){
		if(locationListNonAmbiguous.isEmpty()){
			locationListNonAmbiguous.add(nodeLocation.get(i));
			currentLocationValue = nodeLocation.get(i);
		}
		else{
			if(!(nodeLocation.get(i).equals(currentLocationValue))){
				locationListNonAmbiguous.add(nodeLocation.get(i));
				currentLocationValue = nodeLocation.get(i);
			}
		}
	}
	offSetProcessing();	
}

/**
 * This part of the program applies offset to the event start and stop time. This process is required to make the occurrence 
 * of failures possible during the execution of tasks in cloud computing environment. If this process will not be applied then
 * the task length needs to be kept unrealistically long. 
 */

public void offSetProcessing(){
	ArrayList<Long>firstStartEventPerNode = new ArrayList<Long>();
	ArrayList<Long>differencePerEvent = new ArrayList<Long>();
	ArrayList<Long>startEventWithOffsetPerNode = new ArrayList<Long>();
	ArrayList<Long>offsetStartTimeTemp;
	ArrayList<Long>offsetStopTimeTemp;
	//ArrayList<Long>previousStartList;
	//ArrayList<Long>previousStopList;
	long difference = 0;
	int count = 0;
	long startEventWithOffset = 0;
	for(int i=0; i<nodeIDList.size(); i++){
		for(int j=0; j<nodeID.size(); j++){
			if(nodeIDList.get(i) == nodeID.get(j)){
				firstStartEventPerNode.add(eventStartTime.get(j));
				break;
			}
		}		
	}
	
	long minimumStartEventTime = firstStartEventPerNode.get(0);
	//int nodeIDWithMinimumValue;	
	for(int i=0; i<firstStartEventPerNode.size(); i++){
		if(minimumStartEventTime > firstStartEventPerNode.get(i)){
			minimumStartEventTime = firstStartEventPerNode.get(i);
		//	nodeIDWithMinimumValue = nodeIDList.get(i);
		}
	}
	
	for(int i=0; i<firstStartEventPerNode.size(); i++){
		difference = firstStartEventPerNode.get(i) - minimumStartEventTime;
		startEventWithOffset = difference + offsetValue;
		differencePerEvent.add(difference);
		startEventWithOffsetPerNode.add(startEventWithOffset);
	}
	
	for(int i=0; i<nodeIDList.size(); i++){
		offsetStartTimeTemp = new ArrayList<Long>();
		offsetStopTimeTemp = new ArrayList<Long>();
		int k = 0;
		for(int j=0; j<nodeID.size(); j++){
			if(nodeIDList.get(i).equals(nodeID.get(j))){				
				if(offsetStartTimeTemp.isEmpty()){
					//offsetStartTimeTemp.add(k, startEventWithOffsetPerNode.get(i));
					offsetStartTimeTemp.add(startEventWithOffsetPerNode.get(i));
					difference = eventStopTime.get(j)-eventStartTime.get(j);
					difference = difference + offsetStartTimeTemp.get(k);
					//offsetStopTimeTemp.add(k, difference);
					offsetStopTimeTemp.add(difference);
					k=k+1;
				}
				else{
					//offsetStartTimeTemp.add(k, offsetStopTimeTemp.get(k-1));
					offsetStartTimeTemp.add(offsetStopTimeTemp.get(k-1));
					difference = eventStopTime.get(j)-eventStartTime.get(j);
					difference = difference + offsetStartTimeTemp.get(k);
					//offsetStopTimeTemp.add(k, difference);
					offsetStopTimeTemp.add(difference);
					k=k+1;
				}
			}			
		}		
		count = count + offsetStartTimeTemp.size();
		eventStartTimewithOffset.addAll(offsetStartTimeTemp);
		eventStopTimewithOffset.addAll(offsetStopTimeTemp);		
	}	
	removeSingleEventNodeID();	
}

/**
 * This part of the program removes all the nodes from the list with up to 3 events in FTA Traces
 * This is because MTBF and MTTR will not get calculated with 1 event and simulation further generate null pointed exception
 */

public void removeSingleEventNodeID(){		
		int count;
		int temp1;
		int temp2;
		ArrayList<Integer>nodeIDListTemp = new ArrayList<Integer>();
		ArrayList<String>nodeLocationListTemp = new ArrayList<String>();
		for(int i=0;i<nodeIDList.size();i++){				
			count = 0;
			temp1=nodeIDList.get(i);			
			for(int j=0;j<nodeID.size();j++){
				temp2=nodeID.get(j);
				if(temp1 == temp2){
					count = count + 1;
				}		
				if(count>3){ //Removing all the nodes with events less than 4
					nodeIDListTemp.add(nodeIDList.get(i));			
					nodeLocationListTemp.add(nodeLocation.get(i));
					break;
				}
			}
			
		} 		
		nodeIDList.clear();
		nodeLocation.clear();
		nodeIDList = nodeIDListTemp;
		nodeLocation = nodeLocationListTemp;
		createClusterNodeMap();
}

/**
 * This function creates a table consisting of Node IDs corresponding to each cluster present in the Traces. 
 */
public void createClusterNodeMap(){
	ArrayList<Integer>nodeIDListLocal;
	for(int i=0; i<locationListNonAmbiguous.size(); i++){
		nodeIDListLocal = new ArrayList<Integer>();		
		for(int j=0; j<nodeLocation.size(); j++){
			if(nodeLocation.get(j).equals(locationListNonAmbiguous.get(i))){
				nodeIDListLocal.add(nodeIDList.get(j));
			}
		}
		clusterNodeMap.put(locationListNonAmbiguous.get(i), nodeIDListLocal);
	}		
	maphostID();
}

/**
 * This function returns a list consisting of Node IDs corresponding to a cluster present in the Traces. 
 */
public ArrayList<Integer> getClusterNodeMap(String clusterName){
	return clusterNodeMap.get(clusterName);	
}


/**
 * This function returns a cluster name corresponding to a specific node. 
 */
public String getClusterNameForNode(int nodeID){
	ArrayList<Integer>nodeListLocal;
	ArrayList<String>keyListLocal = new ArrayList<String>();;
	String clusterName = null;
	boolean flagLocal = false;
	keyListLocal.addAll(clusterNodeMap.keySet());	
	//for(int i=0; i<clusterNodeMap.size(); i++){
	//	if(keyListLocal.get(i).equals("G1/site7/c1")){
	//		System.out.println("Check");
	//	}
	//}
	for(int i=0; i<clusterNodeMap.size(); i++){
		//if(keyListLocal.get(i).equals("G1/site7/c1")){
		//	System.out.println("Check");
		//}
		nodeListLocal = new ArrayList<Integer>();		
		nodeListLocal.addAll(clusterNodeMap.get(locationListNonAmbiguous.get(i)));
		for(int j=0; j<nodeListLocal.size(); j++){
			if(nodeListLocal.get(j).equals(nodeID)){
				clusterName = locationListNonAmbiguous.get(i);
				flagLocal = true;
				break;
			}
		}
		if(flagLocal == true){
			break;
		}
	}
	return clusterName; 
}

/**
 * This function assigns an index number starts from 0 to each nodeID. 
 * The index number will be used to access the node in the list. 
 *  
 */	
public void maphostID(){
	for(int i=0;i<nodeIDList.size();i++){			
		mappedhostIDTable.put(i, nodeIDList.get(i));
	}		
eventTimeHours();
}	

/**
 * This function returns mapped host ID table .
 * 
 */	

public HashMap<Integer, Integer>getmappedhostIDTable(){
	return(mappedhostIDTable);
}


/**
 * 
 * This function returns a node id corresponding to an index  .
 * 
 */	
public int getNodeID(int nodeID){
	int node = 0;	
	for(int k=0;k<nodeIDList.size();k++){
		if(mappedhostIDTable.containsKey(nodeID)){
			node=mappedhostIDTable.get(nodeID);
			break;
		}
	}	
	return(node);
}

/**
 * This function converts the epoch time given in seconds into hours.
 * 
 */

public void eventTimeHours(){		
	for(int i=0; i<eventStartTime.size();i++){
		//eventStartTimeHours.add((double)eventStartTime.get(i)/3600);
		//eventStopTimeHours.add((double)eventStopTime.get(i)/3600);
		//eventStartTimeHours.add((double)eventStartTime.get(i));
		//eventStopTimeHours.add((double)eventStopTime.get(i));	
		eventStartTimeHours.add((double)eventStartTimewithOffset.get(i));
		eventStopTimeHours.add((double)eventStopTimewithOffset.get(i));
	}	
	differenceBetweenEvents();
}

public void differenceBetweenEvents(){
	double difference;	
	for(int i=0; i<eventStartTime.size();i++){		
		int count = 0;
		double value;
		int power;		
		difference = (eventStopTime.get(i))-(eventStartTime.get(i));
		value = difference;
		while(value>=1){
			value=value/10;
			count=count+1;
			}
		power = count - 2;			
		difference = difference/Math.pow(10, power);			
		differenceEvents.add(difference);		
		}
	prepareTBF();
}

/**
 * This function returns the number of availability events for a node .
 * 
 */
public int getAvailCount(int node){	
	int availCount=0;	
	for(int k=0;k<nodeID.size();k++){
		if(nodeID.get(k).equals(node) && eventType.get(k)==1){
			availCount=availCount+1;
		}
	}			
	return(availCount);
}

public int tbfCount = 0;
public void prepareTBF(){
	ArrayList<Double>tempTBF;	
	double difference;
	for(int i=0;i<nodeIDList.size();i++){
		tempTBF = new ArrayList<Double>();	
		difference = 0;			
		for(int j=0; j<eventStartTime.size();j++){		
			if((nodeID.get(j).equals(nodeIDList.get(i))) && (eventType.get(j)==1)){		
				tbfCount = tbfCount + 1;
				difference = eventStopTimeHours.get(j) - eventStartTimeHours.get(j);				
				tempTBF.add(difference);
			}
		}	
		hostTBFMap.put(nodeIDList.get(i), tempTBF);		
	}	
	if(RunTimeConstants.syntheticPrediction == true){
		predictionFlagValuesGenerator();
	}
	else{
		prepareMTBF();
	}
}

public void predictionFlagValuesGenerator(){
	if(RunTimeConstants.generatePredictionFlagValues == true){
		int precedingFailuresNumber;
		int laterFailuresNumber;
		int ratio;	
		int flagValue;	
		ratio = RunTimeConstants.ratioPrecedingtoLaterFailures;	
		precedingFailuresNumber = (int) Math.floor((tbfCount*ratio)/100);
		laterFailuresNumber = tbfCount - precedingFailuresNumber;
		for(int i=0; i<tbfCount; ){
			flagValue = (int) Math.round(Math.random());
			if(flagValue == 0 && precedingFailuresNumber > 0){
				predictionFailureFlag.add(flagValue);
				precedingFailuresNumber--;
				i++;
			}
			if(flagValue == 1 && laterFailuresNumber > 0){
				predictionFailureFlag.add(flagValue);
				laterFailuresNumber--;
				i++;
			}
		}
		try{
			FileWriter writer = new FileWriter("/rusers/postgrad/ysharma1/PhDWork/Java/PredictionFailureFlag.txt");
		//	FileWriter writer = new FileWriter("/home/ec2-user/Results_for_Migration_and_Consolidation/PredictionFailureFlag.txt");
			for(int i=0; i<predictionFailureFlag.size(); i++){
				writer.write(String.valueOf(predictionFailureFlag.get(i)));
				writer.write("\n");
			}
			writer.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	else{
		try{
			BufferedReader buffer;
			buffer = new BufferedReader(new FileReader("/rusers/postgrad/ysharma1/PhDWork/Java/PredictionFailureFlag.txt"));
		//	buffer = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/PredictionFailureFlag.txt"));
			String currentstr;
			while((currentstr=buffer.readLine()) != null){				
				String[] column = currentstr.trim().split("\\s+");				
				if(column.length>0){					
					predictionFailureFlag.add(Integer.parseInt(column[0]));					
				}				
			}
			buffer.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	prepareHostPredictionFlagMap();	
}

public void prepareHostPredictionFlagMap(){
	ArrayList<Double>tbfListTemp;
	ArrayList<Integer>predictionFlagListTemp;
	int index = 0;
	for(int i=0; i<nodeIDList.size(); i++){
		tbfListTemp = new ArrayList<Double>();
		predictionFlagListTemp = new ArrayList<Integer>();
		tbfListTemp = hostTBFMap.get(nodeIDList.get(i));
		for(int j=0; j<tbfListTemp.size(); j++){			
			predictionFlagListTemp.add(predictionFailureFlag.get(index));
			index = index + 1;
		}
		hostPredictionFlagMap.put(nodeIDList.get(i), predictionFlagListTemp);
	}
	prepareMTBF();
}

/**
 * This function returns the Time between Failures for a node.
 * 
 */	
public ArrayList<Double>getTBF(int node){
	return hostTBFMap.get(node);
}


public ArrayList<Integer>getHostPredictionFlagMap(int node){
	return hostPredictionFlagMap.get(node);
}
/**
 * This function returns the Mean Time between Failures for a node.
 * 
 */	
public double getMTBF(int node){
	ArrayList<Double>tempTBF = new ArrayList<Double>();
	double totalTBF=0.0;
	double MTBF;		
	tempTBF = hostTBFMap.get(node);
	for(int j=0;j<tempTBF.size();j++){
		totalTBF=totalTBF+(tempTBF.get(j));
	}
	MTBF = totalTBF/(getAvailCount(node));	
	if(MTBF==0){
		MTBF = 1;
	}
	return(MTBF);
}

/**
 * 
 * This function calculates the Mean Time between Failures(MTBF) corresponding to each node in
 * nodeID list.    
 * 
 */

public void prepareMTBF(){
	for(int i=0;i<nodeIDList.size();i++){		
		MTBF.add(getMTBF(nodeIDList.get(i)));	
		FTAMeanTimeBetweenFailure.setMTBF(nodeIDList.get(i), MTBF.get(i));
	}		
	prepareTTR();
}


public void prepareTTR(){
	ArrayList<Double>tempTTR;
	int count;
	double difference;
	for(int i=0;i<nodeIDList.size();i++){
		tempTTR = new ArrayList<Double>();
		count = 0;
		difference = 0;
		for(int j=0; j<eventStartTime.size();j++){		
			if((nodeID.get(j).equals(nodeIDList.get(i))) && (eventType.get(j)==0)){
				count = count + 1;
				difference = eventStopTimeHours.get(j) - eventStartTimeHours.get(j);
				tempTTR.add(difference);
			}
		}
		hostTTRMap.put(nodeIDList.get(i), tempTTR);		
	}	
	prepareMTTR();
}

/**
 * This function returns all the events corresponding to a node .
 * 
 */	

public int getFirstEvent(int node){
	int firstEvent = 0;
	for(int k=0;k<nodeID.size();k++){
		if(nodeID.get(k).equals(node)){
			firstEvent = eventType.get(k);
			break;
		}
	}					
	return(firstEvent);	
}

/**
 * This function returns the number of failure events for a node .
 * 
 */	
public int getFailCount(int node){	
	int failCount = 0;	
	/*
	for(int k=0;k<nodeID.size();k++){
		if(nodeID.get(k).equals(node)){
			if(getFirstEvent(node) == 0){
				failCount = -1;
				break;
			}
			else{
				failCount = 0;
				break;
			}
		}
	}
	*/	
	for(int k=0;k<nodeID.size();k++){	
		if(nodeID.get(k).equals(node) && eventType.get(k)==0){
			failCount=failCount+1;
		}
	}					
	return(failCount);	
}	

/**
 * This function returns the Mean Time to Return for a node.
 * 
 */	
public double getMTTR(int node){
	ArrayList<Double>tempTTR = new ArrayList<Double>();
	double totalTTR=0.0;
	double MTTR;
	tempTTR = hostTTRMap.get(node);
	for(int j=0;j<tempTTR.size();j++){			
		totalTTR = totalTTR + (tempTTR.get(j));
	}	
	MTTR = totalTTR/(getFailCount(node));	
	return(MTTR);	
}	

/**
 * 
 * This function calculates the Mean Time to Return(MTTR) corresponding to each node in
 * nodeID list.    
 * 
 */

public void prepareMTTR(){
	for(int i=0;i<nodeIDList.size();i++){		
		MTTR.add(getMTTR(nodeIDList.get(i)));	
		FTAMeanTimeBetweenFailure.setMTTR(nodeIDList.get(i), MTTR.get(i));
	}	
	prepareMaximumHazardRate();
}

/**
 * 
 * This function calculates the Maximum Hazard Rate(Failure Rate) such that Hazard rate when utilization is 1 corresponding to each node in
 * nodeID list.    
 * 
 */

public void prepareMaximumHazardRate(){
	for(int i=0;i<MTBF.size();i++){
		HazardRate.add(1/MTBF.get(i));
	}	
	prepareCurrentUtilization();
}

/**
 * 
 * This function returns the hazard rate at maximum utilization corresponding to a specific node
 * 
 */

public double getMaxHazardRate(int node){
	double Hazard_rate = 0;
	for(int i=0;i<nodeIDList.size();i++){
		if(nodeIDList.get(i).equals(node)){
			Hazard_rate = HazardRate.get(i);
			break;
		}
	}
	return(Hazard_rate);		
}	

/**
 * 
 * This function sets the initial utilization between 0 to 10 percent for each FTA node
 * 
 */

public void prepareCurrentUtilization(){
	double utilization;	
	try {				
	//	BufferedReader readerUtl = new BufferedReader(new FileReader("/raid/disc1/ysharma1/Java/Results/InitialUtilization.txt"));
	//	BufferedReader readerUtl = new BufferedReader(new FileReader("/home/ec2-user/Results_for_Migration_and_Consolidation/InitialUtilization.txt"));
		BufferedReader readerUtl = new BufferedReader(new FileReader("/Users/yogeshsharma/Documents/Data/FTATraces/InitialUtilization.txt"));
		System.out.println("Size of Node ID List is" +nodeIDList.size());
		for(int i=0;i<nodeIDList.size();i++){	
		utilization = Double.parseDouble(readerUtl.readLine());			
		currentUtilization.add(utilization);
	//	FTACurrentUtilization.setCurrentUtilization(nodeIDList.get(i), utilization);
		}				
		readerUtl.close();		
	} catch (NumberFormatException | IOException e) {
		System.out.println("Error has occurred while readeing Initial Utilization");
	}
	
	if(RunTimeConstants.failureCorrelation == true){
		setClusterConstants();
	}
	else{
		prepareAvailability();
	}	
	//prepareCurrentHazardRate();	
}

public void setClusterConstants(){
	for(int i=0; i<locationListNonAmbiguous.size();i++){
		clusterConstant.put(locationListNonAmbiguous.get(i), i);
	}	
	if(RunTimeConstants.hierarchicalClustering == true && RunTimeConstants.nonhierarchicalClustering == false){
		extractStartTimeInformation();				
	}
	if(RunTimeConstants.hierarchicalClustering == false &&RunTimeConstants.nonhierarchicalClustering == true){
		kmeans.setFTARead(this);
		callKmeans();
	}
}

public void callKmeans(){
	for(int i=0; i<locationListNonAmbiguous.size(); i++){
		kmeans.initializeKMeansAlgorithm(locationListNonAmbiguous.get(i));
	}
	prepareAvailability();
}

public void extractStartTimeInformation(){
	ArrayList<Long>failureStartTimeLocal;
	ArrayList<Integer>nodeIDforCluster;
	for(int i=0; i<locationListNonAmbiguous.size(); i++){		
		failureStartTimeLocal = new ArrayList<Long>();
		nodeIDforCluster = new ArrayList<Integer>();
		nodeIDforCluster = clusterNodeMap.get(locationListNonAmbiguous.get(i));
		for(int j=0; j<nodeIDforCluster.size(); j++){
			for(int k=0; k<nodeID.size(); k++){
				if(nodeID.get(k).equals(nodeIDforCluster.get(j))&& eventType.get(k)==0){					
					failureStartTimeLocal.add(eventStartTime.get(k));
				}
			}			
		}		
		clusterTimeMap.put(locationListNonAmbiguous.get(i), failureStartTimeLocal);
	}
	createNonAmbiguousTimeInformation();	
}

public void createNonAmbiguousTimeInformation(){
	LinkedHashSet<Long>startTimeSet;
	ArrayList<Long>failureStartTimeLocal;
	for(int i=0; i<locationListNonAmbiguous.size(); i++){
		startTimeSet = new LinkedHashSet<Long>();
		failureStartTimeLocal = new ArrayList<Long>();
		failureStartTimeLocal.addAll(clusterTimeMap.get(locationListNonAmbiguous.get(i)));
		for(int j=0; j<failureStartTimeLocal.size(); j++){
			startTimeSet.add(failureStartTimeLocal.get(j));
		}
		failureStartTimeLocal = new ArrayList<Long>();
		failureStartTimeLocal.addAll(startTimeSet);
		clusterTimeMapNonAmbiguous.put(locationListNonAmbiguous.get(i), failureStartTimeLocal);		
	}
	createAdjacencyMatrix();
}

public void createAdjacencyMatrix(){
	ArrayList<ArrayList<Integer>>adjacencyMatrix;
	ArrayList<Integer>listForAdjacencyMatrix;
	ArrayList<Integer>nodeListLocal;
	ArrayList<Long>timeListLocal;
	boolean flagLocal;
	for(int i=0; i<locationListNonAmbiguous.size(); i++){
		nodeListLocal = new ArrayList<Integer>();
		timeListLocal = new ArrayList<Long>();
		nodeListLocal.addAll(clusterNodeMap.get(locationListNonAmbiguous.get(i)));
		timeListLocal.addAll(clusterTimeMapNonAmbiguous.get(locationListNonAmbiguous.get(i)));
		adjacencyMatrix = new ArrayList<ArrayList<Integer>>();
		for(int j=0; j<nodeListLocal.size(); j++){
			listForAdjacencyMatrix = new ArrayList<Integer>();			
			for(int k=0; k<timeListLocal.size(); k++){				
				flagLocal = false;
				for(int l=0; l<nodeID.size(); l++){
					if(nodeListLocal.get(j)==nodeID.get(l)&&timeListLocal.get(k)==eventStartTime.get(l)&&eventType.get(l)==0){						
						listForAdjacencyMatrix.add(1);
						flagLocal = true;
						break;
					}					
				}
				if(flagLocal == false){
					listForAdjacencyMatrix.add(0);
				}
			}			
			adjacencyMatrix.add(listForAdjacencyMatrix);			
		}
		adjacencyMatrixMap.put(locationListNonAmbiguous.get(i), adjacencyMatrix);
	}
	prepareAvailability();
}
	
/**
 * 
 * This function updates the initial utilization during the execution
 * 
 */

public void setCurrentUtilization(int node, double utilization){
	double current_utilization;
	for(int i=0;i<nodeIDList.size();i++){
		if(nodeIDList.get(i).equals(node)){
			current_utilization = currentUtilization.get(i);
			current_utilization = current_utilization + utilization;
			currentUtilization.set(i, current_utilization);			
		}
	}
}	

/**
 * 
 * This function returns the initial utilization 
 * 
 */	
public double getCurrentUtilization(int node){
	double utilization = 0.0;
	for(int i=0;i<nodeIDList.size();i++){
		if(nodeIDList.get(i).equals(node)){
			utilization = currentUtilization.get(i);
			break;
		}
	}
	return(utilization);			
}

/**
 * 
 * This function calculates the hazard rate using the initial utilization of fta host
 * 
 */	

public void prepareCurrentHazardRate(){		
	int sensitivity_factor = 1;
	double current_hazardrate;
	for(int i=0; i<nodeIDList.size(); i++){
		current_hazardrate = getMaxHazardRate(nodeIDList.get(i)) * Math.pow(getCurrentUtilization(nodeIDList.get(i)), sensitivity_factor);
		currentHazardRate.add(current_hazardrate);
	}	
	//setSortedHazardRateNodeID();
}

/**
 * 
 * This function returns the initial hazard rate 
 * 
 */	
public double getCurrentHazardRate(int node){	
	double current_hazardrate = 0.0;
	for(int i=0;i<nodeIDList.size();i++){
		if(nodeIDList.get(i).equals(node)){
			current_hazardrate = currentHazardRate.get(i);
			break;
		}
	}
	return(current_hazardrate);
}

/**
 * 
 * This function sorts all the nodes in nodeIDListWithoutSingle in increasing order according
 * to their initial hazard rates.
 * 
 */
public void setSortedHazardRateNodeID(){	
	nodeIDList_sorted  = new Integer[nodeIDList.size()];
	nodeIDList_sorted = nodeIDList.toArray(nodeIDList_sorted);
	int temp;
	for(int i=0;i<nodeIDList.size()-1;i++){
		for(int j=i+1;j<nodeIDList.size();j++){
			if(getCurrentHazardRate(nodeIDList_sorted[i])>getCurrentHazardRate(nodeIDList_sorted[j])){
				temp = nodeIDList_sorted[i];
				nodeIDList_sorted[i] = nodeIDList_sorted[j];
				nodeIDList_sorted[j] = temp;
			}
		}
	}	
	//setNodeIDListSorted();
}

/**
 * 
 * This function copies an array of sorted nodeIDList create in function setSortedHazardRateNodeID() to an ArrayList
 *  
 */
public void setNodeIDListSorted(){
	for(int i=0;i<nodeIDList.size();i++){
		NodeIDListSorted.add(nodeIDList_sorted[i]);
	}
	//setNodeListPower();
}


/**
 * This function returns the core count corresponding to an FTA node .
 * 
 */	
public int getProcessorCount(int node){
	int processor = 0;	
	for(int i=0;i<nodeIDList.size();i++){		
		if(nodeIDList.get(i).equals(node)){
			processor = (num_procs.get(i)*num_procs_const.get(i));
			break;
		}		
	}		
	return(processor);		
}

/**
 * This function returns the non-redundant list of number of cores per FTA node.
 */

public ArrayList<Integer>getProcessorCountNonRedundantList(){
	ArrayList<Integer>numberOfProcessors = new ArrayList<Integer>();
	int processorCount = 0;
	for(int i=0; i<nodeIDList.size(); i++) {
		processorCount = getProcessorCount(nodeIDList.get(i));
		if(!numberOfProcessors.contains(processorCount)) {
			numberOfProcessors.add(processorCount);
		}
	}
	return numberOfProcessors;
}

/**
 * This function returns the memory size corresponding to an FTA node .
 * 
 */	
public int getMemorySize(int node){
	int memory = 0;
	for(int i=0;i<nodeIDList.size();i++){
		if(nodeIDList.get(i).equals(node)){
			memory = (getProcessorCount(node)*4*1024);
			break;
		}		
	}		
	return(memory);
}



public PowerModel pow = new PowerModel();

public void setNodeListPower(){
	int core_count;	
	int nodeIDwithoutpower;
	int counter = 0;
	for(int i=0;i<nodeIDList.size();i++){		
		double power = 0;
		counter = counter + 1;	
		core_count = getProcessorCount(nodeIDList.get(i));
		double utilization;
		utilization = getCurrentUtilization(nodeIDList.get(i));	
		if(core_count == 2){
			power = pow.getPower(utilization, core_count);			
			powerList.add(power);		
		}		
		if(core_count == 4){
			power = pow.getPower(utilization, core_count);		
			powerList.add(power);	
		}		
		if(core_count == 8){
			power = pow.getPower(utilization, core_count);		
			powerList.add(power);
		}		
		if(core_count == 32){
			power = pow.getPower(utilization, core_count);		
			powerList.add(power);	
		}		
		if(core_count == 80 || core_count == 128){
			power = pow.getPower(utilization, core_count);		
			powerList.add(power);		
		}		
		if(core_count == 256){
			power = pow.getPower(utilization, core_count);		
			powerList.add(power);		
		}		
		if(core_count != 2 && core_count != 4 && core_count != 8 && core_count != 32 && core_count != 80 && core_count != 128 && core_count != 256){
			nodeIDwithoutpower = nodeIDList.get(i);
			System.out.println("Value of counter is " +nodeIDwithoutpower);
		}	
	}
	//setSortedPowerNodeID();
}


/**
 * 
 * This function returns the current power consumption corresponding to a node
 *  
 */
public Double getPowerforHost(int node){
	double power = 0;
	for(int i=0;i<nodeIDList.size();i++){
		if(nodeIDList.get(i).equals(node)){
			power = powerList.get(i);
			break;
		}
	}
	return power;		
}

/**
 * 
 * This function sorts all the nodes in nodeIDList in increasing order according
 * to their current power consumption.
 * 
 */
public void setSortedPowerNodeID(){		
	nodeIDList_sorted  = new Integer[nodeIDList.size()];
	nodeIDList_sorted = nodeIDList.toArray(nodeIDList_sorted);
	int temp;	
	for(int i=0;i<nodeIDList.size()-1;i++){
		for(int j=i+1;j<nodeIDList.size();j++){			
			if(getPowerforHost(nodeIDList_sorted[i])>getPowerforHost(nodeIDList_sorted[j])){
				temp = nodeIDList_sorted[i];
				nodeIDList_sorted[i] = nodeIDList_sorted[j];
				nodeIDList_sorted[j] = temp;
			}		
		}
	}
	//setPowerHostListSorted();
}


/**
 * 
 * This function copies an array of sorted nodeIDList create in function setSortedPowerNodeID() to an ArrayList
 *  
 */
public void setPowerHostListSorted(){
	for(int i=0;i<nodeIDList.size();i++){
		nodeIDListSortedPower.add(nodeIDList_sorted[i]);
	}
//	setProductPowerandHazardRate();
}

/**
 * 
 * This function can be used to perform various mathematical operations using reliability and power related values associated 
 * to the nodes to select hosts
 *  
 */
public void setProductPowerandHazardRate(){
	double productPowerandHazardRate;
	for(int i=0;i<nodeIDList.size();i++){
		productPowerandHazardRate = getMTBF(nodeIDList.get(i))/getPowerforHost(nodeIDList.get(i));
		productPowerandHazardRateList.add(productPowerandHazardRate);
	}	
//	setSortedPowerandHazardRateNodeID();
}


/**
 * 
 * This function returns the product of current power consumption and current hazard rate corresponding to a node
 *  
 */
public Double getProductPowerandHazardRateforHost(int node){
	double power = 0;
	for(int i=0;i<nodeIDList.size();i++){
		if(nodeIDList.get(i).equals(node)){
			power = productPowerandHazardRateList.get(i);
			break;
		}
	}
	return power;		
}

/**
 * 
 * This function sorts all the nodes in nodeIDList in increasing order according
 * to their MTBF and power consumption.
 * 
 */
public void setSortedPowerandHazardRateNodeID(){
	nodeIDList_sorted  = new Integer[nodeIDList.size()];	
	nodeIDList_sorted = nodeIDList.toArray(nodeIDList_sorted);
	int temp;
	for(int i=0;i<nodeIDList.size()-1;i++){
		for(int j=i+1;j<nodeIDList.size();j++){
			if(getProductPowerandHazardRateforHost(nodeIDList_sorted[i])<getProductPowerandHazardRateforHost(nodeIDList_sorted[j])){
				temp = nodeIDList_sorted[i];
				nodeIDList_sorted[i] = nodeIDList_sorted[j];
				nodeIDList_sorted[j] = temp;
			}
		}
	}
	//setPowerandHazardRateHostListSorted();
}

/**
 * 
 * This function copies an array of sorted nodeIDList create in function setSortedPowerandHazardRateNodeID() to an ArrayList
 *  
 */
public void setPowerandHazardRateHostListSorted(){
	for(int i=0;i<nodeIDList.size();i++){
		nodeIDListSortedPowerandHazardRate.add(nodeIDList_sorted[i]);
	}	
	//createNodeMaintainabilityList();
}

/**
 * 
 * This function returns the Availability corresponding to each node in nodeID list.    
 * 
 */
public double getAvailability(int node){	
	double availability;
	double MTBF = getMTBF(node);
	double MTTR = getMTTR(node);
	availability = MTBF/(MTBF+MTTR);
	return availability;
}

/**
 * 
 * This function returns the Maintainability corresponding to each node in nodeID list.    
 * 
 */
public double getMaintainability(int node){
	double maintainability;
	double MTBF = getMTBF(node);
	double MTTR = getMTTR(node);
	maintainability = MTTR/(MTBF+MTTR);
	return maintainability;
}


public void prepareAvailability(){
	for(int i=0;i<nodeIDList.size();i++){			
		FTAMeanTimeBetweenFailure.setAvailability(nodeIDList.get(i), getAvailability(nodeIDList.get(i)));
	}
	prepareMaintainability();
}

public void prepareMaintainability(){
	for(int i=0;i<nodeIDList.size();i++){			
		FTAMeanTimeBetweenFailure.setMaintainability(nodeIDList.get(i), getMaintainability(nodeIDList.get(i)));
	}
	
}


/**
 * 
 * This function prepares a list of Maintainability corresponding to each node in nodeID list.    
 * 
 */
public void createNodeMaintainabilityList(){
	double nodeMaintainability;
	for(int i=0;i<nodeIDList.size();i++){
		nodeMaintainability = getMaintainability(nodeIDList.get(i));
		Maintainability.add(nodeMaintainability);
	}
	//setSortedMaintainabilityNodeID();
}

/**
 * 
 * This function sorts all the nodes in nodeIDList in increasing order according
 * to their Maintainability.
 * 
 */
public void setSortedMaintainabilityNodeID(){		
	nodeIDList_sorted = new Integer[nodeIDList.size()];
	nodeIDList_sorted = nodeIDList.toArray(nodeIDList_sorted);
	int temp;
	for(int i=0;i<nodeIDList.size()-1;i++){
		for(int j=i+1;j<nodeIDList.size();j++){
			if(getMaintainability(nodeIDList_sorted[i])>getMaintainability(nodeIDList_sorted[j])){
				temp = nodeIDList_sorted[i];
				nodeIDList_sorted[i] = nodeIDList_sorted[j];
				nodeIDList_sorted[j] = temp;
			}
		}
	}	
	//setMaintainabilityHostListSorted();
}

/**
 * 
 * This function copies an array of sorted nodeIDList create in function setSortedMaintainabilityNodeID() to an ArrayList
 *  
 */
public void setMaintainabilityHostListSorted(){
	for(int i=0;i<nodeIDList.size();i++){
		nodeIDMaintainabilityListSorted.add(nodeIDList_sorted[i]);			
	}
	//setProductPowerandMaintainability();			
}

/**
 * 
 * This function can be used to perform various mathematical operations using reliability and power related values associated 
 * to the nodes to select hosts
 *  
 */
public void setProductPowerandMaintainability(){	
	double productPowerandMaintainability;	
	for(int i=0;i<nodeIDList.size();i++){		
		productPowerandMaintainability = getMaintainability(nodeIDList.get(i))*getPowerforHost(nodeIDList.get(i));
		productPowerandMaintainabilityList.add(productPowerandMaintainability);
	}
	//setSortedPowerandMaintainabilityNodeID();
}

/**
 * 
 * This function returns the product of power consumption and Maintainability corresponding to a node
 *  
 */
public Double getProductPowerandMaintainabilityforHost(int node){
	double power = 0;
	for(int i=0;i<nodeIDList.size();i++){
		if(nodeIDList.get(i).equals(node)){
			power = productPowerandMaintainabilityList.get(i);
			break;
		}
	}
	return power;
}

/**
 * 
 * This function sorts all the nodes in nodeIDList in increasing order according to their Maintainability and power consumption.
 * 
 */

public void setSortedPowerandMaintainabilityNodeID(){
	nodeIDList_sorted  = new Integer[nodeIDList.size()];	
	nodeIDList_sorted = nodeIDList.toArray(nodeIDList_sorted);
	int temp;
	for(int i=0;i<nodeIDList.size()-1;i++){
		for(int j=i+1;j<nodeIDList.size();j++){
			if(getProductPowerandMaintainabilityforHost(nodeIDList_sorted[i])>getProductPowerandMaintainabilityforHost(nodeIDList_sorted[j])){
				temp = nodeIDList_sorted[i];
				nodeIDList_sorted[i] = nodeIDList_sorted[j];
				nodeIDList_sorted[j] = temp;
			}
		}
	}
	//setPowerandMaintainabilityHostListSorted();
}

/**
 * 
 * This function copies an array of sorted nodeIDList create in function setSortedPowerandMaintainabilityNodeID() to an ArrayList
 *  
 */
public void setPowerandMaintainabilityHostListSorted(){
	for(int i=0;i<nodeIDList.size();i++){
		nodeIDSortedPowerandMaintainability.add(nodeIDList_sorted[i]);		
	}				
	//createNodeAvailabilityList();
}

/**
 * 
 * This function prepares a list of Availability corresponding to each node in nodeID list.    
 * 
 */
public void createNodeAvailabilityList(){
	double nodeAvailability;
	for(int i=0;i<nodeIDList.size();i++){
		nodeAvailability = getAvailability(nodeIDList.get(i));
		Availability.add(nodeAvailability);
	}
	//setSortedAvailabilityNodeID();
}


/**
 * 
 * This function sorts all the nodes in nodeIDList in increasing order according
 * to their Availability.
 * 
 */
public void setSortedAvailabilityNodeID(){		
	nodeIDList_sorted = new Integer[nodeIDList.size()];
	nodeIDList_sorted = nodeIDList.toArray(nodeIDList_sorted);
	int temp;
	for(int i=0;i<nodeIDList.size()-1;i++){
		for(int j=i+1;j<nodeIDList.size();j++){
			if(getAvailability(nodeIDList_sorted[i])>getAvailability(nodeIDList_sorted[j])){
				temp = nodeIDList_sorted[i];
				nodeIDList_sorted[i] = nodeIDList_sorted[j];
				nodeIDList_sorted[j] = temp;
			}
		}
	}	
	//setAvailabilityHostListSorted();
}

/**
 * 
 * This function copies an array of sorted nodeIDList create in function setSortedAvailabilityNodeID() to an ArrayList
 *  
 */
public void setAvailabilityHostListSorted(){
	for(int i=0;i<nodeIDList.size();i++){
		nodeIDAvailabilityListSorted.add(nodeIDList_sorted[i]);			
	}
	//setFractionPowerandAvailability();			
}


/**
 * 
 * This function can be used to perform various mathematical operations using availability and power related values associated 
 * to the nodes 
 *  
 */

public void setFractionPowerandAvailability(){	
	double fractionPowerandAvailability;	
	for(int i=0;i<nodeIDList.size();i++){		
		fractionPowerandAvailability = getAvailability(nodeIDList.get(i))*getPowerforHost(nodeIDList.get(i));
		fractionPowerandAvailabilityList.add(fractionPowerandAvailability);
	}
	//setSortedPowerandAvailabilityNodeID();
}

/**
 * 
 * This function returns the product of power consumption and Availability corresponding to a node
 *  
 */
public Double getFractionPowerandAvailabilityforHost(int node){
	double power = 0;
	for(int i=0;i<nodeIDList.size();i++){
		if(nodeIDList.get(i).equals(node)){
			power = fractionPowerandAvailabilityList.get(i);
			break;
		}
	}
	return power;		
}


/**
 * 
 * This function sorts all the nodes in nodeIDList in increasing order according to their Maintainability and power consumption.
 * 
 */
public void setSortedPowerandAvailabilityNodeID(){
	nodeIDList_sorted  = new Integer[nodeIDList.size()];	
	nodeIDList_sorted = nodeIDList.toArray(nodeIDList_sorted);
	int temp;
	for(int i=0;i<nodeIDList.size()-1;i++){
		for(int j=i+1;j<nodeIDList.size();j++){
			if(getFractionPowerandAvailabilityforHost(nodeIDList_sorted[i])>getFractionPowerandAvailabilityforHost(nodeIDList_sorted[j])){
				temp = nodeIDList_sorted[i];
				nodeIDList_sorted[i] = nodeIDList_sorted[j];
				nodeIDList_sorted[j] = temp;
			}
		}
	}
	//setPowerandAvailabilityHostListSorted();
}

/**
 * 
 * This function copies an array of sorted nodeIDList create in function setSortedPowerandMaintainabilityNodeID() to an ArrayList
 *  
 */
public void setPowerandAvailabilityHostListSorted(){
	for(int i=0;i<nodeIDList.size();i++){
		nodeIDSortedPowerandAvailability.add(nodeIDList_sorted[i]);		
	}				
}

/**
 * 
 * This module populates a temporary list storing failure start times corresponding to a node
 * 
 */
public void preparestartTime(int node){	
	startTimeTemp = new ArrayList<Double>();
	for(int k=0;k<nodeID.size();k++){
		if(nodeID.get(k).equals(node) && eventType.get(k)==0){
			startTimeTemp.add(eventStartTimeHours.get(k));
		}
	}			
}

/**
 * 
 * This module populates a temporary list storing failure stop times corresponding to a node
 * 
 */
public void preparestopTime(int node){	
	stopTimeTemp = new ArrayList<Double>();
	for(int k=0;k<nodeID.size();k++){
		if(nodeID.get(k).equals(node) && eventType.get(k)==0){
			stopTimeTemp.add(eventStopTimeHours.get(k));
		}
	}
}


/**
 * 
 * This module populates a temporary list storing availability start times corresponding to a node
 * 
 */
public void prepareAvailstartTime(int node){	
	startTimeAvailTemp = new ArrayList<Double>();
	for(int k=0;k<nodeID.size();k++){
		if(nodeID.get(k).equals(node) && eventType.get(k)==1){			
			startTimeAvailTemp.add(eventStartTimeHours.get(k));
		}
	}
}

/**
 * 
 * This module populates a temporary list storing failure stop times corresponding to a node
 * 
 */
public void prepareAvailstopTime(int node){	
	stopTimeAvailTemp = new ArrayList<Double>();
	for(int k=0;k<nodeID.size();k++){
		if(nodeID.get(k).equals(node) && eventType.get(k)==1){
			stopTimeAvailTemp.add(eventStopTimeHours.get(k));
		}
	}
}


/**
 * 
 * This module populates a temporary list storing difference between events corresponding to a node
 * 
 */
public void prepareDifference(int node){	
	differenceTemp = new ArrayList<Double>();
	for(int k=0;k<nodeID.size();k++){
		double difference = 0;
		if(nodeID.get(k).equals(node) && eventType.get(k)==0){
			if(differenceEvents.get(k)<1){
				difference = differenceEvents.get(k)+1;
				differenceTemp.add(difference);
			}
			else{
			differenceTemp.add(differenceEvents.get(k));
			}
		}
	}	
}


/**
 * This function returns the start time of a failure for a node.
 * 
 */	
public double getstartTime(){
	Double start=0.0;
	for(int i=0;i<startTimeTemp.size();){
		start=startTimeTemp.get(i);
		startTimeTemp.remove(i);
		break;
	}		
	return(start);	
}

/**
 * This function returns the start time of a first failure for a node without removing it from the list.
 * 
 */	
public double getfirststartTime(){
	Double firstStart=0.0;	
	firstStart=startTimeTemp.get(0);
	return firstStart;
		
}

/**
 * This function returns the stop time of a failure for a node.
 * 
 */	
public double getendTime(){		
	Double stop = 0.0 ;	
	for(int i=0;i<stopTimeTemp.size();){
		stop=stopTimeTemp.get(i);
		stopTimeTemp.remove(i);
		break;
	}		
	return(stop);
}

/**
 * This function returns the difference between two events for a node.
 * 
 */	
public double getDifference(){
	Double difference = 0.0;
	for(int i=0;i<differenceTemp.size();){
		difference=differenceTemp.get(i);
		differenceTemp.remove(i);
		break;
	}
	return difference;
}

/**
 * This function returns the start time of an availability term for a node.
 * 
 */	
public double getstartTimeAvail(){
	Double start=0.0;
	for(int i=0;i<startTimeAvailTemp.size();){
		start=startTimeAvailTemp.get(i);
		startTimeAvailTemp.remove(i);
		break;
	}		
	return(start);	
}

/**
 * This function returns the stop time of an availability term for a node.
 * 
 */	
public double getendTimeAvail(){		
	Double stop = 0.0 ;	
	for(int i=0;i<stopTimeAvailTemp.size();){
		stop=stopTimeAvailTemp.get(i);
		stopTimeAvailTemp.remove(i);
		break;
	}		
	return(stop);
}


/**
 * 
 * This function returns the current power consumption corresponding to all the nodes
 *  
 */
public ArrayList<Double>getPowerList(){
	return powerList;
}

/**
 * 
 * This function returns the ArrayList of sorted nodes according to their initial hazard rate in increasing order
 *  
 */
public ArrayList<Integer> getSortedNodeIDList(){
	return NodeIDListSorted;
}


/**
 * 
 * This function returns the ArrayList of sorted nodes according to their power consumption in increasing order
 *  
 */
public ArrayList<Integer>getPowerHostListSorted(){
	return nodeIDListSortedPower;
}


/**
 * 
 * This function returns the ArrayList of sorted nodes according to their power consumption and hazard rate product in increasing order
 *  
 */
public ArrayList<Integer>getPowerandHazardRateHostListSorted(){
	return nodeIDListSortedPowerandHazardRate;
}


/**
* 
* This function returns the ArrayList of sorted nodes according to their Maintainability in increasing order
*  
*/
public ArrayList<Integer>getMaintainabilityHostListSorted(){
	return nodeIDMaintainabilityListSorted;
}




/**
* 
* This function returns the ArrayList of sorted nodes according to their power consumption and maintainability product in increasing order
*  
*/
public ArrayList<Integer>getPowerandMaintainabilityHostListSorted(){
	return nodeIDSortedPowerandMaintainability;
}


/**
* 
* This function returns the ArrayList of sorted nodes according to their Availability in increasing order
*  
*/
public ArrayList<Integer>getAvailabilityHostListSorted(){
	return nodeIDAvailabilityListSorted;
}

/**
* 
* This function returns the ArrayList of sorted nodes according to their power consumption and availability fraction in increasing order
*  
*/
public ArrayList<Integer>getPowerandAvailabilityHostListSorted(){
	return nodeIDSortedPowerandAvailability;
}

/**
 * 
 * This function returns the maintainability list 
 * 
 */
public ArrayList<Double>getMaintainabilityList(){
	return Maintainability;
}

/**
 * 
 * This function returns the hazard rate list 
 * 
 */	
public ArrayList<Double>getHazardRateList(){
	return currentHazardRate;
}

/**
 * 
 * This function returns the nodeID list
 * 
 */	
public ArrayList<Integer>getNodeIDList(){
	return nodeIDList;
}

/**
 * 
 * This function returns the site and cluster information corresponding to a node
 * 
 */	
public ArrayList<String>getLocationList(){
	return nodeLocation;
}

/**
 * 
 * This function returns the non-ambiguous site and cluster list
 * 
 */	
public ArrayList<String>getNonAmbiguousLocationList(){
	return locationListNonAmbiguous;
}

public K_MeansAlgorithm getKMeansObject(){
	return kmeans;
}

/**
 * This function sets the flag for correlated migration to indicate about the migration happening 
 * because of correlated failures. By using this flag the provisioning of correlated nodes can be avoided. 
 * This flag has been defined in this class because the object of datacenter class is not existing in allocation class
 * and the value of this flag is being governed by datacenter class. 
 *  
 * @param correlatedMigration
 */
public void setCorrelatedMigraitonCounter(int correlatedMigrationCounter){
	this.correlatedMigrationCounter = correlatedMigrationCounter;
}

public int getCorrelatedMigrationCounter(){
	return correlatedMigrationCounter;
}
//public static void main(String args[]){
//	FTAFileReaderGrid5000 ftaRead = new FTAFileReaderGrid5000();
//	ftaRead.removenodeIdAmbiguity();
//}
}
