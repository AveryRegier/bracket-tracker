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
 * Created on Mar 23, 2004
 */
package com.tournamentpool.broker.sql.get;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.broker.sql.PreparedStatementBroker;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Avery J. Regier
 */
public class GroupPoolsGetBroker extends PreparedStatementBroker {
	private final int groupOID;

	/**
	 * @param sp
	 */
	public GroupPoolsGetBroker(SingletonProvider sp, int groupOID) {
		super(sp);
		this.groupOID = groupOID;
	}

	protected String getSQLKey() {
		return "GroupPoolsGetSQL";
	}

	protected void prepare(PreparedStatement stmt) throws SQLException {
		stmt.setInt(1, groupOID);
	}

	protected void processResults(ResultSet set) throws SQLException {
		while(set.next()) {
			sp.getSingleton().getUserManager().associatePoolsToGroup(
				groupOID, set.getInt("POOL_ID"));
		}
	}
}
