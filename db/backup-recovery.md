# StreamFlix – Backup & Recovery Protocol

## Backup Strategy

StreamFlix uses a **daily full dump** approach. The MySQL database is exported
to a compressed SQL file and stored outside the Docker volume.

### Create a backup

```bash
docker exec stream4-db \
  mysqldump -u root -p"$MYSQL_ROOT_PASSWORD" \
  --single-transaction --routines --triggers \
  stream4 | gzip > backups/stream4_$(date +%Y%m%d_%H%M%S).sql.gz
```

- `--single-transaction` ensures a consistent snapshot without locking tables.
- `--routines` includes stored procedures.
- `--triggers` includes triggers.

Run this daily via a scheduled task (Windows Task Scheduler or cron on Linux).

### Backup retention

| Backup type | Keep for |
|---|---|
| Daily dumps | 7 days |
| Weekly dumps (Sunday) | 4 weeks |
| Monthly dumps (1st of month) | 6 months |

---

## Recovery

### Full restore from a backup file

```bash
# 1. Stop the backend so no writes occur during restore
docker stop stream4-backend

# 2. Drop and recreate the database
docker exec stream4-db mysql -u root -p"$MYSQL_ROOT_PASSWORD" \
  -e "DROP DATABASE IF EXISTS stream4; CREATE DATABASE stream4;"

# 3. Restore the backup
gunzip -c backups/stream4_YYYYMMDD_HHMMSS.sql.gz | \
  docker exec -i stream4-db mysql -u root -p"$MYSQL_ROOT_PASSWORD" stream4

# 4. Re-apply employee grants (stored procedures/views are in the dump)
docker cp db/grants.sql stream4-db:/tmp/grants.sql
docker exec stream4-db bash -c "mysql -u root -p\$MYSQL_ROOT_PASSWORD < /tmp/grants.sql"

# 5. Restart the backend
docker start stream4-backend
```

### Point-in-time recovery

MySQL binary logging is not enabled in the current Docker setup. To enable it,
add the following to the `db` service in `docker-compose.yml`:

```yaml
command: --log-bin=mysql-bin --binlog-format=ROW --expire-logs-days=7
```

With binary logging enabled, you can replay events between the last full backup
and the point of failure using `mysqlbinlog`.

---

## Verification

After a restore, verify data integrity:

```bash
docker exec stream4-db mysql -u root -p"$MYSQL_ROOT_PASSWORD" stream4 \
  -e "SELECT COUNT(*) AS users FROM users;
      SELECT COUNT(*) AS profiles FROM profile;
      SELECT COUNT(*) AS subscriptions FROM subscriptions;
      SELECT COUNT(*) AS titles FROM titles;"
```

---

## Preventing Database Downtime

### Automatic container restart

Both the `db` and `backend` containers are configured with `restart: unless-stopped` in `docker-compose.yml`. If the MySQL process crashes or the host machine reboots, Docker automatically restarts the container without manual intervention.

### Health check and dependency ordering

The `db` service has a health check that pings MySQL every 10 seconds. The `backend` and `phpmyadmin` containers declare `depends_on: condition: service_healthy`, so they only start once MySQL is accepting connections. This prevents "connection refused" errors during startup and after a DB restart.

### Lock-free backups

The backup command uses `--single-transaction`, which starts a consistent snapshot using InnoDB's MVCC without acquiring table locks. The database remains fully available for reads and writes while the dump runs. Without this flag, `mysqldump` would lock every table for the duration of the backup, causing downtime proportional to database size.

### Connection pool resilience

The Spring Boot backend uses HikariCP (the default connection pool). HikariCP detects broken connections and replaces them automatically. If the DB container restarts briefly (e.g. after a crash and auto-recovery), the backend reconnects within seconds without needing a restart itself.

### Scheduled backups without stopping the service

Never stop the backend or the database to take a backup. The `--single-transaction` flag makes that unnecessary. The recovery procedure does require stopping the backend (to prevent writes during restore), but the backup itself does not.

### Production note

For a production deployment, the above measures reduce but do not eliminate downtime risk. True high availability would require a MySQL replication setup (one primary, one or more read replicas) with automatic failover. In that model, the replica is promoted to primary if the primary becomes unavailable, keeping the database accessible. This is outside the scope of the current Docker-based setup but is the recommended next step before going to production.

---

## Automated backup script

Save as `db/backup.sh` and schedule it:

```bash
#!/bin/bash
mkdir -p backups
docker exec stream4-db \
  mysqldump -u root -p"${MYSQL_ROOT_PASSWORD}" \
  --single-transaction --routines --triggers \
  stream4 | gzip > backups/stream4_$(date +%Y%m%d_%H%M%S).sql.gz

# Remove backups older than 7 days
find backups/ -name "*.sql.gz" -mtime +7 -delete
echo "Backup completed: $(date)"
```
