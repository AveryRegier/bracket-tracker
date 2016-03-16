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
 * Created on Feb 20, 2003
 */
package com.tournamentpool.domain;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author avery
 */
public class User {
	private String pw;
	private String name;
	private final int oid;
	private String id;
	private String auth;
	private boolean siteAdmin;
	private final Set<Group> groups = new TreeSet<>();
	private final Map<Object, Bracket> brackets = new HashMap<>();
	private String email;

	/**
	 * @param userid
	 * @param id
	 * @param name
	 * @param pw
	 */
	User(String userid, int id, String name, String pw, boolean siteAdmin, String email) {
		this.id = userid;
		this.oid = id;
		this.name = name;
		this.pw = pw;
		this.siteAdmin = siteAdmin;
		this.email = email;
	}

	public String getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	String checkPassword(String pw) {
		if(this.pw.equals(pw)) {
			return resetAuth();
		}
		return null;
	}


	private String resetAuth() {
		byte[] bytes = new byte[10];
		new Random(System.currentTimeMillis()).nextBytes(bytes);
		auth = new String(bytes);
		try {
			auth = URLEncoder.encode(auth, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return auth;
	}

	boolean authenticate(String auth) {
		return auth != null && auth.equals(this.auth);
	}

	public int getOID() {
		return oid;
	}

	/**
	 * @param group
	 */
	public void addGroup(Group group) {
		groups.add(group);
	}

	public Collection<Group> getGroups() {
		return Collections.unmodifiableCollection(groups);
	}
	
	public Group getGroupInHierarchy(Group parent) {
		List<Group> matches = groups.stream()
				.filter(g -> g.isInHierarchy(parent))
				.collect(Collectors.toList());

		return matches.stream()
				.filter(p-> !matches.stream().anyMatch(g->g.isInHierarchy(p)))
				.findFirst().orElse(null);
	}

	public Iterable<Group> getGroupsInHierarchy() {

		Set<Group> allGroups = new TreeSet<>();
		for(Group group: groups) {
			do {
				allGroups.add(group);
				group = group.getParent();				
			} while(group != null);
		}
		return allGroups;
	}

    public Iterable<Group> getMembershipGroupsInHierarchy(Group parent) {
        return groups.stream()
                .filter(group -> group.isInHierarchy(parent))
                .collect(Collectors.toCollection(() -> new TreeSet<>()));
    }

    /**
	 * @param bracket
	 */
	public void loadBracket(Bracket bracket) {
		brackets.put(bracket.getID(), bracket);
	}

	/**
	 * @param bracketOid
	 * @return
	 */
	public Bracket getBracket(int bracketOid) {
		return brackets.get(bracketOid);
	}

	public Collection<Bracket> getBrackets() {
		return brackets.values();
	}

	public boolean isSiteAdmin() {
		return siteAdmin;
	}

	public void setSiteAdmin(boolean siteAdmin) {
		this.siteAdmin = siteAdmin;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}

	void setPassword(String newPassword) {
		this.pw = newPassword;
		resetAuth();
	}


	public String getAuth() {
		if(auth == null) return resetAuth();
		return auth;
	}

	public void removeBracket(Bracket bracket) {
		brackets.remove(bracket.getID());
	}

	public void removeGroup(Group group) {
		groups.remove(group);
	}

    public void setName(String name) {
        this.name = name;
    }

    public void setID(String userID) {
        this.id = userID;
    }

    public boolean hasBrackets() {
        return !brackets.isEmpty();
    }

    // what organizations does the user belong to?
	// what brackets does the user have?
	// to what pools has this user been invited?

}
