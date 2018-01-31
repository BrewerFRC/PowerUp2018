package org.usfirst.frc.team4564.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;

/**
 * The main class for the FIRST Power Up 2018 season robot.
 * Includes project-wide objects and control logic for driver input.
 * 
 * Created January 2018
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Evan McCoy
 * @author Brent Roberts
 * @author Sam "Woodie" Woodward
 */
@SuppressWarnings("deprecation")
public class Robot extends SampleRobot {
	private DriveTrain dt = new DriveTrain();
	private static final double P = 0.075, I = 0, D = 0.08;
	private Intake intake = new Intake();
	private Xbox j0 = new Xbox(0);
	private Xbox j1 = new Xbox(1);
	private Bat bat = new Bat();
	private Elevator elevator = new Elevator();
	private String gameData;

	public Robot() {
		
	}
	
	
	
	@Override
	public void robotInit() {
		elevator.init();
	}
	
	@Override
	public void disabled() {
		while (isDisabled()) {
			gameData = DriverStation.getInstance().getGameSpecificMessage();
			if(gameData != null) {
				Common.dashStr("Game Data", gameData);
			}
			Common.dashNum("IR Output", intake.getDistance() );
			Common.dashBool("Is Loaded", intake.isLoaded());
			/*char c = gameData.charAt(0);
			if (c == 'R') {
				AND = &&
				OR = ||
			}*/
			
			if (gameData.length() == 3) {
				Common.dashBool("Do You Have Game Data", true);
			} else {
				Common.dashBool("Do You Have Game Data" , false);
			}
			elevator.update();
		}
	}
	
	
	/**
	 * Control logic for autonomous mode.
	 */
	@Override
	public void autonomous() {
		Path path1 = new Path();
		path1.addDriveStraight(36, 0, 0.65)
			 .addDriveStraight(200, 9, 0.65)
			 .addDriveStraight(36, 0, 0.65);
		while (isEnabled() && isAutonomous()) {
			long time = Common.time();
			
			double[] power = path1.getDrive();
			
			Common.dashNum("gyroAngle", DriveTrain.instance().getHeading().getAngle());
			Common.dashNum("leftPower", power[0]);
			Common.dashNum("rightPower", power[1]);
			
			DriveTrain.instance().tankDrive(power[0], power[1]);
			
			Timer.delay(Math.max(0, (1000.0/Constants.REFRESH_RATE - (Common.time() - time))/1000));
		}
	}

	/**
	 * Control logic for teleop mode.
	 */
	@Override
	public void operatorControl() {
    	long time;
    	while (isEnabled() && isOperatorControl()) {
    		
    		Common.dashNum("Left ultrasonic", bat.getLeftDistance());
    		time = Common.time();
    		double forward = 0;
    		double turn = 0;
    		forward = j0.getY(GenericHID.Hand.kLeft);
			turn  = j0.getX(GenericHID.Hand.kLeft);
    		elevator.elevatorRight.set(ControlMode.PercentOutput, j0.getX(GenericHID.Hand.kRight));
    		dt.accelDrive(forward, turn);
    		elevator.update();
    		double delay = (1000.0/Constants.REFRESH_RATE - (Common.time() - time)) / 1000.0;
    		Timer.delay((delay > 0) ? delay : 0.001);
    	}
    }
}
