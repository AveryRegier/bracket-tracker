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

/*
 * Created on Mar 14, 2004
 */
package com.tournamentpool.servlet;

import com.tournamentpool.beans.BracketBean;
import com.tournamentpool.broker.sql.DatabaseFailure;
import com.tournamentpool.controller.TournamentVisitor.Node;
import com.tournamentpool.domain.*;
import com.tournamentpool.domain.MainTournament.WinnerSource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;

/**
 * @author Avery J. Regier
 */
public class Tournament extends RequiresLoginServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -244653209640125962L;

	protected void doGet(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException 
	{
		User user = getUser(req, res); 
		com.tournamentpool.domain.Tournament tournament = lookupTournament(req);
		if("remove".equalsIgnoreCase(req.getParameter("request"))) {
			try {
				getApp().getTournamentManager().deleteTournament(user, tournament.getOid());
				// TODO: send message on deletion failure
				res.sendRedirect(req.getHeader("Referer"));
			} catch (DatabaseFailure e) {
				throw new ServletException(e);
			}
		} else if(!tournament.hasAllSeedsAssigned() && tournament.mayEdit(user)) {
            setDate(tournament.getStartTime(), req);
			res.sendRedirect(getApp().getConfig().getProperty("AssignSeedsURL")+"?tournament="+tournament.getOid());
		} else if(tournament.mayEdit(user) && "true".equalsIgnoreCase(req.getParameter("edit"))) {
			BracketBean<Node> bracketBean = getApp().getTournamentController().getTournamentBracketToEdit(tournament.getOid());
			bracketBean.setBracketType("Tournament");
			req.setAttribute("BracketBean", bracketBean);
            setDate(tournament.getStartTime(), req);
			produceJSPPage(req, res, "CreateBracketJSP");
		} else {
			req.setAttribute("BracketBean", 
				getApp().getTournamentController().getTournamentBracket(tournament.getOid())
			);
			produceJSPPage(req, res, "TournamentJSP");
		}
	}
	
	protected void doPost(final HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException 
	{
		User user = getUser(req, res); 

		//	System.out.println(req.getParameterMap());
		try {
			int tournamentOid = Integer.parseInt(req.getParameter("tournament"));
			final MainTournament tournament = (MainTournament) getApp().getTournamentManager().getTournament(tournamentOid);
			
			if(req.getParameter("save") != null) {
				if(!tournament.mayEdit(user)) {
					throw new ServletException("You may not edit "+tournament.getName());
				}
				getApp().getTournamentManager().updateTournament(
                        tournament,
                        req.getParameter("name"),
                        getDate(req));

				WinnerSource winnerSource = (tournamentType, node) -> Optional.of(new GameInfo() {
                    // defer to in-memory object for information we don't know from the UI
                    private final Optional<Game> game = tournament.getGame(node);

                    @Override
                    public String getGameID() {
                        return game.isPresent() ? game.get().getGameID() : null;
                    }

                    @Override
                    public Integer getScore(Opponent opponent) {
                        return game.isPresent() ? game.get().getScore(opponent) : null;
                    }

                    @Override
                    public Date getDate() {
                        return game.isPresent() ?
                                game.get().getDate() :
                                getWinner().isPresent() ? new Date() : null;
                    }

                    @Override
                    public String getStatus() {
                        return game.map(Game::getStatus).orElse(null);
                    }

                    @Override
                    public Optional<Opponent> getWinner() {
                        return tournamentType.getOpponentByOrder(getInt(req, "game"+node.getOid(), -1));
                    }
                });
				
				tournament.updateGames(getApp().getSingletonProvider(), winnerSource);
			
				res.sendRedirect(req.getRequestURI()+"?edit=true&tournament="+tournament.getOid());
			} else { // continue
				res.sendRedirect(getApp().getConfig().getProperty("MyTournamentURL"));
			}
		} catch (DatabaseFailure e) {
			throw new ServletException(e);
		}
	}

}
