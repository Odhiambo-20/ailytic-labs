CREATE TABLE contacts (
    id varchar(255) PRIMARY KEY,
    first_name varchar(255) NOT NULL,
    last_name varchar(255),
    email varchar(255) NOT NULL,
    help_type varchar(255),
    message varchar(4000),
    created_at timestamp(6)
);

CREATE TABLE newsletter_subscriptions (
    email varchar(255) PRIMARY KEY,
    subscribed_at timestamp(6)
);

CREATE TABLE robots (
    id varchar(255) PRIMARY KEY,
    name varchar(255),
    type varchar(255),
    description varchar(4000),
    capabilities text,
    image varchar(2048),
    price varchar(255),
    rating float8,
    reviews integer
);
CREATE INDEX idx_robots_type ON robots(type);

CREATE TABLE drones (
    id varchar(255) PRIMARY KEY,
    name varchar(255),
    type varchar(255),
    description varchar(4000),
    specifications text,
    image varchar(2048),
    price varchar(255),
    rating float8,
    reviews integer,
    flight_time varchar(255),
    range varchar(255)
);
CREATE INDEX idx_drones_type ON drones(type);

CREATE TABLE solar_panels (
    id varchar(255) PRIMARY KEY,
    name varchar(255),
    type varchar(255),
    description varchar(4000),
    features text,
    image varchar(2048),
    power varchar(255),
    efficiency varchar(255),
    warranty varchar(255),
    price varchar(255)
);
CREATE INDEX idx_solar_panels_type ON solar_panels(type);

CREATE TABLE app_users (
    user_id varchar(255) PRIMARY KEY,
    email varchar(255) NOT NULL UNIQUE,
    username varchar(255) NOT NULL UNIQUE,
    password varchar(255),
    first_name varchar(255),
    last_name varchar(255),
    provider varchar(255),
    provider_id varchar(255),
    roles text,
    enabled boolean NOT NULL,
    email_verified boolean NOT NULL,
    profile_picture_url varchar(2048),
    created_at timestamptz,
    updated_at timestamptz,
    last_login_at timestamptz
);
CREATE UNIQUE INDEX uq_users_provider_identity ON app_users(provider, provider_id)
    WHERE provider_id IS NOT NULL;

CREATE TABLE payments (
    id varchar(255) PRIMARY KEY,
    user_id varchar(255),
    merchant_id varchar(255),
    status varchar(32) NOT NULL,
    payment_method varchar(32) NOT NULL,
    amount float8 NOT NULL CHECK (amount >= 0),
    currency varchar(3) NOT NULL,
    transaction_hash varchar(255),
    external_transaction_id varchar(255),
    idempotency_key varchar(255),
    description varchar(4000),
    created_at varchar(255),
    updated_at varchar(255),
    completed_at varchar(255),
    CONSTRAINT fk_payments_user FOREIGN KEY (user_id) REFERENCES app_users(user_id)
);
CREATE UNIQUE INDEX uq_payments_idempotency_key ON payments(idempotency_key)
    WHERE idempotency_key IS NOT NULL;
CREATE UNIQUE INDEX uq_payments_external_transaction ON payments(external_transaction_id)
    WHERE external_transaction_id IS NOT NULL;
CREATE INDEX idx_payments_user ON payments(user_id);
CREATE INDEX idx_payments_merchant ON payments(merchant_id);
CREATE INDEX idx_payments_status ON payments(status);

CREATE TABLE payment_transactions (
    id varchar(255) PRIMARY KEY,
    payment_id varchar(255),
    status varchar(32) NOT NULL,
    method varchar(32) NOT NULL,
    amount float8 NOT NULL CHECK (amount >= 0),
    transaction_id varchar(255),
    transaction_type varchar(255),
    error_message varchar(4000),
    metadata varchar(4000),
    created_at varchar(255),
    updated_at varchar(255),
    CONSTRAINT fk_transactions_payment FOREIGN KEY (payment_id) REFERENCES payments(id)
);
CREATE INDEX idx_transactions_payment ON payment_transactions(payment_id);
CREATE INDEX idx_transactions_status ON payment_transactions(status);
CREATE INDEX idx_transactions_external ON payment_transactions(transaction_id);

