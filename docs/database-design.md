# Database Design Decisions

This document explains the reasoning behind the views, stored procedures, trigger, constraints, and transaction used in the StreamFlix database.

---

## Views

Four views are created for `API_user_account`. This account has no direct access to any base table — it can only query through these views.

### `v_active_subscriptions`
Joins `subscriptions` with `users` and filters `status = 'ACTIVE'` and `deleted = 0`.

**Why:** Exposes billing-relevant data without giving access to sensitive columns such as passwords, verification tokens, or reset tokens. A reporting or analytics tool can query subscription revenue without ever touching the raw `users` table.

### `v_user_profiles`
Joins `profile` with `users`, filters out soft-deleted rows from both tables.

**Why:** Profiles are the main operational entity for customer support. The view merges profile and user details into one row so a support lookup requires one query, not a join written each time. Soft-deleted records are excluded so only live accounts are visible by default.

### `v_title_catalogue`
Selects non-deleted titles with only the public-facing columns (id, name, description, release_year, type, genre, maturity_rating).

**Why:** Hides internal columns (supported_qualities junction table, content_warnings junction table) that are irrelevant to catalogue browsing, and excludes the `deleted` flag so callers cannot see withdrawn titles.

### `v_viewing_history`

**Why:** Watch-event rows only store foreign keys. The view resolves user names and title names at query time, so a reporting query does not need to write joins manually and cannot accidentally read columns outside the view definition.

---

## Stored Procedures

Three stored procedures are granted EXECUTE to `API_user_account`. The account cannot INSERT, UPDATE, or DELETE on any table directly — all mutations go through stored procedures, which enforces the allowed operations at the database layer.

### `sp_get_user_by_email(p_email)`
Looks up a non-deleted user by email and returns only the columns needed for authentication: userid, name, email, role, verified, deleted.


### `sp_cancel_subscription(p_user_id)`
Sets `status = 'CANCELLED'` and `auto_renew = 0` on the user's active subscription.

**Why:** Allows a controlled cancel operation without granting UPDATE on the entire `subscriptions` table. A rogue or compromised API account can only cancel subscriptions, not change plan prices or discount percentages.

### `sp_get_subscription_overview(p_user_id)`
Returns a user's active subscription and active trial in one result set using LEFT JOIN.

**Why:** Combines two separate table reads into one atomic snapshot. A single call to this procedure gives the full subscription state without the caller needing to know the table structure or issue two separate queries.

---

## Trigger

### `trg_convert_trial_on_subscribe`
Fires `AFTER INSERT ON subscriptions`. If the new row has `status = 'ACTIVE'`, the trigger updates the `trial` table to set `status = 'CONVERTED'`, `converted_to_paid = 1`, and `converted_date = CURDATE()` for that user.

**Why:** The conversion of a trial to a paid subscription is a business rule, not an application concern. Enforcing it at the database layer means it fires regardless of which code path creates the subscription — whether that is the Spring service, a stored procedure call, or a manual insert during maintenance. It cannot be forgotten or bypassed by a future code change.

This duplicates the `markTrialAsConverted` call in `SubscriptionService`, which is intentional: the Spring call is the primary path; the trigger is a safety net that catches any insert that bypasses the service.

---

## Constraints

Constraints are applied at two levels:

**Entity-level (Hibernate / DDL)**
- `NOT NULL` on all mandatory columns (e.g. `status`, `plan`, `start_date`, `total_price`, `email`, `name`).
- `UNIQUE` on `users.email`, `users.name`, `employee.email`, and `role_right.role_name` — prevents duplicate accounts.
- `UNIQUE` on `trial.userid` (`@JoinColumn unique = true`) — a user can only have one trial.
- `DECIMAL(10,2)` on price columns — prevents floating-point rounding errors in financial data.

**Referential integrity (foreign keys, added by `DbEmployeeAccessInitializer`)**

| Child table | Column | Parent | On Delete |
|---|---|---|---|
| profile | user_id | users.userid | CASCADE |
| preferences | profile_id | profile.id | CASCADE |
| seasons | title_id | titles.id | CASCADE |
| episodes | season_id | seasons.id | CASCADE |
| watch_events | user_id | users.userid | CASCADE |
| watch_events | title_id | titles.id | CASCADE |
| watchlist | user_id | users.userid | CASCADE |
| watchlist | title_id | titles.id | CASCADE |
| invitations | inviter_user_id | users.userid | SET NULL |
| invitations | invitee_user_id | users.userid | SET NULL |

CASCADE delete is used where the child record has no meaning without its parent (a profile without a user, a watch event without a title). SET NULL is used for invitations because an invitation record has historical value even if the inviting or invited user is later removed.

---

## Transaction — `createSubscription`

**Location:** `SubscriptionService.createSubscription()`

**Isolation level:** `REPEATABLE_READ`

**What the transaction does (in order):**
1. Reads the user record.
2. Checks whether an active subscription already exists for this user.
3. Builds and saves the new subscription.
4. Reads the inviter's user record and active subscription (referral discount path).
5. Updates both the invitee's and inviter's subscription and user records if the discount applies.
6. Checks whether an active trial exists and marks it as CONVERTED.

**Why REPEATABLE_READ:**

The critical section is steps 2 and 3. Under `READ_COMMITTED`, a concurrent transaction running for the same user could also read "no active subscription" at step 2, then both transactions proceed to insert. The result would be two active subscriptions for the same user, bypassing the duplicate guard.

`REPEATABLE_READ` guarantees that any row read once during the transaction returns the same value if read again. The existence check and the insert therefore see a consistent snapshot, and the race condition described above cannot occur.

`SERIALIZABLE` would provide stronger guarantees (preventing phantom reads) but would place range locks on the subscription table, creating unnecessary contention for all other users trying to subscribe at the same time. The subscription operation does not involve range scans that could produce phantom rows, so the extra locking cost is not justified.

`READ_UNCOMMITTED` and `READ_COMMITTED` are both too weak for this operation.

MySQL InnoDB's default isolation level is already `REPEATABLE_READ`. Specifying it explicitly in the `@Transactional` annotation makes the intent clear and ensures the behaviour is correct regardless of any future change to the DataSource default.
