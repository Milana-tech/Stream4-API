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
INSERT IGNORE INTO titles (id, name, description, release_year, duration_seconds, type, genre, maturity_rating, deleted)
VALUES
    ('title-001', 'Breaking Bad',   'A chemistry teacher turned drug lord.',         2008, 2700,  'SERIES', 'DRAMA',           'MATURE', 0),
    ('title-002', 'The Dark Knight','Batman faces the Joker in Gotham City.',        2008, 9180,  'MOVIE',  'ACTION',          'TEEN',   0),
    ('title-003', 'Toy Story',      'Toys come to life when humans are not looking.',1995, 4920,  'MOVIE',  'ANIMATION',       'ALL',    0),
    ('title-004', 'Stranger Things','Kids encounter supernatural forces.',            2016, 3000,  'SERIES', 'HORROR',          'TEEN',   0),
    ('title-005', 'The Crown',      'The reign of Queen Elizabeth II.',              2016, 3600,  'SERIES', 'DRAMA',           'PG',     0),
    -- Movies
    ('title-006', 'Inception',         'A thief who enters people''s dreams to steal secrets.',       2010, 8880,  'MOVIE',  'THRILLER',        'TEEN',   0),
    ('title-007', 'Interstellar',      'Astronauts travel through a wormhole to save humanity.',      2014, 10140, 'MOVIE',  'SCIENCE_FICTION', 'PG',     0),
    ('title-008', 'The Lion King',     'A lion cub must claim his rightful place as king.',           1994, 5280,  'MOVIE',  'ANIMATION',       'ALL',    0),
    ('title-009', 'The Shining',       'A writer becomes dangerously unstable at a remote hotel.',   1980, 8640,  'MOVIE',  'HORROR',          'MATURE', 0),
    ('title-010', 'Pulp Fiction',      'Interconnected crime stories in Los Angeles.',                1994, 9360,  'MOVIE',  'CRIME',           'MATURE', 0),
    -- Series
    ('title-011', 'Game of Thrones',   'Noble families war for control of the Iron Throne.',         2011, 3600,  'SERIES', 'FANTASY',         'MATURE', 0),
    ('title-012', 'Friends',           'Six friends navigate life and love in New York City.',        1994, 1320,  'SERIES', 'COMEDY',          'PG',     0),
    ('title-013', 'Black Mirror',      'Anthology series exploring dark sides of technology.',        2011, 3600,  'SERIES', 'THRILLER',        'MATURE', 0),
    ('title-014', 'Money Heist',       'A criminal mastermind plans an elaborate bank robbery.',      2017, 2700,  'SERIES', 'CRIME',           'MATURE', 0),
    ('title-015', 'The Witcher',       'A monster hunter struggles to find his place in the world.', 2019, 3600,  'SERIES', 'FANTASY',         'MATURE', 0);

-- Title content warnings
INSERT IGNORE INTO title_content_warnings (title_id, warning)
VALUES
    ('title-001', 'VIOLENCE'),
    ('title-001', 'DRUG_USE'),
    ('title-002', 'VIOLENCE'),
    ('title-004', 'FEAR'),
    ('title-004', 'VIOLENCE'),
    ('title-009', 'FEAR'),
    ('title-009', 'VIOLENCE'),
    ('title-010', 'VIOLENCE'),
    ('title-010', 'COARSE_LANGUAGE'),
    ('title-011', 'VIOLENCE'),
    ('title-011', 'SEXUAL_CONTENT'),
    ('title-013', 'VIOLENCE'),
    ('title-013', 'FEAR'),
    ('title-014', 'VIOLENCE'),
    ('title-015', 'VIOLENCE');

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
    ('title-005', 'HD'),
    ('title-006', 'HD'),
    ('title-006', 'UHD'),
    ('title-007', 'HD'),
    ('title-007', 'UHD'),
    ('title-008', 'SD'),
    ('title-008', 'HD'),
    ('title-009', 'SD'),
    ('title-009', 'HD'),
    ('title-010', 'SD'),
    ('title-010', 'HD'),
    ('title-011', 'HD'),
    ('title-011', 'UHD'),
    ('title-012', 'SD'),
    ('title-012', 'HD'),
    ('title-013', 'HD'),
    ('title-013', 'UHD'),
    ('title-014', 'HD'),
    ('title-014', 'UHD'),
    ('title-015', 'HD'),
    ('title-015', 'UHD');

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

-- Seasons & Episodes (Stranger Things)
INSERT IGNORE INTO seasons (id, title_id, season_number, deleted)
VALUES
    ('season-003', 'title-004', 1, 0),
    ('season-004', 'title-004', 2, 0);

INSERT IGNORE INTO episodes (id, season_id, episode_number, name, description, deleted)
VALUES
    ('ep-st-101', 'season-003', 1, 'The Vanishing of Will Byers', 'A boy goes missing in Hawkins, Indiana.', 0),
    ('ep-st-102', 'season-003', 2, 'The Weirdo on Maple Street',  'A strange girl is found in the woods.', 0),
    ('ep-st-103', 'season-003', 3, 'Holly, Jolly',                'Joyce receives a strange message from Will.', 0),
    ('ep-st-201', 'season-004', 1, 'MADMAX',                      'A new player arrives in Hawkins.', 0),
    ('ep-st-202', 'season-004', 2, 'Trick or Treat, Freak',       'Will struggles to readjust to normal life.', 0);

-- Seasons & Episodes (The Crown)
INSERT IGNORE INTO seasons (id, title_id, season_number, deleted)
VALUES
    ('season-005', 'title-005', 1, 0),
    ('season-006', 'title-005', 2, 0);

