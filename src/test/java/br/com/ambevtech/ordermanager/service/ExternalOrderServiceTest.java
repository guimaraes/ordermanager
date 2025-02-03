package br.com.ambevtech.ordermanager.service;

import br.com.ambevtech.ordermanager.dto.OrderItemRequestDTO;
import br.com.ambevtech.ordermanager.dto.OrderRequestDTO;
import br.com.ambevtech.ordermanager.dto.OrderResponseDTO;
import br.com.ambevtech.ordermanager.exception.CustomerNotFoundException;
import br.com.ambevtech.ordermanager.exception.OrderAlreadyExistsException;
import br.com.ambevtech.ordermanager.exception.ProductNotFoundException;
import br.com.ambevtech.ordermanager.mapper.OrderMapper;
import br.com.ambevtech.ordermanager.model.Customer;
import br.com.ambevtech.ordermanager.model.Order;
import br.com.ambevtech.ordermanager.model.OrderItem;
import br.com.ambevtech.ordermanager.model.Product;
import br.com.ambevtech.ordermanager.model.enums.OrderStatus;
import br.com.ambevtech.ordermanager.repository.CustomerRepository;
import br.com.ambevtech.ordermanager.repository.OrderItemRepository;
import br.com.ambevtech.ordermanager.repository.OrderRepository;
import br.com.ambevtech.ordermanager.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExternalOrderServiceTest {

    @InjectMocks
    private ExternalOrderService externalOrderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderMapper orderMapper;

    private UUID orderId;
    private UUID customerId;
    private String externalOrderId;
    private Customer customer;
    private Order order;
    private OrderItemRequestDTO orderItemRequestDTO;
    private OrderRequestDTO orderRequestDTO;
    private Product product;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        customerId = UUID.randomUUID();
        externalOrderId = "EXTERNAL-ORDER-123";

        customer = new Customer();
        customer.setId(customerId);

        product = new Product();
        product.setId(1L);
        product.setPrice(BigDecimal.valueOf(100.00));

        orderItemRequestDTO = new OrderItemRequestDTO(product.getId(), 2);
        orderRequestDTO = new OrderRequestDTO(customerId, List.of(orderItemRequestDTO), OrderStatus.PENDING, externalOrderId);

        orderItem = OrderItem.builder()
                .order(null)
                .product(product)
                .quantity(2)
                .unitPrice(product.getPrice())
                .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(2)))
                .build();

        order = Order.builder()
                .id(orderId)
                .customer(customer)
                .externalOrderId(externalOrderId)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.valueOf(200.00))
                .items(List.of(orderItem))
                .build();
    }

    @Test
    void processExternalOrder_ShouldCreateAndReturnOrder() {
        // Configuração dos mocks
        when(orderRepository.existsByExternalOrderId(externalOrderId)).thenReturn(false);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(productRepository.findById(orderItemRequestDTO.productId())).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toResponseDTO(any(Order.class)))
                .thenReturn(new OrderResponseDTO(orderId, customerId, LocalDateTime.now(), OrderStatus.PENDING, BigDecimal.valueOf(200.00), List.of()));

        // Execução do método
        OrderResponseDTO result = externalOrderService.processExternalOrder(orderRequestDTO);

        // Validações
        assertNotNull(result);
        assertEquals(orderId, result.id());
        assertEquals(BigDecimal.valueOf(200.00), result.totalAmount());

        // Verificações de chamadas dos mocks
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void processExternalOrder_ShouldThrowException_WhenOrderAlreadyExists() {
        when(orderRepository.existsByExternalOrderId(externalOrderId)).thenReturn(true);

        assertThrows(OrderAlreadyExistsException.class, () -> externalOrderService.processExternalOrder(orderRequestDTO));

        verify(orderRepository, times(1)).existsByExternalOrderId(externalOrderId);
        verifyNoInteractions(customerRepository, productRepository, orderItemRepository);
    }

    @Test
    void processExternalOrder_ShouldThrowException_WhenCustomerNotFound() {
        when(orderRepository.existsByExternalOrderId(externalOrderId)).thenReturn(false);
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> externalOrderService.processExternalOrder(orderRequestDTO));

        verify(orderRepository, times(1)).existsByExternalOrderId(externalOrderId);
        verify(customerRepository, times(1)).findById(customerId);
        verifyNoInteractions(productRepository, orderItemRepository);
    }

    @Test
    void processExternalOrder_ShouldThrowException_WhenProductNotFound() {
        when(orderRepository.existsByExternalOrderId(externalOrderId)).thenReturn(false);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(productRepository.findById(orderItemRequestDTO.productId())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> externalOrderService.processExternalOrder(orderRequestDTO));

        verify(orderRepository, times(1)).existsByExternalOrderId(externalOrderId);
        verify(customerRepository, times(1)).findById(customerId);
        verify(productRepository, times(1)).findById(orderItemRequestDTO.productId());
        verifyNoInteractions(orderItemRepository);
    }
}
