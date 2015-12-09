package main;

import gui.*;

public abstract class Harvester extends Thread {
	private int numberOfCores;
	protected SystemMonitorWindow userInterface;
	
	public Harvester(SystemMonitorWindow theGUI) {
		userInterface = theGUI;
	}
	
	public synchronized void collect() {
		this.notifyAll();
	}
	
	public abstract void readData(); // abstract method to be implemented by subclasses
		
	public synchronized void run() { // run the harvester object 
		while(true) { // while the thread is not interrupted
			//System.out.println("run");
			try{
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			readData();
		}
	}
}
