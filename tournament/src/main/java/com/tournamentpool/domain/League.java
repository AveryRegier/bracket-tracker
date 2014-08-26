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

package com.tournamentpool.domain;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.broker.sql.delete.LeagueDeleteBroker;
import com.tournamentpool.broker.sql.delete.LeagueTeamDeleteBroker;
import utility.domain.Reference;
import utility.menu.Menu;
import utility.menu.reference.ReferenceMenu;

import java.util.*;

public class League implements Reference, Comparable<League> {

	private final Integer id;
	private final String name;
	private final Map<Object, Team> teams = new TreeMap<>();

	public League(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getLeagueID() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Object getID() {
		return id;
	}

	void loadTeam(Team team) {
		teams.put(team.getID(), team);
	}
	
	public Menu getTeamMenu() {
		final LinkedHashMap<Object, Team> sortedTeams = new LinkedHashMap<>();
		LinkedList<Team> sortedList = new LinkedList<>(teams.values());
		Collections.sort(sortedList, (a, b) -> {
            if(a == b) return 0;
            if(a == null) return -1;
            if(b == null) return 1;
            String aName = a.getName();
            String bName = b.getName();
            if(aName != null) {
                return aName.compareTo(bName);
            } else return null == bName ? 0 : -1;
        });
		for (Team team: sortedList) {
			sortedTeams.put(team.getID(), team);
		}
		return new ReferenceMenu<Team>("teams") {
			protected Map<Object, Team> getReferences() {
				return sortedTeams;
			}
		};
	}

	public int compareTo(League o) {
		return name.compareTo(o.name);
	}

	public void removeTeam(Team team) {
		teams.remove(team.getID());
	}

	public int getNumTeams() {
		return teams.size();
	}
	
	public Iterator<Team> getTeams() {
		return teams.values().iterator();
	}

	public boolean isDeletable(User user, SingletonProvider sp) {
		if(!user.isSiteAdmin()) return false;
		for(Tournament tournament: sp.getSingleton().getTournamentManager().getTournaments()) {
			if(tournament.getLeague() == this) return false;
		}
		return true;
	}

	public boolean hasTeam(Team team) {
		return teams.containsKey(team.getID());
	}

	public void delete(User user, SingletonProvider sp) {
		if(isDeletable(user, sp)) {
			for (Team team: teams.values())  {
				new LeagueTeamDeleteBroker(sp, this, team).execute();
			}
			new LeagueDeleteBroker(sp, this).execute();
		}
	}
}
