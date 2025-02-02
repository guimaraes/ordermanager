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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderService orderService;

    @Transactional
    public OrderResponseDTO updateOrderItems(UUID orderId, List<OrderItemRequestDTO> itemsDto) {
        log.info("Atualizando itens do pedido ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Pedido não encontrado: " + orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Itens só podem ser alterados se o pedido estiver PENDENTE.");
        }

        orderItemRepository.deleteAll(order.getItems());

        List<OrderItem> updatedItems = orderService.createOrderItems(itemsDto, order);
        order.getItems().addAll(updatedItems);

        BigDecimal totalAmount = orderService.calculateTotalAmount(updatedItems);
        order.setTotalAmount(totalAmount);

        order = orderRepository.save(order);

        log.info("Itens do pedido ID: {} atualizados com sucesso!", orderId);
        return OrderMapper.toResponseDTO(order);
    }

}
