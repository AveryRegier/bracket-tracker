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

/**
 * @author Avery J. Regier
 */
public class TieBreakerAnswerBroker extends InsertBroker {

	private final String tieBreakerAnswer;
	private final Pool pool;

	/**
	 * @param sp
     */
	public TieBreakerAnswerBroker(SingletonProvider sp, Pool pool, String tieBreakerAnswer)
	{
		super(sp);
		this.pool = pool;
		this.tieBreakerAnswer = tieBreakerAnswer;
	}

	protected void prepare(PreparedStatement stmt) throws SQLException {
		stmt.setString(1, tieBreakerAnswer);
		stmt.setInt(2, pool.getOid());
	}

	protected String getSQLKey() {
		return "PoolTieBreakerAnswerSQL";
	}

	protected void processUpdateCount(int updateCount) throws SQLException {
		if(updateCount == 1) {
			pool.commitTieBreakerAnswer(tieBreakerAnswer);
		}
	}
}
