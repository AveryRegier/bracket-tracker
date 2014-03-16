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
 * Created on Mar 21, 2004
 */
package com.tournamentpool.beans;

import com.tournamentpool.controller.Filter;
import com.tournamentpool.domain.Bracket;
import com.tournamentpool.domain.Group;
import com.tournamentpool.domain.Pool;
import com.tournamentpool.domain.User;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Avery J. Regier
 */
public class PlayerBean extends BracketHolderBean implements Comparable<PlayerBean> {
	private final int oid;
	private final String name;
	private final boolean siteAdmin;
    private String id;

    /**
	 * 
	 */
	public PlayerBean(int oid, String name) {
		this.oid = oid;
		this.name = name;
		this.siteAdmin = false;
	}

	public PlayerBean(int oid, String name, boolean siteAdmin) {
		this.oid = oid;
		this.name = name;
		this.siteAdmin = siteAdmin;
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
	public int getNumGroups() {
		return groupBeans.size();
	}

	/**
	 * @return
	 */
	public int getOid() {
		return oid;
	}

	private Set<GroupBean> groupBeans = new LinkedHashSet<GroupBean>();
	private boolean mayRemoveUser;
	public Iterator<GroupBean> getGroups() {
		return groupBeans.iterator();
	}
	
	public void setGroups(Iterator<Group> groups) throws SQLException {
		while (groups.hasNext()) {
			Group group = groups.next();
			if(group != null) {
				GroupBean groupBean = new GroupBean(group);
				groupBean.setPools(group.getPools(), (User)null);
				groupBean.setAdmin(null, group.getAdministrator() != null && group.getAdministrator().getOID() == getOid());
				groupBeans.add(groupBean);
			}
		}
	}

	public void setGroups(Iterator<Group> groups, Filter filter, User user) throws SQLException {
		while (groups.hasNext()) {
			Group group = groups.next();
			if(filter.pass(group)) { // already does a null check
				GroupBean groupBean = new GroupBean(group);
				groupBean.setPools(group.getPools(), filter, user);
				groupBean.setAdmin(null, group.getAdministrator() != null && group.getAdministrator().getOID() == getOid());
				groupBean.setMayDelete(group.mayDelete(user));
				groupBeans.add(groupBean);
			}
		}
	}

	public int compareTo(PlayerBean o) {
		return oid - o.oid;
	}
	
	public boolean isSiteAdmin() {
		return siteAdmin;
	}
	
	protected void addOtherAttributes(Bracket bracket, Pool pool, User user,
			BracketBean<?> bracketBean, PoolBean poolBean) throws SQLException {
		if(pool != null && poolBean != null) {
			poolBean.setMayRemove(pool.mayRemoveBracket(user, bracket));
		}
	}

	public void setMayDelete(boolean mayRemoveUser) {
		this.mayRemoveUser = mayRemoveUser;
	}
	
	public boolean isDeletable() {
		return mayRemoveUser;
	}

    public void setLoginId(String id) {
        this.id = id;
    }

    public String getLoginId() {
        return id;
    }
}
