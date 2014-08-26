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
package com.tournamentpool.broker.sql.delete;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.broker.sql.InsertBroker;
import com.tournamentpool.domain.MainTournament;

/**
 * @author Avery J. Regier
 */
public class TournamentAdminDeleteBroker extends InsertBroker {

	private final MainTournament tournament;
	private final int adminOID;

	/**
	 * @param sp
	 * @param tournament
     */
	public TournamentAdminDeleteBroker(SingletonProvider sp, MainTournament tournament, int adminOID) {
		super(sp);
		this.tournament = tournament;
		this.adminOID = adminOID;
	}

	protected void prepare(PreparedStatement stmt) throws SQLException {
		stmt.setInt(1, tournament.getOid());
		stmt.setInt(2, adminOID);
	}

	protected String getSQLKey() {
		return "TournamentAdminDeleteSQL";
	}

	protected void processUpdateCount(int updateCount) throws SQLException {
		if(updateCount == 1) {
			tournament.removeAdmin(adminOID);
		}
	}
}
