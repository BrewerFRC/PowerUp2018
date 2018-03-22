package org.usfirst.frc.team4564.robot.path;

import org.usfirst.frc.team4564.robot.DriveTrain;
import org.usfirst.frc.team4564.robot.Heading;

/**
 * Must start with an velcoity
 * A class representing a stage of a Path where the robot drives straight on a heading for a specified distance or until velocity < 2.
 * Created January 2018
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Evan McCoy
 * @author Brent Roberts
 * 
 */
public class DriveToWall extends Stage {
	private double target;
	private double angle;
	private double power;
	private int direction = 1;
	private String name;
	
	public DriveToWall(double distance, double angle, double power) {
		super(false);
		this.target = distance;
		this.angle = angle;
		this.power = power;
	}
	
	public void start() {
		DriveTrain.instance().resetEncoders();
		Heading heading = DriveTrain.instance().getHeading();
		heading.setAngle(angle);
		heading.setHeadingHold(true);
		if (target < DriveTrain.instance().getAverageDist()) {
			direction = -1;
		}
	}
	
	public boolean isComplete() {
		if (super.eventsFinished()) {
			if (isDistanceComplete() || DriveTrain.instance().getAverageVelocity() < 2) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isDistanceComplete() {
		if (DriveTrain.instance().getAverageDist() >= target && direction == 1) {
			//System.out.println("DriveStraight: " + this.name + " - Complete");
			return true;
		}
		else if (DriveTrain.instance().getAverageDist() <= target && direction == -1) {
			//System.out.println("DriveStraight: " + this.name + " - Complete");
			return true;
		}
		return false;
	}
	
	public double[] getDrive() {
		if (isDistanceComplete()) {
			return new double[] {0, 0};
		}
		return new double[] {power, power};
	}
}