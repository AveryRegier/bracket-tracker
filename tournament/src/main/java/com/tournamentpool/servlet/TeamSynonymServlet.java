/* 
Copyright (C) 2014 Avery J. Regier.

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

import com.tournamentpool.beans.TeamBean;
import com.tournamentpool.domain.Team;
import com.tournamentpool.domain.TeamManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class TeamSynonymServlet extends RequiresLoginServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4376942544476556758L;

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        TeamManager teamManager = getApp().getTeamManager();
        Team team = teamManager.getTeam(Integer.parseInt(req.getParameter("team")));
        TeamBean bean = new TeamBean(team);
        bean.setSynonyms(team.getSynonyms());
        req.setAttribute("Team", bean);
        produceJSPPage(req, res, "ManageTeamSynonymsJSP");
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			getUser(req, res); // we want the side effect
			TeamManager teamManager = getApp().getTeamManager();
			Team team = teamManager.getTeam(Integer.parseInt(req.getParameter("team")));
            teamManager.createTeamSynonym(team, req.getParameter("name"));

            res.sendRedirect(req.getHeader("referer"));
		} catch (SQLException e) {
			throw new ServletException(e);
		}
	}
}
