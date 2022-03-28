package org.cloudbus.cloudsim.failure;

import java.util.ArrayList;

public class MovingAverage {

	public FTAFileReaderGrid5000 ftareadGrid5000;
	public FTAFileReader ftareadLANL;
	public ArrayList<Double>TBF;
	public ArrayList<Double>forecastedTBF;
	
	public void setFTAReader(FTAFileReaderGrid5000 ftareadGrid5000){
		this.ftareadGrid5000 = ftareadGrid5000;
	}
	
	public void setFTAReader(FTAFileReader ftareadLANL){
		this.ftareadLANL = ftareadLANL;
	}
	
	public ArrayList<Double> movingAverage(int node){
		TBF = new ArrayList<Double>();
		forecastedTBF = new ArrayList<Double>();
		if(RunTimeConstants.traceType.equals("Grid5000")){
			TBF = ftareadGrid5000.getTBF(node);		
		}
		if(RunTimeConstants.traceType.equals("LANL")){
			TBF = ftareadLANL.getTBF(node);
		}
		double windowSize = RunTimeConstants.windowSize;
		boolean flag_first = true;
		for(int i=0;i<=(TBF.size()-(windowSize-1));i++){
			double totalTBF = 0;
			double prediction;
			if(flag_first == true){
				if(i<(windowSize-1)){
					if(RunTimeConstants.traceType.equals("Grid5000")){
						forecastedTBF.add(ftareadGrid5000.getMTBF(node));	
					}
					if(RunTimeConstants.traceType.equals("LANL")){
						forecastedTBF.add(ftareadLANL.getMTBF(node));
					}			
				}
				else{
					for(int j=0; j<=i; j++){
						totalTBF = totalTBF + TBF.get(j);
					}
					prediction = totalTBF/windowSize;
					forecastedTBF.add(prediction);
					flag_first = false;
					i = 1;
				}			
			}
			else{
				for(int j=i-1; j<i+(windowSize-1); j++){
					totalTBF = totalTBF + TBF.get(j);
				}
				prediction = totalTBF/windowSize;
				forecastedTBF.add(prediction);
			}
		}
		return forecastedTBF;
	}
	
}
