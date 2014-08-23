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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


/**
 * @author avery
 */
public class Game implements GameInfo {

	private Tournament tournament;
	private GameNode gameNode;
	private Opponent winner;
	private Date game_date; 
	private boolean isnew = true;
	protected Map<Opponent, Integer> scores = new HashMap<Opponent, Integer>();
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
	public Opponent getWinner() {
		return winner;
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
	
	/**
	 * @return Returns the gameOID.
	 */
//	public int getOid() {
//		return gameOID;
//	}
	
	public boolean isNew() {
		return isnew;
	}
	
	void setOld() {
		isnew = false;
	}

	public Set<Seed> getSeedsStillInPlay() {
		if(getWinner() != null) return null;
		return gameNode.getPossibleWinningSeeds(tournament);
	}

	public Integer getScore(Opponent opponent) {
		return scores.get(opponent);
	}

	public String getGameID() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getStatus() {
		return status;
	}

	/**
	 * 
	 * @param opponent
	 * @param score
	 * @return true if this is an update
	 */
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
}
