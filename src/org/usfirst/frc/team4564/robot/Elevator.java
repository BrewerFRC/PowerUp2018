package org.usfirst.frc.team4564.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Elevator {
	TalonSRX elevatorLeft = new TalonSRX(Constants.ELEVATOR_LEFT);
	TalonSRX elevatorRight = new TalonSRX(Constants.ELEVATOR_RIGHT);
	long offset = 0;

	public void init() {
		elevatorRight.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 10);
		//This method does the same thing elevatorleft.follow(ELEVATOR_RIGHT)
		//elevatorLeft.set(ControlMode.Follower, Constants.ELEVATOR_RIGHT);
	}
	
	public void setElevatorPower(double power) {
		elevatorRight.set(ControlMode.PercentOutput, power);
		elevatorLeft.set(ControlMode.PercentOutput, power);
	}
	
	public void home() {
		//no idea about the power
		this.setElevatorPower(-0.4);
	}
	
	public boolean atTop() {
		//greater or equal to total height
		if (this.getEncoder() >= offset + Constants.ELEVATOR_HEIGHT) {
			return true;
		} else {
			return false;
		}
	}
	
	public void resetEncoder() {
		//elevatorRight.getSensorCollection().setQuadraturePosition(0, 10);
		//elevatorRight.getSensorCollection().setPulseWidthPosition(0, 10);
		elevatorRight.setSelectedSensorPosition(0, 0, 10);
		Common.debug("reseting can encoder");
	}
	
	public long getEncoder() {
		return offset + elevatorRight.getSensorCollection().getPulseWidthPosition();
	}
	
	public void update() {
		Common.dashNum("Extra encoder", elevatorRight.getSensorCollection().getPulseWidthPosition());
	}
	
}