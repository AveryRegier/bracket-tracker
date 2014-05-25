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
 * Created on Mar 9, 2004
 */
package com.tournamentpool.domain;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.broker.sql.delete.GroupDeleteBroker;
import com.tournamentpool.broker.sql.delete.GroupPlayerDeleteBroker;

import java.util.*;

/**
 * @author avery
 */
public class Group implements Comparable<Group> {
	private final UserManager um;
	private final int id;
	private String name;
	private Set<Integer> members;
	private Set<Pool> pools;
	private int adminOID;
	private int invitationCode;
	private Group parent;
	private TreeSet<Group> children = new TreeSet<Group>();
	
	/**
	 * @param groupOID
	 * @param name
	 * @param invitationCode TODO
	 */
	public Group(UserManager um, int groupOID, String name, int adminOID, int invitationCode, Group parent) {
		this.um = um;
		this.id = groupOID;
		this.name = name;
		this.adminOID = adminOID;
		this.invitationCode = invitationCode;
		this.parent = parent;
		if(parent != null){
			parent.addChild(this);
		}
	}

	private void addChild(Group group) {
		children.add(group);
	}
	
	public boolean hasChildren() {
		return !children.isEmpty();
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name The name to set.
	 */
	void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return Returns the id.
	 */
	public int getId() {
		return id;
	}
	
	void addMember(int playerOID) {
		members.add(new Integer(playerOID));
	}
	
	/**
	 * @return Returns the members.
	 */
	public synchronized Set<User> getMembers() {
		if(members == null) {
			members = new HashSet<Integer>();
			um.loadGroupMembers(this);
		}
		Set<User> these = new LinkedHashSet<User>();
		these.addAll(um.getPlayers(members));
		for (Group child : children) {
			these.addAll(child.getMembers());
		}
		return Collections.unmodifiableSet(these);
	}
	
	public synchronized Set<User> getMyMembers() {
		if(members == null) {
			members = new HashSet<Integer>();
			um.loadGroupMembers(this);
		}
		return Collections.unmodifiableSet(um.getPlayers(members));
	}

	
	/**
	 * @param pool
	 */
	public void addPool(Pool pool) {
		pools.add(pool);
	}

	/**
	 * @return
	 */
	public User getAdministrator() {
		return um.getUserObject(adminOID);
	}

	/**
	 * @return
	 */
	public Set<Pool> getPools() {
		if(pools == null) {
			pools = new LinkedHashSet<Pool>();
			um.loadPools(this);
		}
		if(parent != null) {
			LinkedHashSet<Pool> these = new LinkedHashSet<Pool>();
			these.addAll(pools);
			Set<Pool> parentPools = parent.getPools();
			for (Pool pool : parentPools) {
				these.add(new SubPool(pool, this));
			}
			these.addAll(parentPools);
			return Collections.unmodifiableSet(these);
		} else {
			return Collections.unmodifiableSet(pools);
		}
	}

	public Set<Pool> getMyPools() {
		if(pools == null) {
			pools = new LinkedHashSet<Pool>();
			um.loadPools(this);
		}
		return Collections.unmodifiableSet(pools);
	}

	public void addMembers(int[] playerIDs) {
		um.addPlayersTo(this, playerIDs);
		getMembers(); // make sure they are all loaded
		// if the group is already loaded, group won't contain the members just added, so add manually
		for (int playerID: playerIDs) {
			um.associateUserToGroup(getId(), playerID);
			um.associateGroupToUser(playerID, getId());	
		}
	}
	
	public boolean hasMember(int playerOID) {
		getMembers();
		if(members.contains(new Integer(playerOID))) return true;
		
		for (Group group : getChildren()) {
			if(group.hasMember(playerOID)) return true;
		}
		
		return false;
	}

	public int getInvitationCode() {
		return invitationCode;
	}
	
	public String getInvitationShortCode() {
		return invitationCode != 0 ? Integer.toString(invitationCode, 36).toUpperCase() : null;
	}
	
	public boolean validateInvitationShortCode(String code) {
		if(invitationCode == 0) return false;
		try {
			return convertInvitationCode(code) == invitationCode;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static int convertInvitationCode(String code) {
		return Integer.parseInt(code, 36);
	}

	void setInvitationCode(int invitationCode) {
		this.invitationCode = invitationCode;
	}

	public int compareTo(Group o) {
		return id - o.id;
	}

	public void removePool(Pool pool) {
		pools.remove(pool);
		um.removePool(pool);
	}

	public boolean mayDelete(User requestor) {
		return requestor != null 
		    && (requestor == getAdministrator() || requestor.isSiteAdmin())
		    && ((  getMyMembers().contains(getAdministrator()) && getMembers().size() == 1) // administrator is only member
		       || getMyMembers().isEmpty()) // or there are no members
		    && getMyPools().isEmpty(); // and there are no pools
	}
	
	public void applyDelete() {
		for (User user: getMembers()) {
			user.removeGroup(this);
		}
		um.removeGroup(this);
	}
	
	public boolean delete(User requestor, SingletonProvider sp) {
		if(mayDelete(requestor)) {
			if(!getMembers().isEmpty()) {
				// if we're past isEmpty, then due to mayDelete, we know the administrator is the only member
				removeMember(requestor, getAdministrator(), sp); // just in case the next one fails, be consistent with the db
			}
			new GroupDeleteBroker(sp, this).execute();
			return true;
		}
		return false;
	}
	
	public boolean mayRemoveMember(User requestor, User player) {
		if( (requestor.isSiteAdmin() || requestor == getAdministrator() || requestor == player)
		    && (getMyMembers().contains(player))) {
			// does this player have any brackets in a pool in this group?
			for (Pool pool: getPools()) {
				for (Bracket bracket: pool.getBrackets()) {
					if(bracket.getOwner() == player) return false;
				}
			}
			return true;
		}
		return false;
	}

	public boolean removeMember(User requestor, User player, SingletonProvider sp) {
		if(mayRemoveMember(requestor, player)) {
			new GroupPlayerDeleteBroker(sp, this, new int[] {player.getOID()}).execute();
			members.remove(new Integer(player.getOID()));
			player.removeGroup(this);
			return true;
		}
		return false;
	}
	
	public Group getParent() {
		return parent;
	}

	public Pool getPool(int id2) {
		Set<Pool> poolSet = getPools();
		for (Pool pool : poolSet) {
			if(pool.getOid() == id2) {
				return pool;
			}
		}
		return null;
	}

	public Iterable<Group> getChildren() {
		return Collections.unmodifiableSet(children);
	}

    public boolean isInHierarchy(Group parent) {
        Group group = this;
        do {
            if(group == parent) return true;
            group = group.getParent();
        } while(group != null);

        return false;
    }
}
