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
 * @author Brent Roberts
 */
public class Paths {
	public static Path
			NEAR_SWITCH_LEFT, NEAR_SWITCH_RIGHT,
			NEAR_SCALE_LEFT, NEAR_SCALE_RIGHT, 
			FAR_SCALE_LEFT, FAR_SCALE_RIGHT, 
			CROSS_LINE,
			CENTER_SWITCH_LEFT, CENTER_SWITCH_RIGHT, 
			TWO_CUBE_LEFT_SWITCH, TWO_CUBE_RIGHT_SWITCH, 
			TWO_CUBE_LEFT_SCALE, TWO_CUBE_RIGHT_SCALE,
			TWO_CUBE_LEFT_STOP, TWO_CUBE_RIGHT_STOP;
	
	public Paths() {
		//Backup paths
		CROSS_LINE = new Path().addDriveStraight(-95, 0, 0.65, "crossLine");
		
		CENTER_SWITCH_LEFT = new Path()
				.addDriveStraight(96, -30, 0.65, "centerDrive")
				.addEvent(armUp())
				.addDriveStraight(36, 0, 0.5, "straightDrive")
				.addEvent(shootWhenStopped());
		
		CENTER_SWITCH_RIGHT = new Path()
				.addDriveStraight(96, 30, 0.65, "centerDrive")
				.addEvent(armUp())
				.addDriveStraight(36, 0, 0.5, "straightDrive")
				.addEvent(shootWhenStopped());
		
		//Switch paths
		NEAR_SWITCH_LEFT = new Path()
			.addDriveStraight(-180, 0, -.75, "startDrive")
			.addDriveStraight(6, 0, 0.75, "middleDrive")
			.addPowerTurn(-90, 0.75, false)
			.addEvent(armUp())
			.addDrivePower(0.45)
			.addEvent(shoot());
		
		NEAR_SWITCH_RIGHT = new Path()
			.addDriveStraight(-180, 0, -.75, "startDrive")
			.addDriveStraight(6, 0, 0.75, "middleDrive")
			.addPowerTurn(90, 0.75, false)
			.addEvent(armUp())
			.addDrivePower(0.45)
			.addEvent(shoot());
		
		//Scale Paths
		FAR_SCALE_LEFT = new Path()
			//.addDriveStraight(-164, 0, -.85, "startDrive")
			.addDriveStraight(-168, 0, -.85, "startDrive") //added 4 inches
			.addPowerTurn(90, .75, true)
			//.addDriveStraight(-132, 90, -.85, "middleDrive")
			.addDriveStraight(-134, 90, -.85, "middleDrive") //added 2 inches
			.addPowerTurn(0, .75, true)
			//.addDriveStraight(0, DriveTrain.instance().getHeading().getAngle(), 0.0, "drive")
			//.addDriveStraight(1, DriveTrain.instance().getHeading().getAngle(), 0.5, "drive") //added some distance(1 inch at 0.5) 
			.addDriveStraight(-7, DriveTrain.instance().getHeading().getAngle(), -0.5, "drive") //previously drove wrong direction, Was -5 changed to -7
			.addEvent(elevatorUpAtDistance(0))
			.addEvent(intakeOverOnElevatorHeight())
			.addEvent(shootOnEventComplete(1, -0.6))
			.addEvent(intakeHomeOnEventComplete(2))
			.addEvent(elevatorZeroOnEventTwoComplete());
		
		FAR_SCALE_RIGHT = new Path()
				.addDriveStraight(-164, 0, -.85, "startDrive")
				.addPowerTurn(-90, .75, true)
				.addDriveStraight(-132, -90, -.85, "middleDrive")
				.addPowerTurn(0, .75, true)
				.addDriveStraight(0, DriveTrain.instance().getHeading().getAngle(), 0.0, "drive")
				.addEvent(elevatorUpAtDistance(0))
				.addEvent(intakeOverOnElevatorHeight())
				.addEvent(shootOnEventComplete(1, -0.6))
				.addEvent(intakeHomeOnEventComplete(2))
				.addEvent(elevatorZeroOnEventTwoComplete());
		
		NEAR_SCALE_LEFT = nearScaleLeft();
		
		NEAR_SCALE_RIGHT = nearScaleRight();
		
		//Two cube paths
		//Near scale on left side, then pick up second cube.
		TWO_CUBE_LEFT_STOP = nearScaleLeftPickupSecond();
		
		//Near scale on right side, then pick up second cube.
		TWO_CUBE_RIGHT_STOP = nearScaleRightPickupSecond();
		
		//Near scale from the left side, then second cube in near switch.
		TWO_CUBE_LEFT_SWITCH = nearScaleLeftPickupSecond()
				.addEvent(elevatorToSwitchOnEventComplete(0))
				.addDriveStraight(6, -18, 0.75, "drive")
				.addEvent(new Event(false) {
					long startTime = -1;
					@Override
					public void start(Stage stage) {
						this.complete = false;
						startTime = -1;
					}
					@Override
					public void trigger(Stage stage) {
						if (DriveTrain.instance().getAverageVelocity() < 2 && startTime == -1 && DriveTrain.instance().getAverageDist() > 4) {
							startTime = Common.time();
						}
						if (Common.time() > startTime + 300 && startTime != -1) {
							Robot.getIntake().setIntakePower(0.0);
							this.complete = true;
						}
						else if (startTime > 0) {
							Robot.getIntake().setIntakePower(-0.6);
						}
					}
				});
		
		//Near scale from right side, then second cube in near switch.
		TWO_CUBE_RIGHT_SWITCH = nearScaleRightPickupSecond()
				.addEvent(elevatorToSwitchOnEventComplete(0))
				.addDriveStraight(6, 18, 0.75, "drive")
				.addEvent(new Event(false) {
					long startTime = -1;
					@Override
					public void start(Stage stage) {
						this.complete = false;
						startTime = -1;
					}
					@Override
					public void trigger(Stage stage) {
						if (DriveTrain.instance().getAverageVelocity() < 2 && startTime == -1 && DriveTrain.instance().getAverageDist() > 4) {
							startTime = Common.time();
						}
						if (Common.time() > startTime + 300 && startTime != -1) {
							Robot.getIntake().setIntakePower(0.0);
							this.complete = true;
						}
						else if (startTime > 0) {
							Robot.getIntake().setIntakePower(-0.6);
						}
					}
				});
		
		//Near scale on left side, then second cube in near scale.
		TWO_CUBE_LEFT_SCALE = nearScaleLeftPickupSecond()
				.addDriveStraight(-36, 5, -0.65, "drive")
				.addEvent(elevatorUpAtDistance(0))
				.addEvent(intakeOverOnElevatorHeight())
				.addEvent(shootOnEventComplete(1, -0.5))
				.addEvent(intakeHomeOnEventComplete(2))
				.addEvent(elevatorZeroOnEventTwoComplete());
		
		//Near scale on left side, then second cube in near scale 
		TWO_CUBE_RIGHT_SCALE =nearScaleRightPickupSecond()
				.addDriveStraight(-36, -5, -0.65, "drive")
				.addEvent(elevatorUpAtDistance(0))
				.addEvent(intakeOverOnElevatorHeight())
				.addEvent(shootOnEventComplete(1, -0.5))
				.addEvent(intakeHomeOnEventComplete(2))
				.addEvent(elevatorZeroOnEventTwoComplete());
	}
	
