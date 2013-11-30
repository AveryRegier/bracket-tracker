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

package com.tournamentpool.broker.sql.insert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.broker.sql.InsertBroker;
import com.tournamentpool.domain.Level;
import com.tournamentpool.domain.Tournament;

public class SubTournamentInsertBroker extends InsertBroker {
	private int oid;
	private final String name;
	private final Tournament parent;
	private final Level startLevel;
	private final Timestamp startTime;

	public SubTournamentInsertBroker(SingletonProvider sp, String name, Tournament parent, Level startLevel, Date startTime) {
		super(sp);
		this.name = name;
		this.parent = parent;
		this.startLevel = startLevel;
		this.startTime = new Timestamp(startTime.getTime());
	}

	protected void prepare(PreparedStatement stmt) throws SQLException {
		stmt.setString(1, name);
		stmt.setInt(2, parent.getOid());
		stmt.setInt(3, startLevel.getOid());
		stmt.setTimestamp(4, startTime);
	}

	protected String getSQLKey() {
		return "SubTournamentInsertSQL";
	}

	protected void processGeneratedKeys(ResultSet generated) throws SQLException {
		while(generated.next()) {
			oid = generated.getInt(1);
			sp.getSingleton().getTournamentManager().loadTournament(
				oid, name, -1, -1,
				startTime, parent.getOid(), startLevel.getOid());
		}
	}

	public int getGeneratedOID() {
		return oid;
	}
}
