package org.cloudbus.cloudsim.failure;

public class ExecutionModel {
	
	public Double averageExecutionTime(int node, long cloudletLength, Double MTBF, Double MTTR){
		Double avgExecutionTime;
		
		avgExecutionTime = ((MTBF + MTTR)/MTBF)*(cloudletLength/1000);
		
		return(avgExecutionTime);
		
	}

}
