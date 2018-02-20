package org.usfirst.frc.team4564.robot.path;

import org.usfirst.frc.team4564.robot.Common;
import org.usfirst.frc.team4564.robot.DriveTrain;
import org.usfirst.frc.team4564.robot.Elevator;
import org.usfirst.frc.team4564.robot.Intake;
import org.usfirst.frc.team4564.robot.Robot;

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
			NEAR_SCALE_LEFT, NEAR_SCALE_RIGHT, FAR_SCALE_LEFT, FAR_SCALE_RIGHT, CROSS_LINE,
			CENTER_SWITCH_LEFT, CENTER_SWITCH_RIGHT;
	
	public Paths() {
		// Old Paths Written
		TEST_FAR_SCALE = new Path()
			.addDriveStraight(0, 0, 0.65, "startDrive");
		
		// Switch Paths
		FAR_SWITCH_LEFT = new Path()
			.addDriveStraight(-193.85, 0, .65, "startDrive")
			.addPowerTurn(-90, 0.65, true)
			.addDriveStraight(188.31, -90, .65, "middleDrive")
			.addPowerTurn(0, .65, true)
			.addDriveStraight(-24, 0, .65, "cubePickupBackUp")
			.addDriveStraight(24, 0, .65, "cubePickupDriveForward");
		
		FAR_SWITCH_RIGHT = new Path()
			.addDriveStraight(-193.85, 0, .65, "startDrive")
			.addPowerTurn(90, 0.65, true)
			.addDriveStraight(188.31, 90, .65, "middleDrive")
			.addPowerTurn(0, .65, true)
			.addDriveStraight(-24, 0, .65, "cubePickupBackUp")
			.addDriveStraight(24, 0, .65, "cubePickupDriveForward");
		
		NEAR_SWITCH_LEFT = new Path()
			.addDriveStraight(-180, 0, -.75, "startDrive")
			.addDriveStraight(6, 0, 0.75, "middleDrive")
			.addPowerTurn(-90, 0.75, false)
			.addEvent(armUp)
			.addDrivePower(0.45)
			.addEvent(shoot);
		
		NEAR_SWITCH_RIGHT = new Path()
			.addDriveStraight(-180, 0, -.75, "startDrive")
			.addDriveStraight(6, 0, 0.75, "middleDrive")
			.addPowerTurn(90, 0.75, false)
			.addEvent(armUp)
			.addDrivePower(0.45)
			.addEvent(shoot);
		
		//Scale Paths
		FAR_SCALE_LEFT = new Path()
			.addDriveStraight(-164, 0, -.85, "startDrive")
			.addPowerTurn(90, .75, true)
			.addDriveStraight(-136, 90, -.85, "middleDrive")
			.addPowerTurn(0, .75, true)
			.addEvent(new Event(false) {
				@Override
				public void start() {
					this.complete = false;
					Robot.getElevator().moveToHeight(Robot.getElevator().ELEVATOR_HEIGHT);
					Robot.getIntake().movePosition(0.0);
				}
				@Override
				public void trigger() {
					if (Robot.getElevator().isComplete()) {
						this.complete = true;
					}
				}
			})
			.addEvent(intakeOverOnElevatorHeight)
			.addEvent(shootOnEventOneComplete)
			.addEvent(intakeZeroOnEventTwoComplete)
			.addEvent(elevatorZeroOnEventTwoComplete);
			//.addDriveStraight(-2, -19.1, -.75, "finalDrive");
		
		FAR_SCALE_RIGHT = new Path()
				.addDriveStraight(-164, 0, -.85, "startDrive")
				.addPowerTurn(-90, .75, true)
				.addDriveStraight(-136, -90, -.85, "middleDrive")
				.addPowerTurn(0, .75, true)
				.addEvent(new Event(false) {
					@Override
					public void start() {
						this.complete = false;
						Robot.getElevator().moveToHeight(Robot.getElevator().ELEVATOR_HEIGHT);
						Robot.getIntake().movePosition(0.0);
					}
					@Override
					public void trigger() {
						if (Robot.getElevator().isComplete()) {
							this.complete = true;
						}
					}
				})
				.addEvent(intakeOverOnElevatorHeight)
				.addEvent(shootOnEventOneComplete)
				.addEvent(intakeZeroOnEventTwoComplete)
				.addEvent(elevatorZeroOnEventTwoComplete);
		
		NEAR_SCALE_LEFT = new Path()
				.addDriveStraight(-117, 0, -0.85, "testDrive")
				.addDriveStraight(-88, 18, -0.85, "testDriveTurn")
				.addEvent(new Event(true) {
					boolean distReached = false;
					@Override
					public void start() {
						this.complete = false;
						this.distReached = false;
					}
					@Override
					public void trigger() {
						if (DriveTrain.instance().getAverageDist() < -10 && !distReached) {
							distReached = true;
							Robot.getElevator().moveToHeight(Robot.getElevator().ELEVATOR_HEIGHT);
							Robot.getIntake().movePosition(0.0);
						}
						if (Robot.getElevator().isComplete()) {
							this.complete = true;
						}
					}
				})
				.addEvent(intakeOverOnElevatorHeight)
				.addEvent(shootOnEventOneComplete)
				.addEvent(intakeZeroOnEventTwoComplete).addEvent(elevatorZeroOnEventTwoComplete);
		
		NEAR_SCALE_RIGHT = new Path()
				.addDriveStraight(-117, 0, -0.85, "testDrive")
				.addDriveStraight(-88, -18, -0.85, "testDriveTurn")
				.addEvent(new Event(true) {
					boolean distReached = false;
					@Override
					public void start() {
						this.complete = false;
						this.distReached = false;
					}
					@Override
					public void trigger() {
						if (DriveTrain.instance().getAverageDist() < -10 && !distReached) {
							distReached = true;
							Robot.getElevator().moveToHeight(Robot.getElevator().ELEVATOR_HEIGHT);
							Robot.getIntake().movePosition(0.0);
						}
						if (Robot.getElevator().isComplete()) {
							this.complete = true;
						}
					}
				})
				.addEvent(intakeOverOnElevatorHeight)
				.addEvent(shootOnEventOneComplete)
				.addEvent(intakeZeroOnEventTwoComplete).addEvent(elevatorZeroOnEventTwoComplete);
		
		// Combo Paths
		FAR_SCALE_SWITCH_LEFT = new Path();

		FAR_SCALE_SWITCH_RIGHT = new Path();
		
		CENTER_SWITCH_LEFT = new Path()
				.addDriveStraight(96, -30, 0.65, "centerDrive")
				.addEvent(armUp)
				.addDriveStraight(36, 0, 0.5, "straightDrive")
				.addEvent(shootWhenStopped);
		
		CENTER_SWITCH_RIGHT = new Path()
				.addDriveStraight(96, 30, 0.65, "centerDrive")
				.addEvent(armUp)
				.addDriveStraight(36, 0, 0.5, "straightDrive")
				.addEvent(shootWhenStopped);
		
		// Misc. Paths
		CROSS_LINE = new Path().addDriveStraight(50, 0, 0.65, "crossLine");
	}
	
	private Event intakeOverOnElevatorHeight = new Event(false) {
		@Override
		public void start() {
			this.complete = false;
		}
		@Override
		public void trigger() {
			Intake intake = Robot.getIntake();
			if (Robot.getElevator().getInches() >= 30 && Math.abs(DriveTrain.instance().getAverageVelocity()) < 4) {
				intake.movePosition(150);
				if (intake.getPosition() > 140) {
					this.complete = true;
				}
			}
		}
	},
	shootOnEventOneComplete = new Event(false) {
		long startTime = -1;
		@Override
		public void start() {
			this.complete = false;
			startTime = -1;
		}
		@Override
		public void trigger() {
			if (stage.eventComplete(1) && startTime == -1) {
				Common.debug("Complete passed on");
				startTime = Common.time();
			}
			if (Common.time() > startTime + 1000 && startTime != -1) {
				Robot.getIntake().setIntakePower(0.0);
				this.complete = true;
			}
			else if (startTime > 0) {
				Robot.getIntake().setIntakePower(-0.5);
			}
		}
	},
	shoot = new Event(false) {
		long startTime = -1;
		@Override
		public void start() {
			this.complete = false;
			startTime = Common.time();
		}
		@Override
		public void trigger() {
			if (Common.time() > startTime + 1000 && startTime != -1) {
				Robot.getIntake().setIntakePower(0.0);
				this.complete = true;
			}
			else if (startTime > 0) {
				Robot.getIntake().setIntakePower(-0.6);
			}
		}
	},
	shootWhenStopped = new Event(false) {
		long startTime = -1;
		@Override
		public void start() {
			this.complete = false;
			startTime = -1;
		}
		@Override
		public void trigger() {
			if (DriveTrain.instance().getAverageVelocity() < 2 && startTime == -1) {
				startTime = Common.time();
			}
			if (Common.time() > startTime + 1000 && startTime != -1) {
				Robot.getIntake().setIntakePower(0.0);
				this.complete = true;
			}
			else if (startTime > 0) {
				Robot.getIntake().setIntakePower(-0.6);
			}
		}
	},
	intakeZeroOnEventTwoComplete = new Event(false) {
		@Override
		public void start() {
			this.complete = false;
		}
		@Override
		public void trigger() {
			Intake intake = Robot.getIntake();
			if (stage.eventComplete(2)) {
				intake.movePosition(0);
			}
			if (intake.isComplete()) {
				this.complete = true;
			}
		}
	},
	elevatorZeroOnEventTwoComplete = new Event(false) {
		@Override
		public void start() {
			this.complete = true;
		}
		@Override
		public void trigger() {
			Elevator elevator = Robot.getElevator();
			if (stage.eventComplete(2)) {
				elevator.moveToHeight(0);
			}
			if (elevator.isComplete()) {
				this.complete = true;
			}
		}
	},
	armUp = new Event(false) {

		@Override
		public void start() {
			this.complete = false;
			Common.debug("Arm set");
			Robot.getIntake().movePosition(Robot.getIntake().MAX_ELEVATOR_SAFE-5);
		}

		@Override
		public void trigger() {
			Common.debug("Arm angle: " + Robot.getIntake().getPosition());
			if (Robot.getIntake().isComplete()) {
				this.complete = true;
			}
		}
	};
	
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
