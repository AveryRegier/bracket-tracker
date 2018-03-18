package com.tournamentpool.controller.autoupdate;

import com.tournamentpool.domain.GameInfo;
import com.tournamentpool.domain.Opponent;
import com.tournamentpool.domain.Team;

import java.util.*;
import java.util.Map.Entry;

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
	private final Optional<? extends GameInfo> oldGame;

	AutoUpdateGameInfo(LiveGame game, Map<Opponent, Team> teams,
			Optional<? extends GameInfo> oldGame) {
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
					System.out.println("Winner of "+game.getGameID()+" is "+team.getName());
					return Optional.of(entry.getKey());
				}
			}
			System.out.println("No winner found for "+game.getGameID());
		}
		return Optional.empty();
	}

	@Override
	public String getStatus() {
		return game.getStatus();
	}

	@Override
	public Integer getScore(Opponent opponent) {
		Map<String, Integer> playerScores = game.getPlayerScores();
		Collection<String> names = teams.get(opponent).getNames();
		return names.stream()
				.map(name -> playerScores.get(name.toUpperCase()))
				.filter(Objects::nonNull)
				.findFirst()
				.orElse(null);
	}

	@Override
	public String getGameID() {
		return game.getGameID();
	}

	@Override
	public Date getDate() {
		return oldGame.
				flatMap(g->(Optional.ofNullable(g.getDate())))
				.orElse(game.getStartDate());
	}
}