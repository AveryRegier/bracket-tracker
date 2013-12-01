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
 * Created on Oct 2, 2004
 */
package com.tournamentpool.broker.sql.insert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.broker.sql.InsertBroker;

/**
 * @author Avery J. Regier
 */
public class PlayerInsertBroker extends InsertBroker {

	private final String userID;
	private final String password;
	private final String name;
	private final String email;

	public PlayerInsertBroker(SingletonProvider sp, String userID, String password, String name, String email) {
		super(sp);
		this.userID = userID;
		this.password = password;
		this.name = name;
		this.email = email;
	}

	protected void prepare(PreparedStatement stmt) throws SQLException {
		stmt.setString(1, name);
		stmt.setString(2, userID);
		stmt.setString(3, password);
		try {
			stmt.setString(4, email);
		} catch (SQLException e) {
			// email not in the database or not in the SQL statement
		}
	}

	protected String getSQLKey() {
		return "PlayerInsertSQL";
	}


	protected void processGeneratedKeys(ResultSet generated)
			throws SQLException
	{
		while(generated.next()) {
			sp.getSingleton().getUserManager().loadPlayer(
				userID,
				generated.getInt(1),
				name,
				password,
				false,
				email
			);
		}
	}
}
