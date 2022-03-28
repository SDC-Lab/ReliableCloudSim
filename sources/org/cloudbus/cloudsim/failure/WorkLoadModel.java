package org.cloudbus.cloudsim.failure;


import java.util.ArrayList;

import org.cloudbus.cloudsim.distributions.LognormalDistr;

public class WorkLoadModel {
	public int bagSize;
	public double dataRate;
	public double previousDataRate = 0;
	public int counter_bags = 0;
	public int counter_cloudlets = 0;
	public int counter_finishedCloudlets = 0;
	public int total_cloudlets = 0;
	public int cletJump = 0;
	public LognormalDistr taskRunTime; 	
	
	public ArrayList<Long>cletLength;
	public int count = 1;
	
	public int getBagSize(int botID){	
		bagSize = RunTimeConstants.bagSize.get(counter_bags);
		counter_bags++;			
		return bagSize;
		
	}
	
	public double getDataRate(){		
		dataRate = RunTimeConstants.dataRate;		
		return dataRate;
	}
	
	public void setPreviousDataRate(double dataRate){		
		previousDataRate = previousDataRate + dataRate;		
	}
	
	public double getPreviousDataRate(){
		return previousDataRate;
	}
	
	public ArrayList<Long> getCloudlets(int cletNumber){
		cletLength = new ArrayList<Long>();		
		/*
		taskRunTime = new LognormalDistr(RunTimeConstants.taskRunTimeMean, RunTimeConstants.taskRunTimeDeviation);
		for(int i=0; i<cletNumber; i++){
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
			cletSize = cletSize * 60 * 1000;
			cletLength.add((long)cletSize);			
		}
		*/
				
		for(int i=cletJump;i<(cletNumber+cletJump);i++){
			 cletLength.add(RunTimeConstants.cloudletLength.get(i));
		}					
		cletJump = cletJump + cletNumber;
		count = count + 1;	
		return cletLength;
	}
	
	public void setTotalCloudlets(){
		total_cloudlets = counter_cloudlets;
	}
	
	public void setFinishedCloudlets(){
		counter_finishedCloudlets = counter_finishedCloudlets + 1;
	}
	public int getTotalCloudlets(){
		return total_cloudlets;
	}
	
	public int getFinishedCloudlets(){
		return counter_finishedCloudlets;
	}
}
