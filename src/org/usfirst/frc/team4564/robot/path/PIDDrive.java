package org.usfirst.frc.team4564.robot.path;

import org.usfirst.frc.team4564.robot.DriveTrain;
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
	
	public PIDDrive(double distance, double angle, double minPower, double maxPower, boolean inverted, String name) {
		super(false, true, distance, angle, minPower);
		this.pid = new PID(P, I, D, inverted, false, name);
		pid.setTarget(distance);
		pid.setOutputLimits(-maxPower, maxPower);
		pid.setMinMagnitude(minPower);
		this.angle = angle;
	}
	
	@Override
	public double[] getDrive() {
		double curDistance = DriveTrain.instance().getAverageDist();
		double power = pid.calc(curDistance);
		if (Math.abs(super.getDrive()[0]) > 0) {
			return new double[] {power, power};
		}
		return super.getDrive();
	}
}