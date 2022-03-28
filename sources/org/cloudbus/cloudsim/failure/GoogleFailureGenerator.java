package org.cloudbus.cloudsim.failure;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.math3.analysis.function.Log;
import org.cloudbus.cloudsim.distributions.LognormalDistr;
import org.cloudbus.cloudsim.distributions.WeibullDistr;


public class GoogleFailureGenerator {
	
	private ArrayList<Long>hostID = new ArrayList<Long>();	
	private ArrayList<Double>MTBF = new ArrayList<Double>();
	private ArrayList<Double>MTTR = new ArrayList<Double>();
	private ArrayList<Double>SD_TBF = new ArrayList<Double>();
	private ArrayList<Double>Var_TBF = new ArrayList<Double>();
	private ArrayList<Double>SD_TTR = new ArrayList<Double>();
	private ArrayList<Double>Var_TTR = new ArrayList<Double>();
	private ArrayList<Integer>mappedhostID = new ArrayList<Integer>();
	
	public GoogleFailureGenerator(){
		BufferedReader buffer;
		try {
				buffer = new BufferedReader(new FileReader("C://Data/Australia/Information_for_Cloudsim1.csv"));						
				buffer.readLine(); //Skips the first line of the file. 
				String currentstr;
				while((currentstr=buffer.readLine()) != null){
					String[] column = currentstr.split(",");
					//System.out.println("column[0] = " +column[0]);
				
					if(column.length>0){
						//hostID.add(Long.parseLong(column[0]));
						hostID.add(Double.valueOf(column[0]).longValue());
						MTBF.add(Double.parseDouble(column[1]));
						SD_TBF.add(Double.parseDouble(column[2]));
						Var_TBF.add(Double.parseDouble(column[3]));
						MTTR.add(Double.parseDouble(column[4]));
						SD_TTR.add(Double.parseDouble(column[5]));
						Var_TTR.add(Double.parseDouble(column[6]));
						
						
						
					}				
									
				}
			}
			 catch (IOException e) {
				e.printStackTrace();
				System.out.println("The simulation has been terminated due to an IO error");
			} 	
		maphostID();
		}
	
	public void maphostID(){
		for(int i=0;i<hostID.size();i++){
			mappedhostID.add(i);
		}
	}
	
	public ArrayList<Integer>getmaphostID(){		
		return(mappedhostID);
	}
	
	public ArrayList<Double>getMTBF(){
		return(MTBF);
	}
	
	public ArrayList<Double>getMTTR(){
		return(MTTR);
	}
	
	public ArrayList<Double>getSD_TBF(){
		return(SD_TBF);
	} 
	
	public ArrayList<Double>getVar_TBF(){
		return(Var_TBF);
	}
	
	public ArrayList<Double>getSD_TTR(){
		return(SD_TTR);
	}
	
	public ArrayList<Double>getVar_TTR(){
		return(Var_TTR);
	}
	
	public int getsize(){
		return(Var_TTR.size());
	}
	
public double getstartTime(int i){
	
		Collections.shuffle(mappedhostID);	
		
		double Mean;
		double SD;
		double start = 0;
		
		for(int j=1;j<=mappedhostID.size();j++)
		{
			if(mappedhostID.get(j)==i){
				Mean = getMTBF().get(j);
				SD = getSD_TBF().get(j);
				
				LognormalDistr lognormal = new LognormalDistr(Mean, SD);
				start=(double)lognormal.sample();	
				
				break;
				
			}
			
		}	
		
		return(start);		
	}


public double getendTime(int i){
	double Mean;
	double SD;
	double end = 0;
	
	for(int j=1;j<=mappedhostID.size();j++)
	{
		if(mappedhostID.get(j)==i){
			Mean = getMTTR().get(j);
			SD = getSD_TTR().get(j);
			
			LognormalDistr lognormal = new LognormalDistr(Mean, SD);
			end=(double)lognormal.sample();				
			break;
		}		
	}		
	return(end);
	
	/**
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
	*/		
}


}
//}			