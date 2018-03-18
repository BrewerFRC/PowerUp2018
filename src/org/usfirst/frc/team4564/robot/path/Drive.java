package org.usfirst.frc.team4564.robot.path;

import org.usfirst.frc.team4564.robot.Bat;
import org.usfirst.frc.team4564.robot.DriveTrain;
import org.usfirst.frc.team4564.robot.Heading;

public abstract class Drive extends Stage {
	protected double angle;
	protected String name;
	private int direction = 1;
	private double target;
	
	public Drive(boolean hold, double distance) {
		super(hold);
		this.target = distance;
	}
	
	public Drive(boolean hold, boolean persist, double distance) {
		super(hold, persist);
		this.target = distance;
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
	
	public boolean isComplete(double target) {
		/*double dist = DriveTrain.instance().getAverageDist();
		if (dist >= target) {
			System.out.println("Drive - Complete");
		}
		return dist >= target;**/
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
	
	public double[] getDrive(double[] power) {
		double sonicDist = Bat.getInstance().getDistance();
		double robotDist = DriveTrain.instance().getAverageDist();
		double velocity = DriveTrain.instance().getAverageVelocity();
		if (sonicDist >= target - robotDist) {
			return power;
		}
		if (sonicDist <= velocity) {
			return new double[] {0, 0};
		}
		return power;
	}
}
