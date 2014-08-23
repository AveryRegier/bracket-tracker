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

import com.tournamentpool.domain.*;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

public class ArchiveFilter implements Filter {

	public boolean pass(Group group) {
		for(Pool pool: group.getPools()) {
			if(pass(pool)) return true;
		}
		return false;
	}

	public boolean pass(Bracket bracket) {
		return bracket != null && isArchive(bracket.getTournament());
	}

	public static boolean isArchive(Tournament tournament) {
		boolean c = tournament != null && tournament.isComplete();
		if(c) {
			Optional<Game> championshipGame = tournament.getChampionshipGame();
			if(championshipGame.isPresent()) {
				Date endDate = championshipGame.get().getDate();
				if(endDate != null) {
					Calendar gameTime = Calendar.getInstance();
					gameTime.setTime(endDate);
					Calendar archiveDate = Calendar.getInstance();
					archiveDate.add(Calendar.WEEK_OF_YEAR, -1);
//					System.out.println(tournament.getName()+" ended "+(gameTime.getTime())+" Comparing to "+archiveDate.getTime()+" after "+archiveDate.after(endDate));
					return archiveDate.after(gameTime);
				}
			}
		}
		return c;
	}

	public boolean pass(Pool pool) {
		return pool != null && isArchive(pool.getTournament());
	}
}
