package com.stream.four.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Runs automatically after Hibernate has created all tables (docker profile only).
 * Handles four things so that a plain "docker compose up" is enough:
 *   1. Create internal DB users and apply GRANT statements
 *   2. Create API_user_account with access to views and stored procedures
 *   3. Add missing foreign key constraints
 *   4. Seed test data (idempotent – uses INSERT IGNORE)
 */
@Slf4j
@Component
@Profile("docker")
public class DbEmployeeAccessInitializer implements ApplicationRunner {

    @Value("${DB_HOST:db}")
    private String dbHost;

    @Value("${DB_PORT:3306}")
    private String dbPort;

    @Value("${MYSQL_DATABASE:stream4}")
    private String database;

    @Value("${MYSQL_ROOT_PASSWORD}")
    private String rootPassword;

    @Value("${EMPLOYEE_JUNIOR_PASSWORD:Junior@Stream4!}")
    private String juniorPassword;

    @Value("${EMPLOYEE_MID_PASSWORD:Mid@Stream4!}")
    private String midPassword;

    @Value("${EMPLOYEE_SENIOR_PASSWORD:Senior@Stream4!}")
    private String seniorPassword;

    @Value("${API_USER_PASSWORD:Api@Stream4!}")
    private String apiUserPassword;

    @Override
    public void run(ApplicationArguments args) {
        String rootUrl = "jdbc:mysql://" + dbHost + ":" + dbPort +
                "/?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&allowMultiQueries=true";

        validateIdentifier(database);

        try (Connection conn = DriverManager.getConnection(rootUrl, "root", rootPassword);
             Statement stmt = conn.createStatement()) {

            stmt.execute("USE `" + database + "`");

            applyUsers(stmt);
            applyGrants(stmt, database);
            applyApiUserAccount(stmt, database);
            applyForeignKeys(stmt, database);
            applyTestData(stmt);

            stmt.execute("FLUSH PRIVILEGES");
            log.info("Database initialisation complete.");

        } catch (Exception e) {
            log.warn("Database initialisation warning: {}", e.getMessage());
        }
    }

    // ── 0. VALIDATION ─────────────────────────────────────────────────────────

    private void validateIdentifier(String value) {
        if (!value.matches("[A-Za-z0-9_]+")) {
            throw new IllegalArgumentException("Unsafe database identifier: " + value);
        }
    }

    // ── 1. CREATE USERS ───────────────────────────────────────────────────────

    private void applyUsers(Statement stmt) throws Exception {
        log.info("Creating internal DB users...");
        stmt.execute("CREATE USER IF NOT EXISTS 'junior_employee'@'%' IDENTIFIED BY '" + juniorPassword.replace("'", "\\'") + "'");
        stmt.execute("CREATE USER IF NOT EXISTS 'mid_employee'@'%'    IDENTIFIED BY '" + midPassword.replace("'", "\\'") + "'");
        stmt.execute("CREATE USER IF NOT EXISTS 'senior_employee'@'%' IDENTIFIED BY '" + seniorPassword.replace("'", "\\'") + "'");
        stmt.execute("CREATE USER IF NOT EXISTS 'API_user_account'@'%' IDENTIFIED BY '" + apiUserPassword.replace("'", "\\'") + "'");
        stmt.execute("ALTER USER 'junior_employee'@'%' IDENTIFIED BY '" + juniorPassword.replace("'", "\\'") + "'");
        stmt.execute("ALTER USER 'mid_employee'@'%'    IDENTIFIED BY '" + midPassword.replace("'", "\\'") + "'");
        stmt.execute("ALTER USER 'senior_employee'@'%' IDENTIFIED BY '" + seniorPassword.replace("'", "\\'") + "'");
        stmt.execute("ALTER USER 'API_user_account'@'%' IDENTIFIED BY '" + apiUserPassword.replace("'", "\\'") + "'");
    }

    // ── 2. EMPLOYEE GRANTS ────────────────────────────────────────────────────

