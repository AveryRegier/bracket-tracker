/* 
Copyright (C) 2003-2013 Avery J. Regier.

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
 * Created on Feb 22, 2003
 */
package com.tournamentpool.domain;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.application.SingletonProviderHolder;
import com.tournamentpool.broker.mail.JavaMailEmailBroker;
import com.tournamentpool.broker.sql.DatabaseFailure;
import com.tournamentpool.broker.sql.delete.PlayerDeleteBroker;
import com.tournamentpool.broker.sql.get.*;
import com.tournamentpool.broker.sql.insert.*;
import com.tournamentpool.broker.sql.update.*;
import utility.StringUtil;
import utility.menu.Menu;
import utility.menu.reference.ReferenceMenu;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author avery
 */
public class UserManager extends SingletonProviderHolder {
	private final Map<String, User> users = new HashMap<>();
	private final Map<Integer, User> players = new HashMap<>();
	private final Map<Integer, Group> groups = new HashMap<>();
	private final Map<Integer, Pool> pools = new HashMap<>();
	private final Map<Integer, TieBreakerType> tieBreakerTypes = new TreeMap<>();

	/**
	 *
	 */
	public UserManager(SingletonProvider sp) {
		super(sp);
	}

	/**
	 * @param userID
	 * @param auth
	 * @return User
	 */
	public User getUser(String userID, String auth) {
		User user = getUserObject(userID);
		// TODO: check the auth secure token
		if(user.authenticate(auth)) return user;
		else return null;
	}

	public boolean userHasEmail(String userID) {
		User user = users.get(userID);
		if(user == null)  {
			new PlayerGetByLoginBroker(sp, userID).execute();
			user = users.get(userID);
			if(user != null) fillUser(user);
		}
		return user != null && user.getEmail() != null;
	}

	public String registerUser(String userID, String password, String name, String email) {
		userID = StringUtil.killWhitespace(userID);
		password = StringUtil.killWhitespace(password);
		name = StringUtil.killWhitespace(name);
		email = StringUtil.killWhitespace(email);
		if(userID != null && password != null && name != null && users.get(userID) == null)  {
			new PlayerGetByLoginBroker(sp, userID).execute();
			if(users.get(userID) == null) {
				// The first player registered should be an Admin
				boolean checkForAdmin = players.size() == 0;
				new PlayerInsertBroker(sp, userID, password, name, email).execute();
				User user = users.get(userID);
				fillUser(user);
				if(checkForAdmin && user.getOID() == 1) {
					// make this user an admin
					new SiteAdminUpdateBroker(sp, user, true).execute();
				}
				return user.checkPassword(password);
			}
		}
		return null;
	}

	private User getUserObject(String userID) {
		User user = users.get(userID);
		if(user == null)  {
			new PlayerGetByLoginBroker(sp, userID).execute();
			user = users.get(userID);
			if(user == null) throw new IllegalArgumentException("User "+userID+" does not appear to exist.");
			fillUser(user);
		}
		return user;
	}

	public User getUserObject(int playerOID) {
		Integer oid = playerOID;
		User user = players.get(oid);
		if(user == null)  {
			new PlayerGetByOIDBroker(sp, playerOID).execute();
			user = players.get(oid);
			if(user == null) throw new IllegalArgumentException("User "+playerOID+" does not appear to exist.");
			fillUser(user);
		}
		return user;
	}

	/**
	 * @param user
	 */
	private void fillUser(User user) {
		new PlayerGroupsGetBroker(sp, user.getOID()).execute();
		new PlayerBracketsGetBroker(sp, user.getOID()).execute();
	}

	/**
	 * @param userID
	 * @param password
	 * @return String
	 */
	public String authenticate(String userID, String password) {
		try {
			return getUserObject(userID).checkPassword(password);
		} catch (IllegalArgumentException e) {
			System.err.println("Login with unknown user "+userID+" attempted.");
			return null;
		}
	}

	/**
	 * @param userid
	 */
	public void loadPlayer(String userid, int id, String name, String pw, boolean siteAdmin, String email) {
		User user = new User(userid, id, name, pw, siteAdmin, email);
		users.put(userid, user);
		players.put(id, user);
	}

