package main;

import java.util.ArrayList;

import gui.*;

public abstract class Harvester extends Thread {

	protected SystemMonitorWindow userInterface;
	
	/**
	 * Constructor for harvester base class 
	 * @param theGUI taken so that we can add data to the graph
	 */
	public Harvester(SystemMonitorWindow theGUI) {
		userInterface = theGUI;
	}
	
	/**
	 * Wake all harvester threads
	 */
	public synchronized void collect() {
		this.notifyAll();
	}
	
	/**
	 * Wait until interrupted by the scheduler
	 */
	private synchronized void pause() {
		try{
			this.wait(); // wait until woken by scheduler
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * abstract method to be implemented by subclasses for reading data specific to that harvester
	 */
	public abstract void readData(); 
		
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public synchronized void run() { // run the harvester object 
		while(true) { // while the thread is not interrupted
			this.pause();
			this.readData();
		}
	}
}
