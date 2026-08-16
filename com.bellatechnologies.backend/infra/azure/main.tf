terraform {
  required_version = ">= 1.7.0"
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 4.0"
    }
    random = {
      source  = "hashicorp/random"
      version = "~> 3.6"
    }
  }
}

provider "azurerm" {
  features {}
}

locals {
  name                 = "${var.name_prefix}-${var.environment}"
  database_name        = "bella_technologies"
  container_image      = "${azurerm_container_registry.main.login_server}/${var.container_image_repository}:${var.container_image_tag}"
  key_vault_secret_uri = "https://${azurerm_key_vault.main.name}.vault.azure.net/secrets"
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
  common_tags = {
    application = "bella-technologies"
    environment = var.environment
    managed-by  = "terraform"
  }
}

resource "azurerm_resource_group" "main" {
  name     = "rg-${local.name}-${var.location}"
  location = var.location
  tags     = local.common_tags
}

resource "azurerm_virtual_network" "main" {
  name                = "vnet-${local.name}"
  location            = azurerm_resource_group.main.location
  resource_group_name = azurerm_resource_group.main.name
  address_space       = ["10.40.0.0/16"]
  tags                = local.common_tags
}

resource "azurerm_subnet" "container_apps" {
  name                 = "snet-container-apps"
  resource_group_name  = azurerm_resource_group.main.name
  virtual_network_name = azurerm_virtual_network.main.name
  address_prefixes     = ["10.40.0.0/23"]
}

resource "azurerm_subnet" "postgres" {
  name                 = "snet-postgresql"
  resource_group_name  = azurerm_resource_group.main.name
  virtual_network_name = azurerm_virtual_network.main.name
  address_prefixes     = ["10.40.4.0/28"]
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
  resource_group_name = azurerm_resource_group.main.name
  tags                = local.common_tags
}

resource "azurerm_private_dns_zone_virtual_network_link" "postgres" {
  name                  = "postgres-vnet-link"
  private_dns_zone_name = azurerm_private_dns_zone.postgres.name
  virtual_network_id    = azurerm_virtual_network.main.id
  resource_group_name   = azurerm_resource_group.main.name
}

resource "random_password" "postgres" {
  length           = 40
  special          = true
  override_special = "!#%*-_=+"
}

resource "azurerm_postgresql_flexible_server" "main" {
  name                          = "psql-${local.name}"
  resource_group_name           = azurerm_resource_group.main.name
  location                      = azurerm_resource_group.main.location
  version                       = "16"
  delegated_subnet_id           = azurerm_subnet.postgres.id
  private_dns_zone_id           = azurerm_private_dns_zone.postgres.id
  public_network_access_enabled = false
  administrator_login           = var.database_administrator_login
  administrator_password        = random_password.postgres.result
  zone                          = "1"
  storage_mb                    = 131072
  sku_name                      = var.database_sku
  backup_retention_days         = 35
  geo_redundant_backup_enabled  = true

  high_availability {
    mode                      = "ZoneRedundant"
    standby_availability_zone = "2"
  }

  maintenance_window {
    day_of_week  = 0
    start_hour   = 1
    start_minute = 0
  }

  depends_on = [azurerm_private_dns_zone_virtual_network_link.postgres]
  tags       = local.common_tags
}

resource "azurerm_postgresql_flexible_server_database" "app" {
  name      = local.database_name
  server_id = azurerm_postgresql_flexible_server.main.id
  collation = "en_US.utf8"
  charset   = "UTF8"
}

resource "azurerm_log_analytics_workspace" "main" {
  name                = "log-${local.name}"
  location            = azurerm_resource_group.main.location
  resource_group_name = azurerm_resource_group.main.name
  sku                 = "PerGB2018"
  retention_in_days   = 90
  tags                = local.common_tags
}

resource "azurerm_application_insights" "main" {
  name                = "appi-${local.name}"
  location            = azurerm_resource_group.main.location
  resource_group_name = azurerm_resource_group.main.name
  workspace_id        = azurerm_log_analytics_workspace.main.id
  application_type    = "java"
  tags                = local.common_tags
}

resource "azurerm_container_registry" "main" {
  name                          = replace("acr${local.name}", "-", "")
  resource_group_name           = azurerm_resource_group.main.name
  location                      = azurerm_resource_group.main.location
  sku                           = "Premium"
  admin_enabled                 = false
  public_network_access_enabled = true
  zone_redundancy_enabled       = true
  tags                          = local.common_tags
}

