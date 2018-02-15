package org.usfirst.frc.team4564.robot;

import org.usfirst.frc.team4564.robot.path.Path;
import org.usfirst.frc.team4564.robot.path.Paths;

import edu.wpi.first.wpilibj.Compressor;
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
	private static DriveTrain dt = new DriveTrain();
	private static Intake intake = new Intake();
	private static Elevator elevator = new Elevator(intake);
	private static Compressor compressor = new Compressor(1);
	private Auto auto = new Auto();
	private Xbox j0 = new Xbox(0);
	private Xbox j1 = new Xbox(1);
	private Bat bat = new Bat();
	
	private String gameData;
	
	@Override
	public void robotInit() {
		//Initialize all paths.
		new Paths();
		//elevator.resetEncoder();
	}
	
	/**
	 * Control logic when the robot is disabled.
	 */
	@Override
	public void disabled() {
		while (isDisabled()) {
			Common.dashNum("Average Distance", dt.getAverageDist());
			Common.dashNum("Left Counts", dt.getLeftCounts());
			Common.dashNum("Right Counts", dt.getRightCounts());
			Common.dashNum("IR Output", intake.getCubeDistance() );
			Common.dashBool("Is Loaded", intake.isLoaded());
			Common.dashNum("Intake Arm Position", intake.getRawPosition());
			Common.dashNum("Intake Arm Degrees", intake.getPosition());
			compressor.setClosedLoopControl(true);
			gameData = DriverStation.getInstance().getGameSpecificMessage();
			if(gameData != null) {
				Common.dashStr("Game Data", gameData);
				if (gameData.length() == 3) {
					auto.setGameData(gameData);
					Common.dashBool("Do You Have Game Data", true);
					auto.setGameData(gameData);
				} else {
					Common.dashBool("Do You Have Game Data" , false);
				}
			}
			elevator.debug();
		}
	}
	
	
	/**
	 * Control logic for autonomous mode.
	 */
	@Override
	public void autonomous() {
		Paths.reset();
		Path path = Paths.TEST_FAR_SCALE;
		path.start();
		while (isEnabled() && isAutonomous()) {
			long time = Common.time();
			
			path.drive();
			
			Common.dashNum("gyroAngle", DriveTrain.instance().getHeading().getAngle());
			//System.out.println("Left/Right Distance: " + dt.getLeftDist() + ":" + dt.getRightDist() +
			//		"; Motor Powers: " + power[0] + ":" + power[1]);
			
			Timer.delay(Math.max(0, (1000.0/Constants.REFRESH_RATE - (Common.time() - time))/1000));
		}
	}

	/**
	 * Control logic for teleop mode.
	 */
	@Override
	public void operatorControl() {
    	long time;
    	Paths.reset();
    	Path path = Paths.TEST_FAR_SCALE;
    	path.start();
    	elevator.home();
    	while (isEnabled() && isOperatorControl()) {
    		time = Common.time();
    		Common.dashNum("Left Counts", dt.getLeftCounts());
			Common.dashNum("Right Counts", dt.getRightCounts());
    		double forward = 0;
    		double turn = 0;
    		compressor.setClosedLoopControl(true);
    		if (j0.when("dPadLeft")) {
    			DriveTrain.DRIVEACCEL -= 0.005;
    		}
    		if (j0.when("dPadRight")) {
    			DriveTrain.DRIVEACCEL += 0.005;
    		}
    		Common.dashNum("Drive Acceleration", DriveTrain.DRIVEACCEL);
    		
    		forward = -j0.getY(GenericHID.Hand.kLeft);
			turn  = -j0.getX(GenericHID.Hand.kLeft);
			/*if (j0.getPressed("b")) {
				double[] power = path.getDrive();
				dt.accelTankDrive(power[0], power[1]);
			}*/
			dt.accelDrive(forward, turn);
/*			
			if (j0.getPressed("x")) {
				elevator.moveToHeight(30);
			}
    		elevator.joystickControl(j0.deadzone(j0.getY(GenericHID.Hand.kRight), 0.15));
    		
    		elevator.update();
*/    		
    		if (j0.getPressed("x")) {
    			intake.setArmPower(-0.2);
    		} else if (j0.getPressed("y")) {
    			intake.setArmPower(0.5);
    		} else {
    			intake.setArmPower(0.0);
    		}
    			
    		if (j0.getPressed("a")) {
    			intake.setIntakePower(1.0);
    		} else if (j0.getPressed("b")) {
    			intake.setIntakePower(-1.0);
    		} else {
    			intake.setIntakePower(0.0);
    		}
    		
    		double delay = (1000.0/Constants.REFRESH_RATE - (Common.time() - time)) / 1000.0;
    		Timer.delay((delay > 0) ? delay : 0.001);
    	}
    }
	
	public static Elevator getElevator() {
		return elevator;
	}
}
