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

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.broker.sql.LoadBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * @author Avery J. Regier
 */
public class TournamentLoadBroker extends LoadBroker {
	private static final Logger logger = LoggerFactory.getLogger(TournamentLoadBroker.class);
	/**
	 * @param sp
	 */
	public TournamentLoadBroker(SingletonProvider sp) {
		super(sp);
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.broker.sql.SQLBroker#getSQLKey()
	 */
	protected String getSQLKey() {
		return "TournamentLoadSQL";
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.broker.sql.SQLBroker#processResults(java.sql.ResultSet)
	 */
	protected void processResults(ResultSet set) throws SQLException {
		boolean messageSent = false;
		while(set.next()) {
			int tournamentOID = set.getInt("TOURNAMENT_ID");
			String name = set.getString("NAME");
			int tournamentTypeOID = set.getInt("TOURNAMENT_TYPE_ID");
			int leagueOID = set.getInt("LEAGUE_ID");
			Timestamp startTime = set.getTimestamp("START_TIME");
			
//			PARENT_TOURNAMENT_ID INTEGER,
//			START_LEVEL_ID INTEGER,
			int parentTournamentOID = -1;
			int startLevelOID = -1;
			try {
				parentTournamentOID = set.getInt("PARENT_TOURNAMENT_ID");
				startLevelOID = set.getInt("START_LEVEL_ID");
			} catch (SQLException e) {
				if(!messageSent) {
					logger.warn("Database not upgraded to support sub tournaments");
					messageSent = true;
				}
			}

			sp.getSingleton().getTournamentManager().loadTournament(
				tournamentOID, name, tournamentTypeOID, leagueOID, startTime, parentTournamentOID, startLevelOID
			);
		}
	}
}
