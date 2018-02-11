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
public class PIDDrive extends Drive {
	private static final double P = 0.017, I = 0, D = 1.5;
	
	private PID pid;
	private double angle;
	
	public PIDDrive(double distance, double angle, double minPower, double maxPower, boolean inverted, String name) {
		super(false, true);
		this.pid = new PID(P, I, D, inverted, false, name);
		pid.setTarget(distance);
		pid.setOutputLimits(-maxPower, maxPower);
		pid.setMinMagnitude(minPower);
		this.angle = angle;
	}
	
	public boolean isComplete() {
		return super.isComplete(pid.getTarget());
	}
	
	public double[] getDrive() {
		return getDrive(DriveTrain.instance().getAverageDist());
	}
	
	public double[] getDrive(double curDistance) {
		double power = pid.calc(curDistance);
		return super.getDrive(new double[] {power, power}, pid.getTarget());
	}
}