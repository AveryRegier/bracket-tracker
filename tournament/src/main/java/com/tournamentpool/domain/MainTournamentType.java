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

package com.tournamentpool.domain;

import java.util.*;

public class MainTournamentType extends TournamentTypeAdapter implements TournamentType {

	private final int tournamentTypeOID;
	private final String name;
	private final GameNode lastGame;
	private final Map<Integer, Opponent> oponents = new TreeMap<>();
	private final Map<Integer, Seed> seeds = new HashMap<>();
	private final TournamentTypeManager ttm;

	/**
	 *
	 */
	public MainTournamentType(TournamentTypeManager ttm, int tournamentTypeOID, String name, GameNode lastGame) {
		this.ttm = ttm;
		this.tournamentTypeOID = tournamentTypeOID;
		this.name = name;
		this.lastGame = lastGame;
	}

	/**
	 * @param oponentOID
	 * @param name
	 * @param sequence
	 */
	public void loadOponent(int oponentOID, String name, int sequence) {
		oponents.put(oponentOID, new Opponent(oponentOID, name, sequence));
	}

	/**
	 * @param seedOID
	 * @param seedNo
	 */
	public void loadSeed(int seedOID, int seedNo) {
		seeds.put(seedOID, new Seed(seedOID, seedNo));
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.ITournamentType#getSeed(int)
	 */
	public Seed getSeed(int seedOID) {
		return seeds.get(seedOID);
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.ITournamentType#getOpponent(int)
	 */
	public Opponent getOpponent(int oponentOID) {
		return oponents.get(oponentOID);
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.ITournamentType#getOpponentByOrder(int)
	 */
	public Optional<Opponent> getOpponentByOrder(int sequence) {
		for (Opponent opponent: oponents.values()) {
			if(opponent.getSequence() == sequence) return Optional.of(opponent);
		}
		return Optional.empty();
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.ITournamentType#getChampionshipGameNode()
	 */
	public GameNode getChampionshipGameNode() {
		return lastGame;
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.ITournamentType#getNumOponents()
	 */
	public int getNumOponents() {
		return oponents.size();
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.ITournamentType#getOpponents()
	 */
	public Set<Map.Entry<Integer, Opponent>> getOpponents() {
		return oponents.entrySet();
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.ITournamentType#getID()
	 */
	public Object getID() {
		return tournamentTypeOID;
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.ITournamentType#getName()
	 */
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.ITournamentType#getOID()
	 */
	public int getOID() {
		return tournamentTypeOID;
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.ITournamentType#getNumSeeds()
	 */
	public int getNumSeeds() {
		return seeds.size();
	}

	public Optional<GameNode> getGameNode(int gameNodeOID) {
		return Optional.ofNullable(ttm.getGameNode(gameNodeOID));
	}
}
