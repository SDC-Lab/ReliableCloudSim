package org.cloudbus.cloudsim.failure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;

public class BinPacking {
	
	//public ArrayList<Cloudlet> cletList;
	public int vmID = 0;	
	public int vmCount;
	public ArrayList<Integer>cletID = new ArrayList<Integer>();
	public ArrayList<Integer>cletIDTemp;
	public HashMap<Integer, ArrayList<Integer>>vmCletMap = new HashMap<Integer, ArrayList<Integer>>();
	public int count = 1;		
	
	//public ArrayList<Double>normlength;
	//public Double[] normLength;
	CloudletNormalization cletNorm;
	public ArrayList<Double>normLengthList = new ArrayList<Double>();	
	
	public void setCloudletNormalization(CloudletNormalization cletNorm){
		this.cletNorm = cletNorm;		
	}
	
	public void calVMCount(List<Cloudlet>cletList){
		double temp = 0;
		int tempID = 0;
		double [] normLength = new double[cletList.size()];
		int [] cletIDArray = new int[cletList.size()]; 
		int tempLength = 0;
		for(int i=0;i<cletList.size();i++){
			//cletID.add(cletList.get(i).getCloudletId());
			normLength[i] = cletNorm.getNormalizedLength(cletList.get(i));
			cletIDArray[i] = cletList.get(i).getCloudletId();
			//normlength.add(cletNorm.getNormalizedLength(cletList.get(i)));			
		}
		for(int i=0;i<(cletList.size()-1);i++){		
			for(int j=i+1;j<cletList.size();j++){				
				if(normLength[i]<normLength[j]){
					temp = normLength[i];
					tempID = cletIDArray[i];
					normLength[i] = normLength[j];
					cletIDArray[i] = cletIDArray[j];
					normLength[j] = temp;
					cletIDArray[j] = tempID;
				}
			}		
		}
		
		for(int i=0;i<normLength.length;i++){
			normLengthList.add(normLength[i]);
			cletID.add(cletIDArray[i]);
		}
		
		tempLength = normLengthList.size();
		vmCount = 0;
		double total = 0.0;
		boolean flag_vmCount = false;
		cletIDTemp = new ArrayList<Integer>();
		
		for(int i=0;i<tempLength;){			
			if(total<1.0){
				total = total + normLengthList.get(i);				
				if(total>1.0){
					total = total - normLengthList.get(i);					
					i++;
				}
				else{
					cletIDTemp.add(cletID.get(i));
					normLengthList.remove(i);
					cletID.remove(i);
					tempLength = normLengthList.size();
					i = 0;
				}
				//i++;
				if(i == tempLength){
					flag_vmCount = true;
				}
			}
			else{					
				vmCletMap.put(vmID, cletIDTemp);	
				vmID = vmID + 1;
				vmCount = vmCount + 1;
				total = 0.0;
				tempLength = normLengthList.size();
				i = 0;
				cletIDTemp = new ArrayList<Integer>();
			}
			if(flag_vmCount == true){				
				vmCletMap.put(vmID, cletIDTemp);
				vmID = vmID + 1;
				vmCount = vmCount + 1;
				total = 0.0;
				tempLength = normLengthList.size();
				i = 0;
				flag_vmCount = false;
				cletIDTemp = new ArrayList<Integer>();
			}
		}
	//	if(count == 2){
	//		System.out.println("Check");
	//	}
		/*
		int countClets = 0;
		for(int i=1;i<=vmCletMap.size();i++){
			cletIDTemp = vmCletMap.get(i);
			countClets =  countClets + cletIDTemp.size();
		}
		System.out.println("Check");
		*/
	}
	
	public ArrayList<Integer>getVMCletMapList(int vmID){
		return vmCletMap.get(vmID);
	}
	
	public int getVmCount(){
		return vmCount;
	}
	
	
}
