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
import com.tournamentpool.domain.Bracket;
import com.tournamentpool.domain.Pool;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Avery J. Regier
 */
public class BracketPoolGetBroker extends PreparedStatementBroker {
	private final int poolOID;

	/**
	 * @param sp
	 */
	public BracketPoolGetBroker(SingletonProvider sp, int poolOID) {
		super(sp);
		this.poolOID = poolOID;
	}

	protected String getSQLKey() {
		return "BracketPoolGetSQL";
	}

	protected void prepare(PreparedStatement stmt) throws SQLException {
		stmt.setInt(1, poolOID);
	}

	protected void processResults(ResultSet set) throws SQLException {
		Pool pool = sp.getSingleton().getUserManager().getCachedPool(poolOID);
		while(set.next()) {
			int bracketOID = set.getInt("BRACKET_ID");
			String tieBreakerAnswer = set.getString("TIE_BREAKER_ANSWER");
			Bracket bracket = sp.getSingleton().getBracketManager().getBracket(bracketOID);
			pool.loadBracket(bracket, tieBreakerAnswer);
		}
	}
}
