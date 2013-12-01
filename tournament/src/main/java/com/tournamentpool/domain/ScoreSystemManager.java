/* 
Copyright (C) 2003-2008 Avery J. Regier.

This file is part of the Tournament Pool and Bracket Tracker.

Tournament Pool and Bracket Tracker is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

Tournament Pool and Bracket Tracker is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>. */

/*
 * Created on Mar 13, 2004
 */
package com.tournamentpool.domain;

import java.util.HashMap;
import java.util.Map;

import utility.menu.Menu;
import utility.menu.reference.ReferenceMenu;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.application.SingletonProviderHolder;

/**
 * @author Avery J. Regier
 */
public class ScoreSystemManager extends SingletonProviderHolder {
	private Map<Integer, Level> levels = new HashMap<Integer, Level>();
	private Map<Integer, ScoreSystem> scoreSystems = new HashMap<Integer, ScoreSystem>();

	/**
	 * @param sp
	 */
	public ScoreSystemManager(SingletonProvider sp) {
		super(sp);
	}

	/**
	 * @param levelOID
	 * @param name
	 */
	public void loadLevel(int levelOID, String name, int roundNo) {
		levels.put(new Integer(levelOID), new Level(levelOID, name, roundNo));
	}

	/**
	 * @param levelOID
	 * @return
	 */
	public Level getLevel(int levelOID) {
		return levels.get(new Integer(levelOID));
	}

	public int getNumLevels() {
		return levels.size();
	}

	/**
	 * @param scoreSystemOID
	 * @param name
	 */
	public void loadScoreSystem(int scoreSystemOID, String name) {
		scoreSystems.put(new Integer(scoreSystemOID), new ScoreSystem(scoreSystemOID, name));
		
	}
	
	public int getNumScoreSystems() {
		return scoreSystems.size();
	}

	/**
	 * @param scoreSystemOID
	 * @param levelOID
	 * @param points
	 * @param multiplier
	 */
	public void loadScoreSystemDetail(int scoreSystemOID, int levelOID, 
										int points, int multiplier) {
		ScoreSystem scoreSystem = getScoreSystem(scoreSystemOID);
		Level level = getLevel(levelOID);
		scoreSystem.loadDetail(level, points, multiplier);
	}

	public ScoreSystem getScoreSystem(int scoreSystemOID) {
		return (ScoreSystem)scoreSystems.get(new Integer(scoreSystemOID));
	}

	/**
	 * @return
	 */
	public Menu getScoreSystemMenu() {
		return new ReferenceMenu<ScoreSystem>("scoreSystems") {
			protected Map<Integer, ScoreSystem> getReferences() {
				return scoreSystems;
			}
		};
	}
}
