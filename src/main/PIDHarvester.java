package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import gui.SystemMonitorWindow;

public class PIDHarvester extends Harvester {


	public PIDHarvester(SystemMonitorWindow theGUI) {
		super(theGUI);
	}

	@Override
	public void readData() {
		userInterface.removeAllRowsFromProcList(); // remove all of the current rows
		
		File theFile = new File ("/proc/");
		ArrayList<String[]> allpids = new ArrayList<String[]>();
		
		for( File f : theFile.listFiles() ) { // for each file/folder in /proc/
			String dirName = "";
			BufferedReader reader = null;
			int dirNumber;
			try{ // check to see if it contains an integer, if so it is a pid
				dirNumber = Integer.parseInt(f.getName());				
			} catch (NumberFormatException e) { // if it's not a pid folder
				// skip to the next name
				continue;
			}
			
			// if it's a pid
			// get array of string tokens, add to array list of pids to be written
			ArrayList<String> currentProcess = readFile(dirNumber);
			String[] dataToDisplay = tokenize(currentProcess); // name, pid, state, threads, vol ctx switch, nonVol ctx sw
			allpids.add(dataToDisplay);
		}
		
		// add all pids to the gui
		for(String[] pid : allpids) {
			userInterface.addRowToProcList(pid);
		}
	}
	
	private ArrayList<String> readFile(int pidNumber){
		BufferedReader reader = null;
		ArrayList<String> currentProcess = null;
		try {
			reader = new BufferedReader(new FileReader("/proc/" + pidNumber + "/status"));
			currentProcess = new ArrayList<String>();
			String currentLine = null;
			
			while( (currentLine = reader.readLine()) != null) { // until the end of the file
				currentProcess.add(currentLine);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return currentProcess;
	}
	
	/**
	 * Helper function that takes an arraylist containing all lines of data for a given pid and returns all of the tokens for its table entry
	 * 
	 * @param process an arraylist containing all lines of data for a given pid
	 * @return
	 */
	private String[] tokenize(ArrayList<String> process) { 
		String name = null, pid = null, state = null, threads = null, volctx = null, nonvol = null;
		
		for(String lineOfData : process){
			// split the line on the tab
			String[] tokens = lineOfData.split("\t");
			if(tokens.length < 2) continue; // if for some reason it doesn't have everything, skip to the next line
			
			String id = tokens[0].trim();
			String value = tokens[1];

			// get each token
			if(id.equals("Name:")) {
				name = value;
			} else if (id.equals("Pid:")) {
				pid = value;
			} else if (id.equals("State:")) {
				state = value;
			} else if (id.equals("Threads:")) {
				threads = value;
			} else if (id.equals("voluntary_ctxt_switches:")) {
				volctx = value;
			} else if (id.equals("nonvoluntary_ctxt_switches:")) {
				nonvol = value;
			}
		}
		return new String[]{name, pid, state, threads, volctx, nonvol};
	}
}
