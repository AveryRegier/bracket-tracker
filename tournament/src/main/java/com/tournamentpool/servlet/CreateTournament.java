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
import com.tournamentpool.domain.Tournament;
import com.tournamentpool.domain.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.TimeZone;

public class CreateTournament extends RequiresLoginServlet {
	/**
	 *
	 */
	private static final long serialVersionUID = 4361994691528398884L;

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		if(!getUser(req, res).isSiteAdmin()) {
			throw new ServletException("You are not a site administrator.");
		}
		req.setAttribute("tournamentTypes", getApp().getTournamentTypeManager()
				.getTournamentTypeMenu());
		req.setAttribute("leagues", getApp().getTeamManager().getLeagueMenu());
		req.setAttribute("timezones", TimeZone.getAvailableIDs());
		produceJSPPage(req, res, "SelectTournamentTypeJSP");
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			User user = getUser(req, res);
			if(!user.isSiteAdmin()) {
				throw new ServletException("You are not a site administrator.");
			}
			Date startTime = getDate(req);
			Tournament tournament = getApp().getTournamentManager().createTournament(
					req.getParameter("name"),
					getApp().getTournamentTypeManager().getTournamentType(
                            Integer.valueOf(req.getParameter("tournamentType"))),
					getApp().getTeamManager().getLeague(
                            Integer.valueOf(req.getParameter("league"))),
					startTime);
			getApp().getTournamentManager().addAdmins(tournament, new int[] {user.getOID()});
			res.sendRedirect(getApp().getConfig().getProperty("AssignSeedsURL")+"?tournament="+tournament.getOid());
		} catch (NullPointerException | NumberFormatException e) {
			throw new ServletException("Tournament Type is required.", e);
		} catch (DatabaseFailure e) {
			throw new ServletException(e);
		}
	}
}