	/**
	 * @param playerOID
	 * @param groupOID
	 */
	public void associateGroupToUser(int playerOID, int groupOID) {
		User user = getUserObject(playerOID);
		Group group = getGroupObject(groupOID);
		user.addGroup(group);
		// don't add user to group because we aren't getting ALL users for the
		// group at this point
	}

	public User getUser(int playerOID) {
		return players.get(playerOID);
	}

	public Group getGroup(int groupOID) {
		return groups.get(groupOID);
	}

	public Group getGroupObject(int groupOID) {
		Group group = getGroup(groupOID);
		if(group == null)  {
			new GroupGetBroker(sp, groupOID).execute();
			group = getGroup(groupOID);
		}
		return group;
	}

	/**
	 * @param groupOID
	 * @param name
	 * @param adminID
	 * @param invitationCode TODO
	 */
	public void loadGroup(int groupOID, String name, int adminID, int invitationCode, int parentGroupID) {
		Group parent = null;
		if(parentGroupID != 0) {
			parent = getGroupObject(parentGroupID);
		}
		Group group = new Group(this, groupOID, name, adminID, invitationCode, parent);
		groups.put(groupOID, group);
	}

	public void updateGroup(Group group, boolean enableInvitation) {
		if(enableInvitation && group.getInvitationCode() == 0) {
			int invitationCode = Math.abs((int)System.currentTimeMillis());
			GroupUpdateBroker groupUpdateBroker = new GroupUpdateBroker(sp, group.getId(), invitationCode);
			groupUpdateBroker.execute();
			group.setInvitationCode(invitationCode);
		} else if((!enableInvitation) && group.getInvitationCode() != 0) {
			GroupUpdateBroker groupUpdateBroker = new GroupUpdateBroker(sp, group.getId(), 0);
			groupUpdateBroker.execute();
			group.setInvitationCode(0);
		}
	}

	public void updatePool(Pool pool, String name, Tournament tournament, ScoreSystem scoreSystem,
			int bracketLimit, boolean showBracketsEarly, int tieBreakerTypeID, String tieBreakerQuestion)
	{
		tieBreakerQuestion = StringUtil.killWhitespace(tieBreakerQuestion);
		TieBreakerType tieBreakerType = getTieBreakerType(tieBreakerTypeID);
		if(pool.getTournament() != tournament || pool.getScoreSystem() != scoreSystem ||
		   pool.getTieBreakerType() != tieBreakerType ||
		   !(pool.getTieBreakerQuestion() == tieBreakerQuestion || pool.getTieBreakerQuestion() != null && pool.getTieBreakerQuestion().equals(tieBreakerQuestion)))
		{
			if(!pool.getBrackets().isEmpty()) {
				throw new IllegalStateException("Pools that have brackets may not be changed.");
			}
		}
		new PoolUpdateBroker(sp, pool, name, tournament, scoreSystem, bracketLimit, showBracketsEarly,
				tieBreakerType, tieBreakerQuestion).execute();
	}
	/**
	 * @param groupOID
	 */
	public void associatePoolsToGroup(int groupOID, int poolOID) {
		getGroup(groupOID).addPool(getPoolObject(poolOID));
	}

	/**
	 * @param poolOID
	 */
	public Pool getPoolObject(int poolOID) {
		Pool pool = getPool(poolOID);
		if(pool == null)  {
			new PoolGetBroker(sp, poolOID).execute();
			pool = getPool(poolOID);
		}
		return pool;
	}

	/**
	 * @param poolOID
	 * @return
	 */
	public Pool getPool(int poolOID) {
		return pools.get(poolOID);
	}

