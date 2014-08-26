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

import utility.domain.Reference;

import java.util.Collection;
import java.util.Set;

/**
 * @author Avery J. Regier
 */
public interface GameNode extends Reference {
	public abstract class Feeder implements Comparable<Feeder> {
		final Opponent oponent;

		Feeder(Opponent oponent) {
			this.oponent = oponent;
		}

		public int compareTo(Feeder o) {
			return oponent.compareTo(o.oponent);
		}
		public Opponent getOpponent() {
			return oponent;
		}
		public abstract void visit(GameVisitor<?> visitor, GameNode nextNode);
		public abstract Seed visitForWinner(GameVisitor<?> visitor);
		public abstract Reference getFeeder();
		public abstract boolean isSeed();
	}

	Seed visitForWinner(GameVisitor<?> visitor);

	Seed visitForLoser(GameVisitor<?> visitor);

	void visit(GameVisitor<?> visitor, Opponent opponent, GameNode nextNode);

	Level getLevel();

	int getOid();

	Collection<Feeder> getFeeders();

	/**
	 * @param winner
	 * @return
	 */
	Feeder getFeeder(Opponent winner);

	GameFeederType getGameFeederType();

	GameNode getIdentityNode();

	public abstract Set<Seed> getPossibleWinningSeeds(Tournament tournament);
}