package org.cloudbus.cloudsim.frequency;

import org.cloudbus.cloudsim.frequency.FrequencyModelSpecPowerIntelXeonE54669;
import org.cloudbus.cloudsim.frequency.FrequencyModelSpecPowerEdgeR710XeonX5570;
import org.cloudbus.cloudsim.frequency.FrequencyModelSpecPowerHpProLiantDL380G5;
import org.cloudbus.cloudsim.frequency.FrequencyModelSpecPowerHpProLiantDL560Gen9;
import org.cloudbus.cloudsim.frequency.FrequencyModelSpecPowerHpProLiantDL785G5AMD8376;
import org.cloudbus.cloudsim.frequency.FrequencyModelSpecPowerIntelSE7520AF2Xeon3600;
import org.cloudbus.cloudsim.frequency.FrequencyModelSpecPowerPlatHomeTRQX150SA;

import java.util.ArrayList;
import java.util.HashMap;

import org.cloudbus.cloudsim.failure.RunTimeConstants;


public class FrequencyDetails {
	
/*
	public ArrayList<Double>freqLevels_2Cores = new ArrayList<Double>();
	public ArrayList<Double>freqLevels_4Cores = new ArrayList<Double>();
	public ArrayList<Double>freqLevels_8Cores = new ArrayList<Double>();
	public ArrayList<Double>freqLevels_32Cores = new ArrayList<Double>();
	public ArrayList<Double>freqLevels_80Cores = new ArrayList<Double>();
	public ArrayList<Double>freqLevels_128Cores = new ArrayList<Double>();
	public ArrayList<Double>freqLevels_256Cores = new ArrayList<Double>();
*/	
	
	public ArrayList<Double>maxFrequencyList = new ArrayList<Double>();
	public ArrayList<Integer>coreCountList = new ArrayList<Integer>();
	
	public HashMap<Integer, ArrayList<Double>> frequencyLevels = new HashMap<Integer, ArrayList<Double>>();
	public HashMap<Integer, ArrayList<Double>> normalizedFrequencyLevels = new HashMap<Integer, ArrayList<Double>>();
	public HashMap<Integer, Double>maxFrequencyTable = new HashMap<Integer, Double>();
	public HashMap<Integer, Integer>currentFrequencyLevel = new HashMap<Integer, Integer>();
	