CREATE TABLE mpesa_payments (
    id varchar(255) PRIMARY KEY,
    phone_number varchar(255),
    amount float8 CHECK (amount >= 0),
    checkout_request_id varchar(255),
    merchant_request_id varchar(255),
    transaction_id varchar(255),
    mpesa_transaction_id varchar(255),
    status varchar(32),
    payment_id varchar(255),
    account_reference varchar(255),
    transaction_desc varchar(255),
    result_code varchar(255),
    result_desc varchar(4000),
    mpesa_receipt_number varchar(255),
    transaction_date varchar(255),
    callback_payload varchar(8000),
    created_at varchar(255),
    updated_at varchar(255),
    CONSTRAINT fk_mpesa_payment FOREIGN KEY (payment_id) REFERENCES payments(id)
);
CREATE UNIQUE INDEX uq_mpesa_checkout_request ON mpesa_payments(checkout_request_id)
    WHERE checkout_request_id IS NOT NULL;

CREATE TABLE stripe_payments (
    id varchar(255) PRIMARY KEY,
    payment_intent_id varchar(255),
    stripe_payment_intent_id varchar(255),
    amount float8 CHECK (amount >= 0),
    currency varchar(3),
    customer_id varchar(255),
    status varchar(32),
    stripe_status varchar(255),
    payment_id varchar(255),
    refund_id varchar(255),
    refund_amount varchar(255),
    created_at varchar(255),
    updated_at varchar(255),
    CONSTRAINT fk_stripe_payment FOREIGN KEY (payment_id) REFERENCES payments(id)
);
CREATE UNIQUE INDEX uq_stripe_intent ON stripe_payments(stripe_payment_intent_id)
    WHERE stripe_payment_intent_id IS NOT NULL;

CREATE TABLE qr_payments (
    id varchar(255) PRIMARY KEY,
    qr_payment_id varchar(255),
    qr_code varchar(4000),
    qr_code_token varchar(255),
    qr_code_image text,
    status varchar(32) NOT NULL,
    amount float8 NOT NULL CHECK (amount >= 0),
    currency varchar(3),
    payment_id varchar(255),
    merchant_id varchar(255),
    merchant_name varchar(255),
    description varchar(4000),
    created_at varchar(255),
    updated_at varchar(255),
    expires_at varchar(255),
    used_at varchar(255),
    scanned_at varchar(255),
    used boolean,
    single_use boolean,
    scan_count integer,
    max_scans integer,
    ip_address varchar(255),
    device_fingerprint varchar(255),
    totp_secret varchar(255),
    CONSTRAINT fk_qr_payment FOREIGN KEY (payment_id) REFERENCES payments(id)
);
CREATE UNIQUE INDEX uq_qr_payment_token ON qr_payments(qr_code_token)
    WHERE qr_code_token IS NOT NULL;
CREATE INDEX idx_qr_merchant ON qr_payments(merchant_id);

CREATE TABLE webhook_logs (
    webhook_id varchar(255) PRIMARY KEY,
    timestamp bigint NOT NULL,
    payment_id varchar(255),
    provider varchar(64),
    event_type varchar(255),
    payload text,
    signature varchar(2048),
    verified boolean,
    processed boolean,
    processing_status varchar(64),
    error_message varchar(4000),
    retry_count integer,
    ip_address varchar(255),
    user_agent varchar(2048),
    headers text,
    created_at varchar(255),
    processed_at varchar(255)
);
CREATE INDEX idx_webhook_provider ON webhook_logs(provider);
CREATE INDEX idx_webhook_payment ON webhook_logs(payment_id);
CREATE INDEX idx_webhook_timestamp ON webhook_logs(timestamp DESC);
