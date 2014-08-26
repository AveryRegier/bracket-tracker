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

import com.tournamentpool.domain.ScoreSystem.Score;

public class PoolBracket implements Comparable<PoolBracket> {
	private int rank;
	private int maxRank = 1;
	private final ScoreSystem.Score score;
	private final Bracket bracket;
	private final String tieBreakerAnswer;
	@SuppressWarnings("rawtypes")
	private final Comparable tieBreakerDelta;
	private PoolBracket beatBy = null;
	private final Group group;

	@SuppressWarnings("rawtypes")
	public PoolBracket(Bracket bracket, Score score, String tieBreakerAnswer, Comparable tieBreakerDelta, Group group) {
		this.bracket = bracket;
		this.score = score;
		this.tieBreakerAnswer = tieBreakerAnswer;
		this.tieBreakerDelta = tieBreakerDelta;
		this.group = group;
	}

	public int getRank() {
		return rank;
	}

	void setRank(int rank) {
		this.rank = rank;
	}

	void reduceMaxRank(PoolBracket beatBy) {
		maxRank++;
		if(beatBy == null) return;
		this.beatBy = beatBy;
	}

	public ScoreSystem.Score getScore() {
		return score;
	}

	public Bracket getBracket() {
		return bracket;
	}
	
	public Group getGroup() {
		return group;
	}

	public int compareTo(PoolBracket bbean) {
		if(this == bbean) return 0;
		Score bScore = bbean.score;
		if(this.score != null && bScore != null) {
			int score = this.score.getCurrent() - bScore.getCurrent();
			if(score != 0) return -score; // highest score first
		}
		if(this.tieBreakerDelta != null && bbean.tieBreakerDelta != null) {
			@SuppressWarnings("unchecked")
			int result = this.tieBreakerDelta.compareTo(bbean.tieBreakerDelta);
			if(result != 0) return result; // lowest delta first
		}
		if(this.score != null && bScore != null) { // apply max after the tie breaker
			int score = this.score.getMax() - bbean.score.getMax();
			if(score != 0) return -score; // highest max available first
		}
		if(this.bracket.getOwner().getName() != null) {
			int result = this.bracket.getOwner().getName().compareTo(bbean.bracket.getOwner().getName());
			if(result != 0) return result;
		}
		if(this.bracket.getName() != null) {
			int result = this.bracket.getName().compareTo(bbean.bracket.getName());
			if(result != 0) return result;
		}
		return this.bracket.getOID() - bbean.bracket.getOID();
	}

	public String getTieBreakerAnswer() {
		return tieBreakerAnswer;
	}

	Comparable<?> getTieBreakerDelta() {
		return tieBreakerDelta;
	}

	public int getMaxRank() {
		return maxRank;
	}
	
	public String getBeatBy() {
		if(maxRank == 1) return null;
		if(beatBy == null) return "someone";
		return beatBy.bracket.getOwner().getName();
	}
}
