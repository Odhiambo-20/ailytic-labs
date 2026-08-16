output "load_balancer_dns_name" {
  value = aws_lb.main.dns_name
}

output "ecr_repository_url" {
  value = aws_ecr_repository.backend.repository_url
}

output "database_address" {
  value     = aws_db_instance.postgres.address
  sensitive = true
}

output "offsite_backup_bucket" {
  value = aws_s3_bucket.offsite_backups.bucket
}
