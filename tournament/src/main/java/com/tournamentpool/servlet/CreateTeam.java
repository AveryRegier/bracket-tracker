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
import com.tournamentpool.domain.Team;
import com.tournamentpool.domain.TeamManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CreateTeam extends RequiresLoginServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4376942544476556758L;

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			String parameter = req.getParameter("tournament");
			if(parameter != null) {
	 			req.getSession().setAttribute("tournament", parameter);
				req.getSession().setAttribute("league",  
						getApp().getTournamentManager().getTournament(Integer.parseInt(
								(String)req.getSession().getAttribute("tournament"))).getLeague().getID());
			}
		} catch (NumberFormatException e) {
			// Do nothing
		}
		if(req.getParameter("league") != null) {
			req.getSession().setAttribute("league", req.getParameter("league"));
		}
		req.setAttribute("leagues", getApp().getTeamManager().getLeagueMenu());
		produceJSPPage(req, res, "CreateTeamJSP");
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			getUser(req, res); // we want the side effect
			TeamManager teamManager = getApp().getTeamManager();
			Team team = teamManager.createTeam(req.getParameter("name"));
			String[] leagueIDs = req.getParameterValues("addToLeague");
			for (int i = 0; i < leagueIDs.length; i++) {
				int id = Integer.parseInt(leagueIDs[i]);
				League league = getApp().getTeamManager().getLeague(id);
				teamManager.associateTeamToLeague(league, team);
			}
			Object attribute = req.getSession().getAttribute("tournament");
			if(attribute != null) {
				res.sendRedirect(getApp().getConfig().getProperty("AssignSeedsURL")+"?tournament="+attribute);
			} else {
				res.sendRedirect(getApp().getConfig().getProperty("MyTournamentURL"));
			}
		} catch (DatabaseFailure e) {
			throw new ServletException(e);
		}
	}
}
