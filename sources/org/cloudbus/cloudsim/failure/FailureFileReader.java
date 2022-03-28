package org.cloudbus.cloudsim.failure;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.zip.*;

//import com.sun.javafx.Logging;
//import com.sun.media.jfxmedia.logging.Logger;



/**
 * The purpose of this file is to read failure traces from a failure trace archive (FTA) file.
 * 
 * 
 * @author		Bahman Javadi
   @author		Yogesh Sharma
   @since		Cloudsim Toolkit 3.0.3
 */


public class FailureFileReader implements FailureModel {
	
	//private static Logger logger = Logging.getLogger(LoggerEnum.PARALLEL);
	
	private String fileName;
	private int curID;
	private int eventCtr;
	private ArrayList<FailureEvent> events = null;
	private FailureEvent nodeEvent = null;
	
	private int HOST_ID = 2;
	private int EVENT_TYPE = 5;
	private int START_TIME = 6;
	private int END_TIME = 7;
	//private int END_REASON = 8;
	private int MAX_FIELD = 9;
	private String COMMENT = "#";
	
	private String[] fieldArray = null;
	private double TraceStartTime = 0;
	
	boolean addtolist = false;
	
	
// Initialize the variables corresponding to the trace file that need to be processed
	
	public FailureFileReader(String fileName, double TraceStartTime){
		if(fileName == null || fileName.length() == 0){
			throw new IllegalArgumentException("Invalid trace file name.");
		}
		
		this.fileName = fileName;
		this.curID = 0;
		this.eventCtr = 0;
		this.TraceStartTime = TraceStartTime;
	}
	
// Identify the comment line in the file
	
	public boolean setComment(String cmt){
		boolean success = false;
		if(cmt !=null && cmt.length() > 0){
			COMMENT = cmt;
			success = true;
		}
		return success;
	}

// This module tells the class what to look into the trace file
	
	public boolean setField(int maxField, int hostID, int eventType, int startTime, int endTime){
		
		if(maxField > 0){
			MAX_FIELD = maxField;
		}
		else{
			throw new IllegalArgumentException("Invalid maximum number of fields.");
		}
		
		if(hostID > 0){
			HOST_ID = hostID - 1;			
		}
		else{
			throw new IllegalArgumentException("Invalid node id field.");
		}
		
		if(eventType > 0){
			EVENT_TYPE = eventType - 1;
		}
		else{
			throw new IllegalArgumentException("Invalid event type field.");
		}
		
		if(startTime > 0){
			START_TIME = startTime - 1;
		}
		else{
			throw new IllegalArgumentException("Invalid start time field.");
		}
		
		if(endTime > 0){
			END_TIME = endTime - 1;			
		}
		else{
			throw new IllegalArgumentException("Invalid end time field");
		}
		
		return true;
	}
	
//Specify the file from which failure information need to be read
	
	public ArrayList<FailureEvent> generateFailure(){
		if(events == null){
			events = new ArrayList<FailureEvent>();
			
			fieldArray = new String[MAX_FIELD];
			
			try	{
				if(fileName.endsWith(".gz")){
					readGZIPFile(fileName);
				}
				else if(fileName.endsWith(".zip")){
					readZIPFile(fileName);
				}
				else{
					readFile(fileName);
				}
			}
			catch(FileNotFoundException e){
				//logger.log(Level.SEVERE, "File not found", e);
				//Logger.log(Level.SEVERE, "File node found", e);
				System.err.println("File not found : " + e.getMessage());
			}
			catch(IOException e)
			{
				//logger.log(Level.SEVERE, "Error reading file", e);
				System.err.println("IO operation failed: " + e.getMessage());
			}
		}
		return events;
	}
	
// This function reads the contents of an unzipped or uncompressed file one line at a time
	
	private boolean readFile(String flName) throws IOException, FileNotFoundException{
		boolean success = false;
		BufferedReader reader = null;
		
		try{
			FileInputStream file = new FileInputStream(flName);
			reader = new BufferedReader(new InputStreamReader(file));
			
			
			// Read one line at a time
			int line = 1;
			while(reader.ready()){
				parseValue(reader.readLine(), line);
				line++;
			}
			
			events.add(nodeEvent);
			
			reader.close();
			success = true;
		}
		finally	{
			if(reader != null){
				reader.close();
			}
		}
		return success;
	}
	
	// This function reads the contents of a gzip file one line at a time
	
