package org.usfirst.frc.team4564.robot.path;

import org.usfirst.frc.team4564.robot.Common;

/**
 * A class containing pre-built Paths.
 * Created February 2018
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Evan McCoy
 */
public class Paths {
	public static Path FAR_SCALE, NEAR_SCALE, FAR_SCALE_SWITCH, NEAR_SCALE_SWITCH, FAR_SWITCH, NEAR_SWITCH, CROSS_LINE;
	
	public Paths() {
		FAR_SCALE = new Path().addDriveStraight(60, 0, 0.65, "startDrive")
				.addPowerTurn(90, 0.65)
				.addDriveStraight(72, 90, 0.7, "middleDrive")
				.addDriveStraight(36, 90, 0.65, "finalDrive")
				.addPowerTurn(0, 0.65)
				.addPIDDrive(48, 0, 0.4, 0.8, true, "driveScale");
		
		NEAR_SCALE = new Path().addPIDDrive(60, 0, 0.4, 0.7, true, "startDrive");
				 //.addDriveStraight(203.4, 11.8, 0.6, "angledDrive")
				 //.addDriveStraight(36, 0, 0.6, "finalDrive");
		
		FAR_SWITCH = new Path().addPIDDrive(60, 0, 0.4, 0.7, true, "eventTest")
				.addEvent(new Event(false) {
					private long startTime;
					private boolean triggered;
					@Override
					public void start() {
						triggered = false;
					}
					@Override
					public void trigger() {
						if (complete) {
							return;
						}
						if (!triggered) {
							startTime = Common.time();
							triggered = true;
						}
						else {
							if (Common.time() > startTime + 3000) {
								System.out.println("Complete");
								this.complete = true;
							}
						}
					}
				});
		
		NEAR_SWITCH = new Path();
		
		FAR_SCALE_SWITCH = new Path();
		
		NEAR_SCALE_SWITCH = new Path();
		
		CROSS_LINE = new Path().addDriveStraight(50, 0, 0.65, "crossLine");
	}
	
	/**
	 * Resets the state of the pre-builts paths.
	 */
	public static void reset() {
		FAR_SCALE.reset();
		NEAR_SCALE.reset();
		FAR_SWITCH.reset();
		NEAR_SWITCH.reset();
		FAR_SCALE_SWITCH.reset();
		NEAR_SCALE_SWITCH.reset();
		CROSS_LINE.reset();
	}
}
