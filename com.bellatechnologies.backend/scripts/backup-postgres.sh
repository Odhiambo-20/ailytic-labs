#!/usr/bin/env bash
set -euo pipefail

: "${DATABASE_HOST:?DATABASE_HOST is required}"
: "${DATABASE_NAME:?DATABASE_NAME is required}"
: "${DATABASE_USERNAME:?DATABASE_USERNAME is required}"
: "${PGPASSWORD:?PGPASSWORD is required}"
: "${AZURE_STORAGE_ACCOUNT:?AZURE_STORAGE_ACCOUNT is required}"
: "${AZURE_BACKUP_CONTAINER:?AZURE_BACKUP_CONTAINER is required}"
: "${AWS_BACKUP_BUCKET:?AWS_BACKUP_BUCKET is required}"

backup_timestamp="$(date -u +%Y%m%dT%H%M%SZ)"
backup_file="bella-${backup_timestamp}.dump"
checksum_file="${backup_file}.sha256"
backup_dir="$(mktemp -d)"
cleanup() {
  rm -rf "${backup_dir}"
}
trap cleanup EXIT

pg_dump --format=custom --compress=9 --no-owner --no-acl   --host="${DATABASE_HOST}" --username="${DATABASE_USERNAME}"   --dbname="${DATABASE_NAME}" --file="${backup_dir}/${backup_file}"
(
  cd "${backup_dir}"
  sha256sum "${backup_file}" > "${checksum_file}"
)

az storage blob upload --auth-mode login --overwrite   --account-name "${AZURE_STORAGE_ACCOUNT}"   --container-name "${AZURE_BACKUP_CONTAINER}"   --name "${backup_file}" --file "${backup_dir}/${backup_file}"
az storage blob upload --auth-mode login --overwrite   --account-name "${AZURE_STORAGE_ACCOUNT}"   --container-name "${AZURE_BACKUP_CONTAINER}"   --name "${checksum_file}" --file "${backup_dir}/${checksum_file}"

aws s3 cp "${backup_dir}/${backup_file}" "s3://${AWS_BACKUP_BUCKET}/postgres/${backup_file}"
aws s3 cp "${backup_dir}/${checksum_file}" "s3://${AWS_BACKUP_BUCKET}/postgres/${checksum_file}"
