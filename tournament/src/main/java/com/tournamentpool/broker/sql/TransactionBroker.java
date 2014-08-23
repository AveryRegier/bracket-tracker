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
package com.tournamentpool.broker.sql;

import com.tournamentpool.application.SingletonProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @author Avery J. Regier
 */
public class TransactionBroker<Q extends TransactionBroker.Query> extends PreparedStatementBroker {
	public abstract static class Query {
		private String key;
		public Query(String key) {
			this.key = key;
		}
		protected void prepare(PreparedStatement stmt) throws SQLException {}
		protected String getSQLKey() {
			return key;
		}
	}

	public abstract static class MultipleQuery extends Query {
		public MultipleQuery(String key) {
			super(key);
		}
		public abstract boolean hasMore();
	}

	private List<Q> queries = new LinkedList<>();
	protected Optional<Q> current = Optional.empty();

	/**
	 * @param sp
	 */
	public TransactionBroker(SingletonProvider sp) {
		super(sp);
	}

	protected void addQuery(Q query) {
		queries.add(query);
	}

	protected boolean hasMoreQueries() {
		if(queries.isEmpty()) {
			current = Optional.empty();
		} else {
			current = Optional.of(queries.remove(0));
			reset();
		}
		return current.isPresent();
	}

	protected void execute(Connection conn) throws SQLException {
		try {
			conn.setAutoCommit(false);
			while(hasMoreQueries()) {
				super.execute(conn);
			}
			conn.commit();
		} catch(SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}

	protected void prepare(PreparedStatement stmt) throws SQLException {
		current.get().prepare(stmt);
	}

	protected String getSQLKey() {
		return current.map(c->c.getSQLKey()).orElse("");
	}
}
