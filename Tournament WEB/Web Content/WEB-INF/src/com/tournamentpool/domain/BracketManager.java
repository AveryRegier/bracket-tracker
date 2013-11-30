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
package com.tournamentpool.domain;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.application.SingletonProviderHolder;
import com.tournamentpool.broker.sql.get.BracketGetBroker;
import com.tournamentpool.broker.sql.insert.BracketInsertBroker;
import com.tournamentpool.broker.sql.update.BracketUpdateBroker;

/**
 * @author Avery J. Regier
 */
public class BracketManager extends SingletonProviderHolder {
	private Map<Integer, Bracket> brackets = new HashMap<Integer, Bracket>();

	/**
	 * @param sp
	 */
	public BracketManager(SingletonProvider sp) {
		super(sp);
	}
	
	public Bracket loadBracket(
		int bracketOID, int tournamentOID, int playerOID, String name) throws SQLException 
	{
		Bracket bracket = getCachedBracket(bracketOID);
		if(bracket == null) { 
			Tournament tournament = sp.getSingleton().getTournamentManager().getTournament(tournamentOID);
			User user = sp.getSingleton().getUserManager().getUserObject(playerOID);
			bracket = new Bracket(bracketOID, user, tournament, name);
			brackets.put(new Integer(bracketOID), bracket);
		}
		return bracket;
	}
	
	public Bracket getCachedBracket(int bracketOID) {
		return brackets.get(new Integer(bracketOID));
	}

	/**
	 * @param bracketOID
	 * @return
	 */
	public Bracket getBracket(int bracketOID) throws SQLException {
		Bracket bracket = getCachedBracket(bracketOID);
		if(bracket == null) {
			new BracketGetBroker(sp, bracketOID).execute();
			bracket = getCachedBracket(bracketOID);
		}
		return bracket;
	}

	/**
	 * @param user
	 * @param tournament
	 * @param name
	 * @return
	 * @throws SQLException
	 */
	public Bracket createBracket(User user, Tournament tournament, String name) throws SQLException {
		BracketInsertBroker broker = new BracketInsertBroker(sp, name, user, tournament);
		broker.execute();
		return broker.getBracket();
	}

	public void updateBracket(Bracket bracket, String name) throws SQLException {
		BracketUpdateBroker broker = new BracketUpdateBroker(sp, bracket, name);
		broker.execute();
	}
}
