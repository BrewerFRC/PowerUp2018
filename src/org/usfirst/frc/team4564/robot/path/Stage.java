package org.usfirst.frc.team4564.robot.path;

/**
 * An interface representing a generic stage of driving in a Path.
 * Created January 2018
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Evan McCoy
 * 
 */
public abstract class Stage {
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
