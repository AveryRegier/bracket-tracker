/* 
Copyright (C) 2003-2013 Avery J. Regier.

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
import com.tournamentpool.domain.Seed;
import com.tournamentpool.domain.Team;
import com.tournamentpool.domain.User;
import utility.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AssignSeeds extends RequiresLoginServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1666910960085713879L;

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		User user = getUser(req, res); 
		com.tournamentpool.domain.Tournament tournament = lookupTournament(req);
		if(tournament.mayEdit(user)) {
			if(tournament.getLeague() != null) {
				req.setAttribute("teams", tournament.getLeague().getTeamMenu());
			} else {
				req.setAttribute("teams", getApp().getTeamManager().getTeamMenu());
			}
			req.setAttribute("BracketBean", 
					getApp().getTournamentController().getTournamentBracketToEdit(tournament.getOid())
				);
            setDate(tournament.getStartTime(), req);
			produceJSPPage(req, res, "AssignSeedsJSP");
		} else {
			req.setAttribute("BracketBean", 
				getApp().getTournamentController().getTournamentBracket(tournament.getOid())
			);
			produceJSPPage(req, res, "TournamentJSP");
		}
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			String tournamentOIDString = req.getParameter("tournament");
			int tournamentOid = Integer.parseInt(tournamentOIDString);
			com.tournamentpool.domain.Tournament tournament = 
				getApp().getTournamentManager().getTournament(tournamentOid);
	
			getApp().getTournamentManager().updateTournament(
                    tournament,
                    req.getParameter("name"),
                    getDate(req));
			
			Map<Seed, Team> seedTeam = new HashMap<>();
			
			Map params = req.getParameterMap();
            for (Map.Entry entry : (Iterable<Map.Entry>) params.entrySet()) {
                String key = (String) entry.getKey();
                if (key.startsWith("team")) {
                    Integer seedID = Integer.valueOf(key.substring(4).trim());
                    Seed seed = tournament.getTournamentType().getSeed(seedID);
                    String teamIDString = ((String[]) entry.getValue())[0];
                    Team team = null;
                    if (StringUtil.killWhitespace(teamIDString) != null) {
                        team = getApp().getTeamManager().getTeam(Integer.valueOf(teamIDString));
                    }
                    seedTeam.put(seed, team);
                }
            }
		
			getApp().getTournamentManager().updateTounamentSeeds(tournament, seedTeam);
		
			if(!tournament.hasAllSeedsAssigned()) {
				res.sendRedirect(req.getRequestURI()+"?tournament="+tournament.getOid());
			} else { // continue to update games
				res.sendRedirect(getApp().getConfig().getProperty("TournamentURL")+"?edit=true&tournament="+tournament.getOid());
			}
		} catch (DatabaseFailure e) {
			throw new ServletException(e);
		}
	}
}
