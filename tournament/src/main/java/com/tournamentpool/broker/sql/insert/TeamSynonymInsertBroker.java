/* 
Copyright (C) 2014 Avery J. Regier.

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

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.broker.sql.InsertBroker;
import com.tournamentpool.domain.Team;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Avery J. Regier
 */
public class TeamSynonymInsertBroker extends InsertBroker {

	private final Team team;
    private final String alias;

    public TeamSynonymInsertBroker(SingletonProvider sp, Team team, String alias) {
		super(sp);
		this.team = team;
        this.alias = alias;
    }

	protected void prepare(PreparedStatement stmt) throws SQLException {
		stmt.setInt(1, team.getTeamOID());
        stmt.setString(2, alias);
	}

	protected String getSQLKey() {
		return "TeamSynonymInsertSQL";
	}

	protected void processUpdateCount(int updateCount) throws SQLException {
		if(updateCount == 1) {
			sp.getSingleton().getTeamManager().loadTeamSynonym(
					team.getTeamOID(), alias);
		}
	}
}
