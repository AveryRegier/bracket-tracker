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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import utility.domain.Reference;


class SubTournamentType extends TournamentTypeAdapter implements TournamentType {

	private final TournamentType parent;
	private final Level startLevel;
	private final Tournament tournament;

	SubTournamentType(Tournament tournament, Level startLevel) {
		this.parent = tournament.getTournamentType();
		this.tournament = tournament;
		this.startLevel = startLevel;
	}

	public Seed getSeed(int seedOID) {
		return parent.getSeed(seedOID);// this is okay
	}

	public Opponent getOpponent(int oponentOID) {
		return parent.getOpponent(oponentOID);
	}

	public Opponent getOpponentByOrder(int sequence) {
		return parent.getOpponentByOrder(sequence);
	}

	public GameNode getChampionshipGameNode() {
		// Must wrap this game nodes and all feeders until we make the 
		// game nodes at the startLevel look like seeds.
		return wrap(parent.getChampionshipGameNode());
	}

	public int getNumOponents() {
		return parent.getNumOponents();
	}

	public Set<Map.Entry<Integer, Opponent>> getOpponents() {
		return parent.getOpponents();
	}

	public Object getID() {
		return parent.getID();
	}

	public String getName() {
		return parent.getName();
	}

	public int getOID() {
		return parent.getOID();
	}

	public int getNumSeeds() {
		return (int) Math.pow(getNumOponents(), 
			// rely on SubGameNode to get round number right
			getChampionshipGameNode().getLevel().getRoundNo()); 
	}

	public GameNode getGameNode(int gameNodeOID) {
		return wrap(parent.getGameNode(gameNodeOID));
	}
	
	private Map<Reference, Reference> instanceCache = new HashMap<Reference, Reference>();
	synchronized SubGameNode wrap(GameNode parentGameNode) {
		if(parentGameNode == null) return null;
		if(parentGameNode instanceof SubGameNode) return (SubGameNode)parentGameNode;
		SubGameNode subGameNode = (SubGameNode) instanceCache.get(parentGameNode);
		if(subGameNode == null) {
			subGameNode = new SubGameNode(parentGameNode, startLevel, tournament, this);
			instanceCache.put(parentGameNode, subGameNode);
		}
		return subGameNode;
	}

	synchronized Level wrap(GameNode parent, Level startLevel) {
		Level parentLevel = parent.getLevel();
		
		Level level = (Level) instanceCache.get(parentLevel);
		if(level == null) {
			level = new Level(parentLevel.getOid(), parentLevel.getName(), 
					parentLevel.getRoundNo() - startLevel.getRoundNo() + 1);
			instanceCache.put(parentLevel, level);
		}
		return level;
	}
}
