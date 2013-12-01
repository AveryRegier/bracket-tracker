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
package com.tournamentpool.broker.sql.load;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.broker.sql.LoadBroker;

/**
 * @author Avery J. Regier
 */
public class GameFeederTypeLoadBroker extends LoadBroker {

	/**
	 * @param sp
	 */
	public GameFeederTypeLoadBroker(SingletonProvider sp) {
		super(sp);
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.broker.sql.SQLBroker#getSQLKey()
	 */
	protected String getSQLKey() {
		return "GameFeederTypeLoadSQL";
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.broker.sql.SQLBroker#processResults(java.sql.ResultSet)
	 */
	protected void processResults(ResultSet set) throws SQLException {
		while(set.next()) {
			int gameFeederTypeOID = set.getInt("GAME_FEEDER_TYPE_ID");
			String name = set.getString("NAME");
			sp.getSingleton().getTournamentTypeManager().loadGameFeederType(gameFeederTypeOID, name);
		}
	}
}
