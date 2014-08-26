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
public class Opponent implements Reference, Comparable<Opponent> {
	private final int oid;
	private final String name;
	private final int sequence;

	/**
	 * @param oponentOID
	 * @param name
	 * @param sequence
	 */
	public Opponent(int oponentOID, String name, int sequence) {
		this.oid = oponentOID;
		this.name = name;
		this.sequence = sequence;
	}
	
	public int compareTo(Opponent o) {
		return sequence - o.sequence;
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
	public int getOid() {
		return oid;
	}

	/**
	 * @return
	 */
	public int getSequence() {
		return sequence;
	}

	public Object getID() {
		return oid;
	}
}