	///////////////////////////////////////////////
	// Prebuilt Paths
	///////////////////////////////////////////////
	
	/**
	 * The fundamental path for dropping a cube in the near scale from the left side.
	 * 
	 * @return - the path
	 */
	public Path nearScaleLeft() {
		return new Path()
				//.addDriveStraight(-117, 0, -0.85, "testDrive")
				.addDriveStraight(-121, 0, -0.85, "testDrive")
				.addDriveStraight(-88, 17, -0.85, "testDriveTurn")
				//.addDriveStraight(-94, 17, -0.85, "testDriveTurn") //ran test but too close to platform
				.addEvent(elevatorUpAtDistance(-20))
				.addEvent(intakeOverOnElevatorHeight())
				.addEvent(shootOnEventComplete(1, -0.6))
				.addEvent(intakeHomeOnEventComplete(2))
				.addEvent(elevatorZeroOnEventTwoComplete());
	}
	/**
	 * The fundamental path for dropping a cube in the near scale from the right side.
	 * 
	 * @return - the path
	 */
	public Path nearScaleRight() {
		return new Path()
				.addDriveStraight(-117, 0, -0.85, "testDrive")
				.addDriveStraight(-88, -17, -0.85, "testDriveTurn")
				.addEvent(elevatorUpAtDistance(-20))
				.addEvent(intakeOverOnElevatorHeight())
				.addEvent(shootOnEventComplete(1, -0.6))
				.addEvent(intakeHomeOnEventComplete(2))
				.addEvent(elevatorZeroOnEventTwoComplete());
	}
	
