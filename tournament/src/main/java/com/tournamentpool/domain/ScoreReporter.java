package com.tournamentpool.domain;


public interface ScoreReporter {

	public void insertScore(Game game, Opponent opponent, Integer score);

	public void updateScore(Game game, Opponent opponent, Integer score);

	public void deleteScore(Game game, Opponent opponent);

}