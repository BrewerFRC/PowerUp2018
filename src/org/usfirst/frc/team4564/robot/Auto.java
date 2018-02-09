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
	private Mode mode;
	private boolean twoCube;
	private boolean tryAlternative;
	
	/**
	 * Posts the 3-character game data string to autonomous.
	 * 
	 * @param gameData the 3-character game data string
	 */
	public void setGameData(String gameData) {
		this.gameData = gameData.substring(0, 3);
	}
	
	public Path getPath() {
		switch (mode) {
			case CROSS_LINE:
				return Paths.CROSS_LINE;
			case SCALE_ALWAYS:
				if (gameData.charAt(1) == 'L') {
					return Paths.NEAR_SCALE;
				}
				return Paths.FAR_SCALE;
			case SCALE_CLOSE:
				if (gameData.charAt(1) == 'L') {
					return Paths.NEAR_SCALE;
				}
				if (tryAlternative) {
					return Paths.NEAR_SWITCH;
				}
				return Paths.CROSS_LINE;
			case SWITCH_ALWAYS:
				if (gameData.charAt(0) == 'L') {
					return Paths.NEAR_SWITCH;
				}
				return Paths.FAR_SWITCH;
			case SWITCH_CLOSE:
				if (gameData.charAt(0) == 'L') {
					return Paths.NEAR_SWITCH;
				}
				return Paths.CROSS_LINE;
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
