package org.cloudbus.cloudsim;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.lists.VmList;

public class HeftJobDpBroker extends Broker {

	ArrayList<Cloudlet> regularQueue;
	ArrayList<Vm> provisionedVms;

	// task on each resource
	HashSet<Integer> freeResources;
	HashMap<Integer, Long> estimatedWaitingTime;
	HashMap<Integer, ArrayList<Cloudlet>> deadlineQueueMap;
	Hashtable<Cloudlet, CloudletDeadlineInfo> cloudletInfo;

	// ////////////////// DP data structures
	HashMap<Integer, ArrayList<Cloudlet>> externalQueueMap;
	HashMap<Integer, Integer> vmIdToUserIdMap;
	HashMap<Integer, Long> cloudUtilizationTable;
	HashMap<Integer, Long> nextBillingPerVm;
	HashMap<Integer, Integer> cloudVmToTaskMap;
	int dynamicProvisionIdx;
	int vmCreationDelay;

	int maxVMsPerUser;

	int clId;

	public HeftJobDpBroker(String name, String file, int internalVms) {
		super(name, file, internalVms);
		provisionedVms = new ArrayList<Vm>();
		regularQueue = new ArrayList<Cloudlet>();
		freeResources = new HashSet<Integer>();

		vmIdToUserIdMap = new HashMap<Integer, Integer>();
		externalQueueMap = new HashMap<Integer, ArrayList<Cloudlet>>();
		cloudUtilizationTable = new HashMap<Integer, Long>();
		nextBillingPerVm = new HashMap<Integer, Long>();
		cloudVmToTaskMap = new HashMap<Integer, Integer>();
		cloudletInfo = new Hashtable<Cloudlet, CloudletDeadlineInfo>();
		estimatedWaitingTime = new HashMap<Integer, Long>();

		dynamicProvisionIdx = internalVms;

		for (int i = 0; i < internalVms; i++) {
			freeResources.add(i);
		}

		deadlineQueueMap = new HashMap<Integer, ArrayList<Cloudlet>>();
		for (int i = 0; i < deadlineResources; i++) {
			deadlineQueueMap.put(i, new ArrayList<Cloudlet>());
			updateEstimatedWaitingTime(i, 0);
		}

		maxVMsPerUser = Integer.parseInt(ExperimentProperties.BROKER_MAXVMSPERUSER.getProperty());
		vmCreationDelay = Integer.parseInt(ExperimentProperties.VM_CREATION_DELAY.getProperty());
		clId = 0;
	}

	public CloudletDeadlineInfo getCloudletDeadlineInfo(Cloudlet cl) {
		return cloudletInfo.get(cl);
	}

	@Override
	public boolean admit(Job job) {
		return true;
	}

	@Override
	public void scheduleRegular(Job job) {
		// generate cloudlet, fill cloudletJobMap
	}

