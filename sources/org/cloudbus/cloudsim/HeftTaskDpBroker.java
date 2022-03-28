package org.cloudbus.cloudsim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.lists.VmList;

public class HeftTaskDpBroker extends HeftJobDpBroker {

	Hashtable<Integer, Long> lastBillingUpdate;

	public HeftTaskDpBroker(String name, String file, int internalVms) {
		super(name, file, internalVms);
		lastBillingUpdate = new Hashtable<Integer, Long>();
	}

	@Override
	public CloudletDeadlineInfo getCloudletDeadlineInfo(Cloudlet cl) {
		return cloudletInfo.get(cl);
	}

	@Override
	public void scheduleDeadline(Job job) {
		ArrayList<Cloudlet> toCloud = new ArrayList<Cloudlet>();

		// schedules as many tasks as possible locally
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
			long earlierStartTime = estimatedWaitingTime.get(id);
			long estimatedFinishTime = earlierStartTime + job.getEstimatedDuration();
			if (estimatedFinishTime + 120 > job.getDeadline()) {// task goes to the cloud if VM boot time does not compromise deadline
				toCloud.add(cl);
			} else { // cloud runs locally
				ArrayList<Cloudlet> list = deadlineQueueMap.get(id);
				list.add(cl);

				// update earliest start time
				updateEstimatedWaitingTime(id, job.getEstimatedDuration());
			}
		}

