package org.usfirst.frc.team4564.robot;

import org.usfirst.frc.team4564.robot.Elevator.States;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Spark;

/**
 * A class to control the intake arm and loader.
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Evan McCoy
 * @author Brent Roberts
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
	
	public final double MAX_ELEVATOR_SAFE = 64, MIN_ELEVATOR_SAFE = 0, //Safe angles when elevator is not at top
			//The angle at which the intake is horizontal out the front.
			FRONT_HORIZONTAL = 0,
			MIN_POSITION = 210, MAX_POSITION = 3593, 
			MIN_ANGLE = -12, MAX_ANGLE = 160, 
			MAX_ABS_ANGLE = 209.0041,
			//The degrees that the power ramping takes place in at the limits
			DANGER_ZONE = 25,
			//Down powers
			MIN_DOWN_POWER = 0, MAX_DOWN_POWER = -0.1,
			//up powers
			MIN_UP_POWER = 0, MAX_UP_POWER = 0.5,
			//Max power change in accel limit
			MAX_DELTA_POWER = 0.1,
			MIN_VELOCITY = 0, MAX_VELOCITY = 45,
			COUNTS_PER_DEGREE = 14.89444444,
			PARTIALLY_LOADED_DISTANCE = 10,
			//maximum IR distance a fully loaded cube can be
			FULLY_LOADED_DISTANCE = 3;
	
	private double P_POS = 0.01, I_POS = 0, D_POS = 0,
			P_VEL = 0.00035, I_VEL = 0, D_VEL = 0.1,
			lastPower = 0, previousReading = 0;
	
	private long intakeTime = 0;
	public double velocity = 0.0;
	public double position;
	private long previousMillis = Common.time();
	
	public Intake() {
		pid = new PositionByVelocityPID(MIN_ANGLE, MAX_ANGLE, MIN_VELOCITY, MAX_VELOCITY, 0.01, "intake");
		pid.setPositionScalars(P_POS, I_POS, D_POS);
		pid.setVelocityScalars(P_VEL, I_VEL, D_VEL);
		pid.setVelocityInverted(true);
		intakeArm.setInverted(true);
		leftIntake.setInverted(true);
		Thread t = new Thread(new PotUpdate());
		t.start();
	}
	
	/**
	 * Sets the power of the intake arm.
	 * 
	 * @param power - the power
	 */
	public void setArmPower(double power) {
		double maxAngle = 0.0;
		if (Robot.getElevator().intakeSafe()) {
			maxAngle = MAX_ANGLE;
		} 
		else {
			maxAngle = MAX_ELEVATOR_SAFE;
		}
		if (power > 0.0) {
			if (getPosition() >= maxAngle) {
				power = 0.0;
				pid.reset();
			}
		} else {
			if (getPosition() <= MIN_ANGLE) { 
				power = 0.0;
			}
		}
		power = rampPower(power);
		intakeArm.set(power);
		Common.dashNum("Intake arm Power", power);
	}
	
	private void setAccelArmPower(double targetPower) {
		double power = 0; 	
		if (Math.abs(lastPower - targetPower) > MAX_DELTA_POWER) {
			if (lastPower > targetPower) {
				power = lastPower - MAX_DELTA_POWER;
			} else {
				power = lastPower + MAX_DELTA_POWER;
			}
		} else {
			power = targetPower;
		}
		Common.dashNum("Intake arm Power", power);
		Common.dashNum("Intake Last Power", lastPower);
		setArmPower(power);
		lastPower = power;
	}
	
	public double rampPower(double power) {
		
		final double MIDDLE_POWER = 0.5;
		final double MAX_POWER = 0.9;
		final double MIN_POWER = 0.0;
		double maxPower = 0.0;
		double minPower = 0.0;
		if (Robot.getElevator().intakeSafe())
			if (getPosition() < 90) {
				if (power > 0.0) {
					maxPower = Common.map(getPosition(), MIN_ANGLE, 90, MAX_POWER, MIDDLE_POWER);
					power = Math.min(power, maxPower);
				} else {
					minPower = Common.map(getPosition(), MIN_ANGLE, 90, -MIN_POWER, -MIDDLE_POWER);
					power = Math.max(power, minPower);
				}
			} else {
				if (power > 0.0 ) {
					maxPower = Common.map(getPosition(), 90, MAX_ANGLE, MIDDLE_POWER, MIN_POWER);
					power = Math.min(power, maxPower);
				} else {
					minPower = Common.map(getPosition(), 90, MAX_ANGLE, -MIDDLE_POWER, -MAX_POWER);
					power = Math.max(power, minPower);
				}
		} else {
			if ( power > 0.0) {
				maxPower = Common.map(getPosition(), MIN_ANGLE, MAX_ELEVATOR_SAFE, MAX_POWER, 0.25);
				Common.dashNum("Max Power Map", maxPower);
				power = Math.min(power, maxPower);
			} else {
				minPower = Common.map(getPosition(), MIN_ANGLE, MAX_ELEVATOR_SAFE, -MIN_POWER, -MIDDLE_POWER);
				power = Math.max(power, minPower);
			}
		}
		return power;
	}
	
	/**
	 * Sets the power of the left intake motor.
	 * 
	 * @param power - the power
	 */
	public void setLeftIntakePower(double power) {
		if (power > 0.0 && isFullyLoaded()) {
			power = 0.0;
		}
		leftIntake.set(power);
	}
	
	/**
	 * Sets the power of the right intake motor.
	 * 
	 * @param power - the power
	 */
	public void setRightIntakePower(double power) {
		if (power > 0.0 && isFullyLoaded()) {
			power = 0.0;
		}
		rightIntake.set(power);
	}
	
	/**
	 * Sets the power of both intake motors.
	 * 
	 * @param power - the power
	 */
	public void setIntakePower(double power) {
		setRightIntakePower(power);
		setLeftIntakePower(power);
		
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
	 * Whether or not there is a cube partially or fully loaded.
	 * 
	 * @return cube loaded
	 */
	public boolean isPartiallyLoaded() {
		return (getCubeDistance() < PARTIALLY_LOADED_DISTANCE);
	}
	
	/**
	 * Detects fully loaded cube
	 * 
	 * @return -true will cube is loaded fully
	 */
	public boolean isFullyLoaded() {
		return (getCubeDistance() < FULLY_LOADED_DISTANCE);
	}
	
	/**
	 * Controls the intake power when loading a cube.
	 * 
	 * @param power - the power if a cube is not loaded
	 * @return - whether the cube is loaded
	 */
	public boolean loadCube(double power) {
		if (isPartiallyLoaded()) {
			if (Common.time() > intakeTime) {
				setIntakePower(0);
				intakeTime = 0;
				return true;
			} else {
				setIntakePower(power);
				return false;
			}
		}
		else {
			intakeTime = Common.time()+250;
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
		return position;
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
		return velocity;
	}
	
	/**
	 * PID controlled move to a defined position.
	 * 
	 * @param position - the position in degrees
	 */
	public void movePosition(double position) {
		setArmPower(pid.calc(getPosition(), getVelocity()));
	}
	
	/**
	 * PID controlled move at a defined velocity.
	 * 
	 * @param velocity - the velocity in degrees/second.
	 */
	public void moveVelocity(double velocity) {
		double maxAngle = 0.0;
		if (Robot.getElevator().intakeSafe()) {
			maxAngle = MAX_ANGLE;
		} 
		else {
			maxAngle = MAX_ELEVATOR_SAFE;
		}
		if (!Robot.getElevator().intakeSafe() && getPosition() > maxAngle) {
			pid.setTargetVelocity(0);
		}
		else {
			pid.setTargetVelocity(velocity);
		}
		setAccelArmPower(pid.calcVelocity(getVelocity()));
	}
	
	/**
	 * Whether or not the elevator can move in the current arm position.
	 * 
	 * @return - safe
	 */
	public boolean elevatorSafe() {
		if (getPosition() < MAX_ELEVATOR_SAFE) {
			return true;
		}
		return false;
	}
	
	public void reset() {
		pid.reset();
	}
	
	public void update() {
		pid.update();
	}

	public class PotUpdate implements Runnable {

		@Override
		public void run() {
			while (true) {
				double previousPosition = position;
				position = MAX_ABS_ANGLE - (getRawPosition() - 210) / COUNTS_PER_DEGREE; //210 is the lowest potentiometer reading when arm is fully down
				position = 0.05 * position + 0.95 * previousPosition;
				
				double previousVelocity = velocity;
				long millis = Common.time();
				velocity = (position - previousPosition) / ((millis - previousMillis) / 1000.0);
				velocity = 0.97 * previousVelocity + 0.03 * velocity;
				previousMillis = millis;
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {}
			}
		}
		
	}

}
