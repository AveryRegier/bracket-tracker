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
 * Created on Feb 20, 2003
 */
package com.tournamentpool.application;

import com.tournamentpool.broker.sql.LoadBroker;
import com.tournamentpool.broker.sql.LoadManager;
import com.tournamentpool.broker.sql.SQLBroker;
import com.tournamentpool.broker.sql.load.*;
import com.tournamentpool.controller.ShowTournamentController;
import com.tournamentpool.controller.autoupdate.AutoUpdateController;
import com.tournamentpool.domain.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

/**
 * @author avery
 */
public class TournamentApp {
	private BracketManager bracketManager;
	private TournamentManager tournamentManager;
	private ScoreSystemManager scoreSystemManager;
	private TournamentTypeManager tournamentTypeManager;
	private TeamManager teamManager;
	private Properties config;
	private final SingletonProvider sp;
	private UserManager userManager;
	private Date lastUpdated = new Date(System.currentTimeMillis());
	private AutoUpdateController autoUpdateController;

	TournamentApp(SingletonProvider sp) {
		this.sp = sp;
		initialize();
	}

	/**
	 *
	 */
	private void initialize() {
		new Thread(() -> {
            try {
                synchronized(sp) {
                    SQLBroker.loadDriver(getConfig());
                }
                init();
            } catch(Throwable t) {
                t.printStackTrace();
            }
        }, "Initialization").start();
	}

	private void init() {
		LoadManager lm = new LoadManager();

		LoadBroker levelLoader = lm.addLoader(new LevelLoadBroker(sp));
		LoadBroker teamLoader = lm.addLoader(new TeamLoadBroker(sp));
		LoadBroker leagueLoader = lm.addLoader(new LeagueLoadBroker(sp));
		LoadBroker scoreSystemLoader = lm.addLoader(new ScoreSystemLoadBroker(sp));
		
		lm.addLoader(new TeamSynonymLoadBroker(sp)).addDependency(teamLoader);
		
		lm.addLoader(new LeagueTeamLoadBroker(sp))
                .addDependency(teamLoader)
                .addDependency(leagueLoader);

		lm.addLoader(new ScoreSystemDetailLoadBroker(sp))
                .addDependency(scoreSystemLoader)
                .addDependency(levelLoader);

		LoadBroker gameFeederTypeLoader = lm.addLoader(new GameFeederTypeLoadBroker(sp));

		lm.addLoader(new TieBreakerTypeLoadBroker(sp));

		LoadBroker gameNodeLoader = lm.addLoader(new GameNodeLoadBroker(sp))
                .addDependency(levelLoader)
                .addDependency(gameFeederTypeLoader);

		LoadBroker tournamentTypeLoader = lm.addLoader(new TournamentTypeLoadBroker(sp))
                .addDependency(gameNodeLoader);

		LoadBroker opponentLoader = lm.addLoader(new OpponentLoadBroker(sp))
                .addDependency(tournamentTypeLoader);

		LoadBroker seedLoader = lm.addLoader(new SeedLoadBroker(sp))
                .addDependency(tournamentTypeLoader);

		lm.addLoader(new GameSeedLoadBroker(sp))
                .addDependency(opponentLoader)
                .addDependency(seedLoader)
                .addDependency(gameNodeLoader);

        lm.addLoader(new GameFeederLoadBroker(sp))
                .addDependency(opponentLoader)
                .addDependency(seedLoader)
                .addDependency(gameNodeLoader);

		LoadBroker tournamentLoader = lm.addLoader(new TournamentLoadBroker(sp))
                .addDependency(tournamentTypeLoader)
                .addDependency(leagueLoader);

		lm.addLoader(new TournamentAdminLoadBroker(sp))
                .addDependency(tournamentLoader);

		lm.addLoader(new TournamentSeedLoadBroker(sp))
                .addDependency(tournamentLoader)
                .addDependency(teamLoader)
                .addDependency(seedLoader);

		LoadBroker gameLoader = lm.addLoader(new GameLoadBroker(sp))
                .addDependency(tournamentLoader)
                .addDependency(gameNodeLoader)
                .addDependency(opponentLoader);

		lm.addLoader(new GameScoreLoadBroker(sp))
                .addDependency(gameLoader);

		lm.load();
		
		getAutoUpdateController().init();
	}

	/**
	 * @return Object
	 */
	public SingletonProvider getSingletonProvider() {
		return sp;
	}

	/**
	 *
	 */
	public UserManager getUserManager() {
		if(userManager == null) {
			synchronized(this) {
				if(userManager == null) {
					userManager = new UserManager(getSingletonProvider());
				}
			}
		}
		return userManager;
	}

	/**
	 *
	 */
	public AutoUpdateController getAutoUpdateController() {
		if(autoUpdateController == null) {
			synchronized(this) {
				if(autoUpdateController == null) {
					autoUpdateController = new AutoUpdateController(getSingletonProvider());
				}
			}
		}
		return autoUpdateController;
	}

	/**
	 *
	 */
	public Properties getConfig() {
		if(config == null) {
			synchronized(this) {
				if(config == null) {
					config = new Properties();
					try {
						config.load(getClass().getClassLoader().getResourceAsStream("/config/tournament.properties"));
						InputStream propFile = getClass().getClassLoader().getResourceAsStream("/config/jdbc.properties");
						if(propFile != null) {
							config.load(propFile);
						} else {
							InputStream propFile2 = ClassLoader.getSystemResourceAsStream("jdbc.properties");
							if(propFile2 != null) {
								config.load(propFile2);
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return config;
	}

	/**
	 *
	 */
	public TeamManager getTeamManager() {
		if(teamManager == null) {
			synchronized(this) {
				if(teamManager == null) {
					teamManager = new TeamManager(getSingletonProvider());
				}
			}
		}
		return teamManager;
	}

	/**
	 *
	 */
	public TournamentTypeManager getTournamentTypeManager() {
		if(tournamentTypeManager == null) {
			synchronized(this) {
				if(tournamentTypeManager == null) {
					tournamentTypeManager = new TournamentTypeManager(getSingletonProvider());
				}
			}
		}
		return tournamentTypeManager;
	}

	/**
	 *
	 */
	public ScoreSystemManager getScoreSystemManager() {
		if(scoreSystemManager == null) {
			synchronized(this) {
				if(scoreSystemManager == null) {
					scoreSystemManager = new ScoreSystemManager(getSingletonProvider());
				}
			}
		}
		return scoreSystemManager;
	}

	/**
	 *
	 */
	public TournamentManager getTournamentManager() {
		if(tournamentManager == null) {
			synchronized(this) {
				if(tournamentManager == null) {
					tournamentManager = new TournamentManager(getSingletonProvider());
				}
			}
		}
		return tournamentManager;
	}

	/**
	 *
	 */
	public ShowTournamentController getTournamentController() {
		return new ShowTournamentController(getSingletonProvider());
	}

	/**
	 *
	 */
	public BracketManager getBracketManager() {
		if(bracketManager == null) {
			synchronized(this) {
				if(bracketManager == null) {
					bracketManager = new BracketManager(getSingletonProvider());
				}
			}
		}
		return bracketManager;
	}

	public void setLastUpdated(long lastUpdated) {
		if(this.lastUpdated.getTime() < lastUpdated) this.lastUpdated =  new Date(lastUpdated);
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}
}