		// define number of resources necessary for the remaining cloudlets
		// put tasks on queue
		if (!toCloud.isEmpty()) {
			ArrayList<Cloudlet> queue;
			if (!externalQueueMap.containsKey(job.getUserId())) {
				queue = new ArrayList<Cloudlet>();
			} else {
				queue = externalQueueMap.get(job.getUserId());
			}

			queue.addAll(toCloud);
			Collections.sort(queue, new ReverseLagTimeComparator(this));

			externalQueueMap.put(job.getUserId(), queue);

			// --------------- calculate required resources -------------
			int currentTime = (int) Math.ceil(CloudSim.clock());
			long deadline = job.getDeadline();

			long timeToDeadline = deadline - currentTime - vmCreationDelay;

			if (timeToDeadline > 0) {
				// how many execution rounds are left before the deadline?
				int rounds = (int) (timeToDeadline / job.getEstimatedDuration());

				if (rounds > 0) {
					// number of rounds gives the number of tasks to run on each
					// cloud resource
					int requiredResources = toCloud.size() / rounds;
					if (job.getTasks() % rounds > 0) {
						requiredResources++;
					}
					provision(requiredResources, job.getUserId());
				} else {
					// less than 1 round left
					int packageSize = (int) (3600 / job.getEstimatedDuration());
					if (packageSize <= 0) {
						packageSize = 1;
					}
					int number = toCloud.size() / packageSize;
					if (number < 0) {
						number = 0;
					}

					provision(number, job.getUserId());
				}
			} else {
				provision(toCloud.size(), job.getUserId());
			}
		}
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
				// first try a normal task
				if (!regularQueue.isEmpty()) {
					cl = regularQueue.remove(0);
				} else {// no more regular tasks; run a deadline task
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

			if (cl == null) {// only cloud tasks are left; take one to run
				cl = getNextExternalCloudlet(resource);
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

	protected Cloudlet getNextExternalCloudlet(int resource) {
		Cloudlet cl = null;

		long currentTime = (long) CloudSim.clock();

		// look for the task with shortest lag time to execute
		Set<Entry<Integer, ArrayList<Cloudlet>>> entries = externalQueueMap.entrySet();
		int res = -1;

		long smallestLag = Long.MAX_VALUE;
		ArrayList<Cloudlet> smallestLagQueue = null;

		Iterator<Entry<Integer, ArrayList<Cloudlet>>> iter = entries.iterator();
		while (iter.hasNext()) {
			Entry<Integer, ArrayList<Cloudlet>> entry = iter.next();

			ArrayList<Cloudlet> queue = entry.getValue();
			if (!queue.isEmpty()) {
				int cloudletId = queue.get(0).getCloudletId();
				int jobId = cloudletJobMap.get(cloudletId);
				long deadline = jobMap.get(jobId).getDeadline();

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

	@Override
	protected void dispatchRemote(int userId, int resourceId) {
		// find tasks from this user
		ArrayList<Cloudlet> queue = externalQueueMap.get(userId);
		Cloudlet cl = null;

		// is there tasks left from this user?
		if (!queue.isEmpty()) {
			cl = queue.remove(0);
		} else { // no more cloud tasks; looks for a deadline task from this
			// user
			cl = nextCloudletFromUser(userId);

			if (cl == null) {// no deadline tasks from this user. Try a regular
				// one
				for (Cloudlet regCl : regularQueue) {
					int clId = regCl.getCloudletId();
					long jobId = cloudletJobMap.get(clId);
					if (jobMap.get(jobId).getUserId() == userId) {
						cl = regCl;
						break;
					}
				}
				if (cl != null) {
					regularQueue.remove(cl);
				}
			}
		}

		if (cl == null) {// no more tasks from this user at all; get a deadline
			// task from another user
			cl = super.getNextDeadlineCloudlet(resourceId);
			if (cl != null) {// deadline cloudlet found; update resource
				// ownership
				// update usage from previous user
				long currentTime = (long) CloudSim.clock();
				long timeOfLastUpdate = lastBillingUpdate.get(resourceId);

				long interval = currentTime - timeOfLastUpdate;
				long utilization = this.cloudUtilizationTable.get(userId);
				utilization += interval;
				this.cloudUtilizationTable.put(userId, utilization);
				lastBillingUpdate.put(resourceId, currentTime);

				// change ownership
				int newUser = jobMap.get(cloudletJobMap.get(cl.getCloudletId())).getUserId();
				vmIdToUserIdMap.put(resourceId, newUser);
			} else {// no more deadlined tasks at all; run a regular one; in
				// this case don't change ownership
				if (!regularQueue.isEmpty()) {
					cl = regularQueue.remove(0);
				}
			}
		}

		if (cl != null) {
			cl.setVmId(resourceId);
			cloudVmToTaskMap.put(resourceId, cl.getCloudletId());
			cl.setUserId(getId());
			System.out.println(CloudSim.clock() + ": Sending task" + cl.getCloudletId() + " to resource "
					+ resourceId);
			super.send(super.cloudId, 0.0, CloudSimTags.CLOUDLET_SUBMIT, cl);
		}
	}

	protected Cloudlet nextCloudletFromUser(int userId) {
		// Selects the task with smallest lag to execute, from this user
		Cloudlet cl = null;

		Set<Entry<Integer, ArrayList<Cloudlet>>> entries = deadlineQueueMap.entrySet();
		int res = -1;

		long currentTime = (long) CloudSim.clock();

		long smallestLag = Long.MAX_VALUE;
		ArrayList<Cloudlet> smallestLagQueue = null;

		Iterator<Entry<Integer, ArrayList<Cloudlet>>> iter = entries.iterator();
		queueIter: while (iter.hasNext()) {
			// finds each candidate from a queue
			Entry<Integer, ArrayList<Cloudlet>> entry = iter.next();

			ArrayList<Cloudlet> queue = entry.getValue();
			for (Cloudlet cloudlet : queue) {
				int clId = cloudlet.getCloudletId();
				long jobId = cloudletJobMap.get(clId);
				if (jobMap.get(jobId).getUserId() == userId) {
					long deadline = jobMap.get(jobId).getDeadline();
					long ert = jobMap.get(jobId).getEstimatedDuration();
					long lag = deadline - currentTime - ert;
					if (lag < smallestLag) {
						smallestLag = lag;
						smallestLagQueue = queue;
						res = entry.getKey();
					}
					// we already found the candidate of this queue; go to the
					// next
					continue queueIter;
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

	@Override
	public void processRemoteVmCreate(Vm vm) {
		super.processRemoteVmCreate(vm);
		lastBillingUpdate.put(vm.getId(), (long) CloudSim.clock());
	}

	@Override
	public void endOfBillingTime(int vmId) {
		Log.printLine(CloudSim.clock() + ": End of billing time for vm " + vmId);
		long currentTime = (long) CloudSim.clock();
		long timeOfLastUpdate = lastBillingUpdate.get(vmId);

		long interval = currentTime - timeOfLastUpdate;

		int userId = this.vmIdToUserIdMap.get(vmId);
		long utilization = this.cloudUtilizationTable.get(userId);
		utilization += interval;
		this.cloudUtilizationTable.put(userId, utilization);

		// recalculate amount of required resources for this user
		ArrayList<Cloudlet> userOutsourcedTasks = externalQueueMap.get(userId);
		int requiredExternalResources = redistributedCloudlets(userOutsourcedTasks, userId);
		Log.printLine(CloudSim.clock() + " " + requiredExternalResources + " vms required; user has "
				+ resourcesUserHas(userId));
		if (requiredExternalResources < resourcesUserHas(userId)) {
			reallocateResource(vmId);
		}

		lastBillingUpdate.put(vmId, currentTime);
	}

	protected void reallocateResource(int vmId) {
		Log.printLine(CloudSim.clock() + " removing vm " + vmId);
		sendNow(super.cloudId, CloudSimTags.VM_DESTROY, VmList.getById(provisionedVms, vmId));
	}

	// Returns number of required external resources
	protected int redistributedCloudlets(ArrayList<Cloudlet> userOutsourcedTasks, int userId) {
		if (userOutsourcedTasks.isEmpty()) {
			return 0;
		}

		// As this is a bin packing problem, apply BEST-FIT in the sorted lag
		// time tasks list
		ArrayList<Long> binsEstimatedFreeTime = new ArrayList<Long>();
		binsEstimatedFreeTime.add((long) CloudSim.clock());

		for (Cloudlet cloudlet : userOutsourcedTasks) {
			Job job = jobMap.get(cloudletJobMap.get(cloudletJobMap.get(cloudlet.getCloudletId())));
			int trials = 0;
			int next = 0;
			boolean fit = false;
			do {
				long bestFit = binsEstimatedFreeTime.get(next);
				long eft = bestFit + job.getEstimatedDuration();
				if (eft <= job.getDeadline()) {
					fit = true;
					binsEstimatedFreeTime.remove(next);
					binsEstimatedFreeTime.add(eft);
				} else {
					trials++;
					next = (next + 1) % binsEstimatedFreeTime.size();
				}
			} while (!fit || trials >= binsEstimatedFreeTime.size());

			if (trials == binsEstimatedFreeTime.size()) {// couldn't put task in
				// an existing bin;
				// open a new one
				binsEstimatedFreeTime.add((long) CloudSim.clock() + job.getEstimatedDuration());
			}
			Collections.sort(binsEstimatedFreeTime, Collections.reverseOrder());
		}

		return binsEstimatedFreeTime.size();
	}
}
