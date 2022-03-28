package org.cloudbus.cloudsim;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.lists.VmList;

public abstract class Broker extends SimEntity {

	public static int BILLING_TIME = 60 * 60;
	public static final int END_OF_BILLING_TIME = 300042;

	int internalVms;
	int datacenterId = -1;
	int cloudId = -1;

	protected int deadlineResources;
	protected int regularResources;

	int mips = Integer.parseInt(ExperimentProperties.MIPS_PERCORE.getProperty());

	protected List<Vm> vmsRequestedList;
	protected List<Integer> datacenterIdsList;
	protected List<Vm> localVmsList;

	protected HashMap<Integer, Job> jobMap;
	protected HashMap<Integer, Integer> cloudletJobMap;
	protected HashMap<Integer, Long> localUtilizationTable;

	protected JobGenerator generator;
	protected long totalJobs;
	protected long rejectedJobs;
	protected long missedDeadlines;
	protected long metDeadlines;
	protected long pendingJobs;
	protected long idx;
	protected boolean generatingJobs;

	public Broker(String name, String file, int internalVms) {
		super(name);
		this.internalVms = internalVms;
		this.vmsRequestedList = new ArrayList<Vm>();
		this.localVmsList = new ArrayList<Vm>();

		this.jobMap = new HashMap<Integer, Job>();
		this.cloudletJobMap = new HashMap<Integer, Integer>();
		this.localUtilizationTable = new HashMap<Integer, Long>();

		this.generator = new JobGenerator(file);

		this.totalJobs = 0;
		this.missedDeadlines = 0;
		this.metDeadlines = 0;
		this.rejectedJobs = 0;
		this.pendingJobs = 0;
		this.idx = 0;
		this.generatingJobs = true;

		this.regularResources = (int) (Math.ceil(internalVms
				* Double.parseDouble(ExperimentProperties.BROKER_REGULARSHARE.getProperty())));
		this.deadlineResources = internalVms - regularResources;
	}

	public abstract boolean admit(Job job);

	public abstract void scheduleRegular(Job job);

	public abstract void scheduleDeadline(Job job);

	public abstract void dispatch();

	public abstract void taskCompleted(Cloudlet task);

	public abstract void processRemoteVmCreate(Vm vm);

	public abstract void endOfBillingTime(int vmId);

	public abstract void printPolicySummary();

	@Override
	public int getId() {
		return super.getId();
	}

	public int getMips() {
		return mips;
	}

	@Override
	public void processEvent(SimEvent ev) {
		switch (ev.getTag()) {
		case CloudSimTags.RESOURCE_CHARACTERISTICS_REQUEST:
			processResourceCharacteristicsRequest(ev);
			break;
		case CloudSimTags.RESOURCE_CHARACTERISTICS:
			processResourceCharacteristics(ev);
			break;
		case CloudSimTags.VM_CREATE_ACK:
			processVmCreate(ev);
			break;
		case CloudSimTags.CLOUDLET_RETURN:
			processCloudletReturn((Cloudlet) ev.getData());
			break;
		case CloudSimTags.VM_BROKER_EVENT:
			processInternalEvent();
			break;
		case CloudSimTags.END_OF_SIMULATION:
			shutdownEntity();
			break;
		case END_OF_BILLING_TIME:
			processEndOfBillingTime(ev);
			break;
		default:
			processOtherEvent(ev);
		}
	}

	protected void processOtherEvent(SimEvent ev) {
		Log.printLine(getName() + ".processOtherEvent(): " + "Error - unknown event received. Tag="
				+ ev.getTag());
	}

	protected void processResourceCharacteristicsRequest(SimEvent ev) {
		datacenterIdsList = CloudSim.getCloudResourceList();

		Log.printLine(CloudSim.clock() + ": " + getName() + ": Resource List received with "
				+ datacenterIdsList.size() + " resource(s)");

		for (Integer datacenterId : datacenterIdsList) {
			sendNow(datacenterId, CloudSimTags.RESOURCE_CHARACTERISTICS, getId());
		}
	}

	protected void processResourceCharacteristics(SimEvent ev) {
		DatacenterCharacteristics characteristics = (DatacenterCharacteristics) ev.getData();

		if (characteristics.getResourceName().equalsIgnoreCase("Local")) {
			this.datacenterId = characteristics.getId();
		} else {
			this.cloudId = characteristics.getId();
		}

		if (this.cloudId != -1 && this.datacenterId != -1) {
			createVmsInDatacenter(this.datacenterId, this.internalVms, 0);
		}
	}

