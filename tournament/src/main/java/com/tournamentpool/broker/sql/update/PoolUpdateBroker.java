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
package com.tournamentpool.broker.sql.update;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.broker.sql.InsertBroker;
import com.tournamentpool.domain.Pool;
import com.tournamentpool.domain.ScoreSystem;
import com.tournamentpool.domain.TieBreakerType;
import com.tournamentpool.domain.Tournament;

/**
 * @author Avery J. Regier
 */
public class PoolUpdateBroker extends InsertBroker {

	private final String name;
	private final Tournament tournament;
	private final ScoreSystem scoreSystem;
	private final Pool pool;
	private final int bracketLimit;
	private final boolean showBracketsEarly;
	private final TieBreakerType tieBreakerType;
	private final String tieBreakerQuestion;

	/**
	 * @param sp
	 * @param scoreSystem
	 * @param tournament
	 * @param name
	 * @param showBracketsEarly
	 * @param bracketLimit
	 * @param tieBreakerQuestion
	 * @param tieBreakerType
	 */
	public PoolUpdateBroker(SingletonProvider sp, Pool pool, String name, Tournament tournament,
			ScoreSystem scoreSystem, int bracketLimit, boolean showBracketsEarly,
			TieBreakerType tieBreakerType, String tieBreakerQuestion)
	{
		super(sp);
		this.pool = pool;
		this.name = name;
		this.tournament = tournament;
		this.scoreSystem = scoreSystem;
		this.bracketLimit = bracketLimit;
		this.showBracketsEarly = showBracketsEarly;
		this.tieBreakerType = tieBreakerType;
		this.tieBreakerQuestion = tieBreakerQuestion;
	}

	protected void prepare(PreparedStatement stmt) throws SQLException {
		stmt.setString(1, name);
		stmt.setInt(2, scoreSystem.getOid());
		stmt.setInt(3, tournament.getOid());
		stmt.setInt(4, bracketLimit);
		stmt.setString(5, showBracketsEarly ? "Y" : "N");
		stmt.setInt(6, tieBreakerType.getOid());
		stmt.setString(7, tieBreakerQuestion);
		stmt.setInt(8, pool.getOid());
	}

	protected String getSQLKey() {
		return "PoolUpdateSQL";
	}

	protected void processUpdateCount(int updateCount) throws SQLException {
		if(updateCount == 1) {
			pool.commitUpdate(name, scoreSystem, tournament, bracketLimit,
					showBracketsEarly, tieBreakerType, tieBreakerQuestion);
		}
	}
}
