CREATE TABLE payments (
    id BINARY(16) NOT NULL PRIMARY KEY,
    order_id BINARY(16) NOT NULL,
    payment_date DATETIME NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'DECLINED', 'REFUNDED') NOT NULL,
    amount_paid DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(20) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id)
);
