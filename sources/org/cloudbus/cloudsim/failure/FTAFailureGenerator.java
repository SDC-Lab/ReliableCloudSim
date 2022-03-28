package org.cloudbus.cloudsim.failure;

import java.util.ArrayList;

public class FTAFailureGenerator {
	private ArrayList<Integer>hostID = new ArrayList<Integer>();	
	private ArrayList<Double>MTBF = new ArrayList<Double>();
	private ArrayList<Double>MTTR = new ArrayList<Double>();
	private ArrayList<Double>SD_TBF = new ArrayList<Double>();
	private ArrayList<Double>Var_TBF = new ArrayList<Double>();
	private ArrayList<Double>SD_TTR = new ArrayList<Double>();
	private ArrayList<Double>Var_TTR = new ArrayList<Double>();
	private ArrayList<Integer>mappedhostID = new ArrayList<Integer>();
	
	private FTAFileReader freadLANL;
	
	private FTAFileReaderGrid5000 freadGrid5000;
	
	public void setFTAFileReaderObject(FTAFileReader freadLANL){
		this.freadLANL = freadLANL;
	}
	
	public void setFTAFilreReaderObject(FTAFileReaderGrid5000 freadGrid5000){
		this.freadGrid5000 = freadGrid5000;
	}
	public void getHostIDList(){
		if(RunTimeConstants.traceType.equals("LANL")){
			for(int i=0;i<freadLANL.getnodeIdListSize();i++){
				hostID.add(freadLANL.getNodeID(i));
			}
		}
		if(RunTimeConstants.traceType.equals("Grid5000")){
			for(int i=0;i<freadGrid5000.getnodeIdListSize();i++){
				hostID.add(freadGrid5000.getNodeID(i));
			}
		}
				
	}
	
	

}
