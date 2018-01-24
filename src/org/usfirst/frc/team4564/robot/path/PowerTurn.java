package org.usfirst.frc.team4564.robot.path;

import org.usfirst.frc.team4564.robot.DriveTrain;

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
	
	public PowerTurn(double target, double power) {
		this.target = target;
		this.power = power;
	}
	
	public void start() {
		startingAngle = DriveTrain.instance().getHeading().getAngle();
	}
	
	public boolean isComplete() {
		return (startingAngle < target) ? 
				DriveTrain.instance().getHeading().getAngle() >= target : DriveTrain.instance().getHeading().getAngle() <= target;
	}
	
	public double[] getDrive() {
		if (target < DriveTrain.instance().getHeading().getAngle()) {
			return new double[] {0, power};
		}
		return new double[] {power, 0};
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
		
		public PowerTurnOverlay(double target, double power, Stage previousStage) {
			super(target, power);
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
