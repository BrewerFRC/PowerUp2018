package org.usfirst.frc.team4564.robot.path;

import org.usfirst.frc.team4564.robot.Bat;
import org.usfirst.frc.team4564.robot.DriveTrain;
import org.usfirst.frc.team4564.robot.Heading;

public class Drive extends Stage {
	protected double angle;
	protected double power;
	protected int direction = 1;
	protected double target;
	
	public Drive(double distance, double angle, double power) {
		this(false, distance, angle, power);
	}
	
	public Drive(boolean hold, double distance, double angle, double power) {
		super(hold);
		this.target = distance;
		this.angle = angle;
		this.power = power;
	}
	
	public Drive(boolean hold, boolean persist, double distance, double angle, double power) {
		super(hold, persist);
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
		return isDistanceComplete() && super.eventsFinished();
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
		//If the drive is complete, finish drive.
		if (isDistanceComplete()) {
			return new double[] {0, 0};
		}
		double sonicDist = Bat.getInstance().getDistance();
		double robotDist = DriveTrain.instance().getAverageDist();
		double velocity = DriveTrain.instance().getAverageVelocity();
		if (!(sonicDist >= target - robotDist)) {
			if (sonicDist <= velocity) {
				return new double[] {0, 0};
			}
		}
		return new double[] {power, power};
	}
}
