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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.broker.sql.TransactionBroker;
import com.tournamentpool.domain.Seed;
import com.tournamentpool.domain.Team;
import com.tournamentpool.domain.Tournament;

/**
 * @author Avery J. Regier
 */
public class SeedTeamInsertBroker extends TransactionBroker {
	private final Tournament tournament;
	public class MultipleSeedTeamQuery extends MultipleQuery {
		private Map<Seed,Team> map;
		private Iterator<Seed> queries;
		public MultipleSeedTeamQuery(String key, Map<Seed, Team> queries) {
			super(key);
			this.queries = queries.keySet().iterator();
			this.map = queries;
		}
		public boolean hasMore() {
			return queries.hasNext();
		}
		protected void prepare(PreparedStatement stmt) throws SQLException {
			Seed seed = queries.next();
			prepare(stmt, seed, map.get(seed));
		}
		protected void prepare(PreparedStatement stmt, Seed seed, Team team) throws SQLException {
			stmt.setInt(1, team.getTeamOID());
			stmt.setInt(2, tournament.getOid());
			stmt.setInt(3, seed.getOid());
		}
	}
	
	public class MultipleSeedDeleteQuery extends MultipleQuery {
		private Iterator<Seed> queries;
		public MultipleSeedDeleteQuery(String key, Iterator<Seed> queries) {
			super(key);
			this.queries = queries;
		}
		public boolean hasMore() {
			return queries.hasNext();
		}
		protected void prepare(PreparedStatement stmt) throws SQLException {
			Seed seed = queries.next();
			prepare(stmt, seed);
		}
		protected void prepare(PreparedStatement stmt, Seed seed) throws SQLException {
			stmt.setInt(1, tournament.getOid());
			stmt.setInt(2, seed.getOid());
		}
	}

	/**
	 * @param sp
	 */
	public SeedTeamInsertBroker(SingletonProvider sp, Tournament tournament, Map<Seed, Team> inserts, Map<Seed, Team> updates, Set<Seed> deletes) {
		super(sp);
		this.tournament = tournament;
		if(!inserts.isEmpty()) {
			addQuery(new MultipleSeedTeamQuery("SeedTeamInsertSQL", inserts));
		}
		if(!updates.isEmpty()) {
			addQuery(new MultipleSeedTeamQuery("SeedTeamUpdateSQL", updates));
		}
		if(!deletes.isEmpty()) {
			addQuery(new MultipleSeedDeleteQuery("SeedTeamDeleteSQL", deletes.iterator()));
		}
	}

	protected boolean hasMore() {
		return ((MultipleQuery)current).hasMore();
	}
}
