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

package com.tournamentpool.broker.sql;

import com.tournamentpool.application.SingletonProvider;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by avery on 12/14/13.
 */
public abstract class BaseTransactionBroker<Q> extends PreparedStatementBroker {

    private List<Q> queries = new LinkedList<Q>();
    protected Q current = null;

    /**
     * @param sp
     */
    public BaseTransactionBroker(SingletonProvider sp) {
        super(sp);
    }

    protected void addQuery(Q query) {
        queries.add(query);
    }

    protected boolean hasMoreQueries() {
        if(queries.isEmpty()) {
            current = null;
        } else {
            current = queries.remove(0);
            reset();
        }
        return current != null;
    }

    protected void execute(Connection conn) throws SQLException {
        try {
            conn.setAutoCommit(false);
            while(hasMoreQueries()) {
                super.execute(conn);
            }
            conn.commit();
        } catch(SQLException e) {
            conn.rollback();
            throw new SQLException("Error in "+current, e);
        } finally {
            conn.setAutoCommit(true);
        }
    }
}