	protected void processVmCreate(SimEvent ev) {
		int[] data = (int[]) ev.getData();
		int datacenterId = data[0];
		int vmId = data[1];
		int result = data[2];

		Vm vm = VmList.getById(this.vmsRequestedList, vmId);
		vmsRequestedList.remove(vm);

		if (result == CloudSimTags.TRUE) {
			if (datacenterId == this.datacenterId) {
				processLocalVmCreate(vm);
			} else {
				processRemoteVmCreate(vm);
			}
		} else {
			Log.printLine(CloudSim.clock() + ": " + getName() + ": Creation of VM #" + vmId
					+ " failed in Datacenter #" + datacenterId);
		}
	}

	protected void processLocalVmCreate(Vm vm) {
		this.localVmsList.add(vm);
		Log.printLine(CloudSim.clock() + ": " + getName() + ": VM #" + vm.getId()
				+ " has been created in Datacenter #" + datacenterId + ", Host #" + vm.getHost().getId());

		// all the requested VMs have been created
		if (localVmsList.size() == this.internalVms) {
			generateLoad();
		}
	}

	protected void processCloudletReturn(Cloudlet cloudlet) {
		int jobId = this.cloudletJobMap.get(cloudlet.getCloudletId());
		Job job = jobMap.get(jobId);

		job.taskCompleted((int) CloudSim.clock());

		if (job.isJobFinished()) {
			this.pendingJobs--;

			if (!job.isJobRegular()) {// check deadline
				if (job.isDeadlineMissed()) {
					this.missedDeadlines++;
					System.out.println(CloudSim.clock() + ": Missed deadline for job " + job.getJobId()
							+ " (deadline=" + job.getDeadline() + ")");
				} else {
					this.metDeadlines++;
					System.out.println(CloudSim.clock() + ": Met deadline for job " + job.getJobId()
							+ " (deadline=" + job.getDeadline() + ")");
				}
			}
		}

		// update resources utilization of this user
		long utilization = (long) Math.ceil(cloudlet.getFinishTime() - cloudlet.getExecStartTime());
		utilization += this.localUtilizationTable.get(job.getUserId());
		this.localUtilizationTable.put(job.getUserId(), utilization);

		// MIGHT CAUSE PROBLEMS...
		this.cloudletJobMap.remove(cloudlet);

		taskCompleted(cloudlet);
		dispatch();
	}

	public void processEndOfBillingTime(SimEvent ev) {
		int vmId = (Integer) ev.getData();
		endOfBillingTime(vmId);
	}

	protected void createVmsInDatacenter(int datacenterId, int vms, int startIdx) {
		String datacenterName = CloudSim.getEntityName(datacenterId);

		int mips = Integer.parseInt(ExperimentProperties.MIPS_PERCORE.getProperty());
		int pes = 1;
		int pesPerHost = Integer.parseInt(ExperimentProperties.CORES_PERHOST.getProperty());
		int ramPerHost = Integer.parseInt(ExperimentProperties.MEMORY_PERHOST.getProperty());
		int ram = ramPerHost / pesPerHost;

		int vmId = startIdx;
		for (int i = 0; i < vms; i++) {
			Vm vm = new Vm(
					vmId,
					this.getId(),
					mips,
					pes,
					ram,
					1000,
					1000,
					"Xen",
					new CloudletSchedulerTimeShared());
			this.vmsRequestedList.add(vm);

			Log.printLine(CloudSim.clock() + ": " + getName() + ": Trying to Create VM #" + vm.getId()
					+ " in " + datacenterName);
			sendNow(datacenterId, CloudSimTags.VM_CREATE_ACK, vm);
			vmId++;
		}
	}

	protected void processInternalEvent() {
		generateLoad();
	}

	protected void generateLoad() {
		if (generatingJobs) {
			double currentTime = CloudSim.clock();
			scheduleJob(generator.nextJob(currentTime));
			double delay = generator.delayToNextEvent(currentTime);
			if (delay > 0.0) {
				send(getId(), delay, CloudSimTags.VM_BROKER_EVENT);
			} else {
				generatingJobs = false;
				if (pendingJobs == 0) {
					Log.printLine(CloudSim.clock() + ": " + getName()
							+ ": All requests executed. Finishing...");
					finishExecution();
				}
			}
		}
	}

