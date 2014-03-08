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

package com.tournamentpool.controller.autoupdate;

import com.tournamentpool.controller.TournamentVisitor;
import com.tournamentpool.domain.*;
import com.tournamentpool.domain.GameNode.Feeder;
import com.tournamentpool.domain.MainTournament.WinnerSource;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

final class AutoUpdateWinnerSource implements WinnerSource {
	private final MainTournament mainTournament;
	private final Map<String, LiveGame> teamGameMap;

	AutoUpdateWinnerSource(MainTournament mainTournament, Map<String, LiveGame> teamGameMap) {
		this.mainTournament = mainTournament;
		this.teamGameMap = teamGameMap;
	}

	@Override
	public GameInfo getGameInfo(TournamentType tournamentType,
			GameNode node) {
		GameInfo oldGame = mainTournament.getGame(node);
		if(oldGame != null) {
			Opponent oldWinner = oldGame.getWinner();
			if(oldWinner != null) return oldGame;
		}
		
	    Map<Opponent, Team> teams = getTeamsIfReadyToPlay(mainTournament, node);
        return getUpdatedGameInfoFor(oldGame, teams);
	}

    private GameInfo getUpdatedGameInfoFor(GameInfo oldGame, Map<Opponent, Team> teams) {
        if(teams != null) {
            for (Entry<Opponent, Team> entry : teams.entrySet()) {
                Iterable<String> names = entry.getValue().getNames();
            n:	for (String name : names) {
                    String teamName = name.toUpperCase();
                    System.out.println("Evaluating "+teamName);
                    LiveGame game = teamGameMap.get(teamName);
                    if(game != null) {
                        if(verifySameGame(game, teams)) {
                            return new AutoUpdateGameInfo(game, teams, oldGame);
                        }
                        break n;
                    }
                }
            }
        } else {
            System.out.println("No teams are ready to play for "+mainTournament.getOid()+" "+mainTournament.getName());
        }
        return null;
    }

    private Map<Opponent, Team> getTeamsIfReadyToPlay(Tournament tournament, GameNode gameNode) {
	
		Game game = tournament.getGame(gameNode);
		
		if(game == null || game.getWinner() == null) {
			Map<Opponent, Team> teams = new HashMap<Opponent, Team>();
			Collection<Feeder> feeders = gameNode.getFeeders();
			for (Feeder feeder : feeders) {
				Seed seed = feeder.visitForWinner(new TournamentVisitor(tournament));
				Team team = tournament.getTeam(seed);
				if(team != null) {
					teams.put(feeder.getOpponent(), team);
				} else {
					return null;
				}
			}
			return teams;
		}
		return null;
	}

	private boolean verifySameGame(LiveGame game, Map<Opponent, Team> teams) {
		System.out.println("Evaluating game "+game);
		for (Team team : teams.values()) {
			if(anyNamesMatch(game, team)) {
				System.out.println("Team "+team.getName()+" matches!");
			} else {
				System.out.println("Team "+team.getName()+" does not match.");
				return false;
			}
		}
		return true;
	}

	private boolean anyNamesMatch(LiveGame game, Team team) {
		for (String name : team.getNames()) {
			if(teamMatches(game, name)) {
				return true;
			}
		}
		return false;
	}

	private boolean teamMatches(LiveGame game, String name) {
		return game.getPlayerScores().containsKey(name.toUpperCase());
	}

}