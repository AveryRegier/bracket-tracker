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

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.domain.*;
import com.tournamentpool.domain.Bracket.Pick;

import java.util.Optional;

/**
 * @author Avery J. Regier
 */
public class BracketVisitor extends GameVisitorCommon<BracketVisitor.Node> {
	public static class Node extends GameVisitorCommon.Node {
		private boolean decided;
		private int score;
		private boolean correct;
		private boolean upsetPicked;

		Node(Seed seed, Team team, Opponent opponent, int level) {
			super(seed, team, opponent, level);
		}

		public Node(Seed winner, Team team, Opponent opponent, int roundNo, GameNode node) {
			super(winner, team, opponent, roundNo, node);
		}

		public void setDecided(boolean correct, int score) {
			this.decided = true;
			this.correct = correct;
			this.score = score;
		}

		public void setPossible(int score) {
			this.decided = false;
			this.score = score;
		}

		public boolean isDecided() {
			return decided;
		}

		public int getScore() {
			if(decided && correct) {
				return score;
			} else return 0;
		}

		public int getPossible() {
			if(!decided) return score;
			else return 0;
		}

		public boolean isCorrect() {
			return correct;
		}

		public void setUpsetPicked(boolean upsetPicked) {
			this.upsetPicked = upsetPicked;
		}

		public boolean isUpsetPicked() {
			return upsetPicked;
		}
	}

	private final Bracket bracket;

	/**
	 * @param tournament
	 */
	public BracketVisitor(SingletonProvider sp, Tournament tournament, Bracket bracket) {
		super(tournament);
		this.bracket = bracket;
		bracket.getPicks(sp); // preretrieve picks
	}

	public void visit(Seed winner, Opponent opponent, GameNode node, GameNode nextNode) {
		list.add(
			new Node(winner, tournament.getTeam(winner), opponent,
					node.getLevel().getRoundNo(), node));
	}

	public void visit(Opponent opponent, Seed seed, int roundNo, GameNode nextNode) {
		list.add(new Node(seed, tournament.getTeam(seed), opponent, roundNo));
	}

	public Optional<Opponent> getWinner(GameNode node) {
        return bracket.getPickFromMemory(node).map(Pick::getWinner).orElse(Optional.empty());
	}
}
