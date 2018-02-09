package org.usfirst.frc.team4564.robot;

import edu.wpi.first.wpilibj.AnalogInput;

public class Intake {
	private AnalogInput irInput;
	private AnalogInput pot;
	private PositionByVelocityPID pid;
	private double MAX_ELEVATOR_SAFE = 180, MIN_ELEVATOR_SAFE = 0;
	private double previousReading = 0;
	private double MIN_POSITION = 0, MAX_POSITION = 4096, MIN_ANGLE = 0, MAX_ANGLE = 180;

	public Intake() {
		irInput = new AnalogInput(Constants.IR_SENSOR);
		pot = new AnalogInput(Constants.INTAKE_POT);
		pid = new PositionByVelocityPID(MIN_ANGLE, MAX_ANGLE, -10, 10, -1.0, 1.0, 0, "intake");
	}
	
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
	
	
	public boolean elevatorSafe() {
		//Made it equal to inorder to test -Brent
		if (getPosition() >= MIN_ELEVATOR_SAFE && getPosition() < MAX_ELEVATOR_SAFE) {
			return true;
		}
		return false;
	}
}
