package org.cloudbus.cloudsim.failure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
public class FailurePredictionSynthetic {
	public HashMap<Integer, ArrayList<Double>>failurePredictionMap = new HashMap<Integer, ArrayList<Double>>();
	public ArrayList<Double>predictionList; 
	public ArrayList<Double>hostTBF;
	public ArrayList<Integer>predictionFlags;
	
	public double predictionError;	
	public double predictionErrorMaximum;
	FTAFileReaderGrid5000 ftareadGrid5000;
	FTAFileReader ftareadLANL;
	
	public boolean lockFlag;
	
	Random rand = new Random();
	
	public void setFTAReader(FTAFileReaderGrid5000 ftareadGrid5000){
		this.ftareadGrid5000 = ftareadGrid5000;
	}	
	
	public void setFTAReader(FTAFileReader ftareadLANL){
		this.ftareadLANL = ftareadLANL;
	}
	
	public void setLockFlag(boolean val){
		lockFlag = val;
	}

	public void predictFailures(int ftanodeID){
		predictionList = new ArrayList<Double>();
		hostTBF = new ArrayList<Double>();
		predictionFlags = new ArrayList<Integer>();
		if(lockFlag == false){
			predictionErrorMaximum = 100 - RunTimeConstants.predictionAccuracy;
			lockFlag = true;
		}		
		if(RunTimeConstants.traceType.equals("Grid5000")){
			hostTBF = ftareadGrid5000.getTBF(ftanodeID);
			predictionFlags = ftareadGrid5000.getHostPredictionFlagMap(ftanodeID);
		}
		if(RunTimeConstants.traceType.equals("LANL")){
			hostTBF = ftareadLANL.getTBF(ftanodeID);
			predictionFlags = ftareadLANL.getHostPredictionFlagMap(ftanodeID);
		}		
		double predictionTBF;
		for(int i=0; i<predictionFlags.size(); i++){
			//predictionError = predictionErrorMaximum + ((100 - RunTimeConstants.predictionAccuracy)*rand.nextDouble());
			predictionError = ((predictionErrorMaximum)*rand.nextDouble());
			if(predictionFlags.get(i) == 0){
				predictionTBF = hostTBF.get(i)-((predictionError * hostTBF.get(i))/100);
			}
			else{
				predictionTBF = hostTBF.get(i)+((predictionError * hostTBF.get(i))/100);
			}
			predictionList.add(predictionTBF);
		}		
		failurePredictionMap.put(ftanodeID, predictionList);
	}
	
	public ArrayList<Double> getPredictedTBF(int node){				
		return failurePredictionMap.get(node); 
	}

}
