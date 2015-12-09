package main;

import java.util.ArrayList;

public class Scheduler extends Thread {
	
	private ArrayList<Harvester> tasks; // array list of all tasks 
	private int delayInMS; // variable to store time delay in milliseconds
	
	public Scheduler(int delay, ArrayList<Harvester> taskList)
	{
		//System.out.println(taskList.size());
		tasks = taskList;
		delayInMS = delay;
	}
	
	public void updateDelay(int newDelay) {
		//System.out.println("Updating delay in scheduler to " + newDelay);
		delayInMS = newDelay;
	}

	private synchronized void delay()
	{
		// Here we acquire the lock for our current instance of our scheduler
		// and wait for the specified timeout
		try
		{	
			Thread.sleep(delayInMS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		notifyAll();
	}
	
	public void run() {
		for (Harvester h : tasks) {
			h.start();	
		}
		while (true)
		{
			// Wait for the specified timeout
			this.delay();
			// Dispatch each harvester to collect data/update gui.
			
			// For those unfamiliar with Java, this is the equivalent of
			// a for-each loop... For each Harvester h in Tasks...do...
			for (Harvester h : tasks) {
			 	h.collect();
			}
		}
	}
}
