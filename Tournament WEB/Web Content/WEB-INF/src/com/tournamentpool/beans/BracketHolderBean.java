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
 * Created on Oct 18, 2004
 */
package com.tournamentpool.beans;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import com.tournamentpool.controller.Filter;
import com.tournamentpool.domain.Bracket;
import com.tournamentpool.domain.Pool;
import com.tournamentpool.domain.PoolBracket;
import com.tournamentpool.domain.ScoreSystem.Score;
import com.tournamentpool.domain.User;

/**
 * @author Avery J. Regier
 */
public class BracketHolderBean {
	private Set<BracketBean<?>> bracketBeans = new TreeSet<BracketBean<?>>(new Comparator<BracketBean<?>>() {
		public int compare(BracketBean<?> abean, BracketBean<?> bbean) {
			if(abean == bbean) return 0;
			Score aScore = abean.getScore();
			Score bScore = bbean.getScore();
			if(aScore != null && bScore != null) {
				int score = aScore.getCurrent() - bScore.getCurrent();
				if(score != 0) return -score; // highest score first
			}
			if(abean.getRank() != 0 && bbean.getRank() != 0) {
				int result = abean.getRank() - bbean.getRank();
				if(result != 0) return result; // lowest delta first
			}
			if(aScore != null && bScore != null) {
				int score = abean.getScore().getMax() - bbean.getScore().getMax();
				if(score != 0) return -score; // highest max available first
			}
			if(abean.getUserName() != null) {
				int result = abean.getUserName().compareTo(bbean.getUserName());
				if(result != 0) return result;
			}
			if(abean.getName() != null) {
				int result = abean.getName().compareTo(bbean.getName());
				if(result != 0) return result;
			}
			return abean.getOid() - bbean.getOid();
		}});

	public BracketHolderBean() {
		super();
	}

	/**
	 * @return
	 */
	public int getNumBrackets() {
		return bracketBeans.size();
	}

	public Iterator<BracketBean<?>> getBrackets() {
		return bracketBeans.iterator();
	}

	/**
	 * @param brackets
	 * @param user 
	 */
	public void setBrackets(Iterator<Bracket> brackets, User user) {
		setBrackets(brackets, (Filter)null, user);
	}

	protected void addOtherAttributes(Bracket bracket, Pool pool, User user, 
			BracketBean<?> bracketBean, PoolBean poolBean) throws SQLException {
		
	}

	@SuppressWarnings("rawtypes")
	public void setBrackets(Iterator<Bracket> brackets, Filter filter, User user) {
		try {
			if(brackets != null) {
				while (brackets.hasNext()) {
					Bracket bracket = brackets.next();
					// if the filter exists, use it, else ignore
					if(bracket != null && (filter != null ? filter.pass(bracket) : true)) { 
						BracketBean<?> bracketBean = new BracketBean();
						bracketBean.setOid(bracket.getOID());
						bracketBean.setName(bracket.getName());
						User owner = bracket.getOwner();
						if(owner != null) {
							bracketBean.setUserName(owner.getName());
							bracketBean.setMayDelete(bracket.mayDelete(user));
						}
						bracketBean.setTournament(bracket.getTournament());
						Iterator<Pool> pools = bracket.getPools();
						while (pools.hasNext()) {
							Pool pool = pools.next();
							PoolBean poolBean = bracketBean.addPool(pool);
							addOtherAttributes(bracket, pool, user, bracketBean, poolBean);
						}
						this.bracketBeans.add(bracketBean);
					}
				}
			}
		} catch (SQLException e) {
		}
	}

	/**
	 * @param brackets
	 * @param user 
	 * @throws SQLException
	 */
	@SuppressWarnings({ "rawtypes" })
	public void setPoolBrackets(Pool apool, Iterator<PoolBracket> brackets, User user) throws SQLException {
		if(brackets != null) {
			while (brackets.hasNext()) {
				PoolBracket poolBracket = brackets.next();
				if(poolBracket != null) {
					BracketBean<?> bracketBean = new BracketBean();
					Bracket bracket = poolBracket.getBracket();
					bracketBean.setOid(bracket.getOID());
					bracketBean.setName(bracket.getName());
					User owner = bracket.getOwner();
					if(owner != null) {
						bracketBean.setUserName(owner.getName());
					}
					bracketBean.setTournament(bracket.getTournament());
					bracketBean.setGroup(poolBracket.getGroup());
					bracketBean.setScore(poolBracket.getScore());
					bracketBean.setRank(poolBracket.getRank());
					bracketBean.setMaxRank(poolBracket.getMaxRank());
					bracketBean.setBeatBy(poolBracket.getBeatBy());
					bracketBean.setRootingFor(poolBracket.getScore().getRootingFor(), apool);
					bracketBean.setTieBreakerAnswer(poolBracket.getTieBreakerAnswer());
					Iterator<Pool> pools = bracket.getPools();
					while (pools.hasNext()) {
						Pool pool = pools.next();
						PoolBean poolBean = bracketBean.addPool(pool);
						addOtherAttributes(bracket, pool, user, bracketBean, poolBean);
					}
					bracketBeans.add(bracketBean);
				}
			}
		}
	}
}
