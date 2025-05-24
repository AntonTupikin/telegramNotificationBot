CREATE TABLE clients (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    second_name VARCHAR(255),
    telegram_chat_id VARCHAR(50) NOT NULL UNIQUE
);
