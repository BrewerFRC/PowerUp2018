package org.usfirst.frc.team4564.robot;

import org.usfirst.frc.team4564.robot.path.Path;

/**
 * A class for making decisions about Auto paths in the 2018 FRC Power Up game.
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Evan McCoy
 */
public class Auto {
	private Path path;
	private String gameData;
	
	/**
	 * Posts the 3-character game data string to autonomous.
	 * 
	 * @param gameData the 3-character game data string
	 */
	public void setGameData(String gameData) {
		this.gameData = gameData.substring(0, 3);
	}
}
