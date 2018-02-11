package org.usfirst.frc.team4564.robot.path;

/**
 * A class representing a stage of a Path where the robot drives straight on a heading for a specified distance.
 * Created January 2018
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Evan McCoy
 */
public class DriveStraight extends Drive {
	private double target;
	private double power;
	
	public DriveStraight(double distance, double angle, double power) {
		super(false);
		this.target = distance;
		this.angle = angle;
		this.power = power;
	}
	
	public boolean isComplete() {
		return super.isComplete(target);
	}
	
	public double[] getDrive() {
		return super.getDrive(new double[] {power, power}, target);
	}
}
