#!/usr/bin/env bash
set -euo pipefail

: "${KEY_VAULT_NAME:?KEY_VAULT_NAME is required}"
required_secrets=(
  DATABASE_PASSWORD
  JWT_SECRET
  PAYMENT_ENCRYPTION_KEY
  STRIPE_SECRET_KEY
  STRIPE_PUBLIC_KEY
  STRIPE_WEBHOOK_SECRET
  GOOGLE_CLIENT_ID
  GOOGLE_CLIENT_SECRET
  MPESA_CONSUMER_KEY
  MPESA_CONSUMER_SECRET
  MPESA_PASSKEY
  MPESA_SHORT_CODE
  MPESA_INITIATOR_PASSWORD
)

for environment_name in "${required_secrets[@]}"; do
  if [[ -z "${!environment_name:-}" ]]; then
    echo "Missing environment variable: ${environment_name}" >&2
    exit 1
  fi
  secret_name="$(tr [:upper:]_ [:lower:]- <<<"${environment_name}")"
  az keyvault secret set     --vault-name "${KEY_VAULT_NAME}"     --name "${secret_name}"     --value "${!environment_name}"     --output none
done
