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
import com.tournamentpool.domain.Group;
import com.tournamentpool.domain.ScoreSystem;
import com.tournamentpool.domain.TieBreakerType;
import com.tournamentpool.domain.Tournament;

/**
 * @author Avery J. Regier
 */
public class PoolInsertBroker extends InsertBroker {

	private final String name;
	private final Group group;
	private final Tournament tournament;
	private final ScoreSystem scoreSystem;
	private final int bracketLimit;
	private final boolean showBracketsEarly;
	private final TieBreakerType tieBreakerType;
	private final String tieBreakerQuestion;

	/**
	 * @param sp
	 * @param scoreSystem
	 * @param tournament
	 * @param group
	 * @param name
	 * @param showBracketsEarly
	 * @param bracketLimit
	 */
	public PoolInsertBroker(SingletonProvider sp, String name, Group group,
			Tournament tournament, ScoreSystem scoreSystem,
			int bracketLimit, boolean showBracketsEarly,
			TieBreakerType tieBreakerType, String tieBreakerQuestion)
	{
		super(sp);
		this.name = name;
		this.group = group;
		this.tournament = tournament;
		this.scoreSystem = scoreSystem;
		this.bracketLimit = bracketLimit;
		this.showBracketsEarly = showBracketsEarly;
		this.tieBreakerType = tieBreakerType;
		this.tieBreakerQuestion = tieBreakerQuestion;
	}

	protected void prepare(PreparedStatement stmt) throws SQLException {
		stmt.setString(1, name);
		stmt.setInt(2, group.getId());
		stmt.setInt(3, scoreSystem.getOid());
		stmt.setInt(4, tournament.getOid());
		stmt.setInt(5, bracketLimit);
		stmt.setString(6, showBracketsEarly ? "Y" : "N");
		stmt.setInt(7, tieBreakerType.getOid());
		stmt.setString(8, tieBreakerQuestion);
	}

	protected String getSQLKey() {
		return "PoolInsertSQL";
	}


	protected void processGeneratedKeys(ResultSet generated)
			throws SQLException
	{
		while(generated.next()) {
			sp.getSingleton().getUserManager().loadPool(
				generated.getInt(1),
				name,
				group.getId(),
				scoreSystem.getOid(),
				tournament.getOid(),
				bracketLimit,
				showBracketsEarly,
				tieBreakerType.getOid(),
				tieBreakerQuestion,
				null
			);
			sp.getSingleton().getUserManager().associatePoolsToGroup(
				group.getId(),
				generated.getInt(1)
			);
		}
	}
}