    private void applyGrants(Statement stmt, String db) throws Exception {
        log.info("Applying employee grants...");
        String d = "`" + db + "`";
        List<String> grants = List.of(
            // JUNIOR
            "GRANT SELECT ON " + d + ".`users`            TO 'junior_employee'@'%'",
            "GRANT SELECT ON " + d + ".`profile`          TO 'junior_employee'@'%'",
            "GRANT SELECT ON " + d + ".`profile_filters`  TO 'junior_employee'@'%'",
            "GRANT SELECT ON " + d + ".`invitations`      TO 'junior_employee'@'%'",
            "GRANT SELECT ON " + d + ".`employee`         TO 'junior_employee'@'%'",
            "GRANT SELECT ON " + d + ".`role_right`       TO 'junior_employee'@'%'",
            // MID
            "GRANT SELECT ON " + d + ".`users`                     TO 'mid_employee'@'%'",
            "GRANT SELECT ON " + d + ".`profile`                   TO 'mid_employee'@'%'",
            "GRANT SELECT ON " + d + ".`profile_filters`           TO 'mid_employee'@'%'",
            "GRANT SELECT ON " + d + ".`preferences`               TO 'mid_employee'@'%'",
            "GRANT SELECT ON " + d + ".`invitations`               TO 'mid_employee'@'%'",
            "GRANT SELECT ON " + d + ".`employee`                  TO 'mid_employee'@'%'",
            "GRANT SELECT ON " + d + ".`role_right`                TO 'mid_employee'@'%'",
            "GRANT SELECT ON " + d + ".`titles`                    TO 'mid_employee'@'%'",
            "GRANT SELECT ON " + d + ".`title_content_warnings`    TO 'mid_employee'@'%'",
            "GRANT SELECT ON " + d + ".`title_supported_qualities` TO 'mid_employee'@'%'",
            "GRANT SELECT ON " + d + ".`seasons`                   TO 'mid_employee'@'%'",
            "GRANT SELECT ON " + d + ".`episodes`                  TO 'mid_employee'@'%'",
            "GRANT SELECT ON " + d + ".`watchlist`                 TO 'mid_employee'@'%'",
            "GRANT UPDATE (name, avatar, age, maturity_level, deleted) ON " + d + ".`profile` TO 'mid_employee'@'%'",
            "GRANT INSERT, DELETE ON " + d + ".`profile_filters` TO 'mid_employee'@'%'",
            "GRANT UPDATE (deleted) ON " + d + ".`users`         TO 'mid_employee'@'%'",
            // SENIOR
            "GRANT SELECT, INSERT, UPDATE, DELETE ON " + d + ".`users`                     TO 'senior_employee'@'%'",
            "GRANT SELECT, INSERT, UPDATE, DELETE ON " + d + ".`profile`                   TO 'senior_employee'@'%'",
            "GRANT SELECT, INSERT, UPDATE, DELETE ON " + d + ".`profile_filters`           TO 'senior_employee'@'%'",
            "GRANT SELECT, INSERT, UPDATE, DELETE ON " + d + ".`preferences`               TO 'senior_employee'@'%'",
            "GRANT SELECT, INSERT, UPDATE, DELETE ON " + d + ".`subscriptions`             TO 'senior_employee'@'%'",
            "GRANT SELECT, INSERT, UPDATE, DELETE ON " + d + ".`trial`                     TO 'senior_employee'@'%'",
            "GRANT SELECT, INSERT, UPDATE, DELETE ON " + d + ".`invitations`               TO 'senior_employee'@'%'",
            "GRANT SELECT, INSERT, UPDATE, DELETE ON " + d + ".`employee`                  TO 'senior_employee'@'%'",
            "GRANT SELECT, INSERT, UPDATE, DELETE ON " + d + ".`role_right`                TO 'senior_employee'@'%'",
            "GRANT SELECT, INSERT, UPDATE, DELETE ON " + d + ".`titles`                    TO 'senior_employee'@'%'",
            "GRANT SELECT, INSERT, UPDATE, DELETE ON " + d + ".`title_content_warnings`    TO 'senior_employee'@'%'",
            "GRANT SELECT, INSERT, UPDATE, DELETE ON " + d + ".`title_supported_qualities` TO 'senior_employee'@'%'",
            "GRANT SELECT, INSERT, UPDATE, DELETE ON " + d + ".`seasons`                   TO 'senior_employee'@'%'",
            "GRANT SELECT, INSERT, UPDATE, DELETE ON " + d + ".`episodes`                  TO 'senior_employee'@'%'",
            "GRANT SELECT, INSERT, UPDATE, DELETE ON " + d + ".`watch_events`              TO 'senior_employee'@'%'",
            "GRANT SELECT, INSERT, UPDATE, DELETE ON " + d + ".`watchlist`                 TO 'senior_employee'@'%'"
        );
        for (String sql : grants) {
            stmt.execute(sql);
        }
    }

