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

package com.tournamentpool.domain;

import utility.domain.Reference;

public class GameFeederType implements Reference {

	private final Integer oid;
	private final String name;

	public GameFeederType(int oid, String name) {
		this.oid = new Integer(oid);
		this.name = name;
	}

	public String toString() {
		return name;
	}

	public Integer getID() {
		return oid;
	}

	public String getName() {
		return name;
	}
}
