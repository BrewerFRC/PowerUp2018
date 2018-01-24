package org.usfirst.frc.team4564.robot;

import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Spark;

/**
 * Handles actions related to robot motion.
 * Includes motor controller, encoder, and gyro instances.
 * Created January 2018
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Evan McCoy
 */
public class DriveTrain extends RobotDrive {
	private static DriveTrain instance;
	private static final Spark 
			frontL = new Spark(Constants.DRIVE_FL),
			frontR = new Spark(Constants.DRIVE_FR),
			backL = new Spark(Constants.DRIVE_BL),
			backR = new Spark(Constants.DRIVE_BR);
	
	private Encoder encoderL, encoderR;
	private PID pidL, pidR;
	private Heading heading;
	private Pathfinding path = new Pathfinding();
	double driveSpeed = 0;
	double turnSpeed = 0;
	double DRIVEACCEL = .06;
	double TURNACCEL = .06;
	double TURNMAX = .8;
	
	/**
	 * Creates an instance of DriveTrain.
	 * Motor controller and encoder channels are determined in Constants.
	 */
	public DriveTrain() {
		super(frontL, backL, frontR, backR);
		
		encoderL = new Encoder(Constants.DRIVE_ENCODER_LA, Constants.DRIVE_ENCODER_LB, true, EncodingType.k4X);
		encoderL.setDistancePerPulse(0.01152655);
		encoderL.setSamplesToAverage(10);
		encoderR = new Encoder(Constants.DRIVE_ENCODER_RA, Constants.DRIVE_ENCODER_RB, true, EncodingType.k4X);
		encoderR.setDistancePerPulse(0.01143919);
		encoderR.setSamplesToAverage(10);
		heading = new Heading(Heading.P, Heading.I, Heading.D);
		
		pidL = new PID(0.005, 0, 0, false, true, "velL");
		pidR = new PID(0.005, 0, 0, false, true, "velR");
		
		instance = this;
	}
	
	/**
	 * Resets the counts of the left and right encoders.
	 */
	public void resetEncoders() {
		encoderL.reset();
		encoderR.reset();
		pidL.reset();
		pidR.reset();
	}
	
	public void updatePIDs() {
		pidL.update();
		pidR.update();
	}
	
	public void pidVelDrive() {
		//pidL.setTarget(18);
		//pidR.setTarget(54);
		path.update();
		this.tankDrive(path.left(), path.right());
		//this.tankDrive(-pidL.calc(getLeftVelocity()), -pidR.calc(getRightVelocity()));
	}
	
	/**
	 * Returns an instance of DriveTrain which is bound to the motor controllers.
	 * Only this instance will be functional.
	 * 
	 * @return DriveTrain - the DriveTrain instance.
	 */
	public static DriveTrain instance() {
		return instance;
	}
	
	/**
	 * Get raw counts for the left encoder.
	 * 
	 * @return int - the counts
	 */
	public int getLeftCounts() {
		return encoderL.get();
	}
	
	/**
	 * Get the scaled distance of the left encoder.
	 * 
	 * @return double - the distance in inches
	 */
	public double getLeftDist() {
		return encoderL.getDistance();
	}
	
	/**
	 * Get the scaled velocity of the left encoder.
	 * 
	 * @return double - the velocity in inches/second
	 */
	public double getLeftVelocity() {
		return encoderL.getRate();
	}
	
	/**
	 * Get raw counts for the right encoder.
	 * 
	 * @return int - the counts
	 */
	public int getRightCounts() {
		return encoderR.get();
	}
	
	/**
	 * Get the scaled distance of the right encoder.
	 * 
	 * @return double - the distance in inches
	 */
	public double getRightDist() {
		return encoderR.getDistance();
	}
	
	/**
	 * Get the scaled velocity of the right encoder.
	 * 
	 * @return double - the velocity in inches/second
	 */
	public double getRightVelocity() {
		return encoderR.getRate();
	}
	
	/**
	 * Get the averaged counts between the two encoders.
	 * 
	 * @return int - the average counts
	 */
	public int getAverageCounts() {
		return (encoderL.get() + encoderR.get()) / 2;
	}
	
	/**
	 * Get the averaged scaled distance between the two encoders.
	 * 
	 * @return double - the average distance in inches
	 */
	public double getAverageDist() {
		return (encoderL.getDistance() + encoderR.getDistance()) / 2;
	}
	
	/**
	 * Get the averaged scaled velocity between the two encoders.
	 * 
	 * @return double - the average velocity in inches/second
	 */
	public double getAverageVelocity() {
		return (encoderL.getRate() + encoderR.getRate()) / 2;
	}
	
	/**
	 * An instance of Heading, a gyro utility and PID controller.
	 * 
	 * @return Heading - the heading instance.
	 */
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
	 
	 public double turnAccelCurve(double target, double turnAccel, double turnMax) {
		 if (Math.abs(turnSpeed - target) > turnAccel) {
	    		if (turnSpeed > target) {
	    			turnSpeed = turnSpeed - turnAccel;
	    		} else {
	    			turnSpeed = turnSpeed + turnAccel;
	    		}
	    	} else {
	    		turnSpeed = target;
	    	}
		 if (turnSpeed >= 0) {
			 turnSpeed = Math.min(turnMax, turnSpeed);
		 } else {
			 turnSpeed = Math.max(-turnMax, turnSpeed);
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
	
	public void accelDrive(double drive, double turn) {
		drive = driveAccelCurve(drive, DRIVEACCEL );
		turn = turnAccelCurve(turn, TURNACCEL, TURNMAX);
		arcadeDrive(drive, turn);
	}
}
