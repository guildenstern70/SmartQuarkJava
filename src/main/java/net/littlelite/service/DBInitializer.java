/*
 * The SmartQuark Project Java Edition
 * Copyright (c) Alessio Saltarin, 2026
 * This software is licensed under ISC License
 * See LICENSE
 */

package net.littlelite.service;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import net.littlelite.model.Person;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class DBInitializer
{
    private final Instance<AgroalDataSource> dataSource;

    @Inject
    public DBInitializer(Instance<AgroalDataSource> dataSource)
    {
        this.dataSource = dataSource;
    }

    public void populateDB()
    {
        var personsOnDb = Person.count();

        if (personsOnDb == 0L)
        {
            log.info("Populating DB");

            if (!dataSource.isResolvable())
            {
                log.warn("Datasource bean is not resolvable, skipping DB population.");
                return;
            }

            InputStream sqlStream = this.getClass().getClassLoader().getResourceAsStream("import.sql");
            if (sqlStream != null)
            {
                log.info("Found import.sql on classpath, executing script...");
                try (sqlStream; Connection conn = dataSource.get().getConnection())
                {
                    executeSqlScript(conn, sqlStream);
                    log.info("import.sql executed successfully");
                    return;
                } catch (Exception ex) {
                    log.error("Failed to execute import.sql, falling back to programmatic population", ex);
                }
            }
            else
            {
                log.info("import.sql not found on classpath, using programmatic population");
            }
            log.info("Done programmatic population.");
        }
        else
        {
            log.info("DB already populated, skipping population step.");
            log.info("There are {} persons on DB", personsOnDb);
        }
    }

    private void executeSqlScript(Connection conn, InputStream sqlStream) throws IOException, SQLException
    {
        var sb = new StringBuilder();
        try (var reader = new BufferedReader(new InputStreamReader(sqlStream, StandardCharsets.UTF_8)))
        {
            reader.lines().forEach(line ->
            {
                String trimmed = line.trim();
                // Skip SQL comments and empty lines before splitting into statements.
                if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                    return;
                }
                sb.append(line).append('\n');
            });
        }

        var script = sb.toString();
        String[] statements = script.split(";\\s*\\n");
        conn.setAutoCommit(false);
        try (Statement stmt = conn.createStatement())
        {
            for (var s : statements)
            {
                var sql = s.trim();
                if (sql.isEmpty())
                {
                    continue;
                }
                stmt.addBatch(sql);
            }
            stmt.executeBatch();
        }
        conn.commit();
    }
}
