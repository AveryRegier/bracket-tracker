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
 * Created on Mar 14, 2004
 */
package com.tournamentpool.domain;

import com.tournamentpool.controller.GameVisitorCommon.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Avery J. Regier
 */
public abstract class GameVisitor<T extends Node> {
	protected final List<T> list;
	protected final Tournament tournament;
	public GameVisitor(Tournament tournament) {
		this.tournament = tournament;
		TournamentType tournamentType = tournament.getTournamentType();
		int round = tournamentType.getChampionshipGameNode().getLevel().getRoundNo();
		int numOponents = tournamentType.getNumOponents();
		// this will be the number of games + seeds, if the structure is rational
		this.list = new ArrayList<>((int)Math.pow(numOponents, round+1));
	}
	
	public List<T> visit() {
		tournament.getTournamentType().getChampionshipGameNode().visit(this, null, null);
		return list;
	}

	public abstract void visit(Seed winner, Opponent oponent, GameNode node, GameNode nextNode);
	
	public abstract void visit(Opponent oponent, Seed seed, int roundNo, GameNode nextNode);

	public abstract Opponent getWinner(GameNode node);
}
