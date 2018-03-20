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
}
