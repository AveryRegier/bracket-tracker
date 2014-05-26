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

import com.tournamentpool.broker.sql.DatabaseFailure;
import com.tournamentpool.broker.sql.insert.BracketPoolInsertBroker;
import com.tournamentpool.broker.sql.insert.PickInsertBroker;
import com.tournamentpool.domain.*;
import com.tournamentpool.domain.Bracket.Pick;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Avery J. Regier
 */
public class Importer extends RequiresLoginServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5679841714008227452L;
	

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException 
	{
		if(!getUser(req, res).isSiteAdmin()) {
			throw new ServletException("You are not a site administrator.");
		}

		produceJSPPage(req, res, "ImportJSP");
	}


	@SuppressWarnings("unused")
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException 
	{
		if(!getUser(req, res).isSiteAdmin()) {
			throw new ServletException("You are not a site administrator.");
		}
		try {
			String poolID = req.getParameter("poolID");

			Pool pool = null;
			String groupParam = req.getParameter("groupID");
			if(groupParam != null) {
				int groupID = Integer.parseInt(groupParam);
				Group group = getApp().getUserManager().getGroup(groupID);
				if(group != null) {
					pool = group.getPool(Integer.parseInt(poolID));
				} 
			}
			if(pool == null) {
				pool = getApp().getUserManager().getPoolObject(Integer.parseInt(poolID));
			}
			if(pool == null) {
				throw new IllegalArgumentException("Pool "+poolID+" does not exist.");
			}

			Group group = pool.getGroup();
			
			String importString = req.getParameter("import");
			StringTokenizer lineTokenizer = new StringTokenizer(importString, "\n");
			while(lineTokenizer.hasMoreTokens()) {
				User user = null;
				Bracket bracket = null;

				String line = lineTokenizer.nextToken();
				StringTokenizer commaSeparator = new StringTokenizer(line, ",");
				if(commaSeparator.hasMoreTokens()) {
					String id = commaSeparator.nextToken();
				}
				if(commaSeparator.hasMoreTokens()) {
					String userName = commaSeparator.nextToken();
					String userID = userName.substring(0, Math.min(7, userName.length())).trim();
					String password = userName.substring(0, Math.min(8, userName.length())).trim();
					// first check if we've previously imported this user
					for(User auser: group.getMembers()) {
						if(userID.equals(auser.getID())) {
							user = auser;
							for(Bracket abracket: user.getBrackets()) {
								if("imported".equals(abracket.getName())) {
									bracket = abracket;
								}
							}
							
							break;
						}
					}
					if(user == null) {
						String auth = getApp().getUserManager().registerUser(userID, password, userName, null);
						user = getApp().getUserManager().getUser(userID, auth);
						if(!group.hasMember(user.getOID())) {
							group.addMembers(new int[] {user.getOID()});
							user.addGroup(group);
						}
					}
					if(bracket == null) {
						bracket = getApp().getBracketManager().createBracket(user, pool.getTournament(), "imported");
					}
				}
				if(commaSeparator.hasMoreTokens()) {
					String scrumID = commaSeparator.nextToken();
				}
				if(commaSeparator.hasMoreTokens()) {
					String firstFour = commaSeparator.nextToken();
				}
				if(commaSeparator.hasMoreTokens()) {
					bracket.getPicks(getApp().getSingletonProvider()); // just ensure loaded
					List<Pick> insertPicks = new LinkedList<Pick>();
					List<Pick> updatePicks = new LinkedList<Pick>();
					List<Pick> deletePicks = new LinkedList<Pick>();

					String sixtyFour = commaSeparator.nextToken();
					Opponent home = pool.getTournament().getTournamentType().getOpponentByOrder(1);
					Opponent visitor = pool.getTournament().getTournamentType().getOpponentByOrder(2);
					Iterator<GameNode> nodes = pool.getTournament().getTournamentType().getGameNodesInLevelOrder().iterator();
					for(int i=0; i<sixtyFour.length() && nodes.hasNext(); i+=2) {
						GameNode node = nodes.next();
						
						boolean a = '1' == sixtyFour.charAt(i);
						boolean b = '1' == sixtyFour.charAt(i+1);
						
						Opponent winner;
						if(a) { // a pick was made
							if(b) { // bottom pick
								winner = visitor;
							} else { // top pick
								winner = home;								
							}
						} else { // should probably clear the pick
							winner = null;
						}

						Bracket.Pick pick = bracket.createPick(node);
						if(pick.isNew()) {
							if(winner != null) {
								pick.setWinner(winner);
								insertPicks.add(pick);
							}
						} else if(winner != null) {
							pick.setWinner(winner);
							updatePicks.add(pick);
						} else {
							deletePicks.add(pick);
						}
					}

					// update the db.
					new PickInsertBroker(getApp().getSingletonProvider(), insertPicks, updatePicks, deletePicks).execute();

					// update the domain
					updatePicks.addAll(insertPicks);
					bracket.applyPicks(updatePicks); // clears the list of picks in the bracket before applying these
				}
				if(!pool.hasBracket(bracket) && bracket.isComplete(getApp().getSingletonProvider())) {
					// and that the bracket is completed.
					if (bracket.isComplete(getApp().getSingletonProvider())) {
						// and that the player has not exceeded the bracket limit
						if (!pool.hasReachedLimit(bracket.getOwner())) {
							// insert into db then update domain
							new BracketPoolInsertBroker(getApp().getSingletonProvider(), pool, bracket, "0").execute();
						}
					}
				}
			}

			res.sendRedirect(getApp().getConfig().getProperty("MyTournamentURL") + 
					"?request=show&type=pool&id="+pool.getOid()+"&groupID="+group.getId());
		} catch(DatabaseFailure e) {
			throw new ServletException(e);
		}
	}
}
