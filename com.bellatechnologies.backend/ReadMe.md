# Bella Technologies Backend

Production Spring Boot 3 / Java 17 backend for Bella Technologies. The application is a modular monolith backed by PostgreSQL and designed to run on Azure Container Apps, with a secondary Azure region and an AWS warm standby for disaster recovery.

## Architecture

```text
Vercel frontend
      |
Azure Front Door Premium + WAF
      |
Azure Container Apps (2-10 replicas)
      |
private virtual network
      |
Azure Database for PostgreSQL Flexible Server
(zone-redundant primary/standby)
```

Supporting services include Azure Container Registry, Key Vault, Application Insights, Log Analytics, geo-redundant backup storage, a secondary-region PostgreSQL replica, and an AWS ECS/RDS warm standby.

The backend remains a modular monolith. This preserves transactional consistency for orders, inventory, users, and payments while avoiding unnecessary distributed-system complexity.

## Local setup

Requirements: Java 17, Maven 3.9+, Docker with Compose.

```bash
docker compose up --build
```

The API runs at `http://localhost:8080`. Health endpoints are:

- `/actuator/health/liveness`
- `/actuator/health/readiness`

To run tests directly:

```bash
mvn verify
```

Unit/repository tests use H2 in PostgreSQL compatibility mode. The `PostgreSqlMigrationIT` integration test uses Testcontainers when Docker is available and validates the real Flyway migrations against PostgreSQL.

## Database

The active persistence layer is Spring Data JPA with PostgreSQL. Flyway owns the production schema in `src/main/resources/db/migration`.

Never use Hibernate auto-DDL in production. `application-prod.properties` validates the schema and Flyway applies versioned migrations.

For migration from the former DynamoDB deployment, see `docs/migration-runbook.md` and `migration/dynamodb_to_postgres.py`. Legacy DynamoDB files are retained only under `migration/legacy` as migration references and are not part of the running application.

## Production deployment

Infrastructure definitions:

- `infra/azure` — primary Azure production region
- `infra/azure-dr` — secondary Azure region and PostgreSQL replica
- `infra/aws-dr` — AWS ECS/RDS warm standby and independent backup storage

Copy each `terraform.tfvars.example`, supply account-specific values, and store Terraform state in secured remote backends before applying. Run `terraform plan` and require production approval before `terraform apply`.

GitHub Actions workflows at the repository root provide CI, Azure deployment, infrastructure planning/application, and scheduled cross-cloud database backups. Configure GitHub environments with OIDC identities and mandatory reviewers; do not store cloud access keys in the repository.

Secrets must be created in Azure Key Vault with the names expected by `infra/azure/main.tf`. Use `scripts/bootstrap-key-vault.sh` from a trusted runner with private-network access.

## Backups and disaster recovery

The database uses 35-day point-in-time recovery and geo-redundant Azure backups. `scripts/backup-postgres.sh` creates an additional encrypted PostgreSQL dump and copies it to Azure Blob Storage and AWS S3. `scripts/restore-postgres.sh` performs controlled restores.

Operational procedures are documented in:

- `docs/production-architecture.md`
- `docs/migration-runbook.md`
- `docs/disaster-recovery.md`

Do not promote a standby based only on an infrastructure alarm. Follow the runbook, confirm database state, protect payment idempotency, promote exactly one writer, switch traffic, and verify business transactions. Run disaster-recovery exercises regularly and record achieved RPO/RTO.

## Required runtime configuration

Configuration is supplied through environment variables and Key Vault references. Important values include:

- `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`
- `JWT_SECRET`, `PAYMENT_ENCRYPTION_KEY`
- `CORS_ALLOWED_ORIGINS`, `OAUTH2_REDIRECT_URI`
- Stripe credentials and webhook secret
- Google OAuth client credentials
- production M-Pesa credentials and callback URLs

See `src/main/resources/application.properties` for the complete list. Never commit real credentials, generated Terraform state, database dumps, or `.env` files.

## Release process

1. Open a reviewed pull request.
2. Run `mvn verify` and Terraform validation/plans.
3. Back up the current database and verify restore readiness.
4. Deploy an immutable image tagged with the Git commit SHA.
5. Allow Flyway to apply compatible migrations.
6. Verify readiness, authentication, product APIs, and payment callbacks.
7. Monitor errors, latency, saturation, database storage, and failed payments.

Production resources are intentionally not created by cloning this repository. Deployment requires approved Azure/AWS subscriptions, DNS ownership, production secrets, OIDC identities, and an authorized migration window.
