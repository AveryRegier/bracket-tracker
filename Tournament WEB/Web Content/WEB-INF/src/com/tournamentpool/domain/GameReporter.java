package com.tournamentpool.domain;

import java.util.List;


public interface GameReporter {

	public List<Game> getGameReport();

	public void updateGame(Game game);

}