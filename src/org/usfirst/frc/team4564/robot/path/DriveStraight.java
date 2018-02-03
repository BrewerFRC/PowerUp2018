package org.usfirst.frc.team4564.robot.path;

import org.usfirst.frc.team4564.robot.DriveTrain;
import org.usfirst.frc.team4564.robot.PID;

/**
 * A class representing a stage of a Path where the robot drives straight on a heading for a specified distance.
 * Created January 2018
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Evan McCoy
 */
public class DriveStraight extends Stage {
	private static final double P = 0.025, I = 0, D = 1.5;
	private double target;
	private double heading;
	private double power;
	private PID pid;
	private String name;
	
	public DriveStraight(double distance, double heading, double power, String name) {
		super(false);
		this.target = distance;
		this.heading = heading;
		this.power = power;
		this.pid = new PID(P, I, D, false, false, name);
		this.name = name;
	}
	
	public void start() {
		DriveTrain.instance().resetEncoders();
		pid.setTarget(heading);
	}
	
	public boolean isComplete() {
		if (DriveTrain.instance().getAverageDist() >= target) {
			System.out.println("DriveStraight: " + this.name + " - Complete");
		}
		return DriveTrain.instance().getAverageDist() >= target;
	}
	
	public double[] getDrive() {
		pid.update();
		double offset = pid.calc(DriveTrain.instance().getHeading().getAngle());
		if (offset > 0) {
			return new double[] {power, Math.min(power + Math.abs(offset), 1)};
		}
		else {
			return new double[] {Math.min(power + Math.abs(offset), 1), power};
		}
		//double error = heading - DriveTrain.instance().getHeading().getAngle();
		//double offset = error*0.025;
		//if (error > 0) {
		//	return new double[] {power + offset, power};
		//} else {
		//	return new double[] {power, power - offset};
		//}
	}
}
