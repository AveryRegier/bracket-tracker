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
 * Created on Mar 13, 2004
 */
package com.tournamentpool.broker.sql;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Avery J. Regier
 */
public class LoadManager {
	private Set<LoadBroker> loaders = new HashSet<LoadBroker>();
	private Set<LoadBroker> started = new HashSet<LoadBroker>();

	/**
	 * 
	 */
	public LoadManager() {
		super();
	}

	public LoadBroker addLoader(LoadBroker loader) {
		loaders.add(loader);
		loader.setManager(this);
		return loader;
	}
	
	private boolean error = false;
	public synchronized void load() {
		while(loaders.size() > 0 && !error) {
			for (Iterator<LoadBroker> iter = loaders.iterator(); iter.hasNext();) {
				LoadBroker broker = iter.next();
				if(broker.executeIfPossible()) {
					iter.remove();
					started.add(broker);
				} 
			}
			try {
				wait(1000);
			} catch (InterruptedException e) {}
		}
		while(started.size() > 0 && !error) {
			try {
				wait(1000);
			} catch (InterruptedException e) {}
		} 
		System.out.print("Load "+(error? "failed." : "successful."));
	}

	/**
	 * @param broker
	 */
	public synchronized void complete(LoadBroker broker) {
		started.remove(broker);
		error = error ? error : !broker.isSuccessful();
		notifyAll();
	}
}
