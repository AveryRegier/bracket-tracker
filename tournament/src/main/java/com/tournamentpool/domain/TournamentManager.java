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
 * Created on Mar 13, 2004
 */
package com.tournamentpool.domain;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.application.SingletonProviderHolder;
import com.tournamentpool.broker.sql.insert.SeedTeamInsertBroker;
import com.tournamentpool.broker.sql.insert.SubTournamentInsertBroker;
import com.tournamentpool.broker.sql.insert.TournamentAdminInsertBroker;
import com.tournamentpool.broker.sql.insert.TournamentInsertBroker;
import com.tournamentpool.broker.sql.update.TournamentUpdateBroker;
import utility.menu.Menu;
import utility.menu.reference.ReferenceMenu;

import java.sql.Timestamp;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author Avery J. Regier
 */
public class TournamentManager extends SingletonProviderHolder {
	private Map<Integer, Tournament> tournaments = new HashMap<Integer, Tournament>();

	/**
	 * @param sp
	 */
	public TournamentManager(SingletonProvider sp) {
		super(sp);
	}

	/**
	 * @param tournamentOID
	 * @param name
	 * @param tournamentTypeOID
	 * @param leagueOID
	 * @param startTime
	 * @param parentTournamentOID TODO
	 * @param startLevelOID TODO
	 */
	public void loadTournament(int tournamentOID, String name, int tournamentTypeOID,
			int leagueOID, Timestamp startTime, int parentTournamentOID, int startLevelOID) {
		Tournament tournament = null;
		Tournament parentTournament = getTournament(parentTournamentOID);
		if(parentTournament == null) {
			tournament = new MainTournament(
				tournamentOID,
				name,
				sp.getSingleton().getTournamentTypeManager().getTournamentType(tournamentTypeOID),
				sp.getSingleton().getTeamManager().getLeague(leagueOID),
				startTime
			);
		} else {
			tournament = new SubTournament(
				new Integer(tournamentOID),
				name,
				parentTournament,
				sp.getSingleton().getScoreSystemManager().getLevel(startLevelOID),
				startTime
			);
		}
		tournaments.put(new Integer(tournamentOID), tournament);
	}

	public Tournament getTournament(int tournamentOID) {
		return tournaments.get(new Integer(tournamentOID));
	}

	public int getNumTournaments() {
		return tournaments.size();
	}

	/**
	 * @param tournamentOID
	 * @param teamOID
	 * @param seedOID
	 */
	public void loadTournamentSeed(int tournamentOID, int teamOID, int seedOID) {
		Tournament tournament = getTournament(tournamentOID);
		TournamentType type = tournament.getTournamentType();
		Seed seed = type.getSeed(seedOID);
		Team team = sp.getSingleton().getTeamManager().getTeam(teamOID);
		((MainTournament) tournament).loadSeed(seed, team);
	}

	/**
	 * @param gameNodeOID
	 * @param tournamentOID
	 * @param winner
	 * @param startTime
	 */
	public void loadGame(int gameNodeOID, int tournamentOID,
						 Integer winner, Timestamp startTime) {
		Tournament tournament = getTournament(tournamentOID);
		GameNode gameNode = sp.getSingleton().getTournamentTypeManager().getGameNode(gameNodeOID);
		Opponent oponent = winner != null ? tournament.getTournamentType().getOpponent(winner.intValue()) : null;
		((MainTournament) tournament).loadGame(gameNode, oponent, startTime);
	}

	public Iterator<Tournament> getTournaments() {
		return tournaments.values().iterator();
	}

	public Menu getTournamentMenu() {
		return new ReferenceMenu<Tournament>("tournaments") {
			protected Map<?, Tournament> getReferences() {
				return tournaments;
			}
		};
	}

	public Tournament createTournament(String name, TournamentType tournamentType, League league, Date startTime) {
		TournamentInsertBroker tournamentInsertBroker = new TournamentInsertBroker(
				sp, name, tournamentType, league, startTime);
		tournamentInsertBroker.execute();
		return getTournament(tournamentInsertBroker.getGeneratedOID());
	}


	public void addAdmins(Tournament tournament, int[] playerIDs) {
		for (int i = 0; i < playerIDs.length; i++) {
			int j = playerIDs[i];
			new TournamentAdminInsertBroker(sp, (MainTournament) tournament, j).execute();
		}
	}

	public void updateTounamentSeeds(Tournament tournament, Map<Seed, Team> seedTeam) {
		Map<Seed, Team> updates = new HashMap<Seed, Team>();
		Map<Seed, Team> inserts = new HashMap<Seed, Team>();
		Set<Seed> deletes = new HashSet<Seed>();

		for (Entry<Seed, Team> entry : seedTeam.entrySet()) {
			Seed seed = entry.getKey();
			Team newTeam = entry.getValue();
			Team oldTeam = tournament.getTeam(seed);
			if(newTeam != oldTeam) {
				if(newTeam == null) {
					deletes.add(seed);
				} else if(oldTeam == null) {
					inserts.put(seed, newTeam);
				} else {
					updates.put(seed, newTeam);
				}
			}
		}

		new SeedTeamInsertBroker(sp, tournament, inserts, updates, deletes).execute();

		// update the Tournament, only after updating the db is complete
		for (Entry<Seed, Team> entry : seedTeam.entrySet()) {
			Seed seed = entry.getKey();
			Team newTeam = entry.getValue();
			((MainTournament) tournament).loadSeed(seed, newTeam);
		}
	}

	public void updateTournament(Tournament tournament, String name, Timestamp startTime) {
		TournamentUpdateBroker broker = new TournamentUpdateBroker(sp, tournament, name, startTime);
		broker.execute();
	}

	public Tournament createSubTournament(String name, Tournament parenttournament, Level level, Date startTime) {
		SubTournamentInsertBroker tournamentInsertBroker =
			new SubTournamentInsertBroker(sp, name, parenttournament, level, startTime);
		tournamentInsertBroker.execute();
		return getTournament(tournamentInsertBroker.getGeneratedOID());
	}
	
	public boolean deleteTournament(User requestor, int id) {
		Tournament tournament = getTournament(id);
		if(tournament != null && tournament.delete(requestor, sp)) {
			tournaments.remove(new Integer(id));
			return true;
		}
		return false;
	}

	public void loadGameScore(int gameNodeOID, int tournamentOID, int opponentOID, int score) {
		Tournament tournament = getTournament(tournamentOID);
		GameNode gameNode = sp.getSingleton().getTournamentTypeManager().getGameNode(gameNodeOID);
		Opponent opponent = tournament.getTournamentType().getOpponent(opponentOID);
		tournament.getGame(gameNode).setScore(opponent, score);
	}
}
