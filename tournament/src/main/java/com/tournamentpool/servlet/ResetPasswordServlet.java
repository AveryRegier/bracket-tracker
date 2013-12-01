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
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tournamentpool.domain.User;
import com.tournamentpool.domain.UserManager;

/**
 * @author avery
 */
public final class ResetPasswordServlet extends TournamentServlet {
	private static final long serialVersionUID = 8424367770116306883L;

	protected void doGet(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException
	{
		try {
			// validate the auth is correct
			String userID = req.getParameter("uid");
			String auth = req.getParameter("auth");
//			if(auth != null) auth = URLEncoder.encode(auth, "UTF-8");
			User user = getApp().getUserManager().getUser(userID, auth);

			if(user != null) {
				req.getSession().setAttribute("name", user.getName());
				req.getSession().setAttribute("email", user.getEmail());
				// if so, provide opportunity to change
				produceJSPPage(req, res, "ResetPasswordJSP");
			} else {
				// else throw up and error on the login page
				req.getSession().setAttribute("uid", userID);
				req.getSession().setAttribute("resetFailed", "true");
				req.getSession().removeAttribute("passwordReset");
				req.getSession().removeAttribute("loginFailed");
				if(getApp().getUserManager().userHasEmail(userID)) {
					req.getSession().setAttribute("emailAvailable", "true");
				} else {
					req.getSession().removeAttribute("emailAvailable");
				}
				res.sendRedirect(getApp().getConfig().getProperty("LoginURL")+
						"?redirect="+URLEncoder.encode(
						getApp().getConfig().getProperty("MyTournamentURL"), "UTF-8"));
			}
		} catch (SQLException e) {
			throw new ServletException(e);
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException
	{
		// validate the auth is correct again, because otherwise we have a security hole.

		String userID = req.getParameter("uid");
		String password = req.getParameter("password");
		String auth = req.getParameter("auth");

		try {
			String url = req.getParameter("redirect");
			if(url == null) {
				url = getApp().getConfig().getProperty("MyTournamentURL");
			}
			UserManager userManager = getApp().getUserManager();
			// if the auth doesn't check out, this will return null
			User user = userManager.getUser(userID, auth);
			if(user != null) {
				auth = userManager.resetPassword(userID, auth, password);

				if(auth != null) {

					// make sure to reset the auth after the password change.
					// set the cookie
					res.addCookie(new Cookie("userID", userID));
					res.addCookie(new Cookie("auth", auth));

					req.getSession().removeAttribute("uid");
					req.getSession().removeAttribute("loginFailed");
					req.getSession().removeAttribute("resetFailed");

					res.sendRedirect(url);
					return;
				}
			}
			req.getSession().setAttribute("uid", userID);
			req.getSession().removeAttribute("loginFailed");
			req.getSession().setAttribute("resetFailed", "true");
			req.setAttribute("emailAvailable",
					userManager.userHasEmail(userID) ? "true" : "false");
			res.sendRedirect(getApp().getConfig().getProperty("LoginURL")+
				"?redirect="+
				URLEncoder.encode(url, "UTF-8"));
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

}
