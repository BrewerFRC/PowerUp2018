package org.usfirst.frc.team4564.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Elevator {
	TalonSRX elevatorLeft = new TalonSRX(Constants.ELEVATOR_LEFT);
	TalonSRX elevatorRight = new TalonSRX(Constants.ELEVATOR_RIGHT);

	public void init() {
		elevatorRight.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 10);
	}
	
	public void testMotor() {
		elevatorRight.set(ControlMode.PercentOutput, 0.3);
	}
	
	public void update() {
		Common.dashNum("Extra encoder", elevatorRight.getSelectedSensorPosition(0));
	}
	
}



