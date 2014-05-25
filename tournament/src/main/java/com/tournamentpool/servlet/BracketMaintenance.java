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
 * Created on Oct 16, 2004
 */
package com.tournamentpool.servlet;

import com.tournamentpool.beans.GroupBean;
import com.tournamentpool.beans.PoolBean;
import com.tournamentpool.broker.sql.DatabaseFailure;
import com.tournamentpool.broker.sql.insert.PickInsertBroker;
import com.tournamentpool.domain.*;
import com.tournamentpool.domain.Bracket.Pick;
import com.tournamentpool.domain.GameNode.Feeder;
import com.tournamentpool.domain.Tournament;
import utility.StringUtil;
import utility.domain.Reference;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author Avery J. Regier
 */
public class BracketMaintenance extends RequiresLoginServlet {
	/**
	 *
	 */
	private static final long serialVersionUID = 8744591849953091176L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		User user = getUser(req, resp);
		String request = req.getParameter("request");

		try {
			if ("create".equalsIgnoreCase(request)) {
				req.setAttribute("BracketBean",
					getApp().getTournamentController().getTournamentBracket(Integer.parseInt(req.getParameter("tournament")))
				);
				produceJSPPage(req, resp, "CreateBracketJSP");
			} else {
				Bracket bracket = getApp().getBracketManager().getBracket(Integer.parseInt(req.getParameter("id")));
				if ("delete".equalsIgnoreCase(request)) {
					bracket.delete(user, getApp().getSingletonProvider());
					resp.sendRedirect(req.getHeader("Referer"));
				} else {
					setupPools(req, user, bracket);
					if("edit".equalsIgnoreCase(request) && bracket.mayEdit(getUser(req, resp))) {
						getBracketBean(req, false);
						produceJSPPage(req, resp, "CreateBracketJSP");
					} else {
						getBracketBean(req, true);
						produceJSPPage(req, resp, "ViewBracketJSP");
					}
				}
			}
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	private void setupPools(HttpServletRequest req, User user, Bracket bracket) {
		if(user ==  bracket.getOwner() && bracket.isComplete(getApp().getSingletonProvider()) && !bracket.getTournament().isStarted()) {
			Set<PoolBean> poolsAvailable = new HashSet<PoolBean>();
			Iterator<Group> groups = user.getGroupsInHierarchy();
			while (groups.hasNext()) {
				Group group = groups.next();
				GroupBean gBean = new GroupBean(group);
				for(Pool pool: group.getMyPools()) {
					if(pool != null) {
						if(pool.getTournament() == bracket.getTournament() &&
								!pool.hasBracket(bracket) && !pool.hasReachedLimit(user))
						{
							PoolBean pBean = new PoolBean(pool.getOid(), pool.getName());
							pBean.setGroup(gBean);
							poolsAvailable.add(pBean);
						}
					}
				}
			}
			if(poolsAvailable.size() > 0) {
				req.setAttribute("Pools", poolsAvailable);
			}
		}
	}

	/**
	 * @param req
	 * @throws ServletException
	 */
	private void getBracketBean(HttpServletRequest req, boolean view) throws ServletException {
		try {
			req.setAttribute("BracketBean",
				getApp().getTournamentController().getBracket(
					req.getParameter("id"),
					req.getParameter("pool"), view)
			);
		} catch (NumberFormatException e) {
			throw new ServletException(e);
		} catch (DatabaseFailure e) {
			throw new ServletException(e);
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException
	{
		User user = getUser(req, res);

	//	System.out.println(req.getParameterMap());
		try {
			Bracket bracket = createOrUpdateBracket(req, user);

            DecisionMaker decider;
            boolean randomize = "Randomize".equals(req.getParameter("decider"));
            if(randomize) {
                decider = new RandomDecider(bracket.getTournament().getTournamentType());
            } else {
                decider = new UserDecider(req);
            }

			applyPicks(decider, bracket);

			if(randomize || req.getParameter("save") != null) {
				res.sendRedirect(req.getRequestURI()+"?request=edit&id="+bracket.getOID());
			} else { // continue
				res.sendRedirect(getApp().getConfig().getProperty("MyTournamentURL"));
			}
		} catch (DatabaseFailure e) {
			throw new ServletException(e);
		} catch (IllegalAccessException e) {
			throw new ServletException(e);
		}
	}

	private Bracket createOrUpdateBracket(HttpServletRequest req, User user)
			throws IllegalAccessException {
		String name = StringUtil.killWhitespace(req.getParameter("name"));
		// first check for a bracket oid, if doesn't exist, create new
		Bracket bracket = user.getBracket(getInt(req, "id", -1));
		if(bracket == null) {
			int tournamentOid = Integer.parseInt(req.getParameter("tournament"));
			Tournament tournament = getApp().getTournamentManager().getTournament(tournamentOid);
			if(tournament == null) {
				throw new IllegalArgumentException("A tournament is required");
			}
			if(name == null) {
				throw new IllegalArgumentException("A bracket name is required");
			}
			bracket = getApp().getBracketManager().createBracket(user, tournament, name);
		} else {
			if(user != bracket.getOwner()) {
				throw new IllegalAccessException("You may not edit another user's bracket");
			}
			Tournament tournament = bracket.getTournament();
			if(tournament.isStarted() && bracket.isInPool()) {
				throw new IllegalAccessException(
						"Brackets already assigned to a pool with a tournament in progress may not be modified");
			}
			if(name != null) { // never update to an empty name
				getApp().getBracketManager().updateBracket(bracket, name);
			}
		}
		return bracket;
	}

	private void applyPicks(DecisionMaker decider, Bracket bracket)
			throws IllegalAccessException
	{
		List<Pick> insertPicks = new LinkedList<Pick>();
		List<Pick> updatePicks = new LinkedList<Pick>();
		List<Pick> deletePicks = new LinkedList<Pick>();
		sortPicks(decider, bracket, insertPicks, updatePicks, deletePicks);
		
		boolean mayDelete = true;
		if(bracket.isInPool()) {
			mayDelete = false;
		}
		
		if(!mayDelete && !deletePicks.isEmpty()) {
			throw new IllegalAccessException(
				"You may not delete picks from a bracket that is already assigned to a pool.");
		}

		// update the db.
		new PickInsertBroker(getApp().getSingletonProvider(), insertPicks, updatePicks, deletePicks).execute();

		// update the domain
		updatePicks.addAll(insertPicks);
		bracket.applyPicks(updatePicks); // clears the list of picks in the bracket before applying these
	}

	private void sortPicks(DecisionMaker decider,
			Bracket bracket, List<Pick> insertPicks, List<Pick> updatePicks,
			List<Pick> deletePicks) {
		TournamentType tournamentType = bracket.getTournament().getTournamentType();
		List<Reference> nodes = new LinkedList<Reference>();
		nodes.add(tournamentType.getChampionshipGameNode());
		while(!nodes.isEmpty()) {
			Object o = nodes.remove(0);
			if(o instanceof GameNode) {
				GameNode node = (GameNode)o;
				for (Feeder feeder : node.getFeeders()) {
					nodes.add(feeder.getFeeder());
				}
                int decision = decider.getDecision(node);
                Opponent winner = tournamentType.getOpponentByOrder(decision);
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
		}
	}

    interface DecisionMaker {
        public int getDecision(GameNode node);
    }

    public class UserDecider implements DecisionMaker {
        private HttpServletRequest req;

        public UserDecider(HttpServletRequest req) {
            this.req = req;
        }

        public int getDecision(GameNode node) {
            return getInt(req, "game" + node.getOid(), -1);
        }
    }

    public class RandomDecider implements DecisionMaker {
        private final Random r = new Random();
        private final int[] sequences;

        public RandomDecider(TournamentType type) {
            Set<Map.Entry<Integer, Opponent>> opponents = type.getOpponents();
            this.sequences = new int[opponents.size()];
            int i=0;
            for(Map.Entry<Integer, Opponent> entry: opponents) {
                sequences[i++] = entry.getValue().getSequence();
            }
        }

        @Override
        public int getDecision(GameNode node) {
            return sequences[r.nextInt(sequences.length)];
        }
    }

}
