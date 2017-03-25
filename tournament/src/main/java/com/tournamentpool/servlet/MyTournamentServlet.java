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
package com.tournamentpool.servlet;

import com.tournamentpool.beans.GroupBean;
import com.tournamentpool.beans.PlayerBean;
import com.tournamentpool.beans.PoolBean;
import com.tournamentpool.beans.TournamentBean;
import com.tournamentpool.broker.sql.DatabaseFailure;
import com.tournamentpool.controller.*;
import com.tournamentpool.domain.*;
import com.tournamentpool.domain.Tournament;
import utility.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author avery
 */
public class MyTournamentServlet extends RequiresLoginServlet {

    /**
     *
     */
    private static final long serialVersionUID = -2223531495521423099L;

    /**
     *
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            User user = getUser(req, resp);
            String request = req.getParameter("request");
            String type = req.getParameter("type");
            if ("create".equalsIgnoreCase(request)) {
                if ("group".equalsIgnoreCase(type)) {
                    produceJSPPage(req, resp, "CreateGroupJSP");
                } else if ("bracket".equalsIgnoreCase(type)) {
                    resp.sendRedirect(getApp().getConfig().getProperty("BracketMaintenanceURL") + "?request=create&tournament=" + req.getParameter("tournament"));
                } else if ("pool".equalsIgnoreCase(type)) {
                    resp.sendRedirect(getApp().getConfig().getProperty("PoolMaintenanceURL") + "?group=" + req.getParameter("group"));
                }
//			} else if ("list".equalsIgnoreCase(request)) {
//				if ("group".equalsIgnoreCase(type)) {
//					produceJSPPage(req, resp, "ListGroupsJSP");
//				} else if ("bracket".equalsIgnoreCase(type)) {
//					produceJSPPage(req, resp, "ListBracketsJSP");
//				}
            } else if ("group".equalsIgnoreCase(type)) {
                if ("delete".equalsIgnoreCase(request)) {
                    deleteGroup(req, resp, user);
                } else {
                    showGroup(req, resp, user);
                }
            } else if ("pool".equalsIgnoreCase(type)) {
                showPool(req, resp, user);
            } else if (handleInvitation(req, user)) {
                resp.sendRedirect(getApp().getConfig().getProperty("MyTournamentURL"));
            } else if (req.getSession().getAttributeNames().hasMoreElements() &&
                    !req.getParameterNames().hasMoreElements()) {
                // if there are no parameters but the session does have the state of the last screen,
                // then create a URL based on the session and redirect.
                handleTournamentFilter(req, resp, user);
            } else /* if("player".equalsIgnoreCase(type)) */ {
                showMyTournamentPage(req, resp, user);
            }
        } catch (DatabaseFailure e) {
            throw new ServletException(e);
        }
    }

    private void handleTournamentFilter(HttpServletRequest req, HttpServletResponse resp, User user) throws ServletException, IOException {
        String query = "?";
        query = buildFilterQuery(req, query);

        if (query.length() == 1) { // this should never be true, but just in case...
            showMyTournamentPage(req, resp, user);
        } else {
            resp.sendRedirect(getApp().getConfig().getProperty("MyTournamentURL") + query);
            req.getSession().removeAttribute("archives");
            req.getSession().removeAttribute("tournament");
        }
    }

    private String buildFilterQuery(HttpServletRequest req, String query) {
        if (req.getSession().getAttribute("archives") != null) {
            query += "archives=true&";
        }

        if (req.getSession().getAttribute("tournament") != null) {
            query += "tournament=" + req.getSession().getAttribute("tournament");
        }
        return query;
    }

    @SuppressWarnings("rawtypes")
    private void showGroup(HttpServletRequest req, HttpServletResponse resp, User user) throws ServletException, IOException {
        Set keySet = req.getParameterMap().keySet();
        if (req.getSession().getAttributeNames().hasMoreElements() &&
                !(keySet.contains("archives") || keySet.contains("tournament") || keySet.contains("current"))) {
            String query = "";
            query = buildFilterQuery(req, query);

            if (query.length() != 0) {
                resp.sendRedirect(getApp().getConfig().getProperty("MyTournamentURL") + "?type=group&id=" + req.getParameter("id") + "&" + query);
                req.getSession().removeAttribute("archives");
                req.getSession().removeAttribute("tournament");
                return;
            }
        }

        addTournamentBeans(req, user);
        Filter filter = getFilter(req, user);

        Group group = lookupGroup(req);
        GroupBean bean = mapGroupBean(user, filter, group);
        req.setAttribute("Group", bean);

        produceJSPPage(req, resp, "ShowGroupJSP");
    }

    private GroupBean mapGroupBean(User user, Filter filter, Group group) {
        GroupBean bean = new GroupBean(group);
        bean.setMembers(group.getMyMembers(), user, group);
        bean.setSubGroups(group.getChildren());
//		for(Group subGroup: group.getChildren()) {
//			bean.addMembers(group.getMyMembers(), user, subGroup);
//		}
        User administrator = group.getAdministrator();
        if (administrator != null) {
            bean.setAdmin(new PlayerBean(administrator.getOID(), administrator.getName()), user == administrator);
        }
        bean.setMayAddSubGroup(group.mayAddSubGroup(user));
        bean.setPools(group.getPools(), filter, user);
        return bean;
    }

    private Group lookupGroup(HttpServletRequest req) {
        int id = Integer.parseInt(req.getParameter("id"));
        Group group = getApp().getUserManager().getGroup(id);
        if (group == null) {
            throw new IllegalArgumentException("Group " + id + " does not exist.");
        }
        return group;
    }

    private void deleteGroup(HttpServletRequest req, HttpServletResponse resp, User user) throws IOException {
        Group group = lookupGroup(req);
        group.delete(user, getApp().getSingletonProvider());
        resp.sendRedirect(req.getHeader("Referer"));
    }

    private void showPool(HttpServletRequest req, HttpServletResponse resp, User user) throws ServletException, IOException {
        Pool pool = lookupPool(req);
        PoolBean poolBean = mapPoolBean(user, pool);
        req.setAttribute("Pool", poolBean);
        if ("csv".equals(req.getParameter("output"))) {
            resp.setContentType("text/csv");
            produceJSPPage(req, resp, "PoolCSVJSP");
        } else {
            produceJSPPage(req, resp, "ShowPoolJSP");
        }
    }

    private PoolBean mapPoolBean(User user, Pool pool) {
        PoolBean poolBean = new PoolBean(pool.getOid(), pool.getName());
        poolBean.setShowGroups(pool.getGroup().hasChildren());
        poolBean.setPoolBrackets(pool, pool.getRankedBrackets(), user);
        poolBean.setOwner(pool.getOwner());
        poolBean.setScoreSystem(pool.getScoreSystem());
        poolBean.setTieBreaker(pool.getTieBreakerType(), pool.getTieBreakerQuestion(), pool.getTieBreakerAnswer());
        poolBean.setTieBreakerNeeded(pool.isTiebreakerNeeded(user));
        com.tournamentpool.domain.Tournament poolTournament = pool.getTournament();
        if (poolTournament != null) {
            poolBean.setTournament(new TournamentBean(poolTournament, poolTournament.mayEdit(user),
                    poolTournament.hasAllSeedsAssigned(),
                    poolTournament.getStartTime(), poolTournament.getLastUpdated(), false));
            poolBean.setClosed(poolTournament.isStarted());
        }
        if (pool.getGroup() != null) {
            poolBean.setGroup(new GroupBean(pool.getGroup()));
            poolBean.setIsChild(pool.getGroup() != pool.getDefiningGroup());
        }
        poolBean.setEditable(pool.getOwner() == user && !pool.hasAnyBrackets());
        poolBean.setBracketLimit(pool.getBracketLimit());
        poolBean.setShowBracketsEarly(pool.isShowBracketsEarly() || pool.getTournament().isStarted());

        for (Bracket bracket : pool.getBrackets()) {
            poolBean.addEmail(bracket.getOwner().getEmail());
        }
        return poolBean;
    }

    private Pool lookupPool(HttpServletRequest req) {
        int id = Integer.parseInt(req.getParameter("id"));
        Pool pool = null;
        String groupParam = req.getParameter("groupID");
        if (groupParam != null) {
            int groupID = Integer.parseInt(groupParam);
            Group group = getApp().getUserManager().getGroup(groupID);
            if (group != null) {
                pool = group.getPool(id);
            }
        }
        if (pool == null) {
            pool = getApp().getUserManager().getPoolObject(id);
        }
        if (pool == null) {
            throw new IllegalArgumentException("Pool " + id + " does not exist.");
        }
        return pool;
    }

    private void showMyTournamentPage(HttpServletRequest req, HttpServletResponse resp, User user) throws ServletException, IOException {
        addTournamentBeans(req, user);
        Filter filter = getFilter(req, user);

        PlayerBean bean = new PlayerBean(user.getOID(), user.getName(), user.isSiteAdmin());
        bean.setGroups(user.getGroupsInHierarchy(), filter, user);
        bean.setBrackets(user.getBrackets(), filter, user);

        if (filter.isCurrent()) {
            bean.setGames(getInProgressGamesWithPersonalAnalyses(user, filter));
        }

        req.setAttribute("Player", bean);
        produceJSPPage(req, resp, "MyTournamentJSP");
    }

    private Map<Game, Map<Seed, Set<Bracket.Pick>>> getInProgressGamesWithPersonalAnalyses(User user, Filter filter) {
        TreeMap<Game, Map<Seed, Set<Bracket.Pick>>> map = new TreeMap<>(Comparator.comparing(Game::getDate));
        getRecentGames(filter).forEachOrdered(g->map.put(g, getPicks(user, g, filter)));
        return map;
    }

    private static <T> BinaryOperator<T> throwingMerger() {
        return (u, v) -> {
            throw new IllegalStateException(String.format("Duplicate key %s", u));
        };
    }

    public static <T, K, U, M extends Map<K, U>> Collector<T, ?, M> toMap(Function<? super T, ? extends K> keyMapper,
                                                                          Function<? super T, ? extends U> valueMapper,
                                                                          Supplier<M> mapSupplier) {
        return Collectors.toMap(keyMapper, valueMapper, throwingMerger(), mapSupplier);
    }

    private BinaryOperator<Game> pickFirstMerger() {
        return (a,b)->a;
    }

    private Stream<Game> getRecentGames(Filter filter) {
        return getApp().getTournamentManager().getInProgressTournaments().stream()
                .filter(filter::pass)
                .flatMap(Tournament::recentGames)
                .map(Game::getIdentity)
                .flatMap(this::asStream)
                .distinct()
                .sorted(Comparator.comparing(Game::getDate));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private <T> Stream<T> asStream(Optional<T> opt) {
        if (opt.isPresent()) {
            return Stream.of(opt.get());
        }
        System.out.println("Optional is empty");
        return Stream.empty();
    }

    private Map<Seed, Set<Bracket.Pick>> getPicks(User user, Game g, Filter filter) {
        return streamPicksForGame(user, g, filter)
                .collect(Collectors.groupingBy(p->p.getWinner().map(g::getSeed).get(),
                        Collectors.mapping(p->p, Collectors.toSet())));
    }

    private Stream<Bracket.Pick> streamPicksForGame(User user, Game g, Filter filter) {
        return user.getBrackets().stream()
                .filter(filter::pass)
                .peek(b->System.out.println(b.getID()+" is under consideration"))
                .filter(b->b.getTournament().getIdentity() == g.getTournament().getIdentity())
                .filter(Bracket::isInPool)
                .peek(b->System.out.println(b.getID()+" is in a pool"))
                .map(b -> getGameNodeForBracket(b, g)
                        .flatMap((game) -> b.getPick(getApp().getSingletonProvider(), game)))
                .flatMap(this::asStream)
                .filter(p->p.getWinner().isPresent())
                .filter(p->g.isPlaying(p.getSeed()))
                .peek(p->System.out.println("found a pick"));
    }

    private Optional<GameNode> getGameNodeForBracket(Bracket b, Game g) {
        if (b.getTournament() == g.getTournament()) {
            System.out.println("bracket and game in same tournament");
            return Optional.of(g.getGameNode());
        }
        if (b.getTournament().getIdentity() == g.getTournament()) {
            System.out.println("bracket in a sub tournament");
            return b.getTournament()
                    .getTournamentType()
                    .getGameNode(
                            g.getGameNode().getOid());
        }
        System.out.println("bracket is not part of the same tournament");
        return Optional.empty();
    }

    private void addTournamentBeans(HttpServletRequest req, User user) {
        List<TournamentBean> tournamentBeans = new ArrayList<>(getApp().getTournamentManager().getNumTournaments());
        for (com.tournamentpool.domain.Tournament tournament : getApp().getTournamentManager().getTournaments()) {
            tournamentBeans.add(new TournamentBean(tournament, tournament.mayEdit(user),
                    tournament.hasAllSeedsAssigned(),
                    tournament.getStartTime(), tournament.getLastUpdated(), tournament.mayDelete(user, getApp().getSingletonProvider())));
        }
        Collections.sort(tournamentBeans);
        req.setAttribute("Tournaments", tournamentBeans);
    }

    private Filter getFilter(HttpServletRequest req, User user) {
        Filter filter = null;
        if (StringUtil.killWhitespace(req.getParameter("tournament")) != null) {
            com.tournamentpool.domain.Tournament tournament = lookupTournament(req);
            filter = new TournamentFilter(tournament);
            if (!tournament.isStarted()) {
                // the admin filter is only relevant to create a new pool if the
                // tournament hasn't already started
                filter = new AdministratorFilter(filter, user);
            }
            if (!ArchiveFilter.isArchive(tournament))
                req.getSession().removeAttribute("archives");
            else
                req.getSession().setAttribute("archives", "true");
            req.getSession().setAttribute("tournament", Integer.toString(tournament.getOid()));
        } else if (req.getParameter("archives") != null) {
            filter = new ArchiveFilter();
            req.getSession().setAttribute("archives", "true");
            req.getSession().removeAttribute("tournament");
        } else {
            filter = new CurrentFilter();
            if (getApp().getTournamentManager().hasOpenTournaments()) {
                // we only need to be able to create new pools if there are open
                // tournaments at this time.  Otherwise its just noise in the UI.
                filter = new AdministratorFilter(filter, user);
            }
            req.getSession().removeAttribute("archives");
            req.getSession().removeAttribute("tournament");
        }
        return filter;
    }

    private boolean handleInvitation(HttpServletRequest req, User user) {
        String invitationCode = req.getParameter("invitationCode");
        if (invitationCode != null) {
            Group group = getApp().getUserManager().getGroup(invitationCode);
            if (group == null) {
                return false;
            }
            if (!group.hasMember(user.getOID())) {
                group.addMembers(new int[]{user.getOID()});
            }
            // bug workaround: Just in case the group has the member, but the member isn't referencing the group,
            // make sure the user gets in the group
            user.addGroup(group);
            return true;
        }
        return false;
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int groupID = 0;
        if ("group".equals(req.getParameter("type"))) {
            try {
                int parentID = findParentID(req);
                groupID = getApp().getUserManager().createGroup(
                        getUser(req, resp),
                        req.getParameter("name"),
                        "true".equalsIgnoreCase(req.getParameter("createInvitationCode")),
                        parentID);
            } catch (DatabaseFailure | IllegalAccessException e) {
                throw new ServletException(e);
            }
        } else if (req.getParameter("enableInvitation") != null) {
            Group group = getGroup(req, resp);
            try {
                getApp().getUserManager().updateGroup(group, true);
            } catch (DatabaseFailure e) {
                throw new ServletException(e);
            }
            groupID = group.getId();
        } else if (req.getParameter("disableInvitation") != null) {
            Group group = getGroup(req, resp);
            try {
                getApp().getUserManager().updateGroup(group, false);
            } catch (DatabaseFailure e) {
                throw new ServletException(e);
            }
            groupID = group.getId();
        }
        resp.sendRedirect(getApp().getConfig().getProperty("MyTournamentURL") + (groupID != 0 ? "?type=group&id=" + groupID : ""));
    }

    private int findParentID(HttpServletRequest req) {
        String parentIDParam = req.getParameter("parentID");
        int parentID;
        if (parentIDParam == null || parentIDParam.trim().length() == 0) {
            parentID = 0;
        } else {
            parentID = Integer.parseInt(parentIDParam);
        }
        return parentID;
    }

    private Group getGroup(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        int id = Integer.parseInt(req.getParameter("groupID"));
        Group group = getApp().getUserManager().getGroup(id);
        try {
            if (getUser(req, resp) != group.getAdministrator()) {
                throw new ServletException("You are not allowed to take this action");
            }
            return group;
        } catch (DatabaseFailure e) {
            throw new ServletException(e);
        }
    }
}