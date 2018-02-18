package org.usfirst.frc.team4564.robot.path;

/**
 * A class containing pre-built Paths.
 * Created February 2018
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Evan McCoy
 * @author Jacob Cote
 */
public class Paths {
	public static Path
			TEST_FAR_SCALE, TEST_NEAR_SCALE, FAR_SCALE_SWITCH, NEAR_SCALE_SWITCH, FAR_SWITCH_LEFT,
			FAR_SWITCH_RIGHT, NEAR_SWITCH_LEFT, NEAR_SWITCH_RIGHT, FAR_SCALE_SWITCH_LEFT, FAR_SCALE_SWITCH_RIGHT,
			NEAR_SCALE_LEFT, NEAR_SCALE_RIGHT, FAR_SCALE_LEFT, FAR_SCALE_RIGHT, CROSS_LINE;
	
	public Paths() {
		// Old Paths Written
		TEST_FAR_SCALE = new Path()
			.addDriveStraight(60, 0, 0.65, "startDrive")
			.addPowerTurn(90, 0.65)
			.addDriveStraight(72, 90, 0.7, "middleDrive")
			.addDriveStraight(36, 90, 0.65, "finalDrive")
			.addPowerTurn(0, 0.65)
			.addPIDDrive(48, 0, 0.4, 0.8, true, "driveScale");
		
		TEST_NEAR_SCALE = new Path().addDriveStraight(-78, 0, -0.7, "testDrive")
				.addDriveStraight(-140, 18, -0.7, "testDriveTurn");
			//addPIDDrive(60, 0, 0.4, 0.7, true, "startDrive");
			 //.addDriveStraight(203.4, 11.8, 0.6, "angledDrive")
			 //.addDriveStraight(36, 0, 0.6, "finalDrive");
		
		// New Paths Written
		// Switch Paths
		FAR_SWITCH_LEFT = new Path()
			.addDriveStraight(-193.85, 0, .65, "startDrive")
			.addPowerTurn(-90, 0.65)
			.addDriveStraight(188.31, -90, .65, "middleDrive")
			.addPowerTurn(0, .65)
			.addDriveStraight(-24, 0, .65, "cubePickupBackUp")
			.addDriveStraight(24, 0, .65, "cubePickupDriveForward");
		
		FAR_SWITCH_RIGHT = new Path()
			.addDriveStraight(-193.85, 0, .65, "startDrive")
			.addPowerTurn(90, 0.65)
			.addDriveStraight(188.31, 90, .65, "middleDrive")
			.addPowerTurn(0, .65)
			.addDriveStraight(-24, 0, .65, "cubePickupBackUp")
			.addDriveStraight(24, 0, .65, "cubePickupDriveForward");
		
		NEAR_SWITCH_LEFT = new Path()
			.addDriveStraight(-299.7, 0, .65, "startDrive")
			.addDriveStraight(111.85, 21.8, .65, "middleDrive")
			.addDriveStraight(24, -21.8, .65, "cubePickupBackUp")
			.addDriveStraight(-24, 0, .65, "cubePickupDriveForward");
		
		NEAR_SWITCH_RIGHT = new Path()
			.addDriveStraight(-299.7, 0, .65, "startDrive")
			.addDriveStraight(111.85, -21.8, .65, "middleDrive")
			.addDriveStraight(-24, 21.8, .65, "cubePickupBackUp")
			.addDriveStraight(24, 0, .65, "cubePickupDriveForward");
		
		//Scale Paths
		FAR_SCALE_LEFT = new Path()
			.addDriveStraight(-194, 0, .65, "startDrive")
			.addPowerTurn(90, .65)
			.addDriveStraight(-195.46, 90, .65, "middleDrive")
			.addPowerTurn(-10.2, .65)
			.addDriveStraight(-84.5, -10.2, .65, "finalDrive");
		
		FAR_SCALE_RIGHT = new Path()
			.addDriveStraight(-194, 0, .65, "startDrive")
			.addPowerTurn(-90, .65)
			.addDriveStraight(-195.46, -90, .65, "middleDrive")
			.addPowerTurn(10.2, .65)
			.addDriveStraight(-84.5, 10.2, .65, "finalDrive");
		
		NEAR_SCALE_LEFT = new Path()
			.addDriveStraight(-159.2+30, 0, -.65, "startDrive")
			.addDriveStraight(-106.3, 12.4, -.65, "finalDrive");
		
		NEAR_SCALE_RIGHT = new Path()
			.addDriveStraight(-159.2, 0, -.65, "startDrive")
			.addDriveStraight(-106.3, -12.4, -.65, "finalDrive");
		
		// Combo Paths
		FAR_SCALE_SWITCH_LEFT = new Path();

		FAR_SCALE_SWITCH_RIGHT = new Path();
		
		// Misc. Paths
		CROSS_LINE = new Path().addDriveStraight(50, 0, 0.65, "crossLine");
	}
	
	/**
	 * Resets the state of the pre-builts paths.
	 */
	public static void reset() {
		TEST_FAR_SCALE.reset();
		TEST_NEAR_SCALE.reset();
		FAR_SWITCH_LEFT.reset();
		FAR_SWITCH_RIGHT.reset();
		NEAR_SWITCH_LEFT.reset();
		NEAR_SWITCH_RIGHT.reset();
		NEAR_SCALE_LEFT.reset();
		NEAR_SCALE_RIGHT.reset();
		CROSS_LINE.reset();
		FAR_SCALE_RIGHT.reset();
		FAR_SCALE_LEFT.reset();
	}
}
