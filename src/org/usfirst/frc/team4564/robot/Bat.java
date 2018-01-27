package org.usfirst.frc.team4564.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalOutput;

/**
 * Ultrasonic control class
 * Designed for a Y cable on a DIO port to control three ultrasonic sensors.
 * Can also just have them always on.
 * 
 * Created January 2018
 * 
 *  @author Brewer FIRST Robotics Team 4564 
 *  @author Brent Roberts
*/
public class Bat {
	int cycleDelay = Constants.REFRESH_RATE/10;
	long cycleTime = 0;
	int timePerPulse = 50;
	
	AnalogInput sonicFront = new AnalogInput(Constants.SONIC_FRONT_PIN);
	AnalogInput sonicLeft = new AnalogInput(Constants.SONIC_LEFT_PIN);
	AnalogInput sonicRight = new AnalogInput(Constants.SONIC_RIGHT_PIN);
	DigitalOutput sonicAble = new DigitalOutput(Constants.SONIC_ABLE_PIN);
	
	//Constants
	//Stolen from 2017
	private static final double CORRECTION = 1 / 1.04;
	private static final double VOLTS_PER_INCH = 5.0 / 1024 * 2.54 * CORRECTION; // Volts per inch constant
	
	public double getFrontDistance() {
		return sonicFront.getVoltage()/VOLTS_PER_INCH;
	}
	public double getLeftDistance() {
		return sonicLeft.getVoltage()/VOLTS_PER_INCH;
	}
	public double getRightDistance() {
		return sonicRight.getVoltage()/VOLTS_PER_INCH;
	}
	public void update() {
		if(Common.time() >= cycleTime + timePerPulse) {
			sonicAble.set(true);
			cycleTime = Common.time();
		}else {
			sonicAble.set(false);
		}
	}
	
}
