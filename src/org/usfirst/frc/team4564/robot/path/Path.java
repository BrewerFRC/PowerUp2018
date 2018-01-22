package org.usfirst.frc.team4564.robot.path;

import java.util.ArrayList;
import java.util.List;

import org.usfirst.frc.team4564.robot.Common;

/**
 * A class representing a collection of drive stages that forms a continuous drive path.
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Evan McCoy
 * 
 */
public class Path {
	private List<Stage> stages = new ArrayList<Stage>();
	private int state = 0;
	
	public void start() {
		stages.get(0).start();
	}
	
	/**
	 * Reset the path collection to its first stage.
	 */
	public void reset() {
		state = 0;
		for (Stage stage : stages) {
			stage.reset();
		}
	}
	
	/**
	 * Get the left and right motor powers of the current drive state. Updates to next stage if current is complete.
	 * 
	 * @return double[] - The left/right motor powers, or [0, 0] if the path is complete.
	 */
	public double[] getDrive() {
		Common.dashNum("stage", state);
		if (state >= stages.size()) {
			return new double[] {0, 0};
		}
		if (stages.get(state).isComplete()) {
			state++;
			stages.get(state).start();
		}
		stages.get(state).triggerEvents();
		return stages.get(state).getDrive();
	}
	
	/**
	 * Whether or not the path has completed all stages.
	 * 
	 * @return boolean - complete
	 */
	public boolean isComplete() {
		return state >= stages.size();
	}
	
	/**
	 * Adds another straight drive stage with the given parameters.
	 * 
	 * @param distance - the distance to drive straight before advancing to next stage.
	 * @param heading - the heading to drive on
	 * @param power - the power to apply to the drive train
	 * @return {@link Path Path} - the current Path instance
	 */
	public Path addDriveStraight(double distance, double heading, double power) {
		stages.add(new DriveStraight(distance, heading, power));
		return this;
	}
	
	/**
	 * Adds another PID controlled drive stage with the given parameters.
	 * 
	 * @param distance - the distance to drive
	 * @param minPower - the minimum power for the PID to output
	 * @param maxPower - the maximum power for the PID to output
	 * @param p - the P scalar
	 * @param i - the I scalar
	 * @param d - the D scalar
	 * @param name - the name of the PID for SmartDashboard tuning.
	 * @return {@link Path Path} - the current Path instance
	 */
	public Path addPIDDrive(double distance, double heading, double minPower, double maxPower, double p, double i, double d, boolean inverted, String name) {
		stages.add(new PIDDrive(distance, heading, minPower, maxPower, p, i, d, inverted, name));
		return this;
	}
	
	/**
	 * Adds another power turn stage with the given parameters.
	 * 
	 * @param targetAngle - the angle to stop turning once reached
	 * @param power - the power to apply to the turning wheel
	 * @return {@link Path Path} - the current Path instance
	 */
	public Path addPowerTurn(double targetAngle, double power) {
		stages.add(new PowerTurn(targetAngle, power));
		return this;
	}
	
	/**
	 * Adds another power turn stage with the given parameters.  Only the non-zero turn side is derived from this stage.
	 * The normally zero power is derived from the previous stage.
	 * 
	 * @param targetAngle - the angle to stop turning once reached
	 * @param power - the power to apply to the turning wheel
	 * @return {@link Path Path} - the current Path instance
	 */
	public Path addPowerTurnOverlay(double targetAngle, double power) {
		stages.add(new PowerTurn.PowerTurnOverlay(targetAngle, power, stages.get(stages.size() - 1)));
		return this;
	}
	
	/**
	 * Adds an event to the last Stage in the path.
	 * 
	 * @param event - the event
	 * @return {@link Path Path} - the current Path instance
	 */
	public Path addEvent(Event event) {
		if (stages.size() > 0) {
			Stage stage = stages.get(stages.size() - 1);
			event.setStage(stage);
			stage.addEvent(event);
		}
		return this;
	}
}
