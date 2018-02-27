package org.usfirst.frc.team4564.robot;

import org.usfirst.frc.team4564.robot.path.Path;
import org.usfirst.frc.team4564.robot.path.Paths;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * A class for making decisions about Auto paths in the 2018 FRC Power Up game.
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Evan McCoy
 */
public class Auto {
	private Path path;
	private String gameData;
	public char position;
	public Mode mode = Mode.CROSS_LINE;
	//private boolean twoCube;
	
	/**
	 * Runs the startup sequence for the path of the current autonomous selections.
	 */
	public void start() {
		path.reset();
		path.start();
	}
	
	/**
	 * Drives the path of the current autonomous selections.
	 */
	public void drive() {
		path.drive();
	}
	
	/**
	 * Posts the 3-character game data string to autonomous.
	 * 
	 * @param gameData the 3-character game data string
	 */
	public void setGameData(String gameData) {
		this.gameData = gameData.substring(0, 3);
	}
	
	/**
	 * Posts the position to autonomous.
	 * 
	 * @param position the position in the form 'L', 'R', or 'C' for left, right, or center respectively.
	 */
	public void setPosition(char position) {
		this.position = position;
		SmartDashboard.putString("Position", "" + position);
	}
	
	public void setMode(Mode mode) {
		this.mode = mode;
		SmartDashboard.putString("Mode", mode.toString());
	}
	
	/**
	 * Returns the desired path for the current set of autonomous selections.
	 * 
	 * @return the path
	 */
	public Path getPath() {
		if (position == 'C') {
			if (gameData.charAt(0) == 'L') {
				Common.debug("Auto#getPath() - Center Switch Left");
				return Paths.CENTER_SWITCH_LEFT;
			}
			else {
				Common.debug("Auto#getPath() - Center Switch Right");
				return Paths.CENTER_SWITCH_RIGHT;
			}
		}
		switch (mode) {
			case CROSS_LINE:
				Common.debug("Auto#getPath() - Cross Line");
				return Paths.CROSS_LINE;
			case SCALE:
				//If we own the near scale
				if (gameData.charAt(1) == position) {
					//If we own the near switch
					if (gameData.charAt(0) == position) {
						if (position == 'L') {
							Common.debug("Auto#getPath() - Near Scale Left w/Switch 2-Cube");
							return Paths.TWO_CUBE_LEFT_SWITCH;
						}
						Common.debug("Auto#getPath() - Near Scale Right w/Switch 2-Cube");
						return Paths.TWO_CUBE_RIGHT_SWITCH;
					}
					//If we don't own the near switch
					else {
						if (position == 'L') {
							Common.debug("Auto#getPath() - Near Scale Left w/Scale 2-Cube");
							return Paths.TWO_CUBE_LEFT_SCALE;
						}
						Common.debug("Auto#getPath() - Near Scale Right w/2nd Pickup and Stop");
						return Paths.TWO_CUBE_RIGHT_STOP;
					}
				}
				//Otherwise, do far scale
				if (position == 'L') {
					Common.debug("Auto#getPath() - Far Scale Left");
					return Paths.FAR_SCALE_LEFT;
				}
				else {
					Common.debug("Auto#getPath() - Far Scale Right");
					return Paths.FAR_SCALE_RIGHT;
				}
			case CLOSE:
				//If near switch is ours
				if (gameData.charAt(0) == position) {
					if (position == 'L') {
						Common.debug("Auto#getPath() - Near Switch Left");
						return Paths.NEAR_SWITCH_LEFT;
					}
					else {
						Common.debug("Auto#getPath() - Near Switch Right");
						return Paths.NEAR_SWITCH_RIGHT;
					}
				}
				//If near scale is ours
				else if (gameData.charAt(1) == position) {
					//If we own the near switch
					if (gameData.charAt(0) == position) {
						if (position == 'L') {
							Common.debug("Auto#getPath() - Near Scale Left w/Switch 2-Cube");
							return Paths.TWO_CUBE_LEFT_SWITCH;
						}
						Common.debug("Auto#getPath() - Near Scale Right w/Switch 2-Cube");
						return Paths.TWO_CUBE_RIGHT_SWITCH;
					}
					//If we don't own the near switch
					else {
						if (position == 'L') {
							Common.debug("Auto#getPath() - Near Scale Left w/Scale 2-Cube");
							return Paths.TWO_CUBE_LEFT_SCALE;
						}
						Common.debug("Auto#getPath() - Near Scale Right w/2nd Pickup and Stop");
						return Paths.TWO_CUBE_RIGHT_STOP;
					}
				}
				//Otherwise, do far scale
				if (position == 'L') {
					Common.debug("Auto#getPath() - Far Scale Left");
					return Paths.FAR_SCALE_LEFT;
				}
				else {
					Common.debug("Auto#getPath() - Far Scale Right");
					return Paths.FAR_SCALE_RIGHT;
				}
		}
		Common.debug("Auto#getPath() - Cross Line");
		return Paths.CROSS_LINE;
	}
	
	/**
	 * Available auto modes, indexable by string names.
	 * 
	 * @author Brewer FIRST Robotics Team 4564
	 * @author Evan McCoy
	 */
	public enum Mode {
		SCALE("scale"),
		CLOSE("close"),
		CROSS_LINE("cross_line");
		
		public String name;
		private Mode(String name) {
			this.name = name;
		}
		
		/**
		 * Gets a mode by its string name.
		 * 
		 * @param name - the name
		 * @return the mode associated with the name. CROSS_LINE if no matching name.
		 */
		public static Mode getMode(String name) {
			for (Mode mode : Mode.values()) {
				if (mode.name.equalsIgnoreCase(name)) {
					return mode;
				}
			}
			return Mode.CROSS_LINE;
		}
	}
}
