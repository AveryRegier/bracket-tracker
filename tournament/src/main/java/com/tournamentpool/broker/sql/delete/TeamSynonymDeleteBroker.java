/* 
Copyright (C) 2003-2014 Avery J. Regier.

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
package com.tournamentpool.broker.sql.delete;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.broker.sql.PreparedStatementBroker;
import com.tournamentpool.domain.Team;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TeamSynonymDeleteBroker extends PreparedStatementBroker {

	private final Team team;
    private final String name;

    public TeamSynonymDeleteBroker(SingletonProvider sp, Team team, String name) {
		super(sp);
		this.team = team;
        this.name = name;
    }

	protected String getSQLKey() {
		return "TeamSynonymDeleteSQL";
	}

	protected void prepare(PreparedStatement stmt) throws SQLException {
		stmt.setInt(1, team.getTeamOID());
        stmt.setString(2, name);
	}
	
	protected void processUpdateCount(int updateCount) throws SQLException {
		for(int i=0; i<updateCount; i++) {
			team.removeSynonym(name);
		}
	}
}