resource "azurerm_user_assigned_identity" "backend" {
  name                = "id-${local.name}-backend"
  location            = azurerm_resource_group.main.location
  resource_group_name = azurerm_resource_group.main.name
  tags                = local.common_tags
}

resource "azurerm_role_assignment" "acr_pull" {
  scope                = azurerm_container_registry.main.id
  role_definition_name = "AcrPull"
  principal_id         = azurerm_user_assigned_identity.backend.principal_id
}

resource "azurerm_key_vault" "main" {
  name                          = substr(replace("kv-${local.name}", "-", ""), 0, 24)
  location                      = azurerm_resource_group.main.location
  resource_group_name           = azurerm_resource_group.main.name
  tenant_id                     = data.azurerm_client_config.current.tenant_id
  sku_name                      = "premium"
  rbac_authorization_enabled    = true
  purge_protection_enabled      = true
  soft_delete_retention_days    = 90
  public_network_access_enabled = false
  tags                          = local.common_tags
}

data "azurerm_client_config" "current" {}

resource "azurerm_role_assignment" "key_vault_reader" {
  scope                = azurerm_key_vault.main.id
  role_definition_name = "Key Vault Secrets User"
  principal_id         = azurerm_user_assigned_identity.backend.principal_id
}

resource "azurerm_container_app_environment" "main" {
  name                           = "cae-${local.name}"
  location                       = azurerm_resource_group.main.location
  resource_group_name            = azurerm_resource_group.main.name
  log_analytics_workspace_id     = azurerm_log_analytics_workspace.main.id
  infrastructure_subnet_id       = azurerm_subnet.container_apps.id
  zone_redundancy_enabled        = true
  internal_load_balancer_enabled = false
  tags                           = local.common_tags
}

