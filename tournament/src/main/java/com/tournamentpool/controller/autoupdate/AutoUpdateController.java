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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utility.StringUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.*;
import java.util.*;

import static utility.StringUtil.killWhitespace;


public class AutoUpdateController extends TournamentController {
	private final static Logger logger = LoggerFactory.getLogger(AutoUpdateController.class);

	private final List<Timer> timers= new ArrayList<>();

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
				if(sourceLeague != null) {
					startWaitForNewTournamentsTimer(sourceLeague, false);
				}
			}
		} else {
			logger.info("No leagues to auto update.");
		}
	}

	private void startWaitForNewTournamentsTimer(final League sourceLeague, boolean forcePause) {
		final long period = getLongProperty("autoUpdate.tournamentDetectTimeInMins", 60)*1000*60;// 1000 * 60 * 60;
		Timer timer = new Timer("Update "+sourceLeague.getName(), true);
		timers.add(timer);
		Date firstTime = new Date();
		if(forcePause) {
			firstTime = new Date(firstTime.getTime()+period);
			logger.info("Next try at "+firstTime);
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
					logger.info(sourceLeague.getID()+" wait for tournament timer cancelled.");
				} else {
					logger.info("No tournaments in progress for league "+sourceLeague.getLeagueID()+" "+sourceLeague.getName());
					logger.info("Waiting for "+period+" ms "+sourceLeague.getLeagueID()+" "+sourceLeague.getName());
				}
			}
		}, firstTime, period);
	}

	private void startAutoUpdateTimer(final League sourceLeague, Date start) {
		final long period = getLongProperty("autoUpdate.refreshDelayInSeconds", 30)*1000;// 1000 * 60 * 5;
		logger.info(sourceLeague.getID()+" auto update timer set to start at "+start+" or in "+(start.getTime() - new Date().getTime())+" ms");
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
					logger.info(sourceLeague.getID()+" auto update timer cancelled.");
					startWaitForNewTournamentsTimer(sourceLeague, true);
				} else {
					logger.info(sourceLeague.getID()+" auto update timer waiting for "+period);
				}
			}
		}, start, period);
	}

	private boolean doUpdate(final League sourceLeague)
			throws URISyntaxException, IOException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException
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
				logger.info("No games available.");
			}
		} else {
			logger.info("No auto update content for league "+sourceLeague.getLeagueID()+" "+sourceLeague.getName());
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
			final Map<String, LiveGame> teamGameMap) {
		boolean tournamentsPaused = true;
		for (Tournament tournament : getTournaments()) {
			if (isOngoing(sourceLeague, tournament)) {
				logger.info("Applying updates to "+tournament.getOid()+" "+tournament.getName());
				final MainTournament mainTournament = (MainTournament)tournament;
				mainTournament.updateGames(sp, new AutoUpdateWinnerSource(mainTournament, teamGameMap));
				tournamentsPaused = tournamentsPaused && !mainTournament.hasGamesToUpdate();
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
		logger.info("Tournament "+tournament.getName()+(ongoing ? " is ongoing ": " is not ongoing ")+
                "for League: "+sourceLeague.getLeagueID());
        logger.info("Tournament "+tournament.getName()+(tournament.isStarted() ? " is started ": " is not started ")+
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
				logger.info(new Date()+": "+tournament.getName()+" starts at "+tournament.getStartTime());
			}
		}
		logger.info(sourceLeague.getID()+" appropriate start date is "+start);
		return start;
	}

	private Map<String, LiveGame> mapTeamsToGames(List<LiveGame> games) {
		final Map<String, LiveGame> teamGameMap = new TreeMap<>();
		if(games != null) for (LiveGame game : games) {
			//if(game.isFinal()) {
				for (String team : game.getPlayerScores().keySet()) {
					teamGameMap.put(team.toUpperCase(), game);
					logger.info("Added "+team.toUpperCase()+ " for "+game.getGameID());
				}
			//}
		}
		return teamGameMap;
	}

	private ScoreSource loadParser(League sourceLeague) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException, MalformedURLException {
        String prefix = "scoreSource." + sourceLeague.getLeagueID()+".";
		Class<?> clazz = findClass(prefix);
		return (ScoreSource) createObject(prefix, clazz);
    }

	private <T> T createObject(String prefix, Class<T> clazz) throws InstantiationException, IllegalAccessException {
		try {
            Constructor<T> constructor = clazz.getConstructor(Properties.class);
            if(constructor != null) {
                return constructor.newInstance(getSubSet(prefix));
            }
        } catch(ReflectiveOperationException e) {
            e.printStackTrace();
            // fall through
        }
		return clazz.newInstance();
	}

	private Class<?> findClass(String prefix) throws MalformedURLException, ClassNotFoundException {
		String className = getProperty(prefix + "class");
		String classLocation = getProperty(prefix + "classLocation");Class<?> clazz;
		if(classLocation != null) {
			URL[] urls = new URL[]{new File(classLocation).toURI().toURL()};
			URLClassLoader loader = new URLClassLoader(urls, getClass().getClassLoader());
			return Class.forName(className, true, loader);
		} else {
			return Class.forName(className);
		}
	}

	private Properties getSubSet(String prefix) {
        Properties config = getConfig();
        Properties subConfig = new Properties();
        config.stringPropertyNames().stream()
                .filter(prop -> prop.startsWith(prefix))
                .forEach(prop -> {
                    subConfig.put(prop.substring(prefix.length()), config.getProperty(prop));
                });
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

	private Iterable<Tournament> getTournaments() {
		return sp
				.getSingleton()
				.getTournamentManager()
				.getTournaments();
	}

	private static String convertStreamToString(java.io.InputStream is) {
	    @SuppressWarnings("resource")
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}

	public void cancel() {
		// TODO Auto-generated method stub
		logger.info("Need to cancel threads");
		for (Timer timer : timers) {
			timer.cancel();
			logger.info(timer+" cancelled");
		}
	}

    public void update() {
		cancel();
		init();
    }
}