    // ── 3. API_USER_ACCOUNT ───────────────────────────────────────────────────

    private void applyApiUserAccount(Statement stmt, String db) throws Exception {
        log.info("Setting up API_user_account views and procedures...");
        String d = "`" + db + "`";

        stmt.execute("CREATE OR REPLACE VIEW " + d + ".v_active_subscriptions AS " +
            "SELECT s.subscriptionid, u.userid, u.name AS user_name, u.email, " +
            "s.plan, s.status, s.total_price, s.discount_percentage, " +
            "s.discount_end_date, s.start_date, s.end_date, s.auto_renew " +
            "FROM subscriptions s JOIN users u ON u.userid = s.userid " +
            "WHERE s.status = 'ACTIVE' AND u.deleted = 0");

        stmt.execute("CREATE OR REPLACE VIEW " + d + ".v_user_profiles AS " +
            "SELECT p.id AS profile_id, p.user_id, u.name AS user_name, u.email, " +
            "p.name AS profile_name, p.avatar, p.age, p.maturity_level " +
            "FROM profile p JOIN users u ON u.userid = p.user_id " +
            "WHERE p.deleted = 0 AND u.deleted = 0");

        stmt.execute("CREATE OR REPLACE VIEW " + d + ".v_title_catalogue AS " +
            "SELECT id, name, description, release_year, type, genre, maturity_rating " +
            "FROM titles WHERE deleted = 0");

        stmt.execute("CREATE OR REPLACE VIEW " + d + ".v_viewing_history AS " +
            "SELECT we.id AS event_id, we.user_id, u.name AS user_name, " +
            "we.title_id, t.name AS title_name, we.progress_seconds, we.finished, we.last_updated " +
            "FROM watch_events we " +
            "JOIN users u ON u.userid = we.user_id " +
            "JOIN titles t ON t.id = we.title_id");

        stmt.execute("GRANT SELECT ON " + d + ".v_active_subscriptions TO 'API_user_account'@'%'");
        stmt.execute("GRANT SELECT ON " + d + ".v_user_profiles        TO 'API_user_account'@'%'");
        stmt.execute("GRANT SELECT ON " + d + ".v_title_catalogue      TO 'API_user_account'@'%'");
        stmt.execute("GRANT SELECT ON " + d + ".v_viewing_history      TO 'API_user_account'@'%'");

        // Stored procedures
        stmt.execute("DROP PROCEDURE IF EXISTS sp_get_user_by_email");
        stmt.execute("CREATE PROCEDURE sp_get_user_by_email(IN p_email VARCHAR(255)) " +
            "BEGIN SELECT userid, name, email, role, verified, deleted FROM users " +
            "WHERE email = p_email AND deleted = 0; END");

        stmt.execute("DROP PROCEDURE IF EXISTS sp_cancel_subscription");
        stmt.execute("CREATE PROCEDURE sp_cancel_subscription(IN p_user_id VARCHAR(255)) " +
            "BEGIN UPDATE subscriptions SET status = 'CANCELLED', auto_renew = 0 " +
            "WHERE userid = p_user_id AND status = 'ACTIVE'; END");

        stmt.execute("DROP PROCEDURE IF EXISTS sp_get_subscription_overview");
        stmt.execute("CREATE PROCEDURE sp_get_subscription_overview(IN p_user_id VARCHAR(255)) " +
            "BEGIN SELECT s.subscriptionid, s.plan, s.status, s.total_price, " +
            "s.discount_percentage, s.start_date, s.end_date, " +
            "t.status AS trial_status, t.end_date AS trial_end_date " +
            "FROM users u " +
            "LEFT JOIN subscriptions s ON s.userid = u.userid AND s.status = 'ACTIVE' " +
            "LEFT JOIN trial t ON t.userid = u.userid AND t.status = 'ACTIVE' " +
            "WHERE u.userid = p_user_id; END");

        stmt.execute("GRANT EXECUTE ON PROCEDURE " + d + ".sp_get_user_by_email         TO 'API_user_account'@'%'");
        stmt.execute("GRANT EXECUTE ON PROCEDURE " + d + ".sp_cancel_subscription       TO 'API_user_account'@'%'");
        stmt.execute("GRANT EXECUTE ON PROCEDURE " + d + ".sp_get_subscription_overview TO 'API_user_account'@'%'");

        // Trigger
        stmt.execute("DROP TRIGGER IF EXISTS trg_convert_trial_on_subscribe");
        stmt.execute("CREATE TRIGGER trg_convert_trial_on_subscribe " +
            "AFTER INSERT ON subscriptions FOR EACH ROW " +
            "BEGIN IF NEW.status = 'ACTIVE' THEN " +
            "UPDATE trial SET status = 'CONVERTED', converted_to_paid = 1, converted_date = CURDATE() " +
            "WHERE userid = NEW.userid AND status = 'ACTIVE'; " +
            "END IF; END");
    }

