package com.tournamentpool.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
* Created by avery on 6/7/14.
*/
class GameNodeSpliterator implements Spliterator<GameNode> {
    private static int characteristics = Spliterator.DISTINCT & Spliterator.NONNULL & Spliterator.ORDERED;

    private final int size;
    private final List<GameNode> nodes = new LinkedList<>();

    public GameNodeSpliterator(TournamentType tournamentType) {
        this.size = tournamentType.getNumSeeds() * 2;
        nodes.add(tournamentType.getChampionshipGameNode());
    }


    @Override
    public boolean tryAdvance(Consumer<? super GameNode> action) {
        if(!nodes.isEmpty()) {
            GameNode current = nodes.remove(0);
            action.accept(current);
            addGameFeeders(nodes, current);
            return true;
        }
        return false;
    }

    private void addGameFeeders(List<GameNode> nodes, GameNode node) {
        node.getFeeders()
                .stream()
                .map((f) -> f.getFeeder())
                .filter((r) -> r instanceof GameNode)
                .forEach((r) -> nodes.add((GameNode) r));
    }

    @Override
    public Spliterator<GameNode> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return size;
    }

    @Override
    public int characteristics() {
        return characteristics;
    }
}
