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
    private final boolean picked;
    private boolean winning = false;
    private boolean tied = false;

    public ScoreBean(Seed seed, Team team, Integer score, boolean picked) {
        this.seed = seed.getSeedNo();
        this.team = new TeamBean(team);
        this.score = score;
        this.picked = picked;
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

    public boolean isPicked() {
        return picked;
    }

    public void setWinning() {
        this.winning = true;
    }

    public String getColor() {
        if(picked) {
            if (winning) {
                return "green";
            } else if (tied) {
                return "orange";
            } else {
                return "red";
            }
        }
        return "inherit";
    }

    public void setTied() {
        this.tied = true;
    }
}
