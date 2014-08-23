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
 * Created on Mar 13, 2004
 */
package com.tournamentpool.broker.sql;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.application.SingletonProviderHolder;

import java.sql.*;
import java.util.Properties;

/**
 * @author RX39789
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class SQLBroker extends SingletonProviderHolder {
	/**
	 * 
	 */
	public SQLBroker(SingletonProvider sp) {
		super(sp);
	}

	public void execute() {
        Properties config = sp.getSingleton().getConfig();
        boolean ok = false;
        try (Connection conn = DriverManager.getConnection(
				config.getProperty("jdbcURL"),
				config.getProperty("userid"),
				config.getProperty("password"))) {
			execute(conn);
            ok = true;
		} catch (SQLException e) {
            if(!ok) throw new DatabaseFailure(e);
        }
	}

	private static boolean loaded = false;
	public static synchronized void loadDriver(Properties config) throws ClassNotFoundException {
		if(!loaded) {
			Class.forName(config.getProperty("jdbcDriver"));
			loaded = true;
		}
	}

	/**
	 * @param conn
	 */
	protected void execute(Connection conn) throws SQLException {
		executeSimpleQuery(conn);
	}

	/**
	 * @param conn
	 */
	protected void executeSimpleQuery(Connection conn) throws SQLException {
		Statement statement = createStatement(conn);
		while(hasMore()) {
			boolean results = execute(statement);
			if(results) {
				do {
					processResults(statement.getResultSet());
				} while(statement.getMoreResults());
			} else {
				processUpdateCount(statement.getUpdateCount());
			}
		}
	}

	private boolean once = true;
	/**
	 * @return
	 */
	protected boolean hasMore() {
		boolean temp = once;
		once = false;
		return temp;
	}
	
	protected void reset() {
		this.once = true;
	}

	protected void executeInsertQuery(Connection conn) throws SQLException {
		Statement statement = createStatement(conn);
		boolean results = execute(statement);
		ResultSet generated = statement.getGeneratedKeys();
		if(generated != null) {
			processGeneratedKeys(generated);
		}
		if(results) {
			do {
				processResults(statement.getResultSet());
			} while(statement.getMoreResults());
		} else {
			processUpdateCount(statement.getUpdateCount());
		}
	}

	protected boolean execute(Statement statement) throws SQLException {
		return statement.execute(getSQL());
	}

	protected Statement createStatement(Connection conn) throws SQLException {
		return conn.createStatement();
	}

	/**
	 * @param updateCount
	 */
	protected void processUpdateCount(int updateCount) throws SQLException {}

	/**
	 * @param set
	 */
	protected void processResults(ResultSet set) throws SQLException {}

	/**
	 * @param generated
	 */
	protected void processGeneratedKeys(ResultSet generated) throws SQLException {}

	/**
	 * @return
	 */
	protected String getSQL() {
		return sp.getSingleton().getConfig().getProperty(getSQLKey());
	}

	/**
	 * @return
	 */
	protected abstract String getSQLKey();
	
	public String toString() {
		return getSQLKey();
	}
}