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

import utility.domain.Reference;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class MainGameNode extends GameNodeAdapter implements GameNode {

	class SeedFeeder extends Feeder {
		private final Seed seed;
		SeedFeeder(Opponent oponent, Seed seed) {
			super(oponent);
			this.seed = seed;
		}
		public void visit(GameVisitor<?> visitor, GameNode nextNode) {
			visitor.visit(oponent, seed, getLevel().getRoundNo()-1, nextNode);
		}
		public Seed visitForWinner(GameVisitor<?> visitor) {
			return seed;
		}
		public Reference getFeeder() {
			return seed;
		}
		public boolean isSeed() {
			return true;
		}
	}

	class GameNodeFeeder extends Feeder {
		private final GameNode gameNode;
		GameNodeFeeder(Opponent oponent, GameNode gameNode) {
			super(oponent);
			this.gameNode = gameNode;
		}
		public void visit(GameVisitor<?> visitor, GameNode nextNode) {
			gameNode.visit(visitor, oponent, nextNode);
		}
		public Seed visitForWinner(GameVisitor<?> visitor) {
			return gameNode.visitForWinner(visitor);
		}
		public Reference getFeeder() {
			return gameNode;
		}
		public boolean isSeed() {
			return false;
		}
	}

	private final int oid;
	private final Level level;
	private final Map<Opponent, Feeder> feeders = new TreeMap<>();
	private final GameFeederType gameFeederType;	

	/**
	 * @param gameNodeOID
	 * @param level
	 * @param type 
	 */
	public MainGameNode(int gameNodeOID, Level level, GameFeederType type) {
		this.oid = gameNodeOID;
		this.level = level;
		this.gameFeederType = type;
	}
	
	/**
	 * @param oponent
	 * @param seed
	 */
	public void loadFeeder(Opponent oponent, Seed seed) {
		feeders.put(oponent, new SeedFeeder(oponent, seed));
	}
	
	/**
	 * @param oponent
	 * @param gameNode
	 */
	public void loadFeeder(Opponent oponent, GameNode gameNode) {
		feeders.put(oponent, new GameNodeFeeder(oponent, gameNode));
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IGameNode#getLevel()
	 */
	public Level getLevel() {
		return level;
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IGameNode#getOid()
	 */
	public int getOid() {
		return oid;
	}

	public Object getID() {
		return oid;
	}

	public String getName() {
		return level.getName();
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IGameNode#getFeeders()
	 */
	public Collection<Feeder> getFeeders() {
		return feeders.values();
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IGameNode#getFeeder(com.tournamentpool.domain.Opponent)
	 */
	public Feeder getFeeder(Opponent winner) {
		return feeders.get(winner);
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IGameNode#getGameFeederType()
	 */
	public GameFeederType getGameFeederType() {
		return gameFeederType;
	}
	
	public GameNode getIdentityNode() {
		return this;
	}
}
