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

package com.tournamentpool.broker.sql.update;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.broker.sql.InsertBroker;
import com.tournamentpool.domain.User;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProfileUpdateBroker extends InsertBroker {

    private final String userName;
    private final String name;
    private final String email;
    private final int playerID;

	public ProfileUpdateBroker(SingletonProvider sp, int playerID, String userName, String name, String email) {
		super(sp);
		this.playerID = playerID;
        this.userName = userName;
        this.name = name;
        this.email = email;
    }

	protected void prepare(PreparedStatement stmt) throws SQLException {
        stmt.setString(1, userName);
		stmt.setInt(2, playerID);
		stmt.setString(3, name);
        stmt.setString(4, email);
	}

	protected String getSQLKey() {
		return "ProfileUpdateSQL";
	}

	protected void processUpdateCount(int updateCount) throws SQLException {
		if(updateCount == 1) {
            User user = sp.getSingleton().getUserManager().getUser(playerID);
            user.setEmail(email);
            user.setName(name);
            user.setID(userName);
		} else {
            throw new SQLException("Chosen user id is already in use.");
        }
	}
}
