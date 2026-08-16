terraform {
  required_version = ">= 1.7.0"
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 4.0"
    }
  }
}

provider "azurerm" {
  features {}
}

data "azurerm_client_config" "current" {}

locals {
  name                 = "${var.name_prefix}-${var.environment}-dr"
  key_vault_secret_uri = "https://${azurerm_key_vault.dr.name}.vault.azure.net/secrets"
  runtime_secrets = {
    "database-password"        = "DATABASE_PASSWORD"
    "jwt-secret"               = "JWT_SECRET"
    "payment-encryption-key"   = "PAYMENT_ENCRYPTION_KEY"
    "stripe-secret-key"        = "STRIPE_SECRET_KEY"
    "stripe-public-key"        = "STRIPE_PUBLIC_KEY"
    "stripe-webhook-secret"    = "STRIPE_WEBHOOK_SECRET"
    "google-client-id"         = "GOOGLE_CLIENT_ID"
    "google-client-secret"     = "GOOGLE_CLIENT_SECRET"
    "mpesa-consumer-key"       = "MPESA_CONSUMER_KEY"
    "mpesa-consumer-secret"    = "MPESA_CONSUMER_SECRET"
    "mpesa-passkey"            = "MPESA_PASSKEY"
    "mpesa-short-code"         = "MPESA_SHORT_CODE"
    "mpesa-initiator-password" = "MPESA_INITIATOR_PASSWORD"
  }
  tags = {
    application = "bella-technologies"
    environment = var.environment
    role        = "regional-standby"
    managed-by  = "terraform"
  }
}

resource "azurerm_resource_group" "dr" {
  name     = "rg-${local.name}-${var.location}"
  location = var.location
  tags     = local.tags
}

resource "azurerm_virtual_network" "dr" {
  name                = "vnet-${local.name}"
  location            = azurerm_resource_group.dr.location
  resource_group_name = azurerm_resource_group.dr.name
  address_space       = ["10.50.0.0/16"]
  tags                = local.tags
}

resource "azurerm_subnet" "container_apps" {
  name                 = "snet-container-apps"
  resource_group_name  = azurerm_resource_group.dr.name
  virtual_network_name = azurerm_virtual_network.dr.name
  address_prefixes     = ["10.50.0.0/23"]
}

resource "azurerm_subnet" "postgres" {
  name                 = "snet-postgresql"
  resource_group_name  = azurerm_resource_group.dr.name
  virtual_network_name = azurerm_virtual_network.dr.name
  address_prefixes     = ["10.50.4.0/28"]
  delegation {
    name = "postgres-flexible-server"
    service_delegation {
      name    = "Microsoft.DBforPostgreSQL/flexibleServers"
      actions = ["Microsoft.Network/virtualNetworks/subnets/join/action"]
    }
  }
}

resource "azurerm_private_dns_zone" "postgres" {
  name                = "${local.name}.private.postgres.database.azure.com"
  resource_group_name = azurerm_resource_group.dr.name
  tags                = local.tags
}

resource "azurerm_private_dns_zone_virtual_network_link" "postgres" {
  name                  = "postgres-vnet-link"
  private_dns_zone_name = azurerm_private_dns_zone.postgres.name
  virtual_network_id    = azurerm_virtual_network.dr.id
  resource_group_name   = azurerm_resource_group.dr.name
}

resource "azurerm_postgresql_flexible_server" "replica" {
  name                          = "psql-${local.name}"
  resource_group_name           = azurerm_resource_group.dr.name
  location                      = azurerm_resource_group.dr.location
  create_mode                   = "Replica"
  source_server_id              = var.primary_postgres_server_id
  delegated_subnet_id           = azurerm_subnet.postgres.id
  private_dns_zone_id           = azurerm_private_dns_zone.postgres.id
  public_network_access_enabled = false
  zone                          = "1"

  depends_on = [azurerm_private_dns_zone_virtual_network_link.postgres]
  tags       = local.tags
}

resource "azurerm_log_analytics_workspace" "dr" {
  name                = "log-${local.name}"
  location            = azurerm_resource_group.dr.location
  resource_group_name = azurerm_resource_group.dr.name
  sku                 = "PerGB2018"
  retention_in_days   = 90
  tags                = local.tags
}

resource "azurerm_application_insights" "dr" {
  name                = "appi-${local.name}"
  location            = azurerm_resource_group.dr.location
  resource_group_name = azurerm_resource_group.dr.name
  workspace_id        = azurerm_log_analytics_workspace.dr.id
  application_type    = "java"
  tags                = local.tags
}