	@Override
	/**
	 * generate cloudlet, fill cloudletJobMap
	 */
	public void scheduleDeadline(Job job) {

		int requiredRes = requiredExternalResources(job);

		if (requiredRes == 0) {
			// job runs locally
			System.out.println(CloudSim.clock() + ": Job " + job.getJobId() + " runs locally.");
			for (int i = 0; i < job.getTasks(); i++) {
				Cloudlet cl = new Cloudlet(
						clId,
						job.getRuntime() * getMips(),
						1,
						1000,
						1000,
						new UtilizationModelFull(),
						new UtilizationModelFull(),
						new UtilizationModelFull());
				cloudletJobMap.put(clId, job.getJobId());
				clId++;
				cloudletInfo.put(cl, new CloudletDeadlineInfo(job.getDeadline(), job.getEstimatedDuration()));
				// HEFT: sort tasks by decreasing rank. For BoT, rank is given by average estimated runtime of task
				//assign the task to the vm that minimizes EFT (estimated runtime plus available time of vm)
				int id = getSmallerQueueId();

				ArrayList<Cloudlet> list = deadlineQueueMap.get(id);
				list.add(cl);

				// update earliest start time
				updateEstimatedWaitingTime(id, job.getEstimatedDuration());
			}
		} else {
			// job runs on cloud
			System.out.println(CloudSim.clock() + ": Job " + job.getJobId() + " runs on the Cloud.");
			// 1. put tasks on queue
			ArrayList<Cloudlet> queue;
			if (!externalQueueMap.containsKey(job.getUserId())) {
				queue = new ArrayList<Cloudlet>();
			} else {
				queue = externalQueueMap.get(job.getUserId());
			}

			for (int i = 0; i < job.getTasks(); i++) {
				Cloudlet cl = new Cloudlet(
						clId,
						job.getRuntime() * getMips(),
						1,
						1000,
						1000,
						new UtilizationModelFull(),
						new UtilizationModelFull(),
						new UtilizationModelFull());
				cloudletInfo.put(cl, new CloudletDeadlineInfo(job.getDeadline(), job.getEstimatedDuration()));
				cloudletJobMap.put(clId, job.getJobId());
				clId++;

				queue.add(cl);
			}
			Collections.sort(queue, new ReverseLagTimeComparator(this));
			externalQueueMap.put(job.getUserId(), queue);

			// 2. asks for vms
			provision(requiredRes, job.getUserId());
		}
	}

	protected int resourcesUserHas(int userId) {
		int cont = 0;
		Collection<Integer> values = vmIdToUserIdMap.values();
		Iterator<Integer> iter = values.iterator();
		while (iter.hasNext()) {
			int user = iter.next();
			if (user == userId) {
				cont++;
			}
		}
		return cont;
	}

	protected void provision(int requiredRes, int user) {
		if (provisionedVms.size() + requiredRes <= internalVms) {
			super.createVmsInDatacenter(cloudId, requiredRes, dynamicProvisionIdx);
			if (!cloudUtilizationTable.containsKey(user)) {
				cloudUtilizationTable.put(user, 0L);
			}
			System.out.println(CloudSim.clock() + ": Requesting " + requiredRes + " for user " + user);
			for (int i = 0; i < requiredRes; i++) {
				vmIdToUserIdMap.put(dynamicProvisionIdx, user);
				long time = (int) CloudSim.clock();
				nextBillingPerVm.put(dynamicProvisionIdx, time + BILLING_TIME);
				super.send(super.getId(), BILLING_TIME, END_OF_BILLING_TIME, dynamicProvisionIdx);
				dynamicProvisionIdx++;
			}
		}
	}

	protected int requiredExternalResources(Job job) {
		int currentTime = (int) Math.ceil(CloudSim.clock());
		long deadline = job.getDeadline();
		// can it run locally?
		int tasksLeft = job.getTasks();
		for (long estimation : estimatedWaitingTime.values()) {// for each
			// resource
			// estimated
			// free time
			long timeToDeadline = deadline - estimation;
			if (timeToDeadline > 0) {// this resource can be used to this job
				// how many tasks can be executed before the deadline?
				long rounds = timeToDeadline / job.getEstimatedDuration();
				tasksLeft -= rounds;
			}
			if (tasksLeft <= 0) {
				break;
			}
		}

		if (tasksLeft <= 0) {
			return 0;
		}

		// if the whole job cannot run locally, it goes to the cloud
		long timeToDeadline = deadline - currentTime - vmCreationDelay;

		// how many execution rounds are left before the deadline?
		int rounds = (int) (timeToDeadline / job.getEstimatedDuration());

		if (rounds <= 0) { // not enough time to complete execution: 1 vm per
			// task
			return job.getTasks();
		}

		// number of rounds gives the number of tasks to run on each cloud
		// resource
		int requiredResources = job.getTasks() / rounds;
		if (job.getTasks() % rounds > 0) {
			requiredResources++;
		}

		return requiredResources;
	}

	protected int getSmallerQueueId() {
		int key = -1;
		for (Map.Entry<Integer, Long> entry : estimatedWaitingTime.entrySet()) {
			key = entry.getKey();
			break;
		}
		return key;
	}