	/**
	 * @param poolOID
	 * @param name
	 * @param groupOID
	 * @param scoreSystemOID
	 * @param showBracketsEarly
	 * @param bracketLimit
	 * @param tieBreakerTypeID
	 * @param tieBreakerQuestion
	 * @param tieBreakerAnswer
	 */
	public void loadPool(int poolOID, String name, int groupOID, int scoreSystemOID,
			int tournamentOID, int bracketLimit, boolean showBracketsEarly,
			int tieBreakerTypeID, String tieBreakerQuestion, String tieBreakerAnswer)
	{
		Group group = getGroupObject(groupOID);
		ScoreSystem scoreSystem = sp.getSingleton().getScoreSystemManager().getScoreSystem(scoreSystemOID);
		Tournament tournament = sp.getSingleton().getTournamentManager().getTournament(tournamentOID);
		TieBreakerType tieBreakerType = getTieBreakerType(tieBreakerTypeID);
		Pool pool = new MainPool(sp, poolOID, name, group, scoreSystem, tournament,
				bracketLimit, showBracketsEarly, tieBreakerType, tieBreakerQuestion, tieBreakerAnswer);
		pools.put(poolOID, pool);
	}

	/**
	 * @param group
	 */
	public void loadGroupMembers(Group group) {
		new GroupPlayersGetBroker(sp, group.getId()).execute();
	}

	/**
	 * @param groupOID
	 * @param playerOID
	 */
	public void associateUserToGroup(int groupOID, int playerOID) {
		getGroup(groupOID).addMember(playerOID);
	}

	/**
	 * @param playerOIDs
	 * @return
	 */
	public Set<User> getPlayers(Set<Integer> playerOIDs) {
		Set<User> these = new LinkedHashSet<>();
		for (Integer oid: playerOIDs) {
			User user = players.get(oid);
			if(user == null)  {
				new PlayerGetByOIDBroker(sp, oid).execute();
				user = players.get(oid);
				if(user != null) fillUser(user);
			}
			if(user != null) these.add(user);
		}
		return these;
	}

	/**
	 * @param group
	 */
	public void loadPools(Group group) {
		new GroupPoolsGetBroker(sp, group.getId()).execute();
	}

	/**
	 * @param poolOids
	 * @return
	 */
	public Set<Pool> getPools(Set<Integer> poolOids) {
        return poolOids.stream()
                .map(this::getPoolObject)
                .collect(Collectors.toSet());
	}

	/**
	 * @param group
	 * @param tournament
	 * @param scoreSystem
	 * @param showBracketsEarly
	 * @param bracketLimit
	 * @param tieBreakerTypeID
	 * @param tieBreakerQuestion
	 */
	public void createPool(String name, Group group, Tournament tournament, ScoreSystem scoreSystem,
			int bracketLimit, boolean showBracketsEarly, int tieBreakerTypeID, String tieBreakerQuestion)
	{
		group.getPools(); // force the loading of the group's pool objects
		TieBreakerType tieBreakerType = getTieBreakerType(tieBreakerTypeID);
		new PoolInsertBroker(sp, name, group, tournament, scoreSystem,
				bracketLimit, showBracketsEarly, tieBreakerType, tieBreakerQuestion).execute();
	}

	/**
	 * @param pool
	 * @param bracket
	 * @param tieBreakerAnswer
	 */
	public void assignBracket(Pool pool, Bracket bracket, String tieBreakerAnswer) {
		// first validate that this bracket is valid for this pool.
		if(bracket.getTournament() != pool.getTournament()){
			throw new IllegalArgumentException(
					"Pool ["+pool.getOid()+"]"+pool.getName()+
					" does not have the same tournament as Bracket ["+
					bracket.getOID()+"]"+bracket.getName());
		}
		// and that the owner of the bracket is in the pool's group
		Group group = pool.getGroup();
		User owner = bracket.getOwner();
		if(!group.getMembers().contains(owner)) {
			throw new IllegalArgumentException(
					"User ["+owner.getOID()+"]"+owner.getName()+
					" is not a part of Group ["+
					group.getId()+"]"+group.getName());
		}
		// and that the bracket is completed.
		if(!bracket.isComplete(sp)) {
			throw new IllegalArgumentException(
					"Bracket ["+bracket.getOID()+"]"+bracket.getName()+
					" is not completely filled out");
		}
		// and that the player has not exceeded the bracket limit
		if(pool.hasReachedLimit(owner)) {
			throw new IllegalStateException(
				"User ["+owner.getOID()+"]"+owner.getName()+
				" already has the maximum number of brackets allowed assigned to this pool.");
		}

		// and that it isn't too late to join the pool
		if(pool.getTournament().isStarted()) {
			throw new IllegalStateException(pool.getTournament()+" has already begun.");
		}

		pool.getTieBreakerType().validate(tieBreakerAnswer);

		// insert into db then update domain
		new BracketPoolInsertBroker(sp, pool, bracket, tieBreakerAnswer).execute();

		// TODO: set the bracket read-only?
	}

