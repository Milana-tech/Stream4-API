-- =============================================================
--  StreamFlix – Transaction Examples
--
--  This file demonstrates full database transactions used in
--  the StreamFlix application, including explicit isolation
--  levels and justification for each choice.
-- =============================================================

USE stream4;


-- =============================================================
--  TRANSACTION 1: Create Subscription
--
--  Isolation level: REPEATABLE READ (MySQL default, made explicit)
--
--  Justification:
--    This transaction reads the user row (to check it exists),
--    checks that no active subscription already exists, and then
--    inserts a new subscription row. If two concurrent requests
--    for the same user both pass the "no active subscription"
--    check before either has committed, a duplicate subscription
--    would be created (phantom read / lost-update problem).
--
--    REPEATABLE READ prevents this: once the transaction has
--    read the subscription rows for a user, any concurrent
--    INSERT by another transaction is not visible until after
--    commit, and the unique constraint on (userid, status=ACTIVE)
--    acts as a final safety net at the database level.
--
--    READ COMMITTED would leave the window open for concurrent
--    duplicates; SERIALIZABLE would be unnecessarily strict and
--    hurt throughput on a high-traffic subscriptions table.
-- =============================================================

SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;
START TRANSACTION;

    -- Step 1: Verify the user exists and is active
    SELECT userid, name, email
    FROM users
    WHERE userid = 'user-001'
      AND deleted = 0
    FOR UPDATE;                          -- lock the row to block concurrent changes

    -- Step 2: Ensure no active subscription already exists
    SELECT COUNT(*) AS active_count
    FROM subscriptions
    WHERE userid = 'user-001'
      AND status = 'ACTIVE';
    -- Application checks active_count = 0 before proceeding.
    -- If active_count > 0, the transaction is rolled back.

    -- Step 3: Insert the new subscription
    INSERT INTO subscriptions (
        userid, plan, status, total_price, discount_percentage,
        start_date, end_date, auto_renew,
        invite_discount_applied, invite_discount_used
    ) VALUES (
        'user-001', 'HD', 'ACTIVE', 12.99, 0.00,
        CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 MONTH), 1,
        0, 0
    );

    -- Step 4: If the user had an active trial, convert it
    UPDATE trial
    SET status           = 'CONVERTED',
        converted_to_paid = 1
    WHERE userid = 'user-001'
      AND status = 'ACTIVE';

COMMIT;


-- =============================================================
--  TRANSACTION 2: Reset Password
--
--  Isolation level: READ COMMITTED
--
--  Justification:
--    A password reset only needs to write to a single row
--    (the user record). It does not need to re-read any row
--    after the initial lookup, so repeatable reads add no
--    value here. READ COMMITTED is sufficient: it prevents
--    dirty reads (we never see another transaction's
--    uncommitted password changes) while avoiding unnecessary
--    row-level locking that could block concurrent logins
--    on a busy users table.
-- =============================================================

SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
START TRANSACTION;

    -- Step 1: Look up the user by their reset token
    SELECT userid, reset_token, reset_token_expiry
    FROM users
    WHERE reset_token = 'valid-reset-token-abc123'
      AND reset_token_expiry > NOW()
    FOR UPDATE;
    -- If no row is returned, the token is invalid or expired.
    -- The application rolls back and returns an error.

    -- Step 2: Apply the new password and clear the token
    UPDATE users
    SET password            = '$2a$10$newHashedPasswordHere',
        reset_token         = NULL,
        reset_token_expiry  = NULL,
        failed_login_attempts = 0,
        locked_until        = NULL
    WHERE reset_token = 'valid-reset-token-abc123';

COMMIT;


-- =============================================================
--  TRANSACTION 3: Cancel Subscription (with rollback example)
--
--  Isolation level: READ COMMITTED
--
--  Justification:
--    Cancellation reads the current subscription status once and
--    updates it. No phantom-read risk exists because we are
--    modifying a specific, already-identified row. READ COMMITTED
--    prevents dirty reads at minimal cost. Including an explicit
--    ROLLBACK path demonstrates correct error handling — if the
--    subscription is not found or already cancelled, the
--    transaction is aborted cleanly without partial writes.
-- =============================================================

SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
START TRANSACTION;

    -- Step 1: Check that an active subscription exists
    SELECT subscriptionid
    FROM subscriptions
    WHERE userid = 'user-001'
      AND status = 'ACTIVE'
    FOR UPDATE;
    -- If no row is returned, execute ROLLBACK and return 404.

    -- Step 2: Cancel the subscription
    UPDATE subscriptions
    SET status     = 'CANCELLED',
        auto_renew = 0
    WHERE userid = 'user-001'
      AND status  = 'ACTIVE';

    -- If @@ROWCOUNT = 0 (concurrent cancellation won the race),
    -- execute ROLLBACK.

COMMIT;

-- Example of the rollback path:
-- ROLLBACK;


-- =============================================================
--  SUMMARY: Isolation level choices
--
--  REPEATABLE READ  — used when a transaction reads data and
--    then makes a decision based on that data before writing.
--    Prevents phantom reads that could cause duplicate records
--    (e.g. double subscriptions).
--
--  READ COMMITTED   — used for simple single-row updates where
--    the transaction does not need to re-read rows mid-flight.
--    Avoids dirty reads with lower locking overhead.
--
--  READ UNCOMMITTED - NOT used. Dirty reads are unacceptable
--    in a financial context (subscriptions, pricing).
--
--  SERIALIZABLE     — NOT used for routine operations. Full
--    table locking hurts throughput without benefit for the
--    access patterns in this application. Reserved for
--    bulk administrative operations if ever needed.
-- =============================================================
