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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import utility.domain.Reference;

import com.tournamentpool.controller.TournamentVisitor;


/**
 * The purpose of this class is to make game nodes at the start level look like seeds.
 * This supports the 'Sweet Sixteen' feature.
 * @author Avery Regier
 */
class SubGameNode extends GameNodeAdapter implements GameNode {

	class SubFeeder extends Feeder {
		private final Feeder parent;

		private SubFeeder(Feeder parent) {
			super(parent.getOpponent());
			this.parent = parent;
		}

		public void visit(GameVisitor<?> visitor, GameNode nextNode) {
			if(isSeed()) {
				visitor.visit(oponent, getSeed(), getLevel().getRoundNo()-1, nextNode);
			} else {
//				parent.visit(visitor);
				wrap((GameNode)getFeeder()).visit(visitor, oponent, nextNode);
			}
		}

		public Seed visitForWinner(GameVisitor<?> visitor) {
			if(isSeed()) {
				return getSeed();
			} else {
//				return parent.visitForWinner(visitor);
				return wrap((GameNode)getFeeder()).visitForWinner(visitor);
			}
		}

		public Reference getFeeder() {
			Reference feeder = parent.getFeeder();
			return parent.isSeed() || !isAtStartLevel(feeder)? wrap(feeder) :
				getSeed();
		}

		private Seed getSeed() {
			// We always need a tournament visitor, even when doing a bracket.
			// This determines who is the initial seed for the bracket, which
			// comes from actual tournament results.
			// NOTE: We can't really optimize this, as we need to be able to track changes.
			Seed seed = parent.visitForWinner(new TournamentVisitor(tournament));
			return seed != null ? seed : Seed.UNKNOWN;
		}

		public boolean isSeed() {
			boolean isSeed = parent.isSeed();
			return isSeed ? isSeed : isAtStartLevel(parent.getFeeder());
		}

		private boolean isAtStartLevel(Reference feeder) {
			 // should be startLevel.previous()
			return ((GameNode)feeder).getLevel().getRoundNo() == startLevel.getRoundNo() - 1;
		}
	}
	
	private final GameNode parent;
	private final Level startLevel, currentLevel;
	private final Tournament tournament;
	private final SubTournamentType tournamentType;

	SubGameNode(GameNode parent, Level startLevel, Tournament tournament, SubTournamentType tournamentType) {
		this.parent = parent;
		this.startLevel = startLevel;
		this.tournament = tournament;
		this.tournamentType = tournamentType;
		this.currentLevel = tournamentType.wrap(parent, startLevel);
	}

	public Level getLevel() {
		return currentLevel;
	}

	public int getOid() {
		return parent.getOid();
	}

	public Collection<Feeder> getFeeders() {
		Collection<Feeder> feeders = parent.getFeeders();
		List<Feeder> toReturn = new ArrayList<Feeder>(feeders.size());
		for (Feeder feeder: feeders) {
			toReturn.add(new SubFeeder(feeder));
		}
		return toReturn;
	}

	public Feeder getFeeder(Opponent winner) {
		return new SubFeeder(parent.getFeeder(winner));
	}

	public GameFeederType getGameFeederType() {
		return parent.getGameFeederType();
	}

	public Object getID() {
		return parent.getID();
	}

	public String getName() {
		return parent.getName();
	}
	
	public GameNode getIdentityNode() {
		return parent;
	}

	private SubGameNode wrap(GameNode node) {
		return tournamentType.wrap(node);
	}
	
	private Reference wrap(Reference feeder) {
		if(feeder instanceof GameNode) {
			return wrap((GameNode)feeder);
		}
		return feeder;
	}
}