package org.cloudbus.cloudsim.frequency;

import java.util.ArrayList;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.failure.FTAFileReader;
import org.cloudbus.cloudsim.failure.FTAFileReaderGrid5000;
import org.cloudbus.cloudsim.failure.VmCloudletMapping;

public class CheckNodeCompatibility {
	
	public FTAFileReader ftaLANL;
	public FTAFileReaderGrid5000 ftaGrid5000;
	public FrequencyDetails frqDtls;
	public DeadlineModel dlModel;
	
	public void setFTARead(FTAFileReader ftaLANL) {
		this.ftaLANL = ftaLANL;
	}
	
	public void setFTARead(FTAFileReaderGrid5000 ftaGrid5000) {
		this.ftaGrid5000 = ftaGrid5000;
	}
	
	public void setFrequencyDetails(FrequencyDetails frqDtls) {
		this.frqDtls = frqDtls;
	}
	
	public void setDeadlineModel(DeadlineModel dlModel) {
		this.dlModel = dlModel;		
	}
	
	public boolean isNodeCompatible(int ftaNodeID, Vm vm) {		
		int coreCount = 0;		
		double deadline = 0;
		double tentativeCompletionTime = 0;
		long maxLength = 0;
		int maxLengthCletIndex = 0;
		coreCount = ftaGrid5000.getProcessorCount(ftaNodeID);
		frqDtls.setFrequencyLevels(coreCount);
		frqDtls.setNormalizedFrequencyLevels();
		ArrayList<Cloudlet>cletList = new ArrayList<Cloudlet>();
		cletList.addAll(VmCloudletMapping.getCloudlet(vm));
		for(int i=0; i<cletList.size(); i++) {
			if(maxLength < cletList.get(i).getCloudletLength()) {
				maxLength = cletList.get(i).getCloudletLength();
				maxLengthCletIndex = i;
			}						
		}	
		deadline = dlModel.getCletDeadline(cletList.get(maxLengthCletIndex).getCloudletId());
		tentativeCompletionTime = cletList.get(maxLengthCletIndex).getCloudletLength()/frqDtls.getNormalizedMaxFrequency(coreCount);
		if(tentativeCompletionTime<=deadline) {
			return true;
		}
		return false;	
	}
	
	
}
