package org.cloudbus.cloudsim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

import org.cloudbus.cloudsim.core.CloudSim;

public class RoundRobinTaskDpBroker extends HeftTaskDpBroker {

	Hashtable<Integer, Long> lastBillingUpdate;
	int nextResource;

	public RoundRobinTaskDpBroker(String name, String file, int internalVms) {
		super(name, file, internalVms);
		lastBillingUpdate = new Hashtable<Integer, Long>();
		nextResource = 0;
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
			toCloud.add(cl);

			// ROUND ROBIN: assigns the cloudlet to the next resource
			int trials = 0;
			do {
				int id = nextResource;
				long earlierStartTime = estimatedWaitingTime.get(id);
				long estimatedFinishTime = earlierStartTime + job.getEstimatedDuration();
				if (estimatedFinishTime <= job.getDeadline()) {// task runs on
					// this resource
					toCloud.remove(cl);

					ArrayList<Cloudlet> list = deadlineQueueMap.get(id);
					list.add(cl);

					// update earliest start time
					updateEstimatedWaitingTime(id, job.getEstimatedDuration());

					// update nextResource
					nextResource = (nextResource + 1) % deadlineResources;

					// resource found: leave loop
					break;
				} else {
					trials++;
					nextResource = (nextResource + 1) % deadlineResources;
				}
			} while (trials < deadlineResources);
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
}
