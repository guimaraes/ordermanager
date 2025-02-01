CREATE TABLE products (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description TEXT NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  supplier_id BIGINT NOT NULL,
  FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
);