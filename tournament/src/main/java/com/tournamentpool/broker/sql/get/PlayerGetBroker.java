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
 * Created on Mar 18, 2004
 */
package com.tournamentpool.broker.sql.get;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.broker.sql.PreparedStatementBroker;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Avery J. Regier
 */
public abstract class PlayerGetBroker extends PreparedStatementBroker {
	/**
	 * @param sp
	 */
    PlayerGetBroker(SingletonProvider sp) {
		super(sp);
	}
	private static boolean emailWarningGiven = false;
	protected void processResults(ResultSet set) throws SQLException {
		while(set.next()) {
			String email = null;
			try {
				email = set.getString("EMAIL");
			} catch (SQLException e) {
				if(!emailWarningGiven) {
					System.err.println("EMAIL address column not available");
					emailWarningGiven = true;
				}
			}
			sp.getSingleton().getUserManager().loadPlayer(
				set.getString("LOGIN_ID"),
				set.getInt("PLAYER_ID"),
				set.getString("NAME"),
				set.getString("PASSWORD"),
				"Y".equalsIgnoreCase(set.getString("SITE_ADMIN")),
				email
			);
		}
	}
}