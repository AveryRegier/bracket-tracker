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

/*
 * Created on Mar 22, 2004
 */
package com.tournamentpool.beans;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import utility.StringUtil;

import com.tournamentpool.controller.Filter;
import com.tournamentpool.domain.Group;
import com.tournamentpool.domain.Pool;
import com.tournamentpool.domain.User;

/**
 * @author Avery J. Regier
 */
public class GroupBean implements Comparable<GroupBean>{
	private int oid;
	private String name;
	private Set<GroupPlayerBean> members;
	private Set<PoolBean> pools;
	private PlayerBean admin;
	private String invitationCode;
	private boolean currentUserIsAdmin = false;
	private boolean mayDelete;
	private TreeSet<GroupBean> subGroups;

	/**
	 * @param group
	 */
	public GroupBean(Group group) {
		this.oid = group.getId();
		if(StringUtil.killWhitespace(group.getName()) != null) {
			this.name = group.getName();
		} else {
			this.name = "No Name";
		}
		this.invitationCode = group.getInvitationShortCode();
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
	public int getOid() {
		return oid;
	}

	public Iterator<GroupPlayerBean> getMembers() {
		return members.iterator();
	}

	public int getNumMembers() {
		return members != null ? members.size() : 0;
	}

	/**
	 * @param members The members to set.
	 * @throws SQLException 
	 */
	public void setMembers(Set<User> members, User requestor, Group group) throws SQLException {
		TreeSet<GroupPlayerBean> playerBeans = new TreeSet<GroupPlayerBean>();
		Iterator<User> iter = members.iterator();
		while (iter.hasNext()) {
			User player = iter.next();
			playerBeans.add(new GroupPlayerBean(player.getOID(), player.getName(), group.mayRemoveMember(requestor, player)));
		}
		this.members = playerBeans;
	}

	public void addMembers(Set<User> members, User requestor, Group group) throws SQLException {
		GroupBean groupBean = new GroupBean(group);
		Iterator<User> iter = members.iterator();
		while (iter.hasNext()) {
			User player = iter.next();
			this.members.add(new GroupPlayerBean(player.getOID(), player.getName(), group.mayRemoveMember(requestor, player), groupBean));
		}
	}
	
	/**
	 * @return Returns the admin.
	 */
	public PlayerBean getAdmin() {
		return admin;
	}

	/**
	 * @param admin The admin to set.
	 */
	public void setAdmin(PlayerBean admin, boolean currentUserIsAdmin) {
		this.admin = admin;
		this.currentUserIsAdmin = currentUserIsAdmin;
	}


	public Iterator<PoolBean> getPools() {
		return pools.iterator();
	}

	public int getNumPools() {
		return pools != null ? pools.size() : 0;
	}

	/**
	 * @param user 
	 * @param members The members to set.
	 * @throws SQLException
	 */
	public void setPools(Set<Pool> poolObjects, User user) throws SQLException {
		TreeSet<PoolBean> poolBeans = new TreeSet<PoolBean>();
		if(poolObjects != null) {
			Iterator<Pool> pools = poolObjects.iterator();
			while (pools.hasNext()) {
				Pool pool = pools.next();
				if(pool != null) {
					PoolBean poolBean = new PoolBean(pool.getOid(), pool.getName());
					poolBean.setBrackets(pool.getBrackets().iterator(), null);
					poolBean.setShowBracketsEarly(pool.isShowBracketsEarly());
					poolBean.setBracketLimit(pool.getBracketLimit());
					poolBean.setMayDelete(pool.mayDelete(user));
					poolBeans.add(poolBean);
				}
			}
		}
		this.pools = poolBeans;
	}

	public String getInvitationCode() {
		return invitationCode;
	}

	public boolean isCurrentUserAdmin() {
		return currentUserIsAdmin;
	}

	public void setPools(Set<Pool> poolObjects, Filter filter, User user) throws SQLException {
		TreeSet<PoolBean> poolBeans = new TreeSet<PoolBean>();
		if(poolObjects != null) {
			Iterator<Pool> pools = poolObjects.iterator();
			while (pools.hasNext()) {
				Pool pool = pools.next();
				if(filter.pass(pool)) { // already does a null check
					PoolBean poolBean = new PoolBean(pool.getOid(), pool.getName());
					poolBean.setBrackets(pool.getBrackets().iterator(), filter, null);
					poolBean.setShowBracketsEarly(pool.isShowBracketsEarly());
					poolBean.setBracketLimit(pool.getBracketLimit());
					poolBean.setMayDelete(pool.mayDelete(user));
					poolBeans.add(poolBean);
				}
			}
		}
		this.pools = poolBeans;
	}

	public void setMayDelete(boolean mayDelete) {
		this.mayDelete = mayDelete;
	}
	
	public boolean isDeletable() {
		return mayDelete;
	}

	public void setSubGroups(Iterable<Group> children) {
		TreeSet<GroupBean> groupBeans = new TreeSet<GroupBean>();
		if(children != null) {
			for (Group group : children) {
				groupBeans.add(new GroupBean(group));
			}
		}
		this.subGroups = groupBeans;
	}

	public Iterator<GroupBean> getSubGroups() {
		return subGroups == null || subGroups.isEmpty() ? null : subGroups.iterator();
	}

	@Override
	public int compareTo(GroupBean o) {
		// TODO Auto-generated method stub
		return getName().compareTo(o.getName());
	}
}
