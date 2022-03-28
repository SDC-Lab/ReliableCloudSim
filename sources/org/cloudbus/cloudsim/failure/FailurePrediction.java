package org.cloudbus.cloudsim.failure;

import java.util.ArrayList;
import java.util.HashMap;

public class FailurePrediction {
	
	public HashMap<Integer, ArrayList<Double>>failurePredictionMap = new HashMap<Integer, ArrayList<Double>>();
	public ArrayList<Double>predictionList;
	
//	FailureDatacenter DC;
	ExponentialSmoothing expsmooth = new ExponentialSmoothing();	
	MovingAverage moveavg = new MovingAverage();
	
	public void setFTAReader(FTAFileReaderGrid5000 ftaread){
		if(RunTimeConstants.failurePredictionMethod == 1){
			expsmooth.setFTAReader(ftaread);
		}
		else{
			moveavg.setFTAReader(ftaread);
		}
	}	
	
	public void setFTAReader(FTAFileReader ftareadLANL){
		if(RunTimeConstants.failurePredictionMethod == 1){
			expsmooth.setFTAReader(ftareadLANL);
		}
		else{
			moveavg.setFTAReader(ftareadLANL);
		}
	}	

	public void predictFailures(int ftanodeID){	
		predictionList = new ArrayList<Double>();
		if(RunTimeConstants.failurePredictionMethod == 1){
			predictionList = expsmooth.exponentialSmoothing(ftanodeID);
		}
		else{
			predictionList = moveavg.movingAverage(ftanodeID);
		}
		failurePredictionMap.put(ftanodeID, predictionList);
	}
	
	public ArrayList<Double> getPredictedTBF(int node){				
		return failurePredictionMap.get(node); 
	}

}
