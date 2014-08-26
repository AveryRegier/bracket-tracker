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

import com.tournamentpool.controller.TournamentVisitor;
import utility.domain.Reference;

import java.util.*;

abstract class GameNodeAdapter implements GameNode {
	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IGameNode#visitForWinner(com.tournamentpool.domain.GameVisitor)
	 */
	public Seed visitForWinner(GameVisitor<?> visitor) {
		Optional<Opponent> winner = visitor.getWinner(this);
        Seed seed = null;
        if(winner.isPresent()) {
            for (Feeder feeder : getFeeders()) {
                if (feeder.getOpponent() == winner.get()) {
                    seed = feeder.visitForWinner(visitor);
                }
            }
        }
		return seed;
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IGameNode#visitForLoser(com.tournamentpool.domain.GameVisitor)
	 */
	public Seed visitForLoser(GameVisitor<?> visitor) {
		Optional<Opponent> winner = visitor.getWinner(this);
		Seed seed = null;
        if(winner.isPresent()) {
            for (Feeder feeder : getFeeders()) {
                if (feeder.getOpponent() != winner.get()) {
                    seed = feeder.visitForWinner(visitor);
                }
            }
        }
		return seed;
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IGameNode#visit(com.tournamentpool.domain.GameVisitor, com.tournamentpool.domain.Opponent)
	 */
	public void visit(GameVisitor<?> visitor, Opponent opponent, GameNode nextNode) {
		Seed winner = visitForWinner(visitor);
		Collection<Feeder> feeders = getFeeders();
		int half = feeders.size()/2;
        for (Feeder feeder : feeders) {
            if (half-- == 0) visitor.visit(winner, opponent, this, nextNode);
            feeder.visit(visitor, this);
        }
	}

    private static final ThreadLocal<Map<GameNodeAdapter, Set<Seed>>> possibleWinningSeedsCache = new ThreadLocal<>();
    public Set<Seed> getPossibleWinningSeeds(final Tournament tournament) {
		Map<GameNodeAdapter, Set<Seed>> cache = possibleWinningSeedsCache.get();
		if(cache != null) {
			Set<Seed> seedsThatCanWin = cache.get(this);
			if(seedsThatCanWin != null) return seedsThatCanWin;
		}
		if(cache == null) {
			cache = new HashMap<>();
			possibleWinningSeedsCache.set(cache);
		}
        Set<Seed> seedsThatCanWin = calculatePossibleWinningSeeds(tournament);
		cache.put(this, seedsThatCanWin);
		return seedsThatCanWin;
	}

    public static void clearPossibleWinningSeedsCache() {
        possibleWinningSeedsCache.set(null);
    }

    private Set<Seed> calculatePossibleWinningSeeds(final Tournament tournament) {
        Set<Seed> seedsThatCanWin = new HashSet<>();
        Optional<Game> game = tournament.getGame(this);
        if(game.isPresent() && game.get().isComplete()) {
            seedsThatCanWin.add(visitForWinner(new GameVisitor<TournamentVisitor.Node>(tournament) {
                public Optional<Opponent> getWinner(GameNode node) {
                    return tournament.getWinner(node);
                }
                public void visit(Seed winner, Opponent opponent, GameNode node, GameNode nextNode) {}
                public void visit(Opponent opponent, Seed seed, int roundNo, GameNode nextNode) {}
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
        return seedsThatCanWin;
    }
}
