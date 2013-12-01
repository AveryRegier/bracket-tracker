/* 
Copyright (C) 2003-2011 Avery J. Regier.

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
 * Created on Dec 15, 2004
 */
package com.tournamentpool.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import utility.StringUtil;
import utility.menu.Menu;

import com.tournamentpool.beans.BracketBean;
import com.tournamentpool.beans.PoolBean;
import com.tournamentpool.controller.GameVisitorCommon;
import com.tournamentpool.domain.Bracket;
import com.tournamentpool.domain.Pool;
import com.tournamentpool.domain.User;
import com.tournamentpool.domain.UserManager;

/**
 * @author Avery J. Regier
 */
public class AssignBracketToPool extends RequiresLoginServlet {
	/**
	 *
	 */
	private static final long serialVersionUID = -2613143797555949164L;

	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException
	{
		User user = getUser(req, res);
		try {
			int id = Integer.parseInt(req.getParameter("pool"));
			Pool pool = getApp().getUserManager().getPool(id);
			if(pool == null) {
				throw new IllegalArgumentException("Pool "+id+" is not valid");
			}

			Menu bracketsAvailableForPoolMenu = getApp().getUserManager()
				.getBracketsAvailableForPoolMenu(user, pool);
			if(bracketsAvailableForPoolMenu.getItems().hasNext()) {

				PoolBean bean = new PoolBean(pool.getOid(), pool.getName());
				bean.setTieBreaker(pool.getTieBreakerType(), pool.getTieBreakerQuestion(), pool.getTieBreakerAnswer());
				req.setAttribute("Pool", bean);
	
				req.setAttribute("brackets", bracketsAvailableForPoolMenu);

				produceJSPPage(req, res, "AssignBracketToPoolJSP");
			} else {
				String query ="?request=create&type=bracket&tournament="+pool.getTournament().getID();

				res.sendRedirect(getApp().getConfig().getProperty("MyTournamentURL")+query);
			}
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException
	{
		User user = getUser(req, res);
		try {
			UserManager um = getApp().getUserManager();

			int poolID = Integer.parseInt(req.getParameter("pool"));
			Pool pool = um.getPool(poolID);

			int bracketID = Integer.parseInt(req.getParameter("bracket"));
			Bracket bracket = user.getBracket(bracketID);
			if(bracket == null) {
				throw new IllegalAccessException("You may not assign another user's bracket to a pool");
			}
			String tieBreakerAnswer = StringUtil.killWhitespace(req.getParameter("tieBreakerAnswer"));
			if(tieBreakerAnswer == null) {
				if(pool.getTieBreakerType().mustAnswer()) {
					PoolBean bean = new PoolBean(pool.getOid(), pool.getName());
					bean.setTieBreaker(pool.getTieBreakerType(), pool.getTieBreakerQuestion(), pool.getTieBreakerAnswer());
					req.setAttribute("Pool", bean);

					BracketBean<?> bBean = new BracketBean<GameVisitorCommon.Node>();
					bBean.setOid(bracket.getOID());
					bBean.setName(bracket.getName());
					req.setAttribute("Bracket", bBean);
					produceJSPPage(req, res, "AnswerTieBreakerJSP");
					return;
				}
			}

			um.assignBracket(pool, bracket, tieBreakerAnswer);

			res.sendRedirect(getApp().getConfig().getProperty("MyTournamentURL")+"?type=pool&id="+pool.getOid());
		} catch (SQLException e) {
			throw new ServletException(e);
		} catch (IllegalArgumentException e) {
			throw new ServletException(e);
		} catch (IllegalAccessException e) {
			throw new ServletException(e);
		}
	}
}
