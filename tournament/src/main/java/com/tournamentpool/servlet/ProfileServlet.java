/* 
Copyright (C) 2003-2014 Avery J. Regier.

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

import com.tournamentpool.broker.sql.DatabaseFailure;
import com.tournamentpool.domain.User;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;

public class ProfileServlet extends RequiresLoginServlet {
	/**
	 *
	 */
	private static final long serialVersionUID = -7010142433889289326L;

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        User user = getUser(req, res);
        req.setAttribute("name", user.getName());
        req.setAttribute("uid", user.getID());
        req.setAttribute("email", user.getEmail());
        HttpSession session = req.getSession(true);
        if(session.getAttribute("redirect") == null) {
            session.setAttribute("redirect", req.getHeader("referer"));
        }
        produceJSPPage(req, res, "ProfileJSP");
        session.removeAttribute("userIdTaken");
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        User user = getUser(req, res);
        String name = req.getParameter("name");
		String userID = req.getParameter("uid");
		String email = req.getParameter("email");

		try {
			getApp().getUserManager().updateProfile(user, userID, name, email);
            req.getSession().removeAttribute("userIdTaken");

            res.addCookie(new Cookie("userID", userID));

            String redirect = getRedirect(req);
            res.sendRedirect(redirect);
        } catch(DatabaseFailure e) {
            req.getSession().setAttribute("name", name);
            req.getSession().setAttribute("userIdTaken", "true");
            res.sendRedirect(getApp().getConfig().getProperty("ProfileURL")+
                    "?redirect="+
                    URLEncoder.encode(getRedirect(req), "UTF-8"));
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

    private String getRedirect(HttpServletRequest req) {
        String redirect = req.getParameter("redirect");
        if(redirect == null) {
            redirect = (String)req.getSession().getAttribute("redirect");
        }
        if(redirect == null || redirect.contains("Profile")) {
            redirect = getApp().getConfig().getProperty("defaultURL");
        }
        req.getSession().removeAttribute("redirect");
        return redirect;
    }
}