	protected void updateEstimatedWaitingTime(int queueId, long estimatedDuration) {

		if (!estimatedWaitingTime.containsValue(queueId)) {
			// new resource is joining. Just use the value received in the
			// method
			estimatedWaitingTime.put(queueId, estimatedDuration);
		} else {
			// resource is there already. Sum the current waitingTime with the
			// estimated duration
			long time = estimatedWaitingTime.get(queueId);
			time += estimatedDuration;
			estimatedWaitingTime.put(queueId, time);
		}
		estimatedWaitingTime = (HashMap<Integer, Long>) MapUtil.sortByValue(estimatedWaitingTime);
	}

	@Override
	public void dispatch() {

		ArrayList<Integer> toRemove = new ArrayList<Integer>();

		Iterator<Integer> iter = freeResources.iterator();
		while (iter.hasNext()) {// for each free resource...
			int resource = iter.next();

			// is the resource local or deadlined?
			Cloudlet cl = null;

			if (resource < this.regularResources) {// normal resource
				// first try a normal resource
				if (!regularQueue.isEmpty()) {
					cl = regularQueue.remove(0);
				} else {// no more regular tasks; run a deadlined
					cl = getNextDeadlineCloudlet(resource);
				}
			} else {// deadline resource
				// first, try deadline resource pool
				cl = getNextDeadlineCloudlet(resource);

				if (cl == null && !regularQueue.isEmpty()) {// no more
					// deadlined; try a
					// regular
					cl = regularQueue.remove(0);
				}
			}

			// no more tasks to run
			if (cl == null) {
				break;
			}

			int vmId = localVmsList.get(resource).getId();
			cl.setVmId(vmId);

			cl.setUserId(getId());
			System.out.println(CloudSim.clock() + ": Sending task" + cl.getCloudletId() + " to resource "
					+ vmId);
			super.send(super.datacenterId, 0.2, CloudSimTags.CLOUDLET_SUBMIT, cl);

			toRemove.add(resource);
		}

		freeResources.removeAll(toRemove);
	}

	protected Cloudlet getNextDeadlineCloudlet(int resId) {
		// Selects the task with smallest lag to execute
		Cloudlet cl = null;

		if (this.deadlineQueueMap.containsKey(resId)) {
			// this is a deadline resource. Check the queue of the resource
			ArrayList<Cloudlet> queue = deadlineQueueMap.get(resId);
			if (!queue.isEmpty()) {
				cl = queue.remove(0);

				int jobId = super.cloudletJobMap.get(cl.getCloudletId());
				long duration = super.jobMap.get(jobId).getEstimatedDuration();
				updateEstimatedWaitingTime(resId, 0 - duration);
				return cl;
			}
		}

		// either the right queue is empty or free resource is regular
		// select the earliest lag resource, if any
		Set<Entry<Integer, ArrayList<Cloudlet>>> entries = deadlineQueueMap.entrySet();
		int res = -1;

		long currentTime = (long) CloudSim.clock();

		long smallestLag = Long.MAX_VALUE;
		ArrayList<Cloudlet> smallestLagQueue = null;

		Iterator<Entry<Integer, ArrayList<Cloudlet>>> iter = entries.iterator();
		while (iter.hasNext()) {
			Entry<Integer, ArrayList<Cloudlet>> entry = iter.next();

			ArrayList<Cloudlet> queue = entry.getValue();
			if (!queue.isEmpty()) {
				int cloudletId = queue.get(0).getCloudletId();
				int jobId = cloudletJobMap.get(cloudletId);
				// long deadline = jobMap.get(jobId).getDeadline();
				Job j = jobMap.get(jobId);
				long deadline = j.getDeadline();

				long ert = jobMap.get(cloudletJobMap.get(cloudletId)).getEstimatedDuration();

				long lag = deadline - currentTime - ert;

				if (lag < smallestLag) {
					smallestLag = lag;
					smallestLagQueue = queue;
					res = entry.getKey();
				}
			}
		}

		if (smallestLagQueue != null) {
			cl = smallestLagQueue.remove(0);

			int jobId = super.cloudletJobMap.get(cl.getCloudletId());
			long duration = super.jobMap.get(jobId).getEstimatedDuration();

			updateEstimatedWaitingTime(res, 0 - duration);
		}

		return cl;
	}

