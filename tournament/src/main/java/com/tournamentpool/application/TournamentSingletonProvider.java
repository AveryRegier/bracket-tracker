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
 * Created on Mar 9, 2004
 */
package com.tournamentpool.application;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author avery
 */
public class TournamentSingletonProvider implements SingletonProvider, ServletContextListener {
	private TournamentApp app;
	
	public TournamentApp getSingleton() {
		return app;
	}

	public synchronized void contextInitialized(ServletContextEvent sce) {
		app = new TournamentApp(this);
		sce.getServletContext().setAttribute("app", this);
	}

	public void contextDestroyed(ServletContextEvent sce) {
		app.getAutoUpdateController().cancel();
		app = null;
	}

	public void reset() {
		app.getAutoUpdateController().cancel();
		app = new TournamentApp(this);
	}
}
