package org.cloudbus.cloudsim;

public class CloudletDeadlineInfo {
	
	long deadline;
	long estimatedRunTime;
	
	public CloudletDeadlineInfo(long deadline, long estimatedRunTime) {
		this.deadline = deadline;
		this.estimatedRunTime = estimatedRunTime;
	}

	public long getDeadline() {
		return deadline;
	}

	public long getEstimatedRunTime() {
		return estimatedRunTime;
	}
}
