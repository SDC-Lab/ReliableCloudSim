package org.cloudbus.cloudsim.failure;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;
import java.util.*;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import org.cloudbus.cloudsim.distributions.*;


public class FailureGenerator{
	
	private ArrayList<Integer>hostID = new ArrayList<Integer>();
	private ArrayList<Double> startTime = new ArrayList<Double>();
	private ArrayList<Double> endTime = new ArrayList<Double>();
	//private ArrayList<Integer> failCount =  new ArrayList<Integer>();
	int i = -1;
	
	Hashtable<Integer, ArrayList<Double>> failureStart = new Hashtable<Integer, ArrayList<Double>>();
	
	Hashtable<Integer, ArrayList<Double>> failureEnd =  new Hashtable<Integer, ArrayList<Double>>();
	
	public Hashtable<Integer, Integer> failCount = new Hashtable<Integer, Integer>();
	//public FailureGenerator(int host_ID, FileWriter ptr) throws Exception{
		public FailureGenerator(int host_ID)throws Exception{
		
		int randInt;
		//double temp;
		Random randomGenerator = new Random();
		
		randInt = randomGenerator.nextInt(10);
		
		hostID.add(host_ID);
		failCount.put(host_ID, randInt);
		
		
		while(randInt>0){			
		//temp=getstartTime();	
		startTime.add(getstartTime());
		failureStart.put(host_ID, startTime);
		endTime.add(getendTime());
		failureEnd.put(host_ID, endTime);
		randInt--;
		}
		//System.out.println(hostID);
		//System.out.println(startTime);
		//System.out.println(endTime);
		
		//System.out.println(failureStart);
		//System.out.print(failureEnd);
		
				
		//ptr.append("\n");	
		
	/*	
	//ptr.write(String.valueOf(hostID));
	ptr.append(String.valueOf(host_ID));
	//ptr.write(hostID+"");
	ptr.append(',');
	//ptr.write(String.valueOf(startTime));
	ptr.append((String.valueOf(startTime)).replaceAll("\\[|\\]", ""));
	
	//ptr.write(startTime+"");
	
	ptr.append(',');
	//ptr.write(String.valueOf(endTime));
	ptr.append((String.valueOf(endTime)).replaceAll("\\[|\\]", ""));
	
	//ptr.close();
	*/
	}
	
	public static double getstartTime(){
		
		double randMean;
		double randSD;
		double start;
		
		//double minMean = -6.1409;
		double minMean = 0.4063;
		double maxMean = 5.3012;
		
		double minSD = .0108;
		double maxSD = 3.8260;
		
		randMean=ThreadLocalRandom.current().nextDouble(minMean, maxMean+1);
		randSD=ThreadLocalRandom.current().nextDouble(minSD, maxSD+1);
		
		LognormalDistr lognormal = new LognormalDistr(randMean, randSD);
		
		start=(double)lognormal.sample();
		return(start);
		
	}
	
	public static double getendTime(){
		double randMean;
		double randSD;
		double end;
		double end1;
		double end2;
		
		double minMean = 0.0713;
		//double minMean = -3.1397;
		double maxMean = 1.5511;
		
		double minSD = .0581;
		double maxSD = 3.4368;
		
		randMean=ThreadLocalRandom.current().nextDouble(minMean, maxMean+1);
		randSD=ThreadLocalRandom.current().nextDouble(minSD, maxSD+1);
		
		LognormalDistr lognormal = new LognormalDistr(randMean, randSD);
		
		end1=(double)lognormal.sample();
		
		double randScale;
		double randShape;
		
		double minScale = .5497;
		double maxScale = 31.6903;
		
		double minShape = .5936;
		double maxShape = 35.5045;
		
		randScale = ThreadLocalRandom.current().nextDouble(minScale, maxScale+1);
		randShape = ThreadLocalRandom.current().nextDouble(minShape, maxShape+1);
		
		WeibullDistr weib = new WeibullDistr(randScale, randShape);
		end2 = (double)weib.sample();
		
		ArrayList<Double> endNumbers = new ArrayList<Double>();
		endNumbers.add(end1);
		endNumbers.add(end2);
		Collections.shuffle(endNumbers);
		
		end=endNumbers.get(0);
		
		return(end);		
		
	}
	
	
	public int failureListSize(int id){
		int size = 0;
		if(failureStart.containsKey(id)){			
			size = failCount.get(id);	
		}
		return(size);		
	}
	
	public int nodeCount(){
		int size;
		size = hostID.size();
		return(size);
	}
}
