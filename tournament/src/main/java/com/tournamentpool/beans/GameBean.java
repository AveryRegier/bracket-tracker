package com.tournamentpool.beans;

import com.tournamentpool.domain.Game;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by avery on 3/18/17.
 */
public class GameBean {
    private final Date date;
    private final List<ScoreBean> scores;
    private final String status;

    public GameBean(Game game) {
        this.date = game.getDate();
        this.scores = game.getScores().entrySet().stream()
                .map(e->new ScoreBean(e.getKey(), game.getTeam(e.getKey()), e.getValue()))
                .collect(Collectors.toList());
        this.status = game.getStatus();
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
