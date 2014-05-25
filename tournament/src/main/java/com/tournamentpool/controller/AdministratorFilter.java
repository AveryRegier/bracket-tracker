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

package com.tournamentpool.controller;

import com.tournamentpool.domain.Bracket;
import com.tournamentpool.domain.Group;
import com.tournamentpool.domain.Pool;
import com.tournamentpool.domain.User;

public class AdministratorFilter implements Filter {

	private final Filter child;
	private final User currentUser;

	public AdministratorFilter(Filter child, User currentUser) {
		this.child = child;
		this.currentUser = currentUser;
	}

	public boolean pass(Group group) {
		if(group != null && group.getAdministrator() == currentUser) return true;
		return child.pass(group);
	}

	public boolean pass(Bracket bracket) {
		return child.pass(bracket);
	}

	public boolean pass(Pool pool) {
		return child.pass(pool);
	}

}
