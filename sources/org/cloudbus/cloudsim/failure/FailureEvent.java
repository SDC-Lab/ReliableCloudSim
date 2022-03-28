/*
   The purpose of this class is to define a failure event in a cloud environment 
      
   @author		Bahman Javadi
   @author		Yogesh Sharma
   @since		Cloudsim Toolkit 3.0.3
   
 */

/**
******************************************************************************
 	 Variable descriptions corresponding to the Failure Trace Archive files.
******************************************************************************* 
	 * hostID corresponds to node_id 
	 * eventType corresponds to event_type
	 * startTime corresponds to event_start_time
	 * stopTime corresponds to event_stop_time
	 * endReason corresponds to event_end_reason
*/

package org.cloudbus.cloudsim.failure;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.io.File;


public class FailureEvent {
	
	private int hostID = -1;
	private ArrayList<Integer> eventType = null;
	private ArrayList<Double> startTime = null;
	private ArrayList <Double> endTime = null;
	//private ArrayList <Integer> endReason = null;
	//private String filename;	
	//public FailureEvent(int hostID, int eventType, double startTime, double endTime, int endReason)
	
	public FailureEvent(int hostID, int eventType, double startTime, double endTime){
		this.hostID=hostID;
		if(this.eventType == null){
			this.eventType =  new ArrayList<Integer>();
		}
		
		if(this.startTime == null){
			this.startTime = new ArrayList<Double>();
		}
		
		if(this.endTime == null){
			this.endTime = new ArrayList<Double>();
		}
		
		this.eventType.add(eventType);
		this.startTime.add(startTime);
		this.endTime.add(endTime);
		
	}
		
	public int gethostID(){
		return hostID;
	}
	
	
	public void insertEvent(int hostID, int eventType, double startTime, double endTime){
		this.hostID = hostID;
		this.eventType.add(eventType);
		this.startTime.add(startTime);
		this.endTime.add(endTime);
	}
	
	// Returns the number of events in the host file
	public int getnumEvent(){
		return this.eventType.size();
	}
	
	// Returns the type of event at a given index
	public int geteventType(int index){
		return this.eventType.get(index);
	}
	
	// Returns the start time of event at a given index
	public double getstartTime(int index){
		return this.startTime.get(index);
	}
	
}

