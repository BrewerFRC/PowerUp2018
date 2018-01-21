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
@SuppressWarnings("deprecation")
public class Robot extends SampleRobot {
	private DriveTrain dt = new DriveTrain();
	private Xbox j = new Xbox(0);

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
		Path path = new Path();
		path.addDriveStraight(36, 0, 0.65)
			.addPowerTurn(90, 0.65)
			.addDriveStraight(48, 90, 0.9)
			.addPIDDrive(36, 0.4, 0.65, 0.075, 0, 0.08, false, "driveSlow")
			.addPowerTurnOverlay(0, 0.65)
			.addPIDDrive(36, 0.4, 0.8, 0.075, 0, 0.08, false, "driveScale");
		
		while (isEnabled() && isAutonomous()) {
			long time = Common.time();
			
			double[] power = path.getDrive();
			DriveTrain.instance().tankDrive(power[0], power[1]);
			
			Timer.delay((1000.0/Constants.REFRESH_RATE - (Common.time() - time))/1000);
		}
	}

	/**
	 * Control logic for teleoperated mode.
	 */
	@Override
	public void operatorControl() {
		
		Path path = new Path();
		path.addDriveStraight(60, 0, 0.65)
			.addPowerTurn(76, 0.65)
			.addDriveStraight(72, 90, 0.65)
			.addDriveStraight(36, 90, 0.65)
			.addPowerTurn(0, 0.65)
			.addPIDDrive(36, 0.4, 0.8, 0.075, 0, 0.08, true, "driveScale");
		path.start();
		while (isEnabled() && isOperatorControl()) {
			long time = Common.time();
			
			if (j.getPressed("a")) {
				double[] power = path.getDrive();
				Common.dashNum("leftPower", power[0]);
				Common.dashNum("rightPower", power[1]);
				DriveTrain.instance().tankDrive(power[0], power[1]);
			}
			else {
				DriveTrain.instance().tankDrive(0, 0);
			}
			Timer.delay((1000.0/Constants.REFRESH_RATE - (Common.time() - time))/1000);
		}
		/*while (isEnabled() && isOperatorControl()) {
			long time = Common.time();
			
			Common.dashNum("encoderL", dt.getLeftCounts());
			Common.dashNum("encoderR", dt.getRightCounts());
			Common.dashNum("velL", dt.getLeftVelocity());
			Common.dashNum("velR", dt.getRightVelocity());
			dt.updatePIDs();
			if (j.when("x")) {
				dt.resetEncoders();
			}
			if (j.getPressed("y")) {
				dt.pidVelDrive();
			}
			else {
				dt.tankDrive(0, 0);
			}
			
			Timer.delay((1000.0/Constants.REFRESH_RATE - (Common.time() - time))/1000);
		}*/
	}
}
