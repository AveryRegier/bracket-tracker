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

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tournamentpool.beans.LeagueBean;
import com.tournamentpool.domain.League;
import com.tournamentpool.domain.Team;
import com.tournamentpool.domain.Tournament;

public class AddTeamsToLeague extends RequiresLoginServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9138792306988605836L;

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String leagueOID = req.getParameter("league");
		String tournamentOID = req.getParameter("tournament");
		if(leagueOID != null) {
			League league = getLeague(leagueOID);
			if(league == null) {
				throw new IllegalArgumentException("League "+leagueOID+" is not valid");
			}
			req.setAttribute("league", new LeagueBean(league));
		} else if(tournamentOID != null) {
			req.getSession().setAttribute("tournament", tournamentOID);
			req.setAttribute("tournament", tournamentOID);
			Tournament tournament = getApp().getTournamentManager().getTournament(Integer.parseInt(tournamentOID));
			if(tournament != null) {
				req.setAttribute("league", new LeagueBean(tournament.getLeague()));
			}
		}
		String fromLeagueOID = req.getParameter("fromLeague");
		if(fromLeagueOID != null) {
			League fromLeague = getLeague(fromLeagueOID);
			if(fromLeague == null) {
				throw new IllegalArgumentException("League "+fromLeagueOID+" is not valid");
			}
			req.setAttribute("teams", fromLeague.getTeamMenu());
			req.setAttribute("fromLeague", new LeagueBean(fromLeague));
			produceJSPPage(req, res, "AddTeamsToLeagueJSP");
		} else {
			req.setAttribute("leagues", getApp().getTeamManager().getLeagueMenu());
			produceJSPPage(req, res, "SelectLeagueJSP");
		}
	}

	private League getLeague(String leagueOID) {
		League league = getApp().getTeamManager().getLeague(Integer.parseInt(leagueOID));
		return league;
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			getUser(req, res); // We want the side effect here
			League league = getApp().getTeamManager().getLeague(
					Integer.parseInt(req.getParameter("league")));
			for (String teamID : req.getParameterValues("team")) {
				int id = Integer.parseInt(teamID);
				Team team = getApp().getTeamManager().getTeam(id);
				getApp().getTeamManager().associateTeamToLeague(league, team);
			}
			Object attribute = req.getSession().getAttribute("tournament");
			if(attribute != null) {
				res.sendRedirect(getApp().getConfig().getProperty("AssignSeedsURL")+"?tournament="+attribute);
			} else {
				res.sendRedirect(getApp().getConfig().getProperty("CreateTournamentURL"));
			}
		} catch (SQLException e) {
			throw new ServletException(e);
		}
	}
}
