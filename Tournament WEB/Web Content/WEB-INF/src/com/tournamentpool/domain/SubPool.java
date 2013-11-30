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

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.broker.sql.delete.BracketPoolDeleteBroker;

public class SubPool implements Pool {

	private final Pool parentPool;
	private final Group group;

	public SubPool(Pool parentPool, Group group) {
		this.parentPool = parentPool;
		this.group = group;
	}

	@Override
	public Group getGroup() {
		return group;
	}
	
	public Pool getParentPool() {
		return parentPool;
	}
	
	@Override
	public Bracket getBracket(int bracketID) {
		return getParentPool().getBracket(bracketID);
	}
	
	@Override
	public void applyDelete() {
		throw new UnsupportedOperationException("Only applies to parent pools");
	}
	@Override
	public void applyRemoveBracket(Bracket bracket) {
		getParentPool().applyRemoveBracket(bracket);
	}

	@Override
	public void commitTieBreakerAnswer(String tieBreakerAnswer2) {
		throw new UnsupportedOperationException("Only applies to parent pools");
	}
	@Override
	public void commitUpdate(String name2, ScoreSystem scoreSystem2,
			Tournament tournament2, int bracketLimit2,
			boolean showBracketsEarly2, TieBreakerType tieBreakerType2,
			String tieBreakerQuestion2) {
		throw new UnsupportedOperationException("Only applies to parent pools");
	}
	@Override
	public boolean delete(User requestor, SingletonProvider sp)
			throws SQLException {
		throw new UnsupportedOperationException("Only applies to parent pools");
	}
	@Override
	public int getBracketLimit() {
		return getParentPool().getBracketLimit();
	}
	@Override
	public String getName() {
		return getParentPool().getName();
	}
	@Override
	public int getOid() {
		return getParentPool().getOid();
	}
	@Override
	public User getOwner() throws SQLException {
		return getParentPool().getOwner();
	}
	@Override
	public ScoreSystem getScoreSystem() {
		return getParentPool().getScoreSystem();
	}
	@Override
	public String getTieBreakerAnswer() {
		return getParentPool().getTieBreakerAnswer();
	}
	@Override
	public String getTieBreakerQuestion() {
		return getParentPool().getTieBreakerQuestion();
	}
	@Override
	public TieBreakerType getTieBreakerType() {
		return getParentPool().getTieBreakerType();
	}
	@Override
	public Tournament getTournament() {
		return getParentPool().getTournament();
	}
	@Override
	public boolean hasReachedLimit(User owner) throws SQLException {
		return getParentPool().hasReachedLimit(owner);
	}
	@Override
	public boolean isShowBracketsEarly() {
		return getParentPool().isShowBracketsEarly();
	}
	@Override
	public void loadBracket(Bracket bracket, String tieBreakerAnswer2) {
		getParentPool().loadBracket(bracket, tieBreakerAnswer2);
	}
	@Override
	public void setName(String name) {
		throw new UnsupportedOperationException("Only applies to parent pools");
	}
	@Override
	public boolean removeBracket(User requestor, Bracket bracket) throws SQLException {
		if(mayRemoveBracket(requestor, bracket)) { // ensure SubPool version of this method is used
			new BracketPoolDeleteBroker(((MainPool)parentPool).sp, getParentPool(), bracket).execute();
			return true;
		}
		return false;
	}

	@Override
	public boolean isTiebreakerNeeded(User user) throws SQLException {
		return getParentPool().isTiebreakerNeeded(user);
	}

	@Override
	public boolean isTiebreakerNeeded() {
		return getParentPool().isTiebreakerNeeded();
	}

	@Override
	public Iterable<Bracket> getBrackets() throws SQLException {
		Set<Bracket> groupBrackets = new HashSet<Bracket>();
		Iterable<Bracket> brackets = getParentPool().getBrackets();
		for (Bracket bracket : brackets) {
			if(group.hasMember(bracket.getOwner().getOID())) {
				groupBrackets.add(bracket);
			}
		}
		return groupBrackets;
	}

	@Override
	public Iterator<PoolBracket> getRankedBrackets() throws SQLException {
		return getRankedBrackets(getBrackets(), getGroup());
	}
	
	@Override
	public Iterator<PoolBracket> getRankedBrackets(Iterable<Bracket> brackets, Group group) throws SQLException {
		return getParentPool().getRankedBrackets(brackets, group);
	}

	@Override
	public boolean hasBracket(Bracket bracket) throws SQLException {
		if(getParentPool().hasBracket(bracket)) {
			if(group.hasMember(bracket.getOwner().getOID())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean mayDelete(User user) throws SQLException {
		if(user == group.getAdministrator() && !getBrackets().iterator().hasNext()) return true;
		return getParentPool().mayDelete(user);
	}

	@Override
	public boolean mayRemoveBracket(User requestor, Bracket bracket)
			throws SQLException {
		if(requestor != null && (requestor == group.getAdministrator())) {
			return !getTournament().isStarted();
		}
		return getParentPool().mayRemoveBracket(requestor, bracket);
	}	
}
