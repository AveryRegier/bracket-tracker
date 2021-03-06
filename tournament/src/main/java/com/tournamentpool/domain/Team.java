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
 * Created on Feb 20, 2003
 */
package com.tournamentpool.domain;

//import java.net.URI;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.broker.sql.delete.LeagueTeamDeleteBroker;
import com.tournamentpool.broker.sql.delete.TeamDeleteBroker;
import com.tournamentpool.broker.sql.delete.TeamSynonymDeleteBroker;
import utility.domain.Reference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author avery
 */
public class Team implements Reference, Comparable<Team> {
	public static final Team UNKNOWN = new Team(-1, "Unknown");
	private final int teamOID;
	private final String name;
	private final List<String> synonymns = new ArrayList<>();
	
//	private String conference;
//	private URI graphic;
//	
//	private Record homeRecord;
//	private Record conferenceRecord;
//	private Record postSeasonRecord;
//	private Record seasonRecord;
	
	/**
	 * @param teamOID
	 * @param name
	 */
	public Team(int teamOID, String name) {
		this.teamOID = teamOID;
		this.name = name;
	}
	
	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public int getTeamOID() {
		return teamOID;
	}

	public Object getID() {
		return teamOID;
	}

	public int compareTo(Team o) {
		return name.compareTo(o.name);
	}

	public boolean isDeletable(User user, SingletonProvider sp) {
		if(!user.isSiteAdmin()) return false;
        for(Tournament tournament: sp.getSingleton().getTournamentManager().getTournaments()) {
            if(tournament.hasTeam(this)) return false;
		}
		return true;
	}

	public void delete(User user, SingletonProvider sp) {
		if(isDeletable(user, sp)) {
			Iterator<League> leagues = sp.getSingleton().getTeamManager().getLeagues();
			while (leagues.hasNext()) {
				League league = leagues.next();
				if(league.hasTeam(this)) {
					new LeagueTeamDeleteBroker(sp, league, this).execute();
				}
			}
            for(String name: new ArrayList<>(synonymns)) {
                new TeamSynonymDeleteBroker(sp, this, name).execute();
            }
			new TeamDeleteBroker(sp, this).execute();
		}
	}

	public void addSynonym(String name2) {
		synonymns.add(name2);
	}
	
	public Collection<String> getNames() {
        ArrayList<String> names = new ArrayList<>(synonymns);
        names.add(0, name);
        return names;
	}

	public boolean anyNamesMatch(String winner) {
		for (String name : getNames()) {
			if(winner.equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

    public List<String> getSynonyms() {
        return synonymns;
    }

    public void removeSynonym(String name) {
        synonymns.remove(name);
    }
}
