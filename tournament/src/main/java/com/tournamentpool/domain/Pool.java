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

package com.tournamentpool.domain;

import com.tournamentpool.application.SingletonProvider;

import java.util.Collection;
import java.util.Iterator;

public interface Pool extends Comparable<Pool> {
	/**
	 * @return String
	 */
	public abstract String getName();

	/**
	 * @return int
	 */
	public abstract int getOid();

	/**
	 * @return User
	 */
	public abstract User getOwner();

	/**
	 * @return ScoringStrategy
	 */
	public abstract ScoreSystem getScoreSystem();

	/**
	 * Sets the name.
	 * @param name The name to set
	 */
	public abstract void setName(String name);

	/**
	 * @param bracket
	 * @param tieBreakerAnswer2
	 */
	public abstract void loadBracket(Bracket bracket, String tieBreakerAnswer2);

	/**
	 * @return Returns the tournament.
	 */
	public abstract Tournament getTournament();

	/**
	 * @return
	 */
	public abstract Collection<Bracket> getBrackets();

	/**
	 * Return values in iterator are of type PoolBracket.
	 * @return
	 */
	public abstract Iterator<PoolBracket> getRankedBrackets();

	/**
	 * @return
	 */
	public abstract Group getGroup();

	public abstract boolean hasBracket(Bracket bracket);

	public abstract boolean mayDelete(User user);

	public abstract boolean delete(User requestor, SingletonProvider sp);

	public abstract void commitUpdate(String name2, ScoreSystem scoreSystem2,
			Tournament tournament2, int bracketLimit2,
			boolean showBracketsEarly2, TieBreakerType tieBreakerType2,
			String tieBreakerQuestion2);

	public abstract int getBracketLimit();

	public abstract boolean isShowBracketsEarly();

	public abstract boolean hasReachedLimit(User owner);

	public abstract String getTieBreakerQuestion();

	public abstract String getTieBreakerAnswer();

	public abstract TieBreakerType getTieBreakerType();

	/**
	 * Does the tiebreaker question need answered now by this user?
	 * @return
	 */
	public abstract boolean isTiebreakerNeeded(User user);

	/**
	 * Is a tie breaker needed to be answered
	 * @return
	 */
	public abstract boolean isTiebreakerNeeded();

	public abstract void commitTieBreakerAnswer(String tieBreakerAnswer2);

	public abstract void applyDelete();

	public abstract void applyRemoveBracket(Bracket bracket);

	public abstract boolean removeBracket(User requestor, Bracket bracket);

	public abstract boolean mayRemoveBracket(User requestor, Bracket bracket);

	public abstract Bracket getBracket(int bracketID);

	public abstract Iterator<PoolBracket> getRankedBrackets(Iterable<Bracket> brackets, Group group);
}