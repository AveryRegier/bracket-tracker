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

import com.tournamentpool.application.SingletonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Avery J. Regier
 */
public abstract class LoadBroker extends SQLBroker {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Set<LoadBroker> dependencies = new HashSet<>();
	private volatile boolean hasRun = false;
	private volatile boolean successful = false;
	private LoadManager lm;

	/**
	 * @param sp
	 */
    protected LoadBroker(SingletonProvider sp) {
		super(sp);
	}
	
	public LoadBroker addDependency(LoadBroker dependency) {
		dependencies.add(dependency);
        return this;
	}
	
	void setManager(LoadManager lm){
		this.lm = lm;
	}
	
	/* (non-Javadoc)
	 * @see com.tournamentpool.broker.sql.SQLBroker#execute()
	 */
	public void execute() {
		try {
			logger.info("Load "+getSQLKey()+" started.");
			super.execute();
			successful = true;
		} finally {
			hasRun = true;
			logger.info("Load "+getSQLKey()+(successful?" successful.":" failed."));
			lm.complete(this);
		}
	}

	public boolean executeIfPossible() {
        for (LoadBroker dependency : dependencies) {
            if (!dependency.hasRun() || dependency.isFailure()) return false;
        }
		new Thread(() -> {
            try {
                execute();
            } catch (Throwable e) {
                logger.error(getSQLKey(), e);
            }
        }, getSQLKey()).start();
		return true;
	}

	/**
	 * @return
	 */
    boolean hasRun() {
		return hasRun;
	}

	/**
	 * @return
	 */
	public boolean isFailure() {
		return !successful;
	}

}
