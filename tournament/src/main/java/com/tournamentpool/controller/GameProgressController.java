/*
Copyright (C) 2003-2017 Avery J. Regier.

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

package com.tournamentpool.controller;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.domain.*;
import com.tournamentpool.util.Utilities;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by avery on 3/24/17.
 */
public class GameProgressController extends TournamentController {
    public GameProgressController(SingletonProvider sp) {
        super(sp);
    }

    public Map<Game, Map<Seed, Set<Bracket.Pick>>> getInProgressGamesWithPersonalAnalyses(User user, Filter filter) {
        TreeMap<Game, Map<Seed, Set<Bracket.Pick>>> map = new TreeMap<>(Comparator.comparing(Game::getDate));
        getRecentGames(filter).forEachOrdered(g->map.put(g, getPicks(user, g, filter)));
        return map;
    }

    private Stream<Game> getRecentGames(Filter filter) {
        return getApp().getTournamentManager().getInProgressTournaments().stream()
                .filter(filter::pass)
                .flatMap(Tournament::recentGames)
                .map(Game::getIdentity)
                .flatMap(Utilities::asStream)
                .distinct()
                .sorted(Comparator.comparing(Game::getDate).reversed());
    }

    private Map<Seed, Set<Bracket.Pick>> getPicks(User user, Game g, Filter filter) {
        return streamPicksForGame(user, g, filter)
                .collect(Collectors.groupingBy(p->p.getWinner().map(g::getSeed).get(),
                        Collectors.mapping(p->p, Collectors.toSet())));
    }

    private Stream<Bracket.Pick> streamPicksForGame(User user, Game g, Filter filter) {
        return user.getBrackets().stream()
                .filter(filter::pass)
                .filter(b->b.getTournament().getIdentity() == g.getTournament().getIdentity())
                .filter(Bracket::isInPool)
                .map(b -> getGameNodeForBracket(b, g)
                        .flatMap((game) -> b.getPick(sp, game)))
                .flatMap(Utilities::asStream)
                .filter(p->p.getWinner().isPresent())
                .filter(p->g.isPlaying(p.getSeed()));
    }

    private Optional<GameNode> getGameNodeForBracket(Bracket b, Game g) {
        if (b.getTournament() == g.getTournament()) {
            return Optional.of(g.getGameNode());
        }
        if (b.getTournament().getIdentity() == g.getTournament()) {
            return b.getTournament()
                    .getTournamentType()
                    .getGameNode(
                            g.getGameNode().getOid());
        }
        return Optional.empty();
    }
}
