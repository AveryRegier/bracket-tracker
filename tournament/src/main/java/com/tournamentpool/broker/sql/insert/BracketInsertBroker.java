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
import com.tournamentpool.domain.Bracket;
import com.tournamentpool.domain.Tournament;
import com.tournamentpool.domain.User;

/**
 * @author Avery J. Regier
 */
public class BracketInsertBroker extends InsertBroker {

	private final String name;
	private final User user;
	private final Tournament tournament;
	private Bracket bracket;

	/**
	 * @param sp
	 * @param tournament
	 * @param name
	 */
	public BracketInsertBroker(SingletonProvider sp, String name, User user, Tournament tournament) {
		super(sp);
		this.name = name;
		this.tournament = tournament;
		this.user = user;
	}

	protected void prepare(PreparedStatement stmt) throws SQLException {
		stmt.setInt(1, user.getOID());
		stmt.setInt(2, tournament.getOid());
		stmt.setString(3, name);
	}

	protected String getSQLKey() {
		return "BracketInsertSQL";
	}

	
	protected void processGeneratedKeys(ResultSet generated)
			throws SQLException 
	{
		while(generated.next()) {
			bracket = sp.getSingleton().getBracketManager().loadBracket(
				generated.getInt(1),
				tournament.getOid(),
				user.getOID(),
				name
			);
			user.loadBracket(bracket);
		}
	}
	
	/**
	 * @return Returns the bracket.
	 */
	public Bracket getBracket() {
		return bracket;
	}
}
