package org.usfirst.frc.team4564.robot;

import java.util.ArrayList;
import java.util.List;

/**
 * A class representing a collection of drive stages that forms a continuous drive path.
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Evan McCoy
 *
 */
public class Path {
	private List<Stage> stages = new ArrayList<Stage>();
	private int state = 0;
	
	public void start() {
		stages.get(0).start();
	}
	
	/**
	 * Reset the path collection to its first stage.
	 */
	public void resetState() {
		state = 0;
	}
	
	/**
	 * Get the left and right motor powers of the current drive state. Updates to next stage if current is complete.
	 * 
	 * @return double[] - The left/right motor powers, or [0, 0] if the path is complete.
	 */
	public double[] getDrive() {
		Common.dashNum("stage", state);
		if (stages.get(state).isComplete()) {
			state++;
			stages.get(state).start();
		}
		if (state >= stages.size()) {
			return new double[] {0, 0};
		}
		return stages.get(state).getDrive();
	}
	
	/**
	 * Whether or not the path has completed all stages.
	 * 
	 * @return boolean - complete
	 */
	public boolean isComplete() {
		return state >= stages.size();
	}
	
	/**
	 * Adds another straight drive stage with the given parameters.
	 * 
	 * @param distance - the distance to drive straight before advancing to next stage.
	 * @param heading - the heading to drive on
	 * @param power - the power to apply to the drive train
	 * @return {@link Path Path} - the current Path instance
	 */
	public Path addDriveStraight(double distance, double heading, double power) {
		stages.add(new DriveStraight(distance, heading, power));
		return this;
	}
	
	/**
	 * Adds another PID controlled drive stage with the given parameters.
	 * 
	 * @param distance - the distance to drive
	 * @param minPower - the minimum power for the PID to output
	 * @param maxPower - the maximum power for the PID to output
	 * @param p - the P scalar
	 * @param i - the I scalar
	 * @param d - the D scalar
	 * @param name - the name of the PID for SmartDashboard tuning.
	 * @return {@link Path Path} - the current Path instance
	 */
	public Path addPIDDrive(double distance, double minPower, double maxPower, double p, double i, double d, boolean inverted, String name) {
		stages.add(new PIDDrive(distance, minPower, maxPower, p, i, d, inverted, name));
		return this;
	}
	
	/**
	 * Adds another power turn stage with the given parameters.
	 * 
	 * @param targetAngle - the angle to stop turning once reached
	 * @param power - the power to apply to the turning wheel
	 * @return {@link Path Path} - the current Path instance
	 */
	public Path addPowerTurn(double targetAngle, double power) {
		stages.add(new PowerTurn(targetAngle, power));
		return this;
	}
	
	/**
	 * Adds another power turn stage with the given parameters.  Only the non-zero turn side is derived from this stage.
	 * The normally zero power is derived from the previous stage.
	 * 
	 * @param targetAngle - the angle to stop turning once reached
	 * @param power - the power to apply to the turning wheel
	 * @return {@link Path Path} - the current Path instance
	 */
	public Path addPowerTurnOverlay(double targetAngle, double power) {
		stages.add(new PowerTurnOverlay(targetAngle, power, stages.get(stages.size() - 1)));
		return this;
	}
	
	/**
	 * An interface representing a generic stage of driving in a Path.
	 * Created January 2018
	 * 
	 * @author Brewer FIRST Robotics Team 4564
	 * @author Evan McCoy
	 */
	private interface Stage {
		/**
		 * Complete the initializing steps of the stage.
		 */
		public void start();
		
		/**
		 * Whether or not this stage has completed its target.
		 * 
		 * @return boolean - complete
		 */
		public boolean isComplete();
		
		/**
		 * An array of left/right motor powers for the current stage.
		 * 
		 * @return double[] - left/right motor powers
		 */
		public double[] getDrive();
	}
	
	/**
	 * A class representing a stage of a Path where the robot drives straight on a heading for a specified distance.
	 * Created January 2018
	 * 
	 * @author Brewer FIRST Robotics Team 4564
	 * @author Evan McCoy
	 */
	private class DriveStraight implements Stage {
		private double target;
		private double heading;
		private double power;
		
		public DriveStraight(double distance, double heading, double power) {
			this.target = distance;
			this.heading = heading;
			this.power = power;
		}
		
		public void start() {
			DriveTrain.instance().resetEncoders();
		}
		
		public boolean isComplete() {
			return DriveTrain.instance().getAverageDist() >= target;
		}
		
		public double[] getDrive() {
			double error = heading - DriveTrain.instance().getHeading().getAngle();
			double offset = error*0.025;
			if (error > 0) {
				return new double[] {power + offset, power};
			} else {
				return new double[] {power, power - offset};
			}
		}
	}
	
	/**
	 * A class representing a stage of a Path where the robot drives a specified distance under PID motor control.
	 * 
	 * @author Brewer FIRST Robotics Team 4564
	 * @author Evan McCoy
	 */
	private class PIDDrive implements Stage {
		private PID pid;
		
		public PIDDrive(double distance, double minPower, double maxPower, double p, double i, double d, boolean inverted, String name) {
			this.pid = new PID(p, i, d, inverted, false, name);
			pid.setTarget(distance);
			pid.setOutputLimits(-maxPower, maxPower);
			pid.setMin(minPower);
		}
		
		public void start() {
			DriveTrain.instance().resetEncoders();
		}
		
		public boolean isComplete() {
			return DriveTrain.instance().getAverageDist() >= pid.getTarget();
		}
		
		public double[] getDrive() {
			return getDrive(DriveTrain.instance().getAverageDist());
		}
		
		public double[] getDrive(double curDistance) {
			double power = pid.calc(curDistance);
			return new double[] {power, power};
		}
	}
	
	/**
	 * A class representing a stage of a Path where the robot turns by tank drive until a specified angle is reached.
	 * Created January 2018
	 * 
	 * @author Brewer FIRST Robotics Team 4564
	 * @author Evan McCoy
	 */
	private class PowerTurn implements Stage {
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
	}
	
	/**
	 * A class representing a stage of a Path where the robot derives  non-zero power from PowerTurn and one power from the previous stage.
	 * This hybrid is meant to be used with a previous stage of PIDDrive, as to keep the pivoting wheel locked at the appropriate distance 
	 * from the last stage.
	 * 
	 * @author Brewer FIRST Robotics Team 4564
	 * @author Evan McCoy
	 */
	private class PowerTurnOverlay extends PowerTurn {
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
