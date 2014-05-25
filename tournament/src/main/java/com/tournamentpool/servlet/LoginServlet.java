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

import com.tournamentpool.broker.mail.JavaMailEmailBroker;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author avery
 */
public final class LoginServlet extends TournamentServlet {
	private static final long serialVersionUID = 8424367770116306883L;

	protected void doGet(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException
	{
        produceNonUserJSPPage(req, res, "LoginJSP");
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException
	{
		String userID = req.getParameter("uid");
		String password = req.getParameter("password");
		String auth = null;

		try {
			String url = req.getParameter("redirect");
			if(req.getParameter("resetPassword") != null) {
				resetPassword(req, userID);
				res.sendRedirect(encodeLoginURL(url));
			} else if((auth = getApp().getUserManager().authenticate(userID, password)) != null) {
				cleanupLogin(req, res, userID, auth);
				res.sendRedirect(findRedirect(url));
			} else {
				loginFailed(req, userID);
				res.sendRedirect(encodeLoginURL(url));
			}
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	private String encodeLoginURL(String url)
			throws UnsupportedEncodingException {
		return getApp().getConfig().getProperty("LoginURL")+
			"?redirect="+
			URLEncoder.encode(url, "UTF-8");
	}

	private String findRedirect(String url) {
		String redirect = url;
		if(redirect == null) {
			redirect = getApp().getConfig().getProperty("defaultURL");
		}
		return redirect;
	}

	private void loginFailed(HttpServletRequest req, String userID) {
		req.getSession().setAttribute("uid", userID);
		req.getSession().setAttribute("loginFailed", "true");
		req.getSession().removeAttribute("passwordReset");
		req.getSession().removeAttribute("resetFailed");
		if(getApp().getUserManager().userHasEmail(userID)) {
			req.getSession().setAttribute("emailAvailable", "true");
		} else {
			req.getSession().removeAttribute("emailAvailable");
		}
	}

	private void cleanupLogin(HttpServletRequest req, HttpServletResponse res,
			String userID, String auth) {
		res.addCookie(new Cookie("userID", userID));
		res.addCookie(new Cookie("auth", auth));

		req.getSession().removeAttribute("uid");
		req.getSession().removeAttribute("loginFailed");
		req.getSession().removeAttribute("passwordReset");
		req.getSession().removeAttribute("resetFailed");
	}

	private void resetPassword(HttpServletRequest req, String userID)
			throws UnsupportedEncodingException, MessagingException {
		JavaMailEmailBroker.setBaseURL(req.getScheme(), req.getServerName(), req.getServerPort());
		getApp().getUserManager().resetPassword(userID);
		req.getSession().setAttribute("uid", userID);
		req.getSession().removeAttribute("resetFailed");
		req.getSession().removeAttribute("loginFailed");
		req.getSession().setAttribute("passwordReset", "true");
	}
}
