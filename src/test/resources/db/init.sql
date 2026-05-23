-- ============================================================
-- E-Commerce Test Database Schema + Seed Data
-- Run by Testcontainers on container startup
-- ============================================================

CREATE TABLE IF NOT EXISTS users (
    id          SERIAL PRIMARY KEY,
    username    VARCHAR(100) NOT NULL UNIQUE,
    email       VARCHAR(200) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    firstname   VARCHAR(100),
    lastname    VARCHAR(100),
    phone       VARCHAR(20),
    created_at  TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS products (
    id          SERIAL PRIMARY KEY,
    title       VARCHAR(300) NOT NULL,
    price       DECIMAL(10,2) NOT NULL CHECK (price > 0),
    description TEXT,
    category    VARCHAR(100) NOT NULL,
    image_url   VARCHAR(500),
    stock       INT NOT NULL DEFAULT 0 CHECK (stock >= 0),
    created_at  TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS orders (
    id          SERIAL PRIMARY KEY,
    user_id     INT NOT NULL REFERENCES users(id),
    status      VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    total       DECIMAL(10,2) NOT NULL,
    created_at  TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS order_items (
    id          SERIAL PRIMARY KEY,
    order_id    INT NOT NULL REFERENCES orders(id),
    product_id  INT NOT NULL REFERENCES products(id),
    quantity    INT NOT NULL CHECK (quantity > 0),
    unit_price  DECIMAL(10,2) NOT NULL
);

-- ── Seed Data ─────────────────────────────────────────────────────────────────

INSERT INTO users (username, email, password, firstname, lastname, phone)
VALUES
    ('john_doe', 'john@test.com', 'hashed_password_1', 'John', 'Doe', '555-0101'),
    ('jane_doe', 'jane@test.com', 'hashed_password_2', 'Jane', 'Doe', '555-0102'),
    ('test_user', 'test@portfolio.dev', 'hashed_password_3', 'Test', 'User', '555-0103');

INSERT INTO products (title, price, description, category, image_url, stock)
VALUES
    ('Laptop Pro 15"', 1299.99, 'High performance laptop', 'electronics', 'https://example.com/laptop.jpg', 50),
    ('Wireless Headphones', 89.99, 'Noise cancelling headphones', 'electronics', 'https://example.com/headphones.jpg', 100),
    ('Running Shoes', 59.99, 'Lightweight running shoes', 'footwear', 'https://example.com/shoes.jpg', 200),
    ('Out of Stock Item', 29.99, 'This item is out of stock', 'misc', 'https://example.com/item.jpg', 0);

INSERT INTO orders (user_id, status, total)
VALUES
    (1, 'COMPLETED', 1389.98),
    (2, 'PENDING', 89.99);

INSERT INTO order_items (order_id, product_id, quantity, unit_price)
VALUES
    (1, 1, 1, 1299.99),
    (1, 2, 1, 89.99),
    (2, 2, 1, 89.99);