	protected void scheduleJob(Job nextJob) {
		this.totalJobs++;
		// first: admission control
		this.jobMap.put(nextJob.getJobId(), nextJob);
		if (!nextJob.isJobRegular() && !admit(nextJob)) {
			this.jobMap.remove(nextJob.getJobId());
			this.rejectedJobs++;
			return;
		}

		if (!this.localUtilizationTable.containsKey(nextJob.getUserId())) {
			this.localUtilizationTable.put(nextJob.getUserId(), 0L);
		}

		// select the queue the tasks have to go
		if (nextJob.isJobRegular()) {
			scheduleRegular(nextJob);
		} else {
			scheduleDeadline(nextJob);
		}

		pendingJobs++;

		// last: dispatch
		dispatch();
	}

	public void printExecutionSummary() {
		DecimalFormat dft = new DecimalFormat("#####.00");
		DecimalFormat percent = new DecimalFormat("##0.00");
		String indent = "\t";

		double sumOfSqrtRespTime = 0.0;
		double sumOfResponseTime = 0.0;
		Log.printLine("ID  " + indent + "Submission Time" + indent + indent + "Deadline" + indent + indent
				+ "Finish Time");
		for (Job job : jobMap.values()) {
			Log.print(" " + job.getJobId() + indent);

			double serviceTime = job.getCompletionTime() - job.getSubmissionTime();
			sumOfResponseTime += serviceTime;
			sumOfSqrtRespTime += (serviceTime * serviceTime);
			Log.printLine(indent + dft.format(job.getSubmissionTime()) + indent + indent + job.getDeadline()
					+ indent + indent + job.getCompletionTime());
		}
		Log.printLine();

		Log.printLine("========== BROKER SUMMARY ==========");
		Log.printLine("= Submitted: " + this.totalJobs);

		Log.printLine("= Rejected: " + this.rejectedJobs);

		long completed = this.totalJobs - this.rejectedJobs;
		Log.printLine("= Completed: " + completed);

		double completionRate = completed / (double) this.totalJobs;
		Log.printLine("= CompletionRate: " + percent.format(completionRate));

		double rejectionRate = this.rejectedJobs / (double) this.totalJobs;
		Log.printLine("= RejectionRate: " + percent.format(rejectionRate));

		long qosJobs = this.rejectedJobs + this.metDeadlines + this.missedDeadlines;
		Log.printLine("= Qos: " + qosJobs);

		long validQos = this.metDeadlines + this.missedDeadlines;
		Log.printLine("= Met: " + this.metDeadlines);
		double deadlinesMet = this.metDeadlines / (double) validQos;
		Log.printLine("= DeadlineMetRate: " + percent.format(deadlinesMet));

		Log.printLine("= Missed: " + this.missedDeadlines);
		double deadlinesMissed = this.missedDeadlines / (double) validQos;
		Log.printLine("= DeadlineMissedRate: " + percent.format(deadlinesMissed));

		Set<Entry<Integer, Long>> entries = localUtilizationTable.entrySet();
		Log.printLine("== user  == local utilization time (h) ==");
		double totalUtilization = 0.0;
		for (Entry<Integer, Long> entry : entries) {
			double use = entry.getValue() / 3600.0; // utilization in hours
			Log
					.printLine("==" + entry.getKey() + indent + " == " + dft.format(use) + indent + indent
							+ " ==");
			totalUtilization += use;
		}
		Log.printLine("===================================");

		Log.printLine("= LocalUtilization(h): " + dft.format(totalUtilization));

		printPolicySummary();

		Log.printLine("========== END OF SUMMARY =========");
	}

	protected void finishExecution() {
		sendNow(getId(), CloudSimTags.END_OF_SIMULATION);
	}

	@Override
	public void shutdownEntity() {
		Log.printLine(getName() + " is shutting down...");
	}

	@Override
	public void startEntity() {
		Log.printLine(getName() + " is starting...");
		schedule(getId(), 2, CloudSimTags.RESOURCE_CHARACTERISTICS_REQUEST);
	}
}
