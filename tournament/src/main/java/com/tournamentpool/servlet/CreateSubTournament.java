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
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import utility.StringUtil;

import com.tournamentpool.domain.Level;
import com.tournamentpool.domain.Tournament;
import com.tournamentpool.domain.User;

public class CreateSubTournament extends RequiresLoginServlet {

	/**
	 *
	 */
	private static final long serialVersionUID = -3080173127825963838L;

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		if(!getUser(req, res).isSiteAdmin()) {
			throw new ServletException("You are not a site administrator.");
		}
		String tournamentOIDString = req.getParameter("tournament");
		if(StringUtil.killWhitespace(tournamentOIDString) == null) {
			req.setAttribute("tournaments", getApp().getTournamentManager()
					.getTournamentMenu());
			req.setAttribute("leagues", getApp().getTeamManager().getLeagueMenu());
			produceJSPPage(req, res, "SelectTournamentJSP");
		} else {
			int tournamentOid = Integer.parseInt(tournamentOIDString);
			com.tournamentpool.domain.Tournament tournament =
				getApp().getTournamentManager().getTournament(tournamentOid);
			if(tournament == null) {
				throw new IllegalArgumentException("Tournament "+tournamentOid+" is not valid");
			}
			req.setAttribute("levels", tournament.getTournamentType().getLevelMenu());
			req.setAttribute("timezones", TimeZone.getAvailableIDs());
			produceJSPPage(req, res, "CreateSubTournamentJSP");
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			User user = getUser(req, res);
			if(!user.isSiteAdmin()) {
				throw new ServletException("You are not a site administrator.");
			}
			String tournamentOIDString = req.getParameter("tournament");
			String levelOIDString = req.getParameter("level");
			int tournamentOid = Integer.parseInt(tournamentOIDString);
			Tournament parenttournament =
				getApp().getTournamentManager().getTournament(tournamentOid);
			if(parenttournament == null) {
				throw new IllegalArgumentException("Tournament "+tournamentOid+" is not valid");
			}
			Level level = getApp().getScoreSystemManager().getLevel(Integer.parseInt(levelOIDString));
			if(level == null) {
				throw new IllegalArgumentException("Level "+levelOIDString+" is not valid");
			}
			Date startTime = getDate(req);
			Tournament tournament = getApp().getTournamentManager().createSubTournament(
					req.getParameter("name"),
					parenttournament,
					level, startTime);
//			getApp().getTournamentManager().addAdmins(tournament, new int[] {user.getOID()});
			res.sendRedirect(getApp().getConfig().getProperty("MyTournamentURL")+"?tournament="+tournament.getOid());
		} catch (NullPointerException e) {
			throw new ServletException("Level is required.", e);
		} catch (NumberFormatException e) {
			throw new ServletException("Level is required.", e);
		} catch (SQLException e) {
			throw new ServletException(e);
		}
	}
}
