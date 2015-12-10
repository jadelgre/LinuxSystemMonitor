/**
 * 
 */
package main;

import gui.SystemMonitorWindow;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * @author jadelgre
 *
 */
public class MemoryHarvester extends Harvester {

	public MemoryHarvester(SystemMonitorWindow theGUI) {
		super(theGUI);
	}

	/* (non-Javadoc)
	 * @see main.Harvester#readData()
	 */
	@Override
	public void readData() {
		ArrayList<String> linesFromFile = readFile();		
		tokenizeAndGraphMemoryValues(linesFromFile);		
	}

	private ArrayList<String> readFile() {
		ArrayList<String> values = null;
		BufferedReader reader = null;
		String line = null;
		
		try {
			reader = new BufferedReader(new FileReader("/proc/meminfo"));
			values = new ArrayList<String>();
			
			while((line = reader.readLine()) != null) { // until eof, read in line and add to array list
				values.add(line);
			}
			
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return values;
	}
	
	private void tokenizeAndGraphMemoryValues (ArrayList<String> lines) {
		// initialized to -1 for error case
		int totalMem = -1, memFree = -1, memActive = -1, memInactive = -1, swapTotal = -1, swapFree = -1, dirtyPages = -1, writeback = -1;
		
		for(String line : lines) { // for each line, get relevant tokens
			String[] tokens = line.split(" +"); // split on one or more spaces
			if(tokens.length < 2) continue; // if for some reason it doesn't have everything, skip to the next line
			
			String identifier = tokens[0];
			String value = tokens[1];

			// set the values
			if(identifier.equals("MemTotal:")) {
				totalMem = Integer.parseInt(value);
			} else if (identifier.equals("MemFree:")) {
				memFree = Integer.parseInt(value);
			} else if (identifier.equals("Active:")) {
				memActive = Integer.parseInt(value);
			}else if (identifier.equals("Inactive:")) {
				memInactive = Integer.parseInt(value);
			}else if (identifier.equals("SwapTotal:")) {
				swapTotal = Integer.parseInt(value);
			}else if (identifier.equals("SwapFree:")) {
				swapFree = Integer.parseInt(value);
			}else if (identifier.equals("Dirty:")) {
				dirtyPages = Integer.parseInt(value);
			}else if (identifier.equals("Writeback:")) {
				writeback = Integer.parseInt(value);
			}
			
		}
		
		userInterface.updateMemoryInfo(totalMem, memFree, memActive, memInactive, swapTotal, swapFree, dirtyPages, writeback);
	}
}
