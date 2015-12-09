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
		for( File f : theFile.listFiles() ) { // for each file/folder in /proc/
			String dirName = "";
			BufferedReader reader = null;
			try{ // check to see if it contains an integer, if so it is a pid
				int dirNumber = Integer.parseInt(f.getName());
				//System.out.println(dirNumber);
				reader = new BufferedReader(new FileReader("/proc/" + dirNumber + "/status")); // open the status file for that process
				ArrayList<String> currentProcess = new ArrayList<String>();
				String currentLine = null;
				while( (currentLine = reader.readLine()) != null) { // until the end of the file
					currentProcess.add(currentLine);
				}
				//System.out.println(currentProcess.size());
				reader.close();
				// name, pid, state, threads, vol ctx switch, nonVol ctx sw
				String[] dataToDisplay = tokenize(currentProcess);
				//System.out.println(dataToDisplay[3]);
				userInterface.addRowToProcList(dataToDisplay);
			} catch (NumberFormatException e) { // if it's not a pid folder
				// do nothing lol
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private String[] tokenize(ArrayList<String> process) { // takes a line of data and returns all of the tokens to display
		String name = null, pid = null, state = null, threads = null, volctx = null, nonvol = null;
		for(String lineOfData : process){
			// split the line on the tab
			String[] tokens = lineOfData.split("\t");
			if(tokens.length < 2) continue; // if for some reason it doesn't have everything, skip to the next line
			String id = tokens[0].trim();
			String value = tokens[1];
			//System.out.println(id);
			if(id.equals("Name:")) {
//				System.out.println("name");
				name = value;
			} else if (id.equals("Pid:")) {
//				System.out.println("pid");
				pid = value;
			} else if (id.equals("State:")) {
//				System.out.println("st");
				state = value;
			} else if (id.equals("Threads:")) {
//				System.out.println("tc");
				threads = value;
			} else if (id.equals("voluntary_ctxt_switches:")) {
//				System.out.println("v");
				volctx = value;
			} else if (id.equals("nonvoluntary_ctxt_switches:")) {
//				System.out.println("nv");
				nonvol = value;
			}
		}
		return new String[]{name, pid, state, threads, volctx, nonvol};
	}
}
