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
 * Created on Mar 6, 2005
 */
package com.tournamentpool.servlet;

import com.tournamentpool.beans.TournamentBean;
import com.tournamentpool.broker.sql.DatabaseFailure;
import com.tournamentpool.broker.sql.get.TournamentAvailableAdminsGetBroker;
import com.tournamentpool.domain.MainTournament;
import com.tournamentpool.domain.Tournament;
import com.tournamentpool.domain.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AddAdminsServlet extends RequiresLoginServlet {
	/**
	 *
	 */
	private static final long serialVersionUID = -7814703336844474875L;

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			User user = getUser(req, res);
			int id = Integer.parseInt(req.getParameter("tournament"));
			Tournament tournament = getApp().getTournamentManager().getTournament(id);
			if(tournament == null) {
				throw new IllegalArgumentException("Tournament "+id+" is not valid");
			}
			if(!tournament.mayEdit(user)) {
				throw new ServletException("You are not allowed to edit this tournament.");
			}
			TournamentBean bean = new TournamentBean(tournament, tournament.mayEdit(user),
					tournament.hasAllSeedsAssigned(), tournament.getStartTime(), 
					tournament.getLastUpdated(), false);
			req.setAttribute("Tournament", bean);
			req.setAttribute("Players", new TournamentAvailableAdminsGetBroker(getApp().getSingletonProvider(), (MainTournament) tournament).getPlayers());
			produceJSPPage(req, res, "AddAdminsToTournamentJSP");
		} catch (DatabaseFailure e) {
			throw new ServletException(e);
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			User user = getUser(req, res);
			int tournamentID = Integer.parseInt(req.getParameter("tournament"));
			Tournament tournament = getApp().getTournamentManager().getTournament(tournamentID);
			if(tournament == null) {
				throw new IllegalArgumentException("Tournament "+tournamentID+" is not valid");
			}
			if(!tournament.mayEdit(user)) {
				throw new ServletException("You are not allowed to edit this tournament.");
			}
			int[] playerIDs = convertToIntegers(req.getParameterValues("player"));
			getApp().getTournamentManager().addAdmins(tournament, playerIDs);
			res.sendRedirect(getApp().getConfig().getProperty("MyTournamentURL"));
		} catch (DatabaseFailure e) {
			throw new ServletException(e);
		}
	}
}
