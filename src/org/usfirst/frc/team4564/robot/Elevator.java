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
	Intake intake;
	TalonSRX elevatorLeft = new TalonSRX(Constants.ELEVATOR_LEFT);
	TalonSRX elevatorRight = new TalonSRX(Constants.ELEVATOR_RIGHT);
	//true = pressed
	private DigitalInput lowerLimit = new DigitalInput(Constants.LOWER_LIMIT);
	//false = pressed
	private DigitalInput upperLimit = new DigitalInput(Constants.UPPER_LIMIT);
	//in counts
	double offset = 0;
	//random value for now
	final double COUNTS_PER_INCH = 453.42029;
	//Elevator height in inches(random value for now)
	final double ELEVATOR_HEIGHT = 65.75;
	//Reduced speed zone at upper and lower limits in inches.
	final int DANGER_ZONE = 12;
	double speed = 0.0;
	double targetHeight = 0.0;
	double error = 0;
	//How close to the targetHeight that elevator can be to complete
	final double ACCEPTABLE_ERROR = 1.5;
	//The location of the upper limit switch in inches
	final double UPPER_LIMIT_POINT = 57.7;
	//The maximum power that the elevator can be run at
	final double MAX_POWER = 0.4;
	//The minimum power that the elevator can be run at
	final double MIN_POWER = 0.1;
	//The last power that was set
	double lastPower = 0;
	//The maximum power change
	final double MAX_DELTA_POWER = 0.1;
	//In inches per second
	final double MAX_VELOCITY = 12;
	PositionByVelocityPID pid = new PositionByVelocityPID(0, ELEVATOR_HEIGHT, -MAX_VELOCITY, MAX_VELOCITY, 0, "Elevator PID");
	double velP = 0.001, velI = 0.0, velD = 0.0;
	double posP = 0.1, posI = 0.0, posD = 0.0;
	
	public enum States {
		STOPPED, //The state that elevator starts, does nothing unless the home function is run.
		HOMING,  //Brings elevator slowly to the bottom most position possible, sets the offset. Must be done before any use of elevator.
		IDLE, //State of the elevator doing nothing, both types of elevator usage can be used from this state.
		MOVING, //State of elevator moving to a target position within the ACCEPTABLE_ERROR.
		JOYSTICK; //Moves the elevator by a desired power returns to IDLE after setting the power once.
	}
	States state = States.STOPPED;
	
	public Elevator(Intake intake) {
		this.intake = intake;
		elevatorRight.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 10);
		elevatorLeft.setInverted(true);
		pid.setVelocityScalars(velP, velI, velD);
		pid.setVelocityInverted(true);
		pid.setPositionScalars(posP, posI, posD);
	}
	
	/**A safe function to set the power of the elevator, cannot exceed MAX_POWER
	 * 
	 * @param power -power to run the elevator at, + = up and - = down
	 */
	public void setPower(double power) {
		// Check safeties and stop power if necessary
		if (!intake.elevatorSafe()) {	
			power = 0.0;
		}
		if (power > 0.0) {  //Move up
			if(getInches() >= ELEVATOR_HEIGHT) { //hard limit on expected height
				power = 0.0;
			} else if(!upperLimit.get()) {  //Have we made it to the upper limit trigger point(limit switch false = reached)
				if (getInches() < UPPER_LIMIT_POINT) {  //Make sure encoder has counted enough inches
					power = 0.0;
				}
			} else if(getInches()>= ELEVATOR_HEIGHT-DANGER_ZONE) {
				power = Math.min(power, Common.map(ELEVATOR_HEIGHT-getInches(), 0.0, 12.0, MIN_POWER, MAX_POWER));
			} else {
				power = Math.min(power, MAX_POWER);
			}
		} else {  //Moving Down
			if (lowerLimit.get()) { //lower limit true when pressed
				power = 0.0;
			} else if (getInches() <= DANGER_ZONE) {
				power = Math.max(power, Common.map(DANGER_ZONE-getInches(), 0.0, 12.0, -MIN_POWER, -MAX_POWER));
			} else {
				power = Math.max(power, -MAX_POWER);
			}
		}
		lastPower = power;
		elevatorRight.set(ControlMode.PercentOutput, power);
		elevatorLeft.set(ControlMode.PercentOutput, power);
		Common.dashNum("Elevator power:", power);
	}
	/**Limits elevator acceleration for safety
	 * 
	 * @param targetPower -The goal power of the function
	 */
	public void accelPower(double targetPower) {
		double power = 0; 	
		if (Math.abs(lastPower - targetPower) > MAX_DELTA_POWER) {
			if (lastPower > targetPower) {
				power = lastPower - MAX_DELTA_POWER;
			} else {
				power = lastPower + MAX_DELTA_POWER;
			}
		} else {
			power = targetPower;
		}
		setPower(power);
	}
	
	public void pidVelMove(double targetVelocity) {
		pid.setTargetVelocity(targetVelocity);
		double pidVelCalc = pid.calcVelocity(getVelocity());
		Common.dashNum("pidVelCalc", pidVelCalc);
		accelPower(pidVelCalc);
	}
	
	public void pidDisMove(double targetHeight) {
		pid.setTargetPosition(targetHeight);
		accelPower(pid.calc(getInches(), getVelocity()));
	}
	
	/**Gets if the elevator is at the top
	 * 
	 * @return -true = at top
	 */
	public boolean upperLimitSafe() {
		//greater or equal to total height
		if (!upperLimit.get() &&  getInches() < UPPER_LIMIT_POINT) {
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
		Common.debug("New state Homing");
		state = States.HOMING;
	}
	/**Gets the state of the elevator
	 * 
	 * @return -The current state of the elevator
	 */
	public States getState() {
		return state;
	}
	/**Resets the offset of the encoder
	 * 
	 */
	public void resetEncoder() {
		offset = elevatorRight.getSensorCollection().getPulseWidthPosition();
	}
	/**Gets the raw encoder counts + the offset in counts 
	 * 
	 * @return -The current height of the elevator in counts
	 */
	public double getEncoder() {
		return elevatorRight.getSensorCollection().getPulseWidthPosition() - offset;
	}
	/**Gets the current height of the elevator in inches
	 * 
	 * @return -The current height of the elevator in counts 
	 */
	public double getInches() {
		return getEncoder()/COUNTS_PER_INCH;
	}
	/**Completely untested
	 * 
	 * @return
	 */
	public double getVelocity() {
		//should return inches per second
		return (elevatorRight.getSensorCollection().getPulseWidthVelocity()/COUNTS_PER_INCH)*10;
	}
	/**Function designed for joystick control of elevator
	 * Only works for one cycle
	 * @param speed -The speed for -1.0(down) to 1.0(up) to move the elevator at
	 */
	public void joystickControl(double speed) {
		//overrules moveToHeight()
		if (state != States.STOPPED && state != States.HOMING){
			this.speed = speed;
			//Common.debug("New state Joystick");
			state = States.JOYSTICK;
		}
	}
	/**Starts moving the elevator to a target height
	 * 
	 * @param targetHeight -Height in inches that the elevator to move to
	 */
	public void moveToHeight(double targetHeight) {
		if (state != States.STOPPED && state != States.HOMING && state != States.JOYSTICK) {
			this.targetHeight = targetHeight;
			Common.debug("New State Moving");
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
		Common.dashBool("upper limits safe", upperLimitSafe());
		Common.dashBool("upper Limit Triggered", upperLimit.get());
		Common.dashBool("at bottom", atBottom());
		Common.dashStr("Elevator State", state.toString());
		Common.dashNum("Elevator Velocity", getVelocity());
		pid.update();
		switch(state) {
		case STOPPED:
			setPower(0.0);
			break;
		case HOMING:
			if (lowerLimit.get()) {
				resetEncoder();
				setPower(0.0);
				Common.debug("New state Idle");
				state = States.IDLE;
			} else {
				setPower(-0.1);
			}
			break;
		case IDLE:
			setPower(0.0);
			break;
		case MOVING:
			error = targetHeight - getInches();
			if (Math.abs(error) < ACCEPTABLE_ERROR) {
				setPower(0.0);
				Common.debug("New state Idle");
				state = States.IDLE;
			} else {
				double power = Common.map(Math.abs(error), 0, ELEVATOR_HEIGHT, MIN_POWER, MAX_POWER);
				if (error > 0) {
					setPower(power);
				} else {
					setPower(-power);
				}
			}
			break;
		case JOYSTICK:
			pidVelMove(speed);
			//accleMove(speed);
			//Common.debug("new State Idle");
			state = States.IDLE;
			break;
		}
	}
	
}