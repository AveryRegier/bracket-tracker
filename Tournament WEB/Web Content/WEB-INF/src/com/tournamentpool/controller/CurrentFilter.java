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

import java.sql.SQLException;

import com.tournamentpool.domain.Bracket;
import com.tournamentpool.domain.Group;
import com.tournamentpool.domain.Pool;

public class CurrentFilter extends ArchiveFilter {
	public boolean pass(Group group) throws SQLException {
		return super.pass(group) || group.getPools().isEmpty();
	}

	public boolean pass(Pool pool) {
		return pool != null && !ArchiveFilter.isArchive(pool.getTournament());
	}

	public boolean pass(Bracket bracket) {
		return bracket != null && !ArchiveFilter.isArchive(bracket.getTournament());
	}
}
