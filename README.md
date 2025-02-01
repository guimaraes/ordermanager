# Order Manager API

## Índice

1. [Descrição](#descrição)
2. [Tecnologias Utilizadas](#tecnologias-utilizadas)
3. [Estrutura do Projeto](#estrutura-do-projeto)
4. [Configurações](#configurações)
    - [application.yml](#applicationyml)
    - [pom.xml](#pomxml)
5. [Endpoints](#endpoints)
    - [CustomerController](#customercontroller)
    - [OrderController](#ordercontroller)
    - [ProductController](#productcontroller)
    - [SupplierController](#suppliercontroller)
6. [Exceções](#exceções)
7. [Diagrama de Entidade Relacional](#diagrama-de-entidade-relacional)
8. [Conclusão](#conclusão)

---

## Descrição

A **Order Manager API** é um sistema desenvolvido para o gerenciamento de pedidos, clientes, produtos, fornecedores e pagamentos. A API permite a criação, atualização, remoção e consulta dessas entidades, garantindo um fluxo eficiente de pedidos dentro de um sistema de e-commerce ou gestão empresarial.

## Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3.4.2**
- **Spring Data JPA**
- **Spring Boot Validation**
- **Spring Boot Cache**
- **Spring Boot Actuator**
- **SpringDoc OpenAPI**
- **Lombok**
- **MapStruct**
- **MySQL 8.0.33**
- **Flyway**

## Estrutura do Projeto

```
ordermanager/
│── src/
│   ├── main/
│   │   ├── java/br/com/ambevtech/ordermanager/
│   │   │   ├── controller/        # Controllers REST
│   │   │   ├── service/           # Serviços de regra de negócio
│   │   │   ├── repository/        # Camada de persistência (JPA Repositories)
│   │   │   ├── model/             # Entidades JPA
│   │   │   ├── dto/               # Data Transfer Objects (DTOs)
│   │   │   ├── exception/         # Tratamento de exceções
│   │   │   ├── mapper/            # Mapeamento de DTOs para entidades
│   │   │   ├── config/            # Configurações do projeto (Swagger, etc.)
│   │   │   ├── OrderManagerApplication.java  # Classe principal
│   │   ├── resources/
│   │   │   ├── application.yml    # Configuração do projeto
│   │   │   ├── db/migration/      # Scripts Flyway
│── pom.xml
│── README.md
```

## Configurações

### application.yml
```yaml
server:
  port: 8080

spring:
  application:
    name: order-manager

  datasource:
    url: jdbc:mysql://localhost:3306/ordermanager?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC
    username: root
    password: Xse1,lo0
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    database-platform: org.hibernate.dialect.MySQLDialect

springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
```

## Endpoints

As controllers da API são responsáveis por expor os endpoints REST para interação com os recursos do sistema. Cada controller está bem estruturada, seguindo as práticas recomendadas do Spring Boot.

### `CustomerController`

Responsável pelo gerenciamento de clientes.

- **`GET /api/customers`** - Retorna uma lista paginada de clientes.
- **`GET /api/customers/{id}`** - Busca um cliente pelo ID.
- **`POST /api/customers`** - Cria um novo cliente.
- **`PUT /api/customers/{id}`** - Atualiza os dados de um cliente existente.
- **`DELETE /api/customers/{id}`** - Remove um cliente do sistema.

### `OrderController`

Responsável pelo gerenciamento de pedidos.

- **`POST /api/orders`** - Cria um novo pedido associado a um cliente.
- **`GET /api/orders`** - Retorna uma lista paginada de pedidos.
- **`GET /api/orders/{id}`** - Busca um pedido pelo ID.
- **`GET /api/orders/customer/{customerId}`** - Retorna todos os pedidos de um cliente específico.

### `ProductController`

Gerencia os produtos disponíveis na plataforma.

- **`GET /api/products`** - Retorna uma lista paginada de produtos.
- **`GET /api/products/{id}`** - Busca um produto pelo ID.
- **`POST /api/products`** - Cadastra um novo produto.
- **`PUT /api/products/{id}`** - Atualiza um produto existente.
- **`DELETE /api/products/{id}`** - Remove um produto do sistema.

### `SupplierController`

Gerencia os fornecedores cadastrados no sistema.

- **`GET /api/suppliers`** - Retorna uma lista paginada de fornecedores.
- **`GET /api/suppliers/{id}`** - Busca um fornecedor pelo ID.
- **`POST /api/suppliers`** - Cadastra um novo fornecedor.
- **`PUT /api/suppliers/{id}`** - Atualiza os dados de um fornecedor.
- **`DELETE /api/suppliers/{id}`** - Remove um fornecedor do sistema.


## Exceções

O sistema possui um robusto mecanismo de tratamento de exceções para garantir que erros sejam tratados de maneira clara e estruturada. Abaixo estão as exceções personalizadas utilizadas na API:

### Exceções Personalizadas

- `CustomerNotFoundException` - Lançada quando um cliente não é encontrado no banco de dados.
- `OrderNotFoundException` - Lançada quando um pedido não é encontrado.
- `ProductNotFoundException` - Lançada quando um produto não é encontrado.
- `SupplierNotFoundException` - Lançada quando um fornecedor não é encontrado.
- `SupplierDuplicateEmailException` - Lançada quando há tentativa de cadastrar um fornecedor com um e-mail já existente.
- `ShipmentNotFoundException` - Lançada quando uma entrega não é encontrada.

### Detalhamento das Exceções

#### `GlobalExceptionHandler`

A classe `GlobalExceptionHandler` é responsável por capturar todas as exceções lançadas na aplicação e retornar respostas padronizadas para o cliente. Esta classe utiliza a anotação `@RestControllerAdvice`, garantindo que qualquer erro ocorra de forma centralizada.

**Principais Métodos:**

- `handleCustomerNotFoundException` - Captura exceções `CustomerNotFoundException` e retorna `404 Not Found`.
- `handleSupplierNotFoundException` - Captura exceções `SupplierNotFoundException` e retorna `404 Not Found`.
- `handleSupplierDuplicateEmailException` - Captura exceções `SupplierDuplicateEmailException` e retorna `400 Bad Request`.
- `handleDataIntegrityViolationException` - Captura exceções de integridade de banco de dados e retorna `400 Bad Request`.
- `handleValidationExceptions` - Captura erros de validação em requisições e retorna `400 Bad Request` com detalhes dos erros.
- `handleGenericException` - Captura exceções genéricas e retorna `500 Internal Server Error`.

Cada resposta de erro segue um padrão utilizando a classe `ErrorResponse`, que contém os seguintes campos:

```java
public record ErrorResponse(int status, String message, LocalDateTime timestamp, List<String> errors) {
    public ErrorResponse(int status, String message, LocalDateTime timestamp) {
        this(status, message, timestamp, null);
    }
}
```

Essa estrutura garante que todas as respostas de erro sejam consistentes e facilmente interpretáveis pelos clientes da API.


## Modelo de Entidade Relacional
```mermaid
erDiagram
    CUSTOMER ||--o{ ORDER : possui
    ORDER ||--o{ ORDER_ITEM : contem
    ORDER_ITEM ||--|{ PRODUCT : referencia
    PRODUCT ||--|{ SUPPLIER : fornecido_por
    ORDER ||--|{ PAYMENT : possui_pagamento
    ORDER ||--|{ SHIPMENT : possui_entrega

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
```

## Conclusão

A **Order Manager API** foi projetada para oferecer um gerenciamento robusto e eficiente de pedidos e seus componentes. Com a estrutura modular e o uso de tecnologias modernas como Spring Boot, MySQL e MapStruct, a API permite uma fácil escalabilidade e manutenção. O sistema resolve o problema da organização de pedidos, fornecendo endpoints claros e bem documentados para cada entidade essencial ao fluxo de compras, tornando a gestão de pedidos mais ágil e segura.

Além disso, com a implementação de um tratamento avançado de exceções, a API fornece respostas padronizadas, garantindo uma melhor experiência do usuário e facilitando a depuração de erros. O uso de boas práticas, como separação de camadas e injeção de dependências, torna este sistema altamente sustentável e pronto para expansão.