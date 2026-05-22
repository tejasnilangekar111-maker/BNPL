CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(255),
    address VARCHAR(255),
    password_hash VARCHAR(255),
    registration_date TIMESTAMP,
    credit_score INTEGER DEFAULT 650,
    monthly_income DECIMAL(15, 2),
    role VARCHAR(50)
);

CREATE TABLE merchants (
    merchant_id BIGSERIAL PRIMARY KEY,
    business_name VARCHAR(255) NOT NULL,
    category VARCHAR(255),
    registration_details VARCHAR(255)
);

CREATE TABLE products (
    product_id BIGSERIAL PRIMARY KEY,
    merchant_id BIGINT REFERENCES merchants(merchant_id),
    name VARCHAR(255),
    description TEXT,
    price DECIMAL(15, 2)
);

CREATE TABLE orders (
    order_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(user_id),
    merchant_id BIGINT REFERENCES merchants(merchant_id),
    total_amount DECIMAL(15, 2),
    order_date TIMESTAMP,
    status VARCHAR(50)
);

CREATE TABLE bnpl_plans (
    plan_id BIGSERIAL PRIMARY KEY,
    order_id BIGINT REFERENCES orders(order_id),
    principal_amount DECIMAL(15, 2),
    interest_rate DECIMAL(5, 2),
    tenure_months INTEGER,
    emi_amount DECIMAL(15, 2)
);

CREATE TABLE emi_schedule (
    schedule_id BIGSERIAL PRIMARY KEY,
    plan_id BIGINT REFERENCES bnpl_plans(plan_id),
    emi_number INTEGER,
    due_date DATE,
    amount DECIMAL(15, 2),
    status VARCHAR(50),
    payment_date TIMESTAMP,
    late_fee DECIMAL(15, 2)
);

CREATE TABLE payments (
    payment_id BIGSERIAL PRIMARY KEY,
    schedule_id BIGINT REFERENCES emi_schedule(schedule_id),
    amount DECIMAL(15, 2),
    payment_method VARCHAR(255),
    transaction_id VARCHAR(255),
    status VARCHAR(50),
    payment_time TIMESTAMP
);

CREATE TABLE credit_history (
    history_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(user_id),
    transaction_type VARCHAR(50),
    amount DECIMAL(15, 2),
    impact_on_score INTEGER,
    date TIMESTAMP
);
