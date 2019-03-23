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

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.application.TournamentApp;
import com.tournamentpool.beans.PlayerBean;
import com.tournamentpool.domain.User;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author avery
 */
public class TournamentServlet extends HttpServlet {

	/**
	 *
	 */
	private static final long serialVersionUID = 5792267846977299712L;

	TournamentApp getApp() {
		return ((SingletonProvider)getServletContext().getAttribute("app")).getSingleton();
	}

	User getUser(HttpServletRequest req, HttpServletResponse res) throws ServletException {
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

	com.tournamentpool.domain.Tournament lookupTournament(
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

	void produceJSPPage(HttpServletRequest req, HttpServletResponse res, String key) throws ServletException, IOException {
        if(req.getAttribute("Player") == null) {
            User user = getUser(req, res);
            if(user != null) {
                PlayerBean playerBean = new PlayerBean(user.getOID(), user.getName(), user.isSiteAdmin());
                req.setAttribute("Player", playerBean);
            }
        }
        produceNonUserJSPPage(req, res, key);
	}

    void produceNonUserJSPPage(HttpServletRequest req, HttpServletResponse res, String key) throws ServletException, IOException {
        Properties config2 = getApp().getConfig();
        req.setAttribute("config", config2);
        req.getRequestDispatcher(
            config2.getProperty(key)
        ).forward(req, res);
    }

    int getInt(HttpServletRequest req, String key, int defaultValue) {
		String value = killWhitespace(req.getParameter(key));
		if(value != null) {
			return Integer.parseInt(value);
		}
		return defaultValue;
	}

	public static String killWhitespace(String value) {
		if(value != null) {
			value = value.trim();
			if(value.length() == 0) {
				value = null;
			}
		}
		return value;
	}

	Timestamp getDate(HttpServletRequest req) {
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
		return new Timestamp(cal.getTime().getTime());
	}

    void setDate(Date date, HttpServletRequest req) {
        req.setAttribute("timezones", TimeZone.getAvailableIDs());

        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH)+1;
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int year = cal.get(Calendar.YEAR);
        int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        boolean am = cal.get(Calendar.AM_PM) == Calendar.AM;
        TimeZone timeZone = cal.getTimeZone();

        req.setAttribute("month", month);
        req.setAttribute("day", dayOfMonth);
        req.setAttribute("year", year);
        req.setAttribute("hour", am ? hourOfDay : hourOfDay - 12);
        req.setAttribute("minute", minute < 10 ? "0"+minute : minute);
        req.setAttribute("ampm", am ? "AM" : "PM");
        req.setAttribute("timezone", timeZone.getID());
    }

	int[] convertToIntegers(String[] parameterValues) {
		int[] ints = new int[parameterValues.length];
		for (int i = 0; i < parameterValues.length; i++) {
			ints[i] = Integer.parseInt(parameterValues[i]);
		}
		return ints;
	}
}
