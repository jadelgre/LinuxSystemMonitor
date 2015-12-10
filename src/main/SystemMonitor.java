package main;

import java.util.ArrayList;

import gui.SystemMonitorWindow;

public class SystemMonitor {
	private static int delay = 1000; // 1 second default delay for CPU/mem
	private static int pidDelay = 6000; // 6 second pid delay
	private static Scheduler processorScheduler;
	private static Scheduler pidScheduler;
	
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
		
		// initialize the CPU and mem data harvester arraylist
		ArrayList<Harvester> processorArray = new ArrayList<Harvester>();
		processorArray.add(new ProcessorDataHarvester(mySysMon));
		processorArray.add(new MemoryHarvester(mySysMon));
		
		processorScheduler = new Scheduler(delay, processorArray);
		processorScheduler.start();
		
		// initialize the harvester for PID 
		ArrayList<Harvester> pidArray = new ArrayList<Harvester>();
		pidArray.add(new PIDHarvester(mySysMon));
		
		pidScheduler = new Scheduler(pidDelay, pidArray);
		pidScheduler.start();

		
	}
}
