package org.usfirst.frc.team4564.robot.path;

import org.usfirst.frc.team4564.robot.DriveTrain;
import org.usfirst.frc.team4564.robot.Heading;
import org.usfirst.frc.team4564.robot.PID;

/**
 * A class representing a stage of a Path where the robot drives a specified distance under PID motor control.
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Evan McCoy
 */
public class PIDDrive extends Stage {
	private static final double P = 0.017, I = 0, D = 1.5;
	
	private PID pid;
	private double angle;
	
	public PIDDrive(double distance, double angle, double minPower, double maxPower, boolean inverted, String name) {
		super(false, true);
		this.pid = new PID(P, I, D, inverted, false, name);
		pid.setTarget(distance);
		pid.setOutputLimits(-maxPower, maxPower);
		pid.setMin(minPower);
		this.angle = angle;
	}
	
	public void start() {
		DriveTrain.instance().resetEncoders();
		Heading heading = DriveTrain.instance().getHeading();
		heading.setAngle(angle);
		heading.setHeadingHold(true);
	}
	
	public boolean isComplete() {
		return DriveTrain.instance().getAverageDist() >= pid.getTarget();
	}
	
	public double[] getDrive() {
		return getDrive(DriveTrain.instance().getAverageDist());
	}
	
	public double[] getDrive(double curDistance) {
		double power = pid.calc(curDistance);
		return new double[] {power, power};
	}
}