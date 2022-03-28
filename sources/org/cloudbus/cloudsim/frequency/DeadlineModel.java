package org.cloudbus.cloudsim.frequency;

import java.util.HashMap;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.failure.RunTimeConstants;

/**
 * Model to calculate deadline corresponding to each submitted cloudlet
 *
 * @author Yogesh Sharma
 * @since CloudSim Toolkit 4.0
 */

public class DeadlineModel {

	public HashMap<Integer, Double>cletDeadlines = new HashMap<Integer, Double>();
	
	public void calCletDeadline(int cletID, long cletLength) {
		double deadline = 0;
		deadline=(CloudSim.clock() + (RunTimeConstants.stringencyFactor * cletLength));
		cletDeadlines.put(cletID, deadline);
	}
	
	public double getCletDeadline(int cletID) {
		return cletDeadlines.get(cletID);
	}
	
}
