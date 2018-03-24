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
 * Created on Mar 14, 2004
 */
package com.tournamentpool.servlet;

import com.tournamentpool.application.TournamentSingletonProvider;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Avery J. Regier
 */
public class Admin extends RequiresLoginServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5679841714008227452L;
	
	private static final Map<String, String> createProperties = new HashMap<>();
	static {
		createProperties.put("tournament", "CreateTournamentURL");
		createProperties.put("team", "CreateTeamURL");
		createProperties.put("league", "CreateLeagueURL");
		createProperties.put("siteAdmin", "AddSiteAdminsURL");
		createProperties.put("subtournament", "CreateSubTournamentURL");
	}

	private static final Map<String, String> listProperties = new HashMap<>();
	static {
		listProperties.put("tournament", "ListTournamentsURL");
		listProperties.put("team", "ListTeamsURL");
		listProperties.put("league", "ListLeaguesURL");
		listProperties.put("siteAdmin", "ListSiteAdminsURL");
		listProperties.put("subtournament", "ListSubTournamentsURL");
		listProperties.put("group", "ListGroupsURL");
		listProperties.put("player", "ListPlayersURL");
		listProperties.put("pool", "ListPoolsURL");
		listProperties.put("bracket", "ListBracketsURL");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException 
	{
		if(!getUser(req, res).isSiteAdmin()) {
			throw new ServletException("You are not a site administrator.");
		}

		String request = req.getParameter("request");
		String type = req.getParameter("type");
		String property = null;
		if("create".equals(request)) {
			property = createProperties.get(type);
		} else if("list".equals(request)){
			property = listProperties.get(type);
		} else if("reset".equals(request)) {
			((TournamentSingletonProvider)getServletContext().getAttribute("app")).reset();
		} else if("update".equals(request)) {
			getApp().getAutoUpdateController().update();
		}
		
		String url = property != null ? getApp().getConfig().getProperty(property) : null;
		if(url != null) {
			res.sendRedirect(url);
		} else { // if we haven't configured the page, just redirect to the administration main screen
			produceJSPPage(req, res, "AdminJSP");
		}
	}
}
