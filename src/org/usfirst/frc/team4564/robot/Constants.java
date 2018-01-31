package org.usfirst.frc.team4564.robot;

/**
 * Software and hardware constant values for the Power Up 2018 season robot.
 * 
 * Created January 2018
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Evan McCoy
 * @author Brent Roberts
 */
public class Constants {
	public static final int
	
	//Software Constants
	REFRESH_RATE = 50,
	
	//Drive motors
	DRIVE_FL = 7, DRIVE_FR = 8, DRIVE_BL = 6, DRIVE_BR = 9,
	
	//Encoders
	DRIVE_ENCODER_LA = 2, DRIVE_ENCODER_LB = 3,
	DRIVE_ENCODER_RA = 0, DRIVE_ENCODER_RB = 1,
	
	//Sonic
	SONIC_LEFT_PIN = 0, SONIC_FRONT_PIN = 1, SONIC_RIGHT_PIN = 2,
	
	//Sonic able pins
	SONIC_ABLE_PIN = 4,
	
	//Elevator motor can numbers
	ELEVATOR_LEFT = 12, ELEVATOR_RIGHT = 13,
	//IR Sensor
	IR_SENSOR = 3,
			
	//potentiometer intake
	INTAKE_POT = 2
	
	;
	public static final long
	//Elevator height in encoder counts(random value for now)
	ELEVATOR_HEIGHT = 10000
	
	;
}
