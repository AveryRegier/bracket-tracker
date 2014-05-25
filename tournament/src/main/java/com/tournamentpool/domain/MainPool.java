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
 * Created on Feb 20, 2003
 *
 * To change this generated comment go to
 * Window>Preferences>Java>Code Generation>Code Template
 */
package com.tournamentpool.domain;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.broker.sql.delete.BracketPoolDeleteBroker;
import com.tournamentpool.broker.sql.delete.PoolDeleteBroker;
import com.tournamentpool.broker.sql.get.BracketPoolGetBroker;
import com.tournamentpool.domain.ScoreSystem.Score;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

/**
 * @author avery
 */
public class MainPool implements Pool {

	final SingletonProvider sp;
	private int oid;
	private String name;
	private Group group;
	private ScoreSystem scoreSystem;
	private Map<Bracket, String> brackets = null;
	private Tournament tournament;
	private int bracketLimit;
	private boolean showBracketsEarly;
	private String tieBreakerQuestion;
	private TieBreakerType tieBreakerType;
	private String tieBreakerAnswer;

	/**
	 * @param poolOID
	 * @param name
	 * @param group
	 * @param scoreSystem
	 * @param showBracketsEarly
	 * @param bracketLimit
	 * @param tieBreakerAnswer2
	 */
	public MainPool(SingletonProvider sp, int poolOID, String name, Group group,
			ScoreSystem scoreSystem, Tournament tournament,
			int bracketLimit, boolean showBracketsEarly,
			TieBreakerType tieBreakerType, String tieBreakerQuestion, String tieBreakerAnswer2)
	{
		this.sp = sp;
		this.oid = poolOID;
		this.name = name;
		this.group = group;
		this.scoreSystem = scoreSystem;
		this.tournament = tournament;
		this.bracketLimit = bracketLimit;
		this.showBracketsEarly = showBracketsEarly;
		this.tieBreakerType = tieBreakerType;
		this.tieBreakerQuestion = tieBreakerQuestion;
		this.tieBreakerAnswer = tieBreakerAnswer2;
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IPool#getName()
	 */
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IPool#getOid()
	 */
	public int getOid() {
		return oid;
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IPool#getOwner()
	 */
	public User getOwner() {
		return group.getAdministrator();
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IPool#getScoreSystem()
	 */
	public ScoreSystem getScoreSystem() {
		return scoreSystem;
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IPool#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IPool#loadBracket(com.tournamentpool.domain.Bracket, java.lang.String)
	 */
	public void loadBracket(Bracket bracket, String tieBreakerAnswer2) {
		brackets.put(bracket, tieBreakerAnswer2);
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IPool#getTournament()
	 */
	public Tournament getTournament() {
		return tournament;
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IPool#getBrackets()
	 */
	public Iterable<Bracket> getBrackets() {
		if(brackets == null) {
			brackets = new HashMap<Bracket, String>();
			new BracketPoolGetBroker(sp, oid).execute();
		}
		return brackets.keySet();
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IPool#getRankedBrackets()
	 */
	public Iterator<PoolBracket> getRankedBrackets() {
		return getRankedBrackets(getBrackets(), getGroup());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Iterator<PoolBracket> getRankedBrackets(Iterable<Bracket> toEvaluate, Group group) {
		TreeSet<PoolBracket> poolBrackets = new TreeSet<PoolBracket>(); // this will put them in absolute order;
		
		for(Bracket bracket: toEvaluate) {
			Score score = scoreSystem.calculate(bracket, sp); // force brackets to load
			String bracketTieBreakerAnswer = this.brackets.get(bracket);
			Comparable<?> tieBreakerDelta = tieBreakerType.getTieBreakerDelta(
										tieBreakerAnswer, bracketTieBreakerAnswer,
										this, bracket);
			bracketTieBreakerAnswer = tieBreakerType.getTieBreakerReport(bracketTieBreakerAnswer, tieBreakerDelta);
			PoolBracket poolBracket = new PoolBracket(
					bracket,
					score,
					bracketTieBreakerAnswer,
					tieBreakerDelta,
					bracket.getOwner().getGroupInHierarchy(group));
			poolBrackets.add(poolBracket);
		}
		int rank = 0;
		int nextRank = 1; // tie situations skip the next rank
		int previous = Integer.MAX_VALUE;
		Comparable previousDelta = null;
		
		for (PoolBracket poolBracket : poolBrackets) {
			Score bracketScore = poolBracket.getScore();
			int current = bracketScore.getCurrent();
			if(current < previous ||
				(previousDelta != null &&
				 previousDelta.compareTo(poolBracket.getTieBreakerDelta()) < 0)) // tiebreaker
			{
				rank = nextRank;
			}
			poolBracket.setRank(rank);
			previous = current;
			previousDelta = poolBracket.getTieBreakerDelta();
			nextRank++;

			// determine max rank and set up rooting for
			for(PoolBracket toCompare: poolBrackets) {
				if(poolBracket != toCompare) { // never compare against yourself
					if(!bracketScore.canTieOrBeat(toCompare.getScore())) {
						poolBracket.reduceMaxRank(toCompare);
					}
				}
			}
			// since we've collected stats against everyone at canTieOrBeat,
			// we can now do this analysis
			if(bracketScore.willGetBeatByOneOfThoseICanIndividuallyTieOrBeat(getTournament())) {
				poolBracket.reduceMaxRank(null);
			}
		}

		GameNodeAdapter.clearPossibleWinningSeedsCache();
		return poolBrackets.iterator();
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IPool#getGroup()
	 */
	public Group getGroup() {
		return group;
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IPool#hasBracket(com.tournamentpool.domain.Bracket)
	 */
	public boolean hasBracket(Bracket bracket) {
		getBrackets();
		return brackets.keySet().contains(bracket);
	}
	
	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IPool#mayDelete(com.tournamentpool.domain.User)
	 */
	public boolean mayDelete(User user) {
		return user != null 
		    && (group.getAdministrator() == user || user.isSiteAdmin()) 
		    && !getBrackets().iterator().hasNext();
	}
	
	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IPool#delete(com.tournamentpool.domain.User, com.tournamentpool.application.SingletonProvider)
	 */
	public boolean delete(User requestor, SingletonProvider sp) {
		if(mayDelete(requestor)) {
			new PoolDeleteBroker(sp, this).execute();
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IPool#commitUpdate(java.lang.String, com.tournamentpool.domain.ScoreSystem, com.tournamentpool.domain.Tournament, int, boolean, com.tournamentpool.domain.TieBreakerType, java.lang.String)
	 */
	public void commitUpdate(String name2, ScoreSystem scoreSystem2, Tournament tournament2,
			int bracketLimit2, boolean showBracketsEarly2,
			TieBreakerType tieBreakerType2, String tieBreakerQuestion2) {
		this.name = name2;
		this.scoreSystem = scoreSystem2;
		this.tournament = tournament2;
		this.bracketLimit = bracketLimit2;
		this.showBracketsEarly = showBracketsEarly2;
		this.tieBreakerType = tieBreakerType2;
		this.tieBreakerQuestion = tieBreakerQuestion2;
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IPool#getBracketLimit()
	 */
	public int getBracketLimit() {
		return bracketLimit;
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IPool#isShowBracketsEarly()
	 */
	public boolean isShowBracketsEarly() {
		return showBracketsEarly;
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IPool#hasReachedLimit(com.tournamentpool.domain.User)
	 */
	public boolean hasReachedLimit(User owner) {
		if(bracketLimit == 0) return false;
		int count = 0;
		for(Bracket bracket: getBrackets()) {
			if(bracket.getOwner() == owner) {
				count++;
			}
		}
		return count >= bracketLimit;
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IPool#getTieBreakerQuestion()
	 */
	public String getTieBreakerQuestion() {
		return tieBreakerQuestion;
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IPool#getTieBreakerAnswer()
	 */
	public String getTieBreakerAnswer() {
		return tieBreakerAnswer;
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IPool#getTieBreakerType()
	 */
	public TieBreakerType getTieBreakerType() {
		return tieBreakerType;
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IPool#isTiebreakerNeeded(com.tournamentpool.domain.User)
	 */
	public boolean isTiebreakerNeeded(User user) {
		if(user == group.getAdministrator()) { // security
			return isTiebreakerNeeded();
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IPool#isTiebreakerNeeded()
	 */
	public boolean isTiebreakerNeeded() {
		if(tieBreakerType.mustAnswer() && tieBreakerAnswer == null) { // cheap
			if(tournament.isComplete()) return true; // expensive
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IPool#commitTieBreakerAnswer(java.lang.String)
	 */
	public void commitTieBreakerAnswer(String tieBreakerAnswer2) {
		this.tieBreakerAnswer = tieBreakerAnswer2;
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IPool#applyDelete()
	 */
	public void applyDelete() {
		group.removePool(this);
	}
	
	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IPool#applyRemoveBracket(com.tournamentpool.domain.Bracket)
	 */
	public void applyRemoveBracket(Bracket bracket) {
		brackets.remove(bracket);
	}
	
	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IPool#removeBracket(com.tournamentpool.domain.User, com.tournamentpool.domain.Bracket)
	 */
	public boolean removeBracket(User requestor, Bracket bracket) {
		if(mayRemoveBracket(requestor, bracket)) {
			new BracketPoolDeleteBroker(sp, this, bracket).execute();
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IPool#mayRemoveBracket(com.tournamentpool.domain.User, com.tournamentpool.domain.Bracket)
	 */
	public boolean mayRemoveBracket(User requestor, Bracket bracket) {
		if(requestor != null && 
			(requestor == group.getAdministrator() || requestor.isSiteAdmin() || requestor == bracket.getOwner())) 
		{
			return !getTournament().isStarted();
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.tournamentpool.domain.IPool#getBracket(int)
	 */
	public Bracket getBracket(int bracketID) {
		for (Bracket bracket: brackets.keySet()) {
			if(bracket.getOID() == bracketID) {
				return bracket;
			}
		}
		return null;
	}

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Pool) {
            Pool other = (Pool)obj;
            return this.oid == other.getOid() && this.getGroup() == other.getGroup();
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return oid + getGroup().hashCode();
    }

    @Override
    public int compareTo(Pool o) {
        int c = this.oid - o.getOid();
        return c == 0 ? this.getGroup().getId() - o.getGroup().getId(): c;
    }
}
