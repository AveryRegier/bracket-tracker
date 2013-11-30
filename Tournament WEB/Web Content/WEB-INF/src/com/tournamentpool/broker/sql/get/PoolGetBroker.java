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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.broker.sql.PreparedStatementBroker;

/**
 * @author Avery J. Regier
 */
public class PoolGetBroker extends PreparedStatementBroker {
	private final int poolOID;
	/**
	 * @param sp
	 * @param poolOID
	 */
	public PoolGetBroker(SingletonProvider sp, int poolOID) {
		super(sp);
		this.poolOID = poolOID;
	}

	protected String getSQLKey() {
		return "PoolGetSQL";
	}

	protected void prepare(PreparedStatement stmt) throws SQLException {
		stmt.setInt(1, poolOID);
	}

	protected void processResults(ResultSet set) throws SQLException {
		while(set.next()) {
			int poolOID = set.getInt("POOL_ID");
			String name = set.getString("NAME");
			int groupOID = set.getInt("GROUP_ID");
			int scoreSystemOID = set.getInt("SCORE_SYSTEM_ID");
			int tournamentOID = set.getInt("TOURNAMENT_ID");
			int bracketLimit = set.getInt("BRACKET_LIMIT");
			int tieBreakerTypeID = set.getInt("TIE_BREAKER_TYPE_ID");
			String tieBreakerQuestion = set.getString("TIE_BREAKER_QUESTION");
			String tieBreakerAnswer = set.getString("TIE_BREAKER_ANSWER");
			boolean showBracketsEarly = "Y".equalsIgnoreCase(set.getString("SHOW_EARLY"));
			sp.getSingleton().getUserManager().loadPool(
					poolOID, name, groupOID, scoreSystemOID, tournamentOID,
					bracketLimit, showBracketsEarly, tieBreakerTypeID,
					tieBreakerQuestion, tieBreakerAnswer);
		}
	}
}
