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
 * Created on Feb 24, 2003
 */
package com.tournamentpool.controller;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.domain.User;

/**
 * @author avery
 */
public class MyTournamentController extends TournamentController {

	/**
	 * @param sp
	 */
	public MyTournamentController(SingletonProvider sp) {
		super(sp);
	}

	public void getPools(User user) {
//		Iterator iter = getApp().getPoolManager().iteratePools();
//		while (iter.hasNext()) {
//			Pool pool = (Pool) iter.next();
//			if(pool.hasInvitedUser(user)) {
//			//	 add it
//			}
//		}
		
		
	}
}
