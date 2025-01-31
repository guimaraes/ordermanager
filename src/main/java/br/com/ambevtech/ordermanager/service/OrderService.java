package br.com.ambevtech.ordermanager.service;

import br.com.ambevtech.ordermanager.dto.OrderRequestDTO;
import br.com.ambevtech.ordermanager.dto.OrderResponseDTO;
import br.com.ambevtech.ordermanager.exception.CustomerNotFoundException;
import br.com.ambevtech.ordermanager.exception.OrderNotFoundException;
import br.com.ambevtech.ordermanager.exception.ProductNotFoundException;
import br.com.ambevtech.ordermanager.mapper.OrderItemMapper;
import br.com.ambevtech.ordermanager.mapper.OrderMapper;
import br.com.ambevtech.ordermanager.model.Customer;
import br.com.ambevtech.ordermanager.model.Order;
import br.com.ambevtech.ordermanager.model.OrderItem;
import br.com.ambevtech.ordermanager.model.Product;
import br.com.ambevtech.ordermanager.repository.CustomerRepository;
import br.com.ambevtech.ordermanager.repository.OrderItemRepository;
import br.com.ambevtech.ordermanager.repository.OrderRepository;
import br.com.ambevtech.ordermanager.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

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

        List<OrderItem> items = dto.items().stream()
                .map(itemDto -> {
                    Product product = productRepository.findById(itemDto.productId())
                            .orElseThrow(() -> new ProductNotFoundException("Produto não encontrado: " + itemDto.productId()));
                    return OrderItemMapper.toEntity(itemDto, product, null);
                })
                .toList();

        Order order = OrderMapper.toEntity(dto, customer, items);
        order = orderRepository.save(order);
        log.info("Pedido criado com sucesso! ID: {}", order.getId());
        return OrderMapper.toResponseDTO(order);
    }


}
