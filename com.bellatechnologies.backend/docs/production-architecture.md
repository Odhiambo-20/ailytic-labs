# Production architecture

## Runtime

Bella Technologies runs as a modular Spring Boot monolith on Azure Container Apps. Production uses at least two always-on replicas distributed across availability zones. The application is stateless; PostgreSQL is the system of record.

Traffic enters through Azure Front Door Premium and its Web Application Firewall. Front Door connects only to the Container Apps ingress. The database has no public endpoint and is reachable only from the delegated database subnet.

## Data platform

Azure Database for PostgreSQL Flexible Server runs General Purpose compute with zone-redundant high availability, 35-day point-in-time recovery, geo-redundant backup, TLS, and private DNS. Flyway is the only mechanism permitted to change production schemas.

Payment idempotency keys, provider transaction identifiers, user identities, and webhook lookup fields are indexed. Payment child records use foreign keys to prevent orphaned records.

## Secrets and identity

Runtime secrets are stored in Azure Key Vault. Container Apps uses a user-assigned managed identity to pull images from Azure Container Registry and read Key Vault secrets. Secrets must never be stored in GitHub, Bicep parameter files, images, or application logs.

## Observability

Application Insights, Log Analytics, Actuator health probes, structured application logs, platform metrics, and alert rules cover HTTP health, replica count, CPU, memory, PostgreSQL availability, storage, connections, and failed backups.

## Availability objectives

Initial production objectives:

- Availability target: 99.9 percent or higher.
- Database RPO for zonal failures: zero committed transactions through synchronous HA.
- Regional disaster RPO: no more than five minutes when a replica is enabled; otherwise the latest geo-backup.
- Zonal failure RTO: platform-managed failover.
- Regional disaster RTO: 60 minutes after an exercised runbook.

These are engineering targets, not contractual guarantees. Validate them through quarterly recovery exercises.
