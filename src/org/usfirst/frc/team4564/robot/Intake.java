package org.usfirst.frc.team4564.robot;

import edu.wpi.first.wpilibj.AnalogInput;

public class Intake {

	private AnalogInput irInput = new AnalogInput(Constants.IR_SENSOR);
	private AnalogInput pot = new AnalogInput(Constants.INTAKE_POT);
	private double MAX_ELEVATOR_SAFE = 4096, MIN_ELEVATOR_SAFE = 0;
	private double previousReading = 0;

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
	
	
	public double getPosition() {
		double position = pot.getValue();
		return position;		
	}
	
	
	public boolean elevatorSafe() {
		if (getPosition() > MIN_ELEVATOR_SAFE && getPosition() < MAX_ELEVATOR_SAFE) {
			return true;
		}
		return false;
	}
}
