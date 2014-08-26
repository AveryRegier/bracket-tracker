/* 
Copyright (C) 2003-2011 Avery J. Regier.

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
package com.tournamentpool.broker.sql.insert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.broker.sql.InsertBroker;
import com.tournamentpool.domain.User;

/**
 * @author Avery J. Regier
 */
public class GroupInsertBroker extends InsertBroker {

	private final String name;
	private final User admin;
	private int groupOID;
	private final int invitationCode;
	private final int parentID;

	/**
	 * @param sp
	 * @param name
	 * @param parentID 
	 */
	public GroupInsertBroker(SingletonProvider sp, String name, User admin, int invitationCode, int parentID) {
		super(sp);
		this.name = name;
		this.admin = admin;
		this.invitationCode = invitationCode;
		this.parentID = parentID;
	}

	protected void prepare(PreparedStatement stmt) throws SQLException {
		stmt.setString(1, name);
		stmt.setInt(2, admin.getOID());
		stmt.setInt(3, invitationCode);
		if(parentID == 0) {
			stmt.setNull(4, java.sql.Types.INTEGER);
		} else {
			stmt.setInt(4, parentID);
		}
	}

	protected String getSQLKey() {
		return "GroupInsertSQL";
	}

	
	protected void processGeneratedKeys(ResultSet generated)
			throws SQLException 
	{
	//	ResultSetMetaData metaData = generated.getMetaData();
		while(generated.next()) {
			groupOID = generated.getInt(1);
			sp.getSingleton().getUserManager().loadGroup(
				groupOID,
				name,
				admin.getOID(), 
				invitationCode,
				parentID
			);
		}
	}

	public int getGroupOID() {
		return groupOID;
	}
}
