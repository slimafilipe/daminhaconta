CREATE TABLE IF NOT EXISTS clients (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100) UNIQUE
);

CREATE TABLE IF NOT EXISTS payments (
    id SERIAL PRIMARY KEY,
    value DECIMAL(10, 2),
    due_date TIMESTAMP,
    client_id INT,
    FOREIGN KEY (client_id) REFERENCES clients(id)
);
