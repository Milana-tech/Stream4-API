-- =============================================================
--  StreamFlix – Missing Foreign Key Constraints
--  Run once after the backend has started and Hibernate has
--  created all tables.
-- =============================================================

USE stream4;

-- profile.user_id → users.userid
ALTER TABLE profile
    ADD CONSTRAINT fk_profile_user
    FOREIGN KEY (user_id) REFERENCES users(userid)
    ON DELETE CASCADE;

-- preferences.profile_id → profile.id
ALTER TABLE preferences
    ADD CONSTRAINT fk_preferences_profile
    FOREIGN KEY (profile_id) REFERENCES profile(id)
    ON DELETE CASCADE;

-- seasons.title_id → titles.id
ALTER TABLE seasons
    ADD CONSTRAINT fk_season_title
    FOREIGN KEY (title_id) REFERENCES titles(id)
    ON DELETE CASCADE;

-- episodes.season_id → seasons.id
ALTER TABLE episodes
    ADD CONSTRAINT fk_episode_season
    FOREIGN KEY (season_id) REFERENCES seasons(id)
    ON DELETE CASCADE;

-- watch_events.user_id → users.userid
ALTER TABLE watch_events
    ADD CONSTRAINT fk_watch_event_user
    FOREIGN KEY (user_id) REFERENCES users(userid)
    ON DELETE CASCADE;

-- watch_events.title_id → titles.id
ALTER TABLE watch_events
    ADD CONSTRAINT fk_watch_event_title
    FOREIGN KEY (title_id) REFERENCES titles(id)
    ON DELETE CASCADE;

-- watchlist.user_id → users.userid
ALTER TABLE watchlist
    ADD CONSTRAINT fk_watchlist_user
    FOREIGN KEY (user_id) REFERENCES users(userid)
    ON DELETE CASCADE;

-- watchlist.title_id → titles.id
ALTER TABLE watchlist
    ADD CONSTRAINT fk_watchlist_title
    FOREIGN KEY (title_id) REFERENCES titles(id)
    ON DELETE CASCADE;

-- invitations.inviter_user_id → users.userid
ALTER TABLE invitations
    ADD CONSTRAINT fk_invitation_inviter
    FOREIGN KEY (inviter_user_id) REFERENCES users(userid)
    ON DELETE SET NULL;

-- invitations.invitee_user_id → users.userid
ALTER TABLE invitations
    ADD CONSTRAINT fk_invitation_invitee
    FOREIGN KEY (invitee_user_id) REFERENCES users(userid)
    ON DELETE SET NULL;
