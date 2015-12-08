package main;

import java.util.ArrayList;

import gui.SystemMonitorWindow;

public class SystemMonitor {
	private static int delay;
	private static Scheduler processorScheduler;
	
	public static void setUpdateInterval(int updateInterval)
	{
		// This is called when the user selects a new update interval
		// from the GUI
		delay = updateInterval;
		processorScheduler.updateDelay(delay);
	}
	public static void main (String[] args)
	{
		SystemMonitorWindow mySysMon = new SystemMonitorWindow();
		
		// initialize the CPU data harvester
		ArrayList<Harvester> processorArray = new ArrayList<Harvester>();
		processorArray.add(new ProcessorDataHarvester(mySysMon));
		processorScheduler = new Scheduler(delay, processorArray);
		processorScheduler.start();

		
	}
}
