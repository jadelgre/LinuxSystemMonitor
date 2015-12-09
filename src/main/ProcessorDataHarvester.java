package main;

import gui.SystemMonitorWindow;
import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;

public class ProcessorDataHarvester extends Harvester {

	private int numberOfProcessorCores = Runtime.getRuntime().availableProcessors();
	
	public ProcessorDataHarvester(SystemMonitorWindow theGUI) {
		super(theGUI);
	}

	@Override
	public void readData() {
		
		ArrayList<String> firstRead, secondRead;
		
		if((firstRead = getProcessorValues()) == null) { // if we failed to read the values
			return; 
		}
		
		try {
			Thread.sleep(250); // wait 250 ms before collecting data again
		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		}
		
		if((secondRead = getProcessorValues()) == null) { // if we failed to read the values
			return;
		}
		
		calculateAndGraph(firstRead,secondRead);
	}
	
	private ArrayList<String> getProcessorValues() {
		//System.out.println("Reading CPU data");
		ArrayList<String> values;
		BufferedReader reader;
		String line = "";
		try {
			reader = new BufferedReader(new FileReader("/proc/stat"));
			values = new ArrayList<String>();
			line = reader.readLine(); // read in the header since we don't need it
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		while(!line.contains("intr")) { // until the line after cpu data
			try {
				line = reader.readLine(); // read in the line
				if(line.contains("cpu")) { // if it contains cpu info
					String[] lineValues = line.split(" "); // split on space
					String temp = "";
					for(int i = 1; i < lineValues.length; i++) {
						temp += lineValues[i] + " "; // get the key cpu stats
					}
					values.add(temp); // add the values string to the array of cpu statistics
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return values;
	}
	
	private void calculateAndGraph(ArrayList<String> first, ArrayList<String> second) {
		for(int i = 0; i < numberOfProcessorCores; i+=2) { // run 4 times
			int usage = 0;
			for(int p = i; p < i+2; p++) { // for each pair of cores
				String[] firstTokens = first.get(i).split(" ");
				String[] secondTokens = second.get(i).split(" ");
	
				BigInteger user = new BigInteger(secondTokens[0]);
				BigInteger userPrevious = new BigInteger(firstTokens[0]);
				BigInteger userDiff = user.subtract(userPrevious).abs();
	//			System.out.println("userDiff " + userDiff);
				
				BigInteger system = new BigInteger(secondTokens[1]);
				BigInteger systemPrevious = new BigInteger(firstTokens[1]);
				BigInteger systemDiff = system.subtract(systemPrevious).abs();
	//			System.out.println("systemDiff " + systemDiff);
				
				BigInteger nice = new BigInteger(secondTokens[2]);
				BigInteger nicePrevious = new BigInteger(firstTokens[2]);
				BigInteger niceDiff = nice.subtract(nicePrevious).abs();
	//			System.out.println("niceDiff " + niceDiff);
				
				BigInteger idle = new BigInteger(secondTokens[3]);
				BigInteger idlePrevious = new BigInteger(firstTokens[3]);
				BigInteger idleDiff = idle.subtract(idlePrevious).abs();
	//			System.out.println("idle " + secondTokens[3]);
				
	//			System.out.println("idleDiff" + idleDiff);			
				
				double total = userDiff.floatValue() + systemDiff.floatValue() + niceDiff.floatValue() + idleDiff.floatValue();
				double idlePercent = idleDiff.floatValue() / total * 100;
				int cpuUtil = (int) (100.0 - idlePercent);
				
				usage += cpuUtil; // add the core cpu usage to the total
			}
			usage = usage / 2; // average the two cores together
			synchronized(userInterface) {
				userInterface.getCPUGraph().addDataPoint(i / 2, usage); // divide i by 2 to get value in the range of 0-3 for CPU core
				userInterface.getCPUGraph().repaint();
			}
		}
	}
}