	/**
	 * Near scale left and picks up a second cube.
	 * 
	 * @return - the path
	 */
	public Path nearScaleLeftPickupSecond() {
		return nearScaleLeft()
//		.addDriveStraight(18, 18, 0.75, "DriveBack")
//		.addDriveStraight(21, -18, 0.6, "drive")
		.addDriveStraight(21, 18, 0.75, "DriveBack") //adding 6 inches, 3 to each leg
		.addDriveStraight(24, -18, 0.6, "drive")
		.addEvent(loadCubeLeft());
	}
	/**
	 * Near scale right and picks up a second cube.
	 * 
	 * @return - the path
	 */
	public Path nearScaleRightPickupSecond() {
		return nearScaleRight()
		.addDriveStraight(18, -18, 0.75, "DriveBack")
		.addDriveStraight(21, 18, 0.6, "drive")
		.addEvent(loadCubeRight());
	}
		
	///////////////////////////////////////////////
	// Prebuilt Events
	///////////////////////////////////////////////
	
	public Event armUp() {
		return new Event(false) {
			@Override
			public void start(Stage stage) {
				this.complete = false;
				Common.debug("Arm set");
				Robot.getIntake().movePosition(Robot.getIntake().MAX_ELEVATOR_SAFE-5);
			}

			@Override
			public void trigger(Stage stage) {
				//Common.debug("Arm angle: " + Robot.getIntake().getPosition());
				if (Robot.getIntake().isComplete()) {
					this.complete = true;
					Common.debug("armUp complete" + Robot.getIntake().getPositionTarget());
				}
			}
		};
	}
	
	/**
	 * Runs the elevator to the top starting at a specified drivetrain distance. Complete when the elevator is at the top.
	 * 
	 * @param distance - the distance in inches
	 * @return - the event
	 */
	public Event elevatorUpAtDistance(double distance) {
		return new Event(true) {
			boolean distReached = false;
			@Override
			public void start(Stage stage) {
				this.complete = false;
				this.distReached = false;
			}
			@Override
			public void trigger(Stage stage) {
				if (Math.abs(DriveTrain.instance().getAverageDist()) > Math.abs(distance) && !distReached) {
					distReached = true;
					Robot.getElevator().moveToHeight(Robot.getElevator().ELEVATOR_HEIGHT);
					Robot.getIntake().movePosition(Robot.getIntake().MAX_ELEVATOR_SAFE);
				}
				if (Robot.getElevator().isComplete() && distReached) {
					this.complete = true;
				}
			}
		};
	}
	
	/**
	 * Moves the elevator to switch height after a specified event has completed. Complete when the elevator has reached the height.
	 * 
	 * @param event - the event index to listen on.
	 * @return - the event
	 */
	public Event elevatorToSwitchOnEventComplete(int event) {
		return new Event(true) {
			@Override
			public void start(Stage stage) {
				this.complete = false;
			}
			@Override
			public void trigger(Stage stage) {
				if (stage.eventComplete(event)) {
					//Common.debug("Elevator moving up");
					Robot.getElevator().moveToHeight(20);
					if (Robot.getElevator().isComplete()) {
						Common.debug("Elevator Moved up");
						complete = true;
					}
				}
			}
		};
	}
	
	/**
	 * Moves the elevator to the bottom after a specified event has completed. Complete when the elevator is below a specific height.
	 * 
	 * @param event - the event index to listen for.
	 * @return - the event
	 */
	public Event elevatorZeroOnEventTwoComplete() {
		return new Event(true) {
			@Override
			public void start(Stage stage) {
				this.complete = false;
			}
			@Override
			public void trigger(Stage stage) {
				Elevator elevator = Robot.getElevator();
				if (stage.eventComplete(2)) {
					elevator.moveToHeight(0);
					if (elevator.getInches() <= 20) {
						this.complete = true;
					}
				}
			}
		};
	}
	
	/**
	 * Rotates the intake over the top after the elevator is at a minimum height. Complete when intake is over back.
	 * 
	 * @return - the event
	 */
	public Event intakeOverOnElevatorHeight() {
		return new Event(true) {
			@Override
			public void start(Stage stage) {
				this.complete = false;
			}
			@Override
			public void trigger(Stage stage) {
				Intake intake = Robot.getIntake();
				if (Robot.getElevator().getInches() >= 30 && Math.abs(DriveTrain.instance().getAverageVelocity()) < 4) {
					intake.movePosition(150);
					if (intake.getPosition() > 125) {
						this.complete = true;
					}
				}
			}
		};
	}
	
