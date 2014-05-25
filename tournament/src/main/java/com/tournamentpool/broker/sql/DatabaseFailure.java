package com.tournamentpool.broker.sql;

import java.sql.SQLException;

/**
 * Created by avery on 5/24/14.
 */
public class DatabaseFailure extends RuntimeException {
    public DatabaseFailure(SQLException e) {
        super(e);
    }
}
