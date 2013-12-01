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
 * Created on Mar 15, 2005
 */
package com.tournamentpool.broker.sql.update;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.broker.sql.PreparedStatementBroker;
import com.tournamentpool.domain.Tournament;

public class TournamentUpdateBroker extends PreparedStatementBroker {

	private final Tournament tournament;
	private final String name;

	public TournamentUpdateBroker(SingletonProvider sp, Tournament tournament, String name) {
		super(sp);
		this.tournament = tournament;
		this.name = name;
	}

	protected String getSQLKey() {
		return "TournamentUpdateSQL";
	}

	protected void prepare(PreparedStatement stmt) throws SQLException {
		stmt.setString(1, name);
		stmt.setInt(2, tournament.getOid());
	}
	
	protected void processUpdateCount(int updateCount) throws SQLException {
		if(updateCount == 1) {
			tournament.setName(name);
		}
	}
}
