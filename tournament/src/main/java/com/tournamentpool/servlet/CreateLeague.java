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

package com.tournamentpool.servlet;

import com.tournamentpool.broker.sql.DatabaseFailure;
import com.tournamentpool.domain.League;
import com.tournamentpool.domain.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CreateLeague extends RequiresLoginServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8823768380712233397L;

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		if(!getUser(req, res).isSiteAdmin()) {
			throw new ServletException("You are not a site administrator.");
		}
		req.getSession().removeAttribute("tournament");
		produceJSPPage(req, res, "CreateLeagueJSP");
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			User user = getUser(req, res);
			if(!user.isSiteAdmin()) {
				throw new ServletException("You are not a site administrator.");
			}
			League league = getApp().getTeamManager().createLeague(
					req.getParameter("name"));
			req.getSession().setAttribute("league", league.getID());
			if(req.getParameter("AddTeamsToLeague") != null) {
				res.sendRedirect(getApp().getConfig().getProperty("AddTeamsToLeagueURL")+
						"?league="+league.getLeagueID());
			} else {
				res.sendRedirect(getApp().getConfig().getProperty("CreateTournamentURL"));
			}
		} catch (DatabaseFailure e) {
			throw new ServletException(e);
		}
	}
}
