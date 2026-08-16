#!/usr/bin/env bash
set -euo pipefail

: "${BACKUP_FILE:?BACKUP_FILE is required}"
: "${DATABASE_HOST:?DATABASE_HOST is required}"
: "${DATABASE_NAME:?DATABASE_NAME is required}"
: "${DATABASE_USERNAME:?DATABASE_USERNAME is required}"
: "${PGPASSWORD:?PGPASSWORD is required}"

sha256sum --check "${BACKUP_FILE}.sha256"
pg_restore --clean --if-exists --no-owner --no-acl   --host="${DATABASE_HOST}" --username="${DATABASE_USERNAME}"   --dbname="${DATABASE_NAME}" "${BACKUP_FILE}"
