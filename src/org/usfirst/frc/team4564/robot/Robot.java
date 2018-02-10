package org.usfirst.frc.team4564.robot;
import org.usfirst.frc.team4564.robot.path.Event;
import org.usfirst.frc.team4564.robot.path.Path;
import org.usfirst.frc.team4564.robot.path.Paths;
import edu.wpi.first.wpilibj.AnalogInput;
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
	private AnalogInput pot = new AnalogInput(2);
	private DriveTrain dt = new DriveTrain();
	private Xbox j0 = new Xbox(0);
	private Xbox j1 = new Xbox(1);
	private Bat bat = new Bat();
	private Auto auto = new Auto();
	private String gameData;
	
	@Override
	public void robotInit() {
		//Initialize all paths.
		new Paths();
	}
	
	/**
	 * Control logic when the robot is disabled.
	 */
	@Override
	public void disabled() {
		while (isDisabled()) {
			Common.dashNum("Pot Out", pot.getValue());
			Common.dashNum("Average Distance", dt.getAverageDist());
			Common.dashNum("Left Counts", dt.getLeftCounts());
			Common.dashNum("Right Counts", dt.getRightCounts());
			gameData = DriverStation.getInstance().getGameSpecificMessage();
			if(gameData != null) {
				Common.dashStr("Game Data", gameData);
				if (gameData.length() == 3) {
					Common.dashBool("Do You Have Game Data", true);
				} else {
					Common.dashBool("Do You Have Game Data" , false);
				}
				auto.setGameData(gameData);
			}
			
			/*char c = gameData.charAt(0);
			if (c == 'R') {
				AND = &&
				OR = ||
			}*/
		}
	}
	
	
	/**
	 * Control logic for autonomous mode.
	 */
	@Override
	public void autonomous() {
		Paths.reset();
		Path path = Paths.FAR_SWITCH;
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
	 * Control logic for teleoperated mode.
	 */
	@Override
	public void operatorControl() {
    	long time;
    	Paths.reset();
    	Path path = Paths.FAR_SCALE;
    	path.start();
    	while (isEnabled() && isOperatorControl()) {
    		time = Common.time();
    		Common.dashNum("Ultrasonic", bat.getDistance());
    		double forward = 0;
    		double turn = 0;
    		forward = j0.getY(GenericHID.Hand.kLeft);
			turn  = j0.getX(GenericHID.Hand.kLeft);
    		
			if (j0.getPressed("a")) {
				double[] power = path.getDrive();
				dt.accelTankDrive(power[0], power[1]);
			}
			else {
				dt.accelDrive(forward, turn);
			}
    		
    		double delay = (1000.0/Constants.REFRESH_RATE - (Common.time() - time)) / 1000.0;
    		Timer.delay((delay > 0) ? delay : 0.001);
    	}
    }
}