    // ── 4. FOREIGN KEY CONSTRAINTS ────────────────────────────────────────────

    private void applyForeignKeys(Statement stmt, String db) throws Exception {
        log.info("Applying foreign key constraints...");

        addFkIfMissing(stmt, db, "profile",      "fk_profile_user",        "user_id",        "users",   "userid",    "CASCADE");
        addFkIfMissing(stmt, db, "preferences",  "fk_preferences_profile", "profile_id",     "profile", "id",        "CASCADE");
        addFkIfMissing(stmt, db, "seasons",      "fk_season_title",        "title_id",       "titles",  "id",        "CASCADE");
        addFkIfMissing(stmt, db, "episodes",     "fk_episode_season",      "season_id",      "seasons", "id",        "CASCADE");
        addFkIfMissing(stmt, db, "watch_events", "fk_watch_event_user",    "user_id",        "users",   "userid",    "CASCADE");
        addFkIfMissing(stmt, db, "watch_events", "fk_watch_event_title",   "title_id",       "titles",  "id",        "CASCADE");
        addFkIfMissing(stmt, db, "watchlist",    "fk_watchlist_user",      "user_id",        "users",   "userid",    "CASCADE");
        addFkIfMissing(stmt, db, "watchlist",    "fk_watchlist_title",     "title_id",       "titles",  "id",        "CASCADE");
        addFkIfMissing(stmt, db, "invitations",  "fk_invitation_inviter",  "inviter_user_id","users",   "userid",    "SET NULL");
        addFkIfMissing(stmt, db, "invitations",  "fk_invitation_invitee",  "invitee_user_id","users",   "userid",    "SET NULL");
    }

