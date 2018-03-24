package org.usfirst.frc.team4564.robot.path;

import java.util.ArrayList;
import java.util.List;

import org.usfirst.frc.team4564.robot.Common;
import org.usfirst.frc.team4564.robot.DriveTrain;

/**
 * A class representing a collection of drive stages that forms a continuous drive path.
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Evan McCoy
 */
public class Path {
	private static DriveTrain dt;
	private List<Stage> stages = new ArrayList<Stage>();
	private int state = 0;
	private Stage[] edge;
	
	public void start() {
		dt = DriveTrain.instance();
		dt.getHeading().reset();
		dt.resetEncoders();
		stages.get(0).start();
		stages.get(0).startEvents();
	}
	
	/**
	 * Reset the path collection to its first stage.
	 */
	public void reset() {
		state = 0;
		for (Stage stage : stages) {
			stage.reset();
		}
		//start();
	}
	
	/**
	 * Get the left and right motor powers of the current drive state. Updates to next stage if current is complete.
	 * 
	 * @return The left/right motor powers, or [0, 0] if the path is complete.
	 */
	public double[] getDrive() {
		for (int i = state;i >= 0;i--) {
			if (i < stages.size()) {
				stages.get(i).triggerEvents();
			}
		}
		if (state >= stages.size()) {
			if (stages.get(stages.size() - 1).isPersist()) {
				return stages.get(stages.size() - 1).getDrive();
			}
			return new double[] {0, 0};
		}
		if (stages.get(state).isComplete()) {
			//The state is changing. Initialize edge with previous and new stage instances.
			edge = new Stage[2];
			edge[0] = stages.get(state);
			state++;
			System.out.println("Path#getDrive: State Switch - New State = " + state);
			if (state < stages.size()) {
				edge[1] = stages.get(state);
				stages.get(state).start();
				stages.get(state).startEvents();
				if (stages.get(state - 1).isHeld()) {
					return stages.get(state - 1).getDrive();
				}
			}
			else {
				return new double[] {0, 0};
			}
		}
		for (int i = state;i >= 0;i--) {
			stages.get(i).triggerEvents();
		}
		return stages.get(state).getDrive();
	}
	
	/**
	 * Drives the motors with the current Path motor powers.
	 */
	public void drive() {
		double[] power = getDrive();
		if (isEdge(PowerTurn.class)) {
			Common.debug("Power Turn Edge");
			DriveTrain.instance().tankDrive(power[0], power[1]);
		}
		else {
			DriveTrain.instance().accelTankDrive(power[0], power[1]);
		}
		DriveTrain.instance().applyTankDrive();
	}
	
	/**
	 * Whether or not the path has completed all stages.
	 * 
	 * @return complete
	 */
	public boolean isComplete() {
		return state >= stages.size();
	}
	
	/**
	 * Returns the index of the current stage.
	 * 
	 * @return the index.
	 */
	public int currentStage() {
		return state;
	}
	
	/**
	 * Returns whether or not the specified type is a member of the current Stage change.
	 * 
	 * @param type - the class type to check.
	 * @return is edge.
	 */
	public boolean isEdge(Class<? extends Stage> type) {
		//If the last cycle created an edge with both a previous and new stage.
		if (edge != null && edge.length == 2 && edge[0] != null && edge[1] != null) {
			//Whether either of the stages in the edge match the requested class type.
			boolean isEdge = edge[0].getClass().equals(type) || edge[1].getClass().equals(type);
			edge = null;
			return isEdge;
		}
		return false;
	}
	
	/**
	 * Adds another straight drive stage with the given parameters.
	 * 
	 * @param distance - the distance to drive straight before advancing to next stage.
	 * @param heading - the heading to drive on
	 * @param power - the power to apply to the drive train
	 * @return the current Path instance
	 */
	public Path addDriveStraight(double distance, double heading, double power, String name) {
		stages.add(new DriveStraight(distance, heading, power, name));
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
	 * @return the current Path instance
	 */
	public Path addPIDDrive(double distance, double heading, double minPower, double maxPower, boolean inverted, String name) {
		stages.add(new PIDDrive(distance, heading, minPower, maxPower, inverted, name));
		return this;
	}
	
	public Path addDrivePower(double power) {
		stages.add(new DrivePower(power));
		return this;
	}
	
	/**
	 * Adds another power turn stage with the given parameters.
	 * 
	 * @param targetAngle - the angle to stop turning once reached
	 * @param power - the power to apply to the turning wheel
	 * @return the current Path instance
	 */
	public Path addPowerTurn(double targetAngle, double power, boolean backward) {
		stages.add(new PowerTurn(targetAngle, power, backward));
		return this;
	}
	
	/**
	 * Adds another power turn stage with the given parameters.  Only the non-zero turn side is derived from this stage.
	 * The normally zero power is derived from the previous stage.
	 * 
	 * @param targetAngle - the angle to stop turning once reached
	 * @param power - the power to apply to the turning wheel
	 * @return the current Path instance
	 */
	public Path addPowerTurnOverlay(double targetAngle, double power, boolean backward) {
		stages.add(new PowerTurn.PowerTurnOverlay(targetAngle, power, backward, stages.get(stages.size() - 1)));
		return this;
	}
	
	/**
	 * Adds a drive to wall with the given parameters.
	 * MUST HAVE VELOCITY BEFORE IT IS RUN
	 * 
	 * @param distance -Distance to run
	 * @param heading -Heading to move at
	 * @param power -Power to move at
	 * @param name -Not used
	 * @return
	 */
	public Path addDriveToWall(double distance, double heading, double power, String name) {
		stages.add(new DriveToWall(distance, heading, power));
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
