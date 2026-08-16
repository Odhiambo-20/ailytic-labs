output "resource_group_name" {
  value = azurerm_resource_group.main.name
}

output "container_registry" {
  value = azurerm_container_registry.main.login_server
}

output "container_app_fqdn" {
  value = azurerm_container_app.backend.latest_revision_fqdn
}

output "front_door_endpoint" {
  value = azurerm_cdn_frontdoor_endpoint.main.host_name
}

output "postgres_fqdn" {
  value     = azurerm_postgresql_flexible_server.main.fqdn
  sensitive = true
}

output "key_vault_name" {
  value = azurerm_key_vault.main.name
}

output "backup_storage_account" {
  value = azurerm_storage_account.backups.name
}

output "backup_container" {
  value = azurerm_storage_container.backups.name
}

