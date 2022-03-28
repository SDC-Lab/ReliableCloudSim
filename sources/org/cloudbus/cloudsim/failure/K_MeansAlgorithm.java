package org.cloudbus.cloudsim.failure;

import java.util.ArrayList;
import java.util.HashMap;

public class K_MeansAlgorithm {
	
	public FTAFileReaderGrid5000 ftaread;	
	public FTAFileReader ftaread_LANL;
	public String clusterName;	
	public Double clusterNameLANL;
	public ArrayList<Integer>nodeList;
	public HashMap<Integer, Double>mtbfMap;
	public HashMap<Integer, ArrayList<Integer>>partitionMap;
	public HashMap<Integer, ArrayList<Double>>differenceMap;
	public ArrayList<Integer>partitionList;
	public ArrayList<Double>differenceList;	
	public HashMap<String, HashMap<Integer, ArrayList<Integer>>>locationWiseClustering = new HashMap<String, HashMap<Integer, ArrayList<Integer>>>();
	
	
	public void setFTARead(FTAFileReaderGrid5000 ftaread){
		this.ftaread = ftaread;
	}
	
	public void initializeKMeansAlgorithm(String clusterName){
		this.clusterName = clusterName;
		setNodeList();		
	}
	
	public void initializeKMeansAlgorithm(Double clusterNameLANL){
		this.clusterNameLANL = clusterNameLANL;			
		setNodeList();	
	}	
	
	public void setNodeList(){
		nodeList = new ArrayList<Integer>();
		nodeList.addAll(ftaread.getClusterNodeMap(clusterName));
		setMTBFList();
		//createClusters();
	}
	
	
	
	public void setMTBFList(){
		mtbfMap = new HashMap<Integer, Double>();
		double mtbf;
		for(int i=0 ;i<nodeList.size(); i++){
	//		if(i==60){
	//			System.out.println("Check");
	//		}
			mtbf = ftaread.getMTBF(nodeList.get(i));
			//mtbf = FTAMeanTimeBetweenFailure.getMTBF(nodeList.get(i));
			mtbfMap.put(nodeList.get(i), mtbf);
		}
		createClusters();
	}
	
	public void createClusters(){
		int nodeListSize;
		nodeListSize = nodeList.size();
		int numberofClusters;
		numberofClusters = RunTimeConstants.numberofClusters;
		partitionMap = new HashMap<Integer, ArrayList<Integer>>();
		int clusterSize = 0;
		int counter = 1;
		int j = 0;
		int remainingNodes = nodeListSize;
		for(int i=0; i<numberofClusters;i++){
			if((nodeList.size()%numberofClusters)!=0){
				partitionList = new ArrayList<Integer>();						
				if(counter < numberofClusters){
					clusterSize = (int) Math.floor(nodeList.size()/numberofClusters);	
					for(int k=0; k<clusterSize; k++){
						partitionList.add(nodeList.get(j));
						j=j+1;
					}
					partitionMap.put(i, partitionList);
					counter = counter + 1;
					remainingNodes = remainingNodes - clusterSize;
				}
				else{
					clusterSize = (int) Math.ceil(nodeList.size()/numberofClusters);
					remainingNodes = remainingNodes - clusterSize;
					clusterSize = clusterSize + remainingNodes;
					for(int k=0; k<clusterSize; k++){
						partitionList.add(nodeList.get(j));
						j=j+1;
					}
					partitionMap.put(i, partitionList);
				}
			}
			else{
				partitionList = new ArrayList<Integer>();
				clusterSize = nodeList.size()/numberofClusters;
				for(int k=0; k<clusterSize; k++){
					partitionList.add(nodeList.get(j));
					j=j+1;
				}
				partitionMap.put(i,  partitionList);
			}
		}		
		k_means_withInitialPartitioning();
	}
	
