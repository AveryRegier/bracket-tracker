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

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by avery on 12/14/13.
 */
public class CreateBroker extends BaseTransactionBroker<String> {

    /**
     * @param sp
     */
    public CreateBroker(SingletonProvider sp) {
        super(sp);
    }

    protected void prepare(PreparedStatement stmt) throws SQLException {}

    protected String getSQL() {
        return current;
    }

    @Override
    protected String getSQLKey() {
        return "create";
    }

    @Override
    protected void processUpdateCount(int updateCount) throws SQLException {
        if(updateCount > 1)  {
            System.out.println("Success("+updateCount+"): "+getSQL());
        } else {
            System.out.println("Failed("+updateCount+"): "+getSQL());
        }
    }
}
