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
 * Created on Oct 16, 2004
 */
package com.tournamentpool.broker.sql.insert;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.broker.sql.TransactionBroker;
import com.tournamentpool.domain.Bracket;
import com.tournamentpool.domain.Bracket.Pick;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Iterator;
import java.util.List;

/**
 * @author Avery J. Regier
 */
public class PickInsertBroker extends TransactionBroker {
	public class MultiplePickQuery extends Query {
		private Iterator<Bracket.Pick> queries;
		public MultiplePickQuery(String key, Iterator<Pick> queries) {
			super(key);
			this.queries = queries;
		}
		public boolean hasMore() {
			return queries.hasNext();
		}
		protected void prepare(PreparedStatement stmt) throws SQLException {
			prepare(stmt, queries.next());
		}
		protected void prepare(PreparedStatement stmt, Bracket.Pick pick) throws SQLException {
			if(pick.getWinner().isPresent()) {
				stmt.setInt(1, pick.getWinner().get().getOid());
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			stmt.setInt(2, pick.getBracket().getOID());
			stmt.setInt(3, pick.getGameNode().getOid());
		}
	}
	
	/**
	 * @param sp
	 */
	public PickInsertBroker(SingletonProvider sp, List<Pick> inserts, List<Pick> updates, List<Pick> deletes) {
		super(sp);
		if(!inserts.isEmpty()) {
			addQuery(new MultiplePickQuery("PickInsertSQL", inserts.iterator()));
		}
		if(!updates.isEmpty()) {
			addQuery(new MultiplePickQuery("PickUpdateSQL", updates.iterator()));
		}
		if(!deletes.isEmpty()) {
			addQuery(new MultiplePickQuery("PickDeleteSQL", deletes.iterator()) {
				protected void prepare(PreparedStatement stmt, Bracket.Pick pick) throws SQLException {
					stmt.setInt(1, pick.getBracket().getOID());
					stmt.setInt(2, pick.getGameNode().getOid());
				}
			});
		}
	}

	protected boolean hasMore() {
		return ((MultiplePickQuery)current).hasMore();
	}
}
