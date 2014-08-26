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
 * Created on Oct 16, 2004
 */
package com.tournamentpool.broker.sql;

import com.tournamentpool.application.SingletonProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Avery J. Regier
 */
public abstract class InsertBroker extends PreparedStatementBroker {

	/**
	 * @param sp
	 */
    protected InsertBroker(SingletonProvider sp) {
		super(sp);
	}

	protected PreparedStatement prepareStatement(Connection conn) throws SQLException {
		return conn.prepareStatement(getSQL(), Statement.RETURN_GENERATED_KEYS);
	}

	protected void execute(Connection conn) throws SQLException {
		executeInsertQuery(conn);
	}
}
