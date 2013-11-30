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

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import utility.domain.Reference;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.controller.TournamentVisitor;
import com.tournamentpool.domain.Bracket.Pick;

/**
 * @author Avery J. Regier
 */
public class ScoreSystem implements Reference {
	public class Score {
		private int current;
		private int remaining;
		private Map<GameNode, Pick> remainingNodes = new HashMap<GameNode, Pick>();
		private Map<GameNode, Set<Seed>> otherPicks = new HashMap<GameNode, Set<Seed>>();
		private Map<GameNode, Set<Seed>> unforseenPicksMap;

		public int getCurrent() {
			return current;
		}

		public int getMax() {
			return current + remaining;
		}

		public int getRemaining() {
			return remaining;
		}

		public void add(int points) {
			this.current += points;
		}

		public void predict(int points, Pick node) {
			this.remaining += points;
			remainingNodes.put(node.getGameNode(), node);
		}

		public boolean canTieOrBeat(Score other) {
			
			// if my max is less than their current, then I can't win
			if(getMax() < other.current) return false;

			// we can check the difference between the brackets
			// this is a superset of the first check
			Map<GameNode, Pick> uniquePicksLeft = new HashMap<GameNode, Pick>();
			Set<Entry<GameNode, Pick>> entrySet = remainingNodes.entrySet();
			for (Entry<GameNode, Pick> entry : entrySet) {
				if(getSeed(other.remainingNodes.get(entry.getKey())) != getSeed(entry.getValue())) {
					uniquePicksLeft.put(entry.getKey(), entry.getValue());
				}
			}
			if(current < other.current) {
				// if all my remaining picks are in another bracket and I have less points, then I can't win.
				if(uniquePicksLeft.size() == 0) return false;

				// if the difference is greater than my unique points left, then I can't win
				if((other.current - current) > uniquePointsRemaining(uniquePicksLeft) ) {
					checkUnforseen(other);
					return false;
				}
			}
			if(uniquePicksLeft.size() == 0) {
				// set up further analysis that can be done only when all brackets evaluated
				// only do this if we don't already know we're beaten.
				// that way we know we can push this guy down in rank if the analysis proves
				// someone else will be able to get points
				Map<GameNode, Pick> otherUniquePicksLeft = getOtherPicksLeft(other);
				if(otherUniquePicksLeft.size() > 0 &&
					(current < other.current + getPossibleScore(otherUniquePicksLeft.values())))
				{
					for (Map.Entry<GameNode, Pick> entry: otherUniquePicksLeft.entrySet()) {
						Set<Seed> set = this.otherPicks.get(entry.getKey());
						if(set == null) {
							set = new HashSet<Seed>();
							this.otherPicks.put(entry.getKey(), set);
						}
						set.add(getSeed(entry.getValue()));
					}
				}
			}
			// TODO: other, more complex scenarios
			checkUnforseen(other);
			return true;
		}

		private void checkUnforseen(Score other) {
			if(other.getMax() >= current) {
				// if it is at all possible for this guy to beat me, but isn't already, I want to
				// see what teams I can root for, for whom the other guy won't get 
				// points that I won't get as well.
				// In other words, If I'm out and just want to keep a good ranking,
				// then I don't want anyone else to get points and move ahead
				visitForTeamsThatNoOneElseGetsPointsFrom(other);
				
				// FIXME: Make sure I don't visit unnecessarily and thus miss
				// a team I really do want to win
			}
		}

		private Seed getSeed(Pick pick) {
			return pick == null ? null : pick.getSeed();
		}

		private int uniquePointsRemaining(Map<GameNode, Pick> uniquePicksLeft) {
			int pointsLeft = 0;
			Set<GameNode> keySet = uniquePicksLeft.keySet();
			for (GameNode node : keySet) {
				pointsLeft += getScore(node.getLevel());
			}
			return pointsLeft;
		}

		/**
		 * Get picks the other guy has that are still viable for games that this bracket
		 * has no viable picks for.  Use this to determine the potential this other bracket
		 * has to defeat the current bracket.
		 * @param other
		 * @return
		 */
		private Map<GameNode, Pick> getOtherPicksLeft(Score other) {
			Map<GameNode, Pick> uniquePicksLeft = new HashMap<GameNode, Pick>();
			for (Map.Entry<GameNode, Pick> entry: other.remainingNodes.entrySet()) {
				//if I don't have a pick for this node, but the other guy does, I care
				GameNode gameNode = entry.getKey();
				if(remainingNodes.get(gameNode) == null) {
					uniquePicksLeft.put(gameNode, entry.getValue());
				}
			}
			return uniquePicksLeft;
		}
		
