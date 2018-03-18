package org.usfirst.frc.team4564.robot.path;

/**
 * A class representing a stage of a Path where the robot drives straight on a heading for a specified distance.
 * Created January 2018
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Evan McCoy
 */
public class DriveStraight extends Drive {
	private double power;
	
	public DriveStraight(double distance, double angle, double power) {
		super(false, distance);
		
		super.angle = angle;
		this.power = power;
	}
	
	public boolean isComplete() {
		return super.isComplete(target) && super.eventsFinished();
	}
	
	public double[] getDrive() {
		return super.getDrive(new double[] {power, power});

	/*public void start() {
		DriveTrain.instance().resetEncoders();
		Heading heading = DriveTrain.instance().getHeading();
		heading.setAngle(angle);
		heading.setHeadingHold(true);
	}*/
	/*
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
	}*/
	}
}
