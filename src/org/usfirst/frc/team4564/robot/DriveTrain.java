package org.usfirst.frc.team4564.robot;

import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Spark;

public class DriveTrain extends RobotDrive {
	private static DriveTrain instance;
	private static final Spark 
			frontL = new Spark(Constants.DRIVE_FL),
			frontR = new Spark(Constants.DRIVE_FR),
			backL = new Spark(Constants.DRIVE_BL),
			backR = new Spark(Constants.DRIVE_BR);
	
	private Encoder encoderL, encoderR;
	private Heading heading;
	
	public DriveTrain() {
		super(frontL, backL, frontR, backR);
		
		encoderL = new Encoder(Constants.DRIVE_ENCODER_LA, Constants.DRIVE_ENCODER_LB, false, EncodingType.k4X);
		encoderR = new Encoder(Constants.DRIVE_ENCODER_RA, Constants.DRIVE_ENCODER_RB, false, EncodingType.k4X);
		heading = new Heading(Heading.P, Heading.I, Heading.D);
		
		instance = this;
	}
	
	public static DriveTrain instance() {
		return instance;
	}
	
	public Encoder getLeft() {
		return encoderL;
	}
	
	public Encoder getRight() {
		return encoderR;
	}
	
	public Heading getHeading() {
		return this.heading;
	}
}