resource "azurerm_user_assigned_identity" "backend" {
  name                = "id-${local.name}-backend"
  location            = azurerm_resource_group.dr.location
  resource_group_name = azurerm_resource_group.dr.name
  tags                = local.tags
}

resource "azurerm_role_assignment" "acr_pull" {
  scope                = var.primary_acr_id
  role_definition_name = "AcrPull"
  principal_id         = azurerm_user_assigned_identity.backend.principal_id
}

resource "azurerm_key_vault" "dr" {
  name                          = substr(replace("kv-${local.name}", "-", ""), 0, 24)
  location                      = azurerm_resource_group.dr.location
  resource_group_name           = azurerm_resource_group.dr.name
  tenant_id                     = data.azurerm_client_config.current.tenant_id
  sku_name                      = "premium"
  rbac_authorization_enabled    = true
  purge_protection_enabled      = true
  soft_delete_retention_days    = 90
  public_network_access_enabled = false
  tags                          = local.tags
}

resource "azurerm_role_assignment" "key_vault_reader" {
  scope                = azurerm_key_vault.dr.id
  role_definition_name = "Key Vault Secrets User"
  principal_id         = azurerm_user_assigned_identity.backend.principal_id
}

resource "azurerm_container_app_environment" "dr" {
  name                       = "cae-${local.name}"
  location                   = azurerm_resource_group.dr.location
  resource_group_name        = azurerm_resource_group.dr.name
  log_analytics_workspace_id = azurerm_log_analytics_workspace.dr.id
  infrastructure_subnet_id   = azurerm_subnet.container_apps.id
  zone_redundancy_enabled    = true
  tags                       = local.tags
}

resource "azurerm_container_app" "backend" {
  name                         = "ca-${local.name}-backend"
  container_app_environment_id = azurerm_container_app_environment.dr.id
  resource_group_name          = azurerm_resource_group.dr.name
  revision_mode                = "Multiple"

  identity {
    type         = "UserAssigned"
    identity_ids = [azurerm_user_assigned_identity.backend.id]
  }

  registry {
    server   = var.primary_acr_login_server
    identity = azurerm_user_assigned_identity.backend.id
  }

  dynamic "secret" {
    for_each = local.runtime_secrets
    content {
      name                = secret.key
      identity            = azurerm_user_assigned_identity.backend.id
      key_vault_secret_id = "${local.key_vault_secret_uri}/${secret.key}"
    }
  }

  template {
    min_replicas = 0
    max_replicas = 10

    container {
      name   = "backend"
      image  = "${var.primary_acr_login_server}/bella-backend:${var.container_image_tag}"
      cpu    = 1.0
      memory = "2Gi"

      env {
        name  = "SPRING_PROFILES_ACTIVE"
        value = "prod"
      }
      env {
        name  = "DATABASE_URL"
        value = "jdbc:postgresql://${azurerm_postgresql_flexible_server.replica.fqdn}:5432/bella_technologies?sslmode=require"
      }
      env {
        name  = "DATABASE_USERNAME"
        value = var.database_administrator_login
      }
      dynamic "env" {
        for_each = local.runtime_secrets
        content {
          name        = env.value
          secret_name = env.key
        }
      }
      env {
        name  = "APPLICATIONINSIGHTS_CONNECTION_STRING"
        value = azurerm_application_insights.dr.connection_string
      }

      liveness_probe {
        transport        = "HTTP"
        port             = 8080
        path             = "/actuator/health/liveness"
        interval_seconds = 10
      }
      readiness_probe {
        transport        = "HTTP"
        port             = 8080
        path             = "/actuator/health/readiness"
        interval_seconds = 10
      }
    }
  }

  ingress {
    external_enabled           = true
    allow_insecure_connections = false
    target_port                = 8080
    transport                  = "http"
    traffic_weight {
      percentage      = 100
      latest_revision = true
    }
  }

  depends_on = [
    azurerm_role_assignment.acr_pull,
    azurerm_role_assignment.key_vault_reader
  ]

  tags = local.tags
}

output "standby_container_app_fqdn" {
  value = azurerm_container_app.backend.latest_revision_fqdn
}

output "replica_server_id" {
  value = azurerm_postgresql_flexible_server.replica.id
}

output "standby_key_vault_name" {
  value = azurerm_key_vault.dr.name
}
