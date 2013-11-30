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
 * Created on Mar 22, 2004
 */
package com.tournamentpool.broker.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import com.tournamentpool.application.SingletonProvider;

/**
 * @author Avery J. Regier
 */
public abstract class PreparedStatementBroker extends SQLBroker {

	/**
	 * 
	 */
	public PreparedStatementBroker(SingletonProvider sp) {
		super(sp);
	}

	protected boolean execute(Statement statement) throws SQLException {
		try {
			PreparedStatement stmt = (PreparedStatement)statement;
			prepare(stmt);
			return stmt.execute();
		} catch(SQLException e) {
			System.err.println(toString());
			throw e;
		}
	}

	protected Statement createStatement(Connection conn) throws SQLException {
		PreparedStatement stmt = prepareStatement(conn);
	//	prepare(stmt);
		return stmt;
	}
	
	/**
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	protected PreparedStatement prepareStatement(Connection conn) throws SQLException {
		return conn.prepareStatement(getSQL());
	}

	protected abstract void prepare(PreparedStatement stmt) throws SQLException;
}
