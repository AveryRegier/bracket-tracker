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
 * Created on Mar 15, 2005
 */
package com.tournamentpool.broker.sql.update;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.broker.sql.PreparedStatementBroker;
import com.tournamentpool.domain.User;

public class SiteAdminUpdateBroker extends PreparedStatementBroker {

	private final User user;
	private final boolean siteAdmin;

	public SiteAdminUpdateBroker(SingletonProvider sp, User user, boolean siteAdmin) {
		super(sp);
		this.user = user;
		this.siteAdmin = siteAdmin;
	}

	protected String getSQLKey() {
		return "SiteAdminUpdateSQL";
	}

	protected void prepare(PreparedStatement stmt) throws SQLException {
		if(siteAdmin) {
			stmt.setString(1, "Y");
		} else {
			stmt.setNull(1, Types.CHAR);
		}
		stmt.setInt(2, user.getOID());
	}
	
	protected void processUpdateCount(int updateCount) throws SQLException {
		if(updateCount == 1) {
			user.setSiteAdmin(siteAdmin);
		}
	}
}
