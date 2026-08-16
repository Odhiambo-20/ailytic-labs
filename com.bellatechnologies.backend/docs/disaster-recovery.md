# Disaster recovery runbook

## Zone failure

Azure Container Apps and PostgreSQL HA handle zone failures automatically. Confirm healthy replicas, database role, payment-provider callbacks, queue depth, and error rate before closing the incident.

## Azure region failure

1. Declare an incident and freeze nonessential deployments.
2. Confirm the primary region is unavailable rather than experiencing an application regression.
3. Promote the cross-region PostgreSQL read replica, or restore the latest geo-redundant backup.
4. Load production secrets into the secondary Key Vault through the controlled recovery process.
5. Deploy the approved container digest to the secondary Container Apps environment.
6. Run database, authentication, catalog, order, payment, and webhook smoke tests.
7. Change Front Door origin priority to the secondary region.
8. Monitor reconciliation and delayed provider callbacks.
9. Communicate measured RPO and RTO.

## Provider-level failure

AWS warm standby is restored from the latest independently stored PostgreSQL dump. Infrastructure is recreated from the AWS DR Terraform configuration, the approved image is deployed, smoke tests are run, and DNS is changed only after incident-command approval.

## Testing

- Restore a backup monthly in an isolated subscription.
- Exercise regional failover quarterly.
- Exercise provider-level recovery twice yearly.
- Record actual RPO, RTO, missing automation, and corrective actions.
