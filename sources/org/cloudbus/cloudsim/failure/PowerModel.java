package org.cloudbus.cloudsim.failure;

import org.cloudbus.cloudsim.Test2;

public class PowerModel {
	
	/*
	 * (non-Javadoc)
	 * @see org.cloudbus.cloudsim.power.models.PowerModel#getPower(double)
	 */
	
	public double getPower(double utilization, int core_count) throws IllegalArgumentException {
		double power = 0;
		
		if (utilization < 0 || utilization > 1) {
			throw new IllegalArgumentException("Utilization value must be between 0 and 1");
		}
		
		if(core_count == 2){		
			PowerModelSpecPowerIntelSE7520AF2Xeon3600 powerModel_2cores = new PowerModelSpecPowerIntelSE7520AF2Xeon3600(); 
			
			if (utilization % 0.1 == 0) {
				power = (powerModel_2cores.getPowerData((int) (utilization * 10)));
			}
			else{
				int utilization1 = (int) Math.floor(utilization * 10);
				int utilization2 = (int) Math.ceil(utilization * 10);
				double power1 = powerModel_2cores.getPowerData(utilization1);
				double power2 = powerModel_2cores.getPowerData(utilization2);
				double delta = (power2 - power1) / 10;
				power = power1 + delta * (utilization - (double) utilization1 / 10) * 100;	
			//return power;
			}				
		}
		
		if(core_count == 4){
			PowerModelSpecPowerPlatHomeTRQX150SA powerModel_4cores = new PowerModelSpecPowerPlatHomeTRQX150SA();
			
			if (utilization % 0.1 == 0) {
				power = (powerModel_4cores.getPowerData((int) (utilization * 10)));
			}
			else{
				int utilization1 = (int) Math.floor(utilization * 10);
				int utilization2 = (int) Math.ceil(utilization * 10);
				double power1 = powerModel_4cores.getPowerData(utilization1);
				double power2 = powerModel_4cores.getPowerData(utilization2);
				double delta = (power2 - power1) / 10;
				power = power1 + delta * (utilization - (double) utilization1 / 10) * 100;	
					
			}
			//return power;
		}
		
		if(core_count == 8){
			PowerModelSpecPowerEdgeR710XeonX5570 powerModel_8cores = new PowerModelSpecPowerEdgeR710XeonX5570();
			
			if (utilization % 0.1 == 0) {
				power = (powerModel_8cores.getPowerData((int) (utilization * 10)));
			}
			else{
				int utilization1 = (int) Math.floor(utilization * 10);
				int utilization2 = (int) Math.ceil(utilization * 10);
				double power1 = powerModel_8cores.getPowerData(utilization1);
				double power2 = powerModel_8cores.getPowerData(utilization2);
				double delta = (power2 - power1) / 10;
				power = power1 + delta * (utilization - (double) utilization1 / 10) * 100;	
					
			}
			//return power;
		}
		
		if(core_count == 80 || core_count == 128){
			PowerModelSpecPowerHpProLiantDL560Gen9 powerModel_80cores = new PowerModelSpecPowerHpProLiantDL560Gen9();
			
			if (utilization % 0.1 == 0) {
				power = (powerModel_80cores.getPowerData((int) (utilization * 10)));
			}
			else{
				int utilization1 = (int) Math.floor(utilization * 10);
				int utilization2 = (int) Math.ceil(utilization * 10);
				double power1 = powerModel_80cores.getPowerData(utilization1);
				double power2 = powerModel_80cores.getPowerData(utilization2);
				double delta = (power2 - power1) / 10;
				power = power1 + delta * (utilization - (double) utilization1 / 10) * 100;	
			//return power;		
			}
			//return power;
		}
		
		if(core_count == 32){
			
			PowerModelSpecPowerIntelXeonE54669 powerModel_32cores = new PowerModelSpecPowerIntelXeonE54669();
			
			if (utilization % 0.1 == 0) {
				power = (powerModel_32cores.getPowerData((int) (utilization * 10)));
			}
			else{
				int utilization1 = (int) Math.floor(utilization * 10);
				int utilization2 = (int) Math.ceil(utilization * 10);
				double power1 = powerModel_32cores.getPowerData(utilization1);
				double power2 = powerModel_32cores.getPowerData(utilization2);
				double delta = (power2 - power1) / 10;
				power = power1 + delta * (utilization - (double) utilization1 / 10) * 100;	
			//return power;		
			}
			//return power;
		}
		
		if(core_count == 256){
			
			PowerModelSpecPowerHpProLiantDL785G5AMD8376 powerModel_256cores = new PowerModelSpecPowerHpProLiantDL785G5AMD8376();
			
			if (utilization % 0.1 == 0) {
				power = (powerModel_256cores.getPowerData((int) (utilization * 10)));
			}
			else{
				int utilization1 = (int) Math.floor(utilization * 10);
				int utilization2 = (int) Math.ceil(utilization * 10);
				double power1 = powerModel_256cores.getPowerData(utilization1);
				double power2 = powerModel_256cores.getPowerData(utilization2);
				double delta = (power2 - power1) / 10;
				power = power1 + delta * (utilization - (double) utilization1 / 10) * 100;	
			//return power;	
			
			}
			//return power;
		}				
		
		return power;
		
	}	
	
	/**
	 * Gets the power data.
	 * 
	 * @param index the index
	 * @return the power data
	 */
	
	//protected abstract double getPowerData(int index);

}
