package main;

import gui.SystemMonitorWindow;
import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;

public class ProcessorDataHarvester extends Harvester {
	private int numCores;
	
	public ProcessorDataHarvester(SystemMonitorWindow theGUI, int cpuCores) {
		super(theGUI);
		numCores = cpuCores;
	}

	@Override
	public void readData() {
		
		ArrayList<String> firstRead, secondRead;
		
		if((firstRead = getProcessorValues()) == null) { // if we failed to read the values 
			return; 
		}
		
		waitBetweenRead();
		
		if((secondRead = getProcessorValues()) == null) { // if we failed to read the values
			return;
		}
		
		calculateAndGraph(firstRead,secondRead);
	}
	
	/**
	 * Helper function for waiting between file reads
	 */
	private void waitBetweenRead() {
		try {
			wait(250); // wait 250 ms before collecting data again
		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * Helper function that reads in lines containing cpu usage data for each core
	 * 
	 * @return array list of strings, each containing a core's usage data
	 */
	private ArrayList<String> getProcessorValues() {
		//System.out.println("Reading CPU data");
		ArrayList<String> values;
		BufferedReader reader;
		String line = "";
		try {
			reader = new BufferedReader(new FileReader("/proc/stat"));
			values = new ArrayList<String>();
			line = reader.readLine(); // read in the header since we don't need it
			
			while(!line.contains("intr")) { // until the line after cpu data
				line = reader.readLine(); // read in the line
				if(line.contains("cpu")) { // if it contains cpu info
					String[] lineValues = line.split(" "); // split on space
					String temp = "";
					for(int i = 1; i < lineValues.length; i++) {
						temp += lineValues[i] + " "; // get the key cpu stats
					}
					values.add(temp); // add the values string to the array of cpu statistics
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return values;
	}
	
	/**
	 * A helper function that calculates the cpu usage for each core and then adds the data to the graph.
	 * 
	 * @param first arraylist of strings containing cpu usage info from the initial read
	 * @param second arraylist of strings containing cpu usage info from the second read
	 */
	private void calculateAndGraph(ArrayList<String> first, ArrayList<String> second) {
		for(int i = 0; i < numCores; i++) {
				String[] firstTokens = first.get(i).split(" ");
				String[] secondTokens = second.get(i).split(" ");
				
				// calculate user2 - user1
				double user = Double.parseDouble(secondTokens[0]);
				double userPrevious = Double.parseDouble(firstTokens[0]);
				double userDiff = Math.abs(user-userPrevious);
				
				// calculate system2 - system1
				double system = Double.parseDouble(secondTokens[1]);
				double systemPrevious = Double.parseDouble(firstTokens[1]);
				double systemDiff = Math.abs(system-systemPrevious);
				
				// calculate nice2 - nice1
				double nice = Double.parseDouble(secondTokens[2]);
				double nicePrevious = Double.parseDouble(firstTokens[2]);
				double niceDiff = Math.abs(nice-nicePrevious);
				
				// calculate idle2 - idle1
				double idle = Double.parseDouble(secondTokens[3]);
				double idlePrevious = Double.parseDouble(firstTokens[3]);
				double idleDiff = Math.abs(idle-idlePrevious);
								
//				// calculate CPU usage using method from http://www.pplusdomain.net/cgi-bin/blosxom.cgi/2009/04/02
//				double total = userDiff + systemDiff + niceDiff + idleDiff;
//				double idlePercent = idleDiff / total * 100;
//				int cpuUtil2 = (int) (100.0 - idlePercent);
				
				// calculate CPU usage using method from https://piazza.com/class/ic02ole9e534k8?cid=137
				double numerator = systemDiff + userDiff;
				double denominator = numerator + idleDiff;
				int cpuUtil = (int) ((numerator / denominator) * 100.0);
				
				// Write cpu usage for this core to the graph
				userInterface.getCPUGraph().addDataPoint(i, cpuUtil);
				userInterface.getCPUGraph().repaint();
		}
	}
}