	public void setFrequencyLevels(int coreCount) {	
		ArrayList<Double>tempFreqLevels;
		double maxFrequency = 0, minFrequency = 0;
		double difference = 0;
		double gap = 0;
		double tempFreq = 0;
		int i=1;
		if(coreCount == 2) {			
			FrequencyModelSpecPowerIntelSE7520AF2Xeon3600 freqObject = new FrequencyModelSpecPowerIntelSE7520AF2Xeon3600();
			tempFreqLevels = new ArrayList<Double>();
			maxFrequency = freqObject.getMaxFrequency();
			minFrequency = freqObject.getMinFrequency();					
			difference = maxFrequency - minFrequency;
			gap = difference/(RunTimeConstants.numberofFrequencyLevels-1);			
			tempFreqLevels.add(minFrequency);
			while(i<RunTimeConstants.numberofFrequencyLevels) {
				tempFreq = tempFreqLevels.get(i-1)+gap;
				tempFreqLevels.add(i, tempFreq);
				i++;
			}
			frequencyLevels.put(2, tempFreqLevels);
			maxFrequencyTable.put(2, maxFrequency);			
		}
		if(coreCount == 4) {			
			FrequencyModelSpecPowerPlatHomeTRQX150SA freqObject = new FrequencyModelSpecPowerPlatHomeTRQX150SA();
			tempFreqLevels = new ArrayList<Double>();
			maxFrequency = freqObject.getMaxFrequency();
			minFrequency = freqObject.getMinFrequency();
			difference = maxFrequency - minFrequency;
			gap = difference/(RunTimeConstants.numberofFrequencyLevels-1);
			tempFreqLevels.add(minFrequency);						
			while(i<RunTimeConstants.numberofFrequencyLevels) {
				tempFreq = tempFreqLevels.get(i-1)+gap;
				tempFreqLevels.add(i, tempFreq);
				i++;
			}		
			frequencyLevels.put(4, tempFreqLevels);
			maxFrequencyTable.put(4, maxFrequency);			
		}
		if(coreCount == 8) {			
			FrequencyModelSpecPowerEdgeR710XeonX5570 freqObject = new FrequencyModelSpecPowerEdgeR710XeonX5570();
			tempFreqLevels = new ArrayList<Double>();
			maxFrequency = freqObject.getMaxFrequency();
			minFrequency = freqObject.getMinFrequency();
			difference = maxFrequency - minFrequency;
			gap = difference/(RunTimeConstants.numberofFrequencyLevels-1);
			tempFreqLevels.add(minFrequency);						
			while(i<RunTimeConstants.numberofFrequencyLevels) {
				tempFreq = tempFreqLevels.get(i-1)+gap;
				tempFreqLevels.add(i, tempFreq);
				i++;
			}				
			frequencyLevels.put(8, tempFreqLevels);
			maxFrequencyTable.put(8, maxFrequency);	
		}	
		if(coreCount == 32) {			
			FrequencyModelSpecPowerHpProLiantDL785G5AMD8376 freqObject = new FrequencyModelSpecPowerHpProLiantDL785G5AMD8376();
			tempFreqLevels = new ArrayList<Double>();
			maxFrequency = freqObject.getMaxFrequency();
			minFrequency = freqObject.getMinFrequency();
			difference = maxFrequency - minFrequency;
			gap = difference/(RunTimeConstants.numberofFrequencyLevels-1);
			tempFreqLevels.add(minFrequency);						
			while(i<RunTimeConstants.numberofFrequencyLevels) {
				tempFreq = tempFreqLevels.get(i-1)+gap;
				tempFreqLevels.add(i, tempFreq);
				i++;
			}				
			frequencyLevels.put(32, tempFreqLevels);
			maxFrequencyTable.put(32, maxFrequency);			
		}
		if(coreCount == 80) {			
			FrequencyModelSpecPowerHpProLiantDL560Gen9 freqObject = new FrequencyModelSpecPowerHpProLiantDL560Gen9();
			tempFreqLevels = new ArrayList<Double>();
			maxFrequency = freqObject.getMaxFrequency();
			minFrequency = freqObject.getMinFrequency();
			difference = maxFrequency - minFrequency;
			gap = difference/(RunTimeConstants.numberofFrequencyLevels-1);
			tempFreqLevels.add(minFrequency);						
			while(i<RunTimeConstants.numberofFrequencyLevels) {
				tempFreq = tempFreqLevels.get(i-1)+gap;
				tempFreqLevels.add(i, tempFreq);
				i++;
			}		
			frequencyLevels.put(80, tempFreqLevels);
			maxFrequencyTable.put(80, maxFrequency);			
		}
		if(coreCount == 128) {			
			FrequencyModelSpecPowerHpProLiantDL560Gen9 freqObject = new FrequencyModelSpecPowerHpProLiantDL560Gen9();
			tempFreqLevels = new ArrayList<Double>();
			maxFrequency = freqObject.getMaxFrequency();
			minFrequency = freqObject.getMinFrequency();
			difference = maxFrequency - minFrequency;
			gap = difference/(RunTimeConstants.numberofFrequencyLevels-1);
			tempFreqLevels.add(minFrequency);						
			while(i<RunTimeConstants.numberofFrequencyLevels) {
				tempFreq = tempFreqLevels.get(i-1)+gap;
				tempFreqLevels.add(i, tempFreq);
				i++;
			}		
			frequencyLevels.put(128, tempFreqLevels);
			maxFrequencyTable.put(128, maxFrequency);		
		}
		if(coreCount == 256) {			
			FrequencyModelSpecPowerIntelXeonE54669 freqObject = new FrequencyModelSpecPowerIntelXeonE54669();
			tempFreqLevels = new ArrayList<Double>();
			maxFrequency = freqObject.getMaxFrequency();
			minFrequency = freqObject.getMinFrequency();
			difference = maxFrequency - minFrequency;
			gap = difference/(RunTimeConstants.numberofFrequencyLevels-1);
			tempFreqLevels.add(minFrequency);						
			while(i<RunTimeConstants.numberofFrequencyLevels) {
				tempFreq = tempFreqLevels.get(i-1)+gap;
				tempFreqLevels.add(i, tempFreq);
				i++;
			}
			frequencyLevels.put(256, tempFreqLevels);
			maxFrequencyTable.put(256, maxFrequency);			
		}
		coreCountList.add(coreCount);
	}
	
	public void setNormalizedFrequencyLevels() {
		double maxFrequency = 0;
		ArrayList<Double>tempFreqLevels;
		ArrayList<Double>tempNormalizedFreqLevels;
		for(int i=0; i<maxFrequencyTable.size(); i++) {			
			if(maxFrequency < maxFrequencyTable.get(coreCountList.get(i))) {
				maxFrequency = maxFrequencyTable.get(coreCountList.get(i));
			}
		}
		for(int i=0; i<maxFrequencyTable.size(); i++) {
			double normFreq=0;
			tempFreqLevels = new ArrayList<Double>();
			tempFreqLevels.addAll(frequencyLevels.get(coreCountList.get(i)));
			tempNormalizedFreqLevels = new ArrayList<Double>();
			for(int j=0; j<tempFreqLevels.size(); j++) {
				normFreq = tempFreqLevels.get(j)/maxFrequency;
				tempNormalizedFreqLevels.add(normFreq);				
			}
			normalizedFrequencyLevels.put(coreCountList.get(i), tempNormalizedFreqLevels);
		}		
	}
	
	public ArrayList<Double> getNormalizedFrequencyLevels(int coreCount) {
		return normalizedFrequencyLevels.get(coreCount);
	}
	
	public double getNormalizedMaxFrequency(int coreCount) {
		double freqValue;
		ArrayList<Double>frequencyLevels = new ArrayList<Double>();
		frequencyLevels.addAll(getNormalizedFrequencyLevels(coreCount));
		freqValue = frequencyLevels.get(RunTimeConstants.numberofFrequencyLevels - 1);
		return freqValue;
	}
	
}


