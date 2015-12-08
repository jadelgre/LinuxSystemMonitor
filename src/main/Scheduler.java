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
			this.sleep(delayInMS);
			//wait(delayInMS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		for (Harvester h : tasks) {
			Thread task = new Thread(h); // create a new thread for that task and run it
			task.start();	
			//System.out.println("z");
		}
		while (true)
		{
			//System.out.println("a");
			// Wait for the specified timeout
			this.delay();
			//System.out.println("b");
			// Dispatch each harvester to collect data/update gui.
			
			// For those unfamiliar with Java, this is the equivalent of
			// a for-each loop... For each Harvester h in Tasks...do...
			for (Harvester h : tasks) {
			 	 synchronized(h) {
			 		//System.out.println("c");
			 		h.notifyAll();
			 	 }
			}
		}
	}
}
