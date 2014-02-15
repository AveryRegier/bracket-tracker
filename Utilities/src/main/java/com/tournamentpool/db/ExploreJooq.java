package com.tournamentpool.db;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.jooq.SQLDialect;
import org.jooq.tools.jdbc.JDBCUtils;

/**
 * Created by avery on 12/15/13.
 */
public class ExploreJooq {

    public static void main(String... args) throws JSQLParserException {
        SQLDialect dialect = JDBCUtils.dialect("");

        Statement statement = CCJSqlParserUtil.parse("SELECT * FROM MY_TABLE1");
        statement.accept(new JooqBuilder(dialect));

    }
}
