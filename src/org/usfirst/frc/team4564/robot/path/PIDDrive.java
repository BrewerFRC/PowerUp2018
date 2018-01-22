package org.usfirst.frc.team4564.robot.path;

import org.usfirst.frc.team4564.robot.DriveTrain;
import org.usfirst.frc.team4564.robot.PID;

/**
 * A class representing a stage of a Path where the robot drives a specified distance under PID motor control.
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Evan McCoy
 */
public class PIDDrive extends Stage {
	private PID pid;
	private double heading;
	
	public PIDDrive(double distance, double heading, double minPower, double maxPower, double p, double i, double d, boolean inverted, String name) {
		this.pid = new PID(p, i, d, inverted, false, name);
		pid.setTarget(distance);
		pid.setOutputLimits(-maxPower, maxPower);
		pid.setMin(minPower);
		this.heading = heading;
	}
	
	public void start() {
		DriveTrain.instance().resetEncoders();
	}
	
	public boolean isComplete() {
		return DriveTrain.instance().getAverageDist() >= pid.getTarget();
	}
	
	public double[] getDrive() {
		double[] power = getDrive(DriveTrain.instance().getAverageDist());
		double error = heading - DriveTrain.instance().getHeading().getAngle();
		double offset = error*0.025;
		if (error > 0) {
			return new double[] {power[0] + offset, power[1]};
		} else {
			return new double[] {power[0], power[1] - offset};
		}
	}
	
	public double[] getDrive(double curDistance) {
		double power = pid.calc(curDistance);
		return new double[] {power, power};
	}
}