package com.tournamentpool.beans;

import com.tournamentpool.domain.Seed;
import com.tournamentpool.domain.Team;

/**
 * Created by avery on 3/18/17.
 */
public class ScoreBean {
    private int seed;
    private TeamBean team;
    private Integer score;

    public ScoreBean(Seed seed, Team team, Integer score) {
        this.seed = seed.getSeedNo();
        this.team = new TeamBean(team);
        this.score = score;
    }

    public int getSeed() {
        return seed;
    }

    public TeamBean getTeam() {
        return team;
    }

    public Integer getScore() {
        return score;
    }
}
