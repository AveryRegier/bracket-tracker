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
 * Created on Mar 4, 2005
 */
package com.tournamentpool.servlet;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RegistrationServlet extends TournamentServlet {
	/**
	 *
	 */
	private static final long serialVersionUID = -7010142433889289326L;

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		produceJSPPage(req, res, "RegistrationJSP");
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String name = req.getParameter("name");
		String userID = req.getParameter("uid");
		String password = req.getParameter("password");
		String email = req.getParameter("email");
		String auth = null;

		try {
			if((auth = getApp().getUserManager().registerUser(userID, password, name, email)) != null) {
				// set the cookie
				res.addCookie(new Cookie("userID", userID));
				res.addCookie(new Cookie("auth", auth));

				req.getSession().removeAttribute("name");
				req.getSession().removeAttribute("loginFailed");

				String redirect = req.getParameter("redirect");
				if(redirect == null) {
					getApp().getConfig().getProperty("defaultURL");
				}
				res.sendRedirect(redirect);
			} else {
				req.getSession().setAttribute("name", name);
				req.getSession().setAttribute("loginFailed", "true");
				res.sendRedirect(getApp().getConfig().getProperty("RegistrationURL")+
					"?redirect="+
					URLEncoder.encode(req.getParameter("redirect"), "UTF-8"));
			}
		} catch (Exception e) {
			throw new ServletException(e);
		}

	}
}
