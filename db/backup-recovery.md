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
