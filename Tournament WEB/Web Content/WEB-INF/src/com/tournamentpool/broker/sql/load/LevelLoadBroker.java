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
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.tournamentpool.broker.sql.load;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.broker.sql.LoadBroker;

/**
 * @author RX39789
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class LevelLoadBroker extends LoadBroker {

	/**
	 * @param sp
	 */
	public LevelLoadBroker(SingletonProvider sp) {
		super(sp);
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.broker.sql.SQLBroker#getSQL()
	 */
	protected String getSQLKey() {
		return "LevelLoadSQL";
	}
	
	/* (non-Javadoc)
	 * @see com.tournamentpool.broker.sql.SQLBroker#processResults(java.sql.ResultSet)
	 */
	protected void processResults(ResultSet set) throws SQLException {
		while(set.next()) {
			int levelOID = set.getInt("LEVEL_ID");
			String name = set.getString("NAME");
			int roundNo = set.getInt("ROUND_NO");
			sp.getSingleton().getScoreSystemManager().loadLevel(
				levelOID, name, roundNo
			);
		}
	}
}
