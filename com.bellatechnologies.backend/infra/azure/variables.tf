variable "name_prefix" {
  type        = string
  default     = "bella"
  description = "Short globally unique workload prefix."
}

variable "environment" {
  type        = string
  default     = "prod"
  description = "Deployment environment."
}

variable "location" {
  type        = string
  default     = "northeurope"
  description = "Azure region supporting availability zones and geo-redundant PostgreSQL backup."
}

variable "database_sku" {
  type        = string
  default     = "GP_Standard_D2s_v3"
  description = "General Purpose production PostgreSQL SKU."
}

variable "database_administrator_login" {
  type    = string
  default = "bella_platform_admin"
}

variable "container_image_repository" {
  type    = string
  default = "bella-backend"
}

variable "container_image_tag" {
  type        = string
  description = "Immutable image tag, normally the Git commit SHA."
}

variable "workload_profile_name" {
  type        = string
  default     = "D4"
  description = "Dedicated workload profile used by the production backend."
}

variable "operations_email" {
  type        = string
  description = "Production operations address for Azure Monitor alerts."
}