		/**
		 * Make sure to only call this for brackets that can tie or beat me
		 * @param other
		 */
		private void visitForTeamsThatNoOneElseGetsPointsFrom(Score other) {
			if(unforseenPicksMap == null) {
				unforseenPicksMap = new HashMap<GameNode, Set<Seed>>();
			}
			for (Map.Entry<GameNode, Pick> entry: other.remainingNodes.entrySet()) {
				//if I don't have a pick for this node, but the other guy does, I care
				GameNode gameNode = entry.getKey();
				if(!remainingNodes.containsKey(gameNode)) {
					Set<Seed> unforseenPicks = unforseenPicksMap.get(gameNode);
					if(unforseenPicks == null) {
						Tournament tournament = entry.getValue().getBracket().getTournament();
						Set<Seed> stillInPlay = gameNode.getPossibleWinningSeeds(tournament);
						// make a copy because we're going to start removing seeds
						unforseenPicks = new HashSet<Seed>();
						Game game = tournament.getGame(gameNode);
						if(stillInPlay != null && (game == null || game.getWinner() == null) ){
							unforseenPicks.addAll(stillInPlay);
						}
						unforseenPicksMap.put(gameNode, unforseenPicks);
					}
					unforseenPicks.remove(entry.getValue().getSeed());
				}
			}
		}

		public boolean willGetBeatByOneOfThoseICanIndividuallyTieOrBeat(Tournament tournament) {
			for (Map.Entry<GameNode, Set<Seed>> entry: this.otherPicks.entrySet()) {
				GameNode gameNode = entry.getKey();
				Set<Seed> set = entry.getValue();
				// if there is a game node where all remaining teams that can get to that game
				// are picked by someone that this bracket can otherwise tie, then
				// this bracket cannot win.
				Set<Seed> possibleWinners = gameNode.getPossibleWinningSeeds(tournament);
				if(set.containsAll(possibleWinners)) {
					return true;
				}
			}
			return false;
		}

		/**
		 * Get the most important teams for this player to win
		 * @return
		 */
		public Seed[] getRootingFor() {
			int max = 0;
			Set<Seed> seeds = new LinkedHashSet<Seed>();
			for (Entry<GameNode, Pick> entry : remainingNodes.entrySet()) {
				GameNode node = entry.getKey();
				int currentRound = node.getLevel().getRoundNo();
				if(max == 0 || currentRound > max) {
					max = currentRound;
					seeds.clear();
					seeds.add(getSeed(entry.getValue()));
				} else if(currentRound == max) {
					seeds.add(getSeed(entry.getValue()));
				}
			}
			
			// add in any teams that if they win they keep others from getting points
			if(this.unforseenPicksMap != null) {
				for (Entry<GameNode, Set<Seed>> entry : this.unforseenPicksMap.entrySet()) {
					GameNode node = entry.getKey();
					if(!remainingNodes.containsKey(node)) {
						Set<Seed> gameSeeds = entry.getValue();
						int currentRound = node.getLevel().getRoundNo();
						if(currentRound > max) {
							seeds.addAll(gameSeeds);
						}
					}
				}
			}
			
			return seeds.toArray(new Seed[seeds.size()]);
		}
	}

	private int oid;
	private String name;
	private Map<Integer, int[]> levels = new HashMap<Integer, int[]>();

	/**
	 * @param scoreSystemOID
	 * @param name
	 */
	public ScoreSystem(int scoreSystemOID, String name) {
		this.oid = scoreSystemOID;
		this.name = name;
	}

	private int getPossibleScore(Collection<Pick> picks) {
		int score = 0;
		for (Bracket.Pick pick: picks) {
			score += getScore(pick.getGameNode().getLevel());
		}
		return score;
	}

	/**
	 * @param level
	 * @param points
	 * @param multiplier
	 */
	public void loadDetail(Level level, int points, int multiplier) {
		levels.put(new Integer(level.getOid()), new int[] {points, multiplier});
	}

	public int getScore(Level level) {
		int[] detail = levels.get(new Integer(level.getOid()));
		return detail[0] + level.getRoundNo()*detail[1]; // points + level * multiplier
	}

	public Object getID() {
		return new Integer(oid);
	}

	public String getName() {
		return name;
	}
	/**
	 * @return
	 */
	public int getOid() {
		return oid;
	}
	/**
	 * @param bracket
	 * @return
	 * @throws SQLException
	 */
	public Score calculate(Bracket bracket, SingletonProvider sp) throws SQLException {
		Score theScore = new Score();
		Tournament tournament = bracket.getTournament();
		TournamentVisitor tournamentVisitor = new TournamentVisitor(tournament);
		for (Bracket.Pick pick: bracket.getPicks(sp)) {
			GameNode gameNode = pick.getGameNode();
			if(gameNode != null) {
				Seed winner = gameNode.visitForWinner(tournamentVisitor);
				int score = getScore(gameNode.getLevel());
				Game game = tournament.getGame(gameNode);
				boolean played = game != null && game.getWinner() != null;
				if(played) {
					if(winner == pick.getSeed())
						theScore.add(score);
				} else {
					// if the game has not been played, then need to determine
					// if the pick has not already been defeated.
					Seed preWinner;
					do {
						GameNode.Feeder feeder = gameNode.getFeeder(bracket.getPickFromMemory(gameNode).getWinner());
						preWinner = feeder.visitForWinner(tournamentVisitor);
						Reference ref = feeder.getFeeder();
						if(ref instanceof GameNode) {
							gameNode = (GameNode) ref;
						}
					} while( preWinner == null);
					if(preWinner == pick.getSeed())	theScore.predict(score, pick);
				}
			}
		}
		return theScore;
	}
}
