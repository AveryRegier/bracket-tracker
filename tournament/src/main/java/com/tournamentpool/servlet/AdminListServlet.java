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
 * Created on Mar 14, 2004
 */
package com.tournamentpool.servlet;

import com.tournamentpool.beans.*;
import com.tournamentpool.broker.sql.DatabaseFailure;
import com.tournamentpool.domain.*;
import com.tournamentpool.domain.Tournament;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author Avery J. Regier
 */
public class AdminListServlet extends RequiresLoginServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7007820777028908423L;

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException 
	{
		if(!getUser(req, res).isSiteAdmin()) {
			throw new ServletException("You are not a site administrator.");
		}

		String type = req.getParameter("type");

		try {
			if("group".equals(type)) {
				listGroups(req, res);
			} else if("player".equals(type)) {
				if("delete".equalsIgnoreCase(req.getParameter("request"))) {
					deletePlayer(req, res);
				} else {
					listPlayers(req, res);
				}
			} else if("pool".equals(type)) {
				listPools(req, res);
			} else if("bracket".equals(type)) {
				listBrackets(req, res);
			} else if("team".equals(type)) {
				if("delete".equalsIgnoreCase(req.getParameter("request"))) {
					deleteTeam(req, res);
				} else if("remove".equalsIgnoreCase(req.getParameter("request"))) {
					removeTeam(req, res);
				} else{
					listTeams(req, res);
				}
			} else if("league".equals(type)) {
				if("delete".equalsIgnoreCase(req.getParameter("request"))) {
					deleteLeague(req, res);
				} else {
					listLeagues(req, res);
				}
			} else { // if we haven't configured the page, just redirect to the administration main screen
				produceJSPPage(req, res, "AdminJSP");
			}
		} catch (DatabaseFailure e) {
			throw new ServletException(e);
		}
	}

	private void removeTeam(HttpServletRequest req, HttpServletResponse res) {
		// TODO Auto-generated method stub

	}

	private void deleteLeague(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		User user = getUser(req, res);
		League league = getApp().getTeamManager().getLeague(Integer.parseInt(req.getParameter("id")));
		league.delete(user, getApp().getSingletonProvider());
		res.sendRedirect(req.getHeader("Referer"));
	}

	private void listLeagues(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		User user = getUser(req, res);
		
		String teamString = req.getParameter("team");
		Team team = null;
		if(teamString != null) {
			team = getApp().getTeamManager().getTeam(Integer.parseInt(teamString));
			if(team != null) {
				req.setAttribute("Team", new TeamBean(team));
			}
		}
		
		// for efficiency, just run these counts once
		Map<League, int[]> tournamentCounter = new HashMap<League, int[]>();
		for(Tournament tournament: getApp().getTournamentManager().getTournaments()) {
			League league = tournament.getLeague();
			Object counter = tournamentCounter.get(league);
			if(counter == null) {
				tournamentCounter.put(league, new int[] {1});
			} else {
				((int[])counter)[0]++;
			}
		}

		List<LeagueBean> leagueBeans = new ArrayList<LeagueBean>();
		for (Iterator<League> iterator = getApp().getTeamManager().getLeagues(); iterator.hasNext();) {
			League league = iterator.next();
			if(team == null || league.hasTeam(team)) {
				LeagueBean leagueBean = new LeagueBean(league);
				int[] counter = tournamentCounter.get(league);
				leagueBean.setNumTournaments(counter != null ? counter[0] : 0);
				leagueBean.setNumTeams(league.getNumTeams());
				leagueBean.setMayDelete(league.isDeletable(user, getApp().getSingletonProvider()));
				leagueBeans.add(leagueBean);
			}
		}
		req.setAttribute("Leagues", leagueBeans);
		produceJSPPage(req, res, "ListLeaguesJSP");
	}

	private void listTeams(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		User user = getUser(req, res);
		
		String leagueString = req.getParameter("league");
		League requestedLeague = null;
		if(leagueString != null) {
			requestedLeague = getApp().getTeamManager().getLeague(Integer.parseInt(leagueString));
			if(requestedLeague != null) {
				req.setAttribute("League", new LeagueBean(requestedLeague));
			}
		}
	
		// for efficiency, just run these counts once
		Map<Team, int[]> teamCounter = new HashMap<Team, int[]>();
		Iterator<League> leagues = getApp().getTeamManager().getLeagues();
		while (leagues.hasNext()) {
			League league = leagues.next();
			Iterator<Team> teams = league.getTeams();
			while (teams.hasNext()) {
				Team team = teams.next();
				Object counter = teamCounter.get(team);
				if(counter == null) {
					teamCounter.put(team, new int[] {1});
				} else {
					((int[])counter)[0]++;
				}
			}
		}

		List<TeamBean> teamBeans = new ArrayList<TeamBean>();
		Iterator<Team> iterator = requestedLeague != null ? requestedLeague.getTeams() : getApp().getTeamManager().getTeams();
		while(iterator.hasNext()) {
			Team team = iterator.next();
			TeamBean teamBean = new TeamBean(team);
			int[] counter = teamCounter.get(team);
			teamBean.setNumLeagues(counter != null ? counter[0] : 0);
			teamBean.setMayDelete(team.isDeletable(user, getApp().getSingletonProvider()));
            teamBean.setNumSynonyms(team.getSynonyms().size());
			teamBeans.add(teamBean);
		}
		req.setAttribute("Teams", teamBeans);
		produceJSPPage(req, res, "ListTeamsJSP");
	}

	private void deleteTeam(HttpServletRequest req, HttpServletResponse res) 
		throws NumberFormatException, ServletException, IOException
	{
		User user = getUser(req, res);
		Team team = getApp().getTeamManager().getTeam(Integer.parseInt(req.getParameter("id")));
		team.delete(user, getApp().getSingletonProvider());
		res.sendRedirect(req.getHeader("Referer"));
	}


	private void deletePlayer(HttpServletRequest req, HttpServletResponse res) throws ServletException {
		try {
			UserManager userManager = getApp().getUserManager();
			User toRemove = userManager.getUser(Integer.parseInt(req
					.getParameter("id")));
			userManager.removeUser(getUser(req, res), toRemove);
			res.sendRedirect(getApp().getConfig().getProperty("ListPlayersURL"));
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	private void listBrackets(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		User user = getApp().getUserManager().getUser(Integer.parseInt(req.getParameter("playerid")));
		PlayerBean bean = new PlayerBean(user.getOID(), user.getName(), user.isSiteAdmin());
		bean.setBrackets(user.getBrackets(), getUser(req, res));
		req.setAttribute("Player", bean);
		produceJSPPage(req, res, "ListBracketsJSP");
	}

	private void listPools(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		TreeSet<PoolBean> poolBeans = new TreeSet<PoolBean>();
		Iterator<Pool> pools = getApp().getUserManager().getPools();
		User user = getUser(req, res);
		while (pools.hasNext()) {
			Pool pool = pools.next();
			if(pool != null) {
				PoolBean poolBean = new PoolBean(pool.getOid(), pool.getName());
				poolBean.setGroup(new GroupBean(pool.getGroup()));
				poolBean.setBrackets(pool.getBrackets().iterator(), user);
				poolBean.setShowBracketsEarly(pool.isShowBracketsEarly());
				poolBean.setBracketLimit(pool.getBracketLimit());
				poolBean.setScoreSystem(pool.getScoreSystem());
				poolBean.setOwner(pool.getOwner());
				Tournament tournament = pool.getTournament();
				poolBean.setTournament(new TournamentBean(tournament, false, false, false, 
						tournament.getStartTime(), tournament.getLastUpdated(), false));
				poolBean.setTieBreaker(pool.getTieBreakerType(), pool.getTieBreakerQuestion(), pool.getTieBreakerAnswer());
				poolBean.setMayDelete(pool.mayDelete(user));
				poolBeans.add(poolBean);
			}
		}

		req.setAttribute("Pools", poolBeans);
		produceJSPPage(req, res, "ListPoolsJSP");
	}

	private void listPlayers(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		TreeSet<PlayerBean> playerBeans = new TreeSet<PlayerBean>();
		User user = getUser(req, res);
		UserManager userManager = getApp().getUserManager();
		Iterator<User> members = userManager.getPlayers();
		while (members.hasNext()) {
			User player = members.next();
			PlayerBean playerBean = new PlayerBean(player.getOID(), player.getName());
            playerBean.setLoginId(player.getID());
			playerBean.setBrackets(player.getBrackets(), user);
			playerBean.setGroups(player.getGroups());
			playerBean.setMayDelete(userManager.mayRemoveUser(user, player));
			playerBeans.add(playerBean);
		}

		req.setAttribute("Players", playerBeans);
		produceJSPPage(req, res, "ListPlayersJSP");
	}

	/**
	 * @param req
	 * @param res
	 * @throws IOException 
	 * @throws ServletException 
	 */
	private void listGroups(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		User user = getUser(req, res);
		List<GroupBean> groupBeans = new ArrayList<GroupBean>();
		Iterator<Group> groups = getApp().getUserManager().getGroups();
		while (groups.hasNext()) {
			Group group = groups.next();
			GroupBean groupBean = new GroupBean(group);
			User administrator = group.getAdministrator();
			groupBean.setAdmin(new PlayerBean(administrator.getOID(), administrator.getName()), false);
			groupBean.setPools(group.getMyPools(), user);
			groupBean.setMembers(group.getMyMembers(), user, group);
			groupBean.setMayDelete(group.mayDelete(user));
			groupBeans.add(groupBean);
		}
		req.setAttribute("Groups", groupBeans);
		produceJSPPage(req, res, "ListGroupsJSP");
	};
}