	protected void dispatchRemote(int userId, int resourceId) {
		// find tasks from this user
		ArrayList<Cloudlet> queue = externalQueueMap.get(userId);

		// is there tasks left?
		if (!queue.isEmpty()) {
			Cloudlet cl = queue.remove(0);
			cl.setVmId(resourceId);
			cloudVmToTaskMap.put(resourceId, cl.getCloudletId());

			cl.setUserId(getId());
			super.send(super.cloudId, 0.0, CloudSimTags.CLOUDLET_SUBMIT, cl);
			System.out.println(CloudSim.clock() + ": Sending task" + cl.getCloudletId() + " to resource "
					+ resourceId);
		}
	}

	@Override
	public void taskCompleted(Cloudlet task) {

		int vmId = task.getVmId();
		System.out.println(CloudSim.clock() + ": Task completed: " + task.getCloudletId());
		// first, check if this is local or dp task
		Vm vm = VmList.getById(localVmsList, vmId);

		if (vm != null) {
			int id = localVmsList.indexOf(vm);
			// resourceToTask.remove(id);
			freeResources.add(id);
			return;
		}

		// this is a dp task. Find the owner
		int userId = vmIdToUserIdMap.get(vmId);

		// external vm is now free
		cloudVmToTaskMap.remove(vmId);

		long remoteUse = (long) (task.getFinishTime() - task.getExecStartTime());
		long totalUse = this.localUtilizationTable.get(userId);
		// in this table, we count only time using local resources
		// because time for remote processing has been added by
		// the broker, we subtract it here.
		this.localUtilizationTable.put(userId, totalUse - remoteUse);

		cloudletInfo.remove(task);

		// execute a new task
		dispatchRemote(userId, vmId);
	}

	@Override
	public void processRemoteVmCreate(Vm vm) {
		// store the vm
		provisionedVms.add(vm);
		System.out.println(CloudSim.clock() + ": New VM available: " + vm.getId());
		// find owner of the vm
		int userId = vmIdToUserIdMap.get(vm.getId());

		// machine is ready to run tasks!
		dispatchRemote(userId, vm.getId());
	}

	@Override
	public void endOfBillingTime(int vmId) {
		Log.printLine(CloudSim.clock() + ": End of billing time for vm " + vmId);
		int userId = this.vmIdToUserIdMap.get(vmId);
		long utilization = this.cloudUtilizationTable.get(userId);
		utilization += 3600;
		this.cloudUtilizationTable.put(userId, utilization);

		int unsubmittedTasks = externalQueueMap.get(userId).size();
		int toDestroy = resourcesUserHas(userId) - unsubmittedTasks;

		if (!cloudVmToTaskMap.containsKey(vmId) && toDestroy > 0) {// if machine
			// is idle
			// and
			// toDestroy>0,
			// release
			// the VM
			Vm vm = VmList.getById(provisionedVms, vmId);
			sendNow(super.cloudId, CloudSimTags.VM_DESTROY, vm);
			vmIdToUserIdMap.remove(vmId);
			provisionedVms.remove(vm);
			nextBillingPerVm.remove(vmId);
		}
	}

	@Override
	public void printPolicySummary() {

		DecimalFormat dft = new DecimalFormat("#####.00");
		String indent = "\t";

		Set<Entry<Integer, Long>> entries = cloudUtilizationTable.entrySet();
		Log.printLine("== user  == Cloud utilization time (h) ==");
		double totalUtilization = 0.0;
		for (Entry<Integer, Long> entry : entries) {
			double use = entry.getValue() / 3600.0; // utilization in hours
			Log
					.printLine("==" + entry.getKey() + indent + " == " + dft.format(use) + indent + indent
							+ " ==");
			totalUtilization += use;
		}
		Log.printLine("===================================");

		Log.printLine("= CloudUtilization(h): " + dft.format(totalUtilization));
	}
}
