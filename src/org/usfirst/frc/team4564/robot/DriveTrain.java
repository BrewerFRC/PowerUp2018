package org.usfirst.frc.team4564.robot;

import edu.wpi.first.wpilibj.CounterBase.EncodingType;

import org.usfirst.frc.team4564.robot.path.Pathfinding;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

/**
 * Handles actions related to robot motion.
 * Includes motor controller, encoder, and gyro instances.
 * Created January 2018
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Evan McCoy
 * @author Brent Roberts
 */
public class DriveTrain extends DifferentialDrive {
	private static DriveTrain instance;
	
	public static final double DRIVEACCEL = .06, TURNACCEL = .06, TANKACCEL = 0.01, TANKMIN = 0.40, TURNMAX = .8;
	
	private static final Spark 
			frontL = new Spark(Constants.DRIVE_FL),
			frontR = new Spark(Constants.DRIVE_FR),
			backL = new Spark(Constants.DRIVE_BL),
			backR = new Spark(Constants.DRIVE_BR);
	private static final SpeedControllerGroup left = new SpeedControllerGroup(frontL, backL);
	private static final SpeedControllerGroup right = new SpeedControllerGroup(frontR, backR);
	
	private Encoder encoderL, encoderR;
	private PID pidL, pidR;
	private Heading heading;
	private Pathfinding path = new Pathfinding();
	private double driveSpeed = 0, turnSpeed = 0;
	private double tankLeft = 0, tankRight = 0;
	
	/**
	 * Creates an instance of DriveTrain.
	 * Motor controller and encoder channels are determined in Constants.
	 */
	public DriveTrain() {
		super(left, right);
		
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
	
	/**
	 * Update PID tuning values from the SmartDashboard.
	 */
	public void updatePIDs() {
		pidL.update();
		pidR.update();
	}
	
	/**
	 * Drive the robot on controlled left and right velocity targets.
	 */
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
	
	/**
	 * Gradually accelerate to a specified drive value.
	 * 
	 * @param target - the target drive value from -1 to 1
	 * @return double - the allowed drive value for this cycle.
	 */
	 public double driveAccelCurve(double target) {
		 if (Math.abs(driveSpeed - target) > DRIVEACCEL) {
            if (driveSpeed > target) {
                driveSpeed = driveSpeed - DRIVEACCEL;
            } else {
                driveSpeed = driveSpeed + DRIVEACCEL;
            }
        } else {
            driveSpeed = target;
        }
        return driveSpeed;
	 }
	 
	 /**
	  * Gradually accelerate to a specified turn value.
	  * 
	  * @param target - the target turn value from -1 to 1
	  * @return double - the allowed turn value at this cycle.
	  */
	 public double turnAccelCurve(double target) {
		 if (Math.abs(turnSpeed - target) > TURNACCEL) {
	    		if (turnSpeed > target) {
	    			turnSpeed = turnSpeed - TURNACCEL;
	    		} else {
	    			turnSpeed = turnSpeed + TURNACCEL;
	    		}
	    	} else {
	    		turnSpeed = target;
	    	}
		 if (turnSpeed >= 0) {
			 turnSpeed = Math.min(TURNMAX, turnSpeed);
		 } else {
			 turnSpeed = Math.max(-TURNMAX, turnSpeed);
		 }
	    return turnSpeed;
	}
	
	//turn should be inverted on testbed -Brent
	/**
	 * Arcade drive with an acceleration curve.
	 * 
	 * @param drive - the forward/backward value from -1 to 1.
	 * @param turn - the turn value from -1 to 1.
	 */
	public void accelDrive(double drive, double turn) {
		drive = driveAccelCurve(drive);
		turn = turnAccelCurve(turn);
		arcadeDrive(drive, -turn);
	}
	
	/**
	 * An implementation of tank drive that updates current speed values used in acceleration curve methods.
	 */
	@Override
	public void tankDrive(double left, double right) {
		tankLeft = left;
		tankRight = right;
		super.tankDrive(tankLeft, tankRight);
	}
	
	/**
	 * Acceleration control for tank drive.
	 * 
	 * @param left - the target left power.
	 * @param right - the target right power.
	 */
	public void accelTankDrive(double left, double right) {
		//Left
		if (tankLeft < TANKMIN) {
			tankLeft = Math.min(TANKMIN, left);
		}
		else if (Math.abs(tankLeft - left) > TANKACCEL) {
            if (tankLeft > left) {
                tankLeft = tankLeft - TANKACCEL;
            } else {
                tankLeft = tankLeft + TANKACCEL;
            }
        } 
		else {
            tankLeft = left;
        }
		//Right
		if (tankRight < TANKMIN) {
			tankRight = Math.min(TANKMIN, right);
		}
		else if (Math.abs(tankRight - right) > TANKACCEL) {
            if (tankRight > right) {
                tankRight = tankRight - TANKACCEL;
            } else {
                tankRight = tankRight + TANKACCEL;
            }
        } else {
            tankRight = right;
        }
		System.out.println(tankLeft + ":" + tankRight);
		super.tankDrive(tankLeft, tankRight);
	}
}
