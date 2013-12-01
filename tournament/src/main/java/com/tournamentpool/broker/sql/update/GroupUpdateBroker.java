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

package com.tournamentpool.broker.sql.update;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.broker.sql.InsertBroker;

public class GroupUpdateBroker extends InsertBroker {

	private final int groupID;
	private final int invitationCode;

	public GroupUpdateBroker(SingletonProvider sp, int groupID, int invitationCode) {
		super(sp);
		this.groupID = groupID;
		this.invitationCode = invitationCode;
	}

	protected void prepare(PreparedStatement stmt) throws SQLException {
		stmt.setInt(1, invitationCode);
		stmt.setInt(2, groupID);
	}

	protected String getSQLKey() {
		return "GroupUpdateSQL";
	}
}
