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
import com.tournamentpool.broker.sql.delete.BracketPoolDeleteBroker;

import java.util.Collection;
import java.util.stream.Collectors;

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
	
	Pool getParentPool() {
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
	public boolean delete(User requestor, SingletonProvider sp) {
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
	public User getOwner() {
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
	public boolean hasReachedLimit(User owner) {
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
	public boolean removeBracket(User requestor, Bracket bracket) {
		if(mayRemoveBracket(requestor, bracket)) { // ensure SubPool version of this method is used
			new BracketPoolDeleteBroker(((MainPool)parentPool).sp, getParentPool(), bracket).execute();
			return true;
		}
		return false;
	}

	@Override
	public boolean isTiebreakerNeeded(User user) {
		return getParentPool().isTiebreakerNeeded(user);
	}

	@Override
	public boolean isTiebreakerNeeded() {
		return getParentPool().isTiebreakerNeeded();
	}

	@Override
	public Collection<Bracket> getBrackets() {
        return getParentPool().getBrackets().stream()
                .filter(this::ownerIsInGroup)
                .collect(Collectors.toSet());
	}

    private boolean ownerIsInGroup(Bracket bracket) {
        return group.hasMember(bracket.getOwner().getOID());
    }

    @Override
	public Collection<PoolBracket> getRankedBrackets() {
		return getRankedBrackets(getBrackets(), getGroup());
	}
	
	@Override
	public Collection<PoolBracket> getRankedBrackets(Collection<Bracket> brackets, Group group) {
		return getParentPool().getRankedBrackets(brackets, group);
	}

	@Override
	public boolean hasBracket(Bracket bracket) {
		if(getParentPool().hasBracket(bracket)) {
			if(ownerIsInGroup(bracket)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean mayDelete(User user) {
		if(user == group.getAdministrator() && getBrackets().isEmpty()) return true;
		return getParentPool().mayDelete(user);
	}

	@Override
	public boolean mayRemoveBracket(User requestor, Bracket bracket) {
		if(requestor != null && (requestor == group.getAdministrator())) {
			return !getTournament().isStarted();
		}
		return getParentPool().mayRemoveBracket(requestor, bracket);
	}

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Pool) {
            Pool other = (Pool)obj;
            return this.getOid() == other.getOid() && this.getGroup() == other.getGroup();
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return this.getOid() + getGroup().hashCode();
    }

    @Override
    public int compareTo(Pool o) {
        int c = this.getOid() - o.getOid();
        return c == 0 ? this.getGroup().getId() - o.getGroup().getId(): c;
    }
}
