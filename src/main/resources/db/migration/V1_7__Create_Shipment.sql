CREATE TABLE shipments (
    id BINARY(16) NOT NULL PRIMARY KEY,
    order_id BINARY(16) NOT NULL,
    shipped_date DATETIME NOT NULL,
    tracking_number VARCHAR(50) NOT NULL,
    status ENUM('PENDING', 'IN_TRANSIT', 'DELIVERED', 'FAILED') NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id)
);
