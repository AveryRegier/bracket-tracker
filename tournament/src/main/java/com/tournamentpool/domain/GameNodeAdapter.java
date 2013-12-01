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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import utility.domain.Reference;

import com.tournamentpool.controller.TournamentVisitor;

abstract class GameNodeAdapter implements GameNode {
	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IGameNode#visitForWinner(com.tournamentpool.domain.GameVisitor)
	 */
	public Seed visitForWinner(GameVisitor<?> visitor) {
		Opponent winner = visitor.getWinner(this);
		Seed seed = null;
		for (Feeder feeder: getFeeders()) {
			if(feeder.getOpponent() == winner) {
				seed = feeder.visitForWinner(visitor);
			}
		}
		return seed;
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IGameNode#visitForLoser(com.tournamentpool.domain.GameVisitor)
	 */
	public Seed visitForLoser(GameVisitor<?> visitor) {
		Opponent winner = visitor.getWinner(this);
		Seed seed = null;
		for (Feeder feeder: getFeeders()) {
			if(feeder.getOpponent() != winner) {
				seed = feeder.visitForWinner(visitor);
			}
		}
		return seed;
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IGameNode#visit(com.tournamentpool.domain.GameVisitor, com.tournamentpool.domain.Opponent)
	 */
	public void visit(GameVisitor<?> visitor, Opponent oponent, GameNode nextNode) {
		Seed winner = visitForWinner(visitor);
		Collection<Feeder> feeders = getFeeders();
		int half = feeders.size()/2;
		for (Iterator<Feeder> iter = feeders.iterator(); iter.hasNext(); half--) {
			Feeder feeder = iter.next();
			if(half == 0) visitor.visit(winner, oponent, this, nextNode);
			feeder.visit(visitor, this);
		}
	}


	private static ThreadLocal<Map<GameNodeAdapter, Set<Seed>>> possibleWinningSeedsCache = 
		new ThreadLocal<Map<GameNodeAdapter, Set<Seed>>>();
	public Set<Seed> getPossibleWinningSeeds(final Tournament tournament) {
		Map<GameNodeAdapter, Set<Seed>> cache = possibleWinningSeedsCache.get();
		if(cache != null) {
			Set<Seed> seedsThatCanWin = cache.get(this);
			if(seedsThatCanWin != null) return seedsThatCanWin;
		}
		if(cache == null) {
			cache = new HashMap<GameNodeAdapter, Set<Seed>>();
			possibleWinningSeedsCache.set(cache);
		}
//		final Map theCache = cache;
		Set<Seed> seedsThatCanWin = new HashSet<Seed>();
		Game game = tournament.getGame(this);
		if(game != null && game.isComplete()) {
			seedsThatCanWin.add(visitForWinner(new GameVisitor<TournamentVisitor.Node>(tournament) {
				public Opponent getWinner(GameNode node) {
					return tournament.getGame(node).getWinner();
				}
				public void visit(Seed winner, Opponent oponent, GameNode node, GameNode nextNode) {
//					if(winner != null) {// a little performance tweak
//						// if we have an answer, put it in the cache for next time
//						Set seedsThatCanWin = new HashSet();
//						seedsThatCanWin.add(winner);
//						theCache.put(node, seedsThatCanWin);
//					}
				}
				public void visit(Opponent oponent, Seed seed, int roundNo, GameNode nextNode) {}
			}));
		} else {
			for(Feeder feeder: getFeeders()) { 
				Reference reference = feeder.getFeeder();
				if(reference instanceof GameNode) {
					seedsThatCanWin.addAll(((GameNode) reference).getPossibleWinningSeeds(tournament));
				} else { // if(reference instanceof Seed) is assumed to be true
					seedsThatCanWin.add((Seed)reference);
				}
			}
		}
		cache.put(this, seedsThatCanWin);
		return seedsThatCanWin;
	}

	public static void clearPossibleWinningSeedsCache() {
		possibleWinningSeedsCache.set(null);
	}
	
	public List<GameNode> getAllGameNodes() {
		List<GameNode> allGameNodes = new ArrayList<GameNode>();
		
		List<Reference> nodes = new LinkedList<Reference>();
		nodes.add(this);
		while(!nodes.isEmpty()) {
			Object o = nodes.remove(0);
			if(o instanceof GameNode) {
				GameNode node = (GameNode)o;
				for (GameNode.Feeder feeder : node.getFeeders()) {
					nodes.add(feeder.getFeeder());
				}
				allGameNodes.add(node);
			}
		}
		return allGameNodes;
	}

}
