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
 * Created on Dec 28, 2004
 */
package com.tournamentpool.beans;

import com.tournamentpool.domain.ScoreSystem;

/**
 * @author Avery J. Regier
 */
public class ScoreSystemBean {

	private Object id;
	private String name;

	public void setScoreSystem(ScoreSystem scoreSystem) {
		this.id = scoreSystem.getID();
		this.name = scoreSystem.getName();
	}
	
	
	/**
	 * @return Returns the id.
	 */
	public Object getId() {
		return id;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
}