resource "azurerm_container_app" "backend" {
  name                         = "ca-${local.name}-backend"
  container_app_environment_id = azurerm_container_app_environment.main.id
  resource_group_name          = azurerm_resource_group.main.name
  revision_mode                = "Multiple"
  workload_profile_name        = var.workload_profile_name

  identity {
    type         = "UserAssigned"
    identity_ids = [azurerm_user_assigned_identity.backend.id]
  }

  registry {
    server   = azurerm_container_registry.main.login_server
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
    min_replicas = 2
    max_replicas = 10

    container {
      name   = "backend"
      image  = local.container_image
      cpu    = 1.0
      memory = "2Gi"

      env {
        name  = "SPRING_PROFILES_ACTIVE"
        value = "prod"
      }
      env {
        name  = "DATABASE_URL"
        value = "jdbc:postgresql://${azurerm_postgresql_flexible_server.main.fqdn}:5432/${local.database_name}?sslmode=require"
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
        value = azurerm_application_insights.main.connection_string
      }

      liveness_probe {
        transport        = "HTTP"
        port             = 8080
        path             = "/actuator/health/liveness"
        interval_seconds = 10
        timeout          = 5
      }

      readiness_probe {
        transport        = "HTTP"
        port             = 8080
        path             = "/actuator/health/readiness"
        interval_seconds = 10
        timeout          = 5
      }
    }

    http_scale_rule {
      name                = "http-concurrency"
      concurrent_requests = 50
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
    azurerm_role_assignment.key_vault_reader,
    azurerm_postgresql_flexible_server_database.app
  ]

  tags = local.common_tags
}

resource "azurerm_cdn_frontdoor_profile" "main" {
  name                = "afd-${local.name}"
  resource_group_name = azurerm_resource_group.main.name
  sku_name            = "Premium_AzureFrontDoor"
  tags                = local.common_tags
}

resource "azurerm_cdn_frontdoor_endpoint" "main" {
  name                     = "fde-${local.name}"
  cdn_frontdoor_profile_id = azurerm_cdn_frontdoor_profile.main.id
  tags                     = local.common_tags
}

resource "azurerm_cdn_frontdoor_origin_group" "backend" {
  name                     = "backend-origins"
  cdn_frontdoor_profile_id = azurerm_cdn_frontdoor_profile.main.id
  session_affinity_enabled = false

  load_balancing {
    sample_size                 = 4
    successful_samples_required = 3
  }

  health_probe {
    path                = "/actuator/health/readiness"
    protocol            = "Https"
    request_type        = "GET"
    interval_in_seconds = 30
  }
}

resource "azurerm_cdn_frontdoor_origin" "backend" {
  name                           = "backend-primary"
  cdn_frontdoor_origin_group_id  = azurerm_cdn_frontdoor_origin_group.backend.id
  enabled                        = true
  host_name                      = azurerm_container_app.backend.latest_revision_fqdn
  origin_host_header             = azurerm_container_app.backend.latest_revision_fqdn
  http_port                      = 80
  https_port                     = 443
  priority                       = 1
  weight                         = 1000
  certificate_name_check_enabled = true
}

resource "azurerm_cdn_frontdoor_route" "backend" {
  name                          = "backend-route"
  cdn_frontdoor_endpoint_id     = azurerm_cdn_frontdoor_endpoint.main.id
  cdn_frontdoor_origin_group_id = azurerm_cdn_frontdoor_origin_group.backend.id
  cdn_frontdoor_origin_ids      = [azurerm_cdn_frontdoor_origin.backend.id]
  supported_protocols           = ["Http", "Https"]
  patterns_to_match             = ["/*"]
  forwarding_protocol           = "HttpsOnly"
  https_redirect_enabled        = true
  link_to_default_domain        = true
}

resource "azurerm_cdn_frontdoor_firewall_policy" "main" {
  name                = replace("waf${local.name}", "-", "")
  resource_group_name = azurerm_resource_group.main.name
  sku_name            = azurerm_cdn_frontdoor_profile.main.sku_name
  enabled             = true
  mode                = "Prevention"

  managed_rule {
    type    = "Microsoft_DefaultRuleSet"
    version = "2.1"
    action  = "Block"
  }

  managed_rule {
    type    = "Microsoft_BotManagerRuleSet"
    version = "1.1"
    action  = "Block"
  }

  tags = local.common_tags
}

resource "azurerm_cdn_frontdoor_security_policy" "main" {
  name                     = "security-policy"
  cdn_frontdoor_profile_id = azurerm_cdn_frontdoor_profile.main.id

  security_policies {
    firewall {
      cdn_frontdoor_firewall_policy_id = azurerm_cdn_frontdoor_firewall_policy.main.id
      association {
        patterns_to_match = ["/*"]
        domain {
          cdn_frontdoor_domain_id = azurerm_cdn_frontdoor_endpoint.main.id
        }
      }
    }
  }
}


resource "azurerm_storage_account" "backups" {
  name                            = substr(replace("st${local.name}backup", "-", ""), 0, 24)
  resource_group_name             = azurerm_resource_group.main.name
  location                        = azurerm_resource_group.main.location
  account_tier                    = "Standard"
  account_replication_type        = "GZRS"
  min_tls_version                 = "TLS1_2"
  https_traffic_only_enabled      = true
  allow_nested_items_to_be_public = false
  shared_access_key_enabled       = false
  tags                            = local.common_tags
}

resource "azurerm_storage_container" "backups" {
  name                  = "postgres-backups"
  storage_account_id    = azurerm_storage_account.backups.id
  container_access_type = "private"
}

resource "azurerm_monitor_action_group" "operations" {
  name                = "ag-${local.name}-operations"
  resource_group_name = azurerm_resource_group.main.name
  short_name          = "bellaops"
  email_receiver {
    name                    = "operations"
    email_address           = var.operations_email
    use_common_alert_schema = true
  }
  tags = local.common_tags
}

resource "azurerm_monitor_metric_alert" "postgres_storage" {
  name                = "PostgreSQL storage above 80 percent"
  resource_group_name = azurerm_resource_group.main.name
  scopes              = [azurerm_postgresql_flexible_server.main.id]
  severity            = 1
  frequency           = "PT5M"
  window_size         = "PT15M"
  description         = "Production PostgreSQL storage utilization is above 80 percent."

  criteria {
    metric_namespace = "Microsoft.DBforPostgreSQL/flexibleServers"
    metric_name      = "storage_percent"
    aggregation      = "Average"
    operator         = "GreaterThan"
    threshold        = 80
  }

  action {
    action_group_id = azurerm_monitor_action_group.operations.id
  }

  tags = local.common_tags
}

resource "azurerm_monitor_metric_alert" "postgres_cpu" {
  name                = "PostgreSQL CPU above 80 percent"
  resource_group_name = azurerm_resource_group.main.name
  scopes              = [azurerm_postgresql_flexible_server.main.id]
  severity            = 2
  frequency           = "PT5M"
  window_size         = "PT15M"
  description         = "Production PostgreSQL CPU utilization is above 80 percent."

  criteria {
    metric_namespace = "Microsoft.DBforPostgreSQL/flexibleServers"
    metric_name      = "cpu_percent"
    aggregation      = "Average"
    operator         = "GreaterThan"
    threshold        = 80
  }

  action {
    action_group_id = azurerm_monitor_action_group.operations.id
  }

  tags = local.common_tags
}
