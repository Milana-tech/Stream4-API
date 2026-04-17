-- StreamFlix – DBMS Employee Role Grants
-- Run this ONCE after the backend has started and Hibernate has created all tables.
-- Command: docker cp db/grants.sql stream4-db:/tmp/grants.sql
--          docker exec stream4-db bash -c "mysql -u root -p\$MYSQL_ROOT_PASSWORD < /tmp/grants.sql"

-- ---------------------------------------------------------------
-- JUNIOR EMPLOYEE
-- View basic account status and general profile details only.
-- No access to financial or viewing data.
-- ---------------------------------------------------------------
GRANT SELECT ON stream4.users           TO 'junior_employee'@'%';
GRANT SELECT ON stream4.profile         TO 'junior_employee'@'%';
GRANT SELECT ON stream4.profile_filters TO 'junior_employee'@'%';
GRANT SELECT ON stream4.invitations     TO 'junior_employee'@'%';
GRANT SELECT ON stream4.employee        TO 'junior_employee'@'%';
GRANT SELECT ON stream4.role_right      TO 'junior_employee'@'%';

-- ---------------------------------------------------------------
-- MID-LEVEL EMPLOYEE
-- View all non-financial data. Can adjust profiles and
-- activate/deactivate accounts. No access to subscriptions or trial.
-- ---------------------------------------------------------------
GRANT SELECT ON stream4.users                     TO 'mid_employee'@'%';
GRANT SELECT ON stream4.profile                   TO 'mid_employee'@'%';
GRANT SELECT ON stream4.profile_filters           TO 'mid_employee'@'%';
GRANT SELECT ON stream4.preferences               TO 'mid_employee'@'%';
GRANT SELECT ON stream4.invitations               TO 'mid_employee'@'%';
GRANT SELECT ON stream4.employee                  TO 'mid_employee'@'%';
GRANT SELECT ON stream4.role_right                TO 'mid_employee'@'%';
GRANT SELECT ON stream4.titles                    TO 'mid_employee'@'%';
GRANT SELECT ON stream4.title_content_warnings    TO 'mid_employee'@'%';
GRANT SELECT ON stream4.title_supported_qualities TO 'mid_employee'@'%';
GRANT SELECT ON stream4.seasons                   TO 'mid_employee'@'%';
GRANT SELECT ON stream4.episodes                  TO 'mid_employee'@'%';
GRANT SELECT ON stream4.watchlist                 TO 'mid_employee'@'%';

-- Adjust profile settings
GRANT UPDATE (name, avatar, age, maturity_level, deleted) ON stream4.profile TO 'mid_employee'@'%';
GRANT INSERT, DELETE ON stream4.profile_filters TO 'mid_employee'@'%';

-- Activate / deactivate accounts
GRANT UPDATE (deleted) ON stream4.users TO 'mid_employee'@'%';

-- ---------------------------------------------------------------
-- SENIOR EMPLOYEE
-- Full access including subscriptions, trial and viewing history.
-- ---------------------------------------------------------------
GRANT SELECT, INSERT, UPDATE, DELETE ON stream4.users                     TO 'senior_employee'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON stream4.profile                   TO 'senior_employee'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON stream4.profile_filters           TO 'senior_employee'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON stream4.preferences               TO 'senior_employee'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON stream4.subscriptions             TO 'senior_employee'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON stream4.trial                     TO 'senior_employee'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON stream4.invitations               TO 'senior_employee'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON stream4.employee                  TO 'senior_employee'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON stream4.role_right                TO 'senior_employee'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON stream4.titles                    TO 'senior_employee'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON stream4.title_content_warnings    TO 'senior_employee'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON stream4.title_supported_qualities TO 'senior_employee'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON stream4.seasons                   TO 'senior_employee'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON stream4.episodes                  TO 'senior_employee'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON stream4.watch_events              TO 'senior_employee'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON stream4.watchlist                 TO 'senior_employee'@'%';

FLUSH PRIVILEGES;