	private boolean readGZIPFile(String flName) throws IOException, FileNotFoundException{
		
		boolean success = false;
		BufferedReader reader = null;
		
		try{
			FileInputStream file = new FileInputStream(flName);
			GZIPInputStream gz = new GZIPInputStream(file);
			reader = new BufferedReader(new InputStreamReader(gz));
			
			// reads one line at a time
			int line = 1;
			while(reader.ready()){
				parseValue(reader.readLine(), line);
				line++;
			}
		
			events.add(nodeEvent);
			
			reader.close();
			success = true;			
		}
		finally{
			if(reader!=null){
				reader.close();
			}
		}
		
		return success;
	}
	
// This function read the data from a file compressed using zip format.
	
	private boolean readZIPFile(String flName) throws IOException, FileNotFoundException{
		boolean success = false;
		ZipFile zipFile = null;
		
		try
		{
			BufferedReader reader = null;
			
			zipFile = new ZipFile(flName);
			Enumeration<? extends ZipEntry> e = zipFile.entries();
			while(e.hasMoreElements())
			{
				success = false;
				ZipEntry zipEntry = e.nextElement();
				
				reader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(zipEntry)));	
			
						
			int line = 1;
			while(reader.ready()){
				parseValue(reader.readLine(), line);
				line++;
			}
		
			events.add(nodeEvent);
			
			reader.close();
			success = true;			
			}
		}
		finally{
			if(zipFile!=null){
				zipFile.close();
			}
		}
		return success;
	}	
	
	
//This function breaks a line of string into many fields
	
	private void parseValue(String line, int lineNum){
		//skip the comment line
		
		if(line.startsWith(COMMENT)){
			return;
		}
		
		
		String[] sp = line.split("\\s+"); //split the line based on a space
		int len = 0;
		int index = 0;
		
		for(String elem : sp){
			len = elem.length();
			
			// if the string is empty
			if(len == 0){
				continue;
			}
			else{
				fieldArray[index] = elem;
				System.out.println(fieldArray[index]);
				index++;
			}
		}
	
		if(index == MAX_FIELD){
			extractField(fieldArray, lineNum);	
		}
	}
	
//This function extracts the information from the file after breaking it according to the space
	
	private void extractField(String [] array, int line){
		try	{
			Integer obj = null;
			
			// get the node ID
			obj = new Integer(array[HOST_ID].trim());
			int id = obj.intValue();
			
			
			// reset the counter for a new node
			if(id != curID){
				eventCtr = 0;
				addtolist = true;
				curID = id;
			}
			else{
				eventCtr++;
			}
			
			// get the type of an event
			obj = new Integer(array[EVENT_TYPE].trim());
			int eventType = obj.intValue();
			
			// get the start time of an event
			Double l = new Double(array[START_TIME].trim());
			double startTime = l.doubleValue();
			
			// get the end time of an event
			l = new Double(array[END_TIME].trim());
			double endTime = l.doubleValue();
			
			createFailureEvent(id, eventType, startTime, endTime, addtolist);
		}
		catch(Exception e)
		{
			//logger.log(Level.WARNING, "Exception reading file at line #" +line, e);
			System.out.println("Exception reading file at line #");
		}
	}

// This function read the data from a file compressed using gzip format. 
	
	
	
// This function creates a failure event from the extracted infomation from the file and adds them into a list
	
	private void createFailureEvent(int id, int eventType, double startTime, double endTime, boolean flag)	
	{
		if((startTime-TraceStartTime) < 0)
		{
			return;
		}
		
		if(nodeEvent == null)
		{
			nodeEvent = new FailureEvent(id, eventType, (startTime-TraceStartTime), (endTime-TraceStartTime));
			addtolist = false;
		}
		else
		{
			if(flag)
			{
				events.add(nodeEvent);
				nodeEvent = new FailureEvent(id, eventType, (startTime-TraceStartTime), (endTime-TraceStartTime));
				addtolist = false;
			}
			else
			{
				nodeEvent.insertEvent(id, eventType, (startTime-TraceStartTime), (endTime-TraceStartTime));
			}
		}
	}
}
	

