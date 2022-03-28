package org.cloudbus.cloudsim;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

import org.cloudbus.cloudsim.distributions.WeibullDistr;

public class BotWorkloadGenerator {

	protected WeibullDistr iat;
	protected WeibullDistr size;
	protected Random user;
	protected int startTime;
	protected double recountTime;
	protected double endTime;
	protected double urgentRequestsRate;
	protected long currentIat;
	protected Random generator;
	protected Random runTime;

	public BotWorkloadGenerator(int simulationHours, int startTime, double urgentRequestsRate, double alpha, double beta) {
		iat = new WeibullDistr(alpha, beta); //default: alpha=4.25; beta=7.86
		size = new WeibullDistr(1.76, 2.11);
		user = new Random(System.currentTimeMillis());
		endTime = simulationHours * 60 * 60 + startTime;
		generator = new Random(System.currentTimeMillis());
		runTime = new Random(System.currentTimeMillis());
		this.startTime = startTime;
		this.recountTime = -1;
		this.urgentRequestsRate = urgentRequestsRate;
	}

	public static void main(String args[]) {
		if (args.length != 3) {
			System.out
					.println("Required arguments: <users> <simulation hours> <estimation error>");
			System.exit(1);
		}

		int users = Integer.parseInt(args[0]);
		int simulationHours = Integer.parseInt(args[1]);
		double estimationError = Double.parseDouble(args[2]);
		
		int stTime = 5; // delays first job in 5 sec
		
		ArrayList<Double> urgencyList = new ArrayList<Double>();
		urgencyList.add(0.2);
		urgencyList.add(0.5);

		ArrayList<Double> alphaList = new ArrayList<Double>();
		alphaList.add(4.25);
		alphaList.add(8.5);
		
		ArrayList<Double> betaList = new ArrayList<Double>();
		betaList.add(7.86);
		betaList.add(3.93);
		betaList.add(15.72);
		
		for (double urgency: urgencyList){
			for(double alpha: alphaList){
				for(double beta: betaList){

					for (int i = 1; i < 31; i++) {
						String prefix = "exp-"+urgency+"-"+alpha+"-"+beta+"-";
						if (i < 10) {
							prefix = prefix.concat("0");
						}
						String fileName = prefix + i + ".txt";

						Random urgencyGen = new Random(System.currentTimeMillis());
						Random urgencyRateGen = new Random(System.currentTimeMillis());
						Random predictionGen = new Random(System.currentTimeMillis());

						BotWorkloadGenerator gen = new BotWorkloadGenerator(simulationHours, stTime, urgency, alpha, beta);

						int id = 0;
						FileWriter writer = null;

						try {
							writer = new FileWriter(fileName);
							@SuppressWarnings("resource")
							PrintWriter prt = new PrintWriter(writer);

							long currentTime = stTime;
							long delay = 0;

							do {
								currentTime += delay;
								int tasks = gen.nextRequests();

								int uid = gen.nextUser(users);

								int actualDuration = gen.nextRuntime();

								int estimatedDuration = 0;

								// determine deadline
								// is this job high or low urgency?
								boolean isHighUrgency = urgencyRateGen.nextDouble() <= urgency;

								double deadlineRate;
								if (isHighUrgency) {
									deadlineRate = Math.abs(urgencyGen.nextGaussian()) * 1.4 + 3.0;
								} else {
									deadlineRate = Math.abs(urgencyGen.nextGaussian()) * 2.0 + 8.0;
								}

								int deadline = (int) (actualDuration * deadlineRate);

								// determine estimation error
								double thisEstimationError = predictionGen.nextDouble();// [0,1]
								thisEstimationError *= estimationError * 2;// [0,2E]
								thisEstimationError -= estimationError;// [-E,E]
								estimatedDuration = (int) (actualDuration + actualDuration * estimationError);

								prt.printf(
										"%d %d %d %d %d %d %d\n",
										id,
										uid,
										tasks,
										currentTime,
										actualDuration,
										deadline,
								estimatedDuration);

								delay = gen.delayToNextEvent(currentTime);
								id++;
							} while (delay > 0);
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							try {
								writer.close();
							} catch (IOException ignore) {
							}
						}
					}//for i
				}//for beta
			}//for alpha
		}//for urgency
	}

	public long delayToNextEvent(long currentTime) {
		if (currentTime > endTime) {
			return -1;
		}

		long iats = Math.round(iat.sample());
		if (iats <= 0) {
			iats = 1;
		}
		
		return iats;
	}

	public int nextRequests() {
		double tasks = size.sample();
		tasks = Math.pow(2, tasks);

		return (int) tasks;
	}

	public int nextRuntime() {
		double runtime = runTime.nextGaussian() * 6.1 + 2.73;
		double value = Math.pow(2, runtime);

		return (int) Math.ceil(value);
	}

	public int nextUser(int users) {
		return user.nextInt(users);
	}
}
