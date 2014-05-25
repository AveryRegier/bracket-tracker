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
 * Created on Oct 1, 2004
 */
package com.tournamentpool.servlet;

import com.tournamentpool.beans.GroupBean;
import com.tournamentpool.beans.PlayerBean;
import com.tournamentpool.beans.PoolBean;
import com.tournamentpool.beans.TournamentBean;
import com.tournamentpool.broker.sql.DatabaseFailure;
import com.tournamentpool.domain.*;
import com.tournamentpool.domain.Tournament;
import utility.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Avery J. Regier
 */
public class PoolMaintenance extends RequiresLoginServlet {
	/**
	 *
	 */
	private static final long serialVersionUID = -9117090245420730132L;

	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		User user = getUser(req, res);

		try {
			String poolParam = StringUtil.killWhitespace(req.getParameter("pool"));
			if(poolParam != null) {
				int poolID = Integer.parseInt(poolParam);
				Pool pool = getApp().getUserManager().getPoolObject(poolID);
				if(pool == null) {
					throw new IllegalArgumentException("Pool "+poolID+" does not exist.");
				}
				
				if("remove".equalsIgnoreCase(req.getParameter("request"))) {
					String bracketParam = StringUtil.killWhitespace(req.getParameter("bracket"));
					if(bracketParam != null) {
						int bracketID = Integer.parseInt(bracketParam);
						Bracket bracket = pool.getBracket(bracketID);
						pool.removeBracket(user, bracket);
					}
					res.sendRedirect(req.getHeader("Referer"));
					return;
				}
				
				if(!pool.getGroup().getAdministrator().equals(user)) {
					throw new IllegalAccessException("You are not the administrator of this group");
				}

				if("delete".equals(req.getParameter("request"))) {
					pool.delete(user, getApp().getSingletonProvider());
					res.sendRedirect(req.getHeader("Referer"));
				} else {
					PoolBean poolBean = new PoolBean(pool.getOid(), pool.getName());
					poolBean.setGroup(setupBeans(req, user, pool.getGroup()));
					poolBean.setScoreSystem(pool.getScoreSystem());
					poolBean.setTournament(new TournamentBean(pool.getTournament(), false, false, false, 
							pool.getTournament().getStartTime(), pool.getTournament().getLastUpdated(), false));
					poolBean.setBracketLimit(pool.getBracketLimit());
					poolBean.setShowBracketsEarly(pool.isShowBracketsEarly());
					poolBean.setTieBreaker(pool.getTieBreakerType(), pool.getTieBreakerQuestion(), pool.getTieBreakerAnswer());
					req.setAttribute("Pool", poolBean);
	
					if(pool.isTiebreakerNeeded(user)) {
						produceJSPPage(req, res, "ClosePoolJSP");
					} else {
						produceJSPPage(req, res, "EditPoolJSP");
					}
				}
			} else {
				int id = Integer.parseInt(req.getParameter("group"));
				Group group = getApp().getUserManager().getGroupObject(id);
				if(group == null) {
					throw new IllegalArgumentException("Group "+id+" does not exist.");
				}
				if(!group.getAdministrator().equals(user)) {
					throw new IllegalAccessException("You are not the administrator of this group");
				}
				setupBeans(req, user, group);
				produceJSPPage(req, res, "CreatePoolJSP");
			}

		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	private GroupBean setupBeans(HttpServletRequest req, User user, Group group) throws IllegalAccessException {
		if(!group.getAdministrator().equals(user)) {
			throw new IllegalAccessException("You are not the administrator of this group");
		}
		GroupBean bean = new GroupBean(group);
		User administrator = group.getAdministrator();
		bean.setAdmin(new PlayerBean(administrator.getOID(), administrator
				.getName()), user == administrator);
		req.setAttribute("Group", bean);
		req.setAttribute("tournaments", getApp().getTournamentManager()
				.getTournamentMenu());
		req.setAttribute("scoreSystems", getApp().getScoreSystemManager()
				.getScoreSystemMenu());
		req.setAttribute("tieBreakerTypes", getApp().getUserManager()
				.getTieBreakerTypeMenu());
		return bean;
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		User user = getUser(req, res);
		try {
			UserManager um = getApp().getUserManager();

			if(req.getParameter("closePool") != null) {
                closePool(req, res, user, um);
			} else {
                changePool(req, res, user, um);
			}
		} catch (DatabaseFailure e) {
			throw new ServletException(e);
		} catch (IllegalAccessException e) {
			throw new ServletException(e);
		}
	}

    private void changePool(HttpServletRequest req, HttpServletResponse res, User user, UserManager um) throws IllegalAccessException, IOException {
        int tournamentID = Integer.parseInt(req.getParameter("tournament"));
        Tournament tournament = getApp().getTournamentManager()
                .getTournament(tournamentID);
        int scoresystemID = Integer.parseInt(req
                .getParameter("scoreSystem"));
        ScoreSystem scoreSystem = getApp().getScoreSystemManager()
                .getScoreSystem(scoresystemID);
        boolean showBracketsEarly = req.getParameter("showBracketsEarly") != null;
        int bracketLimit = Integer.parseInt(req.getParameter("bracketLimit"));
        int tieBreakerTypeID = Integer.parseInt(req.getParameter("tieBreakerType"));
        String tieBreakerQuestion = StringUtil.killWhitespace(req.getParameter("tieBreakerQuestion"));

        String name = StringUtil.killWhitespace(req.getParameter("name"));
        validateInputs(um, tournament, scoreSystem, tieBreakerTypeID, tieBreakerQuestion, name);

        int groupID = -1;
        String poolParam = StringUtil.killWhitespace(req.getParameter("pool"));
        if(poolParam != null) {
            groupID = updatePool(user, um, tournament, scoreSystem, showBracketsEarly, bracketLimit, tieBreakerTypeID, tieBreakerQuestion, name, poolParam);
        } else {
            groupID = createPool(req, user, um, tournament, scoreSystem, showBracketsEarly, bracketLimit, tieBreakerTypeID, tieBreakerQuestion, name);
        }
        res.sendRedirect(getApp().getConfig().getProperty("ShowGroupURL") + groupID);
    }

    private void validateInputs(UserManager um, Tournament tournament, ScoreSystem scoreSystem, int tieBreakerTypeID, String tieBreakerQuestion, String name) {
        if(name == null) {
            throw new IllegalArgumentException("A pool name is required.");
        }
        if(scoreSystem == null) {
            throw new IllegalArgumentException("A pool score system is required.");
        }
        if(tournament == null) {
            throw new IllegalArgumentException("A pool tournament is required.");
        }
        if(um.getTieBreakerType(tieBreakerTypeID).mustAnswer()) {
            if(tieBreakerQuestion == null) {
                throw new IllegalArgumentException("A tie breaker question is required.");
            }
        }
    }

    private int createPool(HttpServletRequest req, User user, UserManager um, Tournament tournament, ScoreSystem scoreSystem, boolean showBracketsEarly, int bracketLimit, int tieBreakerTypeID, String tieBreakerQuestion, String name) throws IllegalAccessException {
        int groupID;
        groupID = Integer.parseInt(req.getParameter("group"));
        Group group = um.getGroupObject(groupID);
        if(group == null) {
            throw new IllegalArgumentException("A group is required.");
        }
        if(!group.getAdministrator().equals(user)) {
            throw new IllegalAccessException("You are not the administrator of this group");
        }
        um.createPool(name, group, tournament, scoreSystem, bracketLimit, showBracketsEarly, tieBreakerTypeID, tieBreakerQuestion);
        return groupID;
    }

    private int updatePool(User user, UserManager um, Tournament tournament, ScoreSystem scoreSystem, boolean showBracketsEarly, int bracketLimit, int tieBreakerTypeID, String tieBreakerQuestion, String name, String poolParam) throws IllegalAccessException {
        int groupID;
        int poolID = Integer.parseInt(poolParam);
        Pool pool = um.getPoolObject(poolID);
        if(!pool.getGroup().getAdministrator().equals(user)) {
            throw new IllegalAccessException("You are not the administrator of this group");
        }
        groupID = pool.getGroup().getId();
        um.updatePool(pool, name, tournament, scoreSystem, bracketLimit, showBracketsEarly,
                tieBreakerTypeID, tieBreakerQuestion);
        return groupID;
    }

    private void closePool(HttpServletRequest req, HttpServletResponse res, User user, UserManager um) throws IllegalAccessException, IOException {
        String poolParam = StringUtil.killWhitespace(req.getParameter("pool"));
        if(poolParam != null) {
            int poolID = Integer.parseInt(poolParam);
            Pool pool = um.getPoolObject(poolID);
            if(!pool.getGroup().getAdministrator().equals(user)) {
                throw new IllegalAccessException("You are not the administrator of this group");
            }
            um.closePool(pool, req.getParameter("tieBreakerAnswer"));
            res.sendRedirect(getApp().getConfig().getProperty("MyTournamentURL")+"?request=show&type=pool&id=" + poolParam);
        } else {
            res.sendRedirect(getApp().getConfig().getProperty("MyTournamentURL"));
        }
    }
}