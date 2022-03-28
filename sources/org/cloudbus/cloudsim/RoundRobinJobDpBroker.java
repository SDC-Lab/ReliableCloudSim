package org.cloudbus.cloudsim;

import java.util.ArrayList;
import java.util.Collections;

import org.cloudbus.cloudsim.core.CloudSim;

public class RoundRobinJobDpBroker extends HeftJobDpBroker {

	int nextResource;

	public RoundRobinJobDpBroker(String name, String file, int internalVms) {
		super(name, file, internalVms);
		nextResource = 0;
	}

	@Override
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
				// ROUND ROBIN: assigns the cloudlet to the next resource
				int id = nextResource;
				ArrayList<Cloudlet> list = deadlineQueueMap.get(id);
				list.add(cl);

				// update earliest start time
				updateEstimatedWaitingTime(id, job.getEstimatedDuration());

				// update nextResource
				nextResource = (nextResource + 1) % deadlineResources;
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
}
