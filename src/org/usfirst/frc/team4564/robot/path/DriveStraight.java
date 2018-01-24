package org.usfirst.frc.team4564.robot.path;

import org.usfirst.frc.team4564.robot.Common;
import org.usfirst.frc.team4564.robot.DriveTrain;

/**
 * A class representing a stage of a Path where the robot drives straight on a heading for a specified distance.
 * Created January 2018
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Evan McCoy
 */
public class DriveStraight extends Stage {
	private double target;
	private double heading;
	private double power;
	
	public DriveStraight(double distance, double heading, double power) {
		this.target = distance;
		this.heading = heading;
		this.power = power;
	}
	
	public void start() {
		DriveTrain.instance().resetEncoders();
	}
	
	public boolean isComplete() {
		return DriveTrain.instance().getAverageDist() >= target;
	}
	
	public double[] getDrive() {
		Common.dashNum("targetHeading", heading);
		System.out.println(Common.time() + " - targetHeading: " + heading);
		System.out.println(Common.time() + " - gyroAngle: " + DriveTrain.instance().getHeading().getAngle());
		double error = heading - DriveTrain.instance().getHeading().getAngle();
		double offset = error*0.025;
		if (error > 0) {
			return new double[] {power + offset, power};
		} else {
			return new double[] {power, power - offset};
		}
	}
}