	/**
	 * @return completed brackets for the pool's tournament not already in the pool.
	 */
	public Menu getBracketsAvailableForPoolMenu(final User user, final Pool pool) {
		return new ReferenceMenu<Bracket>("tournaments") {
			protected Map<Integer, Bracket> getReferences() {
                if(checkPoolLimit(pool, user)) return Collections.emptyMap();

                // add all user brackets
                return user.getBrackets().stream()
                        // ignore any already assigned to this pool
                        .filter((bracket) -> !pool.hasBracket(bracket))

                        .filter((bracket) -> bracket.getTournament() == pool.getTournament())

                        // only allow those completed for this tournament.  Do last because it is expensive
                        .parallel().filter((bracket) -> bracket.isComplete(sp))

                        .collect(Collectors.toMap(Bracket::getID, Function.identity()));
			}
		};
	}

    private boolean checkPoolLimit(Pool pool, User user) {
        try {
            return pool.hasReachedLimit(user);
        } catch (DatabaseFailure e) {
            throw new RuntimeException("Unable to load user "+user.getID(), e);
        }
    }

    void addPlayersTo(Group group, int[] playerIDs) {
		new GroupPlayerInsertBroker(sp, group, playerIDs).execute();
	}

	public int createGroup(User user, String name, boolean createInvitationCode, int parentID) {
		int invitationCode = 0;
		if(createInvitationCode) {
			invitationCode = Math.abs((int)System.currentTimeMillis());
		}
		// current user becomes the group administrator
		GroupInsertBroker groupInsertBroker = new GroupInsertBroker(sp, name, user, invitationCode, parentID);
		groupInsertBroker.execute(); // adds the group to memory, but without members
		int groupOID = groupInsertBroker.getGroupOID();
		// add the person who creates the group to the group
		new GroupPlayerInsertBroker(sp, getGroupObject(groupOID), new int[] {user.getOID()}).execute(); // update database
		associateGroupToUser(user.getOID(), groupOID); // update the user object's memory
		getGroup(groupOID).getMembers(); // update the group object's memory from the database
		return groupOID;
	}

	public Group getGroup(String invitationCode) {
        // first iterate through loaded groups looking for one that matches.
        Optional<Group> toReturn = groups.values().stream()
                .filter(g -> g.validateInvitationShortCode(invitationCode))
                .findFirst();

		// if not found, load group by invite code from db.
        if (toReturn.isPresent()) {
            return toReturn.get();
        }

        GroupGetByInvitationCodeBroker broker = new GroupGetByInvitationCodeBroker(sp, Group.convertInvitationCode(invitationCode));
        broker.execute();
        // get the group
        return getGroup(broker.getGroupOID());
    }

	public void addSiteAdmins(int[] playerIDs) {
		for (int playerID : playerIDs) {
			new SiteAdminUpdateBroker(sp, getUserObject(playerID), true).execute();
		}
	}

	public void removeSiteAdmins(User current, int[] playerIDs) {
		for (int playerID : playerIDs) {
			if(current.getOID() != playerID) { // you may not remove yourself
				new SiteAdminUpdateBroker(sp, getUserObject(playerID), false).execute();
			}
		}
	}
	
	public List<String[]> getAvailableSiteAdmins() {
		return new SiteAvailableAdminsGetBroker(sp).getPlayers();
	}

	public List<String[]> getSiteAdmins() {
		return new SiteAdminsGetBroker(sp).getPlayers();
	}

	public int getNumGroupsLoaded() {
		return groups.size();
	}

	public int getNumPlayersLoaded()  {
		return players.size();
	}

	public int getNumPoolsLoaded() {
		return pools.size();
	}

	public void resetPassword(String userID) throws UnsupportedEncodingException, MessagingException {
		new JavaMailEmailBroker(sp).sendPasswordChange(getUserObject(userID));
	}

