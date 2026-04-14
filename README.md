# StreamFlix API

A REST API for a streaming platform built with Spring Boot, MySQL and Docker.

---

## Requirements

- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- No Java, Maven or MySQL installation needed — everything runs inside Docker

---

## Getting Started

### 1. Clone the repository

```bash
git clone <repository-url>
cd Stream4-API
```

### 2. Create the `.env` file

Copy the provided template and fill in your values:

```bash
cp .env.example .env
```

Then edit `.env` and replace every `change_me` placeholder with a real value.

> **JWT_SECRET** — use any long random string (32+ characters).
> **MAIL_PASSWORD** — use a Gmail App Password, not your regular password.
> Generate one at: Google Account → Security → 2-Step Verification → App Passwords.

### 3. Start the containers

```bash
docker compose up -d
```

This will automatically:
- Start the MySQL database
- Start the Spring Boot backend on port `8080`
- Start phpMyAdmin on port `8081`
- Create all database tables via Hibernate
- Create internal DB users with role-based access
- Seed test data
- Apply all foreign key constraints
- Set up views, stored procedures and triggers

Wait about 15–20 seconds for everything to be ready.

### 4. Verify it is running

```bash
docker logs stream4-backend | grep "Database initialisation complete"
```

You should see: `Database initialisation complete.`

---

## Access Points

| Service | URL |
|---|---|
| API (Swagger UI) | http://localhost:8080/swagger-ui.html — live, auto-generated, authoritative |
| API Docs (OpenAPI 3.1) | http://localhost:8080/v3/api-docs |
| phpMyAdmin (DBMS) | http://localhost:8081 |
| MySQL (DataGrip / DBeaver) | localhost:3306 |

---

## API Authentication

The API uses JWT Bearer tokens.

1. Register a new account or use a test account (see below)
2. Call `POST /auth/login` with your email and password
3. Copy the token from the response
4. In Swagger UI, click **Authorize** (top right) and paste the token

---

## Test Accounts

All test account passwords are: `Test1234!`

### API Users (login via `POST /auth/login`)

| Name | Email | Password | Status |
|---|---|---|---|
| Alice Johnson | alice@stream4.com | Test1234! | Active HD subscription |
| Bob Smith | bob@stream4.com | Test1234! | 7-day trial |
| Carol Williams | carol@stream4.com | Test1234! | 7-day trial |

### phpMyAdmin — Internal Employees

Log in at `http://localhost:8081` using the DB credentials below.
Each employee only sees the data their role permits.

| Role | DB Username | DB Password | Access |
|---|---|---|---|
| Junior Employee | junior_employee | See `EMPLOYEE_JUNIOR_PASSWORD` in `.env` | View users and profiles only |
| Mid-level Employee | mid_employee | See `EMPLOYEE_MID_PASSWORD` in `.env` | View all non-financial data, edit profiles, activate/deactivate accounts |
| Senior Employee | senior_employee | See `EMPLOYEE_SENIOR_PASSWORD` in `.env` | Full access including subscriptions and viewing history |

### phpMyAdmin — API User Account

| DB Username | DB Password | Access |
|---|---|---|
| API_user_account | See `API_USER_PASSWORD` in `.env` | Views and stored procedures only — no direct table access |

### DataGrip / DBeaver Connection

| Field | Value |
|---|---|
| Host | localhost |
| Port | 3306 |
| Database | stream4 |
| User | admin (or any employee DB user) |
| Password | See `MYSQL_PASSWORD` in `.env` (or the relevant employee password) |

---

## Test Data

The following data is seeded automatically on first startup:

- **3 users** — Alice (adult, active subscription), Bob (teen, trial), Carol (child, trial)
- **4 profiles** — Alice has 2 profiles (adult + kids), Bob and Carol each have 1
- **5 titles** — Breaking Bad, The Dark Knight, Toy Story, Stranger Things, The Crown
- **2 seasons + 3 episodes** for Breaking Bad
- **Watchlist entries** and **viewing history**
- **1 invitation** sent by Alice to newuser@example.com
- **3 employees** — Junior Jan, Mid Marie, Senior Steve

---

## Subscription Plans

| Plan | Monthly Price |
|---|---|
| SD | €7.99 |
| HD | €12.99 |
| UHD | €17.99 |

New users automatically receive a **7-day free trial**.

---

## Supported Data Formats

All endpoints support JSON, XML and CSV via the `Accept` header:

```
Accept: application/json
Accept: application/xml
Accept: text/csv
```

---

## External API Integration

The API integrates with the free [TVmaze public API](https://api.tvmaze.com) to look up TV show information.

| Endpoint | Description |
|---|---|
| `GET /titles/tvmaze/search?query=breaking bad` | Search for shows by name |
| `GET /titles/tvmaze/lookup?query=breaking bad` | Get details for a single show |

---

## Database

### Internal roles

| Role | Permissions |
|---|---|
| junior_employee | SELECT on users, profiles, invitations |
| mid_employee | SELECT on all non-financial tables + UPDATE profiles + activate/deactivate accounts |
| senior_employee | Full SELECT/INSERT/UPDATE/DELETE on all tables |
| API_user_account | SELECT on views + EXECUTE on stored procedures only |

### Views
- `v_active_subscriptions` — active subscriptions with user info
- `v_user_profiles` — profiles with linked user details
- `v_title_catalogue` — all non-deleted titles
- `v_viewing_history` — watch events with user and title names

### Stored Procedures
- `sp_get_user_by_email` — look up a user by email
- `sp_cancel_subscription` — cancel a user's active subscription
- `sp_get_subscription_overview` — get subscription and trial status for a user

### Trigger
- `trg_convert_trial_on_subscribe` — automatically marks a trial as CONVERTED when a paid subscription is created

---

## Backup & Recovery

See [`db/backup-recovery.md`](db/backup-recovery.md) for the full backup and recovery protocol.

Quick backup command:

```bash
docker exec stream4-db \
  mysqldump -u root -p"$MYSQL_ROOT_PASSWORD" \
  --single-transaction --routines --triggers \
  stream4 | gzip > backups/stream4_$(date +%Y%m%d_%H%M%S).sql.gz
```

---

## Resetting Everything

To wipe all data and start fresh:

```bash
docker compose down -v
docker compose up -d
```

The `-v` flag removes the database volume. All data will be re-seeded automatically.

---

## Stopping the Application

```bash
docker compose down
```

This stops and removes the containers but keeps your data volume intact.
