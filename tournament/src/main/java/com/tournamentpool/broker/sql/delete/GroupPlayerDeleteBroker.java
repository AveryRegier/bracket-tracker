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
package com.tournamentpool.broker.sql.delete;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.broker.sql.TransactionBroker;
import com.tournamentpool.domain.Group;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Avery J. Regier
 */
public class GroupPlayerDeleteBroker extends TransactionBroker<TransactionBroker.Query> {
	private final Group group;
	private final int[] playerIDs;
	private int count = 0;
	
	class GroupPlayerDeleteQuery extends Query {

		public GroupPlayerDeleteQuery() {
			super("GroupPlayerDeleteSQL");
		}
		
		protected void prepare(PreparedStatement stmt) throws SQLException {
			stmt.setInt(1, group.getId());
			stmt.setInt(2, playerIDs[count]);
		}
		
		
	}

	public GroupPlayerDeleteBroker(SingletonProvider sp, Group group, int[] playerIDs) {
		super(sp);
		this.group = group;
		this.playerIDs = playerIDs;
		addQuery(new GroupPlayerDeleteQuery());
	}

//	protected void prepare(PreparedStatement stmt) throws SQLException {
//	}

	protected String getSQLKey() {
		return "GroupPlayerDeleteSQL";
	}

	protected void processUpdateCount(int updateCount) throws SQLException {
		if(updateCount == 1) {
			count++;
		} else {
			throw new SQLException("Update count may only be 1:"+updateCount);
		}
	}
	
	protected boolean hasMore() {
		return count < playerIDs.length;
	}
}
