package org.cloudbus.cloudsim;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class JobGenerator {

	int jobCounter;
	int timeCounter;
	int previousTime;

	ArrayList<Integer> timeList;
	ArrayList<Job> jobList;

	public JobGenerator(String file) {
		this.jobCounter = 0;
		this.timeCounter = 0;

		this.timeList = new ArrayList<Integer>();
		this.jobList = new ArrayList<Job>();

		String fileName = file;

		// read workload file, fill lists
		// MAKE SURE THAT TIME LIST IS ONE NODE SMALLER THAN JOB LIST
		BufferedReader reader = null;

		try {
			FileInputStream is = new FileInputStream(fileName);
			InputStreamReader input = new InputStreamReader(is);
			reader = new BufferedReader(input);
			while (reader.ready()) {
				parseLine(reader.readLine());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ioe) {
					Log.printLine("Error parsing trace file");
					ioe.printStackTrace();
				}
			}
		}
	}

	public Job nextJob(double currentTime) {
		Job job = null;
		if (jobCounter < jobList.size()) {
			job = jobList.get(jobCounter);
			jobCounter++;
		}

		return job;
	}

	public double delayToNextEvent(double currentTime) {
		double time = -1.0;
		if (timeCounter < timeList.size()) {
			time = timeList.get(timeCounter) - previousTime;
			this.previousTime = timeList.get(timeCounter);
			timeCounter++;
		}

		return time;
	}

	private void parseLine(String line) {
		if (line.startsWith(";") || line.startsWith("#")) {
			return;
		}

		String[] fields = line.trim().split("\\s+");
		// empty line
		if (fields.length <= 1) {
			return;
		}

		for (int i = 0; i < fields.length; i++) {
			fields[i] = fields[i].trim();
		}

		int id = Integer.parseInt(fields[0]);
		int uid = Integer.parseInt(fields[1]);
		int tasks = Integer.parseInt(fields[2]);
		int submissionTime = Integer.parseInt(fields[3]);
		int actualDuration = Integer.parseInt(fields[4]) * 60;
		int deadline = submissionTime + Integer.parseInt(fields[5]) * 60;
		int estimatedDuration = Integer.parseInt(fields[6]) * 60;

		Job job = new Job(id, uid, deadline, tasks, actualDuration, estimatedDuration, submissionTime);
		jobList.add(job);

		// ignore first submission time
		if (jobList.size() > 1) {
			timeList.add(submissionTime);
		} else {
			this.previousTime = submissionTime;
		}
	}
}
