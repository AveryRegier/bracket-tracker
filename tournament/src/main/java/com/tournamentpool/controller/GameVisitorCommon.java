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
 * Created on Dec 27, 2004
 */
package com.tournamentpool.controller;

import com.tournamentpool.domain.*;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * @author Avery J. Regier
 */
public abstract class GameVisitorCommon<T extends GameVisitorCommon.Node> extends GameVisitor<T> {
	public static class Node {
		public static class Feeder {
			private final Opponent opponent;
			private final Object id;
			private final boolean isSeed;
			public Feeder(Opponent opponent, Object oid, boolean isSeed){
				this.opponent = opponent;
				this.id = oid;
				this.isSeed = isSeed;
			}
			public Opponent getOpponent() {
				return opponent;
			}
			public Object getID() {
				return id;
			}
			public boolean isSeedNode() {
				return isSeed;
			}
		}
		
		private final Team team;
		private final Seed seed;
		private final Opponent opponent;
		private final int level;
		private int gameNodeOID;
		private Collection<Feeder> feeders;
		private final boolean isSeed;
		private boolean upset;
		
		Node(Seed seed, Team team, Opponent opponent, int level) {
			this.seed = seed;
			this.team = team;
			this.opponent = opponent;
			this.level = level;
			this.isSeed = true;
		}

		public Node(Seed seed, Team team, Opponent opponent, int level, GameNode gameNode) {
			this.seed = seed;
			this.team = team;
			this.opponent = opponent;
			this.level = level;
			this.gameNodeOID = gameNode.getOid();
			this.isSeed = false;

            this.feeders = gameNode.getFeeders().stream()
                    .map(feeder -> new Feeder(
                        feeder.getOpponent(),
                        feeder.getFeeder().getID(),
                        feeder.isSeed()))
                    .collect(Collectors.toList());
		}

		public int getLevel() {
			return level;
		}

		public Opponent getOpponent() {
			return opponent;
		}

		public Team getTeam() {
			return team;
		}

		public Seed getSeed() {
			return seed;
		}

		public int getGameNodeOid() {
			return gameNodeOID;
		}
		
		public Iterable<Feeder> getFeeders() {
			return feeders != null ? feeders : Collections.<Feeder>emptyList();
		}
		
		public boolean isSeedNode() {
			return isSeed;
		}
		
		public void setUpset(boolean upset) {
			this.upset = upset;
		}
		
		public boolean isUpset() {
			return upset;
		}
		
		public String getStatus() {
			return null;
		}
	}

	GameVisitorCommon(Tournament tournament) {
		super(tournament);
	}
}
