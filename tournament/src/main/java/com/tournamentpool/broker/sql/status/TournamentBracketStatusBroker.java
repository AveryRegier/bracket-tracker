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
 * Created on Oct 1, 2004
 */
package com.tournamentpool.broker.sql.status;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.broker.sql.PreparedStatementBroker;
import com.tournamentpool.domain.Tournament;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Avery J. Regier
 */
public class TournamentBracketStatusBroker extends PreparedStatementBroker {

	private final Tournament tournament;
	private boolean usedByBrackets;

	/**
	 * @param sp
	 */
	public TournamentBracketStatusBroker(SingletonProvider sp, Tournament tournament) {
		super(sp);
		this.tournament = tournament;
	}

	protected String getSQLKey() {
		return "TournamentBracketStatusSQL";
	}

	protected void prepare(PreparedStatement stmt) throws SQLException {
		stmt.setInt(1, tournament.getOid());
	}

	protected void processResults(ResultSet set) throws SQLException {
		if(set.next()) {
			usedByBrackets = set.getInt("BRACKET_COUNT") > 0;
		}
	}
	
	public boolean isUsedByBrackets() {
		execute();
		return usedByBrackets;
	}
	
}
