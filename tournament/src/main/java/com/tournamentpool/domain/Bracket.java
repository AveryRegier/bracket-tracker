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
 * Created on Feb 20, 2003
 */
package com.tournamentpool.domain;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.broker.sql.delete.BracketDeleteBroker;
import com.tournamentpool.broker.sql.get.PickGetBroker;
import com.tournamentpool.broker.sql.insert.PickInsertBroker;
import com.tournamentpool.domain.GameNode.Feeder;
import utility.domain.Reference;

import java.util.*;

/**
 * @author avery
 */
public class Bracket implements Reference {
	public class Pick {
		private final GameNode game;
		private Opponent winner;
		private boolean isnew = true;
		private Seed seed;

		Pick(GameNode game){
			this.game = game;
		}

		Pick(GameNode game, Opponent winner) {
			this.game = game;
			this.winner = winner;
		}

		public Opponent getWinner() {
			return winner;
		}

		public void setWinner(Opponent winner) {
			this.winner = winner;
		}

		public GameNode getGameNode() {
			return game;
		}

		public Bracket getBracket() {
			return Bracket.this;
		}

		public boolean isNew() {
			return isnew;
		}

		void setOld() {
			isnew = false;
		}

		public Seed getSeed() {
			if(changed || seed == null) {
				synchronized(Bracket.this) {
					synchronized(this) {
						seed = null;
						if (winner != null) {
							Feeder feeder = game.getFeeder(winner);
							if (feeder != null) {
								Reference ref = feeder.getFeeder();
								if (ref != null) {
									if (ref instanceof Seed) {
										seed = (Seed) ref;
									} else {
										seed = getPickFromMemory((GameNode) ref).getSeed();
									}
								}
							}
						}
						changed = true;
					}
				}
			}
			return seed;
		}
	}

	private String name;
	private Tournament tournament;
	private int oid;
	private final User owner;
	private int tiebreak_score; // should somehow tie into the scoring strategy
	private Map<GameNode, Pick> picks;
	private boolean changed = true;

	/**
	 * @param bracketOID
	 * @param tournament
	 * @param name
	 */
	public Bracket(int bracketOID, User owner, Tournament tournament, String name) {
		this.oid = bracketOID;
		this.owner = owner;
		this.tournament = tournament;
		this.name = name;
	}

	/**
	 * @return User
	 */
	public User getOwner() {
		return owner;
	}

	/**
	 * @return int
	 */
	public int getTiebreak_score() {
		return tiebreak_score;
	}

	/**
	 * Sets the tiebreak_score.
	 * @param tiebreak_score The tiebreak_score to set
	 */
	public void setTiebreak_score(int tiebreak_score) {
		this.tiebreak_score = tiebreak_score;
	}

	/**
	 *
	 */
	public int getOID() {
		return oid;
	}

	public Integer getID() {
		return new Integer(oid);
	}

	/**
	 * @param node
	 * @return
	 */
	public Pick createPick(GameNode node) {
		Pick pick = new Pick(node);
		if(picks != null && picks.keySet().contains(node)) {
			pick.setOld();
		}
		return pick;
	}

	/**
	 * @param updatePicks
	 */
	public synchronized void applyPicks(List<Pick> updatePicks) {
		if(picks == null) {
			picks = new HashMap<GameNode, Pick>();
		} else {
			picks.clear();
		}
		for (Pick pick : updatePicks) {
			pick.setOld();
			picks.put(pick.game, pick);
		}
		changed = true;
	}

	public Iterable<Pick> getPicks(SingletonProvider sp) {
		retrievePicks(sp);
		if (picks != null)
			return picks.values();
		else
			return Collections.emptyList();
	}

	/**
	 * @param sp
	 */
	private synchronized void retrievePicks(SingletonProvider sp) {
		if(picks == null) {
			new PickGetBroker(sp, this.oid).execute();
			changed = true;
		}
	}

	public Pick getPick(SingletonProvider sp, GameNode game) {
		retrievePicks(sp);
		return getPickFromMemory(game);
	}

	/**
	 * @param game
	 * @return
	 */
	public Pick getPickFromMemory(GameNode game) {
		return picks.get(game);
	}

	/**
	 * @return
	 */
	public Tournament getTournament() {
		return tournament;
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
	public boolean isComplete(SingletonProvider sp) {
		retrievePicks(sp);
		Map<Object, GameNode> nodes = tournament.getTournamentType().getGameNodes();  // expensive call
		if(picks != null && picks.size() >= nodes.size()) { // then verify every game has a pick
			for(GameNode node: nodes.values()) {
				Pick pick = picks.get(node);
				if(pick == null || pick.getWinner() == null) return false;
			}
			return true;
		} // else something must be missing, so
		return false;
	}

	public boolean isInPool() {
		// assuming all brackets are already loaded in all relevant pools
		User owner = getOwner();
		Iterator<Group> groups = owner.getGroups();
		while (groups.hasNext()) {
			Group group = groups.next();
			for(Pool pool: group.getPools()) {
				if(pool.hasBracket(this)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean mayDelete(User user) {
		return user != null && (user == getOwner() || user.isSiteAdmin()) && !isInPool();
	}
	
	public boolean delete(User requestor, SingletonProvider sp) {
		if(mayDelete(requestor)) {
			ArrayList<Pick> deletePicks = new ArrayList<Pick>(picks.values());
			new PickInsertBroker(sp, Collections.<Pick>emptyList(), Collections.<Pick>emptyList(), deletePicks).execute();
			picks.clear();
			new BracketDeleteBroker(sp, this).execute();
			return true;
		}
		return false;
	}

	public Iterator<Pool> getPools() {
		User owner = getOwner();
		Iterator<Group> groups = owner.getGroups();
		Set<Pool> allPools = new LinkedHashSet<Pool>();
        while (groups.hasNext()) {
            Group group = groups.next();
            allPools.addAll(group.getPools());
        }
        Set<Pool> poolsToReturn = new TreeSet<Pool>();
        for(Pool pool: allPools) {
            if(pool.hasBracket(this)) {
                poolsToReturn.add(pool);
            }
        }
		return poolsToReturn.iterator();
	}

	public void setName(String name) {
		this.name = name;
	}

	public void applyDelete() {
		getOwner().removeBracket(this);
	}

	public boolean mayEdit(User user) {
		return !(user == getOwner() && isInPool() && getTournament().isStarted());
	}
}
