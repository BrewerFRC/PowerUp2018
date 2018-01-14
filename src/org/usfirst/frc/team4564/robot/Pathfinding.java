package org.usfirst.frc.team4564.robot;

import java.util.function.Function;

/**
 * A mathematical model for controlling robot motion along a predefined functional path.
 * Created January 2018
 * 
 * @author Evan McCoy
 */
public class Pathfinding {
	private Function<Double, Double> f = (x) -> {return Math.pow(x, 2);};
	private Function<Double, Double> dfdx = (x) -> {return 2*x;};
	private Function<Double, Double> d2fdx2 = (x) -> {return 2.0;};
	
	//The velocity of the centerpoint of the robot in inches/second.
	private double centerVelocity = 96;
	//Half the width of the robot in inches.
	private double halfWidth = 15;
	
	private PID leftPID;
	private PID rightPID;
	private DriveTrain dt;
	private double x = 0;
	
	public Pathfinding() {
		leftPID = new PID(0, 0, 0, true, "leftEncoderPath");
		rightPID = new PID(0, 0, 0, true, "rightEncoderPath");
		dt = DriveTrain.instance();
	}
	
	public void update() {
		leftPID.setTarget(leftVelocity());
		rightPID.setTarget(rightVelocity());
	}
	
	/**
	 * Calculates a recommended motor power based on functional position and PID control.
	 * 
	 * @return double - the left motor power.
	 */
	public double left() {
		return leftPID.calc(dt.getLeftVelocity());
	}
	
	/**
	 * Calculates a recommended motor power based on functional position and PID control.
	 * 
	 * @return double - the right motor power.
	 */
	public double right() {
		return rightPID.calc(dt.getRightVelocity());
	}
	
	/**
	 * Calculates the desired relative velocity of the left side in inches/second.
	 * 
	 * @return double - the velocity in inches/second
	 */
	public double leftVelocity() {
		double r = radius();
		return centerVelocity * (r - halfWidth) / r;
	}
	
	/**
	 * Calculates the desired relative velocity of the right side in inches/second.
	 * 
	 * @return double - the velocity in inches/second
	 */
	public double rightVelocity() {
		double r = radius();
		return centerVelocity * (r + halfWidth) / r;
	}
	
	/**
	 * The instantaneous turning radius of the robot as determined by the osculating circle of f(x).
	 * 
	 * @return double - the radius.
	 */
	public double radius() {
		return Math.pow(1 + Math.pow(dfdx.apply(x), 1.5), 2) / d2fdx2.apply(x);
	}
}
