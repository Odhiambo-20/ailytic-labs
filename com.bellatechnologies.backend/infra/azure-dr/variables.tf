variable "name_prefix" {
  type    = string
  default = "bella"
}

variable "environment" {
  type    = string
  default = "prod"
}

variable "location" {
  type        = string
  default     = "westeurope"
  description = "Secondary Azure region."
}

variable "primary_postgres_server_id" {
  type = string
}

variable "primary_acr_id" {
  type = string
}

variable "primary_acr_login_server" {
  type = string
}

variable "container_image_tag" {
  type = string
}

variable "database_administrator_login" {
  type    = string
  default = "bella_platform_admin"
}
