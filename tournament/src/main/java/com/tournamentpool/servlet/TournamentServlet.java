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
 * Created on Feb 20, 2003
 */
package com.tournamentpool.servlet;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.application.TournamentApp;
import com.tournamentpool.beans.PlayerBean;
import com.tournamentpool.domain.User;

/**
 * @author avery
 */
public class TournamentServlet extends HttpServlet {

	/**
	 *
	 */
	private static final long serialVersionUID = 5792267846977299712L;

	protected TournamentApp getApp() {
		return ((SingletonProvider)getServletContext().getAttribute("app")).getSingleton();
	}

	public User getUser(HttpServletRequest req, HttpServletResponse res) throws ServletException {
		try {
			String userID = null;
			String auth = null;
			Cookie[] cookies = req.getCookies();
			if(cookies != null){
				for (Cookie cookie : cookies) {
					if("userID".equals(cookie.getName())) {
						userID = cookie.getValue();
					} else if("auth".equals(cookie.getName())) {
						auth = cookie.getValue();
					}
				}
			}
			if("true".equals(req.getParameter("logoff"))) {
				res.addCookie(new Cookie("auth", null));
				return null;
			}
			if(userID != null) {
				return getApp().getUserManager().getUser(userID, auth);
			} else return null;
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	protected com.tournamentpool.domain.Tournament lookupTournament(
			HttpServletRequest req) {
		String tournamentOIDString = req.getParameter("tournament");
		int tournamentOid = Integer.parseInt(tournamentOIDString);
		com.tournamentpool.domain.Tournament tournament = 
			getApp().getTournamentManager().getTournament(tournamentOid);
		if(tournament == null) {
			throw new IllegalArgumentException("Tournament "+tournamentOid+" is not valid");
		}
		return tournament;
	}

	protected void produceJSPPage(HttpServletRequest req, HttpServletResponse res, String key) throws ServletException, IOException {
		Properties config2 = getApp().getConfig();
		req.setAttribute("config", config2);
		if(req.getAttribute("Player") == null) {
			User user = getUser(req, res);
			if(user != null) {
				PlayerBean playerBean = new PlayerBean(user.getOID(), user.getName(), user.isSiteAdmin());
				req.setAttribute("Player", playerBean);
			}
		}
		req.getRequestDispatcher(
			config2.getProperty(key)
		).forward(req, res);
	}

	protected int getInt(HttpServletRequest req, String key, int defaultValue) {
		String value = killWhitespace(req.getParameter(key));
		if(value != null) {
			return Integer.parseInt(value);
		}
		return defaultValue;
	}

	protected String killWhitespace(String value) {
	//	System.out.println(value);
		if(value != null) {
			value = value.trim();
			if(value.length() == 0) {
				value = null;
			}
		}
		return value;
	}

	protected Date getDate(HttpServletRequest req) {
		int month = Integer.parseInt(req.getParameter("month"))-1;// java.util.Calendar uses 0 based months
		int day = Integer.parseInt(req.getParameter("day"));
		int year = Integer.parseInt(req.getParameter("year"));
		int hour = Integer.parseInt(req.getParameter("hour"));
		int minute = Integer.parseInt(req.getParameter("minute"));
		boolean am = "AM".equals(req.getParameter("ampm"));
		String timezone = req.getParameter("timezone");
		int hourOfDay = hour + (am ? 0 : 12);

		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(timezone));
		cal.set(year, month, day, hourOfDay, minute);
		return cal.getTime();
	}

	protected int[] convertToIntegers(String[] parameterValues) {
		int[] ints = new int[parameterValues.length];
		for (int i = 0; i < parameterValues.length; i++) {
			ints[i] = Integer.parseInt(parameterValues[i]);
		}
		return ints;
	}
}
