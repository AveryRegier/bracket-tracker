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

/*
 * Created on Oct 2, 2004
 */
package com.tournamentpool.broker.sql.insert;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.broker.sql.InsertBroker;
import com.tournamentpool.domain.League;
import com.tournamentpool.domain.Team;

/**
 * @author Avery J. Regier
 */
public class LeagueTeamInsertBroker extends InsertBroker {

	private final League league;
	private final Team team;

	public LeagueTeamInsertBroker(SingletonProvider sp, League league, Team team) {
		super(sp);
		this.league = league;
		this.team = team;
	}

	protected void prepare(PreparedStatement stmt) throws SQLException {
		stmt.setInt(1, league.getLeagueID());
		stmt.setInt(2, team.getTeamOID());
	}

	protected String getSQLKey() {
		return "LeagueTeamInsertSQL";
	}

	protected void processUpdateCount(int updateCount) throws SQLException {
		if(updateCount == 1) {
			sp.getSingleton().getTeamManager().loadLeagueTeamAssociation(
					league.getLeagueID(), team.getTeamOID());
		}
	}
}
