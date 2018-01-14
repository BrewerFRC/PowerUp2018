package org.usfirst.frc.team4564.robot;

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
 */
public class Robot extends SampleRobot {
	private DriveTrain dt = new DriveTrain();

	public Robot() {
		
	}

	@Override
	public void robotInit() {
		
	}
	
	/**
	 * Control logic for autonomous mode.
	 */
	@Override
	public void autonomous() {
		while (isEnabled() && isAutonomous()) {
			long time = Common.time();
			
			
			Timer.delay((1000.0/Constants.REFRESH_RATE - (Common.time() - time))/1000);
		}
	}

	/**
	 * Control logic for teleoperated mode.
	 */
	@Override
	public void operatorControl() {
		while (isEnabled() && isOperatorControl()) {
			long time = Common.time();
			
			Common.dashNum("encoderL", dt.getLeftCounts());
			Common.dashNum("encoderR", dt.getRightCounts());
			
			Timer.delay((1000.0/Constants.REFRESH_RATE - (Common.time() - time))/1000);
		}
	}
}
