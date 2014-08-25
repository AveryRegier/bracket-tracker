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
 * Created on Mar 23, 2004
 */
package com.tournamentpool.broker.sql.get;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.broker.sql.PreparedStatementBroker;
import com.tournamentpool.domain.Bracket;
import com.tournamentpool.domain.Bracket.Pick;
import com.tournamentpool.domain.BracketManager;
import com.tournamentpool.domain.GameNode;
import com.tournamentpool.domain.TournamentType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @author Avery J. Regier
 */
public class PickGetBroker extends PreparedStatementBroker {
	private final int bracketOID;
	
	/**
	 * @param sp
	 */
	public PickGetBroker(SingletonProvider sp, int bracketOID) {
		super(sp);
		this.bracketOID = bracketOID;
	}


	protected String getSQLKey() {
		return "PickGetSQL";
	}

	protected void prepare(PreparedStatement stmt) throws SQLException {
		stmt.setInt(1, bracketOID);
	}

	protected void processResults(ResultSet set) throws SQLException {
		BracketManager bracketManager = sp.getSingleton().getBracketManager();
		Bracket bracket = bracketManager.getBracket(bracketOID);
		TournamentType tournamentType = bracket.getTournament().getTournamentType();
		List<Pick> picks = new LinkedList<>();
		while(set.next()) {
            Optional<GameNode> gameNode = tournamentType.getGameNode(set.getInt("GAME_NODE_ID"));
            if(gameNode.isPresent()) {
                Bracket.Pick pick = bracket.createPick(gameNode.get());
                pick.setWinner(tournamentType.getOpponent(set.getInt("WINNER")));
                picks.add(pick);
            }
		}
		bracket.applyPicks(picks);
	}
}
