package com.tournamentpool.db;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by avery on 12/7/13.
 */
public class EmbeddedDBTest {

    @Test
    public void testCreate() throws SQLException, ClassNotFoundException {
        try (Connection connection = createDatabaseConnection()) {

            System.out.println(connection.getMetaData().getDatabaseProductName());
        }
    }


    public static Connection createDatabaseConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        return DriverManager.getConnection("jdbc:derby:sampleDB12");
    }

}
