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
public class GameSeedLoadBroker extends LoadBroker {

	/**
	 * @param sp
	 */
	public GameSeedLoadBroker(SingletonProvider sp) {
		super(sp);
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.broker.sql.SQLBroker#getSQLKey()
	 */
	protected String getSQLKey() {
		return "GameSeedLoadSQL";
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.broker.sql.SQLBroker#processResults(java.sql.ResultSet)
	 */
	protected void processResults(ResultSet set) throws SQLException {
		while(set.next()) {
			int gameNodeOID = set.getInt("GAME_NODE_ID");
			int seedOID = set.getInt("SEED_ID");
			int oponentOID = set.getInt("OPPONENT_ID");
			int tournamentTypeOID = set.getInt("TOURNAMENT_TYPE_ID");
			sp.getSingleton().getTournamentTypeManager().loadGameSeed(
				gameNodeOID, seedOID, oponentOID, tournamentTypeOID
			);
		}
	}

}
