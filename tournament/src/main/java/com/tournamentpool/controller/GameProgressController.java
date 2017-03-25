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
                .sorted(Comparator.comparing(Game::getDate));
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
                        .flatMap((game) -> b.getPick(getApp().getSingletonProvider(), game)))
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
