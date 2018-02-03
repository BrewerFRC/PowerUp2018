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
	
	AnalogInput sonic = new AnalogInput(Constants.SONIC_PIN);
	
	//Constants
	//Stolen from 2017
	private static final double CORRECTION = 1 / 1.04;
	private static final double VOLTS_PER_INCH = 5.0 / 1024 * 2.54 * CORRECTION; // Volts per inch constant
	
	public double getDistance() {
		return sonic.getVoltage()/VOLTS_PER_INCH;
	}
	
	
}
