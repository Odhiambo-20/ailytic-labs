#!/usr/bin/env python3
"""Restartable one-time DynamoDB to PostgreSQL migration."""

import argparse
import json
import logging
import re
from decimal import Decimal

import boto3
import psycopg
from psycopg import sql

LOG = logging.getLogger("dynamodb-to-postgres")
TABLES = [
    ("Users", "app_users", "user_id"),
    ("Robots", "robots", "id"),
    ("Drones", "drones", "id"),
    ("SolarPanels", "solar_panels", "id"),
    ("Contacts", "contacts", "id"),
    ("Newsletters", "newsletter_subscriptions", "email"),
    ("payments", "payments", "id"),
    ("payment_transactions", "payment_transactions", "id"),
    ("mpesa_payments", "mpesa_payments", "id"),
    ("stripe_payments", "stripe_payments", "id"),
    ("qr_payments", "qr_payments", "id"),
    ("WebhookLogs", "webhook_logs", "webhook_id"),
]
DEFAULTS = {
    "app_users": {"enabled": True, "email_verified": False, "roles": []},
    "payments": {"status": "PENDING", "payment_method": "CARD", "amount": 0.0, "currency": "USD"},
    "payment_transactions": {"status": "PENDING", "method": "CARD", "amount": 0.0},
    "qr_payments": {"status": "PENDING", "amount": 0.0, "scan_count": 0},
}


def snake_case(name):
    return re.sub(r"(?<!^)(?=[A-Z])", "_", name).lower()


def normalize(value):
    if isinstance(value, Decimal):
        return int(value) if value == value.to_integral_value() else float(value)
    if isinstance(value, set):
        return json.dumps(sorted(normalize(item) for item in value))
    if isinstance(value, list):
        return json.dumps([normalize(item) for item in value])
    if isinstance(value, dict):
        return json.dumps({key: normalize(item) for key, item in value.items()})
    return value


def destination_columns(connection, table):
    rows = connection.execute(
        "SELECT column_name FROM information_schema.columns WHERE table_schema=public AND table_name=%s",
        (table,),
    ).fetchall()
    return {row[0] for row in rows}


def scan_all(dynamodb, table_name):
    table = dynamodb.Table(table_name)
    arguments = {}
    while True:
        response = table.scan(**arguments)
        yield from response.get("Items", [])
        key = response.get("LastEvaluatedKey")
        if not key:
            return
        arguments["ExclusiveStartKey"] = key


def migrate_table(connection, dynamodb, source, destination, primary_key, dry_run):
    allowed = destination_columns(connection, destination)
    if not allowed:
        raise RuntimeError(f"Destination table {destination} does not exist")

    migrated = 0
    for source_item in scan_all(dynamodb, source):
        item = {snake_case(key): normalize(value) for key, value in source_item.items()}
        for key, value in DEFAULTS.get(destination, {}).items():
            item.setdefault(key, normalize(value))
        item = {key: value for key, value in item.items() if key in allowed}
        if primary_key not in item:
            raise RuntimeError(f"{source} item is missing primary key {primary_key}: {source_item}")

        columns = list(item)
        update_columns = [column for column in columns if column != primary_key]
        updates = sql.SQL(", ").join(
            sql.SQL("{} = EXCLUDED.{}").format(sql.Identifier(column), sql.Identifier(column))
            for column in update_columns
        )
        statement = sql.SQL("INSERT INTO {} ({}) VALUES ({}) ON CONFLICT ({}) DO UPDATE SET {}").format(
            sql.Identifier(destination),
            sql.SQL(", ").join(map(sql.Identifier, columns)),
            sql.SQL(", ").join(sql.Placeholder() for _ in columns),
            sql.Identifier(primary_key),
            updates,
        )
        if not dry_run:
            connection.execute(statement, [item[column] for column in columns])
        migrated += 1

    LOG.info("%s -> %s: %d records", source, destination, migrated)
    return migrated


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--database-url", required=True)
    parser.add_argument("--aws-region", required=True)
    parser.add_argument("--dry-run", action="store_true")
    args = parser.parse_args()
    logging.basicConfig(level=logging.INFO, format="%(asctime)s %(levelname)s %(message)s")
    dynamodb = boto3.resource("dynamodb", region_name=args.aws_region)

    with psycopg.connect(args.database_url) as connection:
        total = 0
        for source, destination, primary_key in TABLES:
            total += migrate_table(connection, dynamodb, source, destination, primary_key, args.dry_run)
        connection.rollback() if args.dry_run else connection.commit()
        LOG.info("Migration complete: %d records", total)


if __name__ == "__main__":
    main()
