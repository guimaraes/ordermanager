package br.com.ambevtech.ordermanager.controller;

import br.com.ambevtech.ordermanager.dto.OrderRequestDTO;
import br.com.ambevtech.ordermanager.dto.OrderResponseDTO;
import br.com.ambevtech.ordermanager.dto.OrderStatusUpdateDTO;
import br.com.ambevtech.ordermanager.external.kafka.OrderKafkaProducer;
import br.com.ambevtech.ordermanager.service.ExternalOrderService;
import br.com.ambevtech.ordermanager.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final ExternalOrderService externalOrderService;
    private final OrderKafkaProducer orderKafkaProducer;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderRequestDTO dto) {
        log.info("Recebida solicitação para criar um novo pedido para o cliente ID: {}", dto.customerId());
        OrderResponseDTO response = orderService.createOrder(dto);
        log.info("Pedido criado com sucesso! ID: {}", response.id());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponseDTO>> getAllOrders(Pageable pageable) {
        log.info("Buscando todos os pedidos. Página: {}, Tamanho: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<OrderResponseDTO> orders = orderService.getAllOrders(pageable);
        log.info("Retornando {} pedidos", orders.getTotalElements());
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable UUID id) {
        log.info("Buscando pedido com ID: {}", id);
        OrderResponseDTO order = orderService.getOrderById(id);
        log.info("Pedido encontrado: {}", order);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Page<OrderResponseDTO>> getOrdersByCustomerId(
            @PathVariable UUID customerId, Pageable pageable) {
        log.info("Buscando pedidos do cliente ID: {}. Página: {}, Tamanho: {}", customerId, pageable.getPageNumber(), pageable.getPageSize());
        Page<OrderResponseDTO> orders = orderService.getOrdersByCustomerId(customerId, pageable);
        log.info("Retornando {} pedidos para o cliente ID: {}", orders.getTotalElements(), customerId);
        return ResponseEntity.ok(orders);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable UUID id,
            @Valid @RequestBody OrderStatusUpdateDTO dto) {
        log.info("Recebida solicitação para atualizar status do pedido ID: {}", id);
        OrderResponseDTO response = orderService.updateOrderStatus(id, dto);
        log.info("Status atualizado com sucesso para pedido ID: {}", id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/external")
    public ResponseEntity<OrderResponseDTO> receiveExternalOrder(@Valid @RequestBody OrderRequestDTO dto) {
        log.info("Recebido pedido externo para o cliente ID: {}", dto.customerId());
        OrderResponseDTO response = externalOrderService.processExternalOrder(dto);
        log.info("Pedido externo processado com sucesso! ID: {}", response.id());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/external-async")
    public ResponseEntity<String> receiveExternalOrderAsync(@Valid @RequestBody OrderRequestDTO dto) {
        log.info("Recebido pedido externo para processamento assíncrono. Cliente ID: {}", dto.customerId());
        orderKafkaProducer.sendOrder(dto);
        return ResponseEntity.ok("Pedido enviado para processamento assíncrono.");
    }
}
