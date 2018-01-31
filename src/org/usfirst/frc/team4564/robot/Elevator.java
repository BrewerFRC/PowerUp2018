package org.usfirst.frc.team4564.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.DigitalInput;

public class Elevator {
	Intake intake = new Intake();
	TalonSRX elevatorLeft = new TalonSRX(Constants.ELEVATOR_LEFT);
	TalonSRX elevatorRight = new TalonSRX(Constants.ELEVATOR_RIGHT);
	//assuming true = pressed
	private DigitalInput lowerLimit = new DigitalInput(Constants.LOWER_LIMIT);
	//assuming true = pressed
	private DigitalInput upperLimit = new DigitalInput(Constants.UPPER_LIMIT);
	//in inches
	long offset = 0;
	//random value for now
	final double COUNTS_PER_INCH = 0.1;
	//Elevator height in inches(random value for now)
	final int ELEVATOR_HEIGHT = 72;
	//reduced speed zone at upper and lower limits in inches
	final int DANGER_ZONE = 12;

	public void init() {
		elevatorRight.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 10);
		//This method does the same thing elevatorleft.follow(ELEVATOR_RIGHT)
		//elevatorLeft.set(ControlMode.Follower, Constants.ELEVATOR_RIGHT);
	}
	
	public void setElevatorPower(double power) {
		// Check safeties and stop power if necessary
		if (!intake.elevatorSafe()) {	
			power = 0.0;
		} if (power > 0.0 && upperLimit.get()) { // upper limit true when pressed
			power = 0.0;
		} if (power < 0.0 && lowerLimit.get()) { // lower limit true when pressed
			power = 0.0;
		} if (power > 0.0 && this.getInches() >= (ELEVATOR_HEIGHT-DANGER_ZONE) + offset) {
			power = Math.min(power, Common.map(ELEVATOR_HEIGHT-getInches(), 0.0, 12.0, 0.4, 1.0));
		} if (power < 0.0 && this.getInches() <= (DANGER_ZONE-ELEVATOR_HEIGHT) + offset) {
			power = Math.max(power, Common.map(DANGER_ZONE-getInches(), 0.0, 12.0, -0.4, -1.0));
		}
		elevatorRight.set(ControlMode.PercentOutput, power);
		elevatorLeft.set(ControlMode.PercentOutput, power);
	}
	
	
	public boolean atTop() {
		//greater or equal to total height
		if (this.getEncoder() >= offset + ELEVATOR_HEIGHT && upperLimit.get()) {
			return true;
		} else {
			return false;
		}
	}
	
	/*public void resetEncoder() {
		//elevatorRight.getSensorCollection().setQuadraturePosition(0, 10);
		//elevatorRight.getSensorCollection().setPulseWidthPosition(0, 10);
		elevatorRight.setSelectedSensorPosition(0, 0, 10);
		Common.debug("reseting can encoder");
	}*/
	
	public long getEncoder() {
		return offset + elevatorRight.getSensorCollection().getPulseWidthPosition();
	}
	
	public double getInches() {
		return getEncoder()*COUNTS_PER_INCH;
	}
	
	public void update() {
		Common.dashNum("Extra encoder", elevatorRight.getSensorCollection().getPulseWidthPosition());
	}
	
}