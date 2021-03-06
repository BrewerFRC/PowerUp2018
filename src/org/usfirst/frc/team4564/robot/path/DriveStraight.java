package org.usfirst.frc.team4564.robot.path;

import org.usfirst.frc.team4564.robot.Common;
import org.usfirst.frc.team4564.robot.DriveTrain;
import org.usfirst.frc.team4564.robot.Heading;

/**
 * A class representing a stage of a Path where the robot drives straight on a heading for a specified distance.
 * Created January 2018
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Evan McCoy
 */
public class DriveStraight extends Stage {
	private double target;
	private double angle;
	private double power;
	private int direction = 1;
	private String name;
	
	public DriveStraight(double distance, double angle, double power, String name) {
		super(false);
		this.name = name;
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
		if (!super.eventsFinished() || !isDistanceComplete()) {
			return false;
		}
		Common.debug("DriveStraight: " + this.name + " - Complete");
		return true;
	}
	
	public boolean isDistanceComplete() {
		double truncatedDistance = ((int)(DriveTrain.instance().getAverageDist() * 10)) / 10.0;
		if (DriveTrain.instance().getAverageDist() >= target && direction == 1) {
			Common.debug("DriveStraight: " + this.name + " - Distance Complete"+ truncatedDistance);
			return true;
		}
		else if (DriveTrain.instance().getAverageDist() <= target && direction == -1) {
			Common.debug("DriveStraight: " + this.name + " - Distance Complete" + truncatedDistance);
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
