package br.com.ambevtech.ordermanager.controller;

import br.com.ambevtech.ordermanager.dto.OrderRequestDTO;
import br.com.ambevtech.ordermanager.dto.OrderResponseDTO;
import br.com.ambevtech.ordermanager.dto.OrderStatusUpdateDTO;
import br.com.ambevtech.ordermanager.external.kafka.OrderKafkaProducer;
import br.com.ambevtech.ordermanager.model.enums.OrderStatus;
import br.com.ambevtech.ordermanager.service.ExternalOrderService;
import br.com.ambevtech.ordermanager.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private OrderService orderService;

    @Mock
    private ExternalOrderService externalOrderService;

    @Mock
    private OrderKafkaProducer orderKafkaProducer;

    private UUID orderId;
    private UUID customerId;
    private OrderResponseDTO orderResponseDTO;
    private OrderRequestDTO orderRequestDTO;
    private OrderStatusUpdateDTO orderStatusUpdateDTO;
    private Page<OrderResponseDTO> orderPage;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        customerId = UUID.randomUUID();

        orderResponseDTO = new OrderResponseDTO(
                orderId,
                customerId,
                LocalDateTime.now(),
                OrderStatus.PENDING,
                BigDecimal.valueOf(100.00),
                List.of()
        );

        orderRequestDTO = new OrderRequestDTO(
                customerId,
                List.of(),
                OrderStatus.PENDING,
                "EXTERNAL-123"
        );

        orderStatusUpdateDTO = new OrderStatusUpdateDTO(OrderStatus.APPROVED, "admin");

        orderPage = new PageImpl<>(List.of(orderResponseDTO), PageRequest.of(0, 10), 1);
    }

    @Test
    void createOrder_ShouldReturnCreatedOrder() {
        when(orderService.createOrder(any(OrderRequestDTO.class))).thenReturn(orderResponseDTO);

        ResponseEntity<OrderResponseDTO> response = orderController.createOrder(orderRequestDTO);

        assertNotNull(response.getBody());
        assertEquals(orderResponseDTO, response.getBody());
        verify(orderService, times(1)).createOrder(any(OrderRequestDTO.class));
    }

    @Test
    void getOrders_ShouldReturnPagedOrders() {
        when(orderService.getAllOrders(any())).thenReturn(orderPage);

        ResponseEntity<Page<OrderResponseDTO>> response = orderController.getOrders(null, null, false, PageRequest.of(0, 10));

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(orderService, times(1)).getAllOrders(any());
    }

    @Test
    void getOrders_ShouldReturnPagedOrdersByCustomerId() {
        when(orderService.getOrdersByCustomerId(any(UUID.class), any())).thenReturn(orderPage);

        ResponseEntity<Page<OrderResponseDTO>> response = orderController.getOrders(customerId, null, false, PageRequest.of(0, 10));

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(orderService, times(1)).getOrdersByCustomerId(any(UUID.class), any());
    }

    @Test
    void getOrders_ShouldReturnPagedOrdersByStatus() {
        when(orderService.getOrdersByStatus(any(OrderStatus.class), any())).thenReturn(orderPage);

        ResponseEntity<Page<OrderResponseDTO>> response = orderController.getOrders(null, OrderStatus.PENDING, false, PageRequest.of(0, 10));

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(orderService, times(1)).getOrdersByStatus(any(OrderStatus.class), any());
    }

    @Test
    void getOrderById_ShouldReturnOrder() {
        when(orderService.getOrderById(orderId)).thenReturn(orderResponseDTO);

        ResponseEntity<OrderResponseDTO> response = orderController.getOrderById(orderId);

        assertNotNull(response.getBody());
        assertEquals(orderResponseDTO, response.getBody());
        verify(orderService, times(1)).getOrderById(orderId);
    }

    @Test
    void updateOrderStatus_ShouldReturnUpdatedOrder() {
        when(orderService.updateOrderStatus(any(UUID.class), any(OrderStatusUpdateDTO.class))).thenReturn(orderResponseDTO);

        ResponseEntity<OrderResponseDTO> response = orderController.updateOrderStatus(orderId, orderStatusUpdateDTO);

        assertNotNull(response.getBody());
        assertEquals(orderResponseDTO, response.getBody());
        verify(orderService, times(1)).updateOrderStatus(any(UUID.class), any(OrderStatusUpdateDTO.class));
    }

    @Test
    void receiveExternalOrder_ShouldReturnProcessedOrder() {
        when(externalOrderService.processExternalOrder(any(OrderRequestDTO.class))).thenReturn(orderResponseDTO);

        ResponseEntity<OrderResponseDTO> response = orderController.receiveExternalOrder(orderRequestDTO);

        assertNotNull(response.getBody());
        assertEquals(orderResponseDTO, response.getBody());
        verify(externalOrderService, times(1)).processExternalOrder(any(OrderRequestDTO.class));
    }

    @Test
    void receiveExternalOrderAsync_ShouldSendOrderAndReturnMessage() {
        ResponseEntity<String> response = orderController.receiveExternalOrderAsync(orderRequestDTO);

        assertNotNull(response.getBody());
        assertEquals("Pedido enviado para processamento assíncrono.", response.getBody());
        verify(orderKafkaProducer, times(1)).sendOrder(any(OrderRequestDTO.class));
    }
}
