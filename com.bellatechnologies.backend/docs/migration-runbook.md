# DynamoDB to PostgreSQL migration

1. Deploy an empty staging PostgreSQL server and run Flyway.
2. Export every DynamoDB table using a consistent point-in-time export.
3. Run the migration utility against staging.
4. Compare item counts, identifiers, totals, payment statuses, and checksums.
5. Run application integration and payment reconciliation tests.
6. Schedule a production maintenance window.
7. Disable writes to the DynamoDB-backed application.
8. Export and migrate the final delta.
9. Re-run reconciliation and retain the export immutably.
10. deploy the PostgreSQL application with database credentials from Key Vault.
11. Run smoke tests before enabling public traffic.
12. Keep the former AWS deployment read-only during the rollback window.

Rollback means routing traffic to the former deployment before any new PostgreSQL-only writes are accepted. After PostgreSQL writes begin, rollback requires reverse reconciliation and explicit approval.
