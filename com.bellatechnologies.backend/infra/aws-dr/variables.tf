variable "aws_region" {
  type    = string
  default = "eu-west-1"
}

variable "environment" {
  type    = string
  default = "prod"
}

variable "database_username" {
  type    = string
  default = "bella_platform_admin"
}

variable "database_instance_class" {
  type    = string
  default = "db.t4g.medium"
}

variable "container_image_tag" {
  type        = string
  description = "Immutable Git commit SHA promoted for disaster recovery."
}

variable "application_secret_arn" {
  type        = string
  description = "Pre-created Secrets Manager JSON secret containing application runtime secrets."
}

variable "acm_certificate_arn" {
  type        = string
  description = "ACM certificate for the warm-standby load balancer."
}

variable "backup_bucket_name" {
  type        = string
  description = "Globally unique S3 bucket for independent PostgreSQL dumps."
}
