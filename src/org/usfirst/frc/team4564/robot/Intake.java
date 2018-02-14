package org.usfirst.frc.team4564.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Spark;

/**
 * A class to control the intake arm and loader.
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Evan McCoy
 * @author Sam Woodward
 */
public class Intake {
	private static final Spark
			intakeArm = new Spark(Constants.INTAKE_ARM),
			leftIntake = new Spark(Constants.LEFT_INTAKE),
			rightIntake = new Spark(Constants.RIGHT_INTAKE);
	private AnalogInput irInput = new AnalogInput(Constants.IR_SENSOR);
	private AnalogInput pot = new AnalogInput(Constants.INTAKE_POT);
	private PositionByVelocityPID pid;
	
	private double MAX_ELEVATOR_SAFE = 4096, MIN_ELEVATOR_SAFE = 0, 
			MIN_POSITION = 210, MAX_POSITION = 3593, 
			MIN_ANGLE = -18.1276, MAX_ANGLE = 209.0041,
			MIN_VELOCITY = 0, MAX_VELOCITY = 45,
			MAX_LOAD_DISTANCE = 10,
			P_POS = 0, I_POS = 0, D_POS = 0,
			P_VEL = 0, I_VEL = 0, D_VEL = 0;
	private double previousReading = 0;
	private double previousPosition = 0;
	private double previousVelocity = 0;
	
	public Intake() {
		pid = new PositionByVelocityPID(MIN_ANGLE, MAX_ANGLE, MIN_VELOCITY, MAX_VELOCITY, 0, "intake");
		pid.setPositionScalars(P_POS, I_POS, D_POS);
		pid.setVelocityScalars(P_VEL, I_VEL, D_VEL);
	}
	
	/**
	 * Sets the power of the intake arm.
	 * 
	 * @param power - the power
	 */
	public void setIntakeArmPower(double power) {
		//Position limits
		if (power > 0 && getPosition() >= MAX_ANGLE) {
			power = 0.0;
		}
		else if (power < 0 && getPosition() <= MIN_ANGLE) {
			power = 0.0;
		}
		else if (!Robot.getElevator().intakeSafe()) {
			power = 0.0;
		}
		//intakeArm.set(power);
	}
	
	/**
	 * Sets the power of the left intake motor.
	 * 
	 * @param power - the power
	 */
	public void setLeftIntakePower(double power) {
		leftIntake.set(power);
	}
	
	/**
	 * Sets the power of the right intake motor.
	 * 
	 * @param power - the power
	 */
	public void setRightIntakePower(double power) {
		rightIntake.set(power);
	}
	
	/**
	 * Sets the power of both intake motors.
	 * 
	 * @param power - the power
	 */
	public void setIntakePower(double power) {
		rightIntake.set(power);
		leftIntake.set(power);
		
	}
	
	/**
	 * Returns the distance of a cube from the infrared sensor.
	 * 
	 * @return - the distance in inches
	 */
	public double getCubeDistance() {
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
		if (getCubeDistance() < MAX_LOAD_DISTANCE) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Controls the intake power when loading a cube.
	 * 
	 * @param power - the power if a cube is not loaded
	 * @return - whether the cube is loaded
	 */
	public boolean loadCube(double power) {
		if (isLoaded()) {
			setIntakePower(0);
			return true;
		}
		else {
			setIntakePower(power);
			return false;
		}
	}
	
	/**
	 * Gets the arm position in degrees.
	 * 
	 * @return - the position
	 */
	public double getPosition() {
		double position = pot.getValue();
		return Common.map(position, MIN_POSITION, MAX_POSITION, MIN_ANGLE, MAX_ANGLE);		
	}
	
	public int getRawPosition() {
		return pot.getValue();
	}
	
	/**
	 * Gets the velocity of the arm in degrees per second.
	 * Uses a complementary function to smooth velocity.
	 * 
	 * @return -the velocity in degrees per second
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
	 * PID controlled move to a defined position.
	 * 
	 * @param position - the position in degrees
	 */
	public void movePosition(double position) {
		if (!Robot.getElevator().intakeSafe()) {
			pid.setTargetPosition(getPosition());
		}
		else {
			pid.setTargetPosition(position);
		}
		setIntakeArmPower(pid.calc(getPosition(), getVelocity()));
	}
	
	/**
	 * PID controlled move at a defined velocity.
	 * 
	 * @param velocity - the velocity in degrees/second.
	 */
	public void moveVelocity(double velocity) {
		if (!Robot.getElevator().intakeSafe()) {
			pid.setTargetVelocity(0);
		}
		else {
			pid.setTargetVelocity(velocity);
		}
		setIntakeArmPower(pid.calcVelocity(getVelocity()));
	}
	
	/**
	 * Whether or not the elevator can move in the current arm position.
	 * 
	 * @return - safe
	 */
	public boolean elevatorSafe() {
		if (getPosition() > MIN_ELEVATOR_SAFE && getPosition() < MAX_ELEVATOR_SAFE) {
			return true;
		}
		return false;
	}
}
