package org.usfirst.frc.team4564.robot.path;

import org.usfirst.frc.team4564.robot.DriveTrain;
import org.usfirst.frc.team4564.robot.Heading;

/**
 * A class representing a stage of a Path where the robot turns by tank drive until a specified angle is reached.
 * Created January 2018
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Evan McCoy
 */
public class PowerTurn extends Stage {
	private double target;
	private double power;
	private double startingAngle;
	private boolean backward;
	
	public PowerTurn(double target, double power, boolean backward) {
		super(true);
		this.target = target;
		this.power = power;
		this.backward = backward;
	}
	
	public void start() {
		Heading heading = DriveTrain.instance().getHeading();
		heading.setHeadingHold(false);
		startingAngle = heading.getAngle();
	}
	
	public boolean isComplete() {
		if (!super.eventsFinished()) {
			return false;
		}
		return (startingAngle < target) ? 
				DriveTrain.instance().getHeading().getAngle() >= target : DriveTrain.instance().getHeading().getAngle() <= target;
	}
	
	public double[] getDrive() {
		if (isComplete()) {
			return new double[] {power, power};
		}
		if (target < DriveTrain.instance().getHeading().getAngle()) {
			if (backward) {
				return new double[] {-power, 0};
			}
			else {
				return new double[] {0, power};
			}
		}
		if (backward) {
			return new double[] {0, -power};
		}
		else {
			return new double[] {power, 0};
		}
	}
	
	/**
	 * A class representing a stage of a Path where the robot derives  non-zero power from PowerTurn and one power from the previous stage.
	 * This hybrid is meant to be used with a previous stage of PIDDrive, as to keep the pivoting wheel locked at the appropriate distance 
	 * from the last stage.
	 * 
	 * @author Brewer FIRST Robotics Team 4564
	 * @author Evan McCoy
	 */
	public static class PowerTurnOverlay extends PowerTurn {
		private Stage previousStage;
		
		public PowerTurnOverlay(double target, double power, boolean backward, Stage previousStage) {
			super(target, power, backward);
			this.previousStage = previousStage;
		}
		
		@Override
		public double[] getDrive() {
			double[] power = super.getDrive();
			if (power[0] == 0) {
				double[] previousPower = (previousStage instanceof PIDDrive) ? 
						((PIDDrive)previousStage).getDrive(DriveTrain.instance().getLeftDist()) : previousStage.getDrive();
				power[0] = previousPower[0];
			}
			if (power[1] == 0) {
				double[] previousPower = (previousStage instanceof PIDDrive) ? 
						((PIDDrive)previousStage).getDrive(DriveTrain.instance().getRightDist()) : previousStage.getDrive();
				power[1] = previousPower[1];
			}
			return power;
		}
	}
}
