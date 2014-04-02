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
 * Created on Oct 1, 2004
 */
package com.tournamentpool.beans;

import com.tournamentpool.domain.*;
import utility.StringUtil;

import java.sql.SQLException;
import java.util.*;

/**
 * @author Avery J. Regier
 */
public class PoolBean extends BracketHolderBean implements Comparable<PoolBean> {
	private int oid;
	private String name;
	private PlayerBean owner;
	private ScoreSystemBean scoreSystem;
	private GroupBean group;
	private TournamentBean tournament;
	private boolean closed = false;
	private boolean editable = false;
	private int bracketLimit;
	private boolean showBracketsEarly;
	private TieBreakerType tieBreakerType;
	private String tieBreakerQuestion;
	private String tieBreakerAnswer;
	private boolean tieBreakerNeeded;
	private boolean mayDelete;
	private boolean mayRemoveBracket;
	private Set<String> email = new LinkedHashSet<String>();
	private boolean showGroups = false;

    private TreeMap<GroupBean, List<Integer>> groupScores = new TreeMap<GroupBean, List<Integer>>();

	/**
	 * @param name
	 * @param oid
	 *
	 */
	public PoolBean(int oid, String name) {
		this.oid = oid;
		if(StringUtil.killWhitespace(name) != null) {
			this.name = name;
		} else {
			this.name = "No Name";
		}
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Returns the oid.
	 */
	public int getOid() {
		return oid;
	}

	public int compareTo(PoolBean o) {
		return oid - o.oid;
	}

	/**
	 * @param owner
	 */
	public void setOwner(User owner) {
		if(owner != null) {
			this.owner = new PlayerBean(owner.getOID(), owner.getName());
		}
	}

	public PlayerBean getOwner() {
		return owner;
	}

	/**
	 * @param scoreSystem
	 */
	public void setScoreSystem(ScoreSystem scoreSystem) {
		if(scoreSystem != null) {
			this.scoreSystem = new ScoreSystemBean();
			this.scoreSystem.setScoreSystem(scoreSystem);
		}
	}

	public ScoreSystemBean getScoreSystem() {
		return scoreSystem;
	}

	/**
	 * @return Returns the group.
	 */
	public GroupBean getGroup() {
		return group;
	}

	/**
	 * @param group The group to set.
	 */
	public void setGroup(GroupBean group) {
		this.group = group;
	}

	public TournamentBean getTournament() {
		return tournament;
	}

	public void setTournament(TournamentBean bean) {
		this.tournament = bean;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public void setBracketLimit(int bracketLimit) {
		this.bracketLimit = bracketLimit;
	}

	public void setShowBracketsEarly(boolean showBracketsEarly) {
		this.showBracketsEarly = showBracketsEarly;
	}

	public int getBracketLimit() {
		return bracketLimit;
	}

	public boolean isShowBracketsEarly() {
		return showBracketsEarly;
	}

	public void setTieBreaker(TieBreakerType tieBreakerType, String tieBreakerQuestion, String tieBreakerAnswer) {
		this.tieBreakerType = tieBreakerType;
		this.tieBreakerQuestion = tieBreakerQuestion;
		this.tieBreakerAnswer = tieBreakerAnswer;
	}

	public void setTieBreakerNeeded(boolean tieBreakerNeeded) {
		this.tieBreakerNeeded = tieBreakerNeeded;
	}

	public String getTieBreakerQuestion() {
		return tieBreakerQuestion;
	}

	public TieBreakerType getTieBreakerType() {
		return tieBreakerType;
	}

	public String getTieBreakerAnswer() {
		return tieBreakerAnswer;
	}

	public boolean isTieBreakerNeeded() {
		return tieBreakerNeeded;
	}

	public void setMayDelete(boolean mayDelete) {
		this.mayDelete = mayDelete;
	}
	
	public boolean isDeletable() {
		return mayDelete;
	}
	
	protected void addOtherAttributes(Bracket bracket, Pool pool, User user,
			BracketBean<?> bracketBean, PoolBean poolBean) throws SQLException 
	{
		if(pool != null) {
			bracketBean.setMayRemove(pool.mayRemoveBracket(user, bracket));
		}
    }

    protected void setupTeamScores(BracketBean<?> bracketBean, Iterable<Group> membershipGroupsInHierarchy) {
        for(Group group: membershipGroupsInHierarchy) {
            GroupBean groupBean = new GroupBean(group);
            List<Integer> scores = groupScores.get(groupBean);
            if(scores == null) {
                scores = new ArrayList<Integer>();
                groupScores.put(groupBean, scores);
            }
            scores.add(bracketBean.getScore().getCurrent());
        }
    }

    public Iterator getTeamScores() {
        Set<TeamScore> averageMap = new TreeSet<TeamScore>(new Comparator<TeamScore>() {
            public int compare(TeamScore abean, TeamScore bbean) {
                if(abean == bbean) return 0;
                Float aScore = abean.getValue();
                Float bScore = bbean.getValue();
                if(aScore != null && bScore != null) {
                    float score = aScore - bScore;
                    if(score != 0) return (int)(-score * 100); // highest score first
                }

                if(abean.getKey().getName() != null) {
                    int result = abean.getKey().getName().compareTo(bbean.getKey().getName());
                    if(result != 0) return result;
                }
                return abean.getKey().getOid() - bbean.getKey().getOid();
            }});

        for(Map.Entry<GroupBean, List<Integer>> entry : groupScores.entrySet()) {
            GroupBean group = entry.getKey();
            List<Integer> scores = entry.getValue();
            int total = 0;
            int count = 0;
            for(; count<3 && count<scores.size(); count++)  {
                total += scores.get(count);
            }
            averageMap.add(new TeamScore(group, total/count));
        }

        return averageMap.iterator();
    }

	public void setMayRemove(boolean mayRemoveBracket) {
		this.mayRemoveBracket = mayRemoveBracket;
	}
	
	public boolean isRemovable() {
		return mayRemoveBracket;
	}
	
	public void addEmail(String emailAddress) {
		email.add(emailAddress);
	}
	
	public String getMailTo() {
		StringBuffer sb = new StringBuffer("mailto:");
		boolean hasPrevious = false;
		for (String address : email) {
			if(address != null) {
				if(hasPrevious) {
					sb.append(",");
				}
				sb.append(address);
				hasPrevious = true;
			}
		}
		sb.append("?subject=");
		if(group != null && group.getName() != null){
			sb.append(group.getName());
			sb.append(' ');
		}
		if(getName() != null) {
			sb.append(getName());
		}
		return sb.toString().trim();
		
	}

	public void setShowGroups(boolean showGroups) {
		this.showGroups = showGroups;
	}

	public boolean isShowGroups() {
		return showGroups;
	}

    private static class TeamScore implements Map.Entry<GroupBean, Float> {
        private final GroupBean group;
        private final float score;

        public TeamScore(GroupBean group, float score) {
            this.group = group;
            this.score = score;
        }

        @Override
        public GroupBean getKey() {
            return group;
        }

        @Override
        public Float getValue() {
            return score;
        }

        @Override
        public Float setValue(Float value) {
            return null;
        }
    }
}
