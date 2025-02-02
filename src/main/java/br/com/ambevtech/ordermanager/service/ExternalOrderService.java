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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalOrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public OrderResponseDTO processExternalOrder(OrderRequestDTO dto) {
        log.info("Processando pedido externo. ID Externo: {}", dto.externalOrderId());

        if (dto.externalOrderId() != null && orderRepository.existsByExternalOrderId(dto.externalOrderId())) {
            log.warn("Pedido duplicado detectado! ID Externo: {}", dto.externalOrderId());
            throw new OrderAlreadyExistsException("Pedido já foi processado anteriormente.");
        }

        Customer customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new CustomerNotFoundException("Cliente não encontrado: " + dto.customerId()));

        List<OrderItem> items = createOrderItems(dto.items(), null);

        Order order = Order.builder()
                .customer(customer)
                .externalOrderId(dto.externalOrderId())
                .orderDate(LocalDateTime.now())
                .status(dto.status() != null ? dto.status() : OrderStatus.PENDING)
                .totalAmount(calculateTotalAmount(items))
                .items(items)
                .build();

        order = orderRepository.save(order);
        log.info("Pedido externo salvo com sucesso! ID Interno: {}", order.getId());

        return OrderMapper.toResponseDTO(order);
    }

    private List<OrderItem> createOrderItems(List<OrderItemRequestDTO> itemsDto, Order order) {
        return itemsDto.stream().map(itemDto -> {
            Product product = productRepository.findById(itemDto.productId())
                    .orElseThrow(() -> new ProductNotFoundException("Produto não encontrado: " + itemDto.productId()));

            return OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemDto.quantity())
                    .unitPrice(product.getPrice())
                    .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(itemDto.quantity())))
                    .build();
        }).toList();
    }

    private BigDecimal calculateTotalAmount(List<OrderItem> items) {
        return items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
