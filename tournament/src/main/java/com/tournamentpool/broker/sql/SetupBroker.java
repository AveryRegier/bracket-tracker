package com.tournamentpool.broker.sql;

import com.tournamentpool.application.SingletonProvider;
import utility.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by avery on 12/14/13.
 */
public class SetupBroker extends CreateBroker {

    private Map<String, String> replacements = new HashMap<>();
    /**
     * @param sp
     */
    public SetupBroker(SingletonProvider sp) {
        super(sp);
    }

    @Override
    protected String getJdbcURL() {
        String jdbcURL = super.getJdbcURL();
        if(jdbcURL.contains("derby")) {
            jdbcURL += ";create=true";
            replacements.put("DROP DATABASE IF EXISTS TOURNAMENT", ""); //"DROP SCHEMA TOURNAMENT RESTRICT"
            replacements.put("CREATE DATABASE TOURNAMENT", "CREATE SCHEMA TOURNAMENT");
            replacements.put("USE TOURNAMENT", "SET SCHEMA TOURNAMENT");
            replacements.put("AUTO_INCREMENT", "GENERATED ALWAYS AS IDENTITY");
        }
        return jdbcURL;
    }

    public void init(String path) throws IOException {
        getJdbcURL(); // so that we detect derby

        String allSQL = readFully(getClass().getClassLoader().getResourceAsStream(path));

        String[] commands = stripComments(allSQL.trim(), "#").trim().split(";");

        for(String sql: commands) {
            sql = sql.trim();

            for(Map.Entry<String,String> replacement: replacements.entrySet())  {
                sql = sql.replace(replacement.getKey(), replacement.getValue());
            }

            if(StringUtil.killWhitespace(sql) != null) {
                System.out.println("Adding "+sql);
                addQuery(sql);
            }
        }
    }

    private String stripComments(String allSQL, String commentIndicator) {
        String regex = "(?m)^((?:(?!" + commentIndicator + "|').|'(?:''|[^'])*')*)" + commentIndicator + ".*$";
        return allSQL.replaceAll(regex, "$1");
    }

    private String readFully(InputStream in) throws IOException {
        StringBuilder build = new StringBuilder();
        byte[] buf = new byte[1024];
        int length;
        try (InputStream is = in) {
            while ((length = is.read(buf)) != -1) {
                build.append(new String(buf, 0, length));
            }
        }
        return build.toString();
    }
}
