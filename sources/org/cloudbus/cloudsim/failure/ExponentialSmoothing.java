package org.cloudbus.cloudsim.failure;

import java.util.ArrayList;

public class ExponentialSmoothing {

	FTAFileReaderGrid5000 ftareadGrid5000;
	FTAFileReader ftareadLANL;
	ArrayList<Double>TBF;
	ArrayList<Double>forecastedTBF;
	
	public void setFTAReader(FTAFileReaderGrid5000 ftareadGrid5000){
		this.ftareadGrid5000 = ftareadGrid5000;
	}
	
	public void setFTAReader(FTAFileReader ftareadLANL){
		this.ftareadLANL = ftareadLANL;
	}
	
	public ArrayList<Double> exponentialSmoothing(int node){
		TBF = new ArrayList<Double>();
		forecastedTBF = new ArrayList<Double>();
		if(RunTimeConstants.traceType.equals("Grid5000")){
			TBF = ftareadGrid5000.getTBF(node);
		}
		if(RunTimeConstants.traceType.equals("LANL")){
			TBF = ftareadLANL.getTBF(node);
		}
		double smoothingConstant = RunTimeConstants.smoothingConstant;
		for(int i=0;i<TBF.size();i++){
			if(i==0){
				if(RunTimeConstants.traceType.equals("Grid5000")){
					forecastedTBF.add(ftareadGrid5000.getMTBF(node));
				}
				if(RunTimeConstants.traceType.equals("LANL")){
					forecastedTBF.add(ftareadLANL.getMTBF(node));
				}				
			}
			else{
				double prediction;
				prediction = ((smoothingConstant)*(TBF.get(i-1)) + ((1-smoothingConstant)*forecastedTBF.get(i-1)));
				forecastedTBF.add(prediction);
			}
		}
		return forecastedTBF;
	}
	
}
