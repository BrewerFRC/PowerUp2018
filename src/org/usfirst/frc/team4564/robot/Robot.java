package org.usfirst.frc.team4564.robot;
import org.usfirst.frc.team4564.robot.Elevator.States;
import org.usfirst.frc.team4564.robot.path.Path;
import org.usfirst.frc.team4564.robot.path.Paths;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
	public static Robot instance;
	private static DriveTrain dt = new DriveTrain();
	private static Intake intake = new Intake();
	private static Elevator elevator = new Elevator(intake);
	private static Compressor compressor = new Compressor(1);
	private Auto auto = new Auto();
	private Xbox driver = new Xbox(0);
	private Xbox operator = new Xbox(1);
	private Bat bat = new Bat();
	private LED led = new LED();
	
	private String gameData;
	
	@Override
	public void robotInit() {
		instance = this;
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
		SmartDashboard.putString("Position", auto.position + "");
		SmartDashboard.putString("Mode", auto.mode.toString());
		while (isDisabled()) {
			compressor.setClosedLoopControl(true);
			gameData = DriverStation.getInstance().getGameSpecificMessage();
			if(gameData != null) {
				Common.dashStr("Game Data", gameData);
				if (gameData.length() == 3) {
					auto.setGameData(gameData.toUpperCase());
				}
			}
			
			//Gyro Buttons
			if (driver.when("start")) {
				dt.getHeading().calibrate();
				Common.debug("Gyro Calculated"+dt.getHeading().getAngle());
			}
			
			//Auto Settings
			if (driver.when("x")) {
				auto.setPosition('L');
			}
			else if (driver.when("y")) {
				auto.setPosition('C');
			}
			else if (driver.when("b")) {
				auto.setPosition('R');
			}
			else if (driver.when("dPadLeft")) {
				auto.setMode(Auto.Mode.CROSS_LINE);
			}
			else if (driver.when("dPadUp")) {
				auto.setMode(Auto.Mode.SCALE);
			}
			else if (driver.when("dPadDown")) {
				auto.setMode(Auto.Mode.CLOSE);
			}
			elevator.debug();
			dashBoard();
    		Timer.delay(0.001);
		}
	}
	
	
	/**
	 * Control logic for autonomous mode.
	 */
	@Override
	public void autonomous() {
		//Path path = Paths.TWO_CUBE_RIGHT_SWITCH;
		Path path = auto.getPath();
		elevator.home();
		dt.getHeading().reset();
		intake.reset();
		path.reset();
		path.start();
		intake.hardArm();
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
    	if (elevator.getState() == States.STOPPED) {
    		elevator.home();
    	}
    	intake.reset();
    	//intake.hardArm();
    	if (intake.isPartiallyLoaded()) {
    		intake.hardArm();
    		intake.loading = true;
    	} else {
    		intake.loading = true;
    	}
    	led.setMode(LED.TELEOP);
    	while (isEnabled() && isOperatorControl()) {
    		time = Common.time();
    		double forward = 0;
    		double turn = 0;
    		compressor.setClosedLoopControl(true);
			SmartDashboard.putString("Position", "" + auto.position);
			SmartDashboard.putString("Mode", auto.mode.toString());
    		
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
    		else if(driver.getPressed("dPadUp")){
    			intake.moveVelocity(intake.MAX_VELOCITY);
    		}
    		else if(driver.getPressed("dPadDown")){
    			intake.moveVelocity(-intake.MAX_VELOCITY);
    		}
    		else{
    			intake.moveVelocity(0);
    		}
    		/*double intakePow = operator.deadzone(operator.getY(GenericHID.Hand.kRight), 0.15);
    		if (intakePow < 0.0) {
    			intake.joystickControl(60);
    		}
    		else if (intakePow > 0.0) {
    			intake.joystickControl(-60);
    		}*/
    		if (operator.getY(GenericHID.Hand.kRight) != 0){
    			intake.joystickControl(operator.deadzone(operator.getY(GenericHID.Hand.kRight), 0.15));
    			
    		}
    		intake.update();
    		//Intake
    		//intake arm pressure
    		if (driver.when("leftTrigger") || operator.when("leftTrigger")) {
    			if (!intake.isPartiallyLoaded()) {
    				intake.loading = true;
    			} else {
    				intake.loading = false;
    			}
    		}
    		if (driver.falling("leftTrigger") || operator.falling("leftTrigger")) {
    			intake.loading = true;
    		}
    		
    		if (intake.loading) {
				/*if (intake.isFullyLoaded()) {
					intake.hardArm();
				}*/
				//else 
				if (intake.isPartiallyLoaded()) {
					//intake.softArm();
					intake.hardArm();
				}
				else if (driver.getPressed("leftTrigger") || operator.getPressed("leftTrigger")) {
					intake.openArm();
				} else {
					//intake.softArm();
					intake.hardArm();
				}
    		} else {	//Unload
    			intake.openArm();
    		}
    			
    		//intake wheel control
    		if (driver.getPressed("a")) {
    			/*if (intake.isFullyLoaded()) {
    				driver.setRumble(RumbleType.kLeftRumble, 0.3);
    				driver.setRumble(RumbleType.kRightRumble, 0.3);
    			} else {
    				driver.setRumble(RumbleType.kLeftRumble, 0.0);
    				driver.setRumble(RumbleType.kRightRumble, 0.0);
    			}*/
    			//intake.setIntakePower(1.0);
    			intake.setLeftIntakePower(0.7);
    			intake.setRightIntakePower(1.0);
    		}
    		else if (driver.getPressed("rightTrigger") || operator.getPressed("rightTrigger")) {
    			intake.setIntakePower(-0.5);
    		}
    		else if (operator.getPressed("rightBumper")) {
    			intake.setIntakePower(-0.8);
    		}
    		else {
    			//driver.setRumble(RumbleType.kLeftRumble, 0.0);
				//driver.setRumble(RumbleType.kRightRumble, 0.0);
    			intake.setIntakePower(0.0);
    		}
    		//Rumble
    		if (intake.isFullyLoaded()){
    			driver.setRumble(RumbleType.kLeftRumble, 0.3);
				driver.setRumble(RumbleType.kRightRumble, 0.3);
    			operator.setRumble(RumbleType.kLeftRumble, 0.3);
				operator.setRumble(RumbleType.kRightRumble, 0.3);
    		}
    		else {
    			driver.setRumble(RumbleType.kLeftRumble, 0.0);
				driver.setRumble(RumbleType.kRightRumble, 0.0);
				operator.setRumble(RumbleType.kLeftRumble, 0.0);
				operator.setRumble(RumbleType.kRightRumble, 0.0);
    		}
    		led.update();
    		dashBoard();
    		//Robot loop delay
    		double delay = (1000.0/Constants.REFRESH_RATE - (Common.time() - time)) / 1000.0;
    		Timer.delay((delay > 0) ? delay : 0.001);
    	}
    }
	
	public void test() {
		while (isTest()) {
			compressor.setClosedLoopControl(true);
			auto.getPath();
			SmartDashboard.putString("Position", "" + auto.position);
			SmartDashboard.putString("Mode", auto.mode.toString());
			dashBoard();
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
		Common.dashBool("Loading", intake.loading);
	}
	
	public static Elevator getElevator() {
		return elevator;
	}
	public static Intake getIntake() {
		return intake;
	}
	public static Robot instance() {
		return instance;
	}
}
