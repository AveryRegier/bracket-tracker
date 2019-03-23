package com.tournamentpool.beans;

import com.tournamentpool.domain.Bracket;
import com.tournamentpool.domain.Game;
import com.tournamentpool.domain.Seed;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by avery on 3/18/17.
 */
public class GameBean {
    private final Date date;
    private final List<ScoreBean> scores;
    private final String status;

    public GameBean(Map.Entry<Game, Map<Seed, Set<Bracket.Pick>>> entry) {
        System.out.println(entry.getKey().getGameID()+
                (entry.getValue().isEmpty() ? " no picks" : " has picks"));
        Game game = entry.getKey();
        this.date = game.getDate();
        this.scores = game.getScores().entrySet().stream()
                .map(e->new ScoreBean(
                        e.getKey(),
                        game.getTeam(e.getKey()),
                        e.getValue(),
                        entry.getValue().get(e.getKey())))
                .collect(Collectors.toList());
        this.status = game.getStatus();
        setupConcernIndicators();
    }

    private void setupConcernIndicators() {
        List<ScoreBean> sortedScores = getSortedScores();
        Integer max = sortedScores.get(0).getScore();
        List<ScoreBean> winners = sortedScores.stream()
                .filter(s -> Objects.equals(s.getScore(), max))
                .collect(Collectors.toList());
        if(winners.size() > 1) {
            winners.forEach(ScoreBean::setTied);
        } else {
            winners.forEach(ScoreBean::setWinning);
        }
    }

    private List<ScoreBean> getSortedScores() {
        List<ScoreBean> result = this.scores.stream()
                .sorted(Comparator.comparingInt(ScoreBean::getScore))
                .collect(Collectors.toList());
        Collections.reverse(result);
        return result;
    }

    public Date getDate() {
        return date;
    }

    public List<ScoreBean> getScores() {
        return scores;
    }

    public String getStatus() {
        return status;
    }
}
