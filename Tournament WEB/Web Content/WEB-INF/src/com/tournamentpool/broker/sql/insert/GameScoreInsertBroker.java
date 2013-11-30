/* 
Copyright (C) 2003-2013 Avery J. Regier.

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
import java.sql.Types;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.broker.sql.TransactionBroker;
import com.tournamentpool.domain.Game;
import com.tournamentpool.domain.Opponent;
import com.tournamentpool.domain.ScoreReporter;

/**
 * @author Avery J. Regier
 */
public class GameScoreInsertBroker extends TransactionBroker implements ScoreReporter {
	public class GameScoreQuery extends Query {
		private Game game;
		private Opponent opponent;
		private Integer score;
		public GameScoreQuery(String key, Game game, Opponent opponent, Integer score) {
			super(key);
			this.game = game;
			this.opponent = opponent;
			this.score = score;
		}
		protected void prepare(PreparedStatement stmt) throws SQLException {
			if(score != null) {
				stmt.setInt(1, score);
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			stmt.setInt(2, game.getTournament().getOid());
			stmt.setInt(3, game.getGameNode().getOid());
			stmt.setInt(4, opponent.getOid());
		}
	}
	
	/**
	 * @param sp
	 */
	public GameScoreInsertBroker(SingletonProvider sp) {
		super(sp);
	}
	
	/* (non-Javadoc)
	 * @see com.tournamentpool.broker.sql.insert.ScoreReporter#insertScore(com.tournamentpool.domain.Game, com.tournamentpool.domain.Opponent, java.lang.Integer)
	 */
	@Override
	public void insertScore(Game game, Opponent opponent, Integer score) {
		addQuery(new GameScoreQuery("GameScoreInsertSQL", game, opponent, score));
	}
	
	/* (non-Javadoc)
	 * @see com.tournamentpool.broker.sql.insert.ScoreReporter#updateScore(com.tournamentpool.domain.Game, com.tournamentpool.domain.Opponent, java.lang.Integer)
	 */
	@Override
	public void updateScore(Game game, Opponent opponent, Integer score) {
		addQuery(new GameScoreQuery("GameScoreUpdateSQL", game, opponent, score));
	}
	
	/* (non-Javadoc)
	 * @see com.tournamentpool.broker.sql.insert.ScoreReporter#deleteScore(com.tournamentpool.domain.Game, com.tournamentpool.domain.Opponent)
	 */
	@Override
	public void deleteScore(final Game game, final Opponent opponent) {
		addQuery(new Query("GameScoreDeleteSQL") {
			protected void prepare(PreparedStatement stmt) throws SQLException {
				stmt.setInt(1, game.getTournament().getOid());
				stmt.setInt(2, game.getGameNode().getOid());
				stmt.setInt(3, opponent.getOid());
			}
		});
	}

}
