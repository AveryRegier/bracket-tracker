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

public interface Pool extends Comparable<Pool> {
	/**
	 * @return String
	 */
	String getName();

	/**
	 * @return int
	 */
	int getOid();

	/**
	 * @return User
	 */
	User getOwner();

	/**
	 * @return ScoringStrategy
	 */
	ScoreSystem getScoreSystem();

	/**
	 * Sets the name.
	 * @param name The name to set
	 */
	void setName(String name);

	/**
	 * @param bracket
	 * @param tieBreakerAnswer2
	 */
	void loadBracket(Bracket bracket, String tieBreakerAnswer2);

	/**
	 * @return Returns the tournament.
	 */
	Tournament getTournament();

	/**
	 * @return
	 */
	Collection<Bracket> getBrackets();

	/**
	 * Return values in iterator are of type PoolBracket.
	 * @return
	 */
	default Collection<PoolBracket> getRankedBrackets() {
		return getRankedBrackets(getBrackets(), getGroup());
	}

	/**
	 * @return
	 */
	Group getGroup();
    Group getDefiningGroup();

	boolean hasBracket(Bracket bracket);

	boolean mayDelete(User user);

	boolean delete(User requestor, SingletonProvider sp);

	void commitUpdate(String name2, ScoreSystem scoreSystem2,
			Tournament tournament2, int bracketLimit2,
			boolean showBracketsEarly2, TieBreakerType tieBreakerType2,
			String tieBreakerQuestion2);

	int getBracketLimit();

	boolean isShowBracketsEarly();

	boolean hasReachedLimit(User owner);

	String getTieBreakerQuestion();

	String getTieBreakerAnswer();

	TieBreakerType getTieBreakerType();

	/**
	 * Does the tiebreaker question need answered now by this user?
	 * @return
	 */
	boolean isTiebreakerNeeded(User user);

	/**
	 * Is a tie breaker needed to be answered
	 * @return
	 */
	boolean isTiebreakerNeeded();

	void commitTieBreakerAnswer(String tieBreakerAnswer2);

	void applyDelete();

	void applyRemoveBracket(Bracket bracket);

	boolean removeBracket(User requestor, Bracket bracket);

	boolean mayRemoveBracket(User requestor, Bracket bracket);

	Bracket getBracket(int bracketID);

	Collection<PoolBracket> getRankedBrackets(Collection<Bracket> brackets, Group group);

	boolean isDefiningPool();

	default boolean hasAnyBrackets() {
        return !getBrackets().isEmpty();
    }
}