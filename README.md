# Order Manager API

## Índice
1. [Descrição do Projeto](#descricao-do-projeto)
2. [Tecnologias Utilizadas](#tecnologias-utilizadas)
3. [Estrutura do Projeto](#estrutura-do-projeto)
4. [Configurações](#configuracoes)
5. [Endpoints das Controllers](#endpoints-das-controllers)
6. [Exceções Tratadas](#excecoes-tratadas)
7. [Diagrama de Entidade Relacional (DER)](#diagrama-de-entidade-relacional)

---

## Descrição do Projeto
A **Order Manager API** é uma aplicação desenvolvida para o gerenciamento de pedidos, fornecendo operações de criação, atualização, remoção e consulta de pedidos, produtos, fornecedores, pagamentos e entregas.

A API é construída utilizando **Spring Boot 3.4.2**, com persistência em **PostgreSQL**, suporte para documentação via **SpringDoc OpenAPI** e caching embutido para otimizar a performance.

---

## Tecnologias Utilizadas
- **Java 21**
- **Spring Boot 3.4.2**
- **Spring Data JPA**
- **Spring Validation**
- **Spring Boot Actuator**
- **PostgreSQL 42.7.4**
- **Lombok 1.18.36**
- **MapStruct 1.6.3**
- **SpringDoc OpenAPI 2.2.0**
- **Maven 3.8.1**

---

## Estrutura do Projeto
```
ordermanager/
│-- src/
│   ├── main/
│   │   ├── java/br/com/ambevtech/ordermanager/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── exception/
│   │   │   ├── mapper/
│   │   │   ├── model/
│   │   │   │   ├── enums/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   ├── config/
│   ├── resources/
│   │   ├── application.yml
│-- pom.xml
```

---

## Configurações

### `application.yml`
```yaml
server:
  port: 8080

spring:
  application:
    name: order-manager

  datasource:
    url: jdbc:postgresql://localhost:5432/ordermanager
    username: postgres
    password: passw@rd

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    database-platform: org.hibernate.dialect.PostgreSQLDialect

springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html

management:
  endpoints:
    web:
      exposure:
        include: "*"
  health:
    show-details: always
```

---

## Endpoints das Controllers

### `SupplierController`
- `GET /api/suppliers` → Lista todos os fornecedores
- `GET /api/suppliers/{id}` → Obtém um fornecedor pelo ID
- `POST /api/suppliers` → Cria um novo fornecedor
- `PUT /api/suppliers/{id}` → Atualiza um fornecedor existente
- `DELETE /api/suppliers/{id}` → Remove um fornecedor

### `ProductController`
- `GET /api/products` → Lista todos os produtos
- `GET /api/products/{id}` → Obtém um produto pelo ID
- `POST /api/products` → Cria um novo produto
- `PUT /api/products/{id}` → Atualiza um produto existente
- `DELETE /api/products/{id}` → Remove um produto

### `OrderController`
- `GET /api/orders` → Lista todos os pedidos
- `GET /api/orders/{id}` → Obtém um pedido pelo ID
- `GET /api/orders/customer/{customerId}` → Obtém os pedidos de um cliente
- `POST /api/orders` → Cria um novo pedido

---

## Exceções Tratadas
- **SupplierNotFoundException** → Lançada quando um fornecedor não é encontrado.
- **ProductNotFoundException** → Lançada quando um produto não é encontrado.
- **OrderNotFoundException** → Lançada quando um pedido não é encontrado.
- **CustomerNotFoundException** → Lançada quando um cliente não é encontrado.
- **ShipmentNotFoundException** → Lançada quando uma entrega não é encontrada.

---

## Diagrama de Entidade Relacional
```mermaid
erDiagram
    CUSTOMER {
        UUID id PK
        string name
        string email
        string phoneNumber
    }
    
    ORDER {
        UUID id PK
        UUID customer_id FK
        datetime orderDate
        enum status
        decimal totalAmount
    }
    
    ORDER_ITEM {
        UUID id PK
        UUID order_id FK
        UUID product_id FK
        int quantity
        decimal unitPrice
        decimal totalPrice
    }
    
    PRODUCT {
        UUID id PK
        string name
        string description
        decimal price
        UUID supplier_id FK
    }
    
    SUPPLIER {
        UUID id PK
        string name
        string email
        string phoneNumber
    }
    
    SHIPMENT {
        UUID id PK
        UUID order_id FK
        datetime shippedDate
        string trackingNumber
        enum status
    }
    
    PAYMENT {
        UUID id PK
        UUID order_id FK
        datetime paymentDate
        decimal amountPaid
        string paymentMethod
    }
    
    CUSTOMER ||--o{ ORDER : possui
    ORDER ||--o{ ORDER_ITEM : contém
    ORDER_ITEM ||--|{ PRODUCT : referencia
    PRODUCT ||--|{ SUPPLIER : fornecido_por
    ORDER ||--|{ PAYMENT : possui_pagamento
    ORDER ||--|{ SHIPMENT : possui_entrega
```

---

Este README documenta todos os detalhes necessários para a compreensão e uso da **Order Manager API**.

