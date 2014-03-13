/* 
Copyright (C) 2003-20013 Avery J. Regier.

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

package com.tournamentpool.controller.autoupdate;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.controller.TournamentController;
import com.tournamentpool.domain.League;
import com.tournamentpool.domain.MainTournament;
import com.tournamentpool.domain.Tournament;
import utility.IterableIterator;
import utility.IteratorUtility;
import utility.StringUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.net.*;
import java.sql.SQLException;
import java.util.*;

import static utility.StringUtil.killWhitespace;


public class AutoUpdateController extends TournamentController {

	private List<Timer> timers= new ArrayList<Timer>();

	public AutoUpdateController(SingletonProvider sp) {
		super(sp);
	}

	public void init() {
		// start some threads that wait until the appropriate time and do updates	
		String autoUpdateLeaguesString = killWhitespace(getConfig().getProperty("autoUpdate.leagues"));
		if(autoUpdateLeaguesString != null) {
			String[] leagueIDStrings = autoUpdateLeaguesString.split(",");
			for (String string : leagueIDStrings) {
				int id = Integer.parseInt(string);
				League sourceLeague = sp.getSingleton().getTeamManager().getLeague(id);
				startWaitForNewTournamentsTimer(sourceLeague, false);
			}
		} else {
			System.out.println("No leagues to auto update.");
		}
	}

	private void startWaitForNewTournamentsTimer(final League sourceLeague, boolean forcePause) {
		final long period = getLongProperty("autoUpdate.tournamentDetectTimeInMins", 60)*1000*60;// 1000 * 60 * 60;
		Timer timer = new Timer("Update "+sourceLeague.getName(), true);
		timers.add(timer);
		Date firstTime = new Date();
		if(forcePause) {
			firstTime = new Date(firstTime.getTime()+period);
			System.out.println("Next try at "+firstTime);
		}
		timer.schedule(new TimerTask() {
			private Date last;
			@Override
			public void run() {
				Date start = findAppropriateStartDate(sourceLeague);
				if(start != null && (last == null || !last.equals(start))) {
					last = start;
					startAutoUpdateTimer(sourceLeague, start);
					this.cancel(); // we no longer need this timer
					System.out.println(sourceLeague.getID()+" wait for tournament timer cancelled.");
				} else {
					System.out.println("No tournaments in progress for league "+sourceLeague.getLeagueID()+" "+sourceLeague.getName());
					System.out.println("Waiting for "+period+" ms "+sourceLeague.getLeagueID()+" "+sourceLeague.getName());
				}
			}
		}, firstTime, period);
	}

	private void startAutoUpdateTimer(final League sourceLeague, Date start) {
		final long period = getLongProperty("autoUpdate.refreshDelayInSeconds", 30)*1000;// 1000 * 60 * 5;
		System.out.println(sourceLeague.getID()+" auto update timer set to start at "+start+" or in "+(start.getTime() - new Date().getTime())+" ms");
		Timer timer = new Timer("Update "+sourceLeague.getName(), true);
		timers.add(timer);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				boolean tournamentsCompletedOrPaused = true;
				try {
					tournamentsCompletedOrPaused = doUpdate(sourceLeague);
				} catch (Exception e) {
					e.printStackTrace();
					this.cancel();
					return;
				}
				if(tournamentsCompletedOrPaused) {
					this.cancel();
					System.out.println(sourceLeague.getID()+" auto update timer cancelled.");
					startWaitForNewTournamentsTimer(sourceLeague, true);
				} else {
					System.out.println(sourceLeague.getID()+" auto update timer waiting for "+period);
				}
			}
		}, start, period);
	}

	private boolean doUpdate(final League sourceLeague)
			throws URISyntaxException, IOException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, MalformedURLException,
			UnsupportedEncodingException, SQLException 
	{
		// TODO: make it possible to have multiple sources
		
		String contents = loadURIContents(sourceLeague);
		boolean tournamentsCompletedOrPaused= true;
		if(contents != null) {
			ScoreSource source = loadParser(sourceLeague);
			final Map<String, LiveGame> teamGameMap = mapTeamsToGames(source.getGames(contents));
			if(!teamGameMap.isEmpty()) {
				tournamentsCompletedOrPaused = applyToTournaments(sourceLeague, teamGameMap);
			} else {
				System.out.println("No games available.");
			}
		} else {
			System.out.println("No auto update content for league "+sourceLeague.getLeagueID()+" "+sourceLeague.getName());
			tournamentsCompletedOrPaused = false;
		}
		return tournamentsCompletedOrPaused;
	}

	private String getProperty(String name) {
		return StringUtil.killWhitespace(
				getConfig()
				.getProperty(name));
	}

    private Properties getConfig() {
        return sp
            .getSingleton()
            .getConfig();
    }

    private long getLongProperty(String name, long defaultValue) {
		String value = StringUtil.killWhitespace(getConfig().getProperty(name));
		if(value == null) return defaultValue;
		return Long.parseLong(value);
	}
	
	private boolean applyToTournaments(final League sourceLeague,
			final Map<String, LiveGame> teamGameMap) throws SQLException {
		boolean tournamentsPaused = true;
		for (Tournament tournament : getTournaments()) {
			if (isOngoing(sourceLeague, tournament)) {
				System.out.println("Applying updates to "+tournament.getOid()+" "+tournament.getName());
				final MainTournament mainTournament = (MainTournament)tournament;
				mainTournament.updateGames(sp, new AutoUpdateWinnerSource(mainTournament, teamGameMap));
				tournamentsPaused = !tournamentsPaused ? false : !mainTournament.hasGamesInProgress();
			}
		}
		return tournamentsPaused;
	}

	private boolean isOngoing(final League sourceLeague, Tournament tournament) {
		boolean ongoing = tournament instanceof MainTournament && 
			tournament.getLeague() == sourceLeague && 
			tournament.isStarted() && 
			!tournament.isComplete() && 
			tournament.hasAllSeedsAssigned();
		System.out.println("Tournament "+tournament.getName()+(ongoing ? " is ongoing ": " is not ongoing ")+
                "for League: "+tournament.getLeague().getName());
        System.out.println("Tournament "+tournament.getName()+(tournament.isStarted() ? " is started ": " is not started ")+
                "at time: "+new Date());

        return ongoing;
	}

	private Date findAppropriateStartDate(final League sourceLeague) {
		Date start = null;
		for (Tournament tournament : getTournaments()) {
			if (isOngoing(sourceLeague, tournament)) 
			{
				// find the earliest game date which does not have a winner
				// if there are no open games with start dates, allow the timer to proceed
				Date tstart = tournament.getNextStartTime();
				start = start == null ? tstart : tstart.before(start) ? tstart : start;
			} else {
				System.out.println(new Date()+": "+tournament.getName()+" starts at "+tournament.getStartTime());
			}
		}
		System.out.println(sourceLeague.getID()+" appropriate start date is "+start);
		return start;
	}

	private Map<String, LiveGame> mapTeamsToGames(List<LiveGame> games) {
		final Map<String, LiveGame> teamGameMap = new TreeMap<String, LiveGame>();
		if(games != null) for (LiveGame game : games) {
			//if(game.isFinal()) {
				for (String team : game.getPlayerScores().keySet()) {
					teamGameMap.put(team.toUpperCase(), game);
				}
			//}
		}
		return teamGameMap;
	}

	private ScoreSource loadParser(League sourceLeague) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException, MalformedURLException {
        String prefix = "scoreSource." + sourceLeague.getLeagueID()+".";
        URL[] urls = new URL[] {new File(getProperty(prefix + "classLocation")).toURI().toURL()};
		URLClassLoader loader = new URLClassLoader(urls, getClass().getClassLoader());
		String className = getProperty(prefix+"class");
		Class<?> clazz = Class.forName(className, true, loader);
        try {
            Constructor<?> constructor = clazz.getConstructor(Properties.class);
            if(constructor != null) {
                return (ScoreSource) constructor.newInstance(getSubSet(prefix));
            }
        } catch(ReflectiveOperationException e) {
            e.printStackTrace();
            // fall through
        }
        return (ScoreSource) clazz.newInstance();
    }

    private Properties getSubSet(String prefix) {
        Properties config = getConfig();
        Properties subConfig = new Properties();
        for(String prop: config.stringPropertyNames()) {
            if(prop.startsWith(prefix)){
                subConfig.put(prop.substring(prefix.length()), config.getProperty(prop));
            }
        }
        return subConfig;
    }

	private String loadURIContents(League sourceLeague) throws URISyntaxException, IOException {
		URI uri = new URI(getProperty("scoreSource."+sourceLeague.getLeagueID()+".uri"));
		InputStream in = null;
		String contents = "";
		try {
			in = (InputStream)uri.toURL().getContent();
			contents = convertStreamToString(in);
		} catch (Exception e) {
			if(in != null) in.close();
		}
		return contents;
	}

	private IterableIterator<Tournament> getTournaments() {
		return IteratorUtility.adapt(
				sp
				.getSingleton()
				.getTournamentManager()
				.getTournaments());
	}

	private static String convertStreamToString(java.io.InputStream is) {
	    @SuppressWarnings("resource")
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}

	public void cancel() {
		// TODO Auto-generated method stub
		System.out.println("Need to cancel threads");
		for (Timer timer : timers) {
			timer.cancel();
			System.out.println(timer+" cancelled");
		}
	}

}
