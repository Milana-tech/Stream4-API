-- =============================================================
--  StreamFlix – API_user_account Setup
--
--  The API_user_account is the dedicated MySQL user for all
--  application (API) access. It has no direct table access;
--  it may only use predefined views and stored procedures.
--
--  NOTE: StreamFlix uses Hibernate ORM, which requires direct
--  table access and therefore deviates from this rule in practice.
--  The views, stored procedures, and trigger below exist on the
--  DBMS to demonstrate the intended access control design.
--
--  Run after the backend has started and Hibernate has created
--  all tables.
-- =============================================================

USE stream4;

-- ---------------------------------------------------------------
-- 1. API_user_account
-- ---------------------------------------------------------------
CREATE USER IF NOT EXISTS 'API_user_account'@'%' IDENTIFIED BY 'Api@Stream4!';
-- No direct table grants — access is restricted to views and procedures only.


-- ---------------------------------------------------------------
-- 2. VIEWS
-- ---------------------------------------------------------------

-- Active subscriptions with user info (no passwords or tokens)
CREATE OR REPLACE VIEW v_active_subscriptions AS
SELECT
    s.subscriptionid,
    u.userid,
    u.name        AS user_name,
    u.email,
    s.plan,
    s.status,
    s.total_price,
    s.discount_percentage,
    s.discount_end_date,
    s.start_date,
    s.end_date,
    s.auto_renew
FROM subscriptions s
JOIN users u ON u.userid = s.userid
WHERE s.status = 'ACTIVE'
  AND u.deleted = 0;

-- User profiles with maturity level (excludes deleted profiles)
CREATE OR REPLACE VIEW v_user_profiles AS
SELECT
    p.id          AS profile_id,
    p.user_id,
    u.name        AS user_name,
    u.email,
    p.name        AS profile_name,
    p.avatar,
    p.age,
    p.maturity_level
FROM profile p
JOIN users u ON u.userid = p.user_id
WHERE p.deleted = 0
  AND u.deleted = 0;

-- Title catalogue (excludes deleted titles)
CREATE OR REPLACE VIEW v_title_catalogue AS
SELECT
    id,
    name,
    description,
    release_year,
    type,
    genre,
    maturity_rating
FROM titles
WHERE deleted = 0;

-- Viewing history per user
CREATE OR REPLACE VIEW v_viewing_history AS
SELECT
    we.id         AS event_id,
    we.user_id,
    u.name        AS user_name,
    we.title_id,
    t.name        AS title_name,
    we.progress_seconds,
    we.finished,
    we.last_updated
FROM watch_events we
JOIN users  u ON u.userid  = we.user_id
JOIN titles t ON t.id      = we.title_id;

-- Grant SELECT on views to API_user_account
GRANT SELECT ON stream4.v_active_subscriptions TO 'API_user_account'@'%';
GRANT SELECT ON stream4.v_user_profiles        TO 'API_user_account'@'%';
GRANT SELECT ON stream4.v_title_catalogue      TO 'API_user_account'@'%';
GRANT SELECT ON stream4.v_viewing_history      TO 'API_user_account'@'%';


-- ---------------------------------------------------------------
-- 3. STORED PROCEDURES
-- ---------------------------------------------------------------

DROP PROCEDURE IF EXISTS sp_get_user_by_email;
DELIMITER $$
CREATE PROCEDURE sp_get_user_by_email(IN p_email VARCHAR(255))
BEGIN
    SELECT
        userid,
        name,
        email,
        role,
        verified,
        deleted
    FROM users
    WHERE email = p_email
      AND deleted = 0;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_cancel_subscription;
DELIMITER $$
CREATE PROCEDURE sp_cancel_subscription(IN p_user_id VARCHAR(255))
BEGIN
    UPDATE subscriptions
    SET status     = 'CANCELLED',
        auto_renew = 0
    WHERE userid = p_user_id
      AND status = 'ACTIVE';
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_get_subscription_overview;
DELIMITER $$
CREATE PROCEDURE sp_get_subscription_overview(IN p_user_id VARCHAR(255))
BEGIN
    SELECT
        s.subscriptionid,
        s.plan,
        s.status,
        s.total_price,
        s.discount_percentage,
        s.start_date,
        s.end_date,
        t.status  AS trial_status,
        t.end_date AS trial_end_date
    FROM users u
    LEFT JOIN subscriptions s ON s.userid = u.userid AND s.status = 'ACTIVE'
    LEFT JOIN trial         t ON t.userid = u.userid AND t.status = 'ACTIVE'
    WHERE u.userid = p_user_id;
END$$
DELIMITER ;

-- Grant EXECUTE on procedures to API_user_account
GRANT EXECUTE ON PROCEDURE stream4.sp_get_user_by_email         TO 'API_user_account'@'%';
GRANT EXECUTE ON PROCEDURE stream4.sp_cancel_subscription       TO 'API_user_account'@'%';
GRANT EXECUTE ON PROCEDURE stream4.sp_get_subscription_overview TO 'API_user_account'@'%';


-- ---------------------------------------------------------------
-- 4. TRIGGER
--    Automatically expires a trial when a paid subscription
--    becomes active for the same user.
-- ---------------------------------------------------------------
DROP TRIGGER IF EXISTS trg_convert_trial_on_subscribe;
DELIMITER $$
CREATE TRIGGER trg_convert_trial_on_subscribe
AFTER INSERT ON subscriptions
FOR EACH ROW
BEGIN
    IF NEW.status = 'ACTIVE' THEN
        UPDATE trial
        SET status          = 'CONVERTED',
            converted_to_paid = 1,
            converted_date  = CURDATE()
        WHERE userid = NEW.userid
          AND status = 'ACTIVE';
    END IF;
END$$
DELIMITER ;


FLUSH PRIVILEGES;
