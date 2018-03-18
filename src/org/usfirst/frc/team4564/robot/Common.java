package org.usfirst.frc.team4564.robot;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Some common functions to make our lives easier.
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Evan McCoy
 * @author Jacob Cote
 * @author Brent Roberts
 */
public class Common {
	private static final DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");

	public static void debug(String text) {
		System.out.println(formatter.format(new Date(time())) + text);
	}
	
	public static void dashStr(String title, String a) {
		SmartDashboard.putString(title + ": ", a);
	}
	
	public static void dashNum(String title, int a) {
		SmartDashboard.putNumber(title + ": ", a);
	}
	
	public static void dashNum(String title, double a) {
		SmartDashboard.putNumber(title + ": ", a);
	}
	
	public static void dashBool(String Title, boolean a) {
		SmartDashboard.putBoolean(Title, a);
	}
	
	public static long time() {
		return Calendar.getInstance().getTimeInMillis();
	}
	/**Takes a value and a range it falls within and converts it to a different range.
	 * Defaults to minimum input if it exceeds the min or max input.
	 * 
	 * @param input -Value to be converted to a different range
	 * @param minInput -Minimum value for the range of the input
	 * @param maxInput -Maximum value for the range of the input
	 * @param minOutput -Minimum value for the range of the output
	 * @param maxOutput -Maximum value for the range of the output
	 * @return double - A value in the output range that is proportional to the input
	 */
	public static double map(double input, double minInput, double maxInput, double minOutput, double maxOutput) {
		input = Math.min(Math.max(input, minInput), maxInput);
		double inputRange = maxInput - minInput;
		double inputPercentage = (input-minInput)/inputRange;
		double outputRange = maxOutput - minOutput;
		double output = (outputRange * inputPercentage) + minOutput;
		return output;
	}
}