INSERT IGNORE INTO episodes (id, season_id, episode_number, name, description, deleted)
VALUES
    ('ep-cr-101', 'season-005', 1, 'Wolferton Splash', 'Elizabeth and Philip begin married life.', 0),
    ('ep-cr-102', 'season-005', 2, 'Hyde Park Corner', 'The King''s health declines rapidly.', 0),
    ('ep-cr-103', 'season-005', 3, 'Windsor',           'Elizabeth faces her first crisis as Queen.', 0),
    ('ep-cr-201', 'season-006', 1, 'Misadventure',      'The fallout of a royal scandal.', 0),
    ('ep-cr-202', 'season-006', 2, 'A Company of Men',  'Philip embarks on a world tour.', 0);

-- Seasons & Episodes (Game of Thrones)
INSERT IGNORE INTO seasons (id, title_id, season_number, deleted)
VALUES
    ('season-007', 'title-011', 1, 0),
    ('season-008', 'title-011', 2, 0);

INSERT IGNORE INTO episodes (id, season_id, episode_number, name, description, deleted)
VALUES
    ('ep-got-101', 'season-007', 1, 'Winter Is Coming',       'The Stark family faces a dangerous world.', 0),
    ('ep-got-102', 'season-007', 2, 'The Kingsroad',          'Ned Stark travels to King''s Landing.', 0),
    ('ep-got-103', 'season-007', 3, 'Lord Snow',              'Jon Snow arrives at Castle Black.', 0),
    ('ep-got-201', 'season-008', 1, 'The North Remembers',    'Joffrey celebrates his name day.', 0),
    ('ep-got-202', 'season-008', 2, 'The Night Lands',        'Tyrion asserts himself as Hand of the King.', 0);

-- Seasons & Episodes (Friends)
INSERT IGNORE INTO seasons (id, title_id, season_number, deleted)
VALUES
    ('season-009', 'title-012', 1, 0),
    ('season-010', 'title-012', 2, 0);

INSERT IGNORE INTO episodes (id, season_id, episode_number, name, description, deleted)
VALUES
    ('ep-fr-101', 'season-009', 1, 'The One Where Monica Gets a Roommate', 'Rachel joins the group after leaving her fiancé.', 0),
    ('ep-fr-102', 'season-009', 2, 'The One with the Sonogram at the End',  'Ross learns his ex-wife is pregnant.', 0),
    ('ep-fr-103', 'season-009', 3, 'The One with the Thumb',                'Monica gets a new boyfriend.', 0),
    ('ep-fr-201', 'season-010', 1, 'The One with Ross''s New Girlfriend',   'Ross returns from China with a girlfriend.', 0),
    ('ep-fr-202', 'season-010', 2, 'The One with the Breast Milk',          'The group meets Ross''s new girlfriend.', 0);

-- Seasons & Episodes (Black Mirror)
INSERT IGNORE INTO seasons (id, title_id, season_number, deleted)
VALUES
    ('season-011', 'title-013', 1, 0),
    ('season-012', 'title-013', 2, 0);

INSERT IGNORE INTO episodes (id, season_id, episode_number, name, description, deleted)
VALUES
    ('ep-bm-101', 'season-011', 1, 'The National Anthem',  'A member of the Royal Family is kidnapped.', 0),
    ('ep-bm-102', 'season-011', 2, 'Fifteen Million Merits','A man tries to get his friend on a talent show.', 0),
    ('ep-bm-103', 'season-011', 3, 'The Entire History of You', 'Everyone has a device that records all memories.', 0),
    ('ep-bm-201', 'season-012', 1, 'Be Right Back',        'A woman uses technology to reconnect with her dead partner.', 0),
    ('ep-bm-202', 'season-012', 2, 'White Bear',           'A woman wakes with no memory in a strange world.', 0);

-- Seasons & Episodes (Money Heist)
INSERT IGNORE INTO seasons (id, title_id, season_number, deleted)
VALUES
    ('season-013', 'title-014', 1, 0),
    ('season-014', 'title-014', 2, 0);

INSERT IGNORE INTO episodes (id, season_id, episode_number, name, description, deleted)
VALUES
    ('ep-mh-101', 'season-013', 1, 'Efectuar lo acordado',     'The Professor assembles a team of criminals.', 0),
    ('ep-mh-102', 'season-013', 2, 'Camina o muere',           'The heist on the Royal Mint begins.', 0),
    ('ep-mh-103', 'season-013', 3, 'El tiempo entre costuras', 'Tensions rise inside the Mint.', 0),
    ('ep-mh-201', 'season-014', 1, 'El professor',             'The Professor races to save the operation.', 0),
    ('ep-mh-202', 'season-014', 2, 'Toulouse',                 'A member of the gang breaks the rules.', 0);

-- Seasons & Episodes (The Witcher)
INSERT IGNORE INTO seasons (id, title_id, season_number, deleted)
VALUES
    ('season-015', 'title-015', 1, 0),
    ('season-016', 'title-015', 2, 0);

INSERT IGNORE INTO episodes (id, season_id, episode_number, name, description, deleted)
VALUES
    ('ep-wi-101', 'season-015', 1, 'The End''s Beginning',     'Geralt of Rivia encounters a cursed princess.', 0),
    ('ep-wi-102', 'season-015', 2, 'Four Marks',               'Yennefer discovers her magical potential.', 0),
    ('ep-wi-103', 'season-015', 3, 'Betrayer Moon',            'Geralt investigates a beast terrorizing a kingdom.', 0),
    ('ep-wi-201', 'season-016', 1, 'A Grain of Truth',         'Geralt reunites with Ciri.', 0),
    ('ep-wi-202', 'season-016', 2, 'Kaer Morhen',              'Geralt brings Ciri to his childhood home.', 0);

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
