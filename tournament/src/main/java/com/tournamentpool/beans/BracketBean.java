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
 * Created on Mar 14, 2004
 */
package com.tournamentpool.beans;

import com.tournamentpool.controller.GameVisitorCommon;
import com.tournamentpool.domain.*;
import com.tournamentpool.domain.ScoreSystem.Score;
import utility.StringUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Avery J. Regier
 */
public class BracketBean<T extends GameVisitorCommon.Node> {
	private String name;
	private List<T> bracket;
	private int maxLevels;
	private Set<Map.Entry<Integer, Opponent>> oponents;
	private int id;
	private String userName;
	private Score score;
	private List<PoolBean> pools;
	private String bracketType = "Bracket";
	private TournamentBean tournament;
	private int rank;
	private String tieBreakerAnswer;
	private int maxRank;
	private String comment;
	private boolean mayDelete;
	private boolean mayRemoveBracket;
	private String beatBy;
	private GroupBean group;

	/**
	 *
	 */
	public BracketBean() {
		super();
	}

	/**
	 * @param string
	 */
	public void setName(String name) {
		if(StringUtil.killWhitespace(name) != null) {
			this.name = name;
		} else {
			this.name = "No Name";
		}
	}

	/**
	 * @param nodes
	 */
	public void setBracket(List<T> nodes) {
		this.bracket = nodes;
	}

	/**
	 * @return
	 */
	public List<T> getBracket() {
		return bracket;
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
	public int getMaxLevels() {
		return maxLevels;
	}

	/**
	 * @param i
	 */
	public void setMaxLevels(int i) {
		maxLevels = i;
	}

	/**
	 * @return Returns the oponents.
	 */
	public Set<Map.Entry<Integer, Opponent>> getOponents() {
		return oponents;
	}

	/**
	 * @param oponents The oponents to set.
	 */
	public void setOponents(Set<Map.Entry<Integer, Opponent>> oponents) {
		this.oponents = oponents;
	}

	/**
	 * @param oid
	 */
	public void setOid(int oid) {
		this.id = oid;
	}

	public int getOid() {
		return id;
	}

	/**
	 * @return Returns the userName.
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName The userName to set.
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @param score
	 */
	public void setScore(Score score) {
		this.score = score;
	}

	public Score getScore() {
		return score;
	}

	public void setTournament(Tournament tournament) {
		if(tournament != null) {
			this.tournament = new TournamentBean(tournament, false, 
					tournament.hasAllSeedsAssigned(), tournament.getStartTime(), tournament.getLastUpdated(), false);
		}
	}

	public PoolBean addPool(Pool pool) {
		if(pool != null) {
			if(pools == null) pools = new ArrayList<>();
			PoolBean poolBean = new PoolBean(pool.getOid(), pool.getName());
			if(pool.getGroup() != null) {
				poolBean.setGroup(new GroupBean(pool.getGroup()));
			}
			pools.add(poolBean);
			return poolBean;
		}
		return null;
	}

	public List<PoolBean> getPools() {
		return pools;
	}
	
	public int getNumPools() {
		return pools != null ? pools.size() : 0; 
	}

	public void setBracketType(String bracketType) {
		this.bracketType = bracketType;
	}

	public String getBracketType() {
		return bracketType;
	}

	public TournamentBean getTournament() {
		return tournament;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public void setTieBreakerAnswer(String tieBreakerAnswer) {
		this.tieBreakerAnswer = tieBreakerAnswer;
	}

	public String getTieBreakerAnswer() {
		return tieBreakerAnswer;
	}

	public void setMaxRank(int maxRank) {
		this.maxRank = maxRank;
	}

	public int getMaxRank() {
		return maxRank;
	}

	public boolean isOut() {
		return maxRank > 1;
	}

	public void setRootingFor(Seed[] rootingFor, Pool pool) {
		if(isOut()) this.comment = "Out"; // might get overridden later
		if(rootingFor.length == 0) {
			if(!isOut()) {
				if(pool.getTournament().isComplete()) {
					if(pool.isTiebreakerNeeded()) {
						this.comment = "Possible winner";
					} else if(rank == 1){ // the rank takes tie breakers into account
						this.comment = "Winner!";
					} else {
						this.comment = "Lost tie breaker";
					}
				} else {
					this.comment = "Complex scenario likely";
				}
			}
			return;
		}
		StringBuffer sb = new StringBuffer("Rooting for ");
        List<String> rootingForList = Arrays.asList(rootingFor)
                .stream()
                .map(r -> pool.getTournament().getTeam(r).getName())
                .collect(Collectors.toList());

        sb.append(String.join(",", rootingForList));
        sb.append('.');

		if(beatBy != null) {
			if(sb.length() > 0) sb.append(" ");
			sb.append("Beat by ");
			sb.append(beatBy);
		}
		this.comment = sb.toString();
	}

	public String getComment() {
		return comment;
	}

	public void setMayDelete(boolean mayDelete) {
		this.mayDelete = mayDelete;		
	}
	
	public boolean isDeletable() {
		return mayDelete;
	}

	public void setMayRemove(boolean mayRemoveBracket) {
		this.mayRemoveBracket = mayRemoveBracket;
	}
	
	public boolean isRemovable() {
		return mayRemoveBracket;
	}

	public void setBeatBy(String beatBy) {
		this.beatBy = beatBy;
	}

	public void setGroup(Group group) {
		this.group = new GroupBean(group);
	}
	
	public GroupBean getGroup() {
		return group;
	}
}
