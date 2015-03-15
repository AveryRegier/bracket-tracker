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
 * Created on Mar 6, 2005
 */
package com.tournamentpool.servlet;

import com.tournamentpool.beans.GroupBean;
import com.tournamentpool.beans.PlayerBean;
import com.tournamentpool.broker.sql.DatabaseFailure;
import com.tournamentpool.broker.sql.get.GroupAvailablePlayersGetBroker;
import com.tournamentpool.domain.Group;
import com.tournamentpool.domain.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PlayerMaintenance extends RequiresLoginServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1126468740693470907L;

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			int id = Integer.parseInt(req.getParameter("group"));
			Group group = getApp().getUserManager().getGroup(id);
			if(group == null) {
				throw new IllegalArgumentException("Group "+id+" does not exist.");
			}
			User user = getUser(req, res);
			if("remove".equalsIgnoreCase(req.getParameter("request"))) {
				int playerOID = Integer.parseInt(req.getParameter("playerID"));
				User player = getApp().getUserManager().getUser(playerOID);
				if(player == null) {
					throw new ServletException("Player does not exist");
				}
				if(!group.getMembers().contains(player)) {
					throw new ServletException(player.getName()+" is not a member of "+group.getName());
				}
				// the following will check access permissions
				if(!group.removeMember(user, player, getApp().getSingletonProvider())) {
					throw new ServletException("Cannot remove "+player.getName()+" from "+group.getName());
				}
				res.sendRedirect(req.getHeader("Referer"));
			} else {
				if(group.getAdministrator() != user) {
					throw new ServletException("You are not allowed to edit this group.");
				}
				GroupBean bean = new GroupBean(group);
			//	bean.setMembers(group.getMembers());
				User administrator = group.getAdministrator();
				bean.setAdmin(new PlayerBean(administrator.getOID(), administrator.getName()), user == administrator);
                bean.setMayAddSubGroup(group.mayAddSubGroup(user));
			//	bean.setPools(group.getPools());
				req.setAttribute("Group", bean);
				req.setAttribute("Players", new GroupAvailablePlayersGetBroker(getApp().getSingletonProvider(), group).getPlayers());
				produceJSPPage(req, res, "AddPlayersToGroupJSP");
			}
		} catch (DatabaseFailure e) {
			throw new ServletException(e);
		}
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			int groupID = Integer.parseInt(req.getParameter("group"));
			Group group = getApp().getUserManager().getGroup(groupID);
			if(group.getAdministrator() != getUser(req, res)) {
				throw new ServletException("You are not allowed to edit this group.");
			}
			int[] playerIDs = convertToIntegers(req.getParameterValues("player"));
			group.addMembers(playerIDs);
			res.sendRedirect(getApp().getConfig().getProperty("ShowGroupURL")+req.getParameter("group"));
		} catch (DatabaseFailure e) {
			throw new ServletException(e);
		}
	}
}
