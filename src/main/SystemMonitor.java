package main;

import java.util.ArrayList;

import gui.SystemMonitorWindow;

public class SystemMonitor {
	private static int delay = 1000; // 1 second default delay for CPU/mem
	private static int pidDelay = 6000; // 6 second pid delay
	private static Scheduler processorScheduler;
	private static Scheduler pidScheduler;
	private static int cpuCores; // used when calculating CPU usage, also used for graphing CPU & RAM usage
	
	/**
	 * @param updateInterval the new interval which we want to update the data at
	 */
	public static void setUpdateInterval(int updateInterval)
	{
		// This is called when the user selects a new update interval
		// from the GUI
		delay = updateInterval;
		processorScheduler.updateDelay(delay);
	}
	
	/**
	 * Main method, creates harvesters, creates schedulers, adds harvesters to respective schedulers, and then starts schedulers.
	 * 
	 * @param args
	 */
	public static void main (String[] args)
	{
		cpuCores = Runtime.getRuntime().availableProcessors();
		
		// create a new system monitor window, passing in number of cpuCores so it knows how many lines to graph
		SystemMonitorWindow mySysMon = new SystemMonitorWindow(cpuCores);
		
		// initialize the CPU and mem data harvester arraylist
		ArrayList<Harvester> processorArray = new ArrayList<Harvester>();
		processorArray.add(new ProcessorDataHarvester(mySysMon,cpuCores));
		processorArray.add(new MemoryHarvester(mySysMon, cpuCores));
		
		// create the CPU and mem scheduler and then start it
		processorScheduler = new Scheduler(delay, processorArray);
		processorScheduler.start();
		
		// initialize the harvester for PID 
		ArrayList<Harvester> pidArray = new ArrayList<Harvester>();
		pidArray.add(new PIDHarvester(mySysMon));
		
		// create the PID scheduler and then start it
		pidScheduler = new Scheduler(pidDelay, pidArray);
		pidScheduler.start();

		
	}
}
