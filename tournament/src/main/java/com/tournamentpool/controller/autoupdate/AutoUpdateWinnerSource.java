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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

final class AutoUpdateWinnerSource implements WinnerSource {
	private static final Logger logger = LoggerFactory.getLogger(AutoUpdateWinnerSource.class);
	
	private final MainTournament mainTournament;
	private final Map<String, LiveGame> teamGameMap;

	AutoUpdateWinnerSource(MainTournament mainTournament, Map<String, LiveGame> teamGameMap) {
		this.mainTournament = mainTournament;
		this.teamGameMap = teamGameMap;
	}

	@Override
	public Optional<GameInfo> getGameInfo(TournamentType tournamentType,
                                                    GameNode node) {
		Optional<Game> oldGame = mainTournament.getGame(node);
		if(oldGame.flatMap(GameInfo::getWinner).isPresent()) {
			return oldGame.map(g->(GameInfo)g);
		}

	    Map<Opponent, Team> teams = getTeamsIfReadyToPlay(mainTournament, node);
        return Optional.ofNullable(getUpdatedGameInfoFor(oldGame, teams));
	}

    private GameInfo getUpdatedGameInfoFor(Optional<Game> oldGame, Map<Opponent, Team> teams) {
        if(teams != null) {
            for (Entry<Opponent, Team> entry : teams.entrySet()) {
                Iterable<String> names = entry.getValue().getNames();
            n:	for (String name : names) {
                    String teamName = name.toUpperCase();
                    logger.info("Evaluating "+teamName);
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
            logger.info("No teams are ready to play for "+mainTournament.getOid()+" "+mainTournament.getName());
        }
        return null;
    }

    private Map<Opponent, Team> getTeamsIfReadyToPlay(Tournament tournament, GameNode gameNode) {
	
		Optional<Opponent> winner = tournament.getWinner(gameNode);
		
		if(!winner.isPresent()) {
			Map<Opponent, Team> teams = new HashMap<>();
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
		logger.info("Evaluating game "+teams.values().stream()
				.flatMap(t->t.getNames().stream())
				.collect(Collectors.joining(", ")));
		for (Team team : teams.values()) {
			if(anyNamesMatch(game, team)) {
				logger.info("Team "+team.getName()+" matches!");
			} else {
				logger.info("Team "+team.getName()+" does not match. " + getFeedNames(game));
				return false;
			}
		}
		return true;
	}

	private String getFeedNames(LiveGame game) {
		return game.getPlayerScores().keySet().stream().collect(Collectors.joining(", "));
	}

	private boolean anyNamesMatch(LiveGame game, Team team) {
		return team.getNames().stream().anyMatch(name -> teamMatches(game, name));
	}

	private boolean teamMatches(LiveGame game, String name) {
		return game.getPlayerScores().containsKey(name.toUpperCase());
	}

}