package main;

import gui.*;

public abstract class Harvester implements Runnable {
	private int numberOfCores;
	protected SystemMonitorWindow userInterface;
	
	public Harvester(SystemMonitorWindow theGUI) {
		userInterface = theGUI;
	}
	
	public abstract void readData(); // abstract method to be implemented by subclasses
		
	public synchronized void run() { // run the harvester object 
		while(true) { // while the thread is not interrupted
			try{
				readData();
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
