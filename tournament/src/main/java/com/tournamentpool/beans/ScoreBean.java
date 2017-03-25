package com.tournamentpool.beans;

import com.tournamentpool.domain.*;
import com.tournamentpool.util.Utilities;

import java.util.Set;
import java.util.function.ToIntBiFunction;

/**
 * Created by avery on 3/18/17.
 */
public class ScoreBean {
    private Seed seed;
    private TeamBean team;
    private Integer score;
    private final boolean picked;
    private final Set<Bracket.Pick> picks;
    private boolean winning = false;
    private boolean tied = false;

    public ScoreBean(Seed seed, Team team, Integer score, Set<Bracket.Pick> picks) {
        this.seed = seed;
        this.team = new TeamBean(team);
        this.score = score;
        this.picked = picks != null && !picks.isEmpty();
        this.picks = picks;
    }

    public int getSeed() {
        return seed.getSeedNo();
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

    public int getCurrent() {
        return sumPoints(this::getScore);
    }

    public int getRemaining() {
        return sumPoints(this::getPotentialPoints);
    }

    private int sumPoints(ToIntBiFunction<Bracket.Pick, Pool> fn) {
        return this.picks != null ? this.picks.stream()
                .distinct()
                .flatMapToInt(p -> p.getBracket().getPoolStream()
                        .filter(Pool::isDefiningPool)
                        .mapToInt(pool->fn.applyAsInt(p, pool)))
                .sum() : 0;
    }

    private int getScore(Bracket.Pick pick, Pool pool) {
        ScoreSystem scoreSystem = pool.getScoreSystem();
        Level level = pick.getGameNode().getLevel();
        return scoreSystem.getScore(level);
    }

    private int getPotentialPoints(Bracket.Pick pick, Pool pool) {
        Bracket bracket = pick.getBracket();
        ScoreSystem scoreSystem = pool.getScoreSystem();
        Level level = pick.getGameNode().getLevel();
        return getPotentialPoints(scoreSystem, level, bracket);
    }

    private int getPotentialPoints(ScoreSystem scoreSystem, Level level, Bracket bracket) {
        return bracket.getTournament().getTournamentType().getGameNodes().stream()
                .filter(n->n.getLevel().compareTo(level) > 0)
                .map(n-> bracket.getPickFromMemory(n).filter(p -> p.getSeed() == seed))
                .flatMap(Utilities::asStream)
                .mapToInt(p->scoreSystem.getScore(p.getGameNode().getLevel()))
                .sum();
    }
}
