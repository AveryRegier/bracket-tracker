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
 * Created on Mar 13, 2004
 */
package com.tournamentpool.domain;

import utility.domain.Reference;

/**
 * @author Avery J. Regier
 */
public class Level implements Reference, Comparable<Level> {
	private Integer oid; 
	private String name;
	private int roundNo;

	/**
	 * @param levelOID
	 * @param name
	 * @param roundNo
	 */
	public Level(int levelOID, String name, int roundNo) {
		this.oid = new Integer(levelOID);
		this.name = name;
		this.roundNo = roundNo;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public int getRoundNo() {
		return roundNo;
	}

	public int getOid() {
		return oid.intValue();
	}

	public Integer getID() {
		return oid;
	}

	@Override
	public int compareTo(Level o) {
		return roundNo - o.roundNo;
	}
}
