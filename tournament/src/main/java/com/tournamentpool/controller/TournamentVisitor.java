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
package com.tournamentpool.controller;

import com.tournamentpool.domain.Bracket.Pick;
import com.tournamentpool.domain.*;

/**
 * @author Avery J. Regier
 */
public class TournamentVisitor extends GameVisitorCommon<TournamentVisitor.Node> {

	public static class Node extends GameVisitorCommon.Node {
		private Opponent pick = null;
		private String status;
		private Integer teamScore;

		Node(Seed seed, Team team, Opponent opponent, int level, Integer teamScore) {
			super(seed, team, opponent, level);
			this.teamScore = teamScore;
		}

		public Node(Seed winner, Team team, Opponent oponent, int roundNo, GameNode node, String status, Integer teamScore) {
			super(winner, team, oponent, roundNo, node);
			this.status = status;
			this.teamScore = teamScore;
		}

		/**
		 * @param pick
		 */
		public void setPick(Pick pick) {
			if(pick != null) {
				this.pick = pick.getWinner();
			}
		}
		public void setPick(Game pick) {
			if(pick != null) {
				this.pick = pick.getWinner();
			}
		}
		
		public Opponent getPick() {
			return this.pick;
		}
		@Override
		public String getStatus() {
			return status;
		}
		
		public Integer getTeamScore() {
			return teamScore;
		}
	}

	/**
	 * 
	 */
	public TournamentVisitor(Tournament tournament) {
		super(tournament);
	}

	public void visit(Seed winner, Opponent opponent, GameNode node, GameNode nextNode) {
		Game game = tournament.getGame(node.getIdentityNode());
		list.add(
			new Node(winner, 
					tournament.getTeam(winner), 
					opponent,
					node.getLevel().getRoundNo(), 
					node, 
					game != null ? game.getStatus() : null,
					getScore(opponent, nextNode)));
	}

	private Integer getScore(Opponent opponent, GameNode nextNode) {
		if(nextNode != null) {
			Game nextGame = tournament.getGame(nextNode.getIdentityNode());
			if(nextGame != null) {
				return nextGame.getScore(opponent);
			}
		}
		return null;
	}

	public void visit(Opponent opponent, Seed seed, int roundNo, GameNode nextNode) {
		list.add(new Node(seed, 
						tournament.getTeam(seed), 
						opponent,
						roundNo,
						getScore(opponent, nextNode)));
	}
	
	public Opponent getWinner(GameNode node) {
		Game game = tournament.getGame(node);
		return game != null ? game.getWinner() : null;
	}
}