    private void addFkIfMissing(Statement stmt, String db, String table, String constraintName,
                                 String column, String refTable, String refColumn,
                                 String onDelete) throws Exception {
        String checkSql = "SELECT COUNT(*) FROM information_schema.KEY_COLUMN_USAGE " +
                "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? AND CONSTRAINT_NAME = ?";
        try (PreparedStatement ps = stmt.getConnection().prepareStatement(checkSql)) {
            ps.setString(1, db);
            ps.setString(2, table);
            ps.setString(3, constraintName);
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) {
                // Identifiers cannot be parameterised; db is validated above, others are hardcoded
                stmt.execute("ALTER TABLE `" + db + "`.`" + table + "` " +
                        "ADD CONSTRAINT `" + constraintName + "` " +
                        "FOREIGN KEY (`" + column + "`) REFERENCES `" + refTable + "`(`" + refColumn + "`) " +
                        "ON DELETE " + onDelete);
            }
        }
    }

    // ── 5. TEST DATA ──────────────────────────────────────────────────────────

    private void applyTestData(Statement stmt) throws Exception {
        log.info("Seeding test data...");
        // Password for all test accounts: Test1234!
        String pw = "$2a$10$KMvvCJ1vLWKFJtz4B0kUzubUPDaFM6wTwk93cBwMYRRKx.3//W7v2";

        // Users
        exec(stmt, "INSERT IGNORE INTO users (userid,name,email,password,age,role,verified,deleted,failed_login_attempts,referral_discount_used,created_date) VALUES " +
            "('user-001','Alice Johnson','alice@stream4.com','" + pw + "',30,'USER',1,0,0,0,NOW())," +
            "('user-002','Bob Smith','bob@stream4.com','" + pw + "',17,'USER',1,0,0,0,NOW())," +
            "('user-003','Carol Williams','carol@stream4.com','" + pw + "',10,'USER',1,0,0,0,NOW())");

        // Profiles
        exec(stmt, "INSERT IGNORE INTO profile (id,user_id,name,avatar,age,maturity_level,deleted) VALUES " +
            "('prof-001','user-001','Alice Main','avatar1.png',30,'ADULT',0)," +
            "('prof-002','user-001','Alice Kids','avatar2.png',8,'KIDS',0)," +
            "('prof-003','user-002','Bob Profile','avatar3.png',17,'TEENS',0)," +
            "('prof-004','user-003','Carol Profile','avatar4.png',10,'KIDS',0)");

        // Preferences
        exec(stmt, "INSERT IGNORE INTO preferences (id,profile_id,preferred_type,minimum_maturity_rating) VALUES " +
            "('pref-001','prof-001','SERIES','PG')," +
            "('pref-002','prof-003','MOVIE','ALL')");

        // Trials
        exec(stmt, "INSERT IGNORE INTO trial (userid,start_date,end_date,status,converted_to_paid) VALUES " +
            "('user-002',CURDATE(),DATE_ADD(CURDATE(),INTERVAL 7 DAY),'ACTIVE',0)," +
            "('user-003',CURDATE(),DATE_ADD(CURDATE(),INTERVAL 7 DAY),'ACTIVE',0)");

        // Subscription
        exec(stmt, "INSERT IGNORE INTO subscriptions (userid,plan,status,total_price,discount_percentage,start_date,end_date,auto_renew,invite_discount_applied,invite_discount_used) VALUES " +
            "('user-001','HD','ACTIVE',12.99,0.00,CURDATE(),DATE_ADD(CURDATE(),INTERVAL 1 MONTH),1,0,0)");

        // Titles
        exec(stmt, "INSERT IGNORE INTO titles (id,name,description,release_year,type,genre,maturity_rating,deleted) VALUES " +
            "('title-001','Breaking Bad','A chemistry teacher turned drug lord.',2008,'SERIES','DRAMA','MATURE',0)," +
            "('title-002','The Dark Knight','Batman faces the Joker in Gotham City.',2008,'MOVIE','ACTION','TEEN',0)," +
            "('title-003','Toy Story','Toys come to life when humans are not looking.',1995,'MOVIE','ANIMATION','ALL',0)," +
            "('title-004','Stranger Things','Kids encounter supernatural forces.',2016,'SERIES','HORROR','TEEN',0)," +
            "('title-005','The Crown','The reign of Queen Elizabeth II.',2016,'SERIES','DRAMA','PG',0)");

        // Content warnings
        exec(stmt, "INSERT IGNORE INTO title_content_warnings (title_id,warning) VALUES " +
            "('title-001','VIOLENCE'),('title-001','DRUG_USE')," +
            "('title-002','VIOLENCE')," +
            "('title-004','FEAR'),('title-004','VIOLENCE')");

        // Supported qualities
        exec(stmt, "INSERT IGNORE INTO title_supported_qualities (title_id,supported_qualities) VALUES " +
            "('title-001','HD'),('title-001','UHD')," +
            "('title-002','SD'),('title-002','HD'),('title-002','UHD')," +
            "('title-003','SD'),('title-003','HD')," +
            "('title-004','HD'),('title-004','UHD')," +
            "('title-005','SD'),('title-005','HD')");

        // Seasons & Episodes
        exec(stmt, "INSERT IGNORE INTO seasons (id,title_id,season_number,deleted) VALUES " +
            "('season-001','title-001',1,0),('season-002','title-001',2,0)");

        exec(stmt, "INSERT IGNORE INTO episodes (id,season_id,episode_number,name,description,deleted) VALUES " +
            "('ep-001','season-001',1,'Pilot','Walter White begins his transformation.',0)," +
            "('ep-002','season-001',2,'Cat''s in the Bag','Walt and Jesse deal with consequences.',0)," +
            "('ep-003','season-002',1,'Seven Thirty-Seven','Walt receives a warning.',0)");

        // Watchlist
        exec(stmt, "INSERT IGNORE INTO watchlist (id,user_id,title_id,added_at) VALUES " +
            "('wl-001','user-001','title-004',UNIX_TIMESTAMP())," +
            "('wl-002','user-001','title-005',UNIX_TIMESTAMP())," +
            "('wl-003','user-002','title-003',UNIX_TIMESTAMP())");

        // Watch events
        exec(stmt, "INSERT IGNORE INTO watch_events (id,user_id,title_id,progress_seconds,finished,last_updated) VALUES " +
            "('we-001','user-001','title-001',2700,0,UNIX_TIMESTAMP())," +
            "('we-002','user-001','title-002',9120,1,UNIX_TIMESTAMP())," +
            "('we-003','user-002','title-003',4800,1,UNIX_TIMESTAMP())");

        // Invitation
        exec(stmt, "INSERT IGNORE INTO invitations (id,inviter_user_id,invitee_email,token,used,discount_applied) VALUES " +
            "('inv-001','user-001','newuser@example.com','invite-token-abc123',0,0)");

        // Role rights & Employees
        // Table names match @Table(name = "RoleRight") and @Table(name = "Employee") exactly
        exec(stmt, "INSERT IGNORE INTO RoleRight (RoleID,RoleName,Description,Permissions) VALUES " +
            "(1,'JUNIOR_EMPLOYEE','Read-only access to basic user and profile data','READ_USERS,READ_PROFILES')," +
            "(2,'MID_EMPLOYEE','Read access plus limited write on profiles and accounts','READ_USERS,READ_PROFILES,UPDATE_PROFILES,DEACTIVATE_ACCOUNTS')," +
            "(3,'SENIOR_EMPLOYEE','Full access including financial and viewing history','ALL')");

        exec(stmt, "INSERT IGNORE INTO Employee (EmployeeID,Name,Email,Password,RoleID,IsActive) VALUES " +
            "(1,'Junior Jan','junior@stream4.com','" + pw + "',1,1)," +
            "(2,'Mid Marie','mid@stream4.com','" + pw + "',2,1)," +
            "(3,'Senior Steve','senior@stream4.com','" + pw + "',3,1)");

        // Employees must also exist in `users` so they can log in via JWT and pass EmployeeSecurity checks
        exec(stmt, "INSERT IGNORE INTO users (userid,name,email,password,age,role,verified,deleted,failed_login_attempts,referral_discount_used,created_date) VALUES " +
            "('emp-001','Junior Jan','junior@stream4.com','" + pw + "',25,'JUNIOR_EMPLOYEE',1,0,0,0,NOW())," +
            "('emp-002','Mid Marie','mid@stream4.com','"   + pw + "',30,'MID_EMPLOYEE',1,0,0,0,NOW())," +
            "('emp-003','Senior Steve','senior@stream4.com','" + pw + "',35,'SENIOR_EMPLOYEE',1,0,0,0,NOW())");

        log.info("Test data seeded successfully. All passwords: Test1234!");
    }

    private void exec(Statement stmt, String sql) {
        try {
            stmt.execute(sql);
        } catch (Exception e) {
            log.warn("Seed statement skipped: {}", e.getMessage());
        }
    }
}
