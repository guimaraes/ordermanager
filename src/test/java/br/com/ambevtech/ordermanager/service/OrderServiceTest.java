package br.com.ambevtech.ordermanager.service;

import br.com.ambevtech.ordermanager.dto.*;
import br.com.ambevtech.ordermanager.exception.CustomerNotFoundException;
import br.com.ambevtech.ordermanager.exception.OrderNotFoundException;
import br.com.ambevtech.ordermanager.exception.ProductNotFoundException;
import br.com.ambevtech.ordermanager.mapper.OrderMapper;
import br.com.ambevtech.ordermanager.model.*;
import br.com.ambevtech.ordermanager.model.enums.OrderStatus;
import br.com.ambevtech.ordermanager.model.enums.PaymentStatus;
import br.com.ambevtech.ordermanager.repository.*;
import br.com.ambevtech.ordermanager.service.OrderService;
import br.com.ambevtech.ordermanager.service.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private OrderBatchRepository orderBatchRepository;
    @Mock
    private OrderHistoryRepository orderHistoryRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private RedisService redisService;
    @Mock
    private ObjectMapper objectMapper;

    private UUID orderId;
    private UUID customerId;
    private Order order;
    private Customer customer;
    private OrderRequestDTO orderRequestDTO;
    private OrderItemRequestDTO orderItemRequestDTO;
    private Product product;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        customerId = UUID.randomUUID();

        customer = new Customer();
        customer.setId(customerId);

        product = new Product();
        product.setId(1L);
        product.setPrice(BigDecimal.valueOf(100.00));

        orderItemRequestDTO = new OrderItemRequestDTO(product.getId(), 2);

        orderRequestDTO = new OrderRequestDTO(customerId, List.of(orderItemRequestDTO), OrderStatus.PENDING, "EXTERNAL-ORDER-123");

        orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        orderItem.setUnitPrice(product.getPrice());
        orderItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(2)));

        order = Order.builder()
                .id(orderId)
                .customer(customer)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .items(List.of(orderItem))
                .build();
    }


    @Test
    void getAllOrders_ShouldReturnPagedOrders() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> ordersPage = new PageImpl<>(List.of(order));

        when(orderRepository.findAll(pageable)).thenReturn(ordersPage);

        List<OrderItemResponseDTO> items = List.of();

        when(OrderMapper.toResponseDTO(any(Order.class)))
                .thenReturn(new OrderResponseDTO(orderId, customerId, LocalDateTime.now(), OrderStatus.PENDING, BigDecimal.ZERO, items));

        Page<OrderResponseDTO> result = orderService.getAllOrders(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(orderRepository, times(1)).findAll(pageable);
    }

    @Test
    void getOrderById_ShouldReturnOrder() {
        List<OrderItemResponseDTO> items = List.of();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(OrderMapper.toResponseDTO(order))
                .thenReturn(new OrderResponseDTO(orderId, customerId, LocalDateTime.now(), OrderStatus.PENDING, BigDecimal.ZERO, items));

        OrderResponseDTO result = orderService.getOrderById(orderId);

        assertNotNull(result);
        assertEquals(orderId, result.id());
    }


    @Test
    void createOrder_ShouldCreateAndReturnOrder() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(productRepository.findById(orderItemRequestDTO.productId())).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(paymentRepository.save(any(Payment.class))).thenReturn(new Payment());

        // Criando uma lista vazia de itens ou populada com mock
        List<OrderItemResponseDTO> items = List.of(); // ou List.of(new OrderItemResponseDTO(...))

        when(OrderMapper.toResponseDTO(any(Order.class)))
                .thenReturn(new OrderResponseDTO(orderId, customerId, LocalDateTime.now(), OrderStatus.PENDING, BigDecimal.ZERO, items));

        OrderResponseDTO result = orderService.createOrder(orderRequestDTO);

        assertNotNull(result);
        assertEquals(orderId, result.id());
        verify(orderRepository, times(2)).save(any(Order.class));
    }

    @Test
    void updateOrderStatus_ShouldUpdateAndReturnOrder() {
        OrderStatusUpdateDTO dto = new OrderStatusUpdateDTO(OrderStatus.APPROVED, "admin");

        List<OrderItemResponseDTO> items = List.of(); // Lista vazia ou com itens de teste

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(OrderMapper.toResponseDTO(any(Order.class)))
                .thenReturn(new OrderResponseDTO(orderId, customerId, LocalDateTime.now(), OrderStatus.APPROVED, BigDecimal.ZERO, items));

        OrderResponseDTO result = orderService.updateOrderStatus(orderId, dto);

        assertNotNull(result);
        assertEquals(OrderStatus.APPROVED, result.status());
    }


    @Test
    void getOrdersByCustomerId_ShouldReturnPagedOrders() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> ordersPage = new PageImpl<>(List.of(order));

        List<OrderItemResponseDTO> items = List.of(); // Lista vazia ou preenchida conforme necessidade

        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(orderRepository.findByCustomerId(customerId, pageable)).thenReturn(ordersPage);
        when(OrderMapper.toResponseDTO(any(Order.class)))
                .thenReturn(new OrderResponseDTO(orderId, customerId, LocalDateTime.now(), OrderStatus.PENDING, BigDecimal.ZERO, items));

        Page<OrderResponseDTO> result = orderService.getOrdersByCustomerId(customerId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

}
