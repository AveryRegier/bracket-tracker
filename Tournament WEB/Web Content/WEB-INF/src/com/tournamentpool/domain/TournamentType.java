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

import java.util.List;
import java.util.Map;
import java.util.Set;

import utility.domain.Reference;
import utility.menu.Menu;

/**
 * @author Avery J. Regier
 */
public interface TournamentType extends Reference {

	/**
	 * @param seedOID
	 * @return
	 */
	public abstract Seed getSeed(int seedOID);

	/**
	 * @param oponentOID
	 * @return
	 */
	public abstract Opponent getOpponent(int oponentOID);

	/**
	 * @param sequence
	 * @return
	 */
	public abstract Opponent getOpponentByOrder(int sequence);

	/**
	 * @return
	 */
	public abstract GameNode getChampionshipGameNode();

	/**
	 * @return
	 */
	public abstract int getNumOponents();

	/**
	 * @return
	 */
	public abstract Set<Map.Entry<Integer, Opponent>> getOpponents();

	public abstract Map<Object, GameNode> getGameNodes();
	public List<GameNode> getGameNodesInLevelOrder();
	
	public abstract GameNode getGameNode(int gameNodeOID);

	public abstract Object getID();

	public abstract String getName();

	public abstract int getOID();

	public abstract int getNumSeeds();

	public abstract Menu getLevelMenu();

}