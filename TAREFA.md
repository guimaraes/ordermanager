### **TAREFA: Implementação do Gerenciamento dos Pedidos**

#### **Feature: Gestão de Pedidos no Serviço ORDER**

Como **desenvolvedor do serviço ORDER**,  
Quero **receber pedidos do Produto Externo A, calcular os valores e armazenar os pedidos processados**,  
Para que **o Produto Externo B possa consultar e consumir os pedidos já processados.**

---

### **Cenário 1: Receber pedidos do Produto Externo A**
**Dado** que o Produto Externo A envia um pedido para o serviço ORDER,  
**Quando** a requisição é recebida pela API,  
**Então** o pedido deve ser validado e armazenado no banco de dados com status "PENDENTE".

---

### **Cenário 2: Calcular o valor total do pedido**
**Dado** que um pedido foi recebido e armazenado no banco,  
**Quando** o cálculo do pedido for executado,  
**Então** o sistema deve somar os valores de todos os produtos no pedido e atualizar o campo "valor_total".

---

### **Cenário 3: Disponibilizar pedidos processados para consulta**
**Dado** que um pedido foi calculado e está armazenado com status "PROCESSADO",  
**Quando** o Produto Externo B fizer uma requisição para consultar os pedidos,  
**Então** o serviço ORDER deve retornar a lista de pedidos já calculados e processados.

---

### **Cenário 4: Enviar pedidos processados para o Produto Externo B**
**Dado** que um pedido está processado e pronto para envio,  
**Quando** a integração com o Produto Externo B for acionada,  
**Então** o pedido deve ser enviado corretamente e marcado como "ENVIADO" no banco.

---

### **Cenário 5: Evitar duplicação de pedidos**
**Dado** que um pedido já foi recebido anteriormente,  
**Quando** o Produto Externo A enviar um pedido com o mesmo ID,  
**Então** o sistema deve rejeitar a duplicação e registrar um log de erro.

---

### **Cenário 6: Garantir consistência e concorrência dos dados**
**Dado** que múltiplas requisições simultâneas tentam acessar e processar pedidos,  
**Quando** a concorrência ocorrer,  
**Então** o sistema deve garantir a consistência das operações, evitando dados corrompidos ou sobrepostos.

---

### **Cenário 7: Garantia de escalabilidade e performance**
**Dado** que a aplicação pode receber entre 150 mil e 200 mil pedidos por dia,  
**Quando** houver um alto volume de requisições,  
**Então** o sistema deve escalar corretamente e processar os pedidos de maneira eficiente sem degradação de performance.

---

## **Definição de Pronto (DoD - Definition of Done)**
- O serviço ORDER recebe pedidos corretamente do Produto Externo A.
- Os pedidos são armazenados no banco com status "PENDENTE".
- O sistema calcula corretamente o valor total dos pedidos.
- Os pedidos processados são armazenados com status "PROCESSADO".
- O serviço ORDER expõe um endpoint para consulta de pedidos para o Produto Externo B.
- O sistema evita duplicação de pedidos e mantém logs de tentativas duplicadas.
- Há garantia de consistência e concorrência na manipulação de dados.
- O serviço consegue processar até 200 mil pedidos/dia sem falhas.
- O serviço ORDER está documentado no Swagger/OpenAPI.
- A API REST está protegida por autenticação/autorizção adequada.
- Testes unitários e de integração cobrem todas as funcionalidades.
- O desenho da arquitetura foi atualizado e documentado.

---

## **Checklist de Atividades**
### **1. Planejamento e Configuração do Projeto**
- Criar o projeto Spring Boot
- Configurar dependências no `pom.xml` (Spring Web, Spring Data, Banco de Dados, etc.)
- Configurar `application.yml` com parâmetros de banco e integração externa

### **2. Implementação do Serviço ORDER**
- Criar as entidades **Pedido** e **Produto**
- Criar os DTOs para transferência de dados entre APIs
- Criar o repositório JPA ou outro mecanismo de persistência
- Criar o serviço para receber pedidos do Produto Externo A
- Implementar a lógica de cálculo do valor total do pedido
- Criar endpoint para receber pedidos `@PostMapping("/order")`
- Criar endpoint para consulta de pedidos `@GetMapping("/order")`
- Criar endpoint para enviar pedidos ao Produto Externo B `@PostMapping("/order/send")`

### **3. Implementação de Recursos Adicionais**
- Implementar verificação de duplicação de pedidos
- Implementar controle de concorrência para garantir consistência
- Verificar gargalos de desempenho no banco de dados
- Implementar filas de mensagens para escalabilidade (se necessário)

### **4. Testes e Validação**
- Criar testes unitários para validação dos serviços
- Criar testes de integração para validar comunicação entre serviços
- Simular carga de dados para testar escalabilidade

### **5. Documentação e Apresentação**
- Criar documentação da API usando Swagger/OpenAPI
- Atualizar desenho da arquitetura final
- Preparar uma demonstração funcional do serviço

---
