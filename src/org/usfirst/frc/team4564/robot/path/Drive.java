package org.usfirst.frc.team4564.robot.path;

import org.usfirst.frc.team4564.robot.Bat;
import org.usfirst.frc.team4564.robot.DriveTrain;
import org.usfirst.frc.team4564.robot.Heading;

public abstract class Drive extends Stage {
	protected double angle;
	protected String name;
	
	public Drive(boolean hold) {
		super(hold);
	}
	
	public Drive(boolean hold, boolean persist) {
		super(hold, persist);
	}
	
	public void start() {
		DriveTrain.instance().resetEncoders();
		Heading heading = DriveTrain.instance().getHeading();
		heading.setAngle(angle);
		heading.setHeadingHold(true);
	}
	
	public boolean isComplete(double target) {
		double dist = DriveTrain.instance().getAverageDist();
		if (dist >= target) {
			System.out.println("Drive - Complete");
		}
		return dist >= target;
	}
	
	public double[] getDrive(double[] power, double targetDist) {
		double sonicDist = Bat.getInstance().getDistance();
		double robotDist = DriveTrain.instance().getAverageDist();
		double velocity = DriveTrain.instance().getAverageVelocity();
		if (sonicDist >= targetDist - robotDist) {
			return power;
		}
		if (sonicDist <= velocity) {
			return new double[] {0, 0};
		}
		return power;
	}
}
