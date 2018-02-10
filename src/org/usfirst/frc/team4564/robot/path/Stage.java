package org.usfirst.frc.team4564.robot.path;

import java.util.ArrayList;
import java.util.List;

/**
 * An interface representing a generic stage of driving in a Path.
 * Created January 2018
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Evan McCoy
 * 
 */
public abstract class Stage {
	private List<Event> events = new ArrayList<Event>();
	private boolean hold;
	private boolean persist;
	
	public Stage(boolean hold) {
		this.hold = hold;
		this.persist = false;
	}
	
	public Stage(boolean hold, boolean persist) {
		this.hold = hold;
		this.persist = persist;
	}
	
	/**
	 * Resets the stage.
	 */
	public void reset() {
		for (Event event : events) {
			event.reset();
		}
	}
	
	/**
	 * Runs trigger routine for all events.  Should only activate events which have met a starting condition.
	 */
	public void triggerEvents() {
		for (Event event : events) {
			event.trigger();
		}
	 }
	  	
	/**
	 * Adds an event to the Stage.
	 * 
	 * @param event - the event
	 */
	public void addEvent(Event event) {
		this.events.add(event);
	  }
	
	/**
	 * Complete the initializing steps of the stage.
	 */
	public abstract void start();
	
	/**
	 * Whether or not this stage has completed its target.
	 * 
	 * @return boolean - complete
	 */
	public abstract boolean isComplete();
	
	/**
	 * An array of left/right motor powers for the current stage.
	 * 
	 * @return double[] - left/right motor powers
	 */
	public abstract double[] getDrive();
	
	public boolean isHeld() {
		return hold;
	}
	
	public boolean isPersist() {
		return persist;
	}
}
