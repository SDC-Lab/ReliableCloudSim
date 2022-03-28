package org.cloudbus.cloudsim;

public class Job {
	
	int jobId;
	int userId;
	long deadline;
	int tasks;
	long actualDuration;
	long estimatedDuration;
	int completedTasks;
	long submissionTime;
	long completionTime;
	boolean deadlineMissed;
	
	public Job(int jobId, int userId, long deadline, int tasks, long actualDuration, long estimatedDuration, long submissionTime) {
		super();
		this.jobId = jobId;
		this.userId = userId;
		this.deadline = deadline;
		this.tasks = tasks;
		this.actualDuration = actualDuration;
		this.estimatedDuration = estimatedDuration;
		this.submissionTime = submissionTime;
		this.completedTasks = 0;
		this.deadlineMissed = false;
	}
	
	public void taskCompleted(int currentTime) {
		if (completedTasks<tasks){
			this.completedTasks++;
			
			//if finished, fill statistics
			if(tasks==completedTasks){
				this.completionTime = currentTime;
				if (currentTime>deadline) this.deadlineMissed = true;
			}
		}
	}
	
	public boolean isJobRegular(){
		return (deadline==-1);
	}
	
	public boolean isJobFinished(){
		return (tasks==completedTasks);
	}
	
	public long getRuntime(){
		return isJobFinished()?completionTime-submissionTime:-1;
	}
	
	public boolean isDeadlineMissed(){
		return this.deadlineMissed;
	}

	public int getJobId() {
		return jobId;
	}

	public int getUserId() {
		return userId;
	}

	public long getDeadline() {
		return deadline;
	}

	public int getTasks() {
		return tasks;
	}

	public long getActualDuration() {
		return actualDuration;
	}

	public long getEstimatedDuration() {
		return estimatedDuration;
	}
	
	public long getSubmissionTime() {
		return completionTime;
	}
	
	public long getCompletionTime() {
		return completionTime;
	}
}
