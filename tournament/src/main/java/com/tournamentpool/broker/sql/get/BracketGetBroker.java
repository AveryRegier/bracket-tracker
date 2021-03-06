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
 * Created on Apr 4, 2004
 */
package com.tournamentpool.broker.sql.get;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.domain.Bracket;

/**
 * @author Avery J. Regier
 */
public class BracketGetBroker extends BaseBracketGetBroker {
	private final int bracketOID;

	/**
	 * @param sp
	 */
	public BracketGetBroker(SingletonProvider sp, int bracketOID) {
		super(sp);
		this.bracketOID = bracketOID;
	}

	protected String getSQLKey() {
		return "BracketGetSQL";
	}


	protected void prepare(PreparedStatement stmt) throws SQLException {
		stmt.setInt(1, bracketOID);
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.broker.sql.get.BaseBracketGetBroker#loadBracket(com.tournamentpool.domain.Bracket)
	 */
	protected void loadBracket(Bracket bracket) {
		
	}
}
