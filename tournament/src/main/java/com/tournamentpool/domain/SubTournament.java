/* 
Copyright (C) 2003-2008 Avery J. Regier.

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

package com.tournamentpool.domain;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.broker.sql.delete.TournamentDeleteBroker;
import com.tournamentpool.broker.sql.status.TournamentBracketStatusBroker;
import com.tournamentpool.broker.sql.status.TournamentPoolStatusBroker;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;

class SubTournament implements Tournament {

	private final Integer id;
	private String name;
	private final Tournament parent;
	private final Level startLevel;
	private final SubTournamentType subTournamentType;
	private Timestamp startTime;

	SubTournament(Integer id, String name, Tournament parent, Level startLevel, Timestamp startTime) {
		this.id = id;
		this.name = name;
		this.parent = parent;
		this.startLevel = startLevel;
		this.startTime = startTime;
		this.subTournamentType = new SubTournamentType(parent, startLevel);
	}

	public Object getID() {
		return id;
	}

	public String getName() {
		return parent.getName() + ": "+name;
	}

	public Tournament getParentTournament() {
		return parent;
	}

	public Level getStartLevel() {
		return startLevel;
	}

	public boolean isStarted() {
		return new Timestamp(System.currentTimeMillis()).after(startTime) ||
			checkStarted(parent.getTournamentType().getChampionshipGameNode());
	}

	public Timestamp getStartTime() {
		return startTime;
	}

    @Override
    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    private boolean checkStarted(GameNode node) {
		if(node == null) return false;
		Level level = node.getLevel();
		if(level == startLevel) {
			Game game = getGame(node);
			return game != null && game.isStarted();
		} else {
			for (GameNode.Feeder feeder: node.getFeeders()) {
				if(!feeder.isSeed()) {
					if(checkStarted((GameNode)feeder.getFeeder()))
						return true;
				}
			}
			return false;
		}
	}

	public boolean isReadyForBrackets() {
		return checkReadyForBrackets(parent.getTournamentType().getChampionshipGameNode());
	}

	private boolean checkReadyForBrackets(GameNode node) {
		if(node == null) return false;
		Level level = node.getLevel();
		if(level.getRoundNo() == startLevel.getRoundNo() - 1) { //TODO: should be startLevel.previous()
			Game game = getGame(node);
			return game != null && game.getWinner() != null;
		} else {
			for (GameNode.Feeder feeder: node.getFeeders()) {
				if(!feeder.isSeed()) {
					if(!checkReadyForBrackets((GameNode)feeder.getFeeder()))
						return false;
				}
			}
			return true;
		}
	}

	public TournamentType getTournamentType() {
		return subTournamentType;
	}

	public Game getChampionshipGame() {
		return parent.getChampionshipGame();
	}

	public Game getGame(GameNode node) {
		return parent.getGame(node.getIdentityNode());
	}

	public Team getTeam(Seed seed) {
		Team team = parent.getTeam(seed);
		return team != null ? team : Team.UNKNOWN;
	}

	public int getOid() {
		return id.intValue();
	}

	public boolean isComplete() {
		return parent.isComplete();
	}

	public boolean hasAllSeedsAssigned() {
		return parent.hasAllSeedsAssigned() && isReadyForBrackets();
	}

	public void setName(String name) {
		this.name = name;
	}

	public League getLeague() {
		return parent.getLeague();
	}

	public boolean mayEdit(User user) {
		return false; // no one may edit a sub tournament, you must always edit the parent
	}

	public Date getLastUpdated() {
		return parent.getLastUpdated();
	}

	public boolean mayDelete(User requestor, SingletonProvider sp) throws SQLException {
		if(requestor != null && (isAdmin(requestor) || requestor.isSiteAdmin())) {
			// no brackets created against this tournament (go to database)
			if(new TournamentBracketStatusBroker(sp, this).isUsedByBrackets()) return false;
			
			// no pools created against this tournament (go to database)
			if(new TournamentPoolStatusBroker(sp, this).isUsedByPools()) return false;
			
			// no sub tournaments against this tournament (can be determined in memory)
			Iterator<Tournament> tournaments = sp.getSingleton().getTournamentManager().getTournaments();
			while (tournaments.hasNext()) {
				Tournament tourney = tournaments.next();
				if(tourney instanceof SubTournament && ((SubTournament)tourney).getParentTournament() == this)
					return false;
			}
			return true;
		}
		return false;
	}
	
	public boolean isAdmin(User requestor) {
		return parent.isAdmin(requestor);
	}
	
	public boolean delete(User requestor, SingletonProvider sp) throws SQLException {
		if(mayDelete(requestor, sp)) {
			// remove tournament object only.  Nothing else applies to sub tournaments
			new TournamentDeleteBroker(sp, this).execute();
			return true;
		}
		return false;
	}
	
	public boolean hasTeam(Team team) {
		return parent.hasTeam(team); // not perfect, but sufficient for our purposes
	}
	
	@Override
	public Date getNextStartTime() {
		return parent.getNextStartTime();
	}
}
