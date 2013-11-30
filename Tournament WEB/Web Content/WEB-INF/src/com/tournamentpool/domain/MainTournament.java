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
package com.tournamentpool.domain;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.broker.sql.delete.TournamentAdminDeleteBroker;
import com.tournamentpool.broker.sql.delete.TournamentDeleteBroker;
import com.tournamentpool.broker.sql.delete.TournamentGamesDeleteBroker;
import com.tournamentpool.broker.sql.delete.TournamentSeedsDeleteBroker;
import com.tournamentpool.broker.sql.insert.GameInsertBroker;
import com.tournamentpool.broker.sql.insert.GameScoreInsertBroker;
import com.tournamentpool.broker.sql.status.TournamentBracketStatusBroker;
import com.tournamentpool.broker.sql.status.TournamentPoolStatusBroker;

/**
 * @author avery
 */
public class MainTournament implements Tournament {
	private Integer oid;
	private String name;
	private TournamentType tournamentType;
	private Timestamp startTime;
	private Map<Seed, Team> seeds = new HashMap<Seed, Team>();
//	private Map games = new HashMap();
	private Map<GameNode, Game> nodeGameMap = new HashMap<GameNode, Game>();
	private Set<Integer> admins = new HashSet<Integer>();
	private final League league;
	private Date lastUpdated;

	/**
	 * @param tournamentOID
	 * @param name
	 * @param type
	 * @param league
	 * @param startTime
	 */
	public MainTournament(int tournamentOID, String name, TournamentType type, League league, Timestamp startTime) {
		this.league = league;
		this.oid = new Integer(tournamentOID);
		this.name = name;
		this.tournamentType = type;
		this.startTime = startTime;
	}

	/**
	 *
	 */
	public TournamentType getTournamentType() {
		return tournamentType;
	}

	/**
	 * @param seed
	 * @param team
	 */
	public void loadSeed(Seed seed, Team team) {
		seeds.put(seed, team);
	}

	/**
	 * @param gameOID
	 * @param gameNode
	 * @param oponent
	 * @param startTime
	 */
	public void loadGame(GameNode gameNode, Opponent oponent, Timestamp startTime) {
		Game game = new Game(this,
			gameNode, oponent, startTime
		);
	//	games.put(new Integer(gameOID), game);
		nodeGameMap.put(gameNode, game);
	}

	public void loadAdmin(int adminOID) {
		this.admins.add(new Integer(adminOID));
	}

	public void removeAdmin(int adminOID) {
		admins.remove(new Integer(adminOID));
	}

	/**
	 * @param node
	 * @param startTime 
	 * @return
	 */
	private Game createGame(GameNode node, Opponent winner, Date startTime) {
		Game game = new Game(this, node, winner, startTime);
		if(nodeGameMap != null) {
			Game oldGame = nodeGameMap.get(node);
			if (oldGame != null) {
				game.setOld();
				game.scores.putAll(oldGame.scores);
			}
		}
		return game;
	}

	/**
	 * @param updatePicks
	 */
	private void applyGames(List<Game> updateGames) {
		if(nodeGameMap == null) {
			nodeGameMap = new HashMap<GameNode, Game>();
		} else {
			nodeGameMap.clear();
		}
		for (Game game: updateGames) {
			game.setOld();
			nodeGameMap.put(game.getGameNode(), game);
		}
		this.lastUpdated = new Date(System.currentTimeMillis());
	}

	public void updateGames(SingletonProvider sp, WinnerSource winnerSource)
			throws SQLException {
		GameScoreInsertBroker scoreReporter = new GameScoreInsertBroker(sp);
		GameInsertBroker gameReporter = new GameInsertBroker(sp);
		List<Game> updateGames = findGameUpdates(winnerSource, scoreReporter, gameReporter);
		gameReporter.execute();
		scoreReporter.execute();

		// update the domain
		applyGames(updateGames);
		
		Date lastUpdated = getLastUpdated();
		if(lastUpdated != null){
			sp.getSingleton().setLastUpdated(lastUpdated.getTime());
		}
	}

	private List<Game> findGameUpdates(WinnerSource winnerSource,
			ScoreReporter scoreReporter, GameReporter gameReporter) {
		for (GameNode node : getAllGameNodes()) {
			updateGameInformation(winnerSource, scoreReporter, gameReporter,
					node);
		}
		return gameReporter.getGameReport();
	}

	private void updateGameInformation(WinnerSource winnerSource,
			ScoreReporter scoreReporter, GameReporter gameReporter,
			GameNode node) {
		GameInfo gameInfo = winnerSource.getGameInfo(getTournamentType(), node);
		Opponent winner = gameInfo != null ? gameInfo.getWinner() : null;
		Date gameDate = gameInfo != null ? gameInfo.getDate() : null;
		Game game = createGame(node, winner, gameDate);
		gameReporter.updateGame(game);
		updateScores(scoreReporter, gameInfo, game);
	}

	private List<GameNode> getAllGameNodes() {
		return getTournamentType().getChampionshipGameNode().getAllGameNodes();
	}


