package org.usfirst.frc.team4564.robot;

import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Spark;

public class DriveTrain extends RobotDrive {
	private static final Spark 
			frontL = new Spark(Constants.DRIVE_FL),
			frontR = new Spark(Constants.DRIVE_FR),
			backL = new Spark(Constants.DRIVE_BL),
			backR = new Spark(Constants.DRIVE_BR);
	
	private Encoder encoderL, encoderR;
	private Heading heading;
	
	double driveSpeed = 0;
	double turnSpeed = 0;
	double slideSpeed = 0;
	double driveAccel = .08;
	double turnAccel = .08;
	
	public DriveTrain() {
		super(frontL, backL, frontR, backR);
		
		encoderL = new Encoder(Constants.DRIVE_ENCODER_LA, Constants.DRIVE_ENCODER_LB, false, EncodingType.k4X);
		encoderR = new Encoder(Constants.DRIVE_ENCODER_RA, Constants.DRIVE_ENCODER_RB, false, EncodingType.k4X);
		heading = new Heading(Heading.P, Heading.I, Heading.D);
	}
	
	public Heading getHeading() {
		return this.heading;
	}
	 public double driveAccelCurve(double target, double driveAccel) {
		 if (Math.abs(driveSpeed - target) > driveAccel) {
	            if (driveSpeed > target) {
	                driveSpeed = driveSpeed - driveAccel;
	            } else {
	                driveSpeed = driveSpeed + driveAccel;
	            }
	        } else {
	            driveSpeed = target;
	        }
	        return driveSpeed;
	 }
	 
	 public double turnAccelCurve(double target, double turnAccel) {
		 if (Math.abs(turnSpeed - target) > turnAccel) {
	    		if (turnSpeed > target) {
	    			turnSpeed = turnSpeed - turnAccel;
	    		} else {
	    			turnSpeed = turnSpeed + turnAccel;
	    		}
	    	} else {
	    		turnSpeed = target;
	    	}
	    return turnSpeed;
	}
	 
	public void manualDrive(double fl, double bl, double fr, double br, double sl, double sr) {
		frontL.set(fl);
		backL.set(bl);
		frontR.set(fr);
		backR.set(br);
	}
	
	public void setDrive(double drive, double turn, double slide) {
		arcadeDrive(drive,turn);
	}
	
	public void accelDrive(double drive, double turn, double slide) {
		drive = driveAccelCurve(drive, driveAccel );
		turn = turnAccelCurve(turn, turnAccel);
		arcadeDrive(drive, turn);
	}
}
