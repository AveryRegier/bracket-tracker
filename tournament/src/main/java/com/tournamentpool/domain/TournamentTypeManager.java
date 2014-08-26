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

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.application.SingletonProviderHolder;
import utility.menu.Menu;
import utility.menu.reference.ReferenceMenu;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Avery J. Regier
 */
public class TournamentTypeManager extends SingletonProviderHolder {
	private final Map<Integer, MainGameNode> gameNodes = new HashMap<>();
	private final Map<Integer, TournamentType> tournamentTypes = new HashMap<>();
	private final Map<Integer, GameFeederType> gameFeederTypes = new HashMap<>();
	
	/**
	 * @param sp
	 */
	public TournamentTypeManager(SingletonProvider sp) {
		super(sp);
	}

	/**
	 * @param gameNodeOID
	 * @param levelOID
	 * @param gameFeederTypeOID 
	 */
	public void loadGameNode(int gameNodeOID, int levelOID, int gameFeederTypeOID) {
		gameNodes.put(gameNodeOID,
			new MainGameNode(gameNodeOID, 
				sp.getSingleton().getScoreSystemManager().getLevel(levelOID),
				getGameFeederType(gameFeederTypeOID)));
	}

	private GameFeederType getGameFeederType(int gameFeederTypeOID) {
		return gameFeederTypes.get(gameFeederTypeOID);
	}
	
	public void loadGameFeederType(int oid, String name) {
		GameFeederType gft = new GameFeederType(oid, name);
		gameFeederTypes.put(gft.getID(), gft);
	}

	public int getNumGameNodes() {
		return gameNodes.size();
	}

	/**
	 * @param tournamentTypeOID
	 * @param name
	 * @param gameNodeOID
	 */
	public void loadTournamentType(int tournamentTypeOID, String name, int gameNodeOID) {
		tournamentTypes.put(tournamentTypeOID,
			new MainTournamentType(this, tournamentTypeOID, name, getGameNode(gameNodeOID)) 
		);
	}
	
	public int getNumTournamentTypes() {
		return tournamentTypes.size();
	}

	/**
	 * @param gameNodeOID
	 */
	GameNode getGameNode(int gameNodeOID) {
		return gameNodes.get(gameNodeOID);
	}
	
	private MainGameNode getMainGameNode(int gameNodeOID) {
		return gameNodes.get(gameNodeOID);
	}

	/**
	 * @param oponentOID
	 * @param tournamentTypeOID
	 * @param name
	 * @param sequence
	 */
	public void loadOponent(int oponentOID, int tournamentTypeOID, 
							String name, int sequence) {
		getTournamentType(tournamentTypeOID).loadOponent(oponentOID, name, sequence);
	}

	/**
	 * @param tournamentTypeOID
	 */
	public MainTournamentType getTournamentType(int tournamentTypeOID) {
		return (MainTournamentType)tournamentTypes.get(tournamentTypeOID);
	}

	public Menu getTournamentTypeMenu() {
		return new ReferenceMenu<TournamentType>("tournamentTypes") {
			protected Map<Integer, TournamentType> getReferences() {
				return tournamentTypes;
			}
		};
	}
	
	/**
	 * @param seedOID
	 * @param tournamentTypeOID
	 * @param seedNo
	 */
	public void loadSeed(int seedOID, int tournamentTypeOID, int seedNo) {
		getTournamentType(tournamentTypeOID).loadSeed(seedOID, seedNo);
	}

	/**
	 * @param gameNodeOID
	 * @param seedOID
	 * @param oponentOID
	 * @param tournamentTypeOID
	 */
	public void loadGameSeed(int gameNodeOID, int seedOID, int oponentOID, int tournamentTypeOID) {
		TournamentType tt = getTournamentType(tournamentTypeOID);
		Seed seed = tt.getSeed(seedOID);
		Opponent oponent = tt.getOpponent(oponentOID);
		getMainGameNode(gameNodeOID).loadFeeder(oponent, seed);
	}

	/**
	 * @param gameNodeOID
	 * @param feederGameNodeOID
	 * @param oponentOID
	 * @param tournamentTypeOID
	 */
	public void loadGameFeeder(int gameNodeOID, int feederGameNodeOID, int oponentOID, int tournamentTypeOID) {
		TournamentType tt = getTournamentType(tournamentTypeOID);
		GameNode gameNode = getGameNode(feederGameNodeOID);
		Opponent oponent = tt.getOpponent(oponentOID);
		getMainGameNode(gameNodeOID).loadFeeder(oponent, gameNode);
	}
}
