CREATE TABLE order_history (
    id BINARY(16) NOT NULL PRIMARY KEY,
    order_id BINARY(16) NOT NULL,
    timestamp DATETIME NOT NULL,
    old_status ENUM('PENDING', 'APPROVED', 'SHIPPED', 'DELIVERED', 'CANCELLED') NOT NULL,
    new_status ENUM('PENDING', 'APPROVED', 'SHIPPED', 'DELIVERED', 'CANCELLED') NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id)
);