/*	
	
public static void main(String [] args) throws IOException
{	
	
	/*try 
		{
			BufferedReader br = new BufferedReader(new FileReader("event_trace.tab"));
			try
				{
					String line = br.readLine();
					String[] columns = line.split("\\|");
					//System.out.println(line);
					System.out.println(columns);
				} 	
			catch (IOException ex)
				{
					// TODO Auto-generated catch block
					System.out.printf("Error, IO exception", ex);
				}
		} 
	catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			System.out.printf("Error, File not found", e);
		}
	
	*/
	
	//counting the number of lines in the file
	
	/*
	int total_number=0;
	int[] numbers=null;
	
	File file=new File("C:/Data/Australia/PhD australia/FTA/lanl05_tab_union/lanl05_tab_union/event_trace1.tab");
	//File file1=new File("test.txt");
	
	
	//File file1=new File("test.txt");
	try 
	{
		LineNumberReader lnr = new LineNumberReader(new FileReader(file));
		try {
			lnr.skip(Long.MAX_VALUE);
			total_number=lnr.getLineNumber();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			System.out.println("IO error");
	}
		System.out.printf("Total number of lines in the file are ");
		System.out.println(total_number);
		
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		String[] textData = new String[total_number];
		
	} 

	catch (FileNotFoundException ex1) 
	{
		System.out.println("File not found");
		
}
	
// Reading the contents of a file line by line	
	

	
	
	
	
	
}
}
	//try
	//{
	//PrintWriter output=new PrintWriter(file);
	//output.println("Yogesh Sharma");
	//output.print(1985);
	//output.close();	
//	}
	//catch(IOException ex)
	//{
	//	System.out.printf("Error :%s\n", ex);
	//}
/*	try
	{
	//@SuppressWarnings("resource")
	Scanner input=new Scanner(file);
	String xyz=input.nextLine();
	//int len=0;
	//int sca=0;
	//@SuppressWarnings("resource")
Scanner val=new Scanner(System.in);
	//String fieldArray[] = null;
	//for(int i=0;i<=2;i++)
	String[] stringArray=null;
	for(int i=0;i<=total_number-1;i++)
		{
		if(input.hasNextLine())
			{
				xyz=input.nextLine();
				stringArray=xyz.split("\\s+");		
				//System.out.println(stringArray[i]);
				//numbers=new int[stringArray.length];
			}
		}
	int sca=0;
	System.out.println("Enter the location of string array");
	sca=val.nextInt();
	PrintStream out;
	FileOutputStream fi=new FileOutputStream("test.txt");
	for(int i=0;i<=total_number-1;i++)
	{
		
		System.out.println(stringArray[sca]);
		out = new PrintStream(fi);
		System.setOut(out);
	}
	//for(int i=0;i<=total_number-1;i++)
	//{
		//System.out.println(stringArray[i]);
	//}
	//for(int j=0;j<stringArray.length;j++)
	//{
		//numbers[j]=Integer.parseInt(stringArray[j]);
		//System.out.println(numbers[j]);
	//}
*/	//}
			
			//HOST_ID[i]=Integer.parseInt(stringArray[3]);
			//System.out.println(HOST_ID[i]);
			
				//for(String srt : stringArray)
				//{
					//len=srt.length();
					//System.out.println(srt.length());
					//System.out.println(srt);
				//	count=count+1;
					//System.out.printf("length is  " +len);				
				//}
				

				//System.out.println("Enter the location of string array");
				//sca=val.nextInt();
				//for(i=0;i<=total_number-1;i++)
				//{
					
					//System.out.println(stringArray[sca]);
					/*try
					{
					fieldArray[index]=srt;
					index++;
					}
					catch(NullPointerException ex3)
					{
						System.out.println("Null pointer exception error");
					}*/
			
					//PrintWriter output=new PrintWriter(file1);
					//output.println(srt);
			//	}

					//System.out.println(stringArray);
			//boolean isWhitespace=xyz.matches(".*\\s+.*");
			//if(isWhitespace==true)
			//{
		//		System.out.println("");
		//	}
				//if(xyz=="HOST_ID")
					//splitted=xyz.split("\\s+");
					/*
					tokens=new StringTokenizer(xyz);
					splitted=new String[tokens.countTokens()];
					while(tokens.hasMoreTokens())
					{
						if(index<=5)
						{	
						splitted[index]=tokens.nextToken();
						index=index+1;
						}
						else
						{
							break;
						}
					}*/
					//System.out.println(splitted);
					//System.out.printf(xyz);
				//else
					//System.out.print("No Output");
	//}
	
	
	
	//}
	/*catch(IOException ex)
	{
		System.out.printf("File not found :%s/n. ex");
	}*/
	

//}

//}*/