	public String resetPassword(String userID, String auth, String newPassword) {
		User user = getUser(userID, auth);
		PasswordResetBroker emailResetBroker = new PasswordResetBroker(sp, user.getOID(), newPassword);
		emailResetBroker.execute();
		getUserObject(userID).setPassword(newPassword);  // FIXME: 1921848 
		return user.getAuth();
	}

	public void loadTieBreakerType(int tieBreakerTypeOID, String name) {
		TieBreakerType type = null;
		switch(tieBreakerTypeOID) {
			case 0:
				type = new TieBreakerType.TieBreakerTypeNone(tieBreakerTypeOID, name);
				break;
			case 1:
				type = new TieBreakerType.TieBreakerTypeClosestNumber(tieBreakerTypeOID, name);
				break;
			case 2:
				type = new TieBreakerType.TieBreakerTypeUpsetPrediction(tieBreakerTypeOID, name);
				break;
			case 3:
				type = new TieBreakerType.TieBreakerTypeUpsetPredictionClosestNumber(tieBreakerTypeOID, name);
				break;
		}
		tieBreakerTypes.put(type.getID(), type);
	}

	public TieBreakerType getTieBreakerType(int id) {
		return tieBreakerTypes.get(id);
	}

	public Menu getTieBreakerTypeMenu() {
		return new ReferenceMenu<TieBreakerType>("tieBreakerTypes") {
			protected Map<Integer, TieBreakerType> getReferences() {
				return tieBreakerTypes;
			}
		};
	}

	public void closePool(Pool pool, String tieBreakerAnswer) {
		pool.getTieBreakerType().validate(tieBreakerAnswer);
		TieBreakerAnswerBroker emailResetBroker = new TieBreakerAnswerBroker(sp, pool, tieBreakerAnswer);
		emailResetBroker.execute();
	}

	public Iterator<Group> getGroups() {
		return new ArrayList<>(groups.values()).iterator();
	}

	public Iterator<User> getPlayers() {
		return new ArrayList<>(players.values()).iterator();
	}

	public Iterator<Pool> getPools() {
		return new ArrayList<>(pools.values()).iterator();
	}

	public void removePool(Pool pool) {
		pools.remove(pool.getOid());
	}

	public void removeGroup(Group group) {
		groups.remove(group.getId());
	}
	
	public boolean removeUser(User requestor, User toRemove) {
		if(mayRemoveUser(requestor, toRemove)) {
			// first, remove this player from any groups that he is in.
			// site administrators are the only ones who may remove players, and 
			// site administrators can remove players from any group, so removing 
			// should succeed from all groups
			Iterator<Group> groups2 = toRemove.getGroups().iterator();
			while (groups2.hasNext()) {
				Group group = groups2.next();
				// FIXME: if the person removed is administrator of a group they aren't in, 
				// then this deletion will fail with a corrupted db.
				if(group.getAdministrator() == toRemove) {
					if(!group.delete(requestor, sp)) return false;
				}
				if(!group.removeMember(requestor, toRemove, sp)) return false;
				groups2 = toRemove.getGroups().iterator(); // avoid the concurrent modification exception
			}
			
			// then delete the player
			new PlayerDeleteBroker(sp, this, toRemove).execute();
			return true;
		}
		return false;
	}

	public boolean mayRemoveUser(User requester, User toRemove) {
		if(requester != null && requester.isSiteAdmin()) {
			// a player with saved brackets shouldn't be deleted.  An administrator 
			// should go through the process of removing brackets from pools and 
			// deleting or moving the brackets before deleting the player.
			if(!toRemove.hasBrackets()) {
                return toRemove.getGroups().stream()
                        .noneMatch(g -> g.getAdministrator() == toRemove && !g.mayDelete(requester));
			}
		}
		return false;
	}

	public void applyDelete(User player) {
		players.remove(player.getOID());
		users.remove(player.getID());
	}

    public void updateProfile(User user, String userID, String name, String email) {
        String oldID = user.getID();
        ProfileUpdateBroker broker = new ProfileUpdateBroker(sp, user.getOID(), userID, name, email);
        broker.execute();
        users.put(userID, users.remove(oldID));
    }
}
