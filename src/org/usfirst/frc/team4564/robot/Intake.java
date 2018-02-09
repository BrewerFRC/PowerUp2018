package org.usfirst.frc.team4564.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Spark;

/**
 * Class to control the intake arm and motors.
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Sam Woodward
 * @author Evan McCoy
 */
public class Intake {
	private static final double P_VEL = 0, I_VEL = 0, D_VEL = 0, 
			P_POS = 0, I_POS = 0, D_POS = 0;
	private Spark arm;
	private AnalogInput irInput;
	private AnalogInput pot;
	private PositionByVelocityPID pid;
	private double MAX_ELEVATOR_SAFE = 180, MIN_ELEVATOR_SAFE = 0;
	private double previousReading = 0;
	private double MIN_POSITION = 0, MAX_POSITION = 4096, MIN_ANGLE = 0, MAX_ANGLE = 180;
	
	private double previousPosition = 0;
	private double previousVelocity = 0;

	public Intake() {
		arm = new Spark(Constants.ARM_DRIVE);
		irInput = new AnalogInput(Constants.IR_SENSOR);
		pot = new AnalogInput(Constants.INTAKE_POT);
		pid = new PositionByVelocityPID(MIN_ANGLE, MAX_ANGLE, -10, 10, -1.0, 1.0, 0, "intake");
		pid.setPositionScalars(P_POS, I_POS, D_POS);
		pid.setVelocityScalars(P_VEL, I_VEL, D_VEL);
	}
	
	/**
	 * Gets the distance a cube is from the infrared sensor.
	 * 
	 * @return the distance in inches.
	 */
	public double getDistance() {
	  // put your main code here, to run repeatedly:
	  double reading = irInput.getValue() / 4 * 0.1 + previousReading * 0.9;
	  double inches = (-20.0/575.0)*reading+20;
	  if (inches < 0){
	    inches = 0;
	  }
	  previousReading = reading;
	  return inches;
	
	}
	
	/**
	 * Whether or not there is a cube loaded.
	 * 
	 * @return cube loaded
	 */
	public boolean isLoaded() {
		if (getDistance() < 10) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Gets claw position.
	 * 
	 * Gets the claw position on a scale of 0-180 to represent angle.
	 * @return
	 */
	public double getPosition() {
		double position = pot.getValue();
		Common.map(position, MIN_POSITION, MAX_POSITION, MIN_ANGLE, MAX_ANGLE);
		return position;		
	}
	
	/**
	 * Whether or not the elevator is safe to move given the current position.
	 * 
	 * @return movable
	 */
	public boolean elevatorSafe() {
		//Made it equal to inorder to test -Brent
		if (getPosition() >= MIN_ELEVATOR_SAFE && getPosition() < MAX_ELEVATOR_SAFE) {
			return true;
		}
		return false;
	}
	
	/**
	 * Gets the velocity of the arm in degrees per second.
	 * Uses a complementary function to smooth velocity.
	 * 
	 * @return the velocity
	 */
	public double getVelocity() {
		double position = getPosition();
		double velocity = (position - previousPosition) / (1 / Constants.REFRESH_RATE);
		velocity = 0.9 * previousVelocity + 0.1 * velocity;
		previousVelocity = velocity;
		previousPosition = position;
		return velocity;
	}
	
	/**
	 * Set the position of the arm in degrees.
	 * 
	 * @param target the position.
	 */
	public void setPosition(double target) {
		pid.setTargetPosition(target);
	}
	
	/**
	 * Set the velocity of the arm in degrees per second.
	 * 
	 * @param target the velocity.
	 */
	public void setVelocity(double target) {
		pid.setTargetVelocity(target);
	}
	
	/**
	 * Calculate the required motor powers to meet the target velocity.
	 * 
	 * @param velocity - the target drive velocity.
	 */
	public void velDrive(double velocity) {
		pid.setTargetVelocity(velocity);
		arm.set(pid.calcVelocity(getVelocity()));
	}
	
	/**
	 * Calculate the required motor powers to meet the target position.
	 * 
	 * @param position - the target drive position.
	 */
	public void drive(double position) {
		pid.setTargetVelocity(position);
		arm.set(pid.calc(getPosition(), getVelocity()));
	}
}
