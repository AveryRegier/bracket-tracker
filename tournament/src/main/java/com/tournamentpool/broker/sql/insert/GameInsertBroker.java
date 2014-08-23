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
import com.tournamentpool.domain.Game;
import com.tournamentpool.domain.GameReporter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Avery J. Regier
 */
public class GameInsertBroker extends TransactionBroker<TransactionBroker.MultipleQuery> implements GameReporter {
	private List<Game> insertedGames = new LinkedList<>();
	private List<Game> updatedGames = new LinkedList<>();
	private List<Game> deletedGames = new LinkedList<>();

	public class MultipleGameQuery extends MultipleQuery {
		private Iterator<Game> queries;
		public MultipleGameQuery(String key, Iterator<Game> queries) {
			super(key);
			this.queries = queries;
		}
		public boolean hasMore() {
			return queries.hasNext();
		}
		protected void prepare(PreparedStatement stmt) throws SQLException {
			prepare(stmt, queries.next());
		}
		protected void prepare(PreparedStatement stmt, Game pick) throws SQLException {
			if(pick.getWinner().isPresent()) {
				stmt.setInt(1, pick.getWinner().get().getOid());
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			if(pick.getDate() != null) {
				stmt.setTimestamp(2, new Timestamp(pick.getDate().getTime()));
			} else {
				stmt.setNull(2, Types.TIMESTAMP);
			}
			stmt.setInt(3, pick.getTournament().getOid());
			stmt.setInt(4, pick.getGameNode().getOid());
		}
	}
	
	/**
	 * @param sp
	 */
	public GameInsertBroker(SingletonProvider sp) {
		super(sp);
	}

	private void addQueries(List<Game> inserts, List<Game> updates,
			List<Game> deletes) {
		if(!inserts.isEmpty()) {
			addQuery(new MultipleGameQuery("GameInsertSQL", inserts.iterator()));
		}
		if(!updates.isEmpty()) {
			addQuery(new MultipleGameQuery("GameUpdateSQL", updates.iterator()));
		}
		if(!deletes.isEmpty()) {
			addQuery(new MultipleGameQuery("GameDeleteSQL", deletes.iterator()) {
				protected void prepare(PreparedStatement stmt, Game game) throws SQLException {
					stmt.setInt(1, game.getTournament().getOid());
					stmt.setInt(2, game.getGameNode().getOid());
				}
			});
		}
	}

	protected boolean hasMore() {
		return current.map(c->c.hasMore()).orElse(false);
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.broker.sql.insert.GameReporter#getGameReport()
	 */
	@Override
	public List<Game> getGameReport() {
		List<Game> reportedGames = new ArrayList<Game>();
		reportedGames.addAll(updatedGames);
		reportedGames.addAll(insertedGames);
		return reportedGames;
	}
	/* (non-Javadoc)
	 * @see com.tournamentpool.broker.sql.insert.GameReporter#updateGame(com.tournamentpool.domain.Game)
	 */
	@Override
	public void updateGame(Game game) {
		if(game.isNew()) {
			if(game.hasInformation()) {
				insertedGames.add(game);
			}
		} else {
			if(game.hasInformation()) {
				updatedGames.add(game);
			} else {
				deletedGames.add(game);
			}
		}
	}
	
	@Override
	public void execute() {
		addQueries(insertedGames, updatedGames, deletedGames);
		super.execute();
	}

}
