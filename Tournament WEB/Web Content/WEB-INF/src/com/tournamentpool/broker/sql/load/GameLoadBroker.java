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
import java.sql.Timestamp;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.broker.sql.LoadBroker;

/**
 * @author Avery J. Regier
 */
public class GameLoadBroker extends LoadBroker {

	/**
	 * @param sp
	 */
	public GameLoadBroker(SingletonProvider sp) {
		super(sp);
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.broker.sql.SQLBroker#getSQLKey()
	 */
	protected String getSQLKey() {
		return "GameLoadSQL";
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.broker.sql.SQLBroker#processResults(java.sql.ResultSet)
	 */
	protected void processResults(ResultSet set) throws SQLException {
		while(set.next()) {
		//	int gameOID = set.getInt("GAME_ID");
			int gameNodeOID = set.getInt("GAME_NODE_ID");
			int tournamentOID = set.getInt("TOURNAMENT_ID");
			Integer winner = (Integer)set.getObject("WINNER");
			Timestamp startTime = set.getTimestamp("START_TIME");
			sp.getSingleton().getTournamentManager().loadGame(
			/*	gameOID,*/ gameNodeOID, tournamentOID, winner, startTime
			);
		}
	}

}
