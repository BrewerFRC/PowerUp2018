package org.usfirst.frc.team4564.robot.path;

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
	private String name;
	
	public DriveStraight(double distance, double angle, double power) {
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
	}
	
	public boolean isComplete() {
		if (Math.abs(DriveTrain.instance().getAverageDist()) >= Math.abs(target)) {
			System.out.println("DriveStraight: " + this.name + " - Complete");
			return true;
		} else {
			return false;
		}
	}
	
	public double[] getDrive() {
		return new double[] {power, power};
	}
}
