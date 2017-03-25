package com.tournamentpool.controller;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.domain.*;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
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

    private static <T> BinaryOperator<T> throwingMerger() {
        return (u, v) -> {
            throw new IllegalStateException(String.format("Duplicate key %s", u));
        };
    }

    public static <T, K, U, M extends Map<K, U>> Collector<T, ?, M> toMap(Function<? super T, ? extends K> keyMapper,
                                                                          Function<? super T, ? extends U> valueMapper,
                                                                          Supplier<M> mapSupplier) {
        return Collectors.toMap(keyMapper, valueMapper, throwingMerger(), mapSupplier);
    }

    private BinaryOperator<Game> pickFirstMerger() {
        return (a,b)->a;
    }

    private Stream<Game> getRecentGames(Filter filter) {
        return getApp().getTournamentManager().getInProgressTournaments().stream()
                .filter(filter::pass)
                .flatMap(Tournament::recentGames)
                .map(Game::getIdentity)
                .flatMap(this::asStream)
                .distinct()
                .sorted(Comparator.comparing(Game::getDate));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private <T> Stream<T> asStream(Optional<T> opt) {
        if (opt.isPresent()) {
            return Stream.of(opt.get());
        }
        System.out.println("Optional is empty");
        return Stream.empty();
    }

    private Map<Seed, Set<Bracket.Pick>> getPicks(User user, Game g, Filter filter) {
        return streamPicksForGame(user, g, filter)
                .collect(Collectors.groupingBy(p->p.getWinner().map(g::getSeed).get(),
                        Collectors.mapping(p->p, Collectors.toSet())));
    }

    private Stream<Bracket.Pick> streamPicksForGame(User user, Game g, Filter filter) {
        return user.getBrackets().stream()
                .filter(filter::pass)
                .peek(b->System.out.println(b.getID()+" is under consideration"))
                .filter(b->b.getTournament().getIdentity() == g.getTournament().getIdentity())
                .filter(Bracket::isInPool)
                .peek(b->System.out.println(b.getID()+" is in a pool"))
                .map(b -> getGameNodeForBracket(b, g)
                        .flatMap((game) -> b.getPick(getApp().getSingletonProvider(), game)))
                .flatMap(this::asStream)
                .filter(p->p.getWinner().isPresent())
                .filter(p->g.isPlaying(p.getSeed()))
                .peek(p->System.out.println("found a pick"));
    }

    private Optional<GameNode> getGameNodeForBracket(Bracket b, Game g) {
        if (b.getTournament() == g.getTournament()) {
            System.out.println("bracket and game in same tournament");
            return Optional.of(g.getGameNode());
        }
        if (b.getTournament().getIdentity() == g.getTournament()) {
            System.out.println("bracket in a sub tournament");
            return b.getTournament()
                    .getTournamentType()
                    .getGameNode(
                            g.getGameNode().getOid());
        }
        System.out.println("bracket is not part of the same tournament");
        return Optional.empty();
    }
}
