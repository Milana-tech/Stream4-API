-- =============================================================
--  StreamFlix – Test Data
--  All passwords are: Test1234!
--  Run once after FK constraints have been applied.
-- =============================================================

USE stream4;

-- ---------------------------------------------------------------
-- Users  (password = Test1234!)
-- ---------------------------------------------------------------
INSERT IGNORE INTO users (userid, name, email, password, age, role, verified, deleted,
                          failed_login_attempts, referral_discount_used, created_date)
VALUES
    ('user-001', 'Alice Johnson',  'alice@stream4.com',  '$2a$10$KMvvCJ1vLWKFJtz4B0kUzubUPDaFM6wTwk93cBwMYRRKx.3//W7v2', 30, 'USER', 1, 0, 0, 0, NOW()),
    ('user-002', 'Bob Smith',      'bob@stream4.com',    '$2a$10$KMvvCJ1vLWKFJtz4B0kUzubUPDaFM6wTwk93cBwMYRRKx.3//W7v2', 17, 'USER', 1, 0, 0, 0, NOW()),
    ('user-003', 'Carol Williams', 'carol@stream4.com',  '$2a$10$KMvvCJ1vLWKFJtz4B0kUzubUPDaFM6wTwk93cBwMYRRKx.3//W7v2', 10, 'USER', 1, 0, 0, 0, NOW());

-- ---------------------------------------------------------------
-- Profiles
-- ---------------------------------------------------------------
INSERT IGNORE INTO profile (id, user_id, name, avatar, age, maturity_level, deleted)
VALUES
    ('prof-001', 'user-001', 'Alice Main',   'avatar1.png', 30, 'ADULT', 0),
    ('prof-002', 'user-001', 'Alice Kids',   'avatar2.png',  8, 'KIDS',  0),
    ('prof-003', 'user-002', 'Bob Profile',  'avatar3.png', 17, 'TEENS', 0),
    ('prof-004', 'user-003', 'Carol Profile','avatar4.png', 10, 'KIDS',  0);

-- ---------------------------------------------------------------
-- Preferences
-- ---------------------------------------------------------------
INSERT IGNORE INTO preferences (id, profile_id, preferred_type, minimum_maturity_rating)
VALUES
    ('pref-001', 'prof-001', 'SERIES', 'PG'),
    ('pref-002', 'prof-003', 'MOVIE',  'ALL');

-- ---------------------------------------------------------------
-- Trials
-- ---------------------------------------------------------------
INSERT IGNORE INTO trial (userid, start_date, end_date, status, converted_to_paid)
VALUES
    ('user-002', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'ACTIVE',    0),
    ('user-003', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'ACTIVE',    0);

-- ---------------------------------------------------------------
-- Subscriptions
-- ---------------------------------------------------------------
INSERT IGNORE INTO subscriptions (userid, plan, status, total_price, discount_percentage,
                                  start_date, end_date, auto_renew,
                                  invite_discount_applied, invite_discount_used)
VALUES
    ('user-001', 'HD',  'ACTIVE', 12.99, 0.00, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 MONTH), 1, 0, 0);

-- ---------------------------------------------------------------
-- Titles
-- ---------------------------------------------------------------
INSERT IGNORE INTO titles (id, name, description, release_year, type, genre, maturity_rating, deleted)
VALUES
    ('title-001', 'Breaking Bad',   'A chemistry teacher turned drug lord.',         2008, 'SERIES', 'DRAMA',   'MATURE', 0),
    ('title-002', 'The Dark Knight','Batman faces the Joker in Gotham City.',        2008, 'MOVIE',  'ACTION',  'TEEN',   0),
    ('title-003', 'Toy Story',      'Toys come to life when humans are not looking.',1995, 'MOVIE',  'ANIMATION','ALL',   0),
    ('title-004', 'Stranger Things','Kids encounter supernatural forces.',            2016, 'SERIES', 'HORROR',  'TEEN',   0),
    ('title-005', 'The Crown',      'The reign of Queen Elizabeth II.',              2016, 'SERIES', 'DRAMA',   'PG',     0);

