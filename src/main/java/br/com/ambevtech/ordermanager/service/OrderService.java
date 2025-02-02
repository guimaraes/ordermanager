package br.com.ambevtech.ordermanager.service;

import br.com.ambevtech.ordermanager.dto.OrderItemRequestDTO;
import br.com.ambevtech.ordermanager.dto.OrderRequestDTO;
import br.com.ambevtech.ordermanager.dto.OrderResponseDTO;
import br.com.ambevtech.ordermanager.dto.OrderStatusUpdateDTO;
import br.com.ambevtech.ordermanager.exception.CustomerNotFoundException;
import br.com.ambevtech.ordermanager.exception.OrderNotFoundException;
import br.com.ambevtech.ordermanager.exception.ProductNotFoundException;
import br.com.ambevtech.ordermanager.mapper.OrderMapper;
import br.com.ambevtech.ordermanager.model.*;
import br.com.ambevtech.ordermanager.model.enums.OrderStatus;
import br.com.ambevtech.ordermanager.model.enums.PaymentStatus;
import br.com.ambevtech.ordermanager.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderBatchRepository orderBatchRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    public Page<OrderResponseDTO> getAllOrders(Pageable pageable) {
        log.info("Buscando pedidos com paginação. Página: {}, Tamanho: {}", pageable.getPageNumber(), pageable.getPageSize());
        return orderRepository.findAll(pageable).map(OrderMapper::toResponseDTO);
    }

    public OrderResponseDTO getOrderById(UUID id) {
        log.info("Buscando pedido com ID: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Pedido com ID {} não encontrado!", id);
                    return new OrderNotFoundException("Pedido não encontrado: " + id);
                });
        return OrderMapper.toResponseDTO(order);
    }

    public Page<OrderResponseDTO> getOrdersByCustomerId(UUID customerId, Pageable pageable) {
        log.info("Buscando pedidos do cliente com ID: {} com paginação", customerId);
        if (!customerRepository.existsById(customerId)) {
            log.error("Cliente com ID {} não encontrado!", customerId);
            throw new CustomerNotFoundException("Cliente não encontrado: " + customerId);
        }

        return orderRepository.findByCustomerId(customerId, pageable).map(OrderMapper::toResponseDTO);
    }

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO dto) {
        log.info("Criando um novo pedido para o cliente ID: {}", dto.customerId());

        Customer customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> {
                    log.error("Cliente com ID {} não encontrado!", dto.customerId());
                    return new CustomerNotFoundException("Cliente não encontrado: " + dto.customerId());
                });

        Order order = Order.builder()
                .customer(customer)
                .orderDate(LocalDateTime.now())
                .status(dto.status() != null ? dto.status() : OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

        order = orderRepository.save(order);

        List<OrderItem> items = createOrderItems(dto.items(), order);
        order.getItems().addAll(items);

        BigDecimal totalAmount = calculateTotalAmount(items);
        order.setTotalAmount(totalAmount);
        order = orderRepository.save(order);

        Payment payment = createPayment(order);

        log.info("Pedido criado com sucesso! ID: {} - Pagamento associado criado ID: {}", order.getId(), payment.getId());

        return OrderMapper.toResponseDTO(order);
    }

    public List<OrderItem> createOrderItems(List<OrderItemRequestDTO> itemsDto, Order order) {
        List<OrderItem> items = new ArrayList<>();

        for (OrderItemRequestDTO itemDto : itemsDto) {
            Product product = productRepository.findById(itemDto.productId())
                    .orElseThrow(() -> new ProductNotFoundException("Produto não encontrado: " + itemDto.productId()));

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemDto.quantity())
                    .unitPrice(product.getPrice())
                    .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(itemDto.quantity())))
                    .build();

            items.add(orderItem);
        }

        orderItemRepository.saveAll(items);
        return items;
    }

    private Payment createPayment(Order order) {
        Payment payment = Payment.builder()
                .order(order)
                .paymentDate(LocalDateTime.now())
                .status(PaymentStatus.PENDING)
                .amountPaid(BigDecimal.ZERO)
                .paymentMethod("UNDEFINED")
                .build();

        return paymentRepository.save(payment);
    }

    public BigDecimal calculateTotalAmount(List<OrderItem> items) {
        return items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public OrderResponseDTO updateOrderStatus(UUID orderId, OrderStatusUpdateDTO dto) {
        log.info("Atualizando status do pedido ID: {} para {}", orderId, dto.newStatus());

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Pedido não encontrado: " + orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("O pedido só pode ser alterado se estiver PENDENTE.");
        }

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(dto.newStatus());
        order = orderRepository.save(order);

        OrderHistory orderHistory = OrderHistory.builder()
                .order(order)
                .oldStatus(oldStatus)
                .newStatus(dto.newStatus())
                .updatedBy(dto.updatedBy())
                .timestamp(LocalDateTime.now())
                .build();

        orderHistoryRepository.save(orderHistory);

        log.info("Status do pedido ID: {} atualizado para {}", orderId, dto.newStatus());

        return OrderMapper.toResponseDTO(order);
    }

    public Page<OrderResponseDTO> getProcessedOrders(Pageable pageable) {
        return orderRepository.findByStatus(OrderStatus.APPROVED, pageable)
                .map(OrderMapper::toResponseDTO);
    }

    public Page<OrderResponseDTO> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        log.info("Buscando pedidos com status: {}", status);
        return orderRepository.findByStatus(status, pageable)
                .map(OrderMapper::toResponseDTO);
    }

    @Transactional
    public void processBatchOrders(List<Order> orders) {
        orderBatchRepository.batchInsertOrders(orders);
    }

    public List<OrderResponseDTO> getCachedOrdersByStatus(OrderStatus status) {
        String cacheKey = "orders:status:" + status.name();
        Optional<String> cachedData = redisService.getFromCache(cacheKey);

        if (cachedData.isPresent()) {
            try {
                return objectMapper.readValue(cachedData.get(), new TypeReference<List<OrderResponseDTO>>() {});
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Erro ao desserializar cache", e);
            }
        }

        List<OrderResponseDTO> orders = orderRepository.findByStatus(status, Pageable.unpaged())
                .stream()
                .map(OrderMapper::toResponseDTO)
                .toList();

        try {
            redisService.saveToCache(cacheKey, objectMapper.writeValueAsString(orders));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao serializar cache", e);
        }

        return orders;
    }
}
