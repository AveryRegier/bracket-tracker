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
 * Created on Feb 22, 2003
 */
package com.tournamentpool.servlet;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import utility.StringUtil;

import com.tournamentpool.domain.User;

/**
 * Redirects to the LoginServlet if this user is not logged in.
 * 
 * @author avery
 */
public class RequiresLoginServlet extends TournamentServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9027029330106923079L;

	/**
	 * Redirects to the LoginServlet if the user is not logged in.
	 */
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		User user = null;
		try {
			user = getUser(req, resp);
		} catch (ServletException e) {
			// probably just a service restart
		}
		if(user == null) {
			String queryString = req.getQueryString();
			if(queryString != null) {
				String lookFor = "logoff=true";
				int index = queryString.indexOf(lookFor);
				if(index != -1) {
					queryString = queryString.substring(0, index) + queryString.substring(index+lookFor.length());
				}
				queryString = StringUtil.killWhitespace(queryString);
			}

			resp.sendRedirect(getApp().getConfig().getProperty("LoginURL")+
				"?redirect="+
				URLEncoder.encode(req.getRequestURL().toString()+(queryString != null ? "?"+queryString : ""), "UTF-8"));
		} else {
			super.service(req, resp);
		}
	}

}
