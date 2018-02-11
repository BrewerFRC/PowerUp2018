package org.usfirst.frc.team4564.robot;

import org.usfirst.frc.team4564.robot.path.Path;
import org.usfirst.frc.team4564.robot.path.Paths;

/**
 * A class for making decisions about Auto paths in the 2018 FRC Power Up game.
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Evan McCoy
 */
public class Auto {
	private Path path;
	private String gameData;
	private char position;
	private Mode mode;
	private boolean twoCube;
	private boolean tryAlternative;
	
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
	}
	
	/**
	 * Reads the autonomous selections from NetworkTables.
	 */
	public void readAutonomousSelections() {
		path = getPath();
	}
	
	/**
	 * Returns the desired path for the current set of autonomous selections.
	 * 
	 * @return the path
	 */
	public Path getPath() {
		switch (mode) {
			case CROSS_LINE:
				return Paths.CROSS_LINE;
			case SCALE_ALWAYS:
				if (gameData.charAt(1) == position) {
					if (position == 'L') {
						return Paths.NEAR_SCALE_LEFT;
					}
					else {
						return Paths.NEAR_SCALE_RIGHT;
					}
				}
				if (position == 'L') {
					return Paths.FAR_SCALE_LEFT;
				}
				else {
					return Paths.FAR_SCALE_RIGHT;
				}
			case SCALE_CLOSE:
				if (gameData.charAt(1) == position) {
					if (position == 'L') {
						return Paths.NEAR_SCALE_LEFT;
					}
					else {
						return Paths.NEAR_SCALE_RIGHT;
					}
				}
				if (tryAlternative) {
					if (gameData.charAt(0) == position) {
						if (position == 'L') {
							return Paths.NEAR_SWITCH_LEFT;
						}
						else {
							return Paths.NEAR_SWITCH_RIGHT;
						}
					}
				}
				break;
			case SWITCH_ALWAYS:
				if (gameData.charAt(0) == position) {
					if (position == 'L') {
						return Paths.NEAR_SWITCH_LEFT;
					}
					else {
						return Paths.NEAR_SWITCH_RIGHT;
					}
				}
				if (position == 'L') {
					return Paths.FAR_SWITCH_LEFT;
				}
				else {
					return Paths.FAR_SWITCH_RIGHT;
				}
			case SWITCH_CLOSE:
				if (gameData.charAt(0) == position) {
					if (position == 'L') {
						return Paths.NEAR_SWITCH_LEFT;
					}
					else {
						return Paths.NEAR_SWITCH_RIGHT;
					}
				}
				if (tryAlternative) {
					if (gameData.charAt(1) == position) {
						if (position == 'L') {
							return Paths.NEAR_SCALE_LEFT;
						}
						else {
							return Paths.NEAR_SCALE_RIGHT;
						}
					}
				}
				break;
		}
		return Paths.CROSS_LINE;
	}
	
	/**
	 * Available auto modes, indexable by string names.
	 * 
	 * @author Brewer FIRST Robotics Team 4564
	 * @author Evan McCoy
	 */
	private enum Mode {
		SCALE_ALWAYS("scale_always"), 
		SCALE_CLOSE("scale_close"),
		SWITCH_ALWAYS("switch_always"),
		SWITCH_CLOSE("switch_close"),
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
		@SuppressWarnings("unused")
		public Mode getMode(String name) {
			for (Mode mode : Mode.values()) {
				if (mode.name.equalsIgnoreCase(name)) {
					return mode;
				}
			}
			return Mode.CROSS_LINE;
		}
	}
}