	private void updateScores(ScoreReporter scoreReporter,
			GameInfo gameInfo, Game game) {
		if(gameInfo != null) {
			game.setStatus(gameInfo.getStatus());
			for (Entry<Integer, Opponent> entry : game.getOpponents()) {
				Opponent opponent = entry.getValue();
				Integer score = gameInfo.getScore(opponent);
				Integer previous = game.setScore(opponent, score);
				if(previous != null && score != null) {
					scoreReporter.updateScore(game, opponent, score);
				} else if(previous == null && score != null){
					scoreReporter.insertScore(game, opponent, score);
				} else if(score == null){
					scoreReporter.deleteScore(game, opponent);
				}
			}
		}
	}

	public static interface WinnerSource {
		GameInfo getGameInfo(TournamentType tournamentType, GameNode node);
	}
	
	/**
	 *
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public Game getChampionshipGame() {
		return getGame(getTournamentType().getChampionshipGameNode());
	}

	/**
	 * @param node
	 * @return
	 */
	public Game getGame(GameNode node) {
		return nodeGameMap.get(node);
	}

	/**
	 * @param seed
	 */
	public Team getTeam(Seed seed) {
		return seed != null ? seeds.get(seed) : null;
	}

	/**
	 * @return Returns the oid.
	 */
	public int getOid() {
		return oid.intValue();
	}

	public Object getID() {
		return oid;
	}

	public Iterator<Integer> getAdminOIDs() {
		return admins.iterator();
	}

	/**
	 * If there are no admins, then only the site admin may edit a tournament
	 * @param user
	 * @return
	 */
	public boolean mayEdit(User user) {
		return user.isSiteAdmin() || admins.contains(new Integer(user.getOID()));
	}

	/**
	 * @return true if this tournament has begun
	 */
	public boolean isStarted() {
		Date now = new Timestamp(System.currentTimeMillis());
		if(now.after(startTime)) return true;
		// find earliest game
		Date earliest = null;
		for (Game game: nodeGameMap.values()) {
			if(game.getWinner() != null) return true; // if a winner is already selected, it had better be started
			if(earliest != null) {
				if(game.getDate() != null && game.getDate().before(earliest)) {
					earliest = game.getDate();
				}
			} else earliest = game.getDate();
		}
		return earliest != null && earliest.before(now);
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public boolean isComplete() {
		Game championshipGame = getChampionshipGame();
		return championshipGame != null && championshipGame.isComplete();
	}

	public boolean hasAdmin(int playerOID) {
		return admins.contains(new Integer(playerOID));
	}

	public boolean hasAllSeedsAssigned() {
		return seeds.size() == tournamentType.getNumSeeds() && !seeds.values().contains(null);
	}

	public void setName(String name) {
		this.name = name;
	}

	public League getLeague() {
		return league;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}
	
	public boolean mayDelete(User requestor, SingletonProvider sp) throws SQLException {
		if(requestor != null && (isAdmin(requestor) || requestor.isSiteAdmin())) {
			// no brackets created against this tournament (go to database)
			if(new TournamentBracketStatusBroker(sp, this).isUsedByBrackets()) return false;
			
			// no pools created against this tournament (go to database)
			if(new TournamentPoolStatusBroker(sp, this).isUsedByPools()) return false;
			
			// no sub tournaments against this tournament (can be determined in memory)
			Iterator<Tournament> tournaments = sp.getSingleton().getTournamentManager().getTournaments();
			while (tournaments.hasNext()) {
				Tournament tourney = tournaments.next();
				if(tourney instanceof SubTournament && ((SubTournament)tourney).getParentTournament() == this)
					return false;
			}
			return true;
		}
		return false;
	}
	
	public boolean isAdmin(User requestor) {
		return admins.contains(new Integer(requestor.getOID()));
	}
	
	public boolean delete(User requestor, SingletonProvider sp) throws SQLException {
		if(mayDelete(requestor, sp)) {
			// remove all games
			new TournamentGamesDeleteBroker(sp, this).execute();
			
			// remove tournament seeds
			new TournamentSeedsDeleteBroker(sp, this).execute();

			// remove tournament administrators
			for (Integer adminOID: admins) {
				new TournamentAdminDeleteBroker(sp, this, adminOID.intValue()).execute();				
			}
			
			// remove tournament object
			new TournamentDeleteBroker(sp, this).execute();
			return true;
		}
		return false;
	}
	
	public boolean hasTeam(Team team) {
		return seeds.values().contains(team);
	}
	
	@Override
	public java.util.Date getNextStartTime() {
		Date toReturn = null;
		
		for (Game game : nodeGameMap.values()) {
			if(game != null) {
				Date gameDate = game.getDate();
				if(gameDate != null && game.getWinner() == null) {
					if(toReturn == null || gameDate.before(toReturn)) {
						System.out.println("Found earlier: "+gameDate);
						toReturn = gameDate;
					}
				}
			}
		}
		if(toReturn == null) {
			System.out.println("Using tournament start time");
		}
		
		return toReturn != null ? toReturn : getStartTime();
	}

	public boolean hasGamesInProgress() {
		Date now = new Date();
		for (Game game : nodeGameMap.values()) {
			if(game != null) {
				Date gameDate = game.getDate();
				if(gameDate != null && game.getWinner() == null) {
					if(gameDate.before(now)) return true;
				}
			}
		}
		return false;
	}
}
