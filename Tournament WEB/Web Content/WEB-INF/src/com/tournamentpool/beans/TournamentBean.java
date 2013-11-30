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
 * Created on Mar 10, 2005
 */
package com.tournamentpool.beans;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.tournamentpool.controller.ArchiveFilter;
import com.tournamentpool.domain.Tournament;

public class TournamentBean {
	private final int id;
	private final String name;
	private final boolean currentUserIsAdmin;
	private final boolean archived;
	private final boolean readyForBrackets;
	private final Calendar startTime;
	private final Date lastUpdated;
	private final boolean deletable;

	public TournamentBean(Tournament tournament, boolean currentUserIsAdmin,
			              boolean archived, boolean readyForBrackets, Timestamp startTime, Date lastUpdated, boolean mayDelete) {
		this.readyForBrackets = readyForBrackets;
		this.deletable = mayDelete;
		this.id = tournament.getOid();
		this.name = tournament.getName();
		this.currentUserIsAdmin = currentUserIsAdmin;
		this.archived = archived;
		this.startTime = Calendar.getInstance();
		this.startTime.setTime(startTime);
		this.lastUpdated = lastUpdated;
	}

	public TournamentBean(Tournament tournament, boolean currentUserIsAdmin,
            boolean readyForBrackets, Timestamp startTime, Date lastUpdated, boolean mayDelete) {
		this.readyForBrackets = readyForBrackets;
		this.deletable = mayDelete;
		this.id = tournament.getOid();
		this.name = tournament.getName();
		this.currentUserIsAdmin = currentUserIsAdmin;
		this.archived = ArchiveFilter.isArchive(tournament);
		this.startTime = Calendar.getInstance();
		this.startTime.setTime(startTime);
		this.lastUpdated = lastUpdated;
	}

	/**
	 * @return Returns the id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	public boolean isCurrentUserIsAdmin() {
		return currentUserIsAdmin;
	}

	public boolean isArchived() {
		return archived;
	}

	public boolean isReadyForBrackets() {
		return readyForBrackets;
	}

	public Calendar getStartCalendar() {
		return startTime;
	}

	public String getStartTime() {
		return SimpleDateFormat.getDateTimeInstance().format(startTime.getTime())+
			" "+startTime.getTimeZone().getID();
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public boolean isDeletable() {
		return deletable;
	}
}
