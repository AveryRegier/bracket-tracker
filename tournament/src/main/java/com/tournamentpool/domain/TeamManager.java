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
 * Created on Mar 13, 2004
 */
package com.tournamentpool.domain;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.application.SingletonProviderHolder;
import com.tournamentpool.broker.sql.delete.TeamSynonymDeleteBroker;
import com.tournamentpool.broker.sql.insert.LeagueInsertBroker;
import com.tournamentpool.broker.sql.insert.LeagueTeamInsertBroker;
import com.tournamentpool.broker.sql.insert.TeamInsertBroker;
import com.tournamentpool.broker.sql.insert.TeamSynonymInsertBroker;
import utility.menu.Menu;
import utility.menu.reference.ReferenceMenu;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Avery Regier
 */
public class TeamManager extends SingletonProviderHolder {
	private Map<Integer, Team> teams = new HashMap<Integer, Team>();
	private Map<Integer, League> leagues = new HashMap<Integer, League>();
	
	/**
	 * @param sp
	 */
	public TeamManager(SingletonProvider sp) {
		super(sp);
	}

	/**
	 * @param teamOID
	 * @param name
	 */
	public void loadTeam(int teamOID, String name) {
		teams.put(new Integer(teamOID), new Team(teamOID, name));
	}

	/**
	 * @param teamOID
	 * @return
	 */
	public Team getTeam(int teamOID) {
		return (Team)teams.get(new Integer(teamOID));
	}

	public int getNumTeams() {
		return teams.size();
	}

	public Menu getTeamMenu() {
		return new ReferenceMenu<Team>("teams") {
			protected Map<Integer, Team> getReferences() {
				return teams;
			}
		};
	}
	
	public Iterator<Team> getTeams() {
		return teams.values().iterator();
	}

	/**
	 * @param leagueOID
	 * @param name
	 */
	public void loadLeague(int leagueOID, String name) {
		leagues.put(new Integer(leagueOID), new League(leagueOID, name));
	}

	/**
	 * @param leagueOID
	 * @return
	 */
	public League getLeague(int leagueOID) {
		return leagues.get(new Integer(leagueOID));
	}

	public void loadLeagueTeamAssociation(int leagueOID, int teamOID) {
		getLeague(leagueOID).loadTeam(getTeam(teamOID));
	}
	
	public int getNumLeagues() {
		return leagues.size();
	}

	public Menu getLeagueMenu() {
		return new ReferenceMenu<League>("leagues") {
			protected Map<Integer, League> getReferences() {
				return leagues;
			}
		};
	}
	
	public Iterator<League> getLeagues() {
		return leagues.values().iterator();
	}

	public Team createTeam(String name) throws SQLException {
		TeamInsertBroker teamInsertBroker = new TeamInsertBroker(sp, name);
		teamInsertBroker.execute();
		return getTeam(teamInsertBroker.getOID());
	}

	public League createLeague(String name) throws SQLException {
		LeagueInsertBroker leagueInsertBroker = new LeagueInsertBroker(sp, name);
		leagueInsertBroker.execute();
		return getLeague(leagueInsertBroker.getOID());
	}

	public void associateTeamToLeague(League league, Team team) throws SQLException {
		new LeagueTeamInsertBroker(sp, league, team).execute();
	}

	public void applyDelete(Team team) {
		teams.remove(team.getID());
	}

	public void applyDelete(League league) {
		leagues.remove(league.getID());
	}

	public void loadTeamSynonym(int teamOID, String name) {
		teams.get(teamOID).addSynonym(name);
	}

    public void createTeamSynonym(Team team, String name) throws SQLException {
        new TeamSynonymInsertBroker(sp, team, name).execute();
    }

    public void removeTeamSynonym(Team team, String name) throws SQLException {
        new TeamSynonymDeleteBroker(sp, team, name).execute();
    }
}
