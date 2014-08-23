package com.tournamentpool.controller.autoupdate;

import com.tournamentpool.domain.Game;
import com.tournamentpool.domain.GameInfo;
import com.tournamentpool.domain.Opponent;
import com.tournamentpool.domain.Team;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

/* 
Copyright (C) 2003-20013 Avery J. Regier.

This file is part of the Tournament Pool and Bracket Tracker.

Tournament Pool and Bracket Tracker is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

Tournament Pool and Bracket Tracker is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>. */


final class AutoUpdateGameInfo implements GameInfo {
	private final LiveGame game;
	private final Map<Opponent, Team> teams;
	private final Optional<Game> oldGame;

	AutoUpdateGameInfo(LiveGame game, Map<Opponent, Team> teams,
			Optional<Game> oldGame) {
		this.game = game;
		this.teams = teams;
		this.oldGame = oldGame;
	}

	@Override
	public java.util.Optional<Opponent> getWinner() {
		if(game.isFinal()) {
			String winnerTeamName = game.getWinner().toUpperCase();
			// making the assumption for now that team names actually match
			for (Entry<Opponent, Team> entry : teams.entrySet()) {
				Team team = entry.getValue();
				if(team.anyNamesMatch(winnerTeamName)) {
					System.out.println("Winner of "+game+" is "+team.getName());
					return Optional.of(entry.getKey());
				}
			}
			System.out.println("No winner found for "+game);
		}
		return null;
	}

	@Override
	public String getStatus() {
		return game.getStatus();
	}

	@Override
	public Integer getScore(Opponent opponent) {
		Map<String, Integer> playerScores = game.getPlayerScores();
		Iterable<String> names = teams.get(opponent).getNames();
		for (String name : names) {
			Integer score = playerScores.get(name.toUpperCase());
			if(score != null) return score;
		}
		return null;
	}

	@Override
	public String getGameID() {
		return game.getGameID();
	}

	@Override
	public Date getDate() {
		if(oldGame.isPresent() && oldGame.get().getDate() != null)
            return oldGame.get().getDate();
		else return game.getStartDate();
	}
}