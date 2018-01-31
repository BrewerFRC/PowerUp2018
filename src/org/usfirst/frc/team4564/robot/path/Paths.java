package org.usfirst.frc.team4564.robot.path;

public class Paths {
	public static Path FAR_SCALE, NEAR_SCALE, FAR_SCALE_SWITCH, NEAR_SCALE_SWITCH, FAR_SWITCH, NEAR_SWITCH, CROSS_LINE;
	
	public Paths() {
		FAR_SCALE = new Path().addDriveStraight(60, 0, 0.65, "startDrive")
				.addPowerTurn(76, 0.65)
				.addDriveStraight(72, 90, 0.9, "middleDrive")
				.addDriveStraight(36, 90, 0.65, "finalDrive")
				.addPowerTurn(12, 0.65)
				.addPIDDrive(36, 0, 0.4, 0.8, true, "driveScale");
		
		NEAR_SCALE = new Path().addDriveStraight(48, 0, 0.8, "startDrive")
				 .addDriveStraight(203.4, 11.8, 0.8, "angledDrive")
				 .addDriveStraight(36, 0, 0.8, "finalDrive");
		
		FAR_SWITCH = new Path();
		
		NEAR_SWITCH = new Path();
		
		FAR_SCALE_SWITCH = new Path();
		
		NEAR_SCALE_SWITCH = new Path();
		
		CROSS_LINE = new Path().addDriveStraight(50, 0, 0.65, "crossLine");
	}
}
