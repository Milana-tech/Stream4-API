package com.stream.four.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

/**
 * Applies MySQL GRANT statements for the three internal employee DB users
 * after Hibernate has finished creating all tables.
 *
 * Runs only when the "docker" Spring profile is active so it is skipped
 * in local development and tests (which use H2).
 */
@Slf4j
@Component
@Profile("docker")
public class DbEmployeeAccessInitializer implements ApplicationRunner {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${DB_HOST:db}")
    private String dbHost;

    @Value("${DB_PORT:3306}")
    private String dbPort;

    @Value("${MYSQL_DATABASE:stream4}")
    private String database;

    @Value("${MYSQL_ROOT_PASSWORD}")
    private String rootPassword;

    @Override
    public void run(ApplicationArguments args) {
        String rootUrl = "jdbc:mysql://" + dbHost + ":" + dbPort +
                "/?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

        try (Connection conn = DriverManager.getConnection(rootUrl, "root", rootPassword);
             Statement stmt = conn.createStatement()) {

            log.info("Applying DBMS employee access grants on database '{}'", database);

            for (String sql : buildGrantStatements(database)) {
                stmt.execute(sql);
            }

            stmt.execute("FLUSH PRIVILEGES");
            log.info("DBMS employee access grants applied successfully.");

        } catch (Exception e) {
            // Log but do not crash the application — grants may already be applied
            log.warn("Could not apply employee DB grants: {}", e.getMessage());
        }
    }

    private List<String> buildGrantStatements(String db) {
        String d = "`" + db + "`";
        return List.of(

            // ── JUNIOR EMPLOYEE ────────────────────────────────────────────
            // View basic account status and general profile details only.
            "GRANT SELECT ON " + d + ".`users`            TO 'junior_employee'@'%'",
            "GRANT SELECT ON " + d + ".`Profile`          TO 'junior_employee'@'%'",
            "GRANT SELECT ON " + d + ".`profile_filters`  TO 'junior_employee'@'%'",
            "GRANT SELECT ON " + d + ".`invitations`      TO 'junior_employee'@'%'",
            "GRANT SELECT ON " + d + ".`Employee`         TO 'junior_employee'@'%'",
            "GRANT SELECT ON " + d + ".`RoleRight`        TO 'junior_employee'@'%'",

            // ── MID-LEVEL EMPLOYEE ─────────────────────────────────────────
            // View all non-financial data; adjust profiles; activate/deactivate accounts.
            "GRANT SELECT ON " + d + ".`users`                     TO 'mid_employee'@'%'",
            "GRANT SELECT ON " + d + ".`Profile`                   TO 'mid_employee'@'%'",
            "GRANT SELECT ON " + d + ".`profile_filters`           TO 'mid_employee'@'%'",
            "GRANT SELECT ON " + d + ".`preferences`               TO 'mid_employee'@'%'",
            "GRANT SELECT ON " + d + ".`preferences_genres`        TO 'mid_employee'@'%'",
            "GRANT SELECT ON " + d + ".`invitations`               TO 'mid_employee'@'%'",
            "GRANT SELECT ON " + d + ".`Employee`                  TO 'mid_employee'@'%'",
            "GRANT SELECT ON " + d + ".`RoleRight`                 TO 'mid_employee'@'%'",
            "GRANT SELECT ON " + d + ".`titles`                    TO 'mid_employee'@'%'",
            "GRANT SELECT ON " + d + ".`title_content_warnings`    TO 'mid_employee'@'%'",
            "GRANT SELECT ON " + d + ".`title_supported_qualities` TO 'mid_employee'@'%'",
            "GRANT SELECT ON " + d + ".`seasons`                   TO 'mid_employee'@'%'",
            "GRANT SELECT ON " + d + ".`episodes`                  TO 'mid_employee'@'%'",
            "GRANT SELECT ON " + d + ".`watchlist`                 TO 'mid_employee'@'%'",
            // Adjust profile settings
            "GRANT UPDATE (name, avatar, age, maturityLevel, deleted) ON " + d + ".`Profile` TO 'mid_employee'@'%'",
            "GRANT INSERT, DELETE ON " + d + ".`profile_filters`           TO 'mid_employee'@'%'",
            // Activate / deactivate accounts
            "GRANT UPDATE (deleted) ON " + d + ".`users`                   TO 'mid_employee'@'%'",

            // ── SENIOR EMPLOYEE ────────────────────────────────────────────
            // Full access including subscriptions, trial, and viewing history.
            "GRANT SELECT, INSERT, UPDATE, DELETE ON " + d + ".`users`                     TO 'senior_employee'@'%'",
            "GRANT SELECT, INSERT, UPDATE, DELETE ON " + d + ".`Profile`                   TO 'senior_employee'@'%'",
            "GRANT SELECT, INSERT, UPDATE, DELETE ON " + d + ".`profile_filters`           TO 'senior_employee'@'%'",
            "GRANT SELECT, INSERT, UPDATE, DELETE ON " + d + ".`preferences`               TO 'senior_employee'@'%'",
            "GRANT SELECT, INSERT, UPDATE, DELETE ON " + d + ".`preferences_genres`        TO 'senior_employee'@'%'",
            "GRANT SELECT, INSERT, UPDATE, DELETE ON " + d + ".`subscriptions`             TO 'senior_employee'@'%'",
            "GRANT SELECT, INSERT, UPDATE, DELETE ON " + d + ".`Trial`                     TO 'senior_employee'@'%'",
            "GRANT SELECT, INSERT, UPDATE, DELETE ON " + d + ".`invitations`               TO 'senior_employee'@'%'",
            "GRANT SELECT, INSERT, UPDATE, DELETE ON " + d + ".`Employee`                  TO 'senior_employee'@'%'",
            "GRANT SELECT, INSERT, UPDATE, DELETE ON " + d + ".`RoleRight`                 TO 'senior_employee'@'%'",
            "GRANT SELECT, INSERT, UPDATE, DELETE ON " + d + ".`titles`                    TO 'senior_employee'@'%'",
            "GRANT SELECT, INSERT, UPDATE, DELETE ON " + d + ".`title_content_warnings`    TO 'senior_employee'@'%'",
            "GRANT SELECT, INSERT, UPDATE, DELETE ON " + d + ".`title_supported_qualities` TO 'senior_employee'@'%'",
            "GRANT SELECT, INSERT, UPDATE, DELETE ON " + d + ".`seasons`                   TO 'senior_employee'@'%'",
            "GRANT SELECT, INSERT, UPDATE, DELETE ON " + d + ".`episodes`                  TO 'senior_employee'@'%'",
            "GRANT SELECT, INSERT, UPDATE, DELETE ON " + d + ".`watch_events`              TO 'senior_employee'@'%'",
            "GRANT SELECT, INSERT, UPDATE, DELETE ON " + d + ".`watchlist`                 TO 'senior_employee'@'%'"
        );
    }
}
