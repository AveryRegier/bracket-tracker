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

package com.tournamentpool.beans;

import com.tournamentpool.domain.League;

public class LeagueBean {

	private final Integer id;
	private final String name;
	private int numTournaments;
	private int numTeams;
	private boolean deletable;

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public LeagueBean(League league) {
		this.id = (Integer)league.getID();
		this.name = league.getName();
	}

	public int getNumTournaments() {
		return numTournaments;
	}

	public void setNumTournaments(int numTournaments) {
		this.numTournaments = numTournaments;
	}

	public int getNumTeams() {
		return numTeams;
	}

	public void setNumTeams(int numTeams) {
		this.numTeams = numTeams;
	}

	public void setMayDelete(boolean deletable) {
		this.deletable = deletable;
	}
	
	public boolean isDeletable() {
		return deletable;
	}
}
