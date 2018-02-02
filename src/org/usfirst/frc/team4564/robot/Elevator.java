package org.usfirst.frc.team4564.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.DigitalInput;
/**A class to control the elevator
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Brent Roberts
 */
public class Elevator {
	Intake intake = new Intake();
	TalonSRX elevatorLeft = new TalonSRX(Constants.ELEVATOR_LEFT);
	TalonSRX elevatorRight = new TalonSRX(Constants.ELEVATOR_RIGHT);
	//assuming true = pressed
	private DigitalInput lowerLimit = new DigitalInput(Constants.LOWER_LIMIT);
	//assuming true = pressed
	private DigitalInput upperLimit = new DigitalInput(Constants.UPPER_LIMIT);
	//in inches
	double offset = 0;
	//random value for now
	final double COUNTS_PER_INCH = 0.1;
	//Elevator height in inches(random value for now)
	final int ELEVATOR_HEIGHT = 72;
	//Reduced speed zone at upper and lower limits in inches.
	final int DANGER_ZONE = 12;
	double speed = 0.0;
	double targetHeight = 0.0;
	double error = 0;
	//How close to the targetHeight that elevator can be to complete
	final double ACCEPTABLE_ERROR = 1.5;
	
	public enum States {
		STOPPED, //The state that elevator starts, does nothing unless the home function is run.
		HOMING,  //Brings elevator slowly to the bottom most position possible, sets the offset. Must be done before any use of elevator.
		IDLE, //State of the elevator doing nothing, both types of elevator usage can be used from this state.
		MOVING, //State of elevator moving to a target position within the ACCEPTABLE_ERROR.
		JOYSTICK; //Moves the elevator by a desired power returns to IDLE after setting the power once.
	}
	States state = States.STOPPED;
	
	/**Sets up elevator encoder
	 * 
	 */
	public void init() {
		elevatorRight.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 10);
	}
	
	/**A safe function to set the power of the elevator
	 * 
	 * @param power -power to run the elevator at, + = up and - = down
	 */
	public void setPower(double power) {
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
		Common.debug("Elevator power:"+power);
	}
	
	/**Gets if the elevator is at the top
	 * 
	 * @return -true = at top
	 */
	public boolean atTop() {
		//greater or equal to total height
		if (upperLimit.get()) {
			return true;
		} else {
			return false;
		}
	}
	/**Gets if the elevator is at the top
	 * 
	 * @return -true = at bottom
	 */
	public boolean atBottom() {
		if (lowerLimit.get()) {
			return true;
		} else {
			return false;
		}
	}
	/**Starts homing the elevator
	 * 
	 */
	public void home() {
		state = States.HOMING;
	}
	/**Gets the state of the elevator
	 * 
	 * @return -The current state of the elevator
	 */
	public States getState() {
		return state;
	}
	/**Gets the raw encoder counts + the offset in counts 
	 * 
	 * @return -The current height of the elevator in counts
	 */
	public double getEncoder() {
		return (offset*COUNTS_PER_INCH) + elevatorRight.getSensorCollection().getPulseWidthPosition();
	}
	/**Gets the current height of the elevator in inches
	 * 
	 * @return -The current height of the elevator in counts 
	 */
	public double getInches() {
		return getEncoder()/COUNTS_PER_INCH;
	}
	/**Function designed for joystick control of elevator
	 * Only works for one cycle
	 * @param speed -The speed for -1.0(down) to 1.0(up) to move the elevator at
	 */
	public void joystickControl(double speed) {
		//overrules moveToHeight()
		if (state != States.STOPPED && state != States.HOMING){
			this.speed = speed;
			state = States.JOYSTICK;
		}
	}
	/**Starts moving the elevator to a target height
	 * 
	 * @param targetHeight -Height in inches that the elevator to move to
	 */
	public void moveToHeight(double targetHeight) {
		if (state != States.STOPPED && state != States.HOMING && state != States.JOYSTICK) {
			this.targetHeight = targetHeight + offset;
			state = States.MOVING;
		}
	}
	/**Update function that should be the last run of all elevator functions.
	 * Exports certain values to smart dashboard and runs the state process of the elevator
	 * 
	 */
	public void update() {
		Common.dashNum("Elevator encoder", getEncoder());
		Common.dashNum("offset", offset);
		Common.dashNum("Elevator encoder in inches", getInches());
		Common.dashBool("at top", atTop());
		Common.dashBool("at bottom", atBottom());
		Common.dashStr("Elevator State", state.toString());
		switch(state) {
		case STOPPED:
			setPower(0.0);
			break;
		case HOMING:
			if (lowerLimit.get()) {
				offset = getInches();
				setPower(0.0);
				state = States.IDLE;
			} else {
				setPower(-0.4);
			}
			break;
		case IDLE:
			setPower(0.0);
			break;
		case MOVING:
			error = targetHeight - getInches();
			if (Math.abs(error) < ACCEPTABLE_ERROR) {
				setPower(0.0);
				state = States.IDLE;
			} else {
				if (error > 0) {
					setPower(Common.map(error, offset, ELEVATOR_HEIGHT+offset, 0.4, 1.0));
				} if (error < 0) {
					setPower(-Common.map(error, offset, ELEVATOR_HEIGHT+offset, 0.4, 1.0));
				}
			}
			break;
		case JOYSTICK:
			setPower(speed);
			state = States.IDLE;
			break;
		}
	}
	
}