	/**
	 * Moves the intake to the bottom after a specified event has completed. Complete when the intake reaches the bottom.
	 * 
	 * @param event - the event index to listen on
	 * @return - the event
	 */
	public Event intakeHomeOnEventComplete(int event) {
		return new Event(true) {
			@Override
			public void start(Stage stage) {
				this.complete = false;
			}
			@Override
			public void trigger(Stage stage) {
				Intake intake = Robot.getIntake();
				if (stage.eventComplete(event)) {
					intake.movePosition(-7);
					if (intake.isComplete()) {
						this.complete = true;
					}
				}
			}
		};
	}
	
	/**
	 * Shoots immediately at the beginning of a stage. Complete after 0.3 seconds of run.
	 * 
	 * @return - the event
	 */
	public Event shoot() {
		return new Event(false) {
			long startTime = -1;
			@Override
			public void start(Stage stage) {
				this.complete = false;
				startTime = Common.time();
			}
			@Override
			public void trigger(Stage stage) {
				if (Common.time() > startTime + 300 && startTime != -1) {
					Robot.getIntake().setIntakePower(0.0);
					this.complete = true;
				}
				else if (startTime > 0) {
					Robot.getIntake().setIntakePower(-0.6);
				}
			}
		};
	}
	
	/**
	 * Shoots when the specified event completes.  Complete after 0.3 seconds of run.
	 * 
	 * @param event - the event index to listen on
	 * @return - the event
	 */
	public Event shootOnEventComplete(int event, double power) {
		return new Event(true) {
			long startTime = -1;
			@Override
			public void start(Stage stage) {
				Common.debug("Shoot 2 started"+startTime);
				this.complete = false;
				startTime = -1;
			}
			@Override
			public void trigger(Stage stage) {
				Common.debug("Event One Complete: " + stage.eventComplete(1));
				if (stage.eventComplete(event) && startTime == -1) {
					Common.debug("Complete passed on");
					startTime = Common.time();
				}
				if (Common.time() > startTime + 300 && startTime != -1) {
					Robot.getIntake().setIntakePower(0.0);
					this.complete = true;
				}
				else if (startTime > 0) {
					Robot.getIntake().setIntakePower(power);
				}
			}
		};
	}
	
	/**
	 * Shoots when the drivetrain reports a near-stopped velocity. Complete after 0.3 seconds of run.
	 * 
	 * @return - the event
	 */
	public Event shootWhenStopped() {
		return new Event(false) {
			long startTime = -1;
			@Override
			public void start(Stage stage) {
				this.complete = false;
				startTime = -1;
			}
			@Override
			public void trigger(Stage stage) {
				if (DriveTrain.instance().getAverageVelocity() < 2 && startTime == -1) {
					startTime = Common.time();
				}
				if (Common.time() > startTime + 300 && startTime != -1) {
					Robot.getIntake().setIntakePower(0.0);
					this.complete = true;
				}
				else if (startTime > 0) {
					Robot.getIntake().setIntakePower(-0.6);
				}
			}
		};
	}
	
	/**
	 * Loads cube at the beginning of the stage. Complete when the intake reads a cube.
	 * 
	 * @return - the event
	 */
	public Event loadCubeLeft() {
		return new Event(true) {
			@Override
			public void start(Stage stage) {
				complete = false;
			}
			@Override
			public void trigger(Stage stage) {
				Robot.getIntake().setLeftIntakePower(1.0);
				Robot.getIntake().setRightIntakePower(0.5);
				if (Robot.getIntake().isPartiallyLoaded()) {
					Robot.getIntake().setIntakePower(0.0);
					complete = true;
				}
			}
		};
	}
	
	/**
	 * Loads cube at the beginning of the stage. Complete when the intake reads a cube.
	 * 
	 * @return - the event
	 */
	public Event loadCubeRight() {
		return new Event(true) {
			@Override
			public void start(Stage stage) {
				complete = false;
			}
			@Override
			public void trigger(Stage stage) {
				Robot.getIntake().setLeftIntakePower(0.5);
				Robot.getIntake().setRightIntakePower(1.0);
				if (Robot.getIntake().isPartiallyLoaded()) {
					Robot.getIntake().setIntakePower(0.0);
					complete = true;
				}
			}
		};
	}
	
	/**
	 * Loads cube at the beginning of the stage. Complete after 1.5 seconds.
	 * 
	 * @return - the event
	 */
	public Event loadCubeTime() {
		return new Event(true) {
			long startTime = -1;
			@Override
			public void start(Stage stage) {
				complete = false;
				startTime = Common.time();
			}

			@Override
			public void trigger(Stage stage) {
				if (Common.time() > startTime + 1500) {
					Robot.getIntake().setIntakePower(0.0);
					complete = true;
				}
				Robot.getIntake().loadCube(1.0);
			}
		};
	}
}
