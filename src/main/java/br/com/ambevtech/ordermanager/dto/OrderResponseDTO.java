package br.com.ambevtech.ordermanager.dto;

import br.com.ambevtech.ordermanager.model.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponseDTO(
        UUID id,
        UUID customerId,
        LocalDateTime orderDate,
        OrderStatus status,
        BigDecimal totalAmount,
        List<OrderItemResponseDTO> items
) {
}
