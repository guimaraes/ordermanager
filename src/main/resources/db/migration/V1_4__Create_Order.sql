CREATE TABLE orders (
    id BINARY(16) NOT NULL PRIMARY KEY,
    customer_id BINARY(16) NOT NULL,
    order_date DATETIME NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'SHIPPED', 'DELIVERED', 'CANCELLED') NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);