	public void k_means_withInitialPartitioning(){
		boolean flag = true;		
		//int globalCounter = 0;
		ArrayList<Double>previousCentroids = new ArrayList<Double>();
		while(flag == true){			
			boolean flag_finished = false;
			ArrayList<Double>centroids = new ArrayList<Double>();		
			ArrayList<Integer>clusterLocal;
			HashMap<Integer, Double>differenceMap;
			HashMap<Integer, Integer>shiftMap;
			boolean flag_difference = false;
			boolean flag_second = false;
			double difference = 0;		
			int counterLocal = 0;
			for(int i=0; i<partitionMap.size(); i++){
				double nuclei = 0;
				clusterLocal = new ArrayList<Integer>();
				clusterLocal.addAll(partitionMap.get(i));
				for(int j=0; j<clusterLocal.size(); j++){
					nuclei = nuclei + mtbfMap.get(clusterLocal.get(j));								
				//	nuclei = nuclei + ftaread.getMTBF(clusterLocal.get(j));
				}
				nuclei = nuclei/clusterLocal.size();
				centroids.add(nuclei);
			}
			if(previousCentroids.isEmpty()){
				previousCentroids.addAll(centroids);
				//globalCounter++;				
			}
			else{
				if(previousCentroids.size() == centroids.size()){
					for(int i=0; i<centroids.size(); i++){
						if(previousCentroids.get(i).equals(centroids.get(i))){
							counterLocal = counterLocal + 1;
						}
					}
					if(counterLocal == centroids.size()){
						flag_finished = true;
						flag = false;
					}
					else{
						previousCentroids = new ArrayList<Double>();
						previousCentroids.addAll(centroids);
					//	globalCounter++;
					}
				}
				else{
					previousCentroids = new ArrayList<Double>();
					previousCentroids.addAll(centroids);
					//globalCounter++;
					
				}
			}
			//differenceMap = new HashMap<Integer, ArrayList<Double>>();	
			if(flag_finished == false){
				for(int i=0; i<partitionMap.size(); i++){
					clusterLocal = new ArrayList<Integer>();
					clusterLocal.addAll(partitionMap.get(i));
					for(int k=0; k<clusterLocal.size(); k++){
						differenceMap = new HashMap<Integer, Double>();
						shiftMap = new HashMap<Integer, Integer>();
						for(int j=0; j<centroids.size(); j++){				
							difference = Math.abs(centroids.get(j)-mtbfMap.get(clusterLocal.get(k)));													
							//difference = Math.abs(centroids.get(j)-ftaread.getMTBF(clusterLocal.get(k)));
							differenceMap.put(j, difference);
						}	
						difference = differenceMap.get(i);
						boolean flag_difference_exchange = false;
						int index = 0;
						for(int j=0; j<centroids.size(); j++){
							if(j == i){
								continue;
							}
							else{						
								if(difference > differenceMap.get(j)){
									difference = differenceMap.get(j);
									index = j;
									flag_difference_exchange = true;									
								}														
							}								
						}
						if(flag_difference_exchange == true){
							int nodeID;
							shiftMap.put(clusterLocal.get(k), index);
							nodeID = clusterLocal.get(k);
							clusterLocal.remove(k);
							partitionMap.remove(i);
							partitionMap.put(i, clusterLocal);
							clusterLocal = new ArrayList<Integer>();
							clusterLocal.addAll(partitionMap.get(index));
							clusterLocal.add(nodeID);
							partitionMap.remove(index);
							partitionMap.put(index, clusterLocal);
							flag_difference = true;							
						}	
						if(flag_difference == true){
							flag_second = true;
							break;
						}
					}
					if(flag_second == true){
						break;
					}				
				}
			}
		}
		//if(clusterName.equals("G1/site9/c1")){
		//	System.out.println("Check");
		//}
		locationWiseClustering.put(clusterName, partitionMap);			
	}
	
	public ArrayList<Integer>getCorrelatedNodes(String clusterName, int nodeID){
		HashMap<Integer, ArrayList<Integer>>partitionMapLocal = new HashMap<Integer, ArrayList<Integer>>();
		ArrayList<Integer>correlatedNodes = new ArrayList<Integer>();
		ArrayList<Integer>keyset = new ArrayList<Integer>();
		boolean flagLocal = false;
		partitionMapLocal = locationWiseClustering.get(clusterName);
		keyset.addAll(partitionMapLocal.keySet());
		for(int i=0; i<partitionMapLocal.size(); i++){
			correlatedNodes = new ArrayList<Integer>();
			correlatedNodes.addAll(partitionMapLocal.get(keyset.get(i)));
			for(int j=0; j<correlatedNodes.size(); j++){
				if(correlatedNodes.get(j).equals(nodeID)){
					flagLocal = true;
					break;
				}
			}
			if(flagLocal == true){
				break;
			}
		}
		return correlatedNodes;
	}
	
	
	public void k_means_withInitialCentroid(){}
}
