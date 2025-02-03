package br.com.ambevtech.ordermanager.controller;

import br.com.ambevtech.ordermanager.dto.OrderRequestDTO;
import br.com.ambevtech.ordermanager.dto.OrderResponseDTO;
import br.com.ambevtech.ordermanager.dto.OrderStatusUpdateDTO;
import br.com.ambevtech.ordermanager.external.kafka.OrderKafkaProducer;
import br.com.ambevtech.ordermanager.model.enums.OrderStatus;
import br.com.ambevtech.ordermanager.service.ExternalOrderService;
import br.com.ambevtech.ordermanager.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
        log.info("Criando novo pedido para cliente ID: {}", dto.customerId());
        return ResponseEntity.ok(orderService.createOrder(dto));
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponseDTO>> getOrders(
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "false") boolean cached,
            Pageable pageable) {

        if (customerId != null) {
            log.info("Buscando pedidos do cliente ID: {}", customerId);
            return ResponseEntity.ok(orderService.getOrdersByCustomerId(customerId, pageable));
        } else if (status != null) {
            log.info("Buscando pedidos pelo status: {} (cache: {})", status, cached);
            if (cached) {
                List<OrderResponseDTO> cachedOrders = orderService.getCachedOrdersByStatus(status);
                Page<OrderResponseDTO> pagedResponse = new PageImpl<>(cachedOrders, pageable, cachedOrders.size());
                return ResponseEntity.ok(pagedResponse);
            }
            return ResponseEntity.ok(orderService.getOrdersByStatus(status, pageable));
        } else {
            log.info("Buscando todos os pedidos paginados.");
            return ResponseEntity.ok(orderService.getAllOrders(pageable));
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable UUID id) {
        log.info("Buscando pedido com ID: {}", id);
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable UUID id,
            @Valid @RequestBody OrderStatusUpdateDTO dto) {
        log.info("Atualizando status do pedido ID: {}", id);
        return ResponseEntity.ok(orderService.updateOrderStatus(id, dto));
    }

    @PostMapping("/external")
    public ResponseEntity<OrderResponseDTO> receiveExternalOrder(@Valid @RequestBody OrderRequestDTO dto) {
        log.info("Recebido pedido externo para cliente ID: {}", dto.customerId());
        return ResponseEntity.ok(externalOrderService.processExternalOrder(dto));
    }

    @PostMapping("/external-async")
    public ResponseEntity<String> receiveExternalOrderAsync(@Valid @RequestBody OrderRequestDTO dto) {
        log.info("Recebido pedido externo para processamento assíncrono. Cliente ID: {}", dto.customerId());
        orderKafkaProducer.sendOrder(dto);
        return ResponseEntity.ok("Pedido enviado para processamento assíncrono.");
    }
}

