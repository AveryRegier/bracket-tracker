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

import com.tournamentpool.application.SingletonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Avery J. Regier
 */
public abstract class PreparedStatementBroker extends SQLBroker {
	private static final Logger logger = LoggerFactory.getLogger(PreparedStatementBroker.class);

	/**
	 * 
	 */
    protected PreparedStatementBroker(SingletonProvider sp) {
		super(sp);
	}

	protected boolean execute(Statement statement) throws SQLException {
		try {
			PreparedStatement stmt = (PreparedStatement)statement;
			prepare(stmt);
			return stmt.execute();
		} catch(SQLException e) {
			logger.error(toString(), e);
			throw e;
		}
	}

	protected Statement createStatement(Connection conn) throws SQLException {
        return prepareStatement(conn);
	}
	
	/**
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
    PreparedStatement prepareStatement(Connection conn) throws SQLException {
		return conn.prepareStatement(getSQL());
	}

	protected abstract void prepare(PreparedStatement stmt) throws SQLException;
}
