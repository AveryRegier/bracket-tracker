/* 
Copyright (C) 2003-2013 Avery J. Regier.

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

/*
 * Created on Feb 20, 2003
 */
package com.tournamentpool.domain;

import com.tournamentpool.controller.TournamentVisitor;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;


/**
 * @author avery
 */
public class Game implements GameInfo, HasWinner {

	private final Tournament tournament;
	private final GameNode gameNode;
	private final Opponent winner;
	private final Date game_date;
	private boolean isnew = true;
	final Map<Opponent, Integer> scores = new HashMap<>();
	private String status;
	
	/**
	 * @param gameNode
	 * @param winner
	 * @param startTime
	 */
	public Game(Tournament tournament, GameNode gameNode, Opponent winner, Date startTime) {
		this.tournament = tournament;
		this.gameNode = gameNode;
		this.winner = winner;
		this.game_date = startTime;
	}
	
	/**
	 * @return
	 */
	public Optional<Opponent> getWinner() {
		return Optional.ofNullable(winner);
	}
	
	public boolean isComplete() {
		return winner != null;
	}
	
	public boolean isStarted() {
		return game_date != null && game_date.before(new Date());
	}
	
	public Tournament getTournament() {
		return tournament;
	}
	
	/**
	 * @return Returns the game_date.
	 */
	public Date getDate() {
		return game_date;
	}
	
	/**
	 * @return Returns the gameNode.
	 */
	public GameNode getGameNode() {
		return gameNode;
	}
	
	public boolean isNew() {
		return isnew;
	}
	
	void setOld() {
		isnew = false;
	}

	public Integer getScore(Opponent opponent) {
		return scores.get(opponent);
	}

	public String getGameID() {
		return tournament.getID()+"-"+gameNode.getID();
	}

	public String getStatus() {
		return status != null ? status : winner != null ? "Final" : "";
	}

	public Integer setScore(Opponent opponent, Integer score) {
		return scores.put(opponent, score);
	}
	
	public void setStatus(String status) {
		this.status = status;
	}

	Set<Entry<Integer, Opponent>> getOpponents() {
		return getTournament().getTournamentType().getOpponents();
	}

	public boolean hasInformation() {
		return winner != null || this.game_date != null || !scores.isEmpty();
	}

	public Optional<Game> getIdentity() {
		return tournament.getIdentity().getGame(getGameNode().getIdentityNode());
	}

	private Team getTeam(GameNode.Feeder feeder) {
		return getTeam(getSeed(feeder));
    }

	public Team getTeam(Seed seed) {
		return getTournament().getTeam(seed);
	}

	private Seed getSeed(GameNode.Feeder feeder) {
		return feeder.visitForWinner(
                new TournamentVisitor(getTournament()));
	}

	public Seed getSeed(Opponent opponent) {
		return getSeed(gameNode.getFeeder(opponent));
	}

	public Map<Seed, Integer> getScores() {
		return getGameNode().getFeeders().stream().collect(Collectors.toMap(
				this::getSeed,
				feeder->getScore(feeder.getOpponent()),
				(u, v) -> u,
				TreeMap::new
		));
	}

	public boolean isPlaying(Seed seed) {
		return seed != null &&
				getGameNode().getFeeders().stream()
					.map(this::getSeed)
					.anyMatch(seed::equals);
	}
}
