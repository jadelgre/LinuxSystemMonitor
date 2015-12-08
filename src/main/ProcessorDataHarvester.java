package main;

import gui.SystemMonitorWindow;
import java.io.*;
import java.util.ArrayList;

public class ProcessorDataHarvester extends Harvester {

	private int numberOfProcessorCores = Runtime.getRuntime().availableProcessors();
	
	public ProcessorDataHarvester(SystemMonitorWindow theGUI) {
		super(theGUI);
	}

	@Override
	public void readData() {
/*		ArrayList<String> firstRead, secondRead;
		
		if((firstRead = getProcessorValues()) == null) {
			return; 
		}
		
		try {
			Thread.sleep(250); // wait 250 ms before collecting data again
		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		}
		
		if((secondRead = getProcessorValues()) == null) {
			return;
		}*/
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ArrayList<String> firstRead, secondRead;
				
				if((firstRead = getProcessorValues()) == null) {
					return; 
				}
				
				try {
					Thread.sleep(250); // wait 250 ms before collecting data again
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
				
				if((secondRead = getProcessorValues()) == null) {
					return;
				}
				
				calculateAndGraph(firstRead,secondRead);
			}
		});
		
		
/*		System.out.println("Reading CPU data");
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("/proc/stat"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}*/
		
/*		ArrayList<ArrayList<String>> values = new ArrayList<ArrayList<String>>();)
		ArrayList<String> firstRead = new ArrayList<String>();
		ArrayList<String> secondRead = new ArrayList<String>();
		values.add(firstRead);
		values.add(secondRead);
		
		for(int i = 0; i < values.size(); i++) { // for each array list in the array list of values
			
		}*/
		
/*		try { // read the line 
			firstLine = reader.readLine();
			Thread.sleep(250);
			secondLine = reader.readLine();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("first " + firstLine);
		System.out.println("second " + secondLine);*/
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
					//System.out.println(temp);
					values.add(temp); // add the values string to the array of cpu statistics
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return values;
	}
	
	private void calculateAndGraph(ArrayList<String> first, ArrayList<String> second) {
		for(int i = 0; i < numberOfProcessorCores; i++) { // for each core
			//System.out.println(first.get(i));
			String[] firstTokens = first.get(i).split(" ");
			String[] secondTokens = second.get(i).split(" ");
/*			for(int k = 0; k < firstTokens.length; k++) {
				System.out.println(firstTokens[k]);
			}*/
			//int user, nice, system, idle, iowait, irq, softirq, steal, guest, guest_nice;
			int userTime1, userTime2, idle1, idle2, system1, system2, iowait1, iowait2;
			
			userTime1 = Integer.parseInt(firstTokens[0]);
			system1 = Integer.parseInt(firstTokens[2]);
			idle1 = Integer.parseInt(firstTokens[3]);
			iowait1 = Integer.parseInt(firstTokens[4]);
			//System.out.println(userTime1 + " " + system1 + " " + idle1 + " " + iowait1);
			
			userTime2 = Integer.parseInt(secondTokens[0]);
			system2 = Integer.parseInt(secondTokens[2]);
			idle2 = Integer.parseInt(secondTokens[3]);
			iowait2 = Integer.parseInt(secondTokens[4]);
			
			
/*			int activeTime1 = userTime1 + system1 + iowait1;
			int totalTime1 = activeTime1 + idle1;
			int userIdleTime1 = totalTime1 - userTime1;
			
			int activeTime2 = userTime2 + system2 + iowait2;
			int totalTime2 = activeTime2 + idle2;
			int userIdleTime2 = totalTime2 - userTime2;*/
						
			Double numerator = (double) (userTime2-userTime1) + ( idle2 - idle1 ) + (system2 - system1);
			//System.out.println(numerator);
			Double denominator = numerator + (iowait2 - iowait1);
			//System.out.println(denominator);
			int value = (int) (numerator / denominator) * 100;
			//System.out.println((double)numerator/denominator);
			//System.out.println("core " + i + first.get(i) + " second " + second.get(i));
			synchronized(userInterface) {
				userInterface.getCPUGraph().addDataPoint(i, 100);
				userInterface.getCPUGraph().repaint();
			}
		}
	}
	

	private double calculateProcessorUsage(ArrayList<String> first, ArrayList<String> second) {
		
		return -1.0;
	}
}