-- Title content warnings
INSERT IGNORE INTO title_content_warnings (title_id, warning)
VALUES
    ('title-001', 'VIOLENCE'),
    ('title-001', 'DRUG_USE'),
    ('title-002', 'VIOLENCE'),
    ('title-004', 'FEAR'),
    ('title-004', 'VIOLENCE');

-- Title supported qualities
INSERT IGNORE INTO title_supported_qualities (title_id, supported_qualities)
VALUES
    ('title-001', 'HD'),
    ('title-001', 'UHD'),
    ('title-002', 'SD'),
    ('title-002', 'HD'),
    ('title-002', 'UHD'),
    ('title-003', 'SD'),
    ('title-003', 'HD'),
    ('title-004', 'HD'),
    ('title-004', 'UHD'),
    ('title-005', 'SD'),
    ('title-005', 'HD');

-- ---------------------------------------------------------------
-- Seasons & Episodes (Breaking Bad)
-- ---------------------------------------------------------------
INSERT IGNORE INTO seasons (id, title_id, season_number, deleted)
VALUES
    ('season-001', 'title-001', 1, 0),
    ('season-002', 'title-001', 2, 0);

INSERT IGNORE INTO episodes (id, season_id, episode_number, name, description, deleted)
VALUES
    ('ep-001', 'season-001', 1, 'Pilot',            'Walter White begins his transformation.', 0),
    ('ep-002', 'season-001', 2, 'Cat''s in the Bag','Walt and Jesse deal with consequences.',   0),
    ('ep-003', 'season-002', 1, 'Seven Thirty-Seven','Walt receives a warning.',                0);

-- ---------------------------------------------------------------
-- Watchlist
-- ---------------------------------------------------------------
INSERT IGNORE INTO watchlist (id, user_id, title_id, added_at)
VALUES
    ('wl-001', 'user-001', 'title-004', UNIX_TIMESTAMP()),
    ('wl-002', 'user-001', 'title-005', UNIX_TIMESTAMP()),
    ('wl-003', 'user-002', 'title-003', UNIX_TIMESTAMP());

-- ---------------------------------------------------------------
-- Watch Events (viewing history)
-- ---------------------------------------------------------------
INSERT IGNORE INTO watch_events (id, user_id, title_id, progress_seconds, finished, last_updated)
VALUES
    ('we-001', 'user-001', 'title-001', 2700,  0, UNIX_TIMESTAMP()),
    ('we-002', 'user-001', 'title-002', 9120,  1, UNIX_TIMESTAMP()),
    ('we-003', 'user-002', 'title-003', 4800,  1, UNIX_TIMESTAMP());

-- ---------------------------------------------------------------
-- Invitation
-- ---------------------------------------------------------------
INSERT IGNORE INTO invitations (id, inviter_user_id, invitee_email, token, used, discount_applied)
VALUES
    ('inv-001', 'user-001', 'newuser@example.com', 'invite-token-abc123', 0, 0);

-- ---------------------------------------------------------------
-- Role rights & Employees
-- ---------------------------------------------------------------
INSERT IGNORE INTO role_right (roleid, role_name, description, permissions)
VALUES
    (1, 'JUNIOR_EMPLOYEE',  'Read-only access to basic user and profile data',         'READ_USERS,READ_PROFILES'),
    (2, 'MID_EMPLOYEE',     'Read access + limited write on profiles and accounts',    'READ_USERS,READ_PROFILES,UPDATE_PROFILES,DEACTIVATE_ACCOUNTS'),
    (3, 'SENIOR_EMPLOYEE',  'Full access including financial and viewing history data', 'ALL');

INSERT IGNORE INTO employee (employeeid, name, email, password, roleid, is_active)
VALUES
    (1, 'Junior Jan',   'junior@stream4.com',  '$2a$10$KMvvCJ1vLWKFJtz4B0kUzubUPDaFM6wTwk93cBwMYRRKx.3//W7v2', 1, 1),
    (2, 'Mid Marie',    'mid@stream4.com',     '$2a$10$KMvvCJ1vLWKFJtz4B0kUzubUPDaFM6wTwk93cBwMYRRKx.3//W7v2', 2, 1),
    (3, 'Senior Steve', 'senior@stream4.com',  '$2a$10$KMvvCJ1vLWKFJtz4B0kUzubUPDaFM6wTwk93cBwMYRRKx.3//W7v2', 3, 1);
