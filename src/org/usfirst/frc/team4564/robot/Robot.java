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
	private Xbox driver = new Xbox(0);
	private Xbox operator = new Xbox(1);
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
		//dt.getHeading().calibrate();
		while (isDisabled()) {
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
			dashBoard();
		}
	}
	
	
	/**
	 * Control logic for autonomous mode.
	 */
	@Override
	public void autonomous() {
		Path path = Paths.TWO_CUBE_LEFT_NO_DROP;
		elevator.home();
		dt.getHeading().reset();
		intake.reset();
		path.reset();
		path.start();
		while (isEnabled() && isAutonomous()) {
			long time = Common.time();
			
			path.drive();
			elevator.update();
			intake.update();
			//System.out.println("Left/Right Distance: " + dt.getLeftDist() + ":" + dt.getRightDist() +
			//		"; Motor Powers: " + power[0] + ":" + power[1]);
			
			dashBoard();
			Timer.delay(Math.max(0, (1000.0/Constants.REFRESH_RATE - (Common.time() - time))/1000));
		}
	}

	/**
	 * Control logic for teleop mode.
	 */
	@Override
	public void operatorControl() {
    	long time;
    	elevator.home();
    	intake.reset();
    	while (isEnabled() && isOperatorControl()) {
    		time = Common.time();
    		double forward = 0;
    		double turn = 0;
    		compressor.setClosedLoopControl(true);
    		
    		//Drivetrain
    		//	Shifting
    		if (driver.when("leftBumper")) {
    			dt.shiftLow();
    		}
    		else if (driver.when("rightBumper")) {
    			dt.shiftHigh();
    		}
    		forward = -driver.getY(GenericHID.Hand.kLeft);
			turn  = -driver.getX(GenericHID.Hand.kLeft);	
			dt.accelDrive(forward, turn);
			
			//Elevator
			//	Move to scale height
			if (operator.when("dPadUp")) {
				elevator.moveToHeight(elevator.ELEVATOR_HEIGHT);
			}
			if (operator.when("dPadDown")) {
				elevator.moveToHeight(elevator.SWITCH_HEIGHT);
			}
			elevator.joystickControl(operator.deadzone(operator.getY(GenericHID.Hand.kLeft), 0.15));
    		elevator.update();
    		
    		//Intake Arm
    		if (operator.when("x")) {
    			intake.movePosition(intake.FRONT_HORIZONTAL);
    		}
    		else if (operator.when("y")) {
    			intake.movePosition(intake.MAX_ELEVATOR_SAFE-10);
    		}
    		else if (operator.when("b")) {
    			intake.movePosition(intake.MAX_ANGLE);
    		}
    		/*double intakePow = operator.deadzone(operator.getY(GenericHID.Hand.kRight), 0.15);
    		if (intakePow < 0.0) {
    			intake.joystickControl(60);
    		}
    		else if (intakePow > 0.0) {
    			intake.joystickControl(-60);
    		}*/
    		intake.joystickControl(operator.deadzone(operator.getY(GenericHID.Hand.kRight), 0.15));
    		intake.update();

    		//Intake
    		if (driver.getPressed("a")) {
    			intake.setIntakePower(1.0);
    		}
    		else if (operator.getPressed("leftTrigger")) {
    			intake.setIntakePower(-0.5);
    		}
    		else if (driver.getPressed("rightTrigger") || operator.getPressed("rightTrigger")) {
    			intake.setIntakePower(-1.0);
    		}
    		else {
    			intake.setIntakePower(0.0);
    		}
    		dashBoard();
    		//Robot loop delay
    		double delay = (1000.0/Constants.REFRESH_RATE - (Common.time() - time)) / 1000.0;
    		Timer.delay((delay > 0) ? delay : 0.001);
    	}
    }
	
	public void dashBoard() {
		Common.dashBool("Intake elevatorSafe", intake.elevatorSafe());
		Common.dashNum("Intake Arm Degrees", intake.getPosition());
		Common.dashNum("Intake Arm Position", intake.getRawPosition());
		Common.dashNum("Intake arm velocity", intake.getVelocity());
		Common.dashBool("Elevator intakeSafe", elevator.intakeSafe());
		Common.dashNum("Elevator encoder", elevator.getEncoder());
		Common.dashNum("Drive Acceleration", DriveTrain.DRIVEACCEL);
		Common.dashNum("Left Counts", dt.getLeftCounts());
		Common.dashNum("Right Counts", dt.getRightCounts());
		Common.dashNum("gyroAngle", DriveTrain.instance().getHeading().getAngle());
		Common.dashNum("Average Distance", dt.getAverageDist());
		Common.dashNum("Left Counts", dt.getLeftCounts());
		Common.dashNum("Right Counts", dt.getRightCounts());
		Common.dashNum("IR Output", intake.getCubeDistance() );
		Common.dashBool("Is fully loaded", intake.isFullyLoaded());
		Common.dashBool("Is partially loaded", intake.isPartiallyLoaded());
		Common.dashNum("Bat", bat.getDistance());
		Common.dashStr("Intake arm State", intake.state.toString());
	}
	
	public static Elevator getElevator() {
		return elevator;
	}
	public static Intake getIntake() {
		return intake;
	}
}
