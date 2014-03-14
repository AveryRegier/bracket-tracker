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
 * Created on Feb 20, 2003
 */
package com.tournamentpool.domain;

import com.tournamentpool.application.SingletonProvider;
import utility.domain.Reference;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author avery
 */
public interface Tournament extends Reference {

	/**
	 *
	 */
	public TournamentType getTournamentType();

	/**
	 * @return
	 */
	public Game getChampionshipGame();

	/**
	 * @param node
	 * @return
	 */
	public Game getGame(GameNode node);

	/**
	 * @param seed
	 */
	public Team getTeam(Seed seed);

	/**
	 * @return Returns the oid.
	 */
	public int getOid();

	public Object getID();

	/**
	 * @return true if this tournament has begun
	 */
	public boolean isStarted();

	public boolean isComplete();

	public boolean hasAllSeedsAssigned();

	public void setName(String name);

	public League getLeague();

	public boolean mayEdit(User user);

	public Timestamp getStartTime();
	
	public Date getLastUpdated();

	public boolean delete(User requestor, SingletonProvider sp)
			throws SQLException;

	public boolean mayDelete(User requestor, SingletonProvider sp)
			throws SQLException;

	public boolean isAdmin(User requestor);

	public boolean hasTeam(Team team);

	public java.util.Date getNextStartTime();

    void setStartTime(Timestamp startTime);
}
