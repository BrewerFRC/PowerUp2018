package org.usfirst.frc.team4564.robot.path;

public class DrivePower extends Stage {
	double power;
	
	public DrivePower(double power) {
		super(false);
		this.power = power;
	}
	
	@Override
	public void start() {
		
	}

	@Override
	public boolean isComplete() {
		return false;
	}

	@Override
	public double[] getDrive() {
		return new double[] {power, power};
	}

}
