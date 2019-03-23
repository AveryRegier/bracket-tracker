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

import com.tournamentpool.broker.sql.DatabaseFailure;
import com.tournamentpool.controller.ShowTournamentController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utility.domain.Reference;

public abstract class TieBreakerType implements Reference {
	private static final Logger logger = LoggerFactory.getLogger(TieBreakerType.class);

	@SuppressWarnings("rawtypes")
	private static final Comparable<?> EQUALIZER = new Comparable() {
		public int compareTo(Object arg0) {
			return 0; // always equal
		}
		public String toString() {
			return "";
		}
	};
	public static class TieBreakerTypeNone extends TieBreakerType {
		public TieBreakerTypeNone(int tieBreakerID, String name) {
			super(tieBreakerID, name);
		}
		public boolean mustAnswer() {
			return false;
		}
		public Comparable<?> getTieBreakerDelta(
				String tieBreakerAnswer, String bracketTieBreakerAnswer,
				Pool pool, Bracket bracket)
		{
			return EQUALIZER;
		}
	}
	public static class TieBreakerTypeClosestNumber extends TieBreakerType {
		public TieBreakerTypeClosestNumber(int tieBreakerID, String name) {
			super(tieBreakerID, name);
		}

		public Comparable<?> getTieBreakerDelta(
				String tieBreakerAnswer, String bracketTieBreakerAnswer,
				Pool pool, Bracket bracket)
		{
			if(tieBreakerAnswer == null) return EQUALIZER;
			return calculateDelta(tieBreakerAnswer, bracketTieBreakerAnswer);
		}

		public void validate(String tieBreakerAnswer) {
			Integer.parseInt(tieBreakerAnswer);
		}
	}
	
	private static Integer calculateDelta(String tieBreakerAnswer,
			String bracketTieBreakerAnswer) {
		return Math.abs(Integer.parseInt(bracketTieBreakerAnswer) - Integer.parseInt(tieBreakerAnswer));
	}

	public static class TieBreakerTypeUpsetPrediction extends TieBreakerType {
		public TieBreakerTypeUpsetPrediction(int tieBreakerID, String name) {
			super(tieBreakerID, name);
		}

		public boolean mustAnswer() {
			return false;
		}

		public Comparable<?> getTieBreakerDelta(
				String tieBreakerAnswer, String bracketTieBreakerAnswer,
				Pool pool, Bracket bracket)
		{
			if(!pool.getTournament().isStarted()) return EQUALIZER;
			try {
				final Integer delta = calculateUpsetDelta(pool, bracket);
				class Delta implements Comparable<Delta> {
					private final Integer delta;
					private Delta(Integer delta) {
						this.delta = delta;
					}
					public int compareTo(Delta b) {
						// higher deltas win
						return b.delta.compareTo(this.delta);
					}
					
					public String toString() {
						return delta.toString();
					}
				}
				return new Delta(delta);
			} catch (DatabaseFailure e) {
				logger.error("TieBreakerTypeUpsetPrediction", e);
				return EQUALIZER;
			}
		}

		public String getTieBreakerReport(String bracketTieBreakerAnswer, Comparable<?> tieBreakerDelta) {
			return tieBreakerDelta.toString();
		}
	}
	
	private static Integer calculateUpsetDelta(Pool pool, Bracket bracket) {
		return new ShowTournamentController(null).getUpsetPredictionDelta(bracket, pool.getScoreSystem());
	}
	
	/** 
	 * Specialized tie breaker type where it uses upset prediction delta as the first tie breaker
	 * and closest number as the final tie breaker.
	 * @author Avery
	 */
	public static class TieBreakerTypeUpsetPredictionClosestNumber extends TieBreakerType {
		private final TieBreakerType first;
		public TieBreakerTypeUpsetPredictionClosestNumber(int tieBreakerID, String name) {
			super(tieBreakerID, name);
			first = new TieBreakerTypeUpsetPrediction(tieBreakerID, name);
		}

		public Comparable<?> getTieBreakerDelta(
				String tieBreakerAnswer, String bracketTieBreakerAnswer,
				Pool pool, Bracket bracket)
		{
			if(tieBreakerAnswer == null) return first.getTieBreakerDelta(tieBreakerAnswer, bracketTieBreakerAnswer, pool, bracket);
			if(!pool.getTournament().isStarted()) { // should never happen
				return calculateDelta(tieBreakerAnswer, bracketTieBreakerAnswer);
			}
			try {
				final Integer upsetDelta = calculateUpsetDelta(pool, bracket);
				final Integer closestNumberDelta = calculateDelta(tieBreakerAnswer, bracketTieBreakerAnswer);
				class CombinedDelta implements Comparable<CombinedDelta> {
					private final Integer upsetDelta;
					private final Integer closestNumberDelta;
					private CombinedDelta(Integer upsetDelta, Integer closestNumberDelta) {
						this.upsetDelta = upsetDelta;
						this.closestNumberDelta = closestNumberDelta;
					}
					public int compareTo(CombinedDelta b) {
						// higher deltas win upset comparisons
						int comparison = b.upsetDelta.compareTo(this.upsetDelta);
						if(comparison != 0) return comparison;
						// lower deltas win closest number comparisons
						else return this.closestNumberDelta.compareTo(b.closestNumberDelta);
					}
					
					public String toString() {
						return upsetDelta.toString() +" | "+closestNumberDelta.toString();
					}
				}
				return new CombinedDelta(upsetDelta, closestNumberDelta);
			} catch (DatabaseFailure e) {
				logger.error("TieBreakerTypeUpsetPredictionClosestNumber", e);
				return EQUALIZER;
			}
		}

		public void validate(String tieBreakerAnswer) {
			Integer.parseInt(tieBreakerAnswer);
		}
		
		public String getTieBreakerReport(String bracketTieBreakerAnswer,
				Comparable<?> tieBreakerDelta) {
			return tieBreakerDelta.toString() +" | "+super.getTieBreakerReport(bracketTieBreakerAnswer, tieBreakerDelta);
		}
	}

	private final Integer tieBreakerID;
	private final String name;

	TieBreakerType(int tieBreakerID, String name) {
		this.tieBreakerID = tieBreakerID;
		this.name = name;
	}

	public Integer getID() {
		return tieBreakerID;
	}

	public String getName() {
		return name;
	}

	public boolean mustAnswer() {
		return true;
	}

	public int getOid() {
		return tieBreakerID;
	}

	public abstract Comparable<?> getTieBreakerDelta(
			String tieBreakerAnswer, String bracketTieBreakerAnswer,
			Pool pool, Bracket bracket);

	public void validate(String tieBreakerAnswer) {}

	public String getTieBreakerReport(String bracketTieBreakerAnswer, Comparable<?> tieBreakerDelta) {
		return bracketTieBreakerAnswer;
	}
}
