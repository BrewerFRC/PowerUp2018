package org.usfirst.frc.team4564.robot.path;

/**
 * A class to accommodate the motion of secondary parts on the robot during a drive train path.
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Evan McCoy
 * 
 */
public abstract class Event {
	protected Stage stage;
	protected boolean complete = false;
	protected boolean holdStage;
	
	public Event(boolean holdStage) {
		this.holdStage = holdStage;
	}
	
	/**
	 * Whether or not event must complete for the Path to progress to the next stage.
	 * 
	 * @return hold stage
	 */
	public boolean isHoldStage() {
		return holdStage;
	}
	
	/**
	 * Sets the stage this event is attached to.
	 * 
	 * @param stage - the stage
	 */
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	/**
	 * Sets the event to not complete.
	 */
	public void reset() {
		this.complete = false;
	}
	
	/**
	 * Returns whether the event has been completed.
	 * 
	 * @return boolean - complete
	 */
	public boolean complete() {
		return complete;
	}
	
	/**
	 * The custom event code to run event startup code.
	 */
	public abstract void start();
	
	/**
	 * The custom event code to handle motion and logic of robot parts.
	 * Should also contain the condition for initiating the event and determining whether it is complete.
	 */
	public abstract void trigger